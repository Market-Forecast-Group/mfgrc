package com.mfg.widget.probabilities.logger;

public interface IProbabilitiesCalcLoggerClient {
	public void clientGotoPrice(long time, long price);

	public void clientTheLoggerSelectionChanged(long selectedTime,
			long selectedPrice);

	public void clientGetTheView(IProbabilitiesCalcLogView view);

	public void clientGetTheLoggerControl(IProbabilitiesCalcLoggerViewControl control);

}
