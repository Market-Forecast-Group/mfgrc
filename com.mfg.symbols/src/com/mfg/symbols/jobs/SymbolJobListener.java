/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.jobs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.ui.views.ChartDBBrowserView;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.chart.ui.views.IAlternativeChartContent;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.adapters.SyntheticInput;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class SymbolJobListener extends SymbolJobChangeAdapter {
	@Override
	public void tradingStarted(TradingPipeChangeEvent event) {
		restartTradingPipe(event.getTradingPipe());
	}

	@Override
	public void tradingRestarted(TradingPipeChangeEvent event) {
		restartTradingPipe(event.getTradingPipe());
	}

	/**
	 * @param tradingPipe
	 */
	private static void restartTradingPipe(TradingPipe tradingPipe) {
		startOpenTradingViews(Arrays.asList(tradingPipe), SymbolsPlugin
				.getDefault().getOpenTradingViews());
	}

	@Override
	public void tradingStopped(TradingPipeChangeEvent event) {
		stopTradingPipe(SymbolsPlugin.getDefault().getOpenTradingViews(),
				event.getTradingPipe());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.jobs.SymbolJobChangeAdapter#inputStopped(com.mfg.symbols
	 * .jobs.InputPipeChangeEvent)
	 */
	@Override
	public void inputStopped(InputPipeChangeEvent event) {
		stopInputPipe(event.getPipe());
	}

	@Override
	public void warmingUpFinished(SymbolJobChangeEvent event) {
		reconnectSynthCharts();
	}

	private static void reconnectSynthCharts() {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		for (ChartView view : views) {
			Object content = view.getContent();
			if (content instanceof SyntheticInput) {
				view.setContent(content);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.JobChangeAdapter#aboutToRun(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		if (PlatformUI.isWorkbenchRunning()) {
			if (event.getJob() instanceof SymbolJob<?>) {
				SymbolJob<?> job = (SymbolJob<?>) event.getJob();
				List<TradingPipe> pipes = job.getTradingPipes();
				if (pipes.size() > 0) {
					// show in trading views
					SymbolsPlugin plugin = SymbolsPlugin.getDefault();
					String[] tradingViewIds = plugin.getTradingViewIds();
					for (String viewId : tradingViewIds) {
						List<ITradingView> views = SymbolsPlugin
								.getOpenTradingViews(viewId);
						startOpenTradingViews(pipes, views);
					}
				}
				startOpenChartViews(job);
				reconnectOpenDBBrowsers(job);
			}
		}
	}

	@Override
	public void done(IJobChangeEvent event) {
		if (event.getJob() instanceof SymbolJob<?>) {

			final SymbolJob<?> job = (SymbolJob<?>) event.getJob();
			stopOpenChartViews(job);
			try {
				job.dispose();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	private static void reconnectOpenDBBrowsers(SymbolJob<?> job) {
		List<ChartDBBrowserView> views = PartUtils
				.getOpenViews(ChartDBBrowserView.VIEW_ID);

		for (ChartDBBrowserView view : views) {
			Object content = view.getContent();
			if (content != null) {
				if (content == job.getSymbolConfiguration()) {
					view.setSession(job.getMdbSession(), null, null, content);
				} else {
					for (InputPipe input : job.getInputPipes()) {
						if (input.getConfiguration() == content) {
							view.setSession(job.getMdbSession(),
									input.getMdbSession(), null, content);
						} else {
							for (TradingPipe trading : job.getTradingPipes()) {
								if (trading == content
										&& trading.getInputPipe() == input) {
									view.setSession(job.getMdbSession(),
											input.getMdbSession(),
											trading.getMdbSession(), content);
								}
							}
						}
					}
				}
			}
		}
	}

	private static void startOpenChartViews(SymbolJob<?> job) {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		for (InputPipe pipe : job.getInputPipes()) {
			startChartViews(views, pipe.getConfiguration());
		}
		startChartViews(views, job.getSymbolConfiguration());
	}

	/**
	 * @param views
	 * @param content
	 */
	private static void startChartViews(List<ChartView> views, Object content) {
		boolean set = false;
		for (ChartView view : views) {
			if (match(view.getContent(), content)) {
				view.setContent(content);
				set = true;
			}
		}
		if (!set) {
			for (ChartView view : views) {
				if (match(view.getContent(), content)) {
					view.setContent(content);
					break;
				}
			}
		}
	}

	/**
	 * @param content
	 * @param content2
	 * @return
	 */
	private static boolean match(Object content1, Object content2) {
		if (content1 == null || content2 == null) {
			return false;
		}

		if (content1 instanceof IAlternativeChartContent) {
			Object alt = ((IAlternativeChartContent) content1)
					.getAlternativeContent(content2);
			if (alt != null) {
				return match(alt, content2);
			}
		}

		if (content2 instanceof IAlternativeChartContent) {
			Object alt = ((IAlternativeChartContent) content2)
					.getAlternativeContent(content1);
			if (alt != null) {
				return match(alt, content1);
			}
		}

		return content1.equals(content2) || content2.equals(content1);
	}

	/**
	 * @param pipes
	 * @param plugin
	 * @param list
	 * @param viewId
	 */
	private static void startOpenTradingViews(List<TradingPipe> pipes,
			List<ITradingView> views) {
		synchronized (pipes) {
			for (final TradingPipe pipe : pipes) {
				boolean set = false;
				for (final ITradingView view : views) {
					// restart connected views
					int pipeSet = pipe.getConfiguration().getInfo()
							.getConfigurationSet();

					if (view.getConfiguration() != null) {
						int viewSet = view.getConfiguration().getInfo()
								.getConfigurationSet();
						if (viewSet == pipeSet) {
							set = true;
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									view.setConfiguration(pipe
											.getConfiguration());
								}
							});
						}
					}
				}

				if (!set) {
					// start empty view
					for (final ITradingView view : views) {
						if (view.getConfiguration() == null) {
							Display.getDefault().syncExec(new Runnable() {

								@Override
								public void run() {
									view.setConfiguration(pipe
											.getConfiguration());
								}
							});
						}
					}
				}
			}
		}
	}

	private static void stopOpenChartViews(SymbolJob<?> job) {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);

		if (job.isCanceledByAppShutdown()) {
			for (ChartView view : views) {
				view.shuttingDown();
			}
		} else {
			for (ChartView view : views) {
				Object content = view.getContent();
				if (!(content instanceof TradingConfiguration || content instanceof InputConfiguration)) {
					// trading charts are closed by stopTradingPipe() method
					// input charts are closed by stopInputPipe() method
					if (job.belongsTo(content)) {
						view.setContent(content);
					}
				}
			}
		}
	}

	/**
	 * @param views
	 * @param pipe
	 */
	private static void stopTradingPipe(List<ITradingView> views,
			final TradingPipe pipe) {

		SymbolJob<?> job = pipe.getSymbolJob();

		if (job.isCanceledByAppShutdown()) {
			for (ITradingView view : views) {
				if (view instanceof ChartView) {
					((ChartView) view).shuttingDown();
				}
			}
		} else {
			for (final ITradingView view : views) {
				if (view.getConfiguration() != null
						&& view.getConfiguration() == pipe.getConfiguration()) {
					view.setConfiguration(view.getConfiguration());
				}
			}
		}
	}

	private static void stopInputPipe(final InputPipe pipe) {
		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		SymbolJob<?> job = pipe.getSymbolJob();
		if (job.isCanceledByAppShutdown()) {
			for (ChartView view : views) {
				view.shuttingDown();
			}
		} else {
			for (final ChartView view : views) {
				if (match(view.getContent(), pipe.getConfiguration())) {
					view.setContent(pipe.getConfiguration());
				} else if (view.getContent() instanceof TradingConfiguration) {
					// let's check the case where the chart contains an stopped
					// trading configuration but yet the input is running
					TradingConfiguration trading = (TradingConfiguration) view
							.getContent();
					UUID refid = trading.getInfo().getInputConfiguratioId();
					if (pipe.getConfiguration().getUUID().equals(refid)) {
						view.setContent(trading);
					}
				}
			}
		}
	}
}
