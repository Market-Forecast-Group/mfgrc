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
package com.mfg.symbols.csv.ui.editors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.ui.widgets.FractionField.DenominatorValidator;

/**
 * @author arian
 * 
 */
public class CSVHistoricalDataInfoComposite extends Composite {
	private final DataBindingContext m_bindingContext;

	/**
	 * 
	 */
	private static final String PROP_MODEL = "model";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final Text text;
	private final Text text_1;
	private final ComboViewer comboViewerNumberOfPrices;
	private final CSVHistoricalDataInfoComposite self = this;
	private CSVHistoricalDataInfo model;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVHistoricalDataInfoComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(3, false));

		Label lblXp = new Label(this, SWT.NONE);
		toolkit.adapt(lblXp, true, true);
		lblXp.setText("XP");

		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		toolkit.adapt(text, true, true);

		Label lblDp = new Label(this, SWT.NONE);
		toolkit.adapt(lblDp, true, true);
		lblDp.setText("DP");

		text_1 = new Text(this, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));
		toolkit.adapt(text_1, true, true);

		_btnFilterOutOf = new Button(this, SWT.CHECK);
		_btnFilterOutOf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));
		toolkit.adapt(_btnFilterOutOf, true, true);
		_btnFilterOutOf.setText("Filter out of range ticks");

		Label lblMinimumGapIn = new Label(this, SWT.NONE);
		lblMinimumGapIn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(lblMinimumGapIn, true, true);
		lblMinimumGapIn.setText("Out of range ticks gap");

		_text = new Text(this, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.horizontalIndent = 5;
		_text.setLayoutData(gd_text);
		toolkit.adapt(_text, true, true);

		Label lblOfPrices = new Label(this, SWT.NONE);
		toolkit.adapt(lblOfPrices, true, true);
		lblOfPrices.setText("Warm-Up Prices");

		comboViewerNumberOfPrices = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo = comboViewerNumberOfPrices.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));
		toolkit.paintBordersFor(combo);
		comboViewerNumberOfPrices.setLabelProvider(new LabelProvider());
		comboViewerNumberOfPrices
				.setContentProvider(new ArrayContentProvider());

		Label lblGapFilling = new Label(this, SWT.NONE);
		lblGapFilling.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(lblGapFilling, true, true);
		lblGapFilling.setText("Gap Filling Type");

		gapFillingTypeCombo = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo_1 = gapFillingTypeCombo.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		toolkit.paintBordersFor(combo_1);
		gapFillingTypeCombo.setLabelProvider(new LabelProvider());
		gapFillingTypeCombo.setContentProvider(new ArrayContentProvider());

		lblDataType = toolkit.createLabel(this, "Data Type", SWT.NONE);
		lblDataType.setFont(SWTResourceManager.getFont("Sans Serif", 9,
				SWT.NORMAL));
		GridData gd_lblDataType = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1);
		gd_lblDataType.verticalIndent = 5;
		lblDataType.setLayoutData(gd_lblDataType);

		Composite composite = toolkit.createCompositeSeparator(this);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false,
				3, 1);
		gd_composite.heightHint = 2;
		composite.setLayoutData(gd_composite);
		toolkit.paintBordersFor(composite);

		toolkit.createLabel(this, "Scale", SWT.NONE);

		scaleCombo = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo_2 = scaleCombo.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		toolkit.paintBordersFor(combo_2);
		scaleCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.equals(Integer.valueOf(0)) ? "Price" : element
						.toString();
			}
		});
		scaleCombo.setContentProvider(new ArrayContentProvider());

		toolkit.createLabel(this, "Gap", SWT.NONE);

		Composite composite_1 = toolkit.createComposite(this, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.paintBordersFor(composite_1);
		GridLayout gl_composite_1 = new GridLayout(3, false);
		gl_composite_1.marginWidth = 2;
		composite_1.setLayout(gl_composite_1);

		text_2 = toolkit.createText(composite_1, "New Text", SWT.NONE);
		text_2.setText("");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label label = toolkit.createLabel(composite_1, "/", SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));

		text_3 = toolkit.createText(composite_1, "New Text", SWT.NONE);
		text_3.setText("");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		afterCreateWidgets();
		m_bindingContext = initDataBindings();

	}

	private void afterCreateWidgets() {
		comboViewerNumberOfPrices.setInput(new Object[] { Integer.valueOf(0),
				Integer.valueOf(10), Integer.valueOf(30), Integer.valueOf(50),
				Integer.valueOf(100), Integer.valueOf(300),
				Integer.valueOf(500), Integer.valueOf(1000),
				Integer.valueOf(3000), Integer.valueOf(10000),
				Integer.valueOf(20000), Integer.valueOf(30000),
				Integer.valueOf(50000), Integer.valueOf(100000),
				Integer.valueOf(1000000) });
		gapFillingTypeCombo.setInput(CSVHistoricalDataInfo.GapFillingType
				.values());
		lblDataType.setForeground(toolkit.getColors().getColor(
				IFormColors.TITLE));
		Integer[] scales = new Integer[21];
		for (int i = 0; i < scales.length; i++) {
			scales[i] = Integer.valueOf(i);
		}
		scaleCombo.setInput(scales);
	}

	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	/**
	 * @return the model
	 */
	public CSVHistoricalDataInfo getModel() {
		return model;
	}

	/**
	 * @param aModel
	 *            the model to set
	 */
	public void setModel(CSVHistoricalDataInfo aModel) {
		this.model = aModel;
		firePropertyChange(PROP_MODEL);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final ComboViewer gapFillingTypeCombo;
	private final Label lblDataType;
	private final ComboViewer scaleCombo;
	private final Text text_2;
	private final Text text_3;
	private Text _text;
	private Button _btnFilterOutOf;

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
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(text);
		IObservableValue modelxpSelfObserveValue = BeanProperties.value(
				"model.xp").observe(self);
		bindingContext.bindValue(observeTextTextObserveWidget,
				modelxpSelfObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_1);
		IObservableValue modeldpSelfObserveValue = BeanProperties.value(
				"model.dp").observe(self);
		bindingContext.bindValue(observeTextText_1ObserveWidget,
				modeldpSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewerNumberOfPrices = ViewerProperties
				.singleSelection().observe(comboViewerNumberOfPrices);
		IObservableValue modelnumberOfPricesSelfObserveValue = BeanProperties
				.value("model.numberOfPrices").observe(self);
		bindingContext.bindValue(
				observeSingleSelectionComboViewerNumberOfPrices,
				modelnumberOfPricesSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionGapFillingTypeCombo = ViewerProperties
				.singleSelection().observe(gapFillingTypeCombo);
		IObservableValue modelgapFillingTypeSelfObserveValue = BeanProperties
				.value("model.gapFillingType").observe(self);
		bindingContext.bindValue(observeSingleSelectionGapFillingTypeCombo,
				modelgapFillingTypeSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionScaleCombo = ViewerProperties
				.singleSelection().observe(scaleCombo);
		IObservableValue modelscaleSelfObserveValue = BeanProperties.value(
				"model.scale").observe(self);
		bindingContext.bindValue(observeSingleSelectionScaleCombo,
				modelscaleSelfObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_2);
		IObservableValue modelgap1SelfObserveValue = BeanProperties.value(
				"model.gap1").observe(self);
		bindingContext.bindValue(observeTextText_2ObserveWidget,
				modelgap1SelfObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_3);
		IObservableValue modelgap2SelfObserveValue = BeanProperties.value(
				"model.gap2").observe(self);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new DenominatorValidator());
		bindingContext.bindValue(observeTextText_3ObserveWidget,
				modelgap2SelfObserveValue, null, strategy);
		//
		IObservableValue observeText_btnFilterOutOfObserveWidget = WidgetProperties
				.selection().observe(_btnFilterOutOf);
		IObservableValue modelfilterOutOfRangeTicksSelfObserveValue = BeanProperties
				.value("model.filterOutOfRangeTicks").observe(self);
		bindingContext.bindValue(observeText_btnFilterOutOfObserveWidget,
				modelfilterOutOfRangeTicksSelfObserveValue, null, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_text);
		IObservableValue modelminGapInTicksSelfObserveValue = BeanProperties
				.value("model.minGapInTicks").observe(self);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new MinGapValidator());
		bindingContext.bindValue(observeText_textObserveWidget,
				modelminGapInTicksSelfObserveValue, strategy_1, null);
		//
		IObservableValue observeEnabled_textObserveWidget = WidgetProperties
				.enabled().observe(_text);
		bindingContext.bindValue(observeEnabled_textObserveWidget,
				modelfilterOutOfRangeTicksSelfObserveValue, null, null);
		//
		return bindingContext;
	}
}
