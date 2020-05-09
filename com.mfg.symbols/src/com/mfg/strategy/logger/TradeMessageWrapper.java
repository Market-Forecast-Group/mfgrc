package com.mfg.strategy.logger;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.ITradeMessage;
import com.mfg.broker.events.OrderMessage;

public class TradeMessageWrapper {

	private final ITradeMessage _tradeMessage;
	private long _fakeTime;
	private double _price;
	private long _equity;
	private double _longCapital;
	private double _shortCapital;
	private int _longQuantity;
	private int _shortQuantity;
	private long _longPricePL;
	private long _shortPricePL;
	private int _orderID;
	private String strategyName;
	private EAccountRouting _routedAccount;

	public TradeMessageWrapper(ITradeMessage tradeMessage) {
		this._tradeMessage = tradeMessage;

		if (tradeMessage instanceof OrderMessage) {
			setOrderID(((OrderMessage) tradeMessage).getOrderId());
		}

		if (tradeMessage instanceof OrderMessage) {
			_routedAccount = ((OrderMessage) _tradeMessage).getOrderRouting();
		} else if (tradeMessage instanceof StrategyMessage) {
			_routedAccount = ((StrategyMessage) tradeMessage).getAccountRouting();
		}
	}

	public ITradeMessage getTradeMessage() {
		return _tradeMessage;
	}

	public String getAccountName() {
		return _tradeMessage instanceof OrderMessage ? ((OrderMessage) _tradeMessage)
				.getAccountName() : "";
	}

	public String getSource() {
		return this._tradeMessage.getSource();
	}

	public long getFakeTime() {
		return _fakeTime;
	}

	public double getPrice() {
		return _price;
	}

	public long getEquity() {
		return _equity;
	}

	public double getLongCapital() {
		return _longCapital;
	}

	public double getShortCapital() {
		return _shortCapital;
	}

	public int getLongQuantity() {
		return _longQuantity;
	}

	public int getShortQuantity() {
		return _shortQuantity;
	}

	public Object getType() {
		return _tradeMessage.getType();
	}

	public String getEvent() {
		return _tradeMessage.getEvent();
	}

	public int getOrderID() {
		return _orderID;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setFakeTime(long aFakeTime) {
		this._fakeTime = aFakeTime;
	}

	public void setPrice(double aPrice) {
		this._price = aPrice;
	}

	public void setEquity(long aEquity) {
		this._equity = aEquity;
	}

	public void setLongCapital(double aLongCapital) {
		this._longCapital = aLongCapital;
	}

	public void setShortCapital(double aShortCapital) {
		this._shortCapital = aShortCapital;
	}

	public void setLongQuantity(int aLongQuantity) {
		this._longQuantity = aLongQuantity;
	}

	public void setShortQuantity(int aShortQuantity) {
		this._shortQuantity = aShortQuantity;
	}

	public long getLongPricePL() {
		return _longPricePL;
	}

	public void setLongPricePL(long aLongPricePL) {
		this._longPricePL = aLongPricePL;
	}

	public long getShortPricePL() {
		return _shortPricePL;
	}

	public void setShortPricePL(long aShortPricePL) {
		this._shortPricePL = aShortPricePL;
	}

	public void setOrderID(int aOrderID) {
		this._orderID = aOrderID;
	}

	public void setStrategyName(String aStrategyName) {
		this.strategyName = aStrategyName;
	}

	@Override
	public String toString() {
		return "" + _fakeTime + "\t" + _price + "\t" + _equity + "\t"
				+ _longCapital + "\t" + _shortCapital + "\t" + _longQuantity
				+ "\t" + _shortQuantity + "\t" + getType() + "\t" + getEvent()
				+ "\t" + getSource() + "\t" + _orderID;
	}

	public EAccountRouting getRoutedAccount() {
		return _routedAccount;
	}

}
