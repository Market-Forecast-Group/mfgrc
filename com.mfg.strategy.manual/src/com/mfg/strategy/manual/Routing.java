package com.mfg.strategy.manual;

import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.strategy.manual.interfaces.IAccountStatus;
import com.mfg.strategy.manual.interfaces.IManualStrategyAlgorithmEnvironment;

public enum Routing {
	LONG {
		@Override
		public long getQuantity(IAccountStatus accountStatus) {
			return accountStatus.getLongQuantity();
		}

		@Override
		public ORDER_TYPE getOrderToOpen() {
			return ORDER_TYPE.BUY;
		}

		@Override
		public int getTrailingLevel(ManualStrategySettings settings) {
			return settings.getLongTrailingLevel();
		}

		@Override
		public IOrderMfg[] getActiveOrders(
				IManualStrategyAlgorithmEnvironment env) {
			return env.getLongActiveOrders();
		}
	},
	SHORT {
		@Override
		public long getQuantity(IAccountStatus accountStatus) {
			return accountStatus.getShortQuantity();
		}

		@Override
		public ORDER_TYPE getOrderToOpen() {
			return ORDER_TYPE.SELL;
		}

		@Override
		public int getTrailingLevel(ManualStrategySettings settings) {
			return settings.getShortTrailingLevel();
		}

		@Override
		public IOrderMfg[] getActiveOrders(
				IManualStrategyAlgorithmEnvironment env) {
			return env.getShortActiveOrders();
		}
	},
	AUTO;

	/**
	 * @param accountStatus
	 */
	@SuppressWarnings("static-method")
	// Used inside this class.
	public long getQuantity(IAccountStatus accountStatus) {
		throw new UnsupportedOperationException(
				"This routing do not has a particular account");
	}

	@SuppressWarnings("static-method")
	// Used inside this class.
	public ORDER_TYPE getOrderToOpen() {
		throw new UnsupportedOperationException(
				"This routing is not associated to an order type");
	}

	public ORDER_TYPE getOrderToClose() {
		return OrderUtils.getOpposite(getOrderToOpen());
	}

	/**
	 * @param settings
	 */
	@SuppressWarnings("static-method")
	// Used inside this class.
	public int getTrailingLevel(ManualStrategySettings settings) {
		throw new UnsupportedOperationException(
				"This routing is not associated to a trailing");
	}

	public Routing getOpposite() {
		if (this == AUTO) {
			return this;
		}
		if (this == LONG) {
			return SHORT;
		}
		return LONG;
	}

	public boolean isLong() {
		return this == LONG;
	}

	public boolean isShort() {
		return this == SHORT;
	}

	/**
	 * @param env
	 */
	@SuppressWarnings("static-method")
	// Used inside this class.
	public IOrderMfg[] getActiveOrders(IManualStrategyAlgorithmEnvironment env) {
		throw new UnsupportedOperationException(
				"This routing is not associated to an account");
	}

	public EAccountRouting getAccountRouting() {
		if (this == AUTO)
			return EAccountRouting.AUTOMATIC_ROUTE;
		if (this == LONG)
			return EAccountRouting.LONG_ACCOUNT;
		if (this == SHORT)
			return EAccountRouting.SHORT_ACCOUNT;
		return null;
	}
}
