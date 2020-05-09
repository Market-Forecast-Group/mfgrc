package com.mfg.dfs.data;

import java.io.IOException;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.dfs.cache.HistoryTablesContainer;

/**
 * Basic class that holds data for a certain maturity and a certain symbol.
 * 
 * <p>
 * This class does not care if the maturity is monthly or quarterly, this is not
 * so important as the maturity is consistent, that is the same symbol does not
 * store mixed maturities (it can be very confusing).
 * 
 * <p>
 * An object of this type simply stores the maturities, each maturity is stored
 * with the data which are currently available.
 * 
 * <p>
 * For example if we have the maturity H2002 we will surely only the daily data,
 * because much time has passed, but for H2013 at time of writing (July 2013) we
 * may still have some minute data.
 * 
 * <p>
 * The code is able to know if the data is available or not (and not search for
 * inexistend data).
 * 
 * 
 * @author Sergio
 * 
 */
public class MaturityData extends HistoryTablesContainer {

	@Override
	public String toString() {
		return _symbol.toString() + _maturity.toDataProviderMediumString();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8530426547701727925L;
	/**
	 * This is the maturity for this data;
	 * <p>
	 * All the tables inside this object share this maturity
	 */
	private final Maturity _maturity;
	private DfsSymbol _symbol;

	public MaturityData(DfsSymbol aSymbol, Maturity aMaturity)
			throws IOException {
		_maturity = aMaturity;
		_symbol = aSymbol;

		// Ok, now I create the tables which will hold the bar type data.
		_tables.put(BarType.MINUTE, new TimeHistoryTable(_symbol, _maturity,
				BarType.MINUTE));
		_tables.put(BarType.RANGE, new RangeHistoryTable(_symbol, _maturity));
		_tables.put(BarType.DAILY, new TimeHistoryTable(_symbol, _maturity,
				BarType.DAILY));
	}

	/**
	 * returns the table for the particular type, this is really useful only in
	 * this package, to make the continous data, so it is package protected.
	 * 
	 * @param aType
	 *            the type, like minute, daily or range.
	 * @return the table of that type.
	 */
	public SingleWidthTable getTable(BarType aType) {
		return _tables.get(aType);
	}

	public boolean isReady() {
		// You have to check all the maturities, if they are ready... or not...
		boolean res = true;
		for (SingleWidthTable ht : _tables.values()) {
			res &= ht.isReady();
		}
		return res;
	}

	public void close() {
		for (SingleWidthTable ht : _tables.values()) {
			ht.close();
		}

	}

	/**
	 * The tables could be ready but all without data, so I have to check wether
	 * they are all empty or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		boolean res = true;
		for (SingleWidthTable ht : _tables.values()) {
			res &= ht.isEmpty();
		}
		return res;
	}

	public Maturity getMaturity() {
		return this._maturity;
	}

	@Override
	public final MaturityStats getStatus(boolean forceCheck) {
		return super.getStatus(_maturity, forceCheck);

	}

	@Override
	public boolean needsRecompute() {
		boolean res = false;
		for (SingleWidthTable ht : _tables.values()) {
			res |= ht.needsRecompute();
		}
		return res;
	}

	/**
	 * truncates data of this maturity at a specified date.
	 * 
	 * @param aType
	 * @param truncateDate
	 * @throws DFSException
	 */
	public void truncate(BarType aType, long truncateDate) throws DFSException {
		_tables.get(aType).truncate(truncateDate);
	}

	public void removeFromDisk() throws DFSException {
		for (SingleWidthTable table : _tables.values()) {
			((HistoryTable) table).removeFromDisk();
		}
	}

	@Override
	protected String getCompleteSymbol() {
		return this._symbol.prefix + _maturity.toDataProviderMediumString();
	}

}
