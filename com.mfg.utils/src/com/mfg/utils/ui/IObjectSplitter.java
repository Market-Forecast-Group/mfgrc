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
package com.mfg.utils.ui;

/**
 * @author arian
 * 
 */
public interface IObjectSplitter {
	/**
	 * Split the object in an array of strings. For example, an object Person
	 * can be splitted into <code>["Enrique", 30, Sex.MALE]</code>.
	 * 
	 * @param obj
	 * @return
	 */
	public Object[] splitObject(Object obj);
}
