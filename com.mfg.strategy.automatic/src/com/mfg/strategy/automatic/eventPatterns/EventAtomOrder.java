/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public abstract class EventAtomOrder extends EventAtomCommand implements
		IOrderFilledListener {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private double executionPrice;
	private OrderImpl order;
	@SuppressWarnings("unused")
	private final OrderImpl orderModel;

	protected EventAtomOrder() {
		super();
		orderModel = null;
		order = null;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		order = getAnOrder(aDealer);
		if (isRequiresConfirmation()) {
			String account = order.getRoutedAccount() == EAccountRouting.SHORT_ACCOUNT ? "SHORT ACCOUNT"
					: "LONG ACCOUNT";
			order.setConfirmationMessage(account + "\n\nEVENT:\n" + toString()
					+ "\n\n" + "ORDER:\n" + order);
		}
		order.setPlaySound(isPlaySound());
		order.setSoundPath(getSoundPath());
		sendOrder(aDealer);
	}

	protected void sendOrder(EventsDealer aDealer) {
		aDealer.addOrder(order, this);
	}

	public void cancelOrder() {
		if (order != null)
			getEventsDealer().cancelOrder(order);
	}

	/**
	 * @param aDealer
	 */
	@SuppressWarnings("static-method")
	protected OrderImpl getAnOrder(EventsDealer aDealer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		return true;
	}

	@Override
	public void orderFilled(IExecutionReport aReport) {
		setTriggered(true);
		executionPrice = aReport.getExecutionPrice();
	}

	@Override
	public boolean isTiedToCloseCommand() {
		return true;
	}

	/**
	 * @return the executionPrice
	 */
	public double getExecutionPrice() {
		return executionPrice;
	}

	/**
	 * @param aExecutionPrice
	 *            the executionPrice to set
	 */
	public void setExecutionPrice(double aExecutionPrice) {
		executionPrice = aExecutionPrice;
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return order.toString() + " at P=" + executionPrice;
	}

	@Override
	public String getLabel() {
		return "ORDER";
	}

	/**
	 * @return the order
	 */
	public OrderImpl getOrder() {
		return order;
	}

	@Override
	public boolean isPure(boolean entry) {
		return entry;
	}

	private boolean _requiresConfirmation = false;
	private boolean _playSound = false;
	private String _soundPath;

	public boolean isRequiresConfirmation() {
		return _requiresConfirmation;
	}

	public void setRequiresConfirmation(boolean requiresConfirmation) {
		_requiresConfirmation = requiresConfirmation;
	}

	public boolean isPlaySound() {
		return _playSound;
	}

	public void setPlaySound(boolean playSound) {
		_playSound = playSound;
	}

	public void setSoundPath(String soundPath) {
		_soundPath = soundPath;
	}

	public String getSoundPath() {
		return _soundPath;
	}

}
