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
import java.util.EnumSet;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.interfaces.trading.ComputationType;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.interfaces.trading.Configuration.SCMode;
import com.mfg.interfaces.trading.RefType;
import com.mfg.utils.StepDefinition;
import com.mfg.widget.interfaces.IProbabilitiesSettingsContainer;

/**
 * @author arian
 * 
 */
public class ProbabilitiesSettingsComposite extends Composite {

	private DataBindingContext m_bindingContext;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Spinner pattCalcScale;
	Spinner scalesNumber;
	private Button multiScale;
	private Spinner intervals0;
	private IProbabilitiesSettingsContainer container;
	private ConfigurationAdapter configAdapter = new ConfigurationAdapter();
	private Label flabel_7;
	private Combo fcomboCalcType;
	private Label flabel_9;
	private Label flabel_8;
	private Spinner fspinnerTicks;
	private Spinner fspinnerMult;
	RefType[] typeItems;
	ComputationType[] computationTypeItems;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProbabilitiesSettingsComposite(Composite parent, int style,
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

		fcombo = new Combo(composite, SWT.NONE);
		fcombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(fcombo);
		toolkit.paintBordersFor(fcombo);
		fcombo.setItems(getTypeItems());

		flabel_7 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_7, true, true);
		flabel_7.setText("Target Type");

		fcomboCalcType = new Combo(composite, SWT.NONE);
		fcomboCalcType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		toolkit.adapt(fcomboCalcType);
		toolkit.paintBordersFor(fcomboCalcType);
		fcomboCalcType.setItems(getComputationTypeItems());

		btnPriceClusters = new Button(composite, SWT.CHECK);
		toolkit.adapt(btnPriceClusters, true, true);
		btnPriceClusters.setText("Price Clusters");

		priceClusters = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(priceClusters);
		toolkit.paintBordersFor(priceClusters);

		btnTimeClusters = new Button(composite, SWT.CHECK);
		toolkit.adapt(btnTimeClusters, true, true);
		btnTimeClusters.setText("Time Clusters");

		timeClusters = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(timeClusters);
		toolkit.paintBordersFor(timeClusters);

		toolkit.createLabel(composite, "Patterns Calculation Scale", SWT.NONE);

		pattCalcScale = new Spinner(composite, SWT.BORDER);
		pattCalcScale.setMinimum(1);
		pattCalcScale.setSelection(3);
		toolkit.adapt(pattCalcScale);
		toolkit.paintBordersFor(pattCalcScale);

