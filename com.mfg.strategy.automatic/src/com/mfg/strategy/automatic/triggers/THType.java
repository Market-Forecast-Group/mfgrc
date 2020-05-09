
package com.mfg.strategy.automatic.triggers;

/**
 * represents the TH value we are checking to be in the range.
 * 
 * @author gardero Possible values are:
 *         <ul>
 *         <li> {@code Both}.
 *         <li> {@code TH_m1}, represents TH<sub>-1</sub>.
 *         <li> {@code TH0}, represents TH<sub>0</sub>.
 *         </ul>
 * 
 */
public enum THType {
	Both("Both"), TH_m1("TH-1"), TH0("TH0");
	protected String s;


	private THType(String aS) {
		this.s = aS;
	}


	@Override
	public String toString() {
		return s;
	}

}
