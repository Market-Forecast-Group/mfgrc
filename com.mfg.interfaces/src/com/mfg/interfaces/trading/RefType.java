package com.mfg.interfaces.trading;

/**
 * the type of the swing reference that is associated to each TH of the series.
 * 
 * @author gardero
 * 
 */
public enum RefType {
	PreCompute {
		@Override
		public String toRString() {
			return "Pre";
		}
	}, Target0 {
		@Override
		public String toRString() {
			return "Pivot0";
		}
	}, Target2 {
		@Override
		public String toRString() {
			return "Pivot-2";
		}
	}, Swing0_00 {
		@Override
		public String toRString() {
			return "Sw0''/Sw0'";
		}
	}, Swing0 {
		@Override
		public String toRString() {
			return "TH1";
		}
	};
	public abstract String toRString();
}
