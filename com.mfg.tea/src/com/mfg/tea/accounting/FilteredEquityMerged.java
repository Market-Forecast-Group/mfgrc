package com.mfg.tea.accounting;

import com.mfg.tea.conn.ISingleAccountStatistics;

/**
 * A equity merger which is able to filter the statistics based on a filter,
 * there is a "real" equity which stores the array of equities.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class FilteredEquityMerged extends DuplexEquityBase {

	private final EquityMerger _mergedEquity;
	private final EFilterMode _filter;

	public FilteredEquityMerged(EquityMerger aMergedEquity, EFilterMode aFilter) {
		_mergedEquity = aMergedEquity;
		_filter = aFilter;
	}

	@Override
	public double getProfitableTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
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
	public long getLoss() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOpenedPositions() {
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
	public int getNumberOfLossingTrades() {
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
	public long getTimeUW() {
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
	public double getLosingTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProfitLossRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxDrawdown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfWinningTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfLosingTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWinningLosingTradedSizesRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvgProfitForWinningTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxOpenTradedSizes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvgOpenTradedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLossInPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalProfitLossPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getProfitableTradedSizesPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLosingTradedSizesPoints() {
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
		// TODO Auto-generated method stub
		return 0;
	}

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
	protected long _getEquity(EFilterMode aFilter) {
		if (aFilter == EFilterMode.LONG_AND_SHORT) {
			return _mergedEquity._getEquity(this._filter);
		}
		throw new IllegalStateException();
	}

	@Override
	protected int _getQuantity(EFilterMode aFilterMode) {
		if (aFilterMode == EFilterMode.LONG_AND_SHORT) {
			return _mergedEquity._getQuantity(this._filter);
		}
		throw new IllegalStateException();
	}

	@Override
	protected long _getGain(EFilterMode aFilter) {
		if (aFilter == EFilterMode.LONG_AND_SHORT) {
			return _mergedEquity._getGain(this._filter);
		}
		throw new IllegalStateException();
	}

}
