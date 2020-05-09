/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfg.widget.priv;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.utils.MathUtils;

/**
 * 
 * @author Enrique Matos
 */
public enum TRIGGER_TYPE {

	PRICE {
		@Override
		public double getCurrentValue(IIndicator aWidget,
				StartPoint aStartPoint, int aWidgetScale) {
			return aWidget.getCurrentPrice();
		}

		@Override
		public double getPivotValue(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			if (aStep == 1)
				return (aWidget.isSwingDown(aWidgetScale) ? aWidget
						.getLLPrice(aWidgetScale) : aWidget
						.getHHPrice(aWidgetScale));
			return aWidget.getLastPivot(aStep, aWidgetScale).fPivotPrice;
		}

		@Override
		public double getTHValue(IIndicator aWidget, int aWidgetScale, int aStep) {
			return aWidget.getLastPivot(aStep, aWidgetScale).fConfirmPrice;
		}

		@Override
		public double getTHSegment(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			Pivot p = aWidget.getLastPivot(aStep, aWidgetScale);
			return Math.abs(p.fConfirmPrice - p.fPivotPrice);
		}

		@Override
		public double getSwingValue(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			if (aStep == 0) {
				return Math.abs(aWidget.getLastPivot(0, aWidgetScale)
						.getPivotPrice()
						- (aWidget.isSwingDown(aWidgetScale) ? aWidget
								.getLLPrice(aWidgetScale) : aWidget
								.getHHPrice(aWidgetScale)));
			}
			return getSwingValue(aWidget.getLastPivot(aStep + 1, aWidgetScale));
		}

		@Override
		public double getDistanceToP0(IIndicator aWidget, int aWidgetScale) {
			return Math.abs(aWidget.getLastPivot(0, aWidgetScale)
					.getPivotPrice() - aWidget.getCurrentPrice());

		}

		@Override
		public double getSwingValue(Pivot pivot) {
			return pivot.fLinearSwing;
		}

		@Override
		public double[] giveMeArrayToEval(double[] swRatios,
				double[] timeRatios, double[] vectorRatios) {
			return swRatios;
		}

		@Override
		public double getSwingValue(Pivot aFirst, Pivot aSecond) {
			return Math.abs(aFirst.fPivotPrice - aSecond.fPivotPrice);
		}

	},
	TIME {

		@Override
		public double getCurrentValue(IIndicator aWidget,
				StartPoint aStartPoint, int aWidgetScale) {
			return aWidget.getCurrentTime();
		}

		@Override
		public double getPivotValue(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			if (aStep == 1)
				return (aWidget.isSwingDown(aWidgetScale) ? aWidget
						.getLLTime(aWidgetScale) : aWidget
						.getHHTime(aWidgetScale));
			return aWidget.getLastPivot(aStep, aWidgetScale).fPivotTime;
		}

		@Override
		public double getTHValue(IIndicator aWidget, int aWidgetScale, int aStep) {
			return getTHSegment(aWidget, aWidgetScale, aStep);
		}

		@Override
		public double getTHSegment(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			Pivot p = aWidget.getLastPivot(aStep, aWidgetScale);
			return p.fConfirmTime - p.fPivotTime;
		}

		@Override
		public double getSwingValue(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			if (aStep == 0) {
				return Math.abs(aWidget.getLastPivot(0, aWidgetScale)
						.getPivotTime()
						- (aWidget.isSwingDown(aWidgetScale) ? aWidget
								.getLLTime(aWidgetScale) : aWidget
								.getHHTime(aWidgetScale)));
			}
			return getSwingValue(aWidget.getLastPivot(aStep + 1, aWidgetScale));
		}

		@Override
		public double getDistanceToP0(IIndicator aWidget, int aWidgetScale) {
			return Math.abs(aWidget.getLastPivot(0, aWidgetScale)
					.getPivotTime() - aWidget.getCurrentTime());

		}

		@Override
		public double getSwingValue(Pivot pivot) {
			return pivot.fTimeInterval;
		}

		@Override
		public double[] giveMeArrayToEval(double[] swRatios,
				double[] timeRatios, double[] vectorRatios) {
			return timeRatios;
		}

		@Override
		public double getSwingValue(Pivot aFirst, Pivot aSecond) {
			return Math.abs(aFirst.fPivotTime - aSecond.fPivotTime);
		}

	},
	VECTORIAL {

		@Override
		public double getCurrentValue(IIndicator aWidget,
				StartPoint aStartPoint, int aWidgetScale) {
			double dp = PRICE.getCurrentValue(aWidget, aStartPoint,
					aWidgetScale)
					- aStartPoint.getStartPoint(aWidget, aWidgetScale, PRICE);
			double dt = TIME
					.getCurrentValue(aWidget, aStartPoint, aWidgetScale)
					- aStartPoint.getStartPoint(aWidget, aWidgetScale, TIME);
			return MathUtils.modulus(dp, dt);
		}

		private double getFactor(IIndicator aWidget, int aScale, int aStep) {
			Pivot p = null;
			try {
				p = aWidget.getLastPivot(aStep, aScale);
			} catch (Exception ex) {
				p = aWidget.getLastPivot(aStep + 1, aScale);
			}
			double price = TRIGGER_TYPE.PRICE.getSwingValue(p);
			double time = TRIGGER_TYPE.TIME.getSwingValue(p);
			return (price != 0) ? (price / time) : 1;
		}

		@Override
		public double getTHSegment(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			double f = getFactor(aWidget, aWidgetScale, aStep);
			return MathUtils.modulus(
					PRICE.getTHSegment(aWidget, aWidgetScale, aStep),
					f * TIME.getTHSegment(aWidget, aWidgetScale, aStep));
		}

		@Override
		public double getSwingValue(IIndicator aWidget, int aWidgetScale,
				int aStep) {
			double f = getFactor(aWidget, aWidgetScale, aStep);
			return MathUtils.modulus(
					PRICE.getSwingValue(aWidget, aWidgetScale, aStep),
					f * TIME.getSwingValue(aWidget, aWidgetScale, aStep));
		}

		@Override
		public double getDistanceToP0(IIndicator aWidget, int aWidgetScale) {
			double f = getFactor(aWidget, aWidgetScale, 0);
			return MathUtils.modulus(
					PRICE.getDistanceToP0(aWidget, aWidgetScale),
					f * TIME.getDistanceToP0(aWidget, aWidgetScale));

		}

		@Override
		public double getSwingValue(Pivot pivot) {
			return MathUtils.modulus(PRICE.getSwingValue(pivot),
					TIME.getSwingValue(pivot));
		}

		@Override
		public double[] giveMeArrayToEval(double[] swRatios,
				double[] timeRatios, double[] vectorRatios) {
			return vectorRatios;
		}

		@Override
		public double getSwingValue(Pivot aFirst, Pivot aSecond) {
			return MathUtils.modulus(PRICE.getSwingValue(aFirst, aSecond),
					TIME.getSwingValue(aFirst, aSecond));
		}

	},
	NONE {

		@Override
		public double[] giveMeArrayToEval(double[] swRatios,
				double[] timeRatios, double[] vectorRatios) {
			return new double[] {};
		}
	};

