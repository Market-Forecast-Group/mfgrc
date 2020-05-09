package com.mfg.dm.speedControl;

public enum DataSpeedControlState {
	PLAYING, STOPPED, PAUSED, FAST_FORWARDING, INITIAL, DISABLED,
	/**
	 * Only used for DFS controller.
	 */
	STEP;

	public boolean isEnabledPlayButton() {
		return this == INITIAL || this == PAUSED || this == FAST_FORWARDING
				|| this == STEP;
	}

	public boolean isEnabledStepButton() {
		return this == PAUSED || this == PLAYING || this == STEP;
	}

	public boolean isEnabledFFButton() {
		return this != INITIAL && this != FAST_FORWARDING && this != STOPPED
				&& this != DISABLED;
	}

	public boolean isEnabledPauseButton() {
		return this != INITIAL && this != PAUSED && this != STOPPED
				&& this != DISABLED && this != STEP;
	}

	public boolean isEnabledStopButton() {
		return this != INITIAL && this != STOPPED && this != DISABLED;
	}

	public boolean isEnabledMoreOrLessButton() {
		return isEnabledStopButton();
	}
}
