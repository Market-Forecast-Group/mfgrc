package com.mfg.tea.accounting;

import java.util.ArrayList;
import java.util.List;

import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * A parent inventory used also as a root.
 * 
 * <p>
 * It is assumed that all the children (and grand-children) of this inventory
 * share the same symbol.
 * 
 * <p>
 * When this is not the case we assume to have a {@link MixedInventoriesFolder}.
 * 
 * 
 * <p>
 * The homogeneous inventory folder contains also the equity. Of course it is a
 * double equity, because it can track the sum of all "longs" and "short"
 * accounts.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public final class HomogeneusInventoriesFolder extends HomogeneousInventoryBase
		implements IInventoriesHolder {

	@Override
	public String toString() {
		String s = "[";
		int i = 0;
		for (HomogeneousInventoryBase child : _children) {
			s += child.toString();
			if (++i != _children.size()) {
				s += ",";
			}
		}
		s += "]";
		return s;
	}

	// A parent inventory can have children, a normal inventory cannot.

	/**
	 * Creates a folder whose children <b>must</b> be of the same material.
	 * 
	 * <p>
	 * This folder may be part of a chain of homogeneous folders, and a mixed
	 * chain of mixed folders. At time of writing I do not know if I can have a
	 * set...
	 * 
	 * @param aStockInfo
	 * @param aParent
	 * @param aMixParent
	 */
	public HomogeneusInventoriesFolder(StockInfo aStockInfo,
			MixedInventoriesFolder aParent) {
		super(new HomogeneousEquityMerger(), aParent, aStockInfo);
	}

	private ArrayList<HomogeneousInventoryBase> _children = new ArrayList<>();

	@Override
	public int getQuantity() {
		int quantity = 0;
		for (HomogeneousInventoryBase aChild : _children) {
			quantity += aChild.getQuantity();
		}
		return quantity;
	}

	/**
	 * adds the given inventory to the children of this object.
	 * 
	 * <p>
	 * The child could be a {@link HomogeneusInventoriesFolder} or a
	 * {@link DuplexInventory}, in either case it <b>must</b> have the same
	 * stock object.
	 * 
	 * @param aInfo
	 * 
	 * @param aChild
	 *            the child to be added.
	 * 
	 * @throws IllegalArgumentException
	 *             if the stock is not equal to the other stocks.
	 */
	void addChild(StockInfo aInfo, HomogeneousInventoryBase aChild) {
		if (aInfo.equals(_stockInfo)) {
			_children.add(aChild);

			((EquityMerger) _equity).addEquity(aChild._equity);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public List<String> getListOfHeldSymbols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDuplexStatisticsMoney getStatsForSymbol(String aSymbol) {
		// TODO Auto-generated method stub
		return null;
	}

}
