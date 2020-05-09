package com.mfg.dfs.data;

import java.io.IOException;

import com.mfg.common.BarType;
import com.mfg.common.DfsSymbol;
import com.mfg.dfs.cache.MfgMdbSession;

/**
 * A time history table used to store data from a CSV data source or in any case
 * an historical, not "live", data source.
 * 
 * <p>
 * Usually these tables have only one maturity and one layer is possible,
 * because they do not have different data densities.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class CsvTimeHistoryTable extends CsvTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3313271210796587728L;

	public CsvTimeHistoryTable(DfsSymbol symbol, BarType aBarType, int baseWidth) {
		super(symbol, aBarType, baseWidth);
	}

	@Override
	protected void _createCache() throws IOException {
		_cache = MfgMdbSession.getInstance().getCsvTimeBarCache(_symbol.prefix);
	}

}