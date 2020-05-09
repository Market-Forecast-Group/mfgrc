package com.marketforecastgroup.dfsa.application;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.mfg.ui.widgets.DfsStatusIndicator;

//import com.jdfsarc.dataprovider.esignal.DataproviderStatus;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction pref;
	private IWorkbenchAction aboutAction;

	// Help content
	private IWorkbenchAction showHelpAction;
	private IWorkbenchAction searchHelpAction;
	private IWorkbenchAction dynamicHelpAction;
	private IAction copyAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		copyAction = ActionFactory.COPY.create(window);
		register(copyAction);
		
		pref = ActionFactory.PREFERENCES.create(window);
		aboutAction = ActionFactory.ABOUT.create(window);
		aboutAction.setText("About DFSA");
		register(aboutAction);

		// Help content
		showHelpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(showHelpAction);

		searchHelpAction = ActionFactory.HELP_SEARCH.create(window);
		register(searchHelpAction);

		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(dynamicHelpAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		
		MenuManager editMenu = new MenuManager("&Edit",
				IWorkbenchActionConstants.M_EDIT);
		
		MenuManager windowMenu = new MenuManager("&Window",
				IWorkbenchActionConstants.M_WINDOW);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(windowMenu);

		windowMenu.add(pref);

		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);

		
		// Edit content
		editMenu.add(copyAction);
		
		// Help content
		helpMenu.add(showHelpAction);
		helpMenu.add(searchHelpAction);

		helpMenu.add(aboutAction);

	}

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(new ControlContribution("feedIndicator") {

			@Override
			protected Control createControl(Composite parent) {
				return DfsStatusIndicator.getInstance().createControl(true, parent);
			}
		});
	}
}
