package com.mfg.symbols.dfs.persistence;

import static java.lang.System.out;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsEmptyDatabaseException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.Slot;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.IAppLogger;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.configurations.DFSConfigurationInfo;
import com.mfg.symbols.dfs.configurations.DFSSymbolData;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.thoughtworks.xstream.XStream;

public class DFSStorage extends SimpleStorage<DFSConfiguration> implements
		IDFSObserver {
	public static final String LOGGER_ID = "com.mfg.symbols.dfs.logger";

	protected IDFS _dfs;
	private HashMap<DFSConfiguration, MaturityStats> _confStatsMap;
	private List<Runnable> _updateListeners;
	List<DfsSymbol> _dfsSymbols;
	private Map<String, MaturityStats> _localSymbolStatMap;

	private IAppLogger _logger;

	protected boolean _ready;

	private boolean _updating;

	public DFSStorage() {
		_ready = false;
		_logger = LoggerPlugin.getDefault().getAppLogger(DFSStorage.LOGGER_ID,
				"Storage");
		_dfsSymbols = new ArrayList<>();
		_updateListeners = new ArrayList<>();
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(IDFS dfs) throws DFSException {
					synchronized (DFSStorage.this) {
						_dfs = dfs;
						updateContent();
						_dfs.addObserver(DFSStorage.this);
						_ready = true;
					}
				}

				@Override
				public void notReady() {
					//
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		SymbolsPlugin.getDefault().getInputsStorage()
				.addStorageListener(new WorkspaceStorageAdapter() {
					@Override
					public void objectAdded(IWorkspaceStorage sotarage,
							Object obj) {
						InputConfiguration input = (InputConfiguration) obj;
						DFSConfiguration conf = findById(input.getInfo()
								.getSymbolId());
						if (conf != null) {
							updateInput(conf, input);
						}
					}
				});
	}

	public void updateMaturityStats(String localSymbol, MaturityStats stats) {
		_localSymbolStatMap.put(localSymbol, stats);
	}

	public void addUpdateTreeListener(Runnable l) {
		synchronized (_updateListeners) {
			_updateListeners.add(l);
		}
	}

	public void removeUpdateTreeListener(Runnable l) {
		synchronized (_updateListeners) {
			_updateListeners.remove(l);
		}
	}

	@Override
	public void configureXStream(XStream xstream) {
		super.configureXStream(xstream);
		configureXStream2(xstream);
	}

	public static void configureXStream2(XStream xstream) {
		xstream.alias("dfs-config", DFSConfiguration.class);
		xstream.alias("dfs-config-info", DFSConfigurationInfo.class);
		xstream.alias("dfs-symbol", DFSSymbolData.class);
		xstream.alias("dfs-historical-data", DFSHistoricalDataInfo.class);
		xstream.omitField(DFSHistoricalDataInfo.class, "_new");
		xstream.alias("dfs-historical-data-slot",
				DFSHistoricalDataInfo.Slot.class);
		xstream.alias("dfs-historical-data-info", DFSHistoricalDataInfo.class);
		xstream.alias("dfs-historical-data-info-slot",
				DFSHistoricalDataInfo.Slot.class);
		xstream.omitField(DFSHistoricalDataInfo.Slot.class, "_numFormat");
		xstream.alias("bar-type", BarType.class);
		xstream.alias("interval-stats", DfsIntervalStats.class);

	}

	protected synchronized void updateContent() throws DFSException {
		out.println("update content");
		try {
			_updating = true;
			_logger.logComment("Start configurations update");

			_dfsSymbols = new ArrayList<>(_dfs.getSymbolsList().symbols);

			// maps
			_confStatsMap = new HashMap</* DFSConfiguration, MaturityStats */>();
			Map<MaturityStats, DfsSymbol> statSymbolMap = new HashMap<>();
			Map<String, DFSConfiguration> confMap = new HashMap<>();

			// build local-symbol/contract map
			_localSymbolStatMap = new HashMap<>();
			for (DfsSymbol symbol : _dfsSymbols) {
				String prefix = symbol.prefix;
				DfsSymbolStatus status = _dfs.getStatusForSymbol(prefix);

				// put continuous contract
				String k = getLocalSymbol(prefix, status.continuousStats);
				_localSymbolStatMap.put(k, status.continuousStats);
				// map stat with symbol
				statSymbolMap.put(status.continuousStats, symbol);
				for (MaturityStats stat : status.maturityStats) {
					// put single contract
					k = getLocalSymbol(prefix, stat);
					_localSymbolStatMap.put(k, stat);
					// map stat with symbol
					statSymbolMap.put(stat, symbol);
				}
			}

			// build local-symbol/configurations map
			for (DFSConfiguration conf : getObjects()) {
				String k = conf.getInfo().getSymbol().getLocalSymbol();
				confMap.put(k, conf);
			}

			// build configuration/contract map
			for (DFSConfiguration conf : getObjects()) {
				String k = conf.getInfo().getSymbol().getLocalSymbol();
				MaturityStats stat = _localSymbolStatMap.get(k);
				_confStatsMap.put(conf, stat);
			}

			// add new configurations
			Set<String> confSet = new HashSet<>();
			for (DFSConfiguration conf : getObjects()) {
				confSet.add(conf.getInfo().getSymbol().getLocalSymbol());
			}
			for (String localSymbol : _localSymbolStatMap.keySet()) {
				DFSConfiguration conf;
				MaturityStats stat = _localSymbolStatMap.get(localSymbol);
				DfsSymbol dfsSymbol = statSymbolMap.get(stat);
				if (confSet.contains(localSymbol)) {
					conf = confMap.get(localSymbol);
				} else {
					// add new configuration
					conf = new DFSConfiguration();
					getObjects().add(conf);
					_confStatsMap.put(conf, stat);
				}
				// update the configuration properties
				conf.setName(getConfigurationName(dfsSymbol, stat));
				DFSConfigurationInfo info = conf.getInfo();
				info.setPrefix(dfsSymbol.prefix);
				DFSSymbolData sym = info.getSymbol();
				sym.setLocalSymbol(localSymbol);
				sym.setType(dfsSymbol.type);

				sym.setCurrency(dfsSymbol.currency);
				sym.setTickValue(dfsSymbol.tickValue);
				info.setIntervalMap(stat._map);
				BigInteger unscaledVal = new BigInteger(
						Integer.toString(dfsSymbol.tick));
				sym.setRealTickSize(new BigDecimal(unscaledVal, dfsSymbol.scale));
			}

			// update configurations
			for (DFSConfiguration conf : getObjects()) {
				_logger.logComment("Updating " + conf.getInfo().getPrefix()
						+ " / " + conf.getName());
				DFSHistoricalDataInfo histInfo = (DFSHistoricalDataInfo) conf
						.getInfo().getHistoricalDataInfo();
				MaturityStats stat = _confStatsMap.get(conf);
				updateHistoricalInfo(conf.getInfo().getSymbol()
						.getLocalSymbol(), histInfo, stat);
			}

			// update inputs
			for (DFSConfiguration conf : getObjects()) {
				InputsStorage inputsStorage = SymbolsPlugin.getDefault()
						.getInputsStorage();
				InputConfiguration[] inputs = inputsStorage.findBySymbol(conf);
				for (InputConfiguration input : inputs) {
					_logger.logComment("Updating input "
							+ conf.getInfo().getPrefix() + " / "
							+ conf.getName() + " / " + input.getName());
					updateInput(conf, input);
				}
			}
		} finally {
			_updating = false;
			fireUpdated();
		}
		out.println("end update content");

		removeDirtyConfigurations();
	}

	private void removeDirtyConfigurations() {
		// after 2 second, remove the configurations are not present in DFS,
		// this gives time to the UI to load and then close the editors etc..
		new Job("Remove dirty configurations") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				Set<DFSConfiguration> keepList = new HashSet<>();
				for (DfsSymbol symbol : _dfsSymbols) {
					keepList.addAll(findByPrefix(symbol.prefix));
				}
				monitor.beginTask("Removing",
						getObjects().size() - keepList.size());
				// now remove everybody is not present in the keep list
				for (DFSConfiguration conf : new ArrayList<>(getObjects())) {
					if (!keepList.contains(conf)) {
						try {
							out.println("Remove dirty configuration "
									+ conf.getName());
							remove(conf);
							monitor.worked(1);
						} catch (RemoveException e) {
							e.printStackTrace();
						}
					}
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	protected void updateInput(DFSConfiguration conf, InputConfiguration input) {
		MaturityStats stat = _confStatsMap.get(conf);
		DFSHistoricalDataInfo histInfo = (DFSHistoricalDataInfo) input
				.getInfo().getHistoricalDataInfo();
		updateHistoricalInfo(conf.getInfo().getSymbol().getLocalSymbol(),
				histInfo, stat);
	}

	private static String getConfigurationName(DfsSymbol dfsSymbol,
			MaturityStats stat) {
		return stat.getMaturity() == null ? dfsSymbol.prefix
				+ " Continuous Contract" : stat.getMaturity().toFileString();
	}

	public synchronized boolean isReady() {
		return _ready;
	}

	public void runWhenReady(final IDFSRunnable run) {
		if (_ready) {
			try {
				run.run(_dfs);
			} catch (DFSException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			run.notReady();
			new Thread() {
				@Override
				public void run() {
					while (!_ready) {
						try {
							sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						run.run(_dfs);
					} catch (DFSException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}.start();
		}
	}

	private void fireUpdated() {
		final List<Runnable> list;
		synchronized (_updateListeners) {
			list = new ArrayList<>(_updateListeners);
		}
		Job job = new Job("Updating DFS UI") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Updating DFS UI", list.size());
				for (Runnable l : list) {
					try {
						l.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
					monitor.worked(1);
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private void updateHistoricalInfo(String localSymbol,
			DFSHistoricalDataInfo histInfo, MaturityStats stat) {
		if (stat == null)
			return;
		for (Slot slot1 : histInfo.getSlots()) {
			BarType barType = slot1.getBarType();
			DfsIntervalStats slot2 = stat._map.get(barType);
			boolean used = histInfo.isUsed();
			if (used) {
				_logger.logComment("Historical data moves to the last date");
				// move to the end date but keep the number of days
				long time = slot1.getEndDate().getTime()
						- slot1.getStartDate().getTime();
				out.println("Updating " + localSymbol + "@"
						+ slot1.getBarType());
				out.println("Keep " + TimeUnit.MILLISECONDS.toDays(time)
						+ " days");

				slot1.setEndDate(new Date(slot2.endDate));
				slot1.setStartDate(new Date(slot2.endDate - time));

				int nunits = slot1.getNumbeOfUnits();
				long newstart = slot1.getStartDate().getTime();
				long newend = slot1.getEndDate().getTime();

				int numBars;
				try {
					out.println("getBarsBetween(" + localSymbol + ")");
					numBars = _dfs.getBarsBetween(localSymbol, barType, nunits,
							newstart, newend);
				} catch (DfsEmptyDatabaseException e) {
					numBars = 0;
				} catch (Exception e) {
					e.printStackTrace();
					numBars = 0;
				}

				slot1.setNumberOfBars(numBars);
			} else {
				_logger.logComment("Historical data gets all the days");
				// get all the days
				slot1.setStartDate(new Date(slot2.startDate));
				slot1.setEndDate(new Date(slot2.endDate));
				slot1.setNumberOfBars(slot2.numBars);
			}
		}
	}

	public Slot createNewSlot(DFSConfiguration conf) {
		Slot slot1 = new Slot();
		MaturityStats stat = _confStatsMap.get(conf);
		DfsIntervalStats slot2 = stat._map.get(slot1.getBarType());
		slot1.setStartDate(new Date(slot2.startDate));
		slot1.setEndDate(new Date(slot2.endDate));
		slot1.setNumberOfBars(slot2.numBars);
		return slot1;
	}

	public List<DFSConfiguration> findMaturitiesByPrefix(String prefix) {
		List<DFSConfiguration> list = new ArrayList<>();
		for (DFSConfiguration conf : getObjects()) {
			if (conf.getInfo().getPrefix().equals(prefix)
					&& !isContinuousContract(conf)) {
				list.add(conf);
			}
		}
		return list;
	}

	public List<DFSConfiguration> findByPrefix(String prefix) {
		List<DFSConfiguration> list = new ArrayList<>();
		for (DFSConfiguration conf : getObjects()) {
			if (conf.getInfo().getPrefix().equals(prefix)) {
				list.add(conf);
			}
		}
		return list;
	}

	@Override
	public DFSConfiguration createDefaultObject() {
		return new DFSConfiguration();
	}

	public static String getLocalSymbol(String prefix, MaturityStats stat) {
		return prefix
				+ (stat.getMaturity() == null ? Maturity.CONTINUOUS_SUFFIX
						: stat.getMaturity().toDataProviderMediumString());
	}

	public List<DfsSymbol> getDfsSymbols() {
		return _dfsSymbols;
	}

	public MaturityStats lookupMaturityStats(DFSConfiguration conf) {
		return _localSymbolStatMap.get(conf.getInfo().getSymbol()
				.getLocalSymbol());
	}

	public static boolean isContinuousContract(DFSConfiguration conf) {
		String confLocalSymbol = conf.getInfo().getSymbol().getLocalSymbol();
		return confLocalSymbol.contains(Maturity.CONTINUOUS_SUFFIX);
	}

	public DFSConfiguration lookupContinuousContract(String prefix) {
		for (DFSConfiguration conf : getObjects()) {
			String confPrefix = conf.getInfo().getPrefix();
			if (confPrefix.equals(prefix) && isContinuousContract(conf)) {
				return conf;
			}
		}
		return null;
	}

	public static void sortSlots(List<Slot> slots, IAppLogger logger) {
		ArrayList<Slot> old = new ArrayList<>(slots);
		Collections.sort(slots);
		boolean orderChanged = false;
		for (int i = 0; i < old.size(); i++) {
			if (old.get(i) != slots.get(i)) {
				orderChanged = true;
			}
		}
		if (orderChanged) {
			logger.logComment("    Some slots was reordered:");
			for (int i = 0; i < old.size(); i++) {
				Slot s1 = old.get(i);
				Slot s2 = slots.get(i);
				if (s1 == s2) {
					logger.logComment("        %s", s1);
				} else {
					logger.logComment("        %s --> %s", s1, s2);
				}
			}
		}
	}

	@Override
	public String getFileName(DFSConfiguration obj) {
		return obj.getInfo().getPrefix() + "-" + obj.getName();
	}

	@Override
	public String getStorageName() {
		return "DFS-Symbol-Configuration-2";
	}

	@Override
	public void onSymbolInitializationEnded(String symbol) {
		try {
			updateContent();
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onSchedulerStartRunning() {
		// do nothing
	}

	@Override
	public void onSchedulerEndedCycle() {
		// do nothing
	}

	public boolean isUpdating() {
		return _updating;
	}

}