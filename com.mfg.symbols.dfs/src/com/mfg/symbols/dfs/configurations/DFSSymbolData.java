package com.mfg.symbols.dfs.configurations;

import static com.mfg.common.DfsSymbol.TYPE_FOREX;

import com.mfg.dm.symbols.SymbolData2;

public class DFSSymbolData extends SymbolData2 {
	/**
	 * 
	 */
	private static final String PROP_MATURITY = "maturity";

	public static final String PROP_TYPE = "type";

	private String type;
	private String maturity;

	/**
	 * 
	 */
	public DFSSymbolData() {
		type = TYPE_FOREX;
		setLocalSymbol("@ES");
	}

	/**
	 * @return the maturity
	 */
	public String getMaturity() {
		return maturity;
	}

	/**
	 * @param aMaturity
	 *            the maturity to set
	 */
	public void setMaturity(String aMaturity) {
		this.maturity = aMaturity;
		firePropertyChange(PROP_MATURITY);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param aType
	 *            the type to set
	 */
	public void setType(String aType) {
		this.type = aType;
		firePropertyChange(PROP_TYPE);
	}
}
