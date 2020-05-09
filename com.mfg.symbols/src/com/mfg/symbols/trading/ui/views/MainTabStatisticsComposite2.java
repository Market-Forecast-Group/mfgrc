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
package com.mfg.symbols.trading.ui.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.tea.conn.ISingleAccountStatistics;

/**
 * @author arian
 * 
 */
public class MainTabStatisticsComposite2 extends Composite {
	private final DataBindingContext m_bindingContext;

	private static final String PROP_ACCOUNT = "account";
	private final MainTabStatisticsComposite2 self = this;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final Text text;
	private final Text text_1;
	private final Text text_2;
	private final Text text_3;
	private final Text text_4;
	private final Text text_5;
	private final Text text_6;
	private final Text text_7;
	private final Text text_8;
	private final Text text_9;
	private final Text text_10;
	private final Text text_11;
	private final Text text_12;
	private final Text text_13;
	private final Text text_14;
	private final Text text_15;
	private ISingleAccountStatistics account;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MainTabStatisticsComposite2(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(5, false));

		toolkit.createLabel(this, "Total Profit/Loss (Points)", SWT.NONE);

		text = toolkit.createText(this, "New Text", SWT.NONE);
		text.setEditable(false);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label = toolkit.createLabel(this, "", SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_label.widthHint = 20;
		label.setLayoutData(gd_label);

		toolkit.createLabel(this, "Number of Traded Sizes", SWT.NONE);

		text_8 = toolkit.createText(this, "New Text", SWT.NONE);
		text_8.setEditable(false);
		text_8.setText("");
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "Total Profit/Loss (Money) ", SWT.NONE);

