package com.mfg.connector.dfs;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.mfg.common.BarType;
import com.mfg.dm.UnitsType;
import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.dm.symbols.MergeSeriesAlgorithm;

//I suppress this warning because serialization uses the fields and is not
//recommendable rename them now.
@SuppressWarnings("hiding")
public class DFSHistoricalDataInfo extends HistoricalDataInfo implements
		Cloneable {

	private static final String PROP_WARM_UP_NUMBER_OF_PRICES = "warmUpNumberOfPrices";
	private static final String PROP_MIN_GAP_IN_TICKS = "minGapInTicks";
	private static final String PROP_FILTER_OUT_OF_RANGE_TICKS = "filterOutOfRangeTicks";
	public static final String PROP_REQUEST_MODE = "requestMode";
	private static final String PROP_DP = "dp";
	private static final String PROP_XP = "xp";
	private static final String PROP_GAP_FILLING_TYPE = "gapFillingType";
	private static final String PROP_MULTIPLE_DATA_SERIES_ALGORITHM = "multipleDataSeriesAlgorithm";
	private static final String PROP_SLOTS = "slots";
	private static final String PROP_PROFILE_ID = "profileId";
	private UUID profileId;
	private List<Slot> slots;
	private MergeSeriesAlgorithm multipleDataSeriesAlgorithm;
	private GapFillingType gapFillingType;
	private double xp;
	private double dp;
	private int _warmUpNumberOfPrices;
	private boolean _filterOutOfRangeTicks;
	private int _minGapInTicks;
	private RequestMode requestMode;
	private boolean _used;

	public static class Slot implements Cloneable, Comparable<Slot> {
		private static final String PROP_GAP2 = "gap2";
		private static final String PROP_GAP1 = "gap1";
		private static final String PROP_GAP_STR = "gapString";
		private static final String PROP_SCALE_NAME = "scaleName";
		public static final String PROP_END_DATE_BLOCK = "endDateBlock";
		public static final String PROP_START_DATE_BLOCK = "startDateBlock";
		public static final String PROP_BAR_TYPE = "barType";
		public static final String PROP_NUMBE_OF_UNITS = "numbeOfUnits";
		public static final String PROP_GAP = "gap";
		public static final String PROP_SCALE = "scale";
		public static final String PROP_END_DATE = "endDate";
		public static final String PROP_START_DATE = "startDate";
		public static final String PROP_NUMBER_OF_BARS = "numberOfBars";

		private static final String PROP_UNITS_TYPE = "unitsType";
		private UnitsType unitsType;
		private int numberOfBars;
		private Date startDate;
		private Date endDate;
		private int scale;
		private double gap;
		private int gap1;
		private int gap2;
		private int numbeOfUnits;
		private BarType barType;
		private boolean startDateBlock;
		private boolean endDateBlock;
		private static final NumberFormat _numFormat = NumberFormat
				.getInstance(Locale.getDefault());
		private static DateFormat format = DateFormat
				.getDateInstance(DateFormat.SHORT);

		public Slot() {
			unitsType = UnitsType.BARS;
			numbeOfUnits = 1;
			barType = BarType.RANGE;
			numberOfBars = 100;
			scale = 0;
			gap = 0;
			startDate = new Date();
			endDate = new Date();
			startDateBlock = false;
			endDateBlock = true;
			gap1 = 3;
			gap2 = 4;
		}

		@Override
		public Slot clone() {
			try {
				Slot clone = (Slot) super.clone();
				clone.startDate = (Date) startDate.clone();
				clone.endDate = (Date) endDate.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		public BarType getBarType() {
			return barType;
		}

		public void setBarType(BarType barType) {
			this.barType = barType;
			firePropertyChange(PROP_BAR_TYPE);
		}

		public int getNumbeOfUnits() {
			return numbeOfUnits;
		}

		public void setNumbeOfUnits(int numbeOfUnits) {
			this.numbeOfUnits = numbeOfUnits;
			firePropertyChange(PROP_NUMBE_OF_UNITS);
		}

		public UnitsType getUnitsType() {
			return unitsType;
		}

		public void setUnitsType(UnitsType unitsType) {
			this.unitsType = unitsType;
			firePropertyChange(PROP_UNITS_TYPE);
		}

		public int getNumberOfBars() {
			return numberOfBars;
		}

		public String getNumberOfBarsString() {
			return _numFormat.format(numberOfBars);
		}

		public void setNumberOfBars(int numberOfBars) {
			this.numberOfBars = numberOfBars;
			firePropertyChange(PROP_NUMBER_OF_BARS);
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
			firePropertyChange(PROP_START_DATE);
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
			firePropertyChange(PROP_END_DATE);
		}

		public long getNumberOfDays() {
			return TimeUnit.MILLISECONDS.toDays(endDate.getTime()
					- startDate.getTime());
		}

		public String getNumberOfDaysString() {
			return _numFormat.format(getNumberOfDays());
		}

		@Deprecated
		public long getNumberOfBarsOrDays() {
			return unitsType == UnitsType.BARS ? getNumberOfBars()
					: getNumberOfDays();
		}

		public String getScaleName() {
			return scale == 0 ? "Price" : scale + "";
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int scale) {
			this.scale = scale;
			firePropertyChange(PROP_SCALE);
			firePropertyChange(PROP_SCALE_NAME);
		}

		public int getGap1() {
			return gap1;
		}

		public void setGap1(int gap1) {
			this.gap1 = gap1;
			firePropertyChange(PROP_GAP1);
			firePropertyChange(PROP_GAP_STR);
		}

		public int getGap2() {
			return gap2;
		}

		public void setGap2(int gap2) {
			this.gap2 = gap2;
			firePropertyChange(PROP_GAP2);
			firePropertyChange(PROP_GAP_STR);
		}

		public String getGapString() {
			return gap1 + "/" + gap2;
		}

		@Deprecated
		public double getGap() {
			return gap;
		}

		@Deprecated
		public void setGap(double gap) {
			this.gap = gap;
			firePropertyChange(PROP_GAP);
		}

		public boolean isStartDateBlock() {
			return startDateBlock;
		}

		public void setStartDateBlock(boolean startDateBlock) {
			this.startDateBlock = startDateBlock;
			firePropertyChange(PROP_START_DATE_BLOCK);
		}

		public boolean isEndDateBlock() {
			return endDateBlock;
		}

		public void setEndDateBlock(boolean endDateBlock) {
			this.endDateBlock = endDateBlock;
			firePropertyChange(PROP_END_DATE_BLOCK);
		}

		private transient final PropertyChangeSupport support = new PropertyChangeSupport(
				this);

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

		public void setStartDateBlock_doNotNotify(boolean startDateBlock) {
			this.startDateBlock = startDateBlock;
		}

		public void setEndDateBlock_doNotNotify(boolean endDateBlock) {
			this.endDateBlock = endDateBlock;
		}

		public void setStartDate_doNotNotify(Date startDate) {
			this.startDate = startDate;
		}

		public void setEndDate_doNotNotify(Date enddAte) {
			this.endDate = enddAte;
		}

		public void setNumberOfBars_doNotNotify(int numberOfBars) {
			this.numberOfBars = numberOfBars;
		}

		@Override
		public int compareTo(Slot o) {
			if (barType == o.barType) {
				return startDate.compareTo(o.startDate);
			}
			return barType.compareTo(o.barType); // daily, minute, range
		}

		@Override
		public String toString() {
			return "[ " + barType + " (" + numberOfBars + ") "
					+ format.format(startDate) + " -> "
					+ format.format(endDate) + " ]";
		}
	}

	public enum GapFillingType {
		SLIDING_WINDOW("Sliding Window"), TICKS_NUMBER("Ticks Number");

		private String label;

		private GapFillingType(String name) {
			this.label = name;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	public enum RequestMode {
		DATABASE, MIXED;
		@Override
		public String toString() {
			return this == DATABASE ? "Database" : "Mixed";
		}
	}

	public DFSHistoricalDataInfo() {
		_used = false;
		requestMode = RequestMode.DATABASE;
		multipleDataSeriesAlgorithm = MergeSeriesAlgorithm.UNMERGE;
		gapFillingType = GapFillingType.TICKS_NUMBER;
		xp = 0.25;
		dp = 0.25;
		_filterOutOfRangeTicks = true;
		_minGapInTicks = 5;
		_warmUpNumberOfPrices = 20_000;

		// UnitsType.DAYS, 1, BAR_TYPE.DAILY, 5
		// UnitsType.DAYS, 1, BAR_TYPE.MINUTE, 1000
		// UnitsType.DAYS, 1, BAR_TYPE.RANGE, 200

		slots = new ArrayList<>();
		Slot slot = new Slot();
		slot.setBarType(BarType.DAILY);
		slot.setNumberOfBars(5);
		slots.add(slot);

		slot = new Slot();
		slot.setBarType(BarType.MINUTE);
		slot.setNumberOfBars(1000);
		slots.add(slot);

		slot = new Slot();
		slot.setBarType(BarType.RANGE);
		slot.setNumberOfBars(200);
		slots.add(slot);

	}

	/**
	 * If was used, it means, opened in an editor.
	 * 
	 * @return
	 */
	public boolean isUsed() {
		return _used;
	}

	public void setUsed(boolean used) {
		_used = used;
	}

	public int getWarmUpNumberOfPrices() {
		return _warmUpNumberOfPrices;
	}

	public void setWarmUpNumberOfPrices(int warmUpNumberOfPrices) {
		_warmUpNumberOfPrices = warmUpNumberOfPrices;
		firePropertyChange(PROP_WARM_UP_NUMBER_OF_PRICES);
	}

	@Override
	public DFSHistoricalDataInfo clone() {
		DFSHistoricalDataInfo clone;
		try {
			clone = (DFSHistoricalDataInfo) super.clone();
			clone.setSlots(new ArrayList<Slot>());
			for (Slot slot : slots) {
				clone.getSlots().add(slot.clone());
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public RequestMode getRequestMode() {
		return requestMode;
	}

	public void setRequestMode(RequestMode requestMode) {
		this.requestMode = requestMode;
		firePropertyChange(PROP_REQUEST_MODE);
	}

	public GapFillingType getGapFillingType() {
		return gapFillingType;
	}

	public void setGapFillingType(GapFillingType gapFillingType) {
		this.gapFillingType = gapFillingType;
		firePropertyChange(PROP_GAP_FILLING_TYPE);
	}

	public MergeSeriesAlgorithm getMultipleDataSeriesAlgorithm() {
		return multipleDataSeriesAlgorithm;
	}

	public void setMultipleDataSeriesAlgorithm(
			MergeSeriesAlgorithm multipleDataSeriesAlgorithm) {
		this.multipleDataSeriesAlgorithm = multipleDataSeriesAlgorithm;
		firePropertyChange(PROP_MULTIPLE_DATA_SERIES_ALGORITHM);
	}

	public UUID getProfileId() {
		return profileId;
	}

	public void setProfileId(UUID profileId) {
		this.profileId = profileId;
		firePropertyChange(PROP_PROFILE_ID);
	}

	public double getDp() {
		return dp;
	}

	public void setDp(double dp) {
		this.dp = dp;
		firePropertyChange(PROP_DP);
	}

	public double getXp() {
		return xp;
	}

	public void setXp(double xp) {
		this.xp = xp;
		firePropertyChange(PROP_XP);
	}

	public int getMinGapInTicks() {
		return _minGapInTicks;
	}

	public void setMinGapInTicks(int minGapInTicks) {
		_minGapInTicks = minGapInTicks;
		firePropertyChange(PROP_MIN_GAP_IN_TICKS);
	}

	public boolean isFilterOutOfRangeTicks() {
		return _filterOutOfRangeTicks;
	}

	public void setFilterOutOfRangeTicks(boolean filterOutOfRangeTicks) {
		_filterOutOfRangeTicks = filterOutOfRangeTicks;
		firePropertyChange(PROP_FILTER_OUT_OF_RANGE_TICKS);
	}

	public List<Slot> getSlots() {
		return slots;
	}

	public void setSlots(List<Slot> slots) {
		this.slots = slots;
		firePropertyChange(PROP_SLOTS);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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

	@Override
	public boolean allowPaperTrading() {
		return true;
	}

	@Override
	public boolean forceDoPaperTrading() {
		return getRequestMode() == RequestMode.DATABASE;
	}

}
