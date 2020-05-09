package com.mfg.plstats.persist;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.plstats.PLStatsPlugin;

public class PLStatsCSVStorageReference implements IWorkspaceStorageReference {

	public PLStatsCSVStorageReference() {
	}

	@Override
	public PLStatsCSVStorage getStorage() {
		return PLStatsPlugin.getDefault().getCSVStorage();
	}

	@Override
	public String getStorageId() {
		return PLStatsCSVStorage.class.getCanonicalName();
	}
}
