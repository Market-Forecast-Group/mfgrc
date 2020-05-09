package com.mfg.widget.arc.strategy;

import java.io.IOException;

import org.mfg.mdb.runtime.MDBList;

import com.mfg.common.QueueTick;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.utils.MathUtils;

/**
 * The base class for all the free hand indicators.
 * 
 * <p>
 * It has the possibility to get the prices from the mdb database, have a left
 * and a right anchor.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BasicFreeHandIndicator implements IFreehandIndicator {

	@Override
	public double getGlobalTopDistanceRight() {
		int lastTime = (int) _list.get(_list.size() - 1).time;

		double maxDistance = 0;
		for (int i = _lastRightAnchor + 1; i <= lastTime; ++i) {
			try {
				_cursor.seek(i);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			double val = _extrapolateRightAnchorTo(i) + _lastTopDistance;
			if (val <= _cursor.price) {
				maxDistance = Math.max(maxDistance, _cursor.price - val);
			}
		}
		return maxDistance;
	}

	@Override
	public double getGlobalBottomDistanceRight() {
		int lastTime = (int) _list.get(_list.size() - 1).time;

		double maxDistance = 0;
		for (int i = _lastRightAnchor + 1; i <= lastTime; ++i) {
			try {
				_cursor.seek(i);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			double val = _extrapolateRightAnchorTo(i) + _lastBottomDistance;
			if (val >= _cursor.price) {
				maxDistance = Math.max(maxDistance, val - _cursor.price);
			}
		}
		return maxDistance;
	}

	@Override
	public int getMinimumTopTouch() {
		try {
			int lastTime = (int) _list.get(_list.size() - 1).time;
			for (int i = _lastRightAnchor + 1; i <= lastTime; ++i) {

				_cursor.seek(i);

				double val = _extrapolateRightAnchorTo(i) + _lastTopDistance;
				if (val <= _cursor.price) {
					return i;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMinimumBottomTouch() {
		int lastTime = (int) _list.get(_list.size() - 1).time;

		for (int i = _lastRightAnchor + 1; i <= lastTime; ++i) {
			try {
				_cursor.seek(i);
			} catch (IOException e) {
				e.printStackTrace();
				return Integer.MAX_VALUE;
			}
			double val = _extrapolateRightAnchorTo(i) - _lastBottomDistance;
			if (val >= _cursor.price) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}

	private final ConvexHull _topHull;

	private final ConvexHull _bottomHull;
	protected RandomCursor _cursor;

	protected int _lastRightAnchor;

	protected final int _leftAnchor;

	protected final MDBList<Record> _list;
	private double _lastTopDistance = -1;

	private double _lastBottomDistance = -1;
	private final int _firstPrice;

	private boolean _lastTopTouch;

	private boolean _lastBottomTouch;

	public BasicFreeHandIndicator(PriceMDB aDb, int aLeftAnchor)
			throws IOException {
		// TODO: this should not create the cursor, else receive it as argument.
		_cursor = aDb.thread_randomCursor();
		// aDb.getSession().defer(_cursor);
		_leftAnchor = aLeftAnchor;
		_lastRightAnchor = _leftAnchor - 1;
		_cursor.seek(_leftAnchor);

		_list = aDb.list(_cursor);

		_topHull = new ConvexHull(true, false);
		_bottomHull = new ConvexHull(false, false);

		_firstPrice = _cursor.price;
	}

	/**
	 * returns the right anchor value.
	 * 
	 * <p>
	 * usually this value is cached in the derived class and it is not
	 * recomputed until the method {@link #setRightAnchor(int)} is called again
	 * with another anchor.
	 * 
	 * <p>
	 * This method is not necessary for all the classes, because there are
	 * classes which do not have a straight line, in that case the default
	 * implementation, which throws, is sufficient
	 * 
	 * @return the right anchor value.
	 */
	@SuppressWarnings("static-method")
	protected double _getRightAnchorValue() {
		throw new UnsupportedOperationException();
	}

	/**
	 * extrapolates the indicator's value towards the right of the right anchor.
	 * 
	 * @param x
	 *            the value after the right anchor value.
	 * 
	 * @return the value of the <b>center</b> line indicator extrapolated to the
	 *         right of the right anchor.
	 */
	protected double _extrapolateRightAnchorTo(int x) {
		// it is a simple straight line.
		return _getRightAnchorValue()
				+ (((_getRightAnchorValue() - _list.get(_leftAnchor).price) / (_lastRightAnchor - _leftAnchor)) * (x - _lastRightAnchor));
	}

	/**
	 * implementation for the right anchor algorithm.
	 * 
	 * <p>
	 * Implementation classes might do some other checks before computing the
	 * indicator, for example the polyline tool will need so many points as the
	 * degree of the polynomial plus one.
	 * 
	 * @param aAnchor
	 * @throws IOException
	 */
	protected abstract void _setRightAnchorImpl(int aAnchor) throws IOException;

	private void _updateHulls(int aAnchor) throws IOException {
		// System.out.println("Update hull from " + _lastRightAnchor + " to "
		// + aAnchor);

		_lastTopDistance = -1;
		_lastBottomDistance = -1;

		if (aAnchor >= _lastRightAnchor) {
			// growing
			for (int i = _lastRightAnchor + 1; i <= aAnchor; ++i) {

				_cursor.seek(i);
				/*
				 * I have to add the points.
				 */
				QueueTick qt = new QueueTick(_cursor.physicalTime,
						(int) _cursor.time, _cursor.price, _cursor.real, 1);

				_topHull.addTick(qt);
				_bottomHull.addTick(qt);

			}
		} else {
			/*
			 * Shrinking hulls is not implemented, so I simply clear and redo
			 * all the computation.
			 */
			_topHull.clear();
			_bottomHull.clear();
			_lastRightAnchor = _leftAnchor;
			_updateHulls(aAnchor);
		}

	}

	@Override
	public final double getBottomDistance() {
		if (_lastBottomDistance < 0) {
			_lastBottomDistance = _bottomHull.getMaxDistanceFromPoly(
					getCenterLineCoefficients(), _leftAnchor, _lastRightAnchor,
					_list);
		}
		return _lastBottomDistance;
	}

	/**
	 * The default implementation of the method returns a straight line that
	 * goes from the left anchor to the value of the indicator at the right
	 * anchor. Subclasses may of course override the default behavior.
	 */
	@Override
	public double[] getCenterLineCoefficients() {
		if (_lastRightAnchor < _leftAnchor + 1) {
			// at least two points
			return null;
		}

		double coeff[] = new double[2];

		double rightAnchorValue = _getRightAnchorValue();
		coeff[1] = (rightAnchorValue - getLeftAnchorIndValue())
				/ (_lastRightAnchor - _leftAnchor);

		coeff[0] = rightAnchorValue - (_lastRightAnchor * coeff[1]);

		return coeff;
	}

	@Override
	public double getLeftAnchorIndValue() {
		/*
		 * default implementation returns the first price, but of course derived
		 * classes may override this.
		 */
		return _firstPrice;
	}

	@Override
	public int getLastRightAnchor() {
		return _lastRightAnchor;
	}

	@Override
	public final double getTopDistance() {
		if (_lastTopDistance < 0) {
			_lastTopDistance = _topHull.getMaxDistanceFromPoly(
					getCenterLineCoefficients(), _leftAnchor, _lastRightAnchor,
					_list);
		}
		return _lastTopDistance;
	}

	@Override
	public final void setRightAnchor(int aAnchor) throws IOException {
		if ((aAnchor == _lastRightAnchor) || (aAnchor < _leftAnchor)) {
			return;
		}
		_setRightAnchorImpl(aAnchor);

		_updateHulls(aAnchor);
		_lastRightAnchor = aAnchor;

		getTopDistance();
		getBottomDistance();
		_updateTouches();

		// double maxTop = getGlobalTopDistanceRight();
		// double maxBottom = getGlobalBottomDistanceRight();
		//
		// U.debug_var(392934, "se right anchor ", aAnchor, " last tick is ",
		// _list.get(_list.size() - 1).time, " max top d ", maxTop,
		// " max bot ", maxBottom);

	}

	private final void _updateTouches() throws IOException {
		_lastTopTouch = false;
		_lastBottomTouch = false;

		double rightAnchorValue = _getRightAnchorValue();
		_cursor.seek(_lastRightAnchor);
		if (MathUtils.almost_equal(rightAnchorValue + _lastTopDistance,
				_cursor.price)) {
			_lastTopTouch = true;
			// U.debug_var(283742, "Up touch! fake ", _lastRightAnchor,
			// " price  ", _cursor.price);
		}

		if (MathUtils.almost_equal(rightAnchorValue - _lastBottomDistance,
				_cursor.price)) {
			_lastBottomTouch = true;
			// U.debug_var(892053, "DOWN touch! fake ", _lastRightAnchor,
			// " price  ", _cursor.price);
		}

	}

	@Override
	public final boolean isTopTouching() {
		return _lastTopTouch;
	}

	@Override
	public final boolean isBottomTouching() {
		return _lastBottomTouch;
	}
}
