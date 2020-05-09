package com.mfg.dm.filters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.IBarCache;
import com.mfg.common.QueueTick;
import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.FillGapsMachine;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.filters.FinalNotFinalClassificator.EAnswer;
import com.mfg.utils.U;

/**
 * A cache expander is used, like its name imply, to expand a cache until a
 * certain date in time.
 * 
 * <p>
 * The cache expanders works collaboratively to expand the prices, more like the
 * scales in the widget, but, unlike the scales, they are usually upside down,
 * that is the more sparse cache expanders are below and they must ask to the
 * upper level the amount of prices to use to fill a gap.
 * 
 * <p>
 * The cache expander is also able to process prices in real time, or bars,
 * whichever they come. There are caches which takes bars and others that take
 * prices... but internally the bars are in any case expanded as usual.
 * 
 * 
 * @author Sergio
 * 
 */
public final class CacheExpander {

	/**
	 * I have the possibility of various gap filling methods.
	 * 
	 * @author Sergio
	 * 
	 */
	public enum EGapFillingMethod {
		PRICE_MULTIPLIER, UPPER_CACHE
	}

	/**
	 * A structure which holds a saved tick (used when the cache expander is
	 * expanding in database mode and with different layers).
	 * <p>
	 * This structure is a simple {@link QueueTick} with a flag that tells if
	 * the tick is final or not.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private static final class FinalNotFinalTick {

		final QueueTick _qt;
		final boolean _final;

		// final int _volume;

		FinalNotFinalTick(QueueTick aQt, boolean isFinal) {
			_qt = aQt;
			_final = isFinal;
			// _volume = aVolume;
		}

		@Override
		public String toString() {
			return _qt.toString();
		}

	}

	// /**
	// * A class used to store a real tick and a volume, this class is used when
	// * the socket sends a new quote and there is still the warm up.
	// *
	// * <p>
	// * In this case the expander will store the tick and the volume.
	// *
	// * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	// *
	// */
	// private static final class RealVolumeTick {
	// public final RealTick tick;
	// public final int volume;
	//
	// public RealVolumeTick(RealTick aTick, int aVolume) {
	// tick = aTick;
	// volume = aVolume;
	// }
	// }

	// private static final int MINIMUM_GAP_TO_FILTER = 5;

	/**
	 * A simple class which takes care of two particular cases:
	 * 
	 * <p>
	 * the zero prices, that is a bar with some equal prices
	 * 
	 * <p>
	 * too much near bars, that is bars which are more than 4 milliseconds near.
	 * 
	 * @author Sergio
	 * 
	 */
	private static class ZeroBarExpanderHelper {

		int _lastPrice = -1;
		long _lastTime = -1;
		private long _startRealTime = -1;

		public ZeroBarExpanderHelper() {
			// nothing here.
		}

		/**
		 * adjusts the tick array which comes from the
		 * {@linkplain Bar#expand(Tick[], long)} method.
		 * 
		 * <p>
		 * It adjusts it in the sense that it removes the prices which are equal
		 * 
		 * @param arr
		 */
		public void adjustTickArray(Tick[] arr, Tick[] arrFixed) {
			Arrays.fill(arrFixed, null);
			int fixIndex = 0;
			for (Tick tk : arr) {
				if (tk.getPrice() == _lastPrice) {
					continue;
				}

				if (tk.getPhysicalTime() <= _startRealTime) {
					continue;
				}

				if (tk.getPhysicalTime() <= _lastTime) {
					tk.setPhysicalTime(_lastTime + 1);
				}

				_lastTime = tk.getPhysicalTime();
				_lastPrice = tk.getPrice();
				arrFixed[fixIndex++] = tk;
			}
		}

		@SuppressWarnings("boxing")
		public void debugStat() {
			U.debug_var(399113, "zero expansion is @", _lastPrice);
		}

		/**
		 * Called when the expansion has ended.
		 * <p>
		 * In this case there is a barrier which is the last time given to the
		 * outside, and it is simply the start of the "real time" for this
		 * expander.
		 * 
		 * <p>
		 * In case of different expanders the lower expanders (the crypt) are
		 * not "real" because their prices have been expanded in this upper
		 * expander and they do not receive the message, they <b>must not</b>
		 * receive the message.
		 */
		public void endExpansion() {
			_startRealTime = _lastTime;
		}

		public int getLastPrice() {
			return _lastPrice;
		}

		@SuppressWarnings("boxing")
		public void makeCoherentTo(FinalNotFinalClassificator _fnfc) {
			if (!_fnfc.isCoherent(_lastPrice)) {
				RealTick rt = _fnfc.getLastTick();
				U.debug_var(191009,
						"////////////////////////// setting the zbeh TO ", rt,
						" I was at ", _lastPrice, " my time is ", new Date(
								_lastTime));
				_lastPrice = rt.getPrice();
				// I cannot go backward in time...
				_lastTime = Math.max(rt.getPhysicalTime(), _lastTime);

			}
		}

		/**
		 * Massages the tick's time.
		 * 
		 * <p>
		 * If the tick's time can't be massaged (because it is before the
		 * starting of real time) this should be discarded and it returns false.
		 * 
		 * <p>
		 * it also returns false if the price is equal to the last price.
		 * 
		 * @param aTick
		 *            the tick from the real time feed.
		 * 
		 * @return true if this tick can be passed along the pipeline false if
		 *         it should be discarded.
		 */
		public boolean massageRealTimeTick(RealTick aTick) {
			// U.debug_var(839193, "massage ", aTick, " for me last price is ",
			// _lastPrice);
			// if (aTick.getPhysicalTime() <= _startRealTime) {
			// return false;
			// }

			if (_lastPrice == aTick.getPrice()) {
				return false;
			}

			aTick.setPhysicalTime(Math.max(_lastTime + 1,
					aTick.getPhysicalTime()));

			_lastTime = aTick.getPhysicalTime();
			_lastPrice = aTick.getPrice();
			return true;
		}

