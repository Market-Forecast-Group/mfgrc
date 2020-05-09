package com.mfg.plstats.ui.editors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter.ProbVer;
import com.mfg.interfaces.probabilities.SCTProbabilityKey;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMFGModelTableContainer;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.SCTProbabilitySet;

public class SCTProbabilitiesView extends ViewPart implements
		ObjectListener<DistributionsContainer>, IMFGModelTableContainer {

	public static final String ID = "com.mfg.plstats.ui.editors.SCTProbabilitiesView"; //$NON-NLS-1$

	static HashMap<SCTProbabilityKey, SCTProbabilitySet> getTheSCTMap() {
		DistributionsContainer distributionsContainer2 = WidgetPlugin
				.getDefault().getProbabilitiesManager()
				.getDistributionsContainer();
		if (distributionsContainer2 == null)
			return new HashMap<>();
		return distributionsContainer2.getSctsMap();
	}

	public class SCTModelGen implements IMfgTableModel {

		private HashMap<SCTProbabilityKey, SCTProbabilitySet> sctsMap;
		private SCTProbabilityKey[] list;

		public SCTModelGen() {
			this(getTheSCTMap());
		}

		public SCTModelGen(DistributionsContainer ds) {
			this(ds.getSctsMap());
		}

		public SCTModelGen(
				HashMap<SCTProbabilityKey, SCTProbabilitySet> aSctsMap) {
			this.sctsMap = aSctsMap;
			Set<SCTProbabilityKey> keySet = aSctsMap.keySet();
			list = keySet.toArray(new SCTProbabilityKey[] {});
			Arrays.sort(list, new Comparator<SCTProbabilityKey>() {
				@Override
				public int compare(SCTProbabilityKey aO1, SCTProbabilityKey aO2) {
					int r = (int) Math.signum(aO1.getScale() - aO2.getScale());
					if (r == 0)
						r = (int) Math.signum(aO1.getSctouches()
								- aO2.getSctouches());
					if (r == 0)
						r = (int) Math.signum(aO1.getBaseScaleCluster()
								- aO2.getBaseScaleCluster());
					return r;
				}
			});

		}

		@SuppressWarnings("unused")
		// Maybe used on inner classes
		private static final long serialVersionUID = 1L;

		@Override
		public Object getContent(int row, int column) {
			SCTProbabilityKey k = list[row];
			SCTProbabilitySet set = sctsMap.get(k);
			switch (column) {
			case 0:
				return Integer.valueOf(k.getScale());
			case 1:
				return Integer.valueOf(k.getSctouches());
			case 2:
				return Integer.valueOf(k.getBaseScaleCluster());
			case 3:
				return Integer.valueOf(set.getSwingsCount()
						- set.getReachedNewHHLL());
			case 4:
				return Integer.valueOf(set.getSwingsCount());
			case 5:
				return Double.valueOf(st.round(set.getNewTHProbability()));
			default:
				return Double.valueOf(st.round(set.getNewHHLLProbability()));
			}
		}

		@Override
		public boolean isEnabled(int aRow, int aColumn) {
			SCTProbabilityKey k = list[aRow];
			switch (aColumn) {
			case 1:
				return k.getSctouches() != 0;
			case 2:
				return k.getBaseScaleCluster() != 0;
			}
			return true;
		}

		@Override
		public int getRowCount() {
			if (list == null)
				return 0;
			return list.length;
		}

		StepDefinition st = new StepDefinition(0.01);
		private final String[] _COLUMNS = new String[] { "Scale", "SCT", "BSC",
				"TH Count", "Sw Count", "TH Prob", "HHLL Prob" };

		public int getScale(int row) {
			return list[row].getScale();
		}

		public int getBaseClusterID(int row) {
			return list[row].getBaseScaleCluster();
		}

		public int getSCTouch(int row) {
			return list[row].getSctouches();
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

	MfgModelTable mfgTable;
	private DistributionsContainer distributionsContainer;
	SCTModelGen mmodel;

	public SCTProbabilitiesView(DistributionsContainer ds) {
		this();
		this.distributionsContainer = ds;
	}

	public SCTProbabilitiesView() {
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsContainerSelection().addObjectListener(this);
	}

	public DistributionsContainer getDistributionsContainer() {
		return distributionsContainer;
	}

	public void setDistributionsContainer(
			DistributionsContainer aDistributionsContainer) {
		distributionsContainer = aDistributionsContainer;
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
		mfgTable = new MfgModelTable(container, mmodel = new SCTModelGen());
		mfgTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				int idx = mfgTable.getSelectedIndex();
				System.out.println("sel " + idx);
				// ProbabilitiesManager man =
				// WidgetPlugin.getDefault().getProbabilitiesManager();
				// ProbabilitiesKey key = list[idx];
				// man.setSelectedTargetKey(key);
				//
				IProbabilitiesFilter filter = WidgetPlugin.getDefault()
						.getProbabilitiesManager().getProbabilitiesLogFilter();

				if (idx != -1) {
					filter.setScale(mmodel.getScale(idx));
					filter.setBCID(Integer.valueOf(mmodel.getBaseClusterID(idx)));
					filter.setSCTIndex(Integer.valueOf(mmodel.getSCTouch(idx)));
					filter.setVersion(ProbVer.SCT);
				}

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
	public void handle(DistributionsContainer aEvent) {
		mmodel = new SCTModelGen();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				mfgTable.setModel(mmodel);
				mfgTable.refresh();
			}
		});
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
