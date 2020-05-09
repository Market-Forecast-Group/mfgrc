package com.mfg.dfs.data;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.misc.IDataFeed;

abstract class CsvTable extends CachedTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3858960967423692061L;

	@Override
	public DfsIntervalStats getStats(boolean forceCheck) {
		return super._getStatsState(EVisibleState.COMPLETE, forceCheck);
	}

	protected CsvTable(DfsSymbol aSymbol, BarType aType, int aBaseWidth) {
		super(aSymbol, aType, aBaseWidth);
	}

	@Override
	public final String getKey() {
		return "[" + _symbol.prefix + ": CSV of :" + _type + "]";
	}

	@Override
	protected final void _truncateImpl(long truncateDate) throws DFSException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void forceUpdate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Maturity getMaturity() {
		return null;
	}

	@Override
	public final boolean isReady() {
		throw new UnsupportedOperationException();
	}

}
