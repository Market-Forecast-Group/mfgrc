package com.mfg.interfaces.symbols;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import com.mfg.utils.XmlIdentifier;

/**
 * This class is a Plain Old Data object. It contains all the parameters which
 * are used by the indicator. They are set by the GUI. The meaning of these
 * parameters is explained in the requirement document for the Adaptative
 * Regression Channel indicator. Here there is a brief explanation.
 * 
 */
public abstract class AbstractIndicatorParamBean extends XmlIdentifier
		implements Serializable {

	public enum CenterLineAlgo {
		MOVING_AVERAGE("Moving Average", 1), LINEAR_REGRESSION(
				"Linear Regression", 1), POLYLINES_2("Polyline 2�", 2), POLYLINES_3(
				"Polyline 3�", 3), POLYLINES_4("Polyline 4�", 4), LR_P2(
				"LR + P2�", 2), @Deprecated
		/**
		 * Just to keep backward compatibility. Instead you should use POLYLINES_2.
		 */
		POLYNOMIAL_FIT("Deprecated - Polyline Fit ", 2);

		private String _name;
		private int _degree;

		private CenterLineAlgo(String name, int degree) {
			_name = name;
			_degree = degree;
		}

		public int getDegree() {
			return _degree;
		}

		@Override
		public String toString() {
			return _name;
		}
	}

	public enum CenterLineValue {
		TRUE("True"), CENTERED("Centered"), BOTH("Both");

		private String name;

		private CenterLineValue(String name1) {
			this.name = name1;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum SPAType {
		MAX_SP("Max SP"), SAME_SWING_SLOPE("Same Swing Slope");

		private String name;

		private SPAType(String aName) {
			this.name = aName;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum StartPointLength {
		FIX_WINDOW("Fix Window"), PIVOT("Pivot"), FIX_WINDOW_PIVOT(
				"Fix Window + Pivot"), SPA("SPA"), FIX_WINDOW_SPA(
				"Fix Window + SPA");

		private String name;

		private StartPointLength(String aName) {
			this.name = aName;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum TopBottomMaxDist {
		FIXED_TICK("Fixed Tick"), PERCENTAGE("Percentage"), CONVEX_HULL(
				"Convex Hull"), HALF_CONVEX_HULL("Half Convex Hull"), CONVEX_HULL_FIXED_TICK(
				"Convex Hull + Fixed Tick"), BRUTE_FORCE("Brute Force");

		private String name;

		private TopBottomMaxDist(String aName) {
			this.name = aName;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final String PROP_BEST_CHANNEL_FITTING = "bestChannelFitting";

	private static final String PROP_BEST_CHANNEL_FITTING_ENABLED = "bestChannelFittingEnabled";

	private static final String PROP_NEGATIVE_ON_SC_TOUCH_TH_PERCENT = "negativeOnSCTouch_thPercent";

	private static final String PROP_NEGATIVE_ON_SC_TOUCH_TH_PERCENT_ENABLED = "negativeOnSCTouch_thPercentEnabled";

	private static final String PROP_A_NEGATIVE_ON_SC_TOUCH_SELF_PIVOT_BRAKOUT = "aNegativeOnSCTouch_selfPivotBrakout";

	private static final String PROP_NO_TB_MIN_FROM_SCALE_UP_ENABLED = "noTBMinFromScaleUpEnabled";

	private static final String PROP_NO_TB_MIN_FROM_SCALE_UP = "noTBMinFromScaleUp";

	private static final String PROP_NEGATIVE_ON_SC_TOUCH_S0_TIME_RATIO_ENABLED = "negativeOnSCTouch_S0TimeRatioEnabled";

	private static final String PROP_NEGATIVE_ON_SC_TOUCH_S0_TIME_RATIO = "negativeOnSCTouch_S0TimeRatio";

	private static final String PROP_NEGATIVE_ON_SC_TOUCH_S0_RATIO_ENABLED = "negativeOnSCTouch_S0RatioEnabled";

	private static final String PROP_A_MAX_PRICES_FOR_POLYLINES_ENABLED = "aMaxPricesForPolylinesEnabled";

	private static final String PROP_MAX_PRICES_FOR_POLYLINES = "maxPricesForPolylines";

	private static final String PROP_TH_PERCENT_FOR_TOP_BOTTOM_MIN_DISTANCE_ENABLED = "thPercentForTopBottomMinDistanceEnabled";

	public static final String PROP_TH_PERCENT_FOR_TOP_BOTTOM_MIN_DISTANCE = "thPercentForTopBottomMinDistance";

	private static final String PROP_NO_INDICATOR_IN_WARM_UP = "noIndicatorInWarmUp";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_MULTIPLIER = "fixWindow_deltaTopBottomMultiplier";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_MAXIMUM_WINDOW_LENGTH = "fixWindow_maximumWindowLength";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_AVOID_TOUCH = "fixWindow_avoidTouch";

	/**
	 * 
	 */
	private static final String PROP_SMOOTHING_CONVERGE_BOOST = "smoothing_convergeBoost";

	/**
	 * 
	 */
	private static final String PROP_SMOOTHING = "smoothing";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_WINDOW_MULTIPLIER = "fixWindow_windowMultiplier";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_START_WINDOW = "fixWindow_startWindow";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_TICKS = "fixWindow_deltaTopBottomTicks";

	/**
	 * 
	 */
	private static final String PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_PERC = "fixWindow_deltaTopBottomPerc";

	/**
	 * 
	 */
	private static final String PROP_1_PIVOT_AT_SAME_SCALE = "_1PivotAtSameScale";

	/**
	 * 
	 */
	private static final String PROP_INDICATOR_TOP_BOTTOM_MAX_DIST = "indicator_TopBottomMaxDist";

	/**
	 * 
	 */
	private static final String PROP_INDICATOR_SPA_TYPE = "indicator_SPAType";

	/**
	 * 
	 */
	private static final String PROP_INDICATOR_START_POINT_LENGTH = "indicator_StartPointLength";

	/**
	 * 
	 */
	private static final String PROP_INDICATOR_CENTER_LINE_VALUE = "indicator_centerLineValue";

	/**
	 * 
	 */
	private static final String PROP_INDICATOR_CENTER_LINE_ALGO = "indicator_centerLineAlgo";

	/**
	 * 
	 */
	private static final String PROP_REGRESSION_LINES_NARROWING_BOOSTING = "regressionLines_narrowingBoosting";

	/**
	 * 
	 */
	private static final String PROP_REGRESSION_LINES_BOOST_INDICATOR = "regressionLines_boostIndicator";

	/**
	 * 
	 */
	private static final String PROP_WEIGHTED_AVERAGE = "weightedAverage";

	/**
	 * 
	 */
	private static final String PROP_USE_SWING1_INDICATOR_LINES_AT_SAME_SCALE = "useSwing1IndicatorLinesAtSameScale";

	/**
	 * 
	 */
	private static final String PROP_USE_SWING0_INDICATOR_LINES_AT1_LOWER = "useSwing0IndicatorLinesAt1Lower";

	/**
	 * 
	 */
	private static final String PROP_NEGATIVE_ON_PRICE_MULTIPLIER_START_TICKS_NUMBERS = "negativeOnPriceMultiplier_startTicksNumbers";

	/**
     * 
     */
	private static final long serialVersionUID = 3951199238749657392L;

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	private int indicatorNumberOfScales = 9;
	private boolean printMessagesForTesting = false;
	private boolean negativeOnPivotBreakout = false;
	private boolean negativeOnFlatChannel = false;
	private int negativeOnSCTouch_startScale = 3;
	private boolean positiveOnSCRCTouch = false;
	private int positiveOnSCRCTouch_startScale = 4;
	private int negativeOnSCTouch_S0Ratio = 200;
	private boolean negativeOnSCTouch_S0RatioEnabled = false;
	private int negativeOnSCTouch_S0TimeRatio = 200;
	private boolean negativeOnSCTouch_S0TimeRatioEnabled = false;
	private boolean negativeOnSCTouch_selfPivotBrakout = false;
	private double negativeOnSCTouch_thPercent = 0.67;
	private boolean negativeOnSCTouch_thPercentEnabled = false;

	private boolean fix = true;
	private boolean dinamyc = false;
	private boolean _1PivotAtSameScale = false;
	private boolean _3PivotsAt1LS = true;
	private boolean _5PivotsAt2LS = true;
	private boolean _13PivotsAt3LS = false;
	private double _bestChannelFitting = 0.5;
	private boolean _bestChannelFittingEnabled = false;
	private boolean usePreviousRegressionValueForSC = true;
	private boolean useSwing1IndicatorLinesAtSameScale = true;
	private boolean useSwing0IndicatorLinesAt1Lower = true;
	private boolean weightedAverage = true;
	private double regressionLines_boostIndicator = 0.5;
	private boolean regressionLines_narrowingBoosting = true;
	private double probLinesPercentValue = 50;
	private double minMatchesPercent = 10;
	private boolean probLinesPercentValueEnabled = false;
	private boolean probLinesConditionalOnlyEnabled = true;
	private boolean negativeOnPriceMultiplier = true;
	private int negativeOnPriceMultiplier_priceMultiplier = 2;
	private int negativeOnPriceMultiplier_startTicksNumbers = 2;
	private boolean positiveOnWideChannels = true;
	private double positiveOnWideChannels_widthParameter = 0.3;
	private int positiveOnWideChannels_startScale = 4;

	private CenterLineAlgo indicator_centerLineAlgo = CenterLineAlgo.MOVING_AVERAGE;
	private CenterLineValue indicator_centerLineValue = CenterLineValue.TRUE;
	private StartPointLength indicator_StartPointLength = StartPointLength.FIX_WINDOW;
	private SPAType indicator_SPAType = SPAType.SAME_SWING_SLOPE;
	private TopBottomMaxDist indicator_TopBottomMaxDist = TopBottomMaxDist.FIXED_TICK;

	private int fixWindow_startWindow = 13;
	private int fixWindow_windowMultiplier = 4;
	private double fixWindow_deltaTopBottomPerc = 0.005;
	private int fixWindow_deltaTopBottomTicks = 2;
	private int fixWindow_deltaTopBottomMultiplier = 2;
	private boolean fixWindow_avoidTouch = true;
	private int fixWindow_maximumWindowLength = 5000000;

	private boolean smoothing = false;
	private double smoothing_convergeBoost = 1;

	private boolean noIndicatorInWarmUp = false;

	private double thPercentForTopBottomMinDistance = 1;
	private boolean thPercentForTopBottomMinDistanceEnabled = true;
	private int maxPricesForPolylines = 2;
	private boolean maxPricesForPolylinesEnabled = false;

	private boolean noTBMinFromScaleUpEnabled = true;
	private int noTBMinFromScaleUp = 8;

	public AbstractIndicatorParamBean() {
		super();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	@Override
	public AbstractIndicatorParamBean clone() {
		try {
			AbstractIndicatorParamBean clone = (AbstractIndicatorParamBean) super
					.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractIndicatorParamBean other = (AbstractIndicatorParamBean) obj;

		if (this.indicatorNumberOfScales != other.indicatorNumberOfScales) {
			return false;
		}

		return true;
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	public double getBestChannelFitting() {
		return _bestChannelFitting;
	}

	/**
	 * @return the fixWindow_deltaTopBottomMultiplier
	 */
	public int getFixWindow_deltaTopBottomMultiplier() {
		return fixWindow_deltaTopBottomMultiplier;
	}

	/**
	 * @return the fixWindow_deltaTopBottomPerc
	 */
	public double getFixWindow_deltaTopBottomPerc() {
		return fixWindow_deltaTopBottomPerc;
	}

	/**
	 * @return the fixWindow_deltaTopBottomTicks
	 */
	public int getFixWindow_deltaTopBottomTicks() {
		return fixWindow_deltaTopBottomTicks;
	}

	/**
	 * @return the fixWindow_maximumWindowLength
	 */
	public int getFixWindow_maximumWindowLength() {
		return fixWindow_maximumWindowLength;
	}

	/**
	 * @return the fixWindow_startWindow
	 */
	public int getFixWindow_startWindow() {
		return fixWindow_startWindow;
	}

	/**
	 * @return the fixWindow_windowMultiplier
	 */
	public int getFixWindow_windowMultiplier() {
		return fixWindow_windowMultiplier;
	}

	/**
	 * @return the indicator_centerLineAlgo
	 */
	public CenterLineAlgo getIndicator_centerLineAlgo() {
		return indicator_centerLineAlgo;
	}

	/**
	 * @return the indicator_centerLineValue
	 */
	public CenterLineValue getIndicator_centerLineValue() {
		return indicator_centerLineValue;
	}

	/**
	 * @return the indicator_SPAType
	 */
	public SPAType getIndicator_SPAType() {
		return indicator_SPAType;
	}

	/**
	 * @return the indicator_StartPointLength
	 */
	public StartPointLength getIndicator_StartPointLength() {
		return indicator_StartPointLength;
	}

	/**
	 * @return the indicator_TopBottomMaxDist
	 */
	public TopBottomMaxDist getIndicator_TopBottomMaxDist() {
		return indicator_TopBottomMaxDist;
	}

	public int getIndicatorNumberOfScales() {
		return indicatorNumberOfScales;
	}

	public int getMaxPricesForPolylines() {
		return maxPricesForPolylines;
	}

	public double getMinMatchesPercent() {
		return minMatchesPercent;
	}

	public int getNegativeOnPriceMultiplier_priceMultiplier() {
		return negativeOnPriceMultiplier_priceMultiplier;
	}

	/**
	 * @return the negativeOnPriceMultiplier_startTicksNumbers
	 */
	public int getNegativeOnPriceMultiplier_startTicksNumbers() {
		return negativeOnPriceMultiplier_startTicksNumbers;
	}

	public int getNegativeOnSCTouch_S0Ratio() {
		return negativeOnSCTouch_S0Ratio;
	}

	public int getNegativeOnSCTouch_S0TimeRatio() {
		return negativeOnSCTouch_S0TimeRatio;
	}

	/**
	 * @return the negativeOnSCTouch_startScale
	 */
	public int getNegativeOnSCTouch_startScale() {
		return negativeOnSCTouch_startScale;
	}

	public double getNegativeOnSCTouch_thPercent() {
		return negativeOnSCTouch_thPercent;
	}

	public int getNoTBMinFromScaleUp() {
		return noTBMinFromScaleUp;
	}

	public int getPositiveOnSCRCTouch_startScale() {
		return positiveOnSCRCTouch_startScale;
	}

	public boolean getPositiveOnWideChannels() {
		return positiveOnWideChannels;
	}

	public int getPositiveOnWideChannelsStartScale() {
		return positiveOnWideChannels_startScale;
	}

	public double getPositiveOnWideChannelsWidthParameter() {
		return positiveOnWideChannels_widthParameter;
	}

	/**
	 * @return the probLinesPercentValue
	 */
	public double getProbLinesPercentValue() {
		return probLinesPercentValue;
	}

	/**
	 * @return
	 */
	@Deprecated
	public double getRegressionLines_boostIndicator() {
		return regressionLines_boostIndicator;
	}

	/**
	 * @return the smoothing_convergeBoost
	 */
	public double getSmoothing_convergeBoost() {
		return smoothing_convergeBoost;
	}

	public double getThPercentForTopBottomMinDistance() {
		return thPercentForTopBottomMinDistance;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + this.indicatorNumberOfScales;
		return hash;
	}

	public boolean is_13PivotsAt3LS() {
		return _13PivotsAt3LS;
	}

	/**
	 * @return the _1PivotAtSameScale
	 */
	public boolean is_1PivotAtSameScale() {
		return _1PivotAtSameScale;
	}

	public boolean is_3PivotsAt1LS() {
		return _3PivotsAt1LS;
	}

	public boolean is_5PivotsAt2LS() {
		return _5PivotsAt2LS;
	}

	public boolean isBestChannelFittingEnabled() {
		return _bestChannelFittingEnabled;
	}

	/**
	 * @return
	 */
	@Deprecated
	public boolean isDinamyc() {
		return dinamyc;
	}

	/**
	 * @return
	 */
	@Deprecated
	public boolean isFix() {
		return fix;
	}

	/**
	 * @return the fixWindow_avoidTouch
	 */
	public boolean isFixWindow_avoidTouch() {
		return fixWindow_avoidTouch;
	}

	public boolean isMaxPricesForPolylinesEnabled() {
		return maxPricesForPolylinesEnabled;
	}

	public boolean isNegativeOnFlatChannel() {
		return negativeOnFlatChannel;
	}

	public boolean isNegativeOnPivotBreakOut() {
		return negativeOnPivotBreakout;
	}

	public boolean isNegativeOnPriceMultiplier() {
		return negativeOnPriceMultiplier;
	}

	public boolean isNegativeOnSCTouch_S0RatioEnabled() {
		return negativeOnSCTouch_S0RatioEnabled;
	}

	public boolean isNegativeOnSCTouch_S0TimeRatioEnabled() {
		return negativeOnSCTouch_S0TimeRatioEnabled;
	}

	public boolean isNegativeOnSCTouch_selfPivotBrakout() {
		return negativeOnSCTouch_selfPivotBrakout;
	}

	public boolean isNegativeOnSCTouch_thPercentEnabled() {
		return negativeOnSCTouch_thPercentEnabled;
	}

	public boolean isNoIndicatorInWarmUp() {
		return noIndicatorInWarmUp;
	}

	public boolean isNoTBMinFromScaleUpEnabled() {
		return noTBMinFromScaleUpEnabled;
	}

	public boolean isPositiveOnSCRCTouch() {
		return positiveOnSCRCTouch;
	}

	/**
	 * @return true if the testing messages are enabled.
	 */
	public boolean isPrintMessagesForTesting() {
		return printMessagesForTesting;
	}

	public boolean isProbLinesConditionalOnlyEnabled() {
		return probLinesConditionalOnlyEnabled;
	}

	/**
	 * @return the probLinesPercentValueEnabled
	 */
	public boolean isProbLinesPercentValueEnabled() {
		return probLinesPercentValueEnabled;
	}

	/**
	 * @return the regressionLines_narrowingBoosting
	 */
	public boolean isRegressionLines_narrowingBoosting() {
		return regressionLines_narrowingBoosting;
	}

	/**
	 * @return the smoothing
	 */
	public boolean isSmoothing() {
		return smoothing;
	}

	public boolean isThPercentForTopBottomMinDistanceEnabled() {
		return thPercentForTopBottomMinDistanceEnabled;
	}

	public boolean isUsePreviousRegressionValueForSC() {
		return usePreviousRegressionValueForSC;
	}

	/**
	 * @return the useSwing0IndicatorLinesAt1Lower
	 */
	public boolean isUseSwing0IndicatorLinesAt1Lower() {
		return useSwing0IndicatorLinesAt1Lower;
	}

	/**
	 * @return the useSwing1IndicatorLinesAtSameScale
	 */
	public boolean isUseSwing1IndicatorLinesAtSameScale() {
		return useSwing1IndicatorLinesAtSameScale;
	}

	/**
	 * @return the weightedAverage
	 */
	public boolean isWeightedAverage() {
		return weightedAverage;
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void set_13PivotsAt3LS(boolean _13PivotsAt3LS_New) {
		this._13PivotsAt3LS = _13PivotsAt3LS_New;
		firePropertyChange("_13PivotsAt3LS");
	}

	/**
	 * @param a_1PivotAtSameScale
	 *            the _1PivotAtSameScale to set
	 */
	public void set_1PivotAtSameScale(boolean a_1PivotAtSameScale) {
		this._1PivotAtSameScale = a_1PivotAtSameScale;
		firePropertyChange(PROP_1_PIVOT_AT_SAME_SCALE);
	}

	public void set_3PivotsAt1LS(boolean _3PivotsAt1LS_New) {
		this._3PivotsAt1LS = _3PivotsAt1LS_New;
		firePropertyChange("_3PivotsAt1LS");
	}

	public void set_5PivotsAt2LS(boolean _5PivotsAt2LS_New) {
		this._5PivotsAt2LS = _5PivotsAt2LS_New;
		firePropertyChange("_5PivotsAt2LS");
	}

	public void setBestChannelFitting(double bestChannelFitting) {
		_bestChannelFitting = bestChannelFitting;
		firePropertyChange(PROP_BEST_CHANNEL_FITTING);
	}

	public void setBestChannelFittingEnabled(boolean bestChannelFittingEnabled) {
		_bestChannelFittingEnabled = bestChannelFittingEnabled;
		firePropertyChange(PROP_BEST_CHANNEL_FITTING_ENABLED);
	}

	public void setDinamyc(boolean dinamyc_New) {
		this.dinamyc = dinamyc_New;
		this.fix = !dinamyc_New;

		if (dinamyc_New) {
			setPositiveOnSCRCTouch(true);
		}

		firePropertyChange("dinamyc");
		firePropertyChange("fix");
	}

	public void setFix(boolean fix_New) {
		this.fix = fix_New;
		this.dinamyc = !fix_New;
		firePropertyChange("fix");
		firePropertyChange("dinamyc");
		if (dinamyc) {
			setPositiveOnSCRCTouch(true);
		}
	}

	/**
	 * @param aFixWindow_avoidTouch
	 *            the fixWindow_avoidTouch to set
	 */
	public void setFixWindow_avoidTouch(boolean aFixWindow_avoidTouch) {
		this.fixWindow_avoidTouch = aFixWindow_avoidTouch;
		firePropertyChange(PROP_FIX_WINDOW_AVOID_TOUCH);
	}

	/**
	 * @param aFixWindow_deltaTopBottomMultiplier
	 *            the fixWindow_deltaTopBottomMultiplier to set
	 */
	public void setFixWindow_deltaTopBottomMultiplier(
			int aFixWindow_deltaTopBottomMultiplier) {
		this.fixWindow_deltaTopBottomMultiplier = aFixWindow_deltaTopBottomMultiplier;
		firePropertyChange(PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_MULTIPLIER);
	}

	/**
	 * @param aFixWindow_deltaTopBottomPerc
	 *            the fixWindow_deltaTopBottomPerc to set
	 */
	public void setFixWindow_deltaTopBottomPerc(
			double aFixWindow_deltaTopBottomPerc) {
		this.fixWindow_deltaTopBottomPerc = aFixWindow_deltaTopBottomPerc;
		firePropertyChange(PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_PERC);
	}

	/**
	 * @param aFixWindow_deltaTopBottomTicks
	 *            the fixWindow_deltaTopBottomTicks to set
	 */
	public void setFixWindow_deltaTopBottomTicks(
			int aFixWindow_deltaTopBottomTicks) {
		this.fixWindow_deltaTopBottomTicks = aFixWindow_deltaTopBottomTicks;
		firePropertyChange(PROP_FIX_WINDOW_DELTA_TOP_BOTTOM_TICKS);
	}

	/**
	 * @param aFixWindow_maximumWindowLength
	 *            the fixWindow_maximumWindowLength to set
	 */
	public void setFixWindow_maximumWindowLength(
			int aFixWindow_maximumWindowLength) {
		this.fixWindow_maximumWindowLength = aFixWindow_maximumWindowLength;
		firePropertyChange(PROP_FIX_WINDOW_MAXIMUM_WINDOW_LENGTH);
	}

	/**
	 * @param aFixWindow_startWindow
	 *            the fixWindow_startWindow to set
	 */
	public void setFixWindow_startWindow(int aFixWindow_startWindow) {
		this.fixWindow_startWindow = aFixWindow_startWindow;
		firePropertyChange(PROP_FIX_WINDOW_START_WINDOW);
	}

	/**
	 * @param aFixWindow_windowMultiplier
	 *            the fixWindow_windowMultiplier to set
	 */
	public void setFixWindow_windowMultiplier(int aFixWindow_windowMultiplier) {
		this.fixWindow_windowMultiplier = aFixWindow_windowMultiplier;
		firePropertyChange(PROP_FIX_WINDOW_WINDOW_MULTIPLIER);
	}

	/**
	 * @param aIndicator_centerLineAlgo
	 *            the indicator_centerLineAlgo to set
	 */
	public void setIndicator_centerLineAlgo(
			CenterLineAlgo aIndicator_centerLineAlgo) {
		this.indicator_centerLineAlgo = aIndicator_centerLineAlgo;
		firePropertyChange(PROP_INDICATOR_CENTER_LINE_ALGO);
	}

	/**
	 * @param aIndicator_centerLineValue
	 *            the indicator_centerLineValue to set
	 */
	public void setIndicator_centerLineValue(
			CenterLineValue aIndicator_centerLineValue) {
		this.indicator_centerLineValue = aIndicator_centerLineValue;
		firePropertyChange(PROP_INDICATOR_CENTER_LINE_VALUE);
	}

	/**
	 * @param aIindicator_SPAType
	 *            the indicator_SPAType to set
	 */
	public void setIndicator_SPAType(SPAType aIindicator_SPAType) {
		this.indicator_SPAType = aIindicator_SPAType;
		firePropertyChange(PROP_INDICATOR_SPA_TYPE);
	}

	/**
	 * @param aIndicator_StartPointLength
	 *            the indicator_StartPointLength to set
	 */
	public void setIndicator_StartPointLength(
			StartPointLength aIndicator_StartPointLength) {
		this.indicator_StartPointLength = aIndicator_StartPointLength;
		firePropertyChange(PROP_INDICATOR_START_POINT_LENGTH);
	}

	/**
	 * @param aIndicator_TopBottomMaxDist
	 *            the indicator_TopBottomMaxDist to set
	 */
	public void setIndicator_TopBottomMaxDist(
			TopBottomMaxDist aIndicator_TopBottomMaxDist) {
		this.indicator_TopBottomMaxDist = aIndicator_TopBottomMaxDist;
		firePropertyChange(PROP_INDICATOR_TOP_BOTTOM_MAX_DIST);

		if (indicator_TopBottomMaxDist == TopBottomMaxDist.CONVEX_HULL_FIXED_TICK) {
			if (getThPercentForTopBottomMinDistance() > 0.8) {
				setThPercentForTopBottomMinDistance(0.8);
			}
		}

	}

	public void setIndicatorNumberOfScales(int aIndicatorNumberOfScales) {
		this.indicatorNumberOfScales = aIndicatorNumberOfScales;
		firePropertyChange("indicatorNumberOfScales");
	}

	public void setMaxPricesForPolylines(int aMaxPricesForPolylines) {
		this.maxPricesForPolylines = aMaxPricesForPolylines;
		firePropertyChange(PROP_MAX_PRICES_FOR_POLYLINES);
	}

	public void setMaxPricesForPolylinesEnabled(
			boolean aMaxPricesForPolylinesEnabled) {
		this.maxPricesForPolylinesEnabled = aMaxPricesForPolylinesEnabled;
		firePropertyChange(PROP_A_MAX_PRICES_FOR_POLYLINES_ENABLED);
	}

	public void setMinMatchesPercent(double aMinMatchesPercent) {
		this.minMatchesPercent = aMinMatchesPercent;
	}

	public void setNegativeOnFlatChannel(boolean negativeOnFlatChannel_New) {
		this.negativeOnFlatChannel = negativeOnFlatChannel_New;
		firePropertyChange("negativeOnFlatChannel");
	}

	public void setNegativeOnPivotBreakOut(boolean negativeOnPivotBreakOut_New) {
		this.negativeOnPivotBreakout = negativeOnPivotBreakOut_New;
		firePropertyChange("negativeOnPivotBreakOut");
	}

	public void setNegativeOnPriceMultiplier(
			boolean negativeOnPriceMultiplier_New) {
		this.negativeOnPriceMultiplier = negativeOnPriceMultiplier_New;
		firePropertyChange("negativeOnPriceMultiplier");
	}

	public void setNegativeOnPriceMultiplier_priceMultiplier(
			int negativeOnPriceMultiplier_priceMultiplier_New) {
		this.negativeOnPriceMultiplier_priceMultiplier = negativeOnPriceMultiplier_priceMultiplier_New;
		firePropertyChange("negativeOnPriceMultiplier_priceMultiplier");
	}

	/**
	 * @param aNegativeOnPriceMultiplier_startTicksNumbers
	 *            the negativeOnPriceMultiplier_startTicksNumbers to set
	 */
	public void setNegativeOnPriceMultiplier_startTicksNumbers(
			int aNegativeOnPriceMultiplier_startTicksNumbers) {
		this.negativeOnPriceMultiplier_startTicksNumbers = aNegativeOnPriceMultiplier_startTicksNumbers;
		firePropertyChange(PROP_NEGATIVE_ON_PRICE_MULTIPLIER_START_TICKS_NUMBERS);
	}

	public void setNegativeOnSCTouch_S0Ratio(int negativeOnSCTouch_S0Ratio_New) {
		this.negativeOnSCTouch_S0Ratio = negativeOnSCTouch_S0Ratio_New;
		firePropertyChange("negativeOnSCTouch_S0Ratio");
	}

	public void setNegativeOnSCTouch_S0RatioEnabled(
			boolean aNegativeOnSCTouch_S0RatioEnabled) {
		this.negativeOnSCTouch_S0RatioEnabled = aNegativeOnSCTouch_S0RatioEnabled;
		firePropertyChange(PROP_NEGATIVE_ON_SC_TOUCH_S0_RATIO_ENABLED);
	}

	public void setNegativeOnSCTouch_S0TimeRatio(
			int aNegativeOnSCTouch_S0TimeRatio) {
		this.negativeOnSCTouch_S0TimeRatio = aNegativeOnSCTouch_S0TimeRatio;
		firePropertyChange(PROP_NEGATIVE_ON_SC_TOUCH_S0_TIME_RATIO);
	}

	public void setNegativeOnSCTouch_S0TimeRatioEnabled(
			boolean aNegativeOnSCTouch_S0TimeRatioEnabled) {
		this.negativeOnSCTouch_S0TimeRatioEnabled = aNegativeOnSCTouch_S0TimeRatioEnabled;
		firePropertyChange(PROP_NEGATIVE_ON_SC_TOUCH_S0_TIME_RATIO_ENABLED);
	}

	public void setNegativeOnSCTouch_selfPivotBrakout(
			boolean aNegativeOnSCTouch_selfPivotBrakout) {
		this.negativeOnSCTouch_selfPivotBrakout = aNegativeOnSCTouch_selfPivotBrakout;
		firePropertyChange(PROP_A_NEGATIVE_ON_SC_TOUCH_SELF_PIVOT_BRAKOUT);
	}

	/**
	 * @param aNegativeOnSCTouch_startScale
	 *            the negativeOnSCTouch_startScale to set
	 */
	public void setNegativeOnSCTouch_startScale(
			int aNegativeOnSCTouch_startScale) {
		this.negativeOnSCTouch_startScale = aNegativeOnSCTouch_startScale;
		firePropertyChange("negativeOnSCTouch_startScale");
	}

	public void setNegativeOnSCTouch_thPercent(
			double aNegativeOnSCTouch_thPercent) {
		this.negativeOnSCTouch_thPercent = aNegativeOnSCTouch_thPercent;
		firePropertyChange(PROP_NEGATIVE_ON_SC_TOUCH_TH_PERCENT);
	}

	public void setNegativeOnSCTouch_thPercentEnabled(
			boolean aNegativeOnSCTouch_thPercentEnabled) {
		this.negativeOnSCTouch_thPercentEnabled = aNegativeOnSCTouch_thPercentEnabled;
		firePropertyChange(PROP_NEGATIVE_ON_SC_TOUCH_TH_PERCENT_ENABLED);
	}

	public void setNoIndicatorInWarmUp(boolean aNoIndicatorInWarmUp) {
		this.noIndicatorInWarmUp = aNoIndicatorInWarmUp;
		firePropertyChange(PROP_NO_INDICATOR_IN_WARM_UP);
	}

	public void setNoTBMinFromScaleUp(int aNoTBMinFromScaleUp) {
		this.noTBMinFromScaleUp = aNoTBMinFromScaleUp;
		firePropertyChange(PROP_NO_TB_MIN_FROM_SCALE_UP);
	}

	public void setNoTBMinFromScaleUpEnabled(boolean aNoTBMinFromScaleUpEnabled) {
		this.noTBMinFromScaleUpEnabled = aNoTBMinFromScaleUpEnabled;
		firePropertyChange(PROP_NO_TB_MIN_FROM_SCALE_UP_ENABLED);
	}

	public void setPositiveOnSCRCTouch(boolean positiveOnSCRCTouch_New) {
		this.positiveOnSCRCTouch = positiveOnSCRCTouch_New;
		firePropertyChange("positiveOnSCRCTouch");
	}

	public void setPositiveOnSCRCTouch_startScale(
			int positiveOnSCRCTouch_startScale_New) {
		this.positiveOnSCRCTouch_startScale = positiveOnSCRCTouch_startScale_New;
		firePropertyChange("positiveOnSCRCTouch_startScale");
	}

	public void setPositiveOnWideChannels(boolean positiveOnWideChannels_New) {
		this.positiveOnWideChannels = positiveOnWideChannels_New;
		firePropertyChange("positiveOnWideChannels");
	}

	public void setPositiveOnWideChannelsStartScale(
			int positiveOnWideChannels_startScale_New) {
		this.positiveOnWideChannels_startScale = positiveOnWideChannels_startScale_New;
		firePropertyChange("positiveOnWideChannels_startScale");
	}

	public void setPositiveOnWideChannelsWidthParameter(
			double positiveOnWideChannelsWidthParameter_New) {
		this.positiveOnWideChannels_widthParameter = positiveOnWideChannelsWidthParameter_New;
		firePropertyChange("positiveOnWideChannels_widthParameter");
	}

	/**
	 * @param aPrintMessagesForTesting
	 *            true if you want to print messages for testing using the
	 *            <code>debug_var</code> function
	 */
	public void setPrintMessagesForTesting(boolean aPrintMessagesForTesting) {
		this.printMessagesForTesting = aPrintMessagesForTesting;
		firePropertyChange("printMessagesForTesting");
	}

	public void setProbLinesConditionalOnlyEnabled(
			boolean probLinesConditionalOnlyEnabled1) {
		this.probLinesConditionalOnlyEnabled = probLinesConditionalOnlyEnabled1;
		firePropertyChange("probLinesConditionalOnlyEnabled");
	}

	/**
	 * @param aProbLinesPercentValue
	 *            the probLinesPercentValue to set
	 */
	public void setProbLinesPercentValue(double aProbLinesPercentValue) {
		this.probLinesPercentValue = aProbLinesPercentValue;
	}

	/**
	 * @param probLinesPercentValueEnabled1
	 *            the probLinesPercentValueEnabled to set
	 */
	public void setProbLinesPercentValueEnabled(
			boolean probLinesPercentValueEnabled1) {
		this.probLinesPercentValueEnabled = probLinesPercentValueEnabled1;
		firePropertyChange("probLinesPercentValueEnabled");
	}

	/**
	 * @param aRegressionLines_boostIndicator
	 *            the regressionLines_boostIndicator to set
	 */
	public void setRegressionLines_boostIndicator(
			double aRegressionLines_boostIndicator) {
		this.regressionLines_boostIndicator = aRegressionLines_boostIndicator;
		firePropertyChange(PROP_REGRESSION_LINES_BOOST_INDICATOR);
	}

	/**
	 * @param aRegressionLines_narrowingBoosting
	 *            the regressionLines_narrowingBoosting to set
	 */
	public void setRegressionLines_narrowingBoosting(
			boolean aRegressionLines_narrowingBoosting) {
		this.regressionLines_narrowingBoosting = aRegressionLines_narrowingBoosting;
		firePropertyChange(PROP_REGRESSION_LINES_NARROWING_BOOSTING);
	}

	/**
	 * @param aSmoothing
	 *            the smoothing to set
	 */
	public void setSmoothing(boolean aSmoothing) {
		this.smoothing = aSmoothing;
		firePropertyChange(PROP_SMOOTHING);
	}

	/**
	 * @param aSmoothing_convergeBoost
	 *            the smoothing_convergeBoost to set
	 */
	public void setSmoothing_convergeBoost(double aSmoothing_convergeBoost) {
		this.smoothing_convergeBoost = aSmoothing_convergeBoost;
		firePropertyChange(PROP_SMOOTHING_CONVERGE_BOOST);
	}

	public void setThPercentForTopBottomMinDistance(
			double aThPercentForTopBottomMinDistance) {
		this.thPercentForTopBottomMinDistance = aThPercentForTopBottomMinDistance;
		firePropertyChange(PROP_TH_PERCENT_FOR_TOP_BOTTOM_MIN_DISTANCE);
	}

	public void setThPercentForTopBottomMinDistanceEnabled(
			boolean aThPercentForTopBottomMinDistanceEnabled) {
		this.thPercentForTopBottomMinDistanceEnabled = aThPercentForTopBottomMinDistanceEnabled;
		firePropertyChange(PROP_TH_PERCENT_FOR_TOP_BOTTOM_MIN_DISTANCE_ENABLED);
	}

	public void setUsePreviousRegressionValueForSC(
			boolean usePreviousRegressionValueForSC_New) {
		this.usePreviousRegressionValueForSC = usePreviousRegressionValueForSC_New;
		firePropertyChange("usePreviousRegressionValueForSC");
	}

	/**
	 * @param aUseSwing0IndicatorLinesAt1Lower
	 *            the useSwing0IndicatorLinesAt1Lower to set
	 */
	public void setUseSwing0IndicatorLinesAt1Lower(
			boolean aUseSwing0IndicatorLinesAt1Lower) {
		this.useSwing0IndicatorLinesAt1Lower = aUseSwing0IndicatorLinesAt1Lower;
		firePropertyChange(PROP_USE_SWING0_INDICATOR_LINES_AT1_LOWER);
	}

	/**
	 * @param aUseSwing1IndicatorLinesAtSameScale
	 *            the useSwing1IndicatorLinesAtSameScale to set
	 */
	public void setUseSwing1IndicatorLinesAtSameScale(
			boolean aUseSwing1IndicatorLinesAtSameScale) {
		this.useSwing1IndicatorLinesAtSameScale = aUseSwing1IndicatorLinesAtSameScale;
		firePropertyChange(PROP_USE_SWING1_INDICATOR_LINES_AT_SAME_SCALE);
	}

	/**
	 * @param aWeightedAverage
	 *            the weightedAverage to set
	 */
	public void setWeightedAverage(boolean aWeightedAverage) {
		this.weightedAverage = aWeightedAverage;
		firePropertyChange(PROP_WEIGHTED_AVERAGE);
	}

	@Override
	public String toString() {
		return serializeToString();
	}

}
