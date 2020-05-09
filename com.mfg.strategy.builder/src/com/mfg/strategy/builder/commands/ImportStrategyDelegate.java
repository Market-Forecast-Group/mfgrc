
package com.mfg.strategy.builder.commands;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

public class ImportStrategyDelegate extends ActionDelegate implements IViewActionDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		new ImportAction().run();
	}


	@Override
	public void init(IViewPart aView) {
		// TODO Auto-generated method stub

	}
}
