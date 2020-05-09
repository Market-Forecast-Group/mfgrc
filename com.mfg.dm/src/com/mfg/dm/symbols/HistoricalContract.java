/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.dm.symbols;

/**
 * @author arian
 * 
 */
@SuppressWarnings("hiding")
public class HistoricalContract {

	private String symbol;
	private String localSymbol;
	private String expiry;
	private double strike;
	private String currency;
	private String exchange;
	private String type;
	private int contractID;
	private int tickValue;
	private int tick;
	private int scale;
	private String timeZoneId;
	private String tradingHours;

	public HistoricalContract() {
	}

	/**
	 * @return the tradingHours
	 */
	public String getTradingHours() {
		return tradingHours;
	}

	/**
	 * @param tradingHours
	 *            the tradingHours to set
	 */
	public void setTradingHours(String tradingHours) {
		this.tradingHours = tradingHours;
	}

	/**
	 * @return the timeZoneId
	 */
	public String getTimeZoneId() {
		return timeZoneId;
	}

	/**
	 * @param timeZoneId
	 *            the timeZoneId to set
	 */
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getSymbol()
	 */
	public String getSymbol() {
		return symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getLocalSymbol()
	 */
	public String getLocalSymbol() {
		return localSymbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getExpiry()
	 */
	public String getExpiry() {
		return expiry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getStrike()
	 */
	public double getStrike() {
		return strike;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getCurrency()
	 */
	public String getCurrency() {
		return currency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getExchange()
	 */
	public String getExchange() {
		return exchange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getType()
	 */
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getId()
	 */
	public int getId() {
		return contractID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getTickValue()
	 */
	public int getTickValue() {
		return tickValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getTick()
	 */
	public int getTick() {
		return tick;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getScale()
	 */
	public int getScale() {
		return scale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#setComputedTick(int)
	 */

	public void setComputedTick(int computed_tick) {
		tick = computed_tick;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#setComputedScale(int)
	 */

	public void setComputedScale(int computed_scale) {
		scale = computed_scale;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setLocalSymbol(String localSymbol) {
		this.localSymbol = localSymbol;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public void setStrike(double strike) {
		this.strike = strike;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(int id) {
		this.contractID = id;
	}

	public void setTickValue(int tickValue) {
		this.tickValue = tickValue;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
}