		public void setLastSent(Tick tk) {
			U.debug_var(871822, "Forcing last sent zbeh to ", tk);
			_lastPrice = tk.getPrice();
		}

	}

	/**
	 * For now the historical volume is zero we do not have information.
	 */
	// private static final int HISTORICAL_VOLUME = 0;

	/**
	 * Array used to store the real time ticks that come in real time before the
	 * warm up has finished.
	 */
	private ArrayList<RealTick> _queuedTicks = new ArrayList<>();

	private ArrayDeque<FinalNotFinalTick> _realQueueTicks = new ArrayDeque<>();

	/**
	 * This flag is used to signal the end of the expansion (a.k.a. warm up);
	 */
	private AtomicBoolean _expansionEnded = new AtomicBoolean();

	private final IBarCache _cache;
	/**
	 * This is the chained expander, its relationship with this expander is
	 * different if we merge or not the layers.
	 * 
	 * <p>
	 * If we merge the layers the upper expander is higher density, the contrary
	 * if we do not merge.
	 */
	private final CacheExpander _upperExpander;
	/**
	 * The filter is not final because it can be set also by an upper layer if
	 * the layers are not merged.
	 */
	private FillGapsMachine _fgm;
	private final ICacheExpanderListener _listener;
	private long _averageDuration = -1;
	private final int _tick;

	/**
	 * The classificator that distinguish a final from a not final price.
	 */
	private FinalNotFinalClassificator _fnfc;

	/**
	 * This is the filter which supersedes the FilterOneBar
	 */
	private OneBarHistoricalFilter _obhf;

	/**
	 * The filter for the lonely ticks.
	 */
	private LonelyTicksFilter _ltf;

	/**
	 * This is used in the expansion AND in the real time phase, so it needs to
	 * be here, for convenience.
	 * 
	 * <p>
	 * It is still a private class because it also needs to be used in the warm
	 * up phase of the filtering (which is different from the expansion phase)
	 * 
	 */
	private ZeroBarExpanderHelper _zbeh;

	/**
	 * This is the filter used to dispense volume to the ticks outside.
	 * <p>
	 * It is called "dispenser" because it may "divide" the entire volume in
	 * shares, used by the fake times.
	 */
	// private VolumeDispenserFilter _vdf;

	/**
	 * I try to warm up the filter only once.
	 */
	private boolean _alreadyTriedToWarmUpFilter = false;

	/**
	 * This is the next fake time, which is resetted when the expander is
	 * starting to expand.
	 */
	private int _nextFakeTime;

	/**
	 * Every expander is tied to a particular layer; the convention is that the
	 * layer zero is the real time layer, other layers are numbered 1,2,3 etc...
	 */
	private final int _layer;

	/**
	 * If true the cache expander behaves as normal, merging the layers.
	 */
	private final boolean _mergeLayer;

	/**
	 * This is the filter used to build the statistics.
	 */
	private FillGapsMachine _learningFilter;

	/**
	 * The iterator is used by the cache expander to save the last bar fetched
	 * from the cache (this is then used by the real time cache bar expander to
	 * know if there are new bars).
	 */
	private Iterator<Bar> _cacheItr;

	/**
	 * this is the previous bar which has been given to the outside, it is used
	 * by the cache expander to know the duration in the simulated real time
	 * part.
	 */
	private Bar _barPrev;

	/**
	 * This is the last array used to expand the current bar (which is in
	 * _barPrev). This array may prematurely end because we have received the
	 * {@link EndOfWarmUpException}
	 */
	private Tick[] arrFixed;

	/**
	 * The index inside the {@link #arrFixed} which has last been sent. The
	 * interruption is caused by the {@link EndOfWarmUpException}
	 */
	private int _lastIndexToBeSent;

	/**
	 * Used by the cache expander to signal the of a simulated real time
	 */
	private boolean _endOfWarmUp = false;

	private Tick[] arr;

	/**
	 * This flag tells us if this cache expander will expand also in real time
	 */
	private final boolean _realTime;

