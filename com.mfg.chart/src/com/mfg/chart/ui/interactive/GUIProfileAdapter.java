package com.mfg.chart.ui.interactive;

import static java.lang.System.out;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import com.mfg.chart.profiles.Profile;
import com.mfg.chart.profiles.Profile.IntValue;
import com.mfg.chart.profiles.ProfileManager2;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.ChartProfilesComposite.IContainer;
import com.mfg.chart.ui.settings.ProfiledObject;

public abstract class GUIProfileAdapter {
	private final ProfiledObject _profiledObj;
	final ChartProfilesComposite _profileComp;
	protected Profile _selectedProfile;

	public GUIProfileAdapter(ChartProfilesComposite profilesComp,
			ProfiledObject profiledObj) {
		super();
		_profiledObj = profiledObj;
		_profileComp = profilesComp;

		_profileComp.init(_profiledObj.getProfileKeySet());
		_profileComp.setContainer(new IContainer() {

			@Override
			public Profile createProfileWithCurrentSettings() {
				Profile p = new Profile();
				updateProfile_fromUI(p);
				return p;
			}

			@Override
			public void updateProfileFromDialogUI(Profile profile) {
				updateProfile_fromUI(profile);
			}

			@Override
			public void updateSettingsModel_fromUI() {
				updateModel_fromUI();
			}
		});
		_profileComp.getProfileSelectionProvier().addSelectionChangedListener(
				new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						_selectedProfile = _profileComp.getSelectedProfile();
						if (_selectedProfile == null) {
							_profileComp.setCustomProfile();
						} else {
							updateUI_fromProfile(_profileComp
									.getSelectedProfile());
						}
					}
				});
		updateUI_fromToolSettings();

		_selectedProfile = profiledObj.getProfile();

		checkForCustomProfile();
	}

	private void checkForCustomProfile() {
		Profile tempProfile = new Profile();
		updateProfile_fromUI(tempProfile);
		boolean same = _selectedProfile != null
				&& tempProfile.sameSettings(_selectedProfile);

		if (_profiledObj instanceof PolylineTool) {

			Set<Object> set = new HashSet<>();

			for (IntValue v : tempProfile.getIntValues()) {
				set.add(v.key);
			}

			for (IntValue v : _selectedProfile.getIntValues()) {
				if (!set.contains(v.key)) {
					out.println("[456356] " + _profiledObj.getClass() + ": Missing profile key " + v.key);
				}
			}
		}

		if (same) {
			selectProfile(_selectedProfile);
		} else {
			// is custom
			// look for a suitable profile
			String key = _profiledObj.getProfileKeySet();
			ProfileManager2 manager = _profiledObj.getProfilesManager();
			Profile[] profiles = manager.getProfiles(key);
			boolean iscustom = true;
			for (Profile p : profiles) {
				if (p.sameSettings(tempProfile)) {
					// profile p matches with the current profile
					// select it.
					selectProfile(p);
					iscustom = false;
					break;
				}
			}

			if (iscustom) {
				// is a custom profile
				selectProfile(null);
			}
		}
	}

	void selectProfile(Profile p) {
		_selectedProfile = p;
		StructuredSelection sel = p == null ? StructuredSelection.EMPTY
				: new StructuredSelection(p);
		_profileComp.getProfileSelectionProvier().setSelection(sel);
	}

	@SuppressWarnings("static-method")
	public void dialogSettingsModified() {
		out.println("Modified");
	}

	protected abstract void updateModel_fromUI();

	protected abstract void updateProfile_fromUI(Profile profile);

	protected abstract void updateUI_fromProfile(Profile profile);

	public abstract void updateUI_fromToolSettings();

	public Profile getSelectedProfile() {
		return _selectedProfile;
	}

	public void save() {
		Profile p = _profileComp.getSelectedProfile();
		if (p != null) {
			updateProfile_fromUI(p);
		}
		_profileComp.save();
	}

}
