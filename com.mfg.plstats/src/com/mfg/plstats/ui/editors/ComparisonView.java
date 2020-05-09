package com.mfg.plstats.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionComparison;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.DistributionsDistances;

public class ComparisonView extends ViewPart implements IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.ComparisonView"; //$NON-NLS-1$
	private final FormToolkit ftoolkit = new FormToolkit(Display.getCurrent());
	int selectedPattern;
	private DistributionComparison[][] comparison;
	DistributionsContainer distributionsContainer;
	HashMap<Integer, ElementsPatterns> emap;
	IMfgTableModel model = new ComparisonModel(comparison,
			distributionsContainer);
	MfgModelTable table;
	private ObjectListener<DistributionsContainer> distributionListener;
	protected DistributionsDistances distributionsDistances;
	private ObjectListener<Integer> patternListener;
	Composite container;
	public ComparisonView() {
		WidgetPlugin
				.getDefault()
				.getProbabilitiesManager()
				.getPatternSelection()
				.addObjectListener(
						patternListener = new ObjectListener<Integer>() {
							@Override
							public void handle(Integer aO) {
								selectedPattern = aO.intValue();
								refresh();
							}
						});
		WidgetPlugin
				.getDefault()
				.getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.addObjectListener(
						distributionListener = new ObjectListener<DistributionsContainer>() {

							@Override
							public void handle(DistributionsContainer aO) {
								distributionsContainer = aO;
								if (distributionsContainer != null) {
									distributionsDistances = new DistributionsDistances(
											distributionsContainer);
									emap = new HashMap<>();
									for (ElementsPatterns e : distributionsContainer
											.getElementsPatternsRoot()
											.getLeaves(
													new ArrayList<ElementsPatterns>())) {
										emap.put(Integer.valueOf(e.getLeafID()), e);
									}
									update();
									Display.getDefault().asyncExec(
											new Runnable() {

												@Override
												public void run() {
													table.dispose();
													table.untrackFocus(getSite());
													table = new MfgModelTable(
															container, model);
													table.trackFocus(getSite());
													container.layout();
												}
											});
								}

							}
						});
		distributionListener.handle(WidgetPlugin.getDefault()
				.getProbabilitiesManager().getDistributionsContainer());
	}
	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		container = ftoolkit.createComposite(parent, SWT.NONE);
		ftoolkit.paintBordersFor(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		table = new MfgModelTable(container, model);
		table.trackFocus(getSite());
		createActions();
		initializeToolBar();
		initializeMenu();
	}
	@Override
	public void dispose() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.removeObjectListener(distributionListener);
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getPatternSelection().removeObjectListener(patternListener);
		table.untrackFocus(getSite());
		super.dispose();
		ftoolkit.dispose();
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

	public void refresh() {
		// if (selectedPattern == WidgetPlugin.getDefault()
		// .getProbabilitiesManager().getSelectedPattern())
		// return;
		update();
		table.setModel(model);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				container.layout();
			}
		});
	}
	void update() {
		ProbabilitiesManager pm = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		DistributionsContainer aDistributionsContainer = pm
				.getDistributionsContainer();
		selectedPattern = pm.getSelectedPattern();
		if (aDistributionsContainer == null || emap == null) {
			return;
		}
		comparison = distributionsDistances.comprareScales(emap
				.get(Integer.valueOf(selectedPattern)));
		model = new ComparisonModel(comparison, aDistributionsContainer);
	}
	@Override
	public MfgModelTable getMFGModelTable() {
		return table;
	}

}
