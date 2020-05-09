package com.mfg.dfs.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Stores the data relative to a crossover;
 * 
 * <p>
 * It is a package private class, useful only for the classes
 * {@link ContinuousData} and {@link ContinuousTable}
 * 
 * 
 * <pre>
 * price offset...
 * 
 * p1 p2 p3
 * 
 * when a new maturity arrives with price offset p4 we will store
 * 
 * (p1+p4) (p2+p4) (p3+p4) p4
 * 
 * But p2 is already p2' + p3' (where the apex is intended to be the "original").
 * 
 * so in reality we have
 * 
 * (p1'+p2'+p3'+p4') (p2'+p3'+p4') (p3'+p4') (p4')
 * 
 * If I want to know p1' (the original) I simply have to substract p1Tot-p2Tot
 * 
 * So I can simply have one offset for all.
 * 
 * </pre>
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class CrossoverData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2916551287755008742L;

	/**
	 * 
	 * @param crossOverDate
	 * @param losingMaturity
	 * @param winnerMaturity
	 * @param beginningOffset
	 */
	public CrossoverData(long crossOverDate, MaturityData losingMaturity,
			MaturityData winnerMaturity, int beginningOffset) {
		crossDate = crossOverDate;
		oldMaturity = losingMaturity;
		newMaturity = winnerMaturity;
		_priceOffset = beginningOffset;
	}

	/**
	 * this is the starting date for the crossover: it is the "next day" after
	 * the actual passing, for example if the xover has been on July 1st, this
	 * date is July 2nd
	 */
	public final long crossDate;

	public final MaturityData oldMaturity;

	/**
	 * This is the new maturity that takes over
	 */
	public final MaturityData newMaturity;

	/**
	 * This is the accumulated price offset from the beginning.
	 * 
	 * <p>
	 * The price offset is always a price which transform the current maturity
	 * (the "new maturity") in a price suitable (comparable) to the <b>last</b>
	 * maturity, not the next.
	 * 
	 * <p>
	 * by definition the last chunk will have a price offset of zero, because
	 * the new maturity is the current maturity.
	 * 
	 * <p>
	 * The <b>relative</b> price offset is another story. If you would like to
	 * have the relative offset from two maturities which are inside the stream
	 * then you have to offset the old by the <i>difference</i> of the two price
	 * offsets.
	 * 
	 * <p>
	 * Usually this is not a problem, because we usually request prices for the
	 * continuous contract which is tied to the rightmost (the newest) maturity.
	 */
	private int _priceOffset;

	/**
	 * returns the price offset valid from the date of this crossover from the
	 * maturity new to the present maturity, the end of the continuous data.
	 * 
	 * @return
	 */
	public int getPriceOffset() {
		return _priceOffset;
	}

	@Override
	public String toString() {
		return "{" + new Date(crossDate) + " current " + newMaturity
				+ " supersedes " + oldMaturity + " @ OFF " + _priceOffset + "}";
	}

	/**
	 * changes the offset using the specific price amount, the amount is simply
	 * added.
	 * 
	 * <p>
	 * There is not a corresponding "set" of the offset, because we have the
	 * 
	 * @param priceOffset
	 */
	public void offsetOffset(int priceOffset) {
		_priceOffset += priceOffset;
	}
}
