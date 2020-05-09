package com.mfg.chart;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.chart.model.Model_MDB;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.profiles.ProfileManager2;
import com.mfg.chart.profiles.ProfileSet;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class ChartPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.chart"; //$NON-NLS-1$
	public static final String PREFERENCES_REALTIME_UPDATE_ON_TICK_SLEEP_VALUE = "realTime.updateOnTickSleepValue";
	public static final String PREFERENCES_ZOOM_OUT_ALL_BLANK_PERCENT = "chart.zoomOutAllBlackPercent";

	/**
	 * Start scrolling: If we set this to 10 (default), it means that when the
	 * chart arrives at last 10% to the right of the screen, the scrolling will
	 * start.
	 */
	public static final String PREFERENCES_START_SCROLLING_PERCENT = "chart.startScrollingPercent";

	public static final String PREFERENCES_ZOOM_WHEEL_PERCENT = "chart.zoomWheelPercent";

	/**
	 * Stop scrolling: If we set this to 50 (default) it means that once price
	 * arrives to the 10%, the chart should scroll and place the last price to
	 * the 50% of the chart (in the middle basically).
	 */
	public static final String PREFERENCES_STOP_SCROLLING_PERCENT = "chart.stopScrollingPercent";

	public static final String PREFERENCES_MAX_NUMBER_OF_POINTS_TO_SHOW = "chart.maxNumberOfPointsToShow";
	public static final String PREFERENCES_PROFILES = "chart.profiles_v2";
	public static final String PREFERENCES_PROFILES_DEFAULT = "chart.profiles.default";
	public static final String LOGGER_REALTIME_CHATR_COMPONENT_ID = "com.mfg.chart.logger.realTimeChart";
	public static final String PREFERENCES_MAX_NUMBER_OF_POSITIONS_TO_SHOW = "chart.maxNumberOfPositionsToShow";
	public static final String CHART_ICON_PATH = "icons/trade.jpg";
	public static final String PREFERENCES_FRAMES_PER_SECOND = "chart.framesPerSecond";

	// The shared instance
	private static ChartPlugin plugin;

	private IPropertyChangeListener prefsListener;

	private ProfileSet profiles;
	private InteractiveToolFactory[] toolFactories;
	private ProfileManager2 _profileManager2;

	/**
	 * The constructor
	 */
	public ChartPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		initPreferences();
	}

	public InteractiveToolFactory[] getInteractiveToolFactories() {
		if (toolFactories == null) {
			List<InteractiveToolFactory> list = new ArrayList<>();
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor("com.mfg.chart.tools");
			for (IConfigurationElement elem : elements) {
				try {
					InteractiveToolFactory factory = (InteractiveToolFactory) elem
							.createExecutableExtension("class");
					list.add(factory);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			toolFactories = list
					.toArray(new InteractiveToolFactory[list.size()]);
		}
		return toolFactories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		if (prefsListener != null) {
			getPreferenceStore().removePropertyChangeListener(prefsListener);
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ChartPlugin getDefault() {
		return plugin;
	}

	public ProfileManager2 getProfileManager2() {
		return _profileManager2;
	}

	/**
	 *
	 */
	private void initPreferences() {
		final IPreferenceStore store = getPreferenceStore();
		store.setDefault(PREFERENCES_REALTIME_UPDATE_ON_TICK_SLEEP_VALUE, 100);
		store.setDefault(PREFERENCES_ZOOM_OUT_ALL_BLANK_PERCENT, 10);
		store.setDefault(PREFERENCES_MAX_NUMBER_OF_POINTS_TO_SHOW,
				Model_MDB.getMaxNumberOfPointsToShow());
		store.setDefault(PREFERENCES_MAX_NUMBER_OF_POSITIONS_TO_SHOW, 200);
		store.setDefault(PREFERENCES_START_SCROLLING_PERCENT, 10);
		store.setDefault(PREFERENCES_STOP_SCROLLING_PERCENT, 50);
		store.setDefault(PREFERENCES_PROFILES_DEFAULT,
				ProfileSet.DEFAULT_PROFILE_NAME);
		store.setDefault(PREFERENCES_FRAMES_PER_SECOND, 24);
		store.setDefault(PREFERENCES_ZOOM_WHEEL_PERCENT, 50);

		try {
			final String xml = store.getString(PREFERENCES_PROFILES);
			if (xml.length() == 0) {
				profiles = new ProfileSet();
				profiles.addProfile(new Profile(ProfileSet.DEFAULT_PROFILE_NAME));
				store.setDefault(PREFERENCES_PROFILES, profiles.toXML());
			} else {
				profiles = ProfileSet.fromXML(xml);
				// Comment this because now the default profile is not reseted:
				// String name = store.getString(PREFERENCES_PROFILES_DEFAULT);
				// Profile defaultProfile = profiles.findProfile(name);
				// defaultProfile.clear();
				store.setValue(PREFERENCES_PROFILES, profiles.toXML());
			}
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

		prefsListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				Model_MDB.setMaxNumberOfPointsToShow(store
						.getInt(PREFERENCES_MAX_NUMBER_OF_POINTS_TO_SHOW));
			}
		};
		store.addPropertyChangeListener(prefsListener);
		prefsListener.propertyChange(null);

		_profileManager2 = new ProfileManager2(store);
	}

	public static ImageDescriptor getBundledImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

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

	public void saveProfiles() {
		String xml;
		try {
			xml = profiles.toXML();
			getPreferenceStore().setValue(PREFERENCES_PROFILES, xml);
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the profiles
	 */
	public ProfileSet getProfiles() {
		return profiles;
	}
}
