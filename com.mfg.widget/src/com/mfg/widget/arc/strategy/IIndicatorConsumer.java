package com.mfg.widget.arc.strategy;

import com.mfg.interfaces.indicator.IIndicator;

public interface IIndicatorConsumer {
	public void consume(IndicatorConsumeArgs args);

	public void stopped(IIndicator indicator);
}
