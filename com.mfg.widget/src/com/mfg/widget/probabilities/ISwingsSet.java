
package com.mfg.widget.probabilities;

/**
 * represents a set of swings.
 * @author gardero
 *
 */
public interface ISwingsSet {

	/**
	 * gets a swing of a specific scale.
	 * @param scale the scale.
	 * @param index the index of the swing.
	 * @return the swing information.
	 */
	public abstract SwingInfo getSwing(int scale, int index);


	/***
	 * asks if this object has information about a specific scale.
	 * @param scale the scale.
	 * @return
	 */
	public abstract boolean hasScale(int scale);

}
