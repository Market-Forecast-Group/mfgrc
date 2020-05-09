package com.mfg.plstats;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;

public class SessionInfo {
	private PriceMDBSession priceSession;
	private IndicatorMDBSession indicatorSession;

	public SessionInfo(PriceMDBSession aPriceSession,
			IndicatorMDBSession aIndicatorSession) {
		super();
		this.priceSession = aPriceSession;
		this.indicatorSession = aIndicatorSession;
	}

	public PriceMDBSession getPriceSession() {
		return priceSession;
	}

	public IndicatorMDBSession getIndicatorSession() {
		return indicatorSession;
	}

}
