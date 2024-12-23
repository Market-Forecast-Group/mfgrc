package com.mfg.utils;

/**
 * This class solves a polynomial.
 * 
 * This is a java translation of the c++ translation of the fortran translation
 * 
 * The sub-routines listed below are translations of the FORTRAN routines
 * included in RPOLY.FOR, // posted off the NETLIB site as TOMS/493: // //
 * http://www.netlib.org/toms/493 // // TOMS/493 is based on the Jenkins-Traub
 * algorithm. *
 * 
 */
public class PolySolver {

	private static final int MAXDEGREE = 100;
	private static final int MDP1 = MAXDEGREE + 1;

	private static final double DBL_MIN = Double.MIN_VALUE;
	/*
	 * this epsilon has been computed, and it has been verified on this machine
	 * that 1 + eps != 1
	 */
	private static double DBL_EPSILON = 2.220446049250313E-16;
	private static final double DBL_MAX = Double.MAX_VALUE;

	// private static final int MAX_ITERATIONS = 20;

	public static void main(String args[]) {

		/*
		 * let's try a simple quadratic equation 4 x ^ 2 - 34 x + 66 which has
		 * real solutions 3 and 5.5
		 */

		double p[];

		double zeror[] = new double[100];
		double zeroi[] = new double[100];
		//
		@SuppressWarnings("unused")
		int res;

		p = new double[] { 4, -34, 66 };
		res = rpoly_ak1(p, zeror, zeroi);
		checkDblEqual(zeror[0], 3.0);
		checkDblEqual(zeror[1], 5.5);

		p = new double[] { 5, 2, 4, 5, 6 };

		res = rpoly_ak1(p, zeror, zeroi);

		checkDblEqual(0.5077707916227718, zeror[0]);
		checkDblEqual(0.5077707916227718, zeror[1]);
		checkDblEqual(-0.7077707916227718, zeror[2]);
		checkDblEqual(-0.7077707916227718, zeror[3]);

		checkDblEqual(1.0431687518447692, zeroi[0]);
		checkDblEqual(-1.0431687518447692, zeroi[1]);
		checkDblEqual(0.6249556988092582, zeroi[2]);
		checkDblEqual(-0.6249556988092582, zeroi[3]);

		// print_roots(zeror, zeroi, res);
		p = new double[] { 7, 34, 33, -1, 5, 27 };
		res = rpoly_ak1(p, zeror, zeroi);

		checkDblEqual(0.49813344212384064, zeror[0]);
		checkDblEqual(0.49813344212384064, zeror[1]);
		checkDblEqual(-1.1706340537590494, zeror[2]);
		checkDblEqual(-1.1706340537590494, zeror[3]);
		checkDblEqual(-3.5121416338724396, zeror[4]);

		checkDblEqual(0.642282288868043, zeroi[0]);
		checkDblEqual(-0.642282288868043, zeroi[1]);
		checkDblEqual(0.5403066263870704, zeroi[2]);
		checkDblEqual(-0.5403066263870704, zeroi[3]);
		checkDblEqual(0, zeroi[4]);

		// print_roots(zeror, zeroi, res);

		p = new double[] { -0.5244755245412083, 1035.3729605028288,
				-681307.2095427993, 149438929.88893226 };
		res = rpoly_ak1(p, zeror, zeroi);
		// print_roots(zeror, zeroi, res);

		checkDblEqual(655.9947993510084, zeror[0]);
		checkDblEqual(656.0000102611339, zeror[1]);
		checkDblEqual(662.1163014993527, zeror[2]);

		checkDblEqual(0, zeroi[0]);
		checkDblEqual(0, zeroi[1]);
		checkDblEqual(0, zeroi[2]);

		p = new double[] { -0.03191057999121513, 94.28154352820253,
				-92849.9548483224, 30478944.561009333 };
		res = rpoly_ak1(p, zeror, zeroi);

		checkDblEqual(978.8533936197484, zeror[0]);
		checkDblEqual(979.0000396189039, zeror[1]);
		checkDblEqual(996.7009356934668, zeror[2]);

		checkDblEqual(0, zeroi[0]);
		checkDblEqual(0, zeroi[1]);
		checkDblEqual(0, zeroi[2]);

		// print_roots(zeror, zeroi, res);

		p = new double[] { -0.43706293159025633, 1289.1899605047624,
				-1267546.021340423, 415417197.5541373 };
		res = rpoly_ak1(p, zeror, zeroi);

		checkDblEqual(979.8639477775505, zeror[0]);
		checkDblEqual(980.0000349404787, zeror[1]);
		checkDblEqual(989.8026838510995, zeror[2]);

		checkDblEqual(0, zeroi[0]);
		checkDblEqual(0, zeroi[1]);
		checkDblEqual(0, zeroi[2]);

		// print_roots(zeror, zeroi, res);

		System.out.println("ALL ok.");
	}

