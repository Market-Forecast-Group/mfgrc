package com.mfg.plstats;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.TradingModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.ui.ILogTableModel;
import com.mfg.logger.ui.views.AbstractLogView;
import com.mfg.logger.ui.views.AbstractLoggerViewControl;
import com.mfg.plstats.charts.HSProbModel;
import com.mfg.plstats.charts.IndicatorChartView;
import com.mfg.utils.PartUtils;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.SimpleLogMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.KeyLogMessage;
import com.mfg.widget.probabilities.logger.ILogNavigatorControlerView;
import com.mfg.widget.probabilities.logger.IProbabilitiesCalcLogView;
import com.mfg.widget.probabilities.logger.IProbabilitiesCalcLoggerManager;
import com.mfg.widget.probabilities.logger.ProbabilitiesCalcLogTableModel;
import com.mfg.widget.probabilities.logger.ProbabilitiesCalcLoggerManager;

public class ProbabilitiesCalcLogView extends AbstractLogView implements
		IProbabilitiesCalcLogView {

	public static String ID = "com.mfg.widget.probabilities.logger.ProbabilitiesLogView";
	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private static final String CONTROL_ID = "com.mfg.widget.probabilities.logger.ProbabilitiesLogView.ControlID";

	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private ILogNavigatorControlerView navcomp;
	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private long fControlID;
	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private AbstractLoggerViewControl control;

	@Override
	protected ILogTableModel createLogModel() {
		return new ProbabilitiesCalcLogTableModel(getLogManager().getReader());
	}

	@Override
	public ILoggerManager getLogManager() {
		return WidgetPlugin.getDefault().getProbabilitiesManager()
				.getLogManager();
	}

	@Override
	public String getName() {
		return getPartName();
	}

	@Override
	protected void fillToolBar(IToolBarManager toolBar) {
		super.fillToolBar(toolBar);
		toolBar.add(new Action("Open Chart") {
			@Override
			public ImageDescriptor getImageDescriptor() {
				return PLStatsPlugin
						.getBundledImageDescriptor("icons/trade.jpg");
			}

			@Override
			public void run() {
				openChart();
				// ILogRecord sel = getSelectedRecord();
				// ISimpleLogMessage msg = (ISimpleLogMessage) sel.getMessage();
				//
				// for (ILoggerClient client : getLoggerClients()) {
				// client.clientGotoPrice(msg.getTime(), msg.getPrice());
				// }
			}
		});
		getViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						ILogRecord record = getSelectedRecord();
						if (record == null)
							return;
						Object message = record.getMessage();

						List<IndicatorChartView> views = PartUtils
								.getOpenViews(IndicatorChartView.VIEW_ID);

						IndicatorConfiguration configuration = getIndicatorConfiguration();
						Assert.isNotNull(configuration);
						for (IndicatorChartView view : views) {
							Chart chart = view.getChart();
							if (view.getConfiguration().equals(configuration)) {
								if (message instanceof KeyLogMessage) {
									KeyLogMessage keyMsg = (KeyLogMessage) message;
									System.out.println("Arian paint this"
											+ keyMsg.getVisitedTargetPrices());
									// As you can see, I don't need to use the
									// PLStatsPlugin.getDefault().getIndicatorManager().
									// As general case, we don't need these kind
									// of
									// managers any more. We just ask to eclipse
									// for
									// open views.
									IChartModel chartModel = chart.getModel();
									ITradingModel indicatorModel = chartModel
											.getTradingModel();
									if (indicatorModel instanceof TradingModel_MDB) {
										out.println("show " + keyMsg
												+ " in chart model "
												+ chartModel);
										HSProbModel hsProbsModel = new HSProbModel(
												keyMsg);
										TradingModel_MDB indicatorModel2 = (TradingModel_MDB) indicatorModel;
										indicatorModel2.setHSProbModel(
												keyMsg.getScale(), hsProbsModel);

										chart.getIndicatorLayer()
												.setVisibleByUser(
														chart.getIndicatorLayer()
																.getScaleLayer(
																		keyMsg.getScale())
																.getProbsLayer(),
														true);
										List<Double> yList = new ArrayList<>();
										List<Long> xList = new ArrayList<>();
										for (int i = 0; i < hsProbsModel
												.getSquaresCount(); i++) {
											yList.add(Double
													.valueOf(hsProbsModel
															.getPrice0(i)));
											yList.add(Double
													.valueOf(hsProbsModel
															.getPrice1(i)));
											xList.add(Long.valueOf(hsProbsModel
													.getTime0(i)));
											xList.add(Long.valueOf(hsProbsModel
													.getTime1(i)));
										}
										double minY = Double.MAX_VALUE;
										double maxY = 0;
										long minX = Long.MAX_VALUE;
										long maxX = 0;
										for (double y : yList) {
											if (y > maxY) {
												maxY = y;
											}
											if (y < minY) {
												minY = y;
											}
										}
										for (long x : xList) {
											if (x > maxX) {
												maxX = x;
											}
											if (x < minX) {
												minX = x;
											}
										}
										PlotRange yrange = chart.getYRange();
										PlotRange xrange = chart.getXRange();
										if (!(yrange.contains(maxY)
												&& yrange.contains(minY)
												&& xrange.contains(maxX) && xrange
												.contains(minX))) {
											chart.setAutoRangeEnabled(false);
											double margin = 0.10 * (maxY - minY);
											chart.setYRange(new PlotRange(minY
													- margin, maxY + margin));
											margin = 0.10 * (maxX - minX);
											chart.setXRange(new PlotRange(minX
													- margin, maxX + margin));
											chart.setCrosshairInPlot(chart
													.getXRange().getMiddle(),
													chart.getYRange()
															.getMiddle());
											chart.fireRangeChanged();
										} else {
											chart.update(chart
													.isAutoRangeEnabled());
										}
									}
								} else {
									if (message instanceof SimpleLogMessage) {
										SimpleLogMessage msg = (SimpleLogMessage) message;
										chart.setCrosshairInPlot(msg.getTime(),
												msg.getPrice());
										chart.setXRange(chart.getXRange()
												.getMovedTo(msg.getTime()));
										chart.fireRangeChanged();
									}
								}
							}

						}

					}
				});
	}

	static IndicatorConfiguration getIndicatorConfiguration() {
		ProbabilitiesManager probabilitiesManager = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		DistributionsContainer distributionsContainer = probabilitiesManager
				.getDistributionsContainer();
		IndicatorConfiguration indicatorConfiguration = distributionsContainer
				.getIndicatorConfiguration();
		return indicatorConfiguration;
	}

	protected static IndicatorChartView openChart() {
		IndicatorChartView chartView = PLStatsPlugin.getDefault()
				.getIndicatorManager()
				.openIndicatorChart(getIndicatorConfiguration(), true);
		return chartView;
	}

	public ProbabilitiesCalcLogView() {
		ProbabilitiesCalcLoggerManager manager = (ProbabilitiesCalcLoggerManager) getLogManager();
		manager.addView(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				ProbabilitiesCalcLoggerManager manager = (ProbabilitiesCalcLoggerManager) getLogManager();
				manager.removeView(ProbabilitiesCalcLogView.this);
			}
		});
		String title = ((IProbabilitiesCalcLoggerManager) getLogManager())
				.getFilter().toString();
		setPartName(title);

	}

	// @Override
	// public AbstractLoggerViewControl getControl() {
	// if (super.getControl() == null) {
	// setControl(new ProbabilitiesLoggerControl(this));
	// }
	// return super.getControl();
	// }

	@Override
	public AbstractLoggerViewControl getControl() {
		if (super.getControl() == null) {
			setControl(new ProbabilitiesLoggerControl(this));
		}
		return super.getControl();
	}

	@Override
	public void refresh() {
		String title = ((IProbabilitiesCalcLoggerManager) getLogManager())
				.getFilter().toString();
		setPartName("Probabilities Log " + title);
		getViewer().refresh();
	}

}
