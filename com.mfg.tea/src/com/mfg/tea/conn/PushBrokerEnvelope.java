package com.mfg.tea.conn;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.utils.XmlIdentifier;

/**
 * The {@link PushBrokerEnvelope} is a "union" type which encloses the two (for
 * now) notifications that a virtual broker may have:
 * 
 * <li>the
 * {@link IVirtualBrokerListener#newExecutionNew(com.mfg.broker.IOrderExec)} and
 * the
 * 
 * <li>
 * {@link IVirtualBrokerListener#orderStatusNew(com.mfg.broker.IOrderStatus)}
 * 
 * <p>
 * In this way a variant like structure is created and this will render it
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class PushBrokerEnvelope extends XmlIdentifier {

	/**
	 * The message type in the envelope can be of two types: an order status or
	 * a new execution.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	enum MessageType {
		ORDER_STATUS, NEW_EXECUTION
	}

	public final MessageType type;

	public final Object realObject;

	public PushBrokerEnvelope(IOrderStatus aStatus) {
		type = MessageType.ORDER_STATUS;
		realObject = aStatus;
	}

	public PushBrokerEnvelope(IOrderExec anExec) {
		type = MessageType.NEW_EXECUTION;
		realObject = anExec;
	}

	public static void main(String args[]) {

		PushBrokerEnvelope pbo = new PushBrokerEnvelope(new OrderExecImpl(
				new MarketOrder(66, ORDER_TYPE.BUY, 3), 3984, 8800));

		System.out.println(pbo.serializeToString());

		PushBrokerEnvelope pbos = new PushBrokerEnvelope(new OrderStatus(
				new MarketOrder(7, ORDER_TYPE.BUY, 3)));

		System.out.println(pbos.serializeToString());

	}
}
