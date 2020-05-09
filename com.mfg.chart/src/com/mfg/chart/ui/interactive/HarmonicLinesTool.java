package com.mfg.chart.ui.interactive;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mfg.opengl.chart.IGLChartCustomization;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.commands.HarmonyLinesChangeLevelHandler;
import com.mfg.chart.commands.ShowHarmonicLinesSettingsHandler;
import com.mfg.chart.commands.ShowHarmonyLinesHandler;
import com.mfg.chart.profiles.Profile;
import com.mfg.utils.ImageUtils;

public class HarmonicLinesTool extends InteractiveTool {
	public static String HARMONIC_LINES_UP_COMMAND = "com.mfg.chart.commands.showHarmonyLines_up";
	public static String HARMONIC_LINES_DOWN_COMMAND = "com.mfg.chart.commands.showHarmonyLines_down";

	public static String PROFILE_SET_KEY = "HarmonicLinesTool";
	private static final String KEY_TYPE = "type";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_COLOR = "color";
	public static String KEY_PARTITION = "partition";
	public static String KEY_MULTIPLIER = "multiplier";
	public static final float[][] DEFAULT_COLORS = new float[][] { COLOR_BLUE,
			COLOR_BLUE, COLOR_RED, COLOR_GREEN, COLOR_YELLOW };

	public static class Settings {

		/**
		 * 2 or 3
		 */
		int _partition;
		int _multiplier;
		int[] _lineWidths;
		int[] _lineTypes;
		float[][] _colors;

		public Settings() {
			_multiplier = 9;
			_partition = 2;
			_colors = Arrays.copyOf(DEFAULT_COLORS, DEFAULT_COLORS.length);
			_lineWidths = new int[] { 4, 1, 1, 1, 1 };
			_lineTypes = new int[] { 0, 0, 0, 0, 0 };
		}

		@Override
		public Settings clone() {
			Settings s = new Settings();
			s.updateFromSettings(this);
			return s;
		}

		public void updateFromSettings(Settings s) {
			Profile p = new Profile();
			s.fillProfile(p);
			updateFromProfile(p);
		}

		public void fillProfile(Profile p) {
			p.putInt(KEY_MULTIPLIER, getMultiplier());
			p.putInt(KEY_PARTITION, getPartition());

			for (int i = 0; i <= 4; i++) {
				p.putFloatArray(KEY_COLOR + i, _colors[i]);
				p.putInt(KEY_WIDTH + i, _lineWidths[i]);
				p.putInt(KEY_TYPE + i, _lineTypes[i]);
			}
		}

		public void updateFromProfile(Profile p) {
			setMultiplier(p.getInt(KEY_MULTIPLIER, 9));
			setPartition(p.getInt(KEY_PARTITION, 2));

			for (int i = 0; i <= 4; i++) {
				_colors[i] = p.getFloatArray(KEY_COLOR + i, DEFAULT_COLORS[i]);
				_lineWidths[i] = p.getInt(KEY_WIDTH + i, i == 0 ? 4 : 1);
				_lineTypes[i] = p.getInt(KEY_TYPE + i, 0);
			}
		}

		public int getMultiplier() {
			return _multiplier;
		}

		public void setMultiplier(int multiplier) {
			_multiplier = multiplier;
		}

		public int getPartition() {
			return _partition;
		}

		public float[] getColorMaster() {
			return _colors[0];
		}

		public void setColorMaster(float[] colorMaster) {
			_colors[0] = colorMaster;
		}

		public float[] getColor1() {
			return _colors[1];
		}

		public void setColor1(float[] color1) {
			_colors[1] = color1;
		}

		public float[] getColor2() {
			return _colors[2];
		}

		public void setColor2(float[] color2) {
			_colors[2] = color2;
		}

		public float[] getColor3() {
			return _colors[3];
		}

		public void setColor3(float[] color3) {
			_colors[3] = color3;
		}

		public float[] getColor4() {
			return _colors[4];
		}

		public void setColor4(float[] color4) {
			_colors[4] = color4;
		}

		public float[][] getColors() {
			return _colors;
		}

		public void setColors(float[][] colors) {
			_colors = colors;
		}

		public int[] getLineTypes() {
			return _lineTypes;
		}

