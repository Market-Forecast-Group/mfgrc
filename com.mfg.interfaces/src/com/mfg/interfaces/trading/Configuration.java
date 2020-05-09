package com.mfg.interfaces.trading;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.mfg.utils.StepDefinition;

/**
 * the configuration of parameters to run the probabilities calculation.
 * 
 * @author gardero
 * 
 */
@XmlType(name = "ProbabilitiesConfiguration")
@XmlSeeAlso(StepDefinition.class)
public class Configuration implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEPTH = "depth";
	private static final String SCMODE = "scMode";
	private static final String MIN_MATCHES_PERCENT = "minMachesPercent";
	private static final String TYPE = "type";
	private static final String MULTI_SCALE = "MultiScale";
	private static final String INTERVAL_STEP = "intervalStep";
	private static final String TARGET_STEP = "targetStep";
	private static final String CLUSTER_SIZE = "clusterSize";
	private static final String CLUSTER_BASE_SCALE = "clusterBaseScale";
	private static final String DEFAULT_SCALE = "defaultScale";
	private static final String INTERVAL = "interval";
	private static final String RATIO = "ratio";
	private static final String DIMENSION = "dimension";
	private static final String LOGGING = "logging";
	private static final String STARTSCALE = "startScale";
	private static final String ENDSCALE = "endScale";

	public enum SCMode {
		NoFilter, SC_Only, SC_and_Cluster;
	}

	private SCMode scMode = SCMode.SC_Only;
	private RefType type = RefType.Swing0;
	private int dimension = 2;
	private boolean[] ratioIncluded;
	private int[] intervals;
	private int referenceScale = 3;
	private StepDefinition intervalsStep = new StepDefinition(2, 1);
	private StepDefinition targetStep = new StepDefinition(1, 1);
	private int clusterSize = 2;
	private int startScale = 3, endScale = Integer.MAX_VALUE;
	private int depth = 1;// 2;
	private boolean multiscale = false;
	private double minMatchesPercent = 0.5;
	private boolean clusteringBaseScale = false;
	private int[] nextRatio;
	private boolean logging = true;
	private ComputationType computationType = ComputationType.S2ndTicks;
	private int ticksTargetStep = 1;
	private int scaleMultiplierTargetStep = 2;
	
	private boolean usingPriceClusters = false;
	private boolean usingTimeClusters = false;
	private int priceClustersInSw0 = 4;
	private int timeClustersInSw0st = 1;
	

	private transient PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private int minScale;
	private int maxScale;

	public Configuration() {
		setDimension(3);
		nextRatio = new int[]{1, 2, 0, 3, -1};

	}

	/**
	 * gets the maximum index of the ratio we can have.
	 * 
	 * @return
	 */
	public int getMaxRatioLevel() {
		for (int i = dimension; i >= 0; i--) {
			if (ratioIncluded[i])
				return i;
		}
		return 0;
	}

	/**
	 * gets the next index of the swing ratios.
	 * 
	 * @param aRatioLevel
	 *            the current index.
	 * @return
	 */
	public int getNextRatio(int aRatioLevel) {
		int res = nextRatio[aRatioLevel + 1];
		while (res != -1 && !isRatioIncluded(res)) {
			res = nextRatio[res + 1];
		}
		return res;
	}

	/**
	 * gets the dimension of the ratios
	 * 
	 * @return
	 */
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int aDimension) {
		dimension = aDimension;
		ratioIncluded = new boolean[dimension + 1];
//		ratioIncluded[0] = true;
//		ratioIncluded[1] = true;
		intervals = new int[dimension + 1];
		Arrays.fill(intervals, 2);
		intervals[0] = 3;
		firePropertyChange(DIMENSION);
	}

	/**
	 * gets if a ratio is included or not.
	 * 
	 * @param index
	 *            the index of the ratio
	 * @return
	 */
	public boolean isRatioIncluded(int index) {
		return ratioIncluded[index];
	}

	public void setRatioIncluded(int index, boolean include) {
		ratioIncluded[index] = include;
		firePropertyChange(RATIO);
	}

	/***
	 * gets the number of intervals to split the ratio values of that index.
	 * 
	 * @param index
	 *            the index
	 * @return
	 */
	public int getIntervals(int index) {
		return intervals[index];
	}

	public void setIntervals(int index, int aIntervals) {
		this.intervals[index] = aIntervals;
		firePropertyChange(INTERVAL);
	}
	
	public boolean[] getRatioIncluded() {
		return ratioIncluded;
	}

	public void setRatioIncluded(boolean[] aRatioIncluded) {
		this.ratioIncluded = aRatioIncluded;
	}

	public int[] getIntervals() {
		return intervals;
	}

	public void setIntervals(int[] aIntervals) {
		this.intervals = aIntervals;
	}

	/***
	 * gets the default scale.
	 * 
	 * @return
	 */
	public int getDefaultScale() {
		return referenceScale;
	}

	public void setDefaultScale(int aReferenceScale) {
		this.referenceScale = aReferenceScale;
		firePropertyChange(DEFAULT_SCALE);
	}

	/***
	 * gets if we are using the clusters of the base scale for the computation
	 * of the SC touch probabilities.
	 * 
	 * @return
	 */
	public boolean isClusteringBaseScale() {
		return clusteringBaseScale;
	}

	public void setClusteringBaseScale(boolean aClusteringBaseScale) {
		clusteringBaseScale = aClusteringBaseScale;
		firePropertyChange(CLUSTER_BASE_SCALE);
	}

	/***
	 * gets the cluster size.
	 * 
	 * @return
	 */
	public int getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(int aClusterSize) {
		this.clusterSize = aClusterSize;
		firePropertyChange(CLUSTER_SIZE);
	}

	/***
	 * gets the start scale.
	 * 
	 * @return
	 */
	public int getStartScale() {
		return startScale;
	}

	public void setStartScale(int aStartScale) {
		startScale = aStartScale;
		firePropertyChange(STARTSCALE);
	}

	public void fixStartScale(int aStartScale) {
		minScale = aStartScale;
		setStartScale(Math.max(aStartScale, startScale));
	}

	/**
	 * gets the end scale.
	 * 
	 * @return
	 */
	public int getEndScale() {
		return endScale;
	}

	public void setEndScale(int aEndScale) {
		endScale = aEndScale;
		firePropertyChange(ENDSCALE);
	}

	public void fixEndScale(int aEndScale) {
		maxScale = aEndScale;
		setEndScale(Math.min(aEndScale, endScale));
	}

	public int getMinScale() {
		return minScale;
	}

	public void setMinScale(int aMinScale) {
		minScale = aMinScale;
	}

	public int getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(int aMaxScale) {
		maxScale = aMaxScale;
	}

	/**
	 * gets the step of the targets.
	 * 
	 * @return
	 */
	public StepDefinition getTargetStep() {
		return targetStep;
	}

	public void setTargetStep(StepDefinition aTargetStep) {
		targetStep = aTargetStep;
		firePropertyChange(TARGET_STEP);
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean aLogging) {
		logging = aLogging;
		firePropertyChange(LOGGING);
	}

	/**
	 * gets the step of the intervals bounds.
	 * 
	 * @return
	 */
	public StepDefinition getIntervalsStep() {
		return intervalsStep;
	}

	public void setIntervalsStep(StepDefinition aIntervalsStep) {
		intervalsStep = aIntervalsStep;
		firePropertyChange(INTERVAL_STEP);
	}

	/***
	 * gets of the probabilities will be multi-scale of not.
	 * 
	 * @return
	 */
	public boolean isMultiscale() {
		return multiscale;
	}

	public void setMultiscale(boolean aMultiscale) {
		multiscale = aMultiscale;
		firePropertyChange(MULTI_SCALE);
	}

	/**
	 * gets the type of the references.
	 * 
	 * @return
	 */
	public RefType getType() {
		return type;
	}

	public void setType(RefType aType) {
		type = aType;
		firePropertyChange(TYPE);
	}

	@XmlTransient
	public List<RefType> getTypes() {
		ArrayList<RefType> res = new ArrayList<>();
		res.add(type);
		return res;
	}

	/***
	 * gets the depth of the dependency between scales.
	 * 
	 * @return
	 */
	public int getDepth() {
		return depth;
	}

	public void setDepth(int aDepth) {
		depth = Math.max(aDepth, 1);
		firePropertyChange(DEPTH);
	}

	/***
	 * gets the minimum matches percent.
	 * 
	 * @return
	 */
	public double getMinMatchesPercent() {
		return minMatchesPercent;
	}

	public void setMinMatchesPercent(double aMinMatchesPercent) {
		minMatchesPercent = aMinMatchesPercent;
		firePropertyChange(MIN_MATCHES_PERCENT);
	}

	/***
	 * gets the mode we will use for the calculation of the SC touch
	 * probabilities.
	 * 
	 * @return
	 */
	public SCMode getScMode() {
		return scMode;
	}

	public void setScMode(SCMode aScMode) {
		scMode = aScMode;
		firePropertyChange(SCMODE);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clusterSize;
		result = prime * result + ticksTargetStep;
		result = prime * result + scaleMultiplierTargetStep;
		result = prime * result + timeClustersInSw0st;
		result = prime * result + priceClustersInSw0;
		result = prime * result + (clusteringBaseScale ? 1231 : 1237);
		result = prime * result + (usingPriceClusters ? 1231 : 1237);
		result = prime * result + (usingTimeClusters ? 1231 : 1237);
		result = prime * result + depth;
		result = prime * result + dimension;
		result = prime * result + Arrays.hashCode(intervals);
		result = prime * result
				+ ((intervalsStep == null) ? 0 : intervalsStep.hashCode());
		long temp;
		temp = Double.doubleToLongBits(minMatchesPercent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (multiscale ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(ratioIncluded);
		result = prime * result + referenceScale;
		result = prime * result + ((scMode == null) ? 0 : scMode.hashCode());
		result = prime * result + ((computationType == null) ? 0 : computationType.hashCode());
		result = prime * result
				+ ((targetStep == null) ? 0 : targetStep.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Configuration other = (Configuration) obj;
		if (clusterSize != other.clusterSize)
			return false;
		if (ticksTargetStep != other.ticksTargetStep)
			return false;
		if (scaleMultiplierTargetStep != other.scaleMultiplierTargetStep)
			return false;
		if (priceClustersInSw0 != other.priceClustersInSw0)
			return false;
		if (timeClustersInSw0st != other.timeClustersInSw0st)
			return false;
		if (clusteringBaseScale != other.clusteringBaseScale)
			return false;
		if (usingPriceClusters != other.usingPriceClusters)
			return false;
		if (usingTimeClusters != other.usingTimeClusters)
			return false;
		if (depth != other.depth)
			return false;
		if (dimension != other.dimension)
			return false;
		if (!Arrays.equals(intervals, other.intervals))
			return false;
		if (intervalsStep == null) {
			if (other.intervalsStep != null)
				return false;
		} else if (!intervalsStep.equals(other.intervalsStep))
			return false;
		if (Double.doubleToLongBits(minMatchesPercent) != Double
				.doubleToLongBits(other.minMatchesPercent))
			return false;
		if (multiscale != other.multiscale)
			return false;
		if (!Arrays.equals(ratioIncluded, other.ratioIncluded))
			return false;
		if (referenceScale != other.referenceScale)
			return false;
		if (scMode != other.scMode)
			return false;
		if (targetStep == null) {
			if (other.targetStep != null)
				return false;
		} else if (!targetStep.equals(other.targetStep))
			return false;
		if (type != other.type)
			return false;
		if (computationType != other.computationType)
			return false;
		return true;
	}

	@Override
	public Configuration clone() {
		try {
			Configuration res = (Configuration) super.clone();
			res.ratioIncluded = ratioIncluded.clone();
			res.intervals = intervals.clone();
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getWorkingDepth() {
		return !multiscale ? 0 : getDepth();
	}

	public ComputationType getComputationType() {
		return computationType;
	}

	public void setComputationType(ComputationType aComputationType) {
		this.computationType = aComputationType;
		firePropertyChange("computationType");
	}

	public int getScaleMultiplier(int aScale) {
		return (int) (ticksTargetStep*Math.pow(2, Math.max(0,aScale-getDefaultScale())));
	}

	public boolean isUsingPriceClusters() {
		return usingPriceClusters;
	}

	public boolean isUsingTimeClusters() {
		return usingTimeClusters;
	}

	public int getPriceClustersInSw0() {
		return priceClustersInSw0;
	}

	public int getTimeClustersInSw0st() {
		return timeClustersInSw0st;
	}

	public void setUsingPriceClusters(boolean aUsingPriceClusters) {
		this.usingPriceClusters = aUsingPriceClusters;
		firePropertyChange("usingPriceClusters");
	}

	public void setUsingTimeClusters(boolean aUsingTimeClusters) {
		this.usingTimeClusters = aUsingTimeClusters;
		firePropertyChange("usingTimeClusters");
	}

	public void setPriceClustersInSw0(int aPriceClustersInSw0) {
		this.priceClustersInSw0 = aPriceClustersInSw0;
		firePropertyChange("priceClustersInSw0");
	}

	public void setTimeClustersInSw0st(int aTimeClustersInSw0st) {
		this.timeClustersInSw0st = aTimeClustersInSw0st;
		firePropertyChange("timeClustersInSw0st");
	}

	public int getTicksTargetStep() {
		return ticksTargetStep;
	}

	public int getScaleMultiplierTargetStep() {
		return scaleMultiplierTargetStep;
	}

	public void setTicksTargetStep(int aTicksTargetStep) {
		this.ticksTargetStep = aTicksTargetStep;
		firePropertyChange("ticksTargetStep");
	}

	public void setScaleMultiplierTargetStep(int aScaleMultiplierTargetStep) {
		this.scaleMultiplierTargetStep = aScaleMultiplierTargetStep;
		firePropertyChange("scaleMultiplierTargetStep");
	}

	
	
	
}
