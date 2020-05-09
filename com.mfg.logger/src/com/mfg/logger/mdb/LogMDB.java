package com.mfg.logger.mdb;

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
 * This class provides the API to manipulate Log files. 
 * Here you will find the methods to modify and query the Log files. 
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
 * <h3>Log definition</h3>
 * <table border=1>
 *	<caption>Log</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>ID</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>timeGeneral</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>priority</td>
 *		<td>FLOAT</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>source</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>time</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>price</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>thTime</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>thPrice</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>targetPrice</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>timeCPU</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>probVersion</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>classID</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>hhllIndex</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>thIndex</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>msgIndex</td>
 *		<td>INTEGER</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>baseScaleCluster</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>scale</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>p0Time</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>p0Price</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>pm1Time</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>pm1Price</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>pid</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>clusterID</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>textIndex</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>textSize</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>strMessage</td>
 *		<td>STRING</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>patternID</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>dirContrarian</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * </table>
 * <h3>LogMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on Log files:
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
 * LoggerMDBSession session = ...;
 * LogMDB mdb = session.connectTo_LogMDB("log.mdb");
 * 
 * // request the appender.
 * LogMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.ID = ...;
 * app.timeGeneral = ...;
 * app.priority = ...;
 * app.source = ...;
 * app.time = ...;
 * app.price = ...;
 * app.thTime = ...;
 * app.thPrice = ...;
 * app.targetPrice = ...;
 * app.timeCPU = ...;
 * app.probVersion = ...;
 * app.classID = ...;
 * app.hhllIndex = ...;
 * app.thIndex = ...;
 * app.msgIndex = ...;
 * app.baseScaleCluster = ...;
 * app.scale = ...;
 * app.p0Time = ...;
 * app.p0Price = ...;
 * app.pm1Time = ...;
 * app.pm1Price = ...;
 * app.pid = ...;
 * app.clusterID = ...;
 * app.textIndex = ...;
 * app.textSize = ...;
 * app.strMessage = ...;
 * app.patternID = ...;
 * app.dirContrarian = ...;

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
 * LogMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * Log.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.ID + " "
 * 			+ cursor.timeGeneral + " "
 * 			+ cursor.priority + " "
 * 			+ cursor.source + " "
 * 			+ cursor.time + " "
 * 			+ cursor.price + " "
 * 			+ cursor.thTime + " "
 * 			+ cursor.thPrice + " "
 * 			+ cursor.targetPrice + " "
 * 			+ cursor.timeCPU + " "
 * 			+ cursor.probVersion + " "
 * 			+ cursor.classID + " "
 * 			+ cursor.hhllIndex + " "
 * 			+ cursor.thIndex + " "
 * 			+ cursor.msgIndex + " "
 * 			+ cursor.baseScaleCluster + " "
 * 			+ cursor.scale + " "
 * 			+ cursor.p0Time + " "
 * 			+ cursor.p0Price + " "
 * 			+ cursor.pm1Time + " "
 * 			+ cursor.pm1Price + " "
 * 			+ cursor.pid + " "
 * 			+ cursor.clusterID + " "
 * 			+ cursor.textIndex + " "
 * 			+ cursor.textSize + " "
 * 			+ cursor.strMessage + " "
 * 			+ cursor.patternID + " "
 * 			+ cursor.dirContrarian + " "
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
 * LogMDB mdb = ...;
 *
 * // request a random cursor
 * LogMDB.RandomCursor cursor = mdb.randomCursor();
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
 * LogMDB mdb = ...;
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
 * LogMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values 										
 * int new_val_ID = ...;
 * long new_val_timeGeneral = ...;
 * float new_val_priority = ...;
 * int new_val_source = ...;
 * long new_val_time = ...;
 * long new_val_price = ...;
 * long new_val_thTime = ...;
 * long new_val_thPrice = ...;
 * long new_val_targetPrice = ...;
 * long new_val_timeCPU = ...;
 * int new_val_probVersion = ...;
 * int new_val_classID = ...;
 * int new_val_hhllIndex = ...;
 * int new_val_thIndex = ...;
 * int new_val_msgIndex = ...;
 * int new_val_baseScaleCluster = ...;
 * int new_val_scale = ...;
 * long new_val_p0Time = ...;
 * long new_val_p0Price = ...;
 * long new_val_pm1Time = ...;
 * long new_val_pm1Price = ...;
 * int new_val_pid = ...;
 * int new_val_clusterID = ...;
 * int new_val_textIndex = ...;
 * int new_val_textSize = ...;
 * String new_val_strMessage = ...;
 * int new_val_patternID = ...;
 * boolean new_val_dirContrarian = ...;
 *
 * mdb.replace(index 
 * 		, new_val_ID
 * 		, new_val_timeGeneral
 * 		, new_val_priority
 * 		, new_val_source
 * 		, new_val_time
 * 		, new_val_price
 * 		, new_val_thTime
 * 		, new_val_thPrice
 * 		, new_val_targetPrice
 * 		, new_val_timeCPU
 * 		, new_val_probVersion
 * 		, new_val_classID
 * 		, new_val_hhllIndex
 * 		, new_val_thIndex
 * 		, new_val_msgIndex
 * 		, new_val_baseScaleCluster
 * 		, new_val_scale
 * 		, new_val_p0Time
 * 		, new_val_p0Price
 * 		, new_val_pm1Time
 * 		, new_val_pm1Price
 * 		, new_val_pid
 * 		, new_val_clusterID
 * 		, new_val_textIndex
 * 		, new_val_textSize
 * 		, new_val_strMessage
 * 		, new_val_patternID
 * 		, new_val_dirContrarian
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link LogMDB#replace_ID(long, int)}: To replace the ID value.</li>
 * <li>{@link LogMDB#replace_timeGeneral(long, long)}: To replace the timeGeneral value.</li>
 * <li>{@link LogMDB#replace_priority(long, float)}: To replace the priority value.</li>
 * <li>{@link LogMDB#replace_source(long, int)}: To replace the source value.</li>
 * <li>{@link LogMDB#replace_time(long, long)}: To replace the time value.</li>
 * <li>{@link LogMDB#replace_price(long, long)}: To replace the price value.</li>
 * <li>{@link LogMDB#replace_thTime(long, long)}: To replace the thTime value.</li>
 * <li>{@link LogMDB#replace_thPrice(long, long)}: To replace the thPrice value.</li>
 * <li>{@link LogMDB#replace_targetPrice(long, long)}: To replace the targetPrice value.</li>
 * <li>{@link LogMDB#replace_timeCPU(long, long)}: To replace the timeCPU value.</li>
 * <li>{@link LogMDB#replace_probVersion(long, int)}: To replace the probVersion value.</li>
 * <li>{@link LogMDB#replace_classID(long, int)}: To replace the classID value.</li>
 * <li>{@link LogMDB#replace_hhllIndex(long, int)}: To replace the hhllIndex value.</li>
 * <li>{@link LogMDB#replace_thIndex(long, int)}: To replace the thIndex value.</li>
 * <li>{@link LogMDB#replace_msgIndex(long, int)}: To replace the msgIndex value.</li>
 * <li>{@link LogMDB#replace_baseScaleCluster(long, int)}: To replace the baseScaleCluster value.</li>
 * <li>{@link LogMDB#replace_scale(long, int)}: To replace the scale value.</li>
 * <li>{@link LogMDB#replace_p0Time(long, long)}: To replace the p0Time value.</li>
 * <li>{@link LogMDB#replace_p0Price(long, long)}: To replace the p0Price value.</li>
 * <li>{@link LogMDB#replace_pm1Time(long, long)}: To replace the pm1Time value.</li>
 * <li>{@link LogMDB#replace_pm1Price(long, long)}: To replace the pm1Price value.</li>
 * <li>{@link LogMDB#replace_pid(long, int)}: To replace the pid value.</li>
 * <li>{@link LogMDB#replace_clusterID(long, int)}: To replace the clusterID value.</li>
 * <li>{@link LogMDB#replace_textIndex(long, int)}: To replace the textIndex value.</li>
 * <li>{@link LogMDB#replace_textSize(long, int)}: To replace the textSize value.</li>
 * <li>{@link LogMDB#replace_strMessage(long, String)}: To replace the strMessage value.</li>
 * <li>{@link LogMDB#replace_patternID(long, int)}: To replace the patternID value.</li>
 * <li>{@link LogMDB#replace_dirContrarian(long, boolean)}: To replace the dirContrarian value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see LoggerMDBSession#connectTo_LogMDB(String)
 */

public final class LogMDB
/* BEGIN MDB EXTENDS */
extends MDB<LogMDB.Record>
/* END MDB EXTENDS */
implements IArrayMDB 
{

/* BEGIN USER MDB */
	/* User can insert his code here */
	/* END USER MDB */
	/**
	 * Log's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"ID",
		"timeGeneral",
		"priority",
		"source",
		"time",
		"price",
		"thTime",
		"thPrice",
		"targetPrice",
		"timeCPU",
		"probVersion",
		"classID",
		"hhllIndex",
		"thIndex",
		"msgIndex",
		"baseScaleCluster",
		"scale",
		"p0Time",
		"p0Price",
		"pm1Time",
		"pm1Price",
		"pid",
		"clusterID",
		"textIndex",
		"textSize",
		"strMessage",
		"patternID",
		"dirContrarian",
	};
	
	/**
	 * Log's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		int.class,
		long.class,
		float.class,
		int.class,
		long.class,
		long.class,
		long.class,
		long.class,
		long.class,
		long.class,
		int.class,
		int.class,
		int.class,
		int.class,
		int.class,
		int.class,
		int.class,
		long.class,
		long.class,
		long.class,
		long.class,
		int.class,
		int.class,
		int.class,
		int.class,
		java.lang.String.class,
		int.class,
		boolean.class,
	};
	
	/**
	 * Log's meta-data: column Java types size (in bytes).
	 */
	public static final int[] COLUMNS_SIZE = { 
		4, 
		8, 
		4, 
		4, 
		8, 
		8, 
		8, 
		8, 
		8, 
		8, 
		4, 
		4, 
		4, 
		4, 
		4, 
		4, 
		4, 
		8, 
		8, 
		8, 
		8, 
		4, 
		4, 
		4, 
		4, 
		12, 
		4, 
		1, 
	};

	/**
	 * Log's meta-data: virtual column flags.
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
	 * Log's meta-data: column byte-offset.
	 */
	public static final int[] COLUMN_OFFSET = {  
		0, 
		4, 
		12, 
		16, 
		20, 
		28, 
		36, 
		44, 
		52, 
		60, 
		68, 
		72, 
		76, 
		80, 
		84, 
		88, 
		92, 
		96, 
		104, 
		112, 
		120, 
		128, 
		132, 
		136, 
		140, 
		144, 
		156, 
		160, 
	};
	
	/**
	 * Log's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 161;
	
	/**
	* ID's meta-data: index in a record.
	*/	
	public static final int COLUMN_ID = 0;
	/**
	* timeGeneral's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIMEGENERAL = 1;
	/**
	* priority's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRIORITY = 2;
	/**
	* source's meta-data: index in a record.
	*/	
	public static final int COLUMN_SOURCE = 3;
	/**
	* time's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIME = 4;
	/**
	* price's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRICE = 5;
	/**
	* thTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_THTIME = 6;
	/**
	* thPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_THPRICE = 7;
	/**
	* targetPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_TARGETPRICE = 8;
	/**
	* timeCPU's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIMECPU = 9;
	/**
	* probVersion's meta-data: index in a record.
	*/	
	public static final int COLUMN_PROBVERSION = 10;
	/**
	* classID's meta-data: index in a record.
	*/	
	public static final int COLUMN_CLASSID = 11;
	/**
	* hhllIndex's meta-data: index in a record.
	*/	
	public static final int COLUMN_HHLLINDEX = 12;
	/**
	* thIndex's meta-data: index in a record.
	*/	
	public static final int COLUMN_THINDEX = 13;
	/**
	* msgIndex's meta-data: index in a record.
	*/	
	public static final int COLUMN_MSGINDEX = 14;
	/**
	* baseScaleCluster's meta-data: index in a record.
	*/	
	public static final int COLUMN_BASESCALECLUSTER = 15;
	/**
	* scale's meta-data: index in a record.
	*/	
	public static final int COLUMN_SCALE = 16;
	/**
	* p0Time's meta-data: index in a record.
	*/	
	public static final int COLUMN_P0TIME = 17;
	/**
	* p0Price's meta-data: index in a record.
	*/	
	public static final int COLUMN_P0PRICE = 18;
	/**
	* pm1Time's meta-data: index in a record.
	*/	
	public static final int COLUMN_PM1TIME = 19;
	/**
	* pm1Price's meta-data: index in a record.
	*/	
	public static final int COLUMN_PM1PRICE = 20;
	/**
	* pid's meta-data: index in a record.
	*/	
	public static final int COLUMN_PID = 21;
	/**
	* clusterID's meta-data: index in a record.
	*/	
	public static final int COLUMN_CLUSTERID = 22;
	/**
	* textIndex's meta-data: index in a record.
	*/	
	public static final int COLUMN_TEXTINDEX = 23;
	/**
	* textSize's meta-data: index in a record.
	*/	
	public static final int COLUMN_TEXTSIZE = 24;
	/**
	* strMessage's meta-data: index in a record.
	*/	
	public static final int COLUMN_STRMESSAGE = 25;
	/**
	* patternID's meta-data: index in a record.
	*/	
	public static final int COLUMN_PATTERNID = 26;
	/**
	* dirContrarian's meta-data: index in a record.
	*/	
	public static final int COLUMN_DIRCONTRARIAN = 27;

	/**
	 * Log's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "8248ffbf-c3bb-483f-a170-502e1c690729";
	
	/**
	 * Log's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "b8aa657d-2fe1-4897-b3ed-efbea3bdde26 INTEGER; 422eddd9-19f0-4114-baca-3443db2a5282 LONG; c9a2843d-67af-46d1-a66f-f1a93ee838d0 FLOAT; 34c47ddb-453e-4935-8ec8-e7a2d204e1d3 INTEGER; 8596a7c0-60b2-4280-b8a7-df819d0165ae LONG; 81025ac2-8406-4e52-86e8-e245b772d433 LONG; e1317417-2b91-42fe-8102-fd782a90cb78 LONG; 0e7129c3-57f4-4a36-a769-ab5d9ade9251 LONG; 5c5abb84-850e-4189-b52c-4f241793c6d1 LONG; e52f58b7-980d-40c7-a3ea-123f776232c6 LONG; d757f244-a340-429f-acd4-70e41d4eaa59 INTEGER; 3f7d34bb-91db-48b9-8910-494404adf14e INTEGER; 7dc38547-5b70-4e1a-b043-922e342de5bf INTEGER; 52e7a182-86ea-406d-8315-69642dd05fdb INTEGER; f6c25839-1f86-45be-9b8b-c64e7da0425e INTEGER; 88b9c945-7980-41f6-8960-e5010b890225 INTEGER; 47011328-5372-49dc-b68c-a746df687e64 INTEGER; fa01072a-87b7-4bd4-83d9-ae056fd90cae LONG; ee5306e5-99a8-4f71-ba9d-fc9502208244 LONG; 7dbc06bf-514e-40e8-916c-d9d51aad01e8 LONG; 07e6c2b6-6896-429e-94f1-8733567d9df6 LONG; 2b961434-2f02-4270-a6d7-9ffc6937c0cd INTEGER; 455f1a9f-005e-496f-ba12-502ee47277b5 INTEGER; 1b0eeadc-a14f-426b-9788-ff0b4564f2a9 INTEGER; cc6eace7-7e3f-4a4e-9a4f-3707e8a814a3 INTEGER; b8e5f183-f767-49ce-b77f-af83ae4aa6a2 STRING; 757f8468-6efd-4437-a76e-b1c9fba18e43 INTEGER; eac45ecb-6ece-4b66-be47-77cbea296dab BOOLEAN; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer_ID;
	private ByteBuffer _replaceBuffer_timeGeneral;
	private ByteBuffer _replaceBuffer_priority;
	private ByteBuffer _replaceBuffer_source;
	private ByteBuffer _replaceBuffer_time;
	private ByteBuffer _replaceBuffer_price;
	private ByteBuffer _replaceBuffer_thTime;
	private ByteBuffer _replaceBuffer_thPrice;
	private ByteBuffer _replaceBuffer_targetPrice;
	private ByteBuffer _replaceBuffer_timeCPU;
	private ByteBuffer _replaceBuffer_probVersion;
	private ByteBuffer _replaceBuffer_classID;
	private ByteBuffer _replaceBuffer_hhllIndex;
	private ByteBuffer _replaceBuffer_thIndex;
	private ByteBuffer _replaceBuffer_msgIndex;
	private ByteBuffer _replaceBuffer_baseScaleCluster;
	private ByteBuffer _replaceBuffer_scale;
	private ByteBuffer _replaceBuffer_p0Time;
	private ByteBuffer _replaceBuffer_p0Price;
	private ByteBuffer _replaceBuffer_pm1Time;
	private ByteBuffer _replaceBuffer_pm1Price;
	private ByteBuffer _replaceBuffer_pid;
	private ByteBuffer _replaceBuffer_clusterID;
	private ByteBuffer _replaceBuffer_textIndex;
	private ByteBuffer _replaceBuffer_textSize;
	private ByteBuffer _replaceBuffer_patternID;
	private ByteBuffer _replaceBuffer_dirContrarian;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final LoggerMDBSession _session;

	/**
	 * The constructor. You can manipulate MDB files with an instance of this class 
	 * and you don't need a session, but we recommend to create a session 
	 * and connect to files with the session "connect" methods, 
	 * specially when you have more than one file.
	 * @param session The session attached to this MDB instance.
	 * @param file The main file.
	 * @param arrayFile The file with the array values.
	 * @param bufferSize The number of records to use in the buffer.
	 * @param mode The session mode.
	 * @throws IOException If there is an I/O error.
	 */
	public LogMDB(LoggerMDBSession session, File file, File arrayFile, int bufferSize, SessionMode mode) throws IOException {
		super(TABLE_ID, TABLE_SIGNATURE, mode, file, arrayFile, bufferSize, COLUMNS_NAME, COLUMNS_TYPE);
		
		if (file == null || arrayFile == null) throw new IllegalArgumentException("Null files.");
		if (file.equals(arrayFile)) throw new IllegalArgumentException("Main file is equal to the array file: " + file);
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
			_replaceBuffer_ID = ByteBuffer.allocate(4);
			_replaceBuffer_timeGeneral = ByteBuffer.allocate(8);
			_replaceBuffer_priority = ByteBuffer.allocate(4);
			_replaceBuffer_source = ByteBuffer.allocate(4);
			_replaceBuffer_time = ByteBuffer.allocate(8);
			_replaceBuffer_price = ByteBuffer.allocate(8);
			_replaceBuffer_thTime = ByteBuffer.allocate(8);
			_replaceBuffer_thPrice = ByteBuffer.allocate(8);
			_replaceBuffer_targetPrice = ByteBuffer.allocate(8);
			_replaceBuffer_timeCPU = ByteBuffer.allocate(8);
			_replaceBuffer_probVersion = ByteBuffer.allocate(4);
			_replaceBuffer_classID = ByteBuffer.allocate(4);
			_replaceBuffer_hhllIndex = ByteBuffer.allocate(4);
			_replaceBuffer_thIndex = ByteBuffer.allocate(4);
			_replaceBuffer_msgIndex = ByteBuffer.allocate(4);
			_replaceBuffer_baseScaleCluster = ByteBuffer.allocate(4);
			_replaceBuffer_scale = ByteBuffer.allocate(4);
			_replaceBuffer_p0Time = ByteBuffer.allocate(8);
			_replaceBuffer_p0Price = ByteBuffer.allocate(8);
			_replaceBuffer_pm1Time = ByteBuffer.allocate(8);
			_replaceBuffer_pm1Price = ByteBuffer.allocate(8);
			_replaceBuffer_pid = ByteBuffer.allocate(4);
			_replaceBuffer_clusterID = ByteBuffer.allocate(4);
			_replaceBuffer_textIndex = ByteBuffer.allocate(4);
			_replaceBuffer_textSize = ByteBuffer.allocate(4);
			_replaceBuffer_patternID = ByteBuffer.allocate(4);
			_replaceBuffer_dirContrarian = ByteBuffer.allocate(1);
		}
	}	

	/**
	* Log record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the ID column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of ID</caption>
		* <tr><td>Column</td><td>ID</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int ID; /* 0 */
		/**
		* Represents the timeGeneral column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of timeGeneral</caption>
		* <tr><td>Column</td><td>timeGeneral</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long timeGeneral; /* 1 */
		/**
		* Represents the priority column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of priority</caption>
		* <tr><td>Column</td><td>priority</td></tr>
		* <tr><td>Type</td><td>FLOAT</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public float priority; /* 2 */
		/**
		* Represents the source column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of source</caption>
		* <tr><td>Column</td><td>source</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int source; /* 3 */
		/**
		* Represents the time column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of time</caption>
		* <tr><td>Column</td><td>time</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long time; /* 4 */
		/**
		* Represents the price column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of price</caption>
		* <tr><td>Column</td><td>price</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long price; /* 5 */
		/**
		* Represents the thTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of thTime</caption>
		* <tr><td>Column</td><td>thTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long thTime; /* 6 */
		/**
		* Represents the thPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of thPrice</caption>
		* <tr><td>Column</td><td>thPrice</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long thPrice; /* 7 */
		/**
		* Represents the targetPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of targetPrice</caption>
		* <tr><td>Column</td><td>targetPrice</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long targetPrice; /* 8 */
		/**
		* Represents the timeCPU column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of timeCPU</caption>
		* <tr><td>Column</td><td>timeCPU</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long timeCPU; /* 9 */
		/**
		* Represents the probVersion column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of probVersion</caption>
		* <tr><td>Column</td><td>probVersion</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int probVersion; /* 10 */
		/**
		* Represents the classID column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of classID</caption>
		* <tr><td>Column</td><td>classID</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int classID; /* 11 */
		/**
		* Represents the hhllIndex column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of hhllIndex</caption>
		* <tr><td>Column</td><td>hhllIndex</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int hhllIndex; /* 12 */
		/**
		* Represents the thIndex column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of thIndex</caption>
		* <tr><td>Column</td><td>thIndex</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int thIndex; /* 13 */
		/**
		* Represents the msgIndex column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of msgIndex</caption>
		* <tr><td>Column</td><td>msgIndex</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int msgIndex; /* 14 */
		/**
		* Represents the baseScaleCluster column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of baseScaleCluster</caption>
		* <tr><td>Column</td><td>baseScaleCluster</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int baseScaleCluster; /* 15 */
		/**
		* Represents the scale column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of scale</caption>
		* <tr><td>Column</td><td>scale</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int scale; /* 16 */
		/**
		* Represents the p0Time column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of p0Time</caption>
		* <tr><td>Column</td><td>p0Time</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long p0Time; /* 17 */
		/**
		* Represents the p0Price column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of p0Price</caption>
		* <tr><td>Column</td><td>p0Price</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long p0Price; /* 18 */
		/**
		* Represents the pm1Time column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of pm1Time</caption>
		* <tr><td>Column</td><td>pm1Time</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long pm1Time; /* 19 */
		/**
		* Represents the pm1Price column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of pm1Price</caption>
		* <tr><td>Column</td><td>pm1Price</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long pm1Price; /* 20 */
		/**
		* Represents the pid column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of pid</caption>
		* <tr><td>Column</td><td>pid</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int pid; /* 21 */
		/**
		* Represents the clusterID column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of clusterID</caption>
		* <tr><td>Column</td><td>clusterID</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int clusterID; /* 22 */
		/**
		* Represents the textIndex column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of textIndex</caption>
		* <tr><td>Column</td><td>textIndex</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int textIndex; /* 23 */
		/**
		* Represents the textSize column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of textSize</caption>
		* <tr><td>Column</td><td>textSize</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int textSize; /* 24 */
		/**
		* Represents the strMessage column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of strMessage</caption>
		* <tr><td>Column</td><td>strMessage</td></tr>
		* <tr><td>Type</td><td>STRING</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public String strMessage; /* 25 */
		/**
		* Represents the patternID column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of patternID</caption>
		* <tr><td>Column</td><td>patternID</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int patternID; /* 26 */
		/**
		* Represents the dirContrarian column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of dirContrarian</caption>
		* <tr><td>Column</td><td>dirContrarian</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean dirContrarian; /* 27 */

		/**
		* Returns an string representation of the record content.
		*/
		@Override
		public String toString() {
			return "Log [ "
				 + "ID=" + ID + " "	
				 + "timeGeneral=" + timeGeneral + " "	
				 + "priority=" + priority + " "	
				 + "source=" + source + " "	
				 + "time=" + time + " "	
				 + "price=" + price + " "	
				 + "thTime=" + thTime + " "	
				 + "thPrice=" + thPrice + " "	
				 + "targetPrice=" + targetPrice + " "	
				 + "timeCPU=" + timeCPU + " "	
				 + "probVersion=" + probVersion + " "	
				 + "classID=" + classID + " "	
				 + "hhllIndex=" + hhllIndex + " "	
				 + "thIndex=" + thIndex + " "	
				 + "msgIndex=" + msgIndex + " "	
				 + "baseScaleCluster=" + baseScaleCluster + " "	
				 + "scale=" + scale + " "	
				 + "p0Time=" + p0Time + " "	
				 + "p0Price=" + p0Price + " "	
				 + "pm1Time=" + pm1Time + " "	
				 + "pm1Price=" + pm1Price + " "	
				 + "pid=" + pid + " "	
				 + "clusterID=" + clusterID + " "	
				 + "textIndex=" + textIndex + " "	
				 + "textSize=" + textSize + " "	
				 + "strMessage=" + strMessage + " "	
				 + "patternID=" + patternID + " "	
				 + "dirContrarian=" + dirContrarian + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Integer.valueOf(ID),
							Long.valueOf(timeGeneral),
							Float.valueOf(priority),
							Integer.valueOf(source),
							Long.valueOf(time),
							Long.valueOf(price),
							Long.valueOf(thTime),
							Long.valueOf(thPrice),
							Long.valueOf(targetPrice),
							Long.valueOf(timeCPU),
							Integer.valueOf(probVersion),
							Integer.valueOf(classID),
							Integer.valueOf(hhllIndex),
							Integer.valueOf(thIndex),
							Integer.valueOf(msgIndex),
							Integer.valueOf(baseScaleCluster),
							Integer.valueOf(scale),
							Long.valueOf(p0Time),
							Long.valueOf(p0Price),
							Long.valueOf(pm1Time),
							Long.valueOf(pm1Price),
							Integer.valueOf(pid),
							Integer.valueOf(clusterID),
							Integer.valueOf(textIndex),
							Integer.valueOf(textSize),
							strMessage,
							Integer.valueOf(patternID),
							Boolean.valueOf(dirContrarian),
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
				Record r = (Record) super.clone();
				return r;
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
				case 0: return ID;
				case 1: return timeGeneral;
				case 2: return priority;
				case 3: return source;
				case 4: return time;
				case 5: return price;
				case 6: return thTime;
				case 7: return thPrice;
				case 8: return targetPrice;
				case 9: return timeCPU;
				case 10: return probVersion;
				case 11: return classID;
				case 12: return hhllIndex;
				case 13: return thIndex;
				case 14: return msgIndex;
				case 15: return baseScaleCluster;
				case 16: return scale;
				case 17: return p0Time;
				case 18: return p0Price;
				case 19: return pm1Time;
				case 20: return pm1Price;
				case 21: return pid;
				case 22: return clusterID;
				case 23: return textIndex;
				case 24: return textSize;
				case 25: return strMessage;
				case 26: return patternID;
				case 27: return dirContrarian;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.ID = record.ID;
			this.timeGeneral = record.timeGeneral;
			this.priority = record.priority;
			this.source = record.source;
			this.time = record.time;
			this.price = record.price;
			this.thTime = record.thTime;
			this.thPrice = record.thPrice;
			this.targetPrice = record.targetPrice;
			this.timeCPU = record.timeCPU;
			this.probVersion = record.probVersion;
			this.classID = record.classID;
			this.hhllIndex = record.hhllIndex;
			this.thIndex = record.thIndex;
			this.msgIndex = record.msgIndex;
			this.baseScaleCluster = record.baseScaleCluster;
			this.scale = record.scale;
			this.p0Time = record.p0Time;
			this.p0Price = record.p0Price;
			this.pm1Time = record.pm1Time;
			this.pm1Price = record.pm1Price;
			this.pid = record.pid;
			this.clusterID = record.clusterID;
			this.textIndex = record.textIndex;
			this.textSize = record.textSize;
			this.strMessage = record.strMessage;
			this.patternID = record.patternID;
			this.dirContrarian = record.dirContrarian;
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
	* 	ap.ID = getID();	
	* 	ap.timeGeneral = getTimeGeneral();	
	* 	ap.priority = getPriority();	
	* 	ap.source = getSource();	
	* 	ap.time = getTime();	
	* 	ap.price = getPrice();	
	* 	ap.thTime = getThTime();	
	* 	ap.thPrice = getThPrice();	
	* 	ap.targetPrice = getTargetPrice();	
	* 	ap.timeCPU = getTimeCPU();	
	* 	ap.probVersion = getProbVersion();	
	* 	ap.classID = getClassID();	
	* 	ap.hhllIndex = getHhllIndex();	
	* 	ap.thIndex = getThIndex();	
	* 	ap.msgIndex = getMsgIndex();	
	* 	ap.baseScaleCluster = getBaseScaleCluster();	
	* 	ap.scale = getScale();	
	* 	ap.p0Time = getP0Time();	
	* 	ap.p0Price = getP0Price();	
	* 	ap.pm1Time = getPm1Time();	
	* 	ap.pm1Price = getPm1Price();	
	* 	ap.pid = getPid();	
	* 	ap.clusterID = getClusterID();	
	* 	ap.textIndex = getTextIndex();	
	* 	ap.textSize = getTextSize();	
	* 	ap.strMessage = getStrMessage();	
	* 	ap.patternID = getPatternID();	
	* 	ap.dirContrarian = getDirContrarian();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected LogMDB _mdb;	
		protected ByteBuffer _buf;	 
		protected RandomAccessFile _arrayRaf;
		FileChannel _arrayChannel;
		public int ID; /* 0 */
		public long timeGeneral; /* 1 */
		public float priority; /* 2 */
		public int source; /* 3 */
		public long time; /* 4 */
		public long price; /* 5 */
		public long thTime; /* 6 */
		public long thPrice; /* 7 */
		public long targetPrice; /* 8 */
		public long timeCPU; /* 9 */
		public int probVersion; /* 10 */
		public int classID; /* 11 */
		public int hhllIndex; /* 12 */
		public int thIndex; /* 13 */
		public int msgIndex; /* 14 */
		public int baseScaleCluster; /* 15 */
		public int scale; /* 16 */
		public long p0Time; /* 17 */
		public long p0Price; /* 18 */
		public long pm1Time; /* 19 */
		public long pm1Price; /* 20 */
		public int pid; /* 21 */
		public int clusterID; /* 22 */
		public int textIndex; /* 23 */
		public int textSize; /* 24 */
		public String strMessage; /* 25 */
		public int patternID; /* 26 */
		public boolean dirContrarian; /* 27 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = LogMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 161);
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
			_arrayRaf.close();
			_arrayChannel.close();
			
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
			_arrayRaf = new RandomAccessFile(getArrayFile(), "rw");
			_arrayChannel = _arrayRaf.getChannel();
			_arrayChannel.position(_arrayChannel.size());
		}
		
		/**
		* Append a new record to the file with the appender's values. 
		*/
		@SuppressWarnings("null") 
		@Override
		public void append() throws IOException {
			if (_basic) {
				try {
					assert _rbufSize == 0 && _rbuf == null : "In basic mode the shared buffer is empty";
				
					/* basic append, do not put the record in memory */
					if (_buf.position() == _buf.capacity()) {
						flush();
					}

				
					_buf.putInt(this.ID);
					_buf.putLong(this.timeGeneral);
					_buf.putFloat(this.priority);
					_buf.putInt(this.source);
					_buf.putLong(this.time);
					_buf.putLong(this.price);
					_buf.putLong(this.thTime);
					_buf.putLong(this.thPrice);
					_buf.putLong(this.targetPrice);
					_buf.putLong(this.timeCPU);
					_buf.putInt(this.probVersion);
					_buf.putInt(this.classID);
					_buf.putInt(this.hhllIndex);
					_buf.putInt(this.thIndex);
					_buf.putInt(this.msgIndex);
					_buf.putInt(this.baseScaleCluster);
					_buf.putInt(this.scale);
					_buf.putLong(this.p0Time);
					_buf.putLong(this.p0Price);
					_buf.putLong(this.pm1Time);
					_buf.putLong(this.pm1Price);
					_buf.putInt(this.pid);
					_buf.putInt(this.clusterID);
					_buf.putInt(this.textIndex);
					_buf.putInt(this.textSize);
					{
						byte[] __strMessage = this.strMessage == null? null : this.strMessage.getBytes();
						int len = __strMessage == null? 0 : __strMessage.length;
						_buf.putLong(_arrayChannel.position());
						_buf.putInt(len * 1);
						if (len > 0) {
							ByteBuffer arrayBuf = ByteBuffer.wrap(new byte[len * 1]);
							for (int i = 0; i < len; i++) {
								arrayBuf.put(__strMessage[i]); 
							}
							arrayBuf.rewind();
							_arrayChannel.write(arrayBuf);
						}
					}
					_buf.putInt(this.patternID);
					_buf.put((byte) (this.dirContrarian? 1 : 0));

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
					r.ID = this.ID;
					r.timeGeneral = this.timeGeneral;
					r.priority = this.priority;
					r.source = this.source;
					r.time = this.time;
					r.price = this.price;
					r.thTime = this.thTime;
					r.thPrice = this.thPrice;
					r.targetPrice = this.targetPrice;
					r.timeCPU = this.timeCPU;
					r.probVersion = this.probVersion;
					r.classID = this.classID;
					r.hhllIndex = this.hhllIndex;
					r.thIndex = this.thIndex;
					r.msgIndex = this.msgIndex;
					r.baseScaleCluster = this.baseScaleCluster;
					r.scale = this.scale;
					r.p0Time = this.p0Time;
					r.p0Price = this.p0Price;
					r.pm1Time = this.pm1Time;
					r.pm1Price = this.pm1Price;
					r.pid = this.pid;
					r.clusterID = this.clusterID;
					r.textIndex = this.textIndex;
					r.textSize = this.textSize;
					r.strMessage = this.strMessage;
					r.patternID = this.patternID;
					r.dirContrarian = this.dirContrarian;
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
				this.ID = record.ID;
				this.timeGeneral = record.timeGeneral;
				this.priority = record.priority;
				this.source = record.source;
				this.time = record.time;
				this.price = record.price;
				this.thTime = record.thTime;
				this.thPrice = record.thPrice;
				this.targetPrice = record.targetPrice;
				this.timeCPU = record.timeCPU;
				this.probVersion = record.probVersion;
				this.classID = record.classID;
				this.hhllIndex = record.hhllIndex;
				this.thIndex = record.thIndex;
				this.msgIndex = record.msgIndex;
				this.baseScaleCluster = record.baseScaleCluster;
				this.scale = record.scale;
				this.p0Time = record.p0Time;
				this.p0Price = record.p0Price;
				this.pm1Time = record.pm1Time;
				this.pm1Price = record.pm1Price;
				this.pid = record.pid;
				this.clusterID = record.clusterID;
				this.textIndex = record.textIndex;
				this.textSize = record.textSize;
				this.strMessage = record.strMessage;
				this.patternID = record.patternID;
				this.dirContrarian = record.dirContrarian;
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
				r.ID = record.ID;
				r.timeGeneral = record.timeGeneral;
				r.priority = record.priority;
				r.source = record.source;
				r.time = record.time;
				r.price = record.price;
				r.thTime = record.thTime;
				r.thPrice = record.thPrice;
				r.targetPrice = record.targetPrice;
				r.timeCPU = record.timeCPU;
				r.probVersion = record.probVersion;
				r.classID = record.classID;
				r.hhllIndex = record.hhllIndex;
				r.thIndex = record.thIndex;
				r.msgIndex = record.msgIndex;
				r.baseScaleCluster = record.baseScaleCluster;
				r.scale = record.scale;
				r.p0Time = record.p0Time;
				r.p0Price = record.p0Price;
				r.pm1Time = record.pm1Time;
				r.pm1Price = record.pm1Price;
				r.pid = record.pid;
				r.clusterID = record.clusterID;
				r.textIndex = record.textIndex;
				r.textSize = record.textSize;
				r.strMessage = record.strMessage;
				r.patternID = record.patternID;
				r.dirContrarian = record.dirContrarian;
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
				this.ID = record.ID;
				this.timeGeneral = record.timeGeneral;
				this.priority = record.priority;
				this.source = record.source;
				this.time = record.time;
				this.price = record.price;
				this.thTime = record.thTime;
				this.thPrice = record.thPrice;
				this.targetPrice = record.targetPrice;
				this.timeCPU = record.timeCPU;
				this.probVersion = record.probVersion;
				this.classID = record.classID;
				this.hhllIndex = record.hhllIndex;
				this.thIndex = record.thIndex;
				this.msgIndex = record.msgIndex;
				this.baseScaleCluster = record.baseScaleCluster;
				this.scale = record.scale;
				this.p0Time = record.p0Time;
				this.p0Price = record.p0Price;
				this.pm1Time = record.pm1Time;
				this.pm1Price = record.pm1Price;
				this.pid = record.pid;
				this.clusterID = record.clusterID;
				this.textIndex = record.textIndex;
				this.textSize = record.textSize;
				this.strMessage = record.strMessage;
				this.patternID = record.patternID;
				this.dirContrarian = record.dirContrarian;
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


		@SuppressWarnings("null") 
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
				_buf.putInt(r.ID);
				_buf.putLong(r.timeGeneral);
				_buf.putFloat(r.priority);
				_buf.putInt(r.source);
				_buf.putLong(r.time);
				_buf.putLong(r.price);
				_buf.putLong(r.thTime);
				_buf.putLong(r.thPrice);
				_buf.putLong(r.targetPrice);
				_buf.putLong(r.timeCPU);
				_buf.putInt(r.probVersion);
				_buf.putInt(r.classID);
				_buf.putInt(r.hhllIndex);
				_buf.putInt(r.thIndex);
				_buf.putInt(r.msgIndex);
				_buf.putInt(r.baseScaleCluster);
				_buf.putInt(r.scale);
				_buf.putLong(r.p0Time);
				_buf.putLong(r.p0Price);
				_buf.putLong(r.pm1Time);
				_buf.putLong(r.pm1Price);
				_buf.putInt(r.pid);
				_buf.putInt(r.clusterID);
				_buf.putInt(r.textIndex);
				_buf.putInt(r.textSize);
				{
					byte[] __strMessage = r.strMessage == null? null : r.strMessage.getBytes();
					int len = __strMessage == null? 0 : __strMessage.length;
					_buf.putLong(_arrayChannel.position());
					_buf.putInt(len * 1);
					if (len > 0) {
						ByteBuffer arrayBuf = ByteBuffer.wrap(new byte[len * 1]);
						for (int i = 0; i < len; i++) {
							arrayBuf.put(__strMessage[i]); 
						}
						arrayBuf.rewind();
						_arrayChannel.write(arrayBuf);
					}
				}
				_buf.putInt(r.patternID);
				_buf.put((byte) (r.dirContrarian? 1 : 0));
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 161);
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
		public LogMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.ID = this.ID;
			r.timeGeneral = this.timeGeneral;
			r.priority = this.priority;
			r.source = this.source;
			r.time = this.time;
			r.price = this.price;
			r.thTime = this.thTime;
			r.thPrice = this.thPrice;
			r.targetPrice = this.targetPrice;
			r.timeCPU = this.timeCPU;
			r.probVersion = this.probVersion;
			r.classID = this.classID;
			r.hhllIndex = this.hhllIndex;
			r.thIndex = this.thIndex;
			r.msgIndex = this.msgIndex;
			r.baseScaleCluster = this.baseScaleCluster;
			r.scale = this.scale;
			r.p0Time = this.p0Time;
			r.p0Price = this.p0Price;
			r.pm1Time = this.pm1Time;
			r.pm1Price = this.pm1Price;
			r.pid = this.pid;
			r.clusterID = this.clusterID;
			r.textIndex = this.textIndex;
			r.textSize = this.textSize;
			r.strMessage = this.strMessage;
			r.patternID = this.patternID;
			r.dirContrarian = this.dirContrarian;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.ID = record.ID;
			this.timeGeneral = record.timeGeneral;
			this.priority = record.priority;
			this.source = record.source;
			this.time = record.time;
			this.price = record.price;
			this.thTime = record.thTime;
			this.thPrice = record.thPrice;
			this.targetPrice = record.targetPrice;
			this.timeCPU = record.timeCPU;
			this.probVersion = record.probVersion;
			this.classID = record.classID;
			this.hhllIndex = record.hhllIndex;
			this.thIndex = record.thIndex;
			this.msgIndex = record.msgIndex;
			this.baseScaleCluster = record.baseScaleCluster;
			this.scale = record.scale;
			this.p0Time = record.p0Time;
			this.p0Price = record.p0Price;
			this.pm1Time = record.pm1Time;
			this.pm1Price = record.pm1Price;
			this.pid = record.pid;
			this.clusterID = record.clusterID;
			this.textIndex = record.textIndex;
			this.textSize = record.textSize;
			this.strMessage = record.strMessage;
			this.patternID = record.patternID;
			this.dirContrarian = record.dirContrarian;
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
	 * LogMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.ID); 
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
		private RandomAccessFile _arrayRaf;
		private FileChannel _arrayChannel;
		public int ID; /* 0 */
		public long timeGeneral; /* 1 */
		public float priority; /* 2 */
		public int source; /* 3 */
		public long time; /* 4 */
		public long price; /* 5 */
		public long thTime; /* 6 */
		public long thPrice; /* 7 */
		public long targetPrice; /* 8 */
		public long timeCPU; /* 9 */
		public int probVersion; /* 10 */
		public int classID; /* 11 */
		public int hhllIndex; /* 12 */
		public int thIndex; /* 13 */
		public int msgIndex; /* 14 */
		public int baseScaleCluster; /* 15 */
		public int scale; /* 16 */
		public long p0Time; /* 17 */
		public long p0Price; /* 18 */
		public long pm1Time; /* 19 */
		public long pm1Price; /* 20 */
		public int pid; /* 21 */
		public int clusterID; /* 22 */
		public int textIndex; /* 23 */
		public int textSize; /* 24 */
		public String strMessage; /* 25 */
		public int patternID; /* 26 */
		public boolean dirContrarian; /* 27 */
		
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
				_channel.position(start * 161);
				_buffer = ByteBuffer.allocate(bufferSize * 161);
				_buffer.position(_buffer.capacity());
				_arrayRaf = new RandomAccessFile(getArrayFile(),"rw");
				_arrayChannel = _arrayRaf.getChannel();
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
					_channel.position(start * 161);
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
						this.ID = r.ID;
						this.timeGeneral = r.timeGeneral;
						this.priority = r.priority;
						this.source = r.source;
						this.time = r.time;
						this.price = r.price;
						this.thTime = r.thTime;
						this.thPrice = r.thPrice;
						this.targetPrice = r.targetPrice;
						this.timeCPU = r.timeCPU;
						this.probVersion = r.probVersion;
						this.classID = r.classID;
						this.hhllIndex = r.hhllIndex;
						this.thIndex = r.thIndex;
						this.msgIndex = r.msgIndex;
						this.baseScaleCluster = r.baseScaleCluster;
						this.scale = r.scale;
						this.p0Time = r.p0Time;
						this.p0Price = r.p0Price;
						this.pm1Time = r.pm1Time;
						this.pm1Price = r.pm1Price;
						this.pid = r.pid;
						this.clusterID = r.clusterID;
						this.textIndex = r.textIndex;
						this.textSize = r.textSize;
						this.strMessage = r.strMessage;
						this.patternID = r.patternID;
						this.dirContrarian = r.dirContrarian;
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
				
				this.ID = _buffer.getInt();
				this.timeGeneral = _buffer.getLong();
				this.priority = _buffer.getFloat();
				this.source = _buffer.getInt();
				this.time = _buffer.getLong();
				this.price = _buffer.getLong();
				this.thTime = _buffer.getLong();
				this.thPrice = _buffer.getLong();
				this.targetPrice = _buffer.getLong();
				this.timeCPU = _buffer.getLong();
				this.probVersion = _buffer.getInt();
				this.classID = _buffer.getInt();
				this.hhllIndex = _buffer.getInt();
				this.thIndex = _buffer.getInt();
				this.msgIndex = _buffer.getInt();
				this.baseScaleCluster = _buffer.getInt();
				this.scale = _buffer.getInt();
				this.p0Time = _buffer.getLong();
				this.p0Price = _buffer.getLong();
				this.pm1Time = _buffer.getLong();
				this.pm1Price = _buffer.getLong();
				this.pid = _buffer.getInt();
				this.clusterID = _buffer.getInt();
				this.textIndex = _buffer.getInt();
				this.textSize = _buffer.getInt();
				{
					long start = _buffer.getLong();
					int byteLen = _buffer.getInt();
					ByteBuffer arrayBuf = ByteBuffer.allocate(byteLen);
					_arrayChannel.read(arrayBuf, start);
					arrayBuf.rewind();
					byte[] __strMessage = new byte[byteLen / 1]; 
					for(int i = 0; i < __strMessage.length; i++) {
						__strMessage[i] = arrayBuf.get(); 
					}
					this.strMessage = new String(__strMessage); 
				}
				this.patternID = _buffer.getInt();
				this.dirContrarian = _buffer.get() == 0? false : true;
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
				_arrayRaf.close(); 
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
			r.ID = this.ID;
			r.timeGeneral = this.timeGeneral;
			r.priority = this.priority;
			r.source = this.source;
			r.time = this.time;
			r.price = this.price;
			r.thTime = this.thTime;
			r.thPrice = this.thPrice;
			r.targetPrice = this.targetPrice;
			r.timeCPU = this.timeCPU;
			r.probVersion = this.probVersion;
			r.classID = this.classID;
			r.hhllIndex = this.hhllIndex;
			r.thIndex = this.thIndex;
			r.msgIndex = this.msgIndex;
			r.baseScaleCluster = this.baseScaleCluster;
			r.scale = this.scale;
			r.p0Time = this.p0Time;
			r.p0Price = this.p0Price;
			r.pm1Time = this.pm1Time;
			r.pm1Price = this.pm1Price;
			r.pid = this.pid;
			r.clusterID = this.clusterID;
			r.textIndex = this.textIndex;
			r.textSize = this.textSize;
			r.strMessage = this.strMessage;
			r.patternID = this.patternID;
			r.dirContrarian = this.dirContrarian;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public LogMDB getMDB() {
			return LogMDB.this;
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
		* LogMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.ID); 
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
			ByteBuffer _buffer_timeGeneral; // used by index-of-timeGeneral method.
			ByteBuffer _buffer_msgIndex; // used by index-of-msgIndex method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			private RandomAccessFile _arrayRaf;
			private FileChannel _arrayChannel;
			public int ID; /* 0 */
			public long timeGeneral; /* 1 */
			public float priority; /* 2 */
			public int source; /* 3 */
			public long time; /* 4 */
			public long price; /* 5 */
			public long thTime; /* 6 */
			public long thPrice; /* 7 */
			public long targetPrice; /* 8 */
			public long timeCPU; /* 9 */
			public int probVersion; /* 10 */
			public int classID; /* 11 */
			public int hhllIndex; /* 12 */
			public int thIndex; /* 13 */
			public int msgIndex; /* 14 */
			public int baseScaleCluster; /* 15 */
			public int scale; /* 16 */
			public long p0Time; /* 17 */
			public long p0Price; /* 18 */
			public long pm1Time; /* 19 */
			public long pm1Price; /* 20 */
			public int pid; /* 21 */
			public int clusterID; /* 22 */
			public int textIndex; /* 23 */
			public int textSize; /* 24 */
			public String strMessage; /* 25 */
			public int patternID; /* 26 */
			public boolean dirContrarian; /* 27 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(161);
					_arrayRaf = new RandomAccessFile(getArrayFile(),"rw");
					_arrayChannel = _arrayRaf.getChannel();
					_buffer_timeGeneral = ByteBuffer.allocate(8);
					_buffer_msgIndex = ByteBuffer.allocate(4);
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
							this.ID = r.ID;
							this.timeGeneral = r.timeGeneral;
							this.priority = r.priority;
							this.source = r.source;
							this.time = r.time;
							this.price = r.price;
							this.thTime = r.thTime;
							this.thPrice = r.thPrice;
							this.targetPrice = r.targetPrice;
							this.timeCPU = r.timeCPU;
							this.probVersion = r.probVersion;
							this.classID = r.classID;
							this.hhllIndex = r.hhllIndex;
							this.thIndex = r.thIndex;
							this.msgIndex = r.msgIndex;
							this.baseScaleCluster = r.baseScaleCluster;
							this.scale = r.scale;
							this.p0Time = r.p0Time;
							this.p0Price = r.p0Price;
							this.pm1Time = r.pm1Time;
							this.pm1Price = r.pm1Price;
							this.pid = r.pid;
							this.clusterID = r.clusterID;
							this.textIndex = r.textIndex;
							this.textSize = r.textSize;
							this.strMessage = r.strMessage;
							this.patternID = r.patternID;
							this.dirContrarian = r.dirContrarian;
							return;					
						}
					} finally {
						_readLock.unlock();
					}
				}
				
				assert !_memory;
				
				_row = position;
				_buffer.rewind();
				_channel.read(_buffer, position * 161);
				_buffer.rewind();
				this.ID = _buffer.getInt();
				this.timeGeneral = _buffer.getLong();
				this.priority = _buffer.getFloat();
				this.source = _buffer.getInt();
				this.time = _buffer.getLong();
				this.price = _buffer.getLong();
				this.thTime = _buffer.getLong();
				this.thPrice = _buffer.getLong();
				this.targetPrice = _buffer.getLong();
				this.timeCPU = _buffer.getLong();
				this.probVersion = _buffer.getInt();
				this.classID = _buffer.getInt();
				this.hhllIndex = _buffer.getInt();
				this.thIndex = _buffer.getInt();
				this.msgIndex = _buffer.getInt();
				this.baseScaleCluster = _buffer.getInt();
				this.scale = _buffer.getInt();
				this.p0Time = _buffer.getLong();
				this.p0Price = _buffer.getLong();
				this.pm1Time = _buffer.getLong();
				this.pm1Price = _buffer.getLong();
				this.pid = _buffer.getInt();
				this.clusterID = _buffer.getInt();
				this.textIndex = _buffer.getInt();
				this.textSize = _buffer.getInt();
				{
					long start = _buffer.getLong();
					int byteLen = _buffer.getInt();
					ByteBuffer arrayBuf = ByteBuffer.allocate(byteLen);
					_arrayChannel.read(arrayBuf, start);
					arrayBuf.rewind();
					byte[] __strMessage = new byte[byteLen / 1]; 
					for(int i = 0; i < __strMessage.length; i++) {
						__strMessage[i] = arrayBuf.get(); 
					}
					this.strMessage = new String(__strMessage); 
				}
				this.patternID = _buffer.getInt();
				this.dirContrarian = _buffer.get() == 0? false : true;
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
					_arrayRaf.close(); 
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
				r.ID = this.ID;
				r.timeGeneral = this.timeGeneral;
				r.priority = this.priority;
				r.source = this.source;
				r.time = this.time;
				r.price = this.price;
				r.thTime = this.thTime;
				r.thPrice = this.thPrice;
				r.targetPrice = this.targetPrice;
				r.timeCPU = this.timeCPU;
				r.probVersion = this.probVersion;
				r.classID = this.classID;
				r.hhllIndex = this.hhllIndex;
				r.thIndex = this.thIndex;
				r.msgIndex = this.msgIndex;
				r.baseScaleCluster = this.baseScaleCluster;
				r.scale = this.scale;
				r.p0Time = this.p0Time;
				r.p0Price = this.p0Price;
				r.pm1Time = this.pm1Time;
				r.pm1Price = this.pm1Price;
				r.pid = this.pid;
				r.clusterID = this.clusterID;
				r.textIndex = this.textIndex;
				r.textSize = this.textSize;
				r.strMessage = this.strMessage;
				r.patternID = this.patternID;
				r.dirContrarian = this.dirContrarian;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public LogMDB getMDB() {
				return LogMDB.this;
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
	 * Update the record at the given <code>index</code>, 
	 * but only the column "ID" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "ID".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_ID(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].ID = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_ID.rewind();
			_replaceBuffer_ID.putInt(value);
			_replaceBuffer_ID.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_ID, index * 161 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "timeGeneral" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "timeGeneral".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_timeGeneral(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].timeGeneral = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_timeGeneral.rewind();
			_replaceBuffer_timeGeneral.putLong(value);
			_replaceBuffer_timeGeneral.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_timeGeneral, index * 161 + 4);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "priority" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "priority".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_priority(long index, float value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].priority = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_priority.rewind();
			_replaceBuffer_priority.putFloat(value);
			_replaceBuffer_priority.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_priority, index * 161 + 12);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "source" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "source".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_source(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].source = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_source.rewind();
			_replaceBuffer_source.putInt(value);
			_replaceBuffer_source.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_source, index * 161 + 16);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "time" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "time".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_time(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].time = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_time.rewind();
			_replaceBuffer_time.putLong(value);
			_replaceBuffer_time.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_time, index * 161 + 20);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "price" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "price".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_price(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].price = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_price.rewind();
			_replaceBuffer_price.putLong(value);
			_replaceBuffer_price.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_price, index * 161 + 28);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "thTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "thTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_thTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].thTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_thTime.rewind();
			_replaceBuffer_thTime.putLong(value);
			_replaceBuffer_thTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_thTime, index * 161 + 36);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "thPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "thPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_thPrice(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].thPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_thPrice.rewind();
			_replaceBuffer_thPrice.putLong(value);
			_replaceBuffer_thPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_thPrice, index * 161 + 44);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "targetPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "targetPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_targetPrice(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].targetPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_targetPrice.rewind();
			_replaceBuffer_targetPrice.putLong(value);
			_replaceBuffer_targetPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_targetPrice, index * 161 + 52);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "timeCPU" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "timeCPU".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_timeCPU(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].timeCPU = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_timeCPU.rewind();
			_replaceBuffer_timeCPU.putLong(value);
			_replaceBuffer_timeCPU.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_timeCPU, index * 161 + 60);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "probVersion" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "probVersion".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_probVersion(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].probVersion = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_probVersion.rewind();
			_replaceBuffer_probVersion.putInt(value);
			_replaceBuffer_probVersion.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_probVersion, index * 161 + 68);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "classID" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "classID".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_classID(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].classID = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_classID.rewind();
			_replaceBuffer_classID.putInt(value);
			_replaceBuffer_classID.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_classID, index * 161 + 72);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "hhllIndex" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "hhllIndex".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_hhllIndex(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].hhllIndex = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_hhllIndex.rewind();
			_replaceBuffer_hhllIndex.putInt(value);
			_replaceBuffer_hhllIndex.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_hhllIndex, index * 161 + 76);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "thIndex" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "thIndex".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_thIndex(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].thIndex = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_thIndex.rewind();
			_replaceBuffer_thIndex.putInt(value);
			_replaceBuffer_thIndex.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_thIndex, index * 161 + 80);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "msgIndex" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "msgIndex".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_msgIndex(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].msgIndex = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_msgIndex.rewind();
			_replaceBuffer_msgIndex.putInt(value);
			_replaceBuffer_msgIndex.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_msgIndex, index * 161 + 84);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "baseScaleCluster" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "baseScaleCluster".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_baseScaleCluster(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].baseScaleCluster = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_baseScaleCluster.rewind();
			_replaceBuffer_baseScaleCluster.putInt(value);
			_replaceBuffer_baseScaleCluster.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_baseScaleCluster, index * 161 + 88);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "scale" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "scale".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_scale(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].scale = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_scale.rewind();
			_replaceBuffer_scale.putInt(value);
			_replaceBuffer_scale.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_scale, index * 161 + 92);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "p0Time" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "p0Time".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_p0Time(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].p0Time = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_p0Time.rewind();
			_replaceBuffer_p0Time.putLong(value);
			_replaceBuffer_p0Time.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_p0Time, index * 161 + 96);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "p0Price" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "p0Price".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_p0Price(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].p0Price = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_p0Price.rewind();
			_replaceBuffer_p0Price.putLong(value);
			_replaceBuffer_p0Price.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_p0Price, index * 161 + 104);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "pm1Time" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "pm1Time".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_pm1Time(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].pm1Time = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_pm1Time.rewind();
			_replaceBuffer_pm1Time.putLong(value);
			_replaceBuffer_pm1Time.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_pm1Time, index * 161 + 112);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "pm1Price" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "pm1Price".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_pm1Price(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].pm1Price = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_pm1Price.rewind();
			_replaceBuffer_pm1Price.putLong(value);
			_replaceBuffer_pm1Price.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_pm1Price, index * 161 + 120);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "pid" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "pid".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_pid(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].pid = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_pid.rewind();
			_replaceBuffer_pid.putInt(value);
			_replaceBuffer_pid.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_pid, index * 161 + 128);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "clusterID" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "clusterID".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_clusterID(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].clusterID = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_clusterID.rewind();
			_replaceBuffer_clusterID.putInt(value);
			_replaceBuffer_clusterID.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_clusterID, index * 161 + 132);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "textIndex" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "textIndex".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_textIndex(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].textIndex = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_textIndex.rewind();
			_replaceBuffer_textIndex.putInt(value);
			_replaceBuffer_textIndex.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_textIndex, index * 161 + 136);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "textSize" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "textSize".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_textSize(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].textSize = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_textSize.rewind();
			_replaceBuffer_textSize.putInt(value);
			_replaceBuffer_textSize.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_textSize, index * 161 + 140);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "patternID" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "patternID".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_patternID(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].patternID = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_patternID.rewind();
			_replaceBuffer_patternID.putInt(value);
			_replaceBuffer_patternID.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_patternID, index * 161 + 156);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "dirContrarian" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , int, long, float, int, long, long, long, long, long, long, int, int, int, int, int, int, int, long, long, long, long, int, int, int, int, int, boolean)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "dirContrarian".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_dirContrarian(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].dirContrarian = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_dirContrarian.rewind();
			_replaceBuffer_dirContrarian.put((byte) (value ? 1 : 0));
			_replaceBuffer_dirContrarian.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_dirContrarian, index * 161 + 160);
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
	 *			The lower value of <code>timeGeneral</code>.
	 * @param upper
	 *			The upper value of <code>timeGeneral</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_TimeGeneral_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTimeGeneral(randCursor, lower) - 1;

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
		
			if (cursor.timeGeneral > upper) {
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
	* Like {@link #select__where_TimeGeneral_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_TimeGeneral_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTimeGeneral(randCursor, lower) - 1;
		long stop = Math.min(indexOfTimeGeneral(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_TimeGeneral_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.timeGeneral > upper) {
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
	 *			The lower value of <code>msgIndex</code>.
	 * @param upper
	 *			The upper value of <code>msgIndex</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_MsgIndex_in(RandomCursor randCursor, Cursor cursor, int lower, int upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfMsgIndex(randCursor, lower) - 1;

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
		
			if (cursor.msgIndex > upper) {
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
	* Like {@link #select__where_MsgIndex_in(RandomCursor, Cursor, int, int)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_MsgIndex_in(RandomCursor randCursor, Cursor cursor, int lower, int upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfMsgIndex(randCursor, lower) - 1;
		long stop = Math.min(indexOfMsgIndex(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_MsgIndex_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.msgIndex > upper) {
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
	* Column <code>timeGeneral</code> order validator.
	*/
	public static final IValidator<Record> TIMEGENERAL_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.timeGeneral;
			long curValue = current.timeGeneral;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"timeGeneral(" + row1 + ")=" + prevValue + " > " + "timeGeneral(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	* Column <code>msgIndex</code> order validator.
	*/
	public static final IValidator<Record> MSGINDEX_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			int prevValue = prev.msgIndex;
			int curValue = current.msgIndex;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"msgIndex(" + row1 + ")=" + prevValue + " > " + "msgIndex(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	 * <p>
	 * Record comparator for the column <code>timeGeneral</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class TimeGeneralComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.timeGeneral < o2.timeGeneral? -1 : (o1.timeGeneral > o2.timeGeneral? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfTimeGeneral(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfTimeGeneral(Record[] data, long key) {
		return indexOfTimeGeneral(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the timeGeneral value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>timeGeneral</code> order specified in the column definition. 
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
	public static int indexOfTimeGeneral(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].timeGeneral;
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
	* Like {@link #indexOfTimeGeneral(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfTimeGeneral(RandomCursor cursor, long key) throws IOException {
		return indexOfTimeGeneral(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the timeGeneral value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>timeGeneral</code> order specified in the column definition. 
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
	public long indexOfTimeGeneral(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.timeGeneral == key ? 0 
							: (r.timeGeneral < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfTimeGeneral(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_timeGeneral;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 161 + 4);
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
	* Like {@link #indexOfTimeGeneral_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfTimeGeneral_exact(Record[] data, long key) {
		return indexOfTimeGeneral_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfTimeGeneral(Record[], long, int, int)} 
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
	public static int indexOfTimeGeneral_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].timeGeneral;
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
	* Like {@link #indexOfTimeGeneral(RandomCursor, long, long, long)} 
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
	public long indexOfTimeGeneral_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.timeGeneral == key ? 0 
							: (r.timeGeneral < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfTimeGeneral_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_timeGeneral;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 161 + 4);
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
	 * Like {@link #indexOfTimeGeneral_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfTimeGeneral_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfTimeGeneral_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given timeGeneral and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of timeGeneral.
	* @param timeGeneral Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateTimeGeneral(RandomCursor randCursor, long timeGeneral) throws IOException {
		if (_size > 0) {
			long len = indexOfTimeGeneral(randCursor, timeGeneral);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.timeGeneral > timeGeneral) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>timeGeneral</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param timeGeneral The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_timeGeneral_is(RandomCursor cursor, long timeGeneral) throws IOException {
		if (_size > 0) {
			long i = indexOfTimeGeneral_exact(cursor, timeGeneral);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.timeGeneral == timeGeneral;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the timeGeneral values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countTimeGeneral(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfTimeGeneral(null, keyLower, 0L, high);
			long b = indexOfTimeGeneral(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfTimeGeneral(cursor, keyLower, 0L, high);
		long b = indexOfTimeGeneral(cursor, keyUpper, 0L, high);
		return b - a;
	}
	/**
	 * <p>
	 * Record comparator for the column <code>msgIndex</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class MsgIndexComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.msgIndex < o2.msgIndex? -1 : (o1.msgIndex > o2.msgIndex? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfMsgIndex(Record[], int, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfMsgIndex(Record[] data, int key) {
		return indexOfMsgIndex(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the msgIndex value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>msgIndex</code> order specified in the column definition. 
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
	public static int indexOfMsgIndex(Record[] data, int key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    int midVal = data[mid].msgIndex;
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
	* Like {@link #indexOfMsgIndex(RandomCursor, int, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfMsgIndex(RandomCursor cursor, int key) throws IOException {
		return indexOfMsgIndex(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the msgIndex value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>msgIndex</code> order specified in the column definition. 
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
	public long indexOfMsgIndex(RandomCursor cursor, int key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.msgIndex == key ? 0 
							: (r.msgIndex < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfMsgIndex(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_msgIndex;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 161 + 84);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final int midVal = buffer.getInt();
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
	* Like {@link #indexOfMsgIndex_exact(Record[], int, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfMsgIndex_exact(Record[] data, int key) {
		return indexOfMsgIndex_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfMsgIndex(Record[], int, int, int)} 
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
	public static int indexOfMsgIndex_exact(Record[] data, int key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    int midVal = data[mid].msgIndex;
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
	* Like {@link #indexOfMsgIndex(RandomCursor, int, long, long)} 
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
	public long indexOfMsgIndex_exact(RandomCursor cursor, int key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.msgIndex == key ? 0 
							: (r.msgIndex < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfMsgIndex_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_msgIndex;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 161 + 84);
				buffer.rewind();
				channel.read(buffer);
				buffer.rewind();
				
				final int midVal = buffer.getInt();
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
	 * Like {@link #indexOfMsgIndex_exact(RandomCursor, int, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfMsgIndex_exact(RandomCursor cursor, int key) throws IOException {
		return indexOfMsgIndex_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given msgIndex and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of msgIndex.
	* @param msgIndex Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateMsgIndex(RandomCursor randCursor, int msgIndex) throws IOException {
		if (_size > 0) {
			long len = indexOfMsgIndex(randCursor, msgIndex);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.msgIndex > msgIndex) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>msgIndex</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param msgIndex The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_msgIndex_is(RandomCursor cursor, int msgIndex) throws IOException {
		if (_size > 0) {
			long i = indexOfMsgIndex_exact(cursor, msgIndex);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.msgIndex == msgIndex;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the msgIndex values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countMsgIndex(RandomCursor cursor, int keyLower, int keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfMsgIndex(null, keyLower, 0L, high);
			long b = indexOfMsgIndex(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfMsgIndex(cursor, keyLower, 0L, high);
		long b = indexOfMsgIndex(cursor, keyUpper, 0L, high);
		return b - a;
	}


	@SuppressWarnings("unchecked")
	@Override
	public LoggerMDBSession getSession() {
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
			File arrayFile = getArrayFile();
			if (arrayFile.exists() && !arrayFile.delete()) {
				java.lang.System.err
						.println("Cannot delete file " + arrayFile);
				result = false;
			}
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
		return _memory? 0 : _file.length() / 161;
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

			long newLen = len * 161;
			appender();
			_appender.flush();
			FileChannel channel = _appender._channel;
			channel.truncate(newLen);
			
				
			long newArrLen = 0;
				
			if (newLen > 0) {				
				// Truncate the array file 
				long startPos = newLen - 161;   
				ByteBuffer buf = ByteBuffer.wrap(new byte[12]);				
				channel.read(buf, startPos + 144);
				buf.rewind();
				long arrPos = buf.getLong();
				int arrLen = buf.getInt();			
				newArrLen = arrPos + arrLen;					
			}
				
			_appender._arrayChannel.truncate(newArrLen);
			if (!_basic) {
				_rbufPos = fsize();
			}
		} finally {
			_size = fsize() + _rbufSize;
			if (!_basic) _writeLock.unlock();
		}					
	}

}	
	

