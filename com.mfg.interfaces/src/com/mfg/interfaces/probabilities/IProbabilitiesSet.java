package com.mfg.interfaces.probabilities;

import java.util.List;

import com.mfg.interfaces.ISimpleLogMessage;

//@XmlSeeAlso({ ProbabilitiesSet.class, InfProbabilitiesSet.class })
public abstract class IProbabilitiesSet {

	/**
	 * counts a case with a th Ratio that contributes to the statistics of the
	 * intervals that contains it.
	 * 
	 * @param thRatio
	 */
	public abstract void countTH(int thRatio);

	/**
	 * for a range of meet targets, counts the observations that passed the
	 * upper bound for each interval.
	 * 
	 * @param from
	 *            initial target.
	 * @param to
	 *            end target.
	 */
	public abstract void countTargets(int lastSet, int target);

	public abstract double getTargetProbability(int target);

	public abstract List<ISimpleLogMessage> getLog();

	public abstract void countNotReachedElement(int target);

	public abstract int getTargetCount(int aI);

	public abstract int getTargetOffCount(int aI);

	public abstract int getMaxTargetCount();

	public abstract void setMaxTargetCount(int aMaxTargetCount);

	public abstract double getTargetProbability(int baseTarget, int target);

	public abstract ProbabilitiesKey getKey();

	public abstract void setKey(ProbabilitiesKey key);

	private int probabilityCursor;
	private double probabilityValue = -1;

	public int locateProbability(double probability) {
		if (probability != probabilityValue) {
			int maxTargetCount = getMaxTargetCount();
			probabilityCursor = binarySearch(probability, 0,
					maxTargetCount / 2, maxTargetCount - 1);
			probabilityValue = probability;
		}
		return probabilityCursor;
	}
	protected int binarySearch(double probability, int start, int mid, int end) {
		if (Math.abs(start - end) <= 1) {
			if (Math.abs(probability - getTargetProbability(start + 1)) < Math
					.abs(probability - getTargetProbability(end + 1)))
				return start;
			return end;
		}
		if (probability >= getTargetProbability(mid + 1)) {
			return binarySearch(probability, start, (start + mid) / 2, mid);
		}
		return binarySearch(probability, mid, (end + mid) / 2, end);
	}

}
