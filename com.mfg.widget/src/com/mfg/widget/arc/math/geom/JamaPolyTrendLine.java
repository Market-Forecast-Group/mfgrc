package com.mfg.widget.arc.math.geom;

import Jama.Matrix;
import Jama.QRDecomposition;

import com.mfg.utils.U;

/**
 * A polynomial trend line based on <i>Jama</i>, the java matrix library.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class JamaPolyTrendLine {

	// private static final int TEST_SIZE = 10000;

	// private static final int TEST_DEGREE = 2;

	/**
	 * Just a performance test for the library itself.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		if (args.length < 3) {
			System.err.println("I need 3 arguments: length, degree iter max");
			System.exit(2);
		}

		int size = Integer.parseInt(args[0]);
		int degree = Integer.parseInt(args[1]);
		int ITER_MAX = Integer.parseInt(args[2]);

		System.out.println("doing " + ITER_MAX + " iter for length " + size
				+ " and degree " + degree);

		long then = System.currentTimeMillis();
		JamaPolyTrendLine jptl;

		for (int i = 0; i < ITER_MAX; ++i) {
			jptl = new JamaPolyTrendLine(degree);
			jptl._test_benchmark(0.0, 0.1, size);
		}

		long now = System.currentTimeMillis();

		long delta = now - then;
		System.out.println("done in " + delta + " avg delta per iter: " + delta
				/ (double) ITER_MAX);
		// for (int i = 0; i < jptl._coeffs.length; ++i) {
		// System.out.println(" " + jptl._coeffs[i]);
		// }

	}

	/**
	 * a simple
	 * 
	 * @param initVal
	 * @param incrVal
	 * @param testSize
	 *            The size of the test
	 */
	private void _test_benchmark(double initVal, double incrVal, int testSize) {
		_coeffs = null; // old values are not good.
		// final int SIZE = x.length;
		double matA_data[][] = new double[testSize][DEGREE + 1];

		double curVal = initVal;
		for (int i = 0; i < testSize; ++i) {
			double val = 1;
			for (int j = 0; j < DEGREE + 1; ++j) {
				matA_data[i][j] = val;
				val *= curVal;
			}
			curVal += incrVal;
		}

		Matrix matA = new Matrix(matA_data, testSize, DEGREE + 1);

		QRDecomposition qr = new QRDecomposition(matA);

		// coeff has many rows as the degree, plus one, and one column.
		try {
			Matrix Y = new Matrix(testSize, 1);
			double[][] ydata = Y.getArray();
			double val = initVal;
			for (int i = 0; i < testSize; ++i) {
				ydata[i][0] = fit_f(val);
				val += incrVal;
			}

			// Matrix _coeff = qr.solve(new Matrix(y, size));
			Matrix _coeff = qr.solve(Y);
			_coeffs = _coeff.getRowPackedCopy();
		} catch (RuntimeException e) {
			U.debug_var(392995, e);
		}

	}

	private static double fit_f(double x) {
		return 4.88 * (3.33 + x * (2.34 + x * (5.34 + x))) + 837 * Math.sin(x);
	}

	private final int DEGREE;

	/**
	 * The coefficients of the trend line, they are set after the user call
	 * {@link #setValues(double[], double[])}
	 */
	private double[] _coeffs;

	public JamaPolyTrendLine(int degree) {
		if (degree < 2) {
			throw new RuntimeException("Cannot have a degree " + degree);
		}
		DEGREE = degree;
	}

	public void setValues(double[] y, double[] x, int size) {
		_coeffs = null; // old values are not good.
		// final int SIZE = x.length;
		double matA_data[][] = new double[size][DEGREE + 1];

		for (int i = 0; i < size; ++i) {
			double val = 1;
			for (int j = 0; j < DEGREE + 1; ++j) {
				matA_data[i][j] = val;
				val *= x[i];
			}
		}

		Matrix matA = new Matrix(matA_data, size, DEGREE + 1);

		QRDecomposition qr = new QRDecomposition(matA);

		// coeff has many rows as the degree, plus one, and one column.
		try {
			Matrix Y = new Matrix(size, 1);
			double[][] ydata = Y.getArray();
			for (int i = 0; i < size; ++i) {
				ydata[i][0] = y[i];
			}

			// Matrix _coeff = qr.solve(new Matrix(y, size));
			Matrix _coeff = qr.solve(Y);
			_coeffs = _coeff.getRowPackedCopy();
		} catch (RuntimeException e) {
			U.debug_var(392995, e);
		}

	}

	/**
	 * This method assumes that the coefficients are polynomial coefficients.
	 * 
	 * @param x
	 * @return
	 */
	public double predict(double x) {
		if (_coeffs == null) {
			return Double.NaN;
		}
		return PolyEvaluator.evaluate(_coeffs, x);
	}

	public void printCoeff() {
		for (int i = 0; i < _coeffs.length; ++i) {
			System.out.println(" " + _coeffs.length);
		}
	}

	public double[] getCoeff() {
		return _coeffs;
	}

	public int getDegree() {
		return this.DEGREE;
	}

}
