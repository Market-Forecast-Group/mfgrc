package com.mfg.broker.orders;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.warn;

import java.io.Serializable;
import java.util.ArrayList;

import com.mfg.broker.IOrderMfg;
import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.XmlIdentifier;

/**
 * The implementation of the order interface.
 * 
 * <p>
 * This is the base class for the order
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class OrderImpl extends XmlIdentifier implements IOrderMfg, Serializable {

	/*
	 * The problem with this class is that it holds state and this is not so
	 * good. The only state should be in the class SimulatedOrder
	 * 
	 * We may have here a state which is the state of the order inside the
	 * broker, but this is not the state in the simulated order, this is
	 * different.
	 */

	/**
	 * This state is used only in simulation to assure that we handle correctly
	 * the order, that for every modification of this structure the market
	 * simulator is notified and that it never tries to work on an order which
	 * has been modified from the outside.
	 */
	public static enum EState {
		BEFORE_FIRST_SEND, /**
		 * Only this state is usable from the market
		 * simulator
		 */
		SENT, MODIFIED, CANCELLED
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6568918657711725483L;

	public static final long INVALID_TEA_ID = -1;

	// public static OrderImpl cloneWithStartingId(OrderImpl source,
	// int newStartingIdPar) {
	// int newStartingId = newStartingIdPar;
	// OrderImpl target = null;
	// try {
	// target = source.clone();
	// } catch (CloneNotSupportedException e) {
	// throw new IllegalStateException(e);
	// }
	// target._id = newStartingId++;
	// for (IOrderMfg child : target._children) {
	// ((OrderImpl) child)._id = newStartingId++;
	// ((OrderImpl) child)._parent = target;
	// }
	// return target;
	// }

	public static void main(String args[]) {
		System.out.println("test of the orders.");

		MarketOrder mo = new MarketOrder(44, ORDER_TYPE.BUY, 2);

		String ser = mo.serializeToString();
		System.out.println(ser);

		OrderImpl oo = (OrderImpl) XmlIdentifier.createFromString(ser);

		String ser1 = oo.serializeToString();

		if (ser.compareTo(ser1) != 0) {
			throw new RuntimeException("not equals");
		}

		LimitOrder lo = new LimitOrder(55, ORDER_TYPE.BUY, 9, 10225);
		lo.setTakeProfit(new LimitOrder(66, ORDER_TYPE.SELL, 9, 20025));
		lo.setStopLoss(new StopOrder(554, ORDER_TYPE.SELL, 9, 9025));
		ser = lo.serializeToString();

		System.out.println(ser);
	}

	protected EState _state = EState.BEFORE_FIRST_SEND;

	// /**
	// * These are used to specify the limit and auxiliary price of the order.
	// * <p>
	// * They are private, you can modify them ONLY in this class, please
	// */
	// private int _priceOffset = 0;

	private int _limitPrice = 0;

	private int _auxPrice = 0;

	// private int _limitOffset = 0;

	private IOrderMfg.EAccountRouting _routing = IOrderMfg.EAccountRouting.AUTOMATIC_ROUTE;

	private OrderChildType childType;
	private String _confirmationMessage;
	/**
	 * The type of the order cannot change after the order is issued
	 */
	private final ORDER_TYPE _type;

	/**
	 * This is the parent order. if the parent is null the order is already a
	 * parent.
	 */
	private IOrderMfg _parent;

	/**
	 * This is the execution type.
	 */
	private final EXECUTION_TYPE _execType;

	// /**
	// * This value is used to build a new id for every order.
	// */
	// protected static AtomicInteger _nextId = new AtomicInteger(1);

	/**
	 * This is the identification for this order
	 */
	private int _id;
	/**
	 * This is the quantity to buy or sell.
	 */
	private int _quantity;
	private ArrayList<IOrderMfg> _children = new ArrayList<>();

	private boolean _playSound = false;

	private String _soundPath;

	/**
	 * This is the broker id, which is the id set by the broker. This id is not
	 * globally unique but it is meant to be unique across the run of the
	 * application. It is different from the _id only in case of the RealBroker.
	 */
	private int _brokerId = -1;

	private String _strategyId;

	private String _tradingSymbol;

	@SuppressWarnings("unused")
	private String _shellId;

	/**
	 * This is the long identifier which univocally identifies this order in the
	 * database.
	 */
	private long _teaId = INVALID_TEA_ID;

	protected OrderImpl(int aId, IOrderMfg parent, ORDER_TYPE type,
			EXECUTION_TYPE execType, int quantity) {
		this(parent, type, execType, quantity, aId);
	}

	protected OrderImpl(IOrderMfg parent, ORDER_TYPE type,
			EXECUTION_TYPE execType, int quantityPar, int id) {

		int quantity = type == ORDER_TYPE.BUY ? quantityPar : Math
				.abs(quantityPar) * -1;

		if (((type == ORDER_TYPE.BUY) && quantity <= 0)
				|| ((type == ORDER_TYPE.SELL) && quantity >= 0)) {
			throw new IllegalArgumentException(
					"Buy negative or sell positive quantity");
		}

		_parent = parent;
		_type = type;
		_execType = execType;
		_quantity = quantity;
		_id = id;

	}

	/**
	 * This is the standard constructor, you cannot call it directly, because we
	 * have more specialized order types.
	 * 
	 * @param type
	 *            the type of the order @see ORDER_TYPE
	 * @param execType
	 *            The execution type. This is set by the derived class
	 * @param quantity
	 *            The quantity to buy or sell.
	 */
	protected OrderImpl(ORDER_TYPE type, EXECUTION_TYPE execType, int quantity,
			int id) {
		this(null, type, execType, quantity, id);
	}

	/**
	 * sets the child type.
	 * <p>
	 * This was a public method but it was error prone, now the child type is
	 * set automatically from the {@link #setStopLoss(IOrderMfg)} and
	 * {@link #setTakeProfit(IOrderMfg)} methods.
	 * 
	 * @param aChildType
	 *            the order child type
	 */
	private void _setChildType(OrderChildType aChildType) {
		this.childType = aChildType;
	}

	/**
	 * This is used to put the order in a stable state, usable for the market
	 * simulator.
	 */
	public void acknowledged() {
		if (_state == EState.CANCELLED) {
			throw new IllegalStateException(
					"You are trying to resend the same cancelled order!");
		}
		assert (_state != EState.SENT) : "You cannot acknowledge twice" + this;
		_state = EState.SENT;
	}

	public void cancelled() {
		assert (_state == EState.MODIFIED || _state == EState.SENT) : "wrong state "
				+ _state + " for order " + this;
		_state = EState.CANCELLED;
	}

	@Override
	public OrderImpl clone() {
		OrderImpl res = null;
		try {
			res = (OrderImpl) super.clone();
			res._children = new ArrayList<>();
			for (IOrderMfg child : _children) {
				res._children.add(((OrderImpl) child).clone());
			}
		} catch (CloneNotSupportedException e) {
			assert (false);
		}
		return res;
	}

	@Override
	public int getAbsQuantity() {
		return Math.abs(_quantity);
	}

	/**
	 * @return the account routing for this order.
	 */
	@Override
	public IOrderMfg.EAccountRouting getAccountRouting() {
		return _routing;
	}

	@Override
	public int getAuxPrice() {
		return this._auxPrice;
	}

	@Override
	public int getBrokerId() {
		return _brokerId;
	}

	// @Override
	// public ArrayList<Integer> getChildrenOpenings() {
	// return _childrenOP;
	// }

	@Override
	public IOrderMfg getChildAt(int index) {
		return _children.get(index);
	}

	// from IOrderMfg
	@Override
	public ArrayList<IOrderMfg> getChildren() {
		return _children;
	}

	@Override
	public OrderChildType getChildType() {
		return childType;
	}

	@Override
	public String getConformationMessage() {
		return _confirmationMessage;
	}

	@Override
	public EXECUTION_TYPE getExecType() {
		return _execType;
	}

	/**
	 * Simply access the id field, read only
	 * 
	 * @return the id for this object.
	 */
	@Override
	public int getId() {
		return _id;
	}

	// @Override
	// public int getLimitOffset() {
	// return _limitOffset;
	// }

	@Override
	public int getLimitPrice() {
		return _limitPrice;
	}

	@Override
	public int getOpeningPrice() {
		throw new IllegalStateException("getOpeningPrice ambiguous here "
				+ this);
	}

	@Override
	public IOrderMfg getParent() {
		return _parent;
	}

	// public double getPriceOffset() {
	// return _priceOffset;
	// }

	@Override
	public int getQuantity() {
		return _quantity;
	}

	/**
	 * @return the real routed account. LONG or SHORT. If automatic it will then
	 *         follow the logic (Parent buy -> LONG, child buy -> SHORT, etc..)
	 */
	@Override
	public EAccountRouting getRoutedAccount() {
		if (_routing != IOrderMfg.EAccountRouting.AUTOMATIC_ROUTE) {
			return _routing;
		}

		// ok, the route is the automatic route... so...
		if (getParent() != null) {
			// I am a child
			if (getType() == ORDER_TYPE.BUY) {
				// child buy -> short
				return IOrderMfg.EAccountRouting.SHORT_ACCOUNT;
			}
			// child sell -> long
			return IOrderMfg.EAccountRouting.LONG_ACCOUNT;
		}
		// I am a parent
		if (getType() == ORDER_TYPE.BUY) {
			// parent buy -> long
			return IOrderMfg.EAccountRouting.LONG_ACCOUNT;
		}
		// parent sell -> short
		return IOrderMfg.EAccountRouting.SHORT_ACCOUNT;
	}

	@Override
	public IOrderMfg getSibling() {
		if (_parent == null) {
			throw new IllegalStateException();
		}
		for (IOrderMfg oc : _parent.getChildren()) {
			if (oc.getId() != _id) {
				return oc;
			}
		}
		throw new IllegalStateException(); // should not arrive here
	}

	public String getSoundPath() {
		return _soundPath;
	}

	public EState getState() {
		return _state;
	}

	@Override
	public int getStopLoss() {
		for (IOrderMfg child : _children) {
			if (child.getChildType() == OrderChildType.STOP_LOSS) {
				return child.getOpeningPrice();
			}
		}
		return -1;
	}

	@Override
	public String getStrategyId() {
		return _strategyId;
	}

	@Override
	public int getTakeProfit() {
		for (IOrderMfg child : _children) {
			if (child.getChildType() == OrderChildType.TAKE_PROFIT) {
				return child.getOpeningPrice();
			}
		}
		return -1;
	}

	public long getTeaId() {
		return _teaId;
	}

	public String getTradingSymbol() {
		return _tradingSymbol;
	}

	/**
	 * @return The type of this order
	 */
	@Override
	public ORDER_TYPE getType() {
		return _type;
	}

	@Override
	public boolean isBuy() {
		return this._quantity > 0;
	}

	@Override
	public boolean isChild() {
		if (_parent == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isLong() {
		return (isChild() == (getType() == ORDER_TYPE.SELL));
	}

	public boolean isPlaySound() {
		return _playSound;
	}

	@Override
	public boolean isSentToLongAccount() {
		switch (getAccountRouting()) {
		case AUTOMATIC_ROUTE:
			return isLong();
		case COMMON_ACCOUNT:
			return true;
		case LONG_ACCOUNT:
			return true;
		case SHORT_ACCOUNT:
			return false;
		default:
			throw new IllegalStateException();

		}
	}

	/**
	 * Set the account routing for this order.
	 * 
	 * @param aRoute
	 *            the routing for this account. Please note that the default is
	 *            the automatic route.
	 */
	public void setAccountRouting(IOrderMfg.EAccountRouting aRoute) {
		if (_state != EState.BEFORE_FIRST_SEND) {
			throw new IllegalStateException(
					"Cannot set the routing after being sent " + this);
		}

		_routing = aRoute;
	}

	public void setAuxPrice(long ap) {
		_auxPrice = MathUtils.longToIntSafe(ap);
		if (_state == EState.SENT) {
			_state = EState.MODIFIED;
		}
	}

	public void setBrokerId(int aId) {
		_brokerId = aId;
	}

	public void setConfirmationMessage(String confirmationMessage) {
		_confirmationMessage = confirmationMessage;
	}

	// public void setLimitOffset(long lo) {
	// debug_var(392562, "order " + _id + " set limit offset " + lo);
	// _limitOffset = MathUtils.longToIntSafe(lo);
	// if (_state == EState.SENT) {
	// _state = EState.MODIFIED;
	// }
	// }

	public void setLimitPrice(int limitPrice) {
		_limitPrice = MathUtils.longToIntSafe(limitPrice);
		if (_state == EState.SENT) {
			_state = EState.MODIFIED;
		}
	}

	/**
	 * @param op
	 *            the opening price
	 */
	public void setOpeningPrice(int op) {
		// In this class this function is ambiguous... please override it.
		assert (false) : " no setOpeningPrice in " + this;
	}

	public void setParent(IOrderMfg parent) {
		if (_state != EState.BEFORE_FIRST_SEND) {
			throw new IllegalStateException(
					"Cannot set parent after being sent");
		}
		_parent = parent;

		// I must set the routing as the parent routing.
		setAccountRouting(_parent.getAccountRouting());
	}

	public void setPlaySound(boolean playSound) {
		_playSound = playSound;
	}

	// public void setPriceOffset(long a_priceOffset) {
	// debug_var(399119, "order " + _id + " set price offset " + a_priceOffset
	// + " state " + _state + " old was: " + _priceOffset);
	// _priceOffset = MathUtils.longToIntSafe(a_priceOffset);
	// if (_state == EState.SENT) {
	// _state = EState.MODIFIED;
	// }
	// }

	public void setQuantity(int quantity) {
		if (_quantity * quantity < 0) {
			throw new IllegalArgumentException(
					"You cannot change the quantity sign.");
		}

		debug_var(291953, "order " + _id + " set quantity " + quantity);

		_quantity = quantity;
		for (IOrderMfg order : _children) {
			OrderImpl oi = (OrderImpl) order;
			oi.setQuantity(-quantity);
		}
		if (_state == EState.SENT) {
			_state = EState.MODIFIED;
		}
	}

	public void setShellId(String shellId) {
		_shellId = shellId;

	}

	public void setSoundPath(String soundPath) {
		_soundPath = soundPath;
	}

	/**
	 * sets the stop loss of this parent order.
	 * <p>
	 * The stop loss is defined as an order which usually limits the loss, even
	 * if, sometimes, it can also be used to exit in gain from a position,
	 * because the stop is moved until it is above the entry price.
	 * 
	 * @param aStopLoss
	 *            the stop loss, the
	 */
	public void setStopLoss(IOrderMfg aStopLoss) {
		if (_state != EState.BEFORE_FIRST_SEND) {
			throw new IllegalStateException(
					"Cannot set stop loss after being sent order " + this);
		}
		// _children.ensureCapacity(2);
		if (_children.size() >= 2) {
			throw new IllegalStateException("too many children "
					+ _children.size());
		}

		_children.add(aStopLoss);
		((OrderImpl) aStopLoss)._setChildType(OrderChildType.STOP_LOSS);
		((OrderImpl) aStopLoss).setParent(this);
	}

	/**
	 * Sets the name of the strategy which has sent the order.
	 * <p>
	 * There may be different strategies in the same portfolio.
	 * 
	 * @param strategyName
	 */
	public void setStrategyId(String strategyName) {
		_strategyId = strategyName;
	}

	/**
	 * sets the take profit for this order.
	 * 
	 * <p>
	 * A take profit is usually a limit order which closes the position in gain.
	 * 
	 * @param aTakeProfit
	 */
	public void setTakeProfit(IOrderMfg aTakeProfit) {
		if (_state != EState.BEFORE_FIRST_SEND) {
			throw new IllegalStateException(
					"Cannot set child after being sent, order " + this);
		}
		if (_children.size() >= 2) {
			throw new IllegalStateException("too many children "
					+ _children.size());
		}
		// _children.ensureCapacity(2);
		_children.add(aTakeProfit);
		((OrderImpl) aTakeProfit)._setChildType(OrderChildType.TAKE_PROFIT);
		((OrderImpl) aTakeProfit).setParent(this);
	}

	public void setTeaId(long newId) {
		/*
		 * The tea id must be the same or a set for the first time
		 */
		assert (_teaId == INVALID_TEA_ID || newId == _teaId);
		_teaId = newId;
	}

	public void setTradingSymbol(String tradingSymbol) {
		_tradingSymbol = tradingSymbol;
	}

	@Override
	public String toString() {
		String s = (getParent() == null) ? "Parent " : "Child ";
		return "ID " + " (" + _id + ") " + s + _execType + ", "
				+ _type.toString() + " " + _quantity + " at " + _limitPrice
				+ " TP " + getTakeProfit() + " SL " + getStopLoss();
	}

	public String toString(StepDefinition tick) {
		String s = (getParent() == null) ? "Parent " : "Child ";
		return "ID " + " (" + _id + ") " + s + _execType + ", "
				+ _type.toString() + " " + _quantity + " at "
				+ MathUtils.getPriceFormat(tick.roundLong(_limitPrice))
				+ " TP "
				+ MathUtils.getPriceFormat(tick.roundLong(getTakeProfit()))
				+ " SL "
				+ MathUtils.getPriceFormat(tick.roundLong(getStopLoss()));
	}

	/**
	 * this method turns the order into one that will be executed as fast as
	 * possible....
	 * 
	 * @param currentPrice
	 *            unused
	 * @param tickSize
	 *            unused
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes
	public void turnIntoMarket(int currentPrice, int tickSize) {
		warn("turn into market called");
	}

}
