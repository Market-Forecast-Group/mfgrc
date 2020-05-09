package com.mfg.strategy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;

public class StopSettings implements Cloneable {
	private transient final PropertyChangeSupport changeSupport;
	private AutoStop autoStop;
	private int numberOfTicks;
	private EXECUTION_TYPE stopType;
	private double triggerPrice;
	private double limitPrice;

	public StopSettings() {
		changeSupport = new PropertyChangeSupport(this);
		autoStop = AutoStop.AUTO;
		numberOfTicks = 5;
		stopType = EXECUTION_TYPE.STOP;
		triggerPrice = 1;
		limitPrice = 1;
	}

	public int getNumberOfTicks() {
		return numberOfTicks;
	}

	public void setNumberOfTicks(int aNumberOfTicks) {
		this.numberOfTicks = aNumberOfTicks;
		changeSupport.firePropertyChange("numberOfTicks", true, false);
	}

	public EXECUTION_TYPE getStopType() {
		return stopType;
	}

	public void setStopType(EXECUTION_TYPE aStopType) {
		this.stopType = aStopType;
		changeSupport.firePropertyChange("stopType", true, false);
	}

	public double getTriggerPrice() {
		return triggerPrice;
	}

	public void setTriggerPrice(double aTriggerPrice) {
		this.triggerPrice = aTriggerPrice;
		changeSupport.firePropertyChange("triggerPrice", true, false);
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double aLimitPrice) {
		this.limitPrice = aLimitPrice;
		changeSupport.firePropertyChange("limitPrice", true, false);
	}

	public void setAutoStop(AutoStop aAutoStop) {
		this.autoStop = aAutoStop;
		changeSupport.firePropertyChange("autoStop", true, false);
	}

	public AutoStop getAutoStop() {
		return autoStop;
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	protected StopSettings clone() {
		try {
			return (StopSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
