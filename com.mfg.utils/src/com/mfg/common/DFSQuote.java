package com.mfg.common;

import java.util.Date;

import com.mfg.utils.U;

/**
 * A quote in DFS can have different meanings.
 * 
 * <p>
 * In its simplest meaning the quote is simply a real time quote, but it may be
 * also the result of an expansion of a Virtual Symbol, that is an historical
 * request.
 * 
 * <p>
 * The stream of prices which comes from a real symbol is always real time, it
 * is a real subscription to a real symbol, instead the stream of prices which
 * comes from a VirtualSymbol can be historical or not. And also they can come
 * in multiple layers.
 * 
 * <p>
 * A quote has now associated also a volume. The volume is different in case of
 * historical or real time, but usually it is a Cumulative volume for all the
 * transactions at a given price. This is not true if the quote is not final, in
 * that case the volume is only a tentative volume.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DFSQuote extends DFSSymbolEvent {

	public final QueueTick tick;

	/**
	 * The layer to which the tick belongs. Real time ticks of real symbols have
	 * a layer of -1. Some listeners will ignore ticks unless they are of level
	 * zero, which is the default real time layer.
	 */
	public final int layer;

	/**
	 * True if this tick is still in warm up phase, some listeners will ignore
	 * the warm up ticks.
	 */
	public final boolean warmUpTick;

	/**
	 * this flag is true if the tick is final. Some listeners ignore not final
	 * ticks.
	 */
	public final boolean finalTick;

	// public final int volume;

	/**
	 * Constructor used for a virtual symbol's quote.
	 * 
	 * @param aTick
	 * @param aSymbol
	 * @param aLayer
	 * @param warmUp
	 */
	public DFSQuote(QueueTick aTick, String aSymbol, int aLayer,
			boolean warmUp, boolean isFinalTick) {
		super(aSymbol);
		tick = aTick;
		layer = aLayer;
		warmUpTick = warmUp;
		finalTick = isFinalTick;
		// volume = aTick._volume;
	}

	/**
	 * Constructor used for a real symbol.
	 * 
	 * <p>
	 * By convention the tick will be at layer zero and it will be, of course,
	 * real.
	 * 
	 * <p>
	 * The quote from the real symbol has also the volume, which is a
	 * <i>tentative</i> volume, as the cumulative volume will be sent only after
	 * the quotes changes.
	 * 
	 * <p>
	 * The translation between the <i>tentative</i> volume and the real volume
	 * is done in the VirtualSymbol class.
	 * 
	 * @param aSymbol
	 * @param datetime
	 * @param price
	 * @param aVolume
	 *            the volume for this quote
	 */
	public DFSQuote(String aSymbol, long datetime, int price, int aVolume) {
		super(aSymbol);
		layer = 0;
		tick = new QueueTick(datetime, -1, price, true, aVolume);
		warmUpTick = false;
		finalTick = true;
		// volume = aVolume;
	}

	@Override
	@SuppressWarnings("boxing")
	public String toPayload() {
		String dateTime;
		synchronized (formatQuote) {
			dateTime = formatQuote.format(new Date(tick.getPhysicalTime()));
		}

		/*
		 * If you modify the payload you have to modify the DfsSymbolEvent
		 * fromPayload method!
		 */
		return U.join(symbol, layer, tick.getFakeTime(), dateTime,
				tick.fIsReal, tick.price, warmUpTick, finalTick, tick._volume);

	}

	// @Override
	// public String toString() {
	// return toPayload();
	// }

}
