package com.mfg.symbols.dfs.persistence;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;

public class DFSProfileStorageReference implements IWorkspaceStorageReference {
	@Override
	public DFSProfileStorage getStorage() {
		return DFSSymbolsPlugin.getDefault().getProfileStorage();
	}

	@Override
	public String getStorageId() {
		return DFSProfileStorage.class.getCanonicalName();
	}
}
