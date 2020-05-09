package com.mfg.dfs.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.connector.csv.reader.CsvAnalysis;
import com.mfg.connector.csv.reader.CsvFileReader;
import com.mfg.connector.csv.reader.CsvFileReader.IBarProcessor;
import com.mfg.dfs.cache.ICache;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.dfs.misc.DfsRangeBar;
import com.mfg.dfs.misc.DfsTimeBar;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * This class holds the data relative to a csv file.
 * 
 * <p>
 * Each csv has of course its tick and scale.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class CSVData extends BaseSymbolData {
	static class UncloseableBuffered extends BufferedInputStream {
		boolean closeEnabled = false;

		public UncloseableBuffered(InputStream in1) {
			super(in1);
		}

		@Override
		public void close() throws IOException {
			if (closeEnabled)
				super.close();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6376939776846413407L;

	/**
	 * Just a test constant, to be deleted.
	 */
	public static final String CSV_TEST_FOLDER = "C:\\Users\\Sergio\\no_backup\\giulio\\mktdata\\";

	/**
	 * @param aCsvStream
	 * @param aName
	 * @throws DFSException
	 * @throws ParseException
	 */
	private static CsvAnalysis _importFile(InputStream aCsvStream, String aName)
			throws DFSException {
		/*
		 * First of all I have to encapsulate the stream inside a buffered one.
		 */

		final CsvAnalysis ca = new CsvAnalysis();

		try (UncloseableBuffered reusableStream = new UncloseableBuffered(
				aCsvStream)) {

			if (!reusableStream.markSupported()) {
				throw new IllegalStateException();
			}
			reusableStream.mark(Integer.MAX_VALUE);
			CsvFileReader.analyse_csv_file(reusableStream, ca);
			U.debug_var(210413, "analysis of the stream " + ca);
			reusableStream.reset();

			/*
			 * Ok now I read the stream.
			 */
			final ICache<DfsBar> csvCache;
			if (ca.is_time_bar) {
				csvCache = MfgMdbSession.getInstance()
						.getCsvTimeBarCache(aName);
			} else {
				csvCache = MfgMdbSession.getInstance().getCsvRangeBarCache(
						aName);
			}

			if (csvCache.size() != 0) {
				csvCache.truncateAt(0);
			}

			IBarProcessor processor = new IBarProcessor() {

				@Override
				public void onEnd() {
					U.debug_var(876392, "processed " + csvCache.size()
							+ " candles");

					csvCache.compact(true);
				}

				@Override
				public void onNewBar(int aRecNumber, Bar aBar)
						throws DFSException {
					DfsBar dfsBar;

					if (ca.is_time_bar) {
						dfsBar = new DfsTimeBar(aBar, ca.computed_tick_size);
					} else {
						dfsBar = new DfsRangeBar(aBar, ca.computed_tick_size);
					}

					csvCache.addLastForce(dfsBar);

				}
			};
			reusableStream.closeEnabled = true;
			CsvFileReader.importCsvBars(reusableStream, processor, ca);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
			throw new DFSException(e);
		}

		return ca;

	}

	public static CSVData createCsvData(InputStream fis, String csvFileName)
			throws DFSException {
		CSVData cData;
		CsvAnalysis ca = _importFile(fis, csvFileName);

		DfsSymbol symbol = new DfsSymbol(csvFileName, csvFileName,
				ca.computed_tick_size, ca.computed_scale, 1);

		BarType importedType;
		int units = 1;
		if (ca.is_time_bar) {
			if (ca.interval % Yadc.ONE_MINUTE_MSEC != 0) {
				throw new DFSException("Cannot import length " + ca.interval);
			}

			if (ca.interval == Yadc.ONE_DAY_MSEC) {
				importedType = BarType.DAILY;
			} else {
				importedType = BarType.MINUTE;
				units = (int) (ca.interval / Yadc.ONE_MINUTE_MSEC);
			}
		} else {
			importedType = BarType.RANGE;
		}

		cData = new CSVData(symbol, importedType, units);

		return cData;
	}

	/**
	 * Used as a test... the csv folder is fixed.
	 * 
	 * @param csvFileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DFSException
	 */
	public static CSVData createCsvData(String csvFileName)
			throws FileNotFoundException, IOException, DFSException {
		CSVData cData;
		try (FileInputStream fis = new FileInputStream(new File(
				CSV_TEST_FOLDER, csvFileName + ".csv"));) {
			cData = createCsvData(fis, csvFileName);
		}

		return cData;
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException, DFSException,
			ParseException {
		String name = "short1min";
		try (FileInputStream fis = new FileInputStream(new File(
				CSV_TEST_FOLDER, name + ".csv"));) {
			CsvAnalysis cData = _importFile(fis, name);
		}

	}

	/**
	 * The csv has been converted to an history table, which is forever frozen,
	 * because it has read the data from a file and it never will change (we may
	 * have in the future a feature to join some data or to split it, but for
	 * now this is the state of the art).
	 * 
	 * <p>
	 * The table itself may be a {@link RangeHistoryTable} or a
	 * {@link TimeHistoryTable} because we may have range or time bars.
	 */
	CachedTable _table;

	BarType _type;

	/**
	 * To create a {@link CSVData} object please use the
	 * {@link #createCsvData(String)} static factory.
	 * 
	 * 
	 * @param aCsvStream
	 *            the stream which is used to read the csv input. It may be a
	 *            physical file or not. It is meant to be written in the normal
	 *            latin 1 encoding.
	 * @param aName
	 *            The name which is used to distinguish the file for DFS.
	 * @throws DFSException
	 *             if something is wrong.
	 * @throws IOException
	 */
	private CSVData(DfsSymbol aSymbol, BarType aType, int units)
			throws DFSException {
		super(aSymbol);
		_type = aType;

		/*
		 * If I am here I have successfully imported the file, so I now can
		 * create the table, this path is run not in deserialization because
		 * otherwise it is created by xstream without using the deserializator.
		 */

		if (_type == BarType.MINUTE || _type == BarType.DAILY) {
			_table = new CsvTimeHistoryTable(aSymbol, _type, units);
		} else {
			_table = new CsvRangeHistoryTable(aSymbol, BarType.RANGE);
		}

	}

	@Override
	public int getBarCount(Maturity parsedMaturity, BarType aType, int barWidth)
			throws DFSException {
		_checkCoherence(parsedMaturity, aType);
		return _table.getBarCount(barWidth);
	}

	/**
	 * Simple helper method that will check the coherence
	 * 
	 * @param parsedMaturity
	 * @param aType
	 * @throws DFSException
	 */
	private void _checkCoherence(Maturity parsedMaturity, BarType aType)
			throws DFSException {
		if (parsedMaturity != null || aType != _type) {
			throw new DFSException("incoherence request parsed maturity "
					+ parsedMaturity + " or type " + aType + " != from "
					+ _type);
		}

	}

	@Override
	public int getBarsBetween(Maturity parsedMaturity, BarType aType,
			int barWidth, long startDate, long endDate) throws DFSException {
		_checkCoherence(parsedMaturity, aType);
		return _table.getBarsBetween(barWidth, startDate, endDate);
	}

	@Override
	public long getDateAfterXBarsFrom(Maturity parsedMaturity, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		_checkCoherence(parsedMaturity, aType);
		return _table.getDateAfterXBarsFrom(barWidth, startDate, numBars);
	}

	@Override
	public long getDateBeforeXBarsFrom(Maturity parsedMaturity, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {
		_checkCoherence(parsedMaturity, aType);
		return _table.getDateBeforeXBarsFrom(barWidth, endTime, numBars);
	}

	@Override
	public IBarCache returnCache(Maturity parsedMaturity, RequestParams aReq)
			throws DFSException {
		_checkCoherence(parsedMaturity, aReq.getBarType());
		return _table.getCache(aReq);
	}

	@Override
	public void truncateMaturity(Maturity parsedMaturity, BarType aType,
			long truncateDate) throws DFSException {
		throw new UnsupportedOperationException();
	}

	public void createCache() throws IOException {
		_table._createCache();
	}

	@Override
	public DfsSymbolStatus getStatus(boolean forceCheck) throws DFSException {
		DfsCsvSymbolStatus status = new DfsCsvSymbolStatus(_symbol);
		status.intervalStats = _table.getStats(forceCheck);
		status.baseWidth = _table.getBaseWidth();
		status.type = _table.getType();
		return status;
	}

	@Override
	public void watchMaturity(Maturity parsedMaturity,
			IDatabaseChangeListener aListener) {
		// A csv data has not maturities inside.
		throw new UnsupportedOperationException();
	}

	@Override
	public void unwatchMaturity(Maturity parsedMaturity) {
		// A csv data has not maturities inside.
		throw new UnsupportedOperationException();

	}
}
