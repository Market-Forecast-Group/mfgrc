package com.mfg.interfaces.trading;

import com.mfg.broker.IOrderMfg;

public class PositionEvent {
	private long executionTime;
	private long executionPrice;
	private boolean isLongPosition;
	protected IOrderMfg order;
	private final long _physicalTime;

	public PositionEvent(IOrderMfg aOrder, long aExecutionTime,
			long aExecutionPrice, boolean aIsLongPosition, long physicalTime) {
		super();
		this.order = aOrder;
		this.executionTime = aExecutionTime;
		this.executionPrice = aExecutionPrice;
		this.isLongPosition = aIsLongPosition;
		_physicalTime = physicalTime;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public long getExecutionPrice() {
		return executionPrice;
	}

	public boolean isLongPosition() {
		return isLongPosition;
	}

	public IOrderMfg getOrder() {
		return order;
	}

	public void setOrder(IOrderMfg aOrder) {
		this.order = aOrder;
	}

	public long getPhysicalExecutionTime() {
		return _physicalTime;
	}

}
