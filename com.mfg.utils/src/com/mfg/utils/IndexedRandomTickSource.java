package com.mfg.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import com.mfg.common.Tick;

/**
 * This is an indexed random tick source which is able to play a random
 * sequence, as the mother, the {@linkplain RandomTickSource}, but, on top of
 * it, it can go to a particular time, because it builds an index of the randoms
 * which are used to build the random ticks.
 * 
 * <p>
 * This class is <b>not</b> thread safe, care must be taken outside the class;
 * in particular there is <b>no</b> guarantee that the call to
 * {@link #putNextTick(Tick)} (or similar) will get the tick after the
 * {@link #goTo(long)} method, because another thread could have told to go
 * somewhere else before.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class IndexedRandomTickSource extends RandomTickSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 218130709422650967L;

	private final AtomicLong _refRandInternal;

	/**
	 * This class holds the state for this random source, this is used to store
	 * the state for the random source to replay exactly the same sequence over
	 * and over again
	 * 
	 * 
	 */
	private static final class RandomSourceState implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2977008362767390692L;

		public RandomSourceState() {
		}

		/**
		 * This is the internal seed of the random generator. It is taken via
		 * reflection, in reality it will be set to the {@linkplain AtomicLong}
		 * which is used in this particular Random implementation.
		 */
		public long internalSeed;

		public long nextTickTime;

		public int curPrice;
	}

	/**
	 * Saves the random state to be used by the index.
	 * 
	 * @return
	 */
	private RandomSourceState _saveRandomState() {
		RandomSourceState rss = new RandomSourceState();

		rss.internalSeed = _refRandInternal.get();
		rss.nextTickTime = _nextTickTime;
		rss.curPrice = fCurPrice;

		return rss;
	}

	/**
	 * This map orders the byte serialization of the {@linkplain Random} class
	 * taken at predetermined moments in time. The size of the map is a
	 * compromise between the size of the map itself and the time you need to
	 * seek a predetermined tick in the future, given the initial state of the
	 * random tick source.
	 */
	private TreeMap<Long, RandomSourceState> _randomSeedMap = new TreeMap<>();

	/**
	 * This is the frozen state of this random source at the moment of the next
	 * tick, that is this is the state of the random source <i>before</i> the
	 * issuing of the next tick. The next tick is in real time so it is true
	 * that
	 * <p>
	 * 
	 * <pre>
	 *  Tick tk = rss.getNextRealTimeTick();
	 *  tk.getPhysicalTime() < System.currentTimeMillis();
	 * </pre>
	 */
	private RandomSourceState _lastRealTimeTickState = null;

	/**
	 * Starts watching the symbol at <b>now</b> current time.
	 */
	public void startWatching() {
		// If this fails it means that you have not stopped before the watching.
		assert (_lastRealTimeTickState == null);
		long now = System.currentTimeMillis();
		goTo(now);
		_lastRealTimeTickState = _saveRandomState();

		// now I am able to give the next real time tick.
	}

	/**
	 * This is the initial time of the sequence;
	 * 
	 * <p>
	 * The sequence is able to restart at that particular moment in time.
	 */
	private long _initialTime;

	private long _initialSeed;

	public IndexedRandomTickSource(long seed, int tick) {
		this(seed, tick, new GregorianCalendar(2000, 0, 1).getTimeInMillis());
	}

	public IndexedRandomTickSource(long seed, int tick, long initialTime) {
		super(seed, tick, initialTime);

		_nextTickTime = initialTime;
		_initialTime = initialTime;
		_initialSeed = seed;

		try {
			java.lang.reflect.Field seedField = Random.class
					.getDeclaredField("seed");
			seedField.setAccessible(true);
			_refRandInternal = (AtomicLong) seedField.get(fRnd);
		} catch (NoSuchFieldError | IllegalArgumentException
				| IllegalAccessException | NoSuchFieldException
				| SecurityException ii) {
			// these should not happen, if they happen it is simply a bug.
			throw new RuntimeException(ii);
		}

	}

	/**
	 * moves the random tick source to the time given.
	 * 
	 * <p>
	 * The contract of this method is that, after calling it, the next call to
	 * either {@link #getNextTick()} or
	 * {@link #putNextTick(com.mfg.common.Tick)} will return the Tick with
	 * <em>least</em> physical time greater than the time given (the ceiling in
	 * set terms.).
	 * 
	 * <p>
	 * That is, it does not exist for this random sequence a Tick with a
	 * physical time less than the returned one and greater (or equal) to the
	 * time given.
	 * 
	 * <p>
	 * Of course the sequence has a minimum time, which is set by the start
	 * method.
	 * 
	 * @param aTime
	 *            the time to which you want to seek in this sequence.
	 * @throws IOException
	 */
	@SuppressWarnings("boxing")
	public void goTo(long aTime) {
		/*
		 * When I go to a particular moment in time I must return to the random
		 * seed which was in a particular moment in time.
		 */

		if (aTime < _initialTime) {
			throw new IllegalArgumentException();
		}

		// if (_randomSeedMap.size() == 0) {
		// U.debug_var(392935, "Building the map until ", new Date(aTime));
		// buildIndexUpTo(aTime, DEFAULT_FREQUENCY);
		// }

		// I have to restore the random...
		Entry<Long, RandomSourceState> entry = _randomSeedMap.floorEntry(aTime);

		if (entry != null) {
			// go the index...
			U.debug_var(293942, "Found the entry ", new Date(
					entry.getValue().nextTickTime), " for time ", new Date(
					aTime));

			// restore the state...
			RandomSourceState rss = entry.getValue();

			_loadRandomState(rss);

			// replay the ticks until the first tick after the cut off time
			Tick thrownTick = new Tick();
			int i = 0;
			while (_nextTickTime < aTime) {
				putNextTick(thrownTick);
				i++;
			}

			U.debug_var(193911, "Replayed ", i, " ticks, until ", thrownTick,
					" which is the last before ", new Date(aTime));

		} else {
			// this in reality should not happen, but may happen if you have not
			// built the index,in this case the random tick source behaves like
			// a normal tick source, without indexing. It resets the random
			// source and then it replays until the requested time.

			U.debug_var(391903, "Cannot find the entry at ", new Date(aTime),
					" I rebuild from start");
			reset();

			Tick throwAwayTick = new Tick();
			int i = 0;
			do {
				putNextTick(throwAwayTick);
				++i;
			} while (_nextTickTime < aTime);

			U.debug_var(939243, "Rebuilt ", i, " ticks, next tick time is ",
					new Date(_nextTickTime));

			// Ok, now I save the state for this random source.
		}

	}

	/**
	 * Loads the random state for this random source.
	 * 
	 * <p>
	 * Care must be given, because this class is not thread safe.
	 * 
	 * @param rss
	 */
	private void _loadRandomState(RandomSourceState rss) {
		this._nextTickTime = rss.nextTickTime;
		this.fCurPrice = rss.curPrice;
		_refRandInternal.set(rss.internalSeed);
	}

	public void reset() {
		fRnd.setSeed(_initialSeed);
		_nextTickTime = _initialTime;
		fCurPrice = INITIAL_TICKS;
	}

	/**
	 * Builds the index for this random data source.
	 * 
	 * <p>
	 * The index is created using the indexFrequency parameter, that is, every
	 * indexFrequency ticks a new milestone is produced.
	 * 
	 * 
	 * @param endTime
	 *            this is the end time of the sequence, it must be greater than
	 *            the initial time, of course.
	 * 
	 * @param indexFrequency
	 */
	@SuppressWarnings("boxing")
	public void buildIndexUpTo(long endTime, int indexFrequency) {
		if (endTime <= _initialTime) {
			throw new IllegalArgumentException();
		}

		reset();

		Tick genTick = new Tick();
		int i = 0;
		do {
			putNextTick(genTick);
			++i;

			if (i % indexFrequency == 0) {
				U.debug_var(294913, "storing the random tick generator @ ",
						genTick, " i ", i);

				RandomSourceState rss = _saveRandomState();

				/*
				 * This means that I put in the map the next state for the
				 * random number generator, so the next tick will surely have a
				 * time greater than this time
				 */
				_randomSeedMap.put(genTick.getPhysicalTime(), rss);

			}

		} while (_nextTickTime < endTime);

	}

	/**
	 * Starts the data sequence, you can give the initial time for this random
	 * data source.
	 */
	public void start() {
		_nextTickTime = _initialTime;
	}

	/**
	 * This is the real time callback used for this class.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public interface RealTimeCallback {
		public void onNewTick(Tick tk);
	}

	/**
	 * Puts the next real time tick in the tick argument; for this simulator a
	 * "real time" tick is a tick whose time is greater than the instant given.
	 * 
	 * <p>
	 * The data source knows the next tick time using the field
	 * {@link #_lastRealTimeTickState}, because in this there is the next tick
	 * time.
	 * 
	 * <p>
	 * This function is <b>NOT</b> synchronized, so the synchronization must be
	 * done outside this class; this is necessary because the synchronization in
	 * this class may be problematic, as there are several state structures.
	 * 
	 * @param aInstant
	 * @param aTick
	 * @return
	 * 
	 * @throws NullPointerException
	 *             if this data source is not watching. Please call
	 *             {@link #startWatching()} before.
	 */
	public long putNextRealTimeTick(long aInstant, Tick aTick) {

		/*
		 * if the next real time is after the instant I do not have to do
		 * anything, because the next tick time is after the simulated present.
		 */
		if (_lastRealTimeTickState.nextTickTime > aInstant) {
			return (-_lastRealTimeTickState.nextTickTime + aInstant);
		}

		_loadRandomState(_lastRealTimeTickState);

		putNextTick(aTick);

		// save the state, please.
		_lastRealTimeTickState = _saveRandomState();

		if (_nextTickTime <= aInstant) {
			return 0; // call me again
		}

		return _nextTickTime - aInstant;
	}

	/**
	 * 
	 */
	public void stopWatching() {
		_lastRealTimeTickState = null;
	}

}
