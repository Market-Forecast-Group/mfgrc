package com.mfg.broker;

import java.util.ArrayList;

import com.mfg.utils.ui.IEnumWithLabel;

/**
 * This is the interface of an "Order" type, which is simply the container for a
 * request for the opening of a position.
 * 
 * <p>
 * The interface is read only, we have only "get" not "set" the order is
 * modifiable only by the Strategy.
 * 
 * <p>
 * The prices which are sent by this class are internal prices, they are not
 * (yet) converted to broker prices (with the correct scale), so they are
 * integers. This is because the order is created usually by the strategy and
 * the strategy does not know anything about the scale.
 * 
 * 
 * 
 * @author lino
 */
public interface IOrderMfg {

	/**
	 * This enumeration is used to distinguish two account types in the system.
	 * The long and the short account. There is also the automatic_route, which
	 * is the previous behavior, that is the parent buy orders go to the long
	 * account, the parent sell orders go to the short account.
	 * 
	 * Common_account is used when the broker has only one account and all the
	 * orders go there.
	 * 
	 * The default is the automatic routing.
	 */
	public enum EAccountRouting {
		LONG_ACCOUNT, SHORT_ACCOUNT, AUTOMATIC_ROUTE, COMMON_ACCOUNT
	}

	/**
	 * The enumeration is the type of execution for orders.
	 */
	public enum EXECUTION_TYPE implements IEnumWithLabel {
		MARKET, STOP, STOP_LIMIT, /* TRAILING_STOP, */LIMIT, /** market if touched */
		MIT, /** limit if touched */
		/* LIT, TRAILING_MIT, TRAILING_LIT */;

		@Override
		public String getLabel() {
			return toString();
		}
	}

	/**
	 * the order type, has two possible values: {@code BUY} or {@code SELL}.
	 */
	public enum ORDER_TYPE {
		BUY, SELL
	}

	/**
	 * Used to label a child order as Take Profit (TP) or Stop Loss (SL).
	 * 
	 * @see IOrderMfg#getChildType()
	 * @author arian
	 * 
	 */
	public enum OrderChildType {
		STOP_LOSS, TAKE_PROFIT
	}

	/**
	 * returns the absolute quantity of this order. The absolute quantity means
	 * the quantity without sign.
	 * 
	 * @return the absolute quantity (sell orders have a negative quantity).
	 */
	public int getAbsQuantity();

	/**
	 * @return the account in which this order will be put. The default is the
	 *         automatic routing.
	 */
	public EAccountRouting getAccountRouting();

	/**
	 * The aux price is used for the stoplimit and lit orders. It is the limit
	 * price used when the condition is met.
	 * 
	 * @return the aux price, if the order has not an aux price an exception is
	 *         thrown.
	 */
	public int getAuxPrice();

	/**
	 * The broker's id is the id with which the order is accepted by the broker.
	 * This is usually a unique id, but probably it is reused across days, for
	 * example TWS reuses the same broker id after midnight.
	 * 
	 * <p>
	 * This is <b>not</b> the global tea id with which the order is stored in
	 * tea database, because that is another matter.
	 * 
	 * @return the broker id, -1 means that this order is not yet accepted by
	 *         the broker. It is perfectly fine to have {@link #getId()} ==
	 *         {@link #getBrokerId()}
	 */
	public int getBrokerId();

	/**
	 * This is in reality a short form for:
	 * 
	 * this.getChildren().elementAt(index)
	 */
	public IOrderMfg getChildAt(int index);

	/**
	 * @return the vector of the children openings, this does not change, it is
	 *         fixed after the first send.
	 */
	// public ArrayList<Integer> getChildrenOpenings();

	/**
	 * @return the vector of children. It should be regarded as read only null
	 *         if this is already a child.
	 */
	public ArrayList<IOrderMfg> getChildren();

	/**
	 * Used to know the type of child: SL or TP.
	 * 
	 * @author arian
	 * @return The child type.
	 */
	public OrderChildType getChildType();

	/**
	 * Get the message to show in a "send order confirmation" dialog. If the
	 * message is <code>null</code>, then no confirmation is required.
	 * 
	 * @return The confirmation message or null if the confirmation is not
	 *         required.
	 */
	public String getConformationMessage();

	/**
	 * @return the execution type for this order
	 */
	public EXECUTION_TYPE getExecType();

	/**
	 * The id of the order.
	 * 
	 * <p>
	 * This id is in reality a local id, the ascending id for the current
	 * execution log. This means that the id starts from 1 for every run of the
	 * application
	 * 
	 * @return The id of this order.
	 */
	public int getId();

	// public int getLimitOffset();

	/**
	 * This is used to get the limit price.
	 */
	public int getLimitPrice();

	/**
	 * @return the price at which the order should be opened. Market orders do
	 *         not have an opening price.
	 */
	public int getOpeningPrice();

	/**
	 * @return the parent order, if this is a parent null is returned
	 */
	public IOrderMfg getParent();

	/**
	 * @return the quantity to buy (sell). The quantity to sell is negative.
	 */
	public int getQuantity();

	/**
	 * @return the account to which this order has been routed. Cannot be
	 *         automatic!
	 */
	public EAccountRouting getRoutedAccount();

	/**
	 * gets the sibling of this child order. It assumes that there is only one
	 * sibling for this child order.
	 * 
	 * @return the sibling of this order.
	 * @throws IllegalStateException
	 *             if this order is a parent and cannot have siblings.
	 */
	public IOrderMfg getSibling();

	/**
	 * returns the stop loss for this order. If the order is a parent the stop
	 * loss has a meaning, otherwise -1 is returned. If the order has not a stop
	 * loss even if it is a parent -2 is returned. In mfg usually -2 will not
	 * happen because we usually send orders always with a bracket.
	 * 
	 * @return the stop loss of this order. -1 means that this order is a child
	 *         and cannot have a stop loss, -2 means that this is a parent order
	 *         and has not a stop loss
	 */
	public int getStopLoss();

	/**
	 * Returns the strategy which has issued this order.
	 * <p>
	 * Every order in Mfg is issued by a strategy. The strategy name can be also
	 * "manual", and that means that the order is issued by the user herself.
	 * 
	 * @return the strategy name.
	 */
	public String getStrategyId();

	/**
	 * returns the take profit of this order. The take profit is only valid for
	 * a parent order
	 * 
	 * @return the take profit of this order. -1 if this order is a child, -2 if
	 *         this order is a parent without a take profit.
	 * 
	 */
	public int getTakeProfit();

	/**
	 * @return the type of this order (buy/sell)
	 */
	public ORDER_TYPE getType();

	/**
	 * 
	 * returns true is this is a buy order, this could mean that it is a
	 * <i>long</i> position if this order is a parent, otherwise could mean that
	 * this is a <i>short</i> position if this order is a child.
	 * 
	 * @return true if this order is a buy order
	 */
	public boolean isBuy();

	/**
	 * @return true if this is a "child" order
	 */
	public boolean isChild();

	/**
	 * returns true if the order is a long order.
	 * 
	 * @return true if this is a long order (that is a parent buy order).
	 */
	public boolean isLong();

	/**
	 * 
	 * @return true if this order has to be counted in the long account. The
	 *         long account is also the only account used if there is only one
	 *         broker account.
	 */
	public boolean isSentToLongAccount();

}
