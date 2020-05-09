package com.mfg.strategy;

import com.mfg.tea.conn.ISingleAccountStatistics;
import com.mfg.tea.conn.PropertySupportStats;
import com.mfg.utils.StepDefinition;

public abstract class AccountStatistics extends PropertySupportStats {

	@Override
	public ISingleAccountStatistics getShortStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISingleAccountStatistics getLongStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEquityTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAbsoluteOpenEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAbsoluteEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfTrades() {
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
	public double getUWA() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOpenedPositions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfLossingTrades() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfWiningTrades() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWinLossRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageWinnigs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageLosing() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGain() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLoss() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGainInPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLossInPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeUW() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getQuantity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAveragePrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final String AVG_OPEN_TRADED_SIZE = "avgOpenTradedSize";
	public static final String MAX_OPEN_TRADED_SIZES = "maxOpenTradedSizes";
	public static final String MAX_DRAWDOWN = "maxDrawdown";
	public static final String AVG_LOSS_FOR_LOSING_TRADED_SIZES_MONEY = "avgLossForLosingTradedSizesMoney";
	public static final String AVG_PROFIT_FOR_WINNING_TRADED_SIZES_MONEY = "avgProfitForWinningTradedSizesMoney";
	public static final String AVG_LOSS_FOR_LOSING_TRADED_SIZES_POINTS = "avgLossForLosingTradedSizesPoints";
	public static final String AVG_PROFIT_FOR_WINNING_TRADED_SIZES_POINTS = "avgProfitForWinningTradedSizesPoints";
	private static final String WINNING_LOSING_TRADED_SIZES_RATIO = "winningLosingTradedSizesRatio";
	public static final String NUMBER_OF_LOSING_TRADED_SIZES = "numberOfLosingTradedSizes";
	public static final String NUMBER_OF_WINNING_TRADED_SIZES = "numberOfWinningTradedSizes";
	private static final String NUMBER_OF_TRADED_SIZES = "numberOfTradedSizes";
	public static final String PROFIT_LOSS_RATIO = "profitLossRatio";
	public static final String LOSING_TRADED_SIZES_MONEY = "losingTradedSizesMoney";
	public static final String LOSING_TRADED_SIZES_POINTS = "losingTradedSizesPoints";
	public static final String PROFITABLE_TRADED_SIZES_MONEY = "profitableTradedSizesMoney";
	public static final String PROFITABLE_TRADED_SIZES_POINTS = "profitableTradedSizesPoints";

	public static final String TOTAL_PROFIT_LOSS_POINTS = "totalProfitLossPoints";
	private static final String TICK = "tick";
	private static final String TICK_VALUE = "tickValue";
	private double tickValue;
	private StepDefinition tick;

	private long totalProfitLossPoints;
	private double totalProfitLossMoney;
	private long profitableTradedSizesPoints;
	private double profitableTradedSizesMoney;
	private long losingTradedSizesPoints;
	private double losingTradedSizesMoney;
	private double profitLossRatio;

	private int numberOfTradedSizes;
	private int numberOfWinningTradedSizes;
	private int numberOfLosingTradedSizes;
	private double winningLosingTradedSizesRatio;

	private double avgProfitForWinningTradedSizesPoints;
	private double avgLossForLosingTradedSizesPoints;
	private double avgProfitForWinningTradedSizesMoney;
	private double avgLossForLosingTradedSizesMoney;

	private long maxDrawdown;
	private int maxOpenTradedSizes;
	private double avgOpenTradedSize;

	// / internal

	private int totalTrades;
	private int openenedTradesEvent;

	protected AccountStatistics(double aTickValue, StepDefinition aTick) {
		super();
		this.tickValue = aTickValue;
		this.tick = aTick;
	}

	public double getTickValue() {
		return tickValue;
	}

	public StepDefinition getTick() {
		return tick;
	}

	@Override
	public long getTotalProfitLossPoints() {
		return totalProfitLossPoints;
	}

	@Override
	public double getTotalProfitLossMoney() {
		return totalProfitLossMoney;
	}

	@Override
	public long getProfitableTradedSizesPoints() {
		return profitableTradedSizesPoints;
	}

	@Override
	public double getProfitableTradedSizesMoney() {
		return profitableTradedSizesMoney;
	}

	@Override
	public long getLosingTradedSizesPoints() {
		return losingTradedSizesPoints;
	}

	@Override
	public double getLosingTradedSizesMoney() {
		return losingTradedSizesMoney;
	}

	@Override
	public double getProfitLossRatio() {
		return profitLossRatio;
	}

	@Override
	public int getNumberOfTradedSizes() {
		return numberOfTradedSizes;
	}

	@Override
	public int getNumberOfWinningTradedSizes() {
		return numberOfWinningTradedSizes;
	}

	@Override
	public int getNumberOfLosingTradedSizes() {
		return numberOfLosingTradedSizes;
	}

	@Override
	public double getWinningLosingTradedSizesRatio() {
		return winningLosingTradedSizesRatio;
	}

	@Override
	public double getAvgProfitForWinningTradedSizesPoints() {
		return avgProfitForWinningTradedSizesPoints;
	}

	public double getAvgLossForLosingTradedSizesPoints() {
		return avgLossForLosingTradedSizesPoints;
	}

	@Override
	public double getAvgProfitForWinningTradedSizesMoney() {
		return avgProfitForWinningTradedSizesMoney;
	}

	public double getAvgLossForLosingTradedSizesMoney() {
		return avgLossForLosingTradedSizesMoney;
	}

	@Override
	public long getMaxDrawdown() {
		return maxDrawdown;
	}

	@Override
	public int getMaxOpenTradedSizes() {
		return maxOpenTradedSizes;
	}

	@Override
	public double getAvgOpenTradedSize() {
		return avgOpenTradedSize;
	}

	public void setTickValue(double aTickValue) {
		if (this.tickValue != aTickValue) {
			this.tickValue = aTickValue;
			firePropertyChange(TICK_VALUE);
		}
	}

	public void setTick(StepDefinition aTtick) {
		if (!aTtick.equals(this.tick)) {
			this.tick = aTtick;
			firePropertyChange(TICK);
		}
	}

	public void setTotalProfitLossPoints(long aTotalProfitLossPoints) {
		if (this.totalProfitLossPoints != aTotalProfitLossPoints) {
			this.totalProfitLossPoints = aTotalProfitLossPoints;
		}
		firePropertyChange(TOTAL_PROFIT_LOSS_POINTS);
	}

	public void setTotalProfitLossMoney(double aTotalProfitLossMoney) {
		if (this.totalProfitLossMoney != aTotalProfitLossMoney) {
			this.totalProfitLossMoney = aTotalProfitLossMoney;
		}
		firePropertyChange(TOTAL_PROFIT_LOSS_MONEY);
	}

	public void setProfitableTradedSizesPoints(long aProfitableTradedSizesPoints) {
		if (this.profitableTradedSizesPoints != aProfitableTradedSizesPoints) {
			this.profitableTradedSizesPoints = aProfitableTradedSizesPoints;
		}
		firePropertyChange(PROFITABLE_TRADED_SIZES_POINTS);
	}

	public void setProfitableTradedSizesMoney(double aProfitableTradedSizesMoney) {
		if (this.profitableTradedSizesMoney != aProfitableTradedSizesMoney) {
			this.profitableTradedSizesMoney = aProfitableTradedSizesMoney;
		}
		firePropertyChange(PROFITABLE_TRADED_SIZES_MONEY);
	}

	public void setLosingTradedSizesPoints(long aLosingTradedSizesPoints) {
		if (this.losingTradedSizesPoints != aLosingTradedSizesPoints) {
			this.losingTradedSizesPoints = aLosingTradedSizesPoints;
		}
		firePropertyChange(LOSING_TRADED_SIZES_POINTS);
	}

	public void setLosingTradedSizesMoney(double aLosingTradedSizesMoney) {
		if (this.losingTradedSizesMoney != aLosingTradedSizesMoney) {
			this.losingTradedSizesMoney = aLosingTradedSizesMoney;
		}
		firePropertyChange(LOSING_TRADED_SIZES_MONEY);
	}

	public void setProfitLossRatio(double aProfitLossRatio) {
		if (this.profitLossRatio != aProfitLossRatio) {
			this.profitLossRatio = aProfitLossRatio;
		}
		firePropertyChange(PROFIT_LOSS_RATIO);
	}

	public void setNumberOfTradedSizes(int aNumberOfTradedSizes) {
		if (this.numberOfTradedSizes != aNumberOfTradedSizes) {
			this.numberOfTradedSizes = aNumberOfTradedSizes;
		}
		firePropertyChange(NUMBER_OF_TRADED_SIZES);
	}

	public void setNumberOfWinningTradedSizes(int aNumberOfWinningTradedSizes) {
		if (this.numberOfWinningTradedSizes != aNumberOfWinningTradedSizes) {
			this.numberOfWinningTradedSizes = aNumberOfWinningTradedSizes;
		}
		firePropertyChange(NUMBER_OF_WINNING_TRADED_SIZES);
	}

	public void setNumberOfLosingTradedSizes(int aNumberOfLosingTradedSizes) {
		if (this.numberOfLosingTradedSizes != aNumberOfLosingTradedSizes) {
			this.numberOfLosingTradedSizes = aNumberOfLosingTradedSizes;
		}
		firePropertyChange(NUMBER_OF_LOSING_TRADED_SIZES);
	}

	public void setWinningLosingTradedSizesRatio(
			double aWinningLosingTradedSizesRatio) {
		if (this.winningLosingTradedSizesRatio != aWinningLosingTradedSizesRatio) {
			this.winningLosingTradedSizesRatio = aWinningLosingTradedSizesRatio;
		}
		firePropertyChange(WINNING_LOSING_TRADED_SIZES_RATIO);
	}

	public void setAvgProfitForWinningTradedSizesPoints(
			double aAvgProfitForWinningTradedSizesPoints) {
		if (this.avgProfitForWinningTradedSizesPoints != aAvgProfitForWinningTradedSizesPoints) {
			this.avgProfitForWinningTradedSizesPoints = aAvgProfitForWinningTradedSizesPoints;
		}
		firePropertyChange(AVG_PROFIT_FOR_WINNING_TRADED_SIZES_POINTS);
	}

	public void setAvgLossForLosingTradedSizesPoints(
			double aAvgLossForLosingTradedSizesPoints) {
		if (this.avgLossForLosingTradedSizesPoints != aAvgLossForLosingTradedSizesPoints) {
			this.avgLossForLosingTradedSizesPoints = aAvgLossForLosingTradedSizesPoints;
		}
		firePropertyChange(AVG_LOSS_FOR_LOSING_TRADED_SIZES_POINTS);
	}

	public void setAvgProfitForWinningTradedSizesMoney(
			double aAvgProfitForWinningTradedSizesMoney) {
		if (this.avgProfitForWinningTradedSizesMoney != aAvgProfitForWinningTradedSizesMoney) {
			this.avgProfitForWinningTradedSizesMoney = aAvgProfitForWinningTradedSizesMoney;
		}
		firePropertyChange(AVG_PROFIT_FOR_WINNING_TRADED_SIZES_MONEY);
	}

	public void setAvgLossForLosingTradedSizesMoney(
			double aAvgLossForLosingTradedSizesMoney) {
		if (this.avgLossForLosingTradedSizesMoney != aAvgLossForLosingTradedSizesMoney) {
			this.avgLossForLosingTradedSizesMoney = aAvgLossForLosingTradedSizesMoney;
		}
		firePropertyChange(AVG_LOSS_FOR_LOSING_TRADED_SIZES_MONEY);
	}

	public void setMaxDrawdown(long aMaxDrawdown) {
		if (this.maxDrawdown != aMaxDrawdown) {
			this.maxDrawdown = aMaxDrawdown;
		}
		firePropertyChange(MAX_DRAWDOWN);
	}

	public void setMaxOpenTradedSizes(int aMaxOpenTradedSizes) {
		if (this.maxOpenTradedSizes != aMaxOpenTradedSizes) {
			this.maxOpenTradedSizes = aMaxOpenTradedSizes;
		}
		firePropertyChange(MAX_OPEN_TRADED_SIZES);
	}

	public void setAvgOpenTradedSize(double aAvgOpenTradedSize) {
		if (this.avgOpenTradedSize != aAvgOpenTradedSize) {
			this.avgOpenTradedSize = aAvgOpenTradedSize;
		}
		firePropertyChange(AVG_OPEN_TRADED_SIZE);
	}

	protected void considerOpenTrades(int trades, int opened) {
		totalTrades += trades;
		setMaxOpenTradedSizes(Math.max(opened, getMaxOpenTradedSizes()));
		openenedTradesEvent++;
	}

	public void setParameters(long aProfitableTradedSizesPoints,
			long aLosingTradedSizesPoints, int aNnumberOfWinningTradedSizes,
			int aNumberOfLosingTradedSizes) {
		setProfitableTradedSizesPoints(aProfitableTradedSizesPoints);
		setProfitableTradedSizesMoney(pointsToMoney(getProfitableTradedSizesPoints()));
		setLosingTradedSizesPoints(aLosingTradedSizesPoints);
		setLosingTradedSizesMoney(pointsToMoney(getLosingTradedSizesPoints()));
		setTotalProfitLossPoints(getProfitableTradedSizesPoints()
				- getLosingTradedSizesPoints());
		setTotalProfitLossMoney(getProfitableTradedSizesMoney()
				- getLosingTradedSizesMoney());
		setNumberOfWinningTradedSizes(aNnumberOfWinningTradedSizes);
		setNumberOfLosingTradedSizes(aNumberOfLosingTradedSizes);
		setProfitLossRatio(new Double(getProfitableTradedSizesPoints())
				.doubleValue()
				/ (Math.max(1,
						new Double(getLosingTradedSizesPoints()).doubleValue())));
		setNumberOfTradedSizes(getNumberOfWinningTradedSizes()
				+ getNumberOfLosingTradedSizes());
		setWinningLosingTradedSizesRatio(new Double(
				getNumberOfWinningTradedSizes()).doubleValue()
				/ (Math.max(1, new Double(getNumberOfLosingTradedSizes())
						.doubleValue())));
		setAvgProfitForWinningTradedSizesPoints(new Double(
				getProfitableTradedSizesPoints()).doubleValue()
				/ Math.max(1, getNumberOfWinningTradedSizes()));
		setAvgLossForLosingTradedSizesPoints(new Double(
				getLosingTradedSizesPoints()).doubleValue()
				/ Math.max(1, getNumberOfLosingTradedSizes()));
		setAvgProfitForWinningTradedSizesMoney(getProfitableTradedSizesMoney()
				/ Math.max(1, getNumberOfWinningTradedSizes()));
		setAvgLossForLosingTradedSizesMoney(getLosingTradedSizesMoney()
				/ Math.max(1, getNumberOfLosingTradedSizes()));
		setAvgOpenTradedSize(new Double(totalTrades).doubleValue()
				/ Math.max(1, openenedTradesEvent));
	}

	/**
	 * translates points measurements to price measurements.
	 * 
	 * @param points
	 * @return
	 */
	private double pointsToMoney(double points) {
		return tick.getTicksOn(points) * tickValue;
	}

}
