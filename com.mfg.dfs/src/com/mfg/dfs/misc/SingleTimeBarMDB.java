package com.mfg.dfs.misc;

import java.io.IOException;

import org.mfg.mdb.runtime.ICursor;
import org.mfg.mdb.runtime.MDB;

import com.mfg.dfs.serv.TimeBarsMDB.RandomCursor;

@SuppressWarnings("rawtypes")
public class SingleTimeBarMDB extends DfsBarDb {

	@SuppressWarnings("unchecked")
	public SingleTimeBarMDB(MDB f1) throws IOException {
		super(f1);
	}

	@Override
	protected DfsBar getBar(ICursor aCursor) {
		return new DfsTimeBar((RandomCursor)aCursor);
	}

}
