package com.mfg.tea.db;

import org.mapdb.BTreeMap;
import org.mapdb.DB;

import com.mfg.broker.IOrderStatus;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.tea.db.Db.OBJECTS;

/**
 * This package will handle the orders objects, their creation and their life
 * through all the modifications.
 * 
 * <p>
 * As it has written in the {@link Db} class all the entries, when written, are
 * immutable so in reality a modification for an order is like a modification of
 * a file in a source control system: all the two versions persist.
 * 
 * <p>
 * In here it is more so: the first version will persist entirely, the database
 * will <b>not</b> try to alter the previous keys, even to save space.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class Orders {

	private static final String ID_FIELD = "id";

	/**
	 * 
	 * Here I have to store the correspondence between the external ids and the
	 * internal (tea) identifiers.
	 * 
	 * I do not want the external world to mess with those.
	 * 
	 * 
	 */

	/**
	 * fills the db with the given order in its first version.
	 * 
	 * @param orderKey
	 *            the basic key underwich the order is known inside the db.
	 * @param aOrder
	 *            the order to be persisted.
	 */
	private static void _fillOrderFields(long newId, OrderImpl aOrder) {

		String orderKey = Db.OBJECTS.ORDER_OBJECT.getPrefix() + new Long(newId);

		String orderType = orderKey + ".type";
		_teaMap.put(orderType, aOrder.getType().toString());

		String quantity = orderKey + ".quantity";
		_teaMap.put(quantity, Integer.toString(aOrder.getQuantity()));

		/*
		 * Maybe the id is not useful, as the id is implicit the order id in
		 * which the order is sent, but I cannot be sure about this, so it is
		 * better to save it.
		 */

		DbKeyFieldHelper.putFieldValue(OBJECTS.ORDER_OBJECT, newId, ID_FIELD,
				aOrder.getId());

		/*
		 * This may not be good because the status counter is a variable value
		 * in the map and I would like only immutable artifacts in the map.
		 * Every key is supposed to have only one value.
		 */
		// _teaMap.put(orderKey + ".statusCounter", "0");

		/*
		 * This is the integer which counts the events for this order, the
		 * events may be executions cancellations etc..
		 */
		// _db.createAtomicInteger(orderKey + Db.SEQUENCE_SUFFIX, 0);

		_teaMap.put(orderKey + ".limitPrice",
				Integer.toString(aOrder.getLimitPrice()));

		_teaMap.put(orderKey + ".auxPrice",
				Integer.toString(aOrder.getAuxPrice()));

		_teaMap.put(orderKey + ".execType", aOrder.getExecType().toString());

		if (aOrder.isChild()) {

			long parentId = ((OrderImpl) aOrder.getParent()).getTeaId();
			_teaMap.put(orderKey + ".parent", Long.toString(parentId));

			/*
			 * I have also to update the parent children relationship.
			 */
			String parentKey = Db.OBJECTS.ORDER_OBJECT.getPrefix() + parentId;
			switch (aOrder.getChildType()) {
			case STOP_LOSS:
				_teaMap.put(parentKey + ".sl", Long.toString(newId));
				break;
			case TAKE_PROFIT:
				_teaMap.put(parentKey + ".tp", Long.toString(newId));
				break;
			default:
				assert (false);
				break;
			}

		} else {
			/*
			 * The parent is null, because I am a parent. I do not have to put
			 * the key, it is implicit
			 */
		}

	}

	public static void initialize(Db aDb) {
		_db = aDb._db;
		_teaMap = Db.i()._teaMap;
	}

	/**
	 * Modifies the existing order in the database. A modification is really a
	 * new insertion with the <b>same</b> tea identifier but a different
	 * timestamp.
	 * 
	 * so we may have
	 * 
	 * o_34.sent = $date o_34.child[0] = 35
	 * 
	 * <p>
	 * The order 1 has been sent to the broker at a certain date.
	 * 
	 * <p>
	 * After 3 minutes the user wants to change the order, so in this case the
	 * 
	 * 
	 * o_34.quantity.1 = 5
	 * 
	 * <p>
	 * Some fields have a version attached to them. In this case I store the
	 * history of the order as a new "version" of it.
	 * 
	 * <p>
	 * version zero is not stored as is, it is the "default" version of any
	 * field. Other versions of the same key will have a % sign attached to them
	 * If some field does not change it does not have a new version
	 * 
	 * 
	 * o_34.sent = $date
	 * 
	 * This is the default version, the "zero" version. For most objects this is
	 * the final version.
	 * 
	 * o_34%1.sent ... maybe not, because I do not have a new version of the
	 * entire object. Only the fields are versioned.
	 * 
	 * o_34.sent%1 = $date1
	 * 
	 * o_34.limitPrice%1 = 1050
	 * 
	 * (it changes only the limit price)
	 * 
	 * then it changes the aux price, this is a second version.
	 * 
	 * o_34.sent%2 = $date2
	 * 
	 * o_34.auxPrice%2 = 1395
	 * 
	 * @param aOrder
	 */
	public static void modifyOrder(OrderImpl aOrder) {
		//

	}

	/**
	 * Creates a new orders status record in database.
	 * 
	 * @param aDbId
	 *            the identifier for the order, it is the unique db TEA
	 *            identifier as returned from {@link #putNewOrder(OrderImpl)}
	 * @param aStatus
	 *            the status of this order.
	 */
	public static void newOrderStatus(long aDbId, IOrderStatus aStatus) {
		//
	}

	/**
	 * The order is not decomposed. If it has children they will be added later.
	 * 
	 * 
	 * @param aOrder
	 *            the order which the user wants to store in the database.
	 * @return
	 */
	public static long putNewOrder(OrderImpl aOrder) {

		// long newId = _oid.incrementAndGet();
		long newId = DbKeyFieldHelper.getNewObjectIdLong(OBJECTS.ORDER_OBJECT);

		_fillOrderFields(newId, aOrder);

		return newId;
	}

	// private static Atomic.Long _oid;

	//

	@SuppressWarnings("unused")
	private static DB _db;

	private static BTreeMap<String, String> _teaMap;
}
