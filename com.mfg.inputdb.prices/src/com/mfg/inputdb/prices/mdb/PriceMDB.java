package com.mfg.inputdb.prices.mdb;

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

import com.mfg.common.QueueTick;

/**
 * <p>
 * This class provides the API to manipulate Price files. 
 * Here you will find the methods to modify and query the Price files. 
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
 * <h3>Price definition</h3>
 * <table border=1>
 *	<caption>Price</caption>
 *	<tr>
 *		<td>Column</td>
 *		<td>Type</td>
 *		<td>Order</td>
 *		<td>Virtual</td>
 *		<td>Formula</td>
 *	</tr>
 * <tr>
 *		<td>physicalTime</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>priceRaw</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>No</td>
 *		<td></td>
 *	</tr>
 * <tr>
 *		<td>price</td>
 *		<td>INTEGER</td>
 *		<td>NONE</td>
 *		<td>Yes</td>
 *		<td>Math.abs($$.priceRaw)</td>
 *	</tr>
 * <tr>
 *		<td>real</td>
 *		<td>BOOLEAN</td>
 *		<td>NONE</td>
 *		<td>Yes</td>
 *		<td>$$.priceRaw > 0</td>
 *	</tr>
 * <tr>
 *		<td>time</td>
 *		<td>LONG</td>
 *		<td>ASCENDING</td>
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
 * <h3>PriceMDB API</h3>
 * <p>
 * Now let's see the operations you can perform using this class on Price files:
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
 * PriceMDBSession session = ...;
 * PriceMDB mdb = session.connectTo_PriceMDB("price.mdb");
 * 
 * // request the appender.
 * PriceMDB.Appender app = mdb.appender(); 
 *
 * // set the appender values
 * app.physicalTime = ...;
 * app.priceRaw = ...;
 * app.time = ...;
 * app.volume = ...;
 *
 * // ignore the following virtual columns, they are computed automatically:
 * //
 * // app.price == "Math.abs($$.priceRaw)"
 * // app.real == "$$.priceRaw > 0"

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
 * PriceMDB mdb = ...;
 * long start = ...;
 * long stop = ...;
 *
 * // request a sequential cursor from start to stop
 * Price.Cursor cursor = mdb.cursor(start, stop);
 *
 * // iterate the records from start to stop
 * while (cursor.next()) {
 * 	// print the content of the current record
 * 	System.out.println("Read "  
 * 			+ cursor.physicalTime + " "
 * 			+ cursor.priceRaw + " "
 * 			+ cursor.price + " "
 * 			+ cursor.real + " "
 * 			+ cursor.time + " "
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
 * PriceMDB mdb = ...;
 *
 * // request a random cursor
 * PriceMDB.RandomCursor cursor = mdb.randomCursor();
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
 * PriceMDB mdb = ...;
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
 * PriceMDB mdb = ...;
 * // the index of the record you want to update/replace.
 * long index = ...;
 *
 * // the new values, remember virtual columns are ignored 	
 * long new_val_physicalTime = ...;
 * int new_val_priceRaw = ...;
 * long new_val_time = ...;
 * int new_val_volume = ...;
 *
 * mdb.replace(index 
 * 		, new_val_physicalTime
 * 		, new_val_priceRaw
 * 		, new_val_time
 * 		, new_val_volume
 *		);
 * </pre>
 * <p>
 * If you want to update just one column of the record, then you may use the following methods:
 * </p>
 * <ul>
 * <li>{@link PriceMDB#replace_physicalTime(long, long)}: To replace the physicalTime value.</li>
 * <li>{@link PriceMDB#replace_priceRaw(long, int)}: To replace the priceRaw value.</li>
 * <li>{@link PriceMDB#replace_time(long, long)}: To replace the time value.</li>
 * <li>{@link PriceMDB#replace_volume(long, int)}: To replace the volume value.</li>
 * </ul>
 *
 * <h3>List API</h3>
 * TODO: Documentation is comming
 *
 * @see PriceMDBSession#connectTo_PriceMDB(String)
 */

