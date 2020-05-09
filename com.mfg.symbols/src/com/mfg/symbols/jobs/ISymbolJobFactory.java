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
package com.mfg.symbols.jobs;


/**
 * @author arian
 * 
 */
public interface ISymbolJobFactory {
	public SymbolJob<?> createSymbolJob(SymbolJobConfig<?> config);
}
