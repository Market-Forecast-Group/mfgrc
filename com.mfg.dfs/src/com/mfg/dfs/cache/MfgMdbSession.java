package com.mfg.dfs.cache;

import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.mfg.mdb.runtime.SessionMode;

import com.mfg.common.BarType;
import com.mfg.common.Maturity;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.dfs.misc.SingleRangeBarMDB;
import com.mfg.dfs.misc.SingleTimeBarMDB;
import com.mfg.dfs.serv.RangeBarsMDB;
import com.mfg.dfs.serv.TimeBarsMDB;
import com.mfg.dfs.serv.dfsdbMDBSession;

/**
 * This object will make a bridge between mfg and the mdb database.
 * 
 * <p>
 * This object is used to store the session and to give a starting root
 * directory for the session itself.
 * 
 * <p>
 * This object is then used to store the session and to give to other objects
 * some utility methods to open an mdb database (cache).
 * 
 * <p>
 * This class is a singleton because we have only one session in each Virtual
 * Machine.
 * 
 * @author Sergio
 * 
 */
public class MfgMdbSession {

	private static final String CSV_COMPILED_FOLDER = "Csv";

	// this is the singleton
	private static MfgMdbSession _instance = null;

	/**
	 * if this flag is set then the instance cannot be created unless the root
	 * is set from the outside.
	 */
	private static AtomicBoolean _mustWaitForRoot = new AtomicBoolean(false);

	/**
	 * the session root is defaulted to an empty string, it may be changed from
	 * the outside, to connect to diff
	 */
	private static String _sessionRoot = "";

	private static boolean _isFakeRoot = false;

	/**
	 * The prefix is pre-added to ".dfsStorage" to create the directory.
	 */
	private static String _dfsPrefix = "";

	/**
	 * If true this flag will tell the system to search for the symbols files
	 * (MDB) in subdirs with the name equal to the symbol prefix (purged)
	 */
	private static boolean _useSubdirs = false;

	private static final Pattern _ourPattern = Pattern.compile("\\W");

	/**
	 * sets from the outside, it is only meaningful if you call it before
	 * {@link #getInstance()}.
	 * 
	 * @param aFakeRoot
	 */
	public static void setFakeRoot(boolean aFakeRoot) {
		_isFakeRoot = aFakeRoot;
	}

	/**
	 * sets the session root.
	 * <p>
	 * It is changed only before the instance is created.
	 * 
	 * @param root
	 *            the new root.
	 */
	public static void setSessionRoot(String root) {
		_sessionRoot = root;
		_mustWaitForRoot.set(false);
		debug_var(381939, "Setting root to ", root);
	}

