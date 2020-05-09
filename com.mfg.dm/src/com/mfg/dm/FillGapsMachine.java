package com.mfg.dm;

import static com.mfg.utils.Utils.debug_var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.dm.filters.CacheExpander;
import com.mfg.utils.CircularInteger;

/**
 * This is the second version of the fill gaps machine. This machine is used to
 * fill the gaps.
 * <p>
 * Internally it will update a markov matrix which is used to fill the gaps (it
 * will use a simple random walk modified in a box. The details are stored in
 * the function).
 * 
 * @author Sergio
 * 
 */
public class FillGapsMachine {

	/**
	 * 
	 * @author Pasqualino
	 * 
	 */
	private class AccumulatedMarkovMatrix {

		private final MarkovMatrixInt fAccumulatedMM = new MarkovMatrixInt();

		// At first the state is not valid.
		private int fCurState = -1;
		/**
		 * The rank of the markov matrix. a rank of 3 means a markov matrix of 8
		 * rows (there is a power of two relation).
		 */
		private final int fRank;

		/**
		 * This is the boolean vector that is used to store the markov flags.
		 */
		private boolean fBv[];

		public AccumulatedMarkovMatrix(int rank1) {
			fRank = rank1;

			reset();

		}

		/**
		 * @return 1 if the output is greater then the last price, 0 otherwise.
		 */
		private int _get_output_of_data_at() {

			int cur_circ = fCursor.subtract(1, fCircPrices.length);
			int cur_circ_minus_one = fCursor.subtract(2, fCircPrices.length);

			if (fCircPrices[cur_circ] > fCircPrices[cur_circ_minus_one]) {
				return 1;
			}
			return 0;
		}

		private int _get_sample_of_data_at() {

			int startingIndex = fCursor.subtract(fRank + 2, fCircPrices.length);
			CircularInteger start = new CircularInteger(startingIndex,
					fCircPrices.length);

			for (int i = 0; i < fRank; ++i) {
				int i_circ = start.add(i + 1, fCircPrices.length);
				int i_circ_prev = start.add(i, fCircPrices.length);

				if (fCircPrices[i_circ_prev] == 0) {
					return -1;
				}
				fBv[i] = fCircPrices[i_circ] > fCircPrices[i_circ_prev];
			}
			return get_class_order();
		}

		/**
		 * 
		 * 
		 * @param curPrice
		 *            the current price to accept
		 * @param num_markov_lookback
		 * @return the new state of this markov chain
		 */
		void acceptNewPrice() {
			int i = _get_sample_of_data_at();
			int j = _get_output_of_data_at();

			if (i < 0) {
				// this means that the sample is invalid (I have not yet put in
				// the circular buffer
				// enough prices)
				return;
			}

			// This is the start of the markov chain.
			if (fCurState < 0) {
				fCurState = i;
			}

			fAccumulatedMM.pij[i][j]++;

			int delta = j == 0 ? -1 : 1;

			advanceMarkovState(delta);
		}

		/**
		 * it accumulates the new price into the markov chain. It uses the
		 * circular buffer...
		 */
		public void accumulate() {
			this.acceptNewPrice();
		}

		/**
		 * This combines the current state of the markov chain with the
		 * information of the next price.
		 * 
		 * To combine the state you have simply to shift left by one (multiply
		 * by two and then to add the delta bit).
		 * 
		 * @param delta
		 *            the current delta price &lt 0 if we have gone downwards
		 * 
		 */
		private void advanceMarkovState(int delta) {
			int temp = (fCurState * 2) + ((delta < 0) ? 0 : 1);
			if (temp >= fAccumulatedMM.pij.length) {
				temp -= fAccumulatedMM.pij.length;
			}
			fCurState = temp;
		}

