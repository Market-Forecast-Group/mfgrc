package com.mfg.widget.probabilities;

import java.io.Serializable;

import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.utils.ui.HtmlUtils.IHtmlStringProvider;

/**
 * the Higher Scale Information used to be shown on the chart.
 * 
 * @author gardero
 * 
 */
public class HSTargetInfo implements IHtmlStringProvider, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long price;
	int targetID;
	double target;
	ProbabilitiesKey key;
	private long pivotPrice;
	private long pivotTime;

	public HSTargetInfo(long aPrice, long pivotPrice1, long pivotTime1,
			int aTargetID, double aTarget, ProbabilitiesKey aKey) {
		super();
		price = aPrice;
		this.pivotPrice = pivotPrice1;
		this.pivotTime = pivotTime1;
		targetID = aTargetID;
		target = aTarget;
		key = aKey;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long aPrice) {
		price = aPrice;
	}

	public int getTargetID() {
		return targetID;
	}

	public void setTargetID(int aTargetID) {
		targetID = aTargetID;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double aTarget) {
		target = aTarget;
	}

	public ProbabilitiesKey getKey() {
		return key;
	}

	public void setKey(ProbabilitiesKey aKey) {
		key = aKey;
	}

	public long getPivotPrice() {
		return pivotPrice;
	}

	public void setPivotPrice(long pivotPrice1) {
		this.pivotPrice = pivotPrice1;
	}

	public long getPivotTime() {
		return pivotTime;
	}

	public void setPivotTime(long pivotTime1) {
		this.pivotTime = pivotTime1;
	}

	@Override
	public String toString() {
		return "HSTargetInfo [TID=" + targetID + ", T=" + target + ", P="
				+ price + ", key=" + key + ", PP=" + pivotPrice + "]";
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "[SC="
				+ key.getScale()
				+ ", TID="
				+ getTargetID()
				+ (key.getPatternID() > -1 ? (", PID=" + key.getPatternID())
						: "")
				+ (key.isContrarian() ? ", C" : ", NC")
				+ (key.getPriceClusterID() > 0 ? (", PCID=" + key
						.getPriceClusterID()) : "")
				+ (key.getTimeClusterID() > 0 ? (", TCID=" + key
						.getTimeClusterID()) : "") + "--> Target="
				+ MathUtils.normalizeUsingStep(getTarget(), 1, 100)
				+ ", TPrice=" + getPrice() + "]";
	}

}
