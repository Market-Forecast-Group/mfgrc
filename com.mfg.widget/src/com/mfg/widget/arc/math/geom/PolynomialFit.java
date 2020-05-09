package com.mfg.widget.arc.math.geom;

import java.util.Random;

import Jama.Matrix;
import Jama.QRDecomposition;
//import org.ejml.alg.dense.linsol.AdjustableLinearSolver;
//import org.ejml.data.DenseMatrix64F;
//import org.ejml.factory.LinearSolverFactory;

/**
 * <p>
 * This example demonstrates how a polynomial can be fit to a set of data. This
 * is done by using a least squares solver that is adjustable. By using an
 * adjustable solver elements can be inexpensively removed and the coefficients
 * recomputed. This is much less expensive than resolving the whole system from
 * scratch.
 * </p>
 * <p>
 * The following is demonstrated:<br>
 * <ol>
 * <li>Creating a solver using LinearSolverFactory</li>
 * <li>Using an adjustable solver</li>
 * <li>reshaping</li>
 * </ol>
 * 
 * @author Peter Abeles
 */
public class PolynomialFit {

	/**
	 * @param args
	 */
	public static void main___(String[] args) {

		final int SIZE = 100_0;

		// PolynomialFit t = new PolynomialFit(4);
		Random rand = new Random();
		double[] x = new double[SIZE];
		double[] err = new double[x.length];
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = 1000 * rand.nextDouble();
		}
		for (int i = 0; i < x.length; i++) {
			err[i] = 100 * rand.nextGaussian();
		}
		for (int i = 0; i < x.length; i++) {
			y[i] = x[i] * x[i] + err[i];
		} // quadratic model
			// t.setValues(y, x);
			// System.out.println(t.predict(12)); // when x=12, y should be... ,
			// eg
			// // 143.61380202745192

		// t.fit(x, y);

		final int DEGREE = 5;
		long then = System.currentTimeMillis();
		double matA_data[][] = new double[SIZE][DEGREE + 1];

		for (int i = 0; i < SIZE; ++i) {
			double val = 1;
			for (int j = 0; j < DEGREE + 1; ++j) {
				matA_data[i][j] = val;
				val *= x[i];
			}
		}

		Matrix matA = new Matrix(matA_data);

		QRDecomposition qr = new QRDecomposition(matA);

		Matrix solved = qr.solve(new Matrix(y, SIZE));

		long delta = System.currentTimeMillis() - then;
		System.out.println("PPP solv   ed in " + delta + " sol is " + solved);

