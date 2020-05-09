package com.mfg.widget.arc.math.geom;

/**
 * Channel data structure class. In a channel there are three straight lines
 * respectively named top, center and bottom.
 * 
 */
public class Channel extends PolyChannel {

	private static final long serialVersionUID = -7829518316322667187L;

	/**
	 * builds a straight channel. A straight channel is a {@link PolyChannel}
	 * with 1 as a degree.
	 * 
	 * @param start
	 * @param end
	 * @param topDistance
	 * @param bottomDistance
	 * @param coeff
	 */
	public Channel(int aLevel, double start, double end, double topDistance,
			double bottomDistance, double coefficients[]) {
		super(aLevel, start, end, topDistance, bottomDistance, coefficients);
	}

}
