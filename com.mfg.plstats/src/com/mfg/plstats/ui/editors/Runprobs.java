
package com.mfg.plstats.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class Runprobs extends ViewPart {

	public static final String ID = "com.mfg.plstats.ui.editors.Runprobs"; //$NON-NLS-1$


	public Runprobs() {
	}


	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			{
				Button btnBuildProbabilities = new Button(composite, SWT.NONE);
				btnBuildProbabilities.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// ProbabilitiesJob job = new ProbabilitiesJob();
						// job.schedule();
					}
				});
				btnBuildProbabilities.setText("Build Probabilities");
			}
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

}
