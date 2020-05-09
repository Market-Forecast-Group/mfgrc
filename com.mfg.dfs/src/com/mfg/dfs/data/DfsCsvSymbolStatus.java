package com.mfg.dfs.data;

import com.mfg.common.BarType;
import com.mfg.common.DfsSymbol;

/**
 * This is the status of a symbol which does not have maturities and is only the
 * result of a CSV import mechanism.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DfsCsvSymbolStatus extends DfsSymbolStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4373568760712552676L;

	public DfsCsvSymbolStatus(DfsSymbol aSymbol) {
		super(aSymbol);
	}

	/**
	 * These are the statistics for the Csv Symbol. They are single, because the
	 * csv symbol has not maturities and has not the continuous part.
	 */
	public DfsIntervalStats intervalStats;
	public int baseWidth;
	public BarType type;

}
