/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.chart.layers;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.layers.IndicatorLayer.AdditionalSettings;
import com.mfg.chart.layers.IndicatorLayer.IndicatorScalesSettings;
import com.mfg.chart.model.IHSProbsModel;
import com.mfg.chart.model.IProbabilityCollection;
import com.mfg.chart.model.IProbabilityModel;
import com.mfg.chart.model.ITradingModel;

class ProbDataset implements IDataset {
	private final IProbabilityCollection data;

	public ProbDataset(IProbabilityCollection data1) {
		super();
		this.data = data1;
	}

	@Override
	public int getSeriesCount() {
		return 2;
	}

	@Override
	public int getItemCount(int series) {
		return data.getSize();
	}

	@Override
	public double getX(int series, int item) {
		return data.getTime(item);
	}

	@Override
	public double getY(int series, int item) {
		return series == 0 ? data.getPositivePrice(item) : data
				.getNegativePrice(item);
	}

	/**
	 * @return the data
	 */
	public IProbabilityCollection getData() {
		return data;
	}

}

class HSProbsDataset implements IDataset {
	private final IHSProbsModel model;

	public HSProbsDataset(IHSProbsModel model1) {
		this.model = model1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getSeriesCount()
	 */
	@Override
	public int getSeriesCount() {
		return model.getSquaresCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getItemCount(int)
	 */
	@Override
	public int getItemCount(int series) {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(int series, int item) {
		return item == 0 ? model.getTime0(series) : model.getTime1(series);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(int series, int item) {
		return item == 0 ? model.getPrice0(series) : model.getPrice1(series);
	}

	public int getScale(int series) {
		return model.getScale(series);
	}

}

public class ProbabilityLayer extends FinalScaleElementLayer {

	public static final String LAYER_NAME = "Probs";
	private IProbabilityModel prob1Model;
	private IProbabilityModel prob2THModel;
	private IProbabilityModel prob2CurrentModel;
	private DatasetDelegate dataset1;
	private DatasetDelegate dataset2TH;
	private DatasetDelegate dataset2Current;
	private IProbabilityModel[] models;
	private DatasetDelegate[] datasets;
	private final boolean isPercentMode;
	private final boolean isConditionalProbabilityOnly;
	private final DatasetDelegate hsProbDataset;
	private final ITradingModel executionModel;

	class Probs1Painter implements ISeriesPainter {

		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			// if positive trade, then positive targets are continue, else
			// dotted.
			DatasetDelegate dataset = (DatasetDelegate) ds;
			if (dataset.getBase() != EMPTY_DATASET) {
				IProbabilityCollection data = ((ProbDataset) dataset.getBase())
						.getData();

				IndicatorLayer indicatorLayer = _chart.getIndicatorLayer();
				AdditionalSettings s = indicatorLayer.getAdditionalSettings();
				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(s.probsProfitLineWidth);

				IndicatorScalesSettings scalesSettings = indicatorLayer
						.getScalesSettings();
				gl.glColor4fv(
						scalesSettings.scalesColors[getScale().getLevel()], 0);

				int profitFactor = s.probsProfitLineType;
				int lossFactor = s.probsLossLineType;

				boolean lastTradeDir = false;
				boolean lastUpSide = false;
				// positive
				for (int i = 0; i < data.getSize(); i++) {
					long time = data.getTime(i);
					double price = data.getPositivePrice(i);
					double negPrice = data.getNegativePrice(i);

					boolean posTradeDir = data.isPositiveTradeDireaction(i);
					boolean tradeDirChanged = posTradeDir != lastTradeDir;

					boolean upSide = price > negPrice;
					boolean upSideChanged = upSide != lastUpSide;

					if (i == 0) {
						setStipple(gl, posTradeDir ? profitFactor : lossFactor);
						gl.glBegin(GL.GL_LINE_STRIP);
						lastTradeDir = posTradeDir;
						lastUpSide = upSide;
					} else {
						if (tradeDirChanged || upSideChanged) {

							if (upSideChanged) {
								gl.glVertex2d(time, negPrice);
							} else {
								gl.glVertex2d(time, price);
							}

							gl.glEnd();
							setStipple(gl, posTradeDir ? profitFactor
									: lossFactor);
							gl.glBegin(GL.GL_LINE_STRIP);

							lastTradeDir = posTradeDir;
							lastUpSide = upSide;
						}
					}
					gl.glVertex2d(time, price);
				}
				gl.glEnd();

				// negative
				for (int i = 0; i < data.getSize(); i++) {
					long time = data.getTime(i);
					double price = data.getNegativePrice(i);
					double posPrice = data.getPositivePrice(i);

					boolean posTradeDir = data.isPositiveTradeDireaction(i);
					boolean tradeDirChanged = posTradeDir != lastTradeDir;

					boolean upSide = price > posPrice;
					boolean upSideChanged = upSide != lastUpSide;

					if (i == 0) {
						setStipple(gl, posTradeDir ? lossFactor : profitFactor);
						gl.glBegin(GL.GL_LINE_STRIP);
						lastTradeDir = posTradeDir;
						lastUpSide = upSide;
					} else {
						if (tradeDirChanged || upSideChanged) {

							if (upSideChanged) {
								gl.glVertex2d(time, posPrice);
							} else {
								gl.glVertex2d(time, price);
							}

							gl.glEnd();
							setStipple(gl, posTradeDir ? lossFactor
									: profitFactor);
							gl.glBegin(GL.GL_LINE_STRIP);

							lastTradeDir = posTradeDir;
							lastUpSide = upSide;
						}
					}
					gl.glVertex2d(time, price);
				}
				gl.glEnd();

				gl.glPopAttrib();
				gl.glEnable(GL2.GL_LINE_STIPPLE);
			}
		}

		private void setStipple(GL2 gl, int factor) {
			if (factor == STIPPLE_FACTOR_NULL) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			} else {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(factor, STIPPLE_PATTERN);
			}
		}

	}

	class Probs2Painter implements ISeriesPainter {

		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			// if positive probabilities, then uses continue lines, else
			// dotted.
			DatasetDelegate dataset = (DatasetDelegate) ds;
			if (dataset.getBase() != EMPTY_DATASET) {
				IProbabilityCollection data = ((ProbDataset) dataset.getBase())
						.getData();
				IndicatorLayer indicatorLayer = _chart.getIndicatorLayer();
				AdditionalSettings s = indicatorLayer.getAdditionalSettings();

				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(s.probsProfitLineWidth);

				gl.glColor4fv(
						indicatorLayer.getScalesSettings().scalesColors[getScale()
								.getLevel()], 0);

				int positiveFactor = s.probsProfitLineType;
				int negativeFactor = s.probsLossLineType;

				boolean lastUpSide = false;
				// positive
				for (int i = 0; i < data.getSize(); i++) {
					long time = data.getTime(i);
					double price = data.getPositivePrice(i);
					double negPrice = data.getNegativePrice(i);

					boolean posTradeDir = data.isPositiveTradeDireaction(i);
					boolean upSide = price > negPrice;
					boolean upSideChanged = upSide != lastUpSide;

					if (i == 0) {
						setStipple(gl, posTradeDir ? positiveFactor
								: negativeFactor);
						gl.glBegin(GL.GL_LINE_STRIP);
						lastUpSide = upSide;
					} else {
						if (upSideChanged) {

							if (upSideChanged) {
								gl.glVertex2d(time, negPrice);
							} else {
								gl.glVertex2d(time, price);
							}

							gl.glEnd();
							setStipple(gl, posTradeDir ? positiveFactor
									: negativeFactor);
							gl.glBegin(GL.GL_LINE_STRIP);

							lastUpSide = upSide;
						}
					}
					gl.glVertex2d(time, price);
				}
				gl.glEnd();

				// negative
				for (int i = 0; i < data.getSize(); i++) {
					long time = data.getTime(i);
					double price = data.getNegativePrice(i);
					double posPrice = data.getPositivePrice(i);

					boolean posTradeDir = data.isPositiveTradeDireaction(i);
					boolean upSide = price > posPrice;
					boolean upSideChanged = upSide != lastUpSide;

					if (i == 0) {
						setStipple(gl, posTradeDir ? negativeFactor
								: positiveFactor);
						gl.glBegin(GL.GL_LINE_STRIP);
						lastUpSide = upSide;
					} else {
						if (upSideChanged) {

							if (upSideChanged) {
								gl.glVertex2d(time, posPrice);
							} else {
								gl.glVertex2d(time, price);
							}

							gl.glEnd();
							setStipple(gl, posTradeDir ? negativeFactor
									: positiveFactor);
							gl.glBegin(GL.GL_LINE_STRIP);

							lastUpSide = upSide;
						}
					}
					gl.glVertex2d(time, price);
				}
				gl.glEnd();

				gl.glPopAttrib();
			}
		}

		private void setStipple(GL2 gl, int factor) {
			if (factor == STIPPLE_FACTOR_NULL) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			} else {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(factor, STIPPLE_PATTERN);
			}
		}

	}

