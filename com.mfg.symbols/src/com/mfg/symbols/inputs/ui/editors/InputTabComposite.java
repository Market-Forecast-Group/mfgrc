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
package com.mfg.symbols.inputs.ui.editors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.ProbabilitiesNames;
import com.mfg.widget.ui.IndicatorSettingsComposite;

/**
 * @author arian
 * 
 */
public class InputTabComposite extends Composite {

	private static final String PROP_INFO = "info";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final ComboViewer comboViewerProbDistribution;
	private InputConfigurationInfo info;
	private final IndicatorSettingsComposite indicatorSettingsComposite;
	private final Button btnProbabilityDistribution;
	private final Button btnUseProbability;
	private final Button btnConditionalProbabilitiesLines;
	private final Text textPercentValue;
	private final Label lblValue;
	private final Text textMinMatches;
	private final Label lblMinMatches;
	private final Combo probDistributionCombo;
	private final InputTabComposite self = this;
	private DataBindingContext m_bindingContext;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public InputTabComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		scrolledComposite = new ScrolledComposite(this, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		toolkit.adapt(scrolledComposite);
		toolkit.paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = toolkit.createComposite(scrolledComposite);
		composite.setLayout(new GridLayout(1, false));

		_tabFolder = new CTabFolder(composite, SWT.BORDER);
		_tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		toolkit.adapt(_tabFolder);
		toolkit.paintBordersFor(_tabFolder);
		_tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		_tabItem = new CTabItem(_tabFolder, SWT.NONE);
		_tabItem.setText("Multiscale ARC Indicator");
		indicatorSettingsComposite = new IndicatorSettingsComposite(_tabFolder,
				SWT.NONE);
		_tabItem.setControl(indicatorSettingsComposite);
		toolkit.adapt(indicatorSettingsComposite);
		toolkit.paintBordersFor(indicatorSettingsComposite);

		_tabItem_1 = new CTabItem(_tabFolder, SWT.NONE);
		_tabItem_1.setText("Probabilities Lines");

		_composite = toolkit.createComposite(_tabFolder, SWT.NONE);
		_tabItem_1.setControl(_composite);
		toolkit.paintBordersFor(_composite);
		_composite.setLayout(new GridLayout(2, false));

		btnProbabilityDistribution = new Button(_composite, SWT.CHECK);
		toolkit.adapt(btnProbabilityDistribution, true, true);
		btnProbabilityDistribution.setText("Probability Distribution");

		comboViewerProbDistribution = new ComboViewer(_composite, SWT.READ_ONLY);
		probDistributionCombo = comboViewerProbDistribution.getCombo();
		probDistributionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		toolkit.paintBordersFor(probDistributionCombo);

		btnUseProbability = new Button(_composite, SWT.CHECK);
		btnUseProbability.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		toolkit.adapt(btnUseProbability, true, true);
		btnUseProbability.setText("Use Probability % Lines");

		lblValue = toolkit.createLabel(_composite, "% Value", SWT.NONE);

		textPercentValue = toolkit.createText(_composite, "New Text", SWT.NONE);
		textPercentValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		textPercentValue.setText("");

		lblMinMatches = toolkit.createLabel(_composite, "MIN Matches %",
				SWT.NONE);

		textMinMatches = toolkit.createText(_composite, "New Text", SWT.NONE);
		textMinMatches.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		textMinMatches.setText("");

		btnConditionalProbabilitiesLines = new Button(_composite, SWT.CHECK);
		btnConditionalProbabilitiesLines.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		toolkit.adapt(btnConditionalProbabilitiesLines, true, true);
		btnConditionalProbabilitiesLines
				.setText("Conditional Probabilities Lines Only");
		comboViewerProbDistribution
				.setContentProvider(new ArrayContentProvider());
		comboViewerProbDistribution.setLabelProvider(new LabelProvider());
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

	}

	public void initWidgets() {
		fillProbabilitiesCombo();
		m_bindingContext = initDataBindings();
	}

	private void fillProbabilitiesCombo() {
		if (!comboViewerProbDistribution.getCombo().isDisposed()) {
			List<String> list = WidgetPlugin.getDefault()
					.getProbabilitiesManager().getDistributionsStorate()
					.getProbabilitiesNames();
			list.add(ProbabilitiesNames.CURRENT_PROBABILITY);
			list.add(ProbabilitiesNames.NO_PROBABILITY);
			comboViewerProbDistribution.setInput(list);
			String name = getInfo().getProbabilityName();
			comboViewerProbDistribution.setSelection(new StructuredSelection(
					name == null ? ProbabilitiesNames.NO_PROBABILITY : name),
					true);
		}
	}

