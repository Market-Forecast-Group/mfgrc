package com.mfg.utils.concurrent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public class SimpleAnimator {
	final Runnable _animate;
	private Job _job;

	public SimpleAnimator(Runnable animate) {
		super();
		_animate = animate;
		_job = new Job("Account Manager Animator") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				while (!monitor.isCanceled()) {
					animate();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// nothing
					}
				}
				return Status.OK_STATUS;
			}

			@Override
			public boolean belongsTo(Object family) {
				return "com.mfg.jobs.refreshingGuiJob".equals(family);
			}
		};
		_job.setSystem(true);
	}

	public void start() {
		_job.schedule();
	}

	public void stop() {
		_job.cancel();
		try {
			_job.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void animate() {
		Display.getDefault().syncExec(_animate);
	}
}
