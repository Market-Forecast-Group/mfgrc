package com.mfg.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UTest {

	@SuppressWarnings({ "static-method", "boxing" })
	@Test
	public void testJoin() {
		String res = U.join("a","b","ccccc");
		assertEquals("a,b,ccccc", res);
		res = U.join("a","b","cc,ccc", 99);
		assertEquals("a,b,cc,ccc,99", res);
		
		res = U.join();
		assertEquals("", res);
		
		res = U.join(399.999, 99, "cip");
		assertEquals("399.999,99,cip", res);
		
		res = U.join( 0.3823823783929393917484275928, -979, "");
		assertEquals("0.38238237839293937,-979,", res);
	}

}
