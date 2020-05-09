package com.mfg.dm.filters;

import com.mfg.common.RealTick;
import com.mfg.dm.FillGapsMachine;
import com.mfg.utils.FinancialMath;
import com.mfg.utils.U;

/**
 * A filter that ensures that all ticks come:
 * <ul>
 * <li>in strict monotonically ascending order
 * <li>divided by, at most, one tick
 * </ul>
 * 
 * <p>
 * This filter is used by the cache expander, and it is the base for the two
 * filters that come after, the {@link FinalNotFinalClassificator} and the
 * {@link OneBarHistoricalFilter}.
 * 
 * <p>
 * This base filter is able to distinguish a tick giving to it a "color", which
 * is a very simple classification, based on the previous tick.
 * 
 * <p>
 * Every tick, except obviously the first, can be Up or Down with respect to the
 * previous one, and this is its color.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class OneTickAscendingFilter {

	/**
	 * The tick size of this filter.
	 */
	protected final int _tick;

	/**
	 * This is only used to check that we are receiving prices in order and at
	 * one tick distance.
	 */
	private RealTick _lastTick = null;

	/**
	 * all the filters, in theory, must handle these input symbols.
	 * 
	 * Up and down are univocally defined, because we accept only one tick
	 * distance
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	protected enum EInputSymbol {
		UNDECIDED, TICK_UP, TICK_DOWN
	}

	protected OneTickAscendingFilter(int aTick) {
		_tick = aTick;
	}

	/**
	 * Asserts the validity of this tick for the filter, and classifies it using
	 * the {@link EInputSymbol} enumeration
	 * 
	 * <p>
	 * The tick must come in strict monotonically chronological ascending order,
	 * and with one tick distance
	 * 
	 * <p>
	 * This is the precondition of all the filters below the
	 * {@link FillGapsMachine}, it wants a strong monotonically ascending
	 * sequence (in times) and it wants also ticks with only one tick distance,
	 * because this filter <b>happens after</b> the fill gaps machine.
	 * 
	 * @param aTick
	 *            the tick to validate
	 * 
	 * @return the classification for this tick
	 * 
	 * @throws IllegalArgumentException
	 *             if this tick is not valid
	 */
	protected EInputSymbol _classifyInputTick(RealTick aTick) {
		EInputSymbol resRet;
		boolean res = true;

		if (_lastTick != null) {
			if (!aTick.happensAfter(_lastTick)) {
				U.debug_var(391932, "Invalid backward tick ", aTick,
						" happens not after ", _lastTick);
				res = false;
			}

			if (res
					&& FinancialMath.getExactDeltaTicks(aTick.getPrice(),
							_lastTick.getPrice(), _tick) != 1) {
				U.debug_var(819221, "BAD TICK ", aTick, " my last tick was ",
						_lastTick);
				res = false;
			}

			if (_lastTick.getPrice() > aTick.getPrice()) {
				resRet = EInputSymbol.TICK_DOWN;
			} else {
				resRet = EInputSymbol.TICK_UP;
			}

		} else {
			resRet = EInputSymbol.UNDECIDED;
		}
		if (!res) {
			throw new IllegalArgumentException();
		}

		_lastTick = aTick.clone();

		return resRet;
	}

	protected boolean _isCoherent(int aPrice) {
		if (_lastTick == null)
			return true;
		return aPrice == _lastTick.getPrice();
	}

	/**
	 * Returns the last tick seen by this filter, used to realign the pipeline
	 * (usually passing from warm up to real time).
	 * 
	 * @return the last tick seen by this filter (a clone, so it is safe to
	 *         modify it).
	 */
	public RealTick getLastTick() {
		return _lastTick.clone();
	}

}
