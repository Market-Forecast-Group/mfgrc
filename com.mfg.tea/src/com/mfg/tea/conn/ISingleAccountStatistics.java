package com.mfg.tea.conn;

/**
 * This interface lists all the methods which are necessary for a single
 * account, or for a set of single accounts which are homogeneous.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ISingleAccountStatistics extends IAccountStatisticsMoney,
		IHomogeneousStatistics {

	public double getAvgProfitForWinningTradedSizesPoints();

	/**
	 * @return the current drawdown in points.
	 */
	public long getCurrentDrawDownClosedEquityPoints();

	public int getEquityTicks();

	public long getGainInPoints();

	public long getLosingTradedSizesPoints();

	public long getLossInPoints();

	/**
	 * 
	 * @return the maximum drawdown possible of this actor.
	 */
	public long getMaxDrawDownClosedEquityPoints();

	// ///////////////////////////////////////////////// end of original
	// interface

	/*
	 * required for the bean, they are the quantities which are asked by the
	 * panel using reflection.
	 */

	/*
	 * Here I will put all the quantities which are observed by the external
	 * world about the account.
	 * 
	 * The properties are observed in the java sense, which means that the
	 * account is like a Java bean.
	 */

	public long getPoints();

	public long getProfitableTradedSizesPoints();

	public long getTotalProfitLossPoints();

}
