package com.mfg.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.trading.PositionClosedEvent;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.interfaces.trading.PositionOpenedEvent;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMfgTableModel;

public class PartialAccount extends AccountStatistics {

	List<IOrderMfg> openedOrders;
	private int margin;
	private int opened;
	private int totalQuantity;
	private long aUW;
	private long equity;
	private long openEquity;
	double tickValue;
	private long maxOpenEquity;
	int gsign;
	long currentPrice;
	private long lastTime;
	boolean isLong;

	static String[] openColumns = new String[] { "Order ID", "Type",
			"Entry Time", "Entry Price", "Current Price", "Price P/L",
			"Money P/L", "Quantity", "Strategy Name", "Account" };
	HashMap<Integer, PositionOpenedEvent> openedMap = new HashMap<>();
	List<PositionClosedEvent> closedOrders = new ArrayList<>();

	private IMfgTableModel openTradesModel = new IMfgTableModel() {
		@Override
		public boolean isEnabled(int row, int column) {
			return true;
		}

		@Override
		public int getRowCount() {
			return openedMap.size();
		}

		@Override
		public int getHighLight(int row, int column) {
			return 0;
		}

		@Override
		public Object getContent(int row, int column) {
			if (row >= getRowCount())
				return null;
			IOrderMfg myorder = openedOrders.get(row);
			int id = myorder.getId();
			PositionOpenedEvent pos = openedMap.get(Integer.valueOf(id));
			int quantity = Math.abs(myorder.getQuantity());
			long executionPrice = pos.getExecutionPrice();
			int sign = OrderUtils.getSign(myorder.getType()) * gsign;
			double pl = -sign * gsign * executionPrice * quantity + gsign
					* quantity * currentPrice;
			switch (column) {
			case 0:
				return Integer.valueOf(id);
			case 1:
				return myorder.getType();
			case 2:
				return Long.valueOf(pos.getExecutionTime());
			case 3:
				return Long.valueOf(executionPrice);
			case 4:
				return Long.valueOf(currentPrice);
			case 5:
				return Double.valueOf(pl);
			case 6:
				return Double.valueOf(pl * tickValue);
			case 7:
				return Integer.valueOf(quantity);
			case 8:
				return null; // strategy name
			case 9:
				return isLong ? "LONG" : "SHORT";
			default:
				break;
			}
			return null;
		}

		@Override
		public String[] getColumnNames() {
			return openColumns;
		}
	};

	static String[] closedColumns = new String[] { "Order ID", "Type",
			"Entry Time", "Entry Price", "Exit Time", "Exit Price",
			"Price P/L", "Money P/L", "Quantity", "Strategy Name", "Account" };

	private IMfgTableModel closedTradesModel = new IMfgTableModel() {
		@Override
		public boolean isEnabled(int row, int column) {
			return true;
		}

		@Override
		public int getRowCount() {
			return openedMap.size();
		}

		@Override
		public int getHighLight(int row, int column) {
			return 0;
		}

		@Override
		public Object getContent(int row, int column) {
			if (row >= getRowCount())
				return null;
			PositionClosedEvent pos = closedOrders.get(row);
			IOrderMfg myorder = pos.getOrder();
			int id = myorder.getId();
			int quantity = Math.abs(myorder.getQuantity());
			long executionPrice = pos.getExecutionPrice();
			long parentExecutionPrice = pos.getParentExecutionPrice();
			int sign = OrderUtils.getSign(myorder.getType()) * gsign;
			long pl = -sign * gsign * parentExecutionPrice * quantity + gsign
					* quantity * executionPrice;
			switch (column) {
			case 0:
				return Integer.valueOf(id);
			case 1:
				return myorder.getType();
			case 2:
				return Long.valueOf(pos.getParentExecutionTime());
			case 3:
				return Long.valueOf(parentExecutionPrice);
			case 4:
				return Long.valueOf(pos.getExecutionTime());
			case 5:
				return Long.valueOf(executionPrice);
			case 6:
				return Long.valueOf(pl);
			case 7:
				return Double.valueOf(new Double(pl).doubleValue() * tickValue);
			case 8:
				return Integer.valueOf(quantity);
			case 9:
				return null;// strategy name
			case 10:
				return isLong ? "LONG" : "SHORT";
			default:
				break;
			}
			return null;
		}

		@Override
		public String[] getColumnNames() {
			return closedColumns;
		}
	};
	private long profitPoints;
	private long lossPoints;
	private int losingSizes;
	private int winningSizes;

