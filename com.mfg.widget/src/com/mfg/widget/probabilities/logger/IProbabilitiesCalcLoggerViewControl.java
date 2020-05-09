package com.mfg.widget.probabilities.logger;

public interface IProbabilitiesCalcLoggerViewControl {
	public void loggerGotoExecution(long time);

	public void loggerGotoNextExecution();

	public void loggerGotoPrevExecution();

	public void loggerGotoFirstExecution();

	public void loggerGotoLastExecution();
}