public final class PriceMDB
/* BEGIN MDB EXTENDS */
extends MDB<PriceMDB.Record>
/* END MDB EXTENDS */
{

/* BEGIN USER MDB */

	public long indexOfTime(long time) throws IOException {
		if (time < 0) {
			return 0;
		}
		long size = size();
		if (time >= size) {
			return size - 1;
		}
		return time;
	}

	/* END USER MDB */
	/**
	 * Price's meta-data: column names.
	 */
	public static final String[] COLUMNS_NAME = {
		"physicalTime",
		"priceRaw",
		"price",
		"real",
		"time",
		"volume",
	};
	
	/**
	 * Price's meta-data: column Java types.
	 */
	public static final Class<?>[] COLUMNS_TYPE = {
		long.class,
		int.class,
		int.class,
		boolean.class,
		long.class,
		int.class,
	};
	
	/**
	 * Price's meta-data: column Java types size (in bytes).
	 */
	public static final int[] COLUMNS_SIZE = { 
		8, 
		4, 
		4, 
		1, 
		8, 
		4, 
	};

	/**
	 * Price's meta-data: virtual column flags.
	 */
	public static final boolean[] COLUMNS_IS_VIRTUAL = { 
		false, 
		false, 
		true, 
		true, 
		false, 
		false, 
	};

	/**
	 * Price's meta-data: column byte-offset.
	 */
	public static final int[] COLUMN_OFFSET = {  
		0, 
		8, 
		12, 
		12, 
		12, 
		20, 
	};
	
	/**
	 * Price's meta-data: size of the record, in bytes.
	 */
	public static final int RECORD_SIZE = 24;
	
	/**
	* physicalTime's meta-data: index in a record.
	*/	
	public static final int COLUMN_PHYSICALTIME = 0;
	/**
	* priceRaw's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRICERAW = 1;
	/**
	* price's meta-data: index in a record.
	*/	
	public static final int COLUMN_PRICE = 2;
	/**
	* real's meta-data: index in a record.
	*/	
	public static final int COLUMN_REAL = 3;
	/**
	* time's meta-data: index in a record.
	*/	
	public static final int COLUMN_TIME = 4;
	/**
	* volume's meta-data: index in a record.
	*/	
	public static final int COLUMN_VOLUME = 5;

	/**
	 * Price's meta-data: UUID used in schemas.
	 */
	public static final String TABLE_ID = "e19d90be-b803-4fe5-9aa2-766011d2c560";
	
	/**
	 * Price's meta-data: signature used to check schema changes.
	 */ 
	public static final String TABLE_SIGNATURE = "ae19f703-7ef7-443b-aaa8-2132d36bc0be LONG; f6bcfd1a-4ee2-4bf4-a6e3-2d1a28087863 INTEGER; 4169855c-60c5-4925-a536-2a46aaaba76e LONG; b5e7118d-ccd1-4211-807e-110c210c62d7 INTEGER; ";


	private Appender _appender;
	private ByteBuffer _replaceBuffer; 
	private ByteBuffer _replaceBuffer_physicalTime;
	private ByteBuffer _replaceBuffer_priceRaw;
	private ByteBuffer _replaceBuffer_time;
	private ByteBuffer _replaceBuffer_volume;
	int _rbufSize;
	AtomicInteger _openCursorCount;
	long _rbufPos;
	Record[] _rbuf;
	long _size;
	final PriceMDBSession _session;

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
	public PriceMDB(PriceMDBSession session, File file, int bufferSize, SessionMode mode) throws IOException {
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
			_replaceBuffer = ByteBuffer.allocate(24);		
			_replaceBuffer_physicalTime = ByteBuffer.allocate(8);
			_replaceBuffer_priceRaw = ByteBuffer.allocate(4);
			_replaceBuffer_time = ByteBuffer.allocate(8);
			_replaceBuffer_volume = ByteBuffer.allocate(4);
		}
	}	

	/**
	* Price record structure.
	*/
	public static class Record 
/* BEGIN RECORD EXTENDS */
		implements IRecord
