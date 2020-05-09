package com.mfg.dfs.misc;

import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.mfg.mdb.runtime.BackupVersion;
import org.mfg.mdb.runtime.IAppender;
import org.mfg.mdb.runtime.ICursor;
import org.mfg.mdb.runtime.IRandomCursor;
import org.mfg.mdb.runtime.IRecord;
import org.mfg.mdb.runtime.MDB;

import com.mfg.common.DFSException;
import com.mfg.dfs.cache.ICache;
import com.mfg.utils.U;

/**
 * This is the single equivalent of the class DoubleMDB. It is strange, but for
 * historical reasons the double class has been created <i>before</i> the single
 * class.
 * 
 * <p>
 * This because at the beginning it was apparently certain that we needed the
 * inverse push mechanism to fill the database but later the mechanism was
 * perfected and probably a normal db, with only "append to last" logic, is
 * sufficient.
 * 
 * @author Sergio
 * 
 * @param <DB>
 * 
 */
public abstract class DfsBarDb<DB extends MDB<IRecord>> implements
		ICache<DfsBar> {

	private BackupVersion _bv;

	// this stores the forward database
	protected final DB _db;

	protected IAppender<IRecord> _app;

	/**
	 * The cursor used to move in the cache.
	 */
	protected IRandomCursor<IRecord> _cursor;

	// this is the last key of the double database, it is the last key which has
	// been "pushed back" at last
	protected long _lastKey;

	// This is the first key of the double db, it is the last key of the reverse
	// database
	protected long _firstkey;

	/**
	 * Creates a double database, which is a database with two endings.
	 * 
	 * @param f1
	 * @param f2
	 * @param firstKey
	 *            the last timestamp which has been written to the file
	 * @param lastKey
	 *            the firs timestamp which has been written to the file
	 * @param buffer
	 * @throws IOException
	 */
	public DfsBarDb(DB f1) throws IOException {
		_db = f1;
		_cursor = f1.randomCursor();
		f1.getSession().defer(_cursor);
		_app = _db.appender();
		_updateFirstLastKeys();
	}

	protected final void _updateFirstLastKeys() throws IOException {
		if (size() == 0) {
			_lastKey = Long.MIN_VALUE;
			_firstkey = Long.MAX_VALUE;
		} else {
			_firstkey = ((IDfsDb) _db).firstKey();
			_lastKey = ((IDfsDb) _db).lastKey();
		}
	}

	/**
	 * Simple add method that will fail if the record is before.
	 * 
	 * 
	 */
	@Override
	public synchronized final boolean addLast(DfsBar aRec) throws DFSException {
		try {

			// _forwardApp.append(aRec); //THIS IS OK

			final long curRecKey = aRec.getPrimaryKey();
			if (curRecKey <= _lastKey) {
				return false; // nothing to do
			}
			_lastKey = curRecKey;

			if (_firstkey == Long.MAX_VALUE) {
				_firstkey = _lastKey;
			}
			this._app.append(aRec); // THIS IS OK

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new DFSException(e);
		}
	}

	@Override
	public synchronized void addLastForce(DfsBar aRec) throws DFSException {
		boolean res = addLast(aRec); // first I try to add it without altering
										// anything.
		if (!res) {
			// Ok, I have to force it
			long delta = _lastKey - aRec.getPrimaryKey() + 1;
			if (delta <= 0) {
				throw new DFSException(
						"This is really strange, delta not positive " + delta);
			}
			aRec.offsetPrimaryKey(delta);
			res = addLast(aRec);
			if (!res) {
				throw new DFSException("BAD! the second add must not fail");
			}
		}
	}

	@Override
	@Deprecated
	public synchronized void backup() throws DFSException {
		try {
			this._cursor.close();
			_bv = _db.getSession().backupFile(_db, "before");
			this._cursor = _db.randomCursor();
		} catch (IOException e) {
			e.printStackTrace();
			throw new DFSException(e);
		}

	}

	@Override
	public synchronized void clear() throws DFSException {
		try {
			_db.truncate(0);
			_lastKey = Long.MIN_VALUE;
			_firstkey = Long.MAX_VALUE;
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	public synchronized final void compact(boolean tryFlush) {
		try {
			_app.close();
			_cursor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void delete() throws DFSException {
		if (size() != 0) {
			throw new DFSException("I refuse to delete a not empty database");
		}
		File file = _cursor.getMDB().getFile();
		compact(true);
		boolean res = file.delete();
		if (!res) {
			throw new DFSException("cannot delete the file "
					+ file.getAbsolutePath());
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public synchronized final DfsBar get(int index) throws DFSException {
		try {
			if (index < 0 || index >= size()) {
				debug_var(381935, _cursor.getMDB().getFile().getAbsolutePath(),
						" asked ", index, " size is ", size());
				throw new ArrayIndexOutOfBoundsException();
			}
			_cursor.seek(index);
			return getBar(_cursor);
		} catch (ClosedByInterruptException ee) {
			/*
			 * This is a normal case because the system is being shutdown
			 */
			U.debug_var(295015, "cannot get the bar for ", this._db.getFile()
					.getAbsolutePath(), " it is closed, by interrupt");
			/*
			 * It is closed so I will reopen it afterwards.
			 */
			try {
				_cursor = _db.randomCursor();
				_db.getSession().defer(_cursor);
			} catch (IOException e1) {
				e1.printStackTrace();
				U.debug_var(198109, "cannot reopen the cursor ", e1);
			}

			throw new DFSException(ee);
		} catch (IOException e) {
			// this is a generic exception, please take note.
			throw new DFSException(e);
		}
	}

	/**
	 * builds a concrete bar to be used, this because this class is abstract.
	 * 
	 * @return a concrete bar (either a range bar or a time bar).
	 */
	protected abstract DfsBar getBar(
			@SuppressWarnings("rawtypes") ICursor aCursor);

	@Override
	public final synchronized int getCeilingIndexForTime(long aTime)
			throws DFSException {
		try {
			if (size() == 0) {
				// special case of size zero, ceiling not possible
				return -1;
			}

			long index = ((IDfsDb) _db).indexOfPrimaryKey(_cursor, aTime);

			/*
			 * time equal to the last
			 */
			if (index == size() - 1) {
				return (int) size();
			}

			/*
			 * this covers the case of the time equal to the first index which
			 * is also the last, because size is 1. And it covers also all cases
			 * where there is no next record.
			 */
			if (index == -_db.size() - 1) {
				return (int) -size();
			}

			if (index >= 0) {
				// Ok I have found a valid index, which is not the first, the
				// ceiling is one more, if there is one more
				return (int) index + 1;
			}

			if (index == -1) {
				// all elements are greater, so the ceiling is simply the first
				return 0;
			}

			// ok the key is not found, but the insertion point valid so I
			// simply return it
			return (int) ((index * (-1)) - 1);
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	public synchronized long getFirstKey() {
		return _firstkey;
	}

	@Override
	public synchronized final int getFloorIndexForTime(long aTime)
			throws DFSException {
		/*
		 * as the normal binary search will return (-(insertion point) - 1) in
		 * case of not finding the element, and as the insertion point is the
		 * first index of key greater than time, we can use the insertion point
		 * as the floor to be returned.
		 */
		try {

			if (size() == 0) {
				return -1;
			}

			long index = ((IDfsDb) _db).indexOfPrimaryKey(_cursor, aTime);

			if (index >= 0) {
				// Ok I have found a valid index, so this is by definition the
				// floor
				return (int) index;
			}

			if (index == -_db.size() - 1) {
				// after the last
				return (int) (_db.size() - 1);
			}

			if (index == -1) {
				return -1; // below the first
			}

			// ok the key is not found, but the insertion point valid so I
			// simply return it
			return (int) ((index * (-1)) - 2);
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	public synchronized final long getIndexOfRecAt(long time) {

		try {
			return ((IDfsDb) _db).indexOfPrimaryKey(_cursor, time);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return -1; // unreachable

	}

	@Override
	public synchronized long getLastKey() {
		return _lastKey;
	}

	@Override
	public synchronized long getNearestIndexOfRecAt(long time)
			throws DFSException {

		try {
			return ((IDfsDb) _db).indexOfPrimaryKey_lenient(_cursor, time);
		} catch (IOException e) {
			throw new DFSException(e);
		}

	}

	@Override
	public synchronized long getNearestIndexOfRecAtBounded(long time)
			throws DFSException {
		if (time < _firstkey || time > _lastKey) {
			throw new DFSException("time " + new Date(time) + " out of bounds");
		}
		return this.getNearestIndexOfRecAt(time);
	}

	@Override
	public synchronized final DfsBar getRecAt(long rowId) {
		try {
			long index = ((IDfsDb) _db).indexOfPrimaryKey(_cursor, rowId);
			if (index < 0) {
				return null;
			}
			_cursor.seek(index);
			return getBar(_cursor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null; // unreachable.
	}

	@Override
	public synchronized DfsBar getRecWithinRange(long time, long halfRange)
			throws DFSException {
		try {
			long index = ((IDfsDb) _db)
					.indexOfPrimaryKey_lenient(_cursor, time);
			if (index < 0) {
				return null;
			}
			DfsBar aBar = get((int) index);
			if (Math.abs(aBar.getPrimaryKey() - time) > halfRange) {
				return null;
			}
			return aBar;
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	@Deprecated
	public synchronized void restore() throws DFSException {

		/*
		 * to restore the database I have simply to copy the backup to the
		 * current file, overwriting, or, better, delete the current file and
		 * rename the old with the new
		 */

		try {

			if (_bv != null) {
				this._cursor.close();
				_db.getSession().restore(_bv);
				this._cursor = this._db.randomCursor();
				_updateFirstLastKeys();
			}

		} catch (IOException | TimeoutException e) {
			throw new DFSException(e);
		}

	}

	@Override
	public synchronized final long size() {
		try {
			return _db.size();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public synchronized void truncateAt(int index) throws DFSException {
		try {
			_db.truncate(index);
			// I must update the last key
			if (index != 0) {
				_lastKey = ((IDfsDb) _db).lastKey();
			} else {
				_lastKey = Long.MIN_VALUE;
				_firstkey = Long.MAX_VALUE;
			}
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

	@Override
	public synchronized void truncateFrom(long time) throws DFSException {
		try {
			IDfsDb revDb = (IDfsDb) _db;
			if (revDb.lastKey() < time) {
				return; // nothing to do.
			}
			long index = ((IDfsDb) _db)
					.indexOfPrimaryKey_lenient(_cursor, time);
			_db.truncate(index);
			// I must update the last key
			if (index != 0) {
				_lastKey = revDb.lastKey();
			} else {
				_lastKey = Long.MIN_VALUE;
				_firstkey = Long.MAX_VALUE;
			}
		} catch (IOException e) {
			throw new DFSException(e);
		}
	}

}
