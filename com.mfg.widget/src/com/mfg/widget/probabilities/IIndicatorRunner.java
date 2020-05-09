package com.mfg.widget.probabilities;

import com.mfg.dm.ITickListener;
import com.mfg.interfaces.indicator.IIndicator;

public interface IIndicatorRunner extends ITickListener{

	public abstract IIndicator getIndicator();

	public abstract void setIndicator(IIndicator indicator2);

	public abstract void stop();

	public abstract boolean isStopped();

	public abstract void setStopped(boolean aStopped);

}