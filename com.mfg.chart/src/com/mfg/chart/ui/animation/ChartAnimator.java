package com.mfg.chart.ui.animation;

import static java.lang.System.out;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.IChartContentAdapter;

public class ChartAnimator {
	int _fps;
	private AnimationJob job;
	final IChartContentAdapter _chartAdapter;

	class AnimationJob extends Job {

		long _lastChartToken = -1;
		long _lastModelToken = -1;

		public AnimationJob() {
			super("Animation of " + _chartAdapter.getChartName());
			setSystem(true);
		}

		@Override
		public boolean belongsTo(Object family) {
			return "com.mfg.jobs.refreshingGuiJob".equals(family);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Runnable paint = new Runnable() {

				@Override
				public void run() {
					_chartAdapter.scrollChart();
					final Chart chart = _chartAdapter.getChart();
					long modelToken;
					IChartModel model = chart.getModel();
					if (chart.getType() == ChartType.EQUITY) {
						modelToken = model.getTradingModel()
								.getEquityUpperTime();
					} else {
						modelToken = model.getToken();
					}

					if (_lastModelToken != modelToken) {
						// out.println("update!!!");
						try {
							chart.update(chart.isAutoRangeEnabled(), false,
									false);
						} catch (Exception e) {
							out.println(e.getMessage());
						}
					}
					// out.println("repaint!!!");
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							chart.repaint();
						}
					});

					_lastModelToken = modelToken;
					_lastChartToken = chart.getToken();
				}
			};

			while (!monitor.isCanceled()) {
				final long chartToken = _chartAdapter.getChart().getToken();
				// out.println("job token " + lastChartToken + ", chart token "
				// + chartToken);
				if (_lastChartToken != chartToken) {
//					IChartModel model = _chartAdapter.getChart().getModel();
//					if (model instanceof ChartModel_MDB) {
//						DBSynchronizer synchro = ((ChartModel_MDB) model)
//								.getPriceSession().getSynchronizer();
//						synchro.operation(paint);
//					} else {
//						paint.run();
//					}
					paint.run();
				}

				try {
					Thread.sleep(1000 / _fps);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return Status.OK_STATUS;
		}
	}

	public ChartAnimator(IChartContentAdapter chartAdapter, int fps) {
		super();
		this._chartAdapter = chartAdapter;
		this._fps = fps;
		// out.println("Create chart animator for mode "
		// + chartAdapter.getChart().getModel());
	}

	public ChartAnimator(IChartContentAdapter chartAdapter) {
		this(chartAdapter, 24);
	}

	public void start() {
		job = new AnimationJob();
		job.schedule();
	}

	public void stop() {
		job.cancel();
//		try {
//			job.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	public int getFps() {
		return _fps;
	}

	public void setFps(int fps) {
		this._fps = fps;
	}

}
