package com.mfg.dfs.misc;

import java.io.IOException;

import org.mfg.mdb.runtime.ICursor;
import org.mfg.mdb.runtime.MDB;

import com.mfg.dfs.serv.RangeBarsMDB.RandomCursor;

/**
 * This is a simple concrete class to add RangeBars to the MDB database.
 * <p>This db is only used to forward append bars.
 * 
 * @author Sergio
 *
 */
@SuppressWarnings("rawtypes")
public class SingleRangeBarMDB extends DfsBarDb{

	@SuppressWarnings("unchecked")
	public SingleRangeBarMDB(MDB f1) throws IOException {
		super(f1);
	}

	@Override
	protected DfsBar getBar(ICursor aCursor) {
		return new DfsRangeBar((RandomCursor) aCursor);
	}

}
