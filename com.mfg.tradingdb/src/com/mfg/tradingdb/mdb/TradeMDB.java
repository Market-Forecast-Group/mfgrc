package com.mfg.tradingdb.mdb;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.mfg.mdb.runtime.*;
/* BEGIN USER IMPORTS */
/* User can insert his code here */
/* END USER IMPORTS */

/**
 * <p>
 * This class provides the API to manipulate Trade files. 
 * Here you will find the methods to modify and query the Trade files. 
 * </p>
 * <p>
 * An MDB file does not contain any meta-data, it is just raw data, 
 * one record next to the other, every sinlge byte is part of the data,
 * however, this class contains the required information to "understand"
 * the files format. Important, do not try to access files created 
 * by other classes because you will get an unexpected behavior and corrupted data.
 * </p>
 * <p>
 * This is the schema this "driver" class understands:
 * </p>
 * <h3>Trade definition</h3>
 * <table border=1>
 *	<caption>Trade</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>openTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>openPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>closeTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>closePrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>isGain</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>isClosed</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>isLong</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>orderId</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>openingCount</td>
 *		<td>BYTE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening0</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening1</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>openPhysicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>closePhysicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>eventPhysicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening0_childType</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening1_childType</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening0_orderId</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>opening1_orderId</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * </table>
 * <h3>TradeMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on Trade files:
 * </p>
 *
 * <h3>Data Insertion</h3>
 *
 * <p>
 * As you know, in MDB you cannot insert a record in the middle of the file, all the data you add to the file is at the end of it, for that reason
 * we named {@link Appender} to the component in charge of this function. So to add data to the file you have to request an appender:
 * </p>
 * <pre>
 * // To connect to an MDB file, the best is to use
 * // the "connect" method of the session.
 * // In the next examples, we will asume you know it.
 * 
 * TradingMDBSession session = ...;
 * TradeMDB mdb = session.connectTo_TradeMDB("trade.mdb");
 * 
 * // request the appender.
 * TradeMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.openTime = ...;
 * app.openPrice = ...;
 * app.closeTime = ...;
 * app.closePrice = ...;
 * app.isGain = ...;
 * app.isClosed = ...;
 * app.isLong = ...;
 * app.orderId = ...;
 * app.openingCount = ...;
 * app.opening0 = ...;
 * app.opening1 = ...;
 * app.openPhysicalTime = ...;
 * app.closePhysicalTime = ...;
 * app.eventPhysicalTime = ...;
 * app.opening0_childType = ...;
 * app.opening1_childType = ...;
 * app.opening0_orderId = ...;
 * app.opening1_orderId = ...;

 * // add the record
 * app.append();
 *
 * ...
 *
 * // You can repeat this operation for each item you want to add to the file.
 * // Important, is possible that some of these records are yet in the memory buffer
 * // so to writes them to the file, you have to flush the appender:
 *
 * app.flush();
 *
 * ...
 *
 * // When you are sure you do not want to add new records, close the appender.
 * // The close method also write the pending records to the disk, 
 * // so it is not needed to call the flush method.
 *
 * app.close();
 * </pre>
 * <p>
 * So use the method {@link #appender()} to get the appender, and use the 
 * methods {@link Appender#append()}, {@link Appender#append(Record)} and 
 * {@link Appender#append_ref_unsafe(Record)} to add the records.
 * </p>
 *
 * <h3>Data Query</h3>
 * <p>
 * We provide different APIs to retrieve data: cursors, selection methods and List wrappers. 
 * All of them are based on cursors, so it is important you understand how cursors work.
 * </p>
 * <h4>Cursors</h4>
 * <p>
 * There are two type of cursors:
 * </p>
 * <ul>
 * <li>{@link Cursor}: sequential cursor.</li>
 * <li>{@link RandomCursor}: random access cursor.</li>
 * </ul>
 * <p>
 * Both cursors have a particular function and you must use the more appropiate 
 * depending on the problem. 
 * </p>
 *
 * <h5>Sequential Cursor</h5>
 *
 * <p>
 * This is the common cursor, it retrieves all the records from a start position to an stop position.
 * The API is very simple, in the following example we print the data from "start" to "stop":
 * </p>
 * <pre>
 * TradeMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * Trade.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.openTime + " "
 * 			+ cursor.openPrice + " "
 * 			+ cursor.closeTime + " "
 * 			+ cursor.closePrice + " "
 * 			+ cursor.isGain + " "
 * 			+ cursor.isClosed + " "
 * 			+ cursor.isLong + " "
 * 			+ cursor.orderId + " "
 * 			+ cursor.openingCount + " "
 * 			+ cursor.opening0 + " "
 * 			+ cursor.opening1 + " "
 * 			+ cursor.openPhysicalTime + " "
 * 			+ cursor.closePhysicalTime + " "
 * 			+ cursor.eventPhysicalTime + " "
 * 			+ cursor.opening0_childType + " "
 * 			+ cursor.opening1_childType + " "
 * 			+ cursor.opening0_orderId + " "
 * 			+ cursor.opening1_orderId + " "
 * 		);
 * }
 * // important always close the cursor
 * cursor.close();
 * </pre>
 * <p>
 * As you can see it is very simple, just to highlight that you should close the cursor
 * when you stop using it. A cursor creates certain OS resources that should be released as
 * soon the cursor is not needed anymore, also in certain operating systems like Windows, you 
 * cannot delete the underlaying file until it gets released.
 * <p> 
 * See the {@link Cursor} javadoc for more details.
 * </p>
 * 
 * <h5>Random Access Cursor</h5>
 * <p>
 * This cursor provides random access to the file. It is possible to implement a 
 * random access method using sequential cursors (open it, read a record, and close), 
 * but a {@link RandomCursor} is the API we provide
 * to perform this task in a more efficient way.
 * </p>
 * <p>
 * Let's see this API with an exmaple:
 * </p>
 * <pre>
 * TradeMDB mdb = ...;
 *
 * // request a random cursor
 * TradeMDB.RandomCursor cursor = mdb.randomCursor();
 *
 * // read record at position 10
 * cursor.seek(10);
 * System.out.println(cursor.toRecord());
 *
 * // read record at position 34
 * cursor.seek(34);
 * System.out.println(cursor.toRecord());
 *
 * // remember always to close the cursor
 * cursor.close();
 * </pre>
 * <p>
 * In our experience, many times you would like to keep alive a random access cursor
 * until the session gets closed. In this case, we recommend to "defer" the cursor.
 * A deferred cursor is not more than a cursor that is closed automatically
 * when the session is closed.
 * See the {@link MDBSession#defer(ICursor)} javadoc for more information.
 * </p>
 * <p>
 * MDB uses random access cursors to implement other APIs with a higher level of abstraction
 * like the {@link MDB#record(IRandomCursor, long)} method and the lists. Now we want to focus
 * on the "record" method. It is very easy to use, the previous example can be re-implemented
 * in this way:
 * </p>
 * <pre>
 * TradeMDB mdb = ...;
 * // read record at position 10
 * System.out.println(mdb.record(10));
 *
 * // read record at position 34
 * System.out.println(mdb.record(34));
 * </pre>
 * <p>
 * Is it simpler right? And it performs very well, yet a random cursor is a bit faster
 * but you can use the "record" if you have a deadline, just do not use to retrieve
 * sequential, in that case remember to use a sequential cursor or one of the "select"
 * methods (we explain later these methods).
 * </p>
 * <p>
 * Something to highlight about the "record" method implementation, it creates 
 * a random-deferred cursor per thread, so it i safe if many threads call it 
 * at the same time.
 * </p>
 *
 *
 * <h4>Index Search</h4>
 * <p>
 * TODO: Documentation is coming...
 * </p>
 *
 * <h3>Data Update</h3>
 *
 * <p>
 * MDB provides two type of methods to update the values:
 * </p>
 * <ul>
 * 	<li>
 * 		Update a unqiue row. It re-writes the content of the whole record. 
 * 		It is available for tables without array definitions.
 * </li>
 * 	<li>
 *		Update a particular column or a unique row. It re-writes only that field of the record. 
 * 		It is available only for non-array columns.
 * 	</li>
 * </ul>
 * <p>
 * You see in both cases it updates only one record at the same time, and the value to replace should be primitive.
 * Also remember virtual columns are not updated because its values are computed automatically, they are not stored physically.
 * </p>
 * <p>
 * The API is simple:
 * </p>
 * <pre>
 * TradeMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values 										
 * long new_val_openTime = ...;
 * double new_val_openPrice = ...;
 * long new_val_closeTime = ...;
 * double new_val_closePrice = ...;
 * boolean new_val_isGain = ...;
 * boolean new_val_isClosed = ...;
 * boolean new_val_isLong = ...;
 * int new_val_orderId = ...;
 * byte new_val_openingCount = ...;
 * long new_val_opening0 = ...;
 * long new_val_opening1 = ...;
 * long new_val_openPhysicalTime = ...;
 * long new_val_closePhysicalTime = ...;
 * long new_val_eventPhysicalTime = ...;
 * int new_val_opening0_childType = ...;
 * int new_val_opening1_childType = ...;
 * int new_val_opening0_orderId = ...;
 * int new_val_opening1_orderId = ...;
 *
 * mdb.replace(index 
 * 		, new_val_openTime
 * 		, new_val_openPrice
 * 		, new_val_closeTime
 * 		, new_val_closePrice
 * 		, new_val_isGain
 * 		, new_val_isClosed
 * 		, new_val_isLong
 * 		, new_val_orderId
 * 		, new_val_openingCount
 * 		, new_val_opening0
 * 		, new_val_opening1
 * 		, new_val_openPhysicalTime
 * 		, new_val_closePhysicalTime
 * 		, new_val_eventPhysicalTime
 * 		, new_val_opening0_childType
 * 		, new_val_opening1_childType
 * 		, new_val_opening0_orderId
 * 		, new_val_opening1_orderId
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link TradeMDB#replace_openTime(long, long)}: To replace the openTime value.</li>
 * <li>{@link TradeMDB#replace_openPrice(long, double)}: To replace the openPrice value.</li>
 * <li>{@link TradeMDB#replace_closeTime(long, long)}: To replace the closeTime value.</li>
 * <li>{@link TradeMDB#replace_closePrice(long, double)}: To replace the closePrice value.</li>
 * <li>{@link TradeMDB#replace_isGain(long, boolean)}: To replace the isGain value.</li>
 * <li>{@link TradeMDB#replace_isClosed(long, boolean)}: To replace the isClosed value.</li>
 * <li>{@link TradeMDB#replace_isLong(long, boolean)}: To replace the isLong value.</li>
 * <li>{@link TradeMDB#replace_orderId(long, int)}: To replace the orderId value.</li>
 * <li>{@link TradeMDB#replace_openingCount(long, byte)}: To replace the openingCount value.</li>
 * <li>{@link TradeMDB#replace_opening0(long, long)}: To replace the opening0 value.</li>
 * <li>{@link TradeMDB#replace_opening1(long, long)}: To replace the opening1 value.</li>
 * <li>{@link TradeMDB#replace_openPhysicalTime(long, long)}: To replace the openPhysicalTime value.</li>
 * <li>{@link TradeMDB#replace_closePhysicalTime(long, long)}: To replace the closePhysicalTime value.</li>
 * <li>{@link TradeMDB#replace_eventPhysicalTime(long, long)}: To replace the eventPhysicalTime value.</li>
 * <li>{@link TradeMDB#replace_opening0_childType(long, int)}: To replace the opening0_childType value.</li>
 * <li>{@link TradeMDB#replace_opening1_childType(long, int)}: To replace the opening1_childType value.</li>
 * <li>{@link TradeMDB#replace_opening0_orderId(long, int)}: To replace the opening0_orderId value.</li>
 * <li>{@link TradeMDB#replace_opening1_orderId(long, int)}: To replace the opening1_orderId value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see TradingMDBSession#connectTo_TradeMDB(String)
 */

public final class TradeMDB
/* BEGIN MDB EXTENDS */
		extends MDB<TradeMDB.Record>
