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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.interfaces.trading.ComputationType;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.interfaces.trading.Configuration.SCMode;
import com.mfg.widget.interfaces.IProbabilitiesSettingsContainer;

/**
 * @author arian
 * 
 */
public class ProbabilitiesSettingsROComposite extends Composite {

	private DataBindingContext m_bindingContext;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text pattCalcScale;
	Text scalesNumber;
	private Button multiScale;
	private Text intervals0;
	private IProbabilitiesSettingsContainer container;
	private ConfigurationAdapter configAdapter = new ConfigurationAdapter();
	private Label flabel_7;
	private Text ftext_3;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProbabilitiesSettingsROComposite(Composite parent, int style,
			IProbabilitiesSettingsContainer aContainer) {
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
		sctnIndicatorLines.setText("General");
		sctnIndicatorLines.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnIndicatorLines,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnIndicatorLines.setClient(composite);
		composite.setLayout(new GridLayout(2, false));

		flabel_6 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_6, true, true);
		flabel_6.setText("End Calculation");

		ftext_2 = new Text(composite, SWT.BORDER);
		ftext_2.setEditable(false);
		ftext_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(ftext_2, true, true);

		flabel_7 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_7, true, true);
		flabel_7.setText("Target Type");

		ftext_3 = new Text(composite, SWT.BORDER);
		ftext_3.setEditable(false);
		ftext_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(ftext_3, true, true);

		toolkit.createLabel(composite, "Patterns Calculation Scale", SWT.NONE);

		pattCalcScale = new Text(composite, SWT.BORDER);
		pattCalcScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		pattCalcScale.setEditable(false);
		pattCalcScale.setSelection(3);

		toolkit.adapt(pattCalcScale, true, true);

		flabel_4 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_4, true, true);
		flabel_4.setText("Start Scale");

		ftext = new Text(composite, SWT.BORDER);
		ftext.setEditable(false);
		ftext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(ftext, true, true);

		flabel_5 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_5, true, true);
		flabel_5.setText("End Scale");

		ftext_1 = new Text(composite, SWT.BORDER);
		ftext_1.setEditable(false);
		ftext_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(ftext_1, true, true);

		toolkit.createLabel(composite, "Min Matches %", SWT.NONE);

		minMatchesPercent = new Text(composite, SWT.BORDER);
		minMatchesPercent.setEditable(false);
		minMatchesPercent.setText("0.5");
		GridData gd_minMatchesPercent = new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1);
		gd_minMatchesPercent.widthHint = 80;
		minMatchesPercent.setLayoutData(gd_minMatchesPercent);
		toolkit.adapt(minMatchesPercent, true, true);

		flabel_1 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_1, true, true);
		flabel_1.setText("SC Filter");

		scFilter = new Text(composite, SWT.NONE);
		scFilter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));

		Section sctnNegativeChannels = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNegativeChannels.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, false, 1, 1));
		toolkit.paintBordersFor(sctnNegativeChannels);
		sctnNegativeChannels.setText("Patterns Generation");
		sctnNegativeChannels.setExpanded(true);

		Composite composite_3 = toolkit.createComposite(sctnNegativeChannels,
				SWT.NONE);
		toolkit.paintBordersFor(composite_3);
		sctnNegativeChannels.setClient(composite_3);
		composite_3.setLayout(new GridLayout(3, false));

		flabel_2 = new Label(composite_3, SWT.NONE);
		toolkit.adapt(flabel_2, true, true);
		flabel_2.setText("Ratio");

		flabel_3 = new Label(composite_3, SWT.NONE);
		toolkit.adapt(flabel_3, true, true);
		flabel_3.setText("Intervals");
		@SuppressWarnings("unused")
		Label ll = new Label(composite_3, SWT.NONE);

		ratio0 = new Button(composite_3, SWT.CHECK);
		ratio0.setEnabled(false);
		ratio0.setText("Sw0'/Sw-1");
		toolkit.adapt(ratio0, true, true);

		intervals0 = new Text(composite_3, SWT.BORDER);
		intervals0.setEditable(false);
		intervals0.setSelection(2);
		intervals0.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		toolkit.adapt(intervals0, true, true);

		ratio1 = new Button(composite_3, SWT.CHECK);
		ratio1.setEnabled(false);
		toolkit.adapt(ratio1, true, true);
		ratio1.setText("Sw-1/Sw-2");

		intervals1 = new Text(composite_3, SWT.BORDER);
		intervals1.setEditable(false);
		intervals1.setSelection(2);
		ll = new Label(composite_3, SWT.NONE);
		toolkit.adapt(intervals1, true, true);

		ratio2 = new Button(composite_3, SWT.CHECK);
		ratio2.setEnabled(false);
		toolkit.adapt(ratio2, true, true);
		ratio2.setText("Sw-2/Sw-3");

		intervals2 = new Text(composite_3, SWT.BORDER);
		intervals2.setEditable(false);
		intervals2.setSelection(2);
		ll = new Label(composite_3, SWT.NONE);
		toolkit.adapt(intervals2, true, true);

		ratio3 = new Button(composite_3, SWT.CHECK);
		ratio3.setEnabled(false);
		toolkit.adapt(ratio3, true, true);
		ratio3.setText("Sw-3/Sw-4");

		intervals3 = new Text(composite_3, SWT.BORDER);
		intervals3.setEditable(false);
		intervals3.setSelection(2);
		toolkit.adapt(intervals3, true, true);

		ll = new Label(composite_3, SWT.NONE);
		Composite composite_5 = toolkit.createCompositeSeparator(composite_1);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		gd_composite_5.heightHint = 2;
		composite_5.setLayoutData(gd_composite_5);
		toolkit.paintBordersFor(composite_5);
		ll = new Label(composite_1, SWT.NONE);
		ll = new Label(composite_1, SWT.NONE);

		Section sctnNewSection = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText("Multi Scales");
		sctnNewSection.setExpanded(true);

		Composite composite_2 = toolkit.createComposite(sctnNewSection,
				SWT.NONE);
		toolkit.paintBordersFor(composite_2);
		sctnNewSection.setClient(composite_2);
		composite_2.setLayout(new GridLayout(5, false));
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		multiScale = new Button(composite_2, SWT.CHECK);
		multiScale.setEnabled(false);
		multiScale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 5, 1));
		toolkit.adapt(multiScale, true, true);
		multiScale.setText("Multiple Scales");

		flabel = new Label(composite_2, SWT.NONE);
		toolkit.adapt(flabel, true, true);
		flabel.setText("Scales Number");
		ll = new Label(composite_2, SWT.NONE);

		scalesNumber = new Text(composite_2, SWT.BORDER);
		scalesNumber.setEditable(false);
		scalesNumber.setSelection(2);
		scalesNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(scalesNumber, true, true);

		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		toolkit.createLabel(composite_2, "Clusters Size", SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		clusterSize = new Text(composite_2, SWT.BORDER);
		clusterSize.setEditable(false);
		clusterSize.setSelection(2);
		toolkit.adapt(clusterSize, true, true);
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		fsection = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		fsection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.paintBordersFor(fsection);
		fsection.setText("Others");
		fsection.setExpanded(true);

		fcomposite = new Composite(fsection, SWT.NONE);
		toolkit.adapt(fcomposite);
		toolkit.paintBordersFor(fcomposite);
		fsection.setClient(fcomposite);
		fcomposite.setLayout(new GridLayout(2, false));

		toolkit.createLabel(fcomposite, "Bounds Step", SWT.NONE);

		boundsStep = new Text(fcomposite, SWT.BORDER);
		boundsStep.setEditable(false);
		boundsStep.setText("0.01");
		toolkit.adapt(boundsStep, true, true);

		toolkit.createLabel(fcomposite, "Targets Step", SWT.NONE);

		targetsStep = new Text(fcomposite, SWT.BORDER);
		targetsStep.setEditable(false);
		targetsStep.setText("0.1");
		toolkit.adapt(targetsStep, true, true);

		m_bindingContext = initDataBindings();
	}

	/*
	 * private String[] getItems() { SCMode[] t = getSCValues(); String[] res =
	 * new String[t.length]; for (int i = 0; i < res.length; i++) { res[i] =
	 * t[i].toString(); } return res; }
	 */

	public static SCMode[] getSCValues() {
		return SCMode.values();
	}

	public static SCMode getSCValuesData() {
		return SCMode.NoFilter;
	}

	public Configuration getConfiguration() {
		return container.getConfiguration();
	}

	public ConfigurationAdapter getConfigurationAdapter() {
		return configAdapter;
	}

	public class ConfigurationAdapter {

		public int getPattCalcScale() {
			return getConfiguration().getDefaultScale();
		}

		public void setPattCalcScale(int aPattCalcScale) {
			getConfiguration().setDefaultScale(aPattCalcScale);
		}

		public int getStartScale() {
			return getConfiguration().getStartScale();
		}

		public int getEndScale() {
			return getConfiguration().getEndScale();
		}

		public int getScalesNumber() {
			return getConfiguration().getDepth() + 1;
		}

		public void setScalesNumber(int aScalesNumber) {
			getConfiguration().setDepth(aScalesNumber - 1);
		}

		public double getBoundsStep() {
			return getConfiguration().getIntervalsStep().getStepDouble();
		}

		public void setBoundsStep(double aBoundsStep) {
			getConfiguration().getIntervalsStep().setStepDouble(aBoundsStep);
		}

		public double getTargetsStep() {
			return getConfiguration().getTargetStep().getStepDouble();
		}

		public void setTargetsStep(double aTargetsStep) {
			getConfiguration().getTargetStep().setStepDouble(aTargetsStep);
		}

		public double getMinMatchesPerc() {
			return getConfiguration().getMinMatchesPercent();
		}

		public void setMinMatchesPerc(double aMinMatchesPerc) {
			getConfiguration().setMinMatchesPercent(aMinMatchesPerc);
		}

		public boolean isMultiScale() {
			return getConfiguration().isMultiscale();
		}

		public void setMultiScale(boolean aMultiScale) {
			getConfiguration().setMultiscale(aMultiScale);
			if (!aMultiScale) {
				setScalesNumber(1);
			}
			scalesNumber.setEnabled(aMultiScale);
		}

		public int getClusterSize() {
			return getConfiguration().getClusterSize();
		}

		public void setClusterSize(int aClusterSize) {
			getConfiguration().setClusterSize(aClusterSize);
		}

		public String getScFilter() {
			return getConfiguration().getScMode().toString();
		}

		public String getTargetType() {
			return getConfiguration().getType().toRString();
		}

		public String getComputationType() {
			return getConfiguration().getComputationType().toString();
		}

		public void setComputationType(String aComputationType) {
			getConfiguration().setComputationType(
					ComputationType.valueOf(aComputationType));
		}

		public void setScFilter(String aScFilter) {
			getConfiguration().setScMode(SCMode.valueOf(aScFilter));
		}

		public boolean isPair0() {
			return getConfiguration().isRatioIncluded(0);
		}

		public void setPair0(boolean aPair0) {
			getConfiguration().setRatioIncluded(0, aPair0);
		}

		public boolean isPair1() {
			return getConfiguration().isRatioIncluded(1);
		}

		public void setPair1(boolean aPair1) {
			getConfiguration().setRatioIncluded(1, aPair1);
		}

		public boolean isPair2() {
			return getConfiguration().isRatioIncluded(2);
		}

		public void setPair2(boolean aPair2) {
			getConfiguration().setRatioIncluded(2, aPair2);
		}

		public boolean isPair3() {
			return getConfiguration().isRatioIncluded(3);
		}

		public void setPair3(boolean aPair3) {
			getConfiguration().setRatioIncluded(3, aPair3);
		}

		public int getIntervals0() {
			return getConfiguration().getIntervals(0);
		}

		public void setIntervals0(int aIntervals0) {
			getConfiguration().setIntervals(0, aIntervals0);
		}

		public int getIntervals1() {
			return getConfiguration().getIntervals(1);
		}

		public void setIntervals1(int aIntervals1) {
			getConfiguration().setIntervals(1, aIntervals1);
		}

		public int getIntervals2() {
			return getConfiguration().getIntervals(2);
		}

		public void setIntervals2(int aIntervals2) {
			getConfiguration().setIntervals(2, aIntervals2);
		}

		public int getIntervals3() {
			return getConfiguration().getIntervals(3);
		}

		public void setIntervals3(int aIntervals3) {
			getConfiguration().setIntervals(3, aIntervals3);
		}

	}

	/**
	 * @param aConfig
	 */
	public void setConfiguration(Configuration aConfig) {
		// Adding a comment to avoid empty block warning.
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

	public Color getBlueColor() {
		return toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
	}

	// private void updateConfig() {
	// config.setDefaultScale(Integer.parseInt(pattCalcScale.getText()));
	// config.setIntervalsStep(new
	// StepDefinition(Double.parseDouble(boundsStep.getText())));
	// config.setTargetStep(new
	// StepDefinition(Double.parseDouble(targetsStep.getText())));
	// // boolean ms = multiScale.;
	// // config.setMultiscale(ms);
	// // config.setType((RefTypeUI) comboBox.getSelectedItem());
	// // config.setDepth(ms ? (Integer.parseInt(scalesNumber.getText()) - 1) :
	// 0);
	// config.setMinMatchesPercent(new Double(minMatchesPercent.getText()));
	// config.setClusterSize(Integer.parseInt(clusterSize.getText()));
	// config.setScMode((SCMode) scFilter.getData());
	// }

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	private Label flabel;
	private Text clusterSize;
	private Label flabel_1;
	private Text scFilter;
	private Text boundsStep;
	private Text targetsStep;
	private Text minMatchesPercent;
	private Button ratio0;
	private Label flabel_2;
	private Label flabel_3;
	private Button ratio3;
	private Text intervals3;
	private Button ratio2;
	private Text intervals2;
	private Text intervals1;
	private Button ratio1;
	private DataBindingContext bindingContext;
	private Section fsection;
	private Composite fcomposite;
	private Label flabel_4;
	private Label flabel_5;
	private Text ftext;
	private Text ftext_1;
	private Label flabel_6;
	private Text ftext_2;

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
		bindingContext = new DataBindingContext();
		//
		IObservableValue boundsStepObserveTextObserveWidget = SWTObservables
				.observeText(boundsStep, SWT.Modify);
		IObservableValue getConfigurationAdapterBoundsStepObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "boundsStep");
		bindingContext.bindValue(boundsStepObserveTextObserveWidget,
				getConfigurationAdapterBoundsStepObserveValue, null, null);
		//
		IObservableValue targetsStepObserveTextObserveWidget = SWTObservables
				.observeText(targetsStep, SWT.Modify);
		IObservableValue getConfigurationAdapterTargetsStepObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "targetsStep");
		bindingContext.bindValue(targetsStepObserveTextObserveWidget,
				getConfigurationAdapterTargetsStepObserveValue, null, null);
		//
		IObservableValue minMatchesPercentObserveTextObserveWidget = SWTObservables
				.observeText(minMatchesPercent, SWT.Modify);
		IObservableValue getConfigurationAdapterMinMatchesPercObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "minMatchesPerc");
		bindingContext.bindValue(minMatchesPercentObserveTextObserveWidget,
				getConfigurationAdapterMinMatchesPercObserveValue, null, null);
		//
		IObservableValue multiScaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(multiScale);
		IObservableValue getConfigurationAdapterMultiScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "multiScale");
		bindingContext.bindValue(multiScaleObserveSelectionObserveWidget,
				getConfigurationAdapterMultiScaleObserveValue, null, null);
		//
		IObservableValue ratio0ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio0);
		IObservableValue getConfigurationAdapterPair0ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair0");
		bindingContext.bindValue(ratio0ObserveSelectionObserveWidget,
				getConfigurationAdapterPair0ObserveValue, null, null);
		//
		IObservableValue ratio1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio1);
		IObservableValue getConfigurationAdapterPair1ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair1");
		bindingContext.bindValue(ratio1ObserveSelectionObserveWidget,
				getConfigurationAdapterPair1ObserveValue, null, null);
		//
		IObservableValue ratio2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio2);
		IObservableValue getConfigurationAdapterPair2ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair2");
		bindingContext.bindValue(ratio2ObserveSelectionObserveWidget,
				getConfigurationAdapterPair2ObserveValue, null, null);
		//
		IObservableValue ratio3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio3);
		IObservableValue getConfigurationAdapterPair3ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair3");
		bindingContext.bindValue(ratio3ObserveSelectionObserveWidget,
				getConfigurationAdapterPair3ObserveValue, null, null);
		//
		IObservableValue clusterSizeObserveTextObserveWidget = SWTObservables
				.observeText(clusterSize, SWT.Modify);
		IObservableValue getConfigurationAdapterClusterSizeObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "clusterSize");
		bindingContext.bindValue(clusterSizeObserveTextObserveWidget,
				getConfigurationAdapterClusterSizeObserveValue, null, null);
		//
		IObservableValue scalesNumberObserveTextObserveWidget = SWTObservables
				.observeText(scalesNumber, SWT.Modify);
		IObservableValue getConfigurationAdapterScalesNumberObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "scalesNumber");
		bindingContext.bindValue(scalesNumberObserveTextObserveWidget,
				getConfigurationAdapterScalesNumberObserveValue, null, null);
		//
		IObservableValue pattCalcScaleObserveTextObserveWidget = SWTObservables
				.observeText(pattCalcScale, SWT.Modify);
		IObservableValue getConfigurationAdapterPattCalcScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pattCalcScale");
		bindingContext.bindValue(pattCalcScaleObserveTextObserveWidget,
				getConfigurationAdapterPattCalcScaleObserveValue, null, null);
		//
		IObservableValue scFilterObserveTextObserveWidget = SWTObservables
				.observeText(scFilter, SWT.Modify);
		IObservableValue getConfigurationAdapterScFilterObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "scFilter");
		bindingContext.bindValue(scFilterObserveTextObserveWidget,
				getConfigurationAdapterScFilterObserveValue, null, null);
		//
		IObservableValue intervals0ObserveTextObserveWidget = SWTObservables
				.observeText(intervals0, SWT.Modify);
		IObservableValue getConfigurationAdapterIntervals0ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals0");
		bindingContext.bindValue(intervals0ObserveTextObserveWidget,
				getConfigurationAdapterIntervals0ObserveValue, null, null);
		//
		IObservableValue intervals1ObserveTextObserveWidget = SWTObservables
				.observeText(intervals1, SWT.Modify);
		IObservableValue getConfigurationAdapterIntervals1ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals1");
		bindingContext.bindValue(intervals1ObserveTextObserveWidget,
				getConfigurationAdapterIntervals1ObserveValue, null, null);
		//
		IObservableValue intervals2ObserveTextObserveWidget = SWTObservables
				.observeText(intervals2, SWT.Modify);
		IObservableValue getConfigurationAdapterIntervals2ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals2");
		bindingContext.bindValue(intervals2ObserveTextObserveWidget,
				getConfigurationAdapterIntervals2ObserveValue, null, null);
		//
		IObservableValue intervals3ObserveTextObserveWidget = SWTObservables
				.observeText(intervals3, SWT.Modify);
		IObservableValue getConfigurationAdapterIntervals3ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals3");
		bindingContext.bindValue(intervals3ObserveTextObserveWidget,
				getConfigurationAdapterIntervals3ObserveValue, null, null);
		//
		IObservableValue ftextObserveTextObserveWidget = SWTObservables
				.observeText(ftext, SWT.Modify);
		IObservableValue getConfigurationAdapterStartScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "startScale");
		bindingContext.bindValue(ftextObserveTextObserveWidget,
				getConfigurationAdapterStartScaleObserveValue, null, null);
		//
		IObservableValue ftext_1ObserveTextObserveWidget = SWTObservables
				.observeText(ftext_1, SWT.Modify);
		IObservableValue getConfigurationAdapterEndScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "endScale");
		bindingContext.bindValue(ftext_1ObserveTextObserveWidget,
				getConfigurationAdapterEndScaleObserveValue, null, null);
		//
		IObservableValue ftext_2ObserveTextObserveWidget = SWTObservables
				.observeText(ftext_2, SWT.Modify);
		IObservableValue getConfigurationAdapterTargetTypeObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "targetType");
		bindingContext.bindValue(ftext_2ObserveTextObserveWidget,
				getConfigurationAdapterTargetTypeObserveValue, null, null);
		//
		IObservableValue observeTextFtext_3ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(ftext_3);
		IObservableValue computationTypeGetConfigurationAdapterObserveValue = PojoProperties
				.value("computationType").observe(getConfigurationAdapter());
		bindingContext.bindValue(observeTextFtext_3ObserveWidget,
				computationTypeGetConfigurationAdapterObserveValue, null, null);
		//
		return bindingContext;
	}
}
