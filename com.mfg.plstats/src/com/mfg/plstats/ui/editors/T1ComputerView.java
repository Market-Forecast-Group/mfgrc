package com.mfg.plstats.ui.editors;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.utils.ObjectListener;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.T1Computer;
import com.mfg.widget.probabilities.T1Model;

public class T1ComputerView extends ViewPart
		implements
			ObjectListener<Integer>,
			IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.T1ComputerView"; //$NON-NLS-1$

	public T1ComputerView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getPatternSelection().addObjectListener(this);
		update();
	}

	private HashMap<Integer, T1Computer> t1ComputersMap;
	private IMfgTableModel model;
	MfgModelTable table;
	private int selectedPattern;
	private Text meanProb;

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.VERTICAL));

		SashForm sashForm = new SashForm(composite_1, SWT.VERTICAL);
		{
			Composite composite = new Composite(sashForm, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			Label lblMeanProbabilty = new Label(composite, SWT.NONE);
			lblMeanProbabilty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					false, false, 1, 1));
			lblMeanProbabilty.setText("Mean Probabilty");

			meanProb = new Text(composite, SWT.BORDER);
			meanProb.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			meanProb.setEditable(false);
		}

		table = new MfgModelTable(sashForm, model);
		sashForm.setWeights(new int[]{69, 396});
		table.trackFocus(getSite());

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void refresh() {
		if (selectedPattern == WidgetPlugin.getDefault()
				.getProbabilitiesManager().getSelectedPattern())
			return;
		update();
		table.setModel(model);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				table.refresh();
			}
		});
	}

	private void update() {
		ProbabilitiesManager pm = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		DistributionsContainer distributionsContainer = pm
				.getDistributionsContainer();
		if (distributionsContainer == null) {
			model = new T1Model(null);
			return;
		}
		t1ComputersMap = distributionsContainer.getT1Map();
		selectedPattern = pm.getSelectedPattern();
		T1Computer t1Computer = (selectedPattern > -1 && t1ComputersMap != null)
				? t1ComputersMap.get(Integer.valueOf(selectedPattern))
				: null;
		if (t1Computer != null) {
			model = t1Computer.getModel();
			if (meanProb != null)
				meanProb.setText(""
						+ T1Computer.StepDefinition.round(t1Computer
								.getMeanProb()));
		} else
			model = new T1Model(null);
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
		getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void handle(Integer aEvent) {
		refresh();
	}

	@Override
	public void dispose() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getPatternSelection().removeObjectListener(this);
		table.untrackFocus(getSite());
		super.dispose();
	}

	@Override
	public MfgModelTable getMFGModelTable() {
		return table;
	}
}
