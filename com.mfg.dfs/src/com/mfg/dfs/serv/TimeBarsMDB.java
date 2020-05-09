package com.mfg.dfs.serv;

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
 * This class provides the API to manipulate TimeBars files. 
 * Here you will find the methods to modify and query the TimeBars files. 
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
 * <h3>TimeBars definition</h3>
 * <table border=1>
 *	<caption>TimeBars</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>timestamp</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>low</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>to_open</td>
 *		<td>SHORT</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>to_high</td>
 *		<td>SHORT</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>to_close</td>
 *		<td>SHORT</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>volume</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * </table>
 * <h3>TimeBarsMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on TimeBars files:
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
 * dfsdbMDBSession session = ...;
 * TimeBarsMDB mdb = session.connectTo_TimeBarsMDB("timebars.mdb");
 * 
 * // request the appender.
 * TimeBarsMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.timestamp = ...;
 * app.low = ...;
 * app.to_open = ...;
 * app.to_high = ...;
 * app.to_close = ...;
 * app.volume = ...;

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
 * TimeBarsMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * TimeBars.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.timestamp + " "
 * 			+ cursor.low + " "
 * 			+ cursor.to_open + " "
 * 			+ cursor.to_high + " "
 * 			+ cursor.to_close + " "
 * 			+ cursor.volume + " "
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
 * TimeBarsMDB mdb = ...;
 *
 * // request a random cursor
 * TimeBarsMDB.RandomCursor cursor = mdb.randomCursor();
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
 * TimeBarsMDB mdb = ...;
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
 * TimeBarsMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values 										
 * long new_val_timestamp = ...;
 * int new_val_low = ...;
 * short new_val_to_open = ...;
 * short new_val_to_high = ...;
 * short new_val_to_close = ...;
 * int new_val_volume = ...;
 *
 * mdb.replace(index 
 * 		, new_val_timestamp
 * 		, new_val_low
 * 		, new_val_to_open
 * 		, new_val_to_high
 * 		, new_val_to_close
 * 		, new_val_volume
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link TimeBarsMDB#replace_timestamp(long, long)}: To replace the timestamp value.</li>
 * <li>{@link TimeBarsMDB#replace_low(long, int)}: To replace the low value.</li>
 * <li>{@link TimeBarsMDB#replace_to_open(long, short)}: To replace the to_open value.</li>
 * <li>{@link TimeBarsMDB#replace_to_high(long, short)}: To replace the to_high value.</li>
 * <li>{@link TimeBarsMDB#replace_to_close(long, short)}: To replace the to_close value.</li>
 * <li>{@link TimeBarsMDB#replace_volume(long, int)}: To replace the volume value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see dfsdbMDBSession#connectTo_TimeBarsMDB(String)
 */

public final class TimeBarsMDB
/* BEGIN MDB EXTENDS */
extends MDB<TimeBarsMDB.Record> implements com.mfg.dfs.misc.IDfsDb
/* END MDB EXTENDS */
{

/* BEGIN USER MDB */

	@Override
	public long indexOfPrimaryKey(IRandomCursor<? extends IRecord> rc, long time)
			throws IOException {
		// // T ODO: lino, the best is to get the cursor as an argument
		// try (RandomCursor c = randomCursor()) {
		//
		// }
		return indexOfTimestamp_exact((RandomCursor) rc, time);

	}

	@Override
	public long indexOfPrimaryKey_lenient(IRandomCursor<? extends IRecord> rc,
			long time) throws IOException {
		// // TO DO: lino, the best is to get the cursor as an argument
		// try (RandomCursor c = randomCursor()) {

		//
		// }
		if (size() == 0) {
			return -1;
		}
		return indexOfTimestamp((RandomCursor) rc, time);
	}

	@Override
	public long firstKey() throws IOException {
		RandomCursor thread_randomCursor = thread_randomCursor();
		thread_randomCursor.seekFirst();
		return thread_randomCursor.timestamp;
	}

	@Override
	public long lastKey() throws IOException {
		RandomCursor thread_randomCursor = thread_randomCursor();
		thread_randomCursor.seekLast();
		return thread_randomCursor.timestamp;
	}

	/* END USER MDB */
	/**
	 * TimeBars's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"timestamp",
		"low",
		"to_open",
		"to_high",
		"to_close",
		"volume",
	};
	
	/**
	 * TimeBars's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		long.class,
		int.class,
		short.class,
		short.class,
		short.class,
		int.class,
	};
	
	/**
	 * TimeBars's meta-data: column Java types size (in bytes).
	 */
	public static final int[] COLUMNS_SIZE = { 
		8, 
		4, 
		2, 
		2, 
		2, 
		4, 
	};

	/**
	 * TimeBars's meta-data: virtual column flags.
	 */
	public static final boolean[] COLUMNS_IS_VIRTUAL = { 
		false, 
		false, 
		false, 
		false, 
		false, 
		false, 
	};

	/**
	 * TimeBars's meta-data: column byte-offset.
	 */
	public static final int[] COLUMN_OFFSET = {  
		0, 
		8, 
		12, 
		14, 
		16, 
		18, 
	};
	
	/**
	 * TimeBars's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 22;
	
	/**
	* timestamp's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIMESTAMP = 0;
	/**
	* low's meta-data: index in a record.
	*/	
	public static final int COLUMN_LOW = 1;
	/**
	* to_open's meta-data: index in a record.
	*/	
	public static final int COLUMN_TO_OPEN = 2;
	/**
	* to_high's meta-data: index in a record.
	*/	
	public static final int COLUMN_TO_HIGH = 3;
	/**
	* to_close's meta-data: index in a record.
	*/	
	public static final int COLUMN_TO_CLOSE = 4;
	/**
	* volume's meta-data: index in a record.
	*/	
	public static final int COLUMN_VOLUME = 5;

	/**
	 * TimeBars's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "06c238ef-7bc4-40f7-8a1e-cdf5a9011f18";
	
	/**
	 * TimeBars's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "b4c8d183-8156-40b3-b45e-35b6d52c8937 LONG; 79d52d78-0bd7-455b-b5b0-3b6aad2ba830 INTEGER; 0e6031a8-4abe-4c29-8457-14be2edf198d SHORT; 55808c44-8ad4-4f5a-83c8-e5196219ce3e SHORT; afd30ebe-1160-469c-94b2-6a9c8eb63997 SHORT; c0f92a98-d0d1-4735-9c5f-f716078ab760 INTEGER; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer; 
	private ByteBuffer _replaceBuffer_timestamp;
	private ByteBuffer _replaceBuffer_low;
	private ByteBuffer _replaceBuffer_to_open;
	private ByteBuffer _replaceBuffer_to_high;
	private ByteBuffer _replaceBuffer_to_close;
	private ByteBuffer _replaceBuffer_volume;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final dfsdbMDBSession _session;

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
	public TimeBarsMDB(dfsdbMDBSession session, File file, int bufferSize, SessionMode mode) throws IOException {
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
			_replaceBuffer = ByteBuffer.allocate(22);		
			_replaceBuffer_timestamp = ByteBuffer.allocate(8);
			_replaceBuffer_low = ByteBuffer.allocate(4);
			_replaceBuffer_to_open = ByteBuffer.allocate(2);
			_replaceBuffer_to_high = ByteBuffer.allocate(2);
			_replaceBuffer_to_close = ByteBuffer.allocate(2);
			_replaceBuffer_volume = ByteBuffer.allocate(4);
		}
	}	

	/**
	* TimeBars record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the timestamp column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of timestamp</caption>
		* <tr><td>Column</td><td>timestamp</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long timestamp; /* 0 */
		/**
		* Represents the low column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of low</caption>
		* <tr><td>Column</td><td>low</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int low; /* 1 */
		/**
		* Represents the to_open column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of to_open</caption>
		* <tr><td>Column</td><td>to_open</td></tr>
		* <tr><td>Type</td><td>SHORT</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public short to_open; /* 2 */
		/**
		* Represents the to_high column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of to_high</caption>
		* <tr><td>Column</td><td>to_high</td></tr>
		* <tr><td>Type</td><td>SHORT</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public short to_high; /* 3 */
		/**
		* Represents the to_close column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of to_close</caption>
		* <tr><td>Column</td><td>to_close</td></tr>
		* <tr><td>Type</td><td>SHORT</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public short to_close; /* 4 */
		/**
		* Represents the volume column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of volume</caption>
		* <tr><td>Column</td><td>volume</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int volume; /* 5 */

		/**
		* Returns an string representation of the record content.
		*/
		@Override
		public String toString() {
			return "TimeBars [ "
				 + "timestamp=" + timestamp + " "	
				 + "low=" + low + " "	
				 + "to_open=" + to_open + " "	
				 + "to_high=" + to_high + " "	
				 + "to_close=" + to_close + " "	
				 + "volume=" + volume + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Long.valueOf(timestamp),
							Integer.valueOf(low),
							Short.valueOf(to_open),
							Short.valueOf(to_high),
							Short.valueOf(to_close),
							Integer.valueOf(volume),
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
				case 0: return timestamp;
				case 1: return low;
				case 2: return to_open;
				case 3: return to_high;
				case 4: return to_close;
				case 5: return volume;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.timestamp = record.timestamp;
			this.low = record.low;
			this.to_open = record.to_open;
			this.to_high = record.to_high;
			this.to_close = record.to_close;
			this.volume = record.volume;
		}

/* BEGIN USER RECORD */

		public long getPrimaryKey() {
			return timestamp;
		}

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
	* 	ap.timestamp = getTimestamp();	
	* 	ap.low = getLow();	
	* 	ap.to_open = getTo_open();	
	* 	ap.to_high = getTo_high();	
	* 	ap.to_close = getTo_close();	
	* 	ap.volume = getVolume();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected TimeBarsMDB _mdb;	
		protected ByteBuffer _buf;	 
		public long timestamp; /* 0 */
		public int low; /* 1 */
		public short to_open; /* 2 */
		public short to_high; /* 3 */
		public short to_close; /* 4 */
		public int volume; /* 5 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = TimeBarsMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 22);
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

				
					_buf.putLong(this.timestamp);
					_buf.putInt(this.low);
					_buf.putShort(this.to_open);
					_buf.putShort(this.to_high);
					_buf.putShort(this.to_close);
					_buf.putInt(this.volume);

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
					r.timestamp = this.timestamp;
					r.low = this.low;
					r.to_open = this.to_open;
					r.to_high = this.to_high;
					r.to_close = this.to_close;
					r.volume = this.volume;
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
				this.timestamp = record.timestamp;
				this.low = record.low;
				this.to_open = record.to_open;
				this.to_high = record.to_high;
				this.to_close = record.to_close;
				this.volume = record.volume;
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
				r.timestamp = record.timestamp;
				r.low = record.low;
				r.to_open = record.to_open;
				r.to_high = record.to_high;
				r.to_close = record.to_close;
				r.volume = record.volume;
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
				this.timestamp = record.timestamp;
				this.low = record.low;
				this.to_open = record.to_open;
				this.to_high = record.to_high;
				this.to_close = record.to_close;
				this.volume = record.volume;
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
				_buf.putLong(r.timestamp);
				_buf.putInt(r.low);
				_buf.putShort(r.to_open);
				_buf.putShort(r.to_high);
				_buf.putShort(r.to_close);
				_buf.putInt(r.volume);
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 22);
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
		public TimeBarsMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.timestamp = this.timestamp;
			r.low = this.low;
			r.to_open = this.to_open;
			r.to_high = this.to_high;
			r.to_close = this.to_close;
			r.volume = this.volume;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.timestamp = record.timestamp;
			this.low = record.low;
			this.to_open = record.to_open;
			this.to_high = record.to_high;
			this.to_close = record.to_close;
			this.volume = record.volume;
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
	 * TimeBarsMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.timestamp); 
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
		public long timestamp; /* 0 */
		public int low; /* 1 */
		public short to_open; /* 2 */
		public short to_high; /* 3 */
		public short to_close; /* 4 */
		public int volume; /* 5 */
		
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
				_channel.position(start * 22);
				_buffer = ByteBuffer.allocate(bufferSize * 22);
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
					_channel.position(start * 22);
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
						this.timestamp = r.timestamp;
						this.low = r.low;
						this.to_open = r.to_open;
						this.to_high = r.to_high;
						this.to_close = r.to_close;
						this.volume = r.volume;
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
				
				this.timestamp = _buffer.getLong();
				this.low = _buffer.getInt();
				this.to_open = _buffer.getShort();
				this.to_high = _buffer.getShort();
				this.to_close = _buffer.getShort();
				this.volume = _buffer.getInt();
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
			r.timestamp = this.timestamp;
			r.low = this.low;
			r.to_open = this.to_open;
			r.to_high = this.to_high;
			r.to_close = this.to_close;
			r.volume = this.volume;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public TimeBarsMDB getMDB() {
			return TimeBarsMDB.this;
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
		* TimeBarsMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.timestamp); 
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
			ByteBuffer _buffer_timestamp; // used by index-of-timestamp method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			public long timestamp; /* 0 */
			public int low; /* 1 */
			public short to_open; /* 2 */
			public short to_high; /* 3 */
			public short to_close; /* 4 */
			public int volume; /* 5 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(22);
					_buffer_timestamp = ByteBuffer.allocate(8);
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
							this.timestamp = r.timestamp;
							this.low = r.low;
							this.to_open = r.to_open;
							this.to_high = r.to_high;
							this.to_close = r.to_close;
							this.volume = r.volume;
							return;					
						}
					} finally {
						_readLock.unlock();
					}
				}
				
				assert !_memory;
				
				_row = position;
				_buffer.rewind();
				_channel.read(_buffer, position * 22);
				_buffer.rewind();
				this.timestamp = _buffer.getLong();
				this.low = _buffer.getInt();
				this.to_open = _buffer.getShort();
				this.to_high = _buffer.getShort();
				this.to_close = _buffer.getShort();
				this.volume = _buffer.getInt();
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
				r.timestamp = this.timestamp;
				r.low = this.low;
				r.to_open = this.to_open;
				r.to_high = this.to_high;
				r.to_close = this.to_close;
				r.volume = this.volume;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public TimeBarsMDB getMDB() {
				return TimeBarsMDB.this;
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
		buffer.putLong(obj.timestamp); 
		buffer.putInt(obj.low); 
		buffer.putShort(obj.to_open); 
		buffer.putShort(obj.to_high); 
		buffer.putShort(obj.to_close); 
		buffer.putInt(obj.volume); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(RandomCursor obj, ByteBuffer buffer) {
		buffer.putLong(obj.timestamp); 
		buffer.putInt(obj.low); 
		buffer.putShort(obj.to_open); 
		buffer.putShort(obj.to_high); 
		buffer.putShort(obj.to_close); 
		buffer.putInt(obj.volume); 
	}
	
	/**
	 * <p>Write the field's values into the buffer in the same order they was declared.
	 * The virtual fields are ignored.
	 * </p> 
	 * @param obj The object to serialize into the buffer.
	 * @param buffer The buffer to fill.
	 */
	public static void writeBuffer(Record obj, ByteBuffer buffer) {
		buffer.putLong(obj.timestamp); 
		buffer.putInt(obj.low); 
		buffer.putShort(obj.to_open); 
		buffer.putShort(obj.to_high); 
		buffer.putShort(obj.to_close); 
		buffer.putInt(obj.volume); 
	}
	


	/**
	 * Replace the record at the given <code>index</code>.
	 *
	 * @param index The index to update.
	 * @param val_timestamp The value for column timestamp.
	 * @param val_low The value for column low.
	 * @param val_to_open The value for column to_open.
	 * @param val_to_high The value for column to_high.
	 * @param val_to_close The value for column to_close.
	 * @param val_volume The value for column volume.
	* @throws IOException If there is any I/O error.
	 */
	public void replace(long index 
							, long val_timestamp
							, int val_low
							, short val_to_open
							, short val_to_high
							, short val_to_close
							, int val_volume
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
						r.timestamp = val_timestamp;
						r.low = val_low;
						r.to_open = val_to_open;
						r.to_high = val_to_high;
						r.to_close = val_to_close;
						r.volume = val_volume;
					}
					return;
				} 				
			}
		
			_replaceBuffer.rewind();
			_replaceBuffer.putLong(val_timestamp);
			_replaceBuffer.putInt(val_low);
			_replaceBuffer.putShort(val_to_open);
			_replaceBuffer.putShort(val_to_high);
			_replaceBuffer.putShort(val_to_close);
			_replaceBuffer.putInt(val_volume);
			_replaceBuffer.rewind();
			appender();
			_appender._channel.write(_replaceBuffer, index * 22);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	
	/**
	 * Update the record at the given <code>index</code>.
	 * Also you can use {@link #replace(long , long, int, short, short, short, int)}.
	 *
	 * @param index The index to update.
	 * @param record Contains the data to set.
	 * @see #replace(long , long, int, short, short, short, int)
	 * @throws IOException If there is any I/O error.
	 */
	public void replace(long index, Record record) throws IOException {
		replace(index 
					, record.timestamp		
					, record.low		
					, record.to_open		
					, record.to_high		
					, record.to_close		
					, record.volume		
				);			
	}

	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "timestamp" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "timestamp".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_timestamp(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].timestamp = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_timestamp.rewind();
			_replaceBuffer_timestamp.putLong(value);
			_replaceBuffer_timestamp.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_timestamp, index * 22 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "low" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "low".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_low(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].low = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_low.rewind();
			_replaceBuffer_low.putInt(value);
			_replaceBuffer_low.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_low, index * 22 + 8);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "to_open" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "to_open".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_to_open(long index, short value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].to_open = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_to_open.rewind();
			_replaceBuffer_to_open.putShort(value);
			_replaceBuffer_to_open.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_to_open, index * 22 + 12);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "to_high" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "to_high".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_to_high(long index, short value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].to_high = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_to_high.rewind();
			_replaceBuffer_to_high.putShort(value);
			_replaceBuffer_to_high.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_to_high, index * 22 + 14);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "to_close" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "to_close".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_to_close(long index, short value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].to_close = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_to_close.rewind();
			_replaceBuffer_to_close.putShort(value);
			_replaceBuffer_to_close.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_to_close, index * 22 + 16);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "volume" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, short, short, short, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "volume".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_volume(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].volume = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_volume.rewind();
			_replaceBuffer_volume.putInt(value);
			_replaceBuffer_volume.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_volume, index * 22 + 18);
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
	 *			The lower value of <code>timestamp</code>.
	 * @param upper
	 *			The upper value of <code>timestamp</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_Timestamp_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTimestamp(randCursor, lower) - 1;

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
		
			if (cursor.timestamp > upper) {
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
	* Like {@link #select__where_Timestamp_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_Timestamp_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfTimestamp(randCursor, lower) - 1;
		long stop = Math.min(indexOfTimestamp(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_Timestamp_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.timestamp > upper) {
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
	* Column <code>timestamp</code> order validator.
	*/
	public static final IValidator<Record> TIMESTAMP_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.timestamp;
			long curValue = current.timestamp;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"timestamp(" + row1 + ")=" + prevValue + " > " + "timestamp(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
	/**
	 * <p>
	 * Record comparator for the column <code>timestamp</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class TimestampComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.timestamp < o2.timestamp? -1 : (o1.timestamp > o2.timestamp? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfTimestamp(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfTimestamp(Record[] data, long key) {
		return indexOfTimestamp(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the timestamp value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>timestamp</code> order specified in the column definition. 
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
	public static int indexOfTimestamp(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].timestamp;
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
	* Like {@link #indexOfTimestamp(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfTimestamp(RandomCursor cursor, long key) throws IOException {
		return indexOfTimestamp(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the timestamp value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>timestamp</code> order specified in the column definition. 
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
	public long indexOfTimestamp(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.timestamp == key ? 0 
							: (r.timestamp < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfTimestamp(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_timestamp;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 22 + 0);
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
	* Like {@link #indexOfTimestamp_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfTimestamp_exact(Record[] data, long key) {
		return indexOfTimestamp_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfTimestamp(Record[], long, int, int)} 
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
	public static int indexOfTimestamp_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].timestamp;
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
	* Like {@link #indexOfTimestamp(RandomCursor, long, long, long)} 
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
	public long indexOfTimestamp_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.timestamp == key ? 0 
							: (r.timestamp < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfTimestamp_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_timestamp;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 22 + 0);
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
	 * Like {@link #indexOfTimestamp_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfTimestamp_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfTimestamp_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given timestamp and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of timestamp.
	* @param timestamp Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncateTimestamp(RandomCursor randCursor, long timestamp) throws IOException {
		if (_size > 0) {
			long len = indexOfTimestamp(randCursor, timestamp);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.timestamp > timestamp) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>timestamp</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param timestamp The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_timestamp_is(RandomCursor cursor, long timestamp) throws IOException {
		if (_size > 0) {
			long i = indexOfTimestamp_exact(cursor, timestamp);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.timestamp == timestamp;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the timestamp values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countTimestamp(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfTimestamp(null, keyLower, 0L, high);
			long b = indexOfTimestamp(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfTimestamp(cursor, keyLower, 0L, high);
		long b = indexOfTimestamp(cursor, keyUpper, 0L, high);
		return b - a;
	}


	@SuppressWarnings("unchecked")
	@Override
	public dfsdbMDBSession getSession() {
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
		return _memory? 0 : _file.length() / 22;
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

			long newLen = len * 22;
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
	