	private static void checkDblEqual(double d1, double d2) {

		if (d1 == 0) {
			if (Math.abs(d2) > 1e-6) {
				throw new RuntimeException(" d1 " + d1 + " != d2 " + d2);
			}
		}
		if ((Math.abs(d1 - d2) / Math.abs(d1)) > 1e-6) {
			throw new RuntimeException(" d1 " + d1 + " != d2 " + d2);
		}

	}

	@SuppressWarnings("unused")
	private static void print_roots(double[] zeror, double[] zeroi, int res) {
		for (int i = 0; i < res; ++i) {
			System.out.println((i + 1) + " root is: " + zeror[i] + " + "
					+ zeroi[i] + "  i");
		}

	}

	/**
	 * The solver method.
	 * 
	 * <p>
	 * The solver method communicates with the outside with arrays, it is not so
	 * object oriented but it is a direct translation from the c++ code, which
	 * was already a translation from fortran... so this is it :)
	 * 
	 * @param op
	 *            the original polynomial, it has n+1 coefficients, where n is
	 *            the degree of the polynomial. They are ordered from the
	 *            highest coefficient to the lowest. In Java the length of the
	 *            array is an attribute of the array, so the degree is simply
	 *            the length minus one.
	 * 
	 * @param zeror
	 *            the real part of the roots found. The array should at least be
	 *            long as op minus one.
	 * 
	 * @param zeroi
	 *            the imaginary part of the roots found.
	 * 
	 * 
	 * @return the number of roots found. If -1 something is wrong. If zero the
	 *         highest coefficient was zero.
	 * 
	 */
	public static int rpoly_ak1(double op[], double zeror[], double zeroi[]) {

		final int Degree = op.length - 1;

		/*
		 * This stores the number of roots that I find.
		 */
		int answerDegree = Degree;

		int i, j, jj, l, N, NM1, NN, zerok;

		// double K[MDP1], p[MDP1], pt[MDP1], qp[MDP1], temp[MDP1];
		double K[] = new double[MDP1];
		double p[] = new double[MDP1];
		double pt[] = new double[MDP1];
		double qp[] = new double[MDP1];
		double temp[] = new double[MDP1];

		double bnd, df, dx, factor, ff, moduli_max, moduli_min, sc, x, xm;
		double aa, bb, cc, /* lzi, lzr, */sr, /* szi, szr, */t, u, xx, xxx, yy;

		Fxshfr_par fxshPar = new Fxshfr_par();

		final double RADFAC = 3.14159265358979323846 / 180;
		// Degrees-to-radians
		// conversion factor
		// = pi/180

		final double lb2 = Math.log(2.0); // Dummy variable to avoid
		// re-calculating this value in loop
		// below

		final double lo = DBL_MIN / DBL_EPSILON;

		final double cosr = Math.cos(94.0 * RADFAC); // = -0.069756474
		final double sinr = Math.sin(94.0 * RADFAC); // = 0.99756405

		if ((Degree) > MAXDEGREE) {
			U.debug_var(342463,
					"The entered Degree is greater than MAXDEGREE. Exiting rpoly.");
			return -1;
		} // End ((*Degree) > MAXDEGREE)

		// Do a quick check to see if leading coefficient is 0
		if (op[0] != 0) {

			N = Degree;
			xx = Math.sqrt(0.5); // = 0.70710678
			yy = -xx;

			// Remove zeros at the origin, if any
			j = 0;
			while (op[N] == 0) {
				zeror[j] = zeroi[j] = 0.0;
				N--;
				j++;
			} // End while (op[N] == 0)

			NN = N + 1;

			// Make a copy of the coefficients
			for (i = 0; i < NN; i++)
				p[i] = op[i];

			while (N >= 1) { // Main loop
				// Start the algorithm for one zero
				if (N <= 2) {
					// Calculate the final zero or pair of zeros
					if (N < 2) {
						zeror[Degree - 1] = -(p[1] / p[0]);
						zeroi[Degree - 1] = 0.0;
					} // End if (N < 2)
					else { // else N == 2
							// Quad_ak1(p[0], p[1], p[2], &zeror[(*Degree) - 2],
							// &zeroi[(*Degree) - 2], &zeror[(*Degree) - 1],
							// &zeroi[(*Degree) - 1]);

						Quad_ak1Par par = new Quad_ak1Par();
						Quad_ak1(p[0], p[1], p[2], par);
						zeror[Degree - 2] = par.sr;
						zeroi[Degree - 2] = par.si;
						zeror[Degree - 1] = par.lr;
						zeroi[Degree - 1] = par.li;

					} // End else N == 2
					break;
				} // End if (N <= 2)

				// Find the largest and smallest moduli of the coefficients

				moduli_max = 0.0;
				moduli_min = DBL_MAX;

				for (i = 0; i < NN; i++) {
					x = Math.abs(p[i]);
					if (x > moduli_max)
						moduli_max = x;
					if ((x != 0) && (x < moduli_min))
						moduli_min = x;
				} // End for i

				// Scale if there are large or very small coefficients
				// Computes a scale factor to multiply the coefficients of the
				// polynomial. The scaling
				// is done to avoid overflow and to avoid undetected underflow
				// interfering with the
				// convergence criterion.
				// The factor is a power of the base.

				sc = lo / moduli_min;

				if (((sc <= 1.0) && (moduli_max >= 10))
						|| ((sc > 1.0) && (DBL_MAX / sc >= moduli_max))) {
					sc = ((sc == 0) ? DBL_MIN : sc);
					l = (int) Math.floor((Math.log(sc) / lb2 + 0.5));
					factor = Math.pow(2.0, l);
					if (factor != 1.0) {
						for (i = 0; i < NN; i++)
							p[i] *= factor;
					} // End if (factor != 1.0)
				} // End if (((sc <= 1.0) && (moduli_max >= 10)) || ((sc > 1.0)
					// && (DBL_MAX/sc >= moduli_max)))

				// Compute lower bound on moduli of zeros

				for (i = 0; i < NN; i++)
					pt[i] = Math.abs(p[i]);
				pt[N] = -(pt[N]);

				NM1 = N - 1;

				// Compute upper estimate of bound

				x = Math.exp((Math.log(-pt[N]) - Math.log(pt[0])) / N);

				if (pt[NM1] != 0) {
					// If Newton step at the origin is better, use it
					xm = -pt[N] / pt[NM1];
					x = ((xm < x) ? xm : x);
				} // End if (pt[NM1] != 0)

				// Chop the interval (0, x) until ff <= 0

				xm = x;
				do {
					x = xm;
					xm = 0.1 * x;
					ff = pt[0];
					for (i = 1; i < NN; i++)
						ff = ff * xm + pt[i];
				} while (ff > 0); // End do-while loop

				dx = x;

				// Do Newton iteration until x converges to two decimal places

				do {
					df = ff = pt[0];
					for (i = 1; i < N; i++) {
						ff = x * ff + pt[i];
						df = x * df + ff;
					} // End for i
					ff = x * ff + pt[N];
					dx = ff / df;
					x -= dx;
				} while (Math.abs(dx / x) > 0.005); // End do-while loop

				bnd = x;

				// Compute the derivative as the initial K polynomial and do 5
				// steps with no shift

				for (i = 1; i < N; i++)
					K[i] = (N - i) * p[i] / (N);
				K[0] = p[0];

				aa = p[N];
				bb = p[NM1];
				zerok = ((K[NM1] == 0) ? 1 : 0);

				for (jj = 0; jj < 5; jj++) {
					cc = K[NM1];
					if (zerok != 0) {
						// Use unscaled form of recurrence
						for (i = 0; i < NM1; i++) {
							j = NM1 - i;
							K[j] = K[j - 1];
						} // End for i
						K[0] = 0;
						zerok = ((K[NM1] == 0) ? 1 : 0);
					} // End if (zerok)

					else { // else !zerok
							// Used scaled form of recurrence if value of K at 0
							// is nonzero
						t = -aa / cc;
						for (i = 0; i < NM1; i++) {
							j = NM1 - i;
							K[j] = t * K[j - 1] + p[j];
						} // End for i
						K[0] = p[0];
						zerok = ((Math.abs(K[NM1]) <= Math.abs(bb)
								* DBL_EPSILON * 10.0) ? 1 : 0);
					} // End else !zerok

				} // End for jj

				// Save K for restarts with new shifts
				for (i = 0; i < N; i++)
					temp[i] = K[i];

				// Loop to select the quadratic corresponding to each new shift

				for (jj = 1; jj <= 20; jj++) {

					// Quadratic corresponds to a double shift to a non-real
					// point and its
					// complex conjugate. The point has modulus BND and
					// amplitude rotated
					// by 94 degrees from the previous shift.

					xxx = -(sinr * yy) + cosr * xx;
					yy = sinr * xx + cosr * yy;
					xx = xxx;
					sr = bnd * xx;
					u = -(2.0 * sr);

					// Second stage calculation, fixed quadratic

					Fxshfr_ak1(20 * jj, sr, bnd, K, N, p, NN, qp, u, fxshPar);

					if (fxshPar.NZ != 0) {

						// The second stage jumps directly to one of the third
						// stage iterations and
						// returns here if successful. Deflate the polynomial,
						// store the zero or
						// zeros, and return to the main algorithm.

						j = Degree - N;
						zeror[j] = fxshPar.szr;
						zeroi[j] = fxshPar.szi;
						NN = NN - fxshPar.NZ;
						N = NN - 1;
						for (i = 0; i < NN; i++)
							p[i] = qp[i];
						if (fxshPar.NZ != 1) {
							zeror[j + 1] = fxshPar.lzr;
							zeroi[j + 1] = fxshPar.lzi;
						} // End if (NZ != 1)
						break;
					} // End if (NZ != 0)
						// else { // Else (NZ == 0)

					/*
					 * This else has been moved out, because the if breaks
					 * always.
					 */

					// If the iteration is unsuccessful, another quadratic
					// is chosen after restoring K
					for (i = 0; i < N; i++)
						K[i] = temp[i];
					// } // End else (NZ == 0)

				} // End for jj

				// Return with failure if no convergence with 20 shifts

				if (jj > 20) {
					answerDegree -= N;
					// U.debug_var(382384, "Failure. No convergence after ",
					// MAX_ITERATIONS, " shifts returning ", answerDegree,
					// " solutions");
					break;
				} // End if (jj > 20)

			} // End while (N >= 1)

		} // End if op[0] != 0
		else { // else op[0] == 0
			U.debug_var(278334,
					"The leading coefficient is zero. No further action taken.");
			return 0;
		} // End else op[0] == 0

		return answerDegree;
	} // End rpoly_ak1

