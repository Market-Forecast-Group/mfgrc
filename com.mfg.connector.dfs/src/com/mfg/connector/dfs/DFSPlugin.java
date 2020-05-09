package com.mfg.connector.dfs;

import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.common.DFSException;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.DfsDataProvider;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.ServerFactory;
import com.mfg.dfs.misc.Service;

public class DFSPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.mfg.connector.dfs";

	public static final String CONNECT_TO_SIMULATOR = "com.mfg.connector.dfs.connect_to_simulator";
	public static final String USE_PROXY = "com.mfg.connector.dfs.useProxy";
	public static final String DFS_REMOTE_ADDRESS = "com.mfg.connector.dfs.remoteAddress";

	public static final String ENABLE_MIXED_MODE = "com.mfg.connector.dfs.enable_mixed_mode";

	public static final String DFS_LOCAL_ROOT = "com.mfg.connector.dfs.localRoot";

	/**
	 * A temporary solution to have multiple symbols in the same directory. Now
	 * in the workspace the user can choose a directory prefix which is used to
	 * distinguish different folders, usually this prefix is empty, but can be
	 * changed. The directory where the symbol will stay will be
	 * ${prefix}.dfsStorage.
	 */
	public static final String DFS_DIR_PREFIX = "com.mfg.connector.dfs.dfs_dir_prefix";

	/**
	 * Returns an image for the image file at the given plug-in relative path.
	 * Client do not need to dispose this image. Images will be disposed
	 * automatically.
	 * 
	 * @param path
	 *            the path
	 * @return image instance
	 */
	public Image getBundledImage(final String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getBundledImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getBundledImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private static DFSPlugin instance = null;
	private DfsDataProvider _provider;

	IDFS _dfs;
	final List<IDFSRunnable> _dfsRunnables = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		DFSPlugin.context = bundleContext;
		instance = this;

		IPreferenceStore prefStore = getPreferenceStore();
		prefStore.setDefault(CONNECT_TO_SIMULATOR, false);
		prefStore.setDefault(DFS_REMOTE_ADDRESS, "mfgserver.bounceme.net");

		String productId = Platform.getProduct().getId();
		out.println("Running product " + productId);
		boolean dfsProduct = productId
				.equals("com.marketforecastgroup.dfsa.product");
		prefStore.setDefault(ENABLE_MIXED_MODE, dfsProduct);
		prefStore.setDefault(DFS_LOCAL_ROOT, System.getProperty("user.home"));
		prefStore.setDefault(DFS_DIR_PREFIX, "");

		String dfsPrefix = prefStore.getString(DFS_DIR_PREFIX);
		MfgMdbSession.setDfsPrefix(dfsPrefix);

		String root = prefStore.getString(DFS_LOCAL_ROOT);
		MfgMdbSession.setSessionRoot(root);

		IPropertyChangeListener restartListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == USE_PROXY) {
					boolean confirm = MessageDialog
							.openConfirm(
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getShell(),
									"Restart",
									"The DFS connection mode was changed. To take this change effect, the system should be restarted. Do you want to restart it now?");
					if (confirm) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								IPersistentPreferenceStore s = (IPersistentPreferenceStore) prefStore;
								try {
									s.save();
									PlatformUI.getWorkbench().restart();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}

				}
			}
		};
		prefStore.addPropertyChangeListener(restartListener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		DFSPlugin.context = null;
		if (_provider != null) {
			_provider.switchOff(); // force the stopping.
		}
		instance = null;
		ServerFactory.disposingServer();
	}

	public static DFSPlugin getDefault() {
		return instance;
	}

	/**
	 * I get the data provider; it is a data provider which is in some way
	 * different for every run.
	 * 
	 * <p>
	 * TBD: is it a singleton?
	 * 
	 * @return the data provider.
	 */
	public synchronized DfsDataProvider getDataProvider() {
		if (_provider == null) {

			IPreferenceStore pref = DFSPlugin.getDefault().getPreferenceStore();
			boolean useSimulator = pref
					.getBoolean(DFSPlugin.CONNECT_TO_SIMULATOR);

			boolean dfsRemote = pref.getBoolean(DFSPlugin.USE_PROXY);

			String remoteAddress;
			remoteAddress = pref.getString(DFSPlugin.DFS_REMOTE_ADDRESS);

			if (remoteAddress.length() == 0) {
				remoteAddress = "localhost";
			}

			boolean isMixedMode = pref.getBoolean(DFSPlugin.ENABLE_MIXED_MODE);

			_provider = new DfsPluginDataProvider(useSimulator, dfsRemote,
					remoteAddress, isMixedMode);
		}
		return _provider;
	}

	List<IDFSRunnable> getDfsRunnables() {
		return _dfsRunnables;
	}

	public boolean isDFSReady() {
		return _dfs != null;
	}

	public void runWithDFS(IDFSRunnable runnable) throws DFSException {
		synchronized (_dfsRunnables) {
			if (_dfs == null) {
				runnable.notReady();
				_dfsRunnables.add(runnable);
			} else {
				runnable.run(_dfs);
			}
		}
	}

	/**
	 * Use this method to access directly to the DFS cache. This should be used
	 * only inside DFS related components. You should be sure that a DFS
	 * instance is available. To know this, you can use the
	 * {@link #runWithDFS(IDFSRunnable)} method.
	 * 
	 * @return
	 */
	public DfsCacheRepo getCache() {
		return ((Service) _dfs).getModel().getCache();
	}

	void setDfs(IDFS dfs) {
		synchronized (_dfsRunnables) {
			_dfs = dfs;
		}
	}

}
