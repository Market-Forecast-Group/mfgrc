package com.mfg.dm.speedControl;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IDelayControl {
	public void delay(IProgressMonitor monitor, long currentTime);

	public void stop();

	/**
	 * @return
	 */
	public DataSpeedModel getModel();
}
