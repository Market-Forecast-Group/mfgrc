package com.mfg.utils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * a simple implementation of a moving average.
 * <p>Even if the moving average is for prices the class is generic enough
 * to be reused in other projects.
 * <p>The class needs N slots to work, where N is the width of the moving average
 * @author Sergio
 *
 */
public class MovingAverage implements Serializable, Cloneable {
	
	@Override
	public MovingAverage clone() {
		MovingAverage cloned;
		try {
			cloned = (MovingAverage) super.clone();
			cloned.fCursor = fCursor.clone();
			cloned.fBuffer = Arrays.copyOf(fBuffer, fBuffer.length);
			return cloned;
		} catch (CloneNotSupportedException e) {
			assert(false);
		}
		
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7658118609752645434L;

	//private final int fWidth;
	private CircularInteger fCursor;
	
	/** This is the array which stores our window*/
	private double fBuffer[];
	
	/** stores the sum of the points in the window */
	private double fSum = 0;
	
	/**
	 * builds a moving average object of a predefined window.
	 * <p>You cannot change the width later, but you could build a new moving
	 * average with a smaller window from this.
	 * 
	 * @param widthWindow the width of the moving average 
	 */
	public MovingAverage(int widthWindow){
		//fWidth = widthWindow;
		fCursor = new CircularInteger(0, widthWindow);
		fBuffer = new double[widthWindow];
		Arrays.fill(fBuffer, 0D);
	}
	
	/**
	 * adds a value to this moving average.
	 * <p> the average is meaningful only after N observations, otherwise
	 * it is underestimated
	 * 
	 * @param val
	 */
	public void addValue(double val){
		//First of all I have to remove the point
		int cur = fCursor.get();
		fSum -= fBuffer[cur];
		fBuffer[cur] = val;
		
		fSum += val;		
		fCursor.plusPlus(fBuffer.length);
	}
	
	/**
	 * 
	 * @return the average of this moving average.
	 */
	public double getAvg(){
		return fSum / fBuffer.length;
	}

	public int getWindow() {
		return fBuffer.length;
	}

}
