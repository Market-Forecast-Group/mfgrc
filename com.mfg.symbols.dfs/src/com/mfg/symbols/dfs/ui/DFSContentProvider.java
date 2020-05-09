package com.mfg.symbols.dfs.ui;

import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.persistence.DFSStorage;
import com.mfg.symbols.ui.views.ISymbolNavigatorRoot;
import com.mfg.symbols.ui.widgets.SymbolContentProvider;

public class DFSContentProvider extends SymbolContentProvider implements
		IDFSObserver {
	public static final String WAITING_FOR_DFS = "Waiting for DFS...";
	public final static String CONTENT_ROOT = "com.mfg.symbols.dfs.ui.contentRoot";
	private Runnable _updateListener;

	public DFSContentProvider() {
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof DfsSymbol) {
			return true;
		}
		return super.hasChildren(element);
	}

	public static class SingleContractNode {
		private DfsSymbol _symbol;

		public SingleContractNode(DfsSymbol symbol) {
			super();
			_symbol = symbol;
		}

		public DfsSymbol getSymbol() {
			return _symbol;
		}

		@Override
		public String toString() {
			return "Single Contracts";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SingleContractNode) {
				return ((SingleContractNode) obj)._symbol.prefix
						.equals(_symbol.prefix);
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return (_symbol.prefix + "#singleContract").hashCode();
		}
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof ISymbolNavigatorRoot) {
			return new Object[] { CONTENT_ROOT };
		}

		if (parent == CONTENT_ROOT) {
			DFSStorage storage = getStorage();
			if (storage.isUpdating()) {
				return new Object[] { WAITING_FOR_DFS };
			}
			synchronized (storage) {
				// register the update listener when the tree is expanded to
				// show the symbols.
				if (_updateListener == null) {
					_updateListener = createUpdateAction();
					storage.addUpdateTreeListener(_updateListener);
				}

				// if DFS is not ready, return a waiting message.
				if (!storage.isReady()) {
					storage.runWhenReady(new IDFSRunnable() {

						@Override
						public void run(IDFS dfs) throws DFSException {
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									getViewer().refresh();
								}
							});
						}

						@Override
						public void notReady() {
							//
						}
					});
					return new Object[] { WAITING_FOR_DFS };
				}
			}

			return storage.getDfsSymbols().toArray();
		}

		if (parent instanceof DfsSymbol) {
			DfsSymbol dfsSymbol = (DfsSymbol) parent;
			return new Object[] {
					getStorage().lookupContinuousContract(dfsSymbol.prefix),
					new SingleContractNode(dfsSymbol) };
		}

		if (parent instanceof SingleContractNode) {
			DfsSymbol dfsSymbol = ((SingleContractNode) parent).getSymbol();
			return getStorage().findMaturitiesByPrefix(dfsSymbol.prefix).toArray();
		}

		if (parent instanceof DFSConfiguration) {
			MaturityStats stats = getStorage().lookupMaturityStats(
					(DFSConfiguration) parent);
			return stats._map.entrySet().toArray();

		}
		return super.getChildren(parent);
	}

	private Runnable createUpdateAction() {
		return new Runnable() {
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						try {
							getViewer().refresh();
						} catch (SWTException e) {
							//
						}
					}
				});
			}
		};
	}

	private static DFSStorage getStorage() {
		return DFSSymbolsPlugin.getDefault().getDFSStorage();
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof DFSConfiguration) {
			return CONTENT_ROOT;
		}
		return super.getParent(element);
	}

	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		super.registerStorages(storages);
		storages.add(DFSSymbolsPlugin.getDefault().getDFSStorage());
	}

	@Override
	public void dispose() {
		if (_updateListener != null) {
			getStorage().removeUpdateTreeListener(_updateListener);
		}
		super.dispose();
	}

	// DFS Observer methods

	@Override
	public void onSymbolInitializationEnded(String symbol) {
		//
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
