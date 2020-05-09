package com.mfg.widget.arc.math.geom;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * A simple class which exposes the protected method which evaluates the
 * polynomial without making a copy of the arguments.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class PolyEvaluator extends PolynomialFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7780111399252904267L;

	/**
	 * Cannot build a poly evaluator
	 * 
	 * @param c
	 * @throws NullArgumentException
	 * @throws NoDataException
	 */
	public PolyEvaluator(double[] c) throws NullArgumentException,
			NoDataException {
		super(c);
		throw new UnsupportedOperationException();
	}

	/**
	 * Evaluates the polynomial without making a copy
	 * 
	 * @see PolynomialFunction#evaluate(double[], double)
	 * 
	 * @param coefficients
	 * @param argument
	 * @return
	 */
	public static double evaluate(double[] coefficients, double argument) {
		return PolynomialFunction.evaluate(coefficients, argument);
	}

}
