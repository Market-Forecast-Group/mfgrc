package com.mfg.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A Dfs symbol is only used to group some properties of a symbol in a handy
 * common place.
 * 
 * <p>
 * The symbol is composed of a prefix, a tick and a scale.
 * 
 * <p>
 * The values are immutable, and usually they are given from the outside (GUI,
 * text file, xml, database, whatsoever). *
 * 
 * 
 * @author Sergio
 * 
 */
public class DfsSymbol implements Serializable {
	public static final String TYPE_FUTURES = "FUT";
	public static final String TYPE_FOREX = "FRX";
	public static final String TYPE_INDEX = "IND";
	public static final String TYPE_OPTION = "OPT";
	public static final String TYPE_STOCK = "STK";
	public static final String[] TYPES = { TYPE_FUTURES, TYPE_FOREX,
			TYPE_INDEX, TYPE_OPTION, TYPE_STOCK };

	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_EUR = "EUR";

	private static final String DEFAULT_TIME_ZONE = "America/New_York";

	public static final Map<String, String> TYPES__DEF_NAME_MAP = new HashMap<>();

	static {
		TYPES__DEF_NAME_MAP.put(TYPE_FOREX, "Forex");
		TYPES__DEF_NAME_MAP.put(TYPE_FUTURES, "Futures");
		TYPES__DEF_NAME_MAP.put(TYPE_INDEX, "Index");
		TYPES__DEF_NAME_MAP.put(TYPE_OPTION, "Option");
		TYPES__DEF_NAME_MAP.put(TYPE_STOCK, "Stock");
	}

	@Override
	public String toString() {
		return prefix;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5823644081805047314L;

	/**
	 * This is the prefix of the current financial instrument, the prefix is
	 * used to fill the table with all the maturities.
	 * 
	 * <p>
	 * The prefix usually is a two letter code, in iqFeed the prefix is also
	 * prepended with a "@", meaning that this is an electronic symbol.
	 */
	public final String prefix;

	public final int tick;

	public final int scale;

	public final String completeName;

	/**
	 * Usually a symbol is in the default time zone which is "America/New York",
	 * but this because our feed is located there. In a case of a simulated data
	 * feed this time zone is GMT. I don't know what happens in case of a real
	 * data feed in Europe. This has to be checked in real life.
	 */
	public String timeZone = DEFAULT_TIME_ZONE;

	/**
	 * The currency of the symbol.
	 * 
	 * @author arian
	 */
	public String currency = CURRENCY_EUR;

	/**
	 * The symbol type, one of {@link #TYPES}.
	 * 
	 * @author arian
	 */
	public String type = TYPE_FUTURES;

	/**
	 * Tick value. It is an integer, to get the real tick value we should use
	 * the {@link #scale}.
	 * 
	 * @author arian
	 */
	public int tickValue = 12;

	/**
	 * @deprecated This method is not used. Arian.
	 * @return
	 */
	@Deprecated
	protected Object readResolve() {
		if (timeZone == null || timeZone.length() == 0) {
			timeZone = DEFAULT_TIME_ZONE;
		}
		if (tickValue == 0) {
			tickValue = 1250;
		}
		return this;
	}

	public DfsSymbol(String aPrefix, String aCompleteName, int aTick,
			int aScale, int aTickValue, String aTimeZone, String aCurrency,
			String aType) {
		prefix = aPrefix;
		tick = aTick;
		scale = aScale;
		completeName = aCompleteName;
		timeZone = aTimeZone;
		tickValue = aTickValue;
		currency = aCurrency;
		type = aType;
	}

	public DfsSymbol(String aPrefix, String aCompleteName, int aTick,
			int aScale, int aTickValue) {
		this(aPrefix, aCompleteName, aTick, aScale, aTickValue,
				DEFAULT_TIME_ZONE, CURRENCY_USD, TYPE_FUTURES);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((completeName == null) ? 0 : completeName.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + scale;
		result = prime * result + tick;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DfsSymbol other = (DfsSymbol) obj;
		if (completeName == null) {
			if (other.completeName != null)
				return false;
		} else if (!completeName.equals(other.completeName))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (scale != other.scale)
			return false;
		if (tick != other.tick)
			return false;
		return true;
	}

}
