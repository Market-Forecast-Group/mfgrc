package org.mfg.opengl.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

public class Plot {
	static class RegistryEntry {
		public RegistryEntry(final IDataset dataset1,
				final ISeriesPainter painter1) {
			this.dataset = dataset1;
			this.painter = painter1;
			visible = true;
		}

		IDataset dataset;
		ISeriesPainter painter;
		boolean visible;
	}

	private final Map<IDataset, RegistryEntry> map;
	private final List<RegistryEntry> list;

	public PlotRange xrange;
	public PlotRange yrange;
	public int screenWidth;
	public int screenHeight;

	public Plot() {
		map = new HashMap<>();
		list = new ArrayList<>();
		xrange = new PlotRange(0, 1);
		yrange = new PlotRange(0, 1);
	}

	public void addDataset(final IDataset ds, final ISeriesPainter painter) {
		final RegistryEntry r = new RegistryEntry(ds, painter);
		map.put(ds, r);
		list.add(r);
	}

	/**
	 * Check if there is data in the datasets.
	 * 
	 * @return <code>true</code> if there is not data in the datasets.
	 */
	public boolean isEmpty() {
		for (RegistryEntry entry : list) {
			IDataset ds = entry.dataset;
			for (int i = 0; i < ds.getSeriesCount(); i++) {
				if (ds.getItemCount(i) > 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void setVisibleDataset(final IDataset ds) {
		map.get(ds).visible = true;
	}

	public void autoRange(final IDataset ds) {
		double lower = Double.MAX_VALUE;
		double upper = Double.MIN_VALUE;
		for (int series = 0; series < ds.getSeriesCount(); series++) {
			for (int item = 0; item < ds.getItemCount(series); item++) {
				final double y = ds.getY(series, item);
				if (y < lower) {
					lower = y;
				}
				if (y > upper) {
					upper = y;
				}
			}
		}
		final double len = yrange.upper - yrange.lower;
		final double margin = len * 0.1;
		if (Math.abs(yrange.lower - lower) > margin || lower < yrange.lower) {
			lower -= margin;
		} else {
			lower = yrange.lower;
		}

		if (Math.abs(yrange.upper - upper) > margin || upper > yrange.upper) {
			upper += margin;
		} else {
			upper = yrange.upper;
		}
		yrange = new PlotRange(lower, upper);
	}

	public void paint(final GL2 gl) {
		for (final RegistryEntry reg : list) {
			final IDataset ds = reg.dataset;
			synchronized (ds) {
				reg.painter.paint(gl, ds, xrange, yrange);
			}
		}
	}
}
