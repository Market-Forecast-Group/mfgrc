package com.mfg.dm;

import java.util.ArrayList;

import com.mfg.common.RandomSymbol;

public class RandomTickDataRequest extends TickDataRequest {

	/**
	 * This field is useless if the request is realtime, it will simply indicate
	 * how many prices to send before the database request shuts down.
	 */
	private int _totalNumberOfPrices;

	/**
	 * Builds a random tick data request using a random symbol and a seed.
	 * 
	 * @param aSymbol
	 * @param aSeed
	 */
	public RandomTickDataRequest(RandomSymbol aSymbol, long aSeed,
			boolean isRealTime, int numWarmUpPrices, int totalNumberOfPrices) {
		/*
		 * TickDataRequest tdr = new TickDataRequest(rs, 0.25, 0.25, true, true,
		 * 9393, true, 3, true, 0);
		 */
		super(aSymbol);

		_realTimeRequest = isRealTime;
		_seed = aSeed;
		_nPricesWarmup = numWarmUpPrices;

		/*
		 * The random tick data request has only one layer which is null,
		 * because
		 */
		requests = new ArrayList<>();
		requests.add(null);

		_totalNumberOfPrices = totalNumberOfPrices;
	}

	public int getMaximumFakeTime() {
		return _totalNumberOfPrices;
	}

}
