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
import com.mfg.common.QueueTick;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.interfaces.trading.PositionClosedEvent;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.interfaces.trading.PositionOpenedEvent;
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
public class Lsc_AM_TabComposite extends Composite implements
		ObjectListener<QueueTick> {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public Lsc_AM_TabComposite(Composite parent, int style) {
		super(parent, style);
		_root = new ArrayList<>();
		createWidgets(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	double tickValue;

	public class MyLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof PositionClosedEvent) {
				PositionClosedEvent pos = (PositionClosedEvent) element;
				long executionTime = pos.getParentExecutionTime();
				long executionPrice = pos.getParentExecutionPrice();
				IOrderMfg myorder = pos.getOrder();

				int id = myorder.getId();
				int quantity = Math.abs(myorder.getQuantity());
				double pl = portfolio.getTick().roundLong(pos.getPL());
				double total = portfolio.getTick().roundLong(pos.getTotal());
				switch (columnIndex) {
				case 0:
					return id + "";
				case 1:
					return myorder.getType() + "";
				case 2:
					return executionTime + "";
				case 3:
					return MathUtils.getPriceFormat(portfolio.getTick()
							.roundLong(executionPrice));
				case 4:
					return pos.getExecutionTime() + "";
				case 5:
					return MathUtils.getPriceFormat(portfolio.getTick()
							.roundLong(pos.getExecutionPrice()));
				case 6:
					return MathUtils.getPriceFormat(pl);
				case 7:
					return MathUtils.getPriceFormat(total);
				case 8:
					return MathUtils.getPriceFormat(portfolio.getTick()
							.getTicksOn(pl) * tickValue);
				case 9:
					return MathUtils.getPriceFormat(portfolio.getTick()
							.getTicksOn(total) * tickValue);
				case 10:
					return quantity + "";
				case 11:
					return null; // strategy name
				case 12:
					return myorder.isLong() ? "LONG" : "SHORT";
				default:
					return "";
				}

			}
			return null;
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
			// if (aElement instanceof ElementsPatterns) {
			// ElementsPatterns ep = (ElementsPatterns) aElement;
			// return ep.getParent();
			// }
			return null;
		}

		@Override
		public boolean hasChildren(Object aElement) {
			// if (aElement instanceof PositionEvent) {
			// PositionEvent ep = (PositionEvent) aElement;
			// return ep.getOrder().getChildren().size()>0;
			// }
			return false;
		}

	}

	private static String[] _closedColumns = new String[] { "Order ID", "Type",
			"Entry Time", "Entry Price", "Exit Time", "Exit Price",
			"Price P/L", "(Tot) Price P/L", "Money P/L", "(Tot) Money P/L",
			"Quantity", "Strategy Name", "Account" };

	TreeViewer _treeViewer;
	List<?> _root;
	HashMap<Integer, PositionOpenedEvent> openedOrdersMap;
	private Runnable _updateRunnable;
	PortfolioStrategy portfolio;

	public void selected() {
		// TODO: getView().getSite().setSelectionProvider(treeViewer);
	}

	public void setTradingPipe(TradingPipe pipe) {
		if (pipe == null) {
			// Documenting empty method to avoid warning.
		} else {
			portfolio = pipe.getPortfolio();
			_root = portfolio.getClosedOrders();
			SymbolJob<?> symbolJob = pipe.getSymbolJob();
			@SuppressWarnings("unchecked")
			SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>> config = (SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>>) symbolJob
					.getSymbolConfiguration();
			tickValue = config.getInfo().getSymbol().getTickValue();
			portfolio.getTickProcessed().addObjectListener(this);
			_treeViewer.setInput(_root);
		}
	}

	@SuppressWarnings("synthetic-access")
	// Use of native method.
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

		for (String col : _closedColumns) {
			TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
			treeColumn.setText(col);
		}

		TableLayout layout = new TableLayout();
		int nColumns = _closedColumns.length;
		int weight = 50;
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

	private void initRequestQueue() {
		_updateRunnable = new Runnable() {
			int _lastSize = 0;

			@Override
			public void run() {
				Tree tree = _treeViewer.getTree();
				if (!tree.isDisposed()) {
					int size = _root.size();
					if (size != _lastSize) {
						_lastSize = size;

						_treeViewer.refresh();
						_treeViewer.expandAll();
					}
				}
			}
		};
	}

	public Runnable getUpdateRunnable() {
		return _updateRunnable;
	}

	@Override
	public void handle(QueueTick object) {
		// nothing
	}

	/**
	 * @return the treeViewer
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

}
