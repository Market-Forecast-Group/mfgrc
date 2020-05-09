package com.mfg.widget.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.trading.Configuration;
import com.mfg.utils.ObjectListener;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.interfaces.IProbabilitiesSettingsContainer;
import com.mfg.widget.probabilities.DistributionsContainer;

public class ProbabilitiesSettingsView extends ViewPart implements
		ObjectListener<DistributionsContainer> {

	public static final String ID = "com.mfg.widget.ui.ProbabilitiesSettingsView"; //$NON-NLS-1$
	Configuration configuration;
	ProbabilitiesSettingsROComposite composite;

	public ProbabilitiesSettingsView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		getTheConfiguration();
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		{
			ScrolledComposite scrolledComposite = new ScrolledComposite(parent,
					SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			scrolledComposite.setExpandHorizontal(true);
			scrolledComposite.setExpandVertical(true);
			// Composite container = new Composite(parent, SWT.NONE);
			composite = new ProbabilitiesSettingsROComposite(scrolledComposite,
					SWT.NONE, new IProbabilitiesSettingsContainer() {

						@Override
						public Configuration getConfiguration() {
							return configuration;
						}
					});
			scrolledComposite.setContent(composite);
			scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
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
	public void handle(DistributionsContainer aEvent) {
		getTheConfiguration();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				composite.refreshValues();
			}
		});
	}

	public void getTheConfiguration() {
		DistributionsContainer dc = WidgetPlugin.getDefault()
				.getProbabilitiesManager().getDistributionsContainer();
		if (dc != null) {
			configuration = dc.getConfiguration();
		} else {
			configuration = new Configuration();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.removeObjectListener(this);

	}

}
