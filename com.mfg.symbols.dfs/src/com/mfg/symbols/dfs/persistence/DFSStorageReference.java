package com.mfg.symbols.dfs.persistence;

import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.inputs.persistence.ISymbolStorageReference;
import com.thoughtworks.xstream.XStream;

public class DFSStorageReference implements ISymbolStorageReference {

	@Override
	public DFSStorage getStorage() {
		return DFSSymbolsPlugin.getDefault().getDFSStorage();
	}

	@Override
	public String getStorageId() {
		return DFSStorage.class.getCanonicalName();
	}

	@Override
	public void configureXStream(XStream xstream) {
		DFSStorage.configureXStream2(xstream);
	}

}
