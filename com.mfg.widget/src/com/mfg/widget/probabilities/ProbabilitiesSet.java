package com.mfg.widget.probabilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.core.runtime.Assert;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.widget.probabilities.DistributionSet.DoubleDistributionSet;

/***
 * contains the statistics for the targets.
 * 
 * @author gardero
 * 
 */
public class ProbabilitiesSet extends IProbabilitiesSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlTransient
	private int[] elements;
	@XmlTransient
	private int[] nrElements;
	private int elementsCount;
	private DoubleDistributionSet dist;
	private DoubleDistributionSet distCount;
	private transient List<ISimpleLogMessage> log;
	private DoubleDistributionSet distNRCount;
	private int maxTargetCount;
	private transient ProbabilitiesKey key;

	private ProbabilitiesSet() {
		super();
		log = new ArrayList<>();
	}

	public ProbabilitiesSet(int maxTargetCount1) {
		this();
		this.maxTargetCount = maxTargetCount1;
		elements = new int[maxTargetCount1];
		nrElements = new int[maxTargetCount1];
	}

	public ProbabilitiesSet(int maxTargetCount1,
			@SuppressWarnings("unused") ElementsPatterns aElementsPatterns) {
		this(maxTargetCount1);
		// model = new CountAndProbsList(this, aElementsPatterns);
	}

	@Override
	public void countTH(int aThRatio) {
		elementsCount++;
	}

	/**
	 * counts the targets in the range.
	 * 
	 * @param lastSet
	 *            the last target we counted.
	 * @param target
	 *            the new target
	 */
	@Override
	public void countTargets(int lastSet, int target) {
		if (target == -1)
			elementsCount++;
		else {
			for (int i = lastSet; i <= target - 1; i++) {
				elements[Math.min(i, elements.length - 1)]++;
			}
		}
	}

	@Deprecated
	public int getElementsCount() {
		return elementsCount;
	}

	/**
	 * gets the target probability.
	 * 
	 * @param target
	 *            the target ID, greater than zero.
	 * @return a value in [0..1]
	 */
	@SuppressWarnings("boxing")
	@Override
	public double getTargetProbability(int target) {
		int targetCount = getTargetCount(target);
		int offTargetCount = getTargetOffCount(target);
		if (targetCount == 0)
			return 0;
		return new Double(targetCount)
				/ new Double(targetCount + offTargetCount);
	}

	/**
	 * gets the target probability conditioned to the fact that we are already
	 * on given target.
	 * 
	 * @param target
	 *            the target ID, greater than zero.
	 * @param baseTarget
	 *            the target we are on.
	 * @return a value in [0..1]
	 */
	@SuppressWarnings("boxing")
	@Override
	public double getTargetProbability(int baseTarget, int target) {
		int targetCount = getTargetCount(target);
		int total = getConditionalTotalCount(baseTarget, target);
		if (targetCount == 0)
			return 0;
		return new Double(targetCount) / new Double(total);
	}

	/**
	 * gets how many times we reached a target.
	 * 
	 * @param target
	 *            the target ID, greater than zero.
	 * @return a non-negative integer.
	 */
	@Override
	public int getTargetCount(int target) {
		return elements[Math.max(0, Math.min(target - 1, maxTargetCount - 1))];
	}

	protected int getConditionalTotalCount(int baseTarget, int target) {
		if (baseTarget == 0)
			return getTargetCount(target) + getTargetOffCount(target);
		return elements[Math.min(baseTarget - 1, maxTargetCount - 1)];
	}

	/**
	 * gets how many times we didn't reached a target.
	 * 
	 * @param target
	 *            the target ID, greater than zero.
	 * @return a non-negative integer.
	 */
	@Override
	public int getTargetOffCount(int target) {
		return nrElements[Math.max(0, Math.min(target - 1, maxTargetCount - 1))];
	}

	/***
	 * gets the distribution of the targets probability.
	 * 
	 * @param aElementsPatterns
	 *            the pattern of this statistics set.
	 * @return
	 */
	@SuppressWarnings("boxing")
	public DoubleDistributionSet getDistribution(
			ElementsPatterns aElementsPatterns) {
		if (dist == null) {
			dist = new DoubleDistributionSet();
			for (int i = 0; i < maxTargetCount; i++) {
				dist.add(aElementsPatterns.getTarget(i + 1),
						(int) (getTargetProbability(i + 1) * 100));
			}
		}
		return dist;
	}

	/***
	 * gets the distribution of the targets count.
	 * 
	 * @param aElementsPatterns
	 *            the pattern of this statistics set.
	 * @return
	 */
	@SuppressWarnings("boxing")
	public DoubleDistributionSet getCountDistribution(
			ElementsPatterns aElementsPatterns) {
		if (distCount == null) {
			distCount = new DoubleDistributionSet();
			for (int i = 0; i < elements.length; i++) {
				distCount.add(aElementsPatterns.getTarget(i + 1), elements[i]);
			}
		}
		return distCount;
	}

	// public DoubleDistributionSet getTotalCountDistribution(ElementsPatterns
	// aElementsPatterns) {
	// if (distTotalCount==null){
	// distTotalCount = new DoubleDistributionSet();
	// for (int i = 0; i < elements.length; i++) {
	// distTotalCount.add(aElementsPatterns.getTarget(i+1),
	// elements[i]+nrElements[i]);
	// }
	// }
	// return distTotalCount;
	// }

	/***
	 * gets the distribution of the non-reached targets count.
	 * 
	 * @param aElementsPatterns
	 *            the pattern of this statistics set.
	 * @return
	 */
	@SuppressWarnings("boxing")
	public DoubleDistributionSet getNRCountDistribution(
			ElementsPatterns aElementsPatterns) {
		if (distNRCount == null) {
			distNRCount = new DoubleDistributionSet();
			for (int i = 0; i < elements.length; i++) {
				distNRCount.add(aElementsPatterns.getTarget(i + 1),
						nrElements[i]);
			}
		}
		return distNRCount;
	}

	// public CountAndProbsList getModel(){
	// return model;
	// }

	@Override
	public List<ISimpleLogMessage> getLog() {
		return log;
	}

	/**
	 * counts a non-reached target.
	 * 
	 * @param aTarget
	 */
	@Override
	public void countNotReachedElement(int aTarget) {
		nrElements[aTarget - 1]++;
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

	@Override
	public void setMaxTargetCount(int aMaxTargetCount) {
		maxTargetCount = aMaxTargetCount;
	}

	@Override
	public ProbabilitiesKey getKey() {
		return key;
	}

	@Override
	public void setKey(ProbabilitiesKey aKey) {
		this.key = aKey;
	}

	@XmlList
	public int[] getElements() {
		return elements;
	}

	public void setElements(int[] aElements) {
		elements = aElements;
	}

	@XmlList
	public int[] getNrElements() {
		Assert.isNotNull(nrElements);
		return nrElements;
	}

	public void setNrElements(int[] aNrElements) {
		nrElements = aNrElements;
		Assert.isNotNull(nrElements);
	}

	public void setElementsCount(int aElementsCount) {
		elementsCount = aElementsCount;
	}

}
