package com.mfg.tea.accounting;

import com.mfg.tea.conn.IAccountStatistics;
import com.mfg.tea.conn.IAccountStatisticsMoney;

/**
 * This is a "leaf" class, it is tied to a {@link SingleInventory}, because it
 * computes the quantities related to the equity (the historical movement of the
 * <b>value</b> of an inventory, or, better, of the <b>deltas</b> of this value,
 * because this object is updated only when there is a diminuition of the
 * quantity of the inventory).
 * 
 * <p>
 * Remember that for short inventories the "diminuition" for the inventory is
 * actually a raise in the quantity, because we are "less" negative.
 * 
 * <p>
 * The unit of measure for an equity are "cents" of a dollar, or whatever the
 * currency is. We have <b>not</b> here a measure of ticks or points, because an
 * equity could be a mixture of equities of different symbols and each symbol
 * could have a different tick size and tick value. The only common denominator
 * for equities is the "money", which is the unit of measure for equities.
 * 
 * <p>
 * Only a <b>single</b> equity (an equity of a {@link SingleInventory}), may
 * have the value in ticks and in points, or an equity of a
 * {@link HomogeneusInventoriesFolder}... because they have the same "material".
 * 
 * 
 * <p>
 * The equity is a single valued function which is defined by a tabular
 * representation of points (t,e) where t is a time and e is the equity at that
 * moment. Equity is the closed value equity or the open value equity, they are
 * the same when there is not an opened position, but usually they differ.
 * 
 * <p>
 * The simple equity extends the {@link IAccountStatistics} interface, not only
 * the {@link IAccountStatisticsMoney} because by definition a simple equity is
 * composed of only one material so it can also compute the points and tick
 * values of the equity itself.
 * 
 * <p>
 * This class is a bean and it supports the bean property notification.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class SimpleEquity extends BaseEquity {

	/**
	 * This is the sum of all the gains, every time the user makes a winning
	 * trade I put here the amount won.
	 */
	private long _sumOfGains = 0;

	/**
	 * Here it is the absolute value of all the losses.
	 * 
	 * <p>
	 * It is an identity {@code equity = _sumOfGain - _sumOfLosses}
	 */
	private long _sumOfLosses = 0;

	/**
	 * Every simple equity is connected to a simple inventory.
	 */
	private final SingleInventory _inventory;

	private DuplexEquity _duplex;

	/**
	 *
	 */
	public SimpleEquity(SingleInventory aInventory) {
		_inventory = aInventory;
		_stockInfo = aInventory._info;
	}

	// @Override
	// public void dump() {
	// // TODO Auto-generated method stub
	//
	// }

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
	public long getEquity() {
		return _sumOfGains - _sumOfLosses;
	}

	@Override
	public int getEquityTicks() {
		return _stockInfo.convertToTicks(getEquity());
	}

	@Override
	public long getGain() {
		return _sumOfGains;
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
		return _sumOfLosses;
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
	public long getOpenEquityMoney() {
		return getEquity() + _inventory.getUnrealizedPL();
	}

	@Override
	public long getPoints() {
		return _stockInfo.convertToPoints(getEquity());
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
	public int getQuantity() {
		return _inventory.getQuantity();
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

	/**
	 * the normal method to update an equity account. It gives a point in time
	 * and a delta for the equity. The delta has the correct sign, short
	 * equities need not to concern about the sign of the delta.
	 * 
	 * <p>
	 * The delta is actually the difference in the value of the
	 * {@link SingleInventory} when a trade is performed which <b>tends to
	 * close</b> the positions.
	 * 
	 * <p>
	 * When the agent opens a position the closed equity does not change,
	 * because it is assumed that the agent has the capital to open that
	 * position and to maintain all the positions opened.
	 * 
	 * @param time
	 *            the time for the new equity point.
	 * 
	 * @param delta
	 *            the difference in the equity, that is the profit (positive) or
	 *            loss (negative) or the last trade.
	 */
	void registerDeltaPoints(long time, long deltaPoints) {

		// the check for tick size coherence is done in the DuplexInventory
		// class, if the delta is zero nothing is to be registered here.
		if (deltaPoints == 0) {
			return;
		}

		/*
		 * if you get a npe that means that you are mixing different materials
		 * or trying to update an equity in points, but this equity is mixed.
		 * That is not good.
		 */
		long deltaMoney = _stockInfo.convertToPrice(deltaPoints);

		if (deltaPoints > 0) {
			_sumOfGains += deltaMoney;
		} else {
			_sumOfLosses += deltaMoney * -1;
		}

		long curEquity = _sumOfGains - _sumOfLosses;

		_updateMinMaxEquity(curEquity);

		if (_duplex != null)
			_duplex.equityChanged();

		_propSupport.firePropertyChange(
				IAccountStatisticsMoney.TOTAL_PROFIT_LOSS_MONEY, true, false);
	}

	/**
	 * links the given duplex equity to this simple equity. When this simple
	 * equity is updated it updates also the duplex equity chained.
	 * 
	 * @param duplexEquity
	 */
	void linkDuplexEquity(DuplexEquity duplexEquity) {
		_duplex = duplexEquity;
	}
}
