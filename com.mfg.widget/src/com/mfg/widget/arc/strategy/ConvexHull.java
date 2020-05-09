package com.mfg.widget.arc.strategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.mfg.mdb.runtime.MDBList;

import com.mfg.common.QueueTick;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.utils.MathUtils;
import com.mfg.utils.PolySolver;
import com.mfg.utils.U;
import com.mfg.widget.arc.math.geom.PolyEvaluator;

/**
 * A convex hull of prices, this implementation of convex hull is probably only
 * useful in this application. It assumes that the prices arrive ordered in
 * time.
 * 
 * <p>
 * The internal representation of this convex hull is very simple. It is a
 * simple tree map that from a start point (<i>fake time</i>) it gives an end
 * point. Because from different start points we can have only one end point.
 * 
 * <p>
 * After that we have a hash map that tells us from an end point a list of start
 * point useful to traverse the points backwards.
 * 
 * @author Sergio
 * 
 */
public class ConvexHull implements Cloneable, Serializable {

	protected static final class DistanceHull extends
			U.Tuple<Double, QueueTick> {

		@SuppressWarnings("boxing")
		public DistanceHull() {
			f1 = 0.0;
		}

		public void print() {
			System.out.print(toString());
		}

		@Override
		@SuppressWarnings("boxing")
		public String toString() {
			return "{max dist. " + f1 + " where? "
					+ (f2 == null ? " null " : f2.getFakeTime());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5888266830686072307L;

	private static final int HURRY_UP_CHUNK = 15;

	private static final int MAX_POLY = 10;

	private double[] _zeror = new double[MAX_POLY];

	private double[] _zeroi = new double[MAX_POLY];

	/**
	 * iteratively adds a point to this convex hull, it does not matter if the
	 * hull is upper or backward. The only thing which is important is that it
	 * should be coherent.
	 * 
	 * If you try to add a point twice it will be ignored (this behavior is not
	 * so standard but it simplify the development of the convex hull for the
	 * channels)
	 * 
	 * @param qt
	 *            the tick to be added.
	 * 
	 * @param aConvexHull
	 *            the hull that receives the tick
	 */
	private static void _addPointHelper(QueueTick qt, ArrayList<QueueTick> aCh,
			int upHull) {

		if (aCh.size() != 0
				&& aCh.get(aCh.size() - 1).getFakeTime() == qt.getFakeTime()) {
			return;
		}

		// Ok, I have at least 2 points in the convex hull!
		while (aCh.size() >= 2) {

			QueueTick lastMinusOne = aCh.get(aCh.size() - 2);
			QueueTick last = aCh.get(aCh.size() - 1);

			if ((crossTicks(lastMinusOne, last, qt) * upHull) < 0) {
				break;
			}
			aCh.remove(aCh.size() - 1);
		}
		aCh.add(qt);
	}

	/**
	 * checks wheter the two polynomials (the second is a straight line)
	 * intersect between start and end OR they intersect at the outside AND the
	 * value of the first polynomial at the left point is up (down) regarding to
	 * the down (up) nature of the half hull considered, this means that the
	 * rightmost root to the left point has a derivative positive (negative) is
	 * the hull is a bottom (up) hull.
	 * 
	 * <p>
	 * Usually the first polynomial is given by the outside and the second is a
	 * segment of the convex hull, but this method is general, it is here only
	 * because this is the place where it is used.
	 * 
	 * <p>
	 * The searched interval is open because usually the two polynomials
	 * intersect either at start or end, because the convex hull was designed in
	 * this way.
	 * 
	 * @param alteredPolynomial
	 *            first polynomial
	 * @param p2
	 *            second polynomial
	 * 
	 * @param start
	 * 
	 * @param end
	 * 
	 * @return true if there is an intersection between start and end
	 *         <b>exclusive</b>. In the sense in the interval (start, end).
	 */
	@SuppressWarnings("boxing")
	private boolean _findIntersectionPolynomials(double[] alteredPolynomial,
			double[] p2, double start, double end) {

		PolynomialFunction fp1 = new PolynomialFunction(alteredPolynomial);
		PolynomialFunction sub = fp1.subtract(new PolynomialFunction(p2));
		if (sub.degree() <= 1) {
			return false;
		}

		double[] pSub = sub.getCoefficients();

		/*
		 * I have to reverse them, because the polysolver wants them from
		 * highest to lower.
		 */
		double floorHalfL = Math.floor(pSub.length / 2.0);
		for (int i = 0; i < floorHalfL; ++i) {
			double tmp = pSub[i];
			pSub[i] = pSub[pSub.length - 1 - i];
			pSub[pSub.length - 1 - i] = tmp;
		}

		int res = PolySolver.rpoly_ak1(pSub, _zeror, _zeroi);

		if ((res + 1) != pSub.length) {
			/*
			 * the algorithm has not converged, I return true anyway and this
			 * will trigger a brute force inside
			 */
			U.debug_var(382395, "no convergence, I will use brute force here ",
					start, " to ", end);
			return true;
		}

		boolean oneRealRootInside = false;
		// for (double r : roots) {
		for (int i = 0; i < res; ++i) {
			/*
			 * Here the number is a magic number, but the cost of putting a
			 * magic number is not so high, because I may take for real some
			 * roots which are imaginary, but the cost of having this is to
			 * compute the brute force inside the interval when it is not the
			 * case.
			 * 
			 * In some sense I may write here some more complex conditions, but
			 * I will do it only if there will be the necessity.
			 */
			if (Math.abs(_zeroi[i]) > 0.21) {
				/*
				 * complex root
				 */
				continue;
			}
			double r = _zeror[i];

			if (MathUtils.almost_equalEps(r, start, 1e-3)) {
				if (oneRealRootInside) {
					return true;
				}
				oneRealRootInside = true;
				continue;
			}
			if (MathUtils.almost_equalEps(r, end, 1e-3)) {
				if (oneRealRootInside) {
					return true;
				}
				oneRealRootInside = true;
				continue;
			}

			if (r > start && r < end) {
				if (oneRealRootInside) {
					return true;
				}
				oneRealRootInside = true;
				continue;
			}
		}

		return false;

	}

	/**
	 * 
	 2D cross product of OA and OB vectors, i.e. z-component of their 3D cross
	 * product. Returns a positive value, if OAB makes a counter-clockwise turn,
	 * negative for clockwise turn, and zero if the points are collinear.
	 * 
	 * <p>
	 * In this case we don't have points but prices. The x coordinate is the
	 * fake time and the y coordinate is the price.
	 * 
	 * 
	 * @param o
	 *            the first (origin) queue tick
	 * @param a
	 *            the second queue tick
	 * @param b
	 *            the last queue tick
	 * @return the z component of the cross product, positive is counter
	 *         clockwise, negative if clockwise, zero if collinear.
	 */
	@SuppressWarnings({})
	private static double crossTicks(QueueTick o, QueueTick a, QueueTick b) {
		return (((a.getFakeTime() - o.getFakeTime()) * (b.getPrice() - o
				.getPrice())) - ((a.getPrice() - o.getPrice()) * (b
				.getFakeTime() - o.getFakeTime())));
	}

	private final int fUpHull;

	/**
	 * the convex hull is implemented as a simple array of ticks, ordered from
	 * left to right.
	 */
	private ArrayList<QueueTick> fCh = new ArrayList<>();

	private final boolean _useBruteForceForced;

	private int _hurryUp;

	/**
	 * creates a convex hull which can be "up" or "down", the difference is
	 * simply that a "up" hull is encloses the top half of the prices and a
	 * "down" hull encloses the bottom half of the prices.
	 * 
	 * @param aUpHull
	 *            if true means that this is a up hull.
	 * @param useBruteForce
	 */
	ConvexHull(boolean aUpHull, boolean useBruteForce) {
		// If I have a up hull I would add clockwise points, otherwise
		// counter clock wise.
		fUpHull = aUpHull ? 1 : -1;
		_useBruteForceForced = useBruteForce;
	}

	/**
	 * This is a helper method which gets the line coefficients for the segment
	 * that unites the price at p1 and the price at p2;
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("static-method")
	private double[] _getChordCoefficients(QueueTick p1, QueueTick p2) {

		// double coeff[] = new double[2];

		double coefficients[] = new double[2];

		// the angular coefficient
		coefficients[1] = (p2.getPrice() - p1.getPrice())
				/ (double) (p2.getFakeTime() - p1.getFakeTime());

		// the intercept
		coefficients[0] = p1.getPrice() - (coefficients[1] * p1.getFakeTime());

		return coefficients;

		// return coeff;
	}

	/**
	 * 
	 * @param poly
	 *            the augmented (or lowered) polynomial which is used to
	 *            intersect with the concave hull of the points between the
	 *            extremes f1 and f2 of the convex hull. F1 and F2 <b>MUST</b>
	 *            be points of the convex hull
	 * @param f1
	 *            the leftmost point for the concave hull
	 * @param f2
	 *            the rightmost point for the concave hull
	 * @param _list
	 * 
	 * @return the maximum delta, that is how much the poly must be heightened
	 *         (or lowered, depending on the convex hull type, top or bottom) as
	 *         to make it touch the highest (lowest) zero-scale zig (or zag)
	 *         pivot. The maximum delta returned is always positive, it may be
	 *         zero if all the points are lower (higher) than the modified
	 *         polyline.
	 */
	@SuppressWarnings("boxing")
	private DistanceHull _getMaxDeltaInternal(double[] poly, QueueTick f1,
			QueueTick f2, MDBList<Record> _list) {

		/*
		 * brute force between f1 and f2.
		 */

		DistanceHull dh = new DistanceHull();

		// double delta = 0;
		for (int i = f1.getFakeTime(); i < f2.getFakeTime(); ++i) {
			/*
			 * I could do a zig zag at zero scale, filtering the point which are
			 * down (up) depending if this is a up (down) hull.
			 */
			double val = PolyEvaluator.evaluate(poly, i);

			Record iQt = _list.get(i);

			double distance = fUpHull > 0 ? iQt.price - val : val - iQt.price;

			if (distance > dh.f1) {
				dh.f1 = distance;
				QueueTick qt = new QueueTick();
				iQt.copyTo(qt);
				dh.f2 = qt;
			}
		}

		return dh;
	}

	/**
	 * adds a point to this convex hull, updating the internal structures, if
	 * necessary.
	 * 
	 * @param qt
	 */
	public void addTick(QueueTick qt) {
		_addPointHelper(qt, fCh, fUpHull);
	}

	/**
	 * clears this convex hull. No points will be left after.
	 */
	public void clear() {
		fCh.clear();
	}

	// @Override
	// protected ConvexHull clone() {
	// ConvexHull cloned;
	// try {
	// cloned = (ConvexHull) super.clone();
	// cloned.fCh = (ArrayList<QueueTick>) this.fCh.clone();
	// return cloned;
	// } catch (CloneNotSupportedException e) {
	// assert (false);
	// }
	// return null;
	// }

	/**
	 * returns true if the convex hull is coherent.
	 * 
	 * @param anotherCh
	 * @return
	 */
	public boolean coherent(ConvexHull anotherCh) {

		if (this.fUpHull != anotherCh.fUpHull) {
			return false;
		}

		if (this.fCh.size() != anotherCh.fCh.size()) {
			return false;
		}

		for (int i = 0; i < fCh.size(); ++i) {
			if (fCh.get(i) != anotherCh.fCh.get(i)) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Returns the maximum distance of the polynomial from the list of prices.
	 * The convex hull is not used any more.
	 * 
	 * @param coeff
	 *            the polynomial function
	 * @param aStart
	 *            where to start
	 * @param aEnd
	 *            where to finish
	 * @param _list
	 *            the list of points
	 * @return the distance and the point where it has been found.
	 */
	@SuppressWarnings("boxing")
	private DistanceHull getMaxDistanceBruteForce(double[] coeff, int aStart,
			int aEnd, MDBList<Record> _list) {
		DistanceHull dh = new DistanceHull();

		int i;
		for (i = aStart; i < aEnd; ++i) {
			Record record = _list.get(i);

			double pred = PolyEvaluator.evaluate(coeff, i);

			double distance = fUpHull > 0 ? record.price - pred : pred
					- record.price;

			if (dh.f1 < distance) {
				dh.f1 = distance;
				QueueTick qt = new QueueTick();
				record.copyTo(qt);
				dh.f2 = qt;
			}

		}

		// then the last tick
		QueueTick lastTick = fCh.get(fCh.size() - 1);
		if (i != lastTick.getFakeTime()) {
			/*
			 * If you are here then the convex hull has not been updated.
			 */
			throw new IllegalStateException();
		}
		double pred = PolyEvaluator.evaluate(coeff, i);

		double distance = fUpHull > 0 ? lastTick.getPrice() - pred : pred
				- lastTick.getPrice();

		if (dh.f1 < distance) {
			dh.f1 = distance;
			dh.f2 = lastTick;
		}

		return dh;
	}

	/**
	 * gets the maximum distance between this convex hull and the polynomial
	 * represented by the coefficients given.
	 * 
	 * <p>
	 * The coefficients are ordered from the zero degree term to the maximum
	 * 
	 * <p>
	 * The left,right extremes are given, because the convex hull could extend
	 * to the left (probably) or to the right (unlikely) of the channel.
	 * 
	 * @param coeff
	 *            the coefficients
	 * @param aStart
	 *            the start x (time) from which the convex hull is valid,
	 *            inclusive
	 * @param aEnd
	 *            the ending x (time) until which the convex hull is valid,
	 *            inclusive
	 * @param _list
	 *            the list of prices, this is used when the algo finds an
	 *            intersection inside the convex hull
	 * @return the maximum distance.
	 */
	@SuppressWarnings("boxing")
	public double getMaxDistanceFromPoly(double coeff[], int aStart, int aEnd,
			MDBList<Record> _list) {

		if (!_useBruteForceForced) {
			/*
			 * Parabola or straight line. I use the convex hull.
			 */
			DistanceHull resCH;
			resCH = getMaxDistanceFromPolyLine(coeff, aStart, aEnd);

			/*
			 * If coeff is linear I am done
			 */
			if (coeff.length < 3) {
				return resCH.f1;
			}

			// generic polynomial
			double alteredPoly[] = Arrays.copyOf(coeff, coeff.length);
			if (fUpHull > 0) {
				alteredPoly[0] += resCH.f1;
			} else {
				alteredPoly[0] -= resCH.f1;
			}

			QueueTick leftSide = null;
			double augmentedDelta = 0;
			for (QueueTick chPoint : fCh) {
				if (leftSide != null) {
					double[] coefficients = _getChordCoefficients(leftSide,
							chPoint);

					/*
					 * now the intersection between the modified polynomial and
					 * the chord.
					 */
					boolean intersection = _findIntersectionPolynomials(
							alteredPoly, coefficients, leftSide.getFakeTime(),
							chPoint.getFakeTime());

					if (intersection) {
						DistanceHull internalDelta = _getMaxDeltaInternal(
								alteredPoly, leftSide, chPoint, _list);
						// U.debug_var(727832, "intersection! found res ",
						// internalDelta);
						augmentedDelta = Math.max(augmentedDelta,
								internalDelta.f1);
					}

				}
				leftSide = chPoint;
			}

			/*
			 * Now in augmentedDelta there is the maximum delta which is used to
			 * put up (or down) the polynomial
			 */

			// U.debug_var(292381, "augmented delta is " + augmentedDelta);
			double maxDistanceNCH = resCH.f1 + augmentedDelta;

			/*
			 * Put it true to force the test with the brute force.
			 */
			boolean testAlsoBruteForce = false;
			if (testAlsoBruteForce) {
				DistanceHull resBF = getMaxDistanceBruteForce(coeff, aStart,
						aEnd, _list);

				if (!MathUtils.almost_equal(maxDistanceNCH, resBF.f1)) {
					U.debug_var(838323,
							"incoherence between brute force and convex hull ",
							maxDistanceNCH, " != ", resBF, " @ ", aEnd);
				}
			}

			return maxDistanceNCH;

		}

		/*
		 * brute force for polynomials 3 and 4, or if brute force flag is true
		 */
		DistanceHull resBF = getMaxDistanceBruteForce(coeff, aStart, aEnd,
				_list);
		return resBF.f1;
	}

	/**
	 * This is a first-cut version that will give the maximum distance computing
	 * it only at the vertexes of the convex hull
	 * 
	 * 
	 * @param coeff
	 * @param aStart
	 * @param aEnd
	 * @return a tuple which contains the maximum distance and the point where
	 *         this distance has been reached.
	 */
	@SuppressWarnings("boxing")
	private DistanceHull getMaxDistanceFromPolyLine(double coeff[], int aStart,
			int aEnd) {
		DistanceHull dh = new DistanceHull();
		if (fCh.size() == 0) {
			return dh;
		}

		// double maxD = 0;
		for (QueueTick qt : fCh) {
			if (qt.getFakeTime() < aStart) {
				continue;
			}
			if (qt.getFakeTime() > aEnd) {
				break;
			}

			double pred = PolyEvaluator.evaluate(coeff, qt.getFakeTime());
			double distance;

			distance = fUpHull > 0 ? qt.getPrice() - pred : pred
					- qt.getPrice();

			if (distance < 0) {
				continue;
			}

			if (dh.f1 < distance) {
				dh.f2 = qt;
				dh.f1 = distance;
			}
		}
		// dh.f1 = maxD;
		return dh;
	}

	public boolean isValid(int startPoint, int endPoint) {
		if (fCh.size() == 0) {
			return true;
		}

		if (fCh.get(0).getFakeTime() != startPoint) {
			return false;
		}

		if (fCh.get(fCh.size() - 1).getFakeTime() != endPoint) {
			return false;
		}

		return true;
	}

	/**
	 * moves this convex hull forward to the fake time considered. This needs
	 * the underlying data set as the ch may need to go <i>inside</i>.
	 * 
	 * @param fakeTime
	 * @param dataSet
	 */
	public void moveForwardTo(int fakeTime, MDBList<Record> ranMDBList) {

		if (fakeTime - fCh.get(0).getFakeTime() < _hurryUp * HURRY_UP_CHUNK)
			return;

		// the convex hull, backward to the faketime is the same.

		ArrayList<QueueTick> ans = new ArrayList<>();
		// I will build the convex hull reverted.

		ListIterator<QueueTick> li = fCh.listIterator(fCh.size());
		while (li.hasPrevious()) {
			QueueTick qt = li.previous();
			if (qt.getFakeTime() < fakeTime) {
				break;
			}
			ans.add(qt);
		}

		// Ok, I now have the convex hull until the point after the fake time,
		// now I should go backward in the data set to find the correct time,
		// after
		// that I will append (glue) the "minor" convex hull, that is the convex
		// hull at minor scale.

		// I have to find the time of the last point in the convex hull
		// which is the first (counting from left), but last (because the list
		// is reversed)
		int lowestChTime = ans.get(ans.size() - 1).getFakeTime();

		if (lowestChTime > 0) {
			for (--lowestChTime; lowestChTime >= fakeTime; --lowestChTime) {
				QueueTick qt = new QueueTick(); // I must always create a new
												// queue tick
				com.mfg.inputdb.prices.mdb.PriceMDB.Record rec = ranMDBList
						.get(lowestChTime);
				rec.copyTo(qt);
				_addPointHelper(qt, ans, -fUpHull);
			}
		}

		// the real convex hull is reverted!
		Collections.reverse(ans);
		fCh = ans;
	}

	public void hurryUp(int hurryUpQuota) {
		_hurryUp = hurryUpQuota;
	}

	public void calmDown(int calmDownQuota) {
		_hurryUp = calmDownQuota;
	}

}
