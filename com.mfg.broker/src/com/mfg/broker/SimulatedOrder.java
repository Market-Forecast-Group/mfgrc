package com.mfg.broker;

import static com.mfg.utils.Utils.debug_var;

import com.mfg.broker.orders.OrderImpl;

/**
 * All times in this class are phyisical times.
 * 
 * <p>
 * The simulation part does not know the concept of "fake" time.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class SimulatedOrder {

	/**
	 * Each simulated order is bound to an actual order.
	 * <p>
	 * this binding is done at construction time and it is not broken. In the
	 * old app there was the possibility to change the order to a simulated
	 * order but that was only done because the order was recreated in the stub
	 * part of TEA.
	 * 
	 * <p>
	 * The order can be changed, for a modification, because TEA can be remote
	 * and there is a decoupling from the order sent by MFG and the order
	 * received from tea.
	 * 
	 */
	private IOrderMfg _o;

	/**
	 * The execution price of this order, it includes slippage and/or extra
	 * price.
	 */
	private int _executionPrice;
	private long _executionTime;

	/**
	 * This is the data saved from the order. It is copied just to save the
	 * order's prices.
	 * 
	 * The other data is kept in the order, because it is immutable
	 */
	private int _ext_limit_price;

	private int _ext_aux_price;

	// private int _ext_limit_offset;

	private int _ext_quantity;

	/**
	 * This boolean tells us if the order is enabled. A "simple" market or limit
	 * order are enabled by default. All the other types: stop, mit, lit,
	 * stop-limit are enabled only when a certain condition is met.
	 */
	private boolean _enabled = false;

	// /**
	// * This value is used for the trailing orders... these orders adjust the
	// * trigger price looking at the market conditions.
	// */
	// private long _triggerPrice;

	/**
	 * This is the limit price used in a trailing stop limit.
	 */
	// private long _limitPrice;

	/**
	 * This flag tells us if we have reached the trigger!
	 */
	// private boolean _trailingLimitGot = false;

	/**
	 * This boolean is used by the limit order family. If it is true than the
	 * execution price is the limit price, otherwise there has been a gap and I
	 * get the market price
	 */
	private boolean _triggerLimitZoneTouched = false;

	/**
	 * This variable tells the simulator the price slippage for market orders.
	 * It must be a positive quantity.
	 * 
	 * The orders affected by this are: market/mit/stop
	 */
	private final int _slippage;

	/**
	 * This variable is used as a simulated "extra" to check if a limit price is
	 * filled or not. Limit orders are not affected by slippage but they are
	 * affected by the fact that the order could remain unexecuted.
	 * 
	 * The orders affected by this parameter are: limit/lit/stoplimit
	 */
	private final int _extraPrice;

	private boolean _amIExecuted;

	/**
	 * The constructor wants an order and a time, the time when it is received
	 * (physical).
	 */
	public SimulatedOrder(IOrderMfg o, int aSlippageAmount, int aExtraTickAmount) {
		_o = o;
		_amIExecuted = false;
		((OrderImpl) o).acknowledged();
		_take_prices_and_quantity();
		_slippage = aSlippageAmount;
		_extraPrice = aExtraTickAmount;
	}

	// /**
	// * sign = +1 sell below/buy above || -1 sell above/buy below
	// *
	// * @param time
	// */
	// private void _commonTrailingStop(long price, long time, int sign) {
	// if (!_enabled) {
	//
	// // first time initialization
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// _triggerPrice = price - (_ext_aux_price * sign);
	// } else {
	// _triggerPrice = price + (_ext_aux_price * sign);
	// }
	// _enabled = true;
	//
	// } else {
	//
	// // Ok, let's see if I have to update the trigger
	// long newTrigger;
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// newTrigger = price - (_ext_aux_price * sign);
	// if (sign > 0) {
	// _triggerPrice = Math.max(_triggerPrice, newTrigger);
	// } else {
	// _triggerPrice = Math.min(_triggerPrice, newTrigger);
	// }
	// } else {
	// newTrigger = price + (_ext_aux_price * sign);
	// if (sign > 0) {
	// _triggerPrice = Math.min(_triggerPrice, newTrigger);
	// } else {
	// _triggerPrice = Math.max(_triggerPrice, newTrigger);
	// }
	// }
	//
	// }
	// }

	/**
	 * This method simply is able to process every limit price. The method takes
	 * cares of the possible gaps.1
	 */
	private void _limitHandlingInternal(long limitPrice, boolean isBuy,
			long price, long time) {

		// handling of the trigger zone...
		if (!_triggerLimitZoneTouched) {

			if ((isBuy && (price <= limitPrice && price >= (limitPrice - _extraPrice)))
					|| (!isBuy && (price >= limitPrice && price <= (limitPrice + _extraPrice)))) {
				_triggerLimitZoneTouched = true;
			}
		}

		// Ok,

		if (isBuy) {

			if (limitPrice - _extraPrice >= price) {
				// Ok, I can be executed!

				// was there a gap?
				if (!_triggerLimitZoneTouched) {
					// a gap, so the execution price is the market price
					_executionPrice = (int) (price + _slippage);
				} else {
					// No gap... the execution price is the limit price.
					_executionPrice = (int) limitPrice;
				}

				_executionTime = time;
				_amIExecuted = true;
			}
		} else {
			// this is a sell
			if (limitPrice + _extraPrice <= price) {

				if (!_triggerLimitZoneTouched) {
					// this is a gap...
					_executionPrice = (int) (price - _slippage);
				} else {
					_executionPrice = (int) limitPrice;
				}
				_executionTime = time;
				_amIExecuted = true;
			}
		}
	}

	private void _nextPriceLimit(long price, long time) {
		// limit is buy below sell above...
		_limitHandlingInternal(_ext_limit_price,
				_o.getType() == IOrderMfg.ORDER_TYPE.BUY, price, time);
	}

	// private void _nextPriceLit(long price, long time) {
	// // the lit price is buy below/sell above
	// if (!_enabled) {
	// // Ok, I am not enabled yet, let's see if the price is triggered.
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// if (price >= _ext_aux_price) {
	// _enabled = true;
	// return;
	// }
	// } else {
	// if (price <= _ext_aux_price) {
	// _enabled = true;
	// return;
	// }
	// }
	// }
	//
	// if (_enabled) {
	// _limitHandlingInternal(_ext_limit_price,
	// _o.getType() == IOrderMfg.ORDER_TYPE.BUY, price, time);
	// }
	// }

	private void _nextPriceMarket(long price, long time) {
		// This is the most simple!
		_amIExecuted = true;
		_executionTime = time;
		if (_o.getType() == IOrderMfg.ORDER_TYPE.BUY) {
			_executionPrice = (int) (price + _slippage);
		} else {
			_executionPrice = (int) (price - _slippage);
		}
	}

	/**
	 * This is exactly specular to the stop order
	 */
	private void _nextPriceMit(long price, long time) {

		if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
			if (price >= _ext_aux_price) {
				// SELL IMMEDIATELY
				_nextPriceMarket(price, time);
			}
		} else {
			if (price <= _ext_aux_price) {
				// BUY IMMEDIATELY
				_nextPriceMarket(price, time);
			}
		}
	}

	private void _nextPriceStop(long price, long time) {
		// If I touch the stop
		// stop is buy above sell below.

		if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
			if (price <= _ext_aux_price) {
				// SELL IMMEDIATELY
				_nextPriceMarket(price, time);
			}
		} else {
			if (price >= _ext_aux_price) {
				// BUY IMMEDIATELY
				_nextPriceMarket(price, time);
			}
		}
	}

	private void _nextPriceStopLimit(long price, long time) {

		if (!_enabled) {
			// Ok, I am not enabled yet, let's see if the price is triggered.
			if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
				if (price <= _ext_aux_price) {
					_enabled = true;
					return; // wait one turn
				}
			} else {
				if (price >= _ext_aux_price) {
					_enabled = true;
					return; // wait one turn
				}
			}
		}

		if (_enabled) {
			_limitHandlingInternal(_ext_limit_price,
					_o.getType() == IOrderMfg.ORDER_TYPE.BUY, price, time);
		}
	}

	// private void _nextPriceTrailingLit(long price, long time) {
	// _commonTrailingStop(price, time, -1);
	//
	// if (!_trailingLimitGot) {
	//
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// if (price >= _triggerPrice) {
	// // SELL at limit
	// _trailingLimitGot = true;
	// _limitPrice = _triggerPrice + _ext_limit_offset;
	// }
	// } else {
	// if (price <= _triggerPrice) {
	// // BUY AT limit
	// _trailingLimitGot = true;
	// _limitPrice = _triggerPrice - _ext_limit_offset;
	// }
	// }
	// }
	//
	// if (_trailingLimitGot) {
	// // limit order logic
	//
	// _limitHandlingInternal(_limitPrice,
	// _o.getType() == IOrderMfg.ORDER_TYPE.BUY, price, time);
	// }
	// }

	// private void _nextPriceTrailingMit(long price, long time) {
	// // I must adjust the stop... above or below if it is a buy or sell...
	// _commonTrailingStop(price, time, -1);
	//
	// // let's see if I have to exit the market
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// if (price >= _triggerPrice) {
	// // SELL IMMEDIATELY
	// _nextPriceMarket(price, time);
	// }
	// } else {
	// if (price <= _triggerPrice) {
	// // BUY IMMEDIATELY
	// _nextPriceMarket(price, time);
	// }
	// }
	//
	// }

	// private void _nextPriceTrailingStop(long price, long time) {
	// // I must adjust the stop... above or below if it is a buy or sell...
	// _commonTrailingStop(price, time, +1);
	//
	// // let's see if I have to exit the market
	// if (_o.getType() == IOrderMfg.ORDER_TYPE.SELL) {
	// if (price <= _triggerPrice) {
	// // SELL IMMEDIATELY
	// _nextPriceMarket(price, time);
	// }
	// } else {
	// if (price >= _triggerPrice) {
	// // BUY IMMEDIATELY
	// _nextPriceMarket(price, time);
	// }
	// }
	//
	// }

	/**
	 * This function simply takes the prices and quantity from the order, just
	 * to be sure that it uses the correct prices
	 */
	private void _take_prices_and_quantity() {
		if (_enabled) {
			throw new IllegalStateException("already enabled");
		}
		// _ext_limit_offset = _o.getLimitOffset();
		_ext_limit_price = _o.getLimitPrice();
		_ext_aux_price = _o.getAuxPrice(); // probably if the aux price is
											// different AND I am enabled
		// I should ignore the change.
		_ext_quantity = _o.getQuantity();
	}

	/**
	 * Acknowledges a new order inside this simulation.
	 * 
	 * <p>
	 * The order must be compatible with the already present order, because for
	 * example you cannot modify a BUY order into a SELL one.
	 * 
	 * <p>
	 * The other part of the orders are modifiable.
	 * 
	 * @param aOrder
	 */
	public void acknowledgeThisOrder(IOrderMfg aOrder) {
		_o = aOrder;
		// OrderImpl.EState state = ((OrderImpl) _o).getState();
		// if (state != OrderImpl.EState.MODIFIED) {
		// return;
		// }

		if (_ext_quantity * _o.getQuantity() < 0) {
			throw new IllegalStateException("order " + _o
					+ " is diff (from buy to sell, or from sell to buy)");
		}
		_take_prices_and_quantity();
		((OrderImpl) _o).acknowledged();
	}

	public boolean areYouExecuted() {
		return _amIExecuted;
	}

	public long getExecutionPrice() {
		return _executionPrice;
	}

	public long getExecutionTime() {
		return _executionTime;
	}

	public IOrderMfg getOrder() {
		return _o;
	}

	public int getOrderId() {
		return _o.getId();
	}

	/**
	 * This is called whenever a new price from the simulator comes.
	 */
	public void nextPrice(long price, long time) {
		if (_amIExecuted) {
			throw new IllegalStateException(
					"I am already executed, don't call me any more");
		}

		OrderImpl.EState state = ((OrderImpl) _o).getState();

		if (state == OrderImpl.EState.MODIFIED) {
			debug_var(392252, "I use the old prices, even if it is modified");
		} else if (state == OrderImpl.EState.BEFORE_FIRST_SEND) {
			throw new IllegalStateException("order " + _o
					+ " is here in the simulator... but I never received it");
		}

		switch (_o.getExecType()) {
		case MARKET:
			_nextPriceMarket(price, time);
			break;
		case LIMIT:
			_nextPriceLimit(price, time);
			break;
		case STOP:
			_nextPriceStop(price, time);
			break;
		case STOP_LIMIT:
			_nextPriceStopLimit(price, time);
			break;
		// case LIT:
		// _nextPriceLit(price, time);
		// break;
		case MIT:
			_nextPriceMit(price, time);
			break;
		// case TRAILING_STOP:
		// _nextPriceTrailingStop(price, time);
		// break;
		// case TRAILING_MIT:
		// _nextPriceTrailingMit(price, time);
		// break;
		// case TRAILING_LIT:
		// _nextPriceTrailingLit(price, time);
		// break;
		default:
			throw new UnsupportedOperationException(
					"not implemented yet the case " + _o.getExecType());
		}

	}

}
