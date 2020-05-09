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

package com.mfg.widget.probabilities.logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import com.mfg.logger.ui.views.AbstractLoggerViewControl;
import com.mfg.logger.ui.views.AbstractLoggerViewControl.IItemListener;

/**
 * @author gardero
 * 
 */
@SuppressWarnings("unused")
public class LogNavigatorComposite extends Composite implements
		ILogNavigatorControlerView {

	private DataBindingContext m_bindingContext;
	private final ILogNavigatorControlerView self = this;
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private AbstractLoggerViewControl control;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LogNavigatorComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@SuppressWarnings("synthetic-access")
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
		composite_1.setLayout(new GridLayout(1, false));

		fsection = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		fsection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.paintBordersFor(fsection);
		fsection.setText("Navigation");
		fsection.setExpanded(true);

		fcomposite = new Composite(fsection, SWT.NONE);
		toolkit.adapt(fcomposite);
		toolkit.paintBordersFor(fcomposite);
		fsection.setClient(fcomposite);
		fcomposite.setLayout(new GridLayout(4, false));

		gPrev = new Button(fcomposite, SWT.NONE);
		gPrev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gotoPrev();
			}
		});
		gPrev.setToolTipText("Go to Previous Event");
		toolkit.adapt(gPrev, true, true);
		gPrev.setText("Prev");

		gNext = new Button(fcomposite, SWT.NONE);
		gNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gotoNext();
			}
		});
		gNext.setToolTipText("Go to Next Event");
		toolkit.adapt(gNext, true, true);
		gNext.setText("Next");

		Label lblNewLabel_2 = toolkit.createLabel(fcomposite, "Event Type",
				SWT.NONE);

		eventType = new Combo(fcomposite, SWT.NONE);
		GridData gd_eventType = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_eventType.widthHint = 96;
		eventType.setLayoutData(gd_eventType);
		eventType.setText(getTypes().get(0));
		toolkit.adapt(eventType);
		toolkit.paintBordersFor(eventType);

		Section sctnIndicatorLines = toolkit.createSection(composite_1,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnIndicatorLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnIndicatorLines);
		sctnIndicatorLines.setText("Section Limits");
		sctnIndicatorLines.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnIndicatorLines,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnIndicatorLines.setClient(composite);
		composite.setLayout(new GridLayout(4, false));

		sStartTime = new Button(composite, SWT.NONE);
		sStartTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setStartTime();
			}
		});
		sStartTime.setToolTipText("Set current time as Start Time");
		toolkit.adapt(sStartTime, true, true);
		sStartTime.setText("Set");

		Label lblNewLabel_1 = toolkit.createLabel(composite, "Start Time",
				SWT.NONE);

		startTime = new Spinner(composite, SWT.BORDER);
		startTime.setMaximum(99999999);
		toolkit.adapt(startTime);
		toolkit.paintBordersFor(startTime);

		gStartTime = new Button(composite, SWT.NONE);
		gStartTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gotoStartTime();
			}
		});
		gStartTime.setToolTipText("Go to Start Time");
		toolkit.adapt(gStartTime, true, true);
		gStartTime.setText("goto");

		sEndTime = new Button(composite, SWT.NONE);
		sEndTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEndTime();
			}
		});
		sEndTime.setToolTipText("Set current time as End Time");
		toolkit.adapt(sEndTime, true, true);
		sEndTime.setText("Set");

		Label lblNewLabel_3 = toolkit.createLabel(composite, "End Time",
				SWT.NONE);

		endTime = new Spinner(composite, SWT.BORDER);
		endTime.setMaximum(99999999);
		toolkit.adapt(endTime);
		toolkit.paintBordersFor(endTime);

		gEndTime = new Button(composite, SWT.NONE);
		gEndTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gotoEndTime();
			}
		});
		gEndTime.setToolTipText("Go to End Time");
		toolkit.adapt(gEndTime, true, true);
		gEndTime.setText("goto");
		updateItems();
		m_bindingContext = initDataBindings();
	}

	private List<String> types;

	private void connectToLogger() {
		getControl().addNewEventTypeListener(new IItemListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void handleItem(Object aItem) {
				String type = (String) aItem;
				if (!getTypes().contains(type)) {
					getTypes().add(type);
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							updateItems();
						}

					});
					System.out.println("types = " + getTypes());
				}
			}
		});
	}

	private void updateItems() {
		String old = eventType.getText();
		eventType.setItems(getTypes().toArray(new String[] {}));
		eventType.setText(old);
	}

	private List<String> getTypes() {
		if (types == null) {
			types = new ArrayList<String>();
			types.add("<ANY>");
		}
		return types;
	}

	protected void gotoNext() {
		getControl().gotoNext(eventType.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.views.ILogNavigatorControlerView#getControl()
	 */
	@Override
	public AbstractLoggerViewControl getControl() {
		return control;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.views.ILogNavigatorControlerView#setControl(com.mfg.
	 * strategy.logger.ILoggerViewControl)
	 */
	@Override
	public void setControl(AbstractLoggerViewControl control1) {
		this.control = control1;
		types = new ArrayList<String>(control1.getEventTypes());
		Display.getDefault().asyncExec(new Runnable() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				updateItems();
			}

		});
		bind();
		connectToLogger();
	}

	protected void gotoPrev() {
		getControl().gotoPrevious(eventType.getText());
	}

	protected void gotoEndTime() {
		getControl().gotoEndTime();
	}

	protected void gotoStartTime() {
		getControl().gotoStartTime();
	}

	protected void setEndTime() {
		getControl().setEndTimeToCurrent();
		bindingContext.updateTargets();
	}

	protected void setStartTime() {
		getControl().setStartTimeToCurrent();
		bindingContext.updateTargets();
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see com.mfg.trading.ui.editors.migratingToWB.AbstractTradeComposite#
	// getDataBindingContext()
	// */
	// public DataBindingContext getDataBindingContext() {
	// return m_bindingContext;
	// }

	public Color getBlueColor() {
		return toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
	}

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	private Spinner startTime;
	private Spinner endTime;
	private Combo eventType;
	private Section fsection;
	private Composite fcomposite;
	private Button sStartTime;
	private Button sEndTime;
	private Button gStartTime;
	private Button gEndTime;
	private Button gPrev;
	private Button gNext;
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

	protected DataBindingContext initDataBindings() {
		bindingContext = new DataBindingContext();
		//
		if (getControl() != null) {
			bind();
		}
		//
		return bindingContext;
	}

	private void bind() {
		IObservableValue startTimeObserveSelectionObserveWidget = SWTObservables
				.observeSelection(startTime);
		IObservableValue controlStartTimeObserveValue = PojoObservables
				.observeValue(getControl(), "startTime");
		bindingContext.bindValue(startTimeObserveSelectionObserveWidget,
				controlStartTimeObserveValue, null, null);
		//
		IObservableValue endTimeObserveSelectionObserveWidget = SWTObservables
				.observeSelection(endTime);
		IObservableValue controlEndTimeObserveValue = PojoObservables
				.observeValue(getControl(), "endTime");
		bindingContext.bindValue(endTimeObserveSelectionObserveWidget,
				controlEndTimeObserveValue, null, null);
	}
}
