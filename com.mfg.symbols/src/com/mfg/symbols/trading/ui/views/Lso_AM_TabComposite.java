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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.common.QueueTick;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.interfaces.trading.PositionOpenedEvent;
import com.mfg.strategy.PendingOrderInfo;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.utils.MathUtils;
import com.mfg.utils.ObjectListener;

/**
 * @author arian
 * 
 */
public class Lso_AM_TabComposite extends Composite implements
		ObjectListener<QueueTick> {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public Lso_AM_TabComposite(Composite parent, int style) {
		super(parent, style);
		_root = new ArrayList<>();
		createWidgets(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	// @SuppressWarnings("unused")
	// Maybe used on inner or inherit classes
	// private StepDefinition tick;
	long currentPrice;

	public class MyLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			//
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof PositionOpenedEvent) {
				PositionOpenedEvent pos = (PositionOpenedEvent) element;
				long executionTime = pos.getExecutionTime();
				long executionPrice = pos.getExecutionPrice();
				IOrderMfg myorder = pos.getOrder();
				int gsign = OrderUtils.getSign(myorder.getType());
				return getOrderInfo(columnIndex, executionTime, executionPrice,
						myorder, gsign);
			}
			if (element instanceof PendingOrderInfo) {
				PendingOrderInfo pos = (PendingOrderInfo) element;
				IOrderMfg myorder = pos.getOrder();
				long executionTime = pos.getTime();
				long executionPrice = (myorder.getExecType() != EXECUTION_TYPE.MARKET) ? myorder
						.getOpeningPrice() : 0;
				int gsign = 0;
				return getOrderInfo(columnIndex, executionTime, executionPrice,
						myorder, gsign);
			}
			if (element instanceof IOrderMfg) {
				IOrderMfg ord = (IOrderMfg) element;
				Double auxPrice = getAux(ord);
				switch (columnIndex) {
				case 0:
					return ord.getId() + "";
				case 1:
					return ord.getType() + "";
				case 2:
					return "";
				case 3:
					return MathUtils.getPriceFormat(portfolio.getTick()
							.roundLong(ord.getOpeningPrice()));
				case 4:
					return auxPrice == null ? "" : MathUtils
							.getPriceFormat(auxPrice.doubleValue());
				case 5:
					return "";
				case 6:
					return "";
				case 7:
					return ord.getQuantity() + "";
				case 8:
					return null; // strategy name
				case 9:
					return ord.isLong() ? "LONG" : "SHORT";
				case 10:
					return "Pending";
				default:
					break;
				}
			}
			return null;
		}

		private Double getAux(IOrderMfg ord) {
			if (ord.getExecType() == EXECUTION_TYPE.MARKET)
				return null;
			return Double.valueOf(portfolio.getTick().roundLong(
					ord.getAuxPrice()));
		}

		private String getOrderInfo(int columnIndex, long executionTime,
				long executionPrice, IOrderMfg myorder, int gsign) {
			int id = myorder.getId();
			int quantity = Math.abs(myorder.getQuantity());
			int sign = OrderUtils.getSign(myorder.getType()) * gsign;
			double pl = portfolio.getTick().roundLong(
					-sign * gsign * executionPrice * quantity + gsign
							* quantity * currentPrice);
			Double auxPrice = getAux(myorder);
			switch (columnIndex) {
			case 0:
				return id + "";
			case 1:
				return myorder.getType() + "";
			case 2:
				return executionTime + "";
			case 3:
				return MathUtils.getPriceFormat(portfolio.getTick().roundLong(
						executionPrice));
			case 4:
				return auxPrice == null ? "" : MathUtils
						.getPriceFormat(auxPrice.doubleValue());
			case 5:
				return MathUtils.getPriceFormat(pl);
			case 6:
				return MathUtils.getPriceFormat(portfolio.getTick().getTicksOn(
						pl)
						* tickValue);
			case 7:
				return quantity + "";
			case 8:
				return null; // strategy name
			case 9:
				return myorder.isLong() ? "LONG" : "SHORT";
			case 10:
				return (gsign != 0) ? "Opened" : "Pending";
			default:
				return "";
			}
		}

	}

	private class MyContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// Documenting empty method to avoid warning.
		}

		@Override
		public void inputChanged(Viewer aViewer, Object aOldInput,
				Object aNewInput) {
			// Documenting empty method to avoid warning.
		}

		@Override
		public Object[] getElements(Object aInputElement) {
			return getChildren(aInputElement);
		}

		@Override
		public Object[] getChildren(Object aParentElement) {
			if (aParentElement instanceof List) {
				return ((List<?>) aParentElement).toArray();
			}
			if (aParentElement instanceof PositionEvent) {
				PositionEvent ep = (PositionEvent) aParentElement;
				return ep.getOrder().getChildren().toArray();
			}
			return new Object[] {};
		}

		@Override
		public Object getParent(Object aElement) {
			if (aElement instanceof IOrderMfg) {
				IOrderMfg order = (IOrderMfg) aElement;
				return openedOrdersMap.get(Integer.valueOf(order.getParent()
						.getId()));
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object aElement) {
			if (aElement instanceof PositionEvent) {
				PositionEvent ep = (PositionEvent) aElement;
				return ep.getOrder().getChildren().size() > 0;
			}
			return false;
		}

	}

	private static String[] _openColumns = new String[] { "Order ID", "Type",
			"Order Time", "Order Price", "Aux Price", "Price P/L", "Money P/L",
			"Quantity", "Strategy Name", "Account", "Status" };

	TreeViewer _treeViewer;
	List<?> _root;
	HashMap<Integer, PositionOpenedEvent> openedOrdersMap;
	private Runnable _updateRunnable;
	double tickValue;
	PortfolioStrategy portfolio;

	public void selected() {
		//
	}

	public void setTradingPipe(TradingPipe pipe) {
		if (pipe == null) {
			// Documenting empty method to avoid warning.
		} else {
			portfolio = pipe.getPortfolio();
			_root = portfolio.getTradingOrders();
			openedOrdersMap = portfolio.getOpenedOrdersMap();
			SymbolJob<?> symbolJob = pipe.getSymbolJob();
			@SuppressWarnings("unchecked")
			SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>> config = (SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>>) symbolJob
					.getSymbolConfiguration();
			tickValue = config.getInfo().getSymbol().getTickValue();
			portfolio.getTickProcessed().addObjectListener(this);
			// to update the table with the current portfolio's state
			currentPrice = portfolio.getCurrentPrice();
			_treeViewer.setInput(_root);
		}
	}

	@SuppressWarnings("synthetic-access")
	// Use of native methods.
	private void createWidgets(Composite parent) {
		parent.setLayout(new FillLayout());
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		// mfgTable = new MfgModelTable(container, getModel());
		// mfgTable.addSelectionListener(this);

		this._treeViewer = new TreeViewer(container, SWT.FULL_SELECTION
				| SWT.MULTI);
		// treeViewer.addSelectionChangedListener(this);
		Tree tree = this._treeViewer.getTree();

		// final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true,
		// true);
		// this.treeViewer.getControl().setLayoutData(gridData);
		this._treeViewer.setUseHashlookup(true);
		// this.treeViewer.setAutoExpandLevel(3);

		/*** Tree table specific code starts ***/

		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		for (String col : _openColumns) {
			TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
			treeColumn.setText(col);
		}

		TableLayout layout = new TableLayout();
		int nColumns = _openColumns.length;
		int weight = 80;
		for (int i = 0; i < nColumns; i++) {
			layout.addColumnData(new ColumnWeightData(weight));
		}

		tree.setLayout(layout);

		/*** Tree table specific code ends ***/

		this._treeViewer.setContentProvider(new MyContentProvider());
		this._treeViewer.setLabelProvider(new MyLabelProvider());
		this._treeViewer.setInput(this._root);

		initRequestQueue();
	}

	/**
	 * 
	 */
	private void initRequestQueue() {
		_updateRunnable = new Runnable() {
			@Override
			public void run() {
				Tree tree = _treeViewer.getTree();
				if (!tree.isDisposed()) {
					_treeViewer.refresh();
					_treeViewer.expandAll();
				}
			}
		};
	}

	public Runnable getUpdateRunnable() {
		return _updateRunnable;
	}

	@Override
	public void handle(QueueTick object) {
		currentPrice = object.getPrice();
	}

	/**
	 * @return the treeViewer
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

}
