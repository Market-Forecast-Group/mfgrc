package com.mfg.dfs.misc;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.utils.U;

public class ServerDelayControl implements IDelayControl {

	private final DataSpeedModel _model;
	private boolean _runningToTime;
	private DataSpeedControlState _lastState;

	public ServerDelayControl() {
		_model = new DataSpeedModel();
		/*
		 * At first the playing is enabled. The average speed is the default
		 * speed of the DataSpeedModel, which, at the time of writing, is 400
		 * milliseconds.
		 */
		_model.setState(DataSpeedControlState.PLAYING);
	}

	@Override
	public void delay(IProgressMonitor monitor, long currentTime) {
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

			}
		} catch (InterruptedException e) {
			U.debug_var(393922, "Thread ", Thread.currentThread().getName(),
					" interrupted in delay in class ServerDelayControl");
			// e.printStackTrace();
		}

	}

	@Override
	public void stop() {
		_model.setState(DataSpeedControlState.STOPPED);
	}

	@Override
	public DataSpeedModel getModel() {
		return _model;
	}

}
