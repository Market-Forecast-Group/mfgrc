package com.mfg.dfs.data;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Date;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.dfs.cache.HistoryTablesContainer;
import com.mfg.utils.U;

/**
 * This class models the continuous data.
 * 
 * <p>
 * This class does not hold any particular data, because it is meant to be a
 * virtual view based on all the data which is already collected in the
 * {@linkplain SymbolData} object.
 * 
 * <p>
 * In this way we can present to the user a continuous version of the data
 * without duplicating data in the db.
 * 
 * @author Sergio
 * 
 */
public class ContinuousData extends HistoryTablesContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -334216915580623288L;

	/**
	 * This is the list of all the crossovers which are used to store the dates
	 * for the crossovers and the price offset for them.
	 * 
	 * <p>
	 * This list is of course persisted in the cache, because I need it for all
	 * the tables to get the chunk for a particular date (instead the search for
	 * a chunk with a particular index is done in the table itself, because of
	 * course every table has its own indexes).
	 * 
	 * <p>
	 * The list is also stored here because we need to store the price offsets
	 * for all the crossovers; this datum is shared by all the table types.
	 * 
	 * <p>
	 * In reality it would not be really necessary to store them in cache,
	 * because also each Continuous Table has a reference to it, but
	 */
	private ArrayList<CrossoverData> _crossOvers = new ArrayList<>();

	/**
	 * used for deserializing the object with another lower version.
	 * 
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		if (_crossOvers == null) {
			_crossOvers = new ArrayList<>();
		}
		return this;
	}

	/**
	 * When I create the continuous data I have to be
	 * 
	 * @param symbolData
	 */
	public ContinuousData(DfsSymbol aSymbol) {
		_tables.put(BarType.DAILY, new ContinuousTable(aSymbol, BarType.DAILY));
		_tables.put(BarType.MINUTE,
				new ContinuousTable(aSymbol, BarType.MINUTE));
		_tables.put(BarType.RANGE, new ContinuousTable(aSymbol, BarType.RANGE));
	}

	/**
	 * returns the current maturity in force for this object.
	 * 
	 * <p>
	 * The current maturity is stored in each table. Every of the three tables
	 * holds this information. We could say that the current maturity can be
	 * stored one time only... but all the tables have different chuncks, or,
	 * better, they could have the same chuncks but with different indeces.
	 * 
	 * <p>
	 * No, they have not the same chuncks, because range are limited.
	 * 
	 * @return
	 * @throws DFSException
	 */
	public Maturity getCurrentMaturity() throws DFSException {
		return ((ContinuousTable) _tables.get(BarType.DAILY))
				.getCurrentMaturity();
	}

	public Maturity getCurrentSymbol() throws DFSException {
		return ((ContinuousTable) _tables.get(BarType.DAILY))
				.getCurrentMaturity();
	}

	@Override
	public MaturityStats getStatus(boolean forceCheck) {
		return super.getStatus(null, forceCheck);
	}

	public DfsSymbol getSymbol() {
		return _tables.get(BarType.DAILY).getSymbol();
	}

	@Override
	public boolean needsRecompute() {
		boolean res = false;
		for (SingleWidthTable ht : _tables.values()) {
			res |= ((ContinuousTable) ht).needsRecompute();
		}
		return res;
	}

	/**
	 * accepts a new crossover, this can come from the historical part or also
	 * from the real time part, it does not matter.
	 * 
	 * @param cd
	 *            the struct which holds the important facts about this
	 *            crossover.
	 * @throws DFSException
	 */
	public void newCrossOver(CrossoverData cd) throws DFSException {

		if (_crossOvers.size() != 0) {
			CrossoverData lastCrossOver = _crossOvers
					.get(_crossOvers.size() - 1);
			if (lastCrossOver.crossDate > cd.crossDate) {
				throw new IllegalStateException();
			} else if (lastCrossOver.crossDate == cd.crossDate) {
				U.debug_var(834828, "same x over at date ", new Date(
						cd.crossDate), " nothing to add.");
			}
		}

		for (CrossoverData item : _crossOvers) {
			item.offsetOffset(cd.getPriceOffset());
		}

		_crossOvers.add(cd);

		// Ok, then I have to give to the continuous table the same message.
		for (SingleWidthTable item : _tables.values()) {
			((ContinuousTable) item).newCrossOver(cd);
		}
	}

	public void startCrossOverComputation() {
		_crossOvers.clear();
		for (SingleWidthTable item : _tables.values()) {
			((ContinuousTable) item).startCrossOverComputation();
		}
	}

	public void endCrossOverComputation() throws DFSException {
		for (SingleWidthTable item : _tables.values()) {
			((ContinuousTable) item).endCrossOverComputation(this);
		}
	}

	/**
	 * returns the last cross over date which is used by the {@link SymbolData}
	 * to know if there has been a crossover recently
	 * 
	 * @return the last cross over date, if there has been none return
	 *         {@link Long#MIN_VALUE}
	 */
	long getLastCrossOverDate() {
		if (_crossOvers.size() == 0) {
			return Long.MIN_VALUE;
		}
		long lastXDate = _crossOvers.get(_crossOvers.size() - 1).crossDate;
		return lastXDate;
	}

	@Override
	protected String getCompleteSymbol() {
		return _tables.get(BarType.DAILY).getSymbol().prefix
				+ Maturity.CONTINUOUS_SUFFIX;
	}
}
