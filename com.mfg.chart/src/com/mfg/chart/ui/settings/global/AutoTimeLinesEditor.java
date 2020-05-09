package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.IndicatorLayer.ATLSettings;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class AutoTimeLinesEditor extends Composite implements
		IChartSettingsEditor {

	Chart _chart;
	private final Button _btnEnabled;
	private final ColorChooserButton _anchorColor;
	private final Button _btnRatio;
	private final Button _btnRatio_1;
	private final Button _btnRatio_2;
	private final Button _btnRatio_3;
	private final Button _btnRatio_4;
	private final Button _btnRatio_5;
	private final Button _btnRatio_6;
	private final Button _btnRatio_7;
	private final Button _btnRatio_8;
	private final Button _btnRatio_9;
	private final ColorChooserButton _colorChooserButton;
	private final ColorChooserButton _colorChooserButton_1;
	private final ColorChooserButton _colorChooserButton_2;
	private final ColorChooserButton _colorChooserButton_3;
	private final ColorChooserButton _colorChooserButton_4;
	private final ColorChooserButton _colorChooserButton_5;
	private final ColorChooserButton _colorChooserButton_6;
	private final ColorChooserButton _colorChooserButton_7;
	private final ColorChooserButton _colorChooserButton_8;
	private final ColorChooserButton _colorChooserButton_9;
	private final LineWidthButton _lineWidthButton;
	private final LineWidthButton _lineWidthButton_1;
	private final LineWidthButton _lineWidthButton_2;
	private final LineWidthButton _lineWidthButton_3;
	private final LineWidthButton _lineWidthButton_4;
	private final LineWidthButton _lineWidthButton_5;
	private final LineWidthButton _lineWidthButton_6;
	private final LineWidthButton _lineWidthButton_7;
	private final LineWidthButton _lineWidthButton_8;
	private final LineWidthButton _lineWidthButton_9;
	private final LineTypeButton _lineTypeButton;
	private final LineTypeButton _lineTypeButton_1;
	private final LineTypeButton _lineTypeButton_2;
	private final LineTypeButton _lineTypeButton_3;
	private final LineTypeButton _lineTypeButton_4;
	private final LineTypeButton _lineTypeButton_5;
	private final LineTypeButton _lineTypeButton_6;
	private final LineTypeButton _lineTypeButton_7;
	private final LineTypeButton _lineTypeButton_8;
	private final LineTypeButton _lineTypeButton_9;
	private Button[] _ratiosChecks;
	private ColorChooserButton[] _ratioColor;
	private LineWidthButton[] _ratioWidth;
	private LineTypeButton[] _ratioType;
	private final ChartProfilesComposite _chartProfilesComposite;
	private final Group _group;
	private GUIProfileAdapter _adapter;
	private Text _ratio1;
	private Text _ratio2;
	private Text _ratio3;
	private Text _ratio4;
	private Text _ratio5;
	private Text _ratio6;
	private Text _ratio7;
	private Text _ratio8;
	private Text _ratio9;
	private Text _ratio10;
	private Text[] _ratioValues;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public AutoTimeLinesEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		composite.setLayout(new GridLayout(2, false));

		Label lblAnchorColor = new Label(composite, SWT.NONE);
		lblAnchorColor.setText("Anchors Color");

		_anchorColor = new ColorChooserButton(composite, SWT.NONE);
		GridData gd__anchorColor = new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1);
		gd__anchorColor.heightHint = 30;
		gd__anchorColor.widthHint = 50;
		_anchorColor.setLayoutData(gd__anchorColor);

		Group grpRatioLines = new Group(this, SWT.NONE);
		grpRatioLines.setLayout(new GridLayout(1, false));
		grpRatioLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1));
		grpRatioLines.setText("Ratio Lines");

		Label lblRatiosCanHave = new Label(grpRatioLines, SWT.NONE);
		lblRatiosCanHave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblRatiosCanHave.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblRatiosCanHave
				.setText("Ratios can have at most 3 decimals like 0.618");

		Composite composite_1 = new Composite(grpRatioLines, SWT.BORDER);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setLayout(new GridLayout(5, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1));

		Label lblLine = new Label(composite_1, SWT.NONE);
		lblLine.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLine.setText("Line");
		Label lblb = new Label(composite_1, SWT.NONE);

		Label lblColor = new Label(composite_1, SWT.NONE);
		lblColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblColor.setText("Color");

		Label lblWidth = new Label(composite_1, SWT.NONE);
		lblWidth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblWidth.setText("Width");

		Label lblType = new Label(composite_1, SWT.NONE);
		lblType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblType.setText("Type");

		_btnRatio = new Button(composite_1, SWT.CHECK);
		GridData gd__btnRatio = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__btnRatio.widthHint = 66;
		_btnRatio.setLayoutData(gd__btnRatio);
		_btnRatio.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio.setText("Ratio 1");

		_ratio1 = new Text(composite_1, SWT.BORDER);
		_ratio1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton = new ColorChooserButton(composite_1, SWT.NONE);
		GridData gd__colorChooserButton = new GridData(SWT.LEFT, SWT.FILL,
				false, false, 1, 1);
		gd__colorChooserButton.widthHint = 50;
		_colorChooserButton.setLayoutData(gd__colorChooserButton);

		_lineWidthButton = new LineWidthButton(composite_1, SWT.NONE);
		GridData gd__lineWidthButton = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd__lineWidthButton.widthHint = 150;
		_lineWidthButton.setLayoutData(gd__lineWidthButton);

		_lineTypeButton = new LineTypeButton(composite_1, SWT.NONE);
		GridData gd__lineTypeButton = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd__lineTypeButton.widthHint = 150;
		_lineTypeButton.setLayoutData(gd__lineTypeButton);

		_btnRatio_1 = new Button(composite_1, SWT.CHECK);
		_btnRatio_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_1.setText("Ratio 2");

		_ratio2 = new Text(composite_1, SWT.BORDER);
		_ratio2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_1 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_1 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_1 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_2 = new Button(composite_1, SWT.CHECK);
		_btnRatio_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_2.setText("Ratio 3");

		_ratio3 = new Text(composite_1, SWT.BORDER);
		_ratio3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_2 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_2 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_2 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_3 = new Button(composite_1, SWT.CHECK);
		_btnRatio_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_3.setText("Ratio 4");

		_ratio4 = new Text(composite_1, SWT.BORDER);
		_ratio4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_3 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_3 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_3 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_4 = new Button(composite_1, SWT.CHECK);
		_btnRatio_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_4.setText("Ratio 5");

		_ratio5 = new Text(composite_1, SWT.BORDER);
		_ratio5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_4 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_4 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_4 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_5 = new Button(composite_1, SWT.CHECK);
		_btnRatio_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_5.setText("Ratio 6");

		_ratio6 = new Text(composite_1, SWT.BORDER);
		_ratio6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_5 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_5 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_5 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_6 = new Button(composite_1, SWT.CHECK);
		_btnRatio_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_6.setText("Ratio 7");

		_ratio7 = new Text(composite_1, SWT.BORDER);
		_ratio7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_6 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_6 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_6 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_7 = new Button(composite_1, SWT.CHECK);
		_btnRatio_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_7.setText("Ratio 8");

		_ratio8 = new Text(composite_1, SWT.BORDER);
		_ratio8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_7 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_7 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_7 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_8 = new Button(composite_1, SWT.CHECK);
		_btnRatio_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_8.setText("Ratio 9");

		_ratio9 = new Text(composite_1, SWT.BORDER);
		_ratio9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_8 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_8 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_8 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnRatio_9 = new Button(composite_1, SWT.CHECK);
		_btnRatio_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_btnRatio_9.setText("Ratio 10");

		_ratio10 = new Text(composite_1, SWT.BORDER);
		_ratio10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_colorChooserButton_9 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton_9 = new LineWidthButton(composite_1, SWT.NONE);
		_lineWidthButton_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_lineTypeButton_9 = new LineTypeButton(composite_1, SWT.NONE);
		_lineTypeButton_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_btnEnabled = new Button(grpRatioLines, SWT.CHECK);
		_btnEnabled.setText("Enabled");

		_group = new Group(this, SWT.NONE);
		_group.setText("Profiles");
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_group.setLayout(new GridLayout(1, false));

		_chartProfilesComposite = new ChartProfilesComposite(_group, SWT.NONE);

	}

	public AutoTimeLinesEditor(Composite parent, Chart chart) {
		this(parent, SWT.None);
		_chart = chart;

		_ratiosChecks = new Button[] { _btnRatio, _btnRatio_1, _btnRatio_2,
				_btnRatio_3, _btnRatio_4, _btnRatio_5, _btnRatio_6,
				_btnRatio_7, _btnRatio_8, _btnRatio_9 };

		_ratioValues = new Text[] { _ratio1, _ratio2, _ratio3, _ratio4,
				_ratio5, _ratio6, _ratio7, _ratio8, _ratio9, _ratio10 };

		_ratioColor = new ColorChooserButton[] { _colorChooserButton,
				_colorChooserButton_1, _colorChooserButton_2,
				_colorChooserButton_3, _colorChooserButton_4,
				_colorChooserButton_5, _colorChooserButton_6,
				_colorChooserButton_7, _colorChooserButton_8,
				_colorChooserButton_9 };
		_ratioWidth = new LineWidthButton[] { _lineWidthButton,
				_lineWidthButton_1, _lineWidthButton_2, _lineWidthButton_3,
				_lineWidthButton_4, _lineWidthButton_5, _lineWidthButton_6,
				_lineWidthButton_7, _lineWidthButton_8, _lineWidthButton_9 };

		_ratioType = new LineTypeButton[] { _lineTypeButton, _lineTypeButton_1,
				_lineTypeButton_2, _lineTypeButton_3, _lineTypeButton_4,
				_lineTypeButton_5, _lineTypeButton_6, _lineTypeButton_7,
				_lineTypeButton_8, _lineTypeButton_9 };

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getIndicatorLayer().getAtlProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_chart.getIndicatorLayer()
						.getAtlSettings());
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				ATLSettings s = new ATLSettings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				ATLSettings s = new ATLSettings();
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

	void updateUI_fromSettings(ATLSettings s) {
		RatioInfo[] globalRatios = s.ratios;
		float[] anchorColor = s.anchorsColor;

		_anchorColor.setColor(anchorColor);
		for (int i = 0; i < 10; i++) {
			RatioInfo info = globalRatios[i];
			_ratiosChecks[i].setSelection(info.isSelected());
			_ratioValues[i].setText(Float.toString(info.getRatio()));
			_ratioColor[i].setColor(info.getColor());
			_ratioWidth[i].setLineWidth(info.getLineWidth());
			_ratioType[i].setLineType(info.getLineType());
		}
		_btnEnabled.setSelection(s.enabled);
	}

	void updateSettings_fromUI(ATLSettings s) {
		s.enabled = _btnEnabled.getSelection();
		s.anchorsColor = _anchorColor.getGLColor();
		for (int i = 0; i < 10; i++) {
			RatioInfo info = s.ratios[i];
			info.setSelected(_ratiosChecks[i].getSelection());
			info.setRatio(Float.parseFloat(_ratioValues[i].getText()));
			info.setColor(_ratioColor[i].getGLColor());
			info.setLineWidth(_ratioWidth[i].getLineWidth());
			info.setLineType(_ratioType[i].getLineType());
		}
	}

	@Override
	public void applyChanges() {
		ATLSettings s = new ATLSettings();
		updateSettings_fromUI(s);
		IndicatorLayer indicatorLayer = _chart.getIndicatorLayer();
		indicatorLayer.setAtlSettings(s);
		indicatorLayer.getAtlProfiledObject().setProfile(
				_adapter.getSelectedProfile());
	}

	@Override
	public Composite getUI() {
		return this;
	}
}