/* END RECORD EXTENDS */	{
		/**
		* Represents the physicalTime column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of physicalTime</caption>
		* <tr><td>Column</td><td>physicalTime</td></tr>
		* <tr><td>Type</td><td>LONG</td></tr>
		* <tr><td>Order</td><td>ASCENDING</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public long physicalTime; /* 0 */
		/**
		* Represents the priceRaw column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of priceRaw</caption>
		* <tr><td>Column</td><td>priceRaw</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>No</td></tr>
		* </table>
		*/
		public int priceRaw; /* 1 */
		/** Virtual field. Formula: <code>Math.abs($$.priceRaw)</code>*/		
		/**
		* Represents the price column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of price</caption>
		* <tr><td>Column</td><td>price</td></tr>
		* <tr><td>Type</td><td>INTEGER</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>Yes</td></tr>
		* <tr><td>Formula</td><td>Math.abs($$.priceRaw)</td></tr> 
		* </table>
		*/
		public int price; /* 2 */
		/** Virtual field. Formula: <code>$$.priceRaw > 0</code>*/		
		/**
		* Represents the real column.
		* <h2>Definition</h2>
		* <table border=1>
		* <caption>Definition of real</caption>
		* <tr><td>Column</td><td>real</td></tr>
		* <tr><td>Type</td><td>BOOLEAN</td></tr>
		* <tr><td>Order</td><td>NONE</td></tr>
		* <tr><td>Virtual</td><td>Yes</td></tr>
		* <tr><td>Formula</td><td>$$.priceRaw > 0</td></tr> 
		* </table>
		*/
		public boolean real; /* 3 */
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
		public long time; /* 4 */
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
			return "Price [ "
				 + "physicalTime=" + physicalTime + " "	
				 + "priceRaw=" + priceRaw + " "	
				 + "price=" + price + " "	
				 + "real=" + real + " "	
				 + "time=" + time + " "	
				 + "volume=" + volume + " "	
				 + " ]";
		}

	
		/**
		* An array of the record values.
		*/
		@Override
		public Object[] toArray() {
			return new Object[] {
							Long.valueOf(physicalTime),
							Integer.valueOf(priceRaw),
							Integer.valueOf(price),
							Boolean.valueOf(real),
							Long.valueOf(time),
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
				case 0: return physicalTime;
				case 1: return priceRaw;
				case 2: return price;
				case 3: return real;
				case 4: return time;
				case 5: return volume;
				default: throw new IndexOutOfBoundsException("Wrong column index " + columnIndex);			 
			}
		}
		
		/**
		* Update the record with the given record's values. In case of arrays the content is copied too. 
		* @param record The record to update.
		*/ 
		public void update(Record record) {
			this.physicalTime = record.physicalTime;
			this.priceRaw = record.priceRaw;
			this.price = record.price;
			this.real = record.real;
			this.time = record.time;
			this.volume = record.volume;
		}