	/**
	 * @return the info
	 */
	public InputConfigurationInfo getInfo() {
		return info;
	}

	/**
	 * @param aInfo
	 *            the info to set
	 */
	public void setInfo(InputConfigurationInfo aInfo) {
		this.info = aInfo;
		firePropertyChange(PROP_INFO);
		indicatorSettingsComposite.setIndicatorSettings(aInfo
				.getIndicatorParams());
	}

	/**
	 * @return the indicatorSettingsComposite
	 */
	public IndicatorSettingsComposite getIndicatorSettingsComposite() {
		return indicatorSettingsComposite;
	}

	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getBindingContext() {
		return m_bindingContext;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue btnProbabilityDistributionObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnProbabilityDistribution);
		IObservableValue getInfoUsingProbabilitiesObserveValue = BeansObservables
				.observeValue(self, "info.usingProbabilities");
		bindingContext.bindValue(
				btnProbabilityDistributionObserveSelectionObserveWidget,
				getInfoUsingProbabilitiesObserveValue, null, null);
		//
		IObservableValue comboViewerProbDistributionObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewerProbDistribution);
		IObservableValue getInfoProbabilityNameObserveValue = BeansObservables
				.observeValue(self, "info.probabilityName");
		bindingContext.bindValue(
				comboViewerProbDistributionObserveSingleSelection,
				getInfoProbabilityNameObserveValue, null, null);
		//
		IObservableValue btnUseProbabilityObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnUseProbability);
		IObservableValue getInfoIndicatorParamsprobLinesPercentValueEnabledObserveValue = BeansObservables
				.observeValue(self,
						"info.indicatorParams.probLinesPercentValueEnabled");
		bindingContext.bindValue(
				btnUseProbabilityObserveSelectionObserveWidget,
				getInfoIndicatorParamsprobLinesPercentValueEnabledObserveValue,
				null, null);
		//
		IObservableValue textPercentValueObserveTextObserveWidget = SWTObservables
				.observeText(textPercentValue, SWT.Modify);
		IObservableValue getInfoIndicatorParamsprobLinesPercentValueObserveValue = BeansObservables
				.observeValue(self,
						"info.indicatorParams.probLinesPercentValue");
		bindingContext.bindValue(textPercentValueObserveTextObserveWidget,
				getInfoIndicatorParamsprobLinesPercentValueObserveValue, null,
				null);
		//
		IObservableValue btnConditionalProbabilitiesLinesObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnConditionalProbabilitiesLines);
		IObservableValue getInfoIndicatorParamsprobLinesConditionalOnlyEnabledObserveValue = BeansObservables
				.observeValue(self,
						"info.indicatorParams.probLinesConditionalOnlyEnabled");
		bindingContext
				.bindValue(
						btnConditionalProbabilitiesLinesObserveSelectionObserveWidget,
						getInfoIndicatorParamsprobLinesConditionalOnlyEnabledObserveValue,
						null, null);
		//
		IObservableValue btnUseProbabilityObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(btnUseProbability);
		IObservableValue btnProbabilityDistributionSelectionObserveValue = SWTObservables
				.observeSelection(btnProbabilityDistribution);
		bindingContext.bindValue(btnUseProbabilityObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue textPercentValueObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(textPercentValue);
		bindingContext.bindValue(textPercentValueObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue lblValueObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(lblValue);
		bindingContext.bindValue(lblValueObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue textMinMatchesObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(textMinMatches);
		bindingContext.bindValue(textMinMatchesObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue lblMinMatchesObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(lblMinMatches);
		bindingContext.bindValue(lblMinMatchesObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue btnConditionalProbabilitiesLinesObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(btnConditionalProbabilitiesLines);
		bindingContext.bindValue(
				btnConditionalProbabilitiesLinesObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		IObservableValue comboObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(probDistributionCombo);
		bindingContext.bindValue(comboObserveEnabledObserveWidget,
				btnProbabilityDistributionSelectionObserveValue, null, null);
		//
		return bindingContext;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final ScrolledComposite scrolledComposite;
	private final CTabFolder _tabFolder;
	private final CTabItem _tabItem;
	private final CTabItem _tabItem_1;
	private final Composite _composite;

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

}
