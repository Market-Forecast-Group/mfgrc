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

package com.mfg.chart.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.Utils;
import com.mfg.utils.ui.IViewWithTable;
import com.mfg.utils.ui.actions.CopyStructuredSelectionAction;

/**
 * @author arian
 * 
 */
public class ChartDBBrowserView extends ViewPart implements IViewWithTable {
	public ChartDBBrowserView() {
	}

	public static final String VIEW_ID = "com.mfg.chart.ui.views.ChartMDBSessionView";
	// private PropertyChangeListener closeSessionListener;
	private ChartDBBrowserComposite comp;
	private Object _content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		// closeSessionListener = new PropertyChangeListener() {
		//
		// @Override
		// public void propertyChange(final PropertyChangeEvent evt) {
		// setSession(null);
		// }
		// };

		updateContent(parent);

		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				new CopyStructuredSelectionAction());
		actionBars.updateActionBars();
	}

	private void updateContent(final Composite parent) {
		comp = new ChartDBBrowserComposite(parent, SWT.NONE);
		getViewSite().setSelectionProvider(comp.getTableViewer());
	}

	@Override
	public void setFocus() {
		comp.setFocus();
	}

	/**
	 * @param session
	 */
	public void setSession(final PriceMDBSession priceSession,
			final IndicatorMDBSession indicatorSession,
			final TradingMDBSession tradingSession, final Object content) {

		Display.getDefault().asyncExec(new Runnable() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				setPartName((content == null) ? "Chart DB Browser"
						: "Chart DB Browser - " + content.toString());

				Utils.debug_id(865896, "Reconnect db browser with " + content);

				_content = content;
				Composite parent = comp.getParent();
				comp.dispose();
				updateContent(parent);
				comp.setSessions(priceSession, indicatorSession, tradingSession);

			}
		});

	}

	public Object getContent() {
		return _content;
	}

	@Override
	public Table getTable() {
		return comp.getTableViewer().getTable();
	}

}