		public void setLineTypes(int[] lineTypes) {
			_lineTypes = lineTypes;
		}

		public int[] getLineWidths() {
			return _lineWidths;
		}

		public void setLineWidths(int[] lineWidths) {
			_lineWidths = lineWidths;
		}

		/**
		 * Only 2 or 3 are allowed.
		 * 
		 * @param partition
		 *            2 or 3.
		 */
		public void setPartition(int partition) {
			if (partition != 2 && partition != 3) {
				throw new IllegalArgumentException("Only 2 or 3 are allowed.");
			}
			_partition = partition;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(_colors);
			result = prime * result + Arrays.hashCode(_lineTypes);
			result = prime * result + Arrays.hashCode(_lineWidths);
			result = prime * result + _multiplier;
			result = prime * result + _partition;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Settings other = (Settings) obj;
			if (!Arrays.deepEquals(_colors, other._colors))
				return false;
			if (!Arrays.equals(_lineTypes, other._lineTypes))
				return false;
			if (!Arrays.equals(_lineWidths, other._lineWidths))
				return false;
			if (_multiplier != other._multiplier)
				return false;
			if (_partition != other._partition)
				return false;
			return true;
		}

	}

	public static class VisibleLines {
		public boolean lines2 = false;
		public boolean lines3 = false;
		public boolean lines4 = false;
	}

	protected Settings _settings;
	private final VisibleLines _visibiles;
	private Partition _bestPartition;

	public HarmonicLinesTool(Chart chart) {
		super("Harmonic Lines", chart, BITMAP_HARMONIC_LINES_TOOL_ICON);
		setTooltip("Harmonic Lines (H H): show/hide the Harmonic Lines.");
		_settings = new Settings();
		_visibiles = new VisibleLines();
		setAlwaysPaint(false);
	}

	@Override
	protected List<Profile> createProfilePresets() {
		Profile p = new Profile("Preset 1");
		Settings s = new Settings();
		s.fillProfile(p);
		return Arrays.asList(p);
	}

	@Override
	protected void migrateProfile(Profile p) {
		for (int i = 0; i <= 4; i++) {
			if (!p.containsKey(KEY_COLOR + i)) {
				p.putFloatArray(KEY_COLOR + i, DEFAULT_COLORS[i]);
			}

			if (!p.containsKey(KEY_WIDTH + i)) {
				p.putInt(KEY_WIDTH + i, i == 0 ? 4 : 1);
			}

			if (!p.containsKey(KEY_TYPE + i)) {
				p.putInt(KEY_TYPE + i, 1);
			}
		}

		super.migrateProfile(p);
	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_SET_KEY;
	}

