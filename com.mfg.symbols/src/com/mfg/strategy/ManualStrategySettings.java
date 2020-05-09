package com.mfg.strategy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;

public class ManualStrategySettings implements Cloneable, IStrategySettings {

	private int basicQuantity;
	private int basicMaxQuantity;
	private boolean basicUseManualOrderConfirmation;
	private int entryExitLimitPrice;
	private EntryExitOrderType entryExitOrderType;
	private ChildToExit entryExitChildToExit;
	private boolean selectedEntryExitTHScale;
	private int entryExitTHScale;
	private StopSettings stopLossSettings;
	private StopSettings takeProfitSettings;

	private int longTrailingLevel;
	private int shortTrailingLevel;

	public ManualStrategySettings() {
		basicQuantity = 1;
		basicMaxQuantity = 2;
		entryExitLimitPrice = 0;
		selectedEntryExitTHScale = false;
		entryExitTHScale = 4;
		entryExitOrderType = EntryExitOrderType.MARKET;
		entryExitChildToExit = ChildToExit.STOP_LOSS;

		stopLossSettings = new StopSettings();
		takeProfitSettings = new StopSettings();
		takeProfitSettings.setStopType(EXECUTION_TYPE.LIMIT);
	}

	public boolean isBasicUseManualOrderConfirmation() {
		return basicUseManualOrderConfirmation;
	}

	public void setBasicUseManualOrderConfirmation(
			boolean aBasicUseManualOrderConfirmation) {
		this.basicUseManualOrderConfirmation = aBasicUseManualOrderConfirmation;
		firePropertyChange("basicUseManualOrderConfirmation");
	}

	public EntryExitOrderType getEntryExitOrderType() {
		return entryExitOrderType;
	}

	public void setEntryExitOrderType(EntryExitOrderType aEntryExitOrderType) {
		this.entryExitOrderType = aEntryExitOrderType;
		firePropertyChange("entryExitOrderType");
	}

	public ChildToExit getEntryExitChildToExit() {
		return entryExitChildToExit;
	}

	public void setEntryExitChildToExit(ChildToExit aEntryExitChildToExit) {
		this.entryExitChildToExit = aEntryExitChildToExit;
		firePropertyChange("entryExitChildToExit");
	}

	public StopSettings getStopLossSettings() {
		return stopLossSettings;
	}

	public void setStopLossSettings(StopSettings aStopLossSettings) {
		this.stopLossSettings = aStopLossSettings;
		firePropertyChange("stopLossSettings");
	}

	public StopSettings getTakeProfitSettings() {
		return takeProfitSettings;
	}

	public void setTakeProfitSettings(StopSettings aTakeProfitSettings) {
		this.takeProfitSettings = aTakeProfitSettings;
		firePropertyChange("takeProfitSettings");
	}

	public int getBasicQuantity() {
		return basicQuantity;
	}

	public void setBasicQuantity(int basic_quantity) {
		this.basicQuantity = basic_quantity;
		firePropertyChange("basicQuantity");
	}

	public int getBasicMaxQuantity() {
		return basicMaxQuantity;
	}

	public void setBasicMaxQuantity(int basic_maxQuantity) {
		this.basicMaxQuantity = basic_maxQuantity;
		firePropertyChange("basicMaxQuantity");
	}

	public int getEntryExitLimitPrice() {
		return entryExitLimitPrice;
	}

	public void setEntryExitLimitPrice(int aEntryExitLimitPrice) {
		this.entryExitLimitPrice = aEntryExitLimitPrice;
		firePropertyChange("entryExitLimitPrice");
	}

	public boolean isSelectedEntryExitTHScale() {
		return selectedEntryExitTHScale;
	}

	public void setSelectedEntryExitTHScale(boolean isSelectedEntryExitTHScale) {
		this.selectedEntryExitTHScale = isSelectedEntryExitTHScale;
		firePropertyChange("selectedEntryExitTHScale");
	}

	public int getEntryExitTHScale() {
		return entryExitTHScale;
	}

	public void setEntryExitTHScale(int aEntryExitTHScale) {
		this.entryExitTHScale = aEntryExitTHScale;
		firePropertyChange("entryExitTHScale");
	}

	@Override
	public ManualStrategySettings clone() {
		try {
			ManualStrategySettings s = (ManualStrategySettings) super.clone();
			s.takeProfitSettings = takeProfitSettings.clone();
			s.stopLossSettings = stopLossSettings.clone();
			return s;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int getLongTrailingLevel() {
		return longTrailingLevel;
	}

	public void setLongTrailingLevel(int trailingLevel) {
		this.longTrailingLevel = trailingLevel;
	}

	public int getShortTrailingLevel() {
		return shortTrailingLevel;
	}

	public void setShortTrailingLevel(int aShortTrailingLevel) {
		this.shortTrailingLevel = aShortTrailingLevel;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	@Override
	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}
}