	/**
	 * This constructor is really ugly, too many parameters.
	 * 
	 * <p>
	 * But it is so because in this temporary phase the cache expander is in
	 * some way related to the {@link CompositeDataSource}, but that class
	 * should be deprecated soon.
	 * 
	 * 
	 * @param upperExpander
	 *            this is the upper expander, the expander of the upper layer.
	 *            The expanders are ordered from the least to the more dense,
	 *            and the least dense asks to the more dense how to fill the
	 *            gaps
	 * @param aTick
	 * @param aSeed
	 * @param aXp
	 * @param aDp
	 * @param filterLonelyTicks
	 *            true, if you want to filter the lonely ticks.
	 * @param aListener
	 *            the composite data source which will get the prices expanded
	 *            by this expander
	 * @param aLayer
	 *            The layer for this expander
	 * @param mergeLayer
	 *            if true the cache expander behaves as normal, it truncates the
	 *            ticks from the crypt whenever the cut off time is reached. The
	 *            expansion starts from daily and goes towards range. If false,
	 *            instead, the layers are stacked differently and the expansion
	 *            starts from range and goes back to minute and daily. Each
	 *            expander will have an autonomous layer and fake time
	 */
	public CacheExpander(IBarCache aCache, CacheExpander upperExpander,
			EGapFillingMethod aMethod, int gap1, int gap2, int aTick,
			long aSeed, double aXp, double aDp, boolean filterLonelyTicks,
			int minimumGap, ICacheExpanderListener aListener, int aLayer,
			boolean mergeLayer, boolean realTime) {
		_realTime = realTime;
		_mergeLayer = mergeLayer;
		_layer = aLayer;
		_cache = aCache;
		_tick = aTick;

		_fnfc = new FinalNotFinalClassificator(aTick);
		_obhf = new OneBarHistoricalFilter(aTick);
		// _vdf = new VolumeDispenserFilter();

		if (!_mergeLayer) {
			_zbeh = new ZeroBarExpanderHelper();
		}

		if (filterLonelyTicks) {
			_ltf = new LonelyTicksFilter(aTick, minimumGap);
			_ltf.enableOff();
		}

		switch (aMethod) {
		case PRICE_MULTIPLIER:
			/*
			 * In case of price multiplier, even with merge, every layer goes on
			 * its own, so every layer has its own fill gaps machine.
			 */
			_fgm = new FillGapsMachine(aTick, aSeed, false, aXp, aDp);
			_fgm.setParameters(gap1, gap2);
			break;
		case UPPER_CACHE:
			if ((_mergeLayer && upperExpander == null) || (!_mergeLayer)) {
				/*
				 * If I merge the layers I have to create the filter if this is
				 * the top range, the ordinal is zero, if I do not have to merge
				 * the layers then I will in any case create the filter because
				 * each layer will have its own (which will become the learning
				 * filter after that).
				 */
				_fgm = new FillGapsMachine(aTick, aSeed, true, aXp, aDp);
			} else {
				_fgm = null;
			}

			break;
		default:
			throw new IllegalStateException();
		}
		_upperExpander = upperExpander;
		_listener = aListener;
	}