	private static final class Quad_ak1Par {
		public Quad_ak1Par() {
			// unused
		}

		double sr;
		double si;
		double lr;
		double li;
	}

	/**
	 * 
	 * @param a
	 * @param b1
	 * @param c
	 * @param mypar
	 *            to have some values by reference. The two roots are returned
	 *            there.
	 */
	static void Quad_ak1(double a, double b1, double c, Quad_ak1Par mypar) {
		// Calculates the zeros of the quadratic a*Z^2 + b1*Z + c
		// The quadratic formula, modified to avoid overflow, is used to find
		// the larger zero if the
		// zeros are real and both zeros are complex. The smaller real zero is
		// found directly from
		// the product of the zeros c/a.

		double b, d, e;

		/*
		 * Java initializes the parameters to zero by default.
		 */
		// _quad_sr = 0.0;
		// _quad_si = 0.0;
		// _quad_lr = 0.0;
		// _quad_li = 0.0;

		// *sr = *si = *lr = *li = 0.0;

		if (a == 0) {
			mypar.sr = ((b1 != 0) ? -(c / b1) : mypar.sr);
			return;
		} // End if (a == 0))

		if (c == 0) {
			mypar.lr = -(b1 / a);
			return;
		} // End if (c == 0)

		// Compute discriminant avoiding overflow

		b = b1 / 2.0;
		if (Math.abs(b) < Math.abs(c)) {
			e = ((c >= 0) ? a : -a);
			e = -e + b * (b / Math.abs(c));
			d = Math.sqrt(Math.abs(e)) * Math.sqrt(Math.abs(c));
		} // End if (Math.abs(b) < Math.abs(c))
		else { // Else (Math.abs(b) >= Math.abs(c))
			e = -((a / b) * (c / b)) + 1.0;
			d = Math.sqrt(Math.abs(e)) * (Math.abs(b));
		} // End else (Math.abs(b) >= Math.abs(c))

		if (e >= 0) {
			// Real zeros

			d = ((b >= 0) ? -d : d);
			mypar.lr = (-b + d) / a;
			mypar.sr = ((mypar.lr != 0) ? (c / (mypar.lr)) / a : mypar.lr);
		} // End if (e >= 0)
		else { // Else (e < 0)
				// Complex conjugate zeros

			mypar.lr = mypar.sr = -(b / a);
			mypar.si = Math.abs(d / a);
			mypar.li = -(mypar.si);
		} // End else (e < 0)

		return;
	} // End Quad_ak1

