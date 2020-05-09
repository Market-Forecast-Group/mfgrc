package com.mfg.tea.db;

import org.mapdb.DB;

import com.mfg.broker.orders.OrderImpl;
import com.mfg.tea.db.Db.OBJECTS;

/**
 * This package provides methods to add/query events in a trading run. All
 * events share some common fields, and have the link to the particular event
 * which is stored in db
 * 
 * <p>
 * I have not yet decided if the relationship is bidirectional or not, for now
 * we can go only from parent to child.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public class Events {

	@SuppressWarnings("unused")
	private static DB _db;

	// private static Long _enventsSequence;

	/**
	 * Creates a new event which signals an order has been submitted.
	 * 
	 * @param aRunIdentifier
	 * @param aOrder
	 */
	public static void newPutOrderEvent(int aRunIdentifier, OrderImpl aOrder) {

		_putGenericEventsFields();

	}

	/**
	 * Every event has a common set of fields which are used for all the events
	 * types.
	 */
	private static long _putGenericEventsFields() {
		// long newEvId = _enventsSequence.incrementAndGet();

		long newEvId = DbKeyFieldHelper
				.getNewObjectIdLong(OBJECTS.EVENT_OBJECT);

		return newEvId;
	}

	/**
	 * Init the events package, it will simply create the sequence generator for
	 * the events.
	 * 
	 * @param aInstance
	 *            the instance which has been just created.
	 */
	public static void initalize(Db aInstance) {

		_db = aInstance._db;
		// String eventsSequenceKey = Db.OBJECTS.EVENT_OBJECT.getPrefix()
		// + Db.SEQUENCE_SUFFIX;
		// _enventsSequence = _db.getAtomicLong(eventsSequenceKey);
	}

}
