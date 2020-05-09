
package com.mfg.strategy.builder.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mfg.strategy.builder.model.EventsCanvasModel;

public class GeneralStrategyStettingsView extends ViewPart {

	public static final String ID = "com.mfg.strategy.builder.ui.GeneralStrategyStettingsView"; //$NON-NLS-1$
	private GeneralSettingsComposite container;


	public GeneralStrategyStettingsView() {
	}


	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		container = new GeneralSettingsComposite(parent, SWT.NONE);

		createActions();
		initializeToolBar();
		initializeMenu();
	}


	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}


	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		getViewSite().getActionBars().getToolBarManager();
	}


	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars().getMenuManager();
	}


	@Override
	public void setFocus() {
		// Set the focus
	}


	public EventsCanvasModel getModel() {
		return container.getModel();
	}


	public void setModel(EventsCanvasModel aModel) {
		container.setModel(aModel);
	}

}