	private static final class Fxshfr_par {
		public Fxshfr_par() {
			// unused
		}

		int NZ;
		double lzi;
		double lzr;
		double szi;
		double szr;
	}

	static void Fxshfr_ak1(int L2, double sr, double v, double K[], int N,
			double p[], int NN, double qp[], double u, Fxshfr_par iPar) {

		// Computes up to L2 fixed shift K-polynomials, testing for convergence
		// in the linear or
		// quadratic case. Initiates one of the variable shift iterations and
		// returns with the
		// number of zeros found.

		// L2 limit of fixed shift steps
		// NZ number of zeros found

		int fflag, i, iFlag = 1, j, spass, stry, tFlag, vpass, vtry;
		double a, /* a1, a3, a7, */b, betas, betav, /* c, d, e, f, g, h, */oss, ots = 0, otv = 0, ovv, s, ss, ts, tss, tv, tvv, ui, vi, vv;
		double qk[] = new double[MDP1];
		double svk[] = new double[MDP1];

		iPar.NZ = 0;
		betav = betas = 0.25;
		oss = sr;
		ovv = v;

		QuadSD_Par sdPar = new QuadSD_Par();
		calcSCPar calcPar = new calcSCPar();

		// Evaluate polynomial by synthetic division
		sdPar.b = sdPar.a = 0.0;
		QuadSD_ak1(NN, u, v, p, qp, sdPar);
		a = sdPar.a;
		b = sdPar.b;
		calcPar.h = calcPar.g = calcPar.f = calcPar.e = calcPar.d = calcPar.c = calcPar.a7 = calcPar.a3 = calcPar.a1 = 0.0;
		tFlag = calcSC_ak1(N, a, b, calcPar, K, u, v, qk);

		for (j = 0; j < L2; j++) {

			fflag = 1;
			// Calculate next K polynomial and estimate v
			nextK_ak1(N, tFlag, a, b, calcPar, K, qk, qp);
			tFlag = calcSC_ak1(N, a, b, calcPar, K, u, v, qk);
			newest_ak1(tFlag, sdPar, a, calcPar.a1, calcPar.a3, calcPar.a7, b,
					calcPar.c, calcPar.d, calcPar.f, calcPar.g, calcPar.h, u,
					v, K, N, p);

			ui = sdPar.a;
			vv = vi = sdPar.b;

			// vv = vi;

			// Estimate s

			ss = ((K[N - 1] != 0.0) ? -(p[N] / K[N - 1]) : 0.0);

			ts = tv = 1.0;

			if ((j != 0) && (tFlag != 3)) {

				// Compute relative measures of convergence of s and v sequences

				tv = ((vv != 0.0) ? Math.abs((vv - ovv) / vv) : tv);
				ts = ((ss != 0.0) ? Math.abs((ss - oss) / ss) : ts);

				// If decreasing, multiply the two most recent convergence
				// measures

				tvv = ((tv < otv) ? tv * otv : 1.0);
				tss = ((ts < ots) ? ts * ots : 1.0);

				// Compare with convergence criteria

				vpass = ((tvv < betav) ? 1 : 0);
				spass = ((tss < betas) ? 1 : 0);

				if ((spass != 0) || (vpass != 0)) {

					// At least one sequence has passed the convergence test.
					// Store variables before iterating

					for (i = 0; i < N; i++)
						svk[i] = K[i];

					s = ss;

					// Choose iteration according to the fastest converging
					// sequence

					stry = vtry = 0;

					for (;;) {

						if (((fflag != 0) && ((fflag = 0) == 0))
								&& ((spass != 0) && (!(vpass != 0) || (tss < tvv)))) {
							// ; // Do nothing. Provides a quick
							// "short circuit".
						} // End if (fflag)

						else { // else !fflag
							QuadIT_ak1(N, iPar, ui, vi, qp, NN, sdPar, p, qk,
									calcPar, K);

							a = sdPar.a;
							b = sdPar.b;

							if ((iPar.NZ) > 0)
								return;

							// Quadratic iteration has failed. Flag that it has
							// been tried and decrease the
							// convergence criterion

							iFlag = vtry = 1;
							betav *= 0.25;

							// Try linear iteration if it has not been tried and
							// the s sequence is converging
							if (stry != 0 || (!(spass != 0))) {
								iFlag = 0;
							} // End if (stry || (!spass))
							else {
								for (i = 0; i < N; i++)
									K[i] = svk[i];
							} // End if (stry || !spass)

						} // End else fflag

						if (iFlag != 0) {
							// Use sdPar for passing in s instead of defining a
							// brand-new variable.
							// sdPar.a = s

							sdPar.a = s;
							iFlag = RealIT_ak1(iPar, sdPar, N, p, NN, qp, K, qk);
							s = sdPar.a;

							if ((iPar.NZ) > 0)
								return;

							// Linear iteration has failed. Flag that it has
							// been tried and decrease the
							// convergence criterion

							stry = 1;
							betas *= 0.25;

							if (iFlag != 0) {

								// If linear iteration signals an almost double
								// real zero, attempt quadratic iteration

								ui = -(s + s);
								vi = s * s;
								continue;

							} // End if (iFlag != 0)
						} // End if (iFlag != 0)

						// Restore variables
						for (i = 0; i < N; i++)
							K[i] = svk[i];

						// Try quadratic iteration if it has not been tried and
						// the v sequence is converging

						if (!(vpass != 0) || (vtry != 0))
							break; // Break out of infinite for loop

					} // End infinite for loop

					// Re-compute qp and scalar values to continue the second
					// stage

					QuadSD_ak1(NN, u, v, p, qp, sdPar);
					a = sdPar.a;
					b = sdPar.b;
					tFlag = calcSC_ak1(N, a, b, calcPar, K, u, v, qk);

				} // End if ((spass) || (vpass))

			} // End if ((j != 0) && (tFlag != 3))

			ovv = vv;
			oss = ss;
			otv = tv;
			ots = ts;
		} // End for j

		return;
	} // End Fxshfr_ak1

