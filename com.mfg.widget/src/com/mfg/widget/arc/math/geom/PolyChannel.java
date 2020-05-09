package com.mfg.widget.arc.math.geom;

import java.io.Serializable;

/**
 * A Channel whose boundaries are arbitrary polynomials.
 * 
 * <p>
 * This class is immutable, once the object is created it cannot be changed any
 * more.
 * 
 * <p>
 * A normal channel is a channel which has <b>lines</b> as boundaries, and this
 * has been the normal channel for so long, but we can have different channels
 * in which the center, top and bottom lines are not straight but are generic
 * polynomial segments of arbitrary degree, usually not more than four, but this
 * is not enforced by the implementation.
 */
public class PolyChannel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4055558900416017027L;

	/**
	 * The level (scale) of the channel, 1 based.
	 */
	private final int _level;

	/**
	 * The coefficients of the center line.
	 */
	private final double[] _coefficients;

	/**
	 * The top polyline lies at a constant distance above the center, so its
	 * coefficients are exactly the same but the first, which is
	 * <code>_coeff[0] + _topDistance</code>.
	 * 
	 * <p>
	 * Invariant: top distance is always positive.
	 */
	private final double _topDistance;

	/**
	 * The bottom polyline lies at a constant distance below the center, so its
	 * coefficients are exactly the same but the first, which is
	 * <code>_coeff[0] - _bottomDistance</code>.
	 * 
	 * <p>
	 * Invariant: _bottomDistance is always positive.
	 */
	private final double _bottomDistance;

	/**
	 * The polychannel has a starting point, the starting point is an integer
	 * value, a fake time, but here it is stored as a double, because in any
	 * case the polychannel deals with floating point values.
	 */
	private final double _x1;

	/**
	 * The polychannel has also an ending point.
	 */
	private final double _x2;

	public PolyChannel(int aLevel, double start, double end,
			double topDistance, double bottomDistance, double coefficients[]) {

		if (topDistance < 0 || bottomDistance < 0) {
			throw new IllegalArgumentException();
		}

		_level = aLevel;
		_coefficients = coefficients;
		_topDistance = topDistance;
		_bottomDistance = bottomDistance;
		_x1 = start;
		_x2 = end;
	}

	public final double evaluateCenter(double x) {
		return PolyEvaluator.evaluate(_coefficients, x);

	}

	public final double getBottomY1() {
		return getCenterY1() - _bottomDistance;
	}

	public final double getBottomY2() {
		return getCenterY2() - _bottomDistance;
	}

	/**
	 * convenience method to get the channel's value at certain points
	 * 
	 * @return the center line at the start of the channel
	 */
	public final double getCenterY1() {
		return PolyEvaluator.evaluate(_coefficients, _x1);
	}

	/**
	 * 
	 * @return the end line at the start of the channel
	 */
	public final double getCenterY2() {
		return PolyEvaluator.evaluate(_coefficients, _x2);
	}

	public final int getDegree() {
		return _coefficients.length - 1;
	}

	/**
	 * returns the end of the channel.
	 * 
	 * @return
	 */
	public final double getEnd() {
		return _x2;
	}

	public final int getLevel() {
		return _level;
	}

	/**
	 * gets the slope for the polychannel, the slope is defined only if the
	 * polychannel has degree one.
	 * 
	 * <p>
	 * In case of other polynomials an average of the slope over the entire
	 * channel is returned instead.
	 * 
	 * @return the slope of the polychannel.
	 */
	public final double getSlope() {
		if (_coefficients.length == 1) {
			throw new IllegalStateException();
		}

		if (_coefficients.length == 2) {
			/*
			 * the slope of a line is the coefficient for the x.
			 */
			return _coefficients[1];
		}

		/*
		 * a average of the slope..., just to silence the client.
		 */
		return (getCenterY2() - getCenterY1()) / (_x2 - _x1);

	}

	public final double getStart() {
		return _x1;
	}

	public final double getTopY1() {
		return getCenterY1() + _topDistance;
	}

	public final double getTopY2() {
		return getCenterY2() + _topDistance;
	}

	/**
	 * returns the channel coefficients as a polyline.
	 * 
	 * <p>
	 * they are in inverse order, from the constant term to the highets.
	 * 
	 * @return a reference to the array, please do not modify it.
	 */
	public double[] getChannelCoefficients() {
		return _coefficients;
	}

}
