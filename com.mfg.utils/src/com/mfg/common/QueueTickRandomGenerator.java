package com.mfg.common;

import com.mfg.utils.RandomTickSource;

/**
 * a generator for random queue ticks. This uses the random tick source.
 * 
 * @author Sergio
 * 
 */
public class QueueTickRandomGenerator {
	private RandomTickSource fRandomPricesGenerator;
	private long fLastPhysicalTime = System.currentTimeMillis();
	private int fFakeTime;

	public QueueTickRandomGenerator(long seed, int tick) {
		fRandomPricesGenerator = new RandomTickSource(seed, tick);
		fRandomPricesGenerator.setNoGaps();
		fFakeTime = 0;

	}

	public QueueTick getNext() {
		int price = fRandomPricesGenerator.getNextPrice();
		QueueTick tk = new QueueTick(fLastPhysicalTime, fFakeTime++, price,
				true, 1);
		fLastPhysicalTime = Math.max(++fLastPhysicalTime,
				System.currentTimeMillis());
		return tk;
	}
}