	private static final class calcSCPar {
		public calcSCPar() {
			//
		}

		double a1;
		double a3;
		double a7;
		double c;
		double d;
		double e;
		double f;
		double g;
		double h;
	}

	static int calcSC_ak1(int N, double a, double b, calcSCPar calcPar,
			double K[], double u, double v, double qk[]) {

		// This routine calculates scalar quantities used to compute the next K
		// polynomial and
		// new estimates of the quadratic coefficients.

		// calcSC - integer variable set here indicating how the calculations
		// are normalized
		// to avoid overflow.

		int dumFlag = 3; // TYPE = 3 indicates the quadratic is almost a factor
							// of K

		QuadSD_Par sdPar = new QuadSD_Par();

		// Synthetic division of K by the quadratic 1, u, v
		QuadSD_ak1(N, u, v, K, qk, sdPar);
		calcPar.c = sdPar.a;
		calcPar.d = sdPar.b;

		if (Math.abs((calcPar.c)) <= (100.0 * DBL_EPSILON * Math.abs(K[N - 1]))) {
			if (Math.abs((calcPar.d)) <= (100.0 * DBL_EPSILON * Math
					.abs(K[N - 2])))
				return dumFlag;
		} // End if (Math.abs(c) <= (100.0*DBL_EPSILON*Math.abs(K[N - 1])))

		calcPar.h = v * b;
		if (Math.abs((calcPar.d)) >= Math.abs((calcPar.c))) {
			dumFlag = 2; // TYPE = 2 indicates that all formulas are divided by
							// d
			calcPar.e = a / (calcPar.d);
			calcPar.f = (calcPar.c) / (calcPar.d);
			calcPar.g = u * b;
			calcPar.a3 = (calcPar.e) * ((calcPar.g) + a) + (calcPar.h)
					* (b / (calcPar.d));
			calcPar.a1 = -a + (calcPar.f) * b;
			calcPar.a7 = (calcPar.h) + ((calcPar.f) + u) * a;
		} // End if(Math.abs(d) >= Math.abs(c))
		else {
			dumFlag = 1; // TYPE = 1 indicates that all formulas are divided by
							// c;
			calcPar.e = a / (calcPar.c);
			calcPar.f = (calcPar.d) / (calcPar.c);
			calcPar.g = (calcPar.e) * u;
			calcPar.a3 = (calcPar.e) * a
					+ ((calcPar.g) + (calcPar.h) / (calcPar.c)) * b;
			calcPar.a1 = -(a * ((calcPar.d) / (calcPar.c))) + b;
			calcPar.a7 = (calcPar.g) * (calcPar.d) + (calcPar.h) * (calcPar.f)
					+ a;
		} // End else

		return dumFlag;
	} // End calcSC_ak1