		/**
		 * expands the markov chain starting from the last price (exclusive) to
		 * the last price, for a certain number of tick
		 * 
		 * @param lastPrice
		 *            the last price (divided by the tick)
		 * @param numTick
		 *            the number of Ticks which are used in this method.
		 * @param numPrices
		 *            how many prices to use
		 * @return
		 */
		public int expandMarkovChain(int lastPrice, int endPrice, int aNumPrices) {

			final double xp = _xp;
			final double dp = _dp;

			int delta = Math.abs(endPrice - lastPrice);

			int numPrices = aNumPrices;

			boolean odd_delta = (delta % 2 != 0);
			boolean odd_num_ticks = (numPrices % 2 != 0);

			boolean up = endPrice > lastPrice;

			int high = up ? endPrice : lastPrice;
			int low = up ? lastPrice : endPrice;

			if (odd_delta ^ odd_num_ticks) {
				if (fRandom.nextBoolean()) {
					numPrices--;
				} else {
					numPrices++;
				}
			}

			if (delta >= numPrices) {

				int gap = up ? 1 : -1;

				int prev = lastPrice;
				for (int i = 0; i < numPrices; ++i) {
					fBuiltPrices[i] = (prev + gap);
					prev = fBuiltPrices[i];
				}

			} else {

				MarkovMatrix mm = getNormalizedMM();

				int idx = 0;
				int prev = lastPrice;

				double delta_box = (endPrice - lastPrice);

				double scale_tick = 1.0 / numPrices;
				double scale_delta_box = 1.0 / delta_box;

				double m_up;
				double m_down;

				double omega = Math.PI / 4.0; // by definition.

				double yp = xp * -1 + 1;
				yp -= dp;

				// by definition
				m_up = yp / xp;

				double beta = Math.atan(m_up);
				if (beta >= (2 * omega)) {
					beta = (2 * omega);
				}

				double gamma = 2 * omega - beta;

				m_down = Math.tan(gamma);

				// then the intercept
				double dqy;

				dqy = dp; // by definition... because the height of the box is
							// 1!

				double dqx = (1 - dqy) / m_up;

				// These are the intercepts of the lines of the barrier
				double intercepts[] = new double[4];
				double m_s[] = new double[4];

				intercepts[0] = dqy; // this by definition
				intercepts[1] = -m_down * dqy;
				intercepts[2] = (1 - dqx);
				intercepts[3] = -m_up + 1 - dqy;

				m_s[0] = m_up;
				m_s[1] = m_down;
				m_s[2] = m_down;
				m_s[3] = m_up;

				double y_test[] = new double[4];

				for (;; ++idx) {
					short to_go = (short) (Math.abs(endPrice - prev));
					int left_ticks = numPrices - idx;

					if (left_ticks <= to_go) {
						break;
					}

					int gap;
					if (prev == high) {
						// I MUST go down, no exceptions
						gap = -1;
					} else if (prev == low) {
						// I MUST go up, no exceptions
						gap = 1;
					} else {
						// I can choose
						gap = get_next_delta_markov_unbounded(mm);

						if (Math.abs(endPrice - lastPrice) > 20) {

							// but I must stay inside xp and dp
							// is the price inside the barrier?
							for (int i = 0; i < 4; ++i) {
								y_test[i] = m_s[i] * (idx * scale_tick)
										+ intercepts[i];
							}

							double testing_y = ((prev + gap) - lastPrice)
									* scale_delta_box;

							if ((testing_y > y_test[1])
									&& (testing_y < y_test[0])
									&& (testing_y > y_test[3])
									&& (testing_y < y_test[2])) {
								// all ok
							} else {
								// I have to check...
								double normal_y = idx * scale_tick; // this is
																	// the
								// diagonal
								double testing_reverse_y = ((prev - gap) - lastPrice)
										* scale_delta_box;

								if (Math.abs(normal_y - testing_reverse_y) < Math
										.abs(normal_y - testing_y)) {
									gap *= -1;
								}
							}
						}

					}

					advanceMarkovState(gap);

					int builtPrice = (prev + gap);
					fBuiltPrices[idx] = builtPrice;
					prev = builtPrice;

				}

				// manual filling of the array.
				int gap = (up ? 1 : -1);
				for (int i = idx; i < numPrices; ++i) {
					fBuiltPrices[i] = fBuiltPrices[i - 1] + gap;
					advanceMarkovState(gap);
				}
			}

			return numPrices;
		}

		/**
		 * A very very simple binary conversion..., I have a vector of booleans
		 * and I return the integer which is the binary conversion of this
		 * boolean vector.
		 */
		private int get_class_order() {
			int out = 0;

			for (int i = 0; i < fBv.length; ++i) {
				out = (out * 2) + (fBv[i] == true ? 1 : 0);
			}

			return out;
		}

		int get_next_delta_markov_unbounded(MarkovMatrix mm) {
			if (mm.pij[fCurState][0] > fRandom.nextDouble()) {
				return -1;
			}
			return 1;
		}

