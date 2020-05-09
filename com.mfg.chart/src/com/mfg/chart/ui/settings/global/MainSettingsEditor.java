package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.mfg.opengl.chart.Settings;
import org.mfg.opengl.chart.SnappingMode;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.Chart.MainSettings;
import com.mfg.chart.backend.opengl.MFGChartCustomization;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.AutoRangeType;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class MainSettingsEditor extends Composite implements
		IChartSettingsEditor {

	Chart _chart;
	private final ColorChooserButton _background;
	private final ColorChooserButton _textColor;
	private final ColorChooserButton _gridColor;
	private final ColorChooserButton _crosshairColor;
	private final LineWidthButton _gridLineWidth;
	private final LineTypeButton _gridLineType;
	private final LineWidthButton _crosshairLineWidth;
	private final LineTypeButton _crosshairLineType;
	private final Group _composite;
	private final ChartProfilesComposite _chartProfilesComposite;
	private final Group _group;
	private GUIProfileAdapter _adapter;
	Settings _settings;
	private final Group _group_1;
	private final Label _label;
	private final Label _label_1;
	private final ColorChooserButton _lastPriceLabelColor;
	private final ColorChooserButton _lastPriceGridColor;
	private final Button _btnDoesNotSnap;
	private final Button _btnSnapsOverPrices;
	private final Button _btnSnapsToTheClosestTick;
	private final Composite _composite_1;
	private final Composite _composite_2;
	private final Group _group_2;
	private final Button _btnAutorangePrices;
	private final Button _btnAutorangeProbs;
	private final Button _btnAutorangeEnabled;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MainSettingsEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		_composite_1 = new Composite(this, SWT.NONE);
		_composite_1.setLayout(new GridLayout(2, false));
		_composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		_composite = new Group(_composite_1, SWT.NONE);
		_composite.setText("Appareance");
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_composite.setLayout(new GridLayout(4, false));

		Label lblBackground = new Label(_composite, SWT.NONE);
		lblBackground.setText("Background");

		_background = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_background = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_background.heightHint = 30;
		gd_background.widthHint = 50;
		_background.setLayoutData(gd_background);
		@SuppressWarnings("unused")
		Label ll = new Label(_composite, SWT.NONE);
		ll = new Label(_composite, SWT.NONE);

		Label lblText = new Label(_composite, SWT.NONE);
		lblText.setText("Text");

		_textColor = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_textColor = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_textColor.heightHint = 30;
		_textColor.setLayoutData(gd_textColor);
		ll = new Label(_composite, SWT.NONE);
		ll = new Label(_composite, SWT.NONE);

		Label lblGridColor = new Label(_composite, SWT.NONE);
		lblGridColor.setText("Grid");

		_gridColor = new ColorChooserButton(_composite, SWT.NONE);
		_gridColor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));

		_gridLineWidth = new LineWidthButton(_composite, SWT.NONE);

		_gridLineType = new LineTypeButton(_composite, SWT.NONE);
		GridData gd_gridLineType = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_gridLineType.widthHint = 100;
		_gridLineType.setLayoutData(gd_gridLineType);

		Label lblCrosshair = new Label(_composite, SWT.NONE);
		GridData gd_lblCrosshair = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblCrosshair.widthHint = 80;
		lblCrosshair.setLayoutData(gd_lblCrosshair);
		lblCrosshair.setText("Crosshair");

		_crosshairColor = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_crosshairColor = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_crosshairColor.widthHint = 50;
		_crosshairColor.setLayoutData(gd_crosshairColor);

		_crosshairLineWidth = new LineWidthButton(_composite, SWT.NONE);

		_crosshairLineType = new LineTypeButton(_composite, SWT.NONE);
		_crosshairLineType.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_group_1 = new Group(_composite_1, SWT.NONE);
		_group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		_group_1.setLayout(new GridLayout(2, false));
		_group_1.setText("Last Price");

		_label = new Label(_group_1, SWT.NONE);
		_label.setText("Label");

		_lastPriceLabelColor = new ColorChooserButton(_group_1, SWT.NONE);
		GridData gd_lastPriceLabelColor = new GridData(SWT.LEFT, SWT.FILL,
				false, false, 1, 1);
		gd_lastPriceLabelColor.heightHint = 30;
		gd_lastPriceLabelColor.widthHint = 50;
		_lastPriceLabelColor.setLayoutData(gd_lastPriceLabelColor);

		_label_1 = new Label(_group_1, SWT.NONE);
		_label_1.setText("Grid");

		_lastPriceGridColor = new ColorChooserButton(_group_1, SWT.NONE);
		GridData gd_lastPriceGridColor = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_lastPriceGridColor.heightHint = 30;
		_lastPriceGridColor.setLayoutData(gd_lastPriceGridColor);

		_composite_2 = new Composite(this, SWT.NONE);
		_composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		_composite_2.setLayout(new GridLayout(2, false));

		Group grpSnapping = new Group(_composite_2, SWT.NONE);
		grpSnapping.setText("Snapping");
		grpSnapping.setLayout(new GridLayout(1, false));

		_btnDoesNotSnap = new Button(grpSnapping, SWT.RADIO);
		_btnDoesNotSnap.setText("Does Not Snap");

		_btnSnapsOverPrices = new Button(grpSnapping, SWT.RADIO);
		_btnSnapsOverPrices.setText("Snaps Over Prices");

		_btnSnapsToTheClosestTick = new Button(grpSnapping, SWT.RADIO);
		_btnSnapsToTheClosestTick.setText("Snaps To The Closest Tick");

		_group_2 = new Group(_composite_2, SWT.NONE);
		_group_2.setLayout(new GridLayout(1, false));
		_group_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		_group_2.setText("Autorange");

		_btnAutorangePrices = new Button(_group_2, SWT.RADIO);
		_btnAutorangePrices.setText("Autorange by Prices");

		_btnAutorangeProbs = new Button(_group_2, SWT.RADIO);
		_btnAutorangeProbs.setText("Autorange by Probabilities");

		_btnAutorangeEnabled = new Button(_group_2, SWT.CHECK);
		_btnAutorangeEnabled.setText("Enabled");

		_group = new Group(this, SWT.NONE);
		_group.setText("Profiles");
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		_group.setLayout(new GridLayout(1, false));

		_chartProfilesComposite = new ChartProfilesComposite(_group, SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, false, false, 1, 1));
	}

	public MainSettingsEditor(Composite parent, Chart chart) {
		this(parent, SWT.NONE);
		_chart = chart;
		_settings = chart.getGLChart().getSettings().clone();

		_adapter = new GUIProfileAdapter(_chartProfilesComposite,
				chart.getMainSettingsProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_chart.getMainSettings(),
						_chart.getCustom());
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				MainSettings s = new MainSettings();
				s.updateFromProfile(profile);
				MFGChartCustomization c = new MFGChartCustomization(_chart);
				c.updateFromProfile(profile);
				updateUI_fromSettings(s, c);
			}

			@Override
			protected void updateProfile_fromUI(Profile p) {
				MainSettings s = new MainSettings();
				MFGChartCustomization c = new MFGChartCustomization(_chart);
				updateSettings_fromUI(s, c);
				s.fillProfile(p);
				c.fillProfile(p);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};
		_adapter.updateUI_fromToolSettings();
		_btnAutorangeProbs.setEnabled(_chart.getType().hasProbs());
	}

	void updateUI_fromSettings(MainSettings s, MFGChartCustomization custom) {
		_background.setColor(s.getBgColor());
		_textColor.setColor(s.getTextColor());
		_gridColor.setColor(s.getGridColor());
		_gridLineWidth.setLineWidth((int) s.getGridWidth());
		_gridLineType.setLineType(s.getGridStippleFactor());
		_crosshairColor.setColor(s.getCrosshairColor());
		_crosshairLineWidth.setLineWidth((int) s.getCrosshairWidth());
		_crosshairLineType.setLineType(s.getCrosshairStippleFactor());

		_lastPriceLabelColor.setColor(custom.getLastPriceLabelColor());
		_lastPriceGridColor.setColor(custom.getLastPriceGridLineColor());

		_btnDoesNotSnap
				.setSelection(s.getSnappingMode() == SnappingMode.DO_NOT_SNAP);
		_btnSnapsOverPrices
				.setSelection(s.getSnappingMode() == SnappingMode.SNAP_XY);
		_btnSnapsToTheClosestTick
				.setSelection(s.getSnappingMode() == SnappingMode.SNAP_Y);

		_btnAutorangeEnabled.setSelection(s.autoRangeEnabled);
		_btnAutorangePrices
				.setSelection(s.autoRangeType == AutoRangeType.AUTORANGE_PRICES);
		_btnAutorangeProbs
				.setSelection(s.autoRangeType == AutoRangeType.AUTORANGE_PROBS);
	}

	void updateSettings_fromUI(MainSettings s, MFGChartCustomization custom) {
		s.setBgColor(_background.getGLColor());
		s.setTextColor(_textColor.getGLColor());
		s.setGridColor(_gridColor.getGLColor());
		s.setGridWidth(_gridLineWidth.getLineWidth());
		s.setGridStippleFactor(_gridLineType.getLineType());
		s.setCrosshairColor(_crosshairColor.getGLColor());
		s.setCrosshairWidth(_crosshairLineWidth.getLineWidth());
		s.setCrosshairStippleFactor(_crosshairLineType.getLineType());
		if (_btnDoesNotSnap.getSelection()) {
			s.setSnappingMode(SnappingMode.DO_NOT_SNAP);
		} else if (_btnSnapsOverPrices.getSelection()) {
			s.setSnappingMode(SnappingMode.SNAP_XY);
		} else if (_btnSnapsToTheClosestTick.getSelection()) {
			s.setSnappingMode(SnappingMode.SNAP_Y);
		}

		s.autoRangeEnabled = _btnAutorangeEnabled.getSelection();
		if (_btnAutorangePrices.getSelection()) {
			s.autoRangeType = AutoRangeType.AUTORANGE_PRICES;
		} else {
			s.autoRangeType = AutoRangeType.AUTORANGE_PROBS;
		}

		custom.setLastPriceGridLineColor(_lastPriceGridColor.getGLColor());
		custom.setLastPriceLabelColor(_lastPriceLabelColor.getGLColor());
	}

	@Override
	public void applyChanges() {
		MainSettings s = new MainSettings();
		updateSettings_fromUI(s, _chart.getCustom());
		_chart.setMainSettings(s);
		_chart.getMainSettingsProfiledObject().setProfile(
				_adapter.getSelectedProfile());
	}

	@Override
	public Composite getUI() {
		return this;
	}

}
