package com.mfg.common;

import com.mfg.common.Maturity.ParseMaturityAns;

/**
 * A class which unifies the concepts of a {@linkplain DfsSymbol} and a
 * {@linkplain Maturity}.
 * 
 * <p>
 * The unification of these two symbols makes a real symbol which the user can
 * use to ask data from the data provider
 * 
 * @author Sergio
 * 
 */
public class DfsRealSymbol extends MfgSymbol {
	private final DfsSymbol _symbol;
	private final Maturity _maturity;

	public DfsRealSymbol(String aCompleteSymbol, String aCompleteName,
			int aTick, int aScale, int aTickValue) throws DFSException {
		ParseMaturityAns ans = Maturity.parseMaturity(aCompleteSymbol);
		_maturity = ans.parsedMaturity;
		_symbol = new DfsSymbol(_maturity == null ? aCompleteSymbol
				: ans.unparsedString, aCompleteName, aTick, aScale, aTickValue);
	}

	@Override
	public int getScale() {
		return _symbol.scale;
	}

	@Override
	public int getTick() {
		return _symbol.tick;
	}

	/**
	 * returns the real name of this symbol, which is the union of the prefix
	 * and the maturity.
	 * 
	 * @return
	 */
	@Override
	public String getSymbol() {
		return _symbol.prefix
				+ (_maturity == null ? "" : _maturity
						.toDataProviderMediumString());
	}

	@Override
	public int getTickValue() {
		return _symbol.tickValue;
	}

}
