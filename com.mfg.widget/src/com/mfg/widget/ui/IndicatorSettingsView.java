package com.mfg.widget.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.utils.ObjectListener;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.probabilities.DistributionsContainer;

public class IndicatorSettingsView extends ViewPart implements
		ObjectListener<DistributionsContainer> {

	public static final String ID = "com.mfg.widget.ui.IndicatorSettingsView"; //$NON-NLS-1$
	IndicatorParamBean indicator;
	IndicatorROSettingsComposite composite;

	public IndicatorSettingsView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		getTheProbabilityConfig();
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
			getTheProbabilityConfig();
			composite = new IndicatorROSettingsComposite(scrolledComposite,
					SWT.NONE, new IIndicatorSettingsContainer() {

						@Override
						public IndicatorParamBean getIndicatorSettings() {
							return indicator;
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
		getTheProbabilityConfig();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				composite.refreshValues();
			}
		});
	}

	private void getTheProbabilityConfig() {
		DistributionsContainer dc = WidgetPlugin.getDefault()
				.getProbabilitiesManager().getDistributionsContainer();
		if (dc != null) {
			indicator = (IndicatorParamBean) dc.getIndicatorConfiguration()
					.getIndicatorSettings();
		} else {
			indicator = new IndicatorParamBean();
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
