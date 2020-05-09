package com.mfg.widget.probabilities;

import com.mfg.common.DFSException;
import com.mfg.interfaces.indicator.IIndicator;

public interface IndicatorRunner {

	void run(IIndicatorRunner ts) throws DFSException;

	void buildIndicator();

	IIndicator getIndicator();

}
