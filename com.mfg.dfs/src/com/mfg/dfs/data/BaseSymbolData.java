package com.mfg.dfs.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.conn.IDatabaseChangeListener;

public abstract class BaseSymbolData implements Serializable {

	public abstract DfsSymbolStatus getStatus(boolean forceCheck)
			throws DFSException;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1554691195478242535L;

	/**
	 * The symbol may be also a csv symbol, in case the data has been imported
	 * by csv sources.
	 */
	protected final DfsSymbol _symbol;

	BaseSymbolData(DfsSymbol aSymbol) {
		_symbol = aSymbol;
	}

	public abstract int getBarCount(Maturity parsedMaturity, BarType aType,
			int barWidth) throws DFSException;

	public abstract int getBarsBetween(Maturity parsedMaturity, BarType aType,
			int barWidth, long startDate, long endDate) throws DFSException;

	/**
	 * @throws DFSException
	 */
	@SuppressWarnings("static-method")
	public String getCurrentSymbol() throws DFSException {
		// you must override it if you want.
		throw new UnsupportedOperationException();
	}

	public abstract long getDateAfterXBarsFrom(Maturity parsedMaturity,
			BarType aType, int barWidth, long startDate, int numBars)
			throws DFSException;

	public abstract long getDateBeforeXBarsFrom(Maturity parsedMaturity,
			BarType aType, int barWidth, long endTime, int numBars)
			throws DFSException;

	public final DfsSymbol getSymbol() {
		return _symbol;
	}

	public final BigDecimal getTick() {
		return new BigDecimal(new BigInteger("" + _symbol.tick), _symbol.scale);
	}

	public abstract IBarCache returnCache(Maturity parsedMaturity,
			RequestParams aReq) throws DFSException;

	public abstract void truncateMaturity(Maturity parsedMaturity,
			BarType aType, long truncateDate) throws DFSException;

	public abstract void watchMaturity(Maturity parsedMaturity,
			IDatabaseChangeListener aListener);

	public abstract void unwatchMaturity(Maturity parsedMaturity);
}
