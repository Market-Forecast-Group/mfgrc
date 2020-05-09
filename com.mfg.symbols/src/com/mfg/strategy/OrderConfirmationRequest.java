package com.mfg.strategy;

import com.mfg.broker.IOrderMfg;

public class OrderConfirmationRequest implements IConfirmationRequest {

	private IOrderMfg order;
	private FinalStrategy strategy;
	private boolean confirmed;
	private PortfolioStrategy portfolio;
	private boolean canceled;

	public OrderConfirmationRequest(PortfolioStrategy aPortfolioStrategy,
			FinalStrategy aStrategy, IOrderMfg aOrder) {
		this.portfolio = aPortfolioStrategy;
		this.strategy = aStrategy;
		this.order = aOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IConfirmationRequest#isConfirmed()
	 */
	@Override
	public boolean isConfirmed() {
		return confirmed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IConfirmationRequest#setConfirmed(boolean)
	 */
	@Override
	public void setConfirmed(boolean aConfirmed) {
		confirmed = aConfirmed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IConfirmationRequest#confirm()
	 */
	@Override
	public void confirm() {
		setConfirmed(true);
		portfolio.addOrder(strategy, getOrder());
	}

	public IOrderMfg getOrder() {
		return order;
	}

	public void setOrder(IOrderMfg aOrder) {
		order = aOrder;
	}

	public FinalStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(FinalStrategy aStrategy) {
		strategy = aStrategy;
	}

	@Override
	public String toString() {
		return "[confirm: " + order + "]";
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean aCancelled) {
		canceled = aCancelled;
	}

	@Override
	public void cancel() {
		setCanceled(true);

		assert (false); // to do.
		// portfolio.cancelSendRequest(order.getId());
	}
}
