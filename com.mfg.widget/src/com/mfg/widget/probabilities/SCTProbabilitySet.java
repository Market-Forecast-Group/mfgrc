package com.mfg.widget.probabilities;

import java.io.Serializable;

import com.mfg.interfaces.probabilities.SCTProbabilityKey;
import com.mfg.widget.probabilities.SimpleLogMessage.SCSectionMessage;

/**
 * the set containing the statistics of the SC Touch probability version.
 * 
 * @author gardero
 * 
 */
public class SCTProbabilitySet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int swingsCount = 0;
	private int reachedNewHHLL = 0;
	private int reachedNewTHIndex = 0;
	private transient SCTProbabilityKey key;

	private transient SCSectionMessage msg;
	private boolean gothhll;

	/**
	 * counts an occurrence of the conditions this class is counting.
	 */
	public void registerSwing() {
		swingsCount++;
		gothhll = false;
	}

	/**
	 * registers a new HHLL.
	 */
	public void registerNewHHLL() {
		reachedNewHHLL++;
		gothhll = true;
		if (msg != null) {
			msg.setHHLLIndex(reachedNewHHLL);
		}
	}

	/***
	 * registers a new TH.
	 */
	public void registerNewTH() {
		if (!gothhll) {
			reachedNewTHIndex++;
			if (msg != null) {
				msg.setTHIndex(reachedNewTHIndex);
			}
		}
	}

	/**
	 * gets how many times this set was considered on an indicator section.
	 * 
	 * @return
	 */
	public int getSwingsCount() {
		return swingsCount;
	}

	/**
	 * gets how many times we got a new HHLL on an indicator section.
	 * 
	 * @return
	 */
	public int getReachedNewHHLL() {
		return reachedNewHHLL;
	}

	public void setReachedNewHHLL(int v) {
		reachedNewHHLL = v;
	}

	/**
	 * the probability of reaching a new HHLL
	 * 
	 * @return
	 */
	@SuppressWarnings("boxing")
	public double getNewHHLLProbability() {
		return new Double(reachedNewHHLL) / new Double(swingsCount);
	}

	/**
	 * the probability of reaching a new TH
	 * 
	 * @return
	 */
	public double getNewTHProbability() {
		return 1 - getNewHHLLProbability();
	}

	public void registerMSG(SCSectionMessage aMsg) {
		this.msg = aMsg;
		msg.setMessageIndex(swingsCount);
	}

	public boolean gotHHLL() {
		return gothhll;
	}

	public void setSwingsCount(int aSwingsCount) {
		swingsCount = aSwingsCount;
	}

	public SCTProbabilityKey getKey() {
		return key;
	}

	public void setKey(SCTProbabilityKey aKey) {
		key = aKey;
	}

}
