package com.mfg.dm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.mfg.common.BAR_TYPE;

@XmlSeeAlso(SlotParams.class)
public class DataProviderParams /* extends GenericIdentifier */{

	/**
	 * 
	 */
	private static final String PROP_USE_DATA_SERIES_MERGED_ALGORITHM = "useDataSeriesMergedAlgorithm";
	/**
	 * 
	 */
	private static final String PROP_SECONDARY_SLOTS = "secondarySlots";

	// @Override
	// protected void _toJsonEmbedded(JSONStringer stringer) throws
	// JSONException {
	// stringer.key("xp");
	// stringer.value(xp);
	// stringer.key("dp");
	// stringer.value(dp);
	// stringer.key("scales");
	// stringer.value(scales);
	// _collectionToJson(stringer, "slots", slots);
	//
	// }

	private double xp;
	private double dp;
	private int scales;
	private List<SlotParams> slots;
	private List<SlotParams> secondarySlots;
	private boolean useDataSeriesMergedAlgorithm;
	private Object extraData;

	private PropertyChangeListener slotListener;
	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private boolean gapFillingTypeSlidingWindow;

	public DataProviderParams() {
		init();
	}

	/**
	 * Provicional method get the historical data of a symbol. It is generic
	 * (Object) because data providers do not have to share the same settings
	 * interface/data. So, each plugin should define his own settings and cast
	 * to the right class.
	 * 
	 * @return the extraData
	 */
	public Object getExtraData() {
		return extraData;
	}

	/**
	 * @param extraData1
	 *            the extraData to set
	 */
	public void setExtraData(Object extraData1) {
		this.extraData = extraData1;
	}

	@SuppressWarnings("static-method")
	@XmlID
	public String getXmlKey() {
		return "eSignal";
	}

	/**
	 * @return the xp
	 */
	public double getXp() {
		return xp;
	}

	/**
	 * @param xp1
	 *            the xp to set
	 */
	public void setXp(double xp1) {
		this.xp = xp1;
		firePropertyChange("xp");
	}

	/**
	 * @return the dp
	 */
	public double getDp() {
		return dp;
	}

	public void setDp(double dp1) {
		this.dp = dp1;
		firePropertyChange("dp");
	}

	public int getScales() {
		return scales;
	}

	public void setScales(int scales1) {
		this.scales = scales1;
		firePropertyChange("scales");
	}

	public List<SlotParams> getSlots() {
		return slots;
	}

	public void addSlot(SlotParams params) {
		slots.add(params);
		firePropertyChange("slots");
		params.addPropertyChangeListener(getSlotListener());
	}

	public void addDefaultSlot() {
		SlotParams slot = new SlotParams();
		addSlot(slot);
	}

	public void removeSlot(SlotParams params) {
		slots.remove(params);
		params.removePropertyChangeListener(getSlotListener());
		firePropertyChange("slots");
	}

	private PropertyChangeListener getSlotListener() {
		if (slotListener == null) {
			slotListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					firePropertyChange("slots");
				}
			};
		}
		return slotListener;
	}

	public void setSlots(List<SlotParams> slots1) {
		this.slots = slots1;
		firePropertyChange("slots");
	}

	/**
	 * @return the secondarySlots
	 */
	public List<SlotParams> getSecondarySlots() {
		return secondarySlots;
	}

	/**
	 * @param secondarySlots1
	 *            the secondarySlots to set
	 */
	public void setSecondarySlots(List<SlotParams> secondarySlots1) {
		this.secondarySlots = secondarySlots1;
		firePropertyChange(PROP_SECONDARY_SLOTS);
	}

	private void init() {
		useDataSeriesMergedAlgorithm = true;

		initESignal();

		for (SlotParams slot : slots) {
			slot.addPropertyChangeListener(getSlotListener());
		}
	}

	private void initESignal() {
		slots = new ArrayList<>();
		slots.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.DAILY, 5));
		slots.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.MINUTE, 1000));
		slots.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.RANGE, 200));

		secondarySlots = new ArrayList<>();
		secondarySlots
				.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.DAILY, 5));
		secondarySlots.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.MINUTE,
				1000));
		secondarySlots.add(new SlotParams(UnitsType.DAYS, 1, BAR_TYPE.RANGE,
				200));

		xp = 0.25;
		dp = 0.25;
		scales = 10;
	}

	/**
	 * @return the useDataSeriesMergedAlgorithm
	 */
	public boolean isUseDataSeriesMergedAlgorithm() {
		return useDataSeriesMergedAlgorithm;
	}

	/**
	 * @param useDataSeriesMergedAlgorithm1
	 *            the useDataSeriesMergedAlgorithm to set
	 */
	public void setUseDataSeriesMergedAlgorithm(
			boolean useDataSeriesMergedAlgorithm1) {
		this.useDataSeriesMergedAlgorithm = useDataSeriesMergedAlgorithm1;
		firePropertyChange(PROP_USE_DATA_SERIES_MERGED_ALGORITHM);
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

	// @Override
	// protected void _updateFromJSON(JSONObject json) throws JSONException {
	// assert (false);
	// }

	/**
	 * @param b
	 */
	public void setGapFillingTypeSlidingWindow(
			boolean gapFillingTypeSlidingWindow1) {
		this.gapFillingTypeSlidingWindow = gapFillingTypeSlidingWindow1;
	}

	/**
	 * @return the gapFillingTypeSlidingWindow
	 */
	public boolean isGapFillingTypeSlidingWindow() {
		return gapFillingTypeSlidingWindow;
	}
}