	private static final class QuadSD_Par {
		public QuadSD_Par() {
			// empty
		}

		double a;
		double b;
	}

	static void QuadSD_ak1(int NN, double u, double v, double p[], double q[],
			QuadSD_Par myPar) {

		// Divides p by the quadratic 1, u, v placing the quotient in q and the
		// remainder in a, b

		int i;

		q[0] = myPar.b = p[0];
		q[1] = myPar.a = -((myPar.b) * u) + p[1];

		for (i = 2; i < NN; i++) {
			q[i] = -((myPar.a) * u + (myPar.b) * v) + p[i];
			myPar.b = (myPar.a);
			myPar.a = q[i];
		} // End for i

		return;
	} // End QuadSD_ak1

	static void nextK_ak1(int N, int tFlag, double a, double b, calcSCPar iPar,
			double K[], double qk[], double qp[]) {

		// Computes the next K polynomials using the scalars computed in
		// calcSC_ak1

		int i;
		double temp;

		if (tFlag == 3) { // Use unscaled form of the recurrence
			K[1] = K[0] = 0.0;

			for (i = 2; i < N; i++)
				K[i] = qk[i - 2];

			return;
		} // End if (tFlag == 3)

		temp = ((tFlag == 1) ? b : a);

		if (Math.abs(iPar.a1) > (10.0 * DBL_EPSILON * Math.abs(temp))) {
			// Use scaled form of the recurrence

			(iPar.a7) /= iPar.a1;
			(iPar.a3) /= iPar.a1;
			K[0] = qp[0];
			K[1] = -((iPar.a7) * qp[0]) + qp[1];

			for (i = 2; i < N; i++)
				K[i] = -((iPar.a7) * qp[i - 1]) + (iPar.a3) * qk[i - 2] + qp[i];

		} // End if (Math.abs(a1) > (10.0*DBL_EPSILON*Math.abs(temp)))
		else {
			// If a1 is nearly zero, then use a special form of the recurrence

			K[0] = 0.0;
			K[1] = -(iPar.a7) * qp[0];

			for (i = 2; i < N; i++)
				K[i] = -((iPar.a7) * qp[i - 1]) + (iPar.a3) * qk[i - 2];
		} // End else

		return;

	} // End nextK_ak1

