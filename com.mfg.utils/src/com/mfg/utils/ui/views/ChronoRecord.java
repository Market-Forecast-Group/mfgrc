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
package com.mfg.utils.ui.views;

import org.eclipse.core.runtime.jobs.Job;

import com.mfg.utils.MemUtils;

class ChronoRecord {
	private String task;
	private long initTime;
	private long finalTime;
	private long finalMem;
	private long initMem;
	private boolean running;
	private Job job;

	public ChronoRecord() {
		running = false;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job aJob) {
		this.job = aJob;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean aRunning) {
		this.running = aRunning;
	}

	public long getConsumedTime() {
		return getFinalTime() - getInitTime();
	}

	public long getConsumedMem() {
		return getFinalMem() - getInitMem();
	}

	public String getTask() {
		return task;
	}

	public void setTask(String aTask) {
		this.task = aTask;
	}

	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long aInitTime) {
		this.initTime = aInitTime;
	}

	public long getFinalTime() {
		return finalTime;
	}

	public void setFinalTime(long aFinalTime) {
		this.finalTime = aFinalTime;
	}

	public long getFinalMem() {
		return finalMem;
	}

	public void setFinalMem(long aFinalMem) {
		this.finalMem = aFinalMem;
	}

	public long getInitMem() {
		return initMem;
	}

	public void setInitMem(long aInitMem) {
		this.initMem = aInitMem;
	}

	public void update() {
		setFinalTime(System.currentTimeMillis());
		setFinalMem(MemUtils.getUsedMemory());
	}

	public void start() {
		setRunning(true);
		setInitTime(System.currentTimeMillis());
		setInitMem(MemUtils.getUsedMemory());
	}

}