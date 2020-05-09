package com.mfg.chart.ui.settings;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.profiles.ProfileManager2;

public class ChartProfilesComposite extends Composite {
	final class ProfileLabelProvider extends LabelProvider {
		private final String _profileKeySet;

		ProfileLabelProvider(String profileKeySet) {
			_profileKeySet = profileKeySet;
		}

		@Override
		public String getText(Object element) {
			Profile p = (Profile) element;
			boolean isDefault = _manager.isDefault(_profileKeySet, p);
			return p.getName() + (isDefault ? " (Default)" : "");
		}
	}

	public static interface IContainer {
		public Profile createProfileWithCurrentSettings();

		public void updateProfileFromDialogUI(Profile profile);

		public void updateSettingsModel_fromUI();
	}

	private final ComboViewer _viewer;
	ProfileManager2 _manager;
	private String _key;
	private final Button _btnDeleteProfile;
	private Runnable _saveRunnable;
	private IContainer _container;
	private final Button _btnSaveProfileAs;
	private final Button _btnSaveProfile;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ChartProfilesComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_1.setLayout(new GridLayout(4, true));

		_viewer = new ComboViewer(composite_1, SWT.READ_ONLY);
		_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				profileChanged();
			}
		});
		Combo combo = _viewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2,
				1));
		combo.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				paintCombo(e);
			}
		});
		_viewer.setContentProvider(new ArrayContentProvider());
		@SuppressWarnings("unused")
		Label l1 = new Label(composite_1, SWT.NONE);
		l1 = new Label(composite_1, SWT.NONE);

		_btnSaveProfile = new Button(composite_1, SWT.NONE);
		_btnSaveProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		_btnSaveProfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePressed();
			}
		});
		_btnSaveProfile.setText("Save");

		Button btnSaveAsDefault = new Button(composite_1, SWT.NONE);
		btnSaveAsDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		btnSaveAsDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveAsDefaultPressed();
			}
		});
		btnSaveAsDefault.setText("Save As Default");

		_btnSaveProfileAs = new Button(composite_1, SWT.NONE);
		_btnSaveProfileAs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		_btnSaveProfileAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveAsPressed();
			}
		});
		_btnSaveProfileAs.setText("Save As...");

		_btnDeleteProfile = new Button(composite_1, SWT.NONE);
		_btnDeleteProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		_btnDeleteProfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deletePressed();
			}
		});
		_btnDeleteProfile.setText("Delete");

	}

	protected void paintCombo(PaintEvent e) {
		if (getSelectedProfile() == null) {
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));
			int y = (e.height - e.gc.getFontMetrics().getHeight()) / 2;
			e.gc.drawText("(Custom Profile)", 5, y, true);
		}
	}

	final protected void profileChanged() {
		Profile profile = getSelectedProfile();
		if (profile == null) {
			_btnDeleteProfile.setEnabled(false);
			_btnSaveProfileAs.setEnabled(false);
			setCustomProfile();
		} else {
			boolean isdefault = _manager.isDefault(_key, profile);
			_btnDeleteProfile.setEnabled(!isdefault);
			_btnSaveProfileAs.setEnabled(true);
		}
	}

	public void setCustomProfile() {
		_viewer.getCombo().setText("(Custom Profile)");
	}

	protected void deletePressed() {
		Profile profile = getSelectedProfile();
		_manager.deleteProfileDialog(_key, profile);
		updateViewer(_manager.getDefault(_key));
		Profile main = _manager.getDefault(_key);
		_viewer.setSelection(new StructuredSelection(main));
	}

	public void init(final String profileKeySet) {
		_key = profileKeySet;
		_manager = ChartPlugin.getDefault().getProfileManager2();
		_viewer.setLabelProvider(new ProfileLabelProvider(profileKeySet));
		updateViewer(_manager.getDefault(profileKeySet));
	}

	boolean saveAsPressed() {
		Profile profile = createProfileWithCurrentSettings();
		if (_manager.saveAsDialog(_key, profile)) {
			updateViewer(profile);

			if (_saveRunnable != null) {
				_saveRunnable.run();
			}

			if (_container != null) {
				_container.updateSettingsModel_fromUI();
			}
			return true;
		}
		return false;
	}

	// Used on inner classes
	protected Profile createProfileWithCurrentSettings() {
		if (_container != null) {
			return _container.createProfileWithCurrentSettings();
		}
		throw new UnsupportedOperationException(
				"This method should be overriden by subclasses");
	}

	public Profile getSelectedProfile() {
		return (Profile) ((StructuredSelection) _viewer.getSelection())
				.getFirstElement();
	}

	private void updateViewer(Profile sel) {
		_viewer.setInput(_manager.getProfiles(_key));
		_viewer.setSelection(new StructuredSelection(sel));
	}

	void savePressed() {
		if (getSelectedProfile() == null) {
			// in case we have a custom profile
			saveAsPressed();
		} else {

			if (_container != null) {
				_container.updateProfileFromDialogUI(getSelectedProfile());
			}

			_manager.save();

			if (_saveRunnable != null) {
				_saveRunnable.run();
			}

			if (_container != null) {
				_container.updateSettingsModel_fromUI();
			}
		}
	}

	protected void saveAsDefaultPressed() {
		if (getSelectedProfile() == null) {
			if (!saveAsPressed()) {
				return;
			}
		}

		Profile p = createProfileWithCurrentSettings();
		Profile selProfile = getSelectedProfile();
		selProfile.updateFrom(p);
		_manager.saveAsDefault(_key, selProfile);
		_viewer.refresh();

		if (_saveRunnable != null) {
			_saveRunnable.run();
		}

		if (_container != null) {
			_container.updateSettingsModel_fromUI();
		}
	}

	public Runnable getSaveRunnable() {
		return _saveRunnable;
	}

	public void setSaveRunnable(Runnable saveRunnable) {
		_saveRunnable = saveRunnable;
	}

	public ISelectionProvider getProfileSelectionProvier() {
		return _viewer;
	}

	public IContainer getContainer() {
		return _container;
	}

	public void setContainer(IContainer container) {
		_container = container;
	}

	public void save() {
		savePressed();
	}

	public void hideSaveButton() {
		_btnSaveProfile.dispose();
	}
}
