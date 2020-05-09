package com.mfg.widget.arc.strategy;

import java.io.IOException;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.mfg.inputdb.prices.mdb.PriceMDB;

/**
 * This is the linear regression version of the free hand indicator.
 * 
 * <p>
 * The linear regression will start from the left anchor and will extends
 * towards the right anchor.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class LinearRegFreeIndicator extends BasicFreeHandIndicator {

	@Override
	public double getLeftAnchorIndValue() {
		return _reg.predict(_leftAnchor);
	}

	private final SimpleRegression _reg = new SimpleRegression();

	@Override
	protected double _getRightAnchorValue() {
		return _reg.predict(_lastRightAnchor);
	}

	public LinearRegFreeIndicator(PriceMDB aDb, int aLeftAnchor)
			throws IOException {
		super(aDb, aLeftAnchor);
	}

	@Override
	protected void _setRightAnchorImpl(int aAnchor) throws IOException {
		for (int i = _lastRightAnchor + 1; i <= aAnchor; ++i) {
			_cursor.seek(i);
			_reg.addData(i, _cursor.price);
		}

	}

	@Override
	protected double _extrapolateRightAnchorTo(int x) {
		return _reg.predict(x);
	}

}
