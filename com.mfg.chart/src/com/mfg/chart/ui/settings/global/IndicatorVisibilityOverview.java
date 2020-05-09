package com.mfg.chart.ui.settings.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.Bands2Layer;
import com.mfg.chart.layers.BandsLayer;
import com.mfg.chart.layers.ChannelLayer;
import com.mfg.chart.layers.FinalScaleElementLayer;
import com.mfg.chart.layers.MergedLayer;
import com.mfg.chart.layers.PivotLayer;
import com.mfg.chart.layers.ProbabilityLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.layers.ZZLayer;
import com.mfg.chart.ui.osd.GlobalScaleElementLayer;

public class IndicatorVisibilityOverview extends Composite {
	private Table _table;
	TableViewer _tableViewer;
	Runnable _refresh;
	Runnable _dispose;
	Label _filtersLabel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public IndicatorVisibilityOverview(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		_filtersLabel = new Label(this, SWT.NONE);
		_filtersLabel.setText("Filters Enabled          ");

		_tableViewer = new TableViewer(this, SWT.BORDER | SWT.HIDE_SELECTION
				| SWT.NO_FOCUS);
		_table = _tableViewer.getTable();
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_table.setLinesVisible(true);
		_table.setHeaderVisible(true);
	}

	public IndicatorVisibilityOverview(final Chart chart, Composite parent) {
		this(parent, SWT.None);

		MergedLayer<GlobalScaleElementLayer> globalLayer = chart
				.getIndicatorLayer().getGlobalLayer();

		TableViewerColumn col = new TableViewerColumn(_tableViewer, SWT.NONE);
		col.getColumn().setText("Scale");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ScaleLayer scale = (ScaleLayer) element;
				return Integer.toString(scale.getLevel());
			}

			@Override
			public Color getForeground(Object element) {
				return getColor(((ScaleLayer) element).getLayerColor());
			}
		});

		Set<String> showSet = new HashSet<>(Arrays.asList(
				BandsLayer.LAYER_NAME, Bands2Layer.LAYER_NAME_2,
				ZZLayer.LAYER_NAME, PivotLayer.LAYER_NAME,
				ChannelLayer.LAYER_NAME, ProbabilityLayer.LAYER_NAME));

		for (final GlobalScaleElementLayer layer : globalLayer.getLayers()) {
			if (!layer.getLayers().isEmpty()
					&& showSet.contains(layer.getLayers().getFirst().getName())) {
				col = new TableViewerColumn(_tableViewer, SWT.NONE);
				col.getColumn().setText(layer.getName());
				col.getColumn().setWidth(100);
				col.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(Object element) {
						ScaleLayer scale = (ScaleLayer) element;
						FinalScaleElementLayer elemLayer = (FinalScaleElementLayer) scale
								.getLayer(layer.getName());
						return scale.getLevel() + elemLayer.getKey();
					}

					@Override
					public Color getBackground(Object element) {
						ScaleLayer scale = (ScaleLayer) element;
						FinalScaleElementLayer elemLayer = (FinalScaleElementLayer) scale
								.getLayer(layer.getName());
						if (elemLayer.isEnabled() && elemLayer.isVisible()) {
							float[] c = scale.getLayerColor();
							return getColor(c);
						}
						return null;
					}

					@Override
					public Color getForeground(Object element) {
						ScaleLayer scale = (ScaleLayer) element;
						FinalScaleElementLayer elemLayer = (FinalScaleElementLayer) scale
								.getLayer(layer.getName());
						if (elemLayer.isEnabled() && elemLayer.isVisible()) {
							return getDisplay().getSystemColor(SWT.COLOR_WHITE);
						}
						return getDisplay().getSystemColor(SWT.COLOR_GRAY);
					}
				});
			}
		}
		_tableViewer.setContentProvider(new ArrayContentProvider());
		List<ScaleLayer> input = new ArrayList<>(chart.getIndicatorLayer()
				.getScales());
		Collections.reverse(input);
		_tableViewer.setInput(input);

		_refresh = new Runnable() {

			@Override
			public void run() {
				refresh(chart);
			}

		};
		chart.addRangeChangedAction(_refresh);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				chart.removeRangeChangeAction(_refresh);
				chart.removeDisposeListener(_dispose);
			}
		});
		_dispose = new Runnable() {

			@Override
			public void run() {
				getShell().dispose();
			}
		};
		chart.addDisposeListener(_dispose);
		refresh(chart);
	}

	void refresh(final Chart chart) {
		_tableViewer.refresh();
		boolean filtersEnabled = chart.getIndicatorLayer().isFiltersEnabled();
		_filtersLabel.setText("Filters "
				+ (filtersEnabled ? "Enabled" : "Disabled"));
		_filtersLabel.setForeground(getDisplay().getSystemColor(
				filtersEnabled ? SWT.COLOR_DARK_GREEN : SWT.COLOR_DARK_RED));
	}

	static Color getColor(float[] color) {
		int r = (int) (color[0] * 255);
		int g = (int) (color[1] * 255);
		int b = (int) (color[2] * 255);
		RGB rgb = new RGB(r, g, b);
		return SWTResourceManager.getColor(rgb);
	}
}
