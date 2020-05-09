package com.mfg.widget.arc.strategy;

import java.io.IOException;

import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.utils.IterativeMean;

/**
 * A very simple free hand indicator based on a moving average.
 * 
 * <p>
 * Top and bottom distances are fixed.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MovingAverageFreeIndicator extends BasicFreeHandIndicator {

	private final IterativeMean _mean;

	/**
	 * This defines the last successful right anchor set. It means that the
	 * indicator has been computed till this point.
	 */
	// private int _lastRightAnchor;
	// private final RandomCursor _cursor;

	/**
	 * The value of the mean at the previous point, the right-1 anchor point
	 */
	// private double _prevMean;

	public MovingAverageFreeIndicator(PriceMDB aDb, int aLeftAnchor)
			throws IOException {
		super(aDb, aLeftAnchor);
		_mean = new IterativeMean();

	}

	@Override
	protected double _getRightAnchorValue() {
		return _mean.getAvg();
	}

	@Override
	public void _setRightAnchorImpl(int aAnchor) throws IOException {
		if (aAnchor > _lastRightAnchor) {
			// growing
			for (int i = _lastRightAnchor + 1; i <= aAnchor; ++i) {
				_cursor.seek(i);
				_mean.add(_cursor.price);
				// System.out.println("ADD " + _cursor.time + " price "
				// + _cursor.price + " CUR MEAN " + _mean.getAvg());

			}
		} else {

			throw new IllegalStateException();
			// // shrinking
			// for (int i = _lastRightAnchor; i > aAnchor; --i) {
			// _cursor.seek(i);
			// _mean.remove(_cursor.price);
			// // System.out.println("DEL " + _cursor.time + " price "
			// // + _cursor.price + " CUR MEAN " + _mean.getAvg());
			// }
		}

		// _lastRightAnchor = aAnchor;
	}

	// @Override
	// public double[] getCenterLineCoefficients() {
	// /*
	// * the coefficients for the moving average are defined as the value of
	// * the moving average at the right point and the value of the moving
	// * average at the point before the last, so the slope of the moving
	// * average is the rate of change of the moving average itself.
	// */
	//
	// if (_lastRightAnchor < _leftAnchor + 1) {
	// // at least two points
	// return null;
	// }
	//
	// double coeff[] = new double[2];
	//
	// /*
	// * the angular coefficient is simply the difference in the mean value,
	// * as the prices are consecutive.
	// */
	// coeff[1] = (_mean.getAvg() - _firstPrice)
	// / (_lastRightAnchor - _leftAnchor);
	// // coefficients[0] = getCenterY1() - (coefficients[1] * getX1());
	// // coeff[0] = _firstPrice - (coeff[1] * _leftAnchor);
	//
	// coeff[0] = _mean.getAvg() - (_lastRightAnchor * coeff[1]);
	//
	// // System.out.println("[ y = " + coeff[1] + " x + " + coeff[0] +
	// // " right "
	// // + _lastRightAnchor + " mean " + _mean.getAvg());
	//
	// return coeff;
	// }

}