	/**
	 * Expand the cache given until the limit time.
	 * 
	 * <p>
	 * This function is recursive because it will call itself, but at different
	 * layers, until it reaches the upper layer.
	 * 
	 * <p>
	 * It will call the filter with the expanded prices. The prices are expanded
	 * in the usual manner, that is if the bar is "up", then the low is first
	 * and then the high, viceversa if the bar is down
	 * 
	 * <p>
	 * The cache is never expanded in reverse, because all the computation is
	 * done feed forward only.
	 * 
	 * <p>
	 * The function is synchronous and it will not return until the expansion is
	 * finished: later the interrupt possibility will be added using the
	 * {@link IProgressMonitor} object.
	 * 
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 * 
	 */
	@SuppressWarnings({ "boxing" })
	private void _expand() throws DFSException, MonitorCancelledException {

		/*
		 * This works only if I am merging layers, otherwise I can filter lonely
		 * ticks
		 */
		if ((_mergeLayer && _upperExpander == null && _ltf != null)
				|| (!_mergeLayer && _ltf != null)) {
			/*
			 * If I am the topmost layer and I have to filter the lonely ticks
			 * then I filter them.
			 */
			_ltf.enableOn();
		}

		if (_cache == null || _cache.size() == 0) {
			/*
			 * The cache is empty, no expansion here, if there is an upper layer
			 * expand to it
			 */
			if (_upperExpander != null) {
				_upperExpander.expand();
			} else {
				// unique expander, this is the real time
				_zbeh.endExpansion();
				_expansionEnded.set(true);
			}
			return;
		}

		/*
		 * I cannot expand a cache of one element, because I need a duration,
		 * and a duration is the difference in time between two bars.
		 */
		if (_cache.size() == 1) {
			throw new IllegalStateException();
		}

		_cacheItr = _cache.iterator();

		_getAverageDuration();

		arr = Bar.getTickExpansionPlace();
		arrFixed = new Tick[arr.length]; // I don't create the objects!

		_barPrev = null;

		// int lastPrice = -1;
		CACHE_ITERATOR: while (_cacheItr.hasNext()) {
			Bar aBar;
			try {
				aBar = _cacheItr.next();
			} catch (NoSuchElementException e) {
				/*
				 * Probably the iterator has found a condition in which the
				 * slicer has been closed, this means that the virtual symbol is
				 * going to shut down.
				 */
				U.debug_var(239151,
						"Expansion aborted because the iterator is broken");
				throw new MonitorCancelledException();
			}

			long curDuration;
			if (_barPrev != null) {
				curDuration = aBar.getTime() - _barPrev.getTime();
			} else {
				curDuration = _averageDuration;
			}
			_barPrev = aBar;

			aBar.expand(arr, curDuration);
			_zbeh.adjustTickArray(arr, arrFixed);

			// now I can pass the ticks to the outside.
			_lastIndexToBeSent = 0;
			for (Tick tk : arrFixed) {
				if (tk == null) {
					break;
				}
				boolean again = true;
				again = _sendTickOutOrUp(tk, true);
				_lastIndexToBeSent++;
				if (!again && _mergeLayer) {
					/*
					 * This usually happens in case of merged layers and in this
					 * case the expansion will end when the lower layer (the
					 * "crypt") has finished expanding.
					 */
					return;
				}

				if (_endOfWarmUp) {
					if (_mergeLayer) {
						throw new IllegalStateException(); // why?
					}
					/*
					 * Return unconditionally, because the "real time"
					 * simulation will be handled by the virtual symbol.
					 */
					U.debug_var(194856, "layer ", _layer,
							" end of warm up expansion. I have ",
							_realQueueTicks.size(),
							" queued real time ticks. its first is ",
							_realQueueTicks.peek());
					break CACHE_ITERATOR;
				}
			}

		}

		if (!(_realTime || _endOfWarmUp)) {
			// I am not in real time but I have not received the end of
			// warm up, I will force it.
			U.debug_var(287195, "layer ", _layer,
					" not in real time, I force end of warm up at fake time ",
					_nextFakeTime - 1);
			_endOfWarmUp = true;
			_listener.endOfExpansion(_layer);
		}

		/*
		 * If in this point the _upperExpander is not null, I have not ended
		 * prematurely the expansion and this is the last level, if the next
		 * assert fails than it means that the times are not in the right order,
		 * there is an upper expander whose end time is before the end time of
		 * this expander, so this upper expander has nothing to expand... It may
		 * be a consistent case, albeit a bit strange, but for now I prefer to
		 * assert it.
		 * 
		 * If I am here and I have to merge then the upper expander must be
		 * null, I must be on the top. However, if I do not have to merge the
		 * fact that the upper expander is not null simply means that I have to
		 * expand it passing to it the fill gaps machine
		 */
		if (_mergeLayer) {

			/*
			 * The upper expander could be not null in the case of poorly put
			 * slots, for example in the simulator, the end date of the daily
			 * could be after the date of the minute, so this may be the case
			 * 
			 * For now I silence the assertion.
			 */

			if (_upperExpander != null) {
				U.debug_var(728347,
						"end expansion and upper expander is not null ",
						_upperExpander);
				/*
				 * I force the expansion of the upper layer, because this means
				 * that the upper layer has a first bar after the last bar of
				 * this layer. There will be a time gap. The price gap, instead,
				 * will be handled by the fill gap machine (that in the case of
				 * merged layers will be active only in the layer zero).
				 */
				_upperExpander.expand();
				return;
			}

		} else {
			// no merge layer
			if (_upperExpander != null) {
				/*
				 * If I have trained the learning filter this is the end of the
				 * training period.
				 */
				FillGapsMachine fgmToTake = _learningFilter == null ? _fgm
						: _learningFilter;
				fgmToTake.freeze();
				_upperExpander._takeUpperGapsMachineFilter(fgmToTake);
				_upperExpander._expand();
				fgmToTake.thaw();

			}
			/*
			 * Ok, the upper (lower!, because I go from range to daily) layer
			 * has finished the expansion so I can thaw the filter. If I had a
			 * learning filter that filter becomes my real filter, otherwise I
			 * get the old filter but I thaw it
			 */
			if (_learningFilter != null) {
				/*
				 * If I gave the learning filter to the lower expander then I
				 * have to thaw it, because it has been used by the lower
				 * expander to fill the gaps.
				 */
				_learningFilter.catchUpWithFilter(_fgm);
				if (!_endOfWarmUp) {
					/*
					 * The order here is important, because the learning filter
					 * must be synchronized with the filter given from the
					 * outside. So first I tell him to end the warm up and then
					 * he will synch at the end of the expansion (taking the
					 * last price and last time).
					 */
					_learningFilter.endOfWarmUpFilter();
					/*
					 * This is tricky, because it changes the fill gap machine,
					 * but it should not be done if the expansion has ended
					 * prematurely for a simulated warm up.
					 */
					_learningFilter.syncAtEndOfExpansion(_fnfc.getLastTick());
				} else {
					_learningFilter.forceEndWarmUp();
				}
				_fgm = _learningFilter;
			}

			if (_endOfWarmUp) {
				/*
				 * This is a forced end, because this means that there is not a
				 * real time part, or, that the historical part has not been
				 * sufficiently long to arrive at the simulated end for warm up,
				 * in that case the end of warm up will be simulated by the
				 * endOfExpansion call which is below.
				 */
				return;
			}
		}

		// At the end of the expansion I have to expand the ticks which have
		// been queued
		synchronized (_queuedTicks) {
			/* This array is synchronized also in the method realTimeTick */

			List<RealTick> incompletedTicks = _obhf.getIncompleteExpansion();
			Iterator<RealTick> filterTicksIt = incompletedTicks.iterator();
			Iterator<RealTick> queuedTicksIt = _queuedTicks.iterator();

			U.debug_var(399034, "Finished the expansion... with ",
					_queuedTicks.size(), " ticks to push (queue)");

			_zbeh.debugStat();

			/*
			 * This call for coherence is done because in the passage from warm
			 * up to real time I do not use the _obhf any more, and this means
			 * that the _fnfc might have stayed behind
			 */
			_zbeh.makeCoherentTo(_fnfc);

			if (_ltf != null) {
				_ltf.makeCoherentTo(_zbeh._lastTime, _zbeh._lastPrice);
			}

			/*
			 * I need to take the fill gap machine at the same level as the
			 * queued ticks, this because the warm up has already seen some
			 * prices which the _fnfc has not, because they are the incomplete
			 * bar, but the incomplete bar may be not sent to the outside,
			 * because the real ticks are fresher.
			 */
			if (_fgm != null) {
				_fgm.syncAtEndOfExpansion(_fnfc.getLastTick());
			}

			// I have to merge the two series, but the real time wins
			RealTick filterTick = null;
			RealTick queuedTick = null;
			// int queueVolume = 0;
			while (true) {

				if (filterTick == null && queuedTick != null) {
					U.debug_var(391039, "Sending queued tick ", queuedTick);
					_preSendTick(queuedTick);
					queuedTick = null;
				} else if (filterTick != null && queuedTick == null) {
					U.debug_var(563113, "Sending filtered tick ", filterTick);
					_preSendTick(filterTick);
					filterTick = null;
				} else if (filterTick != null && queuedTick != null) {
					if (filterTick.happensAfter(queuedTick)) {
						U.debug_var(293913, "Sending queued tick ", queuedTick,
								" because filter is ", filterTick);
						_preSendTick(queuedTick);
						queuedTick = null;
					} else {
						U.debug_var(564221, "Sending filter tick ", filterTick,
								" because queued is ", queuedTick);
						_preSendTick(filterTick);
						filterTick = null;
					}
				}

				if (filterTick == null && filterTicksIt.hasNext()) {
					filterTick = filterTicksIt.next();
				}

				if (queuedTick == null && queuedTicksIt.hasNext()) {
					queuedTick = queuedTicksIt.next();
				}

				if (filterTick == null && queuedTick == null) {
					break;
				}

			}

			_queuedTicks.clear(); // just to make sure that we do not reuse them

			// only in this moment I set the end of expansion, because the real
			// time thread could be waiting in
			// the synchronized entrance to the queued ticks
			_expansionEnded.set(true);

			/*
			 * The end of expansion could be a "no-op", because the listener has
			 * already simulated the end of warm up. But this is necessary if
			 * the user has choosen a warm up number of prices greater than the
			 * available expansion set.
			 */
			_listener.endOfExpansion(_layer);

			U.debug_var(617373, "layer ", _layer, " ENDED EXPANSION @ ",
					_zbeh.getLastPrice(), "  fake time ", _nextFakeTime - 1);
		}

	}