		System.out.println("row " + solved.getRowDimension() + " col "
				+ solved.getColumnDimension());

	}

	// public static void mainEJML(String[] args) {
	// PolynomialFit t = new PolynomialFit(4);
	// Random rand = new Random();
	// double[] x = new double[7000];
	// double[] err = new double[x.length];
	// double[] y = new double[x.length];
	// for (int i = 0; i < x.length; i++) {
	// x[i] = 1000 * rand.nextDouble();
	// }
	// for (int i = 0; i < x.length; i++) {
	// err[i] = 100 * rand.nextGaussian();
	// }
	// for (int i = 0; i < x.length; i++) {
	// y[i] = x[i] * x[i] + err[i];
	// } // quadratic model
	// // t.setValues(y, x);
	// // System.out.println(t.predict(12)); // when x=12, y should be... ,
	// // eg
	// // // 143.61380202745192
	// long then = System.currentTimeMillis();
	// t.fit(x, y);
	// long delta = System.currentTimeMillis() - then;
	// System.out.println("solved in " + delta);
	//
	// // then = System.currentTimeMillis();
	// // t.addObservationFIFO(44.12, 2.34);
	// // delta = System.currentTimeMillis() - then;
	// // System.out.println("solved in " + delta);
	// //
	// // then = System.currentTimeMillis();
	// // t.addObservationFIFO(45.12, 21.34);
	// // delta = System.currentTimeMillis() - then;
	// // System.out.println("2nd solved in " + delta);
	// }

	// public static void main(String args[]) {
	//
	// PolynomialFit pf = new PolynomialFit(2);
	//
	// double x[] = new double[5];
	//
	// for (int i = 0; i < x.length; ++i) {
	// x[i] = i;
	// }
	//
	// double y[] = new double[5];
	// y[0] = 1.52;
	// y[1] = 2.52;
	// y[2] = 5.41;
	// y[3] = 1.23;
	// y[4] = 3.24;
	//
	// long then = System.currentTimeMillis();
	// final int SIZE = 5;
	// final int DEGREE = 5;
	// double matA_data[][] = new double[SIZE][DEGREE + 1];
	//
	// for (int i = 0; i < SIZE; ++i) {
	// double val = 1;
	// for (int j = 0; j < DEGREE + 1; ++j) {
	// matA_data[i][j] = val;
	// val *= x[i];
	// }
	// }
	//
	// Matrix matA = new Matrix(matA_data);
	//
	// QRDecomposition qr = new QRDecomposition(matA);
	//
	// // solved has many rows as the degree, plus one, and one column.
	// Matrix solved = qr.solve(new Matrix(y, SIZE));
	//
	// // pf.fit(x, y);
	// long delta = System.currentTimeMillis() - then;
	// System.out.println("solved in " + delta);
	//
	// // System.out.println(pf.A);
	// //System.out.println(pf.coef);
	// // System.out.println(pf.y);
	// // System.out.println(pf.solver);
	//
	// // // I have to add a point to the solver, removing the first.
	// // then = System.currentTimeMillis();
	// // pf.addObservationFIFO(5, 4.12);
	// // System.out.println("updated in " + delta);
	// //
	// // System.out.println("&&&&&&&&&&&&&&&&&& after the fifo");
	// // // System.out.println(pf.A);
	// // System.out.println(pf.coef);
	// // // System.out.println(pf.y);
	// // // System.out.println(pf.solver);
	//
	// }

	// /**
	// * Adds this observation to the end of the observations removing the
	// first,
	// * using the FIFO <i>first in, first out</i> concept.
	// *
	// * @param x
	// * the x of the sample
	// * @param y
	// * the y of the sample
	// */
	// public void addObservationFIFO(double xObs, double yObs) {
	//
	// /*
	// * I have to shift the y observation to make room for the new
	// * observation
	// */
	// double data[] = y.data;
	// for (int i = 0; i < y.numRows - 1; ++i) {
	// data[i] = data[i + 1];
	// }
	// data[y.numRows - 1] = yObs;
	//
	// /*
	// * delete the first row from A
	// */
	// if (!solver.removeRowFromA(0)) {
	// throw new RuntimeException("cannot remore first row");
	// }
	//
	// double[] newRow = new double[coef.numRows];
	//
	// double factor = 1;
	// for (int i = 0; i < newRow.length; ++i) {
	// newRow[i] = factor;
	// factor *= xObs;
	// }
	//
	// if (!solver.addRowToA(newRow, y.numRows - 1)) {
	// throw new RuntimeException("Cannot add the row at the end");
	// }
	//
	// // solver the the coefficients
	// solver.solve(y, coef);
	//
	// }
	//
	// // Vandermonde matrix
	// DenseMatrix64F A;
	// // matrix containing computed polynomial coefficients
	// DenseMatrix64F coef;
	// // observation matrix
	// DenseMatrix64F y;
	//
	// // solver used to compute
	// AdjustableLinearSolver solver;

	// /**
	// * Constructor.
	// *
	// * @param degree
	// * The polynomial's degree which is to be fit to the
	// * observations.
	// */
	// public PolynomialFit(int degree) {
	// coef = new DenseMatrix64F(degree + 1, 1);
	// A = new DenseMatrix64F(1, degree + 1);
	// y = new DenseMatrix64F(1, 1);
	//
	// // create a solver that allows elements to be added or removed
	// // efficiently
	// solver = LinearSolverFactory.adjustable();
	// }

	// /**
	// * Returns the computed coefficients
	// *
	// * @return polynomial coefficients that best fit the data.
	// */
	// public double[] getCoef() {
	// return coef.data;
	// }

	// /**
	// * Computes the best fit set of polynomial coefficients to the provided
	// * observations.
	// *
	// * @param samplePoints
	// * where the observations were sampled.
	// * @param observations
	// * A set of observations.
	// */
	// public void fit(double samplePoints[], double[] observations) {
	// // Create a copy of the observations and put it into a matrix
	// y.reshape(observations.length, 1, false);
	// System.arraycopy(observations, 0, y.data, 0, observations.length);
	//
	// // reshape the matrix to avoid unnecessarily declaring new memory
	// // save values is set to false since its old values don't matter
	// A.reshape(y.numRows, coef.numRows, false);
	//
	// // set up the A matrix
	// for (int i = 0; i < observations.length; i++) {
	//
	// double obs = 1;
	//
	// for (int j = 0; j < coef.numRows; j++) {
	// A.set(i, j, obs);
	// obs *= samplePoints[i];
	// }
	// }
	//
	// // process the A matrix and see if it failed
	// if (!solver.setA(A))
	// throw new RuntimeException("Solver failed");
	//
	// // solver the the coefficients
	// solver.solve(y, coef);
	// }

	// /**
	// * Removes the observation that fits the model the worst and recomputes
	// the
	// * coefficients. This is done efficiently by using an adjustable solver.
	// * Often times the elements with the largest errors are outliers and not
	// * part of the system being modeled. By removing them a more accurate set
	// of
	// * coefficients can be computed.
	// */
	// public void removeWorstFit() {
	// // find the observation with the most error
	// int worstIndex = -1;
	// double worstError = -1;
	//
	// for (int i = 0; i < y.numRows; i++) {
	// double predictedObs = 0;
	//
	// for (int j = 0; j < coef.numRows; j++) {
	// predictedObs += A.get(i, j) * coef.get(j, 0);
	// }
	//
	// double error = Math.abs(predictedObs - y.get(i, 0));
	//
	// if (error > worstError) {
	// worstError = error;
	// worstIndex = i;
	// }
	// }
	//
	// // nothing left to remove, so just return
	// if (worstIndex == -1)
	// return;
	//
	// // remove that observation
	// removeObservation(worstIndex);
	//
	// // update A
	// solver.removeRowFromA(worstIndex);
	//
	// // solve for the parameters again
	// solver.solve(y, coef);
	// }

	// /**
	// * Removes an element from the observation matrix.
	// *
	// * @param index
	// * which element is to be removed
	// */
	// private void removeObservation(int index) {
	// final int N = y.numRows - 1;
	// final double d[] = y.data;
	//
	// // shift
	// for (int i = index; i < N; i++) {
	// d[i] = d[i + 1];
	// }
	// y.numRows--;
	// }
}