	@Override
	public void paintOnScreenMatrix(GL2 gl, int w, int h) {
		try {
			PlotRange yrange = getChart().getYRange();
			_bestPartition = findBestPartition(yrange, _settings, _visibiles);
			if (_bestPartition == null) {
				out.println("There is a problem here, the partition is not found at "
						+ getChart().getYRange());
			} else {
				// paint multiplier
				gl.glColor4fv(COLOR_DARK_RED, 0);
				String str = "HLM "
						+ getChart().glChart.getCustom().formatYTick(
								_bestPartition.multiplierOrHalf);
				gl.glRasterPos2d(
						w
								- _glut.glutBitmapLength(
										GLUT.BITMAP_HELVETICA_10, str) - 5,
						h - 15);
				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("HL error: " + getChart().getYRange(), e);
		}
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		paintLines(_bestPartition, gl);
	}

	static class Partition implements Comparable<Partition> {
		public List<Double> lines1;
		public List<Double> lines2;
		public List<Double> lines3;
		public List<Double> lines4;
		public int totalVisible;
		public int level;

		public double masterMultiplier;
		public double multiplierOrHalf;
		public double multiplier;

		public Partition() {
		}

		public void computeExtraLines(int partNumber) {
			lines2 = generateSecondary(lines1, partNumber);
			lines3 = generateTertiary(lines1, lines2, partNumber);
			lines3 = generateQuaternary(lines1, lines2, lines3, partNumber);
		}

		@Override
		public int compareTo(Partition o) {
			return Integer.compare(totalVisible, totalVisible);
		}
	}

	static int countVisibleLines(List<Double> lines, PlotRange yrange) {
		if (lines == null) {
			return -1;
		}

		int n = 0;
		for (Double line : lines) {
			if (yrange.contains(line.doubleValue())) {
				n++;
			}
		}
		return n;
	}

	private static Partition findBestPartition(PlotRange yrange,
			Settings settings, VisibleLines visibles) {

		Assert.isTrue(yrange.getLength() >= 0, "The range length is not right");

		// int min = settings._minNumberOfLines;
		int minmult = settings._multiplier;

		List<Partition> allParts = new ArrayList<>();

		{
			// generate partitions
			List<Double> lines;
			int base = 1;
			double mult;
			do {
				mult = minmult * base;
				for (double factor2 : new double[] { mult / 2, mult, mult * 2 }) {
					lines = generatePrimary(factor2, yrange);
					if (!lines.isEmpty()) {
						Partition partition = new Partition();
						partition.lines1 = lines;
						partition.masterMultiplier = mult * 10;
						partition.multiplierOrHalf = factor2;
						partition.multiplier = mult;
						allParts.add(partition);
						// out.println(mult + " "
						// + Arrays.toString(partition.lines1.toArray()));
					}
				}
				base = base * 10;
			} while (mult < yrange.getLength());
		}

		Partition best = null;
		int bestCount = Integer.MAX_VALUE;
		for (Partition p : allParts) {
			int count = countVisibleLines(p.lines1, yrange);
			if (count < bestCount && count > 0) {
				best = p;
				bestCount = count;
			}
		}

		if (best != null) {
			int P = settings.getPartition();
			if (visibles.lines2) {
				best.lines2 = generateSecondary(best.lines1, P);
			}
			if (visibles.lines3) {
				best.lines3 = generateTertiary(best.lines1, best.lines2, P);
			}
			if (visibles.lines4) {
				best.lines4 = generateQuaternary(best.lines1, best.lines2,
						best.lines3, P);
			}
		}

		return best;
	}

	private static List<Double> generatePrimary(double factor, PlotRange yrange) {
		List<Double> lines = new ArrayList<>();
		double lower = yrange.lower;
		double upper = yrange.upper;

		double line = -1;
		int i = (int) (lower / factor);
		do {
			line = i * factor;
			if (line >= lower && line < upper) {
				lines.add(new Double(line));
			}
			i++;
		} while (line < upper);

		if (lines.isEmpty()) {
			lines.add(new Double(line - factor));
			lines.add(new Double(line));
		} else {
			double first = lines.get(0).doubleValue();
			double last = lines.get(lines.size() - 1).doubleValue();
			lines.add(0, new Double(first - factor));
			lines.add(new Double(last + factor));
		}

		return lines;
	}

	private void paintLines(Partition partition, GL2 gl) {
		PlotRange xrange = getChart().getXRange();

		for (double line : partition.lines1) {
			double f = partition.masterMultiplier;
			boolean master = line / f == (long) (line / f);
			float[] color;
			int lineWidth;
			int lineType;
			if (master) {
				lineWidth = _settings.getLineWidths()[0];
				lineType = _settings.getLineTypes()[0];
				color = _settings.getColorMaster();
			} else {
				lineWidth = _settings.getLineWidths()[1];
				lineType = _settings.getLineTypes()[1];
				color = _settings.getColor1();
			}

			gl.glPushAttrib(GL2.GL_LINE_BIT);

			if (lineType != 0) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(lineType, STIPPLE_PATTERN);
			}

			gl.glLineWidth(lineWidth);
			gl.glColor4fv(color, 0);

			gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(xrange.lower, line);
			gl.glVertex2d(xrange.upper, line);
			gl.glEnd();

			if (lineType != 0) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(lineType, STIPPLE_PATTERN);
			}

			gl.glPopAttrib();
		}

		paintPrices(gl, partition.lines1);

		if (_visibiles.lines2) {
			paintPartition(partition.lines2, _settings.getColor2(),
					_settings.getLineWidths()[2], _settings.getLineTypes()[2],
					gl);
		}

