package com.mfg.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

/**
 * Just an util class which has some utility methods.
 * 
 * @author lino
 * 
 */
public final class Utils {

	private static SimpleDateFormat _sdf = new SimpleDateFormat(
			"dd-MM-yy HH:mm:ssZ");
	private static PrintWriter ourLog;
	private static boolean logEnabled;

	// public static void main(String[] args) throws Exception {
	// URL url = new File(
	// "/home/arian/Documents/Source/mfgrc/com.mfg.symbols/sounds/event.wav")
	// .toURI().toURL();
	// playSound(url);
	// }

	/**
	 * Joins a thread (wait for it to finish) for ever.
	 * 
	 * @param th
	 *            : the thread which is waited for.
	 */
	public static void join_thread(Thread th) {
		try {
			th.join();
		} catch (InterruptedException e) {
			exit_ue(e);
		}
	}

	/**
	 * Sleep without throwing exceptions.
	 * 
	 * @param millis
	 *            the number of milliseconds to wait
	 */
	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
			System.exit(-1);

		}
	}

	/**
	 * Notifies an object. You don't have to own the lock on the object, it is
	 * acquired in the function.
	 * 
	 * @param o
	 *            the object to be notified!
	 */
	public static void notifyObject(Object o) {
		synchronized (o) {
			o.notify();
		}
	}

	/**
	 * This function will wait on an object forever. Returns only when the
	 * object is signaled.
	 * 
	 * You MUST already own the monitor on the object!
	 * 
	 * @param o
	 */
	public static void waitForEverOn(Object o) {
		try {
			o.wait();
		} catch (InterruptedException e) {
			exit_ue(e);
		}
	}

	public static void waitForSomeTimeOn(Object o, long timeout) {
		try {
			o.wait(timeout);
		} catch (InterruptedException e) {
			exit_ue(e);
		}
	}

	/**
	 * Diagnostic function. It prints a message preceded by a code, in order to
	 * be able to find it immediately in the sources.
	 * 
	 * It prints only if debug is enabled! (The debug can be disabled using the
	 * --no-dbg flag passed at the application during startup).
	 * 
	 * @param msg_id
	 *            an integer to identify this message
	 * @param message
	 *            The message which you want to print
	 */
	@SuppressWarnings("boxing")
	public synchronized static void debug_id(int msg_id, String message) {
		if (!isDebugEnabled())
			return;
		try (Formatter ft = new Formatter()) {
			ft.format("[%06d] %.200s", msg_id, message);
			System.out.println(ft.toString());
		}
	}

	private static final boolean debugEnabled;

	static {
		// out.println("Load utils plugin");
		if (UtilsPlugin.getDefault() != null) {
			debugEnabled = UtilsPlugin.getDefault().getPreferenceStore()
					.getBoolean(UtilsPlugin.PREFERENCES_STD_DEBUG);
			logEnabled = false;

		} else {
			debugEnabled = true;
			logEnabled = false;
		}
	}

	private static final int MAX_LOG_FILES = 5;

	/**
	 * @return
	 */
	private static boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * Prints a message on the console. The message is a sequence of objects
	 * which are rendered using the toString method. Date and double objects are
	 * formatted using respectively the current locale and 4 digits of
	 * precision.
	 * 
	 * @param handle
	 *            the handle to the file to which you want to write the log.
	 * 
	 * @param msg_id
	 *            The id of the message
	 * @param objs
	 *            the list of objects to be formatted.
	 */
	public synchronized static void debug_handler_var(int handle, int a_msg_id,
			Object... objs) {
		if (!isDebugEnabled() && handle < 0)
			return;

		assert (a_msg_id > 0);
		assert (a_msg_id < 1000000);

		int msg_id = a_msg_id;
		StringBuilder sb = new StringBuilder();
		for (Object ob : objs) {
			if ((ob instanceof Double) || (ob instanceof Float)) {
				try (Formatter ft = new Formatter()) {
					ft.format("%4.10g", ob);
					sb.append(ft.toString());
				}
			} else if (ob instanceof Date) {
				sb.append(_sdf.format(ob));
			} else {
				sb.append(ob);
			}

			// The string is formatted to a single line so we stop if
			// the lines is too long.
			if (sb.length() > 200) {
				sb.insert(197, "...");
				break;
			}
		}

		if (handle >= 0)
			debug_file(handle, sb.toString());
		else
			debug_id(msg_id, sb.toString());
	}

	public synchronized static void debug_var(int aMsg_id, Object... objs) {
		debug_handler_var(-1, aMsg_id, objs);
	}

	/**
	 * @param string
	 *            the string to be put on the file
	 */
	public static void debug_file(String string) {
		if (logEnabled) {
			ourLog.println(string);
		}
	}

	public static void debug_file(int handle, String string) {
		sLogFiles[handle].println(string);
	}

	/**
	 * flushes the log to disk, if enabled.
	 */
	public static void log_flush() {
		if (logEnabled) {
			ourLog.flush();
		}
	}

	private static PrintWriter sLogFiles[] = new PrintWriter[MAX_LOG_FILES];

	/**
	 * Creates an empty log file which will be used using the
	 * <code>debug_file</code> function.
	 * 
	 * @param fileName
	 *            The file name to create, it should be an existing path (the
	 *            directories are not created)
	 * 
	 * @return the handle for this log file, -1 in case of error, a number
	 *         between 0 and MAX_LOG_FILES in case of success
	 */
	@SuppressWarnings("boxing")
	public static int create_a_log_file(String fileName) {
		int handle = -1;

		while (++handle < MAX_LOG_FILES) {
			if (sLogFiles[handle] == null) {
				try {
					sLogFiles[handle] = new PrintWriter(fileName);
					break;
				} catch (FileNotFoundException e) {
					catch_exception_and_continue(e, true);
					return -1;
				}
			}
		}

		debug_var(293525, "Created a log file called ", fileName, " with id ",
				handle);
		return handle;
	}

	/**
	 * Closes the log file associated with that handle. It is an error to close
	 * a log file twice. But it is not an error to close a not existing handle
	 * (<0).
	 * 
	 * @param handle
	 *            the handle to close.
	 */
	public static void close_log_file(int handle) {
		if (handle < 0) {
			return;
		}
		assert (sLogFiles[handle] != null);
		sLogFiles[handle].close();
		sLogFiles[handle] = null;
	}

	/**
	 * Exits the program printing a message.
	 * 
	 * IT NEVER RETURNS
	 */
	public static void exit_s(Object... objs) {
		StringBuilder sb = new StringBuilder();
		for (Object ob : objs) {
			sb.append(ob);
		}
		debug_var(954148, "Program aborted, reason: ", sb.toString());
		Thread.dumpStack();
		System.exit(-1);
	}

	/**
	 * Exits the app for an unexpected exception, usually from the java library.
	 * 
	 * IT NEVER RETURNS.
	 * 
	 * @param e
	 *            the unexpected exception.
	 */
	public static void exit_ue(Exception e) {
		catch_exception_and_continue(e, true);
		System.exit(-1);
	}

	/**
	 * This function handles the exception and continues. It has the possibility
	 * to print the stack trace, if needed.
	 * 
	 * @param e
	 *            the exception to be caught.
	 */
	public static void catch_exception_and_continue(Exception e,
			boolean print_stack) {
		String where = "";
		if (print_stack) {
			StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
			where = ste.getClassName() + " " + ste.getMethodName() + " "
					+ ste.getLineNumber();

		}
		debug_var(63783, "Caught exception ", e, " @ ", where);
	}

	public static void warn(String msg) {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		String where = ste.getClassName() + " " + ste.getMethodName() + " "
				+ ste.getLineNumber();
		System.out.println("[******] WARN[" + msg + "] @ " + where);
	}

	/**
	 * Stops the util plugin, in this version it will simply close the testing
	 * file.
	 */
	static void stop() {
		if (logEnabled) {
			ourLog.close();
		}

	}

	/**
	 * Starts the module: it means that it will simply create the debug file.
	 */
	static void start() {

		Arrays.fill(sLogFiles, null);
		if (logEnabled) {

			String logs_dir = getLogsDir();
			File logs_dirOb = new File(logs_dir);
			logs_dirOb.mkdirs();

			String timeStampDir = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss")
					.format(new Date());

			String logFileName = logs_dir + File.separator + timeStampDir
					+ ".txt";

			try {
				ourLog = new PrintWriter(logFileName);
			} catch (FileNotFoundException e) {
				System.out.println("Cannot create the log @ " + logFileName);
				System.exit(-1);
			}
		}

	}

	private static String getLogsDir() {
		Location instanceLoc = Platform.getInstanceLocation();
		String cwp = instanceLoc.getURL().getPath();
		debug_var(335252, "the workspace path IN UTILS is " + cwp);
		return cwp + "logs";
	}

	public static String scaledNumberToString(int tick, int aScale) {
		String output;
		int scale = aScale;
		if (scale > 0) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(
					Locale.getDefault());
			char sep = symbols.getDecimalSeparator();
			output = "0" + sep;
			while (--scale > 0)
				output += "0";
			output += tick;
		} else {
			output = Integer.toString(tick);
			while (scale++ != 0)
				output += "0";
		}
		return output;
	}

	public static String scaledNumberToString2(int tick, int scale) {
		StringBuilder sb = new StringBuilder();

		char sep = new DecimalFormatSymbols(Locale.getDefault())
				.getDecimalSeparator();

		String tick2 = Integer.toString(tick);
		int tickLen = tick2.length();

		if (scale == 0) {
			return tick2;
		} else if (tickLen <= scale) {
			// it has to starts with "0."
			sb.append('0');
			sb.append(sep);
			for (int i = 0; i < scale - tickLen; i++) {
				sb.append('0');
			}
			sb.append(tick2);
		} else {
			//
			for (int i = 0; i < tickLen; i++) {
				if (i == scale) {
					sb.append(sep);
				}
				sb.append(tick2.charAt(i));
			}
		}

		return sb.toString();
	}
}
