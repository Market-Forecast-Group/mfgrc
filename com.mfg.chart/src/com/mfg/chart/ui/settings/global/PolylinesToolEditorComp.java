package com.mfg.chart.ui.settings.global;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.chart.ui.interactive.PolylineTool.Line;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class PolylinesToolEditorComp extends Composite {

	Line _line;
	private ColorChooserButton _colorChooserButton1;
	private Button _btnRegressionLine1;
	private Button _btnPolyline2;
	private Button _btnPolyline3;
	private Button _btnPolyline4;

	private ColorChooserButton _colorChooserButton2;
	private ColorChooserButton _colorChooserButton3;
	private ColorChooserButton _colorChooserButton4;

	private Button[] _checkButtons;

	private ColorChooserButton[] _colorsButtons;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Composite _composite;
	private LineWidthButton _lineWidthButton1;
	private LineWidthButton _lineWidthButton2;
	private LineWidthButton _lineWidthButton3;
	private LineWidthButton _lineWidthButton4;

	private LineWidthButton[] _widthButtons;
	private ChartProfilesComposite _chartProfilesComposite;

	PolylineTool _tool;

	GUIProfileAdapter _adapter;
	private Text _npointsText;

	private int _npoints;
	private Button _btnUsePivots;
	boolean _usePivotsForAnchorPoints;
	private Button _btnUsePrices;
	private LineTypeButton _type1;
	private LineTypeButton _type2;
	private LineTypeButton _type3;
	private LineTypeButton _type4;
	private LineTypeButton[] _typeButtons;
	private Button[] _mirrorButtons;
	private Line _originalLine;
	private Label _label;
	private Button _mirror1;
	private Button _mirror2;
	private Button _mirror3;
	private Button _mirror4;
	private Label _label_1;
	private Label _label_2;
	private Label _label_3;
	private Label _label_4;

	public PolylinesToolEditorComp(Composite parent, Chart chart, Object context) {
		super(parent, SWT.NONE);
		_tool = chart.getTool(PolylineTool.class);
		if (context != null && context instanceof Line) {
			_originalLine = (Line) context;
		} else {
			_originalLine = _tool.getDefaultLine();
		}

		_line = new Line();
		_line.updateFromLine(_originalLine);

		_npoints = PolylineTool.getPlotingNumberOfPoints();
		_usePivotsForAnchorPoints = _tool.isUsePivotsForAnchorPoints();

		createContents();
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PolylinesToolEditorComp(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	@SuppressWarnings("unused")
	private void createContents() {
		this.setLayout(new GridLayout(1, false));

		_composite = formToolkit.createComposite(this, SWT.BORDER);
		_composite.setLayout(new GridLayout(5, false));
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		formToolkit.paintBordersFor(_composite);
		new Label(_composite, SWT.NONE);

		_label_1 = new Label(_composite, SWT.NONE);
		formToolkit.adapt(_label_1, true, true);
		_label_1.setText("Color");

		_label_2 = new Label(_composite, SWT.NONE);
		formToolkit.adapt(_label_2, true, true);
		_label_2.setText("Width");

		_label_3 = new Label(_composite, SWT.NONE);
		formToolkit.adapt(_label_3, true, true);
		_label_3.setText("Type");

		_label = new Label(_composite, SWT.NONE);
		formToolkit.adapt(_label, true, true);
		_label.setText("Mirror");

		_btnRegressionLine1 = new Button(_composite, SWT.CHECK);
		GridData gd_btnRegressionLine1 = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_btnRegressionLine1.widthHint = 138;
		_btnRegressionLine1.setLayoutData(gd_btnRegressionLine1);
		_btnRegressionLine1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_btnRegressionLine1.setText("Regression Line");

		_colorChooserButton1 = new ColorChooserButton(_composite, SWT.NONE);
		_colorChooserButton1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_colorChooserButton1 = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_colorChooserButton1.widthHint = 128;
		_colorChooserButton1.setLayoutData(gd_colorChooserButton1);

		_lineWidthButton1 = new LineWidthButton(_composite, SWT.NONE);
		_lineWidthButton1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_lineWidthButton1 = new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1);
		gd_lineWidthButton1.widthHint = 127;
		_lineWidthButton1.setLayoutData(gd_lineWidthButton1);
		formToolkit.adapt(_lineWidthButton1);
		formToolkit.paintBordersFor(_lineWidthButton1);

		_type1 = new LineTypeButton(_composite, SWT.NONE);
		_type1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_type1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_type1.widthHint = 127;
		_type1.setLayoutData(gd_type1);
		formToolkit.adapt(_type1);
		formToolkit.paintBordersFor(_type1);

		_mirror1 = new Button(_composite, SWT.CHECK);
		formToolkit.adapt(_mirror1, true, true);
		_btnPolyline2 = new Button(_composite, SWT.CHECK);
		_btnPolyline2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_btnPolyline2.setText("Polyline 2\u00B0");

		_colorChooserButton2 = new ColorChooserButton(_composite, SWT.NONE);
		_colorChooserButton2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton2.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton2 = new LineWidthButton(_composite, SWT.NONE);
		_lineWidthButton2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		_lineWidthButton2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_lineWidthButton2);
		formToolkit.paintBordersFor(_lineWidthButton2);

		_type2 = new LineTypeButton(_composite, SWT.NONE);
		GridData gd_type2 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_type2.widthHint = 127;
		_type2.setLayoutData(gd_type2);
		_type2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_type2);
		formToolkit.paintBordersFor(_type2);

		_mirror2 = new Button(_composite, SWT.CHECK);
		formToolkit.adapt(_mirror2, true, true);

		_btnPolyline3 = new Button(_composite, SWT.CHECK);
		_btnPolyline3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_btnPolyline3.setText("Polyline 3\u00B0");

		_colorChooserButton3 = new ColorChooserButton(_composite, SWT.NONE);
		_colorChooserButton3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton3.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton3 = new LineWidthButton(_composite, SWT.NONE);
		_lineWidthButton3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		_lineWidthButton3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_lineWidthButton3);
		formToolkit.paintBordersFor(_lineWidthButton3);

		_type3 = new LineTypeButton(_composite, SWT.NONE);
		GridData gd_type3 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_type3.widthHint = 127;
		_type3.setLayoutData(gd_type3);
		_type3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_type3);
		formToolkit.paintBordersFor(_type3);

		_mirror3 = new Button(_composite, SWT.CHECK);
		formToolkit.adapt(_mirror3, true, true);

		_btnPolyline4 = new Button(_composite, SWT.CHECK);
		_btnPolyline4.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_btnPolyline4.setText("Polyline 4\u00B0");

		_colorChooserButton4 = new ColorChooserButton(_composite, SWT.NONE);
		_colorChooserButton4.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_colorChooserButton4.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_lineWidthButton4 = new LineWidthButton(_composite, SWT.NONE);
		_lineWidthButton4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		_lineWidthButton4.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_lineWidthButton4);
		formToolkit.paintBordersFor(_lineWidthButton4);

		_type4 = new LineTypeButton(_composite, SWT.NONE);
		GridData gd_type4 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_type4.widthHint = 127;
		_type4.setLayoutData(gd_type4);
		_type4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		formToolkit.adapt(_type4);
		formToolkit.paintBordersFor(_type4);

		_mirror4 = new Button(_composite, SWT.CHECK);
		formToolkit.adapt(_mirror4, true, true);

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		_btnUsePivots = new Button(group, SWT.RADIO);
		GridData gd_btnUsePivots = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_btnUsePivots.widthHint = 260;
		_btnUsePivots.setLayoutData(gd_btnUsePivots);
		_btnUsePivots.setText("Use pivots for anchors points");

		Label lblNumberOfPoints = new Label(group, SWT.NONE);
		lblNumberOfPoints.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblNumberOfPoints.setText("Number of Points");

		_npointsText = new Text(group, SWT.BORDER);
		_npointsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		_btnUsePrices = new Button(group, SWT.RADIO);
		_btnUsePrices.setText("Use prices for anchors points");

		_label_4 = new Label(group, SWT.NONE);
		_label_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				2, 1));
		_label_4.setText("For real-time update, press SHIFT key\nwhen adding the first anchor.");

		Group _group = new Group(this, SWT.NONE);
		_group.setText("Profiles");
		_group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl__group = new GridLayout(1, false);
		gl__group.marginWidth = 0;
		gl__group.marginHeight = 0;
		_group.setLayout(gl__group);

		_chartProfilesComposite = new ChartProfilesComposite(_group, SWT.NONE);
		GridData gd_chartProfilesComposite = new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1);
		gd_chartProfilesComposite.widthHint = 540;
		_chartProfilesComposite.setLayoutData(gd_chartProfilesComposite);

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		_checkButtons = new Button[] { _btnRegressionLine1, _btnPolyline2,
				_btnPolyline3, _btnPolyline4 };
		_colorsButtons = new ColorChooserButton[] { _colorChooserButton1,
				_colorChooserButton2, _colorChooserButton3,
				_colorChooserButton4 };
		_widthButtons = new LineWidthButton[] { _lineWidthButton1,
				_lineWidthButton2, _lineWidthButton3, _lineWidthButton4 };

		_typeButtons = new LineTypeButton[] { _type1, _type2, _type3, _type4 };
		_mirrorButtons = new Button[] { _mirror1, _mirror2, _mirror3, _mirror4 };

		_usePivotsForAnchorPoints = _tool.isUsePivotsForAnchorPoints();

		updateWidgetsFromModel(_line);

		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _tool) {

			@Override
			protected void updateModel_fromUI() {
				updateModelFromWidgets(_line);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				updateModelFromWidgets(_line);
				_line.fillProfile(profile);
				profile.putBoolean(
						PolylineTool.KEY_USE_PIVOTS_FOR_ANCHOR_POINTS,
						_usePivotsForAnchorPoints);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				Line line = new Line();
				line.updateFromProfile(profile);
				_usePivotsForAnchorPoints = profile.getBoolean(
						PolylineTool.KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);
				updateWidgetsFromModel(line);

			}

			@Override
			public void updateUI_fromToolSettings() {
				_usePivotsForAnchorPoints = _tool.isUsePivotsForAnchorPoints();
				updateWidgetsFromModel(_line);
			}
		};
	}

	protected void updateModelFromWidgets(Line line) {
		line.types = new ArrayList<>();

		for (int i = 0; i < _checkButtons.length; i++) {
			Button btn = _checkButtons[i];
			if (btn.getSelection()) {
				line.types.add(EquationType.values()[i]);
			}
		}

		for (EquationType type : EquationType.values()) {
			int i = type.ordinal();
			RGB c = _colorsButtons[i].getColor();
			line.colorsMap.put(type, ColorChooserButton.rgbToColor(c));
			line.widthMap.put(type,
					Integer.valueOf(_widthButtons[i].getLineWidth()));
			line.lineTypeMap.put(type,
					Integer.valueOf(_typeButtons[i].getLineType()));
			line.mirrorMap.put(type,
					Boolean.valueOf(_mirrorButtons[i].getSelection()));
		}
		try {
			_npoints = Integer.parseInt(_npointsText.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		_usePivotsForAnchorPoints = _btnUsePivots.getSelection();
	}

	void updateWidgetsFromModel(Line line) {
		for (int i = 0; i < _colorsButtons.length; i++) {
			EquationType t = EquationType.values()[i];
			_checkButtons[i].setSelection(line.types.contains(t));
			_colorsButtons[i].setColor(ColorChooserButton
					.colorToSWTColor(line.colorsMap.get(t)));
			_widthButtons[i].setLineWidth(line.widthMap.get(t).intValue());
			_typeButtons[i].setLineType(line.lineTypeMap.get(t).intValue());
			_mirrorButtons[i]
					.setSelection(line.mirrorMap.get(t).booleanValue());
		}
		_npointsText.setText(Integer.toString(_npoints));
		_btnUsePivots.setSelection(_usePivotsForAnchorPoints);
		_btnUsePrices.setSelection(!_usePivotsForAnchorPoints);
	}

	public int getNumberOfPoints() {
		return _npoints;
	}

	public Line getLine() {
		return _line;
	}

	public Profile getSelectedProfile() {
		return _adapter.getSelectedProfile();
	}

	public void applyChanges() {
		updateModelFromWidgets(_line);
		if (_originalLine == null) {
			_tool.getDefaultLine().updateFromLine(_line);
		} else {
			_originalLine.updateFromLine(_line);
			Profile defProfile = _tool.getProfilesManager().getDefault(
					_tool.getProfileKeySet());
			_tool.getDefaultLine().updateFromProfile(defProfile);
		}
		PolylineTool.setPlotingNumberOfPoints(getNumberOfPoints());
		PolylineTool.writeGlobalPrefs();
		_tool.setProfile(getSelectedProfile());
		_tool.setUsePivotsForAnchorPoints(_usePivotsForAnchorPoints);

	}

	public boolean isUsePivotsForAnchorPoints() {
		return _usePivotsForAnchorPoints;
	}

}
