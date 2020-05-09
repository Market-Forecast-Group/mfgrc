package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.interactive.TrendLinesTool;
import com.mfg.chart.ui.interactive.TrendLinesTool.Line;
import com.mfg.chart.ui.interactive.TrendLinesTool.Settings;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.chart.ui.settings.ShapeButton;
import com.mfg.chart.ui.settings.ShapeWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class TrendLinesEditor extends Composite implements IChartSettingsEditor {

	private Chart _chart;
	private Line _context;
	private ColorChooserButton _colorChooserButton;
	private LineTypeButton _lineTypeButton;
	private LineWidthButton _lineWidthButton;
	private ShapeButton _shapeButton;
	private ShapeWidthButton _shapeWidthButton;
	private ChartProfilesComposite _chartProfilesComposite;
	private GUIProfileAdapter _adapter;
	private TrendLinesTool _tool;
	Settings _settings;
	private Button _btnMirrorImage;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public TrendLinesEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Group composite = new Group(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		composite.setLayout(new GridLayout(2, false));

		Label lblLineColor = new Label(composite, SWT.NONE);
		lblLineColor.setText("Color");

		_colorChooserButton = new ColorChooserButton(composite, SWT.NONE);
		GridData gd__colorChooserButton = new GridData(SWT.LEFT, SWT.FILL,
				false, false, 1, 1);
		gd__colorChooserButton.heightHint = 30;
		gd__colorChooserButton.widthHint = 50;
		_colorChooserButton.setLayoutData(gd__colorChooserButton);

		Label lblType = new Label(composite, SWT.NONE);
		lblType.setText("Type");

		_lineTypeButton = new LineTypeButton(composite, SWT.NONE);
		GridData gd__lineTypeButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__lineTypeButton.heightHint = 30;
		gd__lineTypeButton.widthHint = 131;
		_lineTypeButton.setLayoutData(gd__lineTypeButton);

		Label lblWidth = new Label(composite, SWT.NONE);
		lblWidth.setText("Width");

		_lineWidthButton = new LineWidthButton(composite, SWT.NONE);
		GridData gd__lineWidthButton = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd__lineWidthButton.widthHint = 131;
		_lineWidthButton.setLayoutData(gd__lineWidthButton);

		Label lblShape = new Label(composite, SWT.NONE);
		lblShape.setText("Shape");

		_shapeButton = new ShapeButton(composite, SWT.NONE);
		GridData gd__shapeButton = new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1);
		gd__shapeButton.heightHint = 50;
		gd__shapeButton.widthHint = 50;
		_shapeButton.setLayoutData(gd__shapeButton);

		Label lblShapeSize = new Label(composite, SWT.NONE);
		lblShapeSize.setText("Shape Size");

		_shapeWidthButton = new ShapeWidthButton(composite, SWT.NONE);
		GridData gd__shapeWidthButton = new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1);
		gd__shapeWidthButton.heightHint = 50;
		gd__shapeWidthButton.widthHint = 50;
		_shapeWidthButton.setLayoutData(gd__shapeWidthButton);

		_btnMirrorImage = new Button(composite, SWT.CHECK);
		_btnMirrorImage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		_btnMirrorImage.setText("Mirror Image");
		new Label(this, SWT.NONE);

		Group grpProfiles = new Group(this, SWT.NONE);
		grpProfiles.setText("Profiles");
		grpProfiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		GridLayout gl_grpProfiles = new GridLayout(1, false);
		gl_grpProfiles.marginHeight = 0;
		gl_grpProfiles.marginWidth = 0;
		grpProfiles.setLayout(gl_grpProfiles);

		_chartProfilesComposite = new ChartProfilesComposite(grpProfiles,
				SWT.NONE);

	}

	public TrendLinesEditor(Chart chart, Composite parent, Object context) {
		this(parent, SWT.NONE);
		_chart = chart;
		_context = (Line) context;
		_tool = _chart.getTool(TrendLinesTool.class);
		_settings = _context == null ? _tool.getDefaultSettings()
				: _context.settings;

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _tool) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_settings);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				Settings s = new Settings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				Settings s = new Settings();
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

	protected void updateSettings_fromUI(Settings s) {
		s.color = _colorChooserButton.getGLColor();
		s.lineType = _lineTypeButton.getLineType();
		s.lineWidth = _lineWidthButton.getLineWidth();
		s.shapeType = _shapeButton.getShapeType();
		s.shapeWidth = _shapeWidthButton.getShapeWidth();
		s.mirror = _btnMirrorImage.getSelection();
	}

	protected void updateUI_fromSettings(Settings s) {
		_colorChooserButton.setColor(s.color);
		_lineTypeButton.setLineType(s.lineType);
		_lineWidthButton.setLineWidth(s.lineWidth);
		_shapeButton.setShapeType(s.shapeType);
		_shapeWidthButton.setShapeWidth(s.shapeWidth);
		_btnMirrorImage.setSelection(s.mirror);
	}

	@Override
	public void applyChanges() {
		updateSettings_fromUI(_settings);
		_tool.setProfile(_adapter.getSelectedProfile());
	}

	@Override
	public Composite getUI() {
		return this;
	}

}