	/**
	 * @param swRatios
	 * @param timeRatios
	 * @param vectorRatios
	 */
	@SuppressWarnings("static-method")
	public double[] giveMeArrayToEval(double[] swRatios, double[] timeRatios,
			double[] vectorRatios) {
		return null;
	}

	public static int giveMeBucketDims(int patternSize) {
		return (patternSize * (patternSize - 1)) / 2;
	}

	/**
	 * gets the corresponding value to the swing we specify.
	 * 
	 * @param aWidget
	 * @param aWidgetScale
	 * @param aSwing
	 * @return
	 */
	@SuppressWarnings("static-method")
	public double getSwingValue(IIndicator aWidget, int aWidgetScale, int aSwing) {
		return 0;
	}

	/**
	 * @param pivot
	 */
	@SuppressWarnings("static-method")
	public double getSwingValue(Pivot pivot) {
		return 0;
	}

	/**
	 * @param aWidget
	 * @param aWidgetScale
	 * @param aStep
	 */
	@SuppressWarnings("static-method")
	public double getPivotValue(IIndicator aWidget, int aWidgetScale, int aStep) {
		return 0;
	}

	/**
	 * @param aWidget
	 * @param aWidgetScale
	 * @param aStep
	 */
	@SuppressWarnings("static-method")
	public double getTHValue(IIndicator aWidget, int aWidgetScale, int aStep) {
		return 0;
	}

	/**
	 * @param aWidget
	 * @param aWidgetScale
	 * @param aStep
	 */
	@SuppressWarnings("static-method")
	public double getTHSegment(IIndicator aWidget, int aWidgetScale, int aStep) {
		return 0;
	}

	/**
	 * @param aWidget
	 * @param aWidgetScale
	 */
	@SuppressWarnings("static-method")
	public double getDistanceToP0(IIndicator aWidget, int aWidgetScale) {
		return 0;

	}

	/**
	 * @param aWidget
	 * @param aStartPoint
	 * @param aWidgetScale
	 */
	@SuppressWarnings("static-method")
	public double getCurrentValue(IIndicator aWidget, StartPoint aStartPoint,
			int aWidgetScale) {
		return 0;
	}

	/**
	 * @param aFirst
	 * @param aSecond
	 */
	@SuppressWarnings("static-method")
	public double getSwingValue(Pivot aFirst, Pivot aSecond) {
		return 0;
	}
}
