package com.mfg.dfs.cache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mfg.common.MaturityTest;
import com.mfg.common.RequestParamsTest;
import com.mfg.dfs.iqfeed.IqDateTest;
import com.mfg.dfs.misc.DfsRangeBarTest;
import com.mfg.dfs.misc.DfsTimeBarTest;
import com.mfg.dfs.serv.SingleDfsBarDbTest;

@RunWith(Suite.class)
@SuiteClasses({ MaturityTest.class, DfsRangeBarTest.class,
		DfsTimeBarTest.class, IqDateTest.class, SingleDfsBarDbTest.class,
		MfgMdbSessionTest.class, RequestParamsTest.class })
public class AllTests {
	//
}
