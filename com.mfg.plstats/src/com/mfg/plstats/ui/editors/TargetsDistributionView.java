package com.mfg.plstats.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;

public class TargetsDistributionView extends ViewPart implements
		ObjectListener<ProbabilitiesKey>, IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.TargetsDistributionView"; //$NON-NLS-1$

	public class TargetDistributionModel implements IMfgTableModel {
		@SuppressWarnings("unused")
		// Maybe used on inner classes.
		private static final long serialVersionUID = 1L;
		private ElementsPatterns elementsPatterns;
		private IProbabilitiesSet set;
		private StepDefinition step = new StepDefinition(0.01);

		public TargetDistributionModel(IProbabilitiesSet aSet,
				ElementsPatterns aElementsPatterns) {
			this.set = aSet;
			elementsPatterns = aElementsPatterns;
		}

		public void clear() {
			// Documenting method to avoid warning.
		}

		@Override
		public int getRowCount() {
			if (set == null) {
				return 0;
			}
			return set.getMaxTargetCount();
		}

		private final String[] _COLUMNS = new String[] { "TID", "Target(>=)",
				"Reached", "Not Reached", "Total", "Probability", "Cond Prob" };

		@Override
		public String[] getColumnNames() {
			return _COLUMNS;
		}

		@Override
		public Object getContent(int row, int column) {
			int target = row + 1;
			switch (column) {
			case 0:
				return Integer.valueOf(target);
			case 1:
				return Double.valueOf(elementsPatterns.getTarget(target));
			case 2:
				return Integer.valueOf(set.getTargetCount(target));
			case 3:
				return Integer.valueOf(set.getTargetOffCount(target));
			case 4:
				return Integer.valueOf(set.getTargetCount(target)
						+ set.getTargetOffCount(target));
			case 5:
				double tp = set.getTargetProbability(target);
				if (!Double.isInfinite(tp)) {
					return Double.valueOf(step.round(tp));
				}
				return "Inf";
			default:
				if (target > 1)
					tp = set.getTargetProbability(target - 1, target);
				else
					tp = set.getTargetProbability(target);
				if (!Double.isInfinite(tp)) {
					return Double.valueOf(step.round(tp));
				}
				return "Inf";
			}
		}

		@Override
		public boolean isEnabled(int aRow, int aColumn) {
			return true;
		}

		@Override
		public int getHighLight(int aRow, int aColumn) {
			return 0;
		}
	}

	TargetDistributionModel model;
	MfgModelTable mfgTable;

	public TargetsDistributionView() {
		ProbabilitiesManager pm = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		pm.getTargetKeySelection().addObjectListener(this);
		setPartName("Targets Distribution for " + pm.getSelectedTargetKey());
		model = new TargetDistributionModel(pm.getSelectedTargetSet(),
				getPatt(pm));

	}

	private static ElementsPatterns getPatt(ProbabilitiesManager pm) {
		ProbabilitiesKey selectedTargetKey = pm.getSelectedTargetKey();
		return selectedTargetKey != null ? selectedTargetKey.getPattern()
				: null;
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		mfgTable = new MfgModelTable(container, model);
		mfgTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				int idx = mfgTable.getSelectedIndex();
				System.out.println("sel " + idx);
				final ProbabilitiesManager pm = WidgetPlugin.getDefault()
						.getProbabilitiesManager();
				pm.setTargetSelected(idx + 1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent aE) {
				// TODO Auto-generated method stub

			}
		});

		mfgTable.trackFocus(getSite());

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

	@Override
	public void dispose() {
		mfgTable.untrackFocus(getSite());
		super.dispose();
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
	public void handle(ProbabilitiesKey aEvent) {
		final ProbabilitiesManager pm = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		model = new TargetDistributionModel(pm.getSelectedTargetSet(),
				getPatt(pm));
		Display.getDefault().asyncExec(new Runnable() {
			@SuppressWarnings("synthetic-access")
			// SetPartName is a private method.
			@Override
			public void run() {
				setPartName("Targets Distribution for "
						+ pm.getSelectedTargetKey());
				mfgTable.setModel(model);
				mfgTable.refresh();
			}
		});
	}

	@Override
	public MfgModelTable getMFGModelTable() {
		return mfgTable;
	}
}
