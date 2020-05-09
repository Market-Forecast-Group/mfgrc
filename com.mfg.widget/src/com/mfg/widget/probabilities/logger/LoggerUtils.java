package com.mfg.widget.probabilities.logger;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.logger.ILogger;
import com.mfg.logger.LogLevel;
import com.mfg.widget.WidgetPlugin;

public class LoggerUtils {
	public static boolean nojobs = false;
	public static void addAlltoLog(final ILogger logger,
			final List<ISimpleLogMessage> eventsList) {
		if (nojobs) {
			addAll(logger, eventsList);
		} else {
			Job e = new Job("filling logger") {
				@Override
				protected IStatus run(IProgressMonitor aMonitor) {
					addAll(logger, eventsList);
					return Status.OK_STATUS;
				}

			};
			e.schedule();
		}
	}
	protected static void addAll(final ILogger logger,
			final List<ISimpleLogMessage> eventsList) {
		if (eventsList != null)
			for (int i = 0; i < eventsList.size(); i++) {
				logger.log(LogLevel.ANY, eventsList.get(i));
			}
		logger.close();
		System.out.println("done with log");
	}
	public static ILogger defaultLogger() {
		return WidgetPlugin.getDefault().getProbabilitiesManager().getLogger();
	}

}
