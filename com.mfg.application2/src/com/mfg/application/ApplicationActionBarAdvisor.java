package com.mfg.application;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.persist.interfaces.SaveWorkspaceAction;
import com.mfg.ui.widgets.DfsStatusIndicator;
//import com.mfg.connector.esignal.ui.ESignalStatusIndicator;
//import com.mfg.connector.ib.ui.TWS_StatusIndicator;

@SuppressWarnings("restriction")
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	// private IContributionItem viewList;
	// private IWorkbenchAction redoAction;
	// private IWorkbenchAction undoAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction importAction;
	private IWorkbenchAction newAction;
	private IWorkbenchAction saveAsAction;
	private IAction helpContentAction;
	private IWorkbenchAction helpSearchAction;
	private IWorkbenchAction exportAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction copyAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(new ControlContribution("DFSIndicator") {

			@Override
			protected Control createControl(Composite parent) {

				boolean useProxy = DFSPlugin.getDefault().getPreferenceStore()
						.getBoolean(DFSPlugin.USE_PROXY);
				return DfsStatusIndicator.getInstance().createControl(useProxy,
						parent);
			}
		});
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		saveAction = ActionFactory.SAVE.create(window);
		saveAsAction = ActionFactory.SAVE_AS.create(window);
		importAction = ActionFactory.IMPORT.create(window);
		exportAction = ActionFactory.EXPORT.create(window);
		newAction = ActionFactory.NEW.create(window);
		copyAction = ActionFactory.COPY.create(window);
		helpContentAction = ActionFactory.HELP_CONTENTS.create(window);
		helpSearchAction = ActionFactory.HELP_SEARCH.create(window);

		register(saveAction);
		register(saveAsAction);
		register(copyAction);
		register(helpSearchAction);
		register(helpContentAction);
		register(importAction);
		register(exportAction);
		register(newAction);

		ActionSetRegistry reg = WorkbenchPlugin.getDefault()
				.getActionSetRegistry();
		IActionSetDescriptor[] actionSets = reg.getActionSets();

		for (int i = 0; i < actionSets.length; i++) {
			if (!actionSets[i].getId().startsWith("com.mfg")) {
				IExtension ext = actionSets[i].getConfigurationElement()
						.getDeclaringExtension();
				reg.removeExtension(ext, new Object[] { actionSets[i] });
			}
		}

		// About actions
		// @Karell
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillActionBars(int)
	 */
	@Override
	public void fillActionBars(int flags) {
		// TODO Auto-generated method stub
		super.fillActionBars(flags);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		MenuManager editMenu = new MenuManager("&Edit",
				IWorkbenchActionConstants.M_EDIT);
		MenuManager windowMenu = new MenuManager("&Window",
				IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		fileMenu.add(SaveWorkspaceAction.getDefault());
		fileMenu.add(newAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.add(importAction);

		editMenu.add(copyAction);
		
		helpMenu.add(helpContentAction);
		helpMenu.add(helpSearchAction);

		// About> Help menu
		// @Karell
		helpMenu.add(new Separator());
		GroupMarker additions = new GroupMarker(
				IWorkbenchActionConstants.MB_ADDITIONS);
		helpMenu.add(additions);
		helpMenu.add(new Separator());
		helpMenu.add(aboutAction);
	}
}
