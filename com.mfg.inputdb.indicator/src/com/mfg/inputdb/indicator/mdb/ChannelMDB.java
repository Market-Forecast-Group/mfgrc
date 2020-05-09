package com.mfg.inputdb.indicator.mdb;

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
 * This class provides the API to manipulate Channel files. 
 * Here you will find the methods to modify and query the Channel files. 
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
 * <h3>Channel definition</h3>
 * <table border=1>
 *	<caption>Channel</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>startTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>endTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>topStartPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>topEndPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>centerStartPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>centerEndPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>bottomStartPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>bottomEndPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>slope</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>startPhysicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>endPhysicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * </table>
 * <h3>ChannelMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on Channel files:
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
 * IndicatorMDBSession session = ...;
 * ChannelMDB mdb = session.connectTo_ChannelMDB("channel.mdb");
 * 
 * // request the appender.
 * ChannelMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.startTime = ...;
 * app.endTime = ...;
 * app.topStartPrice = ...;
 * app.topEndPrice = ...;
 * app.centerStartPrice = ...;
 * app.centerEndPrice = ...;
 * app.bottomStartPrice = ...;
 * app.bottomEndPrice = ...;
 * app.slope = ...;
 * app.startPhysicalTime = ...;
 * app.endPhysicalTime = ...;

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
 * ChannelMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * Channel.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.startTime + " "
 * 			+ cursor.endTime + " "
 * 			+ cursor.topStartPrice + " "
 * 			+ cursor.topEndPrice + " "
 * 			+ cursor.centerStartPrice + " "
 * 			+ cursor.centerEndPrice + " "
 * 			+ cursor.bottomStartPrice + " "
 * 			+ cursor.bottomEndPrice + " "
 * 			+ cursor.slope + " "
 * 			+ cursor.startPhysicalTime + " "
 * 			+ cursor.endPhysicalTime + " "
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
 * ChannelMDB mdb = ...;
 *
 * // request a random cursor
 * ChannelMDB.RandomCursor cursor = mdb.randomCursor();
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
 * ChannelMDB mdb = ...;
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
 * ChannelMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values 										
 * long new_val_startTime = ...;
 * long new_val_endTime = ...;
 * double new_val_topStartPrice = ...;
 * double new_val_topEndPrice = ...;
 * double new_val_centerStartPrice = ...;
 * double new_val_centerEndPrice = ...;
 * double new_val_bottomStartPrice = ...;
 * double new_val_bottomEndPrice = ...;
 * boolean new_val_slope = ...;
 * long new_val_startPhysicalTime = ...;
 * long new_val_endPhysicalTime = ...;
 *
 * mdb.replace(index 
 * 		, new_val_startTime
 * 		, new_val_endTime
 * 		, new_val_topStartPrice
 * 		, new_val_topEndPrice
 * 		, new_val_centerStartPrice
 * 		, new_val_centerEndPrice
 * 		, new_val_bottomStartPrice
 * 		, new_val_bottomEndPrice
 * 		, new_val_slope
 * 		, new_val_startPhysicalTime
 * 		, new_val_endPhysicalTime
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link ChannelMDB#replace_startTime(long, long)}: To replace the startTime value.</li>
 * <li>{@link ChannelMDB#replace_endTime(long, long)}: To replace the endTime value.</li>
 * <li>{@link ChannelMDB#replace_topStartPrice(long, double)}: To replace the topStartPrice value.</li>
 * <li>{@link ChannelMDB#replace_topEndPrice(long, double)}: To replace the topEndPrice value.</li>
 * <li>{@link ChannelMDB#replace_centerStartPrice(long, double)}: To replace the centerStartPrice value.</li>
 * <li>{@link ChannelMDB#replace_centerEndPrice(long, double)}: To replace the centerEndPrice value.</li>
 * <li>{@link ChannelMDB#replace_bottomStartPrice(long, double)}: To replace the bottomStartPrice value.</li>
 * <li>{@link ChannelMDB#replace_bottomEndPrice(long, double)}: To replace the bottomEndPrice value.</li>
 * <li>{@link ChannelMDB#replace_slope(long, boolean)}: To replace the slope value.</li>
 * <li>{@link ChannelMDB#replace_startPhysicalTime(long, long)}: To replace the startPhysicalTime value.</li>
 * <li>{@link ChannelMDB#replace_endPhysicalTime(long, long)}: To replace the endPhysicalTime value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see IndicatorMDBSession#connectTo_ChannelMDB(String)
 */

public final class ChannelMDB
/* BEGIN MDB EXTENDS */
extends MDB<ChannelMDB.Record>
/* END MDB EXTENDS */
{

/* BEGIN USER MDB */
	/* User can insert his code here */
	/* END USER MDB */
	/**
	 * Channel's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"startTime",
		"endTime",
		"topStartPrice",
		"topEndPrice",
		"centerStartPrice",
		"centerEndPrice",
		"bottomStartPrice",
		"bottomEndPrice",
		"slope",
		"startPhysicalTime",
		"endPhysicalTime",
	};
	
	/**
	 * Channel's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		long.class,
		long.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		boolean.class,
		long.class,
		long.class,
	};
	
	/**
	 * Channel's meta-data: column Java types size (in bytes).
	 */
	public static final int[] COLUMNS_SIZE = { 
		8, 
		8, 
		8, 
		8, 
		8, 
		8, 
		8, 
		8, 
		1, 
		8, 
		8, 
	};

	/**
	 * Channel's meta-data: virtual column flags.
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
	};

	/**
	 * Channel's meta-data: column byte-offset.
	 */
	public static final int[] COLUMN_OFFSET = {  
		0, 
		8, 
		16, 
		24, 
		32, 
		40, 
		48, 
		56, 
		64, 
		65, 
		73, 
	};
	
	/**
	 * Channel's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 81;
	
	/**
	* startTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_STARTTIME = 0;
	/**
	* endTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_ENDTIME = 1;
	/**
	* topStartPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_TOPSTARTPRICE = 2;
	/**
	* topEndPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_TOPENDPRICE = 3;
	/**
	* centerStartPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_CENTERSTARTPRICE = 4;
	/**
	* centerEndPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_CENTERENDPRICE = 5;
	/**
	* bottomStartPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_BOTTOMSTARTPRICE = 6;
	/**
	* bottomEndPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_BOTTOMENDPRICE = 7;
	/**
	* slope's meta-data: index in a record.
	*/	
	public static final int COLUMN_SLOPE = 8;
	/**
	* startPhysicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_STARTPHYSICALTIME = 9;
	/**
	* endPhysicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_ENDPHYSICALTIME = 10;

	/**
	 * Channel's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "4295d3b5-f743-4d0e-9c00-7101036ba349";
	
	/**
	 * Channel's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "2049491e-83fa-43f9-846a-d783e58fe865 LONG; 372c317b-c2c2-4126-814c-46ca6edb7ec5 LONG; 7de840a6-c975-4a66-a75b-b42d9dc51829 DOUBLE; d47a3ed0-beb3-4487-b8da-9ba7930ea24f DOUBLE; 7f1f32aa-a3d0-4263-a8ea-e48cc272afdd DOUBLE; 20a81701-5b6d-4441-b9af-022f385a4ce9 DOUBLE; 59158fc8-e15e-4201-9a22-50dee19fd877 DOUBLE; 41ed0b90-9527-421e-9f6d-011a57349fd0 DOUBLE; 23f5ee57-2ac7-48e3-a11f-5ef21555aa54 BOOLEAN; bec417c2-2730-4bfa-8aaf-68196587e4a3 LONG; 5634f269-54c0-4ee4-943b-71447056f2b5 LONG; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer; 
	private ByteBuffer _replaceBuffer_startTime;
	private ByteBuffer _replaceBuffer_endTime;
	private ByteBuffer _replaceBuffer_topStartPrice;
	private ByteBuffer _replaceBuffer_topEndPrice;
	private ByteBuffer _replaceBuffer_centerStartPrice;
	private ByteBuffer _replaceBuffer_centerEndPrice;
	private ByteBuffer _replaceBuffer_bottomStartPrice;
	private ByteBuffer _replaceBuffer_bottomEndPrice;
	private ByteBuffer _replaceBuffer_slope;
	private ByteBuffer _replaceBuffer_startPhysicalTime;
	private ByteBuffer _replaceBuffer_endPhysicalTime;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final IndicatorMDBSession _session;

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
	public ChannelMDB(IndicatorMDBSession session, File file, int bufferSize, SessionMode mode) throws IOException {
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
			_replaceBuffer = ByteBuffer.allocate(81);		
			_replaceBuffer_startTime = ByteBuffer.allocate(8);
			_replaceBuffer_endTime = ByteBuffer.allocate(8);
			_replaceBuffer_topStartPrice = ByteBuffer.allocate(8);
			_replaceBuffer_topEndPrice = ByteBuffer.allocate(8);
			_replaceBuffer_centerStartPrice = ByteBuffer.allocate(8);
			_replaceBuffer_centerEndPrice = ByteBuffer.allocate(8);
			_replaceBuffer_bottomStartPrice = ByteBuffer.allocate(8);
			_replaceBuffer_bottomEndPrice = ByteBuffer.allocate(8);
			_replaceBuffer_slope = ByteBuffer.allocate(1);
			_replaceBuffer_startPhysicalTime = ByteBuffer.allocate(8);
			_replaceBuffer_endPhysicalTime = ByteBuffer.allocate(8);
		}
	}	

	/**
	* Channel record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the startTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of startTime</caption>
		* <tr><td>Column</td><td>startTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long startTime; /* 0 */
		/**
		* Represents the endTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of endTime</caption>
		* <tr><td>Column</td><td>endTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long endTime; /* 1 */
		/**
		* Represents the topStartPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of topStartPrice</caption>
		* <tr><td>Column</td><td>topStartPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double topStartPrice; /* 2 */
		/**
		* Represents the topEndPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of topEndPrice</caption>
		* <tr><td>Column</td><td>topEndPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double topEndPrice; /* 3 */
		/**
		* Represents the centerStartPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of centerStartPrice</caption>
		* <tr><td>Column</td><td>centerStartPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double centerStartPrice; /* 4 */
		/**
		* Represents the centerEndPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of centerEndPrice</caption>
		* <tr><td>Column</td><td>centerEndPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double centerEndPrice; /* 5 */
		/**
		* Represents the bottomStartPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of bottomStartPrice</caption>
		* <tr><td>Column</td><td>bottomStartPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double bottomStartPrice; /* 6 */
		/**
		* Represents the bottomEndPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of bottomEndPrice</caption>
		* <tr><td>Column</td><td>bottomEndPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double bottomEndPrice; /* 7 */
		/**
		* Represents the slope column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of slope</caption>
		* <tr><td>Column</td><td>slope</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean slope; /* 8 */
		/**
		* Represents the startPhysicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of startPhysicalTime</caption>
		* <tr><td>Column</td><td>startPhysicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long startPhysicalTime; /* 9 */
		/**
		* Represents the endPhysicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of endPhysicalTime</caption>
		* <tr><td>Column</td><td>endPhysicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long endPhysicalTime; /* 10 */

		/**
		* Returns an string representation of the record content.
		*/
		@Override
		public String toString() {
			return "Channel [ "
				 + "startTime=" + startTime + " "	
				 + "endTime=" + endTime + " "	
				 + "topStartPrice=" + topStartPrice + " "	
				 + "topEndPrice=" + topEndPrice + " "	
				 + "centerStartPrice=" + centerStartPrice + " "	
				 + "centerEndPrice=" + centerEndPrice + " "	
				 + "bottomStartPrice=" + bottomStartPrice + " "	
				 + "bottomEndPrice=" + bottomEndPrice + " "	
				 + "slope=" + slope + " "	
				 + "startPhysicalTime=" + startPhysicalTime + " "	
				 + "endPhysicalTime=" + endPhysicalTime + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Long.valueOf(startTime),
							Long.valueOf(endTime),
							Double.valueOf(topStartPrice),
							Double.valueOf(topEndPrice),
							Double.valueOf(centerStartPrice),
							Double.valueOf(centerEndPrice),
							Double.valueOf(bottomStartPrice),
							Double.valueOf(bottomEndPrice),
							Boolean.valueOf(slope),
							Long.valueOf(startPhysicalTime),
							Long.valueOf(endPhysicalTime),
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
				case 0: return startTime;
				case 1: return endTime;
				case 2: return topStartPrice;
				case 3: return topEndPrice;
				case 4: return centerStartPrice;
				case 5: return centerEndPrice;
				case 6: return bottomStartPrice;
				case 7: return bottomEndPrice;
				case 8: return slope;
				case 9: return startPhysicalTime;
				case 10: return endPhysicalTime;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.startTime = record.startTime;
			this.endTime = record.endTime;
			this.topStartPrice = record.topStartPrice;
			this.topEndPrice = record.topEndPrice;
			this.centerStartPrice = record.centerStartPrice;
			this.centerEndPrice = record.centerEndPrice;
			this.bottomStartPrice = record.bottomStartPrice;
			this.bottomEndPrice = record.bottomEndPrice;
			this.slope = record.slope;
			this.startPhysicalTime = record.startPhysicalTime;
			this.endPhysicalTime = record.endPhysicalTime;
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
	* 	ap.startTime = getStartTime();	
	* 	ap.endTime = getEndTime();	
	* 	ap.topStartPrice = getTopStartPrice();	
	* 	ap.topEndPrice = getTopEndPrice();	
	* 	ap.centerStartPrice = getCenterStartPrice();	
	* 	ap.centerEndPrice = getCenterEndPrice();	
	* 	ap.bottomStartPrice = getBottomStartPrice();	
	* 	ap.bottomEndPrice = getBottomEndPrice();	
	* 	ap.slope = getSlope();	
	* 	ap.startPhysicalTime = getStartPhysicalTime();	
	* 	ap.endPhysicalTime = getEndPhysicalTime();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected ChannelMDB _mdb;	
		protected ByteBuffer _buf;	 
		public long startTime; /* 0 */
		public long endTime; /* 1 */
		public double topStartPrice; /* 2 */
		public double topEndPrice; /* 3 */
		public double centerStartPrice; /* 4 */
		public double centerEndPrice; /* 5 */
		public double bottomStartPrice; /* 6 */
		public double bottomEndPrice; /* 7 */
		public boolean slope; /* 8 */
		public long startPhysicalTime; /* 9 */
		public long endPhysicalTime; /* 10 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = ChannelMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 81);
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

				
					_buf.putLong(this.startTime);
					_buf.putLong(this.endTime);
					_buf.putDouble(this.topStartPrice);
					_buf.putDouble(this.topEndPrice);
					_buf.putDouble(this.centerStartPrice);
					_buf.putDouble(this.centerEndPrice);
					_buf.putDouble(this.bottomStartPrice);
					_buf.putDouble(this.bottomEndPrice);
					_buf.put((byte) (this.slope? 1 : 0));
					_buf.putLong(this.startPhysicalTime);
					_buf.putLong(this.endPhysicalTime);

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
					r.startTime = this.startTime;
					r.endTime = this.endTime;
					r.topStartPrice = this.topStartPrice;
					r.topEndPrice = this.topEndPrice;
					r.centerStartPrice = this.centerStartPrice;
					r.centerEndPrice = this.centerEndPrice;
					r.bottomStartPrice = this.bottomStartPrice;
					r.bottomEndPrice = this.bottomEndPrice;
					r.slope = this.slope;
					r.startPhysicalTime = this.startPhysicalTime;
					r.endPhysicalTime = this.endPhysicalTime;
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
				this.startTime = record.startTime;
				this.endTime = record.endTime;
				this.topStartPrice = record.topStartPrice;
				this.topEndPrice = record.topEndPrice;
				this.centerStartPrice = record.centerStartPrice;
				this.centerEndPrice = record.centerEndPrice;
				this.bottomStartPrice = record.bottomStartPrice;
				this.bottomEndPrice = record.bottomEndPrice;
				this.slope = record.slope;
				this.startPhysicalTime = record.startPhysicalTime;
				this.endPhysicalTime = record.endPhysicalTime;
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
				r.startTime = record.startTime;
				r.endTime = record.endTime;
				r.topStartPrice = record.topStartPrice;
				r.topEndPrice = record.topEndPrice;
				r.centerStartPrice = record.centerStartPrice;
				r.centerEndPrice = record.centerEndPrice;
				r.bottomStartPrice = record.bottomStartPrice;
				r.bottomEndPrice = record.bottomEndPrice;
				r.slope = record.slope;
				r.startPhysicalTime = record.startPhysicalTime;
				r.endPhysicalTime = record.endPhysicalTime;
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
				this.startTime = record.startTime;
				this.endTime = record.endTime;
				this.topStartPrice = record.topStartPrice;
				this.topEndPrice = record.topEndPrice;
				this.centerStartPrice = record.centerStartPrice;
				this.centerEndPrice = record.centerEndPrice;
				this.bottomStartPrice = record.bottomStartPrice;
				this.bottomEndPrice = record.bottomEndPrice;
				this.slope = record.slope;
				this.startPhysicalTime = record.startPhysicalTime;
				this.endPhysicalTime = record.endPhysicalTime;
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
				_buf.putLong(r.startTime);
				_buf.putLong(r.endTime);
				_buf.putDouble(r.topStartPrice);
				_buf.putDouble(r.topEndPrice);
				_buf.putDouble(r.centerStartPrice);
				_buf.putDouble(r.centerEndPrice);
				_buf.putDouble(r.bottomStartPrice);
				_buf.putDouble(r.bottomEndPrice);
				_buf.put((byte) (r.slope? 1 : 0));
				_buf.putLong(r.startPhysicalTime);
				_buf.putLong(r.endPhysicalTime);
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 81);
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
		public ChannelMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.startTime = this.startTime;
			r.endTime = this.endTime;
			r.topStartPrice = this.topStartPrice;
			r.topEndPrice = this.topEndPrice;
			r.centerStartPrice = this.centerStartPrice;
			r.centerEndPrice = this.centerEndPrice;
			r.bottomStartPrice = this.bottomStartPrice;
			r.bottomEndPrice = this.bottomEndPrice;
			r.slope = this.slope;
			r.startPhysicalTime = this.startPhysicalTime;
			r.endPhysicalTime = this.endPhysicalTime;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.startTime = record.startTime;
			this.endTime = record.endTime;
			this.topStartPrice = record.topStartPrice;
			this.topEndPrice = record.topEndPrice;
			this.centerStartPrice = record.centerStartPrice;
			this.centerEndPrice = record.centerEndPrice;
			this.bottomStartPrice = record.bottomStartPrice;
			this.bottomEndPrice = record.bottomEndPrice;
			this.slope = record.slope;
			this.startPhysicalTime = record.startPhysicalTime;
			this.endPhysicalTime = record.endPhysicalTime;
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
	 * ChannelMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.startTime); 
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
		public long startTime; /* 0 */
		public long endTime; /* 1 */
		public double topStartPrice; /* 2 */
		public double topEndPrice; /* 3 */
		public double centerStartPrice; /* 4 */
		public double centerEndPrice; /* 5 */
		public double bottomStartPrice; /* 6 */
		public double bottomEndPrice; /* 7 */
		public boolean slope; /* 8 */
		public long startPhysicalTime; /* 9 */
		public long endPhysicalTime; /* 10 */
		
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
				_channel.position(start * 81);
				_buffer = ByteBuffer.allocate(bufferSize * 81);
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
					_channel.position(start * 81);
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
						this.startTime = r.startTime;
						this.endTime = r.endTime;
						this.topStartPrice = r.topStartPrice;
						this.topEndPrice = r.topEndPrice;
						this.centerStartPrice = r.centerStartPrice;
						this.centerEndPrice = r.centerEndPrice;
						this.bottomStartPrice = r.bottomStartPrice;
						this.bottomEndPrice = r.bottomEndPrice;
						this.slope = r.slope;
						this.startPhysicalTime = r.startPhysicalTime;
						this.endPhysicalTime = r.endPhysicalTime;
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
				
				this.startTime = _buffer.getLong();
				this.endTime = _buffer.getLong();
				this.topStartPrice = _buffer.getDouble();
				this.topEndPrice = _buffer.getDouble();
				this.centerStartPrice = _buffer.getDouble();
				this.centerEndPrice = _buffer.getDouble();
				this.bottomStartPrice = _buffer.getDouble();
				this.bottomEndPrice = _buffer.getDouble();
				this.slope = _buffer.get() == 0? false : true;
				this.startPhysicalTime = _buffer.getLong();
				this.endPhysicalTime = _buffer.getLong();
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
			r.startTime = this.startTime;
			r.endTime = this.endTime;
			r.topStartPrice = this.topStartPrice;
			r.topEndPrice = this.topEndPrice;
			r.centerStartPrice = this.centerStartPrice;
			r.centerEndPrice = this.centerEndPrice;
			r.bottomStartPrice = this.bottomStartPrice;
			r.bottomEndPrice = this.bottomEndPrice;
			r.slope = this.slope;
			r.startPhysicalTime = this.startPhysicalTime;
			r.endPhysicalTime = this.endPhysicalTime;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public ChannelMDB getMDB() {
			return ChannelMDB.this;
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
		* ChannelMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.startTime); 
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
			ByteBuffer _buffer_startTime; // used by index-of-startTime method.
			ByteBuffer _buffer_endTime; // used by index-of-endTime method.
			ByteBuffer _buffer_startPhysicalTime; // used by index-of-startPhysicalTime method.
			ByteBuffer _buffer_endPhysicalTime; // used by index-of-endPhysicalTime method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			public long startTime; /* 0 */
			public long endTime; /* 1 */
			public double topStartPrice; /* 2 */
			public double topEndPrice; /* 3 */
			public double centerStartPrice; /* 4 */
			public double centerEndPrice; /* 5 */
			public double bottomStartPrice; /* 6 */
			public double bottomEndPrice; /* 7 */
			public boolean slope; /* 8 */
			public long startPhysicalTime; /* 9 */
			public long endPhysicalTime; /* 10 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(81);
					_buffer_startTime = ByteBuffer.allocate(8);
					_buffer_endTime = ByteBuffer.allocate(8);
					_buffer_startPhysicalTime = ByteBuffer.allocate(8);
					_buffer_endPhysicalTime = ByteBuffer.allocate(8);
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
							this.startTime = r.startTime;
							this.endTime = r.endTime;
							this.topStartPrice = r.topStartPrice;
							this.topEndPrice = r.topEndPrice;
							this.centerStartPrice = r.centerStartPrice;
							this.centerEndPrice = r.centerEndPrice;
							this.bottomStartPrice = r.bottomStartPrice;
							this.bottomEndPrice = r.bottomEndPrice;
							this.slope = r.slope;
							this.startPhysicalTime = r.startPhysicalTime;
							this.endPhysicalTime = r.endPhysicalTime;
							return;					
						}
					} finally {
						_readLock.unlock();
					}
				}
				
				assert !_memory;
				
				_row = position;
				_buffer.rewind();
				_channel.read(_buffer, position * 81);
				_buffer.rewind();
				this.startTime = _buffer.getLong();
				this.endTime = _buffer.getLong();
				this.topStartPrice = _buffer.getDouble();
				this.topEndPrice = _buffer.getDouble();
				this.centerStartPrice = _buffer.getDouble();
				this.centerEndPrice = _buffer.getDouble();
				this.bottomStartPrice = _buffer.getDouble();
				this.bottomEndPrice = _buffer.getDouble();
				this.slope = _buffer.get() == 0? false : true;
				this.startPhysicalTime = _buffer.getLong();
				this.endPhysicalTime = _buffer.getLong();
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
				r.startTime = this.startTime;
				r.endTime = this.endTime;
				r.topStartPrice = this.topStartPrice;
				r.topEndPrice = this.topEndPrice;
				r.centerStartPrice = this.centerStartPrice;
				r.centerEndPrice = this.centerEndPrice;
				r.bottomStartPrice = this.bottomStartPrice;
				r.bottomEndPrice = this.bottomEndPrice;
				r.slope = this.slope;
				r.startPhysicalTime = this.startPhysicalTime;
				r.endPhysicalTime = this.endPhysicalTime;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public ChannelMDB getMDB() {
				return ChannelMDB.this;
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
		buffer.putLong(obj.startTime); 
		buffer.putLong(obj.endTime); 
		buffer.putDouble(obj.topStartPrice); 
		buffer.putDouble(obj.topEndPrice); 
		buffer.putDouble(obj.centerStartPrice); 
		buffer.putDouble(obj.centerEndPrice); 
		buffer.putDouble(obj.bottomStartPrice); 
		buffer.putDouble(obj.bottomEndPrice); 
		buffer.put(obj.slope ? (byte) 1 : (byte) 0); 
		buffer.putLong(obj.startPhysicalTime); 
		buffer.putLong(obj.endPhysicalTime); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(RandomCursor obj, ByteBuffer buffer) {
		buffer.putLong(obj.startTime); 
		buffer.putLong(obj.endTime); 
		buffer.putDouble(obj.topStartPrice); 
		buffer.putDouble(obj.topEndPrice); 
		buffer.putDouble(obj.centerStartPrice); 
		buffer.putDouble(obj.centerEndPrice); 
		buffer.putDouble(obj.bottomStartPrice); 
		buffer.putDouble(obj.bottomEndPrice); 
		buffer.put(obj.slope ? (byte) 1 : (byte) 0); 
		buffer.putLong(obj.startPhysicalTime); 
		buffer.putLong(obj.endPhysicalTime); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(Record obj, ByteBuffer buffer) {
		buffer.putLong(obj.startTime); 
		buffer.putLong(obj.endTime); 
		buffer.putDouble(obj.topStartPrice); 
		buffer.putDouble(obj.topEndPrice); 
		buffer.putDouble(obj.centerStartPrice); 
		buffer.putDouble(obj.centerEndPrice); 
		buffer.putDouble(obj.bottomStartPrice); 
		buffer.putDouble(obj.bottomEndPrice); 
		buffer.put(obj.slope ? (byte) 1 : (byte) 0); 
		buffer.putLong(obj.startPhysicalTime); 
		buffer.putLong(obj.endPhysicalTime); 
	}
	


	/**
	 * Replace the record at the given <code>index</code>.
	 *
	 * @param index The index to update.
	 * @param val_startTime The value for column startTime.
	 * @param val_endTime The value for column endTime.
	 * @param val_topStartPrice The value for column topStartPrice.
	 * @param val_topEndPrice The value for column topEndPrice.
	 * @param val_centerStartPrice The value for column centerStartPrice.
	 * @param val_centerEndPrice The value for column centerEndPrice.
	 * @param val_bottomStartPrice The value for column bottomStartPrice.
	 * @param val_bottomEndPrice The value for column bottomEndPrice.
	 * @param val_slope The value for column slope.
	 * @param val_startPhysicalTime The value for column startPhysicalTime.
	 * @param val_endPhysicalTime The value for column endPhysicalTime.
	* @throws IOException If there is any I/O error.
	 */
	public void replace(long index 
							, long val_startTime
							, long val_endTime
							, double val_topStartPrice
							, double val_topEndPrice
							, double val_centerStartPrice
							, double val_centerEndPrice
							, double val_bottomStartPrice
							, double val_bottomEndPrice
							, boolean val_slope
							, long val_startPhysicalTime
							, long val_endPhysicalTime
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
						r.startTime = val_startTime;
						r.endTime = val_endTime;
						r.topStartPrice = val_topStartPrice;
						r.topEndPrice = val_topEndPrice;
						r.centerStartPrice = val_centerStartPrice;
						r.centerEndPrice = val_centerEndPrice;
						r.bottomStartPrice = val_bottomStartPrice;
						r.bottomEndPrice = val_bottomEndPrice;
						r.slope = val_slope;
						r.startPhysicalTime = val_startPhysicalTime;
						r.endPhysicalTime = val_endPhysicalTime;
					}
					return;
				} 				
			}
		
			_replaceBuffer.rewind();
			_replaceBuffer.putLong(val_startTime);
			_replaceBuffer.putLong(val_endTime);
			_replaceBuffer.putDouble(val_topStartPrice);
			_replaceBuffer.putDouble(val_topEndPrice);
			_replaceBuffer.putDouble(val_centerStartPrice);
			_replaceBuffer.putDouble(val_centerEndPrice);
			_replaceBuffer.putDouble(val_bottomStartPrice);
			_replaceBuffer.putDouble(val_bottomEndPrice);
			_replaceBuffer.put((byte) (val_slope ? 1 : 0));
			_replaceBuffer.putLong(val_startPhysicalTime);
			_replaceBuffer.putLong(val_endPhysicalTime);
			_replaceBuffer.rewind();
			appender();
			_appender._channel.write(_replaceBuffer, index * 81);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	
	/**
	 * Update the record at the given <code>index</code>.
	 * Also you can use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)}.
	 *
	 * @param index The index to update.
	 * @param record Contains the data to set.
	 * @see #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)
	 * @throws IOException If there is any I/O error.
	 */
	public void replace(long index, Record record) throws IOException {
		replace(index 
					, record.startTime		
					, record.endTime		
					, record.topStartPrice		
					, record.topEndPrice		
					, record.centerStartPrice		
					, record.centerEndPrice		
					, record.bottomStartPrice		
					, record.bottomEndPrice		
					, record.slope		
					, record.startPhysicalTime		
					, record.endPhysicalTime		
				);			
	}

	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "startTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "startTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_startTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].startTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_startTime.rewind();
			_replaceBuffer_startTime.putLong(value);
			_replaceBuffer_startTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_startTime, index * 81 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "endTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "endTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_endTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].endTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_endTime.rewind();
			_replaceBuffer_endTime.putLong(value);
			_replaceBuffer_endTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_endTime, index * 81 + 8);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "topStartPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "topStartPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_topStartPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].topStartPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_topStartPrice.rewind();
			_replaceBuffer_topStartPrice.putDouble(value);
			_replaceBuffer_topStartPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_topStartPrice, index * 81 + 16);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "topEndPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "topEndPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_topEndPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].topEndPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_topEndPrice.rewind();
			_replaceBuffer_topEndPrice.putDouble(value);
			_replaceBuffer_topEndPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_topEndPrice, index * 81 + 24);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "centerStartPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "centerStartPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_centerStartPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].centerStartPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_centerStartPrice.rewind();
			_replaceBuffer_centerStartPrice.putDouble(value);
			_replaceBuffer_centerStartPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_centerStartPrice, index * 81 + 32);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "centerEndPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "centerEndPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_centerEndPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].centerEndPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_centerEndPrice.rewind();
			_replaceBuffer_centerEndPrice.putDouble(value);
			_replaceBuffer_centerEndPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_centerEndPrice, index * 81 + 40);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "bottomStartPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "bottomStartPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_bottomStartPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].bottomStartPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_bottomStartPrice.rewind();
			_replaceBuffer_bottomStartPrice.putDouble(value);
			_replaceBuffer_bottomStartPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_bottomStartPrice, index * 81 + 48);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "bottomEndPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "bottomEndPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_bottomEndPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].bottomEndPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_bottomEndPrice.rewind();
			_replaceBuffer_bottomEndPrice.putDouble(value);
			_replaceBuffer_bottomEndPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_bottomEndPrice, index * 81 + 56);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "slope" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "slope".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_slope(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].slope = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_slope.rewind();
			_replaceBuffer_slope.put((byte) (value ? 1 : 0));
			_replaceBuffer_slope.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_slope, index * 81 + 64);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "startPhysicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "startPhysicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_startPhysicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].startPhysicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_startPhysicalTime.rewind();
			_replaceBuffer_startPhysicalTime.putLong(value);
			_replaceBuffer_startPhysicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_startPhysicalTime, index * 81 + 65);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "endPhysicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, long, double, double, double, double, double, double, boolean, long, long)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "endPhysicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_endPhysicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].endPhysicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_endPhysicalTime.rewind();
			_replaceBuffer_endPhysicalTime.putLong(value);
			_replaceBuffer_endPhysicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_endPhysicalTime, index * 81 + 73);
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
	 *			The lower value of <code>startTime</code>.
	 * @param upper
	 *			The upper value of <code>startTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_StartTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfStartTime(randCursor, lower) - 1;

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
		
			if (cursor.startTime > upper) {
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
	* Like {@link #select__where_StartTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_StartTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfStartTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfStartTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_StartTime_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.startTime > upper) {
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
	 *			The lower value of <code>endTime</code>.
	 * @param upper
	 *			The upper value of <code>endTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_EndTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEndTime(randCursor, lower) - 1;

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
		
			if (cursor.endTime > upper) {
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
	* Like {@link #select__where_EndTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_EndTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEndTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfEndTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_EndTime_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.endTime > upper) {
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
	 *			The lower value of <code>startPhysicalTime</code>.
	 * @param upper
	 *			The upper value of <code>startPhysicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_StartPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfStartPhysicalTime(randCursor, lower) - 1;

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
		
			if (cursor.startPhysicalTime > upper) {
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
	* Like {@link #select__where_StartPhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_StartPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfStartPhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfStartPhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_StartPhysicalTime_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.startPhysicalTime > upper) {
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
	 *			The lower value of <code>endPhysicalTime</code>.
	 * @param upper
	 *			The upper value of <code>endPhysicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_EndPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEndPhysicalTime(randCursor, lower) - 1;

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
		
			if (cursor.endPhysicalTime > upper) {
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
	* Like {@link #select__where_EndPhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_EndPhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfEndPhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfEndPhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_EndPhysicalTime_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.endPhysicalTime > upper) {
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
	* Column <code>startTime</code> order validator.
	*/
	public static final IValidator<Record> STARTTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.startTime;
			long curValue = current.startTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"startTime(" + row1 + ")=" + prevValue + " > " + "startTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>endTime</code> order validator.
	*/
	public static final IValidator<Record> ENDTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.endTime;
			long curValue = current.endTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"endTime(" + row1 + ")=" + prevValue + " > " + "endTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>startPhysicalTime</code> order validator.
	*/
	public static final IValidator<Record> STARTPHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.startPhysicalTime;
			long curValue = current.startPhysicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"startPhysicalTime(" + row1 + ")=" + prevValue + " > " + "startPhysicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>endPhysicalTime</code> order validator.
	*/
	public static final IValidator<Record> ENDPHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.endPhysicalTime;
			long curValue = current.endPhysicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"endPhysicalTime(" + row1 + ")=" + prevValue + " > " + "endPhysicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	 * <p>
	 * Record comparator for the column <code>startTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class StartTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.startTime < o2.startTime? -1 : (o1.startTime > o2.startTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfStartTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfStartTime(Record[] data, long key) {
		return indexOfStartTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the startTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>startTime</code> order specified in the column definition. 
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
	public static int indexOfStartTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].startTime;
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
	* Like {@link #indexOfStartTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfStartTime(RandomCursor cursor, long key) throws IOException {
		return indexOfStartTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the startTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>startTime</code> order specified in the column definition. 
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
	public long indexOfStartTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.startTime == key ? 0 
							: (r.startTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfStartTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_startTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 81 + 0);
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
	* Like {@link #indexOfStartTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfStartTime_exact(Record[] data, long key) {
		return indexOfStartTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfStartTime(Record[], long, int, int)} 
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
	public static int indexOfStartTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].startTime;
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
	* Like {@link #indexOfStartTime(RandomCursor, long, long, long)} 
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
	public long indexOfStartTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.startTime == key ? 0 
							: (r.startTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfStartTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_startTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 81 + 0);
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
	 * Like {@link #indexOfStartTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfStartTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfStartTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given startTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of startTime.
	* @param startTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateStartTime(RandomCursor randCursor, long startTime) throws IOException {
		if (_size > 0) {
			long len = indexOfStartTime(randCursor, startTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.startTime > startTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>startTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param startTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_startTime_is(RandomCursor cursor, long startTime) throws IOException {
		if (_size > 0) {
			long i = indexOfStartTime_exact(cursor, startTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.startTime == startTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the startTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countStartTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfStartTime(null, keyLower, 0L, high);
			long b = indexOfStartTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfStartTime(cursor, keyLower, 0L, high);
		long b = indexOfStartTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>endTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class EndTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.endTime < o2.endTime? -1 : (o1.endTime > o2.endTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfEndTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfEndTime(Record[] data, long key) {
		return indexOfEndTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the endTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>endTime</code> order specified in the column definition. 
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
	public static int indexOfEndTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].endTime;
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
	* Like {@link #indexOfEndTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfEndTime(RandomCursor cursor, long key) throws IOException {
		return indexOfEndTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the endTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>endTime</code> order specified in the column definition. 
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
	public long indexOfEndTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.endTime == key ? 0 
							: (r.endTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfEndTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_endTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 81 + 8);
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
	* Like {@link #indexOfEndTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfEndTime_exact(Record[] data, long key) {
		return indexOfEndTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfEndTime(Record[], long, int, int)} 
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
	public static int indexOfEndTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].endTime;
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
	* Like {@link #indexOfEndTime(RandomCursor, long, long, long)} 
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
	public long indexOfEndTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.endTime == key ? 0 
							: (r.endTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfEndTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_endTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 81 + 8);
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
	 * Like {@link #indexOfEndTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfEndTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfEndTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given endTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of endTime.
	* @param endTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateEndTime(RandomCursor randCursor, long endTime) throws IOException {
		if (_size > 0) {
			long len = indexOfEndTime(randCursor, endTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.endTime > endTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>endTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param endTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_endTime_is(RandomCursor cursor, long endTime) throws IOException {
		if (_size > 0) {
			long i = indexOfEndTime_exact(cursor, endTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.endTime == endTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the endTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countEndTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfEndTime(null, keyLower, 0L, high);
			long b = indexOfEndTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfEndTime(cursor, keyLower, 0L, high);
		long b = indexOfEndTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>startPhysicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class StartPhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.startPhysicalTime < o2.startPhysicalTime? -1 : (o1.startPhysicalTime > o2.startPhysicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfStartPhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfStartPhysicalTime(Record[] data, long key) {
		return indexOfStartPhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the startPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>startPhysicalTime</code> order specified in the column definition. 
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
	public static int indexOfStartPhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].startPhysicalTime;
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
	* Like {@link #indexOfStartPhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfStartPhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfStartPhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the startPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>startPhysicalTime</code> order specified in the column definition. 
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
	public long indexOfStartPhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.startPhysicalTime == key ? 0 
							: (r.startPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfStartPhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_startPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 81 + 65);
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
	* Like {@link #indexOfStartPhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfStartPhysicalTime_exact(Record[] data, long key) {
		return indexOfStartPhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfStartPhysicalTime(Record[], long, int, int)} 
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
	public static int indexOfStartPhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].startPhysicalTime;
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
	* Like {@link #indexOfStartPhysicalTime(RandomCursor, long, long, long)} 
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
	public long indexOfStartPhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.startPhysicalTime == key ? 0 
							: (r.startPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfStartPhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_startPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 81 + 65);
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
	 * Like {@link #indexOfStartPhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfStartPhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfStartPhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given startPhysicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of startPhysicalTime.
	* @param startPhysicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateStartPhysicalTime(RandomCursor randCursor, long startPhysicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfStartPhysicalTime(randCursor, startPhysicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.startPhysicalTime > startPhysicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>startPhysicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param startPhysicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_startPhysicalTime_is(RandomCursor cursor, long startPhysicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfStartPhysicalTime_exact(cursor, startPhysicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.startPhysicalTime == startPhysicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the startPhysicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countStartPhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfStartPhysicalTime(null, keyLower, 0L, high);
			long b = indexOfStartPhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfStartPhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfStartPhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>endPhysicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class EndPhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.endPhysicalTime < o2.endPhysicalTime? -1 : (o1.endPhysicalTime > o2.endPhysicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfEndPhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfEndPhysicalTime(Record[] data, long key) {
		return indexOfEndPhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the endPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>endPhysicalTime</code> order specified in the column definition. 
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
	public static int indexOfEndPhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].endPhysicalTime;
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
	* Like {@link #indexOfEndPhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfEndPhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfEndPhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the endPhysicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>endPhysicalTime</code> order specified in the column definition. 
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
	public long indexOfEndPhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.endPhysicalTime == key ? 0 
							: (r.endPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfEndPhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_endPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 81 + 73);
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
	* Like {@link #indexOfEndPhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfEndPhysicalTime_exact(Record[] data, long key) {
		return indexOfEndPhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfEndPhysicalTime(Record[], long, int, int)} 
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
	public static int indexOfEndPhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].endPhysicalTime;
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
	* Like {@link #indexOfEndPhysicalTime(RandomCursor, long, long, long)} 
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
	public long indexOfEndPhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.endPhysicalTime == key ? 0 
							: (r.endPhysicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfEndPhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_endPhysicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 81 + 73);
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
	 * Like {@link #indexOfEndPhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfEndPhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfEndPhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given endPhysicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of endPhysicalTime.
	* @param endPhysicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateEndPhysicalTime(RandomCursor randCursor, long endPhysicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfEndPhysicalTime(randCursor, endPhysicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.endPhysicalTime > endPhysicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>endPhysicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param endPhysicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_endPhysicalTime_is(RandomCursor cursor, long endPhysicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfEndPhysicalTime_exact(cursor, endPhysicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.endPhysicalTime == endPhysicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the endPhysicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countEndPhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfEndPhysicalTime(null, keyLower, 0L, high);
			long b = indexOfEndPhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfEndPhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfEndPhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}


	@SuppressWarnings("unchecked")
	@Override
	public IndicatorMDBSession getSession() {
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
		return _memory? 0 : _file.length() / 81;
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

			long newLen = len * 81;
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
	

