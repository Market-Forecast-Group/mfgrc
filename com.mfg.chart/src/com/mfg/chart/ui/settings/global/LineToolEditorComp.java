package com.mfg.chart.ui.settings.global;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.interactive.LineTool;
import com.mfg.chart.ui.interactive.LineTool.Line;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.LineTypeButton;
import com.mfg.chart.ui.settings.LineWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class LineToolEditorComp extends Composite {
	public static final int DELETE_ID = IDialogConstants.CLIENT_ID + 1;
	public static final int SAVE_ID = IDialogConstants.CLIENT_ID + 2;
	Line _line;
	private ColorChooserButton _colorChooserButton;
	private Button _btnRealTime;
	private Label _label;
	private Composite _composite;
	private ChartProfilesComposite _chartProfilesComposite;
	private Composite _composite_1;
	private LineTool _tool;
	private GUIProfileAdapter _adapter;
	private Label _label_1;
	private LineWidthButton _lineWidthButton;
	private Label _label_2;
	private LineTypeButton _lineTypeButton;
	private Composite _composite_2;
	private Button _btnUsePivots;
	private Button _btnUsePrices;
	private Line _originalLine;
	private Button _mirrorImage;

	public LineToolEditorComp(Composite parent, Chart chart, Object context) {
		super(parent, SWT.NONE);
		_tool = chart.getTool(LineTool.class);
		if (context != null && context instanceof Line) {
			Line line = (Line) context;
			_originalLine = line;
			_line = line.clone();
		} else {
			_line = _tool.getDefaultLine().clone();
		}

		createContent();
	}

	public LineToolEditorComp(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	@SuppressWarnings("unused")
	private void createContent() {
		this.setLayout(new GridLayout(1, false));

		_composite = new Composite(this, SWT.BORDER);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		_composite.setLayout(new GridLayout(2, false));

		_btnRealTime = new Button(_composite, SWT.CHECK);
		_btnRealTime.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		_btnRealTime.setText("Real Time");

		_mirrorImage = new Button(_composite, SWT.CHECK);
		_mirrorImage.setText("Mirror Image");
		new Label(_composite, SWT.NONE);

		_label = new Label(_composite, SWT.NONE);
		_label.setText("Line Color");

		_colorChooserButton = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_colorChooserButton = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_colorChooserButton.heightHint = 30;
		gd_colorChooserButton.widthHint = 134;
		_colorChooserButton.setLayoutData(gd_colorChooserButton);

		_label_1 = new Label(_composite, SWT.NONE);
		_label_1.setText("Line Thickness");

		_lineWidthButton = new LineWidthButton(_composite, SWT.NONE);
		_lineWidthButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		_label_2 = new Label(_composite, SWT.NONE);
		_label_2.setText("Line Type");

		_lineTypeButton = new LineTypeButton(_composite, SWT.NONE);
		GridData gd_lineTypeButton = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_lineTypeButton.heightHint = 30;
		_lineTypeButton.setLayoutData(gd_lineTypeButton);

		_composite_2 = new Composite(this, SWT.BORDER);
		_composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		_composite_2.setLayout(new GridLayout(1, false));

		_btnUsePivots = new Button(_composite_2, SWT.RADIO);
		_btnUsePivots.setText("Use pivots for anchors points");

		_btnUsePrices = new Button(_composite_2, SWT.RADIO);
		_btnUsePrices.setText("Use prices for anchors points");

		_composite_1 = new Composite(this, SWT.BORDER);
		_composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		_composite_1.setLayout(gl_composite_1);

		_chartProfilesComposite = new ChartProfilesComposite(_composite_1,
				SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		_colorChooserButton.setColor(ColorChooserButton
				.colorToSWTColor(_line.color));
		_btnRealTime.setSelection(_line.realtime);

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
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				Line line = new Line(null, null);
				line.updateFromProfile(profile);
				updateWidgetsFromModel(line);

			}

			@Override
			public void updateUI_fromToolSettings() {
				updateWidgetsFromModel(_line);
			}
		};
	}

	protected void updateWidgetsFromModel(Line line) {
		_colorChooserButton.setColor(ColorChooserButton
				.colorToSWTColor(line.color));
		_btnRealTime.setSelection(line.realtime);
		_lineWidthButton.setLineWidth(line.lineWidth);
		_lineTypeButton.setLineType(line.lineType);
		_btnUsePivots.setSelection(line.usePivotsForAnchorPoints);
		_btnUsePrices.setSelection(!line.usePivotsForAnchorPoints);
		_mirrorImage.setSelection(line.mirror);
	}

	protected void updateModelFromWidgets(Line line) {
		line.color = ColorChooserButton.rgbToColor(_colorChooserButton
				.getColor());
		line.realtime = _btnRealTime.getSelection();
		line.lineWidth = _lineWidthButton.getLineWidth();
		line.lineType = _lineTypeButton.getLineType();
		line.usePivotsForAnchorPoints = _btnUsePivots.getSelection();
		line.mirror = _mirrorImage.getSelection();
	}

	public void setLine(LineTool tool, Line line) {
		_line = line.clone();
		_tool = tool;
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
			_tool.getDefaultLine().update(_line);
		} else {
			_originalLine.update(_line);
			Profile p = _tool.getProfilesManager().getDefault(
					_tool.getProfileKeySet());
			_tool.getDefaultLine().updateFromProfile(p);
			_tool.getDefaultLine().usePivotsForAnchorPoints = _line.usePivotsForAnchorPoints;
		}
		_tool.setProfile(getSelectedProfile());
	}
}
