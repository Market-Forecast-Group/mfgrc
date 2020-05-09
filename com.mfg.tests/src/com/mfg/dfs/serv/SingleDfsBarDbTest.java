package com.mfg.dfs.serv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfg.common.DFSException;
import com.mfg.dfs.misc.DfsBarDb;
import com.mfg.dfs.misc.DfsRangeBar;
import com.mfg.dfs.misc.SingleRangeBarMDB;
import com.mfg.dfs.serv.RangeBarsMDB.Record;

public class SingleDfsBarDbTest {

	@AfterClass
	public static void afterClass() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		File root = new File(tmpDir, "OOOO");

		File f1 = new File(root, "f1");
		assertTrue("cannot delete " + f1.getAbsolutePath(), f1.delete());
	}

	@BeforeClass
	public static void beforeClass() throws IOException, DFSException,
			TimeoutException {
		@SuppressWarnings("rawtypes")
		DfsBarDb d2Db;

		String tmpDir = System.getProperty("java.io.tmpdir");
		// System.out.println("Creating the session in dir " + tmpDir);
		File root = new File(tmpDir, "OOOO");
		dfsdbMDBSession ses = new dfsdbMDBSession("test", root);

		ses.setDebug(true);

		RangeBarsMDB f1 = ses.connectTo_RangeBarsMDB("f1");

		d2Db = new SingleRangeBarMDB(f1);

		assertEquals(-1, d2Db.getNearestIndexOfRecAt(0));

		// the ceiling and the floor of an empty db are not existent
		int ceiling = d2Db.getCeilingIndexForTime(10);
		assertEquals(-1, ceiling);

		int floor = d2Db.getFloorIndexForTime(10);
		assertEquals(-1, floor);

		// Ok let's try some methods
		DfsRangeBar aRec = new DfsRangeBar();
		aRec.timeStamp = 11;
		aRec.open = 33;
		aRec.type = -1;

		for (int i = 0; i < 1000; ++i) {
			d2Db.addLast(aRec);
			aRec.timeStamp += 3;
		}

		// I try to add three records with the same timestamp.
		aRec.timeStamp = 4000;
		boolean res;
		res = d2Db.addLast(aRec);
		assertTrue(res);

		res = d2Db.addLast(aRec);
		assertFalse(res);

		res = d2Db.addLast(aRec);
		assertFalse(res);

		d2Db.addLastForce(aRec);
		assertEquals(4001, aRec.timeStamp);
		d2Db.addLastForce(aRec);
		assertEquals(4002, aRec.timeStamp);

		// I have to add a different record to force the flushing of
		// the buffer with the same times.
		d2Db.compact(false);
		// if (d2Db._cursor != null) {
		// d2Db._cursor.close();
		// }
		ses.close();
	}

	@SuppressWarnings({ "static-method", "deprecation" })
	@Test
	public void test() throws IOException, DFSException, TimeoutException {
		@SuppressWarnings("rawtypes")
		DfsBarDb d2Db;

		String tmpDir = System.getProperty("java.io.tmpdir");
		// System.out.println("Creating the session in dir " + tmpDir);
		File root = new File(tmpDir, "OOOO");
		dfsdbMDBSession ses = new dfsdbMDBSession("test", root);

		RangeBarsMDB f1 = ses.connectTo_RangeBarsMDB("f1");

		d2Db = new SingleRangeBarMDB(f1);

		assertEquals(1003, d2Db.size());

		// assertEquals(-503, d2Db.lowerAbsIndex());
		assertEquals(1002, d2Db.size() - 1);

		// floor test equal to the maximum
		int floor = d2Db.getFloorIndexForTime(4002);
		assertEquals(1002, floor);
		// this will trigger the exception
		boolean ok = false;
		floor = d2Db.getFloorIndexForTime(4003);
		assertEquals(1002, floor);

		floor = d2Db.getFloorIndexForTime(333003);
		assertEquals(1002, floor); // always the maximum

		floor = d2Db.getFloorIndexForTime(-1);
		assertEquals(-1, floor);

		floor = d2Db.getFloorIndexForTime(11);
		assertEquals(0, floor);
		// I change record
		floor = d2Db.getFloorIndexForTime(12);
		assertEquals(0, floor);

		// //////////////////////////////////////////////////////////////////
		// ceiling

		// ceiling with the highest record
		int ceiling = d2Db.getCeilingIndexForTime(4002);
		assertEquals(1003, ceiling);

		// ceiling with the first
		ceiling = d2Db.getCeilingIndexForTime(11);
		assertEquals(1, ceiling);

		// over the top
		ceiling = d2Db.getCeilingIndexForTime(4003);
		assertEquals(-1003, ceiling);

		// below the bottom
		ceiling = d2Db.getCeilingIndexForTime(10);
		assertEquals(0, ceiling);

		// in between
		ceiling = d2Db.getCeilingIndexForTime(12);
		assertEquals(1, ceiling);

		// equal to the second
		ceiling = d2Db.getCeilingIndexForTime(14);
		assertEquals(2, ceiling);

		// before the last
		ceiling = d2Db.getCeilingIndexForTime(4001);
		assertEquals(1002, ceiling);

		// ////////////////////////////////////////////////////////////////

		// absolute tests
		Record aRecord;
		aRecord = (Record) d2Db.get(0);
		assertEquals(11, aRecord.timeStamp);

		aRecord = (Record) d2Db.getRecAt(3005);
		assertEquals(3005, aRecord.timeStamp);

		// /////////////////////////////////////////////
		// test search on the limit.
		// /////////////////////////////////////////////

		aRecord = (Record) d2Db.getRecAt(4002);
		assertEquals(4002, aRecord.timeStamp);

		aRecord = (Record) d2Db.getRecAt(4003);
		assertNull(aRecord); // This means that we have not a record with time
								// 4003,
		// which is exact. Because we want an exact match.

		aRecord = (Record) d2Db.get((int) d2Db.getNearestIndexOfRecAt(4003));
		assertEquals(4002, aRecord.timeStamp);

		// out of range search
		aRecord = (Record) d2Db.get((int) d2Db.getNearestIndexOfRecAt(9999));
		assertEquals(4002, aRecord.timeStamp);

		aRecord = (Record) d2Db.get((int) d2Db.getNearestIndexOfRecAt(-9999));
		assertEquals(11, aRecord.timeStamp);

		try {
			aRecord = (Record) d2Db.get((int) d2Db
					.getNearestIndexOfRecAtBounded(9999));
		} catch (DFSException e) {
			ok = true;
		}
		assertTrue(ok);

		ok = false;
		try {
			aRecord = (Record) d2Db.get((int) d2Db
					.getNearestIndexOfRecAtBounded(-9999));
		} catch (DFSException e) {
			ok = true;
		}
		assertTrue(ok);
		// ////////////////////////

		aRecord = (Record) d2Db.getRecWithinRange(4005, 5);
		assertEquals(4002, aRecord.timeStamp);

		aRecord = (Record) d2Db.getRecWithinRange(4010, 5);
		assertNull(aRecord);

		aRecord = (Record) d2Db.getRecWithinRange(10, 3);
		assertEquals(11, aRecord.timeStamp);

		aRecord = (Record) d2Db.getRecWithinRange(7, 3);
		assertNull(aRecord);

		aRecord = (Record) d2Db.getRecWithinRange(11, 0);
		assertEquals(11, aRecord.timeStamp);

		// /////////////////////////////////////////
		// end limit search

		aRecord = (Record) d2Db.getRecAt(11);
		assertEquals(11, aRecord.timeStamp);

		long index = d2Db.getIndexOfRecAt(11);
		assertEquals(0, index);

		index = d2Db.getNearestIndexOfRecAt(3999);
		assertEquals(999, index);

		aRecord = (Record) d2Db.get(999);
		assertEquals(3008, aRecord.timeStamp);

		index = d2Db.getNearestIndexOfRecAt(4002);
		assertEquals(1002, index);

		aRecord = (Record) d2Db.get(1002);
		assertEquals(4002, aRecord.timeStamp);

		// Now I try to add a record before the timestamp
		DfsRangeBar aRec = new DfsRangeBar();
		aRec.timeStamp = 11;
		aRec.open = 33;
		aRec.type = -1;
		assertFalse(d2Db.addLast(aRec));

		// this truncate will be a no-op, because thetime is greater!
		long oldSize = d2Db.size();
		d2Db.truncateFrom(83984948);
		long newSize = d2Db.size();
		assertEquals(oldSize, newSize);

		// OK, now I try to truncate it from a certain time
		d2Db.truncateFrom(4000);
		assertEquals(3008, d2Db.getLastKey());
		assertFalse(d2Db.addLast(aRec));
		assertEquals(1000, d2Db.size());

		aRecord = (Record) d2Db.get(1);
		assertEquals(14, aRecord.timeStamp);

		d2Db.backup();

		// NOW IT FAILS...
		aRecord = (Record) d2Db.get(1);
		assertEquals(14, aRecord.timeStamp);

		// /Truncate a certain index
		d2Db.truncateAt(5);
		assertEquals(5, d2Db.size());
		assertEquals(23, d2Db.getLastKey());

		d2Db.restore();
		assertEquals(3008, d2Db.getLastKey());
		assertEquals(1000, d2Db.size());

		d2Db.truncateFrom(-13);
		assertEquals(0, d2Db.size());

		assertEquals(Long.MIN_VALUE, d2Db.getLastKey());
		assertEquals(Long.MAX_VALUE, d2Db.getFirstKey());

		// Ok, now I truncate it
		d2Db.clear();
		assertTrue(d2Db.addLast(aRec));

		d2Db.compact(false);
		ses.close();
	}

}
