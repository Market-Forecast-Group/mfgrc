/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.csv.configurations;

import java.io.File;

import com.mfg.dm.symbols.SymbolData2;
import com.mfg.symbols.csv.CSVSymbolPlugin;

/**
 * @author arian
 * 
 */
public class CSVSymbolData2 extends SymbolData2 {
	public static final String PROP_FILE = "file";

	private File file;
	private String _fileName;

	public CSVSymbolData2() {
		super();
	}

	/**
	 * @deprecated Use {@link #getFileName()}.
	 * @return
	 */
	@Deprecated
	public File getFile() {
		return file;
	}

	public void setFile(File aFile) {
		this.file = aFile;
		firePropertyChange(PROP_FILE);
	}

	/**
	 * The name of the CSV file. The complete path is formed by the folder
	 * {@link CSVSymbolPlugin#getCSVFilesPath()} joined with this name.
	 * 
	 * @return
	 */
	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		_fileName = fileName;
	}
}
