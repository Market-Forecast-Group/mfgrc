package com.mfg.dm.symbols;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

import com.mfg.common.DFSException;
import com.mfg.common.IContract;
import com.mfg.utils.FinancialMath;
import com.mfg.utils.PriceUtils;

/**
 * Root class for all the symbols.
 * 
 * @author arian
 * 
 */
public abstract class SymbolData implements IContract {

	@Override
	public int parsePrice(String price) {
		try {
			return FinancialMath.stringPriceToInt(price, getContractScale());
		} catch (DFSException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public String stringifyPrice(int price) {
		return PriceUtils.longToString(price, getContractScale());
	}

	@Override
	public int getContractTick() {
		if (this.fComputedAutomatically) {
			return this.fAutoTickSize;
		}
		return fManualTickSize;
	}

	@Override
	public int getContractScale() {
		if (this.fComputedAutomatically) {
			return this.fAutoScale;
		}
		return fManualScale;
	}

	private static final String PROP_SCALE = "manualScale";
	private static final String PROP_TICK_VALUE = "tickValue";
	public static final String PROP_TICK_SIZE = "manualTick";
	private static final String PROP_LOCAL_SYMBOL = "localSymbol";
	private static final String PROP_SYMBOL = "symbol";
	public static final String PROP_DATA_TYPE = "dataType";
	public static final String PROP_NAME = "name";
	private static final String PROP_CURRENCY = "currency";

	private UUID uuid;
	private String name;
	private String symbol;
	private String localSymbol;

	private int fManualTickSize;
	private int fManualScale;
	private int fAutoTickSize;
	private int fAutoScale;

	private int tickValue;
	private String currency;
	private String type;
	private String expiry;
	private double strike;
	private String exchange;
	private int id;

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	/**
	 * This field is true when the contract's tick and scale are computed
	 * automatically.
	 */
	private boolean fComputedAutomatically = false;

	public SymbolData(String aName, String aSymbol, String aLocalSymbol,
			int tickSize, int aTickValue, int scale, String aCurrency) {
		super();
		this.name = aName;
		this.symbol = aSymbol;
		this.localSymbol = aLocalSymbol;
		this.fManualTickSize = tickSize;
		this.tickValue = aTickValue;
		this.fManualScale = scale;
		this.currency = aCurrency;
		expiry = "20110916";
		strike = 0;
		exchange = "GLOBEX";
		type = "FUT";
		id = 76308824;
		uuid = UUID.randomUUID();
	}

	public SymbolData() {
		uuid = UUID.randomUUID();
	}

	// @Override
	// public String toJSONString() {
	// throw new UnsupportedOperationException("Not implemented method");
	// }

	@Override
	public int getManualTick() {
		return this.fManualTickSize;
	}

	public void setComputedTick(int computed_tick) {
		this.fAutoTickSize = computed_tick;
		this.fComputedAutomatically = true;
	}

	public void setComputedScale(int computed_scale) {
		this.fAutoScale = computed_scale;
		this.fComputedAutomatically = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#setComputedValues(int, int)
	 */
	@Override
	public void setComputedValues(int computedTick, int computedScale) {
		setComputedTick(computedTick);
		setComputedScale(computedScale);
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type1) {
		this.type = type1;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param uuid1
	 *            the uuid to set
	 */
	public void setUUID(UUID uuid1) {
		this.uuid = uuid1;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	public void setCurrency(String currency1) {
		this.currency = currency1;
		firePropertyChange(PROP_CURRENCY);
	}

	@Override
	public String getCurrency() {
		return currency;
	}

	@XmlID
	public String getName() {
		return name;
	}

	public void setName(String name1) {
		this.name = name1;
		firePropertyChange(PROP_NAME);
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol1) {
		this.symbol = symbol1;
		firePropertyChange(PROP_SYMBOL);
	}

	@Override
	public String getLocalSymbol() {
		return localSymbol;
	}

	public void setLocalSymbol(String localSymbol1) {
		this.localSymbol = localSymbol1;
		firePropertyChange(PROP_LOCAL_SYMBOL);
	}

	public int getTickSize() {
		return fManualTickSize;
	}

	public void setTickSize(int tickSize) {
		this.fManualTickSize = tickSize;
		firePropertyChange(PROP_TICK_SIZE);
	}

	@Override
	public int getTickValue() {
		return tickValue;
	}

	public void setTickValue(int tickValue1) {
		this.tickValue = tickValue1;
		firePropertyChange(PROP_TICK_VALUE);
	}

	@Override
	public int getManualScale() {
		return fManualScale;
	}

	public void setManualScale(int scale) {
		this.fManualScale = scale;
		firePropertyChange(PROP_SCALE);
	}

	@Override
	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry1) {
		this.expiry = expiry1;
	}

	@Override
	public double getStrike() {
		return strike;
	}

	public void setStrike(double strike1) {
		this.strike = strike1;
	}

	@Override
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange1) {
		this.exchange = exchange1;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id1) {
		this.id = id1;
	}

	@Override
	public String toString() {
		return "SymbolData [name=" + name + ", symbol=" + symbol
				+ ", localSymbol=" + localSymbol + ", tickSize="
				+ fManualTickSize + ", tickValue=" + tickValue + ", scale="
				+ fManualScale + "]";
	}

	/**
	 * @param contract
	 */
	public void updateFromContract(HistoricalContract contract) {
		setName(contract.getLocalSymbol());
		setCurrency(contract.getCurrency());
		setExchange(contract.getExchange());
		setExpiry(contract.getExpiry());
		setId(contract.getId());
		setLocalSymbol(contract.getLocalSymbol());
		setStrike(contract.getStrike());
		setTickSize(contract.getTick() <= 0 ? 25 : contract.getTick());
		setType(contract.getType());
		setTickValue(contract.getTickValue());
		setManualScale(contract.getScale());
	}

}
