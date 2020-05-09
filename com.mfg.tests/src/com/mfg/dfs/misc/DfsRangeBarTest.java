package com.mfg.dfs.misc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.RangeBar;
import com.mfg.common.UnparsedBar;

public class DfsRangeBarTest {

	@SuppressWarnings("static-method")
	@Test
	public void testDecodeTo() throws DFSException {

		RangeBar aBar = new RangeBar();
		aBar.setOpen(10);
		aBar.setHigh(10);
		aBar.setLow(9);
		aBar.setClose(10);

		DfsRangeBar drb = new DfsRangeBar(aBar, 1);

		Bar other = drb.decodeTo(1);

		assertTrue(aBar.equals(other));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testUnparsedEncode() throws DFSException {
		UnparsedBar ub = new UnparsedBar(77, "11.25", "11.50", "11.25",
				"11.25", 88);
		DfsRangeBar drb = new DfsRangeBar(ub, 2);
		Bar other = drb.decodeTo(25);

		Bar reference = new Bar(77, 1125, 1150, 1125, 1125, 88);
		assertFalse(reference.equals(other));
		// the assert fails because the passage to range bar does not save the
		// volume

		reference = new Bar(77, 1125, 1150, 1125, 1125, 0);
		assertTrue(reference.equals(other));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testDecodeInPlace() throws DFSException {
		RangeBar aBar = new RangeBar();
		aBar.setOpen(10);
		aBar.setHigh(11);
		aBar.setLow(10);
		aBar.setClose(10);

		DfsRangeBar drb = new DfsRangeBar(aBar, 1);
		DfsRangeBar drb1 = new DfsRangeBar(aBar, 1);

		assertTrue(drb.equals(drb1));
		assertTrue(drb.hashCode() == drb1.hashCode());

		RangeBar secondBar = new RangeBar();

		drb.decodeInPlace(secondBar, 1);

		Assert.assertTrue(aBar.equals(secondBar));

		aBar.setHigh(12);

		drb = new DfsRangeBar(aBar, 1);

		drb.decodeInPlace(secondBar, 1);

		Assert.assertFalse(aBar.equals(secondBar)); // this should fail!

	}

}
