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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.mfg.logger.AbstractLogger;
import com.mfg.logger.AsyncLogger;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LogLevel;
import com.mfg.logger.LogRecord;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.ui.AppLogMessage;
import com.mfg.logger.application.ui.IAppLogMessage;
import com.mfg.logger.memory.MemoryLogger;

/**
 * @author arian
 * 
 */
public class AppLogger extends AsyncLogger implements IAppLogger {

	private String component;
	private String source;
	private String componentID;
	private IConfigurationElement componentConfig;
	private String levelPrefKey;
	private final LoggerPlugin plugin = LoggerPlugin.getDefault();
	private IPropertyChangeListener prefListener;
	private final Runtime runtime;

	/**
	 * @param memory
	 * @param name
	 * @param level
	 * @param memory
	 */
	public AppLogger(ILoggerManager manager, List<ILogRecord> memory) {
		super(new MemoryLogger(manager, "Application", memory));
		component = "Unknown";
		source = "Unknown";
		runtime = Runtime.getRuntime();
	}

	/**
	 * @return the component
	 */
	public String getComponent() {
		return component;
	}

	public void setComponentID(String aComponentID) {
		this.componentID = aComponentID;
		componentConfig = plugin.getLoggerComponentConfiguration(aComponentID);
		component = componentConfig.getAttribute("name");
		levelPrefKey = LoggerPlugin
				.getComponentLogLevelPreferenceKey(aComponentID);

		if (prefListener == null) {
			prefListener = new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					updateLevel();
				}
			};
			plugin.getPreferenceStore().addPropertyChangeListener(prefListener);
		} else {
			plugin.getPreferenceStore().removePropertyChangeListener(
					prefListener);
		}

		updateLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.AsyncLogger#close()
	 */
	@Override
	public void close() {
		super.close();
		if (prefListener != null) {
			plugin.getPreferenceStore().removePropertyChangeListener(
					prefListener);
		}
	}

	/**
	 * @return the componentID
	 */
	public String getComponentID() {
		return componentID;
	}

	/**
	 * 
	 */
	void updateLevel() {
		float priority = plugin.getPreferenceStore()
				.getFloat(this.levelPrefKey);
		LogLevel level = AppLogLevel.getLevelFromPriority(priority);
		setLevel(level);
	}

	public void log(AppLogLevel level, IAppLogMessage msg) {
		log(new LogRecord(AbstractLogger.idCounter, level,
				System.currentTimeMillis(), source, msg));
	}

	@Override
	public void logComment(String comment, Object... args) {
		log(new LogRecord(AbstractLogger.idCounter, AppLogLevel.COMMENT,
				System.currentTimeMillis(), source, new AppLogMessage(
						String.format(comment, args), component, getMem())));
	}

	private long getMem() {
		return convertToMeg(runtime.totalMemory() - runtime.freeMemory());
	}

	private static long convertToMeg(long numBytes) {
		return (numBytes + (512 * 1024)) / (1024 * 1024);
	}

	@Override
	public void logDetail(String detail, Object... args) {
		log(new LogRecord(AbstractLogger.idCounter, AppLogLevel.DETAIL,
				System.currentTimeMillis(), source, new AppLogMessage(
						String.format(detail, args), component, getMem())));
	}

	@Override
	public void logWarning(String warning, Object... args) {
		log(new LogRecord(AbstractLogger.idCounter, AppLogLevel.WARNING,
				System.currentTimeMillis(), source, new AppLogMessage(
						String.format(warning, args), component, getMem())));
	}

	@Override
	public void logDebug(String error, Object... args) {
		log(new LogRecord(AbstractLogger.idCounter, AppLogLevel.DEBUG,
				System.currentTimeMillis(), source, new AppLogMessage(
						String.format(error, args), component, getMem())));
	}

	/**
	 * @param source2
	 */
	@Override
	public void setSource(String aSource) {
		this.source = aSource;
	}

	/**
	 * @return the source
	 */
	@Override
	public String getSource() {
		return source;
	}
}
