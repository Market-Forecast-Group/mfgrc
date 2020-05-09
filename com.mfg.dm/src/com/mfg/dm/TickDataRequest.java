package com.mfg.dm;

import java.util.ArrayList;

import com.mfg.common.BAR_TYPE;
import com.mfg.common.DFSException;
import com.mfg.common.DfsRealSymbol;
import com.mfg.common.MfgSymbol;
import com.mfg.utils.XmlIdentifier;

/**
 * A class which contains all the information to make a post processing chain,
 * that is, from data sources to a TickSource.
 * <p>
 * A TickDataRequest will be associated to a TickDataSource, with all the
 * filters, chains merge algorithm, indicator, etc...
 * 
 * @author Sergio
 * 
 */
public class TickDataRequest extends XmlIdentifier {

	private static final int DEFAULT_INITIAL_MULTIPLIER = 3;
	private static final int DEFAULT_FOLLOWING_MULTIPLIER = 4;
	protected int _nPricesWarmup;
	private final double _xp;
	private final double _dp;

	/**
	 * Each request is tied to a particular symbol, the symbol may be a normal
	 * symbol, a csv symbol.
	 * 
	 * <p>
	 * The virtual symbol is instead the representation of this tick data
	 * request
	 */
	public final MfgSymbol _symbol;

	/**
	 * the request can be real time or not; this roughly is in relation to the
	 * mode of the request, either database or mixed; the fact that we subscribe
	 * or not depends on the type of this request.
	 * 
	 * <p>
	 * A request with only one layer, real time, is subscribed to the quote if
	 * the layer is a range layer.
	 * 
	 * <p>
	 * A request with multiple layer will be subscribed to the quote always and
	 * NOT to the bars (so we can close the cache).
	 * 
	 * <p>
	 * So some requests will ask for the bars, some for the quotes, some for
	 * both, some for none. It depends on the "architecture" of the request
	 * itself.
	 */
	protected boolean _realTimeRequest;

	/**
	 * This is the array of raw data requests. Of course a composite data
	 * request can be composed of only one DataRequest, in that case the merge
	 * parameter is not used.
	 * 
	 */
	protected ArrayList<DataRequest> requests = new ArrayList<>();

	/**
	 * if true the gap filling algorithm will use the sliding window.
	 * 
	 * <p>
	 * If false it will use the price multiplier.
	 */
	private final boolean _useWindow;
	/**
	 * the default value of the seed is -1 and this means to use a different
	 * seed for each run.
	 */
	protected long _seed = -1;

	/**
	 * This boolean is used to know wether we have to filter the out of range
	 * ticks
	 * <p>
	 * This boolean is linked to the FilterLonelyTicksFilter class
	 * 
	 */
	private final boolean _isFilterOutOfRangeTicks;

	/**
	 * minimum gap to declare a tick as lonely
	 */
	private final int _minGapLonelyTick;

	/**
	 * True if this tick data request is composed of merge layers.
	 */
	private final boolean _isMerged;

	/**
	 * Build a composite data request using <b>only</b> one slot. In a certain
	 * sense a <i>composite</i> data request with only one slot is not
	 * composite, but also in the previous version the data request could be
	 * composed of only one slot.
	 * 
	 * @param aContract
	 *            the contract for this request
	 * @param aSlot
	 *            the slot contains the request (bar type, num bars, etc...)
	 * @param continueRealTime
	 *            true if you want this request to continue in real time
	 * @param d
	 * @param isAutomaticTick
	 *            true if you want the data request to compute the tick size.
	 */
	public TickDataRequest(DfsRealSymbol aSymbol, SlotParams aSlot,
			boolean continueRealTime, double aXp, double aDp,
			boolean useWindow, boolean areLonelyTicksFiltered,
			int minGapLonelyTick, int npricesWarmup) {
		// parms = null;
		_useWindow = useWindow;
		_nPricesWarmup = npricesWarmup;
		_xp = aXp;
		_dp = aDp;

		_symbol = aSymbol;

		/*
		 * only one slot, it is not merged, or, better, the merge parameter is
		 * not important.
		 */
		_isMerged = false;

		_realTimeRequest = continueRealTime;
		_isFilterOutOfRangeTicks = areLonelyTicksFiltered;
		_minGapLonelyTick = minGapLonelyTick;

		DataRequest dr = new DataRequest(aSlot.getBarType(),
				aSlot.getMultiplicityBar(), aSlot.getStartDate(),
				aSlot.getNumBars(), aSlot.getUnitsType(),
				DEFAULT_INITIAL_MULTIPLIER, DEFAULT_FOLLOWING_MULTIPLIER);

		ArrayList<DataRequest> reqs = new ArrayList<>();
		reqs.add(dr);
		setRequests(reqs);

	}