		text_1 = toolkit.createText(this, "New Text", SWT.NONE);
		text_1.setEditable(false);
		text_1.setText("");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this, "Number of Winning Traded Sizes", SWT.NONE);

		text_9 = toolkit.createText(this, "New Text", SWT.NONE);
		text_9.setEditable(false);
		text_9.setText("");
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "Profitable Traded Sizes (Points)", SWT.NONE);

		text_2 = toolkit.createText(this, "New Text", SWT.NONE);
		text_2.setEditable(false);
		text_2.setText("");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this, "Number of Losing Traded Sizes", SWT.NONE);

		text_10 = toolkit.createText(this, "New Text", SWT.NONE);
		text_10.setEditable(false);
		text_10.setText("");
		text_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		toolkit.createLabel(this, "Profitable Traded Sizes (Money)", SWT.NONE);

		text_3 = toolkit.createText(this, "New Text", SWT.NONE);
		text_3.setEditable(false);
		text_3.setText("");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this, "Winning/Losing Traded Sizes Ratio", SWT.NONE);

		text_11 = toolkit.createText(this, "New Text", SWT.NONE);
		text_11.setEditable(false);
		text_11.setText("");
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		toolkit.createLabel(this, "Total Loss (Points)", SWT.NONE);

		text_4 = toolkit.createText(this, "New Text", SWT.NONE);
		text_4.setEditable(false);
		text_4.setText("");
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this,
				"Avg Profit for Winning Traded Sizes (Points)", SWT.NONE);

		text_12 = toolkit.createText(this, "New Text", SWT.NONE);
		text_12.setEditable(false);
		text_12.setText("");
		text_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		toolkit.createLabel(this, "Total Loss (Money)", SWT.NONE);

		text_5 = toolkit.createText(this, "New Text", SWT.NONE);
		text_5.setEditable(false);
		text_5.setText("");
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this,
				"Avg Profit for Winning Traded Sizes (Money)", SWT.NONE);

		text_13 = toolkit.createText(this, "New Text", SWT.NONE);
		text_13.setEditable(false);
		text_13.setText("");
		text_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		toolkit.createLabel(this, "Profit/Loss Ratio", SWT.NONE);

		text_6 = toolkit.createText(this, "New Text", SWT.NONE);
		text_6.setEditable(false);
		text_6.setText("");
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this, "Max Open Traded Sizes", SWT.NONE);

		text_14 = toolkit.createText(this, "New Text", SWT.NONE);
		text_14.setEditable(false);
		text_14.setText("");
		text_14.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		toolkit.createLabel(this, "Max Drawdown", SWT.NONE);

		text_7 = toolkit.createText(this, "New Text", SWT.NONE);
		text_7.setEditable(false);
		text_7.setText("");
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(this, "", SWT.NONE);

		toolkit.createLabel(this, "Average Open Traded Sizes", SWT.NONE);

		text_15 = toolkit.createText(this, "New Text", SWT.NONE);
		text_15.setEditable(false);
		text_15.setText("");
		text_15.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		m_bindingContext = initDataBindings();

	}

	public ISingleAccountStatistics getAccount() {
		return account;
	}

	public void setAccount(ISingleAccountStatistics iSingleAccountStatistics) {
		this.account = iSingleAccountStatistics;
		firePropertyChange(PROP_ACCOUNT);
	}

	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getBindingContext() {
		return m_bindingContext;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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
		IObservableValue textObserveTextObserveWidget = SWTObservables
				.observeText(text, SWT.Modify);
		IObservableValue selfAccounttotalProfitLossPointsObserveValue = BeansObservables
				.observeValue(self, "account.totalProfitLossPoints");
		bindingContext.bindValue(textObserveTextObserveWidget,
				selfAccounttotalProfitLossPointsObserveValue, null, null);
		//
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables
				.observeText(text_1, SWT.Modify);
		IObservableValue selfAccounttotalProfitLossMoneyObserveValue = BeansObservables
				.observeValue(self, "account.totalProfitLossMoney");
		bindingContext.bindValue(text_1ObserveTextObserveWidget,
				selfAccounttotalProfitLossMoneyObserveValue, null, null);
		//
		IObservableValue text_2ObserveTextObserveWidget = SWTObservables
				.observeText(text_2, SWT.Modify);
		IObservableValue selfAccountprofitableTradedSizesPointsObserveValue = BeansObservables
				.observeValue(self, "account.profitableTradedSizesPoints");
		bindingContext.bindValue(text_2ObserveTextObserveWidget,
				selfAccountprofitableTradedSizesPointsObserveValue, null, null);
		//
		IObservableValue text_3ObserveTextObserveWidget = SWTObservables
				.observeText(text_3, SWT.Modify);
		IObservableValue selfAccountprofitableTradedSizesMoneyObserveValue = BeansObservables
				.observeValue(self, "account.profitableTradedSizesMoney");
		bindingContext.bindValue(text_3ObserveTextObserveWidget,
				selfAccountprofitableTradedSizesMoneyObserveValue, null, null);
		//
		IObservableValue text_4ObserveTextObserveWidget = SWTObservables
				.observeText(text_4, SWT.Modify);
		IObservableValue selfAccountlosingTradedSizesPointsObserveValue = BeansObservables
				.observeValue(self, "account.losingTradedSizesPoints");
		bindingContext.bindValue(text_4ObserveTextObserveWidget,
				selfAccountlosingTradedSizesPointsObserveValue, null, null);
		//
		IObservableValue text_5ObserveTextObserveWidget = SWTObservables
				.observeText(text_5, SWT.Modify);
		IObservableValue selfAccountlosingTradedSizesMoneyObserveValue = BeansObservables
				.observeValue(self, "account.losingTradedSizesMoney");
		bindingContext.bindValue(text_5ObserveTextObserveWidget,
				selfAccountlosingTradedSizesMoneyObserveValue, null, null);
		//
		IObservableValue text_6ObserveTextObserveWidget = SWTObservables
				.observeText(text_6, SWT.Modify);
		IObservableValue selfAccountprofitLossRatioObserveValue = BeansObservables
				.observeValue(self, "account.profitLossRatio");
		bindingContext.bindValue(text_6ObserveTextObserveWidget,
				selfAccountprofitLossRatioObserveValue, null, null);
		//
		IObservableValue text_7ObserveTextObserveWidget = SWTObservables
				.observeText(text_7, SWT.Modify);
		IObservableValue selfAccountmaxDrawdownObserveValue = BeansObservables
				.observeValue(self, "account.maxDrawdown");
		bindingContext.bindValue(text_7ObserveTextObserveWidget,
				selfAccountmaxDrawdownObserveValue, null, null);
		//
		IObservableValue text_8ObserveTextObserveWidget = SWTObservables
				.observeText(text_8, SWT.Modify);
		IObservableValue selfAccountnumberOfTradedSizesObserveValue = BeansObservables
				.observeValue(self, "account.numberOfTradedSizes");
		bindingContext.bindValue(text_8ObserveTextObserveWidget,
				selfAccountnumberOfTradedSizesObserveValue, null, null);
		//
		IObservableValue text_9ObserveTextObserveWidget = SWTObservables
				.observeText(text_9, SWT.Modify);
		IObservableValue selfAccountnumberOfWinningTradedSizesObserveValue = BeansObservables
				.observeValue(self, "account.numberOfWinningTradedSizes");
		bindingContext.bindValue(text_9ObserveTextObserveWidget,
				selfAccountnumberOfWinningTradedSizesObserveValue, null, null);
		//
		IObservableValue text_10ObserveTextObserveWidget = SWTObservables
				.observeText(text_10, SWT.Modify);
		IObservableValue selfAccountnumberOfLosingTradedSizesObserveValue = BeansObservables
				.observeValue(self, "account.numberOfLosingTradedSizes");
		bindingContext.bindValue(text_10ObserveTextObserveWidget,
				selfAccountnumberOfLosingTradedSizesObserveValue, null, null);
		//
		IObservableValue text_11ObserveTextObserveWidget = SWTObservables
				.observeText(text_11, SWT.Modify);
		IObservableValue selfAccountwinningLosingTradedSizesRatioObserveValue = BeansObservables
				.observeValue(self, "account.winningLosingTradedSizesRatio");
		bindingContext.bindValue(text_11ObserveTextObserveWidget,
				selfAccountwinningLosingTradedSizesRatioObserveValue, null,
				null);
		//
		IObservableValue text_12ObserveTextObserveWidget = SWTObservables
				.observeText(text_12, SWT.Modify);
		IObservableValue selfAccountavgProfitForWinningTradedSizesPointsObserveValue = BeansObservables
				.observeValue(self,
						"account.avgProfitForWinningTradedSizesPoints");
		bindingContext.bindValue(text_12ObserveTextObserveWidget,
				selfAccountavgProfitForWinningTradedSizesPointsObserveValue,
				null, null);
		//
		IObservableValue text_13ObserveTextObserveWidget = SWTObservables
				.observeText(text_13, SWT.Modify);
		IObservableValue selfAccountavgProfitForWinningTradedSizesMoneyObserveValue = BeansObservables
				.observeValue(self,
						"account.avgProfitForWinningTradedSizesMoney");
		bindingContext.bindValue(text_13ObserveTextObserveWidget,
				selfAccountavgProfitForWinningTradedSizesMoneyObserveValue,
				null, null);
		//
		IObservableValue text_14ObserveTextObserveWidget = SWTObservables
				.observeText(text_14, SWT.Modify);
		IObservableValue selfAccountmaxOpenTradedSizesObserveValue = BeansObservables
				.observeValue(self, "account.maxOpenTradedSizes");
		bindingContext.bindValue(text_14ObserveTextObserveWidget,
				selfAccountmaxOpenTradedSizesObserveValue, null, null);
		//
		IObservableValue text_15ObserveTextObserveWidget = SWTObservables
				.observeText(text_15, SWT.Modify);
		IObservableValue selfAccountavgOpenTradedSizeObserveValue = BeansObservables
				.observeValue(self, "account.avgOpenTradedSize");
		bindingContext.bindValue(text_15ObserveTextObserveWidget,
				selfAccountavgOpenTradedSizeObserveValue, null, null);
		//
		return bindingContext;
	}
}
