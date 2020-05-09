package com.mfg.widget.arc.strategy;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.exit_ue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.mfg.mdb.runtime.MDBList;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.common.DFSException;
import com.mfg.common.QueueTick;
import com.mfg.common.Tick;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.utils.U;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.math.geom.Line;

/**
 * this is the complete rewrite of the Channel Widget.
 * 
 * <p>
 * It is a composite FSM. Each scale has its own state, its own rules. And the
 * update is done in chain. The price follows a waterfall model, from the lowest
 * to the highest scale.
 * 
 * <p>
 * The level is 1-based. Internally it is 0-based. In all the public methods the
 * level is 1-based. Of course in the array we have a zero-based index.
 * 
 * @author Pasqualino
 * 
 */
public abstract class ChannelIndicator extends TickAdapter implements
		IIndicator {

	@Override
	public double[] getStatsForLevel(int aLevel) {
		return _s_inds[aLevel - 1].getStats();
	}

	/**
	 * This long is the gatekeeper for all the scales, it is like an atomic bit
	 * set, where each bit signals when one scale has finished computing.
	 * 
	 * <p>
	 * For example, if we have a channel indicator which has 6 scales, the
	 * counter will have at most the value 2^6, signalling that all the six
	 * scales have finished computing.
	 * 
	 * <p>
	 * Using a long we have at most 63 scales, which should be sufficient for
	 * all purposes.
	 */
	AtomicLong _scaleCounter = new AtomicLong(0);

	protected boolean _warmUp = true;

	/**
	 * returns true if the indicator is in warm up.
	 * 
	 * @return true if the indicator is in warm up.
	 */
	public boolean isInWarmUp() {
		return _warmUp;
	}

	public static final int START_SCALE_LEVEL = 2;

	/**
	 * static helper method to create a channel to give to the outside.
	 * 
	 * <p>
	 * The channel model is a polynomial
	 * 
	 * @param level
	 * @param aChannel
	 * 
	 * @return
	 */
	static Channel create_channel_helper(int level, IChannel aChannel) {

		double coefficients[] = aChannel.getChannelCoefficients();

		double topD = aChannel.getTopDistance();
		double bottomD = aChannel.getBottomDistance();
		Channel ch = new Channel(level + 1, aChannel.getX1(), aChannel.getX2(),
				topD, bottomD, coefficients);

		return ch;
	}

	/**
	 * If true this means that the indicator is without an indicator, because it
	 * must before have a pre warm up phase listening to the player
	 */
	protected boolean fNaked = false;
	private IIndicatorConsumer[] consumers = new IIndicatorConsumer[0];

	private HashSet<IIndicatorListener> fListeners = new HashSet<>();

	/**
	 * This is the array of the single scale widgets. We have this array which
	 * holds all the widgets for a single scale. The length of the array is the
	 * number of scales.
	 * 
	 * The widget at scale 1(0) it is a particular widget. It is simply an
	 * indicator which will compute the pivots at scale 0 from the prices (which
	 * can be monotonous)
	 * 
	 * The other widgets will not have a monotonous stream of pivots, there will
	 * be always a "zig/zag".
	 * 
	 */
	BaseScaleIndicator[] _s_inds = null;

	QueueTick lastTick = null;

	/**
	 * This is the bean. The bean is package protected because it should be
	 * accessible from the other classes, the single scales indicators.
	 * 
	 */
	IndicatorParamBean bean;

	private boolean started = false;

	private com.mfg.inputdb.prices.mdb.PriceMDB savedTicksMdb;

	protected MDBList<Record> ranMDBList;

	private PriceMDBSession fSession;

	private com.mfg.inputdb.prices.mdb.PriceMDB.Appender fPricesAppender = null;

	protected int fLayer;

	/**
	 * This long holds the value which serves to know if the single indicators
	 * (in different threads) have finished to compute the scales.
	 */
	private final long ALL_SCALES_BITS;

	/**
	 * For now not all the indicators should go multithread, because for some
	 * there is not a gain: this temporary flag signals if the indicator is
	 * multithread or not, it is package protected to be accessed also by the
	 * single scale indicators.
	 */
	final boolean _IS_MULTITHREAD;

	private RandomCursor _cursor;

	/**
	 * The tick size.
	 */
	int _tick;

	// public boolean GOOD = true;

	/**
	 * Builds an indicator. The indicator is built using an
	 * {@linkplain IndicatorParamBean} object which contains all the parameters
	 * for this indicator (Now it is not more a "bean", but the name is here for
	 * historical reasons). The session is the price session which is given by
	 * the outside.
	 * 
	 * <p>
	 * The session could be null, in this case the indicator will create a
	 * temporary MDB session in which the prices will be stored.
	 * 
	 * @param session
	 * @param layer
	 */
	public ChannelIndicator(IndicatorParamBean bean1, PriceMDBSession session,
			int layer) {

		ALL_SCALES_BITS = (long) (Math.pow(2,
				bean1.getIndicatorNumberOfScales())) - 1;
		this.bean = bean1;
		_IS_MULTITHREAD = _determineIfIndicatorMultithread();
		fLayer = layer;
		try {
			String cwp;
			if (Platform.isRunning()) {
				Location instanceLoc = Platform.getInstanceLocation();
				cwp = instanceLoc.getURL().getPath();
			} else {
				cwp = System.getProperty("user.home");
			}

			if (session != null) {
				fSession = session;
				savedTicksMdb = fSession.connectTo_PriceMDB(layer);
			} else {
				fSession = new PriceMDBSession("test", new File(cwp,
						"AAAAAA_MfgTemporaryIndicators"),
						SessionMode.READ_WRITE);
				File priceTicks = File.createTempFile("ind_", null,
						fSession.getRoot());
				debug_var(399010, "Created indicator file ",
						priceTicks.getAbsolutePath());
				savedTicksMdb = fSession.connectTo_PriceMDB(priceTicks
						.getName());
				// savedTicksMdb = fSession.connectTo_PriceMDB(priceTicks,
				// 5000);
				fPricesAppender = savedTicksMdb.appender();
			}

			// final boolean defer = true;
			_cursor = savedTicksMdb.randomCursor();
			fSession.defer(_cursor);
			ranMDBList = savedTicksMdb.list(_cursor);
		} catch (IOException e) {
			exit_ue(e);
		}
	}

	/**
	 * the multithreaded nature of the indicator is for now set only by the
	 * central line algorithm
	 * 
	 * @return true if the indicator should be multithread
	 */
	@SuppressWarnings("deprecation")
	private boolean _determineIfIndicatorMultithread() {
		switch (bean.getIndicator_centerLineAlgo()) {
		case LINEAR_REGRESSION:
		case MOVING_AVERAGE:
			return false;
		case LR_P2:
		case POLYLINES_2:
		case POLYLINES_3:
		case POLYLINES_4:
		case POLYNOMIAL_FIT:
			return true;
		}
		throw new IllegalStateException();
	}

	/**
	 * @param secondPass
	 *            true if this indicator is being built the second time (after a
	 *            pre-warm up)
	 */
	protected abstract void _buildScales(boolean secondPass);

	/**
	 * empties the temporary memory until the fake time indicated
	 * 
	 * @param pricesToForget
	 */
	private void _forget_until(int pricesToForget) {
		for (final BaseScaleIndicator gi : _s_inds) {
			gi.forgetUntil(pricesToForget);
		}

	}

	@SuppressWarnings("boxing")
	protected void _onStarting_impl(int tick, int scale, boolean isSecondPass) {
		debug_var(425216, "ON STARTING CALLED WITH TICK ", tick, " scale ",
				scale);

		if (started) {
			return;
		}

		_buildScales(isSecondPass);

		lastTick = new QueueTick(new Tick(-1, -1), -1, false);

		for (final BaseScaleIndicator ssw : _s_inds) {
			ssw.begin(tick);
		}

		started = true;
	}

	public void addIndicatorConsumer(IIndicatorConsumer consumer) {
		ArrayList<IIndicatorConsumer> list = new ArrayList<>(
				Arrays.asList(consumers));
		list.add(consumer);
		consumers = list.toArray(new IIndicatorConsumer[list.size()]);
	}

	public void addIndicatorListener(IIndicatorListener listener) {
		synchronized (fListeners) {
			fListeners.add(listener);
		}
	}

	/**
	 * 
	 * the indicator is started by the onStarting method of the ITickListener
	 * interface
	 */
	@Override
	public void begin(final int tick) {
		// void here
	}

	void createAndDispatchNewRealTimeChannel(int level, IChannel fCurChannel) {
		Channel ch = create_channel_helper(level, fCurChannel);
		newRealTimeChannel(ch);
	}

	void createAndDispatchNewStartingChannel(int level, IChannel fCurChannel) {
		Channel ch = create_channel_helper(level, fCurChannel);
		newCompletedChannel(ch);
	}

	public FrozenWidget freeze(CSVSymbolData symbol, IProgressMonitor monitor,
			long totalSteps) throws DFSException {
		FrozenWidget fcw = new FrozenWidget(getChscalelevels(),
				getStartScaleLevelWidget());
		fcw.freezeMe(this, symbol, monitor, totalSteps);
		return fcw;
	}

	@Override
	public boolean getChisgoingup(final int level) {
		return _s_inds[level - 1].getChIsGoingUp();
	}

	@Override
	public int getChscalelevels() {
		return this.bean.getIndicatorNumberOfScales();
	}

	@Override
	public double getChslope(final int level) {
		return _s_inds[level - 1].getCurrentChannel().getSlope();
	}

	@Override
	public double getCurrentBottomRegressionPrice(final int level) {
		return _s_inds[level - 1].getCurrentChannel().getBottomY2();
	}

	@Override
	public double getCurrentCenterRegressionPrice(final int level) {
		return _s_inds[level - 1].getCurrentChannel().getCenterY2();
	}

	@Override
	public int getCurrentPivotsCount(final int level) {
		return _s_inds[level - 1].getCurrentPivotsCount();
	}

	@Override
	public int getCurrentPrice() {
		return lastTick.getPrice();
	}

	public double getCurrentRawBottomRegressionPrice(int level) {
		return _s_inds[level - 1].getCurrentChannel().getRawBottomY2();
	}

	public double getCurrentRawCenterRegressionPrice(int level) {
		return _s_inds[level - 1].getCurrentChannel().getRawCenterY2();
	}

	public double getCurrentRawTopRegressionPrice(int level) {
		return _s_inds[level - 1].getCurrentChannel().getRawTopY2();
	}

	@Override
	public int getCurrentRCTouches(int level) {
		return _s_inds[level - 1].getCurrentChannel().getRcTouches();
	}

	@Override
	public int getCurrentSCTouches(int level) {
		return _s_inds[level - 1].getCurrentChannel().getScTouches();
	}

	@Override
	public java.awt.Point getCurrentTentativePivot(int level) {
		if (_s_inds == null || _s_inds[level - 1] == null) {
			return null;
		}
		return _s_inds[level - 1].getCurrentTentativePivot();
	}

	@Override
	public long getCurrentTime() {
		return lastTick.getFakeTime();
	}

	@Override
	public double getCurrentTopRegressionPrice(final int level) {
		return _s_inds[level - 1].getCurrentChannel().getTopY2();
	}

	@SuppressWarnings("boxing")
	@Override
	public int getFakeTimeFor(long physicalTime, boolean exactMatch) {

		if (physicalTime == 0) {
			U.debug_var(383921, "PHYSICAL TIME IS ZERO RETURN THE LAST: ",
					lastTick.getFakeTime());
			return lastTick.getFakeTime();
		}

		if (!exactMatch) {
			try {
				return (int) savedTicksMdb.indexOfPhysicalTime(_cursor,
						physicalTime);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		long ans = -1;
		try {

			ans = savedTicksMdb
					.indexOfPhysicalTime_exact(_cursor, physicalTime);
			if (ans < 0) {
				if (physicalTime == lastTick.getPhysicalTime()) {
					return lastTick.getFakeTime();
				}
				/*
				 * Ok, the last time is not here, but this may also be a
				 * temporary tick, which has a fake time greater than 1.
				 */
				if (physicalTime > lastTick.getPhysicalTime()) {
					return lastTick.getFakeTime() + 1;
				}

				// As a last resort I look for an inexact match.
				return getFakeTimeFor(physicalTime, false);

				// throw new IllegalStateException("NOT found " + physicalTime
				// + " which is " + new Date(physicalTime)
				// + " my last is " + lastTick);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (int) ans;
	}

	@Override
	public long getHHPrice(final int level) {
		return _s_inds[level - 1].getHHPrice();
	}

	@Override
	public int getHHTime(final int level) {
		return _s_inds[level - 1].getHHTime();
	}

	/**
     *
     */
	@Override
	public Pivot getLastPivot(final int steps, final int level) {
		if (_s_inds == null || _s_inds[level - 1] == null) {
			return null;
		}
		return _s_inds[level - 1].getLastPivot(steps);
	}

	public QueueTick getLastTick() {
		return lastTick;
	}

	@Override
	public long getLLPrice(final int level) {
		return _s_inds[level - 1].getLLPrice();
	}

	@Override
	public int getLLTime(final int level) {
		return _s_inds[level - 1].getLLTime();
	}

	@Override
	public IndicatorParamBean getParamBean() {
		return bean;
	}

	public long getPhysicalTimeAt(int realFakeTime) {
		if (realFakeTime == lastTick.getFakeTime()) {
			return lastTick.getPhysicalTime(); // the last time mayb it is not
												// yet appended
		}
		com.mfg.inputdb.prices.mdb.PriceMDB.Record rec = ranMDBList
				.get(realFakeTime);
		return rec.physicalTime;

	}

	@Override
	public int getStartScaleLevelWidget() {
		return START_SCALE_LEVEL;
	}

	@Override
	public boolean isLevelInformationPresent(final int level) {
		return level <= _s_inds.length;
	}

	@Override
	public boolean isSwingDown(final int level) {
		return _s_inds[level - 1].isSwingDown();
	}

	@Override
	public boolean isThereANewPivot(final int level) {
		return _s_inds[level - 1].isThereANewPivot();
	}

	@Override
	public final boolean isThereANewTentativePivot(final int level) {
		return _s_inds[level - 1].isThereATentativePivot();
	}

	@Override
	public final int getConfirmThreshold(int aLevel) {
		return _s_inds[aLevel - 1].getConfirmThreshold();
	}

	@Override
	public boolean isThereANewRC(int level) {
		return _s_inds[level - 1].getCurrentChannel().isThereANewRc();
	}

	@Override
	public boolean isThereANewSC(int level) {
		return _s_inds[level - 1].getCurrentChannel().isThereANewSc();
	}

	protected void newCompletedChannel(Channel ch) {
		if (fNaked || ch.getLevel() < START_SCALE_LEVEL) {
			return;
		}
		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.newStartedChannel(ch);
			}
		}

	}

	protected void newPivot(Pivot pv) {
		if (fNaked || pv.getLevel() < START_SCALE_LEVEL) {
			return;
		}
		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.newPivot(pv);
			}
		}

	}

	protected void newPointRegressionLine(PointRegressionLine prl) {
		if (fNaked || prl.getLevel() < START_SCALE_LEVEL) {
			return;
		}
		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.newPointRegressionLine(prl);
			}
		}

	}

	protected void newRealTimeChannel(Channel ch) {
		if (fNaked || ch.getLevel() < START_SCALE_LEVEL) {
			return;
		}
		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.newRealTimeChannel(ch);
			}
		}
	}

	/**
	 * notifies the listeners of a new tentative pivot The level 1 is not
	 * notified.
	 * 
	 * @param level
	 *            the level is 1-based.
	 * @param aLine
	 *            The line which starts from the last confirmed pivot to the new
	 *            tentative pivot. The line is <b>not</b> copied, so the
	 *            listeners should avoid to modify it.
	 */
	protected void newTentativePivot(int level, Line aLine) {
		if (fNaked || level < START_SCALE_LEVEL) {
			return;
		}

		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.newTentativePivot(level, aLine);
			}
		}

	}

	protected final void newTickPrice_impl(final QueueTick qt) {
		_scaleCounter.set(0);

		if (_IS_MULTITHREAD) {
			for (BaseScaleIndicator bsi : _s_inds) {
				bsi.queueNewTick(qt);
			}

			while (true) {
				try {
					synchronized (_scaleCounter) {
						if (_scaleCounter.compareAndSet(ALL_SCALES_BITS, 0)) {
							// U.debug_var(291095,
							// "all scales are done for fake time ",
							// qt.getFakeTime(), " I go next");
							break;
						}
						_scaleCounter.wait();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			for (BaseScaleIndicator bsi : _s_inds) {
				bsi.drawOn(this);
			}
		} else { // no multithread, simple code
			for (BaseScaleIndicator bsi : _s_inds) {
				bsi.newTick(qt);
				bsi.drawOn(this);
			}
		}

	}

	public void newTouch(boolean isSupportTouch, int countTouches, int level) {
		if (fNaked || level < START_SCALE_LEVEL) {
			return;
		}

		synchronized (fListeners) {
			for (IIndicatorListener lis : fListeners) {
				lis.onNewTouch(isSupportTouch, countTouches, level);
			}
		}

	}

	@Override
	/**
	 * it is final because I want to centralize the tick processing methods.
	 */
	public final void onNewTick(QueueTick qt) {

		if (fPricesAppender != null) {
			fPricesAppender.physicalTime = qt.getPhysicalTime();
			fPricesAppender.priceRaw = qt.getReal() ? qt.getPrice() : -qt
					.getPrice();

			try {
				fPricesAppender.append();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		int pricesToSaveFrom = onNewTickNoAppend(qt);

		// If I get max_value then all the indicators are responsible for
		// deleting all data
		// and do not need the saved ticks
		if (pricesToSaveFrom == Integer.MAX_VALUE) {
			assert (false);
		} else {
			if (qt.getFakeTime() % 1000 == 0) {
				_forget_until(pricesToSaveFrom);
			}
		}
	}

	protected final int onNewTickNoAppend(QueueTick qt) {
		lastTick = qt;

		newTickPrice_impl(qt);

		if (!fNaked) {
			IndicatorConsumeArgs args = new IndicatorConsumeArgs(this, qt);
			for (IIndicatorConsumer c : consumers) {
				c.consume(args);
			}
		}

		int pricesToSaveFrom = Integer.MAX_VALUE;

		for (final BaseScaleIndicator gi : _s_inds) {
			pricesToSaveFrom = Math.min(pricesToSaveFrom,
					gi.getMinimunTimeToSave());
		}

		return pricesToSaveFrom;
	}

	// @Override
	// public void onNoNewTick() {
	// // nothing
	// }

	@Override
	public void onStarting(final int tick, final int scale) {
		_tick = tick;
		_onStarting_impl(tick, scale, false);
	}

	@Override
	public void onStopping() {
		for (BaseScaleIndicator bsi : _s_inds) {
			bsi.onStopping();
		}
		debug_var(485144, "on Stopping in the widget, ending. ",
				fNaked ? " BEFORE WARMING UP " : " warm up was done");
		for (IIndicatorConsumer consumer : consumers) {
			consumer.stopped(this);
		}
		removeAllConsumers();
	}

	public void removeAllConsumers() {
		consumers = new IIndicatorConsumer[0];
	}

	public void removeIndicatorConsumer(IIndicatorConsumer consumer) {
		ArrayList<IIndicatorConsumer> list = new ArrayList<>(
				Arrays.asList(consumers));
		list.remove(consumer);
		consumers = list.toArray(new IIndicatorConsumer[list.size()]);
	}

	public void removeListener(IIndicatorListener listener) {
		synchronized (fListeners) {
			fListeners.remove(listener);
		}
	}

	/**
	 * called to reset the indicator to the state when it was before being
	 * started. This is used by the {@link MultiscaleIndicator} when it needs to
	 * recompute the scales.
	 */
	protected void resetToStart() {
		started = false;
	}

	@Override
	public void setParamBean(final AbstractIndicatorParamBean parameters) {
		bean = (IndicatorParamBean) parameters;
	}

	public PriceMDB getMdbDatabase() {
		return this.savedTicksMdb;
	}

	// public double[] doS0S0PrimeStats(int level) {
	// return _s_inds[level - 1].doS0S0PrimeStats();
	// }
}
