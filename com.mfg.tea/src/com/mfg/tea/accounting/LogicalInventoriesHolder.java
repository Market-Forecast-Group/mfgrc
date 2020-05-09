package com.mfg.tea.accounting;

import java.util.HashMap;
import java.util.List;

import com.mfg.tea.conn.IDuplexStatistics;
import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * The inventory holder is a simple container class of other holders or
 * inventories.
 * 
 * <p>
 * The main characteristics of this class is that it is a "logical" grouping,
 * not an accountable grouping, in the sense that, even if we measure all in
 * money and we <i>could</i> theoretically have a sum, we decide to not have it.
 * 
 * 
 * <p>
 * As this is a logical grouping then this means that I can add things to this
 * folder but the "things" do not attach to it. They simply are here for a
 * common grouping, but they are not aware of this relationship, as a file is
 * not aware of the folder in which it is put.
 * 
 * <p>
 * This class may be called LogicalInventoriesHolder, because it is only a
 * logical container of inventories.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public final class LogicalInventoriesHolder implements IInventoriesHolder {

	public final static String REAL_TIME_FOLDER = "RealTime";

	public final static String PAPER_TRADING_FOLDER = "PaperTrading";

	public final static String DATABASE_TRADING_FOLDER = "DB tradings";

	@SuppressWarnings("unused")
	private LogicalInventoriesHolder _parent;
	private final String _name;

	/**
	 * These are the logical children, which are other logical folders.
	 * 
	 * <p>
	 * I do not yet know if I can mix logical and physical children.
	 * 
	 * <p>
	 * Yes, I <b>must</b> be able to mix them, because this means that root has
	 * the possibility to be a container of different objects.
	 */
	private HashMap<String, IInventoriesHolder> _children = new HashMap<>();

	/**
	 * A logical folder may have single children which are simply children of
	 * class DuplexInventory, in any case I will store them as interfaces.
	 */
	private HashMap<String, IInventory> _singleChildren = new HashMap<>();

	/**
	 * creates a holder with a certain name and a parent.
	 * 
	 * <p>
	 * The parent needs to be a logical folder, as this is a logical tree, not a
	 * physical one (the one created using the {@link MixedInventoriesFolder}.
	 * 
	 * @param aName
	 * @param object
	 */
	public LogicalInventoriesHolder(String aName,
			LogicalInventoriesHolder aParent) {
		_parent = aParent;
		_name = aName;
	}

	@Override
	public List<String> getListOfHeldSymbols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDuplexStatistics getStatsForSymbol(String aSymbol) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The add child for a logical folder does <b>not</b> create the physical
	 * connection between an inventory and its parent, because the logical
	 * folder does not have statistics on its own.
	 * 
	 * @param aName
	 * @param abstractInventoryItem
	 */
	public void addChild(String aName, IInventoriesHolder abstractInventoryItem) {
		if (_children.containsKey(aName)) {
			throw new IllegalArgumentException("Duplicate child" + aName);
		}
		_children.put(aName, abstractInventoryItem);

	}

	public void addSingleChild(String aName, IInventory aSingleInventory) {
		if (_singleChildren.containsKey(aName)) {
			throw new IllegalArgumentException("Duplicate child " + aName);
		}
		_singleChildren.put(aName, aSingleInventory);
	}

	/**
	 * A logical folder does not have the total statistics, it may contain
	 * objects which have it, though.
	 */
	@Override
	public IDuplexStatisticsMoney getTotalStatistics() {
		return null;
	}

	@Override
	public String getName() {
		return _name;
	}

	public IInventoriesHolder getChild(String aKey) {
		return _children.get(aKey);
	}

}
