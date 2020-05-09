package com.mfg.broker.orders;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;

/**
 * A class which has only utility methods (static) regarding orders.
 * 
 * <p>
 * This class will substitute the various methods inside the enums in
 * {@link IOrderMfg} which were really ugly, even if smart.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class OrderUtils {

	/***
	 * the other order, if this is a BUY opposite order is a SELL.
	 * 
	 * @return
	 */
	public static ORDER_TYPE getOpposite(ORDER_TYPE aType) {
		switch (aType) {
		case BUY:
			return ORDER_TYPE.SELL;
		case SELL:
			return ORDER_TYPE.BUY;
		default:
			throw new IllegalStateException();
		}
	}

	public static int getSign(ORDER_TYPE aType) {
		switch (aType) {
		case BUY:
			return 1;
		case SELL:
			return -1;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * asks if this order execution type is from the market family.
	 * 
	 * @return true if it is from the market family.
	 */
	public static boolean isMarketFamily(EXECUTION_TYPE aType) {
		switch (aType) {
		case LIMIT:
			// case LIT:
		case STOP_LIMIT:
			// case TRAILING_LIT:
			return false;
		case MARKET:
		case MIT:
		case STOP:
			// case TRAILING_MIT:
			// case TRAILING_STOP:
			return true;
		default:
			break;
		}

		throw new IllegalStateException();
	}

	/**
	 * asks if this order execution type is an stop family order.
	 * 
	 * @return true if it is an stop order family.
	 */
	public static boolean isStop(EXECUTION_TYPE aType) {
		switch (aType) {
		case LIMIT:
			// case LIT:
		case MARKET:
		case MIT:
			// case TRAILING_LIT:
			// case TRAILING_MIT:
			return false;
		case STOP:
		case STOP_LIMIT:
			// case TRAILING_STOP:
			return true;
		default:
			break;
		}
		throw new IllegalStateException();
	}

	/**
	 * moves a price to the non executing side of this order execution type.
	 * 
	 * @param otype
	 *            the order type.
	 * @param basePrice
	 *            the initial price
	 * @param deltaPrice
	 *            the delta to move the price.
	 * @return the price moved to the non executing side of this order execution
	 *         type.
	 */
	public static int moveToNonExecutingDirection(EXECUTION_TYPE aExecType,
			ORDER_TYPE otype, long basePrice, double deltaPrice) {
		if (OrderUtils.isStop(aExecType))
			return OrderUtils.somePricesWorse(otype, basePrice, deltaPrice);
		return OrderUtils.somePricesBetter(otype, basePrice, deltaPrice);
	}

	/**
	 * gives a new price at distance delta from the specified price in the
	 * direction to where the price is better for this order type. i.e.
	 * price-delta for a buy type, and price+delta for a sell type
	 * 
	 * @param price
	 *            base price
	 * @param delta
	 *            distance to the new price
	 * @return a new price at delta from the base price, in the direction of
	 *         price improvement.
	 */
	public static int somePricesBetter(ORDER_TYPE aType, long price,
			double delta) {
		return (int) (price - (OrderUtils.getSign(aType) * delta));
	}

	/**
	 * gives a new price at distance delta from the specified price in the
	 * direction to where the price is worse for this order type.
	 * <p>
	 * That is price-delta for a sell type, and price+delta for a buy type
	 * 
	 * @param price
	 *            base price
	 * @param delta
	 *            distance to the new price
	 * @return a new price at delta from the base price, in the opposite
	 *         direction of price improvement.
	 */
	public static int somePricesWorse(ORDER_TYPE aType, long price, double delta) {
		return OrderUtils.somePricesBetter(OrderUtils.getOpposite(aType),
				price, delta);
	}

	/**
	 * some ticks away from the specified price in the direction to where the
	 * price is better for this order type. i.e. some ticks less for a buy
	 * order,...
	 * 
	 * @param price
	 *            base price
	 * @param ticks
	 *            ticks to vary the price
	 * @return a new price some ticks better than price.
	 */
	public static long someTicksBetter(ORDER_TYPE aType, int price, int ticks,
			int tickSize) {
		return price - OrderUtils.getSign(aType) * ticks * tickSize;
	}

	private OrderUtils() {
		// do not create
	}
}
