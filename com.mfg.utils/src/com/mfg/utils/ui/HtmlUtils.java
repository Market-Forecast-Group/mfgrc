package com.mfg.utils.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * some configurable tools to build the HTML representation of objects.
 * 
 * @author gardero
 * 
 */
public class HtmlUtils {
	public static final int DAY_DURATION = 86400000;
	public static final int HOUR_DURATION = 3600000;
	public static final int MINUTE_DURATION = 60000;

	protected boolean fOn = true;
	protected boolean fMultiline = true;

	public static HtmlUtils Plain = new HtmlUtils(false, false);

	public HtmlUtils() {
		this(true);
	}

	public HtmlUtils(boolean isOn) {
		super();
		fOn = isOn;
	}

	public HtmlUtils(boolean isOn, boolean isMultiline) {
		super();
		fOn = isOn;
		fMultiline = isMultiline;
	}

	public boolean isOn() {
		return fOn;
	}

	public void setOn(boolean aOn) {
		fOn = aOn;
	}

	public boolean isMultiline() {
		return fMultiline;
	}

	public void setMultiline(boolean aMultiline) {
		fMultiline = aMultiline;
	}

	public String getHtmlString(IHtmlStringProvider obj) {
		if (!isOn())
			return obj.getHtmlBody(this);
		return "<html><body>" + obj.getHtmlBody(this) + "</body></html>";
	}

	public String getBreakLine() {
		if (!isMultiline())
			return " ";
		if (!isOn())
			return "\n";
		return "<br>";
	}

	public String color(String aString, Color aColor) {
		return getColorString(aColor) + aString + getColorStringClose();
	}

	public String bold(String aString) {
		return getBoldString() + aString + getBoldStringClose();
	}

	private String getBoldStringClose() {
		if (!isOn())
			return "";
		return "</b>";
	}

	private String getBoldString() {
		if (!isOn())
			return "";
		return "<b>";
	}

	public String color(String aString, String rgb) {
		return getColorString(rgb) + aString + getColorStringClose();
	}

	public String getText(String htmlText, String otherText) {
		if (isOn())
			return htmlText;
		return otherText;
	}

	public static String getText(boolean condition, String htmlText,
			String otherText) {
		if (condition)
			return htmlText;
		return otherText;
	}

	public String getColorString(Color c) {
		if (!isOn())
			return "";
		return "<font color=\"" + toEx(c) + "\">";
	}

	/**
	 * 
	 * @param rgb
	 *            example 33AA04
	 * @return
	 */
	public String getColorString(String rgb) {
		if (!isOn())
			return "";
		return "<font color=\"" + fromEx(rgb) + "\">";
	}

	public String getColorStringClose() {
		if (!isOn())
			return "";
		return "</font\">";
	}

	public String getHtmlBucketList(List<?> list) {
		return getHtmlBucketList(list, true, true);
	}

	public String getHtmlBucketList(List<?> list, boolean opened, boolean closed) {
		boolean cond = isOn() && isMultiline();
		String res = opened ? (getText(cond, "<ul>", "{" + getBreakLine()))
				: "";
		for (int i = 0; i < list.size(); i++) {
			Object e = list.get(i);
			res += getText(cond, "<li>", "");
			res += getHtmlStringIfDef(e);
			String s = (i < list.size() - 1) ? ", " : "";
			res += getText(cond, "</li>", s + getBreakLine());
		}
		if (closed)
			res += getText(cond, "</ul>", "}");
		return res;
	}

	public String getHtmlSortedList(List<?> list, boolean opened, boolean closed) {
		boolean cond = isOn() && isMultiline();
		String res = opened ? (getText(cond, "<ol>", "{" + getBreakLine()))
				: "";
		for (int i = 0; i < list.size(); i++) {
			Object e = list.get(i);
			res += getText(cond, "<li>", i + ". ");
			res += getHtmlStringIfDef(e);
			String s = (i < list.size() - 1) ? ", " : "";
			res += getText(cond, "</li>", s + getBreakLine());
		}
		if (closed)
			res += getText(cond, "</ol>", "}");
		return res;
	}

	public String getHtmlStringIfDef(Object aE) {
		if (aE instanceof IHtmlStringProvider)
			return ((IHtmlStringProvider) aE).getHtmlBody(this);
		return aE.toString();
	}

	private static String toEx(Color c) {
		return "#" + toEx(c.getRed()) + toEx(c.getGreen()) + toEx(c.getBlue());
	}

	private static String fromEx(String RGB) {
		return "#" + RGB;
	}

	private static String toEx(int a) {
		String res = Integer.toHexString(a);
		if (res.length() == 1)
			res = "0" + res;
		return res;
	}

	public String getDateDHMFormat(long timePar) {
		long time = timePar;
		int day = (int) (time / DAY_DURATION);
		time = time % DAY_DURATION;
		int hour = (int) (time / HOUR_DURATION);
		time = time % HOUR_DURATION;
		int min = (int) (time / MINUTE_DURATION);
		return color("" + day, Color.GREEN.darker()) + ":"
				+ color("" + hour, Color.RED) + ":"
				+ color("" + min, Color.BLUE);
	}

	// private int fromEx(String ex){
	// int res = Integer.parseInt(ex, 16);
	// return res;
	// }

	/**
	 * Like {@code toString()} but render HTML.
	 * 
	 * @return
	 */
	public String getRawHtml(boolean aIncludeHtmlTag, Object obj) {
		String str = obj.toString();
		if (!isOn())
			return str;
		StringBuilder builder = new StringBuilder();
		if (aIncludeHtmlTag) {
			builder.append("<html>");
		}
		for (char ch : str.toCharArray()) {
			boolean append = true;

			if (ch == '\n') {
				builder.append("<br>");
				append = false;
			} else if (ch == '{') {
				append = true;
			} else if (ch == '}') {
				append = true;
			}
			if (append)
				builder.append(ch);
		}
		if (aIncludeHtmlTag) {
			builder.append("</html>");
		}
		return builder.toString();
	}

	public static String htmlEscape(String aText) {
		if (aText == null) {
			return "";
		}
		StringBuffer escapedText = new StringBuffer();
		for (int i = 0; i < aText.length(); i++) {
			char ch = aText.charAt(i);
			if (ch == '\'')
				escapedText.append("&#39;");
			else if (ch == '\"')
				escapedText.append("&#34;");
			else if (ch == '<')
				escapedText.append("&lt;");
			else if (ch == '>')
				escapedText.append("&gt;");
			else if (ch == '&')
				escapedText.append("&amp;");
			else if (ch == '\n')
				escapedText.append("<br>\n");
			else if (ch == ' ')
				escapedText.append("&nbsp;");
			else if (ch == '\t')
				escapedText.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
				escapedText.append(ch);
		}
		String result = escapedText.toString();
		return result;
	}

	public interface IHtmlStringProvider {
		/**
		 * gets the HTML representation of the object.
		 * 
		 * @param util
		 *            an {@code HtmlUtils} object with some tools to build the
		 *            HTML representation of this object.
		 * @return the HTML string.
		 */
		String getHtmlBody(HtmlUtils util);
	}

	public static void main(String[] args) {
		ArrayList<Object> e = new ArrayList<>();
		e.add("A");
		e.add("B");
		e.add("C");
		HtmlUtils u = new HtmlUtils(false, false);
		System.out.println(u.getHtmlBucketList(e));
	}

	public String underline(String aText) {
		return getText("<u>" + aText + "</u>", aText);
	}

	public String sub(String aString) {
		return getText("<sub>" + aString + "</sub>", aString);
	}

}
