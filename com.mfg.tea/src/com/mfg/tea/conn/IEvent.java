package com.mfg.tea.conn;

/**
 * The interface {@link IEvent} is a container for all the events which have
 * been recorded in this trading session.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IEvent {

	/**
	 * There are several event types in a trading session. This enumerates all
	 * of them.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	enum EEventType {
		ORDER_PLACED, ORDER_EXECUTED, ORDER_STATUS_CHANGED, ORDER_MODIFIED
	}

	/**
	 * returns the event type
	 * 
	 * @return the event type.
	 */
	public EEventType getType();

}