	static void newest_ak1(int tFlag, QuadSD_Par iPar, double a, double a1,
			double a3, double a7, double b, double c, double d, double f,
			double g, double h, double u, double v, double K[], int N,
			double p[]) {
		// Compute new estimates of the quadratic coefficients using the scalars
		// computed in calcSC_ak1

		double a4, a5, b1, b2, c1, c2, c3, c4, temp;

		iPar.a = 0;
		iPar.b = 0;

		// (*vv) = (*uu) = 0.0; // The quadratic is zeroed

		if (tFlag != 3) {

			if (tFlag != 2) {
				a4 = a + u * b + h * f;
				a5 = c + (u + v * f) * d;
			} // End if (tFlag != 2)
			else { // else tFlag == 2
				a4 = (a + g) * f + h;
				a5 = (f + u) * c + v * d;
			} // End else tFlag == 2

			// Evaluate new quadratic coefficients

			b1 = -K[N - 1] / p[N];
			b2 = -(K[N - 2] + b1 * p[N - 1]) / p[N];
			c1 = v * b2 * a1;
			c2 = b1 * a7;
			c3 = b1 * b1 * a3;
			c4 = -(c2 + c3) + c1;
			temp = -c4 + a5 + b1 * a4;
			if (temp != 0.0) {
				iPar.a = -((u * (c3 + c2) + v * (b1 * a1 + b2 * a7)) / temp)
						+ u;
				iPar.b = v * (1.0 + c4 / temp);
			} // End if (temp != 0)

		} // End if (tFlag != 3)

		return;
	} // End newest_ak1

	static void QuadIT_ak1(int N, Fxshfr_par iPar, double uu, double vv,
			double qp[], int NN, QuadSD_Par sdPar, double p[], double qk[],
			calcSCPar calcPar, double K[]) {

		// Variable-shift K-polynomial iteration for a quadratic factor
		// converges only if the
		// zeros are equimodular or nearly so.

		// iPar is a dummy variable for passing in the five parameters--NZ, lzi,
		// lzr, szi, and szr--by reference
		// sdPar is a dummy variable for passing the two parameters--a and b--in
		// by reference
		// calcPar is a dummy variable for passing the nine parameters--a1, a3,
		// a7, c, d, e, f, g, and h --in by reference

		int i, j = 0, tFlag, triedFlag = 0;
		double ee, mp, omp, relstp, t, u, ui, v, vi, zm;

		/*
		 * Dummy initialization to shut up the compiler, but they are
		 * initialized after.
		 */
		relstp = 99;
		omp = 998;

		iPar.NZ = 0; // Number of zeros found
		u = uu; // uu and vv are coefficients of the starting quadratic
		v = vv;

		Quad_ak1Par qPar = new Quad_ak1Par();

		do {
			qPar.li = qPar.lr = qPar.si = qPar.sr = 0.0;
			Quad_ak1(1.0, u, v, qPar);

			iPar.szr = qPar.sr;
			iPar.szi = qPar.si;
			iPar.lzr = qPar.lr;
			iPar.lzi = qPar.li;

			// Return if roots of the quadratic are real and not close to
			// multiple or nearly
			// equal and of opposite sign.

			if (Math.abs(Math.abs(iPar.szr) - Math.abs(iPar.lzr)) > 0.01 * Math
					.abs(iPar.lzr))
				break;

			// Evaluate polynomial by quadratic synthetic division

			QuadSD_ak1(NN, u, v, p, qp, sdPar);

			mp = Math.abs(-((iPar.szr) * (sdPar.b)) + (sdPar.a))
					+ Math.abs((iPar.szi) * (sdPar.b));

			// Compute a rigorous bound on the rounding error in evaluating p

			zm = Math.sqrt(Math.abs(v));
			ee = 2.0 * Math.abs(qp[0]);
			t = -((iPar.szr) * (sdPar.b));

			for (i = 1; i < N; i++)
				ee = ee * zm + Math.abs(qp[i]);

			ee = ee * zm + Math.abs((sdPar.a) + t);
			ee = (9.0 * ee + 2.0 * Math.abs(t) - 7.0 * (Math.abs((sdPar.a) + t) + zm
					* Math.abs((sdPar.b))))
					* DBL_EPSILON;

			// Iteration has converged sufficiently if the polynomial value is
			// less than 20 times this bound

			if (mp <= 20.0 * ee) {
				iPar.NZ = 2;
				break;
			} // End if (mp <= 20.0*ee)

			j++;

			// Stop iteration after 20 steps
			if (j > 20)
				break;

			if (j >= 2) {
				if ((relstp <= 0.01) && (mp >= omp) && (!(triedFlag != 0))) {
					// A cluster appears to be stalling the convergence. Five
					// fixed shift
					// steps are taken with a u, v close to the cluster.

					relstp = ((relstp < DBL_EPSILON) ? Math.sqrt(DBL_EPSILON)
							: Math.sqrt(relstp));

					u -= u * relstp;
					v += v * relstp;

					QuadSD_ak1(NN, u, v, p, qp, sdPar);

					for (i = 0; i < 5; i++) {
						tFlag = calcSC_ak1(N, sdPar.a, sdPar.b, calcPar, K, u,
								v, qk);
						nextK_ak1(N, tFlag, sdPar.a, sdPar.b, calcPar, K, qk,
								qp);
					} // End for i

					triedFlag = 1;
					j = 0;

				} // End if ((relstp <= 0.01) && (mp >= omp) && (!triedFlag))

			} // End if (j >= 2)

			omp = mp;

			// Calculate next K polynomial and new u and v

			tFlag = calcSC_ak1(N, sdPar.a, sdPar.b, calcPar, K, u, v, qk);
			nextK_ak1(N, tFlag, sdPar.a, sdPar.b, calcPar, K, qk, qp);
			tFlag = calcSC_ak1(N, sdPar.a, sdPar.b, calcPar, K, u, v, qk);
			newest_ak1(tFlag, sdPar, sdPar.a, calcPar.a1, calcPar.a3,
					calcPar.a7, sdPar.b, calcPar.c, calcPar.d, calcPar.f,
					calcPar.g, calcPar.h, u, v, K, N, p);

			ui = sdPar.a;
			vi = sdPar.b;

			// If vi is zero, the iteration is not converging
			if (vi != 0) {
				relstp = Math.abs((-v + vi) / vi);
				u = ui;
				v = vi;
			} // End if (vi != 0)
		} while (vi != 0); // End do-while loop

		return;

	} // End QuadIT_ak1

