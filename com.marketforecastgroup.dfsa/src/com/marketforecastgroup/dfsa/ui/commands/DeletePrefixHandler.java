package com.marketforecastgroup.dfsa.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.marketforecastgroup.dfsa.DFSAPlugin;
import com.marketforecastgroup.dfsa.ui.views.SymbolsNavigator;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.IDFS;
import com.mfg.utils.PartUtils;

public class DeletePrefixHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (DFSPlugin.getDefault().isDFSReady()) {
			try {
				DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

					@Override
					public void run(IDFS dfs) {
						openDialog(dfs, event);
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
		} else {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"DFS", "DFS is not ready yet. Try later.");
		}

		return null;
	}

	static void openDialog(IDFS dfs, ExecutionEvent event) {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);

		Object[] array = sel.toArray();
		String msg = "You are to delete "
				+ (array.length == 1 ? ((DfsSymbol) array[0]).prefix + "."
						: array.length + " objects.");
		if (MessageDialog.openConfirm(HandlerUtil.getActiveShell(event),
				"Delete", msg)) {
			for (Object obj : array) {
				DfsSymbol s = (DfsSymbol) obj;
				DfsCacheRepo cache = DFSAPlugin.getCacheRepo(dfs);
				cache.removeSymbol(s.prefix);
				List<SymbolsNavigator> list = PartUtils
						.getOpenViews(SymbolsNavigator.ID);
				for (SymbolsNavigator nav : list) {
					nav.getCommonViewer().refresh();
				}
			}
		}
	}
}
