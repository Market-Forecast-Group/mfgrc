package com.mfg.strategy;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;

public class MyOrderReport implements IExecutionReport {

	private IOrderMfg order;
	private IExecutionReport parent;
	private long executionPrice;
	private long executionTime;
	private final long _physicalTime;

	public MyOrderReport(IOrderMfg aOrder, long aExecutionPrice,
			long aPhysicalExecutionTime, long aExecutionTime) {
		this(aOrder, aExecutionPrice, aPhysicalExecutionTime, aExecutionTime,
				null);
	}

	public MyOrderReport(IOrderMfg aOrder, long aExecutionPrice,
			long aPhysicalExecutionTime, long aExecutionTime,
			IExecutionReport aParent) {
		super();
		order = aOrder;
		executionPrice = aExecutionPrice;
		executionTime = aExecutionTime;
		parent = aParent;
		_physicalTime = aPhysicalExecutionTime;
	}

	@Override
	public IOrderMfg getOrder() {
		return order;
	}

	@Override
	public long getExecutionPrice() {
		return executionPrice;
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}

	@Override
	public int getQuantity() {
		return order.getQuantity();
	}

	@Override
	public int getOrderId() {
		return order.getId();
	}

	@Override
	public boolean isLongPosition() {
		return (!order.isChild() == (order.getType() == IOrderMfg.ORDER_TYPE.BUY));
	}

	@Override
	public IExecutionReport getParentExecutionReport() {
		return parent;
	}

	@Override
	public boolean isClosingInGain() {
		assert (parent != null) : "Cannot ask this for a parent execution report";

		if (isLongPosition()) {
			// I have gained if the price of the child is higher
			return (executionPrice > parent.getExecutionPrice());
		}
		// In the case of short position is the opposite.
		return (executionPrice < parent.getExecutionPrice());
	}

	@Override
	public long getPhysicalExecutionTime() {
		return _physicalTime;
	}

}
