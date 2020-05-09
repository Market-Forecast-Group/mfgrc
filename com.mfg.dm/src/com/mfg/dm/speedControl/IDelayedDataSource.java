package com.mfg.dm.speedControl;

public interface IDelayedDataSource {
	public void setDelayControl(IDelayControl control);

	public IDelayControl getDelayControl();

	/**
	 * returns true if this data source is delayable.
	 * 
	 * <p>
	 * Usually only historical data sources (DB or CSV) are delayable.
	 * 
	 * @return true if you can actually delay this data source.
	 */
	// public boolean isDelayable();
}
