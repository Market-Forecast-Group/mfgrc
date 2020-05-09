package com.mfg.utils.jobs;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Simple adapter for the IProgressMonitor interface.
 * 
 * @author Sergio
 * 
 */
public class ProgressMonitorAdapter implements IProgressMonitor {

	private AtomicBoolean _cancelled = new AtomicBoolean();

	@Override
	public void beginTask(String name, int totalWork) {
		// nothing
	}

	@Override
	public void done() {
		// nothing
	}

	@Override
	public void internalWorked(double work) {
		// nothing
	}

	@Override
	public boolean isCanceled() {
		return _cancelled.get();
	}

	@Override
	public void setCanceled(boolean value) {
		_cancelled.set(value);
	}

	@Override
	public void setTaskName(String name) {
		// nothing
	}

	@Override
	public void subTask(String name) {
		// nothing
	}

	@Override
	public void worked(int work) {
		// nothing
	}

}
