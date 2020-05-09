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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * @author arian
 * 
 */
@SuppressWarnings("unused")
public class IndicatorROSettingsComposite extends Composite {
	private Binding positiveOnSRRC_Touch_Binding;

	private final DataBindingContext m_bindingContext;
	private final IndicatorROSettingsComposite self = this;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	// private final Spinner spinner;
	// private final Button btnCheckButton;
	private final Button btnCheckButton_1;
	private final Spinner spinner_1;
	private final Button btnMaCentraInd;
	private final Spinner spinner_2;
	private final Spinner spinner_3;
	// private final Spinner spinner_4;
	// private final Button btnCheckButton_2;
	// private final Button btnStartScaleFor;
	private final Label lblNewLabel_5;
	// private final Text text;
	private final Button btnMultiscale;
	private final Spinner spinner_5;
	private final Spinner spinner_6;
	private final Button btnNewNegativeOn;
	// private final Spinner spinner_7;
	// private final Button btnEnabledFlatFor;
	// private final Button btnEnabledFlatFor_1;
	// private final Button btnAboveOr;
	// private final Button btnGenerateExtraSc;
	private final Button btnCheckButton_3;
	private final Spinner spinner_8;
	private final Button btnPositiveFromPositive;
	private final Button btnPositiveFromPositive_1;
	// private final Button btnStartFromScale;
	// private final Button btnMultiplePsCheck;
	private final IIndicatorSettingsContainer container;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IndicatorROSettingsComposite(Composite parent, int style,
			IIndicatorSettingsContainer aContainer) {
		super(parent, style);
		this.container = aContainer;
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
		composite_1.setLayout(new GridLayout(2, false));

		Section sctnIndicatorLines = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnIndicatorLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnIndicatorLines);
		sctnIndicatorLines.setText("Indicator Lines");
		sctnIndicatorLines.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnIndicatorLines,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnIndicatorLines.setClient(composite);
		composite.setLayout(new GridLayout(2, false));

		// btnCheckButton = new Button(composite, SWT.CHECK);
		// btnCheckButton.setEnabled(false);
		// btnCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false,
		// false, 2, 1));
		// toolkit.adapt(btnCheckButton, true, true);
		// btnCheckButton.setText("Use previous RGL value for SC");

		// Label lblNewLabel = toolkit.createLabel(composite, "RGL value",
		// SWT.NONE);

		// spinner = new Spinner(composite, SWT.BORDER);
		// spinner.setEnabled(false);
		// toolkit.adapt(spinner);
		// toolkit.paintBordersFor(spinner);

