package com.mfg.broker.events;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.orders.OrderUtils;

/**
 * A broker event associated to an order.
 * 
 * @author arian
 * 
 */
public class OrderMessage extends TradeMessage {
	protected double executionPrice;
	private final EXECUTION_TYPE execType;
	private final boolean isParent;
	private final int orderId;
	private final ORDER_TYPE orderType;
	private final int orderQuantity;
	private final double orderAuxPrice;
	private final double orderLimitPrice;
	private final EAccountRouting orderRouting;
	private final String fAccountName;

	// private final EAccountRouting accountRouting;

	public OrderMessage(TradeMessageType type, String aSource, IOrderMfg order,
			double executionPrice1, String accountName) {
		super(type, aSource);
		execType = order.getExecType();
		isParent = !order.isChild();
		orderId = order.getId();
		orderType = order.getType();
		orderQuantity = order.getQuantity();
		orderAuxPrice = order.getAuxPrice();
		orderLimitPrice = order.getLimitPrice();
		orderRouting = order.getRoutedAccount();
		this.executionPrice = executionPrice1;
		// this.accountRouting = order.getAccountRouting();

		if (accountName != null) {
			this.fAccountName = accountName;
		} else {
			switch (orderRouting) {
			case AUTOMATIC_ROUTE:
			case COMMON_ACCOUNT:
				/*
				 * The order routing must be validated.
				 */
				throw new IllegalStateException();
			case LONG_ACCOUNT:
				this.fAccountName = "Long";
				break;
			case SHORT_ACCOUNT:
				this.fAccountName = "Short";
				break;
			default:
				this.fAccountName = "n/a";
			}
		}
	}

	public OrderMessage(TradeMessageType type, String aSource, IOrderMfg order) {
		this(type, aSource, order, -1, null);
	}

	public String getAccountName() {
		return fAccountName;
	}

	// /**
	// * @return the accountRouting
	// */
	// public EAccountRouting getAccountRouting() {
	// return accountRouting;
	// }

	@Override
	public String getEvent() {
		StringBuilder sb = new StringBuilder();
		String relation = isParent() ? "Parent" : "Child";
		String direction = isBuy() ? "Buy" : "Sell";
		String family = getExecType().toString();

		sb.append(relation).append(", ").append(direction).append(", ")
				.append(family);

		if (getType() == TradeMessage.EXECUTED) {
			sb.append(", Price=" + formatPriceWithScale(getExecutionPrice()));
		} else {
			if (!isParent()) {
				if (OrderUtils.isMarketFamily(getExecType())) {
					sb.append(", Aux="
							+ formatPriceWithScale(getOrderAuxPrice()));
				} else {
					sb.append(", Limit="
							+ formatPriceWithScale(getOrderLimitPrice()));
				}
			}
		}
		return sb.toString();
	}

	public boolean isBuy() {
		return getOrderType() == ORDER_TYPE.BUY;
	}

	public boolean isMarketFamily() {
		return OrderUtils.isMarketFamily(getExecType());
	}

	public double getExecutionPrice() {
		return executionPrice;
	}

	public EXECUTION_TYPE getExecType() {
		return execType;
	}

	public boolean isParent() {
		return isParent;
	}

	public int getOrderId() {
		return orderId;
	}

	public ORDER_TYPE getOrderType() {
		return orderType;
	}

	public int getOrderQuantity() {
		return orderQuantity;
	}

	public double getOrderAuxPrice() {
		return orderAuxPrice;
	}

	public double getOrderLimitPrice() {
		return orderLimitPrice;
	}

	public EAccountRouting getOrderRouting() {
		return orderRouting;
	}
}
