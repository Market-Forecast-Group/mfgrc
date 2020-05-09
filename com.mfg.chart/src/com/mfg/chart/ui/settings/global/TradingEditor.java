package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.TradingLayer.TradingSettings;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;
import com.mfg.chart.ui.settings.ShapeButton;
import com.mfg.chart.ui.settings.ShapeWidthButton;
import com.mfg.utils.ui.ColorChooserButton;

public class TradingEditor extends Composite implements IChartSettingsEditor {

	Chart _chart;
	private Button _btnShowClosedPositions;
	private ChartProfilesComposite _chartProfilesComposite;
	private GUIProfileAdapter _adapter;
	private Composite _group_1;
	private Label _label;
	private Label _label_1;
	private ColorChooserButton _pendingLongColor;
	private ShapeButton _pendingLongShape;
	private ShapeWidthButton _pendingLongSize;
	private ColorChooserButton _pendingShortColor;
	private ShapeButton _pendingShortShape;
	private ShapeWidthButton _pendingShortSize;
	private Label _label_2;
	private Label _label_3;
	private Label _label_4;
	private Label _label_5;
	private Label _label_6;
	private Label _label_7;
	private Label _label_8;
	private Label _label_9;
	private Label _label_10;
	private Label _label_11;
	private Label _label_12;
	private Label _label_13;
	private ColorChooserButton _openGainLongColor;
	private ColorChooserButton _openLoseLongColor;
	private ColorChooserButton _closeGainLongColor;
	private ColorChooserButton _closeLoseLongColor;
	private ShapeButton _openGainLongShape;
	private ShapeButton _openLoseLongShape;
	private ShapeButton _closeGainLongShape;
	private ShapeButton _closeLoseLongShape;
	private ShapeWidthButton _openGainLongSize;
	private ShapeWidthButton _openLoseLongSize;
	private ShapeWidthButton _closeGainLongSize;
	private ShapeWidthButton _closeLoseLongSize;
	private ColorChooserButton _openGainShortColor;
	private ColorChooserButton _openLoseShortColor;
	private ColorChooserButton _closeGainShortColor;
	private ColorChooserButton _closeLoseShortColor;
	private ShapeButton _openGainShortShape;
	private ShapeButton _openLoseShortShape;
	private ShapeButton _closeGainShortShape;
	private ShapeWidthButton _openGainShortSize;
	private ShapeWidthButton _openLoseShortSize;
	private ShapeWidthButton _closeGainShortSize;
	private ShapeWidthButton _closeLoseShortSize;
	private ShapeButton _closeLoseShortShape;

	public TradingEditor(Chart chart, Composite parent) {
		this(parent, SWT.NONE);
		_chart = chart;
		_adapter = new GUIProfileAdapter(_chartProfilesComposite, _chart
				.getTradingLayer().getProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				updateUI_fromSettings(_chart.getTradingLayer().getSettings());
			}

			@Override
			protected void updateUI_fromProfile(Profile p) {
				TradingSettings s = new TradingSettings();
				s.updateFromProfile(p);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile p) {
				TradingSettings s = new TradingSettings();
				updateSettings_fromUI(s);
				s.fillProfile(p);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};
		_adapter.updateUI_fromToolSettings();
	}

	protected void updateUI_fromSettings(TradingSettings s) {
		_btnShowClosedPositions.setSelection(s.showClosedPosition);

		_pendingLongColor.setColor(s.long_Pending_color);
		_pendingLongShape.setShapeType(s.long_Pending_shape);
		_pendingLongSize.setShapeWidth(s.long_Pending_shape_width);

		_pendingShortColor.setColor(s.short_Pending_color);
		_pendingShortShape.setShapeType(s.short_Pending_shape);
		_pendingShortSize.setShapeWidth(s.short_Pending_shape_width);
		
		
		_openGainLongColor.setColor(s.long_Open_Gain_color);
		_openGainLongShape.setShapeType(s.long_Open_Gain_shape);
		_openGainLongSize.setShapeWidth(s.long_Open_Gain_shapeWidth);

		_openGainShortColor.setColor(s.short_Open_Gain_color);
		_openGainShortShape.setShapeType(s.short_Open_Gain_shape);
		_openGainShortSize.setShapeWidth(s.short_Open_Gain_shapeWidth);
		
		_openLoseLongColor.setColor(s.long_Open_Lose_color);
		_openLoseLongShape.setShapeType(s.long_Open_Lose_shape);
		_openLoseLongSize.setShapeWidth(s.long_Open_Lose_shapeWidth);

		_openLoseShortColor.setColor(s.short_Open_Lose_color);
		_openLoseShortShape.setShapeType(s.short_Open_Lose_shape);
		_openLoseShortSize.setShapeWidth(s.short_Open_Lose_shapeWidth);
		
		
		_closeGainLongColor.setColor(s.long_Close_Gain_color);
		_closeGainLongShape.setShapeType(s.long_Close_Gain_shape);
		_closeGainLongSize.setShapeWidth(s.long_Close_Gain_shapeWidth);

		_closeGainShortColor.setColor(s.short_Close_Gain_color);
		_closeGainShortShape.setShapeType(s.short_Close_Gain_shape);
		_closeGainShortSize.setShapeWidth(s.short_Close_Gain_shapeWidth);
		
		_closeLoseLongColor.setColor(s.long_Close_Lose_color);
		_closeLoseLongShape.setShapeType(s.long_Close_Lose_shape);
		_closeLoseLongSize.setShapeWidth(s.long_Close_Lose_shapeWidth);

		_closeLoseShortColor.setColor(s.short_Close_Lose_color);
		_closeLoseShortShape.setShapeType(s.short_Close_Lose_shape);
		_closeLoseShortSize.setShapeWidth(s.short_Close_Lose_shapeWidth);
	}

