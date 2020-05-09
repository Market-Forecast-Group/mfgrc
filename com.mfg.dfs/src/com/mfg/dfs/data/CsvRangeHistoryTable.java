package com.mfg.dfs.data;

import java.io.IOException;

import com.mfg.common.BarType;
import com.mfg.common.DfsSymbol;
import com.mfg.dfs.cache.MfgMdbSession;

public class CsvRangeHistoryTable extends CsvTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6730921899132985155L;

	protected CsvRangeHistoryTable(DfsSymbol aSymbol, BarType aType) {
		super(aSymbol, aType, 1);

	}

	@Override
	protected void _createCache() throws IOException {
		_cache = MfgMdbSession.getInstance()
				.getCsvRangeBarCache(_symbol.prefix);
	}

}
