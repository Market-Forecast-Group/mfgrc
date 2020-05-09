package com.mfg.symbols.csv.persistence;

import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.inputs.persistence.ISymbolStorageReference;
import com.thoughtworks.xstream.XStream;

public class CSVStorageReference implements ISymbolStorageReference {

	public CSVStorageReference() {
	}

	@Override
	public CSVStorage getStorage() {
		return CSVSymbolPlugin.getDefault().getCSVStorage();
	}

	@Override
	public String getStorageId() {
		return CSVStorage.class.getCanonicalName();
	}

	@Override
	public void configureXStream(XStream xstream) {
		CSVStorage.configureXStream2(xstream);
	}
}