	public PartialAccount(double aTickValue, StepDefinition tick,
			boolean aIsLong) {
		super(aTickValue, tick);
		this.tickValue = aTickValue;
		openedOrders = new ArrayList<>();
		this.isLong = aIsLong;
		this.gsign = aIsLong ? 1 : -1;
	}

	public int getOpened() {
		return opened;
	}

	public long getAUW() {
		return aUW;
	}

	@Override
	public long getEquity() {
		return equity;
	}

	// @Override
	// public long getOpenEquity() {
	// return openEquity;
	// }

	public double getOpenMoneyEquity() {
		return openEquity * tickValue;
	}

	public int getMargin() {
		return margin;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public List<IOrderMfg> getOpenedOrdersList() {
		return openedOrders;
	}

	private void computeOpenEquity() {
		openEquity = equity + gsign * totalQuantity * currentPrice;
		maxOpenEquity = Math.max(openEquity, maxOpenEquity);
		setMaxDrawdown(Math.max(getMaxDrawdown(), maxOpenEquity - openEquity));
	}

	public PositionEvent orderFilled(IExecutionReport report) {
		IOrderMfg order = report.getOrder();
		int aQ = Math.abs(order.getQuantity());
		boolean aIsParent = !order.isChild();
		PositionEvent res = null;
		if (aIsParent) {
			openedOrders.add(order);
			long[] childrenOpenings = new long[order.getChildren().size()];
			for (int i = 0; i < childrenOpenings.length; i++) {
				childrenOpenings[i] = order.getChildAt(i).getOpeningPrice();
			}
			res = new PositionOpenedEvent(order,
					report.getPhysicalExecutionTime(),
					report.getExecutionTime(), report.getExecutionPrice(),
					report.isLongPosition(), childrenOpenings);
			openedMap.put(Integer.valueOf(order.getId()),
					(PositionOpenedEvent) res);
		} else {
			openedOrders.remove(order.getParent());
			openedMap.remove(Integer.valueOf(order.getParent().getId()));
			IExecutionReport parent = report.getParentExecutionReport();
			res = new PositionClosedEvent(report.getPhysicalExecutionTime(),
					report.getExecutionTime(), report.getExecutionPrice(),
					report.isLongPosition(), report.isClosingInGain(),
					parent.getExecutionTime(), parent.getExecutionPrice(),
					order.getParent(), equity);
			closedOrders.add((PositionClosedEvent) res);
			long pl = ((PositionClosedEvent) res).getPL();
			if (pl >= 0) {
				winningSizes += order.getQuantity();
				profitPoints += pl;
			} else {
				losingSizes += order.getQuantity();
				lossPoints += (-pl);
			}
		}
		int sign = OrderUtils.getSign(order.getType()) * gsign;
		opened += sign;
		totalQuantity += sign * aQ;
		if (aIsParent) {
			considerOpenTrades(aQ, totalQuantity);
		}
		setParameters(profitPoints, lossPoints, winningSizes, losingSizes);
		long deltaEquity = -sign * gsign * report.getExecutionPrice() * aQ;
		equity += deltaEquity;
		computeOpenEquity();
		return res;
	}

	public void newTick(QueueTick tick) {
		currentPrice = tick.getPrice();
		computeOpenEquity();
		aUW += (tick.getFakeTime() - lastTime) * (maxOpenEquity - openEquity);
		lastTime = tick.getFakeTime();
	}

	public IMfgTableModel getOpenTradesModel() {
		return openTradesModel;
	}

	public IMfgTableModel getClosedTradesModel() {
		return closedTradesModel;
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

	// @Override
	// public IAccountStatistics getLongAccount() {
	// // TO DO revise the inheritance, the partial account must not override
	// // this.
	// return null;
	// }

	// @Override
	// public IAccountStatistics getShortAccount() {
	// // TO DO revise the inheritance, the partial account must not override
	// // this.
	// return null;
	// }

}