	/**
	 * expands a bit this expander, only if the warm up is finished (and this
	 * means that I have not merged the layers).
	 * 
	 * @throws MonitorCancelledException
	 * @throws DFSException
	 */
	private void _expandABit() throws DFSException, MonitorCancelledException {
		assert (_endOfWarmUp); // you must have finished the simulated warm up
		assert (_realQueueTicks.size() == 0); // the queue is empty
		assert (!_mergeLayer); // you cannot merge layers and be here.

		// we may have arrived at the end of the array in the _expand method.
		Tick tick = _lastIndexToBeSent == arrFixed.length ? null
				: arrFixed[_lastIndexToBeSent];

		if (tick == null) {
			/*
			 * That means that I have to expand a bar.
			 */
			if (!_cacheItr.hasNext()) {
				// I have really finished!
				return;
			}
			Bar aBar = _cacheItr.next();
			_lastIndexToBeSent = 0;

			long curDuration;
			if (_barPrev != null) {
				curDuration = aBar.getTime() - _barPrev.getTime();
			} else {
				curDuration = _averageDuration;
			}
			_barPrev = aBar;

			aBar.expand(arr, curDuration);
			_zbeh.adjustTickArray(arr, arrFixed);

			// recurse, this time the tick will be different from null, and I
			// will enter the second if branch
			_expandABit();

		} else {
			// I advance the pointer and send the tick
			_lastIndexToBeSent++;
			if (_lastIndexToBeSent == arrFixed.length) {
				// this is in any case the last tick, next time I will have to
				// send a new bar.
				_lastIndexToBeSent = 0;
				Arrays.fill(arrFixed, null);
			}

			// process this tick in the pipeline
			_sendTickOutOrUp(tick, true);

			if (_realQueueTicks.size() != 0) {
				return; // there are more
			}
			// recurse with the next tick until a tick goes into the queue.
			_expandABit();
		}

	}

	/**
	 * returns the average duration of a cache, this is used to expand correctly
	 * the first bar.
	 * 
	 * @return
	 */
	@SuppressWarnings("boxing")
	private void _getAverageDuration() {
		if (_averageDuration != -1) {
			return;
		}
		long totalDuration = 0;
		Iterator<Bar> it = _cache.iterator();

		Bar barPrev1 = null;
		int items = 0;
		while (it.hasNext()) {
			Bar aBar = it.next();
			if (barPrev1 != null) {
				totalDuration += aBar.getTime() - barPrev1.getTime();
				items++;
			}
			barPrev1 = aBar;
		}

		_averageDuration = Math.max(totalDuration / items, 4);
		U.debug_var(399193, "Average duration is ", _averageDuration, " on ",
				items, " items");
	}

	/**
	 * the pre method for the tick rt. The tick is passed in the
	 * {@link FinalNotFinalClassificator} before sending it outside
	 * 
	 * @param rt
	 *            the real tick (it can be fake!)
	 * @throws MonitorCancelledException
	 * @throws EndOfWarmUpException
	 */
	private void _preOutTick(RealTick rt) throws MonitorCancelledException {

		EAnswer res = _fnfc.acceptTick(rt);
		QueueTick qt;

		/*
		 * Here I may have two ticks, and maybe the exception is received on the
		 * first. In this case I would have to empty somewhere else the pipeline
		 * because all the filters are "ready to fire".
		 * 
		 * In a sense I have the control on the start and on the end of the
		 * pipeline. The start of the pipeline is the array (iterator) of bars.
		 * The end of the pipeline is the output of the final/not-final
		 * classifier.
		 */

		switch (res) {
		case PREVIOUS_AND_THIS_ARE_FINAL:
			int dividedVolume = _fnfc.getSharedVolume() / 2;

			_listener.onVolumeUpdate(_nextFakeTime - 2, dividedVolume);

			RealTick temp = _fnfc.getPreviousFinalTick();
			QueueTick qt_before = new QueueTick(temp, _nextFakeTime++);

			qt_before.setVolume(dividedVolume + _fnfc.getSharedVolume() % 2);

			_sendOrSaveFinalTick(qt_before);
			// _endOfWarmUp = _listener.finalNotifiyTick(_layer, qt_before);
			qt = new QueueTick(rt, _nextFakeTime++);
			_sendOrSaveFinalTick(qt);
			// _endOfWarmUp = _listener.finalNotifiyTick(_layer, qt);
			break;
		case THIS_TICK_IS_FINAL:
			qt = new QueueTick(rt, _nextFakeTime++);
			_sendOrSaveFinalTick(qt);
			// _endOfWarmUp = _listener.finalNotifiyTick(_layer, qt);
			break;
		case THIS_TICK_IS_NOT_FINAL:
			qt = new QueueTick(rt, _nextFakeTime);
			_sendOrSaveNotFinalTick(qt);
			/*
			 * I do not increment the fake time if the tick is not final.
			 */
			break;
		default:
			throw new RuntimeException("Not handled case");

		}

	}