		/**
		 * @return the normalized Markov. This is then used to fill the gap,
		 *         because the normalized mm is used by the expand range
		 *         function (this is a method that can be obsoleted, if we put
		 *         the expandRange here.
		 */
		private MarkovMatrix getNormalizedMM() {
			MarkovMatrix mm = new MarkovMatrix();

			mm.pij = new double[this.fAccumulatedMM.pij.length][2];

			for (int i = 0; i < mm.pij.length; ++i) {
				int tot = this.fAccumulatedMM.pij[i][0]
						+ this.fAccumulatedMM.pij[i][1];
				double p;
				if (tot == 0) {
					p = 0.5;
				} else {
					p = (double) this.fAccumulatedMM.pij[i][0] / (double) tot;
				}
				mm.pij[i][0] = p;
				mm.pij[i][1] = (1 - p);

			}

			return mm;
		}

		public void reset() {
			fCurState = -1;

			// I create the real rank as 2^num_markov_lookback, as this is
			// the state which we will use.
			int real_rank = 1;
			for (int i = 0; i < fRank; ++i) {
				real_rank *= 2;
			}

			fAccumulatedMM.pij = new int[real_rank][2]; // the out states are
			// ALWAYS two.
			fBv = new boolean[fRank];
		}

	}

	/**
	 * This is a simple markov matrix to store the markov probabilities
	 * estimated by the random process 1p.
	 * 
	 * It is like a typedef.
	 */
	public static final class MarkovMatrix {
		// This is the simple matrix.
		public double pij[][];
	}

	/**
	 * The machine needs this matrix to fill the gaps. This is the "normal"
	 * markov matrix. The rows are 2^rank of the matrix. The colums are always
	 * two, they simply store the occurrences of each (next) state.
	 * <p>
	 * For example, for rank 3 we have 8 rows. We encode the prices as binary
	 * digits, for example 010 means: the price went down, then up, then down.
	 * What is the probability of a new up? This is recorded in the pij[2][1], 2
	 * because 010 is 2 in decimal and 1 because we look at the second column
	 * (the first sees the fact that the price will go down).
	 * 
	 */
	private static class MarkovMatrixInt {
		public int pij[][];

		public MarkovMatrixInt() {
		}
	}

	/**
	 * signals the end of price in the returned array.
	 */
	public static final int END_OF_PRICE = -1;

	/**
	 * This is the initial circular buffer.
	 */
	private static final int MIN_CIRCULAR = 500;

	/**
	 * This is the final circular buffer (the maximum dimension)
	 */
	private static final int MAX_CIRCULAR = 5000;

	/**
	 * This is the markov lookback used for the markov chain.
	 */
	private static final int MARKOV_LOOKBACK = 6;

	/**
	 * The starting array which is used to
	 */
	private static final int START_BUILT_ARRAY = 5;

	private static final int INCREMENTAL_CIRCULAR = 300;

	/**
	 * The value of the tick, all the prices inside the filter are divided by
	 * this amount, in this case the interpretation of the gaps is easier, a gap
	 * of 2 is always a gap of 2 "ticks".
	 */
	private final int fTick;
	int fCircPrices[];

	/**
	 * these are the prices which are saved during the period, because in this
	 * way the filter will use the old period to compute the statistics.
	 */
	// int fLingeringPrices[];
	long fCircTimes[];
	// long fLingeringTimes[];

	/**
	 * This is the cursor in the circular buffer. The cursor points always to
	 * the next free space in the circular buffer.
	 */
	CircularInteger fCursor;

	private final AccumulatedMarkovMatrix fAmm = new AccumulatedMarkovMatrix(
			MARKOV_LOOKBACK);
	/**
	 * These are the built prices.
	 */
	int[] fBuiltPrices = new int[START_BUILT_ARRAY];

	long[] fBuiltTimes = new long[START_BUILT_ARRAY];

	Random fRandom;

	private boolean fIsReady = false;

	/**
	 * If true means that we use the {@linkplain SlidingWindowComputer} to
	 * compute the gaps
	 */
	private final boolean fUseWindow;

	private double fPricesForGapTwo = 3;

	private double fGapMultiplier = 4;

	final double _xp;

	final double _dp;

	/**
	 * The last price seen by this filter. It is divided by the {@link #fTick}
	 * value.
	 */
	private int fLastPrice;

	private long fLastTime;

	private long _lastInputTime = Long.MIN_VALUE;

	private long _lastOutputTime = Long.MIN_VALUE;

	private final long _seed;

	/**
	 * Usually a filter, after it has filled a gap, will continue to update the
	 * circular prices in order to update the gap statistics. In case of a
	 * "frozen" filter, however, the prices are not updated any more.
	 */
	private boolean _frozen;

