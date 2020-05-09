package com.mfg.tea.accounting;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.mfg.tea.conn.ISingleAccountStatistics;

/**
 * An equity can be concrete, based on real transactions, or composite, like the
 * sum of other equity lines.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class BaseEquity implements ISingleAccountStatistics {

	/*
	 * All the basic equities share the possibility to have a minimum and
	 * maximum equities.
	 */

	/**
	 * This is the absolute maximum value of the equity during its history. The
	 * current drawdown is defined as {@code _maxEquity - _equity}, this
	 * quantity is at most zero, if we are current on the top of the equity
	 * line.
	 */
	protected long _maxEquity = 0;

	/**
	 * The maximum drawdown is kept here because it relates to the history of
	 * the equity.
	 */
	protected long _maxDrawdown = 0;

	/*
	 * Bean related things
	 */
	protected final PropertyChangeSupport _propSupport = new PropertyChangeSupport(
			this);

	protected StockInfo _stockInfo = null;

	/**
	 * While the sum of gains and sum of losses do not depend on the order in
	 * which they are done, the equity is instead a quantity which depends on
	 * the absolute order of trades.
	 * 
	 * @param aEquity
	 */
	protected void _updateMinMaxEquity(long aEquity) {

		if (aEquity > _maxEquity) {
			_maxEquity = aEquity;
		} else {
			long curDrawdown = _maxEquity - aEquity;
			if (curDrawdown > _maxDrawdown) {
				_maxDrawdown = curDrawdown;
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this._propSupport.addPropertyChangeListener(listener);
	}

	@Override
	public final long getCurrentDrawDownClosedEquity() {
		return _maxEquity - getEquity();
	}

	@Override
	public final long getCurrentDrawDownClosedEquityPoints() {
		return _stockInfo.convertToPoints(getCurrentDrawDownClosedEquity());
	}

	@Override
	public final long getGainInPoints() {
		return _stockInfo.convertToPoints(getGain());
	}

	@Override
	public final long getMaxDrawDownClosedEquity() {
		return _maxDrawdown;
	}

	@Override
	public final long getMaxDrawDownClosedEquityPoints() {
		return _stockInfo.convertToPoints(_maxDrawdown);
	}

	@Override
	public long getOpenEquityMoney() {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method is simple an alias for the {@link #getEquity()}
	 */
	@Override
	public final double getTotalProfitLossMoney() {
		return getEquity();
	}

	/*
	 * End of bean related things.
	 */

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this._propSupport.removePropertyChangeListener(listener);
	}
}
