package com.mfg.interfaces.probabilities;

import java.awt.Color;
import java.io.Serializable;

import com.mfg.interfaces.trading.RefType;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents a Targets probability key that groups statistics according to the
 * Scale, the Higher Scale Cluster, the Pattern and the the direction relative
 * to the Higher Scale Swing.
 * 
 * @author gardero
 * 
 */
public class ProbabilitiesKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2986576464006066452L;
	private RefType type = RefType.Swing0;
	private int clusterID, scale, baseScale;
	private int priceClusterID, timeClusterID;
	private ElementsPatterns pattern;
	private boolean contrarian;

	/**
	 * builds an instance of this class.
	 * 
	 * @param aScale
	 *            the scale
	 * @param aBaseScale
	 *            the base scale that is being computed.
	 * @param aPattern
	 *            the pattern of the swing ratios.
	 * @param aContrarian
	 *            the direction to the HS swing ({@code true} iff contrarian)
	 * @param aClusterID
	 *            the HS cluster ID
	 * @param aType
	 *            the type of reference.
	 */
	public ProbabilitiesKey(int aScale, int aBaseScale,
			ElementsPatterns aPattern, boolean aContrarian, int aClusterID,
			RefType aType, int aTimeClusterID, int aPriceClusterID) {
		super();
		scale = aScale;
		this.baseScale = aBaseScale;
		pattern = aPattern;
		contrarian = aContrarian;
		clusterID = aClusterID;
		type = aType;
		this.priceClusterID = aPriceClusterID;
		this.timeClusterID = aTimeClusterID;
		
	}
	public ProbabilitiesKey(int aBaseScale, ElementsPatterns aPattern,
			boolean aContrarian, int aClusterID, RefType aRefType) {
		this(aBaseScale, aBaseScale, aPattern, aContrarian, aClusterID, aRefType,0,0);
	}
	public ProbabilitiesKey(int aBaseScale, ElementsPatterns aPattern,
			RefType aRefType) {
		this(aBaseScale, aBaseScale, aPattern, true, 0, aRefType,0,0);
	}
	public ProbabilitiesKey() {
		super();
	}

	/**
	 * the HS cluster ID.
	 * 
	 * @return a non-negative integer. Zero represents
	 */
	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int aClusterID) {
		clusterID = aClusterID;
	}
	
	public int getPriceClusterID() {
		return priceClusterID;
	}
	public void setPriceClusterID(int aPriceClusterID) {
		this.priceClusterID = aPriceClusterID;
	}
	public int getTimeClusterID() {
		return timeClusterID;
	}
	public void setTimeClusterID(int aTimeClusterID) {
		this.timeClusterID = aTimeClusterID;
	}
	/**
	 * the pattern ID.
	 * 
	 * @return
	 */
	public int getPatternID() {
		return pattern.getLeafID();
	}

	/**
	 * gets the direction to the HS swing ({@code true} iff contrarian)
	 * 
	 * @return
	 */
	public boolean isContrarian() {
		return contrarian;
	}

	public void setContrarian(boolean aContrarian) {
		contrarian = aContrarian;
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

	/**
	 * gets the Base Scale
	 * 
	 * @return
	 */
	public int getBaseScale() {
		return baseScale;
	}

	public void setBaseScale(int aBaseScale) {
		baseScale = aBaseScale;
	}

	/**
	 * gets the reference Type.
	 * 
	 * @return
	 */
	public RefType getType() {
		return type;
	}

	public void setType(RefType aType) {
		type = aType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + baseScale;
		result = prime * result + clusterID;
		result = prime * result + priceClusterID;
		result = prime * result + timeClusterID;
		result = prime * result + (contrarian ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + scale;
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
		ProbabilitiesKey other = (ProbabilitiesKey) obj;
		if (baseScale != other.baseScale)
			return false;
		if (clusterID != other.clusterID)
			return false;
		if (timeClusterID != other.timeClusterID)
			return false;
		if (priceClusterID != other.priceClusterID)
			return false;
		if (contrarian != other.contrarian)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (scale != other.scale)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProbKey [sc=" + scale + ", baseSC=" + getBaseScale() + ", PID="
				+ getPatternID() + ", C=" + contrarian + ", CID=" + clusterID +
				"]";
	}

	/**
	 * gets the pattern if this Key.
	 * 
	 * @return
	 */
	public ElementsPatterns getPattern() {
		return pattern;
	}

	public void setPattern(ElementsPatterns aPattern) {
		pattern = aPattern;
	}

	public String toHtmlString(HtmlUtils hutils) {
		return hutils.bold(hutils.color("[", Color.BLUE))
				+ "SC="+ scale
				+ ((clusterID == 0) ? "" : ", " + (contrarian ? "C" : "NC")
						+ ", CID=" + clusterID) + 
				(priceClusterID>0?(", PCID="+priceClusterID):"")+
				(timeClusterID>0?(", TCID="+timeClusterID):"")+
				", PID=" + getPatternID() +
				hutils.bold(hutils.color("]", Color.BLUE));
	}

	/**
	 * asks if it is on the base scale.
	 * 
	 * @return {@code true} iff scale==baseScale.
	 */
	public boolean isOnTheBase() {
		return scale == baseScale;
	}

	
}
