package com.mfg.strategy;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.Utils;

public class ConfirmationQueue extends ViewPart implements
		ObjectListener<IConfirmationRequest> {

	ConformationQueueComposite composite;

	static List<IConfirmationRequest> getTheList() {
		return SymbolsPlugin.getDefault().getConfirmationsRequests();
	}

	public static final String ID = "com.mfg.strategy.ConfirmationQueue"; //$NON-NLS-1$

	public ConfirmationQueue() {
		SymbolsPlugin.getDefault().getConfirmationsRequestsAdded()
				.addObjectListener(this);
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			composite = new ConformationQueueComposite(parent, SWT.NONE);

		}

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

	@Override
	public void handle(final IConfirmationRequest aObject) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				composite.handle(aObject);
				composite.redraw();
				composite.update();
			}
		});
	}

	protected static void refreshTable() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Utils.debug_var(12345, "confirmation added " + getTheList().size());
				// table.setModel(model);
				// tableViewer.refresh();
			}
		});
	}

}