		btnCheckButton_1 = new Button(composite, SWT.CHECK);
		btnCheckButton_1.setEnabled(false);
		btnCheckButton_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnCheckButton_1, true, true);
		btnCheckButton_1.setText("Forward shifted central Ind");

		Label lblNewLabel_1 = toolkit.createLabel(composite,
				"Shifted central Ind value", SWT.NONE);

		spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setEnabled(false);
		toolkit.adapt(spinner_1);
		toolkit.paintBordersFor(spinner_1);

		btnMaCentraInd = new Button(composite, SWT.CHECK);
		btnMaCentraInd.setEnabled(false);
		btnMaCentraInd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		btnMaCentraInd.setText("MA central Ind");
		toolkit.adapt(btnMaCentraInd, true, true);

		Label lblNewLabel_3 = toolkit.createLabel(composite,
				"MA central Ind value", SWT.NONE);

		spinner_3 = new Spinner(composite, SWT.BORDER);
		spinner_3.setEnabled(false);
		toolkit.adapt(spinner_3);
		toolkit.paintBordersFor(spinner_3);

		Label lblNewLabel_2 = toolkit.createLabel(composite, "Shift", SWT.NONE);

		spinner_2 = new Spinner(composite, SWT.BORDER);
		spinner_2.setEnabled(false);
		toolkit.adapt(spinner_2);
		toolkit.paintBordersFor(spinner_2);

		Section sctnNewSection = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText("Linear Regression Optimization");
		sctnNewSection.setExpanded(true);

		Composite composite_2 = toolkit.createComposite(sctnNewSection,
				SWT.NONE);
		toolkit.paintBordersFor(composite_2);
		sctnNewSection.setClient(composite_2);
		composite_2.setLayout(new GridLayout(3, false));

		// Label lblNewLabel_4 = toolkit.createLabel(composite_2,
		// "Upper/Lower band", SWT.NONE);
		//
		// spinner_4 = new Spinner(composite_2, SWT.BORDER);
		// spinner_4.setEnabled(false);
		// spinner_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		// false, 2, 1));
		// toolkit.adapt(spinner_4);
		// toolkit.paintBordersFor(spinner_4);

		// btnCheckButton_2 = new Button(composite_2, SWT.CHECK);
		// btnCheckButton_2.setEnabled(false);
		// btnCheckButton_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 3, 1));
		// toolkit.adapt(btnCheckButton_2, true, true);
		// btnCheckButton_2
		// .setText("Start scale for new band algo/Test Indicator");

		// btnStartScaleFor = new Button(composite_2, SWT.CHECK);
		// btnStartScaleFor.setEnabled(false);
		// btnStartScaleFor.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// }
		// });
		// btnStartScaleFor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 3, 1));
		// btnStartScaleFor.setText("Start scale for new band algo/Log");
		// toolkit.adapt(btnStartScaleFor, true, true);

		lblNewLabel_5 = toolkit.createLabel(composite_1, "Multiscale ARC",
				SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		Composite composite_5 = toolkit.createCompositeSeparator(composite_1);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		gd_composite_5.heightHint = 2;
		composite_5.setLayoutData(gd_composite_5);
		toolkit.paintBordersFor(composite_5);

		Section sctnNegativeChannels = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNegativeChannels.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		toolkit.paintBordersFor(sctnNegativeChannels);
		sctnNegativeChannels.setText("Negative Channels");
		sctnNegativeChannels.setExpanded(true);

		Composite composite_3 = toolkit.createComposite(sctnNegativeChannels,
				SWT.NONE);
		toolkit.paintBordersFor(composite_3);
		sctnNegativeChannels.setClient(composite_3);
		composite_3.setLayout(new GridLayout(3, false));

		btnMultiscale = new Button(composite_3, SWT.CHECK);
		btnMultiscale.setEnabled(false);
		btnMultiscale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		toolkit.adapt(btnMultiscale, true, true);
		btnMultiscale.setText("Multi-scales");

		Label lblSize = toolkit.createLabel(composite_3, "Size", SWT.NONE);

		spinner_5 = new Spinner(composite_3, SWT.BORDER);
		spinner_5.setEnabled(false);
		spinner_5.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(spinner_5);
		toolkit.paintBordersFor(spinner_5);

		Label lblPivots = toolkit.createLabel(composite_3, "Pivots", SWT.NONE);

		spinner_6 = new Spinner(composite_3, SWT.BORDER);
		spinner_6.setEnabled(false);
		spinner_6.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(spinner_6);
		toolkit.paintBordersFor(spinner_6);

		btnNewNegativeOn = new Button(composite_3, SWT.CHECK);
		btnNewNegativeOn.setEnabled(false);
		btnNewNegativeOn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnNewNegativeOn, true, true);
		btnNewNegativeOn.setText("New negative on N-P flat");

		Label lblStartScaleOn = toolkit.createLabel(composite_3,
				"Start scale for Negative on Flat", SWT.NONE);
		new Label(composite_3, SWT.NONE);
		new Label(composite_3, SWT.NONE);

		// spinner_7 = new Spinner(composite_3, SWT.BORDER);
		// spinner_7.setEnabled(false);
		// spinner_7.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		// false, 2, 1));
		// toolkit.adapt(spinner_7);
		// toolkit.paintBordersFor(spinner_7);

		// btnEnabledFlatFor = new Button(composite_3, SWT.CHECK);
		// btnEnabledFlatFor.setEnabled(false);
		// btnEnabledFlatFor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 3, 1));
		// toolkit.adapt(btnEnabledFlatFor, true, true);
		// btnEnabledFlatFor.setText("Enabled Flat for Negative");

		// btnEnabledFlatFor_1 = new Button(composite_3, SWT.CHECK);
		// btnEnabledFlatFor_1.setEnabled(false);
		// btnEnabledFlatFor_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 3, 1));
		// toolkit.adapt(btnEnabledFlatFor_1, true, true);
		// btnEnabledFlatFor_1.setText("Enabled Flat for Positive");

		// btnAboveOr = new Button(composite_3, SWT.CHECK);
		// btnAboveOr.setEnabled(false);
		// btnAboveOr.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
		// false, 3, 1));
		// toolkit.adapt(btnAboveOr, true, true);
		// btnAboveOr.setText("Avoid 3 or More Identical Scales");

		// btnGenerateExtraSc = new Button(composite_3, SWT.CHECK);
		// btnGenerateExtraSc.setEnabled(false);
		// btnGenerateExtraSc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 3, 1));
		// toolkit.adapt(btnGenerateExtraSc, true, true);
		// btnGenerateExtraSc.setText("Generate extra SC pivots");

		// Label lblRglTolerance = toolkit.createLabel(composite_3,
		// "Rgl. Tolerance", SWT.NONE);

		// text = toolkit.createText(composite_3, "New Text", SWT.NONE);
		// text.setEditable(false);
		// text.setText("");
		// text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
		// 1));

		Section sctnPositiveChannels = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnPositiveChannels.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));
		toolkit.paintBordersFor(sctnPositiveChannels);
		sctnPositiveChannels.setText("Positive Channels");
		sctnPositiveChannels.setExpanded(true);

		Composite composite_4 = toolkit.createComposite(sctnPositiveChannels,
				SWT.NONE);
		toolkit.paintBordersFor(composite_4);
		sctnPositiveChannels.setClient(composite_4);
		composite_4.setLayout(new GridLayout(2, false));

		btnCheckButton_3 = new Button(composite_4, SWT.CHECK);
		btnCheckButton_3.setEnabled(false);
		btnCheckButton_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnCheckButton_3, true, true);
		btnCheckButton_3.setText("Enabled Positive Channels");

		Label lblPositiveChannelsStart = toolkit.createLabel(composite_4,
				"Positive Channels Start Scale", SWT.NONE);

		spinner_8 = new Spinner(composite_4, SWT.BORDER);
		spinner_8.setEnabled(false);
		toolkit.adapt(spinner_8);
		toolkit.paintBordersFor(spinner_8);

		btnPositiveFromPositive = new Button(composite_4, SWT.CHECK);
		btnPositiveFromPositive.setEnabled(false);
		btnPositiveFromPositive.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		toolkit.adapt(btnPositiveFromPositive, true, true);
		btnPositiveFromPositive.setText("Positive from Positive at LS");

		btnPositiveFromPositive_1 = new Button(composite_4, SWT.CHECK);
		btnPositiveFromPositive_1.setEnabled(false);
		btnPositiveFromPositive_1.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		toolkit.adapt(btnPositiveFromPositive_1, true, true);
		btnPositiveFromPositive_1.setText("Positive from Positive at 2 LS");

		// btnStartFromScale = new Button(composite_4, SWT.CHECK);
		// btnStartFromScale.setEnabled(false);
		// btnStartFromScale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 2, 1));
		// toolkit.adapt(btnStartFromScale, true, true);
		// btnStartFromScale.setText("Start from Scale 3");

		// btnMultiplePsCheck = new Button(composite_4, SWT.CHECK);
		// btnMultiplePsCheck.setEnabled(false);
		// btnMultiplePsCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
		// false, false, 2, 1));
		// toolkit.adapt(btnMultiplePsCheck, true, true);
		// btnMultiplePsCheck.setText("Multiple PS Check");

		lblNewIndicator = toolkit.createLabel(composite_1, "New Indicator",
				SWT.NONE);
		lblNewIndicator.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		composite_6 = toolkit.createCompositeSeparator(composite_1);
		GridData gd_composite_6 = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		gd_composite_6.heightHint = 2;
		composite_6.setLayoutData(gd_composite_6);
		toolkit.paintBordersFor(composite_6);

		Composite composite_11 = toolkit.createComposite(composite_1, SWT.NONE);
		composite_11.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		toolkit.paintBordersFor(composite_11);
		composite_11.setLayout(new GridLayout(3, false));

		sctnMain = toolkit.createSection(composite_11,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,
				1));
		sctnMain.setSize(343, 116);
		toolkit.paintBordersFor(sctnMain);
		sctnMain.setText("Main");
		sctnMain.setExpanded(true);

		composite_7 = toolkit.createComposite(sctnMain, SWT.NONE);
		toolkit.paintBordersFor(composite_7);
		sctnMain.setClient(composite_7);
		composite_7.setLayout(new GridLayout(2, false));

		btnNewIndicator = new Button(composite_7, SWT.CHECK);
		btnNewIndicator.setEnabled(false);
		btnNewIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		toolkit.adapt(btnNewIndicator, true, true);
		btnNewIndicator.setText("New Indicator");

		Label lblNumberOfScales = toolkit.createLabel(composite_7,
				"Number of Scales", SWT.NONE);

		spinner_9 = new Spinner(composite_7, SWT.BORDER);
		spinner_9.setEnabled(false);
		toolkit.adapt(spinner_9);
		toolkit.paintBordersFor(spinner_9);

		btnPrintMessages = new Button(composite_7, SWT.CHECK);
		btnPrintMessages.setEnabled(false);
		btnPrintMessages.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnPrintMessages, true, true);
		btnPrintMessages.setText("Print Messages for Testing");
		new Label(composite_7, SWT.NONE);
		new Label(composite_7, SWT.NONE);

		Section sctnNewChannelsRules = toolkit.createSection(composite_11,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewChannelsRules.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));
		sctnNewChannelsRules.setSize(352, 116);
		toolkit.paintBordersFor(sctnNewChannelsRules);
		sctnNewChannelsRules.setText("New Channels Rules");
		sctnNewChannelsRules.setExpanded(true);

		Composite composite_8 = toolkit.createComposite(sctnNewChannelsRules,
				SWT.NONE);
		toolkit.paintBordersFor(composite_8);
		sctnNewChannelsRules.setClient(composite_8);
		composite_8.setLayout(new GridLayout(3, false));

		btnNegativeOnPivot = new Button(composite_8, SWT.CHECK);
		btnNegativeOnPivot.setEnabled(false);
		btnNegativeOnPivot.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnNegativeOnPivot, true, true);
		btnNegativeOnPivot.setText("Negative on Pivot Breakout");

		btnNegativeOnFlat = new Button(composite_8, SWT.CHECK);
		btnNegativeOnFlat.setEnabled(false);
		btnNegativeOnFlat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnNegativeOnFlat, true, true);
		btnNegativeOnFlat.setText("Negative on Flat Channel");

		btnPositiveOnScrc = new Button(composite_8, SWT.CHECK);
		btnPositiveOnScrc.setEnabled(false);
		btnPositiveOnScrc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1));
		toolkit.adapt(btnPositiveOnScrc, true, true);
		btnPositiveOnScrc.setText("Positive on SC/RC Touch");
		new Label(composite_8, SWT.NONE);

		Label lblStartScale = toolkit.createLabel(composite_8, "Start Scale",
				SWT.NONE);

		spinner_10 = new Spinner(composite_8, SWT.BORDER);
		spinner_10.setEnabled(false);
		toolkit.adapt(spinner_10);
		toolkit.paintBordersFor(spinner_10);

		Section sctnStartPoint = toolkit.createSection(composite_11,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnStartPoint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnStartPoint);
		sctnStartPoint.setText("Start Point");
		sctnStartPoint.setExpanded(true);

		Composite composite_9 = toolkit.createComposite(sctnStartPoint,
				SWT.NONE);
		toolkit.paintBordersFor(composite_9);
		sctnStartPoint.setClient(composite_9);
		composite_9.setLayout(new GridLayout(1, false));

		btnFix = new Button(composite_9, SWT.RADIO);
		btnFix.setEnabled(false);
		toolkit.adapt(btnFix, true, true);
		btnFix.setText("Fix");

		btnDinamyc = new Button(composite_9, SWT.RADIO);
		btnDinamyc.setEnabled(false);
		toolkit.adapt(btnDinamyc, true, true);
		btnDinamyc.setText("Dynamic");

		Section sctnStartPointArray = toolkit.createSection(composite_11,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnStartPointArray.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));
		toolkit.paintBordersFor(sctnStartPointArray);
		sctnStartPointArray.setText("Start Point Array Rules");
		sctnStartPointArray.setExpanded(true);

		Composite composite_10 = toolkit.createComposite(sctnStartPointArray,
				SWT.NONE);
		toolkit.paintBordersFor(composite_10);
		sctnStartPointArray.setClient(composite_10);
		composite_10.setLayout(new GridLayout(1, false));

		btnPivotsAt = new Button(composite_10, SWT.CHECK);
		btnPivotsAt.setEnabled(false);
		toolkit.adapt(btnPivotsAt, true, true);
		btnPivotsAt.setText("3 Pivots at 1 LS ");

		btnPivotsAt_1 = new Button(composite_10, SWT.CHECK);
		btnPivotsAt_1.setEnabled(false);
		toolkit.adapt(btnPivotsAt_1, true, true);
		btnPivotsAt_1.setText("5 Pivots at 2 LS ");

		btnPivotsAt_2 = new Button(composite_10, SWT.CHECK);
		btnPivotsAt_2.setEnabled(false);
		toolkit.adapt(btnPivotsAt_2, true, true);
		btnPivotsAt_2.setText("13 Pivots at 3LS ");

		Section sctnRegressionlines = toolkit.createSection(composite_11,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnRegressionlines.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		toolkit.paintBordersFor(sctnRegressionlines);
		sctnRegressionlines.setText("Regression Lines");
		sctnRegressionlines.setExpanded(true);

		Composite composite_12 = toolkit.createComposite(sctnRegressionlines,
				SWT.NONE);
		toolkit.paintBordersFor(composite_12);
		sctnRegressionlines.setClient(composite_12);
		composite_12.setLayout(new GridLayout(1, false));

		btnUsePreviousRegression = new Button(composite_12, SWT.CHECK);
		btnUsePreviousRegression.setEnabled(false);
		toolkit.adapt(btnUsePreviousRegression, true, true);
		btnUsePreviousRegression
				.setText("Use Previous Regression Value for SC");
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		m_bindingContext = initDataBindings();
		afterCreate();
	}

	/**
	 * 
	 */
	private void afterCreate() {
		positiveOnSRRC_Touch_Binding.getTarget().addChangeListener(
				new IChangeListener() {

					@SuppressWarnings({ "deprecation", "boxing" })
					@Override
					public void handleChange(ChangeEvent event) {
						if (getIndicatorSettings().isDinamyc()) {
							IObservableValue obsValue = (IObservableValue) event
									.getObservable();
							if (!(Boolean) obsValue.getValue()) {
								obsValue.setValue(true);
								MessageDialog
										.openError(
												getShell(),
												"Invalid Parameter",
												"You cannot deselect the parameter Positive on SC/RC Touch when the Dynamic start point is selected. You have to change first the start point to Fix");
							}
						}
					}
				});
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
		return container.getIndicatorSettings();
	}

	public Color getBlueColor() {
		return toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
	}

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final Label lblNewIndicator;
	private final Composite composite_6;
	private final Button btnNewIndicator;
	private final Section sctnMain;
	private final Composite composite_7;
	private final Spinner spinner_9;
	private final Button btnNegativeOnPivot;
	private final Button btnNegativeOnFlat;
	private final Button btnPositiveOnScrc;
	private final Spinner spinner_10;
	private final Button btnFix;
	private final Button btnDinamyc;
	private final Button btnPivotsAt;
	private final Button btnPivotsAt_1;
	private final Button btnPivotsAt_2;
	private final Button btnUsePreviousRegression;
	private final Button btnPrintMessages;
	private DataBindingContext bindingContext;

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

	public void refreshValues() {
		bindingContext.updateTargets();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext aBindingContext1 = new DataBindingContext();
		//
		// IObservableValue spinnerObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner);
		// IObservableValue getIndicatorSettingsStartScalePrevRglObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "startScalePrevRgl");
		// bindingContext.bindValue(spinnerObserveSelectionObserveWidget,
		// getIndicatorSettingsStartScalePrevRglObserveValue, null, null);
		//
		// IObservableValue btnCheckButtonObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnCheckButton);
		// IObservableValue
		// getIndicatorSettingsEnabledRglPreviousValueObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledRglPreviousValue");
		// bindingContext.bindValue(btnCheckButtonObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledRglPreviousValueObserveValue, null,
		// null);
		//
		// IObservableValue btnCheckButton_1ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnCheckButton_1);
		// IObservableValue
		// getIndicatorSettingsEnabledforwardshcntindObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledforwardshcntind");
		// bindingContext.bindValue(btnCheckButton_1ObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledforwardshcntindObserveValue, null,
		// null);
		//
		// IObservableValue spinner_1ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_1);
		// IObservableValue getIndicatorSettingsForwardshcntindObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "forwardshcntind");
		// bindingContext.bindValue(spinner_1ObserveSelectionObserveWidget,
		// getIndicatorSettingsForwardshcntindObserveValue, null, null);
		//
		// IObservableValue btnMaCentraIndObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnMaCentraInd);
		// IObservableValue getIndicatorSettingsEnabledmacentralindObserveValue
		// = BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledmacentralind");
		// bindingContext
		// .bindValue(btnMaCentraIndObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledmacentralindObserveValue,
		// null, null);
		//
		// IObservableValue spinner_2ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_2);
		// IObservableValue getIndicatorSettingsMacentralindObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "macentralind");
		// bindingContext.bindValue(spinner_2ObserveSelectionObserveWidget,
		// getIndicatorSettingsMacentralindObserveValue, null, null);
		//
		// IObservableValue spinner_3ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_3);
		// bindingContext.bindValue(spinner_3ObserveSelectionObserveWidget,
		// getIndicatorSettingsMacentralindObserveValue, null, null);
		//
		// IObservableValue spinner_4ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_4);
		// IObservableValue getIndicatorSettingsStartScaleBandAlgoObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "startScaleBandAlgo");
		// bindingContext.bindValue(spinner_4ObserveSelectionObserveWidget,
		// getIndicatorSettingsStartScaleBandAlgoObserveValue, null, null);
		//
		// IObservableValue btnCheckButton_2ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnCheckButton_2);
		// IObservableValue getIndicatorSettingsEnabledTestIndicatorObserveValue
		// = BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledTestIndicator");
		// bindingContext.bindValue(btnCheckButton_2ObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledTestIndicatorObserveValue, null,
		// null);
		//
		// IObservableValue btnStartScaleForObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnStartScaleFor);
		// IObservableValue
		// getIndicatorSettingsEnabledLogChannelBandObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledLogChannelBand");
		// bindingContext.bindValue(btnStartScaleForObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledLogChannelBandObserveValue, null,
		// null);
		//
		IObservableValue lblNewLabel_5ObserveForegroundObserveWidget = SWTObservables
				.observeForeground(lblNewLabel_5);
		IObservableValue selfBlueColorObserveValue = BeansObservables
				.observeValue(self, "blueColor");
		aBindingContext1.bindValue(lblNewLabel_5ObserveForegroundObserveWidget,
				selfBlueColorObserveValue, null, null);
		//
		// IObservableValue btnMultiscaleObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnMultiscale);
		// IObservableValue getIndicatorSettingsEnabledMsSizeObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledMsSize");
		// bindingContext.bindValue(btnMultiscaleObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledMsSizeObserveValue, null, null);
		//
		// IObservableValue spinner_5ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_5);
		// IObservableValue getIndicatorSettingsMsSizeObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "msSize");
		// bindingContext.bindValue(spinner_5ObserveSelectionObserveWidget,
		// getIndicatorSettingsMsSizeObserveValue, null, null);
		//
		IObservableValue spinner_6ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_6);
		IObservableValue getIndicatorSettingsStartScaleScPivotsObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "startScaleScPivots");
		aBindingContext1.bindValue(spinner_6ObserveSelectionObserveWidget,
				getIndicatorSettingsStartScaleScPivotsObserveValue, null, null);
		//
		// IObservableValue btnNewNegativeOnObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnNewNegativeOn);
		// IObservableValue
		// getIndicatorSettingsEnabledNewNegativeOnFlatObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledNewNegativeOnFlat");
		// bindingContext.bindValue(btnNewNegativeOnObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledNewNegativeOnFlatObserveValue, null,
		// null);
		//
		// IObservableValue spinner_7ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_7);
		// IObservableValue
		// getIndicatorSettingsStartScaleNewNegOnFlatObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "startScaleNewNegOnFlat");
		// bindingContext.bindValue(spinner_7ObserveSelectionObserveWidget,
		// getIndicatorSettingsStartScaleNewNegOnFlatObserveValue, null,
		// null);
		//
		// IObservableValue btnEnabledFlatForObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnEnabledFlatFor);
		// IObservableValue
		// getIndicatorSettingsEnabledFlatForNegativeObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledFlatForNegative");
		// bindingContext.bindValue(
		// btnEnabledFlatForObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledFlatForNegativeObserveValue, null,
		// null);
		//
		// IObservableValue btnEnabledFlatFor_1ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnEnabledFlatFor_1);
		// IObservableValue
		// getIndicatorSettingsEnabledFlatForPositiveObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledFlatForPositive");
		// bindingContext.bindValue(
		// btnEnabledFlatFor_1ObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledFlatForPositiveObserveValue, null,
		// null);
		//
		// IObservableValue btnAboveOrObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnAboveOr);
		// IObservableValue
		// getIndicatorSettingsEnabledAvoidIdenticalScalesObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledAvoidIdenticalScales");
		// bindingContext.bindValue(btnAboveOrObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledAvoidIdenticalScalesObserveValue,
		// null, null);
		//
		// IObservableValue btnGenerateExtraScObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnGenerateExtraSc);
		// IObservableValue
		// getIndicatorSettingsEnabledGenerateScRcPivotsObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledGenerateScRcPivots");
		// bindingContext.bindValue(
		// btnGenerateExtraScObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledGenerateScRcPivotsObserveValue,
		// null, null);
		//
		// IObservableValue textObserveTextObserveWidget = SWTObservables
		// .observeText(text, SWT.Modify);
		// IObservableValue getIndicatorSettingsRglToleranceObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "rglTolerance");
		// bindingContext.bindValue(textObserveTextObserveWidget,
		// getIndicatorSettingsRglToleranceObserveValue, null, null);
		//
		// IObservableValue btnCheckButton_3ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnCheckButton_3);
		// IObservableValue getIndicatorSettingsEnabledmspositivechObserveValue
		// = BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledmspositivech");
		// bindingContext
		// .bindValue(btnCheckButton_3ObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledmspositivechObserveValue,
		// null, null);
		//
		// IObservableValue spinner_8ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(spinner_8);
		// IObservableValue
		// getIndicatorSettingsStartscalelevelpositivechObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "startscalelevelpositivech");
		// bindingContext.bindValue(spinner_8ObserveSelectionObserveWidget,
		// getIndicatorSettingsStartscalelevelpositivechObserveValue,
		// null, null);
		//
		// IObservableValue btnPositiveFromPositiveObserveSelectionObserveWidget
		// = SWTObservables
		// .observeSelection(btnPositiveFromPositive);
		// IObservableValue
		// getIndicatorSettingsEnabledStartNewPositiveFromPositiveAtLsObserveValue
		// = BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledStartNewPositiveFromPositiveAtLs");
		// bindingContext
		// .bindValue(
		// btnPositiveFromPositiveObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledStartNewPositiveFromPositiveAtLsObserveValue,
		// null, null);
		//
		// IObservableValue
		// btnPositiveFromPositive_1ObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnPositiveFromPositive_1);
		// IObservableValue
		// getIndicatorSettingsEnabledPositiveFromNegativeAt2LSObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledPositiveFromNegativeAt2LS");
		// bindingContext
		// .bindValue(
		// btnPositiveFromPositive_1ObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledPositiveFromNegativeAt2LSObserveValue,
		// null, null);
		//
		// IObservableValue btnStartFromScaleObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnStartFromScale);
		// IObservableValue
		// getIndicatorSettingsEnabledStartFromScaleThreeObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(),
		// "enabledStartFromScaleThree");
		// bindingContext.bindValue(
		// btnStartFromScaleObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledStartFromScaleThreeObserveValue,
		// null, null);
		//
		// IObservableValue btnMultiplePsCheckObserveSelectionObserveWidget =
		// SWTObservables
		// .observeSelection(btnMultiplePsCheck);
		// IObservableValue
		// getIndicatorSettingsEnabledMultipleSpCheckObserveValue =
		// BeansObservables
		// .observeValue(getIndicatorSettings(), "enabledMultipleSpCheck");
		// bindingContext.bindValue(
		// btnMultiplePsCheckObserveSelectionObserveWidget,
		// getIndicatorSettingsEnabledMultipleSpCheckObserveValue, null,
		// null);
		//
		IObservableValue lblNewIndicatorObserveForegroundObserveWidget = SWTObservables
				.observeForeground(lblNewIndicator);
		aBindingContext1.bindValue(
				lblNewIndicatorObserveForegroundObserveWidget,
				selfBlueColorObserveValue, null, null);
		//
		IObservableValue btnNewIndicatorObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNewIndicator);
		IObservableValue selfIndicatorSettingsnewIndicatorObserveValue = BeansObservables
				.observeValue(self, "indicatorSettings.newIndicator");
		aBindingContext1.bindValue(
				btnNewIndicatorObserveSelectionObserveWidget,
				selfIndicatorSettingsnewIndicatorObserveValue, null, null);
		//
		IObservableValue spinner_9ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_9);
		IObservableValue getIndicatorSettingsIndicatorNumberOfScales_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "indicatorNumberOfScales");
		aBindingContext1.bindValue(spinner_9ObserveSelectionObserveWidget,
				getIndicatorSettingsIndicatorNumberOfScales_NewObserveValue,
				null, null);
		//
		IObservableValue btnNegativeOnPivotObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNegativeOnPivot);
		IObservableValue getIndicatorSettingsNegativeOnPivotBreakOut_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "negativeOnPivotBreakOut");
		aBindingContext1.bindValue(
				btnNegativeOnPivotObserveSelectionObserveWidget,
				getIndicatorSettingsNegativeOnPivotBreakOut_NewObserveValue,
				null, null);
		//
		IObservableValue btnNegativeOnFlatObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnNegativeOnFlat);
		IObservableValue getIndicatorSettingsNegativeOnFlatChannel_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "negativeOnFlatChannel");
		aBindingContext1.bindValue(
				btnNegativeOnFlatObserveSelectionObserveWidget,
				getIndicatorSettingsNegativeOnFlatChannel_NewObserveValue,
				null, null);
		//
		IObservableValue btnPositiveOnScrcObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPositiveOnScrc);
		IObservableValue getIndicatorSettingsPositiveOnSCRCTouch_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "positiveOnSCRCTouch");
		positiveOnSRRC_Touch_Binding = aBindingContext1.bindValue(
				btnPositiveOnScrcObserveSelectionObserveWidget,
				getIndicatorSettingsPositiveOnSCRCTouch_NewObserveValue, null,
				null);
		//
		IObservableValue spinner_10ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_10);
		IObservableValue getIndicatorSettingsPositiveOnSCRCTouch_startScale_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(),
						"positiveOnSCRCTouch_startScale");
		aBindingContext1
				.bindValue(
						spinner_10ObserveSelectionObserveWidget,
						getIndicatorSettingsPositiveOnSCRCTouch_startScale_NewObserveValue,
						null, null);
		//
		IObservableValue btnFixObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnFix);
		IObservableValue getIndicatorSettingsFix_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "fix");
		aBindingContext1.bindValue(btnFixObserveSelectionObserveWidget,
				getIndicatorSettingsFix_NewObserveValue, null, null);
		//
		IObservableValue btnDinamycObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnDinamyc);
		IObservableValue getIndicatorSettingsDinamyc_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "dinamyc");
		aBindingContext1.bindValue(btnDinamycObserveSelectionObserveWidget,
				getIndicatorSettingsDinamyc_NewObserveValue, null, null);
		//
		IObservableValue btnPivotsAtObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt);
		IObservableValue getIndicatorSettings_3PivotsAt1LS_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "_3PivotsAt1LS");
		aBindingContext1.bindValue(btnPivotsAtObserveSelectionObserveWidget,
				getIndicatorSettings_3PivotsAt1LS_NewObserveValue, null, null);
		//
		IObservableValue btnPivotsAt_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt_1);
		IObservableValue getIndicatorSettings_5PivotsAt2LS_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "_5PivotsAt2LS");
		aBindingContext1.bindValue(btnPivotsAt_1ObserveSelectionObserveWidget,
				getIndicatorSettings_5PivotsAt2LS_NewObserveValue, null, null);
		//
		IObservableValue btnPivotsAt_2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPivotsAt_2);
		IObservableValue getIndicatorSettings_13PivotsAt3LS_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "_13PivotsAt3LS");
		aBindingContext1.bindValue(btnPivotsAt_2ObserveSelectionObserveWidget,
				getIndicatorSettings_13PivotsAt3LS_NewObserveValue, null, null);
		//
		IObservableValue btnUsePreviousRegressionObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnUsePreviousRegression);
		IObservableValue getIndicatorSettingsUsePreviousRegressionValueForSC_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(),
						"usePreviousRegressionValueForSC");
		aBindingContext1
				.bindValue(
						btnUsePreviousRegressionObserveSelectionObserveWidget,
						getIndicatorSettingsUsePreviousRegressionValueForSC_NewObserveValue,
						null, null);
		//
		IObservableValue btnPrintMessagesObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnPrintMessages);
		IObservableValue getIndicatorSettingsPrintMessagesForTesting_NewObserveValue = BeansObservables
				.observeValue(getIndicatorSettings(), "printMessagesForTesting");
		aBindingContext1.bindValue(
				btnPrintMessagesObserveSelectionObserveWidget,
				getIndicatorSettingsPrintMessagesForTesting_NewObserveValue,
				null, null);
		//
		return aBindingContext1;
	}
}
