package com.mfg.tea.db;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.mfg.broker.OrderStatus;
import com.mfg.broker.orders.OrderExecImpl;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.tea.conn.VirtualBrokerParams;
import com.mfg.utils.U;

/**
 * The Db class which will hold all data of TEA, as orders, virtual brokers,
 * executions, etc.
 * 
 * <p>
 * For now the data is not persisted in database, but it will be persisted
 * eventually in a relational db, probably H2.
 * 
 * <p>
 * I do not intend to use the MDB... because it lacks a query language, but it
 * may be a simple solution.
 * 
 * <p>
 * All the IDs which are inside this "database" are given by TEA, expecially the
 * order id, this is the id which is used to distinguish an order from every
 * other order in the system.
 * 
 * <p>
 * This class is <b>not</b> synchronized, because I have thought that the only
 * client of this class is MultiTea which is already synchronized.
 * 
 * 
 * 
 * 
 * 
 * The format of the database is a simple key/value pair store.
 * 
 * for example an order may be have this key
 * 
 * o1.type=buy o1.sent=time_sent
 * 
 * 
 * ...
 * 
 * o239428.type=buy
 * 
 * and so on.
 * 
 * The execution log is simply a list of events, but I should make an effort to
 * make a hierarchy
 * 
 * for example
 * 
 * teaId/shellId/virtualsymbolId/strategyid/order1.type=buy
 * 
 * 
 * The problem is that we will have a gigantic key and a very small value. This
 * may be a problem.
 * 
 * otherwise I can have
 * 
 * log1.teaId=... log1. log11.order1.type=
 * 
 * In this case the "base object" is the log.
 * 
 * 
 * But how can I store the information about a log? when a tea connects to
 * multitea I must have the number of logs presents, I must be able to list
 * them.
 * 
 * 
 * <p>
 * I have yet to define a suitable data format, because in this way I will have
 * the possibility to access the data easily.
 * 
 * 
 * 
 * 
 * So the structure of db is a hive, like the windows registry.
 * 
 * So something like
 * 
 * st1 : sub tea 1
 * 
 * st1.shell1.complete_name = "TEST_ID" st1.shell1.password_hashed =
 * "c7erouopqruwo854"
 * 
 * for example then we will have the log
 * 
 * st1.shell1.log1 = ""
 * 
 * 
 * <p>
 * The database stores only elementary strings as keys <b>and</b> values. This
 * means that even values are strings, and <b>not</b> structured strings like
 * xml or json, just simple plain strings or base64 encoded byte arrays, only if
 * there will be the necessity to store a binary object, or something about
 * that.
 * 
 * <p>
 * Doing this the database will be immutable appending. An entry is immutable
 * apart from the "counters" which are in any case managed by the engine. In
 * this way a pair (K,V) is either absent or present, and, if present, it will
 * be forever.
 * 
 * 
 * 
 * Structure of the database.
 * 
 * <p>
 * _tea : it is the main map. It stores all the information. In reality we could
 * only transfer the map, and all the other information could be recreated using
 * only the information stored in this map.
 * 
 * <p>
 * it is like the <i>fossil</i> database where all the information is contained
 * in a bag of "objects" hashed by their hash, unordered.
 * 
 * <p>
 * Also in our database all the information is the key/value store. The other
 * sets and indeces are really not needed. For example the atomic long is not
 * needed because I could simply traverse all the objects and know the last
 * written
 * 
 * 
 * THE ATOMIC LONGS ================
 * 
 * stid : the sequence for the sub teas
 * 
 * oid : The sequence for orders.
 * 
 * tsid: the sequence for the trading sessions.
 * 
 * <P>
 * The PREFIXES
 * 
 * <P>
 * Each object in db has a prefix given by the atomic counter.
 * 
 * "st" is the prefix for the subtea "st_1", "st_2", etc are the various
 * subteas.
 * 
 * "o" is the prefix for the order,
 * 
 * "ts" the prefix for the trading session.
 * 
 * <p>
 * The fields
 * 
 * <p>
 * Each object has a field. A field is stored as ".$field"
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class Db {

	/**
	 * The enumeration of all the objects which are stored in the system with
	 * their corresponding prefixes.
	 * 
	 * <p>
	 * The {@link #EVENT_OBJECT} is a kind of meta_object tag, because it is the
	 * generic root for all the events which may arrive in the system.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */

	enum OBJECTS {
		/*
		 * The root object is the root of the hierarchy, its only children are
		 * sub tea objects. Of course the root has not a sequence, it is a
		 * singleton.
		 */
		ROOT_OBJECT("$_"), ORDER_OBJECT("o_"), SUB_TEA_OBJECT("st_"), TRADING_SESSION_OBJECT(
				"ts_"), TRADING_RUN_OBJECT("tr_"), EVENT_OBJECT("ev_"), EXECUTION_OBJECT(
				"ex_"), ORDER_STATUS_OBJECT("os_");

		private final String _prefix;

		OBJECTS(String aPrefix) {
			_prefix = aPrefix;
		}

		public String getPrefix() {
			return _prefix;
		}
	}

	/**
	 * Initialization of the packages.
	 * 
	 * <p>
	 * It should be noted that the order is not important, all methods are
	 * static and the packages do not communicate with themselves.
	 */
	private static void _packageInitialization() {

		DbKeyFieldHelper.initialize(_instance);

		SubTeas.initialize(_instance);
		TradingSessions.initialize(_instance);
		Orders.initialize(_instance);
		Events.initalize(_instance);
		TradingRuns.initialize(_instance);

	}

	static String currentTimeAsDbString() {
		return dbTeaDateFormat.format(new Date());
	}

	/**
	 * Of course the Db is a singleton.
	 * 
	 * @return the singleton.
	 */
	public static Db i() {

		if (_instance == null) {
			_instance = new Db();

			_packageInitialization();

		}
		return _instance;
	}

	/**
	 * 
	 * Creates a new Trading Pipe in the database. Each trading session can have
	 * different trading pipes, each is characterized by the use of a trading
	 * symbol and differen parameters.
	 * 
	 * <p>
	 * The database does not distinguish from real time sessions or paper
	 * trading runs, they are listed in the same way.
	 * 
	 * <p>
	 * But of course the database will store the trading type in db, then when
	 * the user makes some queries the database will filter only the runs which
	 * have a particular type.
	 * 
	 * 
	 * @param sessionId
	 *            the session identifier for this pipe.
	 * @param vb
	 * @param params
	 * 
	 * @return the trading pipe identifier. It is used to identify the trading
	 *         run which later will end.
	 */
	public static int newTradingRun(int sessionId, VirtualBrokerParams params) {

		int tradingPipeId = TradingRuns.createTradingRun(sessionId, params);

		/*
		 * I have to add this trading run to the trading session.
		 * 
		 * This call will make the association between the two.
		 */
		TradingSessions.newTradingRun(sessionId, tradingPipeId);

		return tradingPipeId;

	}

	/**
	 * This is the date format used in TEA. All the dates follow this
	 * convention, at the present moment I see no reason why we should have
	 * other formats.
	 */
	static SimpleDateFormat dbTeaDateFormat;

	static {
		dbTeaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	/**
	 * This is the suffix used for the sequences in the database. Each sequence
	 * is used to generate a unique id for each object type. So for example the
	 * sequence for the orders will be
	 * 
	 * <pre>
	 * {@link OBJECTS#ORDER_OBJECT} + {@link #SEQUENCE_SUFFIX}
	 * </pre>
	 * 
	 */
	static final String SEQUENCE_SUFFIX = ".seq";

	/**
	 * The array index is the @ sign to remember Perl. It is the starting string
	 * for all the arrays in the tea map, remember however that the trading map
	 * is the only thing we need.
	 */
	static final String ARRAY_INDEX_PREFIX = "@_";

	/**
	 * The prefix used to have the objects ids in the database. It is the "star"
	 * sign in memory of the C star operator for pointers...
	 */
	static final String INSTANCE_SEQUENCE_PREFIX = "*_";

	private static Db _instance = null;

	/**
	 * The map db database. It is stored here for order, but it belongs
	 * logically to MultiTea.
	 * 
	 * <p>
	 * It is package protected because all the various submodules have access to
	 * it.
	 */
	DB _db;

	/*
	 * The atomic longs responsible for the maps are stored in the modules.
	 */
	// Atomic.Long _oid;

	/**
	 * This is the second try to the orders table.
	 * 
	 * <p>
	 * My idea is to have a very basic store, in which either key and values are
	 * string, like a "gigantic" structured text file
	 * 
	 */
	BTreeMap<String, String> _teaMap;

	/**
	 * the temporary map holds the things which are used only the life of tea
	 * and could be destroyed.
	 */
	@SuppressWarnings("unused")
	private BTreeMap<String, String> _tempTeaMap;

	/**
	 * Private constructor to create the database using a Mapdb object.
	 */
	private Db() {

		String cwp;
		if (Platform.isRunning()) {
			Location instanceLoc = Platform.getInstanceLocation();
			cwp = instanceLoc.getURL().getPath();
		} else {
			cwp = System.getProperty("user.home");
		}
		File teaDbFile = new File(cwp, "__teaDb");

		if (DBPARS.IS_MEMORY_FILE) {
			_db = DBMaker.newMemoryDirectDB().closeOnJvmShutdown()
					.transactionDisable().make();
		} else {
			_db = DBMaker.newFileDB(teaDbFile).closeOnJvmShutdown()
					.transactionDisable().make();
		}

		/*
		 * The tea map is the ONLY object which is really necessary to have the
		 * complete information about tea.
		 * 
		 * All other objects in this database (mostly sequences) could be
		 * recreated by looking at tea map.
		 * 
		 * Every package can have some "live" information which stores
		 * information for a particular run but this is transferred to TEA
		 * whenever it becomes immutable.
		 */
		_teaMap = _db.createTreeMap("tea")
				.keySerializer(BTreeKeySerializer.STRING)
				.valueSerializer(Serializer.STRING).makeOrGet();

		/*
		 * The temporary map is always memory based, because it does not require
		 * to persist.
		 * 
		 * Maybe it is not useful, I have yet to think about it.
		 */
		_tempTeaMap = _db.createTreeMap("tempMap")
				.keySerializer(BTreeKeySerializer.STRING)
				.valueSerializer(Serializer.STRING).makeOrGet();

	}

	/**
	 * Gives the opportunity to shutdown all the packages which have been
	 * initialized in the {@link #_packageInitialization()} method.
	 */
	private void _packageDeinitialization() {
		// TO DO Auto-generated method stub
	}

	public void beginTransaction() {
		// here it is a no op.
	}

	public void close() {

		_packageDeinitialization();

		/*
		 * Should I autocommit it?
		 */
		if (DBPARS.COMMIT_ENABLED) {
			_db.commit();
		}

		if (DBPARS.DUMP_ENABLED) {
			dump();
		}
	}

	/**
	 * Commits the DB, for some parameters the commit is a no-op.
	 */
	public void commit() {
		if (DBPARS.COMMIT_ENABLED) {
			_db.commit();
		}
	}

	/**
	 * @param anExec
	 *            the execution which has been done.
	 */
	@SuppressWarnings("boxing")
	public static void insertExecution(OrderExecImpl anExec) {

		OrderImpl oi = (OrderImpl) anExec.order;

		U.debug_var(291295, "exec for dbid ", oi.getTeaId(), " ex ", anExec);
		// String orderKey = "O_" + new Long(aDbId).toString();
		// _teaMap.put(orderKey + ".exec", anExec.toString());
	}

	@SuppressWarnings("static-method")
	public synchronized void insertStatus(OrderStatus aStatus) {

		long aDbId = 99;

		U.debug_var(716281, "DB HOOK aDbid " + aDbId + " status " + aStatus);

		Orders.newOrderStatus(aDbId, aStatus);

		// String orderKey = Db.OBJECTS.ORDER_OBJECT.getPrefix()
		// + new Long(aDbId).toString() + ".statusCounter";
		// String counter = _teaMap.get(orderKey);
		//
		// String eventKey = "OrderStatus_" + aDbId + "_" + counter;
		//
		// int newCounter = Integer.parseInt(counter) + 1;
		// _teaMap.replace(orderKey, "" + newCounter);
		//
		// _teaMap.put(eventKey, aStatus.toString());

	}

	@SuppressWarnings("static-method")
	public void modifyExistingOrder(OrderImpl aOrder) {
		Orders.modifyOrder(aOrder);
	}

	/**
	 * Register the connection of a sub TEA.
	 * 
	 * <p>
	 * This starts a new trading session. Orders are sent only in an active
	 * trading session, and through an active virtual broker.
	 * 
	 * <p>
	 * The database does not care about security, even though it may contain a
	 * hashed password, but this is not designed now. Probably when we arrive
	 * here the authentication has been already done.
	 * 
	 * @param aTeaId
	 *            The tea identifier which has connected. A TEA id cannot have
	 *            two active sessions. It is an error.
	 * @return the session identifier for this active session. This token must
	 *         be given back to the database whenever the user starts a new
	 *         trading pipe.
	 */
	@SuppressWarnings("static-method")
	public int newTradingSession(String aTeaId) {
		// String teaId = SUB_TEA_PREFIX + aTeaId;

		// SubTeas.newSubTeaConnected(aTeaId);

		// if (!_teaMap.containsKey(teaId)) {
		// _teaMap.put(teaId, "");
		// }

		/*
		 * to have a complete list of all the subteas which are registered in
		 * this database I have to create a set, this set is like a bag which
		 * contains all the subteas which have been "seen" by this database.
		 * 
		 * That means that probably the key value store is not sufficient, but I
		 * have also to have other datastores, for example a set... these sets
		 * will enable me to make queries like: what are the trading sessions in
		 * this date?
		 */

		int subTeaId = SubTeas.newSubTeaConnected(aTeaId);

		int ts = TradingSessions.createTradingSession(subTeaId);

		/*
		 * The trading session is created and it has a identifier, which is the
		 * unique identifier of all the sessions of this subtea.
		 * 
		 * 
		 * The database is a hierarchical store
		 */

		/*
		 * Ok now we connect the new trading session to the sub tea.
		 */
		SubTeas.newTradingSession(subTeaId, ts);

		return ts;

	}

	/**
	 * Puts a new order into the database.
	 * 
	 * <p>
	 * The method will create all the necessary data that accompains the order,
	 * like the counter, etc...
	 * 
	 * @param aRunIdentifier
	 *            the identifier of the run, this is the identifier also for all
	 *            the events in the run itself.
	 * 
	 * @param aOrder
	 *            the order which will be inserted.
	 * @return the new id of this order, the unique id inside the database.
	 */
	@SuppressWarnings("static-method")
	public long putNewOrder(int aRunIdentifier, OrderImpl aOrder) {

		long dbId = Orders.putNewOrder(aOrder);

		/*
		 * The order is put, this is a new event. If the order is a parent we
		 * record that we have added this event to the run.
		 */
		if (!aOrder.isChild()) {
			Events.newPutOrderEvent(aRunIdentifier, aOrder);
		}

		TradingRuns.newOrderAdded(aRunIdentifier, dbId);

		return dbId;

	}

	public void rollback() {
		if (DBPARS.COMMIT_ENABLED) {
			_db.rollback();
		}
	}

	/**
	 * registers that the given trading pipe has been stopped.
	 * 
	 * @param tradingId
	 */
	public synchronized static void closedTradingRun(int tradingId) {
		TradingRuns.closeTradingRun(tradingId);
	}

	/**
	 * the trading session has been closed.
	 * 
	 * @param _teaId
	 * 
	 * @param aTradingSessionId
	 */
	public static void tradingSessionClosed(String _teaId, int aTradingSessionId) {

		TradingSessions.closeTradingSession(aTradingSessionId);

	}

	/**
	 * A simple test method to dump the database.
	 */
	public synchronized void dump() {
		U.debug_var(
				209815,
				"***********************************************  DUMPING DB.... **********************");
		DbDumper.dump(_db, new ConsoleDbDumper(
				new PrintWriter(System.out, true)));
		U.debug_var(
				209815,
				"***********************************************  DUMPING DB END **********************");

	}

}
