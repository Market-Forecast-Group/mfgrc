package com.marketforescastgroup.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Display;

public final class LogManager {

	private static final String LOGS_XML = "logs.xml";

	public final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	public final static SimpleDateFormat dateFormat_1 = new SimpleDateFormat(
			"yyyy-MM-dd_hh-mm");

	private String logPath;

	// static {
	// getInstance().restore();
	// }

	private static LogManager logManager;

	private List<Log> logs;

	// private final XStream xstream;

	private ILogManagerListener listener;

	// private MessageConsole messageConsole;

	private final boolean _saveEnabled;

	private LogManager(boolean saveEnabled) {

		_saveEnabled = saveEnabled;

		logs = new ArrayList<>();

		// xstream = new XStream();
		// xstream.addDefaultImplementation(ArrayList.class, List.class);
		// xstream.alias("Logs", List.class);

		// restore();

		if (_saveEnabled) {
			final String path = LogViewPlugin.getDefault().getPreferenceStore()
					.getString(LogViewPlugin.LOG_DIRECTORY);
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			final String time = dateFormat_1.format(Long.valueOf(Calendar
					.getInstance().getTimeInMillis()));
			logPath = path + File.separator + "log_" + time + ".txt";
		}

	}

	public static LogManager getInstance() {
		if (logManager == null) {
			logManager = new LogManager(false);
		}
		return logManager;
	}

	public void setLogManagerListener(final ILogManagerListener listener1) {
		this.listener = listener1;
	}

	public void INFO(final String message) {
		final String date = dateFormat.format(new Date());
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[INFO] " + date + " : ");
		buffer.append(message);
		System.out.println(buffer.toString());
		// sendToConsole(buffer.toString());
		writeToLog(buffer.toString());
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				addLog(new Log(date, buffer.toString(), MessageType.INFO));
			}
		});
	}

	private void writeToLog(final String message) {
		if (!_saveEnabled) {
			return;
		}

		try (

		final FileOutputStream fostream = new FileOutputStream(logPath, true);

		final OutputStreamWriter oswriter = new OutputStreamWriter(fostream);

		final BufferedWriter bwriter = new BufferedWriter(oswriter);

		) {

			bwriter.append(message);
			bwriter.newLine();
			bwriter.close();
			oswriter.close();
			fostream.close();
		} catch (IOException e) {
			ERROR("err: " + e.getMessage());
		}
	}

	public void WARNING(final String message) {
		final String date = dateFormat.format(new Date());
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[WARNING] ");
		buffer.append(message);
		System.out.println(buffer.toString());
		// sendToConsole(buffer.toString());
		writeToLog(buffer.toString());
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				addLog(new Log(date, buffer.toString(), MessageType.WARNING));
			}
		});
	}

	public void ERROR(final String message) {
		final String date = dateFormat.format(new Date());
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[ERROR] ");
		buffer.append(message);
		System.out.println(buffer.toString());
		// sendToConsole(buffer.toString());
		writeToLog(buffer.toString());
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				addLog(new Log(date, buffer.toString(), MessageType.ERROR));
			}
		});
	}

	void addLog(final Log log) {
		logs.add(log);
		if (this.listener != null) {
			this.listener.logAdded(log);
		}
		save();
	}

	public void removeLog(final Log log) {
		logs.remove(log);
		if (this.listener != null) {
			this.listener.logRemoved(log);
		}
		save();
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void clear() {
		logs.clear();
	}

	public void save() {

		if (!_saveEnabled) {
			return;
		}

		try {
			final String path = LogViewPlugin.getDefault().getPreferenceStore()
					.getString(LogViewPlugin.LOG_DIRECTORY);
			final File file = new File(path + LOGS_XML);

			if (!file.exists()) {
				file.createNewFile();
				INFO(file.getAbsolutePath() + "is created.");
			}
			// FileOutputStream fs = new FileOutputStream(file);
			// xstream.toXML(logs, fs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
