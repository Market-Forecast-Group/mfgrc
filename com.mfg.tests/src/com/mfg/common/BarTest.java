package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BarTest {

	@SuppressWarnings("static-method")
	@Test
	public void testSerialize() {
		Bar aBar = new Bar(1234, 100, 105, 90, 101, 919);

		assertEquals("1970-01-01-01-00-01.234,100,105,90,101,919",
				aBar.serialize());

		// Ok, now we get the bar back.

		Bar check = Bar
				.parseFromString("1970-01-01-01-00-01.234,100,105,90,101,919");

		assertTrue(check.equals(aBar));

	}

	@SuppressWarnings("static-method")
	@Test
	public void testExpandRangeBar() {

		// a two ticks bar.
		RangeBar rb = new RangeBar(1000, 100, 105, 100, 105, 11, 15);

		Tick arr[] = new Tick[4];
		for (int i = 0; i < 4; ++i) {
			arr[i] = new Tick();
		}

		rb.expand(arr, 1000);

		// assertEquals(1000, arr[3].physicalTime);
		assertEquals(105, arr[3].price);
		assertEquals(105, arr[2].price);

		assertEquals(1000, arr[1].physicalTime);
		assertEquals(105, arr[1].price);
		assertEquals(15, arr[1]._volume);

		assertEquals(500, arr[0].physicalTime);
		assertEquals(100, arr[0].price);
		assertEquals(11, arr[0]._volume);

		// a 3-ticks bar, the open is equal to the close.
		rb = new RangeBar(1000, 100, 105, 100, 100, 11, 15);

		rb.expand(arr, 1000);

		assertEquals(100, arr[3].price);

		assertEquals(1000, arr[2].physicalTime);
		assertEquals(100, arr[2].price);
		assertEquals(5, arr[2]._volume);

		assertEquals(667, arr[1].physicalTime);
		assertEquals(105, arr[1].price);
		assertEquals(15, arr[1]._volume);

		assertEquals(334, arr[0].physicalTime);
		assertEquals(100, arr[0].price);
		assertEquals(6, arr[0]._volume);

	}

	@SuppressWarnings("static-method")
	@Test
	public void testExpand() {
		Bar aBar = new Bar(1000, 100, 105, 90, 101, 919);
		Tick arr[] = new Tick[4];
		for (int i = 0; i < 4; ++i) {
			arr[i] = new Tick();
		}

		aBar.expand(arr, 1000);

		assertEquals(1000, arr[3].physicalTime);
		assertEquals(101, arr[3].price);
		assertEquals(229, arr[3]._volume);

		assertEquals(750, arr[2].physicalTime);
		assertEquals(105, arr[2].price);
		assertEquals(229, arr[2]._volume);

		assertEquals(500, arr[1].physicalTime);
		assertEquals(90, arr[1].price);
		assertEquals(229, arr[1]._volume);

		assertEquals(250, arr[0].physicalTime);
		assertEquals(100, arr[0].price);
		assertEquals(232, arr[0]._volume);

	}

}
