package com.mfg.systests.tea;

import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.StopOrder;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSStoppingSubscriptionEvent;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.ISymbolListener;
import com.mfg.tea.conn.ITEA;
import com.mfg.tea.conn.IVirtualBrokerListener;
import com.mfg.utils.U;

/**
 * The tested strategy is a strategy that will live inside a {@link TestedShell}
 * and it will send orders and test that its account information is coherent
 * with the account information which comes from {@link ITEA}.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TestedStrategy implements ISymbolListener, IVirtualBrokerListener {

	/**
	 * The shell is used to ask information about the account and to stop it
	 * when the data source arrives at the end of the subscription.
	 */
	private TestedShell _shell;
	private int _newId = 1;
	private int _tick;

	public TestedStrategy(TestedShell testedShell) {
		_shell = testedShell;
	}

	@Override
	public void newExecutionNew(IOrderExec anExec) {
		U.debug_var(329025, "a new execution ", anExec);

	}

	@Override
	public void orderStatusNew(IOrderStatus aStatus) {
		U.debug_var(589193, "a new status ", aStatus);
	}

	@Override
	public void onNewSymbolEvent(DFSSymbolEvent anEvent) {
		U.debug_var(320295, "rec ", anEvent);

		if (anEvent instanceof DFSQuote) {
			DFSQuote quote = (DFSQuote) anEvent;
			if (quote.tick.getFakeTime() % 13 == 0) {

				OrderImpl mfgOrder = new MarketOrder(_newId++, ORDER_TYPE.BUY,
						1);
				mfgOrder.setStrategyId("II");

				OrderImpl takeProfit = new LimitOrder(_newId++,
						ORDER_TYPE.SELL, -1, quote.tick.getPrice() + 6 * _tick);

				mfgOrder.setTakeProfit(takeProfit);

				OrderImpl stopLoss = new StopOrder(_newId++, ORDER_TYPE.SELL,
						-1, quote.tick.getPrice() - 4 * _tick);

				mfgOrder.setStopLoss(stopLoss);

				_shell.sendOrder(mfgOrder);
			}
		} else if (anEvent instanceof DFSStoppingSubscriptionEvent) {
			U.debug_var(934845, "Stopping the subscription!");
			_shell.end();
		} else if (anEvent instanceof DFSSubscriptionStartEvent) {
			DFSSubscriptionStartEvent dsse = (DFSSubscriptionStartEvent) anEvent;
			_tick = dsse._tick;
		}

	}
}
