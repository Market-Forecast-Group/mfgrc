package com.mfg.widget.probabilities.logger;

import org.eclipse.jface.viewers.TableViewer;

import com.mfg.logger.ILogFilter;
import com.mfg.logger.ui.LogViewerAdapter;
import com.mfg.logger.ui.views.ILogClient;
import com.mfg.logger.ui.views.ILogView;

public interface IProbabilitiesCalcLogView extends ILogView {
	@Override
	public String getName();

	@Override
	public void connectToClient(ILogClient client);

	public TableViewer getViewer();

	public LogViewerAdapter getAdapter();

	public void setFilters(ILogFilter... filters);

	public void refresh();

}