	/**
	 * returns the instance of the session.
	 * 
	 * <p>
	 * If the session cannot be opened for some reasons it returns null.
	 * 
	 * @return
	 */
	public static synchronized MfgMdbSession getInstance() {
		if (_instance == null) {

			if (_mustWaitForRoot.get()) {

				try {
					synchronized (_mustWaitForRoot) {
						while (true) {
							debug_var(391934,
									"must wait for root... please hold.");
							_mustWaitForRoot.wait(3000);
							if (_mustWaitForRoot.get() == false)
								break;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				_instance = new MfgMdbSession(_isFakeRoot);
			} catch (IOException e) {
				e.printStackTrace(); // I will return null.
			}
		}
		return _instance;
	}

	private File _cacheDir;

	/**
	 * Uncallable constructor from the outside, the session is only created by
	 * the singleton constructor.
	 * 
	 * @throws IOException
	 */
	private MfgMdbSession(boolean isFakeRoot) throws IOException {
		if (_sessionRoot.length() == 0) {
			_sessionRoot = System.getProperty("user.home");
		}
		_rootOfDfs = new File(_sessionRoot, isFakeRoot ? "DFS_FAKE_STORAGE"
				: _dfsPrefix + ".dfsStorage");

		_rootOfDfs.mkdirs();

		// I also make the temp directory inside the cache
		_cacheDir = new File(_rootOfDfs, "cache");
		_cacheDir.mkdirs();

		_session = new dfsdbMDBSession("dfsSession", _rootOfDfs,
				SessionMode.READ_WRITE);
	}

	/**
	 * This is the session which is inside the
	 */
	private dfsdbMDBSession _session;

	/**
	 * This is the root for all the databases in the system and also for the
	 * cache database
	 */
	private File _rootOfDfs;

	/**
	 * returns the root of the session which could be used to build the cache
	 * name (used to store the serialized version of the cache).
	 * 
	 * @return the session root
	 */
	public File getSessionRoot() {
		return _rootOfDfs;
	}

	/**
	 * Creates a range bar MDB file in the dfs root.
	 * 
	 * @param name
	 * @param aSymbolPrefix
	 * @return
	 * @throws IOException
	 */
	private RangeBarsMDB getRangeBarsMdb(String aSymbolPrefix, String name)
			throws IOException {
		File completeFile = _getCompleteFileName(aSymbolPrefix, name);
		return _session.connectTo_RangeBarsMDB(completeFile.getPath());
	}

	private TimeBarsMDB getTimeBarsMdb(String aSymbolPrefix, String name)
			throws IOException {

		File completeFile = _getCompleteFileName(aSymbolPrefix, name);
		return _session.connectTo_TimeBarsMDB(completeFile.getPath());
	}

	private File _getCompleteFileName(String aSymbolPrefix, String name) {
		File completeFile;
		if (_useSubdirs) {
			completeFile = new File((File) null, purgeSymbol(aSymbolPrefix));
			completeFile = new File(completeFile, name);
		} else {
			completeFile = new File(_rootOfDfs, name);
		}
		return completeFile;
	}

	@SuppressWarnings("unchecked")
	private ICache<DfsBar> getRangeBarsCache(String aSymbolPrefix, String name)
			throws IOException {
		RangeBarsMDB db = getRangeBarsMdb(aSymbolPrefix, name);
		return new SingleRangeBarMDB(db);
	}

	@SuppressWarnings("unchecked")
	private ICache<DfsBar> getTimeBarsCache(String aSymbolPrefix, String name)
			throws IOException {
		TimeBarsMDB db = getTimeBarsMdb(aSymbolPrefix, name);
		return new SingleTimeBarMDB(db);
	}

	/**
	 * Gets the forward cache from the symbol, maturity and bar type
	 * 
	 * @param symbol
	 * @param aMaturity
	 * @param aType
	 * @return
	 * @throws IOException
	 */
	public ICache<DfsBar> getTimeBarsCache(String symbol, Maturity aMaturity,
			BarType aType) throws IOException {
		String name = getCacheKey(symbol, aMaturity, aType);
		return getTimeBarsCache(symbol, name);
	}

	/**
	 * gets the forward range bar cache.
	 * 
	 * @param symbol
	 * @param aMaturity
	 * @return
	 * @throws IOException
	 */
	public ICache<DfsBar> getRangeBarCache(String symbol, Maturity aMaturity)
			throws IOException {
		String name = getCacheKey(symbol, aMaturity, BarType.RANGE);
		return getRangeBarsCache(symbol, name);
	}

	/**
	 * Gets the MDB database associated with a csv file composed of range bars.
	 * 
	 * @param name
	 *            the simple name, no path
	 * @return the opened database
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public ICache<DfsBar> getCsvRangeBarCache(String name) throws IOException {
		File completeFile = new File((File) null, CSV_COMPILED_FOLDER);
		completeFile = new File(completeFile, name);

		RangeBarsMDB mdb = _session.connectTo_RangeBarsMDB(completeFile
				.getPath());
		return new SingleRangeBarMDB(mdb);
	}

	/**
	 * returns the file representing the cache given a symbol, maturity, type
	 * and barWidth.
	 * 
	 * <p>
	 * The file has already the absolute path fixed in the cache dir.
	 * 
	 * @param symbol
	 * @param aMaturity
	 * @param aType
	 * @param barWidth
	 * @return
	 */
	public File getMultipleCacheKeyFile(String symbol, Maturity aMaturity,
			BarType aType, int barWidth) {
		String symNew = symbol.replaceAll("\\W", "X");
		String intervalNew = aType.toString();
		// the maturity can be null because of the continuous contract
		String maturityString = aMaturity == null ? "_cont_" : aMaturity
				.toFileString();

		return new File(_cacheDir, symNew + "-" + maturityString + "-"
				+ intervalNew + "-" + barWidth);
	}

	public static String getCacheKey(String symbol, Maturity aMaturity,
			BarType aType) {
		String symNew = symbol.replaceAll("\\W", "X");
		String intervalNew = aType.toString();
		return symNew + "-" + aMaturity.toFileString() + "-" + intervalNew;
	}

	/**
	 * Closes the session.
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void close() throws IOException, TimeoutException {
		_session.close();
	}

	/**
	 * waits for the root which will be set by an external entity.
	 * 
	 * <p>
	 * This is done because this plugin has not a user interface and it may get
	 * the root from the outside.
	 */
	public synchronized static void waitForTheRoot() {
		if (_instance == null) {
			_mustWaitForRoot.set(true);
		}
	}

	/**
	 * sets the prefix of the dfs folder. Usually it is called ".dfsStorage",
	 * but it can have an arbitrary prefix, for example $symbol.dfsStorage
	 * 
	 * @param prefix
	 *            the prefix
	 */
	public static void setDfsPrefix(String prefix) {
		_dfsPrefix = prefix;
	}

	/**
	 * purges the string from all the not/word characters in order to have a
	 * version suitable for a file name.
	 * 
	 * @param val
	 *            the value to be purged
	 * @return the purged value
	 */
	public static String purgeSymbol(String val) {
		return _ourPattern.matcher(val).replaceAll("X");
	}

	/**
	 * Tells the session to use the subdirs where to store the mdb database
	 * files.
	 */
	public static void useSubdirs() {
		_useSubdirs = true;
	}

	/**
	 * this method for now is only used to
	 * 
	 * @return
	 */
	public static boolean isUsingSubdirs() {
		return _useSubdirs;
	}

	@SuppressWarnings("unchecked")
	public ICache<DfsBar> getCsvTimeBarCache(String name) throws IOException {
		File completeFile = new File((File) null, CSV_COMPILED_FOLDER);
		completeFile = new File(completeFile, name);

		TimeBarsMDB mdb = _session
				.connectTo_TimeBarsMDB(completeFile.getPath());
		return new SingleTimeBarMDB(mdb);
	}

}
