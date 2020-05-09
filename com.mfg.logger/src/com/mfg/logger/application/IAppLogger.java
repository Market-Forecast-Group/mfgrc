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

import static java.lang.System.out;

import com.mfg.logger.LogLevel;

/**
 * @author arian
 * 
 */
public interface IAppLogger {
	IAppLogger SYSTEM_PRINT_LOGGER = new IAppLogger() {

		@Override
		public void setSource(String source) {
			// Adding a comment to avoid empty block warning.
		}

		@Override
		public void logWarning(String warning, Object... args) {
			// Adding a comment to avoid empty block warning.
		}

		@Override
		public void logDetail(String detail, Object... args) {
			// Adding a comment to avoid empty block warning.
		}

		@Override
		public void logDebug(String error, Object... args) {
			// Adding a comment to avoid empty block warning.
		}

		@Override
		public void logComment(String comment, Object... args) {
			out.println(String.format(comment, args));
		}

		@Override
		public boolean isLoggable(LogLevel level) {
			return true;
		}

		@Override
		public String getSource() {
			return "OutsideEclipse";
		}

		@Override
		public LogLevel getLevel() {
			return LogLevel.ANY;
		}

		@Override
		public void close() {
			// Adding a comment to avoid empty block warning.
		}
	};

	public boolean isLoggable(LogLevel level);

	public void logComment(String comment, Object... args);

	public void logDetail(String detail, Object... args);

	public void logWarning(String warning, Object... args);

	public void logDebug(String error, Object... args);

	public String getSource();

	public void setSource(String source);

	public void close();

	public LogLevel getLevel();

}
