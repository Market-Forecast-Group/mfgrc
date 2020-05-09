package com.mfg.tea.accounting;

import java.util.HashMap;
import java.util.List;

import com.mfg.common.TEAException;
import com.mfg.tea.conn.IDuplexStatistics;

/**
 * A folder which contains {@link HomogeneusInventoriesFolder} of different
 * symbols.
 * 
 * <p>
 * The duplex inventory is linked to inventories of the same symbol, instead
 * this class models a folder which is able to contain inventories of different*
 * symbols.
 * 
 * <p>
 * The symbols are "traded" symbols.
 * 
 * <p>
 * A folder does not participate in the transactions, like a normal folder it
 * does not occupy "space", its use is only to aggregate different inventories
 * into a "logical" structure, for example a trading session, several trading
 * sessions into a tea, several TEAs into an aggregate of all teas.
 * 
 * <p>
 * This for the mixed materials, then we have the homogeneous view, that is
 * homogeneous, but it transcend the "barriers" of the various trading agents
 * because it groups apples with apples, lemons with lemons, etc. not regarding
 * the agent that has done the first trade.
 * 
 * 
 * <p>
 * Is the inventory allowed to have inventories of the same name at the same
 * level? If we follow the file system metaphor than no, this is not possible,
 * because we cannot have two files in the same folder with the same name.
 * 
 * <p>
 * But in that case how could I manage it? Maybe with an homogeneous folder,
 * because that would have all the symbols of the same name stored together.
 * 
 * <p>
 * Suppose a subTEA object, it has different trading pipes of the same name...
 * for example "goog", we have t1 and t2 which both trade goog stocks. How could
 * we build the tree?
 * 
 * <p>
 * there is a mixed tree like subTEA1 [ t1, t2], where t1 and t2 are mixed
 * folders.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public final class MixedInventoriesFolder extends AbstractInventoryItem
		implements IInventoriesHolder {

	@Override
	public String toString() {
		return "[ Folder: " + _name + " size: " + this._inventories.size()
				+ "]";
	}

	public MixedInventoriesFolder(String aName,
			MixedInventoriesFolder aMixFolder) {
		super(new EquityMerger(), aMixFolder, null, aName);
		_name = aName;
	}

	private final String _name;

	/**
	 * Also a folder may have a folder as a parent, and it will be of course
	 * mixed, even if we have a single symbol we might have a folder of
	 * different symbols (added later).
	 */
	private MixedInventoriesFolder _parent;

	/**
	 * I have the Map for all the symbols inside this folder.
	 * 
	 * <p>
	 * But how to relate the inventory folder with its equity?
	 * 
	 * <p>
	 * A folder has, of course, an equity (that is the purpose of having a
	 * folder! : to update different symbols as if one.).
	 * 
	 * <p>
	 * But so the duplex inventory may have a physical parent (which is one) and
	 * possibly different logical parents...
	 * 
	 * <p>
	 * Maybe the logical parent is like a symbolic link in a file system...
	 * 
	 * <p>
	 * The key for this inventory is the name of the symbol, which will be the
	 * name of the folder in a tree-like structure for example in a GUI... the
	 * value is the duplex inventory, which could be a <b>real</b> inventory or
	 * a homogeneous one.
	 * 
	 * <p>
	 * We cannot have logical children here.
	 * 
	 */
	private final HashMap<String, AbstractInventoryItem> _inventories = new HashMap<>();

	/**
	 * Adds the given inventory to the folder. It is an error to add an
	 * inventory with the same symbol twice.
	 * 
	 * <p>
	 * This is really a container for homogeneous symbols... actually I could
	 * have a mixed folder which contains also a mixed folder, but this is not
	 * really used in practice.
	 * 
	 * @param duplexInventoryBase
	 *            the inventory which is added in the folder, in a mixed
	 *            inventory I can add both a folder or another mixed inventory.
	 * @param aInfo
	 * @throws TEAException
	 */
	public void addInventory(String aKey, AbstractInventoryItem aii,
			StockInfo aInfo) {

		if (_inventories.containsKey(aKey)) {
			/*
			 * if these casts fail then you are trying to make a strange
			 * hierarchy
			 */
			HomogeneusInventoriesFolder folder = (HomogeneusInventoriesFolder) _inventories
					.get(aKey);
			folder.addChild(aInfo, (HomogeneousInventoryBase) aii);
		} else {
			if (!(aii instanceof IInventoriesHolder)) {
				HomogeneusInventoriesFolder hif = new HomogeneusInventoriesFolder(
						aInfo, this);

				hif.addChild(aInfo, (HomogeneousInventoryBase) aii);

			} else {
				_inventories.put(aKey, aii);
				((EquityMerger) _equity).addEquity(aii._equity);
			}

		}

		/*
		 * Chain the link to the upside, this allows the equity chaining of
		 * different levels.
		 * 
		 * //to be checked...!
		 */
		if (_parent != null) {
			_parent.addInventory(aKey, aii, aInfo);
		}

	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public long getUnrealizedPL() {
		return 224;
	}

	@Override
	public long getUnrealizedPL(int aPrice) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAverageCost() {
		// TODO Auto-generated method stub
		return 0;
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

	public void removeChild(String aTeaId) {
		AbstractInventoryItem item = _inventories.remove(aTeaId);
		assert (item != null); // It must be present.
	}

}
