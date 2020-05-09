package com.mfg.widget.probabilities;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.widget.WidgetPlugin;

public class PLStatsProbabilitiesStorageReference implements
		IWorkspaceStorageReference {

	public PLStatsProbabilitiesStorageReference() {
	}

	@Override
	public PLStatsProbabilitiesStorage getStorage() {
		return WidgetPlugin.getDefault().getProbsStorage();
	}

	@Override
	public String getStorageId() {
		return PLStatsProbabilitiesStorage.class.getCanonicalName();
	}
}
