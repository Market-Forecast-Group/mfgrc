package com.mfg.chart.ui.interactive;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.HarmonicLinesTool.Settings;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class HarmonicLinesToolEditorComp extends Composite {
	private Text _multiplier;
	private ChartProfilesComposite _chartProfilesComposite;
	Settings _settings;
	private HarmonicLinesTool _tool;
	private ColorChooserButton _color0;
	private ColorChooserButton _color1;
	private ColorChooserButton _color2;
	private ColorChooserButton _color3;
	private ColorChooserButton _color4;
	private LineWidthButton _width0;
	private LineWidthButton _width1;
	private LineWidthButton _width2;
	private LineWidthButton _width3;
	private LineWidthButton _width4;
	private LineTypeButton _line0;
	private LineTypeButton _line1;
	private LineTypeButton _line2;
	private LineTypeButton _line3;
	private LineTypeButton _line4;
	private LineWidthButton[] _widths;
	private ColorChooserButton[] _colors;
	private LineTypeButton[] _types;
	private GUIProfileAdapter _adapter;
	private Button _btnSecondaryLine_1_3;
	private Button _btnSecondaryLine_1_2;

	public HarmonicLinesToolEditorComp(Composite parent, Chart chart) {
		super(parent, SWT.None);
		_tool = chart.getTool(HarmonicLinesTool.class);
		_settings = _tool.getSettings().clone();
		createContent();
	}

	public HarmonicLinesToolEditorComp(Composite parent, int style) {
		super(parent, style);

		createContent();

	}

	private void createContent() {
		this.setLayout(new GridLayout(1, false));

		Composite group = new Composite(this, SWT.BORDER);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblMultiplier = new Label(group, SWT.NONE);
		lblMultiplier.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMultiplier.setText("Multiplier");

		_multiplier = new Text(group, SWT.BORDER);
		GridData gd_multiplier = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_multiplier.widthHint = 148;
		_multiplier.setLayoutData(gd_multiplier);

		_btnSecondaryLine_1_2 = new Button(group, SWT.RADIO);
		_btnSecondaryLine_1_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		_btnSecondaryLine_1_2.setText("Secondary Line 1/2 Ratio");

		_btnSecondaryLine_1_3 = new Button(group, SWT.RADIO);
		_btnSecondaryLine_1_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		_btnSecondaryLine_1_3.setText("Secondary Line 1/3 Ratio");

		Composite composite = new Composite(this, SWT.BORDER);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite = new GridLayout(4, true);
		gl_composite.marginLeft = 5;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		Label lblMasterPrimary = new Label(composite, SWT.NONE);
		lblMasterPrimary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		lblMasterPrimary.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblMasterPrimary.setText("Master Primary");

		_color0 = new ColorChooserButton(composite, SWT.NONE);
		_color0.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_color0.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_width0 = new LineWidthButton(composite, SWT.NONE);
		_width0.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_width0.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_line0 = new LineTypeButton(composite, SWT.NONE);
		_line0.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label lblPrimary = new Label(composite, SWT.NONE);
		lblPrimary.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPrimary.setText("Primary");

		_color1 = new ColorChooserButton(composite, SWT.NONE);
		_color1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_color1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_width1 = new LineWidthButton(composite, SWT.NONE);
		_width1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_width1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_line1 = new LineTypeButton(composite, SWT.NONE);
		_line1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		Label lblSecondary = new Label(composite, SWT.NONE);
		lblSecondary
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSecondary.setText("Secondary");

		_color2 = new ColorChooserButton(composite, SWT.NONE);
		_color2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_color2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_width2 = new LineWidthButton(composite, SWT.NONE);
		_width2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_width2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_line2 = new LineTypeButton(composite, SWT.NONE);
		_line2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		Label lblTertiary = new Label(composite, SWT.NONE);
		lblTertiary.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTertiary.setText("Tertiary");

		_color3 = new ColorChooserButton(composite, SWT.NONE);
		_color3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_color3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_width3 = new LineWidthButton(composite, SWT.NONE);
		_width3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_width3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_line3 = new LineTypeButton(composite, SWT.NONE);
		_line3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		Label lblQuaternary = new Label(composite, SWT.NONE);
		lblQuaternary.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblQuaternary.setText("Quaternary");

		_color4 = new ColorChooserButton(composite, SWT.NONE);
		_color4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_color4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_width4 = new LineWidthButton(composite, SWT.NONE);
		_width4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_width4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		_line4 = new LineTypeButton(composite, SWT.NONE);
		_line4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));

		_chartProfilesComposite = new ChartProfilesComposite(this, SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));

		afterCreateWidgets();
	}

	public Settings getSettings() {
		return _settings;
	}

	public HarmonicLinesTool getTool() {
		return _tool;
	}

	public GUIProfileAdapter getAdapter() {
		return _adapter;
	}

	private void afterCreateWidgets() {
		_colors = new ColorChooserButton[] { _color0, _color1, _color2,
				_color3, _color4 };
		_widths = new LineWidthButton[] { _width0, _width1, _width2, _width3,
				_width4 };
		_types = new LineTypeButton[] { _line0, _line1, _line2, _line3, _line4 };

		updateWidgetsFromSettings(_settings);

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _tool) {

			@Override
			public void updateUI_fromToolSettings() {
				updateWidgetsFromSettings(_settings);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				Settings s = new Settings();
				s.updateFromProfile(profile);
				updateWidgetsFromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				Settings s = new Settings();
				updateSettingsFromWidgets(s);
				s.fillProfile(profile);
			}

			@Override
			protected void updateModel_fromUI() {
				updateSettingsFromWidgets(_settings);
			}
		};
	}

	public void applyChanges() {
		updateSettingsFromWidgets(_settings);
		_tool.getSettings().updateFromSettings(_settings);
		_tool.setProfile(_adapter.getSelectedProfile());
	}

	protected void updateSettingsFromWidgets(Settings s) {
		s.setMultiplier(Integer.parseInt(_multiplier.getText()));
		s.setPartition(_btnSecondaryLine_1_2.getSelection() ? 2 : 3);
		for (int i = 0; i <= 4; i++) {
			s.getColors()[i] = ColorChooserButton.rgbToColor(_colors[i]
					.getColor());
			s.getLineTypes()[i] = _types[i].getLineType();
			s.getLineWidths()[i] = _widths[i].getLineWidth();
		}
	}

	protected void updateWidgetsFromSettings(Settings s) {
		_multiplier.setText(Integer.toString(s.getMultiplier()));
		_btnSecondaryLine_1_2.setSelection(s.getPartition() == 2);
		_btnSecondaryLine_1_3.setSelection(s.getPartition() == 3);
		for (int i = 0; i <= 4; i++) {
			_colors[i].setColor(s.getColors()[i]);
			_widths[i].setLineWidth(s.getLineWidths()[i]);
			_types[i].setLineType(s.getLineTypes()[i]);
		}
	}

}
