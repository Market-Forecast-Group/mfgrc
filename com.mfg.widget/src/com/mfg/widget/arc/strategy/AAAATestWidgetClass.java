package com.mfg.widget.arc.strategy;

import java.io.IOException;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.CenterLineAlgo;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.StartPointLength;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.TopBottomMaxDist;
import com.mfg.utils.PropertiesEx;
import com.mfg.utils.RandomTickSource;
import com.mfg.utils.jobs.ProgressMonitorAdapter;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.proc.src.s_acc;

public class AAAATestWidgetClass {

	/**
	 * This main tests the indicator.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {

		/*
		 * I test the indicator using various parameters, in this way I am
		 * assured that any change is as smooth as possibile.
		 */
		IndicatorParamBean ipb;

		ipb = _getSmoothingPolyWithWarmUpFilterParams();
		MdbPolyTrendHelper.TESTING_SO_SERIALIZE = true;
		_testingIndicator("smoothing poly with warm up filter params test :  ",
				ipb, 6284, 1403107108561L, 2763);

		MdbPolyTrendHelper.TESTING_SO_SERIALIZE = false;

		ipb = _getQuadricParamsWithConvexHull();
		_testingIndicator("Polyline test 2nd ", ipb, 2812, 1043204414331L, 1500);

		ipb = _getQuadricParamsWithConvexHullBf();
		_testingIndicator("Polyline test 2nd Brute force. ", ipb, 2812,
				1043204414331L, 1500);

		ipb = _getNegativeOnScTouchParamsWithSelfBreakAndThreshold();
		_testingIndicator("Negative on sc touch *with self break* and th ",
				ipb, 18385, 12147649287416L, 3000);

		ipb = _getNegativeOnScTouchParamsWithSelfBreak();
		_testingIndicator("Negative on sc touch *with self break* ", ipb,
				18385, 12147663522847L, 3000);

		ipb = _getSmoothingPolyParams();
		_testingIndicator("smoothing poly params test :  ", ipb, 6284,
				1402706013632L, 2763);

		ipb = _getSmoothingParams();
		_testingIndicator("smoothing test :  ", ipb, 25362, 8946014039794L,
				2763);

		ipb = _getBestFittingRuleParams();
		_testingIndicator("best fitting rule test :  ", ipb, 25362,
				17912138241401L, 2763);

		ipb = _getAverageParameters();
		_testingIndicator("Average Indicator test ", ipb, 200255,
				115103522137015L, 5000);

		ipb = _getConvexHullParams();
		_testingIndicator("Convex Hull test ", ipb, 10839, 4585257097232L, 5000);

		ipb = _getCubicParams();
		_testingIndicator("Polyline test ", ipb, 2132, 502839185492L, 1500);

		ipb = _getNegativeOnScTouchParams();
		_testingIndicator("Negative on sc touch ", ipb, 18385, 12147673559116L,
				3000);

		ipb = _getMixedWindowPivotParams();
		_testingIndicator("Mixed window - pivot test:  ", ipb, 18385,
				12152475197195L, 3000);

