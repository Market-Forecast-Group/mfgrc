package com.mfg.dfs.conn;

import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BAR_TYPE;
import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DFSWarmUpFinishedEvent;
import com.mfg.common.DfsRealSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;
import com.mfg.common.Maturity;
import com.mfg.common.MfgSymbol;
import com.mfg.common.RandomSymbol;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.data.CSVData;
import com.mfg.dfs.data.DfsClientsModel;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.misc.MultiServer;
import com.mfg.dfs.misc.Service;
import com.mfg.dm.DataRequest;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.UnitsType;
import com.mfg.utils.AppLock;
import com.mfg.utils.CmdLineParser.IllegalOptionValueException;
import com.mfg.utils.CmdLineParser.UnknownOptionException;
import com.mfg.utils.U;

/**
 * The server factory is able to build a {@linkplain IDFS} object, either local
 * or remote.
 * 
 * <p>
 * The factory is able to create a local server (used to connect to a local
 * computer, maybe with a unique connection to the real time data provider,
 * etc).
 * 
 * @author Sergio
 * 
 */
public class ServerFactory {

	private static final int SOCKET_PORT = 8999;

	/**
	 * I have only one service in a vm.
	 * <p>
	 * the service can be local <b>or</b> remote, but not <b>both</b>.
	 * 
	 */
	private static IDFS _service = null;

	private static MultiServer _multiserver;
	private static SocketServer _ss = null;

	/**
	 * Command to add a symbol to the cache. This symbol is set into the
	 * "collecting symbols". To get it ready, you should run the scheduler.
	 * 
	 * @param splits
	 *            The command components. An example of command is:
	 * 
	 *            <pre>
	 * ads,@ES,25,2,America/New York,12,EUR,FUT
	 * </pre>
	 */
	private static void _doAddSymbolCommand(String[] splits) {
		if (splits.length < 6) {
			System.out
					.println("I need 6 parameters, prefix, tick, scale, timezone, tick-value, currency, type");
		}
		String aSymbol = splits[1];
		// the tick size
		int tick = Integer.parseInt(splits[2]);
		// the scale of the tick size and tick value
		int scale = Integer.parseInt(splits[3]);
		// for example, America/New York
		String timeZone = splits[4];
		// the tick value is an integer, to get the real tick value, we should
		// use the scale
		int tickValue = Integer.parseInt(splits[5]);
		// one of DfsSymbol.CURRENCY_EUR or DfsSymbol.CURRENCY_USD
		String currency = splits[6];
		// on of DfsSymbol.TYPES
		String type = splits[7];

		DfsCacheRepo cache = _multiserver.getModel().getCache();
		cache.addSymbol(aSymbol, "complete name", tick, scale, tickValue,
				timeZone, currency, type);
		debug_var(301934, "Added symbol ", aSymbol);
	}

	@SuppressWarnings("unused")
	private static void _doCacheCommand(String[] splits, String allTheRestLine) {
		assert (false); // not used any more
		// try {
		//
		// // take parameters, if the length is one then all the parameters
		// // are default
		// // String symbol = "@ESU13";
		// // I have to get the Request param from the line
		// RequestParams reqPar = null;
		//
		// if (splits.length == 1) {
		// reqPar = RequestParams.createRequestLastDailyDays("@ESU13", 30); // a
		// // month
		// } else {
		// reqPar = RequestParams.parse(allTheRestLine);
		// }
		// try (IBarCache cache = _service.requestHistory(reqPar,
		// new DfsBarAsyncImpl(null, null));) {
		// int maxI = cache.size();
		// for (int i = 0; i < maxI; ++i) {
		// Bar aBar = cache.getBar(i);
		// System.out.println("[" + i + "/ " + maxI + "] : "
		// + aBar.toString());
		// }
		// }
		//
		// } catch (DFSException e) {
		// debug_var(839923, "************************* Cannot dump! ");
		// e.printStackTrace();
		// }
	}

