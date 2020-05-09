package com.mfg.symbols.dfs.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class RefreshSymbolsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		throw new UnsupportedOperationException("Not implemented");
		
		// Job job = new Job("Refresh DFS symbols") {
		//
		// @Override
		// protected IStatus run(IProgressMonitor monitor) {
		// DFSSymbolsPlugin.getDefault().getDFSStorage().refresh(true);
		// return Status.OK_STATUS;
		// }
		// };
		// job.schedule();
		// return null;
	}

}
