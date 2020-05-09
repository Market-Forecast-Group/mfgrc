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

package com.mfg.strategy.builder.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageListener;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.ProbabilitiesNames;

/**
 * @author arian
 * 
 */
public class GeneralSettingsComposite extends Composite {

	final DataBindingContext m_bindingContext;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private EventsCanvasModel model = new EventsCanvasModel();
	private InternalModel internalModel = new InternalModel();


	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GeneralSettingsComposite(Composite parent, int style) {
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

		fsashForm_1 = new SashForm(this, SWT.VERTICAL);
		toolkit.adapt(fsashForm_1);
		toolkit.paintBordersFor(fsashForm_1);

		Section sctnIndicatorLines = toolkit.createSection(fsashForm_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		toolkit.paintBordersFor(sctnIndicatorLines);
		sctnIndicatorLines.setText("General");
		sctnIndicatorLines.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnIndicatorLines,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnIndicatorLines.setClient(composite);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		fsashForm = new SashForm(composite, SWT.VERTICAL);
		toolkit.adapt(fsashForm);
		toolkit.paintBordersFor(fsashForm);

		fcomposite = new Composite(fsashForm, SWT.NONE);
		toolkit.adapt(fcomposite);
		toolkit.paintBordersFor(fcomposite);
		fcomposite.setLayout(new GridLayout(2, false));

		Label lblNewLabel_1 = toolkit.createLabel(fcomposite, "Name", SWT.NONE);
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblNewLabel_1.widthHint = 45;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);

		nameText = new Text(fcomposite, SWT.BORDER);
		GridData gd_nameText = new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1);
		gd_nameText.widthHint = 118;
		nameText.setLayoutData(gd_nameText);
		toolkit.adapt(nameText, true, true);

		fcomposite_2 = new Composite(fsashForm, SWT.NONE);
		toolkit.adapt(fcomposite_2);
		toolkit.paintBordersFor(fcomposite_2);
		fcomposite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		fgroup = new Group(fcomposite_2, SWT.NONE);
		fgroup.setText("Description");
		fgroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		toolkit.adapt(fgroup);
		toolkit.paintBordersFor(fgroup);

		targetsStep = new Text(fgroup, SWT.BORDER | SWT.MULTI);
		toolkit.adapt(targetsStep, true, true);
		fsashForm.setWeights(new int[] { 31, 99 });

