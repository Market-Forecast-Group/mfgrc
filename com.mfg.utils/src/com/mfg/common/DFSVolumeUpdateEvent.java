package com.mfg.common;

import com.mfg.utils.U;

public class DFSVolumeUpdateEvent extends DFSSymbolEvent {

	public final int _fakeTime;
	public final int _volume;

	public DFSVolumeUpdateEvent(String aSymbol, int aFakeTime, int aVolume) {
		super(aSymbol);
		_fakeTime = aFakeTime;
		_volume = aVolume;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toPayload() {
		return U.join(this.symbol, VOLUME_UPDATE_EVENT, _fakeTime, _volume);
	}

}
