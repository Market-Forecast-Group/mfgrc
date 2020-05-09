package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.chart.ui.settings.ShapeButton;
import com.mfg.chart.ui.settings.ShapeWidthButton;

public class ARCIndicatorEditor extends Composite implements
		IChartSettingsEditor {
	Chart _chart;
	private final LineWidthButton _zzWidth;
	private final LineTypeButton _zzType;
	private final LineWidthButton _bandsTopBottomWidth;
	private final LineTypeButton _bandsTopBottomType;
	private final LineWidthButton _channelsWidth;
	private final Button _zzEnable;
	private final LineTypeButton _channelsType;
	private final Button _channelsEnabled;
	private final LineWidthButton _bandsCenterWidth;
	private final LineTypeButton _bandsCenterType;
	private final Button _bandsEnabled;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public ARCIndicatorEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Group grpZigZag = new Group(this, SWT.NONE);
		grpZigZag.setLayout(new GridLayout(3, false));
		grpZigZag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		grpZigZag.setText("Zig Zag");

		Label lblLineWidth = new Label(grpZigZag, SWT.NONE);
		lblLineWidth.setText("Line Width");

		Label lblLineType = new Label(grpZigZag, SWT.NONE);
		lblLineType.setText("Line Type");

		Label lblEnabled = new Label(grpZigZag, SWT.NONE);
		lblEnabled.setText("Enabled");

		_zzWidth = new LineWidthButton(grpZigZag, SWT.NONE);
		GridData gd__zzWidth = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gd__zzWidth.widthHint = 130;
		_zzWidth.setLayoutData(gd__zzWidth);

		_zzType = new LineTypeButton(grpZigZag, SWT.NONE);
		GridData gd__zzType = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gd__zzType.widthHint = 130;
		_zzType.setLayoutData(gd__zzType);

		_zzEnable = new Button(grpZigZag, SWT.CHECK);

		_group_1 = new Group(grpZigZag, SWT.NONE);
		_group_1.setText("TH");
		_group_1.setLayout(new GridLayout(2, false));
		_group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				3, 1));

		_label = new Label(_group_1, SWT.NONE);
		_label.setText("Size");

		_label_1 = new Label(_group_1, SWT.NONE);
		_label_1.setText("Shape");

		_shapeWidthButton = new ShapeWidthButton(_group_1, SWT.NONE);
		GridData gd_shapeWidthButton = new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1);
		gd_shapeWidthButton.heightHint = 50;
		gd_shapeWidthButton.widthHint = 50;
		_shapeWidthButton.setLayoutData(gd_shapeWidthButton);

		_shapeTypeButton = new ShapeButton(_group_1, SWT.NONE);
		GridData gd_shapeTypeButton = new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1);
		gd_shapeTypeButton.widthHint = 50;
		_shapeTypeButton.setLayoutData(gd_shapeTypeButton);

		_zzMarkers = new Button(grpZigZag, SWT.CHECK);
		_zzMarkers.setText("Markers");
		new Label(grpZigZag, SWT.NONE);
		new Label(grpZigZag, SWT.NONE);

		_btnParallelRealTimeZZ = new Button(grpZigZag, SWT.CHECK);
		_btnParallelRealTimeZZ.setText("Parallel Real Time ZZ");
		new Label(grpZigZag, SWT.NONE);
		new Label(grpZigZag, SWT.NONE);

		Group grpBands = new Group(this, SWT.NONE);
		grpBands.setLayout(new GridLayout(3, false));
		grpBands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		grpBands.setText("Bands");

		Label lblTopBottom = new Label(grpBands, SWT.NONE);
		lblTopBottom.setText("Top && Bottom");
		Label l1 = new Label(grpBands, SWT.NONE);
		Label l2 = new Label(grpBands, SWT.NONE);

		_bandsTopBottomWidth = new LineWidthButton(grpBands, SWT.NONE);
		GridData gd_bandsToBottomWidth = new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1);
		gd_bandsToBottomWidth.widthHint = 130;
		_bandsTopBottomWidth.setLayoutData(gd_bandsToBottomWidth);

		_bandsTopBottomType = new LineTypeButton(grpBands, SWT.NONE);
		GridData gd_bandsTopBottomType = new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1);
		gd_bandsTopBottomType.widthHint = 130;
		_bandsTopBottomType.setLayoutData(gd_bandsTopBottomType);

		_bandsEnabled = new Button(grpBands, SWT.CHECK);

		Label lblCenter = new Label(grpBands, SWT.NONE);
		lblCenter.setText("Center");
		new Label(grpBands, SWT.NONE);
		new Label(grpBands, SWT.NONE);

		_bandsCenterWidth = new LineWidthButton(grpBands, SWT.NONE);
		_bandsCenterWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_bandsCenterType = new LineTypeButton(grpBands, SWT.NONE);
		_bandsCenterType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		new Label(grpBands, SWT.NONE);

		Group grpChannels = new Group(this, SWT.NONE);
		grpChannels.setLayout(new GridLayout(3, false));
		grpChannels.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		grpChannels.setText("Channels");

		_channelsWidth = new LineWidthButton(grpChannels, SWT.NONE);
		GridData gd__channelsWidth = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__channelsWidth.widthHint = 130;
		_channelsWidth.setLayoutData(gd__channelsWidth);

		_channelsType = new LineTypeButton(grpChannels, SWT.NONE);
		GridData gd__channelsType = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__channelsType.widthHint = 130;
		_channelsType.setLayoutData(gd__channelsType);

		_channelsEnabled = new Button(grpChannels, SWT.CHECK);

		_group = new Group(this, SWT.NONE);
		_group.setText("Profiles");
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_group.setLayout(new GridLayout(1, false));

		_chartProfilesComposite = new ChartProfilesComposite(_group, SWT.NONE);

	}

	class ScaleInfo {
		boolean visible;
		float[] color;

		public ScaleInfo(ScaleLayer scale) {
			visible = scale.isEnabled();
			color = scale.getLayerColor();
		}
	}

	private final ChartProfilesComposite _chartProfilesComposite;
	private final Group _group;
	private GUIProfileAdapter _adapter;
	private final Button _zzMarkers;
	private final Button _btnParallelRealTimeZZ;
	private final Group _group_1;
	private final ShapeButton _shapeTypeButton;
	private final ShapeWidthButton _shapeWidthButton;
	private final Label _label;
	private final Label _label_1;

	public ARCIndicatorEditor(Composite parent, Chart chart) {
		this(parent, SWT.NONE);
		_chart = chart;

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getIndicatorLayer().getArcProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				ARCSettings s = _chart.getIndicatorLayer().getArcSettings();
				updateUI_FromSettings(s);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				ARCSettings s = new ARCSettings();
				s.updateFromProfile(profile);
				updateUI_FromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				ARCSettings s = new ARCSettings();
				updateSettings_fromUI(s);
				s.fillProfile(profile);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};

		ARCSettings s = _chart.getIndicatorLayer().getArcSettings();
		updateUI_FromSettings(s);
	}

	void updateUI_FromSettings(ARCSettings s) {
		_zzWidth.setLineWidth(s.zzWidth);
		_zzType.setLineType(s.zzType);
		_zzEnable.setSelection(s.zzEnabled);
		_zzMarkers.setSelection(s.zzMarkersEnabled);
		_btnParallelRealTimeZZ.setSelection(s.zzParallel);
		_shapeTypeButton.setShapeType(s.thShapeType);
		_shapeWidthButton.setShapeWidth(s.thShapeWidth);

		_bandsTopBottomWidth.setLineWidth(s.bandsTopBottomWidth);
		_bandsTopBottomType.setLineType(s.bandsTopBottomType);
		_bandsCenterWidth.setLineWidth(s.bandsCenterWidth);
		_bandsCenterType.setLineType(s.bandsCenterType);
		_bandsEnabled.setSelection(s.bandsEnabled);

		_channelsWidth.setLineWidth(s.channelsWidth);
		_channelsType.setLineType(s.channelsType);
		_channelsEnabled.setSelection(s.channelsEnabled);
	}

	@Override
	public void applyChanges() {
		ARCSettings s = new ARCSettings();
		updateSettings_fromUI(s);
		IndicatorLayer layer = _chart.getIndicatorLayer();
		layer.setArcSettings(s);
		layer.getArcProfiledObject().setProfile(_adapter.getSelectedProfile());
	}

	void updateSettings_fromUI(ARCSettings s) {
		s.zzType = _zzType.getLineType();
		s.zzWidth = _zzWidth.getLineWidth();
		s.zzEnabled = _zzEnable.getSelection();
		s.zzMarkersEnabled = _zzMarkers.getSelection();
		s.zzParallel = _btnParallelRealTimeZZ.getSelection();
		s.thShapeType = _shapeTypeButton.getShapeType();
		s.thShapeWidth = _shapeWidthButton.getShapeWidth();

		s.bandsCenterType = _bandsCenterType.getLineType();
		s.bandsCenterWidth = _bandsCenterWidth.getLineWidth();
		s.bandsTopBottomType = _bandsTopBottomType.getLineType();
		s.bandsTopBottomWidth = _bandsTopBottomWidth.getLineWidth();
		s.bandsEnabled = _bandsEnabled.getSelection();

		s.channelsWidth = _channelsWidth.getLineWidth();
		s.channelsType = _channelsType.getLineType();
		s.channelsEnabled = _channelsEnabled.getSelection();
	}

	@Override
	public Composite getUI() {
		return this;
	}
}
