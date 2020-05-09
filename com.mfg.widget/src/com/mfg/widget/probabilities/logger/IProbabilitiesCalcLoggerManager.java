package com.mfg.widget.probabilities.logger;

import java.util.List;

import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;

public interface IProbabilitiesCalcLoggerManager extends ILoggerManager {
	public IProbabilitiesCalcLogView[] getViews();

	public IProbabilitiesCalcLogView getFirstView();

	public void refreshViews();

	public void clearViews();

	public void setFilter(ILogFilter filter);

	public ILogFilter getFilter();

	public void changeMemory(List<ILogRecord> newLog);
}
