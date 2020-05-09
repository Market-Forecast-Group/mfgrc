package com.mfg.interfaces.trading;

import com.mfg.broker.IOrderMfg;

public class PositionOpenedEvent extends PositionEvent {

	private long[] childrenOpenings;

	public PositionOpenedEvent(IOrderMfg aOrder, long physicalTime,
			long executionTime, long executionPrice, boolean isLongPosition,
			long[] aChildrenOpenings) {
		super(aOrder, executionTime, executionPrice, isLongPosition,
				physicalTime);
		this.childrenOpenings = aChildrenOpenings;
	}

	public long[] getChildrenOpenings() {
		return childrenOpenings;
	}

}
