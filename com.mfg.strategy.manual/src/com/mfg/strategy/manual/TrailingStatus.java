package com.mfg.strategy.manual;

public class TrailingStatus {

	private boolean isLongRC;
	private boolean isLongCL;
	private boolean isLongSC;

	private boolean isShortRC;
	private boolean isShortCL;
	private boolean isShortSC;

	public TrailingStatus() {
		isLongRC = false;
		isLongCL = false;
		isLongSC = false;

		isShortRC = false;
		isShortCL = false;
		isShortSC = false;
	}

	public TrailingStatus(TrailingStatus status) {
		isLongRC = status.isLongRC();
		isLongCL = status.isLongCL();
		isLongSC = status.isLongSC();

		isShortRC = status.isShortRC();
		isShortCL = status.isShortCL();
		isShortSC = status.isShortSC();
	}

	public boolean isLongRC() {
		return isLongRC;
	}

	public void setLongRC(boolean aIsLongRC) {
		this.isLongRC = aIsLongRC;
	}

	public boolean isLongCL() {
		return isLongCL;
	}

	public void setLongCL(boolean aIsLongCL) {
		this.isLongCL = aIsLongCL;
	}

	public boolean isLongSC() {
		return isLongSC;
	}

	public void setLongSC(boolean aIsLongSC) {
		this.isLongSC = aIsLongSC;
	}

	public boolean isShortRC() {
		return isShortRC;
	}

	public void setShortRC(boolean aIsShortRC) {
		this.isShortRC = aIsShortRC;
	}

	public boolean isShortCL() {
		return isShortCL;
	}

	public void setShortCL(boolean aIsShortCL) {
		this.isShortCL = aIsShortCL;
	}

	public boolean isShortSC() {
		return isShortSC;
	}

	public void setShortSC(boolean aIsShortSC) {
		this.isShortSC = aIsShortSC;
	}

}
