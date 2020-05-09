/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
/**
 *
 */

package com.mfg.chart.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.ChartType;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.IAppLogger;
import com.mfg.utils.concurrent.TwoElementsRequestQueue;

public class AbstractRTChartView extends AbstractChartView {
	private static final String CONTEXT_REAL_TIME_ID = "com.mfg.chart.contexts.chartView.realtime";
	Action enableScrollingAction;
	Action enableZoomingOutAction;
	private Action enableAlwaysUpdateChartAction;
	TwoElementsRequestQueue queue;
	protected IAppLogger logger;
	private IContextActivation contextRTChartActivation;
	final int maxDelay = ChartPlugin
			.getDefault()
			.getPreferenceStore()
			.getInt(ChartPlugin.PREFERENCES_REALTIME_UPDATE_ON_TICK_SLEEP_VALUE);
	private int startScrollingPercent;
	private int stopScrollingPercent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#getContent()
	 */
	@Override
	public Object getContent() {
		throw new UnsupportedOperationException("Not supported methods.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#setContent(java.lang.Object)
	 */
	@Override
	public void setContent(Object newContent) {
		throw new UnsupportedOperationException("Not supported methods.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#getContentAdapter()
	 */
	@Override
	public IChartContentAdapter getContentAdapter() {
		throw new UnsupportedOperationException("Not supported methods.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.AbstractChartView#chartPluginPreferenceStoreChanged
	 * (org.eclipse.jface.preference.IPreferenceStore)
	 */
	@Override
	public void preferenceStoreChanged(IPreferenceStore store) {
		startScrollingPercent = store
				.getInt(ChartPlugin.PREFERENCES_START_SCROLLING_PERCENT);
		stopScrollingPercent = store
				.getInt(ChartPlugin.PREFERENCES_STOP_SCROLLING_PERCENT);
		super.preferenceStoreChanged(store);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.chart.ChartView#getChartType()
	 */
	@Override
	protected ChartType getChartType() {
		return ChartType.TRADING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.ChartView#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		queue = new TwoElementsRequestQueue("RT Chart Painting");
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				queue.close();
			}
		});

		logger = LoggerPlugin.getDefault().getAppLogger(
				ChartPlugin.LOGGER_REALTIME_CHATR_COMPONENT_ID, getPartName());

		final IContextService service = (IContextService) getSite().getService(
				IContextService.class);
		contextRTChartActivation = service
				.activateContext(CONTEXT_REAL_TIME_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		queue.close();
		super.dispose();
		final IContextService service = (IContextService) getSite().getService(
				IContextService.class);
		service.deactivateContext(contextRTChartActivation);
	}

	protected void restartChartThread() {
		queue.restart();
	}

	public void updateChartOnTick() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Display display = Display.getDefault();
				long delay = System.currentTimeMillis();
				if (display != null) {
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							swtUpdateChartOnTick();
						}
					});
					delay = System.currentTimeMillis() - delay;
					if (delay < maxDelay) {
						try {
							delay = maxDelay - delay;
							Thread.sleep(delay);
						} catch (final InterruptedException e) {
							//
						}
					}
				}
			}
		};
		queue.addRequest(runnable);
	}

	public void close() {
		queue.close();
	}

	protected boolean isChartThreadRunning() {
		return !queue.isClosed();
	}

	protected void swtUpdateChartOnTick() {
		final Chart chart = getChart();
		int dataLayer = chart.getDataLayer();
		String action = null;
		if (enableAlwaysUpdateChartAction != null && chart.getModel().isAlive()) {
			if (enableScrollingAction.isChecked()) {
				scrollChartToEnd(chart);
				action = "ScrollToEnd";
			} else if (enableZoomingOutAction.isChecked()) {
				chart.zoomOutAll(true);
				action = "ZoomOutAll";
			} else {
				final long lastTime = chart.getModel().getPriceModel()
						.getUpperDisplayTime(dataLayer);
				final boolean inChangingRange = chart.getXRange().contains(
						lastTime)
						|| lastTime < 10;
				if (enableAlwaysUpdateChartAction.isChecked()
						|| inChangingRange) {
					action = "UpdateRange";
					chart.update(chart.isAutoRangeEnabled());
				}
			}
		}

		if (logger.isLoggable(logger.getLevel())) {
			logger.logDebug("Updated Chart %s %s", getPartName(),
					action == null ? "" : "(" + action + ")");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.AbstractChartView#fillMenuBar(org.eclipse.jface
	 * .action.IMenuManager)
	 */
	@Override
	protected void fillMenuBar(final IMenuManager manager) {
		super.fillMenuBar(manager);
		manager.add(new Separator("real-time"));
		manager.add(enableAlwaysUpdateChartAction);
		manager.add(enableZoomingOutAction);
		manager.add(enableScrollingAction);
	}

	/**
	 * @return the enableScrollingAction
	 */
	public Action getEnableScrollingAction() {
		return enableScrollingAction;
	}

	/**
	 * @return the enableZoomingOutAction
	 */
	public Action getEnableZoomingOutAction() {
		return enableZoomingOutAction;
	}

	protected void createActions() {

		enableScrollingAction = new Action("Scrolling", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				if (isChecked()) {
					enableZoomingOutAction.setChecked(false);
				}
				_chart.setScrollingMode(_chart.getScrollingMode()
						.swapScrolling());
			}
		};
		// enableScrollToEndAction.setImageDescriptor(ImageDescriptor.createFromImage(sharedImages.getImage(ISharedImages.IMG_TOOL_FORWARD)));
		enableScrollingAction.setChecked(false);
		enableScrollingAction
				.setToolTipText("Scroll the chart when a new tick arrives.");

		enableZoomingOutAction = new Action("Zooming Out", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				if (isChecked()) {
					enableScrollingAction.setChecked(false);
				}
			}
		};

		// enableUpdateChartRangeAction.setImageDescriptor(ImageDescriptor.createFromImage(sharedImages.getImage(ISharedImages.IMG_ELCL_SYNCED)));
		enableZoomingOutAction.setChecked(true);
		enableZoomingOutAction
				.setToolTipText("Zoom out the chart when a new tick arrives.");

		enableAlwaysUpdateChartAction = new Action("Always Update Chart",
				IAction.AS_CHECK_BOX) {
			//
		};
		enableAlwaysUpdateChartAction
				.setToolTipText("Always Uupdate the chart, even if the current range does not include the last ticks.");
		enableAlwaysUpdateChartAction.setChecked(false);
	}

	/**
	 * @param chart
	 */
	private void scrollChartToEnd(final Chart chart) {
		int dataLayer = _chart.getDataLayer();
		final PlotRange xrange = chart.getXRange();
		final double len = xrange.getLength();
		final long lastTime = chart.getModel().getPriceModel()
				.getUpperDisplayTime(dataLayer);
		double startScrollingTime = xrange.upper
				- (startScrollingPercent / 100.0) * len;
		double stopScrollingShift = len * stopScrollingPercent / 100.0;

		if (lastTime > startScrollingTime || lastTime < xrange.lower) {
			chart.setXRange(new PlotRange(lastTime - stopScrollingShift,
					lastTime + len - stopScrollingShift));
		}
		chart.fireRangeChanged();
	}
}
