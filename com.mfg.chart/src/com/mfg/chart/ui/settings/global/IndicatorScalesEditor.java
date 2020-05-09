package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IndicatorLayer.IndicatorScalesSettings;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.utils.ui.ColorChooserButton;

public class IndicatorScalesEditor extends Composite implements
		IChartSettingsEditor {
	private ColorChooserButton _colorChooserButton;
	private ColorChooserButton _colorChooserButton_1;
	private ColorChooserButton _colorChooserButton_2;
	private ColorChooserButton _colorChooserButton_3;
	private ColorChooserButton _colorChooserButton_4;
	private ColorChooserButton _colorChooserButton_5;
	private ColorChooserButton _colorChooserButton_6;
	private ColorChooserButton _colorChooserButton_7;
	private ColorChooserButton _colorChooserButton_8;
	private ColorChooserButton _colorChooserButton_9;
	private ColorChooserButton _colorChooserButton_10;
	private ColorChooserButton _colorChooserButton_11;
	private ColorChooserButton _colorChooserButton_12;
	private ColorChooserButton _colorChooserButton_13;
	Chart _chart;
	private ChartProfilesComposite _chartProfilesComposite;
	private GUIProfileAdapter _adapter;
	private ColorChooserButton[] _colors;
	private Button _button;
	private Button _button_1;
	private Button _button_2;
	private Button _button_3;
	private Button _button_4;
	private Button _button_5;
	private Button _button_6;
	private Button _button_7;
	private Button _button_8;
	private Button _button_9;
	private Button _button_10;
	private Button _button_11;
	private Button _button_12;
	private Button _button_13;
	Button[] _visibles;
	private Button _filterScales;
	private Label _label;
	private Text _maxNumberScales;
	private Label _label_1;
	private Text _maxNumberBands;

	public IndicatorScalesEditor(Composite parent, Chart chart) {
		super(parent, SWT.NONE);
		_chart = chart;

		createContent();

		_colors = new ColorChooserButton[] { _colorChooserButton,
				_colorChooserButton_1, _colorChooserButton_2,
				_colorChooserButton_3, _colorChooserButton_4,
				_colorChooserButton_5, _colorChooserButton_6,
				_colorChooserButton_7, _colorChooserButton_8,
				_colorChooserButton_9, _colorChooserButton_10,
				_colorChooserButton_11, _colorChooserButton_12,
				_colorChooserButton_13 };
		_visibles = new Button[] { _button, _button_1, _button_2, _button_3,
				_button_4, _button_5, _button_6, _button_7, _button_8,
				_button_9, _button_10, _button_11, _button_12, _button_13 };
		_filterScales.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateVisibleButtons();
			}
		});

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getIndicatorLayer().getScalesProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				Profile p = new Profile();
				_chart.getIndicatorLayer().getScalesSettings().fillProfile(p);
				IndicatorScalesSettings s = new IndicatorScalesSettings();
				s.updateFromProfile(p);

				for (ScaleLayer layer : _chart.getIndicatorLayer().getScales()) {
					s.scalesVisible[layer.getLevel()] = layer.isVisible();
				}
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				IndicatorScalesSettings s = new IndicatorScalesSettings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				IndicatorScalesSettings s = new IndicatorScalesSettings();
				updateSettings_fromUI(s);
				s.fillProfile(profile);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};

		if (!_chart.getIndicatorLayer().getScales().isEmpty()) {
			int lastLevel = _chart.getIndicatorLayer().getScales().getLast()
					.getLevel();
			int i = 2;
			Font bold = SWTResourceManager.getFont(
					getFont().getFontData()[0].getName(), 9, SWT.BOLD);
			for (Button b : _visibles) {
				if (i <= lastLevel) {
					b.setFont(bold);
				}
				i++;
			}
		}
		_adapter.updateUI_fromToolSettings();
	}

	protected void updateSettings_fromUI(IndicatorScalesSettings s) {
		s.filterEnabled = _filterScales.getSelection();
		for (int i = 0; i < _visibles.length; i++) {
			s.scalesVisible[i + 2] = _visibles[i].getSelection();
			s.scalesColors[i + 2] = _colors[i].getGLColor();
		}
		s.maxVisibleBands = Integer.parseInt(_maxNumberBands.getText());
		s.maxVisibleScales = Integer.parseInt(_maxNumberScales.getText());
	}

	protected void updateUI_fromSettings(IndicatorScalesSettings s) {
		_filterScales.setSelection(s.filterEnabled);
		for (int i = 0; i < _visibles.length; i++) {
			_visibles[i].setSelection(s.scalesVisible[i + 2]);
			_colors[i].setColor(s.scalesColors[i + 2]);
		}
		_maxNumberBands.setText(Integer.toString(s.maxVisibleBands));
		_maxNumberScales.setText(Integer.toString(s.maxVisibleScales));
		updateVisibleButtons();
	}

	@Override
	public void applyChanges() {
		IndicatorScalesSettings s = new IndicatorScalesSettings();
		updateSettings_fromUI(s);
		_chart.getIndicatorLayer().setScalesSettings(s);
		_chart.getIndicatorLayer().applyScalesSettings();
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IndicatorScalesEditor(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	@SuppressWarnings("unused")
	void createContent() {
		setLayout(new GridLayout(1, false));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		_filterScales = new Button(composite, SWT.CHECK);
		_filterScales.setText("Filter Scales");
		new Label(composite, SWT.NONE);

		_label = new Label(composite, SWT.NONE);
		_label.setText("Max Number of Visible Scales");

		_maxNumberScales = new Text(composite, SWT.BORDER);
		_maxNumberScales.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		_label_1 = new Label(composite, SWT.NONE);
		_label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		_label_1.setText("Max Number of Visible Bands");

		_maxNumberBands = new Text(composite, SWT.BORDER);
		_maxNumberBands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Composite composite_1 = new Composite(this, SWT.BORDER);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setLayout(new GridLayout(5, false));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_composite_1.heightHint = 261;
		composite_1.setLayoutData(gd_composite_1);

		Label lblScale = new Label(composite_1, SWT.NONE);
		lblScale.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblScale.setText("Scale");

		Label lblColor = new Label(composite_1, SWT.NONE);
		lblColor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblColor.setText("Color");

		Label label = new Label(composite_1, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_label.widthHint = 30;
		label.setLayoutData(gd_label);
		label.setText("     ");

		Label lblScale_1 = new Label(composite_1, SWT.NONE);
		lblScale_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblScale_1.setText("Scale");

		Label lblColorl = new Label(composite_1, SWT.NONE);
		lblColorl.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblColorl.setText("Color");

		_button = new Button(composite_1, SWT.CHECK);
		_button.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button.setText("2");

		_colorChooserButton = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_colorChooserButton = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_colorChooserButton.widthHint = 100;
		_colorChooserButton.setLayoutData(gd_colorChooserButton);
		new Label(composite_1, SWT.NONE);

		_button_7 = new Button(composite_1, SWT.CHECK);
		_button_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_7.setText("9");

		_colorChooserButton_7 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_7.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_colorChooserButton_7 = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_colorChooserButton_7.widthHint = 100;
		_colorChooserButton_7.setLayoutData(gd_colorChooserButton_7);

		_button_1 = new Button(composite_1, SWT.CHECK);
		_button_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_1.setText("3");

		_colorChooserButton_1 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_8 = new Button(composite_1, SWT.CHECK);
		_button_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_8.setText("10");

		_colorChooserButton_8 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_8.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_button_2 = new Button(composite_1, SWT.CHECK);
		_button_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_2.setText("4");

		_colorChooserButton_2 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_9 = new Button(composite_1, SWT.CHECK);
		_button_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_9.setText("11");

		_colorChooserButton_9 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_9.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_button_3 = new Button(composite_1, SWT.CHECK);
		_button_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_3.setText("5");

		_colorChooserButton_3 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_10 = new Button(composite_1, SWT.CHECK);
		_button_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_10.setText("12");

		_colorChooserButton_10 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_10.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_10.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_button_4 = new Button(composite_1, SWT.CHECK);
		_button_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_4.setText("6");

		_colorChooserButton_4 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_4.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_11 = new Button(composite_1, SWT.CHECK);
		_button_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_11.setText("13");

		_colorChooserButton_11 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_11.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_11.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_button_5 = new Button(composite_1, SWT.CHECK);
		_button_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_5.setText("7");

		_colorChooserButton_5 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_5.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_12 = new Button(composite_1, SWT.CHECK);
		_button_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_12.setText("14");

		_colorChooserButton_12 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_12.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_12.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_button_6 = new Button(composite_1, SWT.CHECK);
		_button_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_6.setText("8");

		_colorChooserButton_6 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_6.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(composite_1, SWT.NONE);

		_button_13 = new Button(composite_1, SWT.CHECK);
		_button_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_button_13.setText("15");

		_colorChooserButton_13 = new ColorChooserButton(composite_1, SWT.NONE);
		_colorChooserButton_13.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton_13.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_chartProfilesComposite = new ChartProfilesComposite(this, SWT.NONE);
	}

	@Override
	public Composite getUI() {
		return this;
	}

	void updateVisibleButtons() {
		for (Button b : _visibles) {
			b.setEnabled(!_filterScales.getSelection());
		}
	}

}