		flabel_4 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_4, true, true);
		flabel_4.setText("Start Scale");

		fspinner = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(fspinner);
		toolkit.paintBordersFor(fspinner);

		flabel_5 = new Label(composite, SWT.NONE);
		toolkit.adapt(flabel_5, true, true);
		flabel_5.setText("End Scale");

		fspinner_1 = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(fspinner_1);
		toolkit.paintBordersFor(fspinner_1);

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
		ratio0.setText("Sw0'/Sw-1");
		toolkit.adapt(ratio0, true, true);

		intervals0 = new Spinner(composite_3, SWT.BORDER);
		intervals0.setMinimum(2);
		intervals0.setSelection(2);
		intervals0.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(intervals0);
		toolkit.paintBordersFor(intervals0);

		ratio1 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(ratio1, true, true);
		ratio1.setText("Sw-1/Sw-2");

		intervals1 = new Spinner(composite_3, SWT.BORDER);
		intervals1.setMinimum(2);
		intervals1.setSelection(2);
		toolkit.adapt(intervals1);
		toolkit.paintBordersFor(intervals1);
		ll = new Label(composite_3, SWT.NONE);

		ratio2 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(ratio2, true, true);
		ratio2.setText("Sw-2/Sw-3");

		intervals2 = new Spinner(composite_3, SWT.BORDER);
		intervals2.setMinimum(2);
		intervals2.setSelection(2);
		toolkit.adapt(intervals2);
		toolkit.paintBordersFor(intervals2);
		ll = new Label(composite_3, SWT.NONE);

		ratio3 = new Button(composite_3, SWT.CHECK);
		toolkit.adapt(ratio3, true, true);
		ratio3.setText("Sw-3/Sw-4");

		intervals3 = new Spinner(composite_3, SWT.BORDER);
		intervals3.setMinimum(2);
		intervals3.setSelection(2);
		toolkit.adapt(intervals3);
		toolkit.paintBordersFor(intervals3);
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
		multiScale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 5, 1));
		toolkit.adapt(multiScale, true, true);
		multiScale.setText("Multiple Scales");

		flabel = new Label(composite_2, SWT.NONE);
		toolkit.adapt(flabel, true, true);
		flabel.setText("Scales Number");
		ll = new Label(composite_2, SWT.NONE);

		scalesNumber = new Spinner(composite_2, SWT.BORDER);
		scalesNumber.setMinimum(2);
		scalesNumber.setSelection(2);
		scalesNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.adapt(scalesNumber);
		toolkit.paintBordersFor(scalesNumber);
		ll = new Label(composite_2, SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		toolkit.createLabel(composite_2, "Clusters Size", SWT.NONE);
		ll = new Label(composite_2, SWT.NONE);

		clusterSize = new Spinner(composite_2, SWT.BORDER);
		clusterSize.setMinimum(2);
		clusterSize.setSelection(2);
		toolkit.adapt(clusterSize);
		toolkit.paintBordersFor(clusterSize);
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

		loggingchk = new Button(fcomposite, SWT.CHECK);
		toolkit.adapt(loggingchk, true, true);
		loggingchk.setText("Logging");
		ll = new Label(fcomposite, SWT.NONE);
		toolkit.createLabel(fcomposite, "Bounds Step", SWT.NONE);

		boundsStep = new Text(fcomposite, SWT.BORDER);
		boundsStep.setText("0.01");
		toolkit.adapt(boundsStep, true, true);
		toolkit.createLabel(fcomposite, "Targets Step", SWT.NONE);

		targetsStep = new Text(fcomposite, SWT.BORDER);
		targetsStep.setText("0.1");
		toolkit.adapt(targetsStep, true, true);

		toolkit.createLabel(fcomposite, "Min Matches %", SWT.NONE);

		minMatchesPercent = new Text(fcomposite, SWT.BORDER);
		minMatchesPercent.setText("0.5");
		GridData gd_minMatchesPercent = new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1);
		gd_minMatchesPercent.widthHint = 80;
		minMatchesPercent.setLayoutData(gd_minMatchesPercent);
		toolkit.adapt(minMatchesPercent, true, true);

		flabel_1 = new Label(fcomposite, SWT.NONE);
		toolkit.adapt(flabel_1, true, true);
		flabel_1.setText("SC Filter");

		scFilter = new Combo(fcomposite, SWT.NONE);
		scFilter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(scFilter);
		toolkit.paintBordersFor(scFilter);
		scFilter.setItems(getItems());

		flabel_9 = new Label(fcomposite, SWT.NONE);
		toolkit.adapt(flabel_9, true, true);
		flabel_9.setText("Start Targets Ticks#");

		fspinnerTicks = new Spinner(fcomposite, SWT.BORDER);
		toolkit.adapt(fspinnerTicks);
		toolkit.paintBordersFor(fspinnerTicks);

		flabel_8 = new Label(fcomposite, SWT.NONE);
		toolkit.adapt(flabel_8, true, true);
		flabel_8.setText("Price Multiplier for Targets");

		fspinnerMult = new Spinner(fcomposite, SWT.BORDER);
		toolkit.adapt(fspinnerMult);
		toolkit.paintBordersFor(fspinnerMult);

		m_bindingContext = initDataBindings();
	}

	private static String[] getItems() {
		SCMode[] t = getSCValues();
		String[] res = new String[t.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = t[i].toString();
		}
		return res;
	}

	private String[] getTypeItems() {
		typeItems = EnumSet
				.of(RefType.Swing0, RefType.Target0, RefType.Target2).toArray(
						new RefType[] {});
		String[] res = new String[typeItems.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = typeItems[i].toRString();
		}
		return res;
	}

	private String[] getComputationTypeItems() {
		computationTypeItems = EnumSet.of(ComputationType.Sm1Ratio,
				ComputationType.S1stRatio, ComputationType.S2ndTicks).toArray(
				new ComputationType[] {});
		String[] res = new String[computationTypeItems.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = computationTypeItems[i].toRString();
		}
		return res;
	}

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

		public int getScalesNumber() {
			return getConfiguration().getDepth() + 1;
		}

		public boolean isLogging() {
			return getConfiguration().isLogging();
		}

		public void setLogging(boolean aLogging) {
			getConfiguration().setLogging(aLogging);
		}

		public void setScalesNumber(int aScalesNumber) {
			getConfiguration().setDepth(aScalesNumber - 1);
		}

		public double getBoundsStep() {
			return getConfiguration().getIntervalsStep().getStepDouble();
		}

		public void setBoundsStep(double aBoundsStep) {
			getConfiguration()
					.setIntervalsStep(new StepDefinition(aBoundsStep));
		}

		public double getTargetsStep() {
			return getConfiguration().getTargetStep().getStepDouble();
		}

		public void setTargetsStep(double aTargetsStep) {
			getConfiguration().setTargetStep(new StepDefinition(aTargetsStep));
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

		public int getStartScale() {
			return getConfiguration().getStartScale();
		}

		public void setStartScale(int aStartScale) {
			getConfiguration().setStartScale(aStartScale);
		}

		public int getEndScale() {
			return getConfiguration().getEndScale();
		}

		public void setEndScale(int aEndScale) {
			getConfiguration().setEndScale(aEndScale);
		}

		public int getMinScale() {
			return getConfiguration().getMinScale();
		}

		public int getMaxScale() {
			return getConfiguration().getMaxScale();
		}

		public String getScFilter() {
			return getConfiguration().getScMode().toString();
		}

		public void setScFilter(String aScFilter) {
			getConfiguration().setScMode(SCMode.valueOf(aScFilter));
		}

		public int getTargetType() {
			for (int i = 0; i < typeItems.length; i++) {
				RefType array_element = typeItems[i];
				if (array_element.equals(getConfiguration().getType()))
					return i;
			}
			return -1;
		}

		public void setTargetType(int aTargetType) {
			getConfiguration().setType(typeItems[aTargetType]);
		}

		public int getComputationType() {
			for (int i = 0; i < computationTypeItems.length; i++) {
				ComputationType array_element = computationTypeItems[i];
				if (array_element.equals(getConfiguration()
						.getComputationType()))
					return i;
			}
			return -1;
		}

		public void setComputationType(int aComputationType) {
			getConfiguration().setComputationType(
					computationTypeItems[aComputationType]);
		}

		public boolean isUsingPriceClusters() {
			return getConfiguration().isUsingPriceClusters();
		}

		public boolean isUsingTimeClusters() {
			return getConfiguration().isUsingTimeClusters();
		}

		public int getPriceClustersInSw0() {
			return getConfiguration().getPriceClustersInSw0();
		}

		public int getTimeClustersInSw0st() {
			return getConfiguration().getTimeClustersInSw0st();
		}

		public void setUsingPriceClusters(boolean usingPriceClusters) {
			getConfiguration().setUsingPriceClusters(usingPriceClusters);
		}

		public void setUsingTimeClusters(boolean usingTimeClusters) {
			getConfiguration().setUsingTimeClusters(usingTimeClusters);
		}

		public void setPriceClustersInSw0(int priceClustersInSw0) {
			getConfiguration().setPriceClustersInSw0(priceClustersInSw0);
		}

		public void setTimeClustersInSw0st(int timeClustersInSw0st) {
			getConfiguration().setTimeClustersInSw0st(timeClustersInSw0st);
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

		public int getTicksTargetStep() {
			return getConfiguration().getTicksTargetStep();
		}

		public int getScaleMultiplierTargetStep() {
			return getConfiguration().getScaleMultiplierTargetStep();
		}

		public void setTicksTargetStep(int ticksTargetStep) {
			getConfiguration().setTicksTargetStep(ticksTargetStep);
		}

		public void setScaleMultiplierTargetStep(int scaleMultiplierTargetStep) {
			getConfiguration().setScaleMultiplierTargetStep(
					scaleMultiplierTargetStep);
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
	private Spinner clusterSize;
	private Label flabel_1;
	private Combo scFilter;
	private Text boundsStep;
	private Text targetsStep;
	private Text minMatchesPercent;
	private Button ratio0;
	private Label flabel_2;
	private Label flabel_3;
	private Button ratio3;
	private Spinner intervals3;
	private Button ratio2;
	private Spinner intervals2;
	private Spinner intervals1;
	private Button ratio1;
	private DataBindingContext bindingContext;
	private Section fsection;
	private Composite fcomposite;
	private Button loggingchk;
	private Label flabel_4;
	private Label flabel_5;
	private Spinner fspinner;
	private Spinner fspinner_1;
	private Label flabel_6;
	private Combo fcombo;
	private Button btnPriceClusters;
	private Button btnTimeClusters;
	private Spinner priceClusters;
	private Spinner timeClusters;

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
		DataBindingContext aBindingContext = new DataBindingContext();
		//
		IObservableValue pattCalcScaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(pattCalcScale);
		IObservableValue getConfigurationAdapterPattCalcScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pattCalcScale");
		aBindingContext.bindValue(pattCalcScaleObserveSelectionObserveWidget,
				getConfigurationAdapterPattCalcScaleObserveValue, null, null);
		//
		IObservableValue boundsStepObserveTextObserveWidget = SWTObservables
				.observeText(boundsStep, SWT.Modify);
		IObservableValue getConfigurationAdapterBoundsStepObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "boundsStep");
		aBindingContext.bindValue(boundsStepObserveTextObserveWidget,
				getConfigurationAdapterBoundsStepObserveValue, null, null);
		//
		IObservableValue targetsStepObserveTextObserveWidget = SWTObservables
				.observeText(targetsStep, SWT.Modify);
		IObservableValue getConfigurationAdapterTargetsStepObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "targetsStep");
		aBindingContext.bindValue(targetsStepObserveTextObserveWidget,
				getConfigurationAdapterTargetsStepObserveValue, null, null);
		//
		IObservableValue minMatchesPercentObserveTextObserveWidget = SWTObservables
				.observeText(minMatchesPercent, SWT.Modify);
		IObservableValue getConfigurationAdapterMinMatchesPercObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "minMatchesPerc");
		aBindingContext.bindValue(minMatchesPercentObserveTextObserveWidget,
				getConfigurationAdapterMinMatchesPercObserveValue, null, null);
		//
		IObservableValue scFilterObserveSelectionObserveWidget = SWTObservables
				.observeSelection(scFilter);
		IObservableValue getConfigurationAdapterScFilterObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "scFilter");
		aBindingContext.bindValue(scFilterObserveSelectionObserveWidget,
				getConfigurationAdapterScFilterObserveValue, null, null);
		//
		IObservableValue multiScaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(multiScale);
		IObservableValue getConfigurationAdapterMultiScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "multiScale");
		aBindingContext.bindValue(multiScaleObserveSelectionObserveWidget,
				getConfigurationAdapterMultiScaleObserveValue, null, null);
		//
		IObservableValue scalesNumberObserveSelectionObserveWidget = SWTObservables
				.observeSelection(scalesNumber);
		IObservableValue getConfigurationAdapterScalesNumberObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "scalesNumber");
		aBindingContext.bindValue(scalesNumberObserveSelectionObserveWidget,
				getConfigurationAdapterScalesNumberObserveValue, null, null);
		//
		IObservableValue clusterSizeObserveSelectionObserveWidget = SWTObservables
				.observeSelection(clusterSize);
		IObservableValue getConfigurationAdapterClusterSizeObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "clusterSize");
		aBindingContext.bindValue(clusterSizeObserveSelectionObserveWidget,
				getConfigurationAdapterClusterSizeObserveValue, null, null);
		//
		IObservableValue ratio0ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio0);
		IObservableValue getConfigurationAdapterPair0ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair0");
		aBindingContext.bindValue(ratio0ObserveSelectionObserveWidget,
				getConfigurationAdapterPair0ObserveValue, null, null);
		//
		IObservableValue ratio1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio1);
		IObservableValue getConfigurationAdapterPair1ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair1");
		aBindingContext.bindValue(ratio1ObserveSelectionObserveWidget,
				getConfigurationAdapterPair1ObserveValue, null, null);
		//
		IObservableValue ratio2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio2);
		IObservableValue getConfigurationAdapterPair2ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair2");
		aBindingContext.bindValue(ratio2ObserveSelectionObserveWidget,
				getConfigurationAdapterPair2ObserveValue, null, null);
		//
		IObservableValue ratio3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(ratio3);
		IObservableValue getConfigurationAdapterPair3ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "pair3");
		aBindingContext.bindValue(ratio3ObserveSelectionObserveWidget,
				getConfigurationAdapterPair3ObserveValue, null, null);
		//
		IObservableValue intervals0ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(intervals0);
		IObservableValue getConfigurationAdapterIntervals0ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals0");
		aBindingContext.bindValue(intervals0ObserveSelectionObserveWidget,
				getConfigurationAdapterIntervals0ObserveValue, null, null);
		//
		IObservableValue intervals1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(intervals1);
		IObservableValue getConfigurationAdapterIntervals1ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals1");
		aBindingContext.bindValue(intervals1ObserveSelectionObserveWidget,
				getConfigurationAdapterIntervals1ObserveValue, null, null);
		//
		IObservableValue intervals2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(intervals2);
		IObservableValue getConfigurationAdapterIntervals2ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals2");
		aBindingContext.bindValue(intervals2ObserveSelectionObserveWidget,
				getConfigurationAdapterIntervals2ObserveValue, null, null);
		//
		IObservableValue intervals3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(intervals3);
		IObservableValue getConfigurationAdapterIntervals3ObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "intervals3");
		aBindingContext.bindValue(intervals3ObserveSelectionObserveWidget,
				getConfigurationAdapterIntervals3ObserveValue, null, null);
		//
		IObservableValue loggingchkObserveSelectionObserveWidget = SWTObservables
				.observeSelection(loggingchk);
		IObservableValue getConfigurationAdapterLoggingObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "logging");
		aBindingContext.bindValue(loggingchkObserveSelectionObserveWidget,
				getConfigurationAdapterLoggingObserveValue, null, null);
		//
		IObservableValue scalesNumberObserveEnabledObserveWidget = SWTObservables
				.observeEnabled(scalesNumber);
		aBindingContext.bindValue(scalesNumberObserveEnabledObserveWidget,
				getConfigurationAdapterMultiScaleObserveValue, null, null);
		//
		IObservableValue fspinnerObserveSelectionObserveWidget = SWTObservables
				.observeSelection(fspinner);
		IObservableValue getConfigurationAdapterStartScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "startScale");
		aBindingContext.bindValue(fspinnerObserveSelectionObserveWidget,
				getConfigurationAdapterStartScaleObserveValue, null, null);
		//
		IObservableValue fspinner_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(fspinner_1);
		IObservableValue getConfigurationAdapterEndScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "endScale");
		aBindingContext.bindValue(fspinner_1ObserveSelectionObserveWidget,
				getConfigurationAdapterEndScaleObserveValue, null, null);
		//
		IObservableValue fspinner_1ObserveMinObserveWidget = SWTObservables
				.observeMin(fspinner_1);
		aBindingContext.bindValue(fspinner_1ObserveMinObserveWidget,
				getConfigurationAdapterStartScaleObserveValue, null, null);
		//
		IObservableValue fspinnerObserveMaxObserveWidget = SWTObservables
				.observeMax(fspinner);
		aBindingContext.bindValue(fspinnerObserveMaxObserveWidget,
				getConfigurationAdapterEndScaleObserveValue, null, null);
		//
		IObservableValue fspinnerObserveMinObserveWidget = SWTObservables
				.observeMin(fspinner);
		IObservableValue getConfigurationAdapterMinScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "minScale");
		aBindingContext.bindValue(fspinnerObserveMinObserveWidget,
				getConfigurationAdapterMinScaleObserveValue, null, null);
		//
		IObservableValue fspinner_1ObserveMaxObserveWidget = SWTObservables
				.observeMax(fspinner_1);
		IObservableValue getConfigurationAdapterMaxScaleObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "maxScale");
		aBindingContext.bindValue(fspinner_1ObserveMaxObserveWidget,
				getConfigurationAdapterMaxScaleObserveValue, null, null);
		//
		IObservableValue pattCalcScaleObserveMinObserveWidget = SWTObservables
				.observeMin(pattCalcScale);
		aBindingContext.bindValue(pattCalcScaleObserveMinObserveWidget,
				getConfigurationAdapterStartScaleObserveValue, null, null);
		//
		IObservableValue pattCalcScaleObserveMaxObserveWidget = SWTObservables
				.observeMax(pattCalcScale);
		aBindingContext.bindValue(pattCalcScaleObserveMaxObserveWidget,
				getConfigurationAdapterEndScaleObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnPriceClustersObserveWidget = WidgetProperties
				.selection().observe(btnPriceClusters);
		IObservableValue usingPriceClustersGetConfigurationAdapterObserveValue = PojoProperties
				.value("usingPriceClusters").observe(getConfigurationAdapter());
		aBindingContext.bindValue(
				observeSelectionBtnPriceClustersObserveWidget,
				usingPriceClustersGetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeSelectionBtnTimeClustersObserveWidget = WidgetProperties
				.selection().observe(btnTimeClusters);
		IObservableValue usingTimeClustersGetConfigurationAdapterObserveValue = PojoProperties
				.value("usingTimeClusters").observe(getConfigurationAdapter());
		aBindingContext.bindValue(observeSelectionBtnTimeClustersObserveWidget,
				usingTimeClustersGetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeSelectionPriceClustersObserveWidget = WidgetProperties
				.selection().observe(priceClusters);
		IObservableValue priceClustersInSw0GetConfigurationAdapterObserveValue = PojoProperties
				.value("priceClustersInSw0").observe(getConfigurationAdapter());
		aBindingContext.bindValue(observeSelectionPriceClustersObserveWidget,
				priceClustersInSw0GetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeEnabledPriceClustersObserveWidget = WidgetProperties
				.enabled().observe(priceClusters);
		aBindingContext.bindValue(observeEnabledPriceClustersObserveWidget,
				usingPriceClustersGetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeSelectionTimeClustersObserveWidget = WidgetProperties
				.selection().observe(timeClusters);
		IObservableValue timeClustersInSw0stGetConfigurationAdapterObserveValue = PojoProperties
				.value("timeClustersInSw0st")
				.observe(getConfigurationAdapter());
		aBindingContext.bindValue(observeSelectionTimeClustersObserveWidget,
				timeClustersInSw0stGetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeEnabledTimeClustersObserveWidget = WidgetProperties
				.enabled().observe(timeClusters);
		aBindingContext.bindValue(observeEnabledTimeClustersObserveWidget,
				usingTimeClustersGetConfigurationAdapterObserveValue, null,
				null);
		//
		IObservableValue observeEnabledIntervals0ObserveWidget = WidgetProperties
				.enabled().observe(intervals0);
		aBindingContext.bindValue(observeEnabledIntervals0ObserveWidget,
				getConfigurationAdapterPair0ObserveValue, null, null);
		//
		IObservableValue observeEnabledIntervals1ObserveWidget = WidgetProperties
				.enabled().observe(intervals1);
		aBindingContext.bindValue(observeEnabledIntervals1ObserveWidget,
				getConfigurationAdapterPair1ObserveValue, null, null);
		//
		IObservableValue observeEnabledIntervals2ObserveWidget = WidgetProperties
				.enabled().observe(intervals2);
		aBindingContext.bindValue(observeEnabledIntervals2ObserveWidget,
				getConfigurationAdapterPair2ObserveValue, null, null);
		//
		IObservableValue observeEnabledIntervals3ObserveWidget = WidgetProperties
				.enabled().observe(intervals3);
		aBindingContext.bindValue(observeEnabledIntervals3ObserveWidget,
				getConfigurationAdapterPair3ObserveValue, null, null);
		//
		IObservableValue observeSelectionFspinnerTicksObserveWidget = WidgetProperties
				.selection().observe(fspinnerTicks);
		IObservableValue ticksTargetStepGetConfigurationAdapterObserveValue = PojoProperties
				.value("ticksTargetStep").observe(getConfigurationAdapter());
		aBindingContext.bindValue(observeSelectionFspinnerTicksObserveWidget,
				ticksTargetStepGetConfigurationAdapterObserveValue, null, null);
		//
		IObservableValue observeSelectionFspinnerMultObserveWidget = WidgetProperties
				.selection().observe(fspinnerMult);
		IObservableValue scaleMultiplierTargetStepGetConfigurationAdapterObserveValue = PojoProperties
				.value("scaleMultiplierTargetStep").observe(
						getConfigurationAdapter());
		aBindingContext.bindValue(observeSelectionFspinnerMultObserveWidget,
				scaleMultiplierTargetStepGetConfigurationAdapterObserveValue,
				null, null);
		//
		IObservableValue observeSingleSelectionIndexFcomboObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(fcombo);
		IObservableValue getConfigurationAdapterTargetTypeObserveValue = PojoObservables
				.observeValue(getConfigurationAdapter(), "targetType");
		aBindingContext.bindValue(
				observeSingleSelectionIndexFcomboObserveWidget,
				getConfigurationAdapterTargetTypeObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexFcomboCalcTypeObserveWidget = WidgetProperties
				.singleSelectionIndex().observe(fcomboCalcType);
		IObservableValue computationTypeGetConfigurationAdapterObserveValue = PojoProperties
				.value("computationType").observe(getConfigurationAdapter());
		aBindingContext.bindValue(
				observeSingleSelectionIndexFcomboCalcTypeObserveWidget,
				computationTypeGetConfigurationAdapterObserveValue, null, null);
		//
		return aBindingContext;
	}
}