	private static void _doDumpCacheCommand(
			@SuppressWarnings("unused") String[] splits) {
		try (IBarCache cache = _service.getCache("@ES", new Maturity(),
				BarType.MINUTE, 1);) {

			int maxI = cache.size();
			for (int i = 0; i < maxI; ++i) {
				Bar aBar = cache.getBar(i);
				System.out.println("[" + i + "/ " + maxI + "] : "
						+ aBar.toString());
			}

		} catch (DFSException e) {
			debug_var(839923, "************************* Cannot dump!");
			e.printStackTrace();
		}
	}

	private static void _doDumpCacheParsCommands(String[] splits) {
		if (splits.length < 4) {
			debug_var(193910, "I need 3 parameters,symbol, bartype, units...");
			return;
		}
		String symbol = splits[1];

		BarType aType = BarType.valueOf(splits[2]);
		int nUnits = Integer.parseInt(splits[3]);
		try (IBarCache cache = _service.getCache(symbol, null, aType, nUnits);) {

			int maxI = cache.size();
			for (int i = 0; i < maxI; ++i) {
				Bar aBar = cache.getBar(i);
				System.out.println("[" + i + "/ " + maxI + "] : "
						+ aBar.toString());
			}

		} catch (DFSException e) {
			debug_var(839923, "************************* Cannot dump!");
			e.printStackTrace();
		}
	}

	private static void _doGetBarsBetweenCommand(String[] splits)
			throws DFSException {

		String symbol = splits[1];
		BarType aType = BarType.valueOf(splits[2]);

		int barWidth = Integer.parseInt(splits[3]);
		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		long start = 0, end = 0;
		try {
			start = sdf.parse(splits[4]).getTime();
			end = sdf.parse(splits[5]).getTime();
		} catch (ParseException e) {

			e.printStackTrace();
		}

		int nBars = _service
				.getBarsBetween(symbol, aType, barWidth, start, end);

		System.out.println(">>>>>>>> there are " + nBars + " bars");

	}

	private static void _doGetDateAfterBeforeCommand(String[] splits)
			throws DFSException {
		String symbol = splits[1];
		BarType aType = BarType.valueOf(splits[2]);

		int barWidth = Integer.parseInt(splits[3]);
		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		long startDate = 0;
		try {
			startDate = sdf.parse(splits[4]).getTime();
		} catch (ParseException e) {

			e.printStackTrace();
		}

		int numBars = Integer.parseInt(splits[5]);

		long date;
		if (splits[0].compareTo("gdaxb") == 0) {
			date = _service.getDateAfterXBarsFrom(symbol, aType, barWidth,
					startDate, numBars);
		} else {
			date = _service.getDateBeforeXBarsFrom(symbol, aType, barWidth,
					startDate, numBars);
		}

		System.out.println("the date raw is " + date + " which is "
				+ new Date(date));

	}

