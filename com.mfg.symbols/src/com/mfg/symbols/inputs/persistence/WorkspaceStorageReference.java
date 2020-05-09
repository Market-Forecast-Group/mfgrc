package com.mfg.symbols.inputs.persistence;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.symbols.SymbolsPlugin;

public class WorkspaceStorageReference implements IWorkspaceStorageReference {

	@Override
	public InputsStorage getStorage() {
		return SymbolsPlugin.getDefault().getInputsStorage();
	}

	@Override
	public String getStorageId() {
		return InputsStorage.class.getCanonicalName();
	}

}
