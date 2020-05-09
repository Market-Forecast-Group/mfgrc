package com.mfg.strategy;

import com.mfg.utils.ui.IEnumWithLabel;

public enum EntryExitOrderType implements IEnumWithLabel {
	MARKET("Market"), LIMIT("Limit");

	private String str;

	EntryExitOrderType(String aStr) {
		this.str = aStr;
	}

	@Override
	public String toString() {
		return str;
	}
	
	/* (non-Javadoc)
	 * @see com.mfg.utils.ui.IEnumWithLabel#getLabel()
	 */
	@Override
	public String getLabel() {
		return str;
	}
}