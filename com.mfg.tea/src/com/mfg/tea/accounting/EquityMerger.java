package com.mfg.tea.accounting;

import java.util.ArrayList;

import com.mfg.tea.conn.ISingleAccountStatistics;

/**
 * A class which merges different equities, also of different materials and it
 * creates a "virtual" equity based on the sum of all its equities.
 * 
 * <p>
 * If the equities are made from the same material then also the methods which
 * return the equity in ticks or points are valid, otherwise only the methods
 * which return money units are meaningful.
 * 
 * Equity merger is always duplex, because I add always a duplex equity.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class EquityMerger extends DuplexEquityBase {

	private ArrayList<DuplexEquityBase> _equities = new ArrayList<>();

	// @Override
	// void _addSimpleEquity(SimpleEquity _equity, boolean b) {
	// /*
	// * Here it is not defined, because we do not have simple equities
	// */
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Private function to check if this is a homogeneous merger, used to halt
	 * code that wants to call homogeneuous methods on a mixed equity object.
	 */
	private void _checkHomogeneusMerger() {
		if (_equities.size() != 0 && _stockInfo == null) {
			// I am not homogeneous
			throw new IllegalStateException();
		}
	}

	/**
	 * returns the equity merged using a specific filter.
	 * 
	 * @param aFilter
	 * @return
	 */
	@Override
	protected long _getEquity(EFilterMode aFilter) {
		long totalEquity = 0;
		for (DuplexEquityBase equity : _equities) {
			totalEquity += equity._getEquity(aFilter);
		}
		return totalEquity;
	}

	@Override
	protected int _getQuantity(EFilterMode aFilter) {

		_checkHomogeneusMerger();

		int totalQuantity = 0;
		for (DuplexEquityBase equity : _equities) {
			totalQuantity += equity._getQuantity(aFilter);
		}
		return totalQuantity;
	}

	void addEquity(DuplexEquityBase _equity) {
		if (_equities.size() == 0) {
			/*
			 * This is the first equity added.
			 */
			_stockInfo = _equity._stockInfo;
		} else {
			if (_stockInfo != null && !_stockInfo.equals(_equity._stockInfo)) {
				/*
				 * Either this is a mixed equity already or it has already set
				 * to a mixed equity.
				 */
				_stockInfo = null;
			}
		}

		_equities.add(_equity);
	}

	// @Override
	// public void dump() {
	// // TODO Auto-generated method stub
	//
	// }

	// @Override
	// public double getMaxDrawDown() {
	// // TODO Auto-generated method stub
	// return 0;
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
	public int getEquityTicks() {
		return _stockInfo.convertToTicks(getEquity());
	}

	// @Override
	// public long getGain() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

	// @Override
	// public long getGainInPoints() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

	@Override
	public ISingleAccountStatistics getLongStatistics() {
		return new FilteredEquityMerged(this, EFilterMode.ONLY_LONG);
	}

	@Override
	public double getLosingTradedSizesMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

	// @Override
	// public int getQuantity() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

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

	// @Override
	// public double getTotalProfitLossMoney() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

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

	// @Override
	// public long getOpenEquity() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

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
	public ISingleAccountStatistics getShortStatistics() {
		/*
		 * I have to return a "view" of this equity based only the short
		 * equities.
		 */
		return new FilteredEquityMerged(this, EFilterMode.ONLY_SHORT);
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

	// @Override
	// public boolean haveYouReachedMinNumberOfTrades() {
	// // TODO Auto-generated method stub
	// return false;
	// }

	// @Override
	// public boolean testEquals(IAccountStatistics other) {
	// // TODO Auto-generated method stub
	// return false;
	// }

	@Override
	protected long _getGain(EFilterMode aFilter) {
		long gain = 0;
		for (DuplexEquityBase equity : _equities) {
			gain += equity._getEquity(aFilter);
		}
		return gain;
	}

}
