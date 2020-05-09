package com.mfg.interfaces.probabilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.mfg.interfaces.IObjectProcessor;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;

/**
 * represents a Tree-like structure that contains the patterns generated to
 * classify the previous consecutive swings ratios:
 * <p>
 * Sw'<sub>0</sub>/Sw<sub>-1</sub>, Sw<sub>-1</sub>/Sw<sub>-2</sub>,
 * Sw<sub>-2</sub>/Sw<sub>-3</sub>,...
 * <p>
 * The structure is built using the swings ratios of a selected base scale. Then
 * for each ratio (starting from the older one to the current one
 * Sw'<sub>0</sub>/Sw<sub>-1</sub>) we do a split trying to come up with n (n
 * consecutive subintervals of the [0,Inf) range) sets with approximately the
 * same amount of elements inside. Such task is performed sorting the values of
 * the ratio and taking as cut points for the intervals the elements in position
 * k*m/n, with k=1,2,... and m being the amount of values for the ratio to be
 * split.
 * <p>
 * The leaves of this Tree will represent the patterns that will let us classify
 * all elements we analyze in our indicator as belonging to one of them.
 * 
 * @author gardero
 * 
 */
public class ElementsPatterns implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient IElement[] elist;
	private List<ElementsPatterns> children;
	private ElementsPatterns parent = null;
	private double lowerBound = 0, upperBound = Double.POSITIVE_INFINITY;
	private Configuration configuration;
	private int ratioLevel = -1;
	private int leafID = -1;
	private double firstTarget;
	private boolean isInfinite;
	private double lastTH;
	private double maxSw0Ratio;
	private double maxTarget;
	private int t1count;
	private int size;

	/***
	 * builds an instance of this class with a list of elements representing the
	 * ratios available on each TH of the series.
	 * 
	 * @param aElist
	 *            the list of swing ratios objects
	 * @param aConfig
	 *            the configuration.
	 */
	public ElementsPatterns(List<IElement> aElist, Configuration aConfig) {
		this(aElist, aConfig, -1);
		if (!isLeaf())
			setTheLeafIDs(1);
		setTheTargets(0);
	}

	public ElementsPatterns() {
		super();
	}

	/***
	 * gets the pattern leaves that holds a restriction for each of the ratios
	 * selected in the configuration.
	 * 
	 * @param list
	 *            to call with an empty list.
	 * @return the same list we got from the arguments but with the leaves
	 *         present in this branch.
	 */
	/*
	 * private List<ElementsPatterns> getInfLeaves(List<ElementsPatterns> list)
	 * { if (isLeaf()) { if (!Double.isInfinite(upperBound)) { list.add(this); }
	 * } else { for (ElementsPatterns e : children) { e.getInfLeaves(list); } }
	 * return list; }
	 */

	public List<ElementsPatterns> getLeaves(List<ElementsPatterns> list) {
		if (isLeaf()) {
			list.add(this);
		} else {
			for (ElementsPatterns e : children) {
				e.getLeaves(list);
			}
		}
		return list;
	}

	public ElementsPatterns(IElement[] aElist, Configuration aConfig) {
		this(aElist, aConfig, -1);
		if (!isLeaf())
			setTheLeafIDs(1);
		setTheTargets(0);
	}

	/***
	 * builds an instance of this class with a list of elements representing the
	 * ratios available on each TH of the series.
	 * 
	 * @param aElist
	 *            the list of swing ratios objects
	 * @param aConfig
	 *            the configuration.
	 * @param aRatioLevel
	 *            the index of the ratio that represents this level of the tree.
	 */
	private ElementsPatterns(List<IElement> aElist, Configuration aConfig,
			int aRatioLevel) {
		this(aElist.toArray(new IElement[] {}), aConfig, aRatioLevel);
	}

	private ElementsPatterns(IElement[] aElist, Configuration aConfig,
			int aRatioLevel) {
		super();
		elist = aElist;
		size = elist.length;
		configuration = aConfig;
		int thisRatio = configuration.getNextRatio(aRatioLevel);
		if (thisRatio > -1) {
			ratioLevel = thisRatio;
			children = split();
		} else {
			ratioLevel = aRatioLevel;
		}
		maxSw0Ratio = 0;
		for (IElement e : aElist) {
			double ratio = e.getRatio(0, configuration.getDefaultScale());
			maxSw0Ratio = Math.max(maxSw0Ratio, ratio);
		}
		maxTarget = 0;
		for (IElement e : aElist) {
			double target = e.getTarget(configuration.getDefaultScale());
			maxTarget = Math.max(maxTarget, target);
		}
	}

	/***
	 * gets the bounds of the split performed in the sorted list.
	 * <p>
	 * The more different elements in the list, the better will be the result of
	 * the split.
	 * 
	 * @return an array of elements that splits the sorted list in sets of
	 *         approximately the same size.
	 */
	private double[] getBounds() {
		final int n = elist.length;
		final int m = configuration.getIntervals(ratioLevel);
		@SuppressWarnings("boxing")
		int isize = (int) Math.ceil(new Double(n) / new Double(m));
		double[] cps = new double[m - 1];
		int index = isize - 1;
		for (int i = 0; i < cps.length; i++) {
			cps[i] = configuration.getIntervalsStep().round(
					elist[index].getRatio(ratioLevel,
							configuration.getDefaultScale()));
			index += isize;
		}
		return cps;
	}

	/***
	 * splits this branch according to the ratio values and the number of sets
	 * that we specified in the configuration to do the split.
	 * 
	 * @return
	 */
	private List<ElementsPatterns> split() {
		List<ElementsPatterns> res = new ArrayList<>();
		Arrays.sort(
				elist,
				new ElementsComparator(ratioLevel, configuration
						.getDefaultScale()));
		double[] cps = getBounds();
		ArrayList<IElement> t = new ArrayList<>();
		int index = 0;
		double prevC = 0;
		for (int i = 0; i < elist.length; i++) {
			if (index == cps.length
					|| elist[i].getRatio(ratioLevel,
							configuration.getDefaultScale()) <= cps[index]) {
				t.add(elist[i]);
			} else {
				ElementsPatterns e = new ElementsPatterns(t, configuration,
						ratioLevel);
				res.add(e);
				e.parent = this;
				e.lowerBound = prevC;
				prevC = e.upperBound = cps[index];
				t = new ArrayList<>();
				t.add(elist[i]);
				index++;
			}
		}
		ElementsPatterns e = new ElementsPatterns(t, configuration, ratioLevel);
		res.add(e);
		e.parent = this;
		e.lowerBound = prevC;
		e.upperBound = Double.POSITIVE_INFINITY;
		return res;
	}

	/**
	 * gets the first target of the pattern
	 * 
	 * @return
	 */
	public double getFirstTarget() {
		return firstTarget;
	}

	/**
	 * gets the target points of an index.
	 * 
	 * @param index
	 *            an integer greater than 0.
	 * @return
	 */
	public double getTarget(int index) {
		StepDefinition targetStep = configuration.getTargetStep();
		double theFirstTarget = getFirstTarget();
		return getTargetPoints(index, targetStep, theFirstTarget);
	}

	public static double getTargetPoints(int index, StepDefinition targetStep,
			double theFirstTarget) {
		double res = theFirstTarget + (index - 1) * targetStep.getStepDouble();
		return targetStep.round(res);
	}

	/**
	 * gets the Target Id
	 * 
	 * @param ref
	 *            the swing reference.
	 * @param currentPrice
	 *            the current price.
	 * @return an integer greater than 0.
	 */
	public int getTID(SwingReference ref, double currentPrice) {
		double currentTargetPoints = ref.getCurrentTargetPoints(currentPrice);
		if (currentTargetPoints < getFirstTarget())
			return -1;
		StepDefinition s = configuration.getTargetStep();
		int steps = MathUtils.getStepDiffAbs(currentTargetPoints,
				getFirstTarget(), s.getStepInteger(), s.getStep10Scale()) + 1;
		if (MathUtils.isRoundStepDiffAbs(currentTargetPoints, getFirstTarget(),
				s.getStepInteger(), s.getStep10Scale()))
			return steps - 1;
		return steps;
	}

	/**
	 * gets if this branch contains an element.
	 * 
	 * @param e
	 *            the element
	 * @param scale
	 *            the scale of the ratios
	 * @return {@code true} iff the element's ratios fall in this branch.
	 */
	public boolean contains(IElement e, int scale) {
		return containsRatio(e.getRatio(ratioLevel,
				configuration.getDefaultScale()));
	}

	/**
	 * gets if this branch contains a ratio.
	 * 
	 * @param v
	 *            the ratio in question.
	 * @return {@code true} iff the ratio value is in the bounds limits of this
	 *         branch.
	 */
	public boolean containsRatio(double v) {
		return (v == lowerBound && v == 0)
				|| (v > lowerBound && v <= upperBound);
	}

	/**
	 * gets the leaf that contains the element we ask.
	 * 
	 * @param p
	 *            the swing element
	 * @param scale
	 *            the scale of the ratios
	 * @return the pattern leaf
	 */
	public ElementsPatterns getPatternLeaf(IElement p, int scale) {
		if (isLeaf())
			return this;
		double ratio = p.getRatio(ratioLevel, scale);
		for (ElementsPatterns e : children) {
			if (e.containsRatio(ratio))
				return e.getPatternLeaf(p, scale);
		}
		return null;
	}

	/**
	 * gets the leaf that contains the element we ask.
	 * 
	 * @param p
	 *            the swing element
	 * @param scale
	 *            the scale of the ratios
	 * @return the pattern leaf
	 */
	public ElementsPatterns getNextPatternLeaf(IElement p, int scale) {
		if (isLeaf())
			return this;
		double ratio = p.getRatio(ratioLevel, scale, -1);
		for (ElementsPatterns e : children) {
			if (e.containsRatio(ratio))
				return e.getNextPatternLeaf(p, scale);
		}
		return null;
	}

	/**
	 * visits the nodes of this tree.
	 * 
	 * @param proc
	 *            a method to process the nodes.
	 */
	public void visitNodes(IObjectProcessor<ElementsPatterns> proc) {
		proc.process(this);
		if (!isLeaf()) {
			for (ElementsPatterns e : children) {
				e.visitNodes(proc);
			}
		}
	}

	private int setTheLeafIDs(int idPar) {
		int id = idPar;
		if (isLeaf()) {
			setLeafID(id);
			return id + 1;
		}
		for (ElementsPatterns e : children) {
			id = e.setTheLeafIDs(id);
		}
		return id;

	}

	private void setTheTargets(double last) {
		if (isLeaf()) {
			lastTH = last;
			isInfinite = Double.isInfinite(upperBound);
			if (!isInfinite)
				firstTarget = configuration
						.getTargetStep()
						.round(upperBound
								+ configuration.getTargetStep().getStepDouble());
			else
				firstTarget = configuration.getTargetStep().roundUp(lowerBound);
		} else {
			for (ElementsPatterns e : children) {
				e.setTheTargets(e.elist[e.elist.length - 1].getRatio(
						ratioLevel, configuration.getDefaultScale()));
			}
		}
	}

	public boolean isLeaf() {
		return children == null;
	}

	public double getMaxSw0Ratio() {
		return maxSw0Ratio;
	}

	public double getMaxTarget() {
		return maxTarget;
	}

	public boolean isInfinite() {
		return isInfinite;
	}

	public int getLeafID() {
		return leafID;
	}

	public void setFirstTarget(double aFirstTarget) {
		firstTarget = aFirstTarget;
	}

	public void setLeafID(int aLeafID) {
		this.leafID = aLeafID;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void printMe(String pre) {
		System.out.println(pre + " (" + lowerBound + "," + upperBound
				+ "] with " + elist.length + " at index=" + ratioLevel);
		if (children != null) {
			for (ElementsPatterns e : children) {
				e.printMe(pre + "  ");
			}
		}
	}

	public double getLastTH() {
		return lastTH;
	}

	public int getSize() {
		return size;
	}

	public List<ElementsPatterns> getChildren() {
		return children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + leafID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementsPatterns other = (ElementsPatterns) obj;
		if (leafID != other.leafID)
			return false;
		return true;
	}

	public void setReachedT1Count(int aT1count) {
		this.t1count = aT1count;
	}

	/**
	 * gets the count of elements that reached T1. This is used for the patterns
	 * that have a finite upper bound for the
	 * Swing<sub>0</sub>/Swing<sub>-1</sub>.
	 * 
	 * @return a non-negative integer.
	 */
	public int getReachedT1Count() {
		return t1count;
	}

	/**
	 * gets the the probability to reach T1. This is used for the patterns that
	 * have a finite upper bound for the Swing<sub>0</sub>/Swing<sub>-1</sub>.
	 * 
	 * @return a value in [0..1].
	 */
	@SuppressWarnings("boxing")
	public double getT1Probability() {
		return new Double(t1count) / new Double(getSize());
	}

	public void setChildren(List<ElementsPatterns> aChildren) {
		children = aChildren;
	}

	public void setLowerBound(double aLowerBound) {
		lowerBound = aLowerBound;
	}

	public void setInfinite(boolean aIsInfinite) {
		isInfinite = aIsInfinite;
	}

	public void setUpperBound(double aUpperBound) {
		upperBound = aUpperBound;
	}

	public void setMaxSw0Ratio(double aMaxSw0Ratio) {
		maxSw0Ratio = aMaxSw0Ratio;
	}

	public void setMaxTarget(double aMaxTarget) {
		maxTarget = aMaxTarget;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration aConfiguration) {
		configuration = aConfiguration;
		Assert.isNotNull(aConfiguration);
	}

	public ElementsPatterns getParent() {
		return parent;
	}

	public void setParent(ElementsPatterns aParent) {
		parent = aParent;
	}

	public int getRatioLevel() {
		return ratioLevel;
	}

	public void setRatioLevel(int aRatioLevel) {
		ratioLevel = aRatioLevel;
	}

	@Override
	public String toString() {
		return "ElementsPatterns [size=" + size + ", leafID=" + leafID + ", L="
				+ lowerBound + ", U=" + upperBound + "]";
	}

}
