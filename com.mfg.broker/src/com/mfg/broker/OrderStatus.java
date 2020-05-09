package com.mfg.broker;

import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;

/**
 * A concrete implementation of the {@linkplain IOrderStatus} interface.
 * 
 * This class contains also the order, because in this way we have only one map
 * in the {@linkplain AbstractBroker} class.
 * 
 * 
 * @author Sergio
 * 
 */
public class OrderStatus implements IOrderStatus, Cloneable {

	@Override
	public OrderStatus clone() {
		try {
			IOrderMfg orderBack = this.fOrder;
			OrderStatus res = (OrderStatus) super.clone();
			this.fOrder = orderBack;
			return res;
		} catch (CloneNotSupportedException e) {
			throw new Error();
		}
	}

	@Override
	public String toString() {
		return "[ " + _id + ", oid " + fOrder.getId() + " st " + fStatus + "]";
	}

	private IOrderMfg fOrder;
	private EOrderStatus fStatus;
	// private int fFilled;
	// private int fRemaining;
	// private double fAvgFillPrice;
	// private int fParentId;
	// private long fLastFilledPrice;
	// private long fLastExecutionTime;
	private int _id;

	/**
	 * Creates an OrderStatus object which will store all the details that a
	 * Broker need to keep track of this order.
	 * 
	 * @param anOrder
	 *            the order which needs to be tracked.
	 */
	public OrderStatus(IOrderMfg anOrder) {
		fOrder = anOrder;
		_id = anOrder.getBrokerId();
	}

	/**
	 * @return the id of this order status translated to the client's realm.
	 */
	@Override
	public int getOrderId() {
		return _id;
	}

	@Override
	public EOrderStatus getStatus() {
		return fStatus;
	}

	// @Override
	// public int getFilled() {
	// return fFilled;
	// }

	// @Override
	// public int getRemaining() {
	// return fRemaining;
	// }

	// @Override
	// public double getAverageFillPrice() {
	// return fAvgFillPrice;
	// }

	// @Override
	// public int getParentOrderId() {
	// return fParentId;
	// }

	// @Override
	// public long getLastFilledPrice() {
	// return fLastFilledPrice;
	// }

	// @Override
	// public long getLastExecutionTime() {
	// return fLastExecutionTime;
	// }

	@Override
	public boolean isTotallyExecuted() {
		return this.fStatus == EOrderStatus.TOTAL_FILLED;
	}

	/**
	 * sets this order as totally filled.
	 * 
	 * This is usually called from the broker simulator
	 * 
	 * @param anExecutionTime
	 *            the execution time of this order
	 * 
	 * @param anExecutionPrice
	 *            the execution price.
	 */
	public void setTotalFilled(long anExecutionTime, long anExecutionPrice) {
		// this.fAvgFillPrice = anExecutionPrice;
		// this.fFilled = fOrder.getQuantity();
		// this.fLastExecutionTime = anExecutionTime;
		// this.fParentId = fOrder.getParent() == null ? -1 : fOrder.getParent()
		// .getId();
		// this.fRemaining = 0;
		this.fStatus = EOrderStatus.TOTAL_FILLED;

	}

	public void setStatus(EOrderStatus aStatus) {
		this.fStatus = aStatus;

	}

	public IOrderMfg getOrder() {
		return fOrder;
	}

	/**
	 * Sets the order id (translating from external to internal).
	 * 
	 * <p>
	 * This method is used by MultiTEA to give to the outside a coherent view of
	 * the order, and not obliging the client to memorize the external ids.
	 * 
	 * @param newId
	 */
	public void setOrderId(int newId) {
		_id = newId;
	}

}
