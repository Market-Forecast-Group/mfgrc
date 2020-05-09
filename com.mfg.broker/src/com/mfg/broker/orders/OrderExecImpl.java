package com.mfg.broker.orders;

import java.util.Date;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.MarketSimulator;

/**
 * A simple class which implements the {@link IOrderExec} information. Probably
 * it may be removed sometimes, or, better, the inheritance of this class may be
 * changed, because this is used only by the {@link MarketSimulator}, instead we
 * need another class which is capable of being transported by the socket. This
 * class is network friendly, but maybe I do not want to transmit the entire
 * order on the wire.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class OrderExecImpl implements IOrderExec {

	private final long fExecTime;
	private final long fExecPrice;
	public final IOrderMfg order;
	private int _id;

	@Override
	public String toString() {
		return "[ intID(" + _id + ") " + order + "," + fExecPrice + ","
				+ new Date(fExecTime) + "]";
	}

	/**
	 * 
	 * It is assumend that the order is fully executed, because this comes from
	 * the market simulator.
	 * 
	 */
	public OrderExecImpl(IOrderMfg aOrder, long execTime, long execPrice) {
		if ((aOrder.getQuantity() == 0) || (execPrice <= 0) || (execTime <= 0)) {
			throw new IllegalArgumentException("validation error q = "
					+ aOrder.getQuantity() + " price " + execPrice + " time "
					+ execTime);
		}

		this.fExecTime = execTime;
		this.fExecPrice = execPrice;
		order = aOrder;
		_id = aOrder.getId();
	}

	@Override
	public int getOrderId() {
		return _id;
	}

	@Override
	public long getExecutionTime() {
		return fExecTime;
	}

	@Override
	public long getExecutionPrice() {
		return fExecPrice;
	}

	// public void setOrderId(int newId) {
	// _id = newId;
	// }

}
