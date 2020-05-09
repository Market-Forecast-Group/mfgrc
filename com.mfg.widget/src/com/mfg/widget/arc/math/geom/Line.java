package com.mfg.widget.arc.math.geom;

import java.io.Serializable;

public class Line implements Serializable {

	private static final long serialVersionUID = -8869735624231087142L;
	private Point startpoint;
	private Point endpoint;

	public Line() {
	}

	public Point getStart() {
		return startpoint;
	}

	public Point getEnd() {
		return endpoint;
	}

	/**
	 * sets the start point of this line, without cloning, this is useful in the
	 * usual idiom: <code>setBeginPoint(new Point...)</code> with a thrown away
	 * object. If this method is called with an external visible object you may
	 * alter the Line violating encapsulation.
	 * 
	 * @param point
	 *            the point to set. It will <b>not</b> be copied.
	 */
	public void setBeginPoint(Point point) {
		this.startpoint = point;
	}

	/**
	 * sets the end point of this line, without cloning.
	 * 
	 * @see #setBeginPoint(Point) setBeginPoint for details
	 * 
	 * @param point
	 *            the point to set. It will <b>not</b> be copied.
	 */
	public void setEndPoint(Point point) {
		this.endpoint = point;
	}

}
