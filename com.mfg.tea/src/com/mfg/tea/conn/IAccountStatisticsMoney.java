package com.mfg.tea.conn;

/**
 * This interface holds the statistics only for the money part, because it refer
 * to a statistic of a mixed account which can be espressed only in money terms,
 * not in points or ticks.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IAccountStatisticsMoney {

	/**
	 * These are the strings used to name the properties which change during
	 * time. Subscribers may use these properties to subscribe to the change.
	 */
	public static final String TOTAL_PROFIT_LOSS_MONEY = "totalProfitLossMoney";

	/**
	 * This will return the equity not adjusted with the zero level.
	 */
	public double getAbsoluteEquity();

	/**
	 * @return the equity "open trade" absolute.
	 */
	public double getAbsoluteOpenEquity();

	/**
	 * @return the average losing for the losing trades.
	 */
	public double getAverageLosing();

	public double getAveragePrice();

	/**
	 * @return the average win for the winning trades
	 */
	public double getAverageWinnigs();

	public double getAvgOpenTradedSize();

	public double getAvgProfitForWinningTradedSizesMoney();

	/**
	 * 
	 * @return the current drawdown based on the closed equity.
	 */
	public long getCurrentDrawDownClosedEquity();

	/**
	 * The equity is intended as a "closed" trade equity, that is an equity
	 * which changes only when the strategy closes some positions.
	 * 
	 * <p>
	 * The equity is measured in money. Some equities allow the retrieval of the
	 * information in points or in ticks, but they need to be attached to a
	 * homogeneous inventory.
	 * 
	 * <p>
	 * The equity is defined as {@code getGain() - getLoss()}
	 * 
	 */
	public long getEquity();

	/**
	 * It returns the sum of all the trades that have won something.
	 * 
	 * <p>
	 * It turns out that the maximum equity is at most equal to this.
	 * 
	 * 
	 * @return The total gain in money.
	 */
	public long getGain();

	public double getLosingTradedSizesMoney();

	/**
	 * @return The total loss in money.
	 */
	public long getLoss();

	public long getMaxDrawdown();

	/**
	 * 
	 * @return the maximum drawdown (in money)
	 */
	public long getMaxDrawDownClosedEquity();

	/**
	 * @return the maximum number of simultaneusly opened positions.
	 */
	public int getMaxOpenedPositions();

	public int getMaxOpenTradedSizes();

	/**
	 * Returns the merit figure, which is defined as
	 * 
	 * @code (equity / (1 + under water area))
	 * 
	 *       This merit figure is filtered with the minimum number of trades
	 *       required.
	 */
	public double getMeritFigure();

	/**
	 * This is used to return the merit figure without filter, used for example
	 * in display.
	 */
	public double getMeritFigureWithoutFilter();

	public int getNumberOfLosingTradedSizes();

	/**
	 * todo: fix the spelling
	 * 
	 * @return the number of losing trades (excuse the spelling)
	 */
	public int getNumberOfLossingTrades();

	// useful for testing
	// public int getQuantity();

	public int getNumberOfTradedSizes();

	/**
	 * Returns the number of closed trades, that is the number of "pairs" (open
	 * + close).
	 */
	public int getNumberOfTrades();

	/**
	 * @return the number of winning trades (excuse the spelling)
	 */
	public int getNumberOfWiningTrades();

	public int getNumberOfWinningTradedSizes();

	/**
	 * @return the equity "open trade" relative (with the zero level)...
	 */
	// public long getOpenEquity();

	/**
	 * 
	 * @return the open equity measured in money. If there are no positions
	 *         opened than the open equity is the same as the
	 *         {@link #getEquity()}
	 */
	public long getOpenEquityMoney();

	public double getProfitableTradedSizesMoney();

	public double getProfitLossRatio();

	public long getTimeUW();

	public double getTotalProfitLossMoney();

	/**
	 * Return the underwater area, which is the area under the line of the
	 * maximum equity.
	 */
	public double getUWA();

	/**
	 * @return the ratio between winning and losing trades.
	 */
	public double getWinLossRatio();

	public double getWinningLosingTradedSizesRatio();

}
