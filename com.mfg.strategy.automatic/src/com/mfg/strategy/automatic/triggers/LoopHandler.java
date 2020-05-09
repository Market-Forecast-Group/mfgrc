package com.mfg.strategy.automatic.triggers;

public class LoopHandler implements Runnable {
	protected class LoopRange {
		public int start, end;
	}

	protected Thread lookupThreads[];
	protected int startLoop, endLoop, curLoop, numThreads;

	public LoopHandler(int start, int end, int threads) {
		startLoop = curLoop = start;
		endLoop = end;
		numThreads = threads;
		lookupThreads = new Thread[numThreads];
	}

	protected synchronized LoopRange loopGetRange() {
		if (curLoop >= endLoop)
			return null;
		LoopRange ret = new LoopRange();
		ret.start = curLoop;
		curLoop += (endLoop - startLoop) / numThreads + 1;
		ret.end = (curLoop < endLoop) ? curLoop : endLoop;
		return ret;
	}

	/**
	 * @param start  
	 * @param end 
	 */
	public void loopDoRange(int start, int end) {
		//DO NOTHING
	}

	public void loopProcess() {
		for (int i = 0; i < numThreads; i++) {
			lookupThreads[i] = new Thread(this);
			lookupThreads[i].start();
		}
		for (int i = 0; i < numThreads; i++) {
			try {
				lookupThreads[i].join();
			} catch (InterruptedException iex) {
				//DO NOTHING
			}
		}
	}

	@Override
	public void run() {
		LoopRange str;
		while ((str = loopGetRange()) != null) {
			loopDoRange(str.start, str.end);
		}
	}
}