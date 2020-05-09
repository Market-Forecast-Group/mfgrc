package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.Maturity.ParseMaturityAns;
import com.mfg.common.RequestParams;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.data.BaseSymbolData;
import com.mfg.dfs.data.CSVData;
import com.mfg.dfs.data.ContinuousTable;
import com.mfg.dfs.data.CrossoverData;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.data.MaturityData;
import com.mfg.dfs.data.RangeHistoryTable;
import com.mfg.dfs.data.SymbolData;
import com.mfg.dfs.data.TimeHistoryTable;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;
import com.sun.istack.internal.NotNull;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

/**
 * This is the repository for all the caches in the system.
 * 
 * <p>
 * The Dfs contains some caches which are used to store the data which DFS
 * caches for the clients.
 * 
 * <p>
 * The cache repository can be thought as a container of several databases;
 * these databases are then stored here.
 * 
 * <p>
 * There may be different backend for the cache, for now I have the MDB backend,
 * but we may have others instead.
 * 
 * @author Sergio
 * 
 */
public class DfsCacheRepo {

	/**
	 * This is the representation of the master cache, which is the cache that
	 * lists the other caches.
	 * 
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private static final class MasterCacheRepresentation {

		public MasterCacheRepresentation() {
			// nothing to do.
		}

		/**
		 * The version of the master cache.
		 */
		@SuppressWarnings("unused")
		public int version;

		/**
		 * The map of symbols present in the map. Each symbol is referred to a
		 * prefix, which is usually the name with which is known by the feed,
		 * and a prefix which is the name of the directory where the symbol is
		 * stored.
		 */
		public HashMap<String, String> symbols = new HashMap<>();
		/**
		 * The list of collecting symbols, each one has its own directory.
		 */
		public HashMap<String, String> collectingMap = new HashMap<>();

		/**
		 * These are the same scheduling array which was in the normal cache
		 * version
		 */
		public ArrayList<String> schedulings = new ArrayList<>();

		public HashMap<String, CSVData> csvData = new HashMap<>();

	}

	/**
	 * This class holds the data which will be saved in the cache.
	 * <p>
	 * It is the root of the xml tree which is stored in the file (from version
	 * 2 of the cache).
	 * 
	 * @author Sergio
	 * 
	 */
	private static final class CacheXmlRepresentation {
		public int version;

		public HashMap<String, SymbolData> symbolMap;
		public HashMap<String, SymbolData> collectingMap;
		public ArrayList<String> schedulings;

		public CacheXmlRepresentation() {
			// void
		}

		/**
		 * Very dummy method to adjust the nulls inside the restored cache, some
		 * nulls may come from one version to another.
		 */
		public void adjustNulls() {
			if (collectingMap == null) {
				collectingMap = new HashMap<>();
			}

		}

		// other data may follow.
	}

	/**
	 * Just a tuple used to
	 * 
	 * @author Sergio
	 * 
	 */
	public static final class GetSymbolDataAns extends
			U.Tuple<ParseMaturityAns, BaseSymbolData> {
		// nothing.
	}

	/**
	 * This value will be incremented every time the cache changes in a not
	 * compatible way.
	 * <p>
	 * The name of the cache is "cache_ver_$CURRENT_CACHE_VERSION".
	 */
	private static final int CURRENT_CACHE_VERSION = 8;

	// private static final int MINIMUM_SUPPORTED_CACHE_VERSION = 8;

	private static final String CACHE_TIME_FORMAT = "HH:mm:ss";

	private static final int CURRENT_MASTER_CACHE_VERSION = 2;

	private static final int MINIMUM_SUPPORTED_MASTER_CACHE_VERSION = 1;

	/**
	 * This is a static method used to erase all the cache inside. (mainly used
	 * as a test tool for the cache itself)
	 */
	public static void eraseCache() {
		String fileCacheName = getCacheFileName(1, "");
		debug_var(393913, "deleting the file name...", fileCacheName);
		new File(fileCacheName).delete();
	}

	private static String getCacheFileName(int version, String subRootDir) {
		String relFileName = "cache";

		relFileName += "_ver_" + version;

		File subDir;
		if (subRootDir.length() != 0) {
			subDir = new File(MfgMdbSession.getInstance().getSessionRoot(),
					subRootDir);
		} else {
			subDir = MfgMdbSession.getInstance().getSessionRoot();
		}

		subDir.mkdirs();

		String fileName = new File(subDir, relFileName).getAbsolutePath();
		return fileName;
	}

	/**
	 * This is the second symbol data which will be used to store the symbol
	 * data.
	 * <p>
	 * The former hash map will be then deleted, because not more used.
	 * <p>
	 * For now I start to store the symbol data in another map, just to let the
	 * software be prepared for the change little by little.
	 * 
	 * <p>
	 * The key of the map is the prefix of the symbol. In case the symbol is
	 * "concrete" (that is: without maturities) than the SymbolData holds the
	 * data directly. Otherwise the SymbolData itself is only a container of
	 * different symbols and maturities.
	 * 
	 * <p>
	 * In this latter case the symbol string is only the prefix, like "@ES". The
	 * class will then contain all the data for the different maturities.
	 * 
	 */
	private HashMap<String, SymbolData> _symbols;

	/**
	 * This map holds all the csv files encoded in mdb which are present in the
	 * system. The data is stored in an ad hoc class, but the real data is
	 * inside a simple MDB file.
	 */
	private HashMap<String, CSVData> _csvStoredData;

	/**
	 * This is the map which holds the symbols which are still collecting data.
	 * They are not displayed to the outside until the initial data has been
	 * collected.
	 */
	private HashMap<String, SymbolData> _collectingSymbols;

	/**
	 * This is the array of scheduled dates which are used to wake up the data
	 * model.
	 * <p>
	 * The data model will wake up for 3 different cases:
	 * 
	 * <li>1. if the application is started
	 * <li>2. if the time is equal to one of the scheduled dates
	 * <li>3. if an open view is on a table.
	 */
	private ArrayList<Date> _scheduledDates;

	/**
	 * This is a string used to build the cache name, it has not other use
	 * whatsoever.
	 * 
	 * <p>
	 * It is not even stored in the file (because it is <b>part</b> of the file
	 * name)
	 */
	// private final transient String _suffix;

	private final transient IDataFeed _feed;

	private transient XStream xstream;

	/**
	 * This stores the most recent scheduler which has fired: it is used to
	 * avoid a double firing
	 */
	private int _firedScheduler = -1; // at first no scheduled event is fired.

	private AtomicBoolean _manualScheduleFlagRaised = new AtomicBoolean();

	/**
	 * 
	 * Creates the object. The object tries always to get the state from the
	 * serialized version of the same object.
	 * 
	 * <p>
	 * The only part which is serialized is the map of all the symbols in the
	 * database. This map is for now stored as a serialized representation, but
	 * this may change in the future to accomodate several symbols and also to
	 * have a better transaction support.
	 * 
	 * <p>
	 * The object contains the session of all the tables in MDB. This session is
	 * not serialized, of course, because MDB files handle the serialized data
	 * in their own way.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws DFSException
	 */
	public DfsCacheRepo(IDataFeed aFeed) throws IOException,
			ClassNotFoundException, DFSException {
		_feed = aFeed;
		_initXStreamBackend();
		boolean read = false;
		for (int tryVersion = CURRENT_MASTER_CACHE_VERSION; tryVersion >= MINIMUM_SUPPORTED_MASTER_CACHE_VERSION; --tryVersion) {
			if (_loadStoredCache(tryVersion)) {
				read = true;
				break;
			}
		}
		if (!read) {
			debug_var(399331, "NO CACHE, I start again with a void map");
			_initMaps();
			MfgMdbSession.useSubdirs();
		}
	}

	/**
	 * this is just a private method used to get the symbol data in a safe way.
	 * 
	 * @param symbol
	 * @return
	 * @throws DFSException
	 */
	public GetSymbolDataAns getSymbolDataSafe(String symbol)
			throws DFSException {
		GetSymbolDataAns gsda = new GetSymbolDataAns();

		gsda.f1 = Maturity.parseMaturity(symbol);

		// Let's see if I have the data for this suffix
		SymbolData sd = _symbols.get(gsda.f1.unparsedString);

		if (sd == null) {
			/*
			 * Maybe it is a csv symbol
			 */
			CSVData data = _csvStoredData.get(gsda.f1.unparsedString);
			if (data == null) {
				throw new DFSException("Cannot find symbol "
						+ gsda.f1.unparsedString);
			}

			gsda.f2 = data;

		} else {
			gsda.f2 = sd;
		}

		return gsda;
	}

	/**
	 * simple method to init the backend; it will be used for all the versions
	 * of the cache.
	 */
	private void _initXStreamBackend() {
		xstream = new XStream();
		xstream.alias("symbol-data", SymbolData.class);
		xstream.alias("maturity", Maturity.class);
		xstream.alias("maturity-data", MaturityData.class);
		xstream.alias("bar-type", BarType.class);
		xstream.alias("range-history-table", RangeHistoryTable.class);
		xstream.alias("time-history-table", TimeHistoryTable.class);
		xstream.alias("DfsCache", CacheXmlRepresentation.class);
		xstream.alias("chunk", ContinuousTable.ContinuousChunk2.class);
		xstream.alias("contTable", ContinuousTable.class);
		xstream.alias("crossover", CrossoverData.class);
		xstream.alias("MasterCache", MasterCacheRepresentation.class);
		xstream.alias("Tick", com.mfg.common.Tick.class);
		xstream.alias("CsvFile", com.mfg.dfs.data.CSVData.class);
		xstream.alias("CsvTimeTable",
				com.mfg.dfs.data.CsvTimeHistoryTable.class);
		xstream.alias("CsvRangeTable",
				com.mfg.dfs.data.CsvRangeHistoryTable.class);

		xstream.registerConverter(new ReflectionConverter(xstream.getMapper(),
				xstream.getReflectionProvider()) {
			@Override
			public boolean canConvert(Class type) {
				return (type == TimeHistoryTable.class)
						|| (type == RangeHistoryTable.class)
						|| (type == ContinuousTable.class);
			}
		});
	}

	/**
	 * This helper method will load the stored cache.
	 * <p>
	 * It will start from the current version backwards, maybe the application
	 * will not be able to load the cache from a minimum version, so this should
	 * be addressed with some other means (for example an exception)
	 * 
	 * @param tryVersion
	 *            the version to try to load.
	 * 
	 * @return false if the cache cannot be found.
	 * @throws DFSException
	 *             if the cache is good but maybe corrupt.
	 */
	@SuppressWarnings("boxing")
	private boolean _loadStoredCache(int tryVersion) throws DFSException {

		String masterCacheName = getMasterCacheFileName(tryVersion);
		debug_var(391033, "checking master storage cache @ ", masterCacheName);
		long then = System.currentTimeMillis();

		File masterCheck = new File(masterCacheName);
		if (masterCheck.exists() && masterCheck.canRead()) {
			debug_var(839281, "OK, the master file ", masterCacheName,
					" exists and can be read");

			_initMaps();
			MfgMdbSession.useSubdirs();

			/*
			 * I have to read all the single caches...
			 */
			MasterCacheRepresentation mcxr = (MasterCacheRepresentation) xstream
					.fromXML(new File(masterCacheName));

			// this._symbols = new HashMap<>();

			for (Entry<String, String> entry : mcxr.symbols.entrySet()) {
				U.debug_var(293158, "adding the symbol ", entry.getKey(),
						" with prefix ", entry.getValue());

				CacheXmlRepresentation singleCache = _readSingleCache(entry
						.getValue());

				for (Entry<String, SymbolData> aVal : singleCache.symbolMap
						.entrySet()) {
					this._symbols.put(aVal.getKey(), aVal.getValue());
				}
			}

			for (Entry<String, String> collectingSymbol : mcxr.collectingMap
					.entrySet()) {
				U.debug_var(293158, "adding the collecting symbol ",
						collectingSymbol.getKey(), " with prefix ",
						collectingSymbol.getValue());

				CacheXmlRepresentation singleCache = _readSingleCache(collectingSymbol
						.getValue());

				for (Entry<String, SymbolData> aVal : singleCache.symbolMap
						.entrySet()) {
					this._collectingSymbols.put(aVal.getKey(), aVal.getValue());
				}
			}

			for (Entry<String, CSVData> entry : mcxr.csvData.entrySet()) {
				U.debug_var(299151, "Adding the csv ", entry.getKey());
				this._csvStoredData.put(entry.getKey(), entry.getValue());
				try {
					entry.getValue().createCache();
				} catch (IOException e) {
					throw new DFSException("Cannot find csv data "
							+ entry.getKey());
				}
			}

			// Now I try to fix the schedulings...
			_scheduledDates = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
			for (String st : mcxr.schedulings) {
				Date aDate = null;
				try {
					aDate = sdf.parse(st);
					_scheduledDates.add(aDate);
				} catch (ParseException e) {
					throw new DFSException(e);
				}
				debug_var(381934, "Scheduled the update @ ", aDate);
			}

			long delta = System.currentTimeMillis() - then;
			U.debug_var(291059, "Done reading the cache in ", delta, " ms");
			return true;
		}
		return false;
	}

	/**
	 * Just a simple helper method.
	 * 
	 * <p>
	 * When the cache will read only the master cache maybe this method is not
	 * used any more (only in the case the master cache is not existend and we
	 * have to start with a void map).
	 */
	private void _initMaps() {
		_symbols = new HashMap<>();
		_scheduledDates = new ArrayList<>();
		_collectingSymbols = new HashMap<>();
		_csvStoredData = new HashMap<>();
	}

	/**
	 * read a sngle cache without interpreting it, this method is used either to
	 * load a single cache or to load multiple caches.
	 * 
	 * <p>
	 * in this way the cache is used as a simple record to retrieve the symbol
	 * map, which is added later to the "master" database.
	 * 
	 * @return
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private CacheXmlRepresentation _readSingleCache(String subRootDir)
			throws DFSException {
		// for (int cacheVersion = CURRENT_CACHE_VERSION; cacheVersion >=
		// MINIMUM_SUPPORTED_CACHE_VERSION; --cacheVersion) {
		String fileName = getCacheFileName(CURRENT_CACHE_VERSION, subRootDir);
		debug_var(391033, "checking storage cache @ ", fileName);

		File check = new File(fileName);
		if (check.exists() && check.canRead()) {
			debug_var(839281, "OK, the file ", fileName,
					" exists and can be read! the version is ",
					CURRENT_CACHE_VERSION);

			CacheXmlRepresentation cxr = (CacheXmlRepresentation) xstream
					.fromXML(new File(fileName));
			if (cxr.version != CURRENT_CACHE_VERSION) {
				throw new DFSException("invalid version check");
			}

			cxr.adjustNulls();

			return cxr;

			// switch (cacheVersion) {
			// case 7:
			// case 8:
			//
			// default:
			// throw new DFSException("invalid version, or unsupported... "
			// + cacheVersion);
			// }
		}
		// }

		return null;
	}

	// @SuppressWarnings("boxing")
	// private boolean _loadSingleCache(String subRootDir) throws DFSException {
	// // for (int cacheVersion = CURRENT_CACHE_VERSION; cacheVersion >=
	// // MINIMUM_SUPPORTED_CACHE_VERSION; --cacheVersion) {
	// String fileName = getCacheFileName(CURRENT_CACHE_VERSION, subRootDir);
	// debug_var(391033, "checking storage cache @ ", fileName);
	//
	// File check = new File(fileName);
	// if (check.exists() && check.canRead()) {
	// debug_var(839281, "OK, the file ", fileName,
	// " exists and can be read! the version is ",
	// CURRENT_CACHE_VERSION);
	//
	// CacheXmlRepresentation cxr = (CacheXmlRepresentation) xstream
	// .fromXML(new File(fileName));
	// if (cxr.version != CURRENT_CACHE_VERSION) {
	// throw new DFSException("invalid version check");
	// }
	//
	// cxr.adjustNulls();
	//
	// this._symbols = cxr.symbolMap;
	// this._collectingSymbols = cxr.collectingMap;
	//
	// // Now I try to fix the schedulings...
	// _scheduledDates = new ArrayList<>();
	// SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
	// for (String st : cxr.schedulings) {
	// Date aDate = null;
	// try {
	// aDate = sdf.parse(st);
	// _scheduledDates.add(aDate);
	// } catch (ParseException e) {
	// throw new DFSException(e);
	// }
	// debug_var(381934, "Scheduled the update @ ", aDate);
	// }
	//
	// return true;
	//
	// // switch (cacheVersion) {
	// // case 7:
	// // case 8:
	// //
	// // default:
	// // throw new DFSException("invalid version, or unsupported... "
	// // + cacheVersion);
	// // }
	// }
	// // }
	//
	// return false;
	// }

	/**
	 * Adds the given symbol to the cache.
	 * 
	 * <p>
	 * It does not yet download the data for this symbol, it will be downloaded
	 * only by demand when the scheduler is run for the first time.
	 * 
	 * @param prefix
	 * @param completeName
	 * @param tick
	 * @param scale
	 * @param aTimeZone
	 * @return
	 */
	public DfsSymbol addSymbol(String prefix, String completeName, int tick,
			int scale, int aTickValue, String aTimeZone, String aCurrency,
			String aType) {
		DfsSymbol sym = new DfsSymbol(prefix, completeName, tick, scale,
				aTickValue, aTimeZone, aCurrency, aType);
		SymbolData sd = new SymbolData(sym);
		_collectingSymbols.put(prefix, sd);
		return sym;
	}

	/**
	 * imports Csv formatted data inside DFS encoding it in the normal MDB
	 * encoding system, either range or time bar data.
	 * 
	 * @param aData
	 *            a stream which must be already opened to the start of the Csv
	 *            data. It is assumed that this stream is a text stream encoded
	 *            in "latin 1" charset.
	 * @param aName
	 *            the name with which this csv data must be known from the
	 *            outside.
	 * @return the newly added symbol, if successful.
	 * @throws DFSException
	 */
	public DfsSymbol importCsvData(InputStream aData, String aName)
			throws DFSException {
		if (getStatusForSymbol(aName, false) != null) {
			throw new DFSException("Duplicate symbol " + aName);
		}

		CSVData csvData = CSVData.createCsvData(aData, aName);
		_csvStoredData.put(aName, csvData);
		return csvData.getSymbol();
	}

	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(symbol);
		return gsda.f2.getBarCount(gsda.f1.parsedMaturity, aType, barWidth);
	}

	/**
	 * @param aType
	 *            the type used to query the database.
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(symbol);
		return gsda.f2.getBarsBetween(gsda.f1.parsedMaturity, aType, barWidth,
				startDate, endDate);
	}

	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(symbol);
		return gsda.f2.getDateAfterXBarsFrom(gsda.f1.parsedMaturity, aType,
				barWidth, startDate, numBars);
	}

	/**
	 * @param endTime
	 * @throws DFSException
	 */
	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {

		GetSymbolDataAns gsda = getSymbolDataSafe(symbol);
		return gsda.f2.getDateBeforeXBarsFrom(gsda.f1.parsedMaturity, aType,
				barWidth, endTime, numBars);
	}

	/**
	 * gets the scale for a symbol. This is used for now by the multiserver to
	 * convert the price from a string to an integer
	 * 
	 * 
	 * @param symbol
	 *            the complete symbol
	 * @return the scale of this symbol
	 * @throws DFSException
	 *             if the symbol is not found (strange, because you should have
	 *             it already in cache)
	 */
	public int getScaleForSymbol(String symbol) throws DFSException {
		ParseMaturityAns pma = Maturity.parseMaturity(symbol);
		SymbolData sd = _symbols.get(pma.unparsedString);
		if (sd == null) {
			throw new DFSException("cannot find symbol " + symbol);
		}
		return sd.getSymbol().scale;
	}

	public DfsSchedulingTimes getSchedulingTimes() {
		DfsSchedulingTimes dst = new DfsSchedulingTimes();
		dst.schedulings = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
		for (Date aDate : _scheduledDates) {
			String formatted = sdf.format(aDate);
			dst.schedulings.add(formatted);
		}
		return dst;
	}

	/**
	 * creates the symbol status for the prefix symbol
	 * 
	 * @param symbol
	 *            it is actually a prefix!
	 * 
	 * @return the symbol status for this symbol, null if this symbol is not yet
	 *         in cache.
	 * @throws DFSException
	 */
	public DfsSymbolStatus getStatusForSymbol(String symbol, boolean forceCheck)
			throws DFSException {
		BaseSymbolData sd = _symbols.get(symbol);
		if (sd == null) {
			sd = _collectingSymbols.get(symbol);
			if (sd == null)
				sd = _csvStoredData.get(symbol);
			if (sd == null)
				return null;
		}

		return sd.getStatus(forceCheck);
	}

	public DfsSymbolList getSymbolsList() {
		DfsSymbolList dsl = new DfsSymbolList();
		ArrayList<DfsSymbol> res = new ArrayList<>();
		dsl.symbols = res;
		for (SymbolData symbol : _symbols.values()) {
			res.add(symbol.getSymbol());
		}
		res = new ArrayList<>();
		dsl.csvSymbols = res;
		for (CSVData csvData : _csvStoredData.values()) {
			res.add(csvData.getSymbol());
		}
		return dsl;
	}

	/**
	 * Method created to be used by DFS symbols navigator. This return the
	 * symbols was added but does not contain data yet.
	 * 
	 * @author arian
	 * 
	 * @return An array with a prefix of the symbols
	 */
	public String[] getCollectingSymbols() {
		Set<String> keySet = _collectingSymbols.keySet();
		return keySet.toArray(new String[keySet.size()]);
	}

	/**
	 * this method will simply raise the flag used to
	 */
	public void manualScheduling() {
		_manualScheduleFlagRaised.set(true);
	}

	/**
	 * This method only removes the symbol from the xml cache.
	 * <p>
	 * It does not remove the mdb files, they are too precious and perhaps it is
	 * better to remove them <b>ONLY</b> from a direct deleting from the file
	 * system
	 * 
	 * <p>
	 * The method fails silently if the symbol does not exist.
	 * 
	 * @param prefix
	 *            the prefix to be removed.
	 */
	public void removeSymbol(@NotNull String prefix) {
		if (_symbols.containsKey(prefix)) {
			_symbols.remove(prefix);
			debug_var(399332, "Symbol ", prefix, " successfuly removed");
		}
	}

	public IBarCache returnCache(RequestParams aReq) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(aReq.getSymbol());

		return gsda.f2.returnCache(gsda.f1.parsedMaturity, aReq);
	}

	public IBarCache returnCache(String prefixSymbol, Maturity aMaturity,
			BarType aType, int nUnits) throws DFSException {
		SymbolData sd = _symbols.get(prefixSymbol);
		if (sd == null) {
			return null; // nothing
		}
		return sd.getCache(aMaturity, aType, nUnits);
	}

	/**
	 * helper method which is used to save the cache with a particular version.
	 * <p>
	 * The cache is saved completely, with all the symbols, this was before the
	 * 
	 * @param subRootDir
	 * 
	 * @param version
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void saveCache(int aVersion, String subRootDir)
			throws FileNotFoundException, IOException {
		String filename = getCacheFileName(aVersion, subRootDir);
		debug_var(391334, "Storing the cache @ ", filename);

		File cacheFile = new File(filename);

		if (aVersion < CURRENT_CACHE_VERSION) {
			throw new IllegalStateException("I refuse to save version "
					+ aVersion);
		}

		try (FileOutputStream fos = new FileOutputStream(cacheFile);) {

			CacheXmlRepresentation cxr = new CacheXmlRepresentation();
			cxr.version = aVersion;
			cxr.symbolMap = _symbols;
			cxr.collectingMap = _collectingSymbols;
			cxr.schedulings = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
			for (Date aDate : _scheduledDates) {
				String formatted = sdf.format(aDate);
				cxr.schedulings.add(formatted);
			}
			xstream.toXML(cxr, fos);

		}
	}

	/**
	 * Sets the scheduling times.
	 * 
	 * <p>
	 * the scheduling is
	 * 
	 * @param aSchedulingTimes
	 * @throws DFSException
	 */
	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes)
			throws DFSException {
		ArrayList<Date> nSched = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
		long lastTime = -1;
		for (String st : aSchedulingTimes.schedulings) {
			Date dt;
			try {
				dt = sdf.parse(st);
				if (lastTime != -1 && dt.getTime() <= lastTime) {
					throw new DFSException("times are not in order");
				}
				lastTime = dt.getTime();
				nSched.add(dt);
			} catch (ParseException e) {
				throw new DFSException(e);
			}

		}
		this._scheduledDates = nSched;
	}

	/**
	 * In the stop method the cache is stored. The cache is <b>always</b> stored
	 * at the latest possible version.
	 * <p>
	 * This is done on purpose, as the program is able to at least read the
	 * latest two versions of the cache.
	 * 
	 * @param isOffline
	 * 
	 * @throws IOException
	 */
	public void stop(boolean isOffline) throws IOException {

		if (!isOffline) {
			_saveMasterCache(CURRENT_MASTER_CACHE_VERSION);
			if (MfgMdbSession.isUsingSubdirs()) {
				for (Entry<String, SymbolData> symbol : _symbols.entrySet()) {
					saveSingleCache(CURRENT_CACHE_VERSION, symbol.getKey(),
							symbol.getValue());
				}

				for (Entry<String, SymbolData> symbol : _collectingSymbols
						.entrySet()) {
					saveSingleCache(CURRENT_CACHE_VERSION, symbol.getKey(),
							symbol.getValue());
				}

			} else {
				saveCache(CURRENT_CACHE_VERSION, "");
			}

		}

		/*
		 * I close all the symbols in cache, they may have some data left to be
		 * flushed.
		 */
		for (SymbolData sic : _symbols.values()) {
			sic.close();
		}
		// I have also to close the session
		try {
			MfgMdbSession.getInstance().close();
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		debug_var(399193, "Closed successfully the session");
	}

	private void saveSingleCache(int aVersion, String symbol, SymbolData aData)
			throws FileNotFoundException, IOException {

		String subRootDir = MfgMdbSession.purgeSymbol(symbol);
		String filename = getCacheFileName(aVersion, subRootDir);
		debug_var(391334, "Storing the cache @ ", filename, " for symbol ",
				symbol);

		File cacheFile = new File(filename);

		if (aVersion < CURRENT_CACHE_VERSION) {
			throw new IllegalStateException("I refuse to save version "
					+ aVersion);
		}

		try (FileOutputStream fos = new FileOutputStream(cacheFile);) {
			CacheXmlRepresentation cxr = new CacheXmlRepresentation();
			cxr.version = aVersion;
			cxr.symbolMap = new HashMap<>();
			cxr.collectingMap = new HashMap<>();
			cxr.schedulings = new ArrayList<>();
			cxr.symbolMap.put(symbol, aData);
			xstream.toXML(cxr, fos);
		}
	}

	/**
	 * A simple method which will save the master cache.
	 * 
	 * <p>
	 * The master cache does not hold data, it is only a list of all the symbols
	 * which are stored inside the cache itself
	 * 
	 * @param version
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void _saveMasterCache(int aVersion) throws FileNotFoundException,
			IOException {
		String filename = getMasterCacheFileName(aVersion);
		debug_var(391334, "Storing the cache @ ", filename);

		File cacheFile = new File(filename);

		if (aVersion < CURRENT_MASTER_CACHE_VERSION) {
			throw new IllegalStateException("I refuse to save version "
					+ aVersion);
		}

		try (FileOutputStream fos = new FileOutputStream(cacheFile);) {

			MasterCacheRepresentation mcxr = new MasterCacheRepresentation();
			mcxr.version = aVersion;

			/*
			 * Now the maps, first the symbol map and later the collecting
			 * symbol map.
			 */
			for (Entry<String, SymbolData> symbol : this._symbols.entrySet()) {
				mcxr.symbols.put(symbol.getKey(),
						MfgMdbSession.purgeSymbol(symbol.getKey()));
			}

			for (Entry<String, SymbolData> symbol : this._collectingSymbols
					.entrySet()) {
				mcxr.collectingMap.put(symbol.getKey(),
						MfgMdbSession.purgeSymbol(symbol.getKey()));
			}

			for (Entry<String, CSVData> csv : this._csvStoredData.entrySet()) {
				mcxr.csvData.put(csv.getKey(), csv.getValue());
			}

			mcxr.schedulings = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat(CACHE_TIME_FORMAT);
			for (Date aDate : _scheduledDates) {
				String formatted = sdf.format(aDate);
				mcxr.schedulings.add(formatted);
			}
			xstream.toXML(mcxr, fos);

		}

	}

	private static String getMasterCacheFileName(int aVersion) {
		String relFileName = "MasterCache";

		relFileName += "_ver_" + aVersion;

		String fileName = new File(
				MfgMdbSession.getInstance().getSessionRoot(), relFileName)
				.getAbsolutePath();
		return fileName;
	}

	public String translateMfgSymbol(String mfgSymbol) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(mfgSymbol);

		if (gsda.f1.parsedMaturity == null) {
			return gsda.f2.getCurrentSymbol();
		}
		return mfgSymbol;
	}

	public void truncateMaturity(String aSymbol, BarType aType,
			long truncateDate) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(aSymbol);
		gsda.f2.truncateMaturity(gsda.f1.parsedMaturity, aType, truncateDate);

	}

	/**
	 * gives a chance to all the history tables to update themselves.
	 * 
	 * <p>
	 * This method is <i>only</i> called from the data model thread, because in
	 * this way we have the possibility to update all the tables.
	 * 
	 * @param symbolToUpdateList
	 * 
	 * @return true if there is at least one active view to satisfy.
	 * @throws IOException
	 * @throws DFSException
	 */
	public void update(ArrayList<String> symbolToUpdateList)
			throws DFSException {

		// to do here.
		boolean isFromScheduler = false;

		Calendar cal = new GregorianCalendar();
		cal.set(1970, 0, 1);

		int scheduledIndex = 0;
		for (Date aDate : _scheduledDates) {
			long delta_sec = (cal.getTimeInMillis() - aDate.getTime()) / 1000;
			if (delta_sec > 0 && delta_sec < 60) {
				if (_firedScheduler != scheduledIndex) {
					LogManager.getInstance().INFO(
							"Activated the automatic scheduling for time "
									+ aDate);
					isFromScheduler = true;
					_firedScheduler = scheduledIndex;
				}
			}
			scheduledIndex++;
		}

		if (_manualScheduleFlagRaised.getAndSet(false)) {
			isFromScheduler = true;
		}

		if (isFromScheduler) {
			ServerFactory.getRealService().onSchedulerStartRunning();
		}

		for (SymbolData sic : _symbols.values()) {
			if (symbolToUpdateList.size() != 0) {
				/*
				 * The list is of length zero or one, I have used a list only to
				 * have an object to synchronize to. If this assert fail you
				 * have to check the method refreshSynchSymbol in the DataModel
				 * class.
				 */
				assert (symbolToUpdateList.size() == 1);
				if (!sic.getSymbol().prefix.equals(symbolToUpdateList.get(0))) {
					continue;
				}
			}

			try {
				sic.doOneStep(_feed, isFromScheduler);
			} catch (IOException e) {
				throw new DFSException(e);
			}
		}

		if (!isFromScheduler) {
			return;
		}

		if (!_feed.isConnected()) {
			LogManager.getInstance().ERROR(
					"FEED is not connected, scheduler is not possible.");
		} else {
			ArrayList<String> toRemove = new ArrayList<>();
			for (SymbolData sic : _collectingSymbols.values()) {
				try {
					while (!sic.isReady()) {
						debug_var(199359,
								"Stepping into the initialize of symbol ",
								sic.getSymbol());
						sic.initialize(_feed);
					}
					toRemove.add(sic.getSymbol().prefix);
					_symbols.put(sic.getSymbol().prefix, sic);

					ServerFactory.getRealService().onSymbolInitializationEnded(
							sic.getSymbol().prefix);

					LogManager.getInstance().INFO(
							"Initialization ended for symbol "
									+ sic.getSymbol());

				} catch (IOException e) {
					throw new DFSException(e);
				}
			}

			for (String sd : toRemove) {
				SymbolData removed = _collectingSymbols.remove(sd);
				/*
				 * If this fails you are not doing right the removal process.
				 */
				assert (removed != null);
			}
		}
		ServerFactory.getRealService().onSchedulerEndedCycle();

	}

	public void watchSymbol(String symbol, IDatabaseChangeListener aListener)
			throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(symbol);

		if (gsda.f2 instanceof CSVData) {
			/*
			 * Actually I might watch a csv symbol, it will not modify itself!
			 */
			throw new DFSException("cannot watch a CSV symbol!");
		}

		/*
		 * Ok this is not a csv symbol so I may watch it
		 */
		gsda.f2.watchMaturity(gsda.f1.parsedMaturity, aListener);
	}

	public void unwatchSymbol(String aSymbol) throws DFSException {
		GetSymbolDataAns gsda = getSymbolDataSafe(aSymbol);

		if (gsda.f2 instanceof CSVData) {
			/*
			 * Actually I might watch a csv symbol, it will not modify itself!
			 */
			throw new DFSException("cannot unwatch a CSV symbol!");
		}

		/*
		 * Ok this is not a csv symbol so I may watch it
		 */
		gsda.f2.unwatchMaturity(gsda.f1.parsedMaturity);

	}

}