		ipb = _getMixedConvexHullParams();
		_testingIndicator("Convex hull - fixed ticks test :  ", ipb, 19285,
				12826828341155L, 3000);

	}

	private static IndicatorParamBean _getQuadricParamsWithConvexHullBf() {
		IndicatorParamBean ipb = _getQuadricParamsWithConvexHull();
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.BRUTE_FORCE);
		return ipb;
	}

	private static IndicatorParamBean _getNegativeOnScTouchParamsWithSelfBreakAndThreshold() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setNegativeOnSCTouch_S0RatioEnabled(true);
		ipb.setNegativeOnSCTouch_S0TimeRatioEnabled(true);
		ipb.setNegativeOnSCTouch_startScale(3);
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setNegativeOnSCTouch_selfPivotBrakout(true);
		ipb.setNegativeOnSCTouch_thPercentEnabled(true);
		ipb.setNegativeOnSCTouch_thPercent(0.64);
		return ipb;
	}

	private static IndicatorParamBean _getNegativeOnScTouchParamsWithSelfBreak() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setNegativeOnSCTouch_S0RatioEnabled(true);
		ipb.setNegativeOnSCTouch_S0TimeRatioEnabled(true);
		ipb.setNegativeOnSCTouch_startScale(3);
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setNegativeOnSCTouch_selfPivotBrakout(true);
		return ipb;
	}

	private static IndicatorParamBean _getSmoothingPolyWithWarmUpFilterParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setSmoothing(true);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.POLYLINES_2);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setIndicator_StartPointLength(StartPointLength.PIVOT);
		ipb.setRegressionLines_narrowingBoosting(false);
		ipb.setSmoothing_convergeBoost(0.5);
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setPositiveOnWideChannels(true);
		ipb.setPositiveOnWideChannelsStartScale(4);
		ipb.setPositiveOnWideChannelsWidthParameter(0.5);
		ipb.setMaxPricesForPolylinesEnabled(true);
		ipb.setMaxPricesForPolylines(2);
		return ipb;
	}

	private static IndicatorParamBean _getSmoothingPolyParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setSmoothing(true);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.POLYLINES_2);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setIndicator_StartPointLength(StartPointLength.PIVOT);
		ipb.setRegressionLines_narrowingBoosting(false);
		ipb.setSmoothing_convergeBoost(0.5);
		ipb.setNoIndicatorInWarmUp(true);
		ipb.setPositiveOnWideChannels(true);
		ipb.setPositiveOnWideChannelsStartScale(4);
		ipb.setPositiveOnWideChannelsWidthParameter(0.5);
		return ipb;
	}

	private static IndicatorParamBean _getSmoothingParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setSmoothing(true);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.LINEAR_REGRESSION);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setIndicator_StartPointLength(StartPointLength.SPA);
		ipb.setRegressionLines_narrowingBoosting(false);
		ipb.setSmoothing_convergeBoost(0.5);
		return ipb;
	}

	private static IndicatorParamBean _getBestFittingRuleParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.MOVING_AVERAGE);
		ipb.setIndicator_StartPointLength(StartPointLength.SPA);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.FIXED_TICK);
		ipb.setBestChannelFittingEnabled(true);
		ipb.setBestChannelFitting(0.534);
		return ipb;
	}

	private static IndicatorParamBean _getMixedConvexHullParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.MOVING_AVERAGE);
		ipb.setIndicator_StartPointLength(StartPointLength.FIX_WINDOW_PIVOT);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL_FIXED_TICK);
		return ipb;
	}

	private static IndicatorParamBean _getMixedWindowPivotParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setNoIndicatorInWarmUp(false);
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.MOVING_AVERAGE);
		ipb.setIndicator_StartPointLength(StartPointLength.FIX_WINDOW_PIVOT);
		return ipb;
	}

	private static IndicatorParamBean _getNegativeOnScTouchParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		// ipb.setNegativeOnSCTouch(true);
		ipb.setNegativeOnSCTouch_S0RatioEnabled(true);
		ipb.setNegativeOnSCTouch_S0TimeRatioEnabled(true);
		ipb.setNegativeOnSCTouch_startScale(3);
		ipb.setNoIndicatorInWarmUp(false);
		return ipb;
	}

	private static IndicatorParamBean _getQuadricParamsWithConvexHull() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.POLYLINES_2);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setThPercentForTopBottomMinDistanceEnabled(false);
		ipb.setNoIndicatorInWarmUp(false);
		return ipb;
	}

	private static IndicatorParamBean _getCubicParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setIndicator_centerLineAlgo(CenterLineAlgo.POLYLINES_3);
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setThPercentForTopBottomMinDistanceEnabled(false);
		ipb.setNoIndicatorInWarmUp(false);
		return ipb;
	}

	private static IndicatorParamBean _getConvexHullParams() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setIndicator_TopBottomMaxDist(TopBottomMaxDist.CONVEX_HULL);
		ipb.setThPercentForTopBottomMinDistanceEnabled(false);
		return ipb;
	}

	private static void _testingIndicator(String msgTest,
			IndicatorParamBean ipb, int maxIterations,
			long expectedAccumulator, int warmUpTrigger) {

		long then = System.currentTimeMillis();
		MultiscaleIndicator cw = new MultiscaleIndicator(ipb, null, 0);
		cw.onStarting(25, 0);

		// 56 is the GOOD SEED
		RandomTickSource rts = new RandomTickSource(56, 25);

		long accumulator = 0;

		// cw.onStarting(25, 2);
		// Random rnd = new Random(18021973);
		rts.setNoGaps();

		long start = System.currentTimeMillis();
		long startPeriod = start;
		// 213255
		// for (int i = 0; i < Integer.MAX_VALUE ; ++i) {
		for (int i = 0; i < maxIterations; ++i) {

			// QueueTick qt = new QueueTick(new Tick(System.currentTimeMillis(),
			// price), i, true);
			int price = rts.getNextPrice();
			QueueTick qt = new QueueTick(System.currentTimeMillis(), i, price,
					true, 1);

			cw.onNewTick(qt);

			if (qt.getFakeTime() == warmUpTrigger) {
				cw.preWarmUpFinishedEvent(new ProgressMonitorAdapter());
				cw.onWarmUpFinished();
			}

			if (i >= warmUpTrigger) {
				accumulator += s_acc.wid_acc(cw);
			}

			if (i % 5000 == 0) {
				long now = System.currentTimeMillis();
				double speed = (double) i / (double) (now - start);
				double speedPeriod = (5000.0) / (now - startPeriod);
				System.out.println(" i " + i + " acc " + accumulator
						+ " speed " + speed + " p/msec " + speedPeriod
						+ " p/msec now");
				// System.out.println("i = " + i + " price = " + price);
				startPeriod = now;
			}
		}

		cw.onStopping();

		if (accumulator != expectedAccumulator) {
			System.out.println(msgTest + " : ** fail ** exp: "
					+ expectedAccumulator + " got " + accumulator);
			throw new RuntimeException();
		}

		long now = System.currentTimeMillis();
		long delta = now - then;
		System.out.println(msgTest + " : OK, done in " + delta + " msec.");

	}

	private static IndicatorParamBean _getAverageParameters() {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setPrintMessagesForTesting(false);
		ipb.setNegativeOnPivotBreakOut(true);
		ipb.setPositiveOnSCRCTouch(true);
		ipb.setNegativeOnFlatChannel(true);
		// ipb.setNegativeOnSCTouch(true);
		ipb.setNegativeOnPriceMultiplier(false);
		ipb.setDinamyc(true);

		PropertiesEx props = new PropertiesEx();
		props.setProperty("Naked", "false");
		props.setProperty("PreComputedWindows", "true");
		props.setProperty("PRE_COMPUTED_WINDOW_LAYER_0_LEV_1", "12");
		props.setProperty("PRE_COMPUTED_WINDOW_LAYER_0_LEV_2", "63");
		props.setProperty("PRE_COMPUTED_WINDOW_LAYER_0_LEV_3", "192");
		props.setProperty("PRE_COMPUTED_WINDOW_LAYER_0_LEV_4", "625");
		props.setProperty("PRE_COMPUTED_WINDOW_LAYER_0_LEV_5", "2031");
		// props.setProperty("PRE_COMPUTED_WINDOW_LEV_6", "12");
		ipb.setProperties(props);
		return ipb;
	}

	// @SuppressWarnings("boxing")
	// public static void main2(@SuppressWarnings("unused") String args[])
	// throws IOException {
	// IndicatorParamBean ipb = new IndicatorParamBean();
	// ipb.setPrintMessagesForTesting(false);
	// ChannelIndicator cw = new MultiscaleIndicator(ipb, null, 0);
	// cw.begin(25);
	//
	// // cw.onStarting(25, 2);
	// Random rnd = new Random(18021973);
	//
	// int price = 1000;
	// for (int i = 0; i < 500; ++i) {
	// if (rnd.nextDouble() < 0.5) {
	// price += 25;
	// } else {
	// price -= 25;
	// }
	//
	// QueueTick qt = new QueueTick(new Tick(System.currentTimeMillis(),
	// price), i, true);
	// cw.onNewTick(qt);
	// // System.out.println("i = " + i + " price = " + price);
	//
	// if (i % 500 == 0) {
	// System.out.println("i = " + i + " price = " + price);
	// }
	// }
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ObjectOutputStream oos = new ObjectOutputStream(baos);
	//
	// oos.writeObject(cw);
	//
	// byte[] outB = baos.toByteArray();
	//
	// byte[] outB1 = cw.getState();
	//
	// debug_var(234341, "Ending serialization length is ", outB.length,
	// " get state length ", outB1.length);
	// }
}
