package com.mfg.dm.speedControl;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.progress.ProgressView;

import com.mfg.utils.PartUtils;

@SuppressWarnings("restriction")
public class ThreadDelayControl implements IDelayControl {

	private final DataSpeedModel _model;
	private boolean _firstTime;
	private DataSpeedControlState _lastState;
	private boolean _runningToTime;

	public ThreadDelayControl(DataSpeedModel model1) {
		_model = model1;
		_firstTime = true;
		_lastState = model1.getState();
		_runningToTime = false;
	}

	@Override
	public void delay(IProgressMonitor monitorUnused/*
													 * to be removed, it is not
													 * used.
													 */, long currentTime) {
		try {
			boolean pausedByRunToTime = false;
			DataSpeedControlState state = _model.getState();
			if (_model.getTimeToRun() > currentTime && currentTime > 0) {
				_runningToTime = true;
				synchronized (_model) {
					_model.notifyAll();
				}
				return;
			}
			if (_runningToTime) {
				_runningToTime = false;
				pausedByRunToTime = true;
				_model.setState(DataSpeedControlState.PAUSED);
			}

			if (state == DataSpeedControlState.FAST_FORWARDING) {
				if (_lastState != DataSpeedControlState.FAST_FORWARDING) {
					synchronized (_model) {
						_model.notifyAll();
					}
				}
				return; // no delay at all!
			}

			_lastState = state;

			if (state == DataSpeedControlState.PAUSED || pausedByRunToTime) {
				synchronized (_model) {
					_model.wait();
				}
			}

			state = _model.getState();

			if (state != DataSpeedControlState.STOPPED) {
				long delay = _model.getDelayInMillis();
				synchronized (_model) {
					_model.wait(delay);
				}

				// TODO: To fix a ProgressView bug. It does not show the
				// progress if
				// the job is delayed using Thread.sleep(delay) or
				// Object.wait(delay).
				if (_firstTime) {
					_firstTime = false;
					List<IViewPart> views = PartUtils
							.getOpenViews("org.eclipse.ui.views.ProgressView");
					for (IViewPart view : views) {
						final ProgressView pView = (ProgressView) view;
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								if (!pView.getViewer().getControl()
										.isDisposed()) {
									pView.getViewer().refresh();
								}
							}
						});

					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.dm.speedControl.IDelayControl#getModel()
	 */
	@Override
	public DataSpeedModel getModel() {
		return _model;
	}

	@Override
	public void stop() {
		_model.setState(DataSpeedControlState.STOPPED);
	}

}
