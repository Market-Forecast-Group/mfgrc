package com.mfg.common;

import java.io.Serializable;
import java.util.Date;

import com.mfg.utils.PriceUtils;

/**
 * This is a simple tick that comes from the data feed which has not the fields
 * parsed (the price remains as a string).
 * 
 * <p>
 * The tick has also the volume information
 * 
 * 
 * @author Sergio
 * 
 */
public class UnparsedTick implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8671226964582041758L;

	/**
	 * I have the real physical time, with milliseconds precision.
	 */
	private long physicalTime;

	/**
	 * This is the last trade price.
	 */
	private String price;

	private final int volume;

	public UnparsedTick(long time, String priceS, String aVolumeS) {
		physicalTime = time;
		price = priceS;
		volume = Integer.parseInt(aVolumeS);
	}

	public UnparsedTick(Tick simTick, int scale, int aVolume) {
		physicalTime = simTick.physicalTime;
		price = PriceUtils.longToString(simTick.price, scale);
		volume = aVolume;
	}

	public String getPrice() {
		return price;
	}

	public long getTime() {
		return physicalTime;
	}

	@Override
	public String toString() {
		return "[" + new Date(physicalTime) + "," + price + " v " + volume
				+ "]";
	}

	public int getVolume() {
		return volume;
	}
}
