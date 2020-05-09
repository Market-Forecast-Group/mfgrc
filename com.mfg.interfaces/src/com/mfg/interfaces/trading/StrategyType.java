package com.mfg.interfaces.trading;

import com.mfg.utils.ui.IEnumWithLabel;

public enum StrategyType implements IEnumWithLabel {
	MANUAL {
		@Override
		public String getLabel() {
			return "Manual";
		}
	},
	AUTOMATIC {
		@Override
		public String getLabel() {
			return "Automatic";
		}
	},
	MIXED {
		@Override
		public String getLabel() {
			return "Mixed";
		}
	};
	
	@Override
	public String toString() {
		return getLabel();
	}
}
