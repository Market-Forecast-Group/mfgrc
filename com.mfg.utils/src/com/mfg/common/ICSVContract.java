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
package com.mfg.common;

import java.io.File;

/**
 * TODO: This interface should be moved to the csv connector plugin when the
 * SymbolData class was replaced by the SymbolData2, which is defined in the csv
 * connector plugin.
 * 
 * @author arian
 * 
 */
public interface ICSVContract extends IContract {
	public File getFile();
}
