package com.mfg.common;

import java.util.Date;

/**
 * 
 * The class is a bar with a secondary volume, because the first volume is
 * attached to the open, and the second volume to the other price of the bar,
 * which can be the high or the low, depening on its shape.
 */
public class RangeBar extends Bar {

	private int _secondaryVolume;

	public RangeBar() {
		// simple void constructor.
	}

	public RangeBar(long time2, int open2, int high2, int low2, int close2,
			int aVolume, int aSecondaryVolume) {
		super(time2, open2, high2, low2, close2, aVolume);
		this._secondaryVolume = aSecondaryVolume;
	}

	@Override
	public void accumulateVolume(int aVolume) {
		throw new UnsupportedOperationException();
	}

	/**
	 * I want to accumulate the tick which can be the first or the second.
	 */
	@Override
	public void accumulateVolume(Tick aTick) {
		if (aTick.getPrice() == this.open) {
			this.volume += aTick._volume;
		} else {
			this._secondaryVolume += aTick._volume;
		}
	}

	/**
	 * The expand for a range bar is different because the range bar has two
	 * volumes and I have to distribute the two volumes among the four prices.
	 */
	@Override
	public void expand(Tick arr[], long duration) {

		long tempTime = this.time;
		// boolean up = this.close > this.open;

		if (open == close) {
			/*
			 * This is a T or a T^-1 bar. The primary volume must be splitted
			 * between open and close.
			 */
			arr[3].price = close; // filter the last tick, is not used.

			int volumeHalf = volume / 2;

			arr[2].physicalTime = tempTime;
			arr[2].price = close;
			arr[2]._volume = volumeHalf;

			long durationThird = Math.max(duration / 3, 1);

			tempTime -= durationThird;

			arr[1].physicalTime = tempTime;
			arr[1].price = open == low ? high : low;
			arr[1]._volume = _secondaryVolume;

			tempTime -= durationThird;

			arr[0].physicalTime = tempTime;
			arr[0].price = this.open;
			arr[0]._volume = volumeHalf + volume % 2;

		} else {
			/*
			 * This is a up bar or a down bar, so the volumes are mapped
			 * identically to the open and the close (the high and low are not
			 * necessary).
			 */
			/*
			 * In this way the 0p filter will filter the last two ticks!
			 */
			arr[3].price = close;
			arr[2].price = close;

			arr[1].physicalTime = tempTime;
			arr[1].price = close;
			arr[1]._volume = _secondaryVolume;

			long durationHalf = Math.max(duration / 2, 1);
			tempTime -= durationHalf;

			arr[0].physicalTime = tempTime;
			arr[0].price = this.open;
			arr[0]._volume = this.volume;

		}

	}

	//

	/**
	 * Returns the first volume, that is the volume associated with the open
	 * price.
	 * 
	 * @return the volume associated with the open price.
	 */
	public int getFirstVolume() {
		return volume;
	}

	/**
	 * Returns the secondary volume.
	 * 
	 * @return
	 */
	public int getSecondVolume() {
		return _secondaryVolume;
	}

	public void setSecondaryVolume(int volSecond) {
		_secondaryVolume = volSecond;

	}

	@Override
	public String toString() {
		return "[" + new Date(time) + "," + this.open + "," + this.high + ","
				+ this.low + "," + this.close + " vol1 " + this.volume
				+ " vol2 " + this._secondaryVolume + " vtot "
				+ (volume + _secondaryVolume) + "]";
	}

}
