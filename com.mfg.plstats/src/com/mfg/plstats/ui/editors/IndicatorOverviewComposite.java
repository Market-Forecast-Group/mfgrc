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

package com.mfg.plstats.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.utils.ObjectListener;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.ProbabilityElement;

/**
 * @author arian
 * 
 */
public class IndicatorOverviewComposite extends Composite implements
		ObjectListener<DistributionsContainer> {
	final DataBindingContext m_bindingContext;

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	final IIndicatorConfiguration configuration;
	private final ImageHyperlink createIndicatorLink;
	private final ImageHyperlink loadIndicatorLink;
	private final ImageHyperlink openChartLink;
	private final Button btnMultiscales;
	private final Text text;

	private final ImageHyperlink createProbabilitiesLink;
	private final ImageHyperlink exportIndicatorLink;
	private final ImageHyperlink createIndexLink;
	private final Text ftext;
	private final Text probName;
	private final Label flblProbabilityName;

	String probabilityName;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IndicatorOverviewComposite(Composite parent,
			IIndicatorConfiguration aConfiguration) {
		super(parent, SWT.NONE);
		this.configuration = aConfiguration;

		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent aEvt) {
				updateName();
			}
		};
		aConfiguration.getProbabilitiesSettings().addPropertyChangeListener(
				listener);
		aConfiguration.addPropertyChangeListener(listener);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = toolkit.createComposite(this, SWT.NONE);
		toolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(2, true));

		Section sctnNegativeChannels = toolkit.createSection(composite,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNegativeChannels.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, true, 1, 1));
		toolkit.paintBordersFor(sctnNegativeChannels);
		sctnNegativeChannels.setText("Negative Channels");
		sctnNegativeChannels.setExpanded(true);

		Composite composite_2 = toolkit.createComposite(sctnNegativeChannels,
				SWT.NONE);
		toolkit.paintBordersFor(composite_2);
		sctnNegativeChannels.setClient(composite_2);
		composite_2.setLayout(new GridLayout(3, false));

		btnMultiscales = new Button(composite_2, SWT.CHECK);
		btnMultiscales.setEnabled(false);
		toolkit.adapt(btnMultiscales, true, true);

		Label lblMultiscales = new Label(composite_2, SWT.NONE);
		lblMultiscales.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(lblMultiscales, true, true);
		lblMultiscales.setText("Multi-scales");

		Label lblScales = new Label(composite_2, SWT.NONE);
		lblScales.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(lblScales, true, true);
		lblScales.setText("Scales");

		text = new Text(composite_2, SWT.BORDER | SWT.READ_ONLY);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(text, true, true);

		Section sctnGeneralInformation = toolkit.createSection(composite,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 1, 1));
		toolkit.paintBordersFor(sctnGeneralInformation);
		sctnGeneralInformation.setText("General Information");
		sctnGeneralInformation.setExpanded(true);

		Composite composite_4 = new Composite(sctnGeneralInformation, SWT.NONE);
		toolkit.adapt(composite_4);
		toolkit.paintBordersFor(composite_4);
		sctnGeneralInformation.setClient(composite_4);
		composite_4.setLayout(new GridLayout(2, false));

		Label lblCsv = new Label(composite_4, SWT.NONE);
		lblCsv.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		toolkit.adapt(lblCsv, true, true);
		lblCsv.setText("CSV Name:");

		ftext = new Text(composite_4, SWT.BORDER);
		ftext.setEditable(false);
		ftext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(ftext, true, true);

		flblProbabilityName = new Label(composite_4, SWT.NONE);
		flblProbabilityName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		toolkit.adapt(flblProbabilityName, true, true);
		flblProbabilityName.setText("Probability Name:");

		probName = new Text(composite_4, SWT.BORDER);
		probName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(probName, true, true);
		new Label(composite, SWT.NONE).setText("");

		Section sctnNewSection = toolkit.createSection(composite,
				ExpandableComposite.TITLE_BAR);
		sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,
				true, 1, 1));
		toolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText("Commands");
		sctnNewSection.setExpanded(true);

		Composite composite_1 = toolkit.createComposite(sctnNewSection,
				SWT.NONE);
		toolkit.paintBordersFor(composite_1);
		sctnNewSection.setClient(composite_1);
		composite_1.setLayout(new GridLayout(1, false));

		createIndicatorLink = toolkit.createImageHyperlink(composite_1,
				SWT.NONE);
		createIndicatorLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		toolkit.paintBordersFor(createIndicatorLink);
		createIndicatorLink.setText("Create Indicator");

		createProbabilitiesLink = toolkit.createImageHyperlink(composite_1,
				SWT.NONE);
		createProbabilitiesLink.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, false, false, 1, 1));
		toolkit.paintBordersFor(createProbabilitiesLink);
		createProbabilitiesLink.setText("Create Probabilities");

		// ///////////

		exportIndicatorLink = toolkit.createImageHyperlink(composite_1,
				SWT.NONE);
		exportIndicatorLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		toolkit.paintBordersFor(createProbabilitiesLink);
		exportIndicatorLink.setText("Export Indicator");

		// ///////////

		openChartLink = toolkit.createImageHyperlink(composite_1, SWT.NONE);
		openChartLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		toolkit.paintBordersFor(openChartLink);
		openChartLink.setText("Open Chart");

		loadIndicatorLink = toolkit.createImageHyperlink(composite_1, SWT.NONE);
		loadIndicatorLink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		toolkit.paintBordersFor(loadIndicatorLink);
		loadIndicatorLink.setText("Load Indicator");

		createIndexLink = toolkit.createImageHyperlink(composite_1, SWT.NONE);
		createIndexLink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		toolkit.paintBordersFor(createIndexLink);
		createIndexLink.setText("Create Indexing");

		Label label_1 = toolkit.createLabel(composite_1, "", SWT.NONE);
		label_1.setText("");

		Label label = toolkit.createLabel(composite_1, "", SWT.NONE);
		label.setText("");
		m_bindingContext = initDataBindings();
		updateName();
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsStorate()
				.addStorageListener(new WorkspaceStorageAdapter() {
					@Override
					public void objectRemoved(IWorkspaceStorage aStorage,
							Object aObj) {
						updateName();
					}

					@Override
					public void listRemoved(IWorkspaceStorage aStorage,
							List<? extends Object> aList) {
						updateName();
					}

				});

	}

	protected void updateName() {
		probabilityName = "New Probability";
		List<ProbabilityElement> list = WidgetPlugin.getDefault()
				.getProbabilitiesManager().getDistributionsStorate()
				.getDistributions(configuration);
		if (list != null) {
			for (ProbabilityElement probabilityElement : list) {
				if (configuration.getProbabilitiesSettings().equals(
						probabilityElement.getProbabilityConfiguration())) {
					probabilityName = probabilityElement.getName();
					break;
				}
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				configuration.setProbabilityName(probabilityName);
				System.out.println("new name " + probabilityName);
				m_bindingContext.updateTargets();
			}
		});
	}

	public IndicatorParamBean getIndicatorSettings() {
		return (IndicatorParamBean) configuration.getIndicatorSettings();
	}

	/**
	 * @return the createIndicatorLink
	 */
	public ImageHyperlink getCreateIndicatorLink() {
		return createIndicatorLink;
	}

	/**
	 * @return the createIndicatorLink
	 */
	public ImageHyperlink getCreateProbabilitiesLink() {
		return createProbabilitiesLink;
	}

	public ImageHyperlink getExportIndicatorLink() {
		return exportIndicatorLink;
	}

	/**
	 * @return the loadIndicatorLink
	 */
	public ImageHyperlink getLoadIndicatorLink() {
		return loadIndicatorLink;
	}

	public ImageHyperlink getCreateIndexLink() {
		return createIndexLink;
	}

	/**
	 * @return the openChartLink
	 */
	public ImageHyperlink getOpenChartLink() {
		return openChartLink;
	}

	/**
	 * @return
	 */
	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	@Override
	public void handle(DistributionsContainer aObject) {
		updateName();
	}

	@Override
	public void dispose() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.removeObjectListener(this);
		super.dispose();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue ftextObserveTextObserveWidget = SWTObservables
				.observeText(ftext, SWT.Modify);
		IObservableValue configurationSymbolnameObserveValue = BeansObservables
				.observeValue(configuration, "symbol.name");
		bindingContext.bindValue(ftextObserveTextObserveWidget,
				configurationSymbolnameObserveValue, null, null);
		//
		IObservableValue flblProbabilityNameObserveTextObserveWidget = SWTObservables
				.observeText(probName);
		IObservableValue configurationProbabilityNameObserveValue = BeansObservables
				.observeValue(configuration, "probabilityName");
		bindingContext
				.bindValue(flblProbabilityNameObserveTextObserveWidget,
						configurationProbabilityNameObserveValue,
						new UpdateValueStrategy(
								UpdateValueStrategy.POLICY_NEVER), null);
		//
		return bindingContext;
	}
}