		fsctnNewSection = toolkit.createSection(fsashForm_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		toolkit.paintBordersFor(fsctnNewSection);
		fsctnNewSection.setText("Probabilities");
		fsctnNewSection.setExpanded(true);

		Composite composite_2 = toolkit.createComposite(fsctnNewSection,
				SWT.NONE);
		toolkit.paintBordersFor(composite_2);
		fsctnNewSection.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));

		flabel_1 = new Label(composite_2, SWT.NONE);
		toolkit.adapt(flabel_1, true, true);
		flabel_1.setText("Probability Distribution");

		fcombo = new Combo(composite_2, SWT.NONE);
		fcombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		fillDDL();

		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsStorate().addStorageListener(new IWorkspaceStorageListener() {

					@Override
					public void storageChanged(IWorkspaceStorage aStorage) {
						fillLaterDDL();
					}


					@Override
					public void objectRemoved(IWorkspaceStorage aStorage, Object aObj) {
						fillLaterDDL();
					}


					@Override
					public void objectModified(IWorkspaceStorage aStorage, Object aObj) {
						// TODO Auto-generated method stub

					}


					@Override
					public void objectAdded(IWorkspaceStorage aSotarage, Object aObj) {
						fillLaterDDL();
					}


					@Override
					public void objectAboutToRemove(IWorkspaceStorage aStorage, Object aObj) throws RemoveException {
						// fillLaterDDL();
					}


					@Override
					public void listRemoved(IWorkspaceStorage aStorage, List<? extends Object> aList) {
						fillLaterDDL();
					}


					@Override
					public void listAdded(IWorkspaceStorage aStorage, List<? extends Object> aList) {
						fillLaterDDL();
					}
				});

		toolkit.adapt(fcombo);
		toolkit.paintBordersFor(fcombo);
		fsashForm_1.setWeights(new int[] { 1, 1 });

		m_bindingContext = initDataBindings();
	}


	void fillLaterDDL() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				fillDDL();
			}
		});
	}


	public void fillDDL() {
		List<String> list = WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsStorate().getProbabilitiesNames();
		list.add(0, ProbabilitiesNames.NO_PROBABILITY);
		list.add(0, ProbabilitiesNames.CURRENT_PROBABILITY);
		probArray = list.toArray(new String[] {});
		String t = fcombo.getText();
		fcombo.setItems(probArray);
		fcombo.setText(t);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.trading.ui.editors.migratingToWB.AbstractTradeComposite# getDataBindingContext()
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

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final Text nameText;
	private final Text targetsStep;
	private DataBindingContext bindingContext;
	private final Group fgroup;
	private final Composite fcomposite;
	private final SashForm fsashForm;
	private final Composite fcomposite_2;
	private final SashForm fsashForm_1;
	private final Label flabel_1;
	private final Combo fcombo;
	private Section fsctnNewSection;
	private PropertyChangeListener changer;
	private String[] probArray;


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


	public EventsCanvasModel getModel() {
		return model;
	}


	public void setModel(EventsCanvasModel aModel) {
		if (model != null && changer != null)
			model.removePropertyChangeListener(changer);
		model = aModel;
		m_bindingContext.updateTargets();
		model.addPropertyChangeListener(getChanger());
	}


	private PropertyChangeListener getChanger() {
		if (changer == null)
			changer = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent aEvt) {
					if (aEvt.getPropertyName().equals(PropertiesID.PROBABILISTIC_EVENTS)) {
						m_bindingContext.updateTargets();
					}
				}
			};
		return changer;
	}

	public class InternalModel {

		public String getName() {
			return getModel().getName();
		}


		public void setName(String aName) {
			getModel().setName(aName);
		}


		public String getDescription() {
			return getModel().getDescription();
		}


		public void setDescription(String aDescription) {
			getModel().setDescription(aDescription);
		}


		public boolean isProbabilistic() {
			return getModel().isProbabilistic();
		}


		public void setProbabilistic(boolean aProbabilistic) {
			getModel().setProbabilistic(aProbabilistic);
		}


		public String getProbabilityName() {
			return getModel().getProbabilityName();
		}


		public void setProbabilityName(String aProbabilityName) {
			getModel().setProbabilityName(aProbabilityName);
		}

	}


	public InternalModel getInternalModel() {
		return internalModel;
	}


	public void setInternalModel(InternalModel aInternalModel) {
		internalModel = aInternalModel;
	}


	protected DataBindingContext initDataBindings() {
		DataBindingContext aBindingContext = new DataBindingContext();
		//
		IObservableValue nameTextObserveTextObserveWidget = SWTObservables.observeText(nameText, SWT.Modify);
		IObservableValue getModelNameObserveValue = PojoObservables.observeValue(getInternalModel(), "name");
		aBindingContext.bindValue(nameTextObserveTextObserveWidget, getModelNameObserveValue, null, null);
		//
		IObservableValue targetsStepObserveMessageObserveWidget = SWTObservables.observeText(targetsStep, SWT.Modify);
		IObservableValue getModelDescriptionObserveValue = PojoObservables.observeValue(getInternalModel(), "description");
		aBindingContext.bindValue(targetsStepObserveMessageObserveWidget, getModelDescriptionObserveValue, null, null);
		//
		IObservableValue fsctnNewSectionObserveVisibleObserveWidget = SWTObservables.observeVisible(fsctnNewSection);
		IObservableValue getInternalModelProbabilisticObserveValue = PojoObservables.observeValue(getInternalModel(), "probabilistic");
		aBindingContext.bindValue(fsctnNewSectionObserveVisibleObserveWidget, getInternalModelProbabilisticObserveValue, null, null);
		//
		IObservableValue fcomboObserveTextObserveWidget_1 = SWTObservables.observeText(fcombo);
		IObservableValue getInternalModelProbabilityNameObserveValue = PojoObservables.observeValue(getInternalModel(), "probabilityName");
		aBindingContext.bindValue(fcomboObserveTextObserveWidget_1, getInternalModelProbabilityNameObserveValue, null, null);
		//
		return aBindingContext;
	}
}
