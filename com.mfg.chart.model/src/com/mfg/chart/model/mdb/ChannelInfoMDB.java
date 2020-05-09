package com.mfg.chart.model.mdb;

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
 * This class provides the API to manipulate ChannelInfo files. 
 * Here you will find the methods to modify and query the ChannelInfo files. 
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
 * <h3>ChannelInfo definition</h3>
 * <table border=1>
 *	<caption>ChannelInfo</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>time</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>slope</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>maxslope</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>minslope</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>maxwidth</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>minwidth</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>width</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>maxprice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>minprice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>tickslength</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>lwratio</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>tickspivot</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>pricepivot</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>ticksth</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>priceth</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>goingup</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>HHPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>LLPrice</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>HHTime</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>LLTime</td>
 *		<td>LONG</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>pearsonr</td>
 *		<td>DOUBLE</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * </table>
 * <h3>ChannelInfoMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on ChannelInfo files:
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
 * BaseChartMDBSession session = ...;
 * ChannelInfoMDB mdb = session.connectTo_ChannelInfoMDB("channelinfo.mdb");
 * 
 * // request the appender.
 * ChannelInfoMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.time = ...;
 * app.slope = ...;
 * app.maxslope = ...;
 * app.minslope = ...;
 * app.maxwidth = ...;
 * app.minwidth = ...;
 * app.width = ...;
 * app.maxprice = ...;
 * app.minprice = ...;
 * app.tickslength = ...;
 * app.lwratio = ...;
 * app.tickspivot = ...;
 * app.pricepivot = ...;
 * app.ticksth = ...;
 * app.priceth = ...;
 * app.goingup = ...;
 * app.HHPrice = ...;
 * app.LLPrice = ...;
 * app.HHTime = ...;
 * app.LLTime = ...;
 * app.pearsonr = ...;

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
 * ChannelInfoMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * ChannelInfo.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.time + " "
 * 			+ cursor.slope + " "
 * 			+ cursor.maxslope + " "
 * 			+ cursor.minslope + " "
 * 			+ cursor.maxwidth + " "
 * 			+ cursor.minwidth + " "
 * 			+ cursor.width + " "
 * 			+ cursor.maxprice + " "
 * 			+ cursor.minprice + " "
 * 			+ cursor.tickslength + " "
 * 			+ cursor.lwratio + " "
 * 			+ cursor.tickspivot + " "
 * 			+ cursor.pricepivot + " "
 * 			+ cursor.ticksth + " "
 * 			+ cursor.priceth + " "
 * 			+ cursor.goingup + " "
 * 			+ cursor.HHPrice + " "
 * 			+ cursor.LLPrice + " "
 * 			+ cursor.HHTime + " "
 * 			+ cursor.LLTime + " "
 * 			+ cursor.pearsonr + " "
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
 * ChannelInfoMDB mdb = ...;
 *
 * // request a random cursor
 * ChannelInfoMDB.RandomCursor cursor = mdb.randomCursor();
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
 * ChannelInfoMDB mdb = ...;
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
 * ChannelInfoMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values 										
 * long new_val_time = ...;
 * double new_val_slope = ...;
 * double new_val_maxslope = ...;
 * double new_val_minslope = ...;
 * double new_val_maxwidth = ...;
 * double new_val_minwidth = ...;
 * double new_val_width = ...;
 * double new_val_maxprice = ...;
 * double new_val_minprice = ...;
 * int new_val_tickslength = ...;
 * double new_val_lwratio = ...;
 * int new_val_tickspivot = ...;
 * double new_val_pricepivot = ...;
 * int new_val_ticksth = ...;
 * double new_val_priceth = ...;
 * boolean new_val_goingup = ...;
 * double new_val_HHPrice = ...;
 * double new_val_LLPrice = ...;
 * long new_val_HHTime = ...;
 * long new_val_LLTime = ...;
 * double new_val_pearsonr = ...;
 *
 * mdb.replace(index 
 * 		, new_val_time
 * 		, new_val_slope
 * 		, new_val_maxslope
 * 		, new_val_minslope
 * 		, new_val_maxwidth
 * 		, new_val_minwidth
 * 		, new_val_width
 * 		, new_val_maxprice
 * 		, new_val_minprice
 * 		, new_val_tickslength
 * 		, new_val_lwratio
 * 		, new_val_tickspivot
 * 		, new_val_pricepivot
 * 		, new_val_ticksth
 * 		, new_val_priceth
 * 		, new_val_goingup
 * 		, new_val_HHPrice
 * 		, new_val_LLPrice
 * 		, new_val_HHTime
 * 		, new_val_LLTime
 * 		, new_val_pearsonr
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link ChannelInfoMDB#replace_time(long, long)}: To replace the time value.</li>
 * <li>{@link ChannelInfoMDB#replace_slope(long, double)}: To replace the slope value.</li>
 * <li>{@link ChannelInfoMDB#replace_maxslope(long, double)}: To replace the maxslope value.</li>
 * <li>{@link ChannelInfoMDB#replace_minslope(long, double)}: To replace the minslope value.</li>
 * <li>{@link ChannelInfoMDB#replace_maxwidth(long, double)}: To replace the maxwidth value.</li>
 * <li>{@link ChannelInfoMDB#replace_minwidth(long, double)}: To replace the minwidth value.</li>
 * <li>{@link ChannelInfoMDB#replace_width(long, double)}: To replace the width value.</li>
 * <li>{@link ChannelInfoMDB#replace_maxprice(long, double)}: To replace the maxprice value.</li>
 * <li>{@link ChannelInfoMDB#replace_minprice(long, double)}: To replace the minprice value.</li>
 * <li>{@link ChannelInfoMDB#replace_tickslength(long, int)}: To replace the tickslength value.</li>
 * <li>{@link ChannelInfoMDB#replace_lwratio(long, double)}: To replace the lwratio value.</li>
 * <li>{@link ChannelInfoMDB#replace_tickspivot(long, int)}: To replace the tickspivot value.</li>
 * <li>{@link ChannelInfoMDB#replace_pricepivot(long, double)}: To replace the pricepivot value.</li>
 * <li>{@link ChannelInfoMDB#replace_ticksth(long, int)}: To replace the ticksth value.</li>
 * <li>{@link ChannelInfoMDB#replace_priceth(long, double)}: To replace the priceth value.</li>
 * <li>{@link ChannelInfoMDB#replace_goingup(long, boolean)}: To replace the goingup value.</li>
 * <li>{@link ChannelInfoMDB#replace_HHPrice(long, double)}: To replace the HHPrice value.</li>
 * <li>{@link ChannelInfoMDB#replace_LLPrice(long, double)}: To replace the LLPrice value.</li>
 * <li>{@link ChannelInfoMDB#replace_HHTime(long, long)}: To replace the HHTime value.</li>
 * <li>{@link ChannelInfoMDB#replace_LLTime(long, long)}: To replace the LLTime value.</li>
 * <li>{@link ChannelInfoMDB#replace_pearsonr(long, double)}: To replace the pearsonr value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see BaseChartMDBSession#connectTo_ChannelInfoMDB(String)
 */

public final class ChannelInfoMDB
/* BEGIN MDB EXTENDS */
extends MDB<ChannelInfoMDB.Record>
/* END MDB EXTENDS */
{

/* BEGIN USER MDB */
	/* User can insert his code here */
	/* END USER MDB */
	/**
	 * ChannelInfo's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"time",
		"slope",
		"maxslope",
		"minslope",
		"maxwidth",
		"minwidth",
		"width",
		"maxprice",
		"minprice",
		"tickslength",
		"lwratio",
		"tickspivot",
		"pricepivot",
		"ticksth",
		"priceth",
		"goingup",
		"HHPrice",
		"LLPrice",
		"HHTime",
		"LLTime",
		"pearsonr",
	};
	
	/**
	 * ChannelInfo's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		long.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		double.class,
		int.class,
		double.class,
		int.class,
		double.class,
		int.class,
		double.class,
		boolean.class,
		double.class,
		double.class,
		long.class,
		long.class,
		double.class,
	};
	
	/**
	 * ChannelInfo's meta-data: column Java types size (in bytes).
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
		8, 
		4, 
		8, 
		4, 
		8, 
		4, 
		8, 
		1, 
		8, 
		8, 
		8, 
		8, 
		8, 
	};

	/**
	 * ChannelInfo's meta-data: virtual column flags.
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
	};

	/**
	 * ChannelInfo's meta-data: column byte-offset.
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
		72, 
		76, 
		84, 
		88, 
		96, 
		100, 
		108, 
		109, 
		117, 
		125, 
		133, 
		141, 
	};
	
	/**
	 * ChannelInfo's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 149;
	
	/**
	* time's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIME = 0;
	/**
	* slope's meta-data: index in a record.
	*/	
	public static final int COLUMN_SLOPE = 1;
	/**
	* maxslope's meta-data: index in a record.
	*/	
	public static final int COLUMN_MAXSLOPE = 2;
	/**
	* minslope's meta-data: index in a record.
	*/	
	public static final int COLUMN_MINSLOPE = 3;
	/**
	* maxwidth's meta-data: index in a record.
	*/	
	public static final int COLUMN_MAXWIDTH = 4;
	/**
	* minwidth's meta-data: index in a record.
	*/	
	public static final int COLUMN_MINWIDTH = 5;
	/**
	* width's meta-data: index in a record.
	*/	
	public static final int COLUMN_WIDTH = 6;
	/**
	* maxprice's meta-data: index in a record.
	*/	
	public static final int COLUMN_MAXPRICE = 7;
	/**
	* minprice's meta-data: index in a record.
	*/	
	public static final int COLUMN_MINPRICE = 8;
	/**
	* tickslength's meta-data: index in a record.
	*/	
	public static final int COLUMN_TICKSLENGTH = 9;
	/**
	* lwratio's meta-data: index in a record.
	*/	
	public static final int COLUMN_LWRATIO = 10;
	/**
	* tickspivot's meta-data: index in a record.
	*/	
	public static final int COLUMN_TICKSPIVOT = 11;
	/**
	* pricepivot's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRICEPIVOT = 12;
	/**
	* ticksth's meta-data: index in a record.
	*/	
	public static final int COLUMN_TICKSTH = 13;
	/**
	* priceth's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRICETH = 14;
	/**
	* goingup's meta-data: index in a record.
	*/	
	public static final int COLUMN_GOINGUP = 15;
	/**
	* HHPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_HHPRICE = 16;
	/**
	* LLPrice's meta-data: index in a record.
	*/	
	public static final int COLUMN_LLPRICE = 17;
	/**
	* HHTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_HHTIME = 18;
	/**
	* LLTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_LLTIME = 19;
	/**
	* pearsonr's meta-data: index in a record.
	*/	
	public static final int COLUMN_PEARSONR = 20;

	/**
	 * ChannelInfo's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "8813bf9b-344e-4b1b-8daf-440dca2ef2e1";
	
	/**
	 * ChannelInfo's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "21d8356f-1c0c-4c46-8e52-3d7e45acdd05 LONG; 3db27543-1d04-45a7-8592-7967056b78d3 DOUBLE; 3849d275-2410-455b-8d09-c2276f2b3442 DOUBLE; f61fa8fc-f14d-47f4-a14c-512208e0bdd2 DOUBLE; af183629-3898-49ec-b5bd-4b2c6f290d1c DOUBLE; e3824e82-4171-4330-8dde-1100fb9b8937 DOUBLE; 9a7970c2-01b7-4bc5-96ab-7f23950529bb DOUBLE; 6f6f610f-5085-433d-b668-e8198528eb47 DOUBLE; a4061e85-d894-4852-b9b0-902117246083 DOUBLE; 059252ad-4394-420a-8aed-a2e4dc60a970 INTEGER; 7f40b607-a017-45ff-aa1f-452410f4ecdd DOUBLE; 35fac5bf-1be9-4f32-9daa-98e2dba2e89b INTEGER; f975b0df-7d66-41e6-a9fa-08aa1f438339 DOUBLE; 432023d5-d3f7-472a-b71d-fbe7bafac6ec INTEGER; 9b58e619-9ab9-4f20-b03e-726c31899042 DOUBLE; 81096ad7-7300-4785-956c-51f78c707c80 BOOLEAN; d3c0c246-bf74-4df8-a2d4-f413abc2ae12 DOUBLE; 7c43f8cc-eef7-4c81-b95f-33c709f0187e DOUBLE; 708a52ec-68a4-4c1c-9882-a671a7373590 LONG; f6e87f90-909c-40e0-96e7-ae1616b02a1a LONG; 77c0b2fd-fae2-49f4-b0cd-25e5f010a710 DOUBLE; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer; 
	private ByteBuffer _replaceBuffer_time;
	private ByteBuffer _replaceBuffer_slope;
	private ByteBuffer _replaceBuffer_maxslope;
	private ByteBuffer _replaceBuffer_minslope;
	private ByteBuffer _replaceBuffer_maxwidth;
	private ByteBuffer _replaceBuffer_minwidth;
	private ByteBuffer _replaceBuffer_width;
	private ByteBuffer _replaceBuffer_maxprice;
	private ByteBuffer _replaceBuffer_minprice;
	private ByteBuffer _replaceBuffer_tickslength;
	private ByteBuffer _replaceBuffer_lwratio;
	private ByteBuffer _replaceBuffer_tickspivot;
	private ByteBuffer _replaceBuffer_pricepivot;
	private ByteBuffer _replaceBuffer_ticksth;
	private ByteBuffer _replaceBuffer_priceth;
	private ByteBuffer _replaceBuffer_goingup;
	private ByteBuffer _replaceBuffer_HHPrice;
	private ByteBuffer _replaceBuffer_LLPrice;
	private ByteBuffer _replaceBuffer_HHTime;
	private ByteBuffer _replaceBuffer_LLTime;
	private ByteBuffer _replaceBuffer_pearsonr;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final BaseChartMDBSession _session;

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
	public ChannelInfoMDB(BaseChartMDBSession session, File file, int bufferSize, SessionMode mode) throws IOException {
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
			_replaceBuffer = ByteBuffer.allocate(149);		
			_replaceBuffer_time = ByteBuffer.allocate(8);
			_replaceBuffer_slope = ByteBuffer.allocate(8);
			_replaceBuffer_maxslope = ByteBuffer.allocate(8);
			_replaceBuffer_minslope = ByteBuffer.allocate(8);
			_replaceBuffer_maxwidth = ByteBuffer.allocate(8);
			_replaceBuffer_minwidth = ByteBuffer.allocate(8);
			_replaceBuffer_width = ByteBuffer.allocate(8);
			_replaceBuffer_maxprice = ByteBuffer.allocate(8);
			_replaceBuffer_minprice = ByteBuffer.allocate(8);
			_replaceBuffer_tickslength = ByteBuffer.allocate(4);
			_replaceBuffer_lwratio = ByteBuffer.allocate(8);
			_replaceBuffer_tickspivot = ByteBuffer.allocate(4);
			_replaceBuffer_pricepivot = ByteBuffer.allocate(8);
			_replaceBuffer_ticksth = ByteBuffer.allocate(4);
			_replaceBuffer_priceth = ByteBuffer.allocate(8);
			_replaceBuffer_goingup = ByteBuffer.allocate(1);
			_replaceBuffer_HHPrice = ByteBuffer.allocate(8);
			_replaceBuffer_LLPrice = ByteBuffer.allocate(8);
			_replaceBuffer_HHTime = ByteBuffer.allocate(8);
			_replaceBuffer_LLTime = ByteBuffer.allocate(8);
			_replaceBuffer_pearsonr = ByteBuffer.allocate(8);
		}
	}	

	/**
	* ChannelInfo record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the time column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of time</caption>
		* <tr><td>Column</td><td>time</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long time; /* 0 */
		/**
		* Represents the slope column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of slope</caption>
		* <tr><td>Column</td><td>slope</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double slope; /* 1 */
		/**
		* Represents the maxslope column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of maxslope</caption>
		* <tr><td>Column</td><td>maxslope</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double maxslope; /* 2 */
		/**
		* Represents the minslope column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of minslope</caption>
		* <tr><td>Column</td><td>minslope</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double minslope; /* 3 */
		/**
		* Represents the maxwidth column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of maxwidth</caption>
		* <tr><td>Column</td><td>maxwidth</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double maxwidth; /* 4 */
		/**
		* Represents the minwidth column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of minwidth</caption>
		* <tr><td>Column</td><td>minwidth</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double minwidth; /* 5 */
		/**
		* Represents the width column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of width</caption>
		* <tr><td>Column</td><td>width</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double width; /* 6 */
		/**
		* Represents the maxprice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of maxprice</caption>
		* <tr><td>Column</td><td>maxprice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double maxprice; /* 7 */
		/**
		* Represents the minprice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of minprice</caption>
		* <tr><td>Column</td><td>minprice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double minprice; /* 8 */
		/**
		* Represents the tickslength column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of tickslength</caption>
		* <tr><td>Column</td><td>tickslength</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int tickslength; /* 9 */
		/**
		* Represents the lwratio column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of lwratio</caption>
		* <tr><td>Column</td><td>lwratio</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double lwratio; /* 10 */
		/**
		* Represents the tickspivot column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of tickspivot</caption>
		* <tr><td>Column</td><td>tickspivot</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int tickspivot; /* 11 */
		/**
		* Represents the pricepivot column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of pricepivot</caption>
		* <tr><td>Column</td><td>pricepivot</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double pricepivot; /* 12 */
		/**
		* Represents the ticksth column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of ticksth</caption>
		* <tr><td>Column</td><td>ticksth</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int ticksth; /* 13 */
		/**
		* Represents the priceth column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of priceth</caption>
		* <tr><td>Column</td><td>priceth</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double priceth; /* 14 */
		/**
		* Represents the goingup column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of goingup</caption>
		* <tr><td>Column</td><td>goingup</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public boolean goingup; /* 15 */
		/**
		* Represents the HHPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of HHPrice</caption>
		* <tr><td>Column</td><td>HHPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double HHPrice; /* 16 */
		/**
		* Represents the LLPrice column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of LLPrice</caption>
		* <tr><td>Column</td><td>LLPrice</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double LLPrice; /* 17 */
		/**
		* Represents the HHTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of HHTime</caption>
		* <tr><td>Column</td><td>HHTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long HHTime; /* 18 */
		/**
		* Represents the LLTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of LLTime</caption>
		* <tr><td>Column</td><td>LLTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long LLTime; /* 19 */
		/**
		* Represents the pearsonr column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of pearsonr</caption>
		* <tr><td>Column</td><td>pearsonr</td></tr>
		* <tr><td>Type</td><td>DOUBLE</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public double pearsonr; /* 20 */

		/**
		* Returns an string representation of the record content.
		*/
		@Override
		public String toString() {
			return "ChannelInfo [ "
				 + "time=" + time + " "	
				 + "slope=" + slope + " "	
				 + "maxslope=" + maxslope + " "	
				 + "minslope=" + minslope + " "	
				 + "maxwidth=" + maxwidth + " "	
				 + "minwidth=" + minwidth + " "	
				 + "width=" + width + " "	
				 + "maxprice=" + maxprice + " "	
				 + "minprice=" + minprice + " "	
				 + "tickslength=" + tickslength + " "	
				 + "lwratio=" + lwratio + " "	
				 + "tickspivot=" + tickspivot + " "	
				 + "pricepivot=" + pricepivot + " "	
				 + "ticksth=" + ticksth + " "	
				 + "priceth=" + priceth + " "	
				 + "goingup=" + goingup + " "	
				 + "HHPrice=" + HHPrice + " "	
				 + "LLPrice=" + LLPrice + " "	
				 + "HHTime=" + HHTime + " "	
				 + "LLTime=" + LLTime + " "	
				 + "pearsonr=" + pearsonr + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Long.valueOf(time),
							Double.valueOf(slope),
							Double.valueOf(maxslope),
							Double.valueOf(minslope),
							Double.valueOf(maxwidth),
							Double.valueOf(minwidth),
							Double.valueOf(width),
							Double.valueOf(maxprice),
							Double.valueOf(minprice),
							Integer.valueOf(tickslength),
							Double.valueOf(lwratio),
							Integer.valueOf(tickspivot),
							Double.valueOf(pricepivot),
							Integer.valueOf(ticksth),
							Double.valueOf(priceth),
							Boolean.valueOf(goingup),
							Double.valueOf(HHPrice),
							Double.valueOf(LLPrice),
							Long.valueOf(HHTime),
							Long.valueOf(LLTime),
							Double.valueOf(pearsonr),
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
				case 0: return time;
				case 1: return slope;
				case 2: return maxslope;
				case 3: return minslope;
				case 4: return maxwidth;
				case 5: return minwidth;
				case 6: return width;
				case 7: return maxprice;
				case 8: return minprice;
				case 9: return tickslength;
				case 10: return lwratio;
				case 11: return tickspivot;
				case 12: return pricepivot;
				case 13: return ticksth;
				case 14: return priceth;
				case 15: return goingup;
				case 16: return HHPrice;
				case 17: return LLPrice;
				case 18: return HHTime;
				case 19: return LLTime;
				case 20: return pearsonr;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.time = record.time;
			this.slope = record.slope;
			this.maxslope = record.maxslope;
			this.minslope = record.minslope;
			this.maxwidth = record.maxwidth;
			this.minwidth = record.minwidth;
			this.width = record.width;
			this.maxprice = record.maxprice;
			this.minprice = record.minprice;
			this.tickslength = record.tickslength;
			this.lwratio = record.lwratio;
			this.tickspivot = record.tickspivot;
			this.pricepivot = record.pricepivot;
			this.ticksth = record.ticksth;
			this.priceth = record.priceth;
			this.goingup = record.goingup;
			this.HHPrice = record.HHPrice;
			this.LLPrice = record.LLPrice;
			this.HHTime = record.HHTime;
			this.LLTime = record.LLTime;
			this.pearsonr = record.pearsonr;
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
	* 	ap.time = getTime();	
	* 	ap.slope = getSlope();	
	* 	ap.maxslope = getMaxslope();	
	* 	ap.minslope = getMinslope();	
	* 	ap.maxwidth = getMaxwidth();	
	* 	ap.minwidth = getMinwidth();	
	* 	ap.width = getWidth();	
	* 	ap.maxprice = getMaxprice();	
	* 	ap.minprice = getMinprice();	
	* 	ap.tickslength = getTickslength();	
	* 	ap.lwratio = getLwratio();	
	* 	ap.tickspivot = getTickspivot();	
	* 	ap.pricepivot = getPricepivot();	
	* 	ap.ticksth = getTicksth();	
	* 	ap.priceth = getPriceth();	
	* 	ap.goingup = getGoingup();	
	* 	ap.HHPrice = getHHPrice();	
	* 	ap.LLPrice = getLLPrice();	
	* 	ap.HHTime = getHHTime();	
	* 	ap.LLTime = getLLTime();	
	* 	ap.pearsonr = getPearsonr();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected ChannelInfoMDB _mdb;	
		protected ByteBuffer _buf;	 
		public long time; /* 0 */
		public double slope; /* 1 */
		public double maxslope; /* 2 */
		public double minslope; /* 3 */
		public double maxwidth; /* 4 */
		public double minwidth; /* 5 */
		public double width; /* 6 */
		public double maxprice; /* 7 */
		public double minprice; /* 8 */
		public int tickslength; /* 9 */
		public double lwratio; /* 10 */
		public int tickspivot; /* 11 */
		public double pricepivot; /* 12 */
		public int ticksth; /* 13 */
		public double priceth; /* 14 */
		public boolean goingup; /* 15 */
		public double HHPrice; /* 16 */
		public double LLPrice; /* 17 */
		public long HHTime; /* 18 */
		public long LLTime; /* 19 */
		public double pearsonr; /* 20 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = ChannelInfoMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 149);
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

				
					_buf.putLong(this.time);
					_buf.putDouble(this.slope);
					_buf.putDouble(this.maxslope);
					_buf.putDouble(this.minslope);
					_buf.putDouble(this.maxwidth);
					_buf.putDouble(this.minwidth);
					_buf.putDouble(this.width);
					_buf.putDouble(this.maxprice);
					_buf.putDouble(this.minprice);
					_buf.putInt(this.tickslength);
					_buf.putDouble(this.lwratio);
					_buf.putInt(this.tickspivot);
					_buf.putDouble(this.pricepivot);
					_buf.putInt(this.ticksth);
					_buf.putDouble(this.priceth);
					_buf.put((byte) (this.goingup? 1 : 0));
					_buf.putDouble(this.HHPrice);
					_buf.putDouble(this.LLPrice);
					_buf.putLong(this.HHTime);
					_buf.putLong(this.LLTime);
					_buf.putDouble(this.pearsonr);

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
					r.time = this.time;
					r.slope = this.slope;
					r.maxslope = this.maxslope;
					r.minslope = this.minslope;
					r.maxwidth = this.maxwidth;
					r.minwidth = this.minwidth;
					r.width = this.width;
					r.maxprice = this.maxprice;
					r.minprice = this.minprice;
					r.tickslength = this.tickslength;
					r.lwratio = this.lwratio;
					r.tickspivot = this.tickspivot;
					r.pricepivot = this.pricepivot;
					r.ticksth = this.ticksth;
					r.priceth = this.priceth;
					r.goingup = this.goingup;
					r.HHPrice = this.HHPrice;
					r.LLPrice = this.LLPrice;
					r.HHTime = this.HHTime;
					r.LLTime = this.LLTime;
					r.pearsonr = this.pearsonr;
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
				this.time = record.time;
				this.slope = record.slope;
				this.maxslope = record.maxslope;
				this.minslope = record.minslope;
				this.maxwidth = record.maxwidth;
				this.minwidth = record.minwidth;
				this.width = record.width;
				this.maxprice = record.maxprice;
				this.minprice = record.minprice;
				this.tickslength = record.tickslength;
				this.lwratio = record.lwratio;
				this.tickspivot = record.tickspivot;
				this.pricepivot = record.pricepivot;
				this.ticksth = record.ticksth;
				this.priceth = record.priceth;
				this.goingup = record.goingup;
				this.HHPrice = record.HHPrice;
				this.LLPrice = record.LLPrice;
				this.HHTime = record.HHTime;
				this.LLTime = record.LLTime;
				this.pearsonr = record.pearsonr;
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
				r.time = record.time;
				r.slope = record.slope;
				r.maxslope = record.maxslope;
				r.minslope = record.minslope;
				r.maxwidth = record.maxwidth;
				r.minwidth = record.minwidth;
				r.width = record.width;
				r.maxprice = record.maxprice;
				r.minprice = record.minprice;
				r.tickslength = record.tickslength;
				r.lwratio = record.lwratio;
				r.tickspivot = record.tickspivot;
				r.pricepivot = record.pricepivot;
				r.ticksth = record.ticksth;
				r.priceth = record.priceth;
				r.goingup = record.goingup;
				r.HHPrice = record.HHPrice;
				r.LLPrice = record.LLPrice;
				r.HHTime = record.HHTime;
				r.LLTime = record.LLTime;
				r.pearsonr = record.pearsonr;
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
				this.time = record.time;
				this.slope = record.slope;
				this.maxslope = record.maxslope;
				this.minslope = record.minslope;
				this.maxwidth = record.maxwidth;
				this.minwidth = record.minwidth;
				this.width = record.width;
				this.maxprice = record.maxprice;
				this.minprice = record.minprice;
				this.tickslength = record.tickslength;
				this.lwratio = record.lwratio;
				this.tickspivot = record.tickspivot;
				this.pricepivot = record.pricepivot;
				this.ticksth = record.ticksth;
				this.priceth = record.priceth;
				this.goingup = record.goingup;
				this.HHPrice = record.HHPrice;
				this.LLPrice = record.LLPrice;
				this.HHTime = record.HHTime;
				this.LLTime = record.LLTime;
				this.pearsonr = record.pearsonr;
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
				_buf.putLong(r.time);
				_buf.putDouble(r.slope);
				_buf.putDouble(r.maxslope);
				_buf.putDouble(r.minslope);
				_buf.putDouble(r.maxwidth);
				_buf.putDouble(r.minwidth);
				_buf.putDouble(r.width);
				_buf.putDouble(r.maxprice);
				_buf.putDouble(r.minprice);
				_buf.putInt(r.tickslength);
				_buf.putDouble(r.lwratio);
				_buf.putInt(r.tickspivot);
				_buf.putDouble(r.pricepivot);
				_buf.putInt(r.ticksth);
				_buf.putDouble(r.priceth);
				_buf.put((byte) (r.goingup? 1 : 0));
				_buf.putDouble(r.HHPrice);
				_buf.putDouble(r.LLPrice);
				_buf.putLong(r.HHTime);
				_buf.putLong(r.LLTime);
				_buf.putDouble(r.pearsonr);
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 149);
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
		public ChannelInfoMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.time = this.time;
			r.slope = this.slope;
			r.maxslope = this.maxslope;
			r.minslope = this.minslope;
			r.maxwidth = this.maxwidth;
			r.minwidth = this.minwidth;
			r.width = this.width;
			r.maxprice = this.maxprice;
			r.minprice = this.minprice;
			r.tickslength = this.tickslength;
			r.lwratio = this.lwratio;
			r.tickspivot = this.tickspivot;
			r.pricepivot = this.pricepivot;
			r.ticksth = this.ticksth;
			r.priceth = this.priceth;
			r.goingup = this.goingup;
			r.HHPrice = this.HHPrice;
			r.LLPrice = this.LLPrice;
			r.HHTime = this.HHTime;
			r.LLTime = this.LLTime;
			r.pearsonr = this.pearsonr;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.time = record.time;
			this.slope = record.slope;
			this.maxslope = record.maxslope;
			this.minslope = record.minslope;
			this.maxwidth = record.maxwidth;
			this.minwidth = record.minwidth;
			this.width = record.width;
			this.maxprice = record.maxprice;
			this.minprice = record.minprice;
			this.tickslength = record.tickslength;
			this.lwratio = record.lwratio;
			this.tickspivot = record.tickspivot;
			this.pricepivot = record.pricepivot;
			this.ticksth = record.ticksth;
			this.priceth = record.priceth;
			this.goingup = record.goingup;
			this.HHPrice = record.HHPrice;
			this.LLPrice = record.LLPrice;
			this.HHTime = record.HHTime;
			this.LLTime = record.LLTime;
			this.pearsonr = record.pearsonr;
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
	 * ChannelInfoMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.time); 
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
		public long time; /* 0 */
		public double slope; /* 1 */
		public double maxslope; /* 2 */
		public double minslope; /* 3 */
		public double maxwidth; /* 4 */
		public double minwidth; /* 5 */
		public double width; /* 6 */
		public double maxprice; /* 7 */
		public double minprice; /* 8 */
		public int tickslength; /* 9 */
		public double lwratio; /* 10 */
		public int tickspivot; /* 11 */
		public double pricepivot; /* 12 */
		public int ticksth; /* 13 */
		public double priceth; /* 14 */
		public boolean goingup; /* 15 */
		public double HHPrice; /* 16 */
		public double LLPrice; /* 17 */
		public long HHTime; /* 18 */
		public long LLTime; /* 19 */
		public double pearsonr; /* 20 */
		
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
				_channel.position(start * 149);
				_buffer = ByteBuffer.allocate(bufferSize * 149);
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
					_channel.position(start * 149);
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
						this.time = r.time;
						this.slope = r.slope;
						this.maxslope = r.maxslope;
						this.minslope = r.minslope;
						this.maxwidth = r.maxwidth;
						this.minwidth = r.minwidth;
						this.width = r.width;
						this.maxprice = r.maxprice;
						this.minprice = r.minprice;
						this.tickslength = r.tickslength;
						this.lwratio = r.lwratio;
						this.tickspivot = r.tickspivot;
						this.pricepivot = r.pricepivot;
						this.ticksth = r.ticksth;
						this.priceth = r.priceth;
						this.goingup = r.goingup;
						this.HHPrice = r.HHPrice;
						this.LLPrice = r.LLPrice;
						this.HHTime = r.HHTime;
						this.LLTime = r.LLTime;
						this.pearsonr = r.pearsonr;
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
				
				this.time = _buffer.getLong();
				this.slope = _buffer.getDouble();
				this.maxslope = _buffer.getDouble();
				this.minslope = _buffer.getDouble();
				this.maxwidth = _buffer.getDouble();
				this.minwidth = _buffer.getDouble();
				this.width = _buffer.getDouble();
				this.maxprice = _buffer.getDouble();
				this.minprice = _buffer.getDouble();
				this.tickslength = _buffer.getInt();
				this.lwratio = _buffer.getDouble();
				this.tickspivot = _buffer.getInt();
				this.pricepivot = _buffer.getDouble();
				this.ticksth = _buffer.getInt();
				this.priceth = _buffer.getDouble();
				this.goingup = _buffer.get() == 0? false : true;
				this.HHPrice = _buffer.getDouble();
				this.LLPrice = _buffer.getDouble();
				this.HHTime = _buffer.getLong();
				this.LLTime = _buffer.getLong();
				this.pearsonr = _buffer.getDouble();
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
			r.time = this.time;
			r.slope = this.slope;
			r.maxslope = this.maxslope;
			r.minslope = this.minslope;
			r.maxwidth = this.maxwidth;
			r.minwidth = this.minwidth;
			r.width = this.width;
			r.maxprice = this.maxprice;
			r.minprice = this.minprice;
			r.tickslength = this.tickslength;
			r.lwratio = this.lwratio;
			r.tickspivot = this.tickspivot;
			r.pricepivot = this.pricepivot;
			r.ticksth = this.ticksth;
			r.priceth = this.priceth;
			r.goingup = this.goingup;
			r.HHPrice = this.HHPrice;
			r.LLPrice = this.LLPrice;
			r.HHTime = this.HHTime;
			r.LLTime = this.LLTime;
			r.pearsonr = this.pearsonr;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public ChannelInfoMDB getMDB() {
			return ChannelInfoMDB.this;
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
		* ChannelInfoMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.time); 
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
			ByteBuffer _buffer_time; // used by index-of-time method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			public long time; /* 0 */
			public double slope; /* 1 */
			public double maxslope; /* 2 */
			public double minslope; /* 3 */
			public double maxwidth; /* 4 */
			public double minwidth; /* 5 */
			public double width; /* 6 */
			public double maxprice; /* 7 */
			public double minprice; /* 8 */
			public int tickslength; /* 9 */
			public double lwratio; /* 10 */
			public int tickspivot; /* 11 */
			public double pricepivot; /* 12 */
			public int ticksth; /* 13 */
			public double priceth; /* 14 */
			public boolean goingup; /* 15 */
			public double HHPrice; /* 16 */
			public double LLPrice; /* 17 */
			public long HHTime; /* 18 */
			public long LLTime; /* 19 */
			public double pearsonr; /* 20 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(149);
					_buffer_time = ByteBuffer.allocate(8);
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
							this.time = r.time;
							this.slope = r.slope;
							this.maxslope = r.maxslope;
							this.minslope = r.minslope;
							this.maxwidth = r.maxwidth;
							this.minwidth = r.minwidth;
							this.width = r.width;
							this.maxprice = r.maxprice;
							this.minprice = r.minprice;
							this.tickslength = r.tickslength;
							this.lwratio = r.lwratio;
							this.tickspivot = r.tickspivot;
							this.pricepivot = r.pricepivot;
							this.ticksth = r.ticksth;
							this.priceth = r.priceth;
							this.goingup = r.goingup;
							this.HHPrice = r.HHPrice;
							this.LLPrice = r.LLPrice;
							this.HHTime = r.HHTime;
							this.LLTime = r.LLTime;
							this.pearsonr = r.pearsonr;
							return;					
						}
					} finally {
						_readLock.unlock();
					}
				}
				
				assert !_memory;
				
				_row = position;
				_buffer.rewind();
				_channel.read(_buffer, position * 149);
				_buffer.rewind();
				this.time = _buffer.getLong();
				this.slope = _buffer.getDouble();
				this.maxslope = _buffer.getDouble();
				this.minslope = _buffer.getDouble();
				this.maxwidth = _buffer.getDouble();
				this.minwidth = _buffer.getDouble();
				this.width = _buffer.getDouble();
				this.maxprice = _buffer.getDouble();
				this.minprice = _buffer.getDouble();
				this.tickslength = _buffer.getInt();
				this.lwratio = _buffer.getDouble();
				this.tickspivot = _buffer.getInt();
				this.pricepivot = _buffer.getDouble();
				this.ticksth = _buffer.getInt();
				this.priceth = _buffer.getDouble();
				this.goingup = _buffer.get() == 0? false : true;
				this.HHPrice = _buffer.getDouble();
				this.LLPrice = _buffer.getDouble();
				this.HHTime = _buffer.getLong();
				this.LLTime = _buffer.getLong();
				this.pearsonr = _buffer.getDouble();
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
				r.time = this.time;
				r.slope = this.slope;
				r.maxslope = this.maxslope;
				r.minslope = this.minslope;
				r.maxwidth = this.maxwidth;
				r.minwidth = this.minwidth;
				r.width = this.width;
				r.maxprice = this.maxprice;
				r.minprice = this.minprice;
				r.tickslength = this.tickslength;
				r.lwratio = this.lwratio;
				r.tickspivot = this.tickspivot;
				r.pricepivot = this.pricepivot;
				r.ticksth = this.ticksth;
				r.priceth = this.priceth;
				r.goingup = this.goingup;
				r.HHPrice = this.HHPrice;
				r.LLPrice = this.LLPrice;
				r.HHTime = this.HHTime;
				r.LLTime = this.LLTime;
				r.pearsonr = this.pearsonr;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public ChannelInfoMDB getMDB() {
				return ChannelInfoMDB.this;
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
		buffer.putLong(obj.time); 
		buffer.putDouble(obj.slope); 
		buffer.putDouble(obj.maxslope); 
		buffer.putDouble(obj.minslope); 
		buffer.putDouble(obj.maxwidth); 
		buffer.putDouble(obj.minwidth); 
		buffer.putDouble(obj.width); 
		buffer.putDouble(obj.maxprice); 
		buffer.putDouble(obj.minprice); 
		buffer.putInt(obj.tickslength); 
		buffer.putDouble(obj.lwratio); 
		buffer.putInt(obj.tickspivot); 
		buffer.putDouble(obj.pricepivot); 
		buffer.putInt(obj.ticksth); 
		buffer.putDouble(obj.priceth); 
		buffer.put(obj.goingup ? (byte) 1 : (byte) 0); 
		buffer.putDouble(obj.HHPrice); 
		buffer.putDouble(obj.LLPrice); 
		buffer.putLong(obj.HHTime); 
		buffer.putLong(obj.LLTime); 
		buffer.putDouble(obj.pearsonr); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(RandomCursor obj, ByteBuffer buffer) {
		buffer.putLong(obj.time); 
		buffer.putDouble(obj.slope); 
		buffer.putDouble(obj.maxslope); 
		buffer.putDouble(obj.minslope); 
		buffer.putDouble(obj.maxwidth); 
		buffer.putDouble(obj.minwidth); 
		buffer.putDouble(obj.width); 
		buffer.putDouble(obj.maxprice); 
		buffer.putDouble(obj.minprice); 
		buffer.putInt(obj.tickslength); 
		buffer.putDouble(obj.lwratio); 
		buffer.putInt(obj.tickspivot); 
		buffer.putDouble(obj.pricepivot); 
		buffer.putInt(obj.ticksth); 
		buffer.putDouble(obj.priceth); 
		buffer.put(obj.goingup ? (byte) 1 : (byte) 0); 
		buffer.putDouble(obj.HHPrice); 
		buffer.putDouble(obj.LLPrice); 
		buffer.putLong(obj.HHTime); 
		buffer.putLong(obj.LLTime); 
		buffer.putDouble(obj.pearsonr); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(Record obj, ByteBuffer buffer) {
		buffer.putLong(obj.time); 
		buffer.putDouble(obj.slope); 
		buffer.putDouble(obj.maxslope); 
		buffer.putDouble(obj.minslope); 
		buffer.putDouble(obj.maxwidth); 
		buffer.putDouble(obj.minwidth); 
		buffer.putDouble(obj.width); 
		buffer.putDouble(obj.maxprice); 
		buffer.putDouble(obj.minprice); 
		buffer.putInt(obj.tickslength); 
		buffer.putDouble(obj.lwratio); 
		buffer.putInt(obj.tickspivot); 
		buffer.putDouble(obj.pricepivot); 
		buffer.putInt(obj.ticksth); 
		buffer.putDouble(obj.priceth); 
		buffer.put(obj.goingup ? (byte) 1 : (byte) 0); 
		buffer.putDouble(obj.HHPrice); 
		buffer.putDouble(obj.LLPrice); 
		buffer.putLong(obj.HHTime); 
		buffer.putLong(obj.LLTime); 
		buffer.putDouble(obj.pearsonr); 
	}
	


	/**
	 * Replace the record at the given <code>index</code>.
	 *
	 * @param index The index to update.
	 * @param val_time The value for column time.
	 * @param val_slope The value for column slope.
	 * @param val_maxslope The value for column maxslope.
	 * @param val_minslope The value for column minslope.
	 * @param val_maxwidth The value for column maxwidth.
	 * @param val_minwidth The value for column minwidth.
	 * @param val_width The value for column width.
	 * @param val_maxprice The value for column maxprice.
	 * @param val_minprice The value for column minprice.
	 * @param val_tickslength The value for column tickslength.
	 * @param val_lwratio The value for column lwratio.
	 * @param val_tickspivot The value for column tickspivot.
	 * @param val_pricepivot The value for column pricepivot.
	 * @param val_ticksth The value for column ticksth.
	 * @param val_priceth The value for column priceth.
	 * @param val_goingup The value for column goingup.
	 * @param val_HHPrice The value for column HHPrice.
	 * @param val_LLPrice The value for column LLPrice.
	 * @param val_HHTime The value for column HHTime.
	 * @param val_LLTime The value for column LLTime.
	 * @param val_pearsonr The value for column pearsonr.
	* @throws IOException If there is any I/O error.
	 */
	public void replace(long index 
							, long val_time
							, double val_slope
							, double val_maxslope
							, double val_minslope
							, double val_maxwidth
							, double val_minwidth
							, double val_width
							, double val_maxprice
							, double val_minprice
							, int val_tickslength
							, double val_lwratio
							, int val_tickspivot
							, double val_pricepivot
							, int val_ticksth
							, double val_priceth
							, boolean val_goingup
							, double val_HHPrice
							, double val_LLPrice
							, long val_HHTime
							, long val_LLTime
							, double val_pearsonr
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
						r.time = val_time;
						r.slope = val_slope;
						r.maxslope = val_maxslope;
						r.minslope = val_minslope;
						r.maxwidth = val_maxwidth;
						r.minwidth = val_minwidth;
						r.width = val_width;
						r.maxprice = val_maxprice;
						r.minprice = val_minprice;
						r.tickslength = val_tickslength;
						r.lwratio = val_lwratio;
						r.tickspivot = val_tickspivot;
						r.pricepivot = val_pricepivot;
						r.ticksth = val_ticksth;
						r.priceth = val_priceth;
						r.goingup = val_goingup;
						r.HHPrice = val_HHPrice;
						r.LLPrice = val_LLPrice;
						r.HHTime = val_HHTime;
						r.LLTime = val_LLTime;
						r.pearsonr = val_pearsonr;
					}
					return;
				} 				
			}
		
			_replaceBuffer.rewind();
			_replaceBuffer.putLong(val_time);
			_replaceBuffer.putDouble(val_slope);
			_replaceBuffer.putDouble(val_maxslope);
			_replaceBuffer.putDouble(val_minslope);
			_replaceBuffer.putDouble(val_maxwidth);
			_replaceBuffer.putDouble(val_minwidth);
			_replaceBuffer.putDouble(val_width);
			_replaceBuffer.putDouble(val_maxprice);
			_replaceBuffer.putDouble(val_minprice);
			_replaceBuffer.putInt(val_tickslength);
			_replaceBuffer.putDouble(val_lwratio);
			_replaceBuffer.putInt(val_tickspivot);
			_replaceBuffer.putDouble(val_pricepivot);
			_replaceBuffer.putInt(val_ticksth);
			_replaceBuffer.putDouble(val_priceth);
			_replaceBuffer.put((byte) (val_goingup ? 1 : 0));
			_replaceBuffer.putDouble(val_HHPrice);
			_replaceBuffer.putDouble(val_LLPrice);
			_replaceBuffer.putLong(val_HHTime);
			_replaceBuffer.putLong(val_LLTime);
			_replaceBuffer.putDouble(val_pearsonr);
			_replaceBuffer.rewind();
			appender();
			_appender._channel.write(_replaceBuffer, index * 149);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	
	/**
	 * Update the record at the given <code>index</code>.
	 * Also you can use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)}.
	 *
	 * @param index The index to update.
	 * @param record Contains the data to set.
	 * @see #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)
	 * @throws IOException If there is any I/O error.
	 */
	public void replace(long index, Record record) throws IOException {
		replace(index 
					, record.time		
					, record.slope		
					, record.maxslope		
					, record.minslope		
					, record.maxwidth		
					, record.minwidth		
					, record.width		
					, record.maxprice		
					, record.minprice		
					, record.tickslength		
					, record.lwratio		
					, record.tickspivot		
					, record.pricepivot		
					, record.ticksth		
					, record.priceth		
					, record.goingup		
					, record.HHPrice		
					, record.LLPrice		
					, record.HHTime		
					, record.LLTime		
					, record.pearsonr		
				);			
	}

	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "time" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
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
			_appender._channel.write(_replaceBuffer_time, index * 149 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "slope" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "slope".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_slope(long index, double value) throws IOException {
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
			_replaceBuffer_slope.putDouble(value);
			_replaceBuffer_slope.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_slope, index * 149 + 8);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "maxslope" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "maxslope".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_maxslope(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].maxslope = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_maxslope.rewind();
			_replaceBuffer_maxslope.putDouble(value);
			_replaceBuffer_maxslope.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_maxslope, index * 149 + 16);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "minslope" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "minslope".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_minslope(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].minslope = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_minslope.rewind();
			_replaceBuffer_minslope.putDouble(value);
			_replaceBuffer_minslope.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_minslope, index * 149 + 24);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "maxwidth" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "maxwidth".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_maxwidth(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].maxwidth = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_maxwidth.rewind();
			_replaceBuffer_maxwidth.putDouble(value);
			_replaceBuffer_maxwidth.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_maxwidth, index * 149 + 32);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "minwidth" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "minwidth".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_minwidth(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].minwidth = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_minwidth.rewind();
			_replaceBuffer_minwidth.putDouble(value);
			_replaceBuffer_minwidth.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_minwidth, index * 149 + 40);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "width" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "width".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_width(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].width = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_width.rewind();
			_replaceBuffer_width.putDouble(value);
			_replaceBuffer_width.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_width, index * 149 + 48);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "maxprice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "maxprice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_maxprice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].maxprice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_maxprice.rewind();
			_replaceBuffer_maxprice.putDouble(value);
			_replaceBuffer_maxprice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_maxprice, index * 149 + 56);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "minprice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "minprice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_minprice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].minprice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_minprice.rewind();
			_replaceBuffer_minprice.putDouble(value);
			_replaceBuffer_minprice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_minprice, index * 149 + 64);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "tickslength" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "tickslength".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_tickslength(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].tickslength = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_tickslength.rewind();
			_replaceBuffer_tickslength.putInt(value);
			_replaceBuffer_tickslength.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_tickslength, index * 149 + 72);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "lwratio" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "lwratio".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_lwratio(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].lwratio = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_lwratio.rewind();
			_replaceBuffer_lwratio.putDouble(value);
			_replaceBuffer_lwratio.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_lwratio, index * 149 + 76);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "tickspivot" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "tickspivot".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_tickspivot(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].tickspivot = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_tickspivot.rewind();
			_replaceBuffer_tickspivot.putInt(value);
			_replaceBuffer_tickspivot.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_tickspivot, index * 149 + 84);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "pricepivot" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "pricepivot".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_pricepivot(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].pricepivot = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_pricepivot.rewind();
			_replaceBuffer_pricepivot.putDouble(value);
			_replaceBuffer_pricepivot.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_pricepivot, index * 149 + 88);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "ticksth" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "ticksth".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_ticksth(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].ticksth = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_ticksth.rewind();
			_replaceBuffer_ticksth.putInt(value);
			_replaceBuffer_ticksth.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_ticksth, index * 149 + 96);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "priceth" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "priceth".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_priceth(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].priceth = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_priceth.rewind();
			_replaceBuffer_priceth.putDouble(value);
			_replaceBuffer_priceth.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_priceth, index * 149 + 100);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "goingup" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "goingup".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_goingup(long index, boolean value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].goingup = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_goingup.rewind();
			_replaceBuffer_goingup.put((byte) (value ? 1 : 0));
			_replaceBuffer_goingup.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_goingup, index * 149 + 108);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "HHPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "HHPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_HHPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].HHPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_HHPrice.rewind();
			_replaceBuffer_HHPrice.putDouble(value);
			_replaceBuffer_HHPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_HHPrice, index * 149 + 109);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "LLPrice" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "LLPrice".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_LLPrice(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].LLPrice = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_LLPrice.rewind();
			_replaceBuffer_LLPrice.putDouble(value);
			_replaceBuffer_LLPrice.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_LLPrice, index * 149 + 117);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "HHTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "HHTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_HHTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].HHTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_HHTime.rewind();
			_replaceBuffer_HHTime.putLong(value);
			_replaceBuffer_HHTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_HHTime, index * 149 + 125);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "LLTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "LLTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_LLTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].LLTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_LLTime.rewind();
			_replaceBuffer_LLTime.putLong(value);
			_replaceBuffer_LLTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_LLTime, index * 149 + 133);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "pearsonr" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, double, double, double, double, double, double, double, double, int, double, int, double, int, double, boolean, double, double, long, long, double)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "pearsonr".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_pearsonr(long index, double value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].pearsonr = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_pearsonr.rewind();
			_replaceBuffer_pearsonr.putDouble(value);
			_replaceBuffer_pearsonr.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_pearsonr, index * 149 + 141);
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
	 *			The lower value of <code>time</code>.
	 * @param upper
	 *			The upper value of <code>time</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_Time_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTime(randCursor, lower) - 1;

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
		
			if (cursor.time > upper) {
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
	* Like {@link #select__where_Time_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_Time_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_Time_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.time > upper) {
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
	* Column <code>time</code> order validator.
	*/
	public static final IValidator<Record> TIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.time;
			long curValue = current.time;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"time(" + row1 + ")=" + prevValue + " > " + "time(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	 * <p>
	 * Record comparator for the column <code>time</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class TimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.time < o2.time? -1 : (o1.time > o2.time? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfTime(Record[] data, long key) {
		return indexOfTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the time value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>time</code> order specified in the column definition. 
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
	public static int indexOfTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].time;
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
	* Like {@link #indexOfTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfTime(RandomCursor cursor, long key) throws IOException {
		return indexOfTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the time value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>time</code> order specified in the column definition. 
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
	public long indexOfTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.time == key ? 0 
							: (r.time < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_time;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 149 + 0);
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
	* Like {@link #indexOfTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfTime_exact(Record[] data, long key) {
		return indexOfTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfTime(Record[], long, int, int)} 
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
	public static int indexOfTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].time;
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
	* Like {@link #indexOfTime(RandomCursor, long, long, long)} 
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
	public long indexOfTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.time == key ? 0 
							: (r.time < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_time;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 149 + 0);
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
	 * Like {@link #indexOfTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given time and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of time.
	* @param time Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateTime(RandomCursor randCursor, long time) throws IOException {
		if (_size > 0) {
			long len = indexOfTime(randCursor, time);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.time > time) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>time</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param time The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_time_is(RandomCursor cursor, long time) throws IOException {
		if (_size > 0) {
			long i = indexOfTime_exact(cursor, time);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.time == time;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the time values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfTime(null, keyLower, 0L, high);
			long b = indexOfTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfTime(cursor, keyLower, 0L, high);
		long b = indexOfTime(cursor, keyUpper, 0L, high);
		return b - a;
	}


	@SuppressWarnings("unchecked")
	@Override
	public BaseChartMDBSession getSession() {
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
		return _memory? 0 : _file.length() / 149;
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

			long newLen = len * 149;
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
	

