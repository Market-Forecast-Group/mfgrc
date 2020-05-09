package com.mfg.common;

import com.mfg.utils.U;

/**
 * Event which is sent as the first event of a subscription, it has some
 * characteristics of the symbol which is being subscribed.
 */
public class DFSSubscriptionStartEvent extends DFSSymbolEvent {

	public final int _tick;
	public final int _scale;

	/**
	 * At the start of the subscription the server sends the tick and the scale
	 * for this particular symbol.
	 * 
	 * <p>
	 * This particular event is done for the use of the TEA, because TEA may not
	 * have a direct access to symbol's data.
	 * 
	 * <p>
	 * This event is sent for now only for the virtual symbol subscriptions.
	 * 
	 * @param aSymbol
	 * @param aTick
	 * @param aScale
	 */
	public DFSSubscriptionStartEvent(String aSymbol, int aTick, int aScale) {
		super(aSymbol);
		_tick = aTick;
		_scale = aScale;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toPayload() {
		return U.join(this.symbol, START_SUB_EVENT, _tick, _scale);
	}

}
