package com.mfg.dfs.cache;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mfg.common.BarType;
import com.mfg.common.Maturity;

public class MfgMdbSessionTest {

	@SuppressWarnings("static-method")
	@Test
	public void testCacheKey(){
		
		Maturity mat = new Maturity(0);
		
		String key = MfgMdbSession.getCacheKey("ES #F", mat, BarType.MINUTE);
		assertEquals("ESXXF-1970-1Q-MINUTE", key);
		
		key = MfgMdbSession.getCacheKey("GB 1!-DT", mat,  BarType.RANGE);
		assertEquals("GBX1XXDT-1970-1Q-RANGE", key);
	}

}
