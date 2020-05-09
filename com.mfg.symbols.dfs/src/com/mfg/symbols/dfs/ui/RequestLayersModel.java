package com.mfg.symbols.dfs.ui;

import java.util.List;
import java.util.Map;

import com.mfg.common.BarType;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.Slot;
import com.mfg.dfs.data.DfsIntervalStats;

public class RequestLayersModel implements SlotsCanvas.ILayersModel {

	private final List<Slot> _slots;
	private final Map<BarType, DfsIntervalStats> _intervals;

	public RequestLayersModel(List<Slot> slots,
			Map<BarType, DfsIntervalStats> intervals) {
		super();
		_slots = slots;
		_intervals = intervals;
	}

	@Override
	public int size() {
		return _slots.size();
	}

	@Override
	public long getStartDate(int i) {
		return _slots.get(i).getStartDate().getTime();
	}

	@Override
	public long getEndDate(int i) {
		return _slots.get(i).getEndDate().getTime();
	}

	@Override
	public long getAvailableStartDate(int i) {
		DfsIntervalStats interval = _intervals.get(getBarType(i));
		return interval.startDate;
	}

	@Override
	public long getAvailableEndDate(int i) {
		DfsIntervalStats interval = _intervals.get(getBarType(i));
		return interval.endDate;
	}

	@Override
	public BarType getBarType(int i) {
		return _slots.get(i).getBarType();
	}

}
