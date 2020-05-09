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
package com.mfg.utils.concurrent;

/**
 * @author arian
 * 
 */
public interface IRequestQueue {

	public String getName();

	public void addRequest(Runnable request);

	public void close();

	boolean isClosed();

	public void restart();

}
