package com.mfg.widget.arc.data;

/**
 * 
 * @author utente
 */
public class PointRegressionLine implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = -7364737661487003781L;

	private long time;
	private int level;
	private double pricetop;
	private double pricecenter;
	private double pricebottom;

	private double _rawBottom;

	private double _rawCenter;

	private double _rawTop;

	// private double price;

	@Override
	public PointRegressionLine clone() {
		try {
			// bitcopy sufficient
			PointRegressionLine prl = (PointRegressionLine) super.clone();
			return prl;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * 
	 * @return the given regression line level.
	 */
	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level1) {
		this.level = level1;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time1) {
		this.time = time1;
	}

	// public void setPrice(double price1) {
	// this.price = price1;
	// }

	// public double getPrice() {
	// return this.price;
	// }

	public double getPriceTop() {
		return pricetop;
	}

	public void setPriceTop(double price1) {
		this.pricetop = price1;
	}

	public double getPriceCenter() {
		return pricecenter;
	}

	public void setPriceCenter(double price1) {
		this.pricecenter = price1;
	}

	public double getPriceBottom() {
		return pricebottom;
	}

	public void setPriceBottom(double price1) {
		this.pricebottom = price1;
	}

	// public PointRegressionLine copy_dep() {
	// PointRegressionLine point = new PointRegressionLine();
	// point.setLevel(level);
	// point.setPriceBottom(pricebottom);
	// point.setPriceCenter(pricecenter);
	// point.setPriceTop(pricetop);
	// point.setTime(time);
	// return point;
	// }

	@Override
	public String toString() {
		return "PointRegressionLine [level=" + level + ", pricebottom="
				+ pricebottom + ", pricecenter=" + pricecenter + ", pricetop="
				+ pricetop + ", time=" + time + "]";
	}

	/**
	 * sets the unadjusted values of the indicator.
	 * <p>
	 * The unadjusted values can cross the prices and are the raw output of the
	 * indicator, before any adjustment
	 * 
	 * <p>
	 * No check is done on the validity of these values, in particular the
	 * object does not check whether bottom is below center is below top.
	 * 
	 * @param aRawBottom
	 *            a bottom value
	 * @param aRawCenter
	 *            a center value
	 * @param aRawTop
	 *            a top value
	 */
	public void setUnadjustedIndicator(double aRawBottom, double aRawCenter,
			double aRawTop) {
		_rawBottom = aRawBottom;
		_rawCenter = aRawCenter;
		_rawTop = aRawTop;
	}

	public double getRawBottom() {
		return _rawBottom;
	}

	public double getRawTop() {
		return _rawTop;
	}

	public double getRawCenter() {
		return _rawCenter;
	}

}
