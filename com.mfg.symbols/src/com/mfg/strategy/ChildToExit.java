package com.mfg.strategy;

import com.mfg.utils.ui.IEnumWithLabel;

public enum ChildToExit implements IEnumWithLabel{
	STOP_LOSS("Stop Loss"), TAKE_PROFIT("Take Profit");

	private String str;

	ChildToExit(String aStr) {
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