package com.mfg.dm;

import com.mfg.common.BAR_TYPE;

/**
 * This is the class which holds the data of a request. The request is then
 * passed to a IDataProvider to have some data.
 * 
 * 
 * @author Pasqualino
 * 
 */
public class DataRequest {

	/**
	 * The start date of the request is valid only if the request is not in real
	 * time. This time is in UTC coordinates.
	 * 
	 * <p>
	 * If the request is in real time this value is not considered.
	 */
	public final long startDate;

	public final BAR_TYPE barType;

	public final int widthOfBar;

	public final int numberOfBarsRequested;

	public final UnitsType fUnitsType;

	private final int _gap1;
	private final int _gap2;

	/**
	 * builds a data request. The data request has 3 flags which are used to
	 * identify how the data request behaves in respect to the real time
	 * 
	 * <p>
	 * The datarequest is always linked to a {@link RawDataSource} and a
	 * RawDataSource is linked to a bar request.
	 * 
	 * <p>
	 * Nevertheless a {@link RawDataSource} is also linked to a subscription,
	 * because some data sources are also used in real time.
	 * 
	 * <p>
	 * Push incomplete means that the data provider is able to push also
	 * incomplete bars, bars which have only the close which is changing.
	 * 
	 * @param _contract
	 * @param _barType
	 * @param _widthOfBar
	 * @param _numberOfBarsRequested
	 * @param _continueInRealTime
	 *            if true means that the data source will continue in real time.
	 * @param pushIncomplete1
	 * @param aType
	 * @param initialMultiplierl
	 * @param followingMultiplier
	 * @param mustSubscribeFlag
	 */
	public DataRequest(BAR_TYPE _barType, int _widthOfBar, long aStartDate,
			int _numberOfBarsRequested, UnitsType aType,
			int initialMultiplierl, int followingMultiplier) {
		// this._symbol = aSymbol;
		this.barType = _barType;
		this.widthOfBar = _widthOfBar;
		this.numberOfBarsRequested = _numberOfBarsRequested;
		this.fUnitsType = aType;

		startDate = aStartDate;
		_gap1 = initialMultiplierl;
		_gap2 = followingMultiplier;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// DataRequest other = (DataRequest) obj;
	// if (barType != other.barType)
	// return false;
	// if (numberOfBarsRequested != other.numberOfBarsRequested)
	// return false;
	// if (widthOfBar != other.widthOfBar)
	// return false;
	// return true;
	// }

	public int getGap1() {
		return _gap1;
	}

	public int getGap2() {
		return _gap2;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((barType == null) ? 0 : barType.hashCode());
	// result = prime * result + numberOfBarsRequested;
	// result = prime * result + widthOfBar;
	// return result;
	// }

	@Override
	public String toString() {
		return "[" + barType + "," + numberOfBarsRequested + "]";
	}

}
