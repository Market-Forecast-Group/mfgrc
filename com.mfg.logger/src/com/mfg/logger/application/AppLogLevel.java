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
package com.mfg.logger.application;

import java.util.HashMap;
import java.util.Map;

import com.mfg.logger.LogLevel;

/**
 * @author arian
 * 
 */
public class AppLogLevel extends LogLevel {
	public static final AppLogLevel aOFF = new AppLogLevel(
			LogLevel.OFF.getPriority(), "Off");
	public static final AppLogLevel aANY = new AppLogLevel(
			LogLevel.ANY.getPriority(), "Any");
	public static final AppLogLevel COMMENT = new AppLogLevel(10, "Comment");
	public static final AppLogLevel DETAIL = new AppLogLevel(20, "Detail");
	public static final AppLogLevel WARNING = new AppLogLevel(30, "Warning");
	public static final AppLogLevel DEBUG = new AppLogLevel(40, "Debug");

	private static Map<Float, AppLogLevel> map = new HashMap<Float, AppLogLevel>() {
		private static final long serialVersionUID = 1L;

		{
			for (AppLogLevel level : new AppLogLevel[] { aOFF, aANY, COMMENT,
					DETAIL, WARNING, DEBUG }) {

				float priority = level.getPriority();

				put(Float.valueOf(priority), level);
			}
		}
	};

	/**
	 * @param priority
	 * @return
	 */
	public static LogLevel getLevelFromPriority(float priority) {
		return map.get(Float.valueOf(priority));
	}

	/**
	 * @param priority
	 * @param name
	 */
	private AppLogLevel(float priority, String name) {
		super(priority, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

}