	private static void _doImportCsvCommand(String[] splits)
			throws DFSException {
		if (splits.length < 2) {
			System.out.println("I need a file parameter");
			return;
		}

		String csvFileName = splits[1];

		System.out.println("you want to import the file " + csvFileName);

		try (FileInputStream fis = new FileInputStream(new File(
				CSVData.CSV_TEST_FOLDER, csvFileName + ".csv"));) {
			getRealService().getModel().getCache()
					.importCsvData(fis, csvFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void _doListCommand(
			@SuppressWarnings("unused") String[] splits) {
		try {
			DfsSymbolList dsl = _service.getSymbolsList();

			System.out.println("The list is ");
			System.out.println(dsl.toString());
		} catch (DFSException e) {
			debug_var(391039, "Cannot have list of symbols");
			e.printStackTrace();
		}
	}

	/**
	 * Request a random symbol
	 * 
	 * @param splits
	 * @throws DFSException
	 */
	private static void _doRequestRandomSymbol(String[] splits)
			throws DFSException {
		if (splits.length != 2) {
			System.err.println("I need two parameters");
			return;
		}
		String randomSymbol = splits[1];
		RandomSymbol rs = new RandomSymbol(randomSymbol, 5, 2, 1);

		/*
		 * The parameters are not really important, but at least the request
		 * must be coherent
		 */
		TickDataRequest tdr = new TickDataRequest(rs, 0.25, 0.25, true, true,
				9393, true, 3, true, 0);
		ArrayList<DataRequest> ar = new ArrayList<>();
		ar.add(null);
		tdr.setRequests(ar);

		String virtualSymbol = _multiserver.createVirtualSymbol(tdr);

		_multiserver.subscribeQuote(new ISymbolListener() {

			@Override
			public void onNewSymbolEvent(DFSSymbolEvent anEvent) {
				System.out.println("[vs] " + anEvent);
			}
		}, virtualSymbol);

	}

	private static void _doStatCommmand(String[] splits) {
		String symbol = splits[1];
		try {
			DfsSymbolStatus dss = getRealService().getStatusForSymbol(symbol,
					true);
			if (dss == null) {
				System.out.println("null.");
			} else {
				System.out.println("The status is ");
				System.out.println(dss.toString());
			}

		} catch (DFSException e) {
			debug_var(391039, "Cannot have status");
			e.printStackTrace();
		}
	}

	private static void _doTruncateCommand(String[] splits) throws DFSException {
		String aSymbol = splits[1];
		BarType aType = BarType.valueOf(splits[2]);

		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		long truncateDate = 0;
		try {
			truncateDate = sdf.parse(splits[3]).getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return;
		}
		debug_var(839134, "Truncating date to ", new Date(truncateDate));
		_service.truncateMaturity("testpp", aSymbol, aType, truncateDate);
	}

	private static void _doVirtualSymbolCommand(String[] splits)
			throws DFSException {
		/*
		 * Virtual symbol, for now I have only a hard coded request, I need only
		 * a symbol.
		 */
		if (splits.length < 2) {
			System.out.println("I need at least a symbol.");
			return;
		}
		String symbol = splits[1];

		// create tick data request

		MfgSymbol aSymbol = new DfsRealSymbol(symbol, symbol, 25, 0, 5);
		TickDataRequest tdr = new TickDataRequest(aSymbol, 0.25, 0.25, true,
				false /* no real time */, 99, false, 0, true, 47);

		DataRequest dr;
		ArrayList<DataRequest> ardr = new ArrayList<>();

		// dr = new DataRequest(BAR_TYPE.DAILY, 1, 29, UnitsType.BARS, 2,
		// 4);
		// ardr.add(dr);

		dr = new DataRequest(BAR_TYPE.MINUTE, 1, -1, 564, UnitsType.BARS, 2, 4);
		ardr.add(dr);

		// dr = new DataRequest(BAR_TYPE.RANGE, 1, -1, 141, UnitsType.BARS,
		// 2,
		// 4);
		// ardr.add(dr);
		tdr.setRequests(ardr);

		// create tick data request end

		final IDataSource datasource = _service.createDataSource(tdr,
				new ISymbolListener() {

					@Override
					public void onNewSymbolEvent(DFSSymbolEvent anEvent) {
						if (anEvent instanceof DFSWarmUpFinishedEvent) {
							System.out
									.println("end... of request......................................** "
											+ anEvent.toPayload());
						} else {
							System.out.println(anEvent.toPayload());
						}

					}
				});
		datasource.start();
	}

	private static void _doWatchSymbolCommand(String[] splits)
			throws DFSException {
		if (splits.length != 2) {
			System.err.println("I need a symbol");
			return;
		}
		String aSymbol = splits[1];
		_service.watchDbSymbol(null, aSymbol);
	}

	/**
	 * 
	 * 
	 * @param isOffline
	 *            true if the server (local) will not try to start the data
	 *            feed.
	 * @param createRemoteSocket
	 */
	@SuppressWarnings("boxing")
	public static synchronized IDFS createLocalServer(IDFSListener aListener,
			boolean useSimulator, boolean isOffline, boolean createRemoteSocket)
			throws DFSException {

		if (_service == null) {

			if (_multiserver == null) {
				getMultiServer(useSimulator, isOffline);
			}

			_service = new Service(_multiserver);
			_service.start(aListener);

			if (createRemoteSocket) {
				try {
					_ss = new SocketServer(SOCKET_PORT, useSimulator, isOffline);
					_ss.start();
				} catch (IOException e) {
					debug_var(381466, "Cannot open port ", SOCKET_PORT,
							" probably there is another server running");
				} catch (Exception e) {
					e.printStackTrace();
					throw new DFSException(e);
				}

			}

		}
		return _service;
	}

	/**
	 * creates a remote dfs tea interface, that is a proxy.
	 * 
	 * @param aListener
	 *            also the remote server needs a listener.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * 
	 * @return an interface to the remote server (actually it is returned a
	 *         remote object, like in .net when we create a proxy/stub pair).
	 * 
	 * @throws DFSException
	 *             if something goes wrong.
	 */
	public static synchronized IDFS createRemoteServer(IDFSListener aListener,
			String host, int port, String user, String password)
			throws DFSException {

		if (_service != null) {
			return _service;
		}

		try {
			_service = DfsProxy.createProxy(host, port);
			_service.start(aListener);
			_service.login(user, password);
		} catch (DFSException e) {
			// something went wrong, I undo the creation7
			debug_var(718391, "Got exception, ", e,
					" I undo the creation of the proxy");
			if (_service != null) {
				_service.stop(); // to stop the thread
			}
			_service = null;
			throw e; // rethrow it.
		}

		return _service;
	}

	public static synchronized void disposingServer() {
		if (_ss != null) {
			_ss.stop();
			_ss = null;
		}

		if (_service != null) {
			_service.stop();
			_service = null;
		}
		if (_multiserver != null) {
			_multiserver.stop();
			_multiserver = null;
		}
	}

	public static DfsClientsModel getModel() {
		if (_ss == null) {
			return null;
		}
		return _ss.getModel();
	}

	public static void killClient(String remoteIp) {
		if (_ss == null) {
			return;
		}
		_ss.killClient(remoteIp);
	}

	/**
	 * creates a multiserver, which is a dfs server which is able to have
	 * multiple clients.
	 * 
	 * @param useSimulator
	 *            true if you want to use the simulator (it simply use the fake
	 *            root of the database)
	 * 
	 * @return
	 * @throws DFSException
	 */
	static synchronized MultiServer getMultiServer(boolean useSimulator,
			boolean isOffline) throws DFSException {

		if (useSimulator) {
			MfgMdbSession.setFakeRoot(true);
		}

		if (!AppLock.setLock("com.mfg.dfs.conn.dfs_"
				+ MfgMdbSession.getInstance().getSessionRoot()
						.getAbsolutePath())) {
			System.out
					.println("Cannot do the lock, probably another DFS server is running");
			throw new DFSException("cannot lock the database @ "
					+ MfgMdbSession.getInstance().getSessionRoot()
							.getAbsolutePath());
		}

		if (_multiserver == null) {
			LogManager.getInstance().INFO(
					"Multiserver started, offline " + isOffline);

			_multiserver = new MultiServer();
			try {
				_multiserver.start(useSimulator, isOffline);
			} catch (DFSException e) {
				debug_var(829112, "exception found while starting server ", e);
				e.printStackTrace();
				_multiserver.stop();
				_multiserver = null;
				throw e;
			}
		}
		return _multiserver;

	}

	/**
	 * returns the real server which is inside this virtual machine.
	 * 
	 * @return the <i>real</i> server, or null if this virtual machine has
	 *         connected to a proxy
	 */
	static MultiServer getRealService() {
		if (_multiserver == null) {
			return null;
		}
		return _multiserver;
	}

	/**
	 * it does not attempt to create the server, connects to existing one
	 * 
	 * @return the existing multiserver
	 */
	static MultiServer giveExistingMultiserver() {
		return _multiserver;
	}

	/**
	 * This is the entry point for the standalone server, used also as a testing
	 * bed for the history tables.
	 * 
	 * @param args
	 * @throws DFSException
	 * @throws UnknownOptionException
	 * @throws IllegalOptionValueException
	 */
	public static void main(String args[]) throws DFSException,
			IllegalOptionValueException, UnknownOptionException {

		MfgCmdLineParser cmdLine = new MfgCmdLineParser(args);

		MfgMdbSession.setSessionRoot(cmdLine.customDfsRoot);

		try (Scanner sc = new Scanner(System.in)) {

			if (cmdLine.useCustomPrefix) {
				System.out
						.println("tell me the prefix of the dfs root you want to use:");
				String prefix = sc.nextLine();
				MfgMdbSession.setDfsPrefix(prefix);
			}

			if (cmdLine.remote) {
				ServerFactory.createRemoteServer(new ProxyDfsListener(
						new PrintWriter(System.out)), "localhost", 8999,
						"scott", "tiger");
			} else {
				ServerFactory.createLocalServer(new ProxyDfsListener(
						new PrintWriter(System.out)), cmdLine.useSimulator,
						cmdLine.offline, false);
			}

			debug_var(
					391913,
					"DFS system, v.0.1 (c) 2013 - Giulio Rugarli, Market Forecast Group. ",
					cmdLine.offline ? " OFFLINE " : " online");

			String line;

			do {
				line = sc.nextLine();
				if (line.compareTo("quit") == 0) {
					break;
				}

				try {
					processServerCommandLine(line);
				} catch (DFSException e) {
					e.printStackTrace();
					debug_var(390334, "Exception received ", e,
							" type quit to exit.");
				} catch (Throwable e) {
					e.printStackTrace();
					break;
				}

			} while (true);

		}

		debug_var(390103, "Stopping server, please wait");
		ServerFactory.disposingServer();
		debug_var(839193, "Normal end of console server. Bye.");

	}

	/**
	 * This is a simple test function, just to test some methods in the server,
	 * it has no real meaning outside the main method of this.
	 * 
	 * @param line
	 * @throws DFSException
	 */
	private static void processServerCommandLine(String line)
			throws DFSException {
		// Ok, I have to split the line in tokens
		String splits[] = line.split(",");
		String splits1[] = line.split(",", 2);
		String allTheRestLine;
		if (splits1.length == 2) {
			allTheRestLine = splits1[1];
		} else {
			allTheRestLine = null;
		}

		if (splits[0].compareTo("s") == 0) {
			// debug_var(782833, "subscribe to ", splits[1]);
			// _service.subscribeQuote(splits[1]);
		} else if (splits[0].compareTo("stat") == 0) {
			_doStatCommmand(splits);
		} else if (splits[0].compareTo("list") == 0) {
			_doListCommand(splits);
		} else if (splits[0].compareTo("dumps") == 0) {
			_doDumpCacheCommand(splits);
		} else if (splits[0].compareTo("dumpc") == 0) {
			_doDumpCacheParsCommands(splits);
		} else if (splits[0].compareTo("ds") == 0) {
			// ok, I want to delete a symbol.
			ServerFactory.getRealService().getModel().getCache()
					.removeSymbol(splits[1]);
		} else if (splits[0].compareTo("virts") == 0) {
			_doVirtualSymbolCommand(splits);
		} else if (splits[0].compareTo("cache") == 0) {
			_doCacheCommand(splits, allTheRestLine);
		} else if (splits[0].compareTo("gbw") == 0) {
			_doGetBarsBetweenCommand(splits);
		} else if (splits[0].compareTo("gdaxb") == 0
				|| splits[0].compareTo("gdbxb") == 0) {
			_doGetDateAfterBeforeCommand(splits);
		} else if (splits[0].compareTo("sched") == 0) {
			DfsSchedulingTimes dst = _service.getSchedulingTimes();
			System.out.println("The schedulings are " + dst.toString());
		} else if (splits[0].compareTo("fcs") == 0) {
			System.out.println("Booked the manual scheduling, please wait");
			_service.manualScheduling();
		} else if (splits[0].compareTo("ads") == 0) {
			_doAddSymbolCommand(splits);
		} else if (splits[0].compareTo("csv") == 0) {
			_doImportCsvCommand(splits);
		} else if (splits[0].compareTo("watch") == 0) {
			_doWatchSymbolCommand(splits);
		} else if (splits[0].compareTo("trunc") == 0) {
			_doTruncateCommand(splits);
		} else if (splits[0].compareTo("randvs") == 0) {
			_doRequestRandomSymbol(splits);
		} else {
			debug_var(393901, "Unknown [", line, "] 'quit' to exit");
		}
	}

	// don't create me
	private ServerFactory() {
		//
	}

}
