package com.mfg.broker.events;

import java.util.ArrayList;
import java.util.List;

import com.mfg.broker.IOrderMfg;

/**
 * Received order event.
 * 
 * @author arian
 * 
 */
public class ReceivedOrderMessage extends OrderMessage {

	private List<OrderMessage> innerEvents;

	public ReceivedOrderMessage(String aSource, boolean isParentExecuted, IOrderMfg order) {
		super(isParentExecuted ? ADDED : RECEIVED, aSource, order);
		innerEvents = new ArrayList<>();

		if (!isParentExecuted) {
			for (IOrderMfg child : order.getChildren()) {
				innerEvents.add(new OrderMessage(RECEIVED, aSource, child));
			}
		}
	}

	public List<OrderMessage> getInnerEvents() {
		return innerEvents;
	}

}
