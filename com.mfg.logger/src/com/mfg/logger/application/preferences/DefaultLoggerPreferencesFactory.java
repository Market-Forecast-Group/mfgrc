package com.mfg.logger.application.preferences;

import com.mfg.logger.LogLevel;
import com.mfg.logger.application.AppLogLevel;

public class DefaultLoggerPreferencesFactory implements
		ILoggerComponentPreferencesFactory {

	public DefaultLoggerPreferencesFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.application.preferences.IComponentLoggerPreferences#
	 * getPossibleLevels()
	 */
	@Override
	public LogLevel[] getPossibleLevels() {
		return new LogLevel[] { LogLevel.ANY, LogLevel.OFF,
				AppLogLevel.COMMENT, AppLogLevel.DETAIL, AppLogLevel.WARNING,
				AppLogLevel.DEBUG };
	}

}
