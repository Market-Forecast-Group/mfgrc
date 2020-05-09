package com.mfg.symbols.dfs.configurations;

import java.util.ArrayList;
import java.util.List;

import com.mfg.common.BarType;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.Slot;
import com.mfg.persist.interfaces.AbstractStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;

public class DFSProfile extends AbstractStorageObject {

	public static class SlotInfo {
		private BarType _barType;
		private int _numberOfDays;

		public SlotInfo() {
		}
		
		public SlotInfo(BarType barType, int numberOfDays) {
			super();
			_barType = barType;
			_numberOfDays = numberOfDays;
		}

		public BarType getBarType() {
			return _barType;
		}

		public void setBarType(BarType barType) {
			_barType = barType;
		}

		public int getNumberOfDays() {
			return _numberOfDays;
		}

		public void setNumberOfDays(int numberOfDays) {
			_numberOfDays = numberOfDays;
		}
	}

	private List<SlotInfo> _slots;

	public DFSProfile() {
		_slots = new ArrayList<>();
	}

	/**
	 * @return the slots
	 */
	public List<SlotInfo> getSlots() {
		return _slots;
	}

	/**
	 * @param slots
	 *            the slots to set
	 */
	public void setSlots(List<SlotInfo> slots) {
		this._slots = slots;
	}

	public void updateFromSlots(List<Slot> slots) {
		_slots = new ArrayList<>();
		for (Slot slot : slots) {
			SlotInfo info = new SlotInfo();
			info.setBarType(slot.getBarType());
			info.setNumberOfDays((int) slot.getNumberOfDays());
			_slots.add(info);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getStorage()
	 */
	@Override
	public SimpleStorage<?> getStorage() {
		return DFSSymbolsPlugin.getDefault().getProfileStorage();
	}

	public boolean sameOf(List<Slot> aSlots) {
		if (this._slots.size() != aSlots.size()) {
			return false;
		}
		for (int i = 0; i < this._slots.size(); i++) {
			SlotInfo s1 = this._slots.get(i);
			Slot s2 = aSlots.get(i);

			if (s1.getNumberOfDays() != s2.getNumberOfDays()) {
				return false;
			}
		}
		return true;
	}
}
