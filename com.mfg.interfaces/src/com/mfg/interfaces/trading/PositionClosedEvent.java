package com.mfg.interfaces.trading;

import com.mfg.broker.IOrderMfg;

public class PositionClosedEvent extends PositionEvent {

	private final boolean gain;
	private final long parentExecutionTime;
	private final long parentExecutionPrice;
	private long total, pl;

	public PositionClosedEvent(long physicalTime, long executionTime,
			long executionPrice, boolean isLongPosition, boolean aGain,
			long aParentExecutionTime, long aParentExecutionPrice,
			IOrderMfg aOrder, long aTotal) {
		super(aOrder, executionTime, executionPrice, isLongPosition,
				physicalTime);
		this.gain = aGain;
		this.parentExecutionTime = aParentExecutionTime;
		this.parentExecutionPrice = aParentExecutionPrice;
		pl = -getOrder().getQuantity()
				* (aParentExecutionPrice - getExecutionPrice());
		this.total = aTotal + pl;

	}

	public boolean isGain() {
		return gain;
	}

	public long getParentExecutionTime() {
		return parentExecutionTime;
	}

	public long getParentExecutionPrice() {
		return parentExecutionPrice;
	}

	public long getPL() {
		return pl;
	}

	public long getTotal() {
		return total;
	}

}
