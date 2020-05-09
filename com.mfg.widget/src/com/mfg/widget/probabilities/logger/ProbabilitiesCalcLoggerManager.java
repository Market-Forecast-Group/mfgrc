package com.mfg.widget.probabilities.logger;

import java.util.ArrayList;
import java.util.List;

import com.mfg.logger.ILogFilter;
import com.mfg.logger.memory.MemoryLoggerManager;

public class ProbabilitiesCalcLoggerManager extends MemoryLoggerManager
		implements IProbabilitiesCalcLoggerManager {

	private List<IProbabilitiesCalcLogView> views;
	private ILogFilter filter;

	public ProbabilitiesCalcLoggerManager() {
		super("Probabilities Logger", true);
		views = new ArrayList<>();
	}

	@Override
	public IProbabilitiesCalcLogView[] getViews() {
		return views.toArray(new IProbabilitiesCalcLogView[views.size()]);
	}

	@Override
	public IProbabilitiesCalcLogView getFirstView() {
		IProbabilitiesCalcLogView[] list = getViews();
		return list.length == 0 ? null : list[0];
	}

	public void addView(IProbabilitiesCalcLogView view) {
		views.add(view);
	}

	public void removeView(IProbabilitiesCalcLogView view) {
		views.remove(view);
	}

	@Override
	public void refreshViews() {
		connectFilter();
		for (IProbabilitiesCalcLogView view : views) {
			view.refresh();
		}
	}

	@Override
	public void clearViews() {
		for (IProbabilitiesCalcLogView view : views) {
			view.getAdapter().clearTable();
		}
	}

	@Override
	public void setFilter(ILogFilter filter1) {
		this.filter = filter1;
		// Display.getDefault().asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// connectFilter();
		// }
		// });
	}

	@Override
	public ILogFilter getFilter() {
		return filter;
	}

	private void connectFilter() {
		for (IProbabilitiesCalcLogView view : views) {
			view.setFilters(filter);
		}
		System.out.println("setting the probabilities filters");
	}
}
