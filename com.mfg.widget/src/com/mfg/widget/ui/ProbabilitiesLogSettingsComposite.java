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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.interfaces.probabilities.IProbabilitiesFilter.CNCDir;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter.ProbVer;
import com.mfg.interfaces.trading.Configuration.SCMode;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.ProbababilitiesFilter;

/**
 * @author gardero
 * 
 */
public class ProbabilitiesLogSettingsComposite extends Composite {

	DataBindingContext m_bindingContext;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Spinner scale;
	Spinner sctIndex;
	private PropertyChangeListener probListener;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProbabilitiesLogSettingsComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		getFilter().addPropertyChangeListener(
				probListener = new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent aEvt) {
						m_bindingContext.updateTargets();
					}
				});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_1 = new Composite(this, SWT.NONE);
		toolkit.adapt(composite_1);
		toolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		fsection = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		fsection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.paintBordersFor(fsection);
		fsection.setText("General");
		fsection.setExpanded(true);

		fcomposite = new Composite(fsection, SWT.NONE);
		toolkit.adapt(fcomposite);
		toolkit.paintBordersFor(fcomposite);
		fsection.setClient(fcomposite);
		fcomposite.setLayout(new GridLayout(2, false));

		Label lblNewLabel = toolkit.createLabel(fcomposite, "Scale", SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		scale = new Spinner(fcomposite, SWT.BORDER);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		scale.setSelection(3);
		toolkit.adapt(scale);
		toolkit.paintBordersFor(scale);

		flabel_1 = new Label(fcomposite, SWT.NONE);
		toolkit.adapt(flabel_1, true, true);
		flabel_1.setText("Prob Version");

		probVersion = new Combo(fcomposite, SWT.NONE);
		probVersion.setItems(filterAdapter.getVERS());
		toolkit.adapt(probVersion);
		toolkit.paintBordersFor(probVersion);
		@SuppressWarnings("unused")
		Label ll = new Label(composite_1, SWT.NONE);

		Section sctnIndicatorLines = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnIndicatorLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnIndicatorLines);
		sctnIndicatorLines.setText("Targets");
		sctnIndicatorLines.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnIndicatorLines,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnIndicatorLines.setClient(composite);
		composite.setLayout(new GridLayout(2, false));

		toolkit.createLabel(composite, "PID", SWT.NONE);

		pid = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(pid);
		toolkit.paintBordersFor(pid);

		toolkit.createLabel(composite, "CID", SWT.NONE);

		cid = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(cid);
		toolkit.paintBordersFor(cid);

		toolkit.createLabel(composite, "DIR", SWT.NONE);

		fcombo = new Combo(composite, SWT.NONE);
		fcombo.setItems(filterAdapter.getDIRS());
		fcombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(fcombo);
		toolkit.paintBordersFor(fcombo);

		allswings = new Button(composite, SWT.CHECK);
		toolkit.adapt(allswings, true, true);
		allswings.setText("All Swings and Targets");
		ll = new Label(composite, SWT.NONE);
		ll = new Label(composite_1, SWT.NONE);

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
		sctnNegativeChannels.setText("SC Touch");
		sctnNegativeChannels.setExpanded(true);

		Composite composite_3 = toolkit.createComposite(sctnNegativeChannels,
				SWT.NONE);
		toolkit.paintBordersFor(composite_3);
		sctnNegativeChannels.setClient(composite_3);
		composite_3.setLayout(new GridLayout(3, false));

		flabel_4 = new Label(composite_3, SWT.NONE);
		toolkit.adapt(flabel_4, true, true);
		flabel_4.setText("SCT index");

		sctIndex = new Spinner(composite_3, SWT.BORDER);
		sctIndex.setMinimum(0);
		sctIndex.setSelection(0);
		sctIndex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				2, 1));
		toolkit.adapt(sctIndex);
		toolkit.paintBordersFor(sctIndex);

		flabel_5 = new Label(composite_3, SWT.NONE);
		toolkit.adapt(flabel_5, true, true);
		flabel_5.setText("Base Scale Cluster");

		bsc = new Spinner(composite_3, SWT.BORDER);
		bsc.setMinimum(0);
		bsc.setSelection(0);
		toolkit.adapt(bsc);
		toolkit.paintBordersFor(bsc);
		ll = new Label(composite_3, SWT.NONE);
		ll = new Label(composite_1, SWT.NONE);
		m_bindingContext = initDataBindings();
	}

	// private String[] getItems() {
	// SCMode[] t = getSCValues();
	// String[] res = new String[t.length];
	// for (int i = 0; i < res.length; i++) {
	// res[i] = t[i].toString();
	// }
	// return res;
	// }

	public static SCMode[] getSCValues() {
		return SCMode.values();
	}

	public static SCMode getSCValuesData() {
		return SCMode.NoFilter;
	}

	public class FilterAdapter {

		public String[] getDIRS() {
			CNCDir[] vals = CNCDir.values();
			String[] res = new String[vals.length];
			int i = 0;
			for (CNCDir v : vals) {
				res[i++] = v.toString();
			}
			return res;
		}

		public String[] getVERS() {
			ProbVer[] vals = ProbVer.values();
			String[] res = new String[vals.length];
			int i = 0;
			for (ProbVer v : vals) {
				res[i++] = v.toString();
			}
			return res;
		}

		public int getScale() {
			return getFilter().getScale();
		}

		public void setScale(int aScale) {
			getFilter().setScale(aScale);
			getFilter().apply();
		}

		public int getPID() {
			return getFilter().getPID();
		}

		public void setPID(int aPID) {
			getFilter().setPID(aPID);
			getFilter().apply();
		}

		public int getCID() {
			return getFilter().getCID();
		}

		public void setCID(int aCID) {
			getFilter().setCID(aCID);
			getFilter().apply();
		}

		public String getDir() {
			return getFilter().getDir().toString();
		}

		public void setDir(String aDir) {
			getFilter().setDir(CNCDir.valueOf(aDir));
			getFilter().apply();
		}

		public boolean isAllswings() {
			return getFilter().isAllswings();
		}

		public void setAllswings(boolean aAllswings) {
			getFilter().setAllswings(aAllswings);
			fcombo.setEnabled(!aAllswings);
			cid.setEnabled(!aAllswings);
			getFilter().apply();
		}

		public boolean isNotAllswings() {
			return !isAllswings();
		}

		public void setNotAllswings(boolean notallswings) {
			setAllswings(!notallswings);
		}

		public String getVersion() {
			return getFilter().getVersion().toString();
		}

		public void setVersion(String aVersion) {
			getFilter().setVersion(ProbVer.valueOf(aVersion));
			boolean sct = isTargetMode();
			boolean tar = isSCtMode();
			sctIndex.setEnabled(sct);
			bsc.setEnabled(sct);
			pid.setEnabled(tar);
			cid.setEnabled(tar);
			fcombo.setEnabled(tar);

			getFilter().apply();
		}

		private boolean isSCtMode() {
			return getFilter().getVersion() != ProbVer.SCT;
		}

		private boolean isTargetMode() {
			return getFilter().getVersion() != ProbVer.Targets;
		}

		public int getSctIndex() {
			return getFilter().getSCTIndex();
		}

		@SuppressWarnings("boxing")
		public void setSctIndex(int aSctIndex) {
			getFilter().setSCTIndex(aSctIndex);
			getFilter().apply();
		}

		public int getBcid() {
			return getFilter().getBCID();
		}

		@SuppressWarnings("boxing")
		public void setBcid(int aBcid) {
			getFilter().setBCID(aBcid);
			getFilter().apply();
		}

	}

	public static ProbababilitiesFilter getFilter() {
		return (ProbababilitiesFilter) WidgetPlugin.getDefault()
				.getProbabilitiesManager().getProbabilitiesLogFilter();
	}

	private FilterAdapter filterAdapter = new FilterAdapter();

	public FilterAdapter getFilterAdapter() {
		return filterAdapter;
	}

	public void setFilterAdapter(FilterAdapter aFilterAdapter) {
		filterAdapter = aFilterAdapter;
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
	private Label flabel_1;
	private Combo probVersion;
	Spinner bsc;
	private Button allswings;
	private Label flabel_4;
	private Label flabel_5;
	Spinner pid;
	Spinner cid;
	Combo fcombo;
	private Section fsection;
	private Composite fcomposite;

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
		IObservableValue scaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(scale);
		IObservableValue filterAdapterScaleObserveValue = PojoObservables
				.observeValue(filterAdapter, "scale");
		bindingContext.bindValue(scaleObserveSelectionObserveWidget,
				filterAdapterScaleObserveValue, null, null);
		//
		IObservableValue probVersionObserveSelectionObserveWidget = SWTObservables
				.observeSelection(probVersion);
		IObservableValue filterAdapterVersionObserveValue = PojoObservables
				.observeValue(filterAdapter, "version");
		bindingContext.bindValue(probVersionObserveSelectionObserveWidget,
				filterAdapterVersionObserveValue, null, null);
		//
		IObservableValue pidObserveSelectionObserveWidget = SWTObservables
				.observeSelection(pid);
		IObservableValue filterAdapterPIDObserveValue = PojoObservables
				.observeValue(filterAdapter, "PID");
		bindingContext.bindValue(pidObserveSelectionObserveWidget,
				filterAdapterPIDObserveValue, null, null);
		//
		IObservableValue cidObserveSelectionObserveWidget = SWTObservables
				.observeSelection(cid);
		IObservableValue filterAdapterCIDObserveValue = PojoObservables
				.observeValue(filterAdapter, "CID");
		bindingContext.bindValue(cidObserveSelectionObserveWidget,
				filterAdapterCIDObserveValue, null, null);
		//
		IObservableValue fcomboObserveSelectionObserveWidget = SWTObservables
				.observeSelection(fcombo);
		IObservableValue filterAdapterDirObserveValue = PojoObservables
				.observeValue(filterAdapter, "dir");
		bindingContext.bindValue(fcomboObserveSelectionObserveWidget,
				filterAdapterDirObserveValue, null, null);
		//
		IObservableValue allswingsObserveSelectionObserveWidget = SWTObservables
				.observeSelection(allswings);
		IObservableValue filterAdapterAllswingsObserveValue = PojoObservables
				.observeValue(filterAdapter, "allswings");
		bindingContext.bindValue(allswingsObserveSelectionObserveWidget,
				filterAdapterAllswingsObserveValue, null, null);
		//
		IObservableValue sctIndexObserveSelectionObserveWidget = SWTObservables
				.observeSelection(sctIndex);
		IObservableValue filterAdapterSctIndexObserveValue = PojoObservables
				.observeValue(filterAdapter, "sctIndex");
		bindingContext.bindValue(sctIndexObserveSelectionObserveWidget,
				filterAdapterSctIndexObserveValue, null, null);
		//
		IObservableValue bscObserveSelectionObserveWidget = SWTObservables
				.observeSelection(bsc);
		IObservableValue filterAdapterBcidObserveValue = PojoObservables
				.observeValue(filterAdapter, "bcid");
		bindingContext.bindValue(bscObserveSelectionObserveWidget,
				filterAdapterBcidObserveValue, null, null);
		//

		WidgetPlugin.getDefault().getProbabilitiesManager()
				.setLogSettingsBindingContext(bindingContext);
		return bindingContext;
	}

	@Override
	public void dispose() {
		getFilter().removePropertyChangeListener(probListener);
		super.dispose();
	}
}
