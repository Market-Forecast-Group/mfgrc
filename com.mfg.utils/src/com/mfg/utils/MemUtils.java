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
package com.mfg.utils;

/**
 * @author arian
 * 
 */
public class MemUtils {
	private static final Runtime runtime = Runtime.getRuntime();

	public static long getUsedMemory() {
		return convertToMeg(runtime.totalMemory() - runtime.freeMemory());
	}

	private static long convertToMeg(long numBytes) {
		return (numBytes + (512 * 1024)) / (1024 * 1024);
	}
}
