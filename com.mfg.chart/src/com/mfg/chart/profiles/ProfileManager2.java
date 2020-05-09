package com.mfg.chart.profiles;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.utils.PartUtils;

/**
 * For now this is used by tools.
 * 
 * <p>
 * 1) I would create a specific profile for lines like TL and I would NOT use
 * those in S window which are ok for zz, bands ... In the TL settings, we can
 * add 3 buttons: Save Profile, Save Profile As and Delete Profile. Then we can
 * also add a drop down list to select the profile we want to use. <br>
 * a. The Save Profile implies that we want to save the profile that is
 * currently selected.
 * </p>
 * <p>
 * b. The Save Profile As implies that we want to save the changes using either
 * another profile name that we have already created OR a new name. <br>
 * c. The Delete Profile obviously implies that we want to delete the current
 * active profile. When we delete the current profile, we are automatically
 * going to switch to the Default profile (which I would like to rename as Main
 * Profile ... see more below). This profile can be modified BUT not deleted. <br>
 * d. The drop down list is where all the profiles that we have saved are
 * listed. In this list on top we will have the Main Profile and all the other
 * below. As soon as we select one, the settings of that profile will populate
 * the settings window.
 * </p>
 * <p>
 * 2) Please be careful now because I am going to redefine the rule of setting a
 * profile as default one by pressing the Save as Default button.
 * </p>
 * Giulio<br>
 * Mail: Default settings for TL<br>
 * Date: 12/24/2013 9:50 AM<br>
 * <br>
 * 
 * @author Arian
 * 
 */
public class ProfileManager2 {
	private static final String PREF_KEY = "chart.profiles3";
	// private static final String MAIN_PROFILE_NAME = "Main Profile";

	Map<String, ProfileSet> _profileSetMap;
	Map<String, String> _defaultProfilesMap;

	private final IPreferenceStore _store;

	@XmlRootElement(name = "Data")
	public static class Data {
		public Map<String, ProfileSet> sets;
		public Map<String, String> defaults;
	}

	public ProfileManager2(IPreferenceStore store) {
		super();
		_store = store;
		Data data = null;
		if (_store != null && _store.contains(PREF_KEY)) {
			String xml = _store.getString(PREF_KEY);
			try {
				data = fromXML(xml);
				_profileSetMap = new LinkedHashMap<>(data.sets);
				_defaultProfilesMap = data.defaults;
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

		if (data == null) {
			_profileSetMap = new LinkedHashMap<>();
			_defaultProfilesMap = new HashMap<>();
		}
	}

	static Data fromXML(final String xml) throws JAXBException {
		final JAXBContext c = JAXBContext.newInstance(Data.class);
		final Unmarshaller um = c.createUnmarshaller();
		Data data = (Data) um.unmarshal(new StringReader(xml));
		return data;
	}

	public String toXML() throws JAXBException {
		Data data = new Data();
		data.defaults = _defaultProfilesMap;
		data.sets = _profileSetMap;

		final JAXBContext c = JAXBContext.newInstance(Data.class);
		final Marshaller ma = c.createMarshaller();
		ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter writer = new StringWriter();
		ma.marshal(data, writer);
		return writer.toString();
	}

	public void ensureSet(String keySet) {
		if (!_profileSetMap.containsKey(keySet)) {
			_profileSetMap.put(keySet, new ProfileSet());
		}
	}

	public Profile getDefault(String setKey) {
		ProfileSet set = _profileSetMap.get(setKey);
		String name = _defaultProfilesMap.get(setKey);
		Profile p = set.findProfile(name);
		return p;
	}

	public boolean isDefault(String setKey, Profile profile) {
		String name = _defaultProfilesMap.get(setKey);
		return profile.getName().equals(name);
	}

	public void saveAsDefault(String setKey, Profile profile) {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		List<InteractiveTool> toolsWithDefault = new ArrayList<>();

		for (ChartView view : views) {
			Chart chart = view.getChart();
			for (InteractiveTool tool : chart.getTools()) {
				String toolKey = tool.getProfileKeySet();
				if (toolKey.equals(setKey)) {
					boolean isDefault = isDefault(toolKey, tool.getProfile());
					if (isDefault) {
						toolsWithDefault.add(tool);
					}
				}
			}
		}

		String profileName = profile.getName();

		for (InteractiveTool tool : toolsWithDefault) {
			_defaultProfilesMap.put(tool.getProfileKeySet(), profileName);
			tool.setProfile(profile);
			tool.repaint();
		}

		_defaultProfilesMap.put(setKey, profileName);
		save();
	}

	public void setDefault(String key, Profile p) {
		_defaultProfilesMap.put(key, p.getName());
	}

	public Profile[] getProfiles(String setKey) {
		return _profileSetMap.get(setKey).toArray();
	}

	public ProfileSet getProfileSet(String setKey) {
		return _profileSetMap.get(setKey);
	}

	private static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public boolean saveAsDialog(String setKey, Profile newProfile) {
		final ProfileSet profileSet = _profileSetMap.get(setKey);
		String initName = profileSet.newProfileName();
		InputDialog dlg = new InputDialog(getShell(), "Save Profile",
				"Enter profile name", initName, new IInputValidator() {

					@Override
					public String isValid(String newText) {
						Profile p = profileSet.findProfile(newText);
						return p == null ? null : "The profile " + newText
								+ " already exists.";
					}
				});
		if (dlg.open() == Window.OK) {
			String name = dlg.getValue();
			Profile profile = newProfile;
			profile.setName(name);
			profileSet.addProfile(profile);
			save();
			return true;
		}
		return false;
	}

	public void save() {
		try {
			_store.putValue(PREF_KEY, toXML());
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void deleteProfileDialog(String setKey, Profile profile) {
		// look for the tools with the same profile
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		List<InteractiveTool> toolsWithSameProfile = new ArrayList<>();

		String profileName = profile.getName();

		for (ChartView view : views) {
			Chart chart = view.getChart();
			for (InteractiveTool tool : chart.getTools()) {
				String toolKey = tool.getProfileKeySet();
				if (toolKey.equals(setKey)) {
					if (tool.getProfile().getName().equals(profileName)) {
						toolsWithSameProfile.add(tool);
					}
				}
			}
		}

		// replace the to-delete profiles with the default profile
		ProfileSet profileSet = _profileSetMap.get(setKey);
		Profile defprofile = getDefault(setKey);

		for (InteractiveTool tool : toolsWithSameProfile) {
			_defaultProfilesMap.put(tool.getProfileKeySet(),
					defprofile.getName());
			tool.setProfile(defprofile);
			tool.repaint();
		}

		// finally, remove the profile from the set
		profileSet.removeProfile(profile);
	}

	public boolean containsSetKey(String setKey) {
		return _profileSetMap.containsKey(setKey);
	}
}
