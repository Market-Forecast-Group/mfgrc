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

package com.mfg.strategy.automatic.triggers;

public class SlopeTrigger extends ScaleSpecificTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private ComparationOperator slopeOperator;
	private double slopeCutPoint;
	private double infiniteSlope;


	@Override
	protected boolean internalIsActive() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * checks the slope logic.
	 * <p>
	 * to be implemented.
	 * 
	 * @return {@code true} if the slope trigger is on at this price tick.
	 */
	public boolean slopeLogic() {
		double slope = (fWidget.getChslope(fWidgetScale) * 3) / infiniteSlope;
		boolean currentEval = slopeOperator.compare(slope, slopeCutPoint);
		return currentEval;
	}


	/**
	 * @return the slopeOperator
	 */
	public ComparationOperator getSlopeOperator() {
		return slopeOperator;
	}


	/**
	 * @param aSlopeOperator
	 *            the slopeOperator to set
	 */
	public void setSlopeOperator(ComparationOperator aSlopeOperator) {
		slopeOperator = aSlopeOperator;
	}


	/**
	 * @return the slopeCutPoint
	 */
	public double getSlopeCutPoint() {
		return slopeCutPoint;
	}


	/**
	 * @param aSlopeCutPoint
	 *            the slopeCutPoint to set
	 */
	public void setSlopeCutPoint(double aSlopeCutPoint) {
		slopeCutPoint = aSlopeCutPoint;
	}


	/**
	 * @return the infiniteSlope
	 */
	public double getInfiniteSlope() {
		return infiniteSlope;
	}


	/**
	 * @param aInfiniteSlope
	 *            the infiniteSlope to set
	 */
	public void setInfiniteSlope(double aInfiniteSlope) {
		infiniteSlope = aInfiniteSlope;
	}
}
