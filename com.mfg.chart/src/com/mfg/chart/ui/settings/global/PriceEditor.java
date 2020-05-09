package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.PriceLayer.PriceSettings;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class PriceEditor extends Composite implements IChartSettingsEditor {

	Chart _chart;
	private ChartProfilesComposite _chartProfilesComposite;
	private GUIProfileAdapter _adapter;
	private ColorChooserButton _colorChooserButton;
	private LineWidthButton _lineWidthButton;
	private LineTypeButton _lineTypeButton;
	private Button _btnShowAllPrices;
	private Button _btnShowLowerZz;
	private Button _btnEnabled;
	private Group _group;
	private Label _label;
	private Label _label_1;
	private Label _label_2;
	private ColorChooserButton _volColor;
	private LineWidthButton _volLineWidth;
	private LineTypeButton _volLineType;
	private Button _volEnabled;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PriceEditor(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	public PriceEditor(Chart chart, Composite parent) {
		super(parent, SWT.NONE);
		_chart = chart;
		createContent();

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getPriceLayer().getProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_chart.getPriceLayer().getSettings());
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				PriceSettings s = new PriceSettings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				PriceSettings s = new PriceSettings();
				updateSettings_fromUI(s);
				s.fillProfile(profile);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};
		_adapter.updateUI_fromToolSettings();
	}

	protected void updateSettings_fromUI(PriceSettings s) {
		s.color = _colorChooserButton.getGLColor();
		s.lineType = _lineTypeButton.getLineType();
		s.lineWidth = _lineWidthButton.getLineWidth();
		s.enabled = _btnEnabled.getSelection();
		s.zzCompression = _btnShowLowerZz.getSelection();

		s.showVolume = _volEnabled.getSelection();
		s.volumeColor = _volColor.getGLColor();
		s.volumeType = _volLineType.getLineType();
		s.volumeWidth = _volLineWidth.getLineWidth();
	}

	protected void updateUI_fromSettings(PriceSettings s) {
		_colorChooserButton.setColor(s.color);
		_lineTypeButton.setLineType(s.lineType);
		_lineWidthButton.setLineWidth(s.lineWidth);
		_btnEnabled.setSelection(s.enabled);
		_btnShowAllPrices.setSelection(!s.zzCompression);
		_btnShowLowerZz.setSelection(s.zzCompression);

		_volColor.setColor(s.volumeColor);
		_volLineWidth.setLineWidth(s.volumeWidth);
		_volLineType.setLineType(s.volumeType);
		_volEnabled.setSelection(s.showVolume);
	}

	@Override
	public void applyChanges() {
		PriceSettings s = new PriceSettings();
		updateSettings_fromUI(s);
		_chart.getPriceLayer().setSettings(s);

		_chart.getPriceLayer().getProfiledObject()
				.setProfile(_chartProfilesComposite.getSelectedProfile());
		// to clean or restore prices in case the enabled changed.
		_chart.getPriceLayer().updateDataset();
	}

	@Override
	public Composite getUI() {
		return this;
	}

	void createContent() {
		setLayout(new GridLayout(1, false));

		Group grpAppareance = new Group(this, SWT.NONE);
		grpAppareance.setText("Appareance");
		grpAppareance.setLayout(new GridLayout(3, false));
		grpAppareance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblColor = new Label(grpAppareance, SWT.NONE);
		lblColor.setText("Color");

		Label lblLineWidth = new Label(grpAppareance, SWT.NONE);
		lblLineWidth.setText("Line Width");

		Label lblLineType = new Label(grpAppareance, SWT.NONE);
		lblLineType.setText("Line Type");

		_colorChooserButton = new ColorChooserButton(grpAppareance, SWT.NONE);
		GridData gd__colorChooserButton = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd__colorChooserButton.widthHint = 50;
		_colorChooserButton.setLayoutData(gd__colorChooserButton);

		_lineWidthButton = new LineWidthButton(grpAppareance, SWT.NONE);
		GridData gd__lineWidthButton = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd__lineWidthButton.widthHint = 130;
		_lineWidthButton.setLayoutData(gd__lineWidthButton);

		_lineTypeButton = new LineTypeButton(grpAppareance, SWT.NONE);
		GridData gd__lineTypeButton = new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1);
		gd__lineTypeButton.widthHint = 130;
		_lineTypeButton.setLayoutData(gd__lineTypeButton);

		Group grpCompression = new Group(this, SWT.NONE);
		grpCompression.setText("Compression");
		grpCompression.setLayout(new GridLayout(1, false));
		grpCompression.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));

		_btnShowAllPrices = new Button(grpCompression, SWT.RADIO);
		_btnShowAllPrices.setText("Show All Prices");

		_btnShowLowerZz = new Button(grpCompression, SWT.RADIO);
		_btnShowLowerZz.setText("Compress Prices with ZZ");

		_btnEnabled = new Button(grpCompression, SWT.CHECK);
		_btnEnabled.setText("Enabled");

		_group = new Group(this, SWT.NONE);
		_group.setLayout(new GridLayout(3, false));
		_group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_group.setText("Volume");

		_label = new Label(_group, SWT.NONE);
		_label.setText("Color");

		_label_1 = new Label(_group, SWT.NONE);
		_label_1.setText("Line Width");

		_label_2 = new Label(_group, SWT.NONE);
		_label_2.setText("Line Type");

		_volColor = new ColorChooserButton(_group, SWT.NONE);
		GridData gd_volColor = new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1);
		gd_volColor.widthHint = 50;
		_volColor.setLayoutData(gd_volColor);

		_volLineWidth = new LineWidthButton(_group, SWT.NONE);
		GridData gd_volLineWidth = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_volLineWidth.widthHint = 130;
		_volLineWidth.setLayoutData(gd_volLineWidth);

		_volLineType = new LineTypeButton(_group, SWT.NONE);
		GridData gd_volLineType = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_volLineType.widthHint = 130;
		_volLineType.setLayoutData(gd_volLineType);

		_volEnabled = new Button(_group, SWT.CHECK);
		_volEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		_volEnabled.setText("Enabled");

		Group grpProfiles = new Group(this, SWT.NONE);
		grpProfiles.setText("Profiles");
		grpProfiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpProfiles.setLayout(new GridLayout(1, false));

		_chartProfilesComposite = new ChartProfilesComposite(grpProfiles,
				SWT.NONE);
	}
}
