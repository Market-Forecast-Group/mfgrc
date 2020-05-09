package com.mfg.common;

import com.mfg.utils.U;

/**
 * An event which is sent when the warm up in the server has finished.
 * 
 * <p>
 * That event of course does not arrive from a real symbol, but only from a
 * virtual symbol event.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DFSWarmUpFinishedEvent extends DFSSymbolEvent {

	public final int layer;

	public DFSWarmUpFinishedEvent(String aSymbol, int aLayer) {
		super(aSymbol);
		layer = aLayer;
	}

	@SuppressWarnings("boxing")
	@Override
	public String toPayload() {
		return U.join(this.symbol, WARMUP_FINISHED_EVENT, layer);
	}

}
