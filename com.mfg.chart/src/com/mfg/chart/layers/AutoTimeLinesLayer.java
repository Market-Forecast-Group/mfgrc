package com.mfg.chart.layers;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IndicatorLayer.ATLSettings;
import com.mfg.chart.model.IAutoTimeLinesModel;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;

public class AutoTimeLinesLayer extends FinalScaleElementLayer {
	class RatiosLinesDataset implements IDataset {
		public RatiosLinesDataset() {
		}

		@Override
		public int getSeriesCount() {
			return _anchorsDataset.getSeriesCount();
		}

		@Override
		public int getItemCount(int series) {
			return getRatios().length;
		}

		@Override
		public double getX(int series, int item) {
			long x1 = (long) _anchorsDataset.getX(0, 0);
			long x2 = (long) _anchorsDataset.getX(0, 1);

			float ratio = getRatios()[item].getRatio();

			return (long) (x2 + (x2 - x1) * ratio);
		}

		@Override
		public double getY(int series, int item) {
			return _anchorsDataset.getY(0, 1);
		}

	}

	private final class RatiosLinesPainter implements ISeriesPainter {
		private final GLUT _glut;

		public RatiosLinesPainter() {
			_glut = new GLUT();
		}

		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {

			Chart chart = getChart();
			PlotRange yr = chart.getYRange();
			PlotRange xr = chart.getXRange();
			if (_ratiosDataset.getSeriesCount() > 0) {
				for (int i = 0; i < _ratiosDataset.getItemCount(0); i++) {
					RatioInfo info = getRatios()[i];
					if (info.isSelected()) {

						gl.glPushAttrib(GL2.GL_LINE_BIT);

						if (info.getLineType() != STIPPLE_FACTOR_NULL) {
							gl.glEnable(GL2.GL_LINE_STIPPLE);
							gl.glLineStipple(info.getLineType(),
									STIPPLE_PATTERN);
						}

						gl.glLineWidth(info.getLineWidth());

						long x = (long) _ratiosDataset.getX(0, i);
						// long y = lines.getAnchor2().getPrice();

						float[] color = info.getColor();

						gl.glColor4fv(color, 0);
						gl.glBegin(GL.GL_LINE_STRIP);

						gl.glVertex2d(x, yr.upper);
						gl.glVertex2d(x, yr.lower);
						gl.glEnd();

						int x2 = (int) (x + (xr.plotWidth(20,
								chart.glChart.plot.screenWidth)));
						float ratio = info.getRatio();
						String str = Float.toString(ratio);

						int y = (int) _ratiosDataset.getY(0, i);
						gl.glRasterPos2i(x2, y);
						_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);

						gl.glDisable(GL2.GL_LINE_STIPPLE);

						gl.glPopAttrib();
					}
				}
			}
		}
	}

	private final class AnchorsLinesPainter implements ISeriesPainter {
		private GLUT _glut;
		{
			_glut = new GLUT();
		}

		public AnchorsLinesPainter() {
		}

		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {

			if (ds.getItemCount(0) < 2) {
				return;
			}

			BitmapData anchorBmp = BITMAP_AUTO_ANCHOR_LINES_TOOL_ICON;

			PlotRange xr = getChart().getXRange();
			PlotRange yr = getChart().getYRange();
			GLChart glChart = getChart().glChart;
			for (int i = 0; i < 2; i++) {

				final long x = (long) ds.getX(0, i);
				final long y = (long) ds.getY(0, i);

				gl.glColor4fv(getSettings().anchorsColor, 0);
				gl.glBegin(GL.GL_LINE_STRIP);

				gl.glVertex2d(x, yr.upper);
				gl.glVertex2d(x, yr.lower);
				gl.glEnd();

				gl.glRasterPos2i((int) x, (int) y);
				gl.glBitmap(anchorBmp.width, anchorBmp.height, 0, 0, 0, 0,
						anchorBmp.bitmap, 0);

				double xlen = xr.plotWidth(23, glChart.plot.screenWidth);
				double ylen = yr.plotWidth(15, glChart.plot.screenHeight);

				double bmpLen = yr.plotWidth(anchorBmp.height,
						glChart.plot.screenHeight);

				int y2 = (int) (y - (1 * ylen) / 2 + bmpLen / 2);

				int x2 = (int) (x + xlen);

				int scaleInfo = (i == 0 ? -1 : 1) * getLevel();

				String str = (scaleInfo < 0 ? "> " : "< ")
						+ Integer.toString(Math.abs(scaleInfo));

				gl.glRasterPos2i(x2, y2);
				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);

				y2 += ylen;
			}
		}
	}

	public static final String LAYER_NAME = "AutoTimeLines";

	final DatasetDelegate _anchorsDataset;

	final RatiosLinesDataset _ratiosDataset;

	public AutoTimeLinesLayer(ScaleLayer scale) {
		super(LAYER_NAME, "J", scale, BITMAP_AUTO_ANCHOR_LINES_TOOL_ICON);

		_anchorsDataset = new DatasetDelegate(EMPTY_DATASET);
		_chart.addDataset(_anchorsDataset, new AnchorsLinesPainter());

		_ratiosDataset = new RatiosLinesDataset();
		_chart.addDataset(_ratiosDataset, new RatiosLinesPainter());
	}

	@Override
	public boolean isEnabled() {
		IndicatorLayer indicatorLayer = _chart.getIndicatorLayer();
		return indicatorLayer == null
				|| indicatorLayer.getAtlSettings().enabled;
	}

	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();
		IAutoTimeLinesModel model = _chart.getModel().getScaledIndicatorModel()
				.getAutoTimeLinesModel(getLevel());
		_anchorsDataset.setBase(new TimePriceDataset(model
				.getAutoTimeLines(dataLayer)));
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _anchorsDataset;
	}

	@Override
	public void clearDatasets() {
		_anchorsDataset.setBase(EMPTY_DATASET);
	}

	@Override
	public float getDefaultLayerWidth() {
		return 1f;
	}

	RatioInfo[] getRatios() {
		return getSettings().ratios;
	}

	ATLSettings getSettings() {
		return _chart.getIndicatorLayer().getAtlSettings();
	}
}
