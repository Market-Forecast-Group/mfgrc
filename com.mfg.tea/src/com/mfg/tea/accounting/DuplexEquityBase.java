package com.mfg.tea.accounting;

import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * This is the base class of all the equities that have the possibility to have
 * a long and a short view.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class DuplexEquityBase extends BaseEquity implements
		IDuplexStatisticsMoney {

	/**
	 * The merger is able to filter the equities... or the equity merger is only
	 * able to merge one kind of stats: to be defined.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	enum EFilterMode {
		ONLY_LONG, ONLY_SHORT, LONG_AND_SHORT
	}

	/**
	 * 
	 * @param aFilter
	 *            the filter which identifies the "mode" used to return the
	 *            equity.
	 * @return the equity filtered by the mode.
	 */
	protected abstract long _getEquity(EFilterMode aFilter);

	protected abstract long _getGain(EFilterMode aFilter);

	protected abstract int _getQuantity(EFilterMode aFilterMode);

	@Override
	public final long getEquity() {
		return _getEquity(EFilterMode.LONG_AND_SHORT);
	}

	@Override
	public final long getGain() {
		return _getGain(EFilterMode.LONG_AND_SHORT);
	}

	//

	@Override
	public final int getQuantity() {
		return _getQuantity(EFilterMode.LONG_AND_SHORT);
	}
}
