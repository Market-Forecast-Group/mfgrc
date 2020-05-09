package com.mfg.chart.ui.settings;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.profiles.ProfileManager2;
import com.mfg.chart.profiles.ProfileSet;

public abstract class ProfiledObject {

	private final ProfileManager2 _profilesManager;
	private Profile _profile;

	public ProfiledObject() {
		_profilesManager = ChartPlugin.getDefault().getProfileManager2();
		String key = getProfileKeySet();
		_profilesManager.ensureSet(key);

		List<Profile> profiles = createProfilePresets();
		Assert.isTrue(!profiles.isEmpty());
		ProfileSet profileSet = _profilesManager.getProfileSet(key);
		for (Profile p : profiles) {
			String pname = p.getName();
			if (profileSet.existsProfile(pname)) {
				migrateProfile(profileSet.findProfile(pname));
			} else {
				profileSet.addProfile(p);
			}
		}

		Profile defprofile = _profilesManager.getDefault(key);
		if (defprofile == null || defprofile.isEmpty()) {
			defprofile = profiles.get(0);
			_profilesManager.setDefault(key, defprofile);
		}
		_profile = defprofile;
	}

	public abstract String getProfileKeySet();

	/**
	 * 
	 * @param profile
	 */
	protected void migrateProfile(Profile profile) {
		//
	}

	@SuppressWarnings("static-method")
	protected List<Profile> createProfilePresets() {
		return Arrays.asList(new Profile("Profile 1"));
	}

	public Profile getProfile() {
		return _profile;
	}

	public Profile getDefault() {
		return _profilesManager.getDefault(getProfileKeySet());
	}

	public void setProfile(Profile profile) {
		_profile = profile;
	}

	public ProfileManager2 getProfilesManager() {
		return _profilesManager;
	}
}
