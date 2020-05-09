package com.mfg.tea.conn;

/**
 * This interface lists the statistics which are homogeneous, relative to a
 * single or a set of inventories with the same symbol.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IHomogeneousStatistics {

	/**
	 * 
	 * @return the quantity held. This is equal to the sum of the inventories
	 *         quantites which constitute this equity. Remember that we are now
	 *         in a homogeneous realm, so we can refer to a quantity.
	 *         <p>
	 *         A negative quantity means that we are short of this material.
	 */
	public int getQuantity();
}
