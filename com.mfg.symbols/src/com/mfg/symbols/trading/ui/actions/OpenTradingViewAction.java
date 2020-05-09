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
package com.mfg.symbols.trading.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class OpenTradingViewAction extends Action implements IMenuCreator {
	private Menu menu;
	final String viewId;
	private final String viewName;
	private final TradingConfiguration configuration;

	public OpenTradingViewAction(TradingConfiguration aConfiguration,
			String aViewId, String aViewName, String pluginId, String imgPath) {
		super("Open In " + aViewName, ImageUtils.getBundledImageDescriptor(
				pluginId, imgPath));
		this.viewId = aViewId;
		this.viewName = aViewName;
		setToolTipText("Open In " + aViewName);
		this.configuration = aConfiguration;
		setMenuCreator(this);
	}

	/**
	 * @return the configuration_
	 */
	public TradingConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		TradingConfiguration config = getConfiguration();
		if (config != null) {
			SymbolsPlugin.getDefault();
			List<ITradingView> views = SymbolsPlugin
					.getOpenTradingViews(viewId);
			boolean set = false;
			for (ITradingView view : views) {
				if (view.getConfiguration() == config) {
					set = true;
					view.setConfiguration(config);
				}
			}
			if (!set) {
				for (ITradingView view : views) {
					if (view.getConfiguration() == null) {
						set = true;
						view.setConfiguration(config);
						break;
					}
				}
			}
			if (!set) {
				ITradingView view = (ITradingView) PartUtils.openView(viewId);
				view.setConfiguration(config);
			}
		}
	}

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
	protected Menu fillMenu(Menu aMenu) {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.menu = aMenu;
		MenuItem item = new MenuItem(aMenu, SWT.PUSH);
		item.setText("Open In New " + viewName);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ITradingView view = (ITradingView) PartUtils.openView(viewId,
						true);
				view.setConfiguration(getConfiguration());
			}

		});
		return aMenu;
	}
}
