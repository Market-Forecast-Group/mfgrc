package com.mfg.widget.arc.strategy;

import java.io.IOException;

import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.widget.arc.math.geom.JamaPolyTrendLine;

/**
 * A free hand indicator based on a polynomial fitting
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class PolyFreehandIndicator extends BasicFreeHandIndicator {

	@Override
	protected double _getRightAnchorValue() {
		return _trendLine.predict(_lastRightAnchor);
	}

	@Override
	public double getLeftAnchorIndValue() {
		return _trendLine.predict(_leftAnchor);
	}

	private final JamaPolyTrendLine _trendLine;

	public PolyFreehandIndicator(PriceMDB aDb, int aLeftAnchor, int degree)
			throws IOException {
		super(aDb, aLeftAnchor);
		_trendLine = new JamaPolyTrendLine(degree);
	}

	@Override
	public void _setRightAnchorImpl(int aAnchor) throws IOException {
		if (aAnchor - _leftAnchor < _trendLine.getDegree()) {
			return;
		}

		/*
		 * Ok, now I build the matrixes
		 */

		int length = aAnchor - _leftAnchor + 1;
		double x[] = new double[length];
		double y[] = new double[length];

		for (int i = 0; i < length; ++i) {
			x[i] = _leftAnchor + i;
			_cursor.seek(_leftAnchor + i);
			y[i] = _cursor.price;
		}

		_trendLine.setValues(y, x, x.length);
	}

	@Override
	public double[] getCenterLineCoefficients() {
		if (_lastRightAnchor - _leftAnchor < _trendLine.getDegree()) {
			return null;
		}
		return _trendLine.getCoeff();
	}

	@Override
	protected double _extrapolateRightAnchorTo(int x) {
		return _trendLine.predict(x);
	}

}
