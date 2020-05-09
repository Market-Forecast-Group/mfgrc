package com.mfg.strategy;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.utils.StepDefinition;

public class PeriodAccount extends AccountStatistics {

	private PartialAccount fLongAccount;
	private PartialAccount fShortAccount;

	public PeriodAccount(double aTickValue, StepDefinition tick) {
		super(aTickValue, tick);
		fLongAccount = new PartialAccount(aTickValue, tick, true);
		fShortAccount = new PartialAccount(aTickValue, tick, false);
	}

	public PositionEvent orderFilled(IExecutionReport report) {
		IOrderMfg order = report.getOrder();
		boolean longAccount = order.getRoutedAccount() == EAccountRouting.LONG_ACCOUNT;
		PositionEvent res;
		if (longAccount)
			res = fLongAccount.orderFilled(report);
		else
			res = fShortAccount.orderFilled(report);
		if (!order.isChild()) {
			considerOpenTrades(
					Math.abs(order.getQuantity()),
					fShortAccount.getTotalQuantity()
							+ fLongAccount.getTotalQuantity());
		}
		setParameters(
				fLongAccount.getProfitableTradedSizesPoints()
						+ fShortAccount.getProfitableTradedSizesPoints(),
				fLongAccount.getLosingTradedSizesPoints()
						+ fShortAccount.getLosingTradedSizesPoints(),
				fLongAccount.getNumberOfWinningTradedSizes()
						+ fShortAccount.getNumberOfWinningTradedSizes(),
				fLongAccount.getNumberOfLosingTradedSizes()
						+ fShortAccount.getNumberOfLosingTradedSizes());
		return res;
	}

	public void newTick(QueueTick e) {
		fLongAccount.newTick(e);
		fShortAccount.newTick(e);
	}

	public PartialAccount getLongAccount() {
		return fLongAccount;
	}

	public PartialAccount getShortAccount() {
		return fShortAccount;
	}

	@Override
	public void setTick(StepDefinition tick) {
		super.setTick(tick);
		fLongAccount.setTick(tick);
		fShortAccount.setTick(tick);
	}

	@Override
	public long getCurrentDrawDownClosedEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCurrentDrawDownClosedEquityPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxDrawDownClosedEquityPoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxDrawDownClosedEquity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getOpenEquityMoney() {
		// TODO Auto-generated method stub
		return 0;
	}

}