	private int _frozenLastPrice;

	private long _frozenLastTime;

	private long _frozenInputTime;

	private long _frozenLastOutputTime;

	private boolean _wasReady;

	public FillGapsMachine(int tick, int i, boolean b) {
		this(tick, i, b, 0.25, 0.25);
	}

	/**
	 * builds a fill gaps machine.
	 * <p>
	 * The fill gap machine is a state machine used to build fake ticks which
	 * will fill the gaps.
	 * 
	 * <p>
	 * We need the contract tick because the ticks are built using the tick
	 * size.
	 * <p>
	 * The seed is used to have repeatable tick filling
	 * 
	 * @param contractTick
	 * @param seed
	 * 
	 * @param useWindow
	 *            true if we want to use the {@linkplain SlidingWindowComputer}
	 *            object to estimate the number of prices needed to fill a gap.
	 *            False if we simple use a linear estimation.
	 */
	@SuppressWarnings("boxing")
	public FillGapsMachine(int contractTick, long seed, boolean useWindow,
			double aXp, double aDp) {
		debug_var(738105, "Fill gaps machine created with tick ", contractTick,
				" and seed ", seed, " use window ", useWindow);
		_seed = seed;
		fUseWindow = useWindow;
		fTick = contractTick;
		_xp = aXp;
		_dp = aDp;
		reset();

	}

	/**
	 * resets the fill gaps machine: this means that the machine returns to
	 * state zero: this is used to
	 */
	private void reset() {

		fCursor = new CircularInteger(0, MIN_CIRCULAR);
		fRandom = new Random(_seed);

		fCircPrices = new int[MIN_CIRCULAR];
		fCircTimes = new long[MIN_CIRCULAR];
		Arrays.fill(fCircPrices, 0);
		Arrays.fill(fCircTimes, 0);

		// fLingeringPrices = null;
		// fLingeringTimes = null;

		fAmm.reset();

		fBuiltPrices = new int[START_BUILT_ARRAY];
		fBuiltTimes = new long[START_BUILT_ARRAY];

		fRandom = new Random(_seed);

		fIsReady = false;
		// fLingeringIndex = 0;

		_lastInputTime = Long.MIN_VALUE;
		_lastOutputTime = Long.MIN_VALUE;
	}

	/**
	 * accepts a tick and returns an array list of ticks that fill the gap (if
	 * present).
	 * 
	 * <p>
	 * Precondition: the ticks must come monotonically in time increasing. No
	 * equal time ticks are allowed.
	 * 
	 * @param tk
	 *            the incoming tick.
	 * 
	 * @return an array list of built ticks, possibly the built ticks array is
	 *         of size one, and contains only the input tick (but it is another
	 *         object, because it is built on the fly).
	 * 
	 */
	public ArrayList<RealTick> accept(Tick tk) {

		int volumeInput = tk.getVolume();

		// coherence in the input... start
		if (tk.getPhysicalTime() <= _lastInputTime) {
			throw new IllegalArgumentException("fgm invalid input rec.ed " + tk
					+ " instead my last input time is "
					+ new Date(_lastInputTime));
		}
		_lastInputTime = tk.getPhysicalTime();
		// coherence in the input end.

		int res[] = this.acceptPrice(tk.getPrice(), tk.getPhysicalTime());
		ArrayList<RealTick> ans = new ArrayList<>();
		if (res == null) { // the input tick is the only tick, it is real, I
							// have only to massage the output time
			RealTick rt = new RealTick(tk, true);
			rt.setPhysicalTime(Math.max(rt.getPhysicalTime(),
					_lastOutputTime + 1));
			rt.setVolume(volumeInput); // volume coherence
			ans.add(rt);
			_lastOutputTime = rt.getPhysicalTime();
			return ans;
		}
		int numTicks = -1;
		while (res[++numTicks] != FillGapsMachine.END_OF_PRICE) {
			// nothing, just to know where to stop, I need the numTicks value
			// before
		}

		int volumeShared = volumeInput / numTicks; // integer division.

		// coherence in output
		long offset = Math.max(0, _lastOutputTime - fBuiltTimes[0] + 1);

		for (int ii = 0; ii < numTicks; ++ii) {
			RealTick newHead = new RealTick(fBuiltTimes[ii] + offset, res[ii],
					ii == numTicks - 1 ? true : false);
			/*
			 * The volume at the last tick includes the remainder, to make the
			 * sum faithful.
			 */
			newHead.setVolume(ii == numTicks ? volumeShared + volumeInput
					% numTicks : volumeShared);
			ans.add(newHead);
		}

		_lastOutputTime = ans.get(ans.size() - 1).getPhysicalTime();

		return ans;

	}