	class HSProbsPainter implements ISeriesPainter {

		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			int seriesCount = ds.getSeriesCount();
			if (seriesCount > 0) {
				HSProbsDataset ds2 = (HSProbsDataset) ((DatasetDelegate) ds)
						.getBase();
				for (int series = 0; series < seriesCount; series++) {
					int boxScale = ds2.getScale(series);
					float[] color = getChart().getIndicatorLayer()
							.getScalesSettings().scalesColors[boxScale];
					gl.glColor3fv(color, 0);
					gl.glBegin(GL.GL_LINE_LOOP);
					double x0 = ds2.getX(series, 0);
					double y0 = ds2.getY(series, 0);
					double x1 = ds2.getX(series, 1);
					double y1 = ds2.getY(series, 1);
					gl.glVertex2d(x0, y0);
					gl.glVertex2d(x1, y0);
					gl.glVertex2d(x1, y1);
					gl.glVertex2d(x0, y1);
					gl.glEnd();
				}
			}
		}

	}

	public ProbabilityLayer(ScaleLayer scale) {
		super(LAYER_NAME, "R", scale, BITMAP_PROBS);
		executionModel = getChart().getModel().getTradingModel();

		isPercentMode = executionModel.isPercentProbabilityMode();
		isConditionalProbabilityOnly = executionModel
				.isConditionalProbabilitiesOnly();

		if (isPercentMode) {
			prob2CurrentModel = executionModel
					.getProbabilityPercentCurrentModel(getLevel());
			dataset2Current = new DatasetDelegate(EMPTY_DATASET);

			if (isConditionalProbabilityOnly) {
				models = new IProbabilityModel[] { prob2CurrentModel };
				datasets = new DatasetDelegate[] { dataset2Current };
			} else {
				prob2THModel = executionModel
						.getProbabilityPercentTHModel(getLevel());
				models = new IProbabilityModel[] { prob2THModel,
						prob2CurrentModel };
				dataset2TH = new DatasetDelegate(EMPTY_DATASET);
				datasets = new DatasetDelegate[] { dataset2TH, dataset2Current };
			}

		} else {
			prob1Model = executionModel.getProbabilityModel(getLevel());
			models = new IProbabilityModel[] { prob1Model };

			dataset1 = new DatasetDelegate(EMPTY_DATASET);
			datasets = new DatasetDelegate[] { dataset1 };
		}

		for (IDataset ds : datasets) {
			_chart.addDataset(ds, new Probs2Painter());
		}

		hsProbDataset = new DatasetDelegate(EMPTY_DATASET);
		_chart.addDataset(hsProbDataset, new HSProbsPainter());
	}

	@Override
	public boolean isEnabled() {
		return getSettings().probsEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().probsEnabled = enabled;
	}

	AdditionalSettings getSettings() {
		return _chart.getIndicatorLayer().getAdditionalSettings();
	}

	@Override
	public void updateDataset() {
		PlotRange range = _chart.getXRange();

		for (int i = 0; i < models.length; i++) {
			IProbabilityModel model = models[i];
			IProbabilityCollection data = model.getProbabilities(
					(long) range.lower, (long) range.upper);
			DatasetDelegate dataset = datasets[i];
			dataset.setBase(new ProbDataset(data));
		}

		IHSProbsModel hsProbModel = executionModel.getHSProbModel(getLevel());
		HSProbsDataset hsDs = new HSProbsDataset(hsProbModel);
		hsProbDataset.setBase(hsDs);

	}

	@Override
	public IDataset getAutorangeDataset() {
		return isPercentMode ? dataset2Current : dataset1;
	}

	@Override
	public void clearDatasets() {
		for (DatasetDelegate dataset : datasets) {
			dataset.setBase(EMPTY_DATASET);
		}
		hsProbDataset.setBase(EMPTY_DATASET);
	}
}