	/**
	 * This is the entry point for the expander, it will give it out (to the
	 * outside) or up (to an upper expander).
	 * 
	 * <p>
	 * In real time this latter case is not possible, as this is the top
	 * expander
	 * 
	 * @param aTick
	 * @param volume
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 * @throws EndOfWarmUpException
	 */
	private void _preSendTick(RealTick aTick) throws DFSException,
			MonitorCancelledException {
		// filter 0p the tick
		if (!_zbeh.massageRealTimeTick(aTick)) {
			int updatedVolume = _fnfc.onRealTimeQuoteVolumeUpdate(
					aTick.getPrice(), aTick.getVolume());

			// ignoring
			if (updatedVolume < 0) {
				return;
			}

			/*
			 * The volume update is about the fake time, the current fake time,
			 * because
			 */
			_listener.onVolumeUpdate(_nextFakeTime - 1, updatedVolume);

			return;
		}

		// _fnfc.onRealTimeNewQuoteInitialVolume(aTick.getPrice(),
		// aTick.getVolume());

		_sendTickOutOrUp(aTick, false);

	}

	private void _propagateExpander(ZeroBarExpanderHelper _zbeh2) {
		_zbeh = _zbeh2;
		if (_upperExpander != null) {
			_upperExpander._propagateExpander(_zbeh2);
		}
	}

	private void _selfSendQueuedTick(FinalNotFinalTick head)
			throws MonitorCancelledException {
		if (head._final) {
			_listener.finalNotifiyTick(_layer, head._qt);
		} else {
			_listener.passNotFinalTick(_layer, head._qt);
		}

	}

	/**
	 * Either sends to the cache listener or saves the current final tick.
	 * <p>
	 * If this layer has already reached the (fictitious) end of warm up, this
	 * tick is saved to be sent as a real time tick.
	 * 
	 * @param aQt
	 *            the tick to be sent or saved.
	 * @throws MonitorCancelledException
	 */
	private void _sendOrSaveFinalTick(QueueTick aQt)
			throws MonitorCancelledException {
		if (_endOfWarmUp) {
			FinalNotFinalTick fnft = new FinalNotFinalTick(aQt, true);
			_realQueueTicks.add(fnft);
		} else {
			// If I merge the layer, I never save the real time ticks, because
			// they are sent in the same order.
			_endOfWarmUp = _listener.finalNotifiyTick(_layer, aQt)
					&& !_mergeLayer;
		}
	}

	private void _sendOrSaveNotFinalTick(QueueTick qt)
			throws MonitorCancelledException {
		if (_endOfWarmUp) {
			FinalNotFinalTick fnft = new FinalNotFinalTick(qt, false);
			_realQueueTicks.add(fnft);
		} else {
			_listener.passNotFinalTick(_layer, qt);
		}

	}

	/**
	 * This method will send the tick out or up, that means that the tick is
	 * sent towards the upper expander if this has one, or to the fill gaps
	 * machine.
	 * 
	 * <p>
	 * The tick <b>must</b> be monotonically increasing, this is enforced by the
	 * fill gap machine
	 * 
	 * @param tk
	 * @param filter1p
	 *            must I filter the ticks?
	 * @return
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 * @throws EndOfWarmUpException
	 */
	private boolean _sendTickOutOrUp(Tick tk, boolean filter1p)
			throws DFSException, MonitorCancelledException {
		if (_mergeLayer && _upperExpander != null) {
			/*
			 * this call may trigger the recursive chain, because we have
			 * reached the limit
			 */
			boolean again = _upperExpander._tickFromTheCrypt(tk, _zbeh);
			return again;
		}

		if (!_fgm.isReady() && !_alreadyTriedToWarmUpFilter) {
			_warmUpFilter();
			_alreadyTriedToWarmUpFilter = true;
		}

		if (_ltf != null) {
			// U.debug_var(731939, "sending ", tk, " to ltf");
			LonelyTicksFilter.EAnswer res = _ltf.acceptTick(tk);
			switch (res) {
			case THIS_AND_PREVIOUS_TICKS_ARE_OK:
				// U.debug_var(293103, "sending prev tick ",
				// _ltf.getPreviousTick(), " and then ", tk);
				_sendToFgm(_ltf.getPreviousTick(), filter1p);
				_sendToFgm(tk, filter1p);
				break;
			case THIS_TICK_IS_IN_JAIL:
				// nothing
				break;
			case THIS_TICK_IS_OK:
				// U.debug_var(190993, "Sending tick ", tk);
				_sendToFgm(tk, filter1p);
				break;
			default:
				break;
			}
		} else {
			// U.debug_var(183899, "sending ", tk, " to FGM, no LTF");
			_sendToFgm(tk, filter1p);
		}
		// I am the ultimate layer, so I want always ticks
		return true;
	}