	/**
	 * This method accepts a new price. And I simply have to add it to the
	 * markov chain and update the circular buffer.
	 * 
	 * <p>
	 * this is public but <b>it is not meant to be called directly</b> just
	 * during warm up, this method should be called warm up filter...
	 * 
	 * @param aPrice
	 *            the price (divided already by the number of ticks). *
	 */
	public void accumulatePrice(int aPrice, long aTime) {

		if (_frozen || fIsReady) {
			return;
		}

		fLastPrice = aPrice;
		fLastTime = aTime;

		// I have to store it in the circular buffer.
		// The cursor points always to the next free position
		fCircPrices[fCursor.get()] = aPrice;
		fCircTimes[fCursor.get()] = aTime;

		if (fCursor.get() == (fCircPrices.length - 1)
				&& fCircPrices.length < MAX_CIRCULAR) {
			// Ok, I can grow the circular buffer
			fCircPrices = Arrays.copyOf(fCircPrices, fCircPrices.length
					+ INCREMENTAL_CIRCULAR);
			fCircTimes = Arrays.copyOf(fCircTimes, fCircTimes.length
					+ INCREMENTAL_CIRCULAR);
			// fIsReady = true;
		}

		// In any case I accumulate the cursor
		fCursor.plusPlus(fCircPrices.length);

		// And then I ask the markov matrix to accumulate
		// if (!fIsReady)
		fAmm.accumulate();

	}

	/**
	 * accepts a price and updates the internal structures of the markov matrix
	 * 
	 * @param price
	 *            The price to be accepted.
	 * @param time
	 * 
	 * @return the prices which are the filling of the last gap, null if the
	 *         previous acceptPrice there was no gap.
	 * 
	 */
	public int[] acceptPrice(long price, long time) {

		// first of all the price is divided by the tick
		int curPrice = (int) (price / fTick);

		/*
		 * last price could be -1 if the filter has not yet received a price
		 * from the outside
		 */
		if (fLastPrice != -1 && (fCursor.get() != 0 || fIsReady)) {

			int gap = Math.abs(fLastPrice - curPrice);
			if (gap > 1) {
				// Ok there is a gap.
				int numPrices;
				int numPricesH;

				if (fIsReady) {

					if (fUseWindow) {
						SlidingWindowComputer swc = new SlidingWindowComputer(
								fRandom.nextLong());
						try {
							numPricesH = (int) (swc.estimateGap(fCircPrices,
									gap));
						} catch (Exception e) {
							// I fallback to the linear estimator
							numPricesH = (int) (((gap - 2) * fGapMultiplier) + fPricesForGapTwo);
						}
					} else {
						numPricesH = (int) (((gap - 2) * fGapMultiplier) + fPricesForGapTwo);
					}

					numPrices = numPricesH;

					if (numPrices < gap) {
						numPrices = gap;
					}
					ensureBuiltPricesCapacity(numPrices);

					int newNumPrices = fAmm.expandMarkovChain(fLastPrice,
							curPrice, numPrices);
					numPrices = newNumPrices;

				} else { // not ready
					// I simply fill the gap linearly.
					numPrices = gap;
					ensureBuiltPricesCapacity(numPrices);

					int delta = curPrice - fLastPrice > 0 ? 1 : -1;
					for (int i = 0; i < numPrices; ++i) {
						fLastPrice += delta;
						fBuiltPrices[i] = fLastPrice;
					}
				}
				if (fBuiltPrices[numPrices - 1] != curPrice) {
					throw new IllegalStateException();
				}

				/* I ALWAYS fill linearly the time */
				long deltaT = Math.max((time - fLastTime) / numPrices, 1);
				for (int i = 0; i < numPrices; ++i) {
					fLastTime += deltaT;
					fBuiltTimes[i] = fLastTime;
				}

				assert (fBuiltPrices[numPrices - 1] == curPrice) : " end price built "
						+ fBuiltPrices[numPrices - 1]
						+ " cur price "
						+ curPrice + " gap " + gap;
				fBuiltPrices[numPrices] = END_OF_PRICE;

				fLastPrice = fBuiltPrices[numPrices - 1];
				fLastTime = fBuiltTimes[numPrices - 1];

				for (int i = 0; i < numPrices; ++i) {

					accumulatePrice(fBuiltPrices[i], fBuiltTimes[i]);

					// In any case I scale back the price, to be consumed by the
					// outside.
					fBuiltPrices[i] *= fTick;
				}

				return fBuiltPrices;
			} // end if gap > 1

		}

		// no gap, or the filter is not ready, store the last price!
		fLastPrice = curPrice;
		fLastTime = time;

		accumulatePrice(curPrice, time);

		// no gap
		return null;

	}

