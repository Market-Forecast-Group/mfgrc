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
package com.mfg.chart.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class ShowObjectInChart extends Action implements IMenuCreator {
	private final Object object;
	private Menu menu;

	public ShowObjectInChart(String text, Object obj) {
		super(text, ChartPlugin
				.getBundledImageDescriptor(ChartPlugin.CHART_ICON_PATH));
		this.object = obj;
		setMenuCreator(this);
	}

	public ShowObjectInChart(Object obj) {
		this("Show in Chart", obj);
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	@Override
	public void run() {
		Object obj = getObject();
		showObject(obj);
	}

	public static void showObject(Object obj) {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		ChartView viewToOpen = null;
		// activate a chart with same content
		for (ChartView view : views) {
			if (obj.equals(view.getContent())) {
				viewToOpen = view;
				break;
			}
		}
		if (viewToOpen != null) {
			PartUtils.activatePart(viewToOpen);
			return;
		}

		// activate chart with empty content
		// if (viewToOpen == null) {
		for (ChartView view : views) {
			if (view.getContent() == null) {
				viewToOpen = view;
				break;
			}
		}
		// }
		if (viewToOpen != null) {
			PartUtils.activatePart(viewToOpen);
			viewToOpen.setContent(obj);
			return;
		}

		// open new view
		ChartView view = PartUtils.openView(ChartView.VIEW_ID, true);
		if (view != null) {
			view.setContent(obj);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	@Override
	public void dispose() {
		if (this.menu != null) {
			this.menu.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
	 * .Control)
	 */
	@Override
	public Menu getMenu(Control parent) {
		return fillMenu(new Menu(parent));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets
	 * .Menu)
	 */
	@Override
	public Menu getMenu(Menu parent) {
		return fillMenu(new Menu(parent));
	}

	/**
	 * @param parent
	 * @return
	 */
	protected Menu fillMenu(Menu menu1) {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.menu = menu1;
		MenuItem item = new MenuItem(menu1, SWT.PUSH);
		item.setText("Show in New Chart");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<ChartView> views = PartUtils
						.getOpenViews(ChartView.VIEW_ID);
				// check for an empty chart
				for (ChartView view : views) {
					if (view.getContent() == null) {
						PartUtils.activatePart(view);
						view.setContent(getObject());
						return;
					}
				}
				ChartView view = PartUtils.openView(ChartView.VIEW_ID, true);
				if (view != null) {
					view.setContent(getObject());
				}
			}

		});
		return menu1;
	}
}
