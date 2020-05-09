package com.mfg.widget.arc.strategy;

import java.io.IOException;

import com.mfg.inputdb.prices.mdb.PriceMDB;

/**
 * A simple indicator which is used to draw a straight line between the left and
 * the right anchor.
 * 
 * <p>
 * Top and bottom distances are used with the convex hull.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class StraightLineFreeIndicator extends BasicFreeHandIndicator {

	private int _lastRightAnchorPrice;

	public StraightLineFreeIndicator(PriceMDB aDb, int aLeftAnchor)
			throws IOException {
		super(aDb, aLeftAnchor);
	}

	@Override
	protected void _setRightAnchorImpl(int aAnchor) throws IOException {
		_cursor.seek(aAnchor);
		_lastRightAnchorPrice = _cursor.price;
	}

	@Override
	protected double _getRightAnchorValue() {
		return _lastRightAnchorPrice;
	}

}
