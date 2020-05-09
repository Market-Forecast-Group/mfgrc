package com.mfg.interfaces.probabilities;

import java.io.Serializable;

/**
 * represents a SC touch probability key that groups statistics according to the
 * Scale, the Base Scale Cluster and the number of SC we have touched in the
 * swing.
 * 
 * @author gardero
 * 
 */
public class SCTProbabilityKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sctouches;
	private int baseScaleCluster;
	private int scale;

	public SCTProbabilityKey() {
		super();
	}

	/**
	 * builds an instance of this class.
	 * 
	 * @param aScale
	 *            the scale
	 * @param aSctouches
	 *            the number of SC we have touched.
	 * @param aBaseScaleCluster
	 *            the cluster of the corresponding probability of the Base
	 *            Scale.
	 */
	public SCTProbabilityKey(int aScale, int aSctouches, int aBaseScaleCluster) {
		super();
		this.scale = aScale;
		sctouches = aSctouches;
		baseScaleCluster = aBaseScaleCluster;
	}

	/***
	 * gets the number of SC touches.
	 * 
	 * @return a non-negative integer. (0 for the case we don't consider the SC
	 *         order)
	 */
	public int getSctouches() {
		return sctouches;
	}

	public void setSctouches(int aSctouches) {
		sctouches = aSctouches;
	}

	/**
	 * the cluster of the probability of the Base Scale.
	 * 
	 * @return a non-negative integer. (0 for the case we don't consider it).
	 */
	public int getBaseScaleCluster() {
		return baseScaleCluster;
	}

	public void setBaseScaleCluster(int aBaseScaleCluster) {
		baseScaleCluster = aBaseScaleCluster;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + scale;
		result = prime * result + baseScaleCluster;
		result = prime * result + sctouches;
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
		SCTProbabilityKey other = (SCTProbabilityKey) obj;
		if (baseScaleCluster != other.baseScaleCluster)
			return false;
		if (sctouches != other.sctouches)
			return false;
		if (scale != other.scale)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SCT ProbabilityKey [scale=" + scale + ", sc touches="
				+ sctouches + ", BScaleC=" + baseScaleCluster + "]";
	}

	/**
	 * gets the Scale.
	 * 
	 * @return
	 */
	public int getScale() {
		return scale;
	}

	public void setScale(int aScale) {
		scale = aScale;
	}

}
