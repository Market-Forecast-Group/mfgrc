package com.mfg.plstats.persist;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.plstats.PLStatsPlugin;

public class PLStatsIndicatorStorageReference implements
		IWorkspaceStorageReference {

	public PLStatsIndicatorStorageReference() {
	}

	@Override
	public PLStatsIndicatorStorage getStorage() {
		return PLStatsPlugin.getDefault().getIndicatorStorage();
	}

	@Override
	public String getStorageId() {
		return PLStatsIndicatorStorage.class.getCanonicalName();
	}
}
