package com.mfg.plstats.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

public class ProbabilitiesSetView extends ViewPart implements
		ObjectListener<DistributionsContainer>, IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.ProbabilitiesSetView"; //$NON-NLS-1$
	ProbabilitiesKey[] list;
	MfgModelTable mfgTable;
	ProbabilitiesSetModel model;
	private Configuration configuration;

	public ProbabilitiesSetView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		takeData(WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainer());
	}

	private static List<ProbabilitiesKey> getKeys() {
		DistributionsContainer distributionsContainer = WidgetPlugin
				.getDefault().getProbabilitiesManager()
				.getDistributionsContainer();
		if (distributionsContainer == null) {
			return new ArrayList<>();
		}
		return distributionsContainer.getAllKeys();
	}

	public ProbabilitiesSetView(List<ProbabilitiesKey> aList) {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
		setList(aList);
	}

	public void setList(List<ProbabilitiesKey> aList) {
		list = aList.toArray(new ProbabilitiesKey[] {});
		Arrays.sort(list, new Comparator<ProbabilitiesKey>() {
			@Override
			public int compare(ProbabilitiesKey aO1, ProbabilitiesKey aO2) {
				int s = (int) Math.signum(aO1.getScale() - aO2.getScale());
				if (s != 0)
					return s;
				s = (int) Math.signum(aO1.getPatternID() - aO2.getPatternID());
				if (s != 0)
					return s;
				s = (int) Math.signum(aO1.getClusterID() - aO2.getClusterID());
				if (s != 0)
					return s;
				return (aO1.isContrarian() ? 0 : 1)
						- (aO2.isContrarian() ? 0 : 1);
			}
		});
		// if (mfgTable != null) {
		// mfgTable.redraw();
		// }
	}

	class ProbabilitiesSetModel implements IMfgTableModel {
		@Override
		public int getRowCount() {
			if (ProbabilitiesSetView.this.list == null) {
				return 0;

			}
			return ProbabilitiesSetView.this.list.length;
		}

		@Override
		public Object getContent(int row, int column) {
			if (row >= ProbabilitiesSetView.this.list.length)
				return null;
			ProbabilitiesKey k = ProbabilitiesSetView.this.list[row];
			switch (column) {
			case 0:
				return Integer.valueOf(k.getPatternID());
			case 1:
				return Integer.valueOf(k.getScale());
			case 2:
				return Integer.valueOf(getHSMAX() - k.getScale()
						+ k.getBaseScale());
			case 3:
				return k.getClusterID() == 0 ? "*" : Integer.valueOf(k
						.getClusterID());
			case 4:
				return k.getClusterID() == 0 ? "*" : (k.isContrarian() ? "C"
						: "NC");
			case 5:
				return k.getPriceClusterID() == 0 ? "*" : Integer.valueOf(k
						.getPriceClusterID());
			case 6:
				return k.getTimeClusterID() == 0 ? "*" : Integer.valueOf(k
						.getTimeClusterID());
			default:
				return null;
			}
		}

		@Override
		public boolean isEnabled(int row, int aColumn) {
			if (row >= ProbabilitiesSetView.this.list.length)
				return true;
			ProbabilitiesKey k = ProbabilitiesSetView.this.list[row];
			switch (aColumn) {
			case 0:
				return k.getPatternID() > 0;
			case 3:
			case 4:
				return (k.getClusterID() != 0);
			case 5:
				return k.getPriceClusterID() != 0;
			case 6:
				return k.getTimeClusterID() != 0;
			default:
				return true;
			}
		}

		private final String[] _COLUMNS = new String[] { "PID", "Sc",
				"HSc Num", "CID", "Dir", "PCID", "TCID" };

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
		mfgTable = new MfgModelTable(container,
				model = new ProbabilitiesSetModel());
		mfgTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				int idx = mfgTable.getSelectedIndex();
				ProbabilitiesManager man = WidgetPlugin.getDefault()
						.getProbabilitiesManager();
				ProbabilitiesKey key = list[idx];
				man.setSelectedTargetKey(key);

				IProbabilitiesFilter filter = WidgetPlugin.getDefault()
						.getProbabilitiesManager().getProbabilitiesLogFilter();
				filter.setProbabilityKey(key);
				DataBindingContext bc = WidgetPlugin.getDefault()
						.getProbabilitiesManager()
						.getLogSettingsBindingContext();
				if (bc != null)
					bc.updateTargets();
				WidgetPlugin.getDefault().getProbabilitiesManager()
						.getLogManager().refreshViews();
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
	public void handle(DistributionsContainer aDist) {
		takeData(aDist);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				mfgTable.setModel(model);
				mfgTable.refresh();
			}
		});
	}

	private void takeData(DistributionsContainer aDist) {
		setList(getKeys());
		if (aDist != null) {
			configuration = aDist.getConfiguration();
		}
	}

	int getHSMAX() {
		return configuration == null ? 0 : configuration.getWorkingDepth();
	}

	@Override
	public void dispose() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection()
				.removeObjectListener(this);
		mfgTable.untrackFocus(getSite());
		super.dispose();
	}

	@Override
	public MfgModelTable getMFGModelTable() {
		return mfgTable;
	}

}
