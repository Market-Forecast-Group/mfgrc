package com.mfg.tea.conn;

import java.util.HashMap;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.tea.db.Db;

/**
 * A simple pass by class used to catch the events for the database. The events
 * are then routed to the real listener (which may also be a stub listener
 * because the real listener is remote).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DbHookBrokerListener implements IVirtualBrokerListener {

	private IVirtualBrokerListener _listener;

	/**
	 * Key is the order id, value is the db id, unique in the database.
	 * 
	 * <p>
	 * Maybe this map should not be here, because it is the db logic to have
	 * this information.
	 */
	private HashMap<Integer, Long> _orderIdToDbId = new HashMap<>();

	public DbHookBrokerListener(IVirtualBrokerListener aListener) {
		_listener = aListener;
	}

	/**
	 * Associates the given order to the unique db identifier which is given by
	 * the Database.
	 * 
	 * @param aOrder
	 * @param dbId
	 */
	@SuppressWarnings("boxing")
	public void associate(IOrderMfg aOrder, long dbId) {
		_orderIdToDbId.put(aOrder.getId(), dbId);
	}

	@SuppressWarnings({ "static-access" })
	@Override
	public void newExecutionNew(IOrderExec anExec) {

		OrderExecImpl oei = (OrderExecImpl) anExec;

		// Long dbId = _orderIdToDbId.get(anExec.getOrderId());
		// assert (dbId != null);

		Db.i().insertExecution(oei);

		_listener.newExecutionNew(anExec);
	}

	@Override
	public void orderStatusNew(IOrderStatus aStatus) {

		OrderStatus os = (OrderStatus) aStatus;

		// Long dbId = _orderIdToDbId.get(aStatus.getOrderId());
		// assert (dbId != null);
		Db.i().insertStatus(os);
		_listener.orderStatusNew(aStatus);
	}

	@SuppressWarnings("boxing")
	public void setTeaIdForOrder(OrderImpl aOrder) {
		aOrder.setTeaId(_orderIdToDbId.get(aOrder.getId()));
	}

}
