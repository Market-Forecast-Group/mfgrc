package com.mfg.chart.ui.animation;

import org.eclipse.swt.widgets.Display;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.ScrollingMode;

public class ChartAnimator_Standalone {
	int _fps;
	private AnimationThread job;
	Chart _chart;

	class AnimationThread extends Thread {

		long _lastChartToken = -1;
		long _lastModelToken = -1;
		private boolean _canceled;

		public AnimationThread() {
			super("Animation of " + _chart.getChartName());
			_canceled = false;
		}

		@Override
		public void run() {
			while (!_canceled) {
				final long chartToken = _chart.getToken();
				// out.println("job token " + lastChartToken + ", chart token "
				// + chartToken);
				if (_lastChartToken != chartToken) {
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							if (_chart.getScrollingMode() == ScrollingMode.SCROLLING) {
								_chart.scrollToEnd();
							}
							long modelToken = _chart.getModel().getToken();
							if (_lastModelToken != modelToken) {
								// out.println("update!!!");
								_chart.update(_chart.isAutoRangeEnabled());
							} else {
								// out.println("repaint!!!");
								_chart.repaint();
							}
							_lastModelToken = modelToken;
							_lastChartToken = chartToken;
						}
					});

				}

				try {
					Thread.sleep(1000 / _fps);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void cancel() {
			_canceled = true;
		}
	}

	public ChartAnimator_Standalone(Chart chart, int fps) {
		super();
		_chart = chart;
		_fps = fps;
		// out.println("Create chart animator for mode "
		// + chartAdapter.getChart().getModel());
	}

	public ChartAnimator_Standalone(Chart chart) {
		this(chart, 24);
	}

	public void start() {
		job = new AnimationThread();
		job.start();
	}

	public void stop() {
		job.cancel();
		// out.println("stop animator for model "
		// + chartAdapter.getChart().getModel());
	}

	public int getFps() {
		return _fps;
	}

	public void setFps(int fps) {
		this._fps = fps;
	}

}
