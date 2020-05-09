package com.mfg.widget.arc.math.geom;

import java.io.Serializable;

public class Point implements Serializable, Cloneable {

	private static final long serialVersionUID = -7951059625776542460L;
	private double price;
	private long time;

	@Override
	public Point clone() {
		try {
			Point pt = (Point) super.clone();
			return pt;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Point() {
	}

	public Point(double aX, double aY) {
		setTime((long) aX);
		setPrice(aY);
	}

	public Point(long aTime, double aPrice) {
		setTime(aTime);
		setPrice(aPrice);
	}

	public void setPrice(double p) {
		price = p;
	}

	public void setTime(long t) {
		time = t;
	}

	public double getPrice() {
		return price;
	}

	public long getTime() {
		return time;
	}

	public double getX() {
		return time;
	}

	public double getY() {
		return price;
	}

	@Override
	public String toString() {
		return "(" + getTime() + "," + getPrice() + ")";
	}
}
