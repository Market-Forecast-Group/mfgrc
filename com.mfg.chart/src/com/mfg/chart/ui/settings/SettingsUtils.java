package com.mfg.chart.ui.settings;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.mfg.chart.ChartPlugin;
import com.mfg.utils.ImageUtils;

public class SettingsUtils {
	public static int[] LINE_WIDTHS = { 1, 2, 3, 4, 5, 6, 7, 10, 15 };
	public static Object[] LINE_WIDTHS_INPUT = { Integer.valueOf(1),
			Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4),
			Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7),
			Integer.valueOf(10), Integer.valueOf(15) };

	public static Object[] LINE_TYPES_INPUT = { Integer.valueOf(0),
			Integer.valueOf(1), Integer.valueOf(3), Integer.valueOf(5),
			Integer.valueOf(7), Integer.valueOf(9), Integer.valueOf(11),
			Integer.valueOf(13), Integer.valueOf(15) };

	public static Object[] SHAPE_TYPES_INPUT = { Integer.valueOf(0),
			Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) };

	public static String[] SHAPE_NAMES = { "shape_bar", "shape_circle",
			"shape_rect", "shape_triangle" };

	public static String[] SHAPE_WIDTH_NAMES = { "shape_circle_1",
			"shape_circle_2", "shape_circle_3" };

	public static Object[] SHAPE_WIDTH_INPUT = { Integer.valueOf(0),
			Integer.valueOf(1), Integer.valueOf(2) };

	private SettingsUtils() {
	}

	public static Image getLineWidthImage(int width) {
		ChartPlugin plugin = ChartPlugin.getDefault();
		if (plugin == null) {
			return null;
		}
		Image img = ImageUtils.getBundledImage(plugin, "icons/settings/width"
				+ width + ".gif");
		return img;
	}

	public static int openLineWidthDialog(final Shell shell, int currentWidth) {
		ListPopup dlg = new ListPopup(shell);
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LineWidthLabelProvider());
		dlg.setInput(LINE_WIDTHS_INPUT);
		dlg.setInitialSelection(Integer.valueOf(currentWidth));

		Object result = dlg.open();

		if (result != null) {
			return ((Integer) result).intValue();
		}

		return -1;
	}

	public static Image getLineTypeImage(int width) {
		ChartPlugin plugin = ChartPlugin.getDefault();
		if (plugin == null) {
			return null;
		}
		Image img = ImageUtils.getBundledImage(plugin, "icons/settings/type"
				+ width + ".png");
		return img;
	}

	public static Image getShapeWidthImage(int type) {
		ChartPlugin plugin = ChartPlugin.getDefault();
		if (plugin == null) {
			return null;
		}
		Image img = ImageUtils.getBundledImage(plugin, "icons/settings/"
				+ SHAPE_WIDTH_NAMES[type] + ".png");
		return img;
	}

	public static int openShapeWidthDialog(final Shell shell, int currentType) {
		ListPopup dlg = new ListPopup(shell);
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return getShapeWidthImage(((Integer) element).intValue());
			}

			@Override
			public String getText(Object element) {
				return "";
			}
		});
		dlg.setInput(SHAPE_WIDTH_INPUT);
		dlg.setInitialSelection(Integer.valueOf(currentType));

		Object result = dlg.open();

		if (result != null) {
			return ((Integer) result).intValue();
		}

		return -1;
	}

	public static Image getShapeTypeImage(int type) {
		ChartPlugin plugin = ChartPlugin.getDefault();
		if (plugin == null) {
			return null;
		}
		Image img = ImageUtils.getBundledImage(plugin, "icons/settings/"
				+ SHAPE_NAMES[type] + ".png");
		return img;
	}

	public static int openShapeTypeDialog(final Shell shell, int currentType) {
		ListPopup dlg = new ListPopup(shell);
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return getShapeTypeImage(((Integer) element).intValue());
			}

			@Override
			public String getText(Object element) {
				return "";
			}
		});
		dlg.setInput(SHAPE_TYPES_INPUT);
		dlg.setInitialSelection(Integer.valueOf(currentType));

		Object result = dlg.open();

		if (result != null) {
			return ((Integer) result).intValue();
		}

		return -1;
	}

	public static int openLineTypeDialog(final Shell shell, int currentType) {
		ListPopup dlg = new ListPopup(shell);
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LineTypeLabelProvider());
		dlg.setInput(LINE_TYPES_INPUT);
		dlg.setInitialSelection(Integer.valueOf(currentType));

		Object result = dlg.open();

		if (result != null) {
			return ((Integer) result).intValue();
		}

		return -1;
	}
}