	private void _sendToFgm(Tick tk, boolean filter1p)
			throws MonitorCancelledException {

		ArrayList<RealTick> ticks = _fgm.accept(tk);
		if (filter1p) {
			for (RealTick rt : ticks) {
				// U.debug_var(193951, "sending ", rt, " to filter");
				List<RealTick> arrRet = _obhf.acceptTick(rt);
				if (arrRet != null) {
					for (RealTick rt1 : arrRet) {
						_preOutTick(rt1);
					}
				}

			}
		} else {
			for (RealTick rt : ticks) {
				// U.debug_var(193951, "sending ", rt,
				// " to final classificator");
				_preOutTick(rt);
			}
		}

		/*
		 * If the learning filter is not null then I warm up the learning filter
		 * with these ticks
		 */
		if (_learningFilter != null) {
			for (RealTick tk1 : ticks) {
				_learningFilter.accumulatePrice(tk1.getPrice() / _tick,
						tk1.getPhysicalTime());
			}
		}
	}

	/**
	 * Takes an (already warmed up) filter from the upper expander to expand
	 * ticks.
	 * 
	 * @param _fgm2
	 */
	private void _takeUpperGapsMachineFilter(FillGapsMachine _fgm2) {

		/*
		 * I take the fill gaps machine from the upper layer and this becomes my
		 * "normal" filter to do the gap filling, My filter, instead, becomes a
		 * filter which is used to accumulate the prices and give it to the
		 * upper layer (if present)
		 */
		_learningFilter = _fgm;
		_fgm = _fgm2;
		// _fgm.freeze();

	}

	/**
	 * gets the tick from the lower layer... it needs to check if this tick is
	 * above the limit and, if it is.
	 * 
	 * <p>
	 * The crypt is so called because we intend the lower layer which is in some
	 * way the crypt, the below.
	 * 
	 * <p>
	 * When we arrive at this point the object is already the upper expander, so
	 * it is already the upper layer.
	 * 
	 * @param tk
	 * @param _zbeh2
	 * @return
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 * @throws EndOfWarmUpException
	 */
	@SuppressWarnings("boxing")
	private boolean _tickFromTheCrypt(Tick tk, ZeroBarExpanderHelper _zbeh2)
			throws DFSException, MonitorCancelledException {
		_getAverageDuration();
		if (tk.getPhysicalTime() >= (_cache.getBar(0).getTime() - _averageDuration)) {
			U.debug_var(391043, "Overlap... @ ", tk,
					" first bar time (end) is ", _cache.getBar(0).getEndDate(),
					" avg duration (sec) ", _averageDuration / 1000.0);

			// I send the overlap tick anyway
			_sendTickOutOrUp(tk, true);

			_zbeh2.setLastSent(tk);

			// Ok, when I overlap I recurse, taking with myself the expansion
			// filter
			U.debug_var(903913, "EXPANDING WITH the lower zbeh @",
					_zbeh2.getLastPrice());

			/*
			 * PUSH layer, the layer is now upgraded by one.
			 */
			this._expand();
			/*
			 * POP layer, the old layer is restored.
			 */

			return false; // no more expansion from the crypt, please

		}
		// I have to fill this, or pass it to the up level.
		return _sendTickOutOrUp(tk, true);

	}

	private void _warmUpFilter() {
		if (_cache == null || _cache.size() < 2) {
			return; // nothing to warm up
		}

		_fgm.startWarmUpFilter();

		Iterator<Bar> it = _cache.iterator();

		_getAverageDuration();

		Tick arr1[] = Bar.getTickExpansionPlace();
		Tick arrFixed1[] = new Tick[arr1.length];

		ZeroBarExpanderHelper zbeh = new ZeroBarExpanderHelper();

		Bar barPrev1 = null;

		while (it.hasNext()) {
			Bar aBar = it.next();

			long curDuration;
			if (barPrev1 != null) {
				curDuration = aBar.getTime() - barPrev1.getTime();
			} else {
				curDuration = _averageDuration;
			}

			aBar.expand(arr1, curDuration);

			zbeh.adjustTickArray(arr1, arrFixed1);

			// now I can pass the ticks to the outside.
			for (Tick tk : arrFixed1) {
				if (tk == null) {
					break;
				}
				int newPrice = tk.getPrice() / _tick;
				_fgm.accumulatePrice(newPrice, tk.getPhysicalTime());
			}

			barPrev1 = aBar;
		}

		_fgm.endOfWarmUpFilter();
	}

	@SuppressWarnings("boxing")
	public void checkNewBar() throws DFSException, MonitorCancelledException {
		while (_cacheItr.hasNext()) {
			Bar nextBar = _cacheItr.next();
			long duration = nextBar.getTime() - _barPrev.getTime();
			U.debug_var(203915, "layer ", _layer, " passing new bar ", nextBar,
					" with duration ", duration);
			// 4 is the minimum, 4 prices in 4 milliseconds
			duration = Math.max(4, duration);
			Tick[] arr1 = Bar.getTickExpansionPlace();
			nextBar.expand(arr1, duration);
			takeExpandedTicksFromNewBar(arr1);
			_barPrev = nextBar;
		}
	}

	/**
	 * expands the chain of this request.
	 * 
	 * <p>
	 * The object which receives this message can be the top or the bottom of
	 * the chain.
	 * 
	 * <p>
	 * In the case of the top of the chain (the range layer) the zero bar
	 * expander helper does not need to be shared, because each layer will
	 * expand autonomously.
	 * 
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 */
	public void expand() throws DFSException, MonitorCancelledException {
		/*
		 * This is the only zero bar expansion helper which is shared by all the
		 * expanders.
		 */
		_nextFakeTime = 0;
		if (_mergeLayer) {
			_propagateExpander(new ZeroBarExpanderHelper());
		}
		_expand();

	}

	public int getCurrentFakeTime() {
		return _nextFakeTime - 1;
	}

	// public boolean isInWarmUp() {
	// return !_expansionEnded.get();
	// }

	/**
	 * Called when a new real time tick comes from the outside.
	 * <p>
	 * The tick is filtered if its time is inferior to the last expansion time
	 * (which is the last expansion of the upper level). After that the tick is
	 * always accepted, only its time is in some way massaged.
	 * 
	 * <p>
	 * The tick can have two fates: if there is an expansion expanding the tick
	 * is saved for future. Otherwise it is sent if it is congruent with the
	 * last real time. The expander will return times in strict monotonic order.
	 * 
	 * 
	 * @param aTick
	 *            the tick that comes from the outside (from a real time
	 *            subscription)
	 * @param aVolume
	 *            The volume for this tick
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 */
	public void realTimeTick(RealTick aTick) throws DFSException,
			MonitorCancelledException {

		U.debug_var(917583, "Cache expander rec. ", aTick);

		/*
		 * If this fails then you are sending a real time tick to a crypt (lower
		 * expander), which is rather inconsistent
		 */
		if (_mergeLayer)
			assert (_upperExpander == null);
		/*
		 * A negative layer is a dummy placeholder for a csv file... temporary
		 * hack
		 */
		assert (_layer <= 0);

		// code to check the queue for real time ticks.
		synchronized (_queuedTicks) {

			// I have acquired the lock, so the expander thread cannot use this
			// array
			if (_expansionEnded.get()) {
				// this is usually the norm.
				assert (_queuedTicks.size() == 0); // if this fails there is
				_preSendTick(aTick);
			} else {
				/*
				 * I have to make a silly 0p filter, because in the vector I
				 * need only different prices, this is not so urgent, but I
				 * thought that this is more congruent with the logic of the
				 * warm up: during the warm up the real time volume updates are
				 * compressed to the oldest tick.
				 */
				if (_queuedTicks.size() != 0) {
					RealTick lastTick = _queuedTicks
							.get(_queuedTicks.size() - 1);
					if (lastTick.getPrice() == aTick.getPrice()) {
						lastTick.accumulateVolume(aTick.getVolume());
						return;
					}
				}
				_queuedTicks.add(aTick);
			}

		}
	}

	/**
	 * orders to an expander to treat the next (database) tick as a simulated
	 * real time tick and "self sends" it (using the
	 * {@link #realTimeTick(RealTick)} method.
	 * 
	 * <p>
	 * This method will always succeed until there are ticks left in the cache.
	 * 
	 * @return the time of this simulated real time tick @ layer zero. -1 if the
	 *         layer zero has no more simulated ticks to give.
	 * @throws MonitorCancelledException
	 * @throws DFSException
	 */
	public long selfSendRealTimeTick() throws MonitorCancelledException,
			DFSException {
		// only real time layer can send the saved real time tick!
		assert (_layer == 0);

		// send the real time tick and remove it from queue.
		FinalNotFinalTick head = _realQueueTicks.poll();
		if (head == null) {
			/*
			 * I have to check the cache iterator!
			 */
			_expandABit();

			// if after this the queue is still empty I give up
			if (_realQueueTicks.size() == 0) {
				return -1;
			}
			/*
			 * otherwise I recurse, but only one depth, because head is not null
			 * now.
			 */
			return selfSendRealTimeTick();
		}

		// U.debug_var(283716, "Sending simulated real time tick @ layer zero ",
		// head);
		_selfSendQueuedTick(head);
		return head._qt.getPhysicalTime();
	}

	/**
	 * self sends saved (or expanded) simulated real time ticks until the
	 * physical time given.
	 * 
	 * <p>
	 * This method is used for layers different from layer zero, because they
	 * need to have a limit, which is imposed by the real time layer.
	 * 
	 * @param newTick
	 * @throws MonitorCancelledException
	 * @throws DFSException
	 */
	public void selfSendRealTimeTicksUntil(long newTick)
			throws MonitorCancelledException, DFSException {
		// only real time layer can send the saved real time tick!
		assert (_layer != 0);

		FinalNotFinalTick head = _realQueueTicks.peek();

		if (head != null) {
			if (head._qt.getPhysicalTime() < newTick) {
				// Ok, I can remove it from queue and send it.
				_realQueueTicks.poll();
				// U.debug_var(981785,
				// "Self send simulated real time tick layer ", _layer,
				// " ", head);
				_selfSendQueuedTick(head);
				/*
				 * Recurse, because there may be other ticks to send before the
				 * time
				 */
				selfSendRealTimeTicksUntil(newTick);
			}

			// else {
			// U.debug_var(918186, "lay ", _layer,
			// " no tick to send, next will be ", head,
			// " layer zero is @ ", new Date(newTick));
			// }
		} else {
			/*
			 * look inside the cache iterator, expand a little more and recurse.
			 */
			_expandABit();
			if (_realQueueTicks.size() != 0) {
				// recurse
				selfSendRealTimeTicksUntil(newTick);
			}
		}

	}

	/**
	 * gets an array of expanded ticks which come from a real time bar which has
	 * been raw expanded.
	 * 
	 * <p>
	 * The ticks can be also of the same price, it is not important, because I
	 * filter them.
	 * 
	 * @param arr1
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 */
	public void takeExpandedTicksFromNewBar(Tick[] arr1) throws DFSException,
			MonitorCancelledException {
		/*
		 * Here the bar is arrived but in case of database request this bar is
		 * in the past and the expansion may produce ticks which are already
		 * over the last tick sent from layer zero.
		 * 
		 * This has to be worked better.
		 */
		Tick[] arrFixed1 = new Tick[arr1.length];
		_zbeh.adjustTickArray(arr1, arrFixed1);

		for (Tick tk : arrFixed1) {
			if (tk == null) {
				break;
			}
			_sendTickOutOrUp(tk, false);
		}

	}

}
