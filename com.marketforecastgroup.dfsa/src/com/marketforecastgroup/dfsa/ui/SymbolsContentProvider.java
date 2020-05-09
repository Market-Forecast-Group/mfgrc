package com.marketforecastgroup.dfsa.ui;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.DfsSymbolList;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityStats;

public class SymbolsContentProvider implements ITreeContentProvider,
		IDFSObserver {
	public static final String WAITING_FOR_DFS = "WAITING_FOR_DFS";
	public static final Object ROOT = "com.marketforecastgroup.dfsa.ui.symbolsRoot";
	private IDFS _dfs;
	Viewer _viewer;
	private DfsCacheRepo _cache;

	public static class IntervalInfo {
		public BarType type;
		public DfsIntervalStats interval;

		public IntervalInfo(BarType aType, DfsIntervalStats stats) {
			super();
			this.type = aType;
			this.interval = stats;
		}

	}

	static class MaturitiesRootNode {
		List<MaturityStats> maturities;

		@Override
		public String toString() {
			return "Single Contracts";
		}
	}

	public SymbolsContentProvider() {
		try {
			DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

				@Override
				public void run(IDFS dfs) {
					setDfs(dfs);
				}

				@Override
				public void notReady() {
					// nothing
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	void setDfs(IDFS dfs) {
		_dfs = dfs;
		_dfs.addObserver(this);
		_cache = DFSPlugin.getDefault().getCache();

		refresh();
	}

	private void refresh() {
		if (_viewer != null) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					_viewer.refresh();
				}
			});
		}
	}

	@Override
	public void dispose() {
		if (_dfs != null) {
			_dfs.removeObserver(this);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		_viewer = viewer;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (_dfs == null) {
			return new Object[] { WAITING_FOR_DFS };
		}
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent == ROOT) {
			List<Object> result = new ArrayList<>();

			try {
				// collecting symbols
				result.addAll(Arrays.asList(_cache.getCollectingSymbols()));

				// ready symbols
				DfsSymbolList list = _dfs.getSymbolsList();
				result.addAll(list.symbols);
				return result.toArray();
			} catch (DFSException e) {
				e.printStackTrace();
			}
		}

		if (parent instanceof DfsSymbol) {
			DfsSymbol s = (DfsSymbol) parent;
			List<Object> list = new ArrayList<>();
			try {
				String prefix = s.prefix;
				DfsSymbolStatus status = _dfs.getStatusForSymbol(prefix);

				list.add(status.continuousStats);
				MaturitiesRootNode root = new MaturitiesRootNode();
				root.maturities = status.maturityStats;
				list.add(root);
			} catch (Exception e) {
				out.println(e.getClass() + ": " + e.getMessage());
			}

			return list.toArray();
		}

		if (parent instanceof MaturitiesRootNode) {
			return ((MaturitiesRootNode) parent).maturities.toArray();
		}

		if (parent instanceof MaturityStats) {
			return getStatsChildren(parent);
		}
		return null;
	}

	private static Object[] getStatsChildren(Object parent) {
		MaturityStats stats = (MaturityStats) parent;
		return getIntervals(stats);
	}

	private static Object[] getIntervals(MaturityStats stats) {
		List<Object> list = new ArrayList<>();
		HashMap<BarType, DfsIntervalStats> map = stats._map;
		for (Entry<BarType, DfsIntervalStats> entry : map.entrySet()) {
			list.add(new IntervalInfo(entry.getKey(), entry.getValue()));
		}
		return list.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	@Override
	public void onSymbolInitializationEnded(String symbol) {
		// refresh the viewer, a new symbol is ready
		refresh();
	}

	@Override
	public void onSchedulerStartRunning() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSchedulerEndedCycle() {
		// TODO Auto-generated method stub

	}

}
