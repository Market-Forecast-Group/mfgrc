package com.mfg.symbols.inputs.persistence;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.thoughtworks.xstream.XStream;

public interface ISymbolStorageReference extends IWorkspaceStorageReference {
	public void configureXStream(XStream xstream);
}
