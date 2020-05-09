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

package com.mfg.dm.symbols;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mfg.common.ICSVContract;

/**
 * @author arian
 * 
 */
@XmlRootElement(name = "CSVSymbol")
public class CSVSymbolData extends SymbolData implements Serializable,
		ICSVContract {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File file;

	/**
	 * 
	 */
	public CSVSymbolData() {
	}

	@Override
	public String getLocalSymbol() {
		return this.getName();
	}

	/**
	 * @param file1
	 */
	public CSVSymbolData(File file1) {
		setName(file1.getName().substring(0, file1.getName().length() - 4));
		this.file = file1;
	}

	/**
	 * @return the file
	 */
	@Override
	@XmlElement
	public File getFile() {
		return file;
	}

	/**
	 * @param file1
	 *            the file to set
	 */
	public void setFile(File file1) {
		this.file = file1;
	}

	@Override
	public String toString() {
		return "CSVSymbolData [getFile()=" + getFile() + ", getCurrency()="
				+ getCurrency() + ", getName()=" + getName() + ", getSymbol()="
				+ getSymbol() + ", getLocalSymbol()=" + getLocalSymbol()
				+ ", getTickSize()=" + getTickSize() + ", getTickValue()="
				+ getTickValue() + ", getScale()=" + getContractScale() + "]";
	}

}
