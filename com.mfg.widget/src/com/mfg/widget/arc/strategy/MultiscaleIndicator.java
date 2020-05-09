package com.mfg.widget.arc.strategy;

import static com.mfg.utils.Utils.debug_var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;
import com.mfg.common.QueueTickRandomGenerator;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.utils.PropertiesEx;
import com.mfg.utils.U;
import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * defines a multi scale indicator.
 * 
 */
public class MultiscaleIndicator extends ChannelIndicator {

	private int _currentQuota = 0;

	@SuppressWarnings("boxing")
	@Override
	public void realTimeQueueAlertUp(int currentSize) {
		U.debug_var(283374, "channel indicator, queue is UP to ", currentSize);

		for (BaseScaleIndicator baseInd : _s_inds) {
			baseInd.hurryUp(++_currentQuota);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void realTimeQueueAlertDown(int currentSize) {
		U.debug_var(283374, "channel indicator, queue is DOWN to ", currentSize);

		for (BaseScaleIndicator baseInd : _s_inds) {
			baseInd.calmDown(--_currentQuota);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void preWarmUpFinishedEvent(IProgressMonitor aMonitor) {
		if (fNaked) {
			debug_var(991731, "layer ", fLayer,
					" I have to build statistics, I will replay ",
					ranMDBList.size(), " ticks until ", this.lastTick);
			int limitFakeTime = this.lastTick.getFakeTime();
			fNaked = false; // now I am not naked, this is necessary, because I
							// need to
			// make the notifications happen.

			resetToStart();

			_onStarting_impl(fTick, fScale, true);

			// try{

			// I simply use a cursor on the list
			Iterator<Record> cur = ranMDBList.iterator();

			QueueTick qt = new QueueTick();
			QueueTick oldQt = null;
			while (cur.hasNext()) {
				Record curRec = cur.next();
				curRec.copyTo(qt);
				onNewTickNoAppend(qt);
				oldQt = qt;
				/*
				 * This could happen if I am replaying the indicator using a
				 * complete database
				 */
				if (qt.getFakeTime() == limitFakeTime) {
					break;
				}
				qt = new QueueTick(); // I have to create again the object

				if (aMonitor.isCanceled()) {
					return;
				}
			}
			debug_var(991731, "second pass finished!, last qt sent 	", oldQt);
		}

		/*
		 * Naked or not, In the end I surely tell the indicators that the warm
		 * up is finished and they are unconditionally enabled.
		 */
		for (BaseScaleIndicator indicator : _s_inds) {
			indicator.warmUpFinished();
		}

		_warmUp = false;
	}

	// private static final String IS_LIGHT_UPDATE_DURING_WARM_UP =
	// "IsLightUpdateDuringWarmUp";
	public static final String PRE_COMPUTED_WINDOWS_KEY = "PreComputedWindows";
	private int fTick = -1;
	private int fScale = -1;

	@Override
	public void onStarting(int tick, int scale) {
		super.onStarting(tick, scale);
		fTick = tick;
		fScale = scale;
	}

	@Override
	protected void _buildScales(boolean isSecondPass) {
		int i;
		BaseScaleIndicator[] backup = null;

		double[] computedWindows = null;
		if (isSecondPass) {
			backup = Arrays.copyOf(_s_inds, _s_inds.length);
			computedWindows = new double[_s_inds.length];
			computedWindows[0] = 1; // this is the starting window, used in the
									// process.

			/*
			 * Before overwriting the indicators I should stop their thread.
			 */
			for (BaseScaleIndicator baseInd : _s_inds) {
				baseInd.onStopping();
			}
		}
		for (i = 0; i < _s_inds.length; ++i) {
			if (i == 0) {
				_s_inds[i] = new IndicatorSingleZeroScale(this, null,
						_IS_MULTITHREAD);
			} else {
				if (isSecondPass) {
					_s_inds[i] = new IndicatorSingle(this, i, _s_inds[i - 1],
							backup, computedWindows, _IS_MULTITHREAD);
				} else {
					_s_inds[i] = new IndicatorSingle(this, i, _s_inds[i - 1],
							_IS_MULTITHREAD);
				}
			}
		}

	}

	public MultiscaleIndicator(IndicatorParamBean ipb, PriceMDBSession session,
			int layer) {
		super(ipb, session, layer);
		_s_inds = new BaseScaleIndicator[bean.getIndicatorNumberOfScales()];

		PropertiesEx props = bean.getProperties();

		if (props.getBooleanDef("Naked", true) == true) {
			debug_var(391931,
					"I am naked..., so I will use the warm up phase to estimate the statistics");
			fNaked = true;
		}

		if (props.getBooleanDef(PRE_COMPUTED_WINDOWS_KEY, false) == true) {
			if (fNaked) {
				throw new IllegalArgumentException(
						"Cannot have precomputed windows and a naked indicator");
			}
		}
	}

	public static void main(String args[]) {
		IndicatorParamBean ipb = new IndicatorParamBean();
		ipb.setPrintMessagesForTesting(false);
		PropertiesEx props = new PropertiesEx();

		props.setProperty("IndicatorAvoidTouch", "false");
		props.setProperty("Naked", "true");

		ipb.setProperties(props);
		ipb.setNegativeOnPriceMultiplier(true);
		ipb.setNegativeOnPriceMultiplier_startTicksNumbers(2);
		ipb.setNegativeOnPriceMultiplier_priceMultiplier(2);
		ipb.setNegativeOnPivotBreakOut(true);
		ipb.setNegativeOnFlatChannel(true);
		ipb.setPositiveOnSCRCTouch(true);
		ipb.setRegressionLines_narrowingBoosting(true);
		ipb.setRegressionLines_boostIndicator(0.10);
		ipb.setIndicatorNumberOfScales(6);
		QueueTickRandomGenerator qtr = new QueueTickRandomGenerator(33, 10);

		MultiscaleIndicator mi = new MultiscaleIndicator(ipb, null, 0);
		mi.onStarting(10, 0);

		long start = System.currentTimeMillis();
		long startPeriod = start;

		final ArrayList<QueueTick> warmUpTicks = new ArrayList<>();
		final int WARM_UP_TICKS = 1452;

		boolean inWarmUp = true;

		while (true) {

			QueueTick qt = qtr.getNext();
			if ((qt.getFakeTime() % 100000) == 0) {
				// debug_var(914195, "qt ", qt, " size ",
				// mi.getSavedTickSize());
				long now = System.currentTimeMillis();
				double speed = (double) qt.getFakeTime()
						/ (double) (now - start);
				double speedPeriod = (100000.0) / (now - startPeriod);
				System.out.println(" i " + qt.getFakeTime() + "  speed "
						+ speed + " p/msec " + speedPeriod);
				// + " p/msec now size " + mi.getSavedTickSize());
				// System.out.println("i = " + i + " price = " + price);
				startPeriod = now;
			}

			mi.onNewTick(qt);

			if (inWarmUp)
				warmUpTicks.add(qt);
			else
				continue;

			if (warmUpTicks.size() == WARM_UP_TICKS) {

				// oK, I have finished the warm up
				mi.onWarmUpFinished();

				warmUpTicks.clear();
				inWarmUp = false;
			}

		}

	}
}
