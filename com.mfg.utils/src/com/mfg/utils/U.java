package com.mfg.utils;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.mfg.utils.i_fp.Field_fp;

/**
 * A simple container for useful constants, functions, immutable things and the
 * like.
 * <p>
 * The name U is short because in this way Eclipse suggests it quite easily and
 * imports automatically the static things in here.
 * 
 * <p>
 * The class is immutable (it has no "variables") and all the methods are pure,
 * reentrant, they are not synchronized because there is no state to synchronize
 * to.
 * 
 * <p>
 * The class is not instantiable and you cannot derive from it (otherwise we may
 * have U1, U2..., like the band...
 * 
 * <p>
 * For this reasons the fields are immutable, either constants or immutable
 * objects, like strings or regular expression patterns.
 * 
 * @author Sergio
 * 
 */
public final class U {

	private U() {
		// Do not call me
	}

	/**
	 * dumps to the terminal the object o.
	 * <p>
	 * It does not impose any limitation on the object o and it does not alter
	 * it in any way.
	 * 
	 * @param o
	 *            the object which you want to dump
	 */
	public static void dump(Object o) {
		Field_fp ffp = s_fp.get_detail_fp(o);
		s_fp.print_field_fp(ffp);
	}

	/**
	 * This is the ever present pattern of the comma separated values, used in
	 * all the application. It is public and static because it is immutable;
	 */
	public static Pattern commaPattern = Pattern.compile(",");

	/**
	 * this is the normal date format in MFG, with millisecond precision, this
	 * is usually used in the socket representation of the dates. Milliseconds
	 * precision is used because range bars use it.
	 */
	public static String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * This is the format used to format the date/time in a quote message. It
	 * has also the millisecond information and it is formatted in the local
	 * time zone
	 */
	public static String QUOTE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * A simple tuple which is used to hold two objects.
	 * <p>
	 * Nothing is done to prevent copy semantics, memory leaks, etc...
	 * <p>
	 * It is only a container, usually used simply to hold the return type of a
	 * method.
	 * 
	 * @author Sergio
	 * 
	 * @param <Klass1>
	 *            the type of the first item in the tuple
	 * @param <Klass2>
	 *            the type of the second item in the tuple
	 */
	public static class Tuple<Klass1, Klass2> {
		public Klass1 f1;
		public Klass2 f2;
	}

	/**
	 * joins a variable list of strings using a comma as a delimiter.
	 * <p>
	 * This is only a general purpose join, maybe not too efficient but useful.
	 * 
	 * @param strings
	 *            a variable length of strings
	 * 
	 * @return the strings joined together with a comma.
	 */
	public static String join(Object... strings) {
		StringBuilder sb = new StringBuilder();
		// boolean first = true;
		for (Object el : strings) {
			sb.append(el.toString());
			sb.append(',');
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.substring(0, sb.length() - 1); // to remove the final comma.
	}

	public static void messageBox(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(null, "INFO", message);
			}
		});

	}

	/**
	 * 
	 * de-vote the {@linkplain InterruptedException} of the normal sleep when it
	 * is not needed.
	 * 
	 * <p>
	 * the normal {@linkplain Thread#sleep(long)} will throw a checked exception
	 * that must caught or declared to be thrown and sometimes this is
	 * inconvenient, because we <b>know</b> that this thread is not going to be
	 * interrupted.
	 * 
	 * @param delay
	 *            how many milliseconds to wait
	 */
	public static void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// this is only a runtime exception
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * The normal debug function.
	 * 
	 * @param aMsg_id
	 * @param objs
	 */
	public synchronized static void debug_var(int aMsg_id, Object... objs) {
		Utils.debug_handler_var(-1, aMsg_id, objs);
	}

}
