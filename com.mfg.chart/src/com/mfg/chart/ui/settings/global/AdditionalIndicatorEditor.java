package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IndicatorLayer.AdditionalSettings;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;

public class AdditionalIndicatorEditor extends Composite implements
		IChartSettingsEditor {
	Chart _chart;
	private final LineWidthButton _probsProfitWidth;
	private final LineTypeButton _autoType;
	private final LineWidthButton _autoWidth;
	private final LineWidthButton _probsLossWidth;
	private final LineTypeButton _probsLossType;
	private final LineTypeButton _probsProfitType;
	private final Button _probsEnabled;
	private final Button _autoEnabled;
	private final ChartProfilesComposite _chartProfilesComposite;
	private final Group _group;
	private GUIProfileAdapter _adapter;

	public AdditionalIndicatorEditor(Composite parent, Chart chart) {
		this(parent, SWT.NONE);
		_chart = chart;

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getIndicatorLayer().getAdditionalProfileObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_chart.getIndicatorLayer()
						.getAdditionalSettings());
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				AdditionalSettings s = new AdditionalSettings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				AdditionalSettings s = new AdditionalSettings();
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

	protected void updateSettings_fromUI(AdditionalSettings s) {
		s.probsEnabled = _probsEnabled.getSelection();
		s.probsLossLineType = _probsLossType.getLineType();
		s.probsLossLineWidth = _probsLossWidth.getLineWidth();
		s.probsProfitLineType = _probsProfitType.getLineType();
		s.probsProfitLineWidth = _probsProfitWidth.getLineWidth();

		s.autoTrendLinesEnabled = _autoEnabled.getSelection();
		s.autoTrendLinesLineWidth = _autoWidth.getLineWidth();
		s.autoTrendLinesLineType = _autoType.getLineType();
	}

	protected void updateUI_fromSettings(AdditionalSettings s) {
		_probsEnabled.setSelection(s.probsEnabled);
		_probsLossWidth.setLineWidth(s.probsLossLineWidth);
		_probsLossType.setLineType(s.probsLossLineType);
		_probsProfitWidth.setLineWidth(s.probsProfitLineWidth);
		_probsProfitType.setLineType(s.probsProfitLineType);

		_autoEnabled.setSelection(s.autoTrendLinesEnabled);
		_autoWidth.setLineWidth(s.autoTrendLinesLineWidth);
		_autoType.setLineType(s.autoTrendLinesLineType);
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public AdditionalIndicatorEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Group grpProbabilities = new Group(this, SWT.NONE);
		grpProbabilities.setLayout(new GridLayout(2, false));
		grpProbabilities.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpProbabilities.setText("Probabilities");

		_probsEnabled = new Button(grpProbabilities, SWT.CHECK);
		_probsEnabled.setText("Enabled");
		new Label(grpProbabilities, SWT.NONE);

		Label lblProft = new Label(grpProbabilities, SWT.NONE);
		lblProft.setText("Profit");
		new Label(grpProbabilities, SWT.NONE);

		_probsProfitWidth = new LineWidthButton(grpProbabilities, SWT.NONE);
		GridData gd__probsProfitWidth = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd__probsProfitWidth.widthHint = 130;
		_probsProfitWidth.setLayoutData(gd__probsProfitWidth);

		_probsProfitType = new LineTypeButton(grpProbabilities, SWT.NONE);
		GridData gd__probsProfitType = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd__probsProfitType.widthHint = 130;
		_probsProfitType.setLayoutData(gd__probsProfitType);

		Label lblLoss = new Label(grpProbabilities, SWT.NONE);
		lblLoss.setText("Loss");
		new Label(grpProbabilities, SWT.NONE);

		_probsLossWidth = new LineWidthButton(grpProbabilities, SWT.NONE);
		_probsLossWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		_probsLossType = new LineTypeButton(grpProbabilities, SWT.NONE);
		_probsLossType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		Group grpAutoTrendLines = new Group(this, SWT.NONE);
		grpAutoTrendLines.setLayout(new GridLayout(2, false));
		grpAutoTrendLines.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		grpAutoTrendLines.setText("Auto Trend Lines");

		_autoEnabled = new Button(grpAutoTrendLines, SWT.CHECK);
		_autoEnabled.setText("Enabled");
		new Label(grpAutoTrendLines, SWT.NONE);

		_autoWidth = new LineWidthButton(grpAutoTrendLines, SWT.NONE);
		GridData gd__autoWidth = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__autoWidth.widthHint = 130;
		_autoWidth.setLayoutData(gd__autoWidth);

		_autoType = new LineTypeButton(grpAutoTrendLines, SWT.NONE);
		GridData gd__autoType = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd__autoType.widthHint = 130;
		_autoType.setLayoutData(gd__autoType);

		_group = new Group(this, SWT.NONE);
		_group.setText("Profiles");
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		_group.setLayout(new GridLayout(1, false));

		_chartProfilesComposite = new ChartProfilesComposite(_group, SWT.NONE);

	}

	@Override
	public void applyChanges() {
		AdditionalSettings s = new AdditionalSettings();
		updateSettings_fromUI(s);
		_chart.getIndicatorLayer().setAdditionalSettings(s);
	}

	@Override
	public Composite getUI() {
		return this;
	}
}
