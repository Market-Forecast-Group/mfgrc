package com.mfg.strategy;

import com.mfg.utils.ui.IEnumWithLabel;

public enum AutoStop implements IEnumWithLabel {
	NONE("None"), AUTO("Auto"), MANUAL("Manual");

	private String str;

	AutoStop(String aStr) {
		this.str = aStr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.utils.ui.IEnumWithLabel#getLabel()
	 */
	@Override
	public String getLabel() {
		return str;
	}

	@Override
	public String toString() {
		return str;
	}
}