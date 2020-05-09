/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.widget.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.CenterLineAlgo;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.StartPointLength;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.TopBottomMaxDist;
import com.mfg.utils.DataBindingUtils;
import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * @author arian
 * 
 */
@SuppressWarnings("unused")
public class IndicatorSettingsComposite extends Composite {
	private Binding _topBottomMaxDistanceBinding;
	private Binding positiveOnSRRC_Touch_Binding;

	private final DataBindingContext m_bindingContext;
	private final IndicatorSettingsComposite self = this;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private IndicatorParamBean indicatorSettings;

	public static class MaxWindowLengthValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			int num = ((Integer) value).intValue();
			return num >= 10000 && num <= 10000000 ? Status.OK_STATUS
					: ValidationStatus
							.error("Invalid number range (10 000 ... 10 000 000).");
		}

	}

	public static class TopBottomMaxDist_IsNotHull extends Converter {

		/**
		 * @param fromType
		 * @param toType
		 */
		public TopBottomMaxDist_IsNotHull() {
			super(TopBottomMaxDist.class, Boolean.class);
		}

		@Override
		public Object convert(Object fromObject) {
			return new Boolean(fromObject != TopBottomMaxDist.CONVEX_HULL
					&& fromObject != TopBottomMaxDist.HALF_CONVEX_HULL);
		}

	}

	public static class StartPointLengthIsSPA extends Converter {

		/**
		 * @param fromType
		 * @param toType
		 */
		public StartPointLengthIsSPA() {
			super(StartPointLength.class, Boolean.class);
		}

		@Override
		public Object convert(Object fromObject) {
			return new Boolean(fromObject == StartPointLength.SPA);
		}

	}

	public class ThPercentForTopBottomDistanceValidator implements IValidator {
		public ThPercentForTopBottomDistanceValidator() {
		}

		@Override
		public IStatus validate(Object value) {
			if (getIndicatorSettings().getIndicator_TopBottomMaxDist() == TopBottomMaxDist.CONVEX_HULL_FIXED_TICK) {
				double v = ((Double) value).doubleValue();
				if (v > 0.8) {
					return ValidationStatus
							.error("Invalid value for TH % for T Min. Distance, max value allowed is 0.8");
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("static-access")
	public IndicatorSettingsComposite(Composite parent, int style) {
		super(parent, SWT.NONE);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_1 = new Composite(this, SWT.NONE);
		toolkit.adapt(composite_1);
		toolkit.paintBordersFor(composite_1);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		composite_1.setLayout(gl_composite_1);

		sctnMain = toolkit.createSection(composite_1, Section.TITLE_BAR);
		sctnMain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		sctnMain.setSize(343, 116);
		toolkit.paintBordersFor(sctnMain);
		sctnMain.setText("Main");
		sctnMain.setExpanded(true);

		composite_2 = toolkit.createComposite(composite_1, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginWidth = 0;
		gl_composite_2.marginHeight = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 6));
		toolkit.paintBordersFor(composite_2);

		Section sctnIndicator = toolkit.createSection(composite_2,
				Section.TITLE_BAR);
		sctnIndicator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		toolkit.paintBordersFor(sctnIndicator);
		sctnIndicator.setText("Indicator");

		Composite composite_3 = toolkit.createComposite(composite_2, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		toolkit.paintBordersFor(composite_3);
		GridLayout gl_composite_3 = new GridLayout(2, false);
		gl_composite_3.horizontalSpacing = 8;
		composite_3.setLayout(gl_composite_3);

		Label lblCentralLineAlgo = toolkit.createLabel(composite_3,
				"Central Line Algo.", SWT.NONE);

		comboViewerCentralLineAlgo = new ComboViewer(composite_3, SWT.READ_ONLY);
		Combo combo = comboViewerCentralLineAlgo.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.paintBordersFor(combo);
		comboViewerCentralLineAlgo.setLabelProvider(new LabelProvider());
		comboViewerCentralLineAlgo
				.setContentProvider(new ArrayContentProvider());

		_button_4 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(_button_4, true, true);
		_button_4.setText("Warm-up Polylines LS Pivots");

		_spinner_3 = new Spinner(composite_3, SWT.BORDER);
		_spinner_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(_spinner_3);
		toolkit.paintBordersFor(_spinner_3);

		Label lblStartPointLength = toolkit.createLabel(composite_3,
				"Start Point-Length", SWT.NONE);

		comboViewerStartPointLength = new ComboViewer(composite_3,
				SWT.READ_ONLY);
		Combo combo_1 = comboViewerStartPointLength.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_1);
		comboViewerStartPointLength.setLabelProvider(new LabelProvider());
		comboViewerStartPointLength
				.setContentProvider(new ArrayContentProvider());

		Label lblSpaType = toolkit.createLabel(composite_3, "SPA Type",
				SWT.NONE);

		comboViewerSPAType = new ComboViewer(composite_3, SWT.READ_ONLY);
		combo_2 = comboViewerSPAType.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_2);
		comboViewerSPAType.setLabelProvider(new LabelProvider());
		comboViewerSPAType.setContentProvider(new ArrayContentProvider());

		lblTopbottomMaxDist = toolkit.createLabel(composite_3,
				"TopBottom Max. Distance", SWT.NONE);

		comboViewerTopBottomMaxDist = new ComboViewer(composite_3,
				SWT.READ_ONLY);
		combo_3 = comboViewerTopBottomMaxDist.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_3);
		comboViewerTopBottomMaxDist.setLabelProvider(new LabelProvider());
		comboViewerTopBottomMaxDist
				.setContentProvider(new ArrayContentProvider());

		_button_3 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(_button_3, true, true);
		_button_3.setText("TH % for TB Min. Distance");

		_spinner_1 = new Spinner(composite_3, SWT.BORDER);
		_spinner_1.setDigits(1);
		_spinner_1.setMaximum(30);
		_spinner_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(_spinner_1);
		toolkit.paintBordersFor(_spinner_1);

		_button_5 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(_button_5, true, true);
		_button_5.setText(" No TB Min.from scale up");

		_spinner_2 = new Spinner(composite_3, SWT.BORDER);
		_spinner_2.setMinimum(1);
		_spinner_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(_spinner_2);
		toolkit.paintBordersFor(_spinner_2);

		btnUsePreviousRegresion = new Button(composite_3, SWT.CHECK);
		btnUsePreviousRegresion.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		toolkit.adapt(btnUsePreviousRegresion, true, true);
		btnUsePreviousRegresion.setText("Use Previous SC Value");

		_button_1 = new Button(composite_3, SWT.CHECK);
		_button_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(_button_1, true, true);
		_button_1.setText("No Indicator in Warm-Up");

		sctnSpaRules = toolkit.createSection(composite_2, Section.TITLE_BAR);
		sctnSpaRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		toolkit.paintBordersFor(sctnSpaRules);
		sctnSpaRules.setText("SPA Rules");

		composite_4 = toolkit.createComposite(composite_2, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.paintBordersFor(composite_4);

		btnPivotAt = new Button(composite_4, SWT.CHECK);
		btnPivotAt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		toolkit.adapt(btnPivotAt, true, true);
		btnPivotAt.setText("1 Pivot at Same Scale");

		btnPivotsAt = new Button(composite_4, SWT.CHECK);
		btnPivotsAt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(btnPivotsAt, true, true);
		btnPivotsAt.setText("3 Pivots at 1 LS ");

		btnPivotsAt_1 = new Button(composite_4, SWT.CHECK);
		toolkit.adapt(btnPivotsAt_1, true, true);
		btnPivotsAt_1.setText("5 Pivots at 2 LS ");
		new Label(composite_4, SWT.NONE);

		btnPivotsAt_2 = new Button(composite_4, SWT.CHECK);
		btnPivotsAt_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(btnPivotsAt_2, true, true);
		btnPivotsAt_2.setText("13 Pivots at 3LS ");

		sctnSmo = toolkit.createSection(composite_2, Section.TITLE_BAR);
		sctnSmo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		toolkit.paintBordersFor(sctnSmo);
		sctnSmo.setText("Smoothing");

		composite_6 = toolkit.createComposite(composite_2, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.paintBordersFor(composite_6);
		composite_6.setLayout(new GridLayout(2, false));

		btnSmoothing = new Button(composite_6, SWT.CHECK);
		btnSmoothing.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(btnSmoothing, true, true);
		btnSmoothing.setText("Smoothing");

		lblCoverageBoost = toolkit.createLabel(composite_6, "Converge Boost",
				SWT.NONE);

		spinner_11 = new Spinner(composite_6, SWT.BORDER);
		spinner_11.setDigits(1);
		spinner_11.setMaximum(30);
		spinner_11.setMinimum(1);

		btnNarrowingBoosting = new Button(composite_6, SWT.CHECK);
		btnNarrowingBoosting.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnNarrowingBoosting, true, true);
		btnNarrowingBoosting.setText("Narrowing Boosting");

		composite_7 = toolkit.createComposite(composite_1, SWT.NONE);
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.paintBordersFor(composite_7);
		composite_7.setLayout(new GridLayout(2, false));

		Label lblNumberOfScales = toolkit.createLabel(composite_7,
				"Number of Scales", SWT.NONE);

		spinner_9 = new Spinner(composite_7, SWT.BORDER);
		spinner_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_9);
		toolkit.paintBordersFor(spinner_9);

		btnPrintMessages = new Button(composite_7, SWT.CHECK);
		btnPrintMessages.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnPrintMessages, true, true);
		btnPrintMessages.setText("Print Messages for Testing");

		Section sctnNewChannelsRules = toolkit.createSection(composite_1,
				Section.TITLE_BAR);
		sctnNewChannelsRules.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		sctnNewChannelsRules.setSize(352, 116);
		toolkit.paintBordersFor(sctnNewChannelsRules);
		sctnNewChannelsRules.setText("New Swing Rules");
		sctnNewChannelsRules.setExpanded(true);

		Composite composite_8 = toolkit.createComposite(composite_1, SWT.NONE);
		composite_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.paintBordersFor(composite_8);
		composite_8.setLayout(new GridLayout(3, false));

		btnCheckButton = new Button(composite_8, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));
		toolkit.adapt(btnCheckButton, true, true);
		btnCheckButton.setText("Negative on Price Multiplier");

		label = toolkit.createLabel(composite_8, "", SWT.NONE);

		lblStartTicksNumbers = toolkit.createLabel(composite_8,
				"Start Ticks Numbers", SWT.NONE);

		spinner_3 = new Spinner(composite_8, SWT.BORDER);
		spinner_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_3);
		toolkit.paintBordersFor(spinner_3);

		label_1 = toolkit.createLabel(composite_8, "", SWT.NONE);

		lblPriceMultiplier = new Label(composite_8, SWT.NONE);
		lblPriceMultiplier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		toolkit.adapt(lblPriceMultiplier, true, true);
		lblPriceMultiplier.setText("Price Multiplier");

		spinner_2 = new Spinner(composite_8, SWT.BORDER);
		spinner_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_2);
		toolkit.paintBordersFor(spinner_2);

		btnNegativeOnPivot = new Button(composite_8, SWT.CHECK);
		btnNegativeOnPivot.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnNegativeOnPivot, true, true);
		btnNegativeOnPivot.setText("Negative on Pivot Breakout");

		btnNegativeOnFlat = new Button(composite_8, SWT.CHECK);
		btnNegativeOnFlat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnNegativeOnFlat, true, true);
		btnNegativeOnFlat.setText("Negative on Flat Channel");

		btnNegativeOnSc = new Label(composite_8, SWT.CHECK);
		btnNegativeOnSc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		toolkit.adapt(btnNegativeOnSc, true, true);
		btnNegativeOnSc.setText("Negative on SC Touch");

		label_2 = toolkit.createLabel(composite_8, "      ", SWT.NONE);

		lblStartScale_1 = toolkit.createLabel(composite_8, "Start Scale",
				SWT.NONE);
		lblStartScale_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		spinner = new Spinner(composite_8, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		toolkit.adapt(spinner);
		toolkit.paintBordersFor(spinner);
		new Label(composite_8, SWT.NONE);

		_button_2 = new Button(composite_8, SWT.CHECK);
		toolkit.adapt(_button_2, true, true);
		_button_2.setText("S0/S0' Price Ratio");

		spinner_1 = new Spinner(composite_8, SWT.BORDER);
		spinner_1.setDigits(1);
		spinner_1.setMaximum(90);
		spinner_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_1);
		toolkit.paintBordersFor(spinner_1);
		new Label(composite_8, SWT.NONE);

		_button = new Button(composite_8, SWT.CHECK);
		toolkit.adapt(_button, true, true);
		_button.setText("S0/S0' Time Ratio");

		_spinner = new Spinner(composite_8, SWT.BORDER);
		_spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_spinner.setMaximum(90);
		_spinner.setDigits(1);
		toolkit.adapt(_spinner);
		toolkit.paintBordersFor(_spinner);
		new Label(composite_8, SWT.NONE);

		_button_6 = new Button(composite_8, SWT.CHECK);
		_button_6.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(_button_6, true, true);
		_button_6.setText("Self-pivot breakout");
		new Label(composite_8, SWT.NONE);

		_button_7 = new Button(composite_8, SWT.CHECK);
		toolkit.adapt(_button_7, true, true);
		_button_7.setText("TH%");

		_text = toolkit.createText(composite_8, "New Text", SWT.NONE);
		_text.setText("");
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		_label = toolkit
				.createLabel(composite_8, "Positive Channels", SWT.NONE);
		_label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				3, 1));
		new Label(composite_8, SWT.NONE);

		Label lblStartScale = toolkit.createLabel(composite_8, "Start Scale",
				SWT.NONE);
		lblStartScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		spinner_10 = new Spinner(composite_8, SWT.BORDER);
		spinner_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_10);
		toolkit.paintBordersFor(spinner_10);
		new Label(composite_8, SWT.NONE);

		btnPositiveOnScrc = new Button(composite_8, SWT.CHECK);
		toolkit.adapt(btnPositiveOnScrc, true, true);
		btnPositiveOnScrc.setText("SC/RC Touch");
		new Label(composite_8, SWT.NONE);
		new Label(composite_8, SWT.NONE);

		_button_8 = new Button(composite_8, SWT.CHECK);
		toolkit.adapt(_button_8, true, true);
		_button_8.setText("Best Channel Fitting");

		_spinner_4 = new Spinner(composite_8, SWT.BORDER);
		_spinner_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		_spinner_4.setMaximum(10);
		_spinner_4.setMinimum(1);
		_spinner_4.setDigits(1);
		toolkit.adapt(_spinner_4);
		toolkit.paintBordersFor(_spinner_4);

		sctnFixWindow = toolkit.createSection(composite_1, Section.TITLE_BAR);
		sctnFixWindow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.paintBordersFor(sctnFixWindow);
		sctnFixWindow.setText("Fix Window");

		composite_5 = toolkit.createComposite(composite_1, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1));
		toolkit.paintBordersFor(composite_5);
		GridLayout gl_composite_5 = new GridLayout(2, false);
		gl_composite_5.horizontalSpacing = 8;
		composite_5.setLayout(gl_composite_5);

		Label lblStartWindow = toolkit.createLabel(composite_5, "Start Window",
				SWT.NONE);

		spinner_4 = new Spinner(composite_5, SWT.BORDER);
		spinner_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_4);
		toolkit.paintBordersFor(spinner_4);

		Label lblWindowMultiplier = toolkit.createLabel(composite_5,
				"Window Multiplier", SWT.NONE);

		spinner_5 = new Spinner(composite_5, SWT.BORDER);
		spinner_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_5);
		toolkit.paintBordersFor(spinner_5);

		lblMaximumWindowLength = toolkit.createLabel(composite_5,
				"Maximum Window Length", SWT.NONE);
		lblMaximumWindowLength.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, false, false, 1, 1));

		text = toolkit.createText(composite_5, "New Text", SWT.NONE);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblDeltaTopBottom = toolkit.createLabel(composite_5,
				"Delta TopBottom Perc.", SWT.NONE);

		spinner_6 = new Text(composite_5, SWT.BORDER);
		spinner_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		Label lblDeltaTopbottomTicks = toolkit.createLabel(composite_5,
				"Delta TopBottom Ticks", SWT.NONE);

		spinner_7 = new Spinner(composite_5, SWT.BORDER);
		spinner_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_7);
		toolkit.paintBordersFor(spinner_7);

		lblDeltaTopbottomMultipler = toolkit.createLabel(composite_5,
				"Delta TopBottom Multipler", SWT.NONE);

		spinner_8 = new Spinner(composite_5, SWT.BORDER);
		spinner_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(spinner_8);
		toolkit.paintBordersFor(spinner_8);

		btnAvoidTouch = new Button(composite_5, SWT.CHECK);
		btnAvoidTouch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(btnAvoidTouch, true, true);
		btnAvoidTouch.setText("No Price Channel Exit");

		afterCreateWidgets();

		m_bindingContext = initDataBindings();

		afterInitBindings();

	}

	private void afterInitBindings() {
		DataBindingUtils.decorateBindings(m_bindingContext);
		_topBottomMaxDistanceBinding.getTarget().addChangeListener(
				new IChangeListener() {

					@Override
					public void handleChange(ChangeEvent event) {
						_thPercentForTopBottomDistanceBinding
								.updateModelToTarget();
					}
				});
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void afterCreateWidgets() {
		Set<Object> set = new LinkedHashSet<>();
		set.addAll(Arrays.asList(CenterLineAlgo.values()));
		set.remove(CenterLineAlgo.POLYNOMIAL_FIT);
		comboViewerCentralLineAlgo.setInput(set.toArray());
		comboViewerSPAType
				.setInput(AbstractIndicatorParamBean.SPAType.values());
		comboViewerStartPointLength
				.setInput(AbstractIndicatorParamBean.StartPointLength.values());
		comboViewerTopBottomMaxDist
				.setInput(AbstractIndicatorParamBean.TopBottomMaxDist.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.trading.ui.editors.migratingToWB.AbstractTradeComposite#
	 * getDataBindingContext()
	 */
	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	public IndicatorParamBean getIndicatorSettings() {
		return indicatorSettings;
	}

	/**
	 * @param aIndicatorSettings
	 *            the indicatorSettings to set
	 */
	public void setIndicatorSettings(IndicatorParamBean aIndicatorSettings) {
		this.indicatorSettings = aIndicatorSettings;
		firePropertyChange("indicatorSettings");
	}

	public Color getBlueColor() {
		return toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
	}

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final Section sctnMain;
	private final Composite composite_7;
	private final Spinner spinner_9;
	private final Button btnNegativeOnPivot;
	private final Button btnNegativeOnFlat;
	private final Button btnPositiveOnScrc;
	private final Spinner spinner_10;
	private final Button btnPivotsAt;
	private final Button btnPivotsAt_1;
	private final Button btnPivotsAt_2;
	private final Button btnPrintMessages;
	private DataBindingContext bindingContext;
	private final Label btnNegativeOnSc;
	private final Label lblStartScale_1;
	private final Spinner spinner;
	private final Spinner spinner_1;
	private final Button btnCheckButton;
	private final Label lblPriceMultiplier;
	private final Spinner spinner_2;
	private final Label lblStartTicksNumbers;
	private final Spinner spinner_3;
	private final Button btnNarrowingBoosting;
	private final Label label;
	private final Label label_1;
	private final Label label_2;
	private final Composite composite_2;
	private final Label lblTopbottomMaxDist;
	private final Combo combo_3;
	private final ComboViewer comboViewerTopBottomMaxDist;
	private final Section sctnSpaRules;
	private final Composite composite_4;
	private final Section sctnFixWindow;
	private final Composite composite_5;
	private final Section sctnSmo;
	private final Composite composite_6;
	private final Label lblCoverageBoost;
	private final Spinner spinner_11;
	private final Button btnUsePreviousRegresion;
	private final ComboViewer comboViewerCentralLineAlgo;
	private final ComboViewer comboViewerStartPointLength;
	private final ComboViewer comboViewerSPAType;
	private final Button btnPivotAt;
	private final Spinner spinner_4;
	private final Spinner spinner_5;
	private final Text spinner_6;
	private final Spinner spinner_7;
	private final Button btnSmoothing;
	private final Button btnAvoidTouch;
	private final Combo combo_2;
	private final Label lblMaximumWindowLength;
	private final Text text;
	private final Label lblDeltaTopbottomMultipler;
	private final Spinner spinner_8;
	private final Button _button;
	private final Spinner _spinner;
	private final Button _button_1;
	private final Button _button_3;
	private final Spinner _spinner_1;
	private final Button _button_4;
	private final Button _button_2;
	private final Button _button_5;
	private final Spinner _spinner_2;
	Binding _thPercentForTopBottomDistanceBinding;
	private final Spinner _spinner_3;
	private final Button _button_6;
	private final Button _button_7;
	private final Text _text;
	private final Button _button_8;
	private final Spinner _spinner_4;
	private final Label _label;

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext1 = new DataBindingContext();
		//
		IObservableValue spinner_9ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_9);
		IObservableValue getIndicatorSettingsIndicatorNumberOfScales_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.indicatorNumberOfScales");
		bindingContext1.bindValue(spinner_9ObserveSelectionObserveWidget,
				getIndicatorSettingsIndicatorNumberOfScales_NewObserveValue,
				null, null);
		//
		IObservableValue btnNegativeOnPivotObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNegativeOnPivot);
		IObservableValue getIndicatorSettingsNegativeOnPivotBreakOut_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.negativeOnPivotBreakOut");
		bindingContext1.bindValue(
				btnNegativeOnPivotObserveSelectionObserveWidget,
				getIndicatorSettingsNegativeOnPivotBreakOut_NewObserveValue,
				null, null);
		//
		IObservableValue btnNegativeOnFlatObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNegativeOnFlat);
		IObservableValue getIndicatorSettingsNegativeOnFlatChannel_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.negativeOnFlatChannel");
		bindingContext1.bindValue(
				btnNegativeOnFlatObserveSelectionObserveWidget,
				getIndicatorSettingsNegativeOnFlatChannel_NewObserveValue,
				null, null);
		//
		IObservableValue btnPositiveOnScrcObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPositiveOnScrc);
		IObservableValue getIndicatorSettingsPositiveOnSCRCTouch_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.positiveOnSCRCTouch");
		positiveOnSRRC_Touch_Binding = bindingContext1.bindValue(
				btnPositiveOnScrcObserveSelectionObserveWidget,
				getIndicatorSettingsPositiveOnSCRCTouch_NewObserveValue, null,
				null);
		//
		IObservableValue spinner_10ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_10);
		IObservableValue getIndicatorSettingsPositiveOnSCRCTouch_startScale_NewObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.positiveOnSCRCTouch_startScale");
		bindingContext1
				.bindValue(
						spinner_10ObserveSelectionObserveWidget,
						getIndicatorSettingsPositiveOnSCRCTouch_startScale_NewObserveValue,
						null, null);
		//
		IObservableValue btnPivotsAtObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt);
		IObservableValue getIndicatorSettings_3PivotsAt1LS_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings._3PivotsAt1LS");
		bindingContext1.bindValue(btnPivotsAtObserveSelectionObserveWidget,
				getIndicatorSettings_3PivotsAt1LS_NewObserveValue, null, null);
		//
		IObservableValue btnPivotsAt_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt_1);
		IObservableValue getIndicatorSettings_5PivotsAt2LS_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings._5PivotsAt2LS");
		bindingContext1.bindValue(btnPivotsAt_1ObserveSelectionObserveWidget,
				getIndicatorSettings_5PivotsAt2LS_NewObserveValue, null, null);
		//
		IObservableValue btnPivotsAt_2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt_2);
		IObservableValue getIndicatorSettings_13PivotsAt3LS_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings._13PivotsAt3LS");
		bindingContext1.bindValue(btnPivotsAt_2ObserveSelectionObserveWidget,
				getIndicatorSettings_13PivotsAt3LS_NewObserveValue, null, null);
		//
		IObservableValue btnPrintMessagesObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPrintMessages);
		IObservableValue getIndicatorSettingsPrintMessagesForTesting_NewObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.printMessagesForTesting");
		bindingContext1.bindValue(
				btnPrintMessagesObserveSelectionObserveWidget,
				getIndicatorSettingsPrintMessagesForTesting_NewObserveValue,
				null, null);
		//
		IObservableValue spinnerObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner);
		IObservableValue selfIndicatorSettingsnegativeOnSCTouch_startScaleObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.negativeOnSCTouch_startScale");
		bindingContext1.bindValue(spinnerObserveSelectionObserveWidget,
				selfIndicatorSettingsnegativeOnSCTouch_startScaleObserveValue,
				null, null);
		//
		IObservableValue spinner_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_1);
		IObservableValue selfIndicatorSettingspositiveOnSCRCTouch_S0RatioObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.negativeOnSCTouch_S0Ratio");
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setConverter(new SpinnerToModel100Converter());
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setConverter(new Model100ToSpinnerConverter());
		bindingContext1.bindValue(spinner_1ObserveSelectionObserveWidget,
				selfIndicatorSettingspositiveOnSCRCTouch_S0RatioObserveValue,
				strategy_5, strategy_6);
		//
		IObservableValue btnCheckButtonObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnCheckButton);
		IObservableValue selfIndicatorSettingsnegativeOnPriceMultiplierObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.negativeOnPriceMultiplier");
		bindingContext1.bindValue(btnCheckButtonObserveSelectionObserveWidget,
				selfIndicatorSettingsnegativeOnPriceMultiplierObserveValue,
				null, null);
		//
		IObservableValue spinner_2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_2);
		IObservableValue selfIndicatorSettingsnegativeOnPriceMultiplier_priceMultiplierObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.negativeOnPriceMultiplier_priceMultiplier");
		bindingContext1
				.bindValue(
						spinner_2ObserveSelectionObserveWidget,
						selfIndicatorSettingsnegativeOnPriceMultiplier_priceMultiplierObserveValue,
						null, null);
		//
		IObservableValue spinner_3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_3);
		IObservableValue selfIndicatorSettingsnegativeOnPriceMultiplier_startTicksNumbersObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.negativeOnPriceMultiplier_startTicksNumbers");
		bindingContext1
				.bindValue(
						spinner_3ObserveSelectionObserveWidget,
						selfIndicatorSettingsnegativeOnPriceMultiplier_startTicksNumbersObserveValue,
						null, null);
		//
		IObservableValue btnNarrowingBoostingObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNarrowingBoosting);
		IObservableValue selfIndicatorSettingsregressionLines_narrowingBoostingObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.regressionLines_narrowingBoosting");
		bindingContext1
				.bindValue(
						btnNarrowingBoostingObserveSelectionObserveWidget,
						selfIndicatorSettingsregressionLines_narrowingBoostingObserveValue,
						null, null);
		//
		IObservableValue observeSingleSelectionComboViewerCentralLineAlgo = ViewerProperties
				.singleSelection().observe(comboViewerCentralLineAlgo);
		IObservableValue indicatorSettingsindicator_centerLineAlgoSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.indicator_centerLineAlgo");
		bindingContext1.bindValue(
				observeSingleSelectionComboViewerCentralLineAlgo,
				indicatorSettingsindicator_centerLineAlgoSelfObserveValue,
				null, null);
		//
		IObservableValue observeSingleSelectionComboViewerStartPointLength = ViewerProperties
				.singleSelection().observe(comboViewerStartPointLength);
		IObservableValue indicatorSettingsindicator_StartPointLengthSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.indicator_StartPointLength");
		bindingContext1.bindValue(
				observeSingleSelectionComboViewerStartPointLength,
				indicatorSettingsindicator_StartPointLengthSelfObserveValue,
				null, null);
		//
		IObservableValue observeSingleSelectionComboViewerSPAType = ViewerProperties
				.singleSelection().observe(comboViewerSPAType);
		IObservableValue indicatorSettingsindicator_SPATypeSelfObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.indicator_SPAType");
		bindingContext1.bindValue(observeSingleSelectionComboViewerSPAType,
				indicatorSettingsindicator_SPATypeSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewerTopBottomMaxDist = ViewerProperties
				.singleSelection().observe(comboViewerTopBottomMaxDist);
		IObservableValue indicatorSettingsindicator_TopBottomMaxDistSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.indicator_TopBottomMaxDist");
		_topBottomMaxDistanceBinding = bindingContext1.bindValue(
				observeSingleSelectionComboViewerTopBottomMaxDist,
				indicatorSettingsindicator_TopBottomMaxDistSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelectionBtnUsePreviousRegresionObserveWidget = WidgetProperties
				.selection().observe(btnUsePreviousRegresion);
		IObservableValue indicatorSettingsusePreviousRegressionValueForSCSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.usePreviousRegressionValueForSC");
		bindingContext1
				.bindValue(
						observeSelectionBtnUsePreviousRegresionObserveWidget,
						indicatorSettingsusePreviousRegressionValueForSCSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelectionBtnPivotAtObserveWidget = WidgetProperties
				.selection().observe(btnPivotAt);
		IObservableValue indicatorSettings_1PivotAtSameScaleSelfObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings._1PivotAtSameScale");
		bindingContext1
				.bindValue(observeSelectionBtnPivotAtObserveWidget,
						indicatorSettings_1PivotAtSameScaleSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelectionSpinner_4ObserveWidget = WidgetProperties
				.selection().observe(spinner_4);
		IObservableValue indicatorSettingsfixWindow_startWindowSelfObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.fixWindow_startWindow");
		bindingContext1.bindValue(observeSelectionSpinner_4ObserveWidget,
				indicatorSettingsfixWindow_startWindowSelfObserveValue, null,
				null);
		//
		IObservableValue observeSelectionSpinner_5ObserveWidget = WidgetProperties
				.selection().observe(spinner_5);
		IObservableValue indicatorSettingsfixWindow_windowMultiplierSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.fixWindow_windowMultiplier");
		bindingContext1.bindValue(observeSelectionSpinner_5ObserveWidget,
				indicatorSettingsfixWindow_windowMultiplierSelfObserveValue,
				null, null);
		//
		IObservableValue observeTextSpinner_6ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(spinner_6);
		IObservableValue indicatorSettingsfixWindow_deltaTopBottomPercSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.fixWindow_deltaTopBottomPerc");
		bindingContext1.bindValue(observeTextSpinner_6ObserveWidget,
				indicatorSettingsfixWindow_deltaTopBottomPercSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelectionSpinner_7ObserveWidget = WidgetProperties
				.selection().observe(spinner_7);
		IObservableValue indicatorSettingsfixWindow_deltaTopBottomTicksSelfObserveValue = BeansObservables
				.observeValue(self,
						"indicatorSettings.fixWindow_deltaTopBottomTicks");
		bindingContext1.bindValue(observeSelectionSpinner_7ObserveWidget,
				indicatorSettingsfixWindow_deltaTopBottomTicksSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelectionBtnSmoothingObserveWidget = WidgetProperties
				.selection().observe(btnSmoothing);
		IObservableValue indicatorSettingssmoothingSelfObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.smoothing");
		bindingContext1.bindValue(observeSelectionBtnSmoothingObserveWidget,
				indicatorSettingssmoothingSelfObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnAvoidTouchObserveWidget = WidgetProperties
				.selection().observe(btnAvoidTouch);
		IObservableValue indicatorSettingsfixWindow_avoidTouchSelfObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.fixWindow_avoidTouch");
		bindingContext1.bindValue(observeSelectionBtnAvoidTouchObserveWidget,
				indicatorSettingsfixWindow_avoidTouchSelfObserveValue, null,
				null);
		//
		IObservableValue observeEnabledCombo_2ObserveWidget = WidgetProperties
				.enabled().observe(combo_2);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StartPointLengthIsSPA());
		bindingContext1.bindValue(observeEnabledCombo_2ObserveWidget,
				indicatorSettingsindicator_StartPointLengthSelfObserveValue,
				null, strategy);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(text);
		IObservableValue indicatorSettingsfixWindow_maximumWindowLengthSelfObserveValue = BeanProperties
				.value("indicatorSettings.fixWindow_maximumWindowLength")
				.observe(self);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new MaxWindowLengthValidator());
		bindingContext1.bindValue(observeTextTextObserveWidget,
				indicatorSettingsfixWindow_maximumWindowLengthSelfObserveValue,
				strategy_1, null);
		//
		IObservableValue observeEnabledBtnAvoidTouchObserveWidget = WidgetProperties
				.enabled().observe(btnAvoidTouch);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new TopBottomMaxDist_IsNotHull());
		bindingContext1.bindValue(observeEnabledBtnAvoidTouchObserveWidget,
				indicatorSettingsindicator_TopBottomMaxDistSelfObserveValue,
				null, strategy_2);
		//
		IObservableValue observeSelectionSpinner_8ObserveWidget = WidgetProperties
				.selection().observe(spinner_8);
		IObservableValue indicatorSettingsfixWindow_deltaTopBottomMultiplierSelfObserveValue = BeanProperties
				.value("indicatorSettings.fixWindow_deltaTopBottomMultiplier")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelectionSpinner_8ObserveWidget,
						indicatorSettingsfixWindow_deltaTopBottomMultiplierSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_button_1ObserveWidget = WidgetProperties
				.selection().observe(_button_1);
		IObservableValue indicatorSettingsnoIndicatorInWarmUpSelfObserveValue = BeanProperties
				.value("indicatorSettings.noIndicatorInWarmUp").observe(self);
		bindingContext1.bindValue(observeSelection_button_1ObserveWidget,
				indicatorSettingsnoIndicatorInWarmUpSelfObserveValue, null,
				null);
		//
		IObservableValue observeSelection_button_3ObserveWidget = WidgetProperties
				.selection().observe(_button_3);
		IObservableValue indicatorSettingsthPercentForTopBottomMinDistanceEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.thPercentForTopBottomMinDistanceEnabled")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelection_button_3ObserveWidget,
						indicatorSettingsthPercentForTopBottomMinDistanceEnabledSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_spinner_1ObserveWidget = WidgetProperties
				.selection().observe(_spinner_1);
		IObservableValue indicatorSettingsthPercentForTopBottomMinDistanceSelfObserveValue = BeanProperties
				.value("indicatorSettings.thPercentForTopBottomMinDistance")
				.observe(self);
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new SpinnerToModelConverter());
		strategy_3
				.setBeforeSetValidator(new ThPercentForTopBottomDistanceValidator());
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setConverter(new ModelToSpinnerConverter());
		_thPercentForTopBottomDistanceBinding = bindingContext1
				.bindValue(
						observeSelection_spinner_1ObserveWidget,
						indicatorSettingsthPercentForTopBottomMinDistanceSelfObserveValue,
						strategy_3, strategy_4);
		//
		IObservableValue observeSelection_button_4ObserveWidget = WidgetProperties
				.selection().observe(_button_4);
		IObservableValue indicatorSettingsmaxPricesForPolylinesEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.maxPricesForPolylinesEnabled")
				.observe(self);
		bindingContext1.bindValue(observeSelection_button_4ObserveWidget,
				indicatorSettingsmaxPricesForPolylinesEnabledSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelection_button_2ObserveWidget = WidgetProperties
				.selection().observe(_button_2);
		IObservableValue indicatorSettingsnegativeOnSCTouch_S0RatioEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_S0RatioEnabled")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelection_button_2ObserveWidget,
						indicatorSettingsnegativeOnSCTouch_S0RatioEnabledSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_spinnerObserveWidget = WidgetProperties
				.selection().observe(_spinner);
		IObservableValue indicatorSettingsnegativeOnSCTouch_S0TimeRatioSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_S0TimeRatio")
				.observe(self);
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new SpinnerToModel100Converter());
		UpdateValueStrategy strategy_8 = new UpdateValueStrategy();
		strategy_8.setConverter(new Model100ToSpinnerConverter());
		bindingContext1.bindValue(observeSelection_spinnerObserveWidget,
				indicatorSettingsnegativeOnSCTouch_S0TimeRatioSelfObserveValue,
				strategy_7, strategy_8);
		//
		IObservableValue observeSelection_buttonObserveWidget = WidgetProperties
				.selection().observe(_button);
		IObservableValue indicatorSettingsnegativeOnSCTouch_S0TimeRatioEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_S0TimeRatioEnabled")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelection_buttonObserveWidget,
						indicatorSettingsnegativeOnSCTouch_S0TimeRatioEnabledSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_button_5ObserveWidget = WidgetProperties
				.selection().observe(_button_5);
		IObservableValue indicatorSettingsnoTBMinFromScaleUpEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.noTBMinFromScaleUpEnabled").observe(
						self);
		bindingContext1.bindValue(observeSelection_button_5ObserveWidget,
				indicatorSettingsnoTBMinFromScaleUpEnabledSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelection_spinner_2ObserveWidget = WidgetProperties
				.selection().observe(_spinner_2);
		IObservableValue indicatorSettingsnoTBMinFromScaleUpSelfObserveValue = BeanProperties
				.value("indicatorSettings.noTBMinFromScaleUp").observe(self);
		bindingContext1
				.bindValue(observeSelection_spinner_2ObserveWidget,
						indicatorSettingsnoTBMinFromScaleUpSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_spinner_3ObserveWidget = WidgetProperties
				.selection().observe(_spinner_3);
		IObservableValue indicatorSettingsmaxPricesForPolylinesSelfObserveValue = BeanProperties
				.value("indicatorSettings.maxPricesForPolylines").observe(self);
		bindingContext1.bindValue(observeSelection_spinner_3ObserveWidget,
				indicatorSettingsmaxPricesForPolylinesSelfObserveValue, null,
				null);
		//
		IObservableValue observeSelection_button_6ObserveWidget = WidgetProperties
				.selection().observe(_button_6);
		IObservableValue indicatorSettingsnegativeOnSCTouch_selfPivotBrakoutSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_selfPivotBrakout")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelection_button_6ObserveWidget,
						indicatorSettingsnegativeOnSCTouch_selfPivotBrakoutSelfObserveValue,
						null, null);
		//
		IObservableValue observeSelection_button_7ObserveWidget = WidgetProperties
				.selection().observe(_button_7);
		IObservableValue indicatorSettingsnegativeOnSCTouch_thPercentEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_thPercentEnabled")
				.observe(self);
		bindingContext1
				.bindValue(
						observeSelection_button_7ObserveWidget,
						indicatorSettingsnegativeOnSCTouch_thPercentEnabledSelfObserveValue,
						null, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_text);
		IObservableValue indicatorSettingsnegativeOnSCTouch_thPercentSelfObserveValue = BeanProperties
				.value("indicatorSettings.negativeOnSCTouch_thPercent")
				.observe(self);
		bindingContext1.bindValue(observeText_textObserveWidget,
				indicatorSettingsnegativeOnSCTouch_thPercentSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelection_button_8ObserveWidget = WidgetProperties
				.selection().observe(_button_8);
		IObservableValue indicatorSettingsbestChannelFittingEnabledSelfObserveValue = BeanProperties
				.value("indicatorSettings.bestChannelFittingEnabled").observe(
						self);
		bindingContext1.bindValue(observeSelection_button_8ObserveWidget,
				indicatorSettingsbestChannelFittingEnabledSelfObserveValue,
				null, null);
		//
		IObservableValue observeSelection_spinner_4ObserveWidget = WidgetProperties
				.selection().observe(_spinner_4);
		IObservableValue indicatorSettingsbestChannelFittingSelfObserveValue = BeanProperties
				.value("indicatorSettings.bestChannelFitting").observe(self);
		UpdateValueStrategy strategy_9 = new UpdateValueStrategy();
		strategy_9.setConverter(new SpinnerToModelConverter());
		UpdateValueStrategy strategy_10 = new UpdateValueStrategy();
		strategy_10.setConverter(new ModelToSpinnerConverter());
		bindingContext1.bindValue(observeSelection_spinner_4ObserveWidget,
				indicatorSettingsbestChannelFittingSelfObserveValue,
				strategy_9, strategy_10);
		//
		IObservableValue observeSelectionSpinner_11ObserveWidget = WidgetProperties
				.selection().observe(spinner_11);
		IObservableValue indicatorSettingssmoothing_convergeBoostSelfObserveValue = BeanProperties
				.value("indicatorSettings.smoothing_convergeBoost").observe(
						self);
		UpdateValueStrategy strategy_11 = new UpdateValueStrategy();
		strategy_11.setConverter(new SpinnerToModelConverter());
		UpdateValueStrategy strategy_12 = new UpdateValueStrategy();
		strategy_12.setConverter(new ModelToSpinnerConverter());
		bindingContext1.bindValue(observeSelectionSpinner_11ObserveWidget,
				indicatorSettingssmoothing_convergeBoostSelfObserveValue,
				strategy_11, strategy_12);
		//
		return bindingContext1;
	}
}