		if (_visibiles.lines3) {
			paintPartition(partition.lines3, _settings.getColor3(),
					_settings.getLineWidths()[3], _settings.getLineTypes()[3],
					gl);
		}

		if (_visibiles.lines4) {
			paintPartition(partition.lines4, _settings.getColor4(),
					_settings.getLineWidths()[4], _settings.getLineTypes()[4],
					gl);
		}
	}

	private void paintPrices(GL2 gl, List<Double> values) {
		Chart chart = getChart();
		IGLChartCustomization custom = chart.getGLChart().getCustom();

		double x = chart.getXRange().lower;
		double offs = chart.getYRange().plotWidth(10,
				chart.getPlotScreenHeight());

		for (Double value : values) {
			double price = value.doubleValue();
			double y = price - offs;
			gl.glRasterPos2d(x, y);

			_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10,
					custom.formatYTick(price));
		}
	}

	private void paintPartition(List<Double> lines, float[] color, int width,
			int type, GL2 gl) {
		PlotRange xrange = getChart().getXRange();
		PlotRange yrange = getChart().getYRange();

		if (type != 0) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(type, STIPPLE_PATTERN);
		}
		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4fv(color, 0);
		gl.glLineWidth(width);

		for (Double line : lines) {
			double y = line.doubleValue();
			if (yrange.contains(y)) {
				gl.glVertex2d(xrange.lower, y);
				gl.glVertex2d(xrange.upper, y);
			}
		}
		gl.glEnd();
		gl.glPopAttrib();

		if (type != 0) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}

		paintPrices(gl, lines);
	}

	public Settings getSettings() {
		return _settings;
	}

	public VisibleLines getVisibiles() {
		return _visibiles;
	}

	public void setSettings(Settings settings) {
		_settings = settings;
	}

	static void debug(String str) {
		out.println(str);
	}

	/**
	 * The same of {@link #generateSecondary(List, int)}.
	 * 
	 * @param lines
	 * @param partition
	 * @return
	 */
	private static List<Double> asList(double[] lines) {
		List<Double> list = new ArrayList<>();
		for (double d : lines) {
			list.add(new Double(d));
		}
		return list;
	}

	/**
	 * 
	 * @param lines1
	 *            Primary lines.
	 * @param partition
	 *            2 or 3
	 * @return
	 */
	static List<Double> generateSecondary(List<Double> lines1, int partition) {
		List<Double> lines2 = new ArrayList<>();

		if (partition == 2) {
			for (int i = 1; i < lines1.size(); i++) {
				double lower = lines1.get(i - 1).doubleValue();
				double upper = lines1.get(i).doubleValue();
				lines2.add(new Double(lower + (upper - lower) / 2));
			}
		} else {
			for (int i = 1; i < lines1.size(); i++) {
				double lower = lines1.get(i - 1).doubleValue();
				double upper = lines1.get(i).doubleValue();
				double len = (upper - lower) / 3;
				lines2.add(new Double(lower + len));
				lines2.add(new Double(upper - len));
			}
		}

		return lines2;
	}

	/**
	 * 
	 * @param lines2
	 *            Primary lines.
	 * @param partition
	 *            2 or 3
	 * @return
	 */
	static List<Double> generateTertiary(List<Double> lines1,
			List<Double> lines2, int partition) {
		List<Double> lines3 = new ArrayList<>();

		if (partition == 2) {
			double step = (lines1.get(1).doubleValue() - lines1.get(0)
					.doubleValue()) / 10;

			for (Double line : lines2) {
				double dline = line.doubleValue();
				lines3.add(new Double(dline - step * 3));
				lines3.add(new Double(dline - step));
				lines3.add(new Double(dline + step));
				lines3.add(new Double(dline + step * 3));
			}
		} else {
			assert lines2.size() % 2 == 0;
			for (int i = 0; i < lines2.size(); i += 2) {
				double lower = lines2.get(i).doubleValue();
				double upper = lines2.get(i + 1).doubleValue();
				double step = (upper - lower) / 2;
				lines3.add(new Double(lower - step));
				lines3.add(new Double(lower + step));
				lines3.add(new Double(upper + step));
			}
		}

		return lines3;
	}

	/**
	 * 
	 * @param lines1
	 *            Primary lines.
	 * @param partition
	 *            2 or 3
	 * @return
	 */
	static List<Double> generateQuaternary(List<Double> lines1,
			List<Double> lines2, List<Double> lines3, int partition) {
		List<Double> lines4 = new ArrayList<>();

		if (partition == 2) {
			double step = (lines1.get(1).doubleValue() - lines1.get(0)
					.doubleValue()) / 10;
			for (Double line : lines2) {
				double dline = line.doubleValue();
				lines4.add(new Double(dline - step * 4));
				lines4.add(new Double(dline - step * 2));
				lines4.add(new Double(dline + step * 2));
				lines4.add(new Double(dline + step * 4));
			}
		} else {
			double step = (lines3.get(1).doubleValue() - lines3.get(0)
					.doubleValue()) / 4;
			for (Double line : lines3) {
				lines4.add(new Double(line.doubleValue() - step));
				lines4.add(new Double(line.doubleValue() + step));
			}
		}

		return lines4;
	}

	public static void __main(@SuppressWarnings("unused") String[] args) {
		{
			PlotRange r = new PlotRange(1, 37);
			r = new PlotRange(186397, 151751);

			Partition p = findBestPartition(r, new Settings(),
					new VisibleLines());
			out.println(Arrays.toString(p.lines1.toArray()));
			System.exit(1);
		}
		{
			out.println("12th division");
			// double[] lines1 = { 13950, 14400, 14850 };
			double[] lines1 = { 0, 90, 180 };
			List<Double> lines1_list = asList(lines1);

			List<Double> lines2 = generateSecondary(lines1_list, 3);
			List<Double> lines3 = generateTertiary(lines1_list, lines2, 3);
			List<Double> lines4 = generateQuaternary(lines1_list, lines2,
					lines3, 3);

			out.println("1: " + Arrays.toString(lines1));
			out.println("2: " + Arrays.toString(lines2.toArray()));
			out.println("3: " + Arrays.toString(lines3.toArray()));
			out.println("4: " + Arrays.toString(lines4.toArray()));
		}

		{
			out.println("10th division");
			// double[] lines1 = { 13950, 14400, 14850 };
			double[] lines1 = { 0, 90, 180 };
			// double[] lines1 = { 9000.0, 18000.0 };
			List<Double> lines1_list = asList(lines1);

			List<Double> lines2 = generateSecondary(lines1_list, 2);
			List<Double> lines3 = generateTertiary(lines1_list, lines2, 2);
			List<Double> lines4 = generateQuaternary(lines1_list, lines2,
					lines3, 2);

			out.println("1: " + Arrays.toString(lines1));
			out.println("2: " + Arrays.toString(lines2.toArray()));
			out.println("3: " + Arrays.toString(lines3.toArray()));
			out.println("4: " + Arrays.toString(lines4.toArray()));
		}
	}

	@Override
	public String getKeywords() {
		return "multiplier secondary line ratio master primary tertiary quaternary color type width";
	}

	@Override
	public void fillMenu(IMenuManager menu) {
		MenuManager menu2 = new MenuManager("Harmonic Lines",
				ImageUtils.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
						"icons/HL_16.png"), "harmoniclines");
		menu.add(menu2);
		menu2.add(new ToolAction(
				ShowHarmonyLinesHandler.SWAP_RATIO_HARMONIC_LINES_COMMAND) {
			@Override
			public void run() {
				ShowHarmonyLinesHandler.execute(getActionDefinitionId(),
						getChart());
			}
		});

		menu2.add(new ToolAction(HARMONIC_LINES_UP_COMMAND) {
			@Override
			public void run() {
				HarmonyLinesChangeLevelHandler.execute(getActionDefinitionId(),
						getChart());
			}
		});

		menu2.add(new ToolAction(HARMONIC_LINES_DOWN_COMMAND) {
			@Override
			public void run() {
				HarmonyLinesChangeLevelHandler.execute(getActionDefinitionId(),
						getChart());
			}
		});

		menu2.add(new ToolAction(ShowHarmonicLinesSettingsHandler.CMD_ID) {
			@Override
			public void run() {
				ShowHarmonicLinesSettingsHandler.execute(getChart());
			}
		});
	}
}
