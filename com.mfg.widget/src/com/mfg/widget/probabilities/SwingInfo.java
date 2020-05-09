package com.mfg.widget.probabilities;

import org.eclipse.core.runtime.Assert;


/**
 * represents the Information of the swings in the series. 
 * @author gardero
 *
 */
public abstract class SwingInfo {
	
	/**
	 * the price length of a swing.
	 * @return
	 */
	public abstract double getSwingLength();
	/**
	 * the price length of the previous swing.
	 * @return
	 */
	public abstract double getPreviousSwingLength();
	/***
	 * gets the length of the target delta. It is relative to the 
	 * Pivot<sub>0</sub>. During the swing<sub>0</sub> it is equal to
	 * the swing length.
	 * @return
	 */
	public abstract double getTargetLength();
	
	/***
	 * gets the distance from TH<sub>0</sub> to Pivot<sub>0</sub>
	 * @return
	 */
	public abstract double getTHLength();

	@Deprecated
	public abstract double getTargetPrice();

	/**
	 * gets the swing length divided by swing<sub>-1</sub> length.
	 * @return
	 */
	public double getSwingLengthPoints(){
		return getSwingLength()/getPreviousSwingLength();
	}
	/**
	 * gets the target length divided by swing<sub>-1</sub> length.
	 * @return
	 */
	public double getTargetLengthPoints(){
		return getTargetLength()/getPreviousSwingLength();
	}
	
	public double getTarget00LengthPoints(){
		double targetLength = getTargetLength();
		double thLength = getTHLength();
		Assert.isTrue(targetLength>=thLength);
		return (targetLength/thLength-1);
	}
	/**
	 * gets the TH length divided by swing<sub>-1</sub> length.
	 * @return
	 */
	public double getTHLengthPoints() {
		return getTHLength()/getPreviousSwingLength();
	}
	
}