	/**
	 * constructor used only for csv files.
	 * 
	 * @param aSymbol
	 *            the symbol is simply the csv file name.
	 */
	public TickDataRequest(MfgSymbol aSymbol) {

		_dp = 0.3;
		_isFilterOutOfRangeTicks = false;
		_minGapLonelyTick = 0;
		_nPricesWarmup = 0;
		_realTimeRequest = false;
		_useWindow = true;
		_xp = 0.3;
		// parms = null;

		_isMerged = false;

		_symbol = aSymbol;

		requests.add(new DataRequest(BAR_TYPE.HOUR, 0, -1, 0, UnitsType.BARS,
				DEFAULT_INITIAL_MULTIPLIER, DEFAULT_FOLLOWING_MULTIPLIER));
	}

	/**
	 * This should be the "normal" constructor, all the others are just for
	 * testing purposes.
	 * 
	 * @param contract
	 * @param parms1
	 * @param fillTheGaps1
	 * @param filterOneTick1
	 * @param npCsv
	 * @param isRealTime
	 */
	public TickDataRequest(MfgSymbol aSymbol, DataProviderParams parms1,
			int npCsv, boolean isRealTime, boolean areLonelyTicksFiltered,
			int minGapLonelyTick) {
		// this.parms = parms1;
		_useWindow = parms1.isGapFillingTypeSlidingWindow();
		this._nPricesWarmup = npCsv;
		_isFilterOutOfRangeTicks = areLonelyTicksFiltered;
		_minGapLonelyTick = minGapLonelyTick;
		_isMerged = parms1.isUseDataSeriesMergedAlgorithm();

		_xp = parms1.getXp();
		_dp = parms1.getDp();
		_initArrayOfRequests(parms1);

		_symbol = aSymbol;

		_realTimeRequest = isRealTime;
	}

	/**
	 * Simple constructor used to create a void tick data request.
	 * <p>
	 * The requests will be added later using the
	 * {@link #setRequests(ArrayList)} method.
	 * 
	 * @param aXp
	 * @param isUseWindow
	 * @param aDp
	 * @param toBeReused
	 *            true if you want to reuse the request: it is an error to reuse
	 *            a real time request.
	 * 
	 * @param numberOfWarmUpPrices
	 *            number of prices used to warm up the data source. it is an
	 *            error to have a number of prices different from zero in a real
	 *            time request, or a number of warm up prices zero in a
	 *            historical request.
	 */
	public TickDataRequest(MfgSymbol aSymbol, double aXp, double aDp,
			boolean isUseWindow, boolean isRealTime, long seed,
			boolean areLonelyTicksFiltered, int minGapLonelyTick,
			boolean isMerged, int numberOfWarmUpPrices) {
		if (isRealTime && numberOfWarmUpPrices != 0) {
			throw new IllegalArgumentException();
		}

		if (!isRealTime && numberOfWarmUpPrices == 0) {
			throw new IllegalArgumentException();
		}
		_nPricesWarmup = numberOfWarmUpPrices;
		_xp = aXp;
		_useWindow = isUseWindow;
		_dp = aDp;
		_realTimeRequest = isRealTime;
		_seed = seed;
		_isFilterOutOfRangeTicks = areLonelyTicksFiltered;
		_minGapLonelyTick = minGapLonelyTick;
		_isMerged = isMerged;
		_symbol = aSymbol;
	}

	/*
	 * This is called when we do the merge, so must subscribe is true and
	 * continue in real time is true but we don't want to have pushed incomplete
	 * bars.
	 */
	private void _initArrayOfRequests(DataProviderParams parms1) {
		ArrayList<DataRequest> reqs = new ArrayList<>();
		for (SlotParams aSlot : parms1.getSlots()) {

			int initialMultiplier = aSlot.getGap1();
			int followingMultiplier = aSlot.getGap2();
			DataRequest dr = new DataRequest(aSlot.getBarType(),
					aSlot.getMultiplicityBar(), aSlot.getStartDate(),
					aSlot.getNumBars(), aSlot.getUnitsType(),
					initialMultiplier, followingMultiplier);
			reqs.add(dr);
		}
		setRequests(reqs);
	}

