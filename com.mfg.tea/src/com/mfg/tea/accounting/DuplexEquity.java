package com.mfg.tea.accounting;

import com.mfg.tea.conn.IDuplexStatistics;
import com.mfg.tea.conn.ISingleAccountStatistics;

public class DuplexEquity extends DuplexEquityBase implements IDuplexStatistics {

	private SimpleEquity _longEquity;
	private SimpleEquity _shortEquity;

	public DuplexEquity(StockInfo aStockInfo) {
		_stockInfo = aStockInfo;
	}

	/**
	 * Adds a single equity to this merger.
	 * 
	 * 
	 * 
	 * @param aEquity
	 * @param isLong
	 */
	void _addSimpleEquity(SimpleEquity aEquity, boolean isLong) {
		/*
		 * Chain the equity, so that I can be notified of the things which
		 * happens here.
		 */
		// aEquity.addPropertyChangeListener(this);

		if (isLong) {
			_longEquity = aEquity;
		} else {
			_shortEquity = aEquity;
		}

		aEquity.linkDuplexEquity(this);
	}

	@Override
	protected long _getEquity(EFilterMode aFilter) {
		switch (aFilter) {
		case LONG_AND_SHORT:
			return _longEquity.getEquity() + _shortEquity.getEquity();
		case ONLY_LONG:
			return _longEquity.getEquity();
		case ONLY_SHORT:
			return _shortEquity.getEquity();
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	protected int _getQuantity(EFilterMode aFilterMode) {
		switch (aFilterMode) {
		case LONG_AND_SHORT:
			return _longEquity.getQuantity() + _shortEquity.getQuantity();
		case ONLY_LONG:
			return _longEquity.getQuantity();
		case ONLY_SHORT:
			return _shortEquity.getQuantity();
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public double getAbsoluteEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAbsoluteOpenEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageLosing() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAveragePrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageWinnigs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvgOpenTradedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvgProfitForWinningTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvgProfitForWinningTradedSizesPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEquityTicks() {
		return _stockInfo.convertToTicks(getEquity());
	}

	@Override
	public ISingleAccountStatistics getLongStatistics() {
		return _longEquity;
	}

	@Override
	public double getLosingTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLosingTradedSizesPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLoss() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLossInPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxDrawdown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOpenedPositions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOpenTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMeritFigure() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMeritFigureWithoutFilter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfLosingTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfLossingTrades() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfTrades() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfWiningTrades() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfWinningTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPoints() {
		return _longEquity.getPoints() + _shortEquity.getPoints();
	}

	@Override
	public double getProfitableTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getProfitableTradedSizesPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProfitLossRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISingleAccountStatistics getShortStatistics() {
		return _shortEquity;
	}

	@Override
	public long getTimeUW() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalProfitLossPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getUWA() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWinLossRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWinningLosingTradedSizesRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	void equityChanged() {
		/*
		 * Some quantities do not need a new computation because they are
		 * computed on the fly, some of them, instead, may need a new
		 * computation, but this is handled by the computation chain. This is
		 * only the notification chain.
		 */

		long equity = getEquity();
		_updateMinMaxEquity(equity);

		_propSupport.firePropertyChange(TOTAL_PROFIT_LOSS_MONEY, true, false);
	}

	@Override
	protected long _getGain(EFilterMode aFilter) {
		/*
		 * The gain of the sum is the sum of the gain.
		 */
		switch (aFilter) {
		case LONG_AND_SHORT:
			return _longEquity.getGain() + _shortEquity.getGain();
		case ONLY_LONG:
			return _longEquity.getGain();
		case ONLY_SHORT:
			return _shortEquity.getGain();
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * @param price
	 *            this is the price used to compute the open equity.
	 */
	public void changedPrice(int price) {
		// getOpenEquityMoney();
	}

}
