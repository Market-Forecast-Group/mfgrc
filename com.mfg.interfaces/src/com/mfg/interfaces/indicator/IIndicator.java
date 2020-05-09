package com.mfg.interfaces.indicator;

import java.awt.Point;

import com.mfg.dm.ITickListener;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;

/**
 * This interface has the functionalities of the channel widget (and other
 * widgets which are pivot based).
 * 
 * Some of these methods are useful for the channel widget... and they work also
 * if the widget is frozen.
 * 
 * @author Enrique Matos, gianni, gio
 */
public interface IIndicator extends ITickListener {

	public void begin(int tick);

	/**
	 * 
	 * @param level
	 * @return true if the channel is going up.
	 */
	public boolean getChisgoingup(int level);

	/**
	 * 
	 * @return the number of scales.
	 */
	public int getChscalelevels();

	/**
	 * 
	 * @param level
	 * @return the channel slope
	 */
	public double getChslope(int level);

	/**
	 * @param chlevel
	 *            Channel Level It starts from level 1 up to max level.
	 * @return the current price of the bottom regression line.
	 */
	public double getCurrentBottomRegressionPrice(int level);

	/**
	 * @param chlevel
	 *            Channel Level It starts from level 1 up to max level.
	 * @return the current price of the mid regression line.
	 */
	public double getCurrentCenterRegressionPrice(int level);

	/**
	 * 
	 * @param level
	 *            the scale which you want to query (1 based)
	 * @return the pivots count at a particular level.
	 */
	public int getCurrentPivotsCount(int level);

	/**
	 * this is EXACT, so it is int. The other prices are "computed", so they can
	 * be double
	 */
	public int getCurrentPrice();

	/**
	 * gets the current number of RC touches for a particular level.
	 * 
	 * @param level
	 *            level (the level used to get the number of RC touches,
	 *            1-based)
	 * 
	 * @return the current number of RC touches (this number may not correspond
	 *         to the number of positive channels created).
	 */
	public int getCurrentRCTouches(int level);

	/**
	 * gets the currenct number of SC touches for a particular level.
	 * 
	 * @param level
	 *            (the level used to get the number of SC touches, 1-based)
	 * 
	 * @return the current number of SC touches (this number may not correspond
	 *         to the number of positive channels created).
	 */
	public int getCurrentSCTouches(int level);

	/**
	 * returns the current tentative pivot, of a certain level. A tentative
	 * pivot is only useful to draw a line (<i>zigzag</i>) between the last
	 * confirmed pivot and the tentative pivot.
	 * 
	 * @param level
	 *            the level (1 based) from which we want to know the tentative
	 *            pivot.
	 * @return the point which represents the current tentative pivot. The Point
	 *         class is <b>not</b> the Point class in the indicator. This is
	 *         normal Point class in awt package. It is with integer precision
	 *         because we don't need floats here, they are exact prices. The
	 *         Point is created on the fly, you can modify it. It can return
	 *         null if the indicator is frozen because in this case it is not in
	 *         real time (the tentative pivot is only useful for the chart to
	 *         visualize it).
	 */
	Point getCurrentTentativePivot(int level);

	public long getCurrentTime();

	/**
	 * @param chlevel
	 *            Channel Level It starts from level 1 up to max level.
	 * @return the current price of the top regression line.
	 */
	public double getCurrentTopRegressionPrice(int level);

	/**
	 * gets the fake time for a particular physical time. The physical time need
	 * not to be perfectly equal to a saved tick, the first one which ha a time
	 * less than this time is returned.
	 * 
	 * <p>
	 * <i>technical note</i> Probably this method has to be moved because it is
	 * not really pertinent to the {@link IIndicator} interface.
	 * 
	 * 
	 * @param physicalTime
	 *            the physical time to search for.
	 * 
	 * @param exactMatch
	 *            true if you want an exact match, this is usually true only if
	 *            the strategy is connected to an historical data source.
	 * 
	 * @return the fake time of the first tick which has a physical time lesser
	 *         than the parameter.
	 * 
	 */
	int getFakeTimeFor(long physicalTime, boolean exactMatch);

	public long getHHPrice(int level);

	public int getHHTime(int level);

	/**
	 * @param steps
	 *            0 = last confirmed pivot (which starts the current swing), -1
	 *            = start pivot of the last <i>complete</i> swing (which will
	 *            end at pivot 0).
	 * @param level
	 *            1 = scale 1, 2 = scale 2 ...
	 * @return the pivot at step k and level j
	 */
	public Pivot getLastPivot(int steps, int level);

	public long getLLPrice(int level);

	public int getLLTime(int level);

	public AbstractIndicatorParamBean getParamBean();

	public int getStartScaleLevelWidget();

	public boolean isLevelInformationPresent(int level);

	public boolean isSwingDown(int level);

	public boolean isThereANewPivot(int level);

	/**
	 * returns true if at the current tick has been a new RC touch
	 * 
	 * @param level
	 *            the level (1-based)
	 * 
	 * @return true if there has been a RC touch
	 */
	boolean isThereANewRC(int level);

	/**
	 * returns true if at the current tick has been a new SC touch
	 * 
	 * @param level
	 *            the level (1-based)
	 * 
	 * @return true if there has been a SC touch
	 */
	boolean isThereANewSC(int level);

	public void setParamBean(AbstractIndicatorParamBean parameters);

	/**
	 * Returns true if there has been a new tentative pivot
	 * 
	 * @param level
	 * @return true if there has been a new tentative pivot (in the last tick).
	 */
	boolean isThereANewTentativePivot(int level);

	/**
	 * returns the new threshold for a level, it changes when there is a new
	 * tentative pivot.
	 * 
	 * @param aLevel
	 * @return
	 */
	int getConfirmThreshold(int aLevel);

	/**
	 * get a stats for a particular level.
	 * 
	 * @param aLevel
	 * @return the stats are returned as an array of 4 doubles.
	 */
	double[] getStatsForLevel(int aLevel);

}