/* END MDB EXTENDS */
{

/* BEGIN USER MDB */
/* User can insert his code here */
/* END USER MDB */
	/**
	 * Trade's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"openTime",
		"openPrice",
		"closeTime",
		"closePrice",
		"isGain",
		"isClosed",
		"isLong",
		"orderId",
		"openingCount",
		"opening0",
		"opening1",
		"openPhysicalTime",
		"closePhysicalTime",
		"eventPhysicalTime",
		"opening0_childType",
		"opening1_childType",
		"opening0_orderId",
		"opening1_orderId",
	};
	
	/**
	 * Trade's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		long.class,
		double.class,
		long.class,
		double.class,
		boolean.class,
		boolean.class,
		boolean.class,
		int.class,
		byte.class,
		long.class,
		long.class,
		long.class,
		long.class,
		long.class,
		int.class,
		int.class,
		int.class,
		int.class,
	};
	
	/**
	 * Trade's meta-data: column Java types size (in bytes).
	 */
	public static final int[] COLUMNS_SIZE = { 
		8, 
		8, 
		8, 
		8, 
		1, 
		1, 
		1, 
		4, 
		1, 
		8, 
		8, 
		8, 
		8, 
		8, 
		4, 
		4, 
		4, 
		4, 
	};

	/**
	 * Trade's meta-data: virtual column flags.
	 */
	public static final boolean[] COLUMNS_IS_VIRTUAL = { 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
	};

	/**
	 * Trade's meta-data: column byte-offset.
	 */
	public static final int[] COLUMN_OFFSET = {  
		0, 
		8, 
		16, 
		24, 
		32, 
		33, 
		34, 
		35, 
		39, 
		40, 
		48, 
		56, 
		64, 
		72, 
		80, 
		84, 
		88, 
		92, 
	};
	
	/**
	 * Trade's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 96;
	
	/**
	* openTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENTIME = 0;
	/**
	* openPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENPRICE = 1;
	/**
	* closeTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_CLOSETIME = 2;
	/**
	* closePrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_CLOSEPRICE = 3;
	/**
	* isGain's meta-data: index in a record.
	*/	
	public static final int COLUMN_ISGAIN = 4;
	/**
	* isClosed's meta-data: index in a record.
	*/	
	public static final int COLUMN_ISCLOSED = 5;
	/**
	* isLong's meta-data: index in a record.
	*/	
	public static final int COLUMN_ISLONG = 6;
	/**
	* orderId's meta-data: index in a record.
	*/	
	public static final int COLUMN_ORDERID = 7;
	/**
	* openingCount's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENINGCOUNT = 8;
	/**
	* opening0's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING0 = 9;
	/**
	* opening1's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING1 = 10;
	/**
	* openPhysicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENPHYSICALTIME = 11;
	/**
	* closePhysicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_CLOSEPHYSICALTIME = 12;
	/**
	* eventPhysicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_EVENTPHYSICALTIME = 13;
	/**
	* opening0_childType's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING0_CHILDTYPE = 14;
	/**
	* opening1_childType's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING1_CHILDTYPE = 15;
	/**
	* opening0_orderId's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING0_ORDERID = 16;
	/**
	* opening1_orderId's meta-data: index in a record.
	*/	
	public static final int COLUMN_OPENING1_ORDERID = 17;

	/**
	 * Trade's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "265f6fc3-22c8-42c5-84e7-ed6164bb98ce";
	
	/**
	 * Trade's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "6aee131c-6e0a-481c-ba9d-a6a6938c59da LONG; 3ade2d0a-917d-4ec1-b08c-b650a6c65fad DOUBLE; c8347ed1-6c8f-4e2b-8f5c-bf3128f04700 LONG; 45f94d5b-f4d6-4163-9012-c7b611aac721 DOUBLE; f4bea829-6a2b-4953-98e8-fd2e2726391c BOOLEAN; ea85758a-3f02-446e-9533-e4901ad18cc6 BOOLEAN; d6f69786-fa58-416f-abe8-3f2c5ab780cb BOOLEAN; 5c9b0859-573c-42d3-bfde-9fc4fdf60cb4 INTEGER; 5631f68b-0f46-4209-983c-c2464b624516 BYTE; 96e789e8-fdc2-4cf7-b7d9-88507c5c349d LONG; a3729c6b-ebd8-4f34-a3b8-a1dd95b806a2 LONG; afba7d5a-28b2-4ae6-b091-ee87dc6f6b52 LONG; 0d4a22e1-5d86-4003-8ef7-f566aae45dbb LONG; 02d888ef-9720-4d91-8c3f-db3c8a8a4230 LONG; ea4b87f9-1d13-456e-b8fb-ccf5198b4b20 INTEGER; 0b335ed9-9d6c-4b56-820e-4351fed20214 INTEGER; fa503e8b-8ffb-48aa-80c9-63c4ff7cd902 INTEGER; 20a9f688-e7cb-4dd9-b7e7-c04afcdbaf46 INTEGER; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer; 
	private ByteBuffer _replaceBuffer_openTime;
	private ByteBuffer _replaceBuffer_openPrice;
	private ByteBuffer _replaceBuffer_closeTime;
	private ByteBuffer _replaceBuffer_closePrice;
	private ByteBuffer _replaceBuffer_isGain;
	private ByteBuffer _replaceBuffer_isClosed;
	private ByteBuffer _replaceBuffer_isLong;
	private ByteBuffer _replaceBuffer_orderId;
	private ByteBuffer _replaceBuffer_openingCount;
	private ByteBuffer _replaceBuffer_opening0;
	private ByteBuffer _replaceBuffer_opening1;
	private ByteBuffer _replaceBuffer_openPhysicalTime;
	private ByteBuffer _replaceBuffer_closePhysicalTime;
	private ByteBuffer _replaceBuffer_eventPhysicalTime;
	private ByteBuffer _replaceBuffer_opening0_childType;
	private ByteBuffer _replaceBuffer_opening1_childType;
	private ByteBuffer _replaceBuffer_opening0_orderId;
	private ByteBuffer _replaceBuffer_opening1_orderId;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final TradingMDBSession _session;

	/**
	 * The constructor. You can manipulate MDB files with an instance of this class 
	 * and you don't need a session, but we recommend to create a session 
	 * and connect to files with the session "connect" methods, 
	 * specially when you have more than one file.
	 * @param session The session attached to this MDB instance.
	 * @param file The main file.
	 * @param bufferSize The number of records to use in the buffer.
	 * @param mode The session mode.
	 * @throws IOException If there is an I/O error.
	 */
	public TradeMDB(TradingMDBSession session, File file, int bufferSize, SessionMode mode) throws IOException {
		super(TABLE_ID, TABLE_SIGNATURE, mode, file, null, bufferSize, COLUMNS_NAME, COLUMNS_TYPE);
		
		if (file == null) throw new IllegalArgumentException("Null files.");
		_session = session;
		_openCursorCount = new AtomicInteger(0);
		_rbufSize = 0;
		_size = fsize();
		
		if (!_basic) {
			_rbuf = new Record[_bufferSize];
			for (int i = 0; i < _bufferSize; i++) {
				_rbuf[i] = new Record();
			}
			_rbufPos = _size;
			
		}
		if (!_memory) {		
			_replaceBuffer = ByteBuffer.allocate(96);		
			_replaceBuffer_openTime = ByteBuffer.allocate(8);
			_replaceBuffer_openPrice = ByteBuffer.allocate(8);
			_replaceBuffer_closeTime = ByteBuffer.allocate(8);
			_replaceBuffer_closePrice = ByteBuffer.allocate(8);
			_replaceBuffer_isGain = ByteBuffer.allocate(1);
			_replaceBuffer_isClosed = ByteBuffer.allocate(1);
			_replaceBuffer_isLong = ByteBuffer.allocate(1);
			_replaceBuffer_orderId = ByteBuffer.allocate(4);
			_replaceBuffer_openingCount = ByteBuffer.allocate(1);
			_replaceBuffer_opening0 = ByteBuffer.allocate(8);
			_replaceBuffer_opening1 = ByteBuffer.allocate(8);
			_replaceBuffer_openPhysicalTime = ByteBuffer.allocate(8);
			_replaceBuffer_closePhysicalTime = ByteBuffer.allocate(8);
			_replaceBuffer_eventPhysicalTime = ByteBuffer.allocate(8);
			_replaceBuffer_opening0_childType = ByteBuffer.allocate(4);
			_replaceBuffer_opening1_childType = ByteBuffer.allocate(4);
			_replaceBuffer_opening0_orderId = ByteBuffer.allocate(4);
			_replaceBuffer_opening1_orderId = ByteBuffer.allocate(4);
		}
	}	

	/**
	* Trade record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the openTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of openTime</caption>
		* <tr><td>Column</td><td>openTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long openTime; /* 0 */
		/**
		* Represents the openPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of openPrice</caption>
		* <tr><td>Column</td><td>openPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double openPrice; /* 1 */
		/**
		* Represents the closeTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of closeTime</caption>
		* <tr><td>Column</td><td>closeTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long closeTime; /* 2 */
		/**
		* Represents the closePrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of closePrice</caption>
		* <tr><td>Column</td><td>closePrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double closePrice; /* 3 */
		/**
		* Represents the isGain column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of isGain</caption>
		* <tr><td>Column</td><td>isGain</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean isGain; /* 4 */
		/**
		* Represents the isClosed column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of isClosed</caption>
		* <tr><td>Column</td><td>isClosed</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean isClosed; /* 5 */
		/**
		* Represents the isLong column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of isLong</caption>
		* <tr><td>Column</td><td>isLong</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean isLong; /* 6 */
		/**
		* Represents the orderId column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of orderId</caption>
		* <tr><td>Column</td><td>orderId</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int orderId; /* 7 */
		/**
		* Represents the openingCount column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of openingCount</caption>
		* <tr><td>Column</td><td>openingCount</td></tr>
		* <tr><td>Type</td><td>BYTE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public byte openingCount; /* 8 */
		/**
		* Represents the opening0 column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening0</caption>
		* <tr><td>Column</td><td>opening0</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long opening0; /* 9 */
		/**
		* Represents the opening1 column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening1</caption>
		* <tr><td>Column</td><td>opening1</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long opening1; /* 10 */
		/**
		* Represents the openPhysicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of openPhysicalTime</caption>
		* <tr><td>Column</td><td>openPhysicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long openPhysicalTime; /* 11 */
		/**
		* Represents the closePhysicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of closePhysicalTime</caption>
		* <tr><td>Column</td><td>closePhysicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long closePhysicalTime; /* 12 */
		/**
		* Represents the eventPhysicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of eventPhysicalTime</caption>
		* <tr><td>Column</td><td>eventPhysicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long eventPhysicalTime; /* 13 */
		/**
		* Represents the opening0_childType column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening0_childType</caption>
		* <tr><td>Column</td><td>opening0_childType</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int opening0_childType; /* 14 */
		/**
		* Represents the opening1_childType column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening1_childType</caption>
		* <tr><td>Column</td><td>opening1_childType</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int opening1_childType; /* 15 */
		/**
		* Represents the opening0_orderId column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening0_orderId</caption>
		* <tr><td>Column</td><td>opening0_orderId</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int opening0_orderId; /* 16 */
		/**
		* Represents the opening1_orderId column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of opening1_orderId</caption>
		* <tr><td>Column</td><td>opening1_orderId</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int opening1_orderId; /* 17 */

		/**
		* Returns an string representation of the record content.
		*/
		@Override
		public String toString() {
			return "Trade [ "
				 + "openTime=" + openTime + " "	
				 + "openPrice=" + openPrice + " "	
				 + "closeTime=" + closeTime + " "	
				 + "closePrice=" + closePrice + " "	
				 + "isGain=" + isGain + " "	
				 + "isClosed=" + isClosed + " "	
				 + "isLong=" + isLong + " "	
				 + "orderId=" + orderId + " "	
				 + "openingCount=" + openingCount + " "	
				 + "opening0=" + opening0 + " "	
				 + "opening1=" + opening1 + " "	
				 + "openPhysicalTime=" + openPhysicalTime + " "	
				 + "closePhysicalTime=" + closePhysicalTime + " "	
				 + "eventPhysicalTime=" + eventPhysicalTime + " "	
				 + "opening0_childType=" + opening0_childType + " "	
				 + "opening1_childType=" + opening1_childType + " "	
				 + "opening0_orderId=" + opening0_orderId + " "	
				 + "opening1_orderId=" + opening1_orderId + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Long.valueOf(openTime),
							Double.valueOf(openPrice),
							Long.valueOf(closeTime),
							Double.valueOf(closePrice),
							Boolean.valueOf(isGain),
							Boolean.valueOf(isClosed),
							Boolean.valueOf(isLong),
							Integer.valueOf(orderId),
							Byte.valueOf(openingCount),
							Long.valueOf(opening0),
							Long.valueOf(opening1),
							Long.valueOf(openPhysicalTime),
							Long.valueOf(closePhysicalTime),
							Long.valueOf(eventPhysicalTime),
							Integer.valueOf(opening0_childType),
							Integer.valueOf(opening1_childType),
							Integer.valueOf(opening0_orderId),
							Integer.valueOf(opening1_orderId),
			 			};
		}
		
		/**
		* Record meta-data: the column names.
		*/
		@Override
		public String[] getColumnsName() {
			return COLUMNS_NAME;
		} 
		
		/**
		* Record meta-data: the column Java types.
		*/
		@Override
		public Class<?>[] getColumnsType() {
			return COLUMNS_TYPE;
		} 			
		
		/**
		* Clone the record.
		*/
		@Override
		public Record clone() {
			try {
				return (Record) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		* Get the value of the column at the <code>columnIndex</code> index.
		*/
		@Override
		@SuppressWarnings("boxing")
		public Object get(int columnIndex) {
			switch(columnIndex) {
				case 0: return openTime;
				case 1: return openPrice;
				case 2: return closeTime;
				case 3: return closePrice;
				case 4: return isGain;
				case 5: return isClosed;
				case 6: return isLong;
				case 7: return orderId;
				case 8: return openingCount;
				case 9: return opening0;
				case 10: return opening1;
				case 11: return openPhysicalTime;
				case 12: return closePhysicalTime;
				case 13: return eventPhysicalTime;
				case 14: return opening0_childType;
				case 15: return opening1_childType;
				case 16: return opening0_orderId;
				case 17: return opening1_orderId;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.openTime = record.openTime;
			this.openPrice = record.openPrice;
			this.closeTime = record.closeTime;
			this.closePrice = record.closePrice;
			this.isGain = record.isGain;
			this.isClosed = record.isClosed;
			this.isLong = record.isLong;
			this.orderId = record.orderId;
			this.openingCount = record.openingCount;
			this.opening0 = record.opening0;
			this.opening1 = record.opening1;
			this.openPhysicalTime = record.openPhysicalTime;
			this.closePhysicalTime = record.closePhysicalTime;
			this.eventPhysicalTime = record.eventPhysicalTime;
			this.opening0_childType = record.opening0_childType;
			this.opening1_childType = record.opening1_childType;
			this.opening0_orderId = record.opening0_orderId;
			this.opening1_orderId = record.opening1_orderId;
		}

/* BEGIN USER RECORD */
/* User can insert his code here */
/* END USER RECORD */		
	}
	
	@Override
	public Record[] makeRecordArray(int size) {
		return new Record[size];
	}
	
	@Override 
	public Record makeRecord() {
		return new Record();
	}


/**
	* <p>
	* This is the class used to append records to an MDB file.
	* </p>
	* The common way to use an appender is:
	* <pre>
	* Appender ap = mdb.appender();
	* while( ... ) {
	* 	ap.openTime = getOpenTime();	
	* 	ap.openPrice = getOpenPrice();	
	* 	ap.closeTime = getCloseTime();	
	* 	ap.closePrice = getClosePrice();	
	* 	ap.isGain = getIsGain();	
	* 	ap.isClosed = getIsClosed();	
	* 	ap.isLong = getIsLong();	
	* 	ap.orderId = getOrderId();	
	* 	ap.openingCount = getOpeningCount();	
	* 	ap.opening0 = getOpening0();	
	* 	ap.opening1 = getOpening1();	
	* 	ap.openPhysicalTime = getOpenPhysicalTime();	
	* 	ap.closePhysicalTime = getClosePhysicalTime();	
	* 	ap.eventPhysicalTime = getEventPhysicalTime();	
	* 	ap.opening0_childType = getOpening0_childType();	
	* 	ap.opening1_childType = getOpening1_childType();	
	* 	ap.opening0_orderId = getOpening0_orderId();	
	* 	ap.opening1_orderId = getOpening1_orderId();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected TradeMDB _mdb;	
		protected ByteBuffer _buf;	 
		public long openTime; /* 0 */
		public double openPrice; /* 1 */
		public long closeTime; /* 2 */
		public double closePrice; /* 3 */
		public boolean isGain; /* 4 */
		public boolean isClosed; /* 5 */
		public boolean isLong; /* 6 */
		public int orderId; /* 7 */
		public byte openingCount; /* 8 */
		public long opening0; /* 9 */
		public long opening1; /* 10 */
		public long openPhysicalTime; /* 11 */
		public long closePhysicalTime; /* 12 */
		public long eventPhysicalTime; /* 13 */
		public int opening0_childType; /* 14 */
		public int opening1_childType; /* 15 */
		public int opening0_orderId; /* 16 */
		public int opening1_orderId; /* 17 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = TradeMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 96);
				reconnectFile();
			}
		}
		
		/**
		* Close the file handlers and free the files. This method is used to "unlock" the file and 
		* perform other "write" operations outside the appender.
		*/
		void disconnectFile() throws IOException {
			if (_memory) return;
			
			_raf.close();
			_channel.close();	
			
		}
		
		/**
		* Open the file handlers again.
		*/
		void reconnectFile() throws IOException {		
			if (_memory) return;
		
			_raf = new RandomAccessFile(getFile(), "rw");
			_channel = _raf.getChannel();
			_channel.position(_raf.length());
			_buf.rewind();
		}
		
		/**
		* Append a new record to the file with the appender's values. 
		*/
		@Override
		public void append() throws IOException {
			if (_basic) {
				try {
					assert _rbufSize == 0 && _rbuf == null : "In basic mode the shared buffer is empty";
				
					/* basic append, do not put the record in memory */
					if (_buf.position() == _buf.capacity()) {
						flush();
					}

				
					_buf.putLong(this.openTime);
					_buf.putDouble(this.openPrice);
					_buf.putLong(this.closeTime);
					_buf.putDouble(this.closePrice);
					_buf.put((byte) (this.isGain? 1 : 0));
					_buf.put((byte) (this.isClosed? 1 : 0));
					_buf.put((byte) (this.isLong? 1 : 0));
					_buf.putInt(this.orderId);
					_buf.put(this.openingCount);
					_buf.putLong(this.opening0);
					_buf.putLong(this.opening1);
					_buf.putLong(this.openPhysicalTime);
					_buf.putLong(this.closePhysicalTime);
					_buf.putLong(this.eventPhysicalTime);
					_buf.putInt(this.opening0_childType);
					_buf.putInt(this.opening1_childType);
					_buf.putInt(this.opening0_orderId);
					_buf.putInt(this.opening1_orderId);

					_size++;
					
					return;
				} catch (Exception e) {
					_size = fsize();
					throw e;
				}
			}

				/* regular append, put the record in the shared buffer */
				_writeLock.lock();
				try {
					if (_rbufSize == _rbuf.length) {
						if (_memory) {
							int newSize = _rbufSize * 2;
							Record[] b = new Record[newSize];
							System.arraycopy(_rbuf, 0, b, 0, _rbufSize);
							for(int i = 0; i < _rbufSize; i++) {
								b[_rbufSize + i] = new Record();
							}
							_rbuf = b;
						} else {
							flush();
						}
					}	
					Record r = _rbuf[_rbufSize];
					r.openTime = this.openTime;
					r.openPrice = this.openPrice;
					r.closeTime = this.closeTime;
					r.closePrice = this.closePrice;
					r.isGain = this.isGain;
					r.isClosed = this.isClosed;
					r.isLong = this.isLong;
					r.orderId = this.orderId;
					r.openingCount = this.openingCount;
					r.opening0 = this.opening0;
					r.opening1 = this.opening1;
					r.openPhysicalTime = this.openPhysicalTime;
					r.closePhysicalTime = this.closePhysicalTime;
					r.eventPhysicalTime = this.eventPhysicalTime;
					r.opening0_childType = this.opening0_childType;
					r.opening1_childType = this.opening1_childType;
					r.opening0_orderId = this.opening0_orderId;
					r.opening1_orderId = this.opening1_orderId;
					_rbufSize++;
					_size++;
				} catch (Exception e) {
					_size = fsize() + _rbufSize;
					throw e;
				} finally {
					_writeLock.unlock();
				}
			_session.modified();
		}

		/**
		 * Append to the file a copy of the given record.
		 */
		@Override
		public void append(Record record) throws IOException {	
			if (_basic) {
				this.openTime = record.openTime;
				this.openPrice = record.openPrice;
				this.closeTime = record.closeTime;
				this.closePrice = record.closePrice;
				this.isGain = record.isGain;
				this.isClosed = record.isClosed;
				this.isLong = record.isLong;
				this.orderId = record.orderId;
				this.openingCount = record.openingCount;
				this.opening0 = record.opening0;
				this.opening1 = record.opening1;
				this.openPhysicalTime = record.openPhysicalTime;
				this.closePhysicalTime = record.closePhysicalTime;
				this.eventPhysicalTime = record.eventPhysicalTime;
				this.opening0_childType = record.opening0_childType;
				this.opening1_childType = record.opening1_childType;
				this.opening0_orderId = record.opening0_orderId;
				this.opening1_orderId = record.opening1_orderId;
				append();
				return;
			}

			_writeLock.lock();
			try {										
				if (_rbufSize == _rbuf.length) {
					if (_memory) {
						int newSize = _rbufSize * 2;
						Record[] b = new Record[newSize];
						System.arraycopy(_rbuf, 0, b, 0, _rbufSize);
						for(int i = 0; i < _rbufSize; i++) {
							b[_rbufSize + i] = new Record();
						}
						_rbuf = b;
					} else {
						flush();
					}
				}	
				Record r = _rbuf[_rbufSize];
				r.openTime = record.openTime;
				r.openPrice = record.openPrice;
				r.closeTime = record.closeTime;
				r.closePrice = record.closePrice;
				r.isGain = record.isGain;
				r.isClosed = record.isClosed;
				r.isLong = record.isLong;
				r.orderId = record.orderId;
				r.openingCount = record.openingCount;
				r.opening0 = record.opening0;
				r.opening1 = record.opening1;
				r.openPhysicalTime = record.openPhysicalTime;
				r.closePhysicalTime = record.closePhysicalTime;
				r.eventPhysicalTime = record.eventPhysicalTime;
				r.opening0_childType = record.opening0_childType;
				r.opening1_childType = record.opening1_childType;
				r.opening0_orderId = record.opening0_orderId;
				r.opening1_orderId = record.opening1_orderId;
				_rbufSize++;
				_size++;
			} catch (Exception e) {
				_size = fsize() + _rbufPos;
				throw e;
			} finally {
				_writeLock.unlock();
			}
			_session.modified();
		}
		
		/**
		 * <p>
		 * Warning! Do not use this method if you don't know what are you doing!
		 * </p>
		 * <p>
		 * Append the record but not copy it. Use this method if you are fully sure that you will not touch that record instance again, 
		 * else the data will be corrupted.
		 * </p> 
		 */
		@Override
		public void append_ref_unsafe(Record record) throws IOException {
			if (_basic) {
				this.openTime = record.openTime;
				this.openPrice = record.openPrice;
				this.closeTime = record.closeTime;
				this.closePrice = record.closePrice;
				this.isGain = record.isGain;
				this.isClosed = record.isClosed;
				this.isLong = record.isLong;
				this.orderId = record.orderId;
				this.openingCount = record.openingCount;
				this.opening0 = record.opening0;
				this.opening1 = record.opening1;
				this.openPhysicalTime = record.openPhysicalTime;
				this.closePhysicalTime = record.closePhysicalTime;
				this.eventPhysicalTime = record.eventPhysicalTime;
				this.opening0_childType = record.opening0_childType;
				this.opening1_childType = record.opening1_childType;
				this.opening0_orderId = record.opening0_orderId;
				this.opening1_orderId = record.opening1_orderId;
				append();
				return;
			}	

			_writeLock.lock();	
			try {											
				if (_rbufSize == _rbuf.length) {
					if (_memory) {
						int newSize = _rbufSize * 2;
						Record[] b = new Record[newSize];
						System.arraycopy(_rbuf, 0, b, 0, _rbufSize);
						for(int i = 0; i < _rbufSize; i++) {
							b[_rbufSize + i] = new Record();
						}
						_rbuf = b;
					} else {
						flush();
					}
				}	
				_rbuf[_rbufSize] = record;
				_rbufSize++;
				_size++;
			} catch (Exception e) {
				_size = fsize() + _rbufSize;
				throw e;
			} finally {
				_writeLock.unlock();
			}
			_session.modified();
		}


		/**
		* Write pending records, it clears the buffer.
		* @throws IOException If there is any I/O error.
		*/
		public void flush() throws IOException {	
			if (_basic) {
				_buf.limit(_buf.position());
				_buf.rewind();
				_channel.write(_buf);
				_buf.limit(_buf.capacity());
				_buf.rewind();
				_rbufPos = fsize();
				return;
			}
			
			if (_memory) {
				return;
			}

			for(int j = 0; j < _rbufSize; j++) {						
				Record r = _rbuf[j];
				_buf.putLong(r.openTime);
				_buf.putDouble(r.openPrice);
				_buf.putLong(r.closeTime);
				_buf.putDouble(r.closePrice);
				_buf.put((byte) (r.isGain? 1 : 0));
				_buf.put((byte) (r.isClosed? 1 : 0));
				_buf.put((byte) (r.isLong? 1 : 0));
				_buf.putInt(r.orderId);
				_buf.put(r.openingCount);
				_buf.putLong(r.opening0);
				_buf.putLong(r.opening1);
				_buf.putLong(r.openPhysicalTime);
				_buf.putLong(r.closePhysicalTime);
				_buf.putLong(r.eventPhysicalTime);
				_buf.putInt(r.opening0_childType);
				_buf.putInt(r.opening1_childType);
				_buf.putInt(r.opening0_orderId);
				_buf.putInt(r.opening1_orderId);
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 96);
			_channel.write(_buf);
			_buf.limit(_buf.capacity());
			_buf.rewind();
			
			_writeLock.lock();		
			try {			
				_rbufSize = 0;
				_rbufPos = fsize();
			} finally {
				_writeLock.unlock();
			}
		}
		
		/**
		* Flush the pending records and close the associated files.
		*/	
		@Override
		public void close() throws IOException {
			if (_memory) return;
			
			if (_basic || _rbufSize > 0) {
				flush();
			}
			disconnectFile();	
		}
					
		/**
		* Get the associated MDB instance.
		*/
		@Override
		public TradeMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.openTime = this.openTime;
			r.openPrice = this.openPrice;
			r.closeTime = this.closeTime;
			r.closePrice = this.closePrice;
			r.isGain = this.isGain;
			r.isClosed = this.isClosed;
			r.isLong = this.isLong;
			r.orderId = this.orderId;
			r.openingCount = this.openingCount;
			r.opening0 = this.opening0;
			r.opening1 = this.opening1;
			r.openPhysicalTime = this.openPhysicalTime;
			r.closePhysicalTime = this.closePhysicalTime;
			r.eventPhysicalTime = this.eventPhysicalTime;
			r.opening0_childType = this.opening0_childType;
			r.opening1_childType = this.opening1_childType;
			r.opening0_orderId = this.opening0_orderId;
			r.opening1_orderId = this.opening1_orderId;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.openTime = record.openTime;
			this.openPrice = record.openPrice;
			this.closeTime = record.closeTime;
			this.closePrice = record.closePrice;
			this.isGain = record.isGain;
			this.isClosed = record.isClosed;
			this.isLong = record.isLong;
			this.orderId = record.orderId;
			this.openingCount = record.openingCount;
			this.opening0 = record.opening0;
			this.opening1 = record.opening1;
			this.openPhysicalTime = record.openPhysicalTime;
			this.closePhysicalTime = record.closePhysicalTime;
			this.eventPhysicalTime = record.eventPhysicalTime;
			this.opening0_childType = record.opening0_childType;
			this.opening1_childType = record.opening1_childType;
			this.opening0_orderId = record.opening0_orderId;
			this.opening1_orderId = record.opening1_orderId;
		}

	}

	/**
	* Return the singleton appender. For more details see the {@link Appender} class.
	*/
	@Override
	public Appender appender() throws IOException {
		if (!_connectedToFiles) {
			assert !getFile().exists();		
			throw new FileNotFoundException(
					"This MDB was disconnected from the file "
							+ getFile()
							+ ", possibly because a backup restore deleted it.");
		}
		
		if (_session != null) {
			_session.appenderRequested(this);
		}
			
		if (_appender == null) {
			_appender = new Appender();
		}
		return _appender;
	}
		
	/**
	* If the appender is created.
	*
	* @return True in case the appender was requested before.
	*/
	public boolean isAppenderCreated() {
		return _appender != null;
	}
		
	/**
	* If the appender was created and is open.
	*/
	@Override
	public boolean isAppenderOpen() {
		return _appender != null && _appender._channel.isOpen();
	}
		
	/**
	* Close the appender. If no appender was created, do nothing.
	*/
	@Override
	public void closeAppender() throws IOException {
		if (_appender != null) {
			_appender.close();
		}
	}
	
	/**
	* Closes the file handlers. This method is used by the session to restore backups.
	* Do not use this method if you don't know what are you doing.
	*/ 
	@Override
	protected void disconnectFile() throws IOException {
		_writeLock.lock();
		try {
			_connectedToFiles = false;
			if (_appender != null) {
				_appender.disconnectFile();	
			}
			// do not close the underlaying cursor
			// they are supposed to be closed
			// by the session because they are deferred
			_localRandCursor.remove();
			_localSeqCursor.remove();
		} finally {
			_writeLock.unlock();
		}
	}
	
	/**
	* Create the file handlers. This is used for the session backup/recovery methods. 
	* Do not use this method if you don't know what are you doing.
	*/
	@Override
	protected void reconnectFile() throws IOException {
		if (_appender != null) {
			_appender.reconnectFile();
			for(int i = 0; i < _bufferSize; i++) {
				_rbuf[i] = new Record();
			}
		}
		
		if (!_basic) {
			_rbufSize = 0;
		}
		
		_size = fsize();
		_rbufPos = _size;
		_connectedToFiles = true;			
	}
	
	/**
	* Flush the appender. 
	* @see Appender#flush()
	*/
	@Override
	public void flushAppender() throws IOException {
		if (_appender != null) {
			_appender.flush();
		}
	}


	/**
	 * <p>
	 * This class provides a sequential cursor API.
	 * </p>
	 * <p>
	 * This cursor is the basic, faster and more controlled way to retrieve sequential
	 * data, it is used internally by other elements like the "select" methods.
	 * </p>
	 * <p>
	 * The common way to use a sequential cursor is:
	 * </p>
	 * 
	 * <pre>
	 * TradeMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.openTime); 
	 * }
	 * c.close();
	 * </pre>
	 * <p>
	 * A cursor contains the same "column fields" of a record, 
	 * when the <code>next()</code> method is called, the cursor
	 * "column fields" are updated.
	 * </p>
	 * <p>
	 * Warning: remember always to close the cursor, a common mistake
	 * is to try to delete a database when there are opened cursors: 
	 * an open cursor blocks a file (at least in Windows).  
	 * </p>
	 */
	public final class Cursor implements ISeqCursor<Record> {
		private long _stop;
		private long _row;
		private ByteBuffer _buffer;
		FileChannel _channel;
		private RandomAccessFile _raf;
		private long _len;
		private boolean _open;
		public long openTime; /* 0 */
		public double openPrice; /* 1 */
		public long closeTime; /* 2 */
		public double closePrice; /* 3 */
		public boolean isGain; /* 4 */
		public boolean isClosed; /* 5 */
		public boolean isLong; /* 6 */
		public int orderId; /* 7 */
		public byte openingCount; /* 8 */
		public long opening0; /* 9 */
		public long opening1; /* 10 */
		public long openPhysicalTime; /* 11 */
		public long closePhysicalTime; /* 12 */
		public long eventPhysicalTime; /* 13 */
		public int opening0_childType; /* 14 */
		public int opening1_childType; /* 15 */
		public int opening0_orderId; /* 16 */
		public int opening1_orderId; /* 17 */
		
		/**
		* Cursor constructor.
		*/
		Cursor(RandomAccessFile raf, FileChannel channel, long start, long stop, int bufferSize) throws IOException {
			super();
			_openCursorCount.incrementAndGet();
			_open = true;
			_len = _size;
			_stop = Math.min(stop, _len - 1);
			_row = start;
			if (!_memory) {
				_raf = raf;
				_channel = channel;
				_channel.position(start * 96);
				_buffer = ByteBuffer.allocate(bufferSize * 96);
				_buffer.position(_buffer.capacity());
			}
			_session.cursorCreated(this);
		}
		
		@Override
		public void reset(long start, long stop) throws IOException {
			if (!_open) throw new ClosedCursorException(this);
			
			long start2 = start < 0? 0 : start;
			_len = _size;
			_stop = stop < start2? start2 : Math.min(stop, _len - 1);
			_row = start;
			if (!_memory) {
				synchronized (this) {
					_channel.position(start * 96);
					_buffer.position(_buffer.capacity());
				}
			}
		}
			
		/**
		* Fetch the data and move the cursor to the next record.
		*/
		@Override
		public synchronized boolean next() throws IOException {
			// if (!_open) throw new ClosedCursorException(this);
			
			if (_row > _stop || _len == 0) return false;
			
			if (!_basic) {
				_readLock.lock();	
				try {
					if (_rbufSize > 0 && _row >= _rbufPos) {
						Record r;
						r = _rbuf[(int) (_row - _rbufPos)];
						this.openTime = r.openTime;
						this.openPrice = r.openPrice;
						this.closeTime = r.closeTime;
						this.closePrice = r.closePrice;
						this.isGain = r.isGain;
						this.isClosed = r.isClosed;
						this.isLong = r.isLong;
						this.orderId = r.orderId;
						this.openingCount = r.openingCount;
						this.opening0 = r.opening0;
						this.opening1 = r.opening1;
						this.openPhysicalTime = r.openPhysicalTime;
						this.closePhysicalTime = r.closePhysicalTime;
						this.eventPhysicalTime = r.eventPhysicalTime;
						this.opening0_childType = r.opening0_childType;
						this.opening1_childType = r.opening1_childType;
						this.opening0_orderId = r.opening0_orderId;
						this.opening1_orderId = r.opening1_orderId;
						_row ++;
						return true;	
					}
				} finally {
					_readLock.unlock();
				} 
			}
			
			if (!_memory) {
				if (_buffer.position() == _buffer.capacity()) {
					_buffer.rewind();
					_channel.read(_buffer);
					_buffer.rewind();
				}
				
				this.openTime = _buffer.getLong();
				this.openPrice = _buffer.getDouble();
				this.closeTime = _buffer.getLong();
				this.closePrice = _buffer.getDouble();
				this.isGain = _buffer.get() == 0? false : true;
				this.isClosed = _buffer.get() == 0? false : true;
				this.isLong = _buffer.get() == 0? false : true;
				this.orderId = _buffer.getInt();
				this.openingCount = _buffer.get();
				this.opening0 = _buffer.getLong();
				this.opening1 = _buffer.getLong();
				this.openPhysicalTime = _buffer.getLong();
				this.closePhysicalTime = _buffer.getLong();
				this.eventPhysicalTime = _buffer.getLong();
				this.opening0_childType = _buffer.getInt();
				this.opening1_childType = _buffer.getInt();
				this.opening0_orderId = _buffer.getInt();
				this.opening1_orderId = _buffer.getInt();
				_row ++;
			}
			return true;
		}
		
		
		/*
		* Close the cursor. Do that when the cursor is not needed anymore.
		*/
		@Override
		public synchronized void close() throws IOException {
			if (!_open) return;
			_open = false;
			_openCursorCount.decrementAndGet();
			assert _openCursorCount.get() >= 0;
			
			if (!_memory) {
				_raf.close();
			}
			_session.cursorClosed(this);
		}
		
		@Override
		protected void finalize() {
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		* To check if the cursor is open.
		*/	
		@Override
		public synchronized boolean isOpen() {
			return _open;
		}
		
		/**
		* The current position of the cursor.
		*/	
		@Override
		public long position() {
			return _row;
		}
		
		/**
		* Create a record with the cursor data.
		* You can use this method if you need to store the data in a collection.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.openTime = this.openTime;
			r.openPrice = this.openPrice;
			r.closeTime = this.closeTime;
			r.closePrice = this.closePrice;
			r.isGain = this.isGain;
			r.isClosed = this.isClosed;
			r.isLong = this.isLong;
			r.orderId = this.orderId;
			r.openingCount = this.openingCount;
			r.opening0 = this.opening0;
			r.opening1 = this.opening1;
			r.openPhysicalTime = this.openPhysicalTime;
			r.closePhysicalTime = this.closePhysicalTime;
			r.eventPhysicalTime = this.eventPhysicalTime;
			r.opening0_childType = this.opening0_childType;
			r.opening1_childType = this.opening1_childType;
			r.opening0_orderId = this.opening0_orderId;
			r.opening1_orderId = this.opening1_orderId;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public TradeMDB getMDB() {
			return TradeMDB.this;
		}
	}
	
	/**
	* Create a cursor to iterate from position <code>start</code> to <code>stop</code>.
	*
	* @param start 
	* 			Start position.
	* @param stop 
	* 			Stop position.
	*/
	@SuppressWarnings("resource")	
	@Override
	public Cursor cursor(long start, long stop) throws IOException {
		if (_memory) {
			return new Cursor(null, null, start, stop, _bufferSize);
		}
		RandomAccessFile raf = new RandomAccessFile(getFile().getAbsolutePath(), "r");
		return new Cursor(raf, raf.getChannel(), start, stop, _bufferSize);
	}
	
	@Override
	public Cursor cursor(long start) throws IOException {
		return (Cursor) super.cursor(start);
	}
	
	@Override
	public Cursor cursor() throws IOException {
		return (Cursor) super.cursor();
	}



		/**
		* <p>
		* This class provides a random-access cursor API. In case you want to
		* retrieve sequential data, the best is to use a sequential cursor (
		* {@link Cursor}).
		* </p>
		* <p>
		* This cursor is the basic, faster and more controlled way to retrieve
		* random data, it is used internally by other elements like the
		* {@link MDBList} class.
		* </p>
		* <p>
		* The common way to use a random cursor is:
		* </p>
		* 
		* <pre>
		* TradeMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.openTime); 
		* ...
		* c.close();
		* </pre>
		* <p>
		* A random cursor contains the same "column fields" of a record, when the
		* <code>seek()</code> method is called, the cursor "column fields" are
		* updated.
		* </p>
		* <p>
		* Warning: remember always to close the cursor, a common mistake is to try
		* to delete a database when there are opened cursors: an open cursor blocks
		* a file (at least in Windows). 
		* </p>
		* <p>
		* Usually you need only one random cursor per file, so probably you want to keep this
		* cursor open while the session is alive, then you can use "defer" the cursor, this mean, 
		* the cursor will be closed automatically before to close the session. See the {@link MDBSession#defer(ICursor)} method. 
		* </p>
		* @see MDBSession#defer(ICursor)
		* @see Cursor
		*/
		public final class RandomCursor implements IRandomCursor<Record> {
			private ByteBuffer _buffer;
			ByteBuffer _buffer_openTime; // used by index-of-openTime method.
			ByteBuffer _buffer_closeTime; // used by index-of-closeTime method.
			ByteBuffer _buffer_openPhysicalTime; // used by index-of-openPhysicalTime method.
			ByteBuffer _buffer_closePhysicalTime; // used by index-of-closePhysicalTime method.
			ByteBuffer _buffer_eventPhysicalTime; // used by index-of-eventPhysicalTime method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			public long openTime; /* 0 */
			public double openPrice; /* 1 */
			public long closeTime; /* 2 */
			public double closePrice; /* 3 */
			public boolean isGain; /* 4 */
			public boolean isClosed; /* 5 */
			public boolean isLong; /* 6 */
			public int orderId; /* 7 */
			public byte openingCount; /* 8 */
			public long opening0; /* 9 */
			public long opening1; /* 10 */
			public long openPhysicalTime; /* 11 */
			public long closePhysicalTime; /* 12 */
			public long eventPhysicalTime; /* 13 */
			public int opening0_childType; /* 14 */
			public int opening1_childType; /* 15 */
			public int opening0_orderId; /* 16 */
			public int opening1_orderId; /* 17 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(96);
					_buffer_openTime = ByteBuffer.allocate(8);
					_buffer_closeTime = ByteBuffer.allocate(8);
					_buffer_openPhysicalTime = ByteBuffer.allocate(8);
					_buffer_closePhysicalTime = ByteBuffer.allocate(8);
					_buffer_eventPhysicalTime = ByteBuffer.allocate(8);
				}
				_session.cursorCreated(this);
			}
			
			@Override
			public synchronized void seek(long position) throws IOException {
				// if (!_open) throw new ClosedCursorException(this);
				if (position < 0 || position >= _size) throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + _size);
				
				if (!_basic) {
					_readLock.lock();
					try {
						if (_rbufSize > 0 && position >= _rbufPos) {
							_row = position;
							Record r;
							r = _rbuf[(int) (position - _rbufPos)];
							this.openTime = r.openTime;
							this.openPrice = r.openPrice;
							this.closeTime = r.closeTime;
							this.closePrice = r.closePrice;
							this.isGain = r.isGain;
							this.isClosed = r.isClosed;
							this.isLong = r.isLong;
							this.orderId = r.orderId;
							this.openingCount = r.openingCount;
							this.opening0 = r.opening0;
							this.opening1 = r.opening1;
							this.openPhysicalTime = r.openPhysicalTime;
							this.closePhysicalTime = r.closePhysicalTime;
							this.eventPhysicalTime = r.eventPhysicalTime;
							this.opening0_childType = r.opening0_childType;
							this.opening1_childType = r.opening1_childType;
							this.opening0_orderId = r.opening0_orderId;
							this.opening1_orderId = r.opening1_orderId;
							return;					
						}
					} finally {
						_readLock.unlock();
					}
				}
				
				assert !_memory;
				
				_row = position;
				_buffer.rewind();
				_channel.read(_buffer, position * 96);
				_buffer.rewind();
				this.openTime = _buffer.getLong();
				this.openPrice = _buffer.getDouble();
				this.closeTime = _buffer.getLong();
				this.closePrice = _buffer.getDouble();
				this.isGain = _buffer.get() == 0? false : true;
				this.isClosed = _buffer.get() == 0? false : true;
				this.isLong = _buffer.get() == 0? false : true;
				this.orderId = _buffer.getInt();
				this.openingCount = _buffer.get();
				this.opening0 = _buffer.getLong();
				this.opening1 = _buffer.getLong();
				this.openPhysicalTime = _buffer.getLong();
				this.closePhysicalTime = _buffer.getLong();
				this.eventPhysicalTime = _buffer.getLong();
				this.opening0_childType = _buffer.getInt();
				this.opening1_childType = _buffer.getInt();
				this.opening0_orderId = _buffer.getInt();
				this.opening1_orderId = _buffer.getInt();
			}
			
			@Override
			public void seekLast() throws IOException {
				seek(_size - 1);
			}
			
			@Override
			public void seekFirst() throws IOException {
				seek(0);
			}
			
			
			/**
			* Close the cursor. Do that when the cursor is not needed anymore.
			*/
			@Override
			public synchronized void close() throws IOException {
				if (!_open) return;
				_open = false;
			    _openCursorCount.decrementAndGet();
			    assert _openCursorCount.get() >= 0; 
							    
			    if (!_memory) {
					_raf.close();
				}

				_session.cursorClosed(this);
			}
			
			@Override
			protected void finalize() {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			/**
			* To check if the cursor is open.
			*/
			@Override
			public synchronized boolean isOpen() {
				return _open;
			}
			
			/**
			* The current position of the cursor.
			*/
			@Override
			public long position() {
				return _row;
			}
			
			/**
			* Create a record with the cursor data.
			* You can use this method if you need to store the data in a collection.
			*/
			@Override
			public Record toRecord() {
				Record r = new Record();
				r.openTime = this.openTime;
				r.openPrice = this.openPrice;
				r.closeTime = this.closeTime;
				r.closePrice = this.closePrice;
				r.isGain = this.isGain;
				r.isClosed = this.isClosed;
				r.isLong = this.isLong;
				r.orderId = this.orderId;
				r.openingCount = this.openingCount;
				r.opening0 = this.opening0;
				r.opening1 = this.opening1;
				r.openPhysicalTime = this.openPhysicalTime;
				r.closePhysicalTime = this.closePhysicalTime;
				r.eventPhysicalTime = this.eventPhysicalTime;
				r.opening0_childType = this.opening0_childType;
				r.opening1_childType = this.opening1_childType;
				r.opening0_orderId = this.opening0_orderId;
				r.opening1_orderId = this.opening1_orderId;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public TradeMDB getMDB() {
				return TradeMDB.this;
			}
		}
		
	/**
	* Create a random cursor.
	* See the class {@link RandomCursor} for more details.
	*
	*/
	@Override
	public RandomCursor randomCursor() throws IOException {
		return new RandomCursor();
	}


	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(Cursor obj, ByteBuffer buffer) {
		buffer.putLong(obj.openTime); 
		buffer.putDouble(obj.openPrice); 
		buffer.putLong(obj.closeTime); 
		buffer.putDouble(obj.closePrice); 
		buffer.put(obj.isGain ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isClosed ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isLong ? (byte) 1 : (byte) 0); 
		buffer.putInt(obj.orderId); 
		buffer.put(obj.openingCount); 
		buffer.putLong(obj.opening0); 
		buffer.putLong(obj.opening1); 
		buffer.putLong(obj.openPhysicalTime); 
		buffer.putLong(obj.closePhysicalTime); 
		buffer.putLong(obj.eventPhysicalTime); 
		buffer.putInt(obj.opening0_childType); 
		buffer.putInt(obj.opening1_childType); 
		buffer.putInt(obj.opening0_orderId); 
		buffer.putInt(obj.opening1_orderId); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(RandomCursor obj, ByteBuffer buffer) {
		buffer.putLong(obj.openTime); 
		buffer.putDouble(obj.openPrice); 
		buffer.putLong(obj.closeTime); 
		buffer.putDouble(obj.closePrice); 
		buffer.put(obj.isGain ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isClosed ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isLong ? (byte) 1 : (byte) 0); 
		buffer.putInt(obj.orderId); 
		buffer.put(obj.openingCount); 
		buffer.putLong(obj.opening0); 
		buffer.putLong(obj.opening1); 
		buffer.putLong(obj.openPhysicalTime); 
		buffer.putLong(obj.closePhysicalTime); 
		buffer.putLong(obj.eventPhysicalTime); 
		buffer.putInt(obj.opening0_childType); 
		buffer.putInt(obj.opening1_childType); 
		buffer.putInt(obj.opening0_orderId); 
		buffer.putInt(obj.opening1_orderId); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(Record obj, ByteBuffer buffer) {
		buffer.putLong(obj.openTime); 
		buffer.putDouble(obj.openPrice); 
		buffer.putLong(obj.closeTime); 
		buffer.putDouble(obj.closePrice); 
		buffer.put(obj.isGain ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isClosed ? (byte) 1 : (byte) 0); 
		buffer.put(obj.isLong ? (byte) 1 : (byte) 0); 
		buffer.putInt(obj.orderId); 
		buffer.put(obj.openingCount); 
		buffer.putLong(obj.opening0); 
		buffer.putLong(obj.opening1); 
		buffer.putLong(obj.openPhysicalTime); 
		buffer.putLong(obj.closePhysicalTime); 
		buffer.putLong(obj.eventPhysicalTime); 
		buffer.putInt(obj.opening0_childType); 
		buffer.putInt(obj.opening1_childType); 
		buffer.putInt(obj.opening0_orderId); 
		buffer.putInt(obj.opening1_orderId); 
	}
	


	/**
	 * Replace the record at the given <code>index</code>.
	 *
	 * @param index The index to update.
	 * @param val_openTime The value for column openTime.
	 * @param val_openPrice The value for column openPrice.
	 * @param val_closeTime The value for column closeTime.
	 * @param val_closePrice The value for column closePrice.
	 * @param val_isGain The value for column isGain.
	 * @param val_isClosed The value for column isClosed.
	 * @param val_isLong The value for column isLong.
	 * @param val_orderId The value for column orderId.
	 * @param val_openingCount The value for column openingCount.
	 * @param val_opening0 The value for column opening0.
	 * @param val_opening1 The value for column opening1.
	 * @param val_openPhysicalTime The value for column openPhysicalTime.
	 * @param val_closePhysicalTime The value for column closePhysicalTime.
	 * @param val_eventPhysicalTime The value for column eventPhysicalTime.
	 * @param val_opening0_childType The value for column opening0_childType.
	 * @param val_opening1_childType The value for column opening1_childType.
	 * @param val_opening0_orderId The value for column opening0_orderId.
	 * @param val_opening1_orderId The value for column opening1_orderId.
	* @throws IOException If there is any I/O error.
	 */
	public void replace(long index 
							, long val_openTime
							, double val_openPrice
							, long val_closeTime
							, double val_closePrice
							, boolean val_isGain
							, boolean val_isClosed
							, boolean val_isLong
							, int val_orderId
							, byte val_openingCount
							, long val_opening0
							, long val_opening1
							, long val_openPhysicalTime
							, long val_closePhysicalTime
							, long val_eventPhysicalTime
							, int val_opening0_childType
							, int val_opening1_childType
							, int val_opening0_orderId
							, int val_opening1_orderId
			) throws IOException {
		if (index < 0 || index >= _size) {
			throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
		}
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						Record r = _rbuf[pos];
						r.openTime = val_openTime;
						r.openPrice = val_openPrice;
						r.closeTime = val_closeTime;
						r.closePrice = val_closePrice;
						r.isGain = val_isGain;
						r.isClosed = val_isClosed;
						r.isLong = val_isLong;
						r.orderId = val_orderId;
						r.openingCount = val_openingCount;
						r.opening0 = val_opening0;
						r.opening1 = val_opening1;
						r.openPhysicalTime = val_openPhysicalTime;
						r.closePhysicalTime = val_closePhysicalTime;
						r.eventPhysicalTime = val_eventPhysicalTime;
						r.opening0_childType = val_opening0_childType;
						r.opening1_childType = val_opening1_childType;
						r.opening0_orderId = val_opening0_orderId;
						r.opening1_orderId = val_opening1_orderId;
					}
					return;
				} 				
			}
		
			_replaceBuffer.rewind();
			_replaceBuffer.putLong(val_openTime);
			_replaceBuffer.putDouble(val_openPrice);
			_replaceBuffer.putLong(val_closeTime);
			_replaceBuffer.putDouble(val_closePrice);
			_replaceBuffer.put((byte) (val_isGain ? 1 : 0));
			_replaceBuffer.put((byte) (val_isClosed ? 1 : 0));
			_replaceBuffer.put((byte) (val_isLong ? 1 : 0));
			_replaceBuffer.putInt(val_orderId);
			_replaceBuffer.put(val_openingCount);
			_replaceBuffer.putLong(val_opening0);
			_replaceBuffer.putLong(val_opening1);
			_replaceBuffer.putLong(val_openPhysicalTime);
			_replaceBuffer.putLong(val_closePhysicalTime);
			_replaceBuffer.putLong(val_eventPhysicalTime);
			_replaceBuffer.putInt(val_opening0_childType);
			_replaceBuffer.putInt(val_opening1_childType);
			_replaceBuffer.putInt(val_opening0_orderId);
			_replaceBuffer.putInt(val_opening1_orderId);
			_replaceBuffer.rewind();
			appender();
			_appender._channel.write(_replaceBuffer, index * 96);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	
	/**
	 * Update the record at the given <code>index</code>.
	 * Also you can use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)}.
	 *
	 * @param index The index to update.
	 * @param record Contains the data to set.
	 * @see #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)
	 * @throws IOException If there is any I/O error.
	 */
	public void replace(long index, Record record) throws IOException {
		replace(index 
					, record.openTime		
					, record.openPrice		
					, record.closeTime		
					, record.closePrice		
					, record.isGain		
					, record.isClosed		
					, record.isLong		
					, record.orderId		
					, record.openingCount		
					, record.opening0		
					, record.opening1		
					, record.openPhysicalTime		
					, record.closePhysicalTime		
					, record.eventPhysicalTime		
					, record.opening0_childType		
					, record.opening1_childType		
					, record.opening0_orderId		
					, record.opening1_orderId		
				);			
	}

	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "openTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "openTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_openTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].openTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_openTime.rewind();
			_replaceBuffer_openTime.putLong(value);
			_replaceBuffer_openTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_openTime, index * 96 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "openPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "openPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_openPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].openPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_openPrice.rewind();
			_replaceBuffer_openPrice.putDouble(value);
			_replaceBuffer_openPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_openPrice, index * 96 + 8);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "closeTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "closeTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_closeTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].closeTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_closeTime.rewind();
			_replaceBuffer_closeTime.putLong(value);
			_replaceBuffer_closeTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_closeTime, index * 96 + 16);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "closePrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "closePrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_closePrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].closePrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_closePrice.rewind();
			_replaceBuffer_closePrice.putDouble(value);
			_replaceBuffer_closePrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_closePrice, index * 96 + 24);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "isGain" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "isGain".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_isGain(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].isGain = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_isGain.rewind();
			_replaceBuffer_isGain.put((byte) (value ? 1 : 0));
			_replaceBuffer_isGain.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_isGain, index * 96 + 32);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "isClosed" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "isClosed".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_isClosed(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].isClosed = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_isClosed.rewind();
			_replaceBuffer_isClosed.put((byte) (value ? 1 : 0));
			_replaceBuffer_isClosed.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_isClosed, index * 96 + 33);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "isLong" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "isLong".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_isLong(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].isLong = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_isLong.rewind();
			_replaceBuffer_isLong.put((byte) (value ? 1 : 0));
			_replaceBuffer_isLong.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_isLong, index * 96 + 34);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "orderId" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "orderId".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_orderId(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].orderId = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_orderId.rewind();
			_replaceBuffer_orderId.putInt(value);
			_replaceBuffer_orderId.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_orderId, index * 96 + 35);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "openingCount" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "openingCount".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_openingCount(long index, byte value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].openingCount = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_openingCount.rewind();
			_replaceBuffer_openingCount.put(value);
			_replaceBuffer_openingCount.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_openingCount, index * 96 + 39);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening0" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening0".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening0(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening0 = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening0.rewind();
			_replaceBuffer_opening0.putLong(value);
			_replaceBuffer_opening0.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening0, index * 96 + 40);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening1" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening1".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening1(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening1 = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening1.rewind();
			_replaceBuffer_opening1.putLong(value);
			_replaceBuffer_opening1.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening1, index * 96 + 48);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "openPhysicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "openPhysicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_openPhysicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].openPhysicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_openPhysicalTime.rewind();
			_replaceBuffer_openPhysicalTime.putLong(value);
			_replaceBuffer_openPhysicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_openPhysicalTime, index * 96 + 56);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "closePhysicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "closePhysicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_closePhysicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].closePhysicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_closePhysicalTime.rewind();
			_replaceBuffer_closePhysicalTime.putLong(value);
			_replaceBuffer_closePhysicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_closePhysicalTime, index * 96 + 64);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "eventPhysicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "eventPhysicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_eventPhysicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].eventPhysicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_eventPhysicalTime.rewind();
			_replaceBuffer_eventPhysicalTime.putLong(value);
			_replaceBuffer_eventPhysicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_eventPhysicalTime, index * 96 + 72);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening0_childType" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening0_childType".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening0_childType(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening0_childType = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening0_childType.rewind();
			_replaceBuffer_opening0_childType.putInt(value);
			_replaceBuffer_opening0_childType.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening0_childType, index * 96 + 80);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening1_childType" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening1_childType".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening1_childType(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening1_childType = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening1_childType.rewind();
			_replaceBuffer_opening1_childType.putInt(value);
			_replaceBuffer_opening1_childType.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening1_childType, index * 96 + 84);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening0_orderId" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening0_orderId".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening0_orderId(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening0_orderId = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening0_orderId.rewind();
			_replaceBuffer_opening0_orderId.putInt(value);
			_replaceBuffer_opening0_orderId.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening0_orderId, index * 96 + 88);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "opening1_orderId" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, long, double, boolean, boolean, boolean, int, byte, long, long, long, long, long, int, int, int, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "opening1_orderId".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_opening1_orderId(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].opening1_orderId = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_opening1_orderId.rewind();
			_replaceBuffer_opening1_orderId.putInt(value);
			_replaceBuffer_opening1_orderId.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_opening1_orderId, index * 96 + 92);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}



	/**
	 * Like {@link MDB#select(ISeqCursor, long, long)}, but starts at the index of <code>lower</code> 
	 * and stops at the index of <code>upper</code>.
	 * @param randCursor 
	 *			The random cursor used to find the start and stop positions.
	 * @param cursor
	 *			The sequential cursor used to collect the data.
	 * @param lower
	 *			The lower value of <code>openTime</code>.
	 * @param upper
	 *			The upper value of <code>openTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_OpenTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfOpenTime(randCursor, lower) - 1;

		if (start < 0) {
			start = 0;
		}
	
		Record[] data = new Record[10];
		int size = 0;
	
		cursor.reset(start, _size - 1);
		while (cursor.next()) {
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = cursor.toRecord();
			size++;
		
			if (cursor.openTime > upper) {
				break;
			}
		}

		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}
	
	/**
	* Like {@link #select__where_OpenTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
	* @param randCursor
	*			The random cursor used to find the indexes and collect sparse data.
	* @param cursor
	*			The sequential cursor used to collect the continuous data. There are cases where the data is not sparse cause the small range of search.
	* @param lower
	*			The lower value to search.
	* @param upper
	*			The upper value to search.
	* @param maxLen
	*			The maximum number of records to collect.
	* @return The array of sparse data.    
	* @see MDB#select_sparse(IRandomCursor, ISeqCursor, long, long, int)
	* @throws IOException If there is any I/O error.
	*/
	public Record[] select_sparse__where_OpenTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfOpenTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfOpenTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_OpenTime_in(randCursor, cursor, lower, upper);
		}
	
		Record[] data = new Record[10];
		int size = 0;
		long pos = start;
		
		while (pos <= stop) {
			randCursor.seek(pos);
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = randCursor.toRecord();
			size++;
		
			if (randCursor.openTime > upper) {
				break;
			}
			pos += step;
		}
	
		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}	

	/**
	 * Like {@link MDB#select(ISeqCursor, long, long)}, but starts at the index of <code>lower</code> 
	 * and stops at the index of <code>upper</code>.
	 * @param randCursor 
	 *			The random cursor used to find the start and stop positions.
	 * @param cursor
	 *			The sequential cursor used to collect the data.
	 * @param lower
	 *			The lower value of <code>closeTime</code>.
	 * @param upper
	 *			The upper value of <code>closeTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_CloseTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfCloseTime(randCursor, lower) - 1;

		if (start < 0) {
			start = 0;
		}
	
		Record[] data = new Record[10];
		int size = 0;
	
		cursor.reset(start, _size - 1);
		while (cursor.next()) {
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = cursor.toRecord();
			size++;
		
			if (cursor.closeTime > upper) {
				break;
			}
		}

		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}
	
	/**
	* Like {@link #select__where_CloseTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
	* @param randCursor
	*			The random cursor used to find the indexes and collect sparse data.
	* @param cursor
	*			The sequential cursor used to collect the continuous data. There are cases where the data is not sparse cause the small range of search.
	* @param lower
	*			The lower value to search.
	* @param upper
	*			The upper value to search.
	* @param maxLen
	*			The maximum number of records to collect.
	* @return The array of sparse data.    
	* @see MDB#select_sparse(IRandomCursor, ISeqCursor, long, long, int)
	* @throws IOException If there is any I/O error.
	*/
	public Record[] select_sparse__where_CloseTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfCloseTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfCloseTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_CloseTime_in(randCursor, cursor, lower, upper);
		}
	
		Record[] data = new Record[10];
		int size = 0;
		long pos = start;
		
		while (pos <= stop) {
			randCursor.seek(pos);
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = randCursor.toRecord();
			size++;
		
			if (randCursor.closeTime > upper) {
				break;
			}
			pos += step;
		}
	
		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}	

	/**
	 * Like {@link MDB#select(ISeqCursor, long, long)}, but starts at the index of <code>lower</code> 
	 * and stops at the index of <code>upper</code>.
	 * @param randCursor 
	 *			The random cursor used to find the start and stop positions.
	 * @param cursor
	 *			The sequential cursor used to collect the data.
	 * @param lower
	 *			The lower value of <code>openPhysicalTime</code>.
	 * @param upper
	 *			The upper value of <code>openPhysicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_OpenPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfOpenPhysicalTime(randCursor, lower) - 1;

		if (start < 0) {
			start = 0;
		}
	
		Record[] data = new Record[10];
		int size = 0;
	
		cursor.reset(start, _size - 1);
		while (cursor.next()) {
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = cursor.toRecord();
			size++;
		
			if (cursor.openPhysicalTime > upper) {
				break;
			}
		}

		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}
	
	/**
	* Like {@link #select__where_OpenPhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
	* @param randCursor
	*			The random cursor used to find the indexes and collect sparse data.
	* @param cursor
	*			The sequential cursor used to collect the continuous data. There are cases where the data is not sparse cause the small range of search.
	* @param lower
	*			The lower value to search.
	* @param upper
	*			The upper value to search.
	* @param maxLen
	*			The maximum number of records to collect.
	* @return The array of sparse data.    
	* @see MDB#select_sparse(IRandomCursor, ISeqCursor, long, long, int)
	* @throws IOException If there is any I/O error.
	*/
	public Record[] select_sparse__where_OpenPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfOpenPhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfOpenPhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_OpenPhysicalTime_in(randCursor, cursor, lower, upper);
		}
	
		Record[] data = new Record[10];
		int size = 0;
		long pos = start;
		
		while (pos <= stop) {
			randCursor.seek(pos);
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = randCursor.toRecord();
			size++;
		
			if (randCursor.openPhysicalTime > upper) {
				break;
			}
			pos += step;
		}
	
		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}	

	/**
	 * Like {@link MDB#select(ISeqCursor, long, long)}, but starts at the index of <code>lower</code> 
	 * and stops at the index of <code>upper</code>.
	 * @param randCursor 
	 *			The random cursor used to find the start and stop positions.
	 * @param cursor
	 *			The sequential cursor used to collect the data.
	 * @param lower
	 *			The lower value of <code>closePhysicalTime</code>.
	 * @param upper
	 *			The upper value of <code>closePhysicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_ClosePhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfClosePhysicalTime(randCursor, lower) - 1;

		if (start < 0) {
			start = 0;
		}
	
		Record[] data = new Record[10];
		int size = 0;
	
		cursor.reset(start, _size - 1);
		while (cursor.next()) {
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = cursor.toRecord();
			size++;
		
			if (cursor.closePhysicalTime > upper) {
				break;
			}
		}

		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}
	
	/**
	* Like {@link #select__where_ClosePhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
	* @param randCursor
	*			The random cursor used to find the indexes and collect sparse data.
	* @param cursor
	*			The sequential cursor used to collect the continuous data. There are cases where the data is not sparse cause the small range of search.
	* @param lower
	*			The lower value to search.
	* @param upper
	*			The upper value to search.
	* @param maxLen
	*			The maximum number of records to collect.
	* @return The array of sparse data.    
	* @see MDB#select_sparse(IRandomCursor, ISeqCursor, long, long, int)
	* @throws IOException If there is any I/O error.
	*/
	public Record[] select_sparse__where_ClosePhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfClosePhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfClosePhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_ClosePhysicalTime_in(randCursor, cursor, lower, upper);
		}
	
		Record[] data = new Record[10];
		int size = 0;
		long pos = start;
		
		while (pos <= stop) {
			randCursor.seek(pos);
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = randCursor.toRecord();
			size++;
		
			if (randCursor.closePhysicalTime > upper) {
				break;
			}
			pos += step;
		}
	
		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}	

	/**
	 * Like {@link MDB#select(ISeqCursor, long, long)}, but starts at the index of <code>lower</code> 
	 * and stops at the index of <code>upper</code>.
	 * @param randCursor 
	 *			The random cursor used to find the start and stop positions.
	 * @param cursor
	 *			The sequential cursor used to collect the data.
	 * @param lower
	 *			The lower value of <code>eventPhysicalTime</code>.
	 * @param upper
	 *			The upper value of <code>eventPhysicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_EventPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEventPhysicalTime(randCursor, lower) - 1;

		if (start < 0) {
			start = 0;
		}
	
		Record[] data = new Record[10];
		int size = 0;
	
		cursor.reset(start, _size - 1);
		while (cursor.next()) {
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = cursor.toRecord();
			size++;
		
			if (cursor.eventPhysicalTime > upper) {
				break;
			}
		}

		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}
	
	/**
	* Like {@link #select__where_EventPhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
	* @param randCursor
	*			The random cursor used to find the indexes and collect sparse data.
	* @param cursor
	*			The sequential cursor used to collect the continuous data. There are cases where the data is not sparse cause the small range of search.
	* @param lower
	*			The lower value to search.
	* @param upper
	*			The upper value to search.
	* @param maxLen
	*			The maximum number of records to collect.
	* @return The array of sparse data.    
	* @see MDB#select_sparse(IRandomCursor, ISeqCursor, long, long, int)
	* @throws IOException If there is any I/O error.
	*/
	public Record[] select_sparse__where_EventPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEventPhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfEventPhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_EventPhysicalTime_in(randCursor, cursor, lower, upper);
		}
	
		Record[] data = new Record[10];
		int size = 0;
		long pos = start;
		
		while (pos <= stop) {
			randCursor.seek(pos);
			if (size + 2 > data.length) {
				Record[] newData = new Record[(data.length * 3) / 2 + 1];
				System.arraycopy(data, 0, newData, 0, size);
				data = newData;
			}
			data[size] = randCursor.toRecord();
			size++;
		
			if (randCursor.eventPhysicalTime > upper) {
				break;
			}
			pos += step;
		}
	
		if (size < data.length) {
			Record[] newData = new Record[size];
			System.arraycopy(data, 0, newData, 0, size);
			data = newData;
		}
		return data;
	}	
	/**
	* Column <code>openTime</code> order validator.
	*/
	public static final IValidator<Record> OPENTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.openTime;
			long curValue = current.openTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"openTime(" + row1 + ")=" + prevValue + " > " + "openTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>closeTime</code> order validator.
	*/
	public static final IValidator<Record> CLOSETIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.closeTime;
			long curValue = current.closeTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"closeTime(" + row1 + ")=" + prevValue + " > " + "closeTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>openPhysicalTime</code> order validator.
	*/
	public static final IValidator<Record> OPENPHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.openPhysicalTime;
			long curValue = current.openPhysicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"openPhysicalTime(" + row1 + ")=" + prevValue + " > " + "openPhysicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>closePhysicalTime</code> order validator.
	*/
	public static final IValidator<Record> CLOSEPHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.closePhysicalTime;
			long curValue = current.closePhysicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"closePhysicalTime(" + row1 + ")=" + prevValue + " > " + "closePhysicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>eventPhysicalTime</code> order validator.
	*/
	public static final IValidator<Record> EVENTPHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.eventPhysicalTime;
			long curValue = current.eventPhysicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"eventPhysicalTime(" + row1 + ")=" + prevValue + " > " + "eventPhysicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	 * <p>
	 * Record comparator for the column <code>openTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class OpenTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.openTime < o2.openTime? -1 : (o1.openTime > o2.openTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfOpenTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfOpenTime(Record[] data, long key) {
		return indexOfOpenTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the openTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>openTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* This method is an utility, it does not search on a file, else in an arbitrary array. 
	* </p>
	* @param data
	* 			Array of records.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return The index of the value.
	*/
	public static int indexOfOpenTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].openTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
				low1 = mid + 1;
			} else if (cmp > 0) {
				high1 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return low1 == 0 ? 0 : low1 - 1; /* key not found */
    }
	
	/**
	* Like {@link #indexOfOpenTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfOpenTime(RandomCursor cursor, long key) throws IOException {
		return indexOfOpenTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the openTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>openTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* In MDB there is not any type of "indexing" or "automatic sorting" of the data, 
	* binary searches is the fast way used to find values.
	* Usually, to retrieve certain range of data, first you get the start and stop positions
	* (with this method), and then you create a cursor.  
	* </p>
	*
	* @param cursor
	*			The random cursor used to find the value.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return 
	*			The index of the value.
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfOpenTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.openTime == key ? 0 
							: (r.openTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfOpenTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_openTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 96 + 0);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
			return low1 == 0 ? 0 : low1 - 1; /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}

	/**
	* Like {@link #indexOfOpenTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfOpenTime_exact(Record[] data, long key) {
		return indexOfOpenTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfOpenTime(Record[], long, int, int)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param data
	* 			Array or records.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfOpenTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].openTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
		    	low1 = mid + 1;
			} else if (cmp > 0) {
				high2 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return -(low1 + 1); /* key not found */
    }

	/**
	* Like {@link #indexOfOpenTime(RandomCursor, long, long, long)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param cursor
	*			The cursor used to find the value.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a value &lt; 0 if the key is not found.
	* @throws IOException If there is any I/O error.	
	*/
	public long indexOfOpenTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.openTime == key ? 0 
							: (r.openTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfOpenTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_openTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 96 + 0);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
		
			return -(low1 + 1); /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}
	
	/**
	 * Like {@link #indexOfOpenTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfOpenTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfOpenTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given openTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of openTime.
	* @param openTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateOpenTime(RandomCursor randCursor, long openTime) throws IOException {
		if (_size > 0) {
			long len = indexOfOpenTime(randCursor, openTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.openTime > openTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>openTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param openTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_openTime_is(RandomCursor cursor, long openTime) throws IOException {
		if (_size > 0) {
			long i = indexOfOpenTime_exact(cursor, openTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.openTime == openTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the openTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countOpenTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfOpenTime(null, keyLower, 0L, high);
			long b = indexOfOpenTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfOpenTime(cursor, keyLower, 0L, high);
		long b = indexOfOpenTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>closeTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class CloseTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.closeTime < o2.closeTime? -1 : (o1.closeTime > o2.closeTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfCloseTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfCloseTime(Record[] data, long key) {
		return indexOfCloseTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the closeTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>closeTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* This method is an utility, it does not search on a file, else in an arbitrary array. 
	* </p>
	* @param data
	* 			Array of records.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return The index of the value.
	*/
	public static int indexOfCloseTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].closeTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
				low1 = mid + 1;
			} else if (cmp > 0) {
				high1 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return low1 == 0 ? 0 : low1 - 1; /* key not found */
    }
	
	/**
	* Like {@link #indexOfCloseTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfCloseTime(RandomCursor cursor, long key) throws IOException {
		return indexOfCloseTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the closeTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>closeTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* In MDB there is not any type of "indexing" or "automatic sorting" of the data, 
	* binary searches is the fast way used to find values.
	* Usually, to retrieve certain range of data, first you get the start and stop positions
	* (with this method), and then you create a cursor.  
	* </p>
	*
	* @param cursor
	*			The random cursor used to find the value.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return 
	*			The index of the value.
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfCloseTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.closeTime == key ? 0 
							: (r.closeTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfCloseTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_closeTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 96 + 16);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
			return low1 == 0 ? 0 : low1 - 1; /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}

	/**
	* Like {@link #indexOfCloseTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfCloseTime_exact(Record[] data, long key) {
		return indexOfCloseTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfCloseTime(Record[], long, int, int)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param data
	* 			Array or records.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfCloseTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].closeTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
		    	low1 = mid + 1;
			} else if (cmp > 0) {
				high2 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return -(low1 + 1); /* key not found */
    }

	/**
	* Like {@link #indexOfCloseTime(RandomCursor, long, long, long)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param cursor
	*			The cursor used to find the value.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a value &lt; 0 if the key is not found.
	* @throws IOException If there is any I/O error.	
	*/
	public long indexOfCloseTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.closeTime == key ? 0 
							: (r.closeTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfCloseTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_closeTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 96 + 16);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
		
			return -(low1 + 1); /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}
	
	/**
	 * Like {@link #indexOfCloseTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfCloseTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfCloseTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given closeTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of closeTime.
	* @param closeTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateCloseTime(RandomCursor randCursor, long closeTime) throws IOException {
		if (_size > 0) {
			long len = indexOfCloseTime(randCursor, closeTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.closeTime > closeTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>closeTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param closeTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_closeTime_is(RandomCursor cursor, long closeTime) throws IOException {
		if (_size > 0) {
			long i = indexOfCloseTime_exact(cursor, closeTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.closeTime == closeTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the closeTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countCloseTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfCloseTime(null, keyLower, 0L, high);
			long b = indexOfCloseTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfCloseTime(cursor, keyLower, 0L, high);
		long b = indexOfCloseTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>openPhysicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class OpenPhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.openPhysicalTime < o2.openPhysicalTime? -1 : (o1.openPhysicalTime > o2.openPhysicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfOpenPhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfOpenPhysicalTime(Record[] data, long key) {
		return indexOfOpenPhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the openPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>openPhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* This method is an utility, it does not search on a file, else in an arbitrary array. 
	* </p>
	* @param data
	* 			Array of records.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return The index of the value.
	*/
	public static int indexOfOpenPhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].openPhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
				low1 = mid + 1;
			} else if (cmp > 0) {
				high1 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return low1 == 0 ? 0 : low1 - 1; /* key not found */
    }
	
	/**
	* Like {@link #indexOfOpenPhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfOpenPhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfOpenPhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the openPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>openPhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* In MDB there is not any type of "indexing" or "automatic sorting" of the data, 
	* binary searches is the fast way used to find values.
	* Usually, to retrieve certain range of data, first you get the start and stop positions
	* (with this method), and then you create a cursor.  
	* </p>
	*
	* @param cursor
	*			The random cursor used to find the value.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return 
	*			The index of the value.
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfOpenPhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.openPhysicalTime == key ? 0 
							: (r.openPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfOpenPhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_openPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 96 + 56);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
			return low1 == 0 ? 0 : low1 - 1; /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}

	/**
	* Like {@link #indexOfOpenPhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfOpenPhysicalTime_exact(Record[] data, long key) {
		return indexOfOpenPhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfOpenPhysicalTime(Record[], long, int, int)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param data
	* 			Array or records.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfOpenPhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].openPhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
		    	low1 = mid + 1;
			} else if (cmp > 0) {
				high2 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return -(low1 + 1); /* key not found */
    }

	/**
	* Like {@link #indexOfOpenPhysicalTime(RandomCursor, long, long, long)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param cursor
	*			The cursor used to find the value.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a value &lt; 0 if the key is not found.
	* @throws IOException If there is any I/O error.	
	*/
	public long indexOfOpenPhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.openPhysicalTime == key ? 0 
							: (r.openPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfOpenPhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_openPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 96 + 56);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
		
			return -(low1 + 1); /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}
	
	/**
	 * Like {@link #indexOfOpenPhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfOpenPhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfOpenPhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given openPhysicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of openPhysicalTime.
	* @param openPhysicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateOpenPhysicalTime(RandomCursor randCursor, long openPhysicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfOpenPhysicalTime(randCursor, openPhysicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.openPhysicalTime > openPhysicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>openPhysicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param openPhysicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_openPhysicalTime_is(RandomCursor cursor, long openPhysicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfOpenPhysicalTime_exact(cursor, openPhysicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.openPhysicalTime == openPhysicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the openPhysicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countOpenPhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfOpenPhysicalTime(null, keyLower, 0L, high);
			long b = indexOfOpenPhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfOpenPhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfOpenPhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>closePhysicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class ClosePhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.closePhysicalTime < o2.closePhysicalTime? -1 : (o1.closePhysicalTime > o2.closePhysicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfClosePhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfClosePhysicalTime(Record[] data, long key) {
		return indexOfClosePhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the closePhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>closePhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* This method is an utility, it does not search on a file, else in an arbitrary array. 
	* </p>
	* @param data
	* 			Array of records.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return The index of the value.
	*/
	public static int indexOfClosePhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].closePhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
				low1 = mid + 1;
			} else if (cmp > 0) {
				high1 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return low1 == 0 ? 0 : low1 - 1; /* key not found */
    }
	
	/**
	* Like {@link #indexOfClosePhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfClosePhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfClosePhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the closePhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>closePhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* In MDB there is not any type of "indexing" or "automatic sorting" of the data, 
	* binary searches is the fast way used to find values.
	* Usually, to retrieve certain range of data, first you get the start and stop positions
	* (with this method), and then you create a cursor.  
	* </p>
	*
	* @param cursor
	*			The random cursor used to find the value.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return 
	*			The index of the value.
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfClosePhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.closePhysicalTime == key ? 0 
							: (r.closePhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfClosePhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_closePhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 96 + 64);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
			return low1 == 0 ? 0 : low1 - 1; /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}

	/**
	* Like {@link #indexOfClosePhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfClosePhysicalTime_exact(Record[] data, long key) {
		return indexOfClosePhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfClosePhysicalTime(Record[], long, int, int)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param data
	* 			Array or records.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfClosePhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].closePhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
		    	low1 = mid + 1;
			} else if (cmp > 0) {
				high2 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return -(low1 + 1); /* key not found */
    }

	/**
	* Like {@link #indexOfClosePhysicalTime(RandomCursor, long, long, long)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param cursor
	*			The cursor used to find the value.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a value &lt; 0 if the key is not found.
	* @throws IOException If there is any I/O error.	
	*/
	public long indexOfClosePhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.closePhysicalTime == key ? 0 
							: (r.closePhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfClosePhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_closePhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 96 + 64);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
		
			return -(low1 + 1); /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}
	
	/**
	 * Like {@link #indexOfClosePhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfClosePhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfClosePhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given closePhysicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of closePhysicalTime.
	* @param closePhysicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateClosePhysicalTime(RandomCursor randCursor, long closePhysicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfClosePhysicalTime(randCursor, closePhysicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.closePhysicalTime > closePhysicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>closePhysicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param closePhysicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_closePhysicalTime_is(RandomCursor cursor, long closePhysicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfClosePhysicalTime_exact(cursor, closePhysicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.closePhysicalTime == closePhysicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the closePhysicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countClosePhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfClosePhysicalTime(null, keyLower, 0L, high);
			long b = indexOfClosePhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfClosePhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfClosePhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>eventPhysicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class EventPhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.eventPhysicalTime < o2.eventPhysicalTime? -1 : (o1.eventPhysicalTime > o2.eventPhysicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfEventPhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfEventPhysicalTime(Record[] data, long key) {
		return indexOfEventPhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the eventPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>eventPhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* This method is an utility, it does not search on a file, else in an arbitrary array. 
	* </p>
	* @param data
	* 			Array of records.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return The index of the value.
	*/
	public static int indexOfEventPhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].eventPhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
				low1 = mid + 1;
			} else if (cmp > 0) {
				high1 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return low1 == 0 ? 0 : low1 - 1; /* key not found */
    }
	
	/**
	* Like {@link #indexOfEventPhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfEventPhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfEventPhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the eventPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>eventPhysicalTime</code> order specified in the column definition. 
	* </p>
	* <p>
	* In MDB there is not any type of "indexing" or "automatic sorting" of the data, 
	* binary searches is the fast way used to find values.
	* Usually, to retrieve certain range of data, first you get the start and stop positions
	* (with this method), and then you create a cursor.  
	* </p>
	*
	* @param cursor
	*			The random cursor used to find the value.
	* @param key
	* 			The value to find.
	* @param low
	* 			The index to start the search.
	* @param high
	* 			The index to stop the search.
	* @return 
	*			The index of the value.
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfEventPhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.eventPhysicalTime == key ? 0 
							: (r.eventPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfEventPhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_eventPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 96 + 72);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
			return low1 == 0 ? 0 : low1 - 1; /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}

	/**
	* Like {@link #indexOfEventPhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfEventPhysicalTime_exact(Record[] data, long key) {
		return indexOfEventPhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfEventPhysicalTime(Record[], long, int, int)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param data
	* 			Array or records.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfEventPhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].eventPhysicalTime;
		    int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
	
		    if (cmp < 0) {
		    	low1 = mid + 1;
			} else if (cmp > 0) {
				high2 = mid - 1;
			} else {
				return mid; /* key found */
			}
		}
		return -(low1 + 1); /* key not found */
    }

	/**
	* Like {@link #indexOfEventPhysicalTime(RandomCursor, long, long, long)} 
	* but it looks for the exact value, if the value does not exist, returns a number &lt; 0.
	*
	* @param cursor
	*			The cursor used to find the value.
	* @param key
	* 			The value to find the index.
	* @param low
	* 			The start position of the search.
	* @param high
	* 			The stop position of the search.
	*
	* @return The index of <code>key</code>, or a value &lt; 0 if the key is not found.
	* @throws IOException If there is any I/O error.	
	*/
	public long indexOfEventPhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.eventPhysicalTime == key ? 0 
							: (r.eventPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfEventPhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_eventPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 96 + 72);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final long midVal = buffer.getLong();
				final int cmp = midVal == key ? 0 : (midVal < key ? -1 : 1);
				
				if (cmp < 0) {
					low1 = mid + 1;
				}
				else if (cmp > 0) {
					high2 = mid - 1;
				}
				else {
					return mid; /* key found */
				}
			}
		
			return -(low1 + 1); /* key not found */
		} finally {
			if (!_basic) _readLock.unlock();
		}
	}
	
	/**
	 * Like {@link #indexOfEventPhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfEventPhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfEventPhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given eventPhysicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of eventPhysicalTime.
	* @param eventPhysicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateEventPhysicalTime(RandomCursor randCursor, long eventPhysicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfEventPhysicalTime(randCursor, eventPhysicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.eventPhysicalTime > eventPhysicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>eventPhysicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param eventPhysicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_eventPhysicalTime_is(RandomCursor cursor, long eventPhysicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfEventPhysicalTime_exact(cursor, eventPhysicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.eventPhysicalTime == eventPhysicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the eventPhysicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countEventPhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfEventPhysicalTime(null, keyLower, 0L, high);
			long b = indexOfEventPhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfEventPhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfEventPhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}


	@SuppressWarnings("unchecked")
	@Override
	public TradingMDBSession getSession() {
		return _session;
	}

	/**
	 * Get the buffer. Warning: do not use this if you don't know what are you
	 * doing.
	 * 
	 * @return The memory buffer.
	 */
	@Override
	public Record[] getRecentRecordsBuffer() {
		return _rbuf;
	}
	
	/**
	 * The number of opened cursors. You can use this to "debug" your programs.
	 * 
	 * @return The number of cursors.
	 */
	@Override
	public int getOpenCursorCount() {
		return _openCursorCount.get();
	}

	/**
	 * Delete the associated files. Remember to close the cursors before to
	 * perform this operation. If this MDB instance was created with a session,
	 * do not call this method, else the {@link MDBSession#closeAndDelete()}
	 * method.
	 * 
	 * @return <code>true</code> if all files was deleted.
	 */
	 @Override
	public boolean deleteFiles() {
		if (_memory) {
			_rbufSize = 0;
			return true;
		}

		boolean result = true;
		File file = getFile();
		if (file.exists() && !file.delete()) {
			java.lang.System.err.println("Cannot delete file " + file);
			result = false;
		}
		return result;
	}
	
	/**
	 * The number of records.
	 * 
	 * @return The number of rows.
	 * @throws IOException If there is an I/O error.
	 */
	@Override
	public long size() throws IOException {		
		return _size;
	}
	
	/**
	 * The number of records already persisted in the file system.
	 * 
	 * @return The size in rows.
	 * @throws IOException If there is an I/O error.
	 */
	public long fsize() throws IOException {		
		return _memory? 0 : _file.length() / 96;
	}
	
	/**
	 * The number of records in the buffer.
	 * 
	 * @return Count buffer records.
	 */
	@Override
	public int getRecentRecordsCount() {
		return _rbufSize;
	}
	
	@Override
	public RandomCursor thread_randomCursor() throws IOException {
		return (RandomCursor) super.thread_randomCursor();
	}
	
	@Override
	public Cursor thread_cursor() throws IOException {
		return (Cursor) super.thread_cursor();
	}
	
	/**
	 * Truncate the file to the number of rows <code>len</code>.
	 * 
	 * @param len
	 *            The desired number of rows.
	 */
	@Override
	public void truncate(long len) throws IOException {
		if (len < 0 || len > _size) {
			throw new IllegalArgumentException("Cannot truncate to " + len + ", value out of range.");
		}
		
		if (!_basic) _writeLock.lock();
		
		try {
			if (_memory) {
				_rbufSize = (int) len;
				return;
			}

			long newLen = len * 96;
			appender();
			_appender.flush();
			FileChannel channel = _appender._channel;
			channel.truncate(newLen);
			
			if (!_basic) {
				_rbufPos = fsize();
			}
		} finally {
			_size = fsize() + _rbufSize;
			if (!_basic) _writeLock.unlock();
		}					
	}

}	
	

