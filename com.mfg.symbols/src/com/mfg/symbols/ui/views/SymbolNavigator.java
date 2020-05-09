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
package com.mfg.symbols.ui.views;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.mfg.symbols.ui.WorkbenchSymbolsSelectionListener;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class SymbolNavigator extends CommonNavigator {
	public static final String VIEW_ID = "com.mfg.symbols.ui.views.navigator";
	private static final String EXPANDED_ITEMS_KEY = "com.mfg.symbols.ui.views.navigator.expandedItems";
	public static final ISymbolNavigatorRoot ROOT_NODE = new ISymbolNavigatorRoot() {
		// Adding a comment to avoid empty block warning.
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.navigator.CommonNavigator#getInitialInput()
	 */
	@Override
	protected Object getInitialInput() {
		return ROOT_NODE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonNavigator#saveState(org.eclipse.ui.IMemento
	 * )
	 */
	@Override
	public void saveState(IMemento aMemento) {
		super.saveState(aMemento);
		PartUtils.saveTreeExpansionState(aMemento, EXPANDED_ITEMS_KEY,
				getCommonViewer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonNavigator#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite aSite, final IMemento aMemento)
			throws PartInitException {
		super.init(aSite, aMemento);
		getViewSite().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				CommonViewer viewer = getCommonViewer();
				PartUtils.expandTreeFromSavedState(aMemento,
						EXPANDED_ITEMS_KEY, viewer);
			}
		});
		// we have to activate this context because some commands are connected
		// to this that
		IContextService serv = (IContextService) getViewSite().getService(
				IContextService.class);
		serv.activateContext("com.mfg.ui.navigatorContext");
	}

	@Override
	protected void initListeners(final TreeViewer viewer) {
		super.initListeners(viewer);
		// little hack to interprets selection only for single clicks.
		viewer.getTree().addMouseListener(new MouseAdapter() {
			WorkbenchSymbolsSelectionListener listener = new WorkbenchSymbolsSelectionListener();

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					listener.selectionChanged(new SelectionChangedEvent(viewer,
							viewer.getSelection()));
					setFocus();
				}
			}
		});
	}
}
