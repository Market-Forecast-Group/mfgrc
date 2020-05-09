package com.mfg.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.mfg.utils.U;

/**
 * The base class of all the symbol's related events.
 * 
 * <p>
 * each symbol event of course is related to a symbol, that symbol is the key of
 * the event (in case of a remote event the real key is the push key, but this
 * is not always present...).
 * 
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class DFSSymbolEvent {

	// This is the usual format used by the quotes;
	protected static SimpleDateFormat formatQuote = new SimpleDateFormat(
			U.QUOTE_FORMAT);

	protected static final String WARMUP_FINISHED_EVENT = "[end_warm_up]";

	protected static final String STOPPING_SUBSCRIPTION_EVENT = "[on_stopping_sub]";

	/**
	 * This is the start subscription event.
	 */
	protected static final String START_SUB_EVENT = "[start_sub]";

	protected static final String VOLUME_UPDATE_EVENT = "[vol_update]";

	public static DFSSymbolEvent fromPayload(String payload) {

		String splits[] = U.commaPattern.split(payload);

		if (splits[1].compareTo(VOLUME_UPDATE_EVENT) == 0) {
			int fakeTime = Integer.parseInt(splits[2]);
			int volume = Integer.parseInt(splits[3]);
			return new DFSVolumeUpdateEvent(splits[0], fakeTime, volume);
		} else if (splits[1].compareTo(WARMUP_FINISHED_EVENT) == 0) {
			int layer = Integer.parseInt(splits[2]);
			return new DFSWarmUpFinishedEvent(splits[0], layer);
		} else if (splits[1].compareTo(START_SUB_EVENT) == 0) {
			int aTick = Integer.parseInt(splits[2]);
			int aScale = Integer.parseInt(splits[3]);
			return new DFSSubscriptionStartEvent(splits[0], aTick, aScale);
		} else if (splits[1].compareTo(STOPPING_SUBSCRIPTION_EVENT) == 0) {
			return new DFSStoppingSubscriptionEvent(splits[0]);
		}

		if (splits.length != 9) {
			throw new IllegalArgumentException("very very bad... cannot parse "
					+ payload + " into a quote");
		}

		long dateTime;
		synchronized (formatQuote) {

			try {
				dateTime = formatQuote.parse(splits[3]).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}

		boolean real = Boolean.parseBoolean(splits[4]);

		boolean warmUp = Boolean.parseBoolean(splits[6]);

		boolean finalTick = Boolean.parseBoolean(splits[7]);

		int volume = Integer.parseInt(splits[8]);

		// qt.setVolume(volume);

		QueueTick qt = new QueueTick(dateTime, Integer.parseInt(splits[2]),
				Integer.parseInt(splits[5]), real, volume);

		DFSQuote quote = new DFSQuote(qt, splits[0],
				Integer.parseInt(splits[1]), warmUp, finalTick);

		return quote;
	}

	public final String symbol;

	/**
	 * Every event has an id, that id is strictly monotonically increasing... it
	 * is used to give the possibility to have an unique id to have the push to
	 * push, because now we have also volume quotes which are at the same fake
	 * time, same price, but different volume.
	 * 
	 * <p>
	 * The volume information is not yet massaged (I have to think about it,
	 * because this may mean that there is another message, apart from the
	 * {@link DFSQuote}).
	 * 
	 */
	protected int _id;

	public DFSSymbolEvent(String aSymbol) {
		symbol = aSymbol;
	}

	public void setPushToPushId(int aId) {
		_id = aId;

	}

	public abstract String toPayload();

	@Override
	public final String toString() {
		return toPayload();
	}

}
