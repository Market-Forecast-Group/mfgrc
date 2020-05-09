package com.mfg.web.servlets;

import static java.lang.System.out;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.JSONObject;

import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.jobs.DFSJob;

public abstract class AbstractDataWebsocket extends WebSocketAdapter {
	private static final String CMD = "cmd";

	protected RemoteEndpoint _remote;
	private List<Runnable> _removeListenerActions;

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);

		_removeListenerActions = new ArrayList<>();

		out.println("connected socket " + this);
		_remote = sess.getRemote();
	}

	protected void addRemoveListenerAction(Runnable action) {
		_removeListenerActions.add(action);
	}

	private void executeRemoveListenerActions() {
		for (Runnable action : _removeListenerActions) {
			action.run();
		}
		_removeListenerActions.clear();
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		out.println("closed sockect " + this + " [" + statusCode + "]: "
				+ reason);

		out.println("remove listeners");
		executeRemoveListenerActions();

		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketText(String message) {
		out.println("receive " + message);
		try {
			JSONObject json = new JSONObject(message);
			String cmd = json.getString(CMD);
			switch (cmd) {
			case "data":
				processDataRequest(json);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected void processDataRequest(JSONObject json) throws Exception {
		String configId = json.getString("id");
		Object config = SimpleStorage.findByIdInStorages(configId,
				DFSSymbolsPlugin.getDefault().getDFSStorage(), SymbolsPlugin
						.getDefault().getInputsStorage());
		if (config != null) {
			// find the job of that config
			Job[] jobs = Job.getJobManager().find(config);
			DFSJob job = null;
			if (jobs != null && jobs.length == 1) {
				job = (DFSJob) jobs[0];
			}

			if (job != null) {
				// send all the available data
				processDataRequest(job, config);
			}
		}
	}

	protected abstract void processDataRequest(DFSJob job, Object config)
			throws Exception;

	protected enum ObjectType {
		PRICE, PIVOT, BANDS
	}

	protected static ByteBuffer createBuffer(ObjectType type, int dataLayer,
			int scales, int recordByteSize, int recordCount) {
		ByteBuffer buffer = ByteBuffer.allocate(recordByteSize * recordCount
				+ 3);
		writeBufferHeader(type, dataLayer, scales, buffer);
		return buffer;
	}

	protected static void writeBufferHeader(ObjectType type, int dataLayer,
			int scales, ByteBuffer buffer) {
		buffer.put((byte) type.ordinal());
		buffer.put((byte) dataLayer);
		buffer.put((byte) scales);
	}

	protected void sendJSON(JSONObject json) throws IOException {
		_remote.sendString(json.toString());
	}
}
