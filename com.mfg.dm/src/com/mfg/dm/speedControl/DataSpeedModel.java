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

package com.mfg.dm.speedControl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author arian
 * 
 */
public class DataSpeedModel {
	public static final String PROP_TIME_TO_RUN = "timeToRun";
	public static final String PROP_DELAY = "delay";
	public static final String PROP_SCALE_SELECTION = "scaleSelection";
	public static final String PROP_STATE = "state";
	public static final DataSpeedModel DISABLED = new DataSpeedModel(
			DataSpeedControlState.DISABLED, 0);
	public static final DataSpeedModel INITIAL_MODEL = new DataSpeedModel() {
		@Override
		public void addPropertyChangeListener(PropertyChangeListener l) {
			//
		}

		@Override
		public DataSpeedControlState getState() {
			return DataSpeedControlState.INITIAL;
		}

		@Override
		public void setState(DataSpeedControlState state) {
			//
		}

		@Override
		public void addPropertyChangeListener(String property,
				PropertyChangeListener l) {
			//
		}

		@Override
		public void firePropertyChange(String property) {
			//
		}

		@Override
		public String toString() {
			return "UNBOUNDED MODEL";
		}
	};
	private DataSpeedControlState state;
	private double delay;
	private long timeToRun;

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	/**
	 * 
	 */
	public DataSpeedModel() {
		state = DataSpeedControlState.INITIAL;
		delay = 400;
	}

	private DataSpeedModel(DataSpeedControlState state1, long delay1) {
		this.state = state1;
		this.delay = delay1;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	public DataSpeedControlState getState() {
		return state;
	}

	public void setState(DataSpeedControlState state1) {
		// Lino requested to comment this
		// if (this.state != state1)
		{
			this.state = state1;
			firePropertyChange(PROP_STATE);
		}
	}

	public long getDelayInMillis() {
		return (long) (delay * 1);
	}

	/**
	 * @return the timeToRun
	 */
	public long getTimeToRun() {
		return timeToRun;
	}

	/**
	 * @param timeToRun1
	 *            the timeToRun to set
	 */
	public void setTimeToRun(long timeToRun1) {
		this.timeToRun = timeToRun1;
		firePropertyChange(PROP_TIME_TO_RUN);
	}

	/**
	 * @return the delay
	 */
	public double getDelay() {
		return delay;
	}

	/**
	 * @param delay1
	 *            the delay to set
	 * @param b
	 */
	public void setDelay(double delay1) {
		this.delay = delay1;
		firePropertyChange(PROP_DELAY);
	}
}
