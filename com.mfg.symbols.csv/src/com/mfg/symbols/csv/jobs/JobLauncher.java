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
package com.mfg.symbols.csv.jobs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

/**
 * @author arian
 * 
 */
public abstract class JobLauncher {
	private Job job;
	final Action startAction;
	final Action stopAction;
	private final JobChangeAdapter jobListener;

	protected abstract Job createJob();

	public JobLauncher(String jobName) {
		startAction = new Action("Start " + jobName) {
			@Override
			public void run() {
				startJob();
			}
		};
		stopAction = new Action("Stop " + jobName) {
			@Override
			public void run() {
				stopJob();
			}
		};
		jobListener = new JobChangeAdapter() {
			@Override
			public void aboutToRun(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						stopAction.setEnabled(true);
					}
				});
			}

			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						startAction.setEnabled(true);
					}
				});

			}
		};
		startAction.setEnabled(true);
		stopAction.setEnabled(false);
	}

	void startJob() {
		startAction.setEnabled(false);
		if (job != null) {
			job.removeJobChangeListener(jobListener);
		}
		job = createJob();
		job.addJobChangeListener(jobListener);
		job.schedule();
	}

	void stopJob() {
		Assert.isNotNull(job);
		stopAction.setEnabled(false);
		job.cancel();
	}

	/**
	 * @return the job
	 */
	public final Job getJob() {
		return job;
	}

	/**
	 * @return the startAction
	 */
	public final Action getStartAction() {
		return startAction;
	}

	/**
	 * @return the stopAction
	 */
	public final Action getStopAction() {
		return stopAction;
	}
}