/* BEGIN USER RECORD */
		/**
		 * This method should be used to update the record following the virtual
		 * rules.
		 */
		public void update(long time1, long physicalTime1, int price1, boolean real1,
				int volume1) {
			this.time = time1;
			this.physicalTime = physicalTime1;
			this.priceRaw = (real1 ? 1 : -1) * price1;
			this.volume = volume1;
		}

		public void copyTo(com.mfg.common.QueueTick qt) {
			qt.set((int) this.time, this.physicalTime, this.price, this.real);
			qt.setVolume(this.volume);
		}

		public void update(QueueTick tick) {
			this.update(tick.getFakeTime(), tick.getPhysicalTime(), tick.getPrice(),
					tick.getReal(), tick.getVolume());
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
	* 	ap.physicalTime = getPhysicalTime();	
	* 	ap.priceRaw = getPriceRaw();	
	* 	ap.time = getTime();	
	* 	ap.volume = getVolume();	
	* 	ap.append();
	* }
	* ap.close();
	* </pre>
	* <p>
	* Note the virtual columns was ignored, this is because those values 
	* are computed by a formula in the "append" method.
	* </p>
	*/
	public final class Appender implements IAppender<Record> {
		protected RandomAccessFile _raf;
		FileChannel _channel;
		protected PriceMDB _mdb;	
		protected ByteBuffer _buf;	 
		public long physicalTime; /* 0 */
		public int priceRaw; /* 1 */
		/** 
		 * Virtual field. Formula: <code>Math.abs($$.priceRaw)</code>
		 */		
		public int price; /* 2 */
		/** 
		 * Virtual field. Formula: <code>$$.priceRaw > 0</code>
		 */		
		public boolean real; /* 3 */
		public long time; /* 4 */
		public int volume; /* 5 */
		
		/**
		* The constructor.
		*/
		Appender() throws IOException {
			_mdb = PriceMDB.this;
			if (!_memory) {
				_buf = ByteBuffer.allocate(_bufferSize * 24);
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

					/* Virtual fields */
					this.price = Math.abs(this.priceRaw);
					this.real = this.priceRaw > 0;
				
					_buf.putLong(this.physicalTime);
					_buf.putInt(this.priceRaw);
					_buf.putLong(this.time);
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
					r.physicalTime = this.physicalTime;
					r.priceRaw = this.priceRaw;
					r.time = this.time;
					r.volume = this.volume;
					/* Virtual fields */
					r.price = this.price = Math.abs(r.priceRaw);
					r.real = this.real = r.priceRaw > 0;
				
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
				this.physicalTime = record.physicalTime;
				this.priceRaw = record.priceRaw;
				this.time = record.time;
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
				r.physicalTime = record.physicalTime;
				r.priceRaw = record.priceRaw;
				r.time = record.time;
				r.volume = record.volume;
				/* Virtual fields */
				r.price = this.price = Math.abs(r.priceRaw);
				r.real = this.real = r.priceRaw > 0;
				
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
				this.physicalTime = record.physicalTime;
				this.priceRaw = record.priceRaw;
				this.time = record.time;
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
				/* Virtual fields */
				record.price = this.price = Math.abs(record.priceRaw);
				record.real = this.real = record.priceRaw > 0;
				
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
				_buf.putLong(r.physicalTime);
				_buf.putInt(r.priceRaw);
				_buf.putLong(r.time);
				_buf.putInt(r.volume);
			}
			_buf.rewind();
			_buf.limit(_rbufSize * 24);
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
		public PriceMDB getMDB() {
			return _mdb;
		}
		
		/**
		* Create a record with the appender's values.
		*/
		@Override
		public Record toRecord() {
			Record r = new Record();
			r.physicalTime = this.physicalTime;
			r.priceRaw = this.priceRaw;
			r.price = this.price;
			r.real = this.real;
			r.time = this.time;
			r.volume = this.volume;
			return r;
		}
		
		/**
		* Update the appender's values with the values of the given record.
		* @param record The record to update.
		*/
		public void update(Record record) {
			this.physicalTime = record.physicalTime;
			this.priceRaw = record.priceRaw;
			this.price = record.price;
			this.real = record.real;
			this.time = record.time;
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
	 * PriceMDB mdb = ...;
	 * Cursor c = mdb.cursor(...); 
	 * while(c.next()) {
	 *     doSomething(c.physicalTime); 
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
		public long physicalTime; /* 0 */
		public int priceRaw; /* 1 */
		/** Virtual field. Formula: <code>Math.abs($$.priceRaw)</code>*/		
		public int price; /* 2 */
		/** Virtual field. Formula: <code>$$.priceRaw > 0</code>*/		
		public boolean real; /* 3 */
		public long time; /* 4 */
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
				_channel.position(start * 24);
				_buffer = ByteBuffer.allocate(bufferSize * 24);
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
					_channel.position(start * 24);
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
						this.physicalTime = r.physicalTime;
						this.priceRaw = r.priceRaw;
						this.price = r.price;
						this.real = r.real;
						this.time = r.time;
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
				
				this.physicalTime = _buffer.getLong();
				this.priceRaw = _buffer.getInt();
				this.time = _buffer.getLong();
				this.volume = _buffer.getInt();
				/* Virtual fields */
					this.price = Math.abs(this.priceRaw);
					this.real = this.priceRaw > 0;
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
			r.physicalTime = this.physicalTime;
			r.priceRaw = this.priceRaw;
			r.price = this.price;
			r.real = this.real;
			r.time = this.time;
			r.volume = this.volume;
			return r;
		}
		
		/**
		* The associated MDB instance. 
		*/
		@Override
		public PriceMDB getMDB() {
			return PriceMDB.this;
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
		* PriceMDB mdb = ...;
		* RandomCursor c = mdb.randomCursor(); 
		* ...
		* c.seek(somePosition);
		* ...
		* doSomething(c.physicalTime); 
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
			ByteBuffer _buffer_physicalTime; // used by index-of-physicalTime method.
			ByteBuffer _buffer_time; // used by index-of-time method.
			private RandomAccessFile _raf;
			FileChannel _channel;
			private long _row;
			private boolean _open;
			public long physicalTime; /* 0 */
			public int priceRaw; /* 1 */
			/** Virtual field. Formula: <code>Math.abs($$.priceRaw)</code>*/		
			public int price; /* 2 */
			/** Virtual field. Formula: <code>$$.priceRaw > 0</code>*/		
			public boolean real; /* 3 */
			public long time; /* 4 */
			public int volume; /* 5 */

			RandomCursor() throws IOException {
				_open = true;
			    _openCursorCount.incrementAndGet();    
				_row = -1;
				if (!_memory) {
					_raf = new RandomAccessFile(getFile(), "r");
					_channel = _raf.getChannel();
					_buffer = ByteBuffer.allocate(24);
					_buffer_physicalTime = ByteBuffer.allocate(8);
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
							this.physicalTime = r.physicalTime;
							this.priceRaw = r.priceRaw;
							this.price = r.price;
							this.real = r.real;
							this.time = r.time;
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
				_channel.read(_buffer, position * 24);
				_buffer.rewind();
				this.physicalTime = _buffer.getLong();
				this.priceRaw = _buffer.getInt();
				this.time = _buffer.getLong();
				this.volume = _buffer.getInt();
					/* Virtual fields */
				this.price = Math.abs(this.priceRaw);
				this.real = this.priceRaw > 0;
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
				r.physicalTime = this.physicalTime;
				r.priceRaw = this.priceRaw;
				r.price = this.price;
				r.real = this.real;
				r.time = this.time;
				r.volume = this.volume;
				return r;
			}
			
			/**
			* The associated MDB instance.
			*/
			@Override
			public PriceMDB getMDB() {
				return PriceMDB.this;
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
		buffer.putLong(obj.physicalTime); 
		buffer.putInt(obj.priceRaw); 
		buffer.putLong(obj.time); 
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
		buffer.putLong(obj.physicalTime); 
		buffer.putInt(obj.priceRaw); 
		buffer.putLong(obj.time); 
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
		buffer.putLong(obj.physicalTime); 
		buffer.putInt(obj.priceRaw); 
		buffer.putLong(obj.time); 
		buffer.putInt(obj.volume); 
	}
	


	/**
	 * Replace the record at the given <code>index</code>.
	 *
	 * @param index The index to update.
	 * @param val_physicalTime The value for column physicalTime.
	 * @param val_priceRaw The value for column priceRaw.
	 * @param val_time The value for column time.
	 * @param val_volume The value for column volume.
	* @throws IOException If there is any I/O error.
	 */
	public void replace(long index 
							, long val_physicalTime
							, int val_priceRaw
							, long val_time
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
						r.physicalTime = val_physicalTime;
						r.priceRaw = val_priceRaw;
						r.time = val_time;
						r.volume = val_volume;
						// virtual fields
						r.price = Math.abs(val_priceRaw); 
						r.real = val_priceRaw > 0; 
					}
					return;
				} 				
			}
		
			_replaceBuffer.rewind();
			_replaceBuffer.putLong(val_physicalTime);
			_replaceBuffer.putInt(val_priceRaw);
			_replaceBuffer.putLong(val_time);
			_replaceBuffer.putInt(val_volume);
			_replaceBuffer.rewind();
			appender();
			_appender._channel.write(_replaceBuffer, index * 24);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	
	/**
	 * Update the record at the given <code>index</code>.
	 * Also you can use {@link #replace(long , long, int, long, int)}.
	 *
	 * @param index The index to update.
	 * @param record Contains the data to set.
	 * @see #replace(long , long, int, long, int)
	 * @throws IOException If there is any I/O error.
	 */
	public void replace(long index, Record record) throws IOException {
		replace(index 
					, record.physicalTime		
					, record.priceRaw		
					, record.time		
					, record.volume		
				);			
	}

	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "physicalTime" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, long, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "physicalTime".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_physicalTime(long index, long value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].physicalTime = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_physicalTime.rewind();
			_replaceBuffer_physicalTime.putLong(value);
			_replaceBuffer_physicalTime.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_physicalTime, index * 24 + 0);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "priceRaw" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, long, int)},
	 * but if you want to update just one column, use this method because it is faster.
	 * 
	 * @param index The index of the record to update.
	 * @param value The new value to set to the column "priceRaw".
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public void replace_priceRaw(long index, int value) throws IOException {
		if (!_basic) _writeLock.lock();
		try {
			if (!_basic) {
				if (index >= _rbufPos) {
					int pos = (int) (index - _rbufPos);
					if (pos < _rbufSize) {
						_rbuf[pos].priceRaw = value;
					}
					return;
				} 				
			}
		
			_replaceBuffer_priceRaw.rewind();
			_replaceBuffer_priceRaw.putInt(value);
			_replaceBuffer_priceRaw.rewind();
			appender();
			_appender._channel.write(_replaceBuffer_priceRaw, index * 24 + 8);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "time" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, long, int)},
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
			_appender._channel.write(_replaceBuffer_time, index * 24 + 12);
		} finally {
			if (!_basic) _writeLock.unlock();
		}		
	}
	/**
	 * Update the record at the given <code>index</code>, 
	 * but only the column "volume" is updated. If you like to 
	 * update many fields of the record then use {@link #replace(long , long, int, long, int)},
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
			_appender._channel.write(_replaceBuffer_volume, index * 24 + 20);
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
	 *			The lower value of <code>physicalTime</code>.
	 * @param upper
	 *			The upper value of <code>physicalTime</code>
	 * @return The data between the lower and upper values.
	 * @throws IOException If there is any I/O error.
	 */	
	public Record[] select__where_PhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper) throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfPhysicalTime(randCursor, lower) - 1;

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
		
			if (cursor.physicalTime > upper) {
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
	* Like {@link #select__where_PhysicalTime_in(RandomCursor, Cursor, long, long)} but it uses a sparse cursor.
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
	public Record[] select_sparse__where_PhysicalTime_in(RandomCursor randCursor, Cursor cursor, long lower, long upper, int maxLen)
			throws IOException {
		if (_size == 0) return NO_DATA;
		
		long start = indexOfPhysicalTime(randCursor, lower) - 1;
		long stop = Math.min(indexOfPhysicalTime(randCursor, upper) + 1, _size - 1);
		
		if (start < 0) {
			start = 0;
		}
		
		long step = (stop - start) / maxLen;
		
		if (step < 2) {
			return select__where_PhysicalTime_in(randCursor, cursor, lower, upper);
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
		
			if (randCursor.physicalTime > upper) {
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
	* Column <code>physicalTime</code> order validator.
	*/
	public static final IValidator<Record> PHYSICALTIME_ASCENDING_VALIDATOR = new IValidator<Record>() {
		@Override
		public boolean validate(ValidationArgs<Record> args, IValidatorListener<Record> listener) {
			Record prev = args.getPrev();
			Record current = args.getCurrent();
			long prevValue = prev.physicalTime;
			long curValue = current.physicalTime;
			boolean valid = prevValue <= curValue; 
			if (!valid) {
				long row1 = args.getRow() - 1;
				long row2 = args.getRow();
				listener.errorReported(new ValidatorError<>(args, 						
						"physicalTime(" + row1 + ")=" + prevValue + " > " + "physicalTime(" + row2 + ")=" + curValue + ""));
			}
			return valid;
		}
	};
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
	 * Record comparator for the column <code>physicalTime</code>. 
	 * This comparator takes in consideration the order of the column definition.
	 * </p>
	 */
	public static class PhysicalTimeComparator implements java.util.Comparator<Record> {
		
		@Override
		public int compare(Record o1, Record o2) {
			return o1.physicalTime < o2.physicalTime? -1 : (o1.physicalTime > o2.physicalTime? 1 : 0);
		}
	}
	
	/**
	* Like {@link #indexOfPhysicalTime(Record[], long, int, int)}, but searches in the whole array.
	*
	* @param data
	*			The array of data.
	* @param key
	*			The value to search.
	* @return The index of the value.	
	*/
	public static int indexOfPhysicalTime(Record[] data, long key) {
		return indexOfPhysicalTime(data, key, 0, data.length);
	}

	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the physicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>physicalTime</code> order specified in the column definition. 
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
	public static int indexOfPhysicalTime(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high1 = high;
    	
		while (low1 <= high1) {
		    int mid = (low1 + high1) >>> 1;
		    long midVal = data[mid].physicalTime;
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
	* Like {@link #indexOfPhysicalTime(RandomCursor, long, long, long)} but searches on the whole file.
	*
	* @param cursor
	*			Random cursor used to search the value.
	* @param key
	*			The value to search.
	* @return The index of the value.
	*
	* @throws IOException If there is any I/O error.
	*/
	public long indexOfPhysicalTime(RandomCursor cursor, long key) throws IOException {
		return indexOfPhysicalTime(cursor, key, 0, _size - 1);
	}
	
	/**
	* <p>
	* From position <code>low</code> to <code>high</code>, this method finds the index 
	* of the record with the physicalTime value closer to the given <code>key</code>. 
	* This method uses a binary search algorithm (like <code>Arrays.binarySearch(...)</code>), so it assumes all the records 
	* are ordered by the <code>physicalTime</code> order specified in the column definition. 
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
	public long indexOfPhysicalTime(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (!_basic) _readLock.lock();
		try {
			if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
			
			long low1 = low;
			
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.physicalTime == key ? 0 
							: (r.physicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						return _rbufPos + indexOfPhysicalTime(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
					}
				}
			}
		
			assert !_memory;		
				
			/* search in file */
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_physicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				channel.position(mid * 24 + 0);
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
	* Like {@link #indexOfPhysicalTime_exact(Record[], long, int, int)}, 
	* but searches on the whole array.
	* @param data The array of data.
	* @param key The value to search in the array.
	* @return The index of <code>key</code>, or a number &lt; 0 if the key is not found.
	*/
	public static int indexOfPhysicalTime_exact(Record[] data, long key) {
		return indexOfPhysicalTime_exact(data, key, 0, data.length);
	}

	/**
	* Like {@link #indexOfPhysicalTime(Record[], long, int, int)} 
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
	public static int indexOfPhysicalTime_exact(Record[] data, long key, int low, int high) {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + data.length);
		
    	int low1 = low;
    	int high2 = high;
		while (low1 <= high2) {
		    int mid = (low1 + high2) >>> 1;
		    long midVal = data[mid].physicalTime;
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
	* Like {@link #indexOfPhysicalTime(RandomCursor, long, long, long)} 
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
	public long indexOfPhysicalTime_exact(RandomCursor cursor, long key, long low, long high) throws IOException {
		if (low < 0) throw new IndexOutOfBoundsException("Index: " + low + ", Size: " + _size);
		
		long low1 = low;
		if (!_basic) _readLock.lock();
		try {
			if (!_basic) {
				if (low1 >= _rbufPos || high >= _rbufPos) {
					Record r = _rbuf[0];
					if (_memory || (r.physicalTime == key ? 0 
							: (r.physicalTime < key ? -1 : 1)) <= 0) {
						/* search in memory */
						long index = indexOfPhysicalTime_exact(_rbuf, key, 0, (int) Math.min(high - _rbufPos, _rbufSize - 1));
						return index < 0? -_rbufPos + index /* key no found */ : _rbufPos + index;
					}
				}
			}
		
			assert !_memory;
			
			/* search in file */
	
			long high2 = high >= _rbufPos ? _rbufPos - 1 : high;
		
			FileChannel channel = cursor._channel;
			ByteBuffer buffer = cursor._buffer_physicalTime;
				
			while (low1 <= high2) {
				final long mid = (low1 + high2) >>> 1;
				
				channel.position(mid * 24 + 0);
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
	 * Like {@link #indexOfPhysicalTime_exact(RandomCursor, long, long, long)}, but searches on the whole file.
	 *
	 * @param cursor The random cursor used to find the value.
	 * @param key The value to search.
	 * @return The index of the value. 
	 * @throws IOException If there is any I/O error.
	 */
	public long indexOfPhysicalTime_exact(RandomCursor cursor, long key) throws IOException {
		return indexOfPhysicalTime_exact(cursor, key, 0, _size - 1);
	}
	
	/**
	* Search the index of the given physicalTime and then truncate the database in that position.
	*
	* @param randCursor The cursor used to find the position of physicalTime.
	* @param physicalTime Truncate the file in the index of this value.
	* @throws IOException If there is any I/O error.
	*/
	public void truncatePhysicalTime(RandomCursor randCursor, long physicalTime) throws IOException {
		if (_size > 0) {
			long len = indexOfPhysicalTime(randCursor, physicalTime);
			if (len > 0) {
				len--;
			}
			while (len < _size) {
				randCursor.seek(len);
				if (randCursor.physicalTime > physicalTime) {
					break;
				}
				len++;
			}
			truncate(len);
		}
	}
	
	/**
	* Find the record with <code>physicalTime</code> equals to the given value.
	* 
	* @param cursor The random cursor used to find the indexes.
	* @param physicalTime The value to find.
	*
	* @return The found record or <code>null</code> if there is not any record with that value.
	* @throws IOException If there is any I/O error.
	*/
	public Record findRecord_where_physicalTime_is(RandomCursor cursor, long physicalTime) throws IOException {
		if (_size > 0) {
			long i = indexOfPhysicalTime_exact(cursor, physicalTime);
			if (i < 0) {
				return null;
			}
			cursor.seek(i);
			Record r = cursor.toRecord();
			assert r.physicalTime == physicalTime;
			return r;
		}
		return null;
	}
	
	/**
	 * Count the number of records between the physicalTime values <code>keyLower</code> and <code>keyUpper</code>.
	 * If there are not records with those values, then it returns an approximation.
	 *
	 * @param cursor The random cursor used to find the indexes.
	 * @param keyLower The value to start counting.
	 * @param keyUpper The value to stop counting.
	 * @return The number of records between the given values.
	 *
	 * @throws IOException If there is any I/O error.
	 */
	public long countPhysicalTime(RandomCursor cursor, long keyLower, long keyUpper) throws IOException {
		if (_memory) {
			long high = _size - 1;
			long a = indexOfPhysicalTime(null, keyLower, 0L, high);
			long b = indexOfPhysicalTime(null, keyUpper, 0L, high);
			return b - a;
		}
		
		long high = _size - 1;
		long a = indexOfPhysicalTime(cursor, keyLower, 0L, high);
		long b = indexOfPhysicalTime(cursor, keyUpper, 0L, high);
		return b - a;
	}
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
				channel.position(mid * 24 + 12);
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
				
				channel.position(mid * 24 + 12);
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
	public PriceMDBSession getSession() {
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
		return _memory? 0 : _file.length() / 24;
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

			long newLen = len * 24;
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
	

