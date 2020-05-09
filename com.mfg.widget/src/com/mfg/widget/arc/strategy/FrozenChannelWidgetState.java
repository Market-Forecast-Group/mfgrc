package com.mfg.widget.arc.strategy;

import java.io.Serializable;

/**
 * 
 * @author gardero
 */
public class FrozenChannelWidgetState implements Serializable {

	private static final long serialVersionUID = 1L;

	public int pivotIndex;
	public boolean isThereANewPivot;
	public int HHTime;
	public int LLTime;
	public long HHPrice;
	public long LLPrice;
	public double top___RegressionPrice;
	public double centerRegressionPrice;
	public double bottomRegressionPrice;
	public double chslope;

	int currentSCTouches;

	int currentRCTouches;

	public double getHHPrice() {
		return HHPrice;
	}

	public long getHHTime() {
		return HHTime;
	}

	public double getLLPrice() {
		return LLPrice;
	}

	public long getLLTime() {
		return LLTime;
	}

	public double getBottomRegressionPrice() {
		return bottomRegressionPrice;
	}

	public double getCenterRegressionPrice() {
		return centerRegressionPrice;
	}

	public boolean isIsThereANewPivot() {
		return isThereANewPivot;
	}

	public int getPivotIndex() {
		return pivotIndex;
	}

	public double getTop___RegressionPrice() {
		return top___RegressionPrice;
	}

	public void setHHPrice(long HHPrice1) {
		this.HHPrice = HHPrice1;
	}

	public void setHHTime(int HHTime1) {
		this.HHTime = HHTime1;
	}

	public void setLLPrice(long LLPrice1) {
		this.LLPrice = LLPrice1;
	}

	public void setLLTime(int LLTime1) {
		this.LLTime = LLTime1;
	}

	public void setBottomRegressionPrice(double bottomRegressionPrice1) {
		this.bottomRegressionPrice = bottomRegressionPrice1;
	}

	public void setCenterRegressionPrice(double centerRegressionPrice1) {
		this.centerRegressionPrice = centerRegressionPrice1;
	}

	public void setIsThereANewPivot(boolean isThereANewPivot1) {
		this.isThereANewPivot = isThereANewPivot1;
	}

	// public void setPivotIndex(int pivotIndex1) {
	// this.pivotIndex = pivotIndex1;
	// }

	public void setTop___RegressionPrice(double top___RegressionPrice1) {
		this.top___RegressionPrice = top___RegressionPrice1;
	}

	public int getCurrentSCTouches() {
		return this.currentSCTouches;
	}

	public int getCurrentRCTouches() {
		return this.currentRCTouches;
	}

}
