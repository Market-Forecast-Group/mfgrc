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
package com.mfg.symbols.dfs.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.RequestMode;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSListener;
import com.mfg.dm.speedControl.SpeedComposite3;
import com.mfg.dm.symbols.MergeSeriesAlgorithm;

/**
 * @author arian
 * 
 */
public class DFSHistoricalDataInfoComposite extends Composite implements
		IDFSListener {
	private final DataBindingContext m_bindingContext;

	public static class IsDatabaseConverter extends Converter {

		public IsDatabaseConverter() {
			super(RequestMode.class, boolean.class);
		}

		@Override
		public Object convert(Object fromObject) {
			return new Boolean(fromObject == RequestMode.DATABASE);
		}

	}

	/**
	 * 
	 */
	private static final String PROP_MODEL = "model";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private DFSHistoricalDataInfo _model;
	private final DFSHistoricalDataInfoComposite self = this;
	IDFS _dfs;
	Button _playButton;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DFSHistoricalDataInfoComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(2, false));

		toolkit.createLabel(this, "Request Mode", SWT.NONE);

		requestModeViewer = new ComboViewer(this, SWT.READ_ONLY);
		_requestModeCombo = requestModeViewer.getCombo();
		_requestModeCombo.setItems(new String[] {});
		_requestModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		toolkit.paintBordersFor(_requestModeCombo);
		requestModeViewer.setContentProvider(new ArrayContentProvider());
		requestModeViewer.setLabelProvider(new LabelProvider());
		_requestModeCombo.select(0);

		Label lblMultipleDataSeries = new Label(this, SWT.NONE);
		toolkit.adapt(lblMultipleDataSeries, true, true);
		lblMultipleDataSeries.setText("Multiple Data Series Algorithm");

		comboViewerDataSeriesAlgo = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo = comboViewerDataSeriesAlgo.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.paintBordersFor(combo);
		comboViewerDataSeriesAlgo.setLabelProvider(new LabelProvider());
		comboViewerDataSeriesAlgo
				.setContentProvider(new ArrayContentProvider());

		toolkit.createLabel(this, "Gap Filling Type", SWT.NONE);

		gapFillingTypeCombo = new ComboViewer(this, SWT.READ_ONLY);
		combo_1 = gapFillingTypeCombo.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_1);
		gapFillingTypeCombo.setContentProvider(new ArrayContentProvider());
		gapFillingTypeCombo.setLabelProvider(new LabelProvider());

		Label lblXp = new Label(this, SWT.NONE);
		toolkit.adapt(lblXp, true, true);
		lblXp.setText("XP");

		text = toolkit.createText(this, "");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(text, true, true);

		Label lblDp = new Label(this, SWT.NONE);
		toolkit.adapt(lblDp, true, true);
		lblDp.setText("DP");

		text_1 = toolkit.createText(this, "");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(text_1, true, true);

		Label lblWarmupPrices = new Label(this, SWT.NONE);
		toolkit.adapt(lblWarmupPrices, true, true);
		lblWarmupPrices.setText("Warm-Up Prices");

		_warmUpPricesCombo = new ComboViewer(this, SWT.READ_ONLY);
		_combo = _warmUpPricesCombo.getCombo();
		_combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.paintBordersFor(_combo);
		_warmUpPricesCombo.setLabelProvider(new LabelProvider());
		_warmUpPricesCombo.setContentProvider(new ArrayContentProvider());

		_btnFilter = new Button(this, SWT.CHECK);
		_btnFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		toolkit.adapt(_btnFilter, true, true);
		_btnFilter.setText("Filter out of range ticks");

		Label lblMinimumGapIn = new Label(this, SWT.NONE);
		toolkit.adapt(lblMinimumGapIn, true, true);
		lblMinimumGapIn.setText("Out of range ticks gap");

		_minGapText = new Text(this, SWT.BORDER);
		_minGapText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		toolkit.adapt(_minGapText, true, true);
		/* $hide$ */afterCreateWidgets();
		m_bindingContext = initDataBindings();

		/* $hide$ */afterInitBindings();

	}

	private void afterInitBindings() {
		//
	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {
		// needed for WB to avoid a NPE
		if (DFSPlugin.getDefault() == null) {
			return;
		}
		_warmUpPricesCombo.setInput(new Object[] { Integer.valueOf(0),
				Integer.valueOf(10), Integer.valueOf(30), Integer.valueOf(50),
				Integer.valueOf(100), Integer.valueOf(300),
				Integer.valueOf(500), Integer.valueOf(1000),
				Integer.valueOf(3000), Integer.valueOf(10000),
				Integer.valueOf(20000), Integer.valueOf(30000),
				Integer.valueOf(50000), Integer.valueOf(100000),
				Integer.valueOf(1000000) });
		requestModeViewer.setInput(RequestMode.values());
		comboViewerDataSeriesAlgo.setInput(MergeSeriesAlgorithm.values());
		gapFillingTypeCombo.setInput(DFSHistoricalDataInfo.GapFillingType
				.values());

		Assert.isTrue(DFSPlugin.getDefault().isDFSReady());
		_dfs = DFSPlugin.getDefault().getDataProvider().getDfs();
		_dfs.addListener(this);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				_dfs.removeListener(DFSHistoricalDataInfoComposite.this);
			}
		});
	}

	/**
	 * @return the model
	 */
	public DFSHistoricalDataInfo getModel() {
		return _model;
	}

	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	/**
	 * @param model
	 *            the model to set
	 * @param speedControl
	 */
	public void init(DFSHistoricalDataInfo model, SpeedComposite3 speedControl) {
		this._model = model;
		_playButton = speedControl.getPlayButton();
		firePropertyChange(PROP_MODEL);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final Text text;
	private final Text text_1;
	private final ComboViewer comboViewerDataSeriesAlgo;
	private final Combo combo_1;
	private final ComboViewer gapFillingTypeCombo;
	final Combo _requestModeCombo;
	private final ComboViewer requestModeViewer;
	private final Text _minGapText;
	private final Button _btnFilter;
	private final ComboViewer _warmUpPricesCombo;
	private final Combo _combo;
	boolean _dfsDialogOpen = false;

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
		IObservableValue observeSingleSelectionComboViewerDataSeriesAlgo = ViewerProperties
				.singleSelection().observe(comboViewerDataSeriesAlgo);
		IObservableValue modelmultipleDataSeriesAlgorithmSelfObserveValue = BeanProperties
				.value("model.multipleDataSeriesAlgorithm").observe(self);
		bindingContext.bindValue(
				observeSingleSelectionComboViewerDataSeriesAlgo,
				modelmultipleDataSeriesAlgorithmSelfObserveValue, null, null);
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
		IObservableValue observeSingleSelectionGapFillingTypeCombo = ViewerProperties
				.singleSelection().observe(gapFillingTypeCombo);
		IObservableValue modelgapFillingTypeSelfObserveValue = BeanProperties
				.value("model.gapFillingType").observe(self);
		bindingContext.bindValue(observeSingleSelectionGapFillingTypeCombo,
				modelgapFillingTypeSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties
				.singleSelection().observe(requestModeViewer);
		IObservableValue modelrequestModeSelfObserveValue = BeanProperties
				.value("model.requestMode").observe(self);
		bindingContext.bindValue(observeSingleSelectionComboViewer,
				modelrequestModeSelfObserveValue, null, null);
		//
		IObservableValue observeSelection_btnFilterObserveWidget = WidgetProperties
				.selection().observe(_btnFilter);
		IObservableValue modelfilterOutOfRangeTicksSelfObserveValue = BeanProperties
				.value("model.filterOutOfRangeTicks").observe(self);
		bindingContext.bindValue(observeSelection_btnFilterObserveWidget,
				modelfilterOutOfRangeTicksSelfObserveValue, null, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_minGapText);
		IObservableValue modelminGapInTicksSelfObserveValue = BeanProperties
				.value("model.minGapInTicks").observe(self);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new MinGapValidator());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		bindingContext.bindValue(observeText_textObserveWidget,
				modelminGapInTicksSelfObserveValue, strategy_1, strategy);
		//
		IObservableValue observeEnabled_minGapTextObserveWidget = WidgetProperties
				.enabled().observe(_minGapText);
		bindingContext.bindValue(observeEnabled_minGapTextObserveWidget,
				modelfilterOutOfRangeTicksSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelection_warmUpPricesCombo = ViewerProperties
				.singleSelection().observe(_warmUpPricesCombo);
		IObservableValue modelwarmUpNumberOfPricesSelfObserveValue = BeanProperties
				.value("model.warmUpNumberOfPrices").observe(self);
		bindingContext.bindValue(observeSingleSelection_warmUpPricesCombo,
				modelwarmUpNumberOfPricesSelfObserveValue, null, null);
		//
		IObservableValue observeEnabled_comboObserveWidget = WidgetProperties
				.enabled().observe(_combo);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setConverter(new IsDatabaseConverter());
		bindingContext.bindValue(observeEnabled_comboObserveWidget,
				modelrequestModeSelfObserveValue, null, strategy_2);
		//
		return bindingContext;
	}

	@Override
	public synchronized void onConnectionStatusUpdate(
			final ETypeOfData aDataType, final EConnectionStatus aStatus) {
		if (!_dfsDialogOpen) {
			_dfsDialogOpen = true;
			openChangeRequestModeDialog(aDataType, aStatus);
		}
	}

	void openChangeRequestModeDialog(final ETypeOfData aDataType,
			final EConnectionStatus aStatus) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					boolean mixed = getModel() != null
							&& getModel().getRequestMode() == RequestMode.MIXED;
					boolean playEnabled = _playButton != null
							&& _playButton.isEnabled();
					boolean realTime = aDataType == ETypeOfData.REAL_TIME;
					boolean connected = aStatus == EConnectionStatus.CONNECTED;

					if (realTime) {
						if (mixed && !connected && playEnabled) {
							boolean result = MessageDialog
									.openQuestion(getShell(), "DFS",
											"DFS is not connected. Do you want to switch to Database mode?");
							if (result) {
								getModel().setRequestMode(RequestMode.DATABASE);
							} else {
								_playButton.setEnabled(false);
							}
						} else {
							if ((connected || !mixed) && !playEnabled) {
								if (_playButton != null) {
									_playButton.setEnabled(true);
								}
							}
						}
					}
				} catch (SWTException e) {
					// when closing the widgets are disposed
				}
				_dfsDialogOpen = false;
			}
		});
	}

	// @Override
	// public void onNewQuote(DFSSymbolEvent aQuote) {
	// // TODO:
	// }

}