	protected void updateSettings_fromUI(TradingSettings s) {
		s.showClosedPosition = _btnShowClosedPositions.getSelection();
		s.long_Pending_color = _pendingLongColor.getGLColor();
		s.long_Pending_shape = _pendingLongShape.getShapeType();
		s.long_Pending_shape_width = _pendingLongSize.getShapeWidth();

		s.long_Open_Gain_color = _openGainLongColor.getGLColor();
		s.long_Open_Gain_shape = _openGainLongShape.getShapeType();
		s.long_Open_Gain_shapeWidth = _openGainLongSize.getShapeWidth();

		s.long_Open_Lose_color = _openLoseLongColor.getGLColor();
		s.long_Open_Lose_shape = _openLoseLongShape.getShapeType();
		s.long_Open_Lose_shapeWidth = _openLoseLongSize.getShapeWidth();

		s.long_Close_Gain_color = _closeGainLongColor.getGLColor();
		s.long_Close_Gain_shape = _closeGainLongShape.getShapeType();
		s.long_Close_Gain_shapeWidth = _closeGainLongSize.getShapeWidth();

		s.long_Close_Lose_color = _closeLoseLongColor.getGLColor();
		s.long_Close_Lose_shape = _closeLoseLongShape.getShapeType();
		s.long_Close_Lose_shapeWidth = _closeLoseLongSize.getShapeWidth();

		s.short_Pending_color = _pendingShortColor.getGLColor();
		s.short_Pending_shape = _pendingShortShape.getShapeType();
		s.short_Pending_shape_width = _pendingShortSize.getShapeWidth();

		s.short_Open_Gain_color = _openGainShortColor.getGLColor();
		s.short_Open_Gain_shape = _openGainShortShape.getShapeType();
		s.short_Open_Gain_shapeWidth = _openGainShortSize.getShapeWidth();

		s.short_Open_Lose_color = _openLoseShortColor.getGLColor();
		s.short_Open_Lose_shape = _openLoseShortShape.getShapeType();
		s.short_Open_Lose_shapeWidth = _openLoseShortSize.getShapeWidth();

		s.short_Close_Gain_color = _closeGainShortColor.getGLColor();
		s.short_Close_Gain_shape = _closeGainShortShape.getShapeType();
		s.short_Close_Gain_shapeWidth = _closeGainShortSize.getShapeWidth();

		s.short_Close_Lose_color = _closeLoseShortColor.getGLColor();
		s.short_Close_Lose_shape = _closeLoseShortShape.getShapeType();
		s.short_Close_Lose_shapeWidth = _closeLoseShortSize.getShapeWidth();

	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public TradingEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		_btnShowClosedPositions = new Button(group, SWT.CHECK);
		_btnShowClosedPositions.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, true, false, 1, 1));
		_btnShowClosedPositions.setText("Show Closed Positions");

		_group_1 = new Composite(this, SWT.BORDER);
		_group_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_group_1.setLayout(new GridLayout(8, false));
		_group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		new Label(_group_1, SWT.NONE);

		_label = new Label(_group_1, SWT.NONE);
		_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		_label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				3, 1));
		_label.setText("Long");

		_label_8 = new Label(_group_1, SWT.NONE);
		_label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_label_8 = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_label_8.widthHint = 10;
		_label_8.setLayoutData(gd_label_8);

		_label_1 = new Label(_group_1, SWT.NONE);
		_label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		_label_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 3, 1));
		_label_1.setText("Short");
		new Label(_group_1, SWT.NONE);

		_label_2 = new Label(_group_1, SWT.NONE);
		_label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_2.setText("Color");

		_label_3 = new Label(_group_1, SWT.NONE);
		_label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_3.setText("Shape");

		_label_4 = new Label(_group_1, SWT.NONE);
		_label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_4.setText("Size");
		new Label(_group_1, SWT.NONE);

		_label_5 = new Label(_group_1, SWT.NONE);
		_label_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_5.setText("Color");

		_label_6 = new Label(_group_1, SWT.NONE);
		_label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_6.setText("Shape");

		_label_7 = new Label(_group_1, SWT.NONE);
		_label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_7.setText("Size");

		_label_9 = new Label(_group_1, SWT.NONE);
		_label_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_9.setText("Pending Order");

		_pendingLongColor = new ColorChooserButton(_group_1, SWT.NONE);
		_pendingLongColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pensingLongColor = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_pensingLongColor.heightHint = 30;
		gd_pensingLongColor.widthHint = 50;
		_pendingLongColor.setLayoutData(gd_pensingLongColor);

		_pendingLongShape = new ShapeButton(_group_1, SWT.NONE);
		_pendingLongShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pendingLongShape = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_pendingLongShape.widthHint = 50;
		_pendingLongShape.setLayoutData(gd_pendingLongShape);

		_pendingLongSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_pendingLongSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pendingLongSize = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_pendingLongSize.widthHint = 50;
		_pendingLongSize.setLayoutData(gd_pendingLongSize);
		new Label(_group_1, SWT.NONE);

		_pendingShortColor = new ColorChooserButton(_group_1, SWT.NONE);
		_pendingShortColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pendingShortColor = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_pendingShortColor.widthHint = 50;
		_pendingShortColor.setLayoutData(gd_pendingShortColor);

		_pendingShortShape = new ShapeButton(_group_1, SWT.NONE);
		_pendingShortShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pendingShortShape = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_pendingShortShape.widthHint = 50;
		_pendingShortShape.setLayoutData(gd_pendingShortShape);

		_pendingShortSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_pendingShortSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_pendingShortSize = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_pendingShortSize.widthHint = 50;
		_pendingShortSize.setLayoutData(gd_pendingShortSize);

		_label_10 = new Label(_group_1, SWT.NONE);
		_label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_10.setText("Open Gain");

		_openGainLongColor = new ColorChooserButton(_group_1, SWT.NONE);
		_openGainLongColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_openGainLongColor = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_openGainLongColor.heightHint = 30;
		_openGainLongColor.setLayoutData(gd_openGainLongColor);

		_openGainLongShape = new ShapeButton(_group_1, SWT.NONE);
		_openGainLongShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openGainLongShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openGainLongSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_openGainLongSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openGainLongSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(_group_1, SWT.NONE);

		_openGainShortColor = new ColorChooserButton(_group_1, SWT.NONE);
		_openGainShortColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openGainShortColor.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openGainShortShape = new ShapeButton(_group_1, SWT.NONE);
		_openGainShortShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openGainShortShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openGainShortSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_openGainShortSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openGainShortSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_label_11 = new Label(_group_1, SWT.NONE);
		_label_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_11.setText("Open Lose");

		_openLoseLongColor = new ColorChooserButton(_group_1, SWT.NONE);
		_openLoseLongColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_openLoseLongColor = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_openLoseLongColor.heightHint = 30;
		_openLoseLongColor.setLayoutData(gd_openLoseLongColor);

		_openLoseLongShape = new ShapeButton(_group_1, SWT.NONE);
		_openLoseLongShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openLoseLongShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openLoseLongSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_openLoseLongSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openLoseLongSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(_group_1, SWT.NONE);

		_openLoseShortColor = new ColorChooserButton(_group_1, SWT.NONE);
		_openLoseShortColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openLoseShortColor.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openLoseShortShape = new ShapeButton(_group_1, SWT.NONE);
		_openLoseShortShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openLoseShortShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_openLoseShortSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_openLoseShortSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_openLoseShortSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_label_12 = new Label(_group_1, SWT.NONE);
		_label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_12.setText("Close Gain");

		_closeGainLongColor = new ColorChooserButton(_group_1, SWT.NONE);
		_closeGainLongColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_closeGainLongColor = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_closeGainLongColor.heightHint = 30;
		_closeGainLongColor.setLayoutData(gd_closeGainLongColor);

		_closeGainLongShape = new ShapeButton(_group_1, SWT.NONE);
		_closeGainLongShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeGainLongShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeGainLongSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_closeGainLongSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeGainLongSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(_group_1, SWT.NONE);

		_closeGainShortColor = new ColorChooserButton(_group_1, SWT.NONE);
		_closeGainShortColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeGainShortColor.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeGainShortShape = new ShapeButton(_group_1, SWT.NONE);
		_closeGainShortShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeGainShortShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeGainShortSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_closeGainShortSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeGainShortSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_label_13 = new Label(_group_1, SWT.NONE);
		_label_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		_label_13.setText("Close Lose");

		_closeLoseLongColor = new ColorChooserButton(_group_1, SWT.NONE);
		_closeLoseLongColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridData gd_closeLoseLongColor = new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1);
		gd_closeLoseLongColor.heightHint = 30;
		_closeLoseLongColor.setLayoutData(gd_closeLoseLongColor);

		_closeLoseLongShape = new ShapeButton(_group_1, SWT.NONE);
		_closeLoseLongShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeLoseLongShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeLoseLongSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_closeLoseLongSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeLoseLongSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		new Label(_group_1, SWT.NONE);

		_closeLoseShortColor = new ColorChooserButton(_group_1, SWT.NONE);
		_closeLoseShortColor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeLoseShortColor.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeLoseShortShape = new ShapeButton(_group_1, SWT.NONE);
		_closeLoseShortShape.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeLoseShortShape.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		_closeLoseShortSize = new ShapeWidthButton(_group_1, SWT.NONE);
		_closeLoseShortSize.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		_closeLoseShortSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));

		Group grpProfiles = new Group(this, SWT.NONE);
		grpProfiles.setLayout(new GridLayout(1, false));
		grpProfiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		grpProfiles.setText("Profiles");

		_chartProfilesComposite = new ChartProfilesComposite(grpProfiles,
				SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, false, false, 1, 1));

	}

	@Override
	public void applyChanges() {
		TradingSettings s = new TradingSettings();
		updateSettings_fromUI(s);
		_chart.getTradingLayer().setSettings(s);
		_chart.getTradingLayer().getProfiledObject()
				.setProfile(_adapter.getSelectedProfile());
	}

	@Override
	public Composite getUI() {
		return this;
	}
}
