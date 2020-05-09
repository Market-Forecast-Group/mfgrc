/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

/**
 * a three values answer.
 * <p> Possible values are:
 * <ul>
 * <li> {@code YES} for affirmative answers.
 * <li> {@code NO} for negative answers.
 * <li> {@code WHATEVER} for answers that can be affirmative and negative at
 * the same time.
 * </ul> 
 * @author gardero.
 */
public enum MY_BOOLEAN {
	/**
	 * for affirmative answers.
	 */
    YES,
    /**
     * for negative answers.
     */
    NO,
    /**
     * for answers that can be affirmative and negative at
     * the same time.
     */
    WHATEVER;

    /**
     * asks if this value is not negative.
     * @return <code>this != NO</code>
     */
    public boolean isItTrue() {
        return this != NO;
    }

    /**
     * asks if this value is not affirmative.
     * @return <code>this != YES</code>
     */
    public boolean isItFalse() {
        return this != YES;
    }
}
