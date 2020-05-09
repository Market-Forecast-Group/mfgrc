package com.mfg.utils;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Random;

import com.mfg.common.Tick;

/**
 * This class models a tick source of random ticks with gaps. So technically it
 * is a source of RealTicks.
 * 
 * @author Sergio
 * 
 */
public class RandomTickSource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1752897855182747661L;

	protected static final int INITIAL_TICKS = 1000;

	private static final int DEFAULT_TICK_FREQUENCY = 20_000; // 20 seconds
	private static final int MINIMUM_TICK_SPACING = 5000; // 5 seconds

	private static final int MAX_DELTA_VOLUME = 17;

	private double fPercentageOfGaps = 0.05;
	private final int fTick;

	protected final Random fRnd;

	/**
	 * This is the current physical time for this data source.
	 */
	protected long _nextTickTime;

	/**
	 * The cur price is measured in ticks.
	 */
	protected int fCurPrice;

	/**
	 * Builds a Random Tick Source using the supplied seed.
	 * 
	 * <p>
	 * Two random tick sources with the same seed will return the same stream of
	 * prices.
	 * 
	 * <p>
	 * The initial tick time is initialize to a date in the past. It defaults to
	 * January 1st, 2000
	 * 
	 * @param seed
	 * @param tick
	 */
	public RandomTickSource(long seed, int tick) {
		fRnd = new Random(seed);
		fTick = tick;
		fCurPrice = INITIAL_TICKS;
		_nextTickTime = new GregorianCalendar(2000, 0, 1).getTimeInMillis();
	}

	/**
	 * Creates the random tick source with a predefined tick, seed and an
	 * initial time
	 * 
	 * @param seed
	 * @param tick
	 * @param initialTime
	 */
	public RandomTickSource(long seed, int tick, long initialTime) {
		this(seed, tick);
		_nextTickTime = initialTime;
	}

	/**
	 * 
	 * @return the next price of the sequence. It may have a gap from the
	 *         preceding price.
	 */
	public int getNextPrice() {
		// can I go up?
		int curGap;
		if (fCurPrice >= (INITIAL_TICKS * 2)) {
			curGap = -1; // I must go down
		} else if (fCurPrice <= 1) {
			curGap = 1; // I must go up
		} else {
			// I can choose
			curGap = fRnd.nextBoolean() ? 1 : -1;
		}

		// Now I must see if I must put a gap
		if (fRnd.nextDouble() < fPercentageOfGaps) {
			// The gap should not go further the bounds!
			int randomGap = Math.min(50, Math.min(fCurPrice,
					Math.abs(INITIAL_TICKS * 2 - fCurPrice)));
			if (randomGap != 0) {
				curGap *= fRnd.nextInt(randomGap);
			}
		}

		// debug_var(184953, "Adding a gap of ", curGap);
		fCurPrice += curGap;
		return fCurPrice * fTick;
	}

	/**
	 * gets the next tick from this random sequence.
	 * 
	 * @return
	 */
	public Tick getNextTick() {
		Tick tk = new Tick();
		putNextTick(tk);
		return tk;
	}

	/**
	 * identical to the {@link #getNextTick()} but it reuses the tick, avoiding
	 * a new creation.
	 * 
	 * @param aTick
	 *            the tick to overwrite.
	 */
	public void putNextTick(Tick aTick) {

		int price = getNextPrice();

		aTick.setPhysicalTime(_nextTickTime);
		aTick.setPrice(price);
		aTick.setVolume(price % MAX_DELTA_VOLUME + 1);

		_nextTickTime += fRnd.nextInt(DEFAULT_TICK_FREQUENCY)
				+ MINIMUM_TICK_SPACING;

	}

	/**
	 * sets not gaps for this data source.
	 */
	public void setNoGaps() {
		fPercentageOfGaps = 0;
	}

}