	/**
	 * Returns true if in this request we need new bars, this depends first of
	 * all if this is a real time request, and second if this request is of one
	 * layer only, and this layer is not range.
	 * 
	 * @param ordinalLayer
	 *            the layer for which you ask if bars are needed. The ordinal
	 *            has the backward convention, the zero layer is the top most.
	 * 
	 * @return true if we need new bars from this request.
	 */
	public boolean areNewBarsNeeded(int ordinalLayer) {
		if (!_realTimeRequest) {
			return false;
		}

		if (isMerged()) {
			return false;
		}

		int internalLayer = this.requests.size() - ordinalLayer - 1;
		DataRequest dr = requests.get(internalLayer);

		if (dr.barType == BAR_TYPE.RANGE && ordinalLayer == 0) {
			/*
			 * The last range request is handled differently
			 */
			return false;
		}

		return true;
	}

	public double getDp() {
		return _dp;
	}

	/**
	 * The request knows the size of the layers, because it has the slots and
	 * knows if the slots must be merged or not.
	 * 
	 * @return
	 */
	public int getLayersSize() {
		if (_isMerged) {
			return 1;
		}
		return requests.size();
	}

	public String getLocalSymbol() {
		return _symbol.getSymbol();
	}

	public int getMinimumGap() {
		return _minGapLonelyTick;
	}

	public int getNumberWarmupPrices() {
		return _nPricesWarmup;
	}

	public ArrayList<DataRequest> getRequests() {
		return this.requests;
	}

	/**
	 * returns the seed used to expand the tick data request (mainly for the
	 * markovian).
	 * 
	 * <p>
	 * If the seed was set to a negative value then a new random seed is given
	 * each time.
	 * 
	 * @return the seed used or a new random one each time.
	 */
	public long getSeed() {
		if (_seed < 0) {
			return System.currentTimeMillis();
		}
		return _seed;
	}

	public MfgSymbol getSymbol() {
		return _symbol;
	}

	public int getTick() {
		return _symbol.getTick();
	}

	public double getXp() {
		return _xp;
	}

	public boolean isFilterLonelyTicks() {
		return _isFilterOutOfRangeTicks;
	}

	public boolean isGapFillingUsingWindow() {
		return _useWindow;
	}

	/**
	 * returns true if the data series is merged.
	 * 
	 * @return
	 */
	public boolean isMerged() {
		return _isMerged;
		// return parms.isUseDataSeriesMergedAlgorithm();
	}

	/**
	 * returns true if this is a real time request, a.k.a. a <b>MIXED</b>
	 * request.
	 * 
	 * @return true if this request is a real time request. If not it is meant
	 *         that this request will be controllable by the outside.
	 */
	public boolean isRealTime() {
		return this._realTimeRequest;
	}

	/**
	 * returns true if we have to subscribe to quote.
	 * 
	 * 
	 * @return true if we have to subscribe.
	 */
	public boolean mustSubscribeToQuote() {
		if (!_realTimeRequest) {
			return false;
		}

		/*
		 * If the requests are more than one than it means that we merge them,
		 * so we use only the quote, and we have to subscribe
		 */
		if (isMerged()) {
			return true; // unconditionally
		}

		/*
		 * if we do not merge, but the first layer is range I need the quote.
		 */
		if (requests.get(requests.size() - 1).barType == BAR_TYPE.RANGE) {
			return true;
		}

		return false;
	}

	public void setRequests(ArrayList<DataRequest> reqs) {
		requests = reqs;
	}

	public static void main(String args[]) throws DFSException {

		TickDataRequest tdr = new TickDataRequest(new DfsRealSymbol("ESU13",
				"ciei", 20, 1, 20));

		System.out.println(tdr.serializeToString());

		System.out.println(tdr.getHashId());

		TickDataRequest tdr2 = (TickDataRequest) XmlIdentifier
				.createFromString(tdr.serializeToString());

		System.out.println(tdr2.getHashId());
	}

}
