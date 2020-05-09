package com.mfg.web.servlets;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageListener;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.persistence.DFSStorage;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.mfg.symbols.jobs.ISymbolJobChangeListener;
import com.mfg.symbols.jobs.InputPipeChangeEvent;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeAdapter;
import com.mfg.symbols.jobs.TradingPipeChangeEvent;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.persistence.TradingStorage;
import com.mfg.utils.jobs.MFGJob;

public class CommandWebsocket extends WebSocketAdapter {
	private static final String CONFIG_TREE = "config-tree";
	private static final String CMD = "cmd";
	private static final String ID = "id";
	private static final String STOP_CONFIG = "stop-config";
	private static final String PLAY_CONFIG = "play-config";
	private RemoteEndpoint _remote;
	private ISymbolJobChangeListener _jobListener;
	private IWorkspaceStorageListener _storageListener;

	public CommandWebsocket() {
		out.println("create socket " + hashCode());

		_jobListener = new SymbolJobChangeAdapter() {
			@Override
			public void running(IJobChangeEvent event) {
				if (event.getJob() instanceof MFGJob) {
					sendConfigTreeNotification();
				}
			}

			@Override
			public void inputStopped(InputPipeChangeEvent event) {
				sendConfigTreeNotification();
			}

			@Override
			public void tradingChanged(TradingPipeChangeEvent event) {
				sendConfigTreeNotification();
			}

			@Override
			public void done(IJobChangeEvent event) {
				if (event.getJob() instanceof MFGJob) {
					sendConfigTreeNotification();
				}
			}
		};
		SymbolJob.getManager().addJobChangeListener(_jobListener);

		_storageListener = new WorkspaceStorageAdapter() {
			@Override
			public void objectAdded(IWorkspaceStorage sotarage, Object obj) {
				sendConfigTreeNotification();
			}

			@Override
			public void objectRemoved(IWorkspaceStorage storage, Object obj) {
				sendConfigTreeNotification();
			}
		};
		SymbolsPlugin.getDefault().getInputsStorage()
				.addStorageListener(_storageListener);
		SymbolsPlugin.getDefault().getTradingStorage()
				.addStorageListener(_storageListener);
		DFSSymbolsPlugin.getDefault().getDFSStorage()
				.addStorageListener(_storageListener);
	}

	protected void sendConfigTreeNotification() {
		if (_remote == null) {
			out.println("no clients connected");
			return;
		}
		JSONObject json = new JSONObject();
		try {
			json.put(CMD, CONFIG_TREE);
			makeConfigurationTree(json);

			String sendMsg = json.toString();
			out.println("send " + sendMsg);
			_remote.sendString(sendMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		_remote = sess.getRemote();
		out.println("socket connect " + sess.getRemoteAddress());
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		out.println("socket close " + statusCode + ' ' + reason + ' ' + _remote);

		_remote = null;

		// stop listen jobs
		SymbolJob.getManager().removeJobChangeListener(_jobListener);

		// stop listen storages
		SymbolsPlugin.getDefault().getInputsStorage()
				.removeStorageListener(_storageListener);
		SymbolsPlugin.getDefault().getTradingStorage()
				.removeStorageListener(_storageListener);
		DFSSymbolsPlugin.getDefault().getDFSStorage()
				.removeStorageListener(_storageListener);
	}

	@Override
	public void onWebSocketText(String message) {
		out.println("receive " + message);
		try {
			JSONObject json = new JSONObject(message);
			processCommand(json);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private void processCommand(JSONObject json) throws JSONException {
		String cmd = json.getString(CMD);
		switch (cmd) {
		case PLAY_CONFIG:
		case STOP_CONFIG:
			String configId = json.getString(ID);
			processPlayStopConfiguration(cmd, configId);
			break;
		case CONFIG_TREE:
			sendConfigTreeNotification();
			break;

		default:
			break;
		}
	}

	private static void processPlayStopConfiguration(String action,
			String configId) {
		UUID uuid = UUID.fromString(configId);
		IStorageObject config = PersistInterfacesPlugin.getDefault().findById(
				uuid);
		if (config != null) {
			switch (action) {
			case PLAY_CONFIG:
				// run the job at full speed during the warm-up.
				SymbolJob
						.runConfigurations(Arrays.asList(config), config, true);
				break;
			case STOP_CONFIG:
				SymbolJob.stopConfiguration(config);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Build the whole symbol tree.
	 * 
	 * @param jsonResp
	 *            The response.
	 * @throws JSONException
	 *             JSON error.
	 */
	private static void makeConfigurationTree(JSONObject jsonResp)
			throws JSONException {
		JSONArray jsonContContractArray = new JSONArray();
		jsonResp.put("continuousContracts", jsonContContractArray);
		DFSStorage storage = DFSSymbolsPlugin.getDefault().getDFSStorage();

		for (DFSConfiguration contContract : storage.getObjects()) {
			if (DFSStorage.isContinuousContract(contContract)) {
				// add continuous contract
				JSONObject jsonContContract = makeConfigurationContContractToJSON(contContract);
				jsonContContractArray.put(jsonContContract);
				// set the inputs
				setInputsToJSON(contContract, jsonContContract);

				// add maturities of the continuous contract
				JSONArray jsonMaturityArray = new JSONArray();
				jsonContContract.put("maturities", jsonMaturityArray);
				for (DFSConfiguration maturity : storage
						.findMaturitiesByPrefix(contContract.getInfo()
								.getPrefix())) {
					JSONObject jsonMaturity = makeConfigurationJSON(maturity);
					jsonMaturityArray.put(jsonMaturity);
					// set the inputs
					setInputsToJSON(maturity, jsonMaturity);
				}
			}
		}
	}

	private static JSONObject makeConfigurationContContractToJSON(
			DFSConfiguration contContract) throws JSONException {
		JSONObject result = makeConfigurationJSON(contContract);
		// reset the name to the prefix
		result.put("name", contContract.getInfo().getPrefix());
		return result;
	}

	private static void setInputsToJSON(DFSConfiguration conf,
			JSONObject jsonConf) throws JSONException {
		InputsStorage inputsStorage = SymbolsPlugin.getDefault()
				.getInputsStorage();
		TradingStorage tradingsStorage = SymbolsPlugin.getDefault()
				.getTradingStorage();

		// inputs
		InputConfiguration[] inputs = inputsStorage.findBySymbol(conf);
		JSONArray jsonInputArray = new JSONArray();
		jsonConf.put("inputs", jsonInputArray);
		for (InputConfiguration input : inputs) {
			JSONObject jsonInput = makeConfigurationJSON(input);
			jsonInputArray.put(jsonInput);

			// tradings
			List<TradingConfiguration> tradings = tradingsStorage
					.findByInput(input);
			JSONArray jsonTradingArray = new JSONArray();
			jsonInput.put("tradings", jsonTradingArray);
			for (TradingConfiguration trading : tradings) {
				JSONObject jsonTrading = makeConfigurationJSON(trading);
				jsonTradingArray.put(jsonTrading);
			}
		}
	}

	private static JSONObject makeConfigurationJSON(BaseConfiguration<?> symbol)
			throws JSONException {

		boolean running = SymbolJob.isConfigurationRunning(symbol);
		boolean enabled = running || SymbolJob.canRunConfiguration(symbol);

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", symbol.getName());
		jsonObj.put("id", symbol.getUUID().toString());
		jsonObj.put("running", running);
		jsonObj.put("enabled", enabled);

		return jsonObj;
	}
}
