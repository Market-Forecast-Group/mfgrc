package com.mfg.widget.probabilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.utils.StepDefinition;

/**
 * contains the statistics for the algorithm to compute the T1 for the infinite
 * upper bounds patterns.
 * <p>
 * For the corresponding pattern (lower,Inf) we prepare some sub intervals that
 * will be filtered according to the percent ( {@link T1Computer.getPercent}) of
 * the total elements in the pattern:
 * <p>
 * <table border="1">
 * <tr>
 * <td>
 * lower
 * <td>lower
 * <td>...
 * <td>lower
 * <td>lower
 * </tr>
 * <tr>
 * <td>
 * lower+step
 * <td>lower+2*step
 * <td>...
 * <td>lower+(k-1)*step
 * <td>lower+k*step
 * </tr>
 * <tr>
 * <td>
 * C<sub>1</sub>
 * <td>C<sub>2</sub>
 * <td>...
 * <td>C<sub>(k-1)</sub>
 * <td>C<sub>k</sub>=Total
 * </tr>
 * </table>
 * where [lower,lower+k*step) is the first interval that includes all elements
 * of the pattern.
 * <p>
 * Also, for each sub interval is computed a probability as the portion of its
 * elements that reached the upper bound ( {@link T1Computer.getProbability}).
 * Then, it is selected as T1 the upper bound that belongs to the interval with
 * a closer probability to the mean probability for T1.
 * 
 * @author gardero
 * 
 */
public class InfProbabilitiesSet extends IProbabilitiesSet implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlTransient
	Double[] upperbounds;
	@XmlTransient
	int[] elements;
	@XmlTransient
	int[] winnerElements;
	private int total;

	StepDefinition st = new StepDefinition(0.01);
	static final String[] _COLUMNS = new String[] { "Upper", "Matches",
			"Matches%", "Reached", "Prob" };

	private transient int cursor;
	private transient List<ISimpleLogMessage> log;
	private int maxTargetCount;
	private transient ProbabilitiesKey key;

	public InfProbabilitiesSet() {
		super();
	}

	public InfProbabilitiesSet(int aMaxTargetCount) {
		this.maxTargetCount = aMaxTargetCount;
		elements = new int[aMaxTargetCount];
		winnerElements = new int[aMaxTargetCount];
		log = new ArrayList<>();
	}

	/**
	 * gets the number of targets counted.
	 * 
	 * @return
	 */
	@Override
	public int getMaxTargetCount() {
		return maxTargetCount;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int aTotal) {
		total = aTotal;
	}

	public void setUpperbounds(Double[] aUpperbounds) {
		upperbounds = aUpperbounds;
	}

	public void setElements(int[] aElements) {
		elements = aElements;
	}

	public void setWinnerElements(int[] aWinnerElements) {
		winnerElements = aWinnerElements;
	}

	@Override
	public void setMaxTargetCount(int aMaxTargetCount) {
		maxTargetCount = aMaxTargetCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesSet#countTH(int)
	 */
	@Override
	public void countTH(int thRatio) {
		// total++;
		// for (int i = elements.length - 1; i >= thRatio; i--) {
		// elements[i]++;
		// cursor = i;
		// }
		cursor = thRatio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesSet#countTargets(int,
	 * int)
	 */
	@Override
	public void countTargets(int lastSetPar, int targetPar) {
		int lastSet = lastSetPar;
		int target = targetPar;
		lastSet = Math.max(lastSet, 1);
		lastSet = Math.max(lastSet, cursor + 1);
		target = Math.min(target, maxTargetCount + 1);
		for (int i = lastSet; i < target; i++) {
			winnerElements[i - 1]++;
			elements[i - 1]++;
		}
	}

	/**
	 * gets the resulting probability of an interval.
	 * 
	 * @param index
	 *            the index of the interval.
	 * @return the probability.
	 */
	@SuppressWarnings("boxing")
	@Override
	public double getTargetProbability(int indexPar) {
		int index = indexPar;
		if (index > 0)
			index--;
		index = Math.min(winnerElements.length - 1, index);
		return new Double(winnerElements[index]) / new Double(elements[index]);
	}

	/**
	 * gets the percent of the elements in the interval relative to the total
	 * number of elements in the pattern.
	 * 
	 * @param index
	 *            the index of the interval
	 * @return the percent in [0..1]
	 */
	@SuppressWarnings("boxing")
	double getPercent(int indexPar) {
		int index = indexPar;
		index--;
		return new Double(elements[index]) / new Double(total);
	}

	@Override
	public String toString() {
		return "U=" + Arrays.toString(upperbounds) + "\nE="
				+ Arrays.toString(elements) + "\nW="
				+ Arrays.toString(winnerElements) + "\n, total=" + total;
	}

	/**
	 * gets the upper bounds of the sub intervals.
	 * 
	 * @return
	 */
	public Double[] getUpperbounds() {
		return upperbounds;
	}

	/**
	 * gets the counters of elements in each interval.
	 * 
	 * @return a counter for each interval.
	 */
	@XmlList
	public int[] getElements() {
		return elements;
	}

	/**
	 * gets the counters of elements that reached the upper bound of the
	 * interval.
	 * 
	 * @return a counter for each interval.
	 */
	@XmlList
	public int[] getWinnerElements() {
		return winnerElements;
	}

	@Override
	public List<ISimpleLogMessage> getLog() {
		return log;
	}

	@Override
	public void countNotReachedElement(int aTarget) {
		elements[aTarget - 1]++;
	}

	@Override
	public int getTargetCount(int index) {
		return winnerElements[Math.max(0,
				Math.min(index - 1, maxTargetCount - 1))];
	}

	protected int getConditionalTotalCount(int baseTarget, int target) {
		if (baseTarget == 0)
			return getTargetCount(target) + getTargetOffCount(target);
		int tot = getTargetCount(target) + getTargetOffCount(target);
		int tot2 = getTargetCount(baseTarget) + getTargetOffCount(baseTarget);
		return getTargetCount(baseTarget) + (tot - tot2);
	}

	@Override
	public int getTargetOffCount(int indexPar) {
		int index = indexPar;
		if (index == 0)
			index = 1;
		return elements[Math.min(index - 1, maxTargetCount - 1)]
				- winnerElements[Math.min(index - 1, maxTargetCount - 1)];
	}

	@SuppressWarnings("boxing")
	@Override
	public double getTargetProbability(int baseTarget, int target) {
		int targetCount = getTargetCount(target);
		int total1 = getConditionalTotalCount(baseTarget, target);
		if (targetCount == 0)
			return 0;
		return new Double(targetCount) / new Double(total1);
	}

	@Override
	public ProbabilitiesKey getKey() {
		return key;
	}

	@Override
	public void setKey(ProbabilitiesKey aKey) {
		this.key = aKey;
	}

}
