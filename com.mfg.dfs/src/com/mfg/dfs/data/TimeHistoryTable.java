package com.mfg.dfs.data;

import java.io.IOException;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.dfs.misc.DfsTimeBar;

public class TimeHistoryTable extends HistoryTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2466739804526802121L;

	public TimeHistoryTable(DfsSymbol symbol, Maturity maturity,
			BarType aBarType) throws IOException {
		super(symbol, maturity, aBarType);
	}

	@Override
	protected void _createCache() throws IOException {
		_cache = MfgMdbSession.getInstance().getTimeBarsCache(_symbol.prefix,
				_maturity, _type);
	}

	@Override
	protected DfsBar getConcreteBar(UnparsedBar ub, int scale)
			throws DFSException {
		/*
		 * we are lenient towards some "errors" from the outside. In our case
		 * iqfeed sometimes gives a close outside the high/low range, because of
		 * settlements, we update the high/low range
		 */
		return new DfsTimeBar(ub, scale, _symbol.tick, true);
	}

	@Override
	public void onHistoricalTick(UnparsedTick ut) {
		throw new UnsupportedOperationException(
				"you cannot receive an unparsed tick here");
	}
}
