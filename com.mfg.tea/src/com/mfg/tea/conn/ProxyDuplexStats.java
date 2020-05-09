package com.mfg.tea.conn;

/**
 * This class is a proxy view of the duplex statistics and it is kept updated by
 * the {@link ProxyVirtualBroker} using the method
 * {@link IVirtualBroker#watchAccountStats()}.
 * 
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class ProxyDuplexStats extends PropertySupportStats {

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
	public long getCurrentDrawDownClosedEquity() {
		return 99;
	}

	@Override
	public long getCurrentDrawDownClosedEquityPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEquityTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGain() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGainInPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISingleAccountStatistics getLongStatistics() {
		return this;
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
	public long getMaxDrawDownClosedEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxDrawDownClosedEquityPoints() {
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPoints() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISingleAccountStatistics getShortStatistics() {
		return this;
	}

	@Override
	public long getTimeUW() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalProfitLossMoney() {
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

}