	/**
	 * This is a very technical method which purpose is to make the fill gap
	 * machine in synch with the last tick sent to the outside.
	 * 
	 * <p>
	 * At time of writing this method is only used by the {@link CacheExpander}
	 * class after the normal expansion of the slots; maybe it is not useful in
	 * any other place (this may be a sign that the {@link FillGapsMachine} and
	 * the {@link CacheExpander} are linked in some way).
	 * 
	 * @param realTick
	 */
	public void syncAtEndOfExpansion(RealTick realTick) {
		fLastPrice = realTick.getPrice() / fTick;
		fLastTime = realTick.getPhysicalTime();
	}

	public void endOfWarmUpFilter() {

		int prevIndex = fCursor.subtract(1, fCircPrices.length);
		fLastPrice = -1; // the warm up has ended, but the first price is not
							// valid to consider the gap
		fLastTime = fCircTimes[prevIndex];
		fIsReady = true;
	}

	private void ensureBuiltPricesCapacity(int numPrices) {
		if (fBuiltPrices.length < (numPrices + 10)) {
			fBuiltPrices = new int[numPrices + 10];
			fBuiltTimes = new long[numPrices + 10];
		}

	}

	public long[] getBuiltTimes() {
		return fBuiltTimes;
	}

	public boolean isReady() {
		return fIsReady;
	}

	/**
	 * Sets the parameters for the fill gap machine.
	 * <p>
	 * The setting of the parameters have no effect if you have create the
	 * object with the {@link #fUseWindow} boolean parameter set.
	 * 
	 * 
	 * 
	 * @param priceGapForTwo
	 *            number of prices used to fill a gap of two.
	 * 
	 * @param gapMultiplier
	 *            multiplier that adds a certain number of prices to any gap.
	 */
	public void setParameters(double priceGapForTwo, double gapMultiplier) {
		fPricesForGapTwo = priceGapForTwo;
		fGapMultiplier = gapMultiplier;
	}

	public void startWarmUpFilter() {
		// nothing
	}

	/**
	 * A frozen fill gaps machine will compute the gaps but it will not update
	 * the circular buffer any more.
	 */
	public void freeze() {
		_frozen = true;
		/*
		 * Do not consider last times, because I will go backward.
		 */
		_frozenLastPrice = fLastPrice;
		_frozenLastTime = fLastTime;
		_frozenInputTime = _lastInputTime;
		_frozenLastOutputTime = _lastOutputTime;
		fLastPrice = -1;
		fLastTime = -1;
		_lastInputTime = -1;
		_lastOutputTime = -1;
		_wasReady = fIsReady;
		// a frozen filter is ready by definition.
		fIsReady = true;
	}

	public void thaw() {
		assert (_frozen);
		_frozen = false;
		fLastPrice = _frozenLastPrice;
		fLastTime = _frozenLastTime;
		_lastInputTime = _frozenInputTime;
		_lastOutputTime = _frozenLastOutputTime;
		fIsReady = _wasReady;
	}

	/**
	 * When a lower expander does finish the expansion the learning filter
	 * becomes my filter, but it has to update the last input time.
	 * 
	 * @param _fgm
	 *            the old filter
	 */
	public void catchUpWithFilter(FillGapsMachine _fgm) {
		/*
		 * If these assert fails than the learning filter has not been updated.
		 */
		assert (fLastPrice == _fgm.fLastPrice);
		assert (fLastTime == _fgm.fLastTime);
		_lastInputTime = _fgm._lastInputTime;
		_lastOutputTime = _fgm._lastOutputTime;
	}

	/**
	 * A utility method used only in the {@link CacheExpander}, because the
	 * filter is a learning filter which is now a "good" filter, but the
	 * pipeline has been emptied, because the warm up is simulated (it is a
	 * database request). So I do not call the {@link #endOfWarmUpFilter()},
	 * because it also resets the field {@link #fLastPrice}, and this is not
	 * correct.
	 */
	public void forceEndWarmUp() {
		assert (!fIsReady);
		fIsReady = true;
	}

}
