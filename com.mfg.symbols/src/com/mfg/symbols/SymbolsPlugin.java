package com.mfg.symbols;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mfg.mdb.runtime.MDBSession;
import org.osgi.framework.BundleContext;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.strategy.AbstractStrategyFactory;
import com.mfg.strategy.IConfirmationRequest;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.persistence.ISymbolStorageReference;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobListener;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.persistence.TradingStorage;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.ObjectListenersGroup;
import com.mfg.utils.PartUtils;
import com.mfg.utils.PropertiesEx;
import com.mfg.utils.U;
import com.mfg.utils.Utils;

/**
 * The activator class controls the plug-in life cycle
 */
public class SymbolsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.symbols"; //$NON-NLS-1$
	public static final String SYMBOL_GROUP_IMAGE_PATH = "icons/folder.png"; //$NON-NLS-1$
	public static final String SYMBOL_IMAGE_PATH = "icons/symbol.png";//$NON-NLS-1$
	public static final String SYMBOL_CONFIG_IMAGE_PATH = "icons/symbol.png";//$NON-NLS-1$
	public static final String INPUT_IMAGE_PATH = "icons/input.png";
	public static final String STRATEGY_LOG_IMAGE_PATH = "icons/strategyLog.ico";
	public static final String PREF_MAX_NUM_SNAPSHOTS = "com.mfg.symbols.maxNumberOfSnapshots";
	private static final String STRATEGY_EXTENSION_POINT_ID = "com.mfg.symbols.strategies";
	private static final String TRADING_VIEW_EXTENSION_POINT_ID = "com.mfg.symbols.tradingViews";
	private static final Object TRADING_VIEW_EXTENSION_NAME = "tradingView";
	public static final String EMBEDDED_TEA = "com.mfg.symbols.embeddedTEA";
	public static final String DASHBOARD_IMAGE_PATH = "icons/dashboard.png";
	public static final String SND_ORDER = "sounds/order.wav";
	public static final String SND_EVENT = "sounds/event.wav";

	public static final String PREF_PLAY_SOUND_ON_ORDER_FILLED = "PLAY_SOUND_ON_ORDER_FILLED";
	public static final String PREF_SOUND_ON_ORDER_FILLED = "SOUND_ON_ORDER_FILLED";

	// The shared instance
	private static SymbolsPlugin plugin;

	private InputsStorage storage;
	private TradingStorage tradingStorage;

	private AbstractStrategyFactory[] strategyFactories;
	private Map<String, AbstractStrategyFactory> strategyFactoriesMap;
	private String[] tradingViewIds;

	private ConfigurationSetsManager setsManager;
	private PropertiesEx _extraProperties;

	/**
	 * The constructor
	 */
	public SymbolsPlugin() {
	}

	@SuppressWarnings("unchecked")
	public String getFullConfigurationName(IStorageObject obj) {
		if (obj instanceof TradingConfiguration) {
			return getFullConfigurationName(getParentInputConfigurtion((TradingConfiguration) obj))
					+ " / " + obj.getName();
		} else if (obj instanceof InputConfiguration) {
			return getFullConfigurationName(getParentSymbolConfigurtion((InputConfiguration) obj))
					+ " / " + obj.getName();
		} else if (obj instanceof SymbolConfiguration) {
			SymbolConfiguration<?, SymbolConfigurationInfo<?>> symbol = (SymbolConfiguration<?, SymbolConfigurationInfo<?>>) obj;
			return symbol.getFullName();
		} else if (obj == null) {
			return "";
		}
		return obj.getName();
	}

	public InputConfiguration getParentInputConfigurtion(
			TradingConfiguration trading) {
		return getInputsStorage().findById(
				trading.getInfo().getInputConfiguratioId());
	}

	public static SymbolConfiguration<?, ?> getParentSymbolConfigurtion(
			InputConfiguration input) {
		return (SymbolConfiguration<?, ?>) PersistInterfacesPlugin.getDefault()
				.findById(input.getInfo().getSymbolId());
	}

	/**
	 * @return the setManager
	 */
	public ConfigurationSetsManager getSetsManager() {
		if (setsManager == null) {
			setsManager = new ConfigurationSetsManager();
		}
		return setsManager;
	}

	public String[] getTradingViewIds() {
		if (tradingViewIds == null) {
			List<String> list = new ArrayList<>();
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							TRADING_VIEW_EXTENSION_POINT_ID);
			for (IConfigurationElement elem : elements) {
				if (elem.getName().equals(TRADING_VIEW_EXTENSION_NAME)) {
					String viewId = elem.getAttribute("viewId");
					list.add(viewId);
				}
			}
			tradingViewIds = list.toArray(new String[list.size()]);
		}

		return tradingViewIds;
	}

	public static List<ITradingView> getOpenTradingViews(String viewId) {
		List<ITradingView> tradingViews = new ArrayList<>();
		List<IViewPart> views = PartUtils.getOpenViews(viewId);
		IAdapterManager manager = Platform.getAdapterManager();
		for (IViewPart view : views) {
			ITradingView tradingView = (ITradingView) manager.getAdapter(view,
					ITradingView.class);
			if (tradingView != null) {
				tradingViews.add(tradingView);
			}
		}
		return tradingViews;
	}

	public List<ITradingView> getOpenTradingViews() {
		List<ITradingView> list = new ArrayList<>();
		for (String id : getTradingViewIds()) {
			for (ITradingView view : getOpenTradingViews(id)) {
				list.add(view);
			}
		}
		return list;
	}

	public InputsStorage getInputsStorage() {
		if (storage == null) {
			storage = new InputsStorage();
		}
		return storage;
	}

	public TradingStorage getTradingStorage() {
		if (tradingStorage == null) {
			tradingStorage = new TradingStorage();
		}
		return tradingStorage;
	}

	public AbstractStrategyFactory[] getStrategyFactories() {
		if (strategyFactories == null) {
			List<IStrategyFactory> list = new ArrayList<>();
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(STRATEGY_EXTENSION_POINT_ID);
			for (IConfigurationElement elem : elements) {
				if (elem.getName().equals(IStrategyFactory.EXTENSION_NAME)) {
					try {
						AbstractStrategyFactory factory = (AbstractStrategyFactory) elem
								.createExecutableExtension("class");
						list.add(factory);
						if (strategyFactoriesMap == null) {
							strategyFactoriesMap = new HashMap<>();
						}
						strategyFactoriesMap.put(factory.getId(), factory);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			strategyFactories = list.toArray(new AbstractStrategyFactory[list
					.size()]);
		}
		return strategyFactories;
	}

	public IStrategyFactory getStrategyFactory(String strategyId) {
		return strategyFactoriesMap.get(strategyId);
	}

	/**
	 * @param configuration
	 * @return
	 */
	public static File getInputDatabaseRoot(InputConfiguration configuration) {
		return new File(Platform.getInstanceLocation().getURL().getFile(),
				configuration.getInfo().getDatabasePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(PREF_MAX_NUM_SNAPSHOTS, 3);
		store.setDefault(EMBEDDED_TEA, true);
		store.setDefault(PREF_PLAY_SOUND_ON_ORDER_FILLED, true);
		store.setDefault(PREF_SOUND_ON_ORDER_FILLED, UIPlugin.SOUND_BUZZ);
		SymbolJob.getManager().addJobChangeListener(new SymbolJobListener());
	}

	private static String _getPropertiesPath() {
		return System.getProperty("user.home") + "/mfg/symbolProperties.txt";
	}

	public synchronized static void deleteTemporalDatabases() {
		long t = System.currentTimeMillis();
		URL url = Platform.getInstanceLocation().getURL();
		if (url != null) {
			File dbsDir = new File(url.getFile(), "ChartDatabases");
			if (dbsDir.exists()) {
				File[] list = dbsDir.listFiles();
				for (File dbDir : list) {
					if (dbDir.isDirectory()) {
						if (ChartMDBSession.isTemporal(dbDir)) {
							out.println("Deleting temporal db " + dbDir);
							MDBSession.delete(dbDir);
						}
					}
				}
			}
		}
		Utils.debug_id(
				390636,
				"Deleting temporal chart databases "
						+ (System.currentTimeMillis() - t) + "ms");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		// String path = _getPropertiesPath();
		U.debug_var(294851,
				"Stopping the symbol plugin without writing the extra properties");
		// if (_extraProperties != null) {
		// _extraProperties.store(out, "extra properties");
		// }
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SymbolsPlugin getDefault() {
		return plugin;
	}

	private List<IConfirmationRequest> confirmationsRequests;
	private ObjectListenersGroup<IConfirmationRequest> confirmationsRequestsAdded = new ObjectListenersGroup<>();
	private ArrayList<SimpleStorage<?>> symbolStorages;

	public List<IConfirmationRequest> getConfirmationsRequests() {
		if (confirmationsRequests == null)
			confirmationsRequests = new ArrayList<>();
		return confirmationsRequests;
	}

	public void setConfirmationsRequests(
			List<IConfirmationRequest> aConfirmationsRequests) {
		confirmationsRequests = aConfirmationsRequests;
	}

	// public void addConfirmationRequest(
	// OrderConfirmationRequest aOrderConfirmationRequest) {
	// getConfirmationsRequests().add(aOrderConfirmationRequest);
	// confirmationsRequestsAdded.handle(aOrderConfirmationRequest);
	// }

	public ObjectListenersGroup<IConfirmationRequest> getConfirmationsRequestsAdded() {
		return confirmationsRequestsAdded;
	}

	// public void setConfirmationsRequestsAdded(
	// ObjectListenersGroup<IConfirmationRequest> aConfirmationsRequestsAdded) {
	// confirmationsRequestsAdded = aConfirmationsRequestsAdded;
	// }

	public List<SimpleStorage<?>> getSymbolStorages() {
		if (symbolStorages == null) {
			symbolStorages = new ArrayList<>();
			List<IWorkspaceStorageReference> storageRefrences = PersistInterfacesPlugin
					.getDefault().getStorageRefrences();
			for (IWorkspaceStorageReference s : storageRefrences) {
				if (s instanceof ISymbolStorageReference) {
					symbolStorages.add(s.getStorage());
				}
			}
		}
		return symbolStorages;
	}

	public static List<ISymbolStorageReference> getSymbolStorageReferences() {
		List<ISymbolStorageReference> list = new ArrayList<>();
		List<IWorkspaceStorageReference> storageRefrences = PersistInterfacesPlugin
				.getDefault().getStorageRefrences();
		for (IWorkspaceStorageReference ref : storageRefrences) {
			if (ref instanceof ISymbolStorageReference) {
				list.add((ISymbolStorageReference) ref);
			}
		}
		return list;
	}

	public SymbolConfiguration<?, ?> findSymbolConfiguration(UUID id) {
		for (SimpleStorage<?> s : getSymbolStorages()) {
			SymbolConfiguration<?, ?> result = (SymbolConfiguration<?, ?>) s
					.findById(id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public PropertiesEx getProperties() {
		if (_extraProperties == null) {
			_extraProperties = new PropertiesEx();
			String path = _getPropertiesPath();
			try (FileInputStream file = new FileInputStream(path);) {
				_extraProperties.load(file);
			} catch (Exception e) {
				U.debug_var(293284, "Cannot find file ", path,
						" starting with null properties.");
			}
		}
		return _extraProperties;
	}
}