	static int RealIT_ak1(Fxshfr_par iPar, QuadSD_Par sdPar, int N, double p[],
			int NN, double qp[], double K[], double qk[]) {

		// Variable-shift H-polynomial iteration for a real zero

		// sss - starting iterate
		// NZ - number of zeros found
		// iFlag - flag to indicate a pair of zeros near real axis

		// sss - starting iterate = sdPar.a
		// NZ - number of zeros found = iPar.NZ
		// dumFlag - flag to indicate a pair of zeros near real axis, returned
		// to iFlag

		int dumFlag, i, j = 0, nm1 = N - 1;
		double ee, kv, mp, ms, omp, pv, s, t;

		/*
		 * Just to quit the compiler because they are initialized
		 */
		t = 239;
		omp = 991;

		iPar.NZ = j = dumFlag = 0;

		// *iFlag = *NZ = 0;
		s = sdPar.a;

		for (;;) {
			pv = p[0];

			// Evaluate p at s
			qp[0] = pv;
			for (i = 1; i < NN; i++)
				qp[i] = pv = pv * s + p[i];

			mp = Math.abs(pv);

			// Compute a rigorous bound on the error in evaluating p

			ms = Math.abs(s);
			ee = 0.5 * Math.abs(qp[0]);
			for (i = 1; i < NN; i++)
				ee = ee * ms + Math.abs(qp[i]);

			// Iteration has converged sufficiently if the polynomial value is
			// less than
			// 20 times this bound

			if (mp <= 20.0 * DBL_EPSILON * (2.0 * ee - mp)) {
				iPar.NZ = 1;
				iPar.szr = s;
				iPar.szi = 0.0;
				break;
			} // End if (mp <= 20.0*DBL_EPSILON*(2.0*ee - mp))

			j++;

			// Stop iteration after 10 steps

			if (j > 10)
				break;

			if (j >= 2) {
				if ((Math.abs(t) <= 0.001 * Math.abs(-t + s)) && (mp > omp)) {
					// A cluster of zeros near the real axis has been
					// encountered;
					// Return with iFlag set to initiate a quadratic iteration

					dumFlag = 1;

					/*
					 * CHECK HERE... in the code there is iPar, but I think it
					 * is sdPar...
					 */

					// sdPar.a = s;

					break;
				} // End if ((Math.abs(t) <= 0.001*Math.abs(s - t)) && (mp >
					// omp))

			} // End if (j >= 2)

			// Return if the polynomial value has increased significantly

			omp = mp;

			// Compute t, the next polynomial and the new iterate
			qk[0] = kv = K[0];
			for (i = 1; i < N; i++)
				qk[i] = kv = kv * s + K[i];

			if (Math.abs(kv) > Math.abs(K[nm1]) * 10.0 * DBL_EPSILON) {
				// Use the scaled form of the recurrence if the value of K at s
				// is non-zero
				t = -(pv / kv);
				K[0] = qp[0];
				for (i = 1; i < N; i++)
					K[i] = t * qk[i - 1] + qp[i];
			} // End if (Math.abs(kv) > Math.abs(K[nm1])*10.0*DBL_EPSILON)
			else { // else (Math.abs(kv) <= Math.abs(K[nm1])*10.0*DBL_EPSILON)
					// Use unscaled form
				K[0] = 0.0;
				for (i = 1; i < N; i++)
					K[i] = qk[i - 1];
			} // End else (Math.abs(kv) <= Math.abs(K[nm1])*10.0*DBL_EPSILON)

			kv = K[0];
			for (i = 1; i < N; i++)
				kv = kv * s + K[i];

			t = ((Math.abs(kv) > (Math.abs(K[nm1]) * 10.0 * DBL_EPSILON)) ? -(pv / kv)
					: 0.0);

			s += t;

		} // End infinite for loop

		return dumFlag;

	} // End RealIT_ak1

}
