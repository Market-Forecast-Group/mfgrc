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
package com.mfg.symbols.inputs.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeAdapter;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;

/**
 * @author arian
 * 
 */
@SuppressWarnings("unchecked")
public abstract class CreateCommandsRunnable<T extends SymbolConfiguration<?, ?>>
		implements Runnable {
	private final AbstractSymbolEditor editor;
	private int page;
	final Composite parent;

	@SuppressWarnings("unused")
	// Used on inner classes.
	public CreateCommandsRunnable(AbstractSymbolEditor aEditor,
			InputConfiguration inputConfiguration,
			TradingConfiguration tradingConfiguration, Composite aParent) {
		this.editor = aEditor;
		this.parent = aParent;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	@Override
	public void run() {
		page = editor.getActivePage();

		createCommandsWidgets(parent);

		final SymbolJobChangeAdapter jobListener = new SymbolJobChangeAdapter() {
			@Override
			public void aboutToRun(IJobChangeEvent event) {
				SymbolJob<T> job = getJob(event);
				if (job != null) {
					connect(job);
				}
			}

			@Override
			public void done(IJobChangeEvent event) {
				SymbolJob<T> job = getJob(event);
				if (job != null) {
					disconnect(job);
				}
			}

			private SymbolJob<T> getJob(IJobChangeEvent event) {
				if (isJobClass(event.getJob())) {
					return (SymbolJob<T>) event.getJob();
				}
				return null;
			}
		};
		final IJobManager jobManager = Job.getJobManager();
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				jobManager.removeJobChangeListener(jobListener);
			}
		});

		jobManager.addJobChangeListener(jobListener);
		Job[] jobs = jobManager.find(getSymbolConfiguration());
		Assert.isTrue(jobs.length <= 1);

		if (jobs.length == 1) {
			SymbolJob<T> job = (SymbolJob<T>) jobs[0];
			if (job.getSymbolConfiguration() == getSymbolConfiguration()) {
				connect(job);
			}
		}
	}

	void disconnect(final SymbolJob<?> job) {
		if (job.getSymbolConfiguration() == getSymbolConfiguration()) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!parent.isDisposed()) {
						updateCommandsWidgetsToInitialState();
					}
				}
			});
		}
	}

	protected abstract void updateCommandsWidgetsToInitialState();

	void connect(final SymbolJob<?> job) {
		if (job.getSymbolConfiguration() == getSymbolConfiguration()) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!parent.isDisposed()) {
						boolean runningJobInThisTab = commandsSectionMatchsJob(job);
						updateCommandsWidgetsForJob(job, runningJobInThisTab);
					}
				}
			});
		}
	}

	/**
	 * @param startEnabled
	 */
	protected abstract void updateCommandsWidgetsForJob(SymbolJob<?> job,
			boolean runningJobInThisTab);

	public void startJob(boolean startTrading) {
		Job job = createJob(startTrading);
		job.schedule();
	}

	public void stopJob() {
		SymbolJob.stopConfigurationDataRequest(getSymbolConfiguration());
	}

	/**
	 * @param startTrading
	 * @return
	 */
	protected abstract Job createJob(boolean startTrading);

	private T getSymbolConfiguration() {
		return (T) editor.getSymbolConfiguration();
	}

	protected boolean commandsSectionMatchsJob(Job job) {
		if (isJobClass(job)) {
			Object element = editor.getEditorInput().getStorageObject();
			return job.belongsTo(element);
		}
		return false;
	}

	protected abstract boolean isJobClass(Job job);

	protected abstract void createCommandsWidgets(Composite aParent);
}
