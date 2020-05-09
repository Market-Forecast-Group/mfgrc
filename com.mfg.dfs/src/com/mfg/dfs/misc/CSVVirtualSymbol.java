package com.mfg.dfs.misc;

import java.util.Iterator;

import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.connector.csv.reader.CsvFileReader;
import com.mfg.connector.csv.reader.CsvReaderParams;
import com.mfg.connector.csv.reader.DataSource1P;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.filters.CacheExpander;
import com.mfg.dm.filters.CacheExpander.EGapFillingMethod;
import com.mfg.utils.U;

/**
 * A virtual symbol which is used to expand a csv file in servers' space.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class CSVVirtualSymbol extends VirtualSymbolBase {

	private DataSource1P _dataSource;
	private Iterator<Tick> _iterator;
	private int _upperLimitForWarmUp;

	public CSVVirtualSymbol(MultiServer aServer, TickDataRequest aRequest) {
		super(aServer, aRequest);

	}

	@Override
	protected void _virtualSymbolThread() {

		_createDummyCsvExpander(_dataSource.dsc.tick);

		/*
		 * The csv thread is simple, because I already have the csv file read.
		 * The thread will simply give the ticks
		 */
		try {
			_delayControl = new ServerDelayControl();

			// int currentFakeTime = 0;
			while (_iterator.hasNext()) {
				Tick tick = _iterator.next();

				_delayControl.delay(_monitor, _expanders.get(0)
						.getCurrentFakeTime());

				RealTick rt = new RealTick(tick, true);

				_expanders.get(0).realTimeTick(rt);

				if (!_realTimeStartedForLayer[0]
						&& _expanders.get(0).getCurrentFakeTime() >= _upperLimitForWarmUp) {
					baseWarmUpNotify(0);
				}
			}
		} catch (MonitorCancelledException e) {
			// nothing, the thread is exited normally.
			U.debug_var(237462, Thread.currentThread().getName(),
					" monitor cancelled exception. OK");
		} catch (DFSException e) {
			e.printStackTrace();
		}

		U.debug_var(329483, Thread.currentThread().getName(), " NORMAL END.");

	}

	/**
	 * Creates the unique expander used to accept the real time ticks from the
	 * csv...
	 * 
	 * <p>
	 * This may be a temporary method because we may later have the possibility
	 * to convert a csv file in a {@link IBarCache} and that will solve all the
	 * problems (because the bars will be expanded directly in the
	 * {@link CacheExpander}).
	 * 
	 * @param tick
	 */
	private void _createDummyCsvExpander(int tick) {
		_expanders.clear();

		int gap1 = 0;
		int gap2 = 0;

		EGapFillingMethod method = EGapFillingMethod.PRICE_MULTIPLIER;
		double aXp = 0.25;
		double aDp = 0.25;

		/*
		 * if the tick data request is null I can have the possibility to
		 * request the normal data for this request.
		 */
		if (_request != null) {

			gap1 = _request.getRequests().get(0).getGap1();
			gap2 = _request.getRequests().get(0).getGap2();

			aXp = _request.getXp();
			aDp = _request.getDp();

			boolean isUseWindow = this._request.isGapFillingUsingWindow();

			if (isUseWindow) {
				method = EGapFillingMethod.UPPER_CACHE;
			} else {
				method = EGapFillingMethod.PRICE_MULTIPLIER;
			}
		}

		int dummyLayer = 0;
		long _seed = 99;
		CacheExpander ce = new CacheExpander(null, null, method, gap1, gap2,
				tick, _seed, aXp, aDp, false/* do not filter lonely ticks */,
				0, this, dummyLayer, true /* do not truncate the layers. */,
				false /* not real time */);
		_expanders.add(ce);
		try {
			ce.expand();
		} catch (MonitorCancelledException | DFSException e) {
			e.printStackTrace();
			stop();
		}
	}

	@Override
	public DfsSymbol getDfsSymbol() {
		/*
		 * I have to read the csv file, analyse it and return a symbol which has
		 * the tick and the scale appropriately put...
		 * 
		 * This is the trigger to read the csv file... because in the
		 * constructor it has been not read.
		 */

		if (_dataSource == null) {
			String fileName = _request.getSymbol().getSymbol();

			final CsvReaderParams crp = new CsvReaderParams();

			crp.csv_file_name = fileName;

			_dataSource = new DataSource1P();
			_dataSource = CsvFileReader.read_csv_file(crp, _dataSource, false);

			_iterator = _dataSource.ticks.iterator();

			_upperLimitForWarmUp = Math.min(_dataSource.ticks.size(),
					_request.getNumberWarmupPrices());
		}

		return new DfsSymbol(_request.getLocalSymbol(), _id,
				_dataSource.dsc.tick, _dataSource.dsc.scale_from_the_source,
				_dataSource.dsc.tick);
	}

}
