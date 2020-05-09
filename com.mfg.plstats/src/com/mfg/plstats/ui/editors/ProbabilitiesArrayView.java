package com.mfg.plstats.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

public class ProbabilitiesArrayView extends ViewPart
		implements
			ObjectListener<ProbabilitiesKey>,
			IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.ProbabilitiesArrayView"; //$NON-NLS-1$
	double[] array = new double[50];
	MfgModelTable mfgTable;
	ArrayModel model;
	private IProbabilitiesSet set;
	int target;
	private ObjectListener<Integer> targetListener;

	public ProbabilitiesArrayView() {
		Arrays.fill(array, -1);
		ProbabilitiesManager probabilitiesManager = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		probabilitiesManager.getTargetKeySelection().addObjectListener(this);
		probabilitiesManager.getTargetSelectionListener().addObjectListener(
				targetListener = new ObjectListener<Integer>() {

					@Override
					public void handle(Integer aTarget) {
						target = aTarget.intValue();
						updateSet();
						updateArray();
						doRefresh();
					}
				});
	}

	public void updateArray() {
		DistributionsContainer distributionsContainer = WidgetPlugin
				.getDefault().getProbabilitiesManager()
				.getDistributionsContainer();
		if (distributionsContainer != null) {
			array = distributionsContainer.getTargetsProbabilitiesArray(target,
					set, array);
		} else {
			Arrays.fill(array, -1);
		}
	}

	class ArrayModel implements IMfgTableModel {
		private int width = 10;
		private StepDefinition step = new StepDefinition(0.01);

		@Override
		public int getRowCount() {
			if (ProbabilitiesArrayView.this.array == null) {
				return 0;
			}
			return (int) Math.ceil(ProbabilitiesArrayView.this.array.length
					/ (new Double(width)).doubleValue());
		}

		@Override
		public Object getContent(int row, int column) {
			if (row >= ProbabilitiesArrayView.this.array.length)
				return null;
			if (column == 0) {
				int a = row * width + 1, b = a + width - 1;
				return "[" + a + "," + b + "]";
			}
			int pos = row * width + column - 1;
			double res = ProbabilitiesArrayView.this.array[pos];
			if (res < 0)
				return "";
			return Double.valueOf(step.round(res));
		}

		@Override
		public boolean isEnabled(int aRow, int aColumn) {
			return true;
		}

		private final String[] _COLUMNS = getColumns();

		public String[] getColumns() {
			ArrayList<String> list = new ArrayList<>();
			list.add("range");
			for (int i = 0; i < width; i++) {
				list.add("" + (i + 1));
			}
			return list.toArray(new String[]{});
		}

		@Override
		public String[] getColumnNames() {
			return _COLUMNS;
		}

		@Override
		public int getHighLight(int aRow, int aColumn) {
			return 0;
		}
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
		mfgTable = new MfgModelTable(container, model = new ArrayModel());
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
	public void handle(ProbabilitiesKey aEvent) {
		updateSet();
		updateArray();
		doRefresh();
	}

	public void updateSet() {
		set = WidgetPlugin.getDefault().getProbabilitiesManager()
				.getSelectedTargetSet();
	}

	public void doRefresh() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				mfgTable.setModel(model);
				mfgTable.refresh();
			}
		});
	}

	@Override
	public void dispose() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getTargetSelectionListener()
				.removeObjectListener(targetListener);
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getTargetKeySelection().removeObjectListener(this);
		mfgTable.untrackFocus(getSite());
		super.dispose();
	}

	@Override
	public MfgModelTable getMFGModelTable() {
		return mfgTable;
	}

}
