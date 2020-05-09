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
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.interactive.TimeLinesTool;
import com.mfg.chart.ui.interactive.TimeLinesTool.Settings;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class TimeLineToolEditorComp extends Composite {

	private Chart _chart;

	private Text _text1;
	private Text _text2;
	private Text _text3;
	private Text _text4;
	private Text _text5;
	private Text _text6;
	private Text _text7;
	private Text _text8;
	private Text _text9;
	private Text _text10;
	private Button _check1;
	private Button _check2;
	private Button _check3;
	private Button _check4;
	private Button _check5;
	private Button _check6;
	private Button _check7;
	private Button _check8;
	private Button _check9;
	private Button _check10;
	private Button[] _checks;
	private Text[] _ratios;
	private ColorChooserButton[] _colors;
	private ColorChooserButton _color1;
	private ColorChooserButton _color2;
	private ColorChooserButton _color3;
	private ColorChooserButton _color4;
	private ColorChooserButton _color5;
	private ColorChooserButton _color6;
	private ColorChooserButton _color7;
	private ColorChooserButton _color8;
	private ColorChooserButton _color9;
	private ColorChooserButton _color10;
	private LineWidthButton _width1;
	private LineWidthButton _width2;
	private LineWidthButton _width3;
	private LineWidthButton _width4;
	private LineWidthButton _width5;
	private LineWidthButton _width6;
	private LineWidthButton _width7;
	private LineWidthButton _width8;
	private LineWidthButton _width9;
	private LineWidthButton _width10;
	private LineWidthButton[] _widths;
	private ChartProfilesComposite _chartProfilesComposite;
	private TimeLinesTool _tool;
	private GUIProfileAdapter _adapter;
	Settings _settings;
	private ColorChooserButton _colorAnchors;
	private LineWidthButton _widthAnchorButton;
	private LineTypeButton _lineTypeAnchorButton;
	private LineTypeButton _lineTypeButton_1;
	private LineTypeButton _lineTypeButton_2;
	private LineTypeButton _lineTypeButton_3;
	private LineTypeButton _lineTypeButton_4;
	private LineTypeButton _lineTypeButton_5;
	private LineTypeButton _lineTypeButton_6;
	private LineTypeButton _lineTypeButton_7;
	private LineTypeButton _lineTypeButton_8;
	private LineTypeButton _lineTypeButton_9;
	private LineTypeButton _lineTypeButton_10;
	private LineTypeButton[] _types;
	private boolean _generalSettings = false;
	private Group _group;
	private Button _btnUsePricesFor;
	private Button _btnUsePivotsFor;

	private TimeLines _timeLines;

	public TimeLineToolEditorComp(Composite parent, Chart chart, Object context) {
		super(parent, SWT.None);
		_chart = chart;
		_tool = _chart.getTool(TimeLinesTool.class);
		_generalSettings = context == null || !(context instanceof TimeLines);
		if (_generalSettings) {
			_settings = _tool.getDefaultSettings();
		} else {
			_timeLines = (TimeLines) context;
			_settings = _timeLines.getSettings().clone();
		}

		createContent();
	}

	public TimeLineToolEditorComp(Composite parent, int style) {
		super(parent, style);
		createContent();

	}

	@SuppressWarnings("unused")
	void createContent() {
		this.setLayout(new GridLayout(1, false));

		Group grpAnchors = new Group(this, SWT.NONE);
		grpAnchors.setLayout(new GridLayout(3, false));
		grpAnchors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpAnchors.setText("Anchors");

		_colorAnchors = new ColorChooserButton(grpAnchors, SWT.NONE);
		GridData gd_colorAnchors = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_colorAnchors.widthHint = 40;
		_colorAnchors.setLayoutData(gd_colorAnchors);

		_widthAnchorButton = new LineWidthButton(grpAnchors, SWT.NONE);

		_lineTypeAnchorButton = new LineTypeButton(grpAnchors, SWT.NONE);

		Label lblTimeLines = new Label(this, SWT.NONE);
		lblTimeLines.setText("Time Lines");

		Composite grpTimeLines = new Composite(this, SWT.BORDER);
		grpTimeLines
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_grpTimeLines = new GridLayout(5, false);
		gl_grpTimeLines.horizontalSpacing = 10;
		grpTimeLines.setLayout(gl_grpTimeLines);
		grpTimeLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		Label lblLine = new Label(grpTimeLines, SWT.NONE);
		lblLine.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLine.setText("Line");

		Label lblRatio = new Label(grpTimeLines, SWT.NONE);
		lblRatio.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblRatio.setText("Ratio");

		Label lblColor = new Label(grpTimeLines, SWT.NONE);
		lblColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblColor.setText("Color");

		Label lblLineWidth = new Label(grpTimeLines, SWT.NONE);
		lblLineWidth
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLineWidth.setText("Line Width");
		new Label(grpTimeLines, SWT.NONE);

		_check1 = new Button(grpTimeLines, SWT.CHECK);
		_check1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check1.setText("1");

		_text1 = new Text(grpTimeLines, SWT.BORDER);
		GridData gd_text1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1);
		gd_text1.widthHint = 150;
		_text1.setLayoutData(gd_text1);

		_color1 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_color1 = new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1);
		gd_color1.widthHint = 40;
		_color1.setLayoutData(gd_color1);

		_width1 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_1 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check2 = new Button(grpTimeLines, SWT.CHECK);
		_check2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check2.setText("2");

		_text2 = new Text(grpTimeLines, SWT.BORDER);
		_text2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color2 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width2 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_2 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check3 = new Button(grpTimeLines, SWT.CHECK);
		_check3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check3.setText("3");

		_text3 = new Text(grpTimeLines, SWT.BORDER);
		_text3.setText("");
		_text3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color3 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width3 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_3 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_3.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check4 = new Button(grpTimeLines, SWT.CHECK);
		_check4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check4.setText("4");

		_text4 = new Text(grpTimeLines, SWT.BORDER);
		_text4.setText("");
		_text4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color4 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width4 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_4 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_4.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check5 = new Button(grpTimeLines, SWT.CHECK);
		_check5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check5.setText("5");

		_text5 = new Text(grpTimeLines, SWT.BORDER);
		_text5.setText("");
		_text5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color5 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width5 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_5 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_5.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check6 = new Button(grpTimeLines, SWT.CHECK);
		_check6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check6.setText("6");

		_text6 = new Text(grpTimeLines, SWT.BORDER);
		_text6.setText("");
		_text6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color6 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width6 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_6 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_6.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check7 = new Button(grpTimeLines, SWT.CHECK);
		_check7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check7.setText("7");

		_text7 = new Text(grpTimeLines, SWT.BORDER);
		_text7.setText("");
		_text7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color7 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width7 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_7 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_7.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check8 = new Button(grpTimeLines, SWT.CHECK);
		_check8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check8.setText("8");

		_text8 = new Text(grpTimeLines, SWT.BORDER);
		_text8.setText("");
		_text8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color8 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width8 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_8 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_8.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check9 = new Button(grpTimeLines, SWT.CHECK);
		_check9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check9.setText("9");

		_text9 = new Text(grpTimeLines, SWT.BORDER);
		_text9.setText("");
		_text9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color9 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width9 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_9 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_9.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_check10 = new Button(grpTimeLines, SWT.CHECK);
		_check10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_check10.setText("10");

		_text10 = new Text(grpTimeLines, SWT.BORDER);
		_text10.setText("");
		_text10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_color10 = new ColorChooserButton(grpTimeLines, SWT.NONE);
		_color10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_color10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));

		_width10 = new LineWidthButton(grpTimeLines, SWT.NONE);
		_width10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_width10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		_lineTypeButton_10 = new LineTypeButton(grpTimeLines, SWT.NONE);
		_lineTypeButton_10.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

		_group = new Group(this, SWT.NONE);
		_group.setLayout(new GridLayout(1, false));
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		_btnUsePivotsFor = new Button(_group, SWT.RADIO);
		_btnUsePivotsFor.setText("Use pivots for anchors points");

		_btnUsePricesFor = new Button(_group, SWT.RADIO);
		_btnUsePricesFor.setText("Use prices for anchors points");

		Group grpProfiles = new Group(this, SWT.NONE);
		grpProfiles.setText("Profiles");
		grpProfiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		GridLayout gl_grpProfiles = new GridLayout(1, false);
		gl_grpProfiles.marginHeight = 0;
		gl_grpProfiles.marginWidth = 0;
		grpProfiles.setLayout(gl_grpProfiles);

		_chartProfilesComposite = new ChartProfilesComposite(grpProfiles,
				SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		_checks = new Button[] { _check1, _check2, _check3, _check4, _check5,
				_check6, _check7, _check8, _check9, _check10 };
		_ratios = new Text[] { _text1, _text2, _text3, _text4, _text5, _text6,
				_text7, _text8, _text9, _text10 };
		_colors = new ColorChooserButton[] { _color1, _color2, _color3,
				_color4, _color5, _color6, _color7, _color8, _color9, _color10 };
		_widths = new LineWidthButton[] { _width1, _width2, _width3, _width4,
				_width5, _width6, _width7, _width8, _width9, _width10 };

		_types = new LineTypeButton[] { _lineTypeButton_1, _lineTypeButton_2,
				_lineTypeButton_3, _lineTypeButton_4, _lineTypeButton_5,
				_lineTypeButton_6, _lineTypeButton_7, _lineTypeButton_8,
				_lineTypeButton_9, _lineTypeButton_10 };

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

		if (_generalSettings) {
			// global settings window
			// _chartProfilesComposite.hideSaveButton();
		}
	}

	public void applyChanges() {
		// TODO: see what to do with the delete lines
		updateSettingsFromWidgets(_settings);

		_tool.getDefaultSettings().updateFromSettings(_settings);
		_tool.setProfile(_adapter.getSelectedProfile());

		if (!_generalSettings) {
			_timeLines.setSettings(_settings.clone());
			_tool.getDefaultSettings().setUsePivotsForAnchorPoints(
					_timeLines.getSettings().isUsePivotsForAnchorPoints());
		}
	}

	protected void updateSettingsFromWidgets(Settings settings) {
		settings.setAnchorColor(ColorChooserButton.rgbToColor(_colorAnchors
				.getColor()));
		settings.setAnchorLineWidth(_widthAnchorButton.getLineWidth());
		settings.setAnchorLineType(_lineTypeAnchorButton.getLineType());
		settings.setUsePivotsForAnchorPoints(_btnUsePivotsFor.getSelection());

		RatioInfo[] infos = settings.getRatios();
		for (int i = 0; i < _checks.length; i++) {
			RatioInfo info = infos[i];
			info.setSelected(_checks[i].getSelection());
			info.setRatio(Float.parseFloat(_ratios[i].getText()));
			info.setColor(ColorChooserButton.rgbToColor(_colors[i].getColor()));
			info.setLineWidth(_widths[i].getLineWidth());
			info.setLineType(_types[i].getLineType());
		}
	}

	protected void updateWidgetsFromSettings(Settings settings) {
		_colorAnchors.setColor(settings.getAnchorColor());
		_widthAnchorButton.setLineWidth(settings.getAnchorLineWidth());
		_lineTypeAnchorButton.setLineType(settings.getAnchorLineType());
		_btnUsePivotsFor.setSelection(settings.isUsePivotsForAnchorPoints());
		_btnUsePricesFor.setSelection(!settings.isUsePivotsForAnchorPoints());

		RatioInfo[] infos = settings.getRatios();
		for (int i = 0; i < _checks.length; i++) {
			RatioInfo info = infos[i];
			_checks[i].setSelection(info.isSelected());
			_ratios[i].setText(Float.toString(info.getRatio()));
			_colors[i].setColor(info.getColor());
			_widths[i].setLineWidth(info.getLineWidth());
			_types[i].setLineType(info.getLineType());
		}

	}
}
