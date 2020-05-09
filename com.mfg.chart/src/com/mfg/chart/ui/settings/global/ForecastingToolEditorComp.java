package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.ForecastingTool;
import com.mfg.chart.ui.interactive.ForecastingTool.Settings;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.ShapeButton;
import com.mfg.chart.ui.settings.ShapeWidthButton;

public class ForecastingToolEditorComp extends Composite {

	private ForecastingTool _tool;
	Settings _settings;
	private ShapeButton _shapeButton;
	private ShapeWidthButton _shapeWidthButton;
	private Composite _composite;
	private ChartProfilesComposite _chartProfilesComposite;
	private GUIProfileAdapter _adapter;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ForecastingToolEditorComp(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	public ForecastingToolEditorComp(Composite parent, Chart chart) {
		super(parent, 0);
		_tool = chart.getTool(ForecastingTool.class);
		_settings = _tool.getSettings().clone();
		createContent();
	}

	private void createContent() {
		setLayout(new GridLayout(1, false));

		_composite = new Composite(this, SWT.NONE);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1));
		_composite.setLayout(new GridLayout(2, false));

		Label lblShapeType = new Label(_composite, SWT.NONE);
		lblShapeType.setText("Shape Type");

		_shapeButton = new ShapeButton(_composite, SWT.NONE);
		GridData gd_shapeButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_shapeButton.heightHint = 50;
		gd_shapeButton.widthHint = 50;
		_shapeButton.setLayoutData(gd_shapeButton);

		Label lblShapeSize = new Label(_composite, SWT.NONE);
		lblShapeSize.setText("Shape Size");

		_shapeWidthButton = new ShapeWidthButton(_composite, SWT.NONE);
		GridData gd_shapeWidthButton = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_shapeWidthButton.widthHint = 50;
		gd_shapeWidthButton.heightHint = 50;
		_shapeWidthButton.setLayoutData(gd_shapeWidthButton);

		_chartProfilesComposite = new ChartProfilesComposite(this, SWT.NONE);

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
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
		_adapter.updateUI_fromToolSettings();
	}

	protected void updateSettingsFromWidgets(Settings s) {
		s.shapeSize = _shapeWidthButton.getShapeWidth();
		s.shapeType = _shapeButton.getShapeType();
	}

	void updateWidgetsFromSettings(Settings s) {
		_shapeWidthButton.setShapeWidth(s.shapeSize);
		_shapeButton.setShapeType(s.shapeType);
	}

	public void applyChanges() {
		updateSettingsFromWidgets(_settings);
		_tool.setSettings(_settings);
		_tool.setProfile(_adapter.getSelectedProfile());
	}

}
