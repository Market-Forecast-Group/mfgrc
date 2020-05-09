package com.mfg.widget.arc.strategy;

import java.io.IOException;

import com.mfg.widget.arc.math.geom.PolyEvaluator;

/**
 * The generic interface for a <i>free hand</i> indicator, which is an indicator
 * usually interactive drawn on the screen by the user.
 * 
 * <p>
 * A free hand indicator has usually these characteristics:
 * <li>It has only one layer and one scale
 * <li>The left point is fixed as soon as the concrete class is created, to move
 * the left point simply create another indicator with the left point modified
 * <li>The indicator can also be asynchronous, in the sense that the computation
 * is done on a different thread
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IFreehandIndicator {

	/**
	 * Sets the right anchor.
	 * 
	 * <p>
	 * It is an error to set the right anchor before the left anchor.
	 * 
	 * <p>
	 * There is not a method to set the left anchor, it is fixed, just create
	 * another indicator with the left anchor modified.
	 * 
	 * <p>
	 * The calling of this method is <b>blocking</b> until the indicator has
	 * updated itself.
	 * 
	 * @param aAnchor
	 *            the point (inclusive, as fake time) used to fix the right
	 *            limit of the indicator.
	 * @throws IOException
	 */
	public void setRightAnchor(int aAnchor) throws IOException;

	/**
	 * returns the most updated coefficients for the central line as a
	 * polynomial, the coefficients are returned from the lowest degree to the
	 * highest, so a parabola y=ax^+bx+c will be returned as [c,b,a]
	 * 
	 * <p>
	 * This is the same order used by the {@link PolyEvaluator} class to evalate
	 * the polynomial at a certain point in time.
	 * 
	 * <p>
	 * if for some reasons there are not enough points to compute the indicator
	 * null is returned
	 * 
	 * @return the center line coefficients
	 */
	public double[] getCenterLineCoefficients();

	/**
	 * gets the indicator's value at the left anchor.
	 * 
	 * <p>
	 * The value could be the left anchor's price, but it can also be different.
	 * 
	 * @return the value of the indicator at the left anchor fake time.
	 */
	public double getLeftAnchorIndValue();

	/**
	 * return the top distance of this indicator.
	 * 
	 * <p>
	 * This defines the upper half channel.
	 * 
	 * @return the top distance as a positive number
	 */
	public double getTopDistance();

	/**
	 * returns the bottom distance of this indicator
	 * 
	 * <p>
	 * This defines the bottom half channel.
	 * 
	 * @return the bottom distance as a positive number.
	 */
	public double getBottomDistance();

	/**
	 * 
	 * @return true if the top polyine is touching the price at the right
	 *         anchor.
	 */
	public boolean isTopTouching();

	/**
	 * 
	 * @return true if the bottom polyine is touching the price at the right
	 *         anchor.
	 */
	public boolean isBottomTouching();

	/**
	 * Get the last right anchor set to this indicator.
	 * 
	 * @return The last right anchor
	 */
	public int getLastRightAnchor();

	/**
	 * returns the minimum fake time when the top indicator touches the prices.
	 * 
	 * <p>
	 * This time is after the right anchor.
	 * 
	 * <p>
	 * If there is no touch a {@link Integer#MAX_VALUE} is returned.
	 * 
	 * @return
	 */
	public int getMinimumTopTouch();

	/**
	 * returns the minimum fake time when the bottom indicator touches the
	 * prices.
	 * 
	 * <p>
	 * This time is after the right anchor.
	 * 
	 * <p>
	 * If there is no touch a {@link Integer#MAX_VALUE} is returned.
	 * 
	 * @return
	 */
	public int getMinimumBottomTouch();

	/**
	 * 
	 * returns the maximum top distance at right of the right anchor until the
	 * last time
	 * 
	 * <p>
	 * This is the <b>delta</b> distance which is needed to add to the
	 * {@link #getTopDistance()} distance to make the indicator not touch the
	 * prices also after the right anchor
	 * 
	 * <p>
	 * A value of zero means that the top distance returned from
	 * {@link #getTopDistance()} is enough
	 * 
	 * @return the maximum top distance
	 */
	public double getGlobalTopDistanceRight();

	/**
	 * 
	 * returns the maximum bottom distance at right of the right anchor until
	 * the last time
	 * 
	 * 
	 * <p>
	 * This is the <b>delta</b> distance which is needed to add to the
	 * {@link #getBottomDistance()} distance to make the indicator not touch the
	 * prices also after the right anchor
	 * 
	 * <p>
	 * A value of zero means that the top distance returned from
	 * {@link #getBottomDistance()} is enough
	 * 
	 * @return the maximum bottom distance
	 */
	public double getGlobalBottomDistanceRight();

}
