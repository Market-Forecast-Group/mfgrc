/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */
/**
 * 
 */

package com.mfg.widget.recorders;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;

import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.model.mdb.recorders.AbstractChartRecorder;
import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.ChannelMDB;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class IndicatorRecorder extends AbstractChartRecorder {
	private final LayeredIndicator _indicator;
	private final BandsMDB.Appender[] _bandsAppMap;
	private final PivotMDB.Appender[] _pivotsAppMap;
	private final ChannelMDB.Appender[] _channelAppMap;
	private final Channel2MDB.Appender[] _channel2AppMap;
	// [level][direction][type]
	// private final ProbabilityPointMDB.Appender[][][] probPointAppMap;
	private final int _levelCount;

	private final int _layer;
	private final MultiscaleIndicator _layerIndicator;
	private final int _polylineDegree;

	public IndicatorRecorder(LayeredIndicator indicator,
			IndicatorMDBSession session, int layer) throws IOException {
		super(session);
		this._layer = layer;
		AbstractIndicatorParamBean paramBean = indicator.getParamBean();
		int scalesCount = paramBean.getIndicatorNumberOfScales();
		session.setScalesCount(scalesCount);
		session.saveProperties();

		this._indicator = indicator;
		_layerIndicator = indicator.getLayers().get(layer);

		_levelCount = scalesCount;
		_bandsAppMap = new BandsMDB.Appender[_levelCount + 1];
		_pivotsAppMap = new PivotMDB.Appender[_levelCount + 1];
		_channelAppMap = new ChannelMDB.Appender[_levelCount + 1];
		_channel2AppMap = new Channel2MDB.Appender[_levelCount + 1];

		for (int level = 1; level <= _levelCount; level++) {
			if (session.getMode() != SessionMode.MEMORY) {
				new File(getRootDir(), "layer-" + layer + "/" + level).mkdirs();
				// this is needed to probabilities
				new File(getRootDir(), Integer.toString(level)).mkdirs();
			}

			_bandsAppMap[level] = session.connectTo_BandsMDB(layer, level)
					.appender();
			_pivotsAppMap[level] = session.connectTo_PivotMDB(layer, level)
					.appender();
			_channelAppMap[level] = session.connectTo_ChannelMDB(layer, level)
					.appender();
			_channel2AppMap[level] = session
					.connectTo_Channel2MDB(layer, level).appender();
		}

		_polylineDegree = session.getPolylineDegree();
	}

	/**
	 * @return the layer
	 */
	public int getLayer() {
		return _layer;
	}

	/**
	 * @return the indicator
	 */
	public IIndicator getIndicator() {
		return _indicator;
	}

	/**
	 * @return the layerIndicator
	 */
	public MultiscaleIndicator getLayerIndicator() {
		return _layerIndicator;
	}

	public void addPivot(Pivot pivot) throws IOException {
		// if (pivot.getPivotTime() <= session.getLastSavedTime()) {
		// return;
		// }
		try {

			PivotMDB.Appender pivotApp = _pivotsAppMap[pivot.getLevel()];
			pivotApp.confirmPrice = pivot.fConfirmPrice;
			pivotApp.confirmTime = pivot.fConfirmTime;
			pivotApp.pivotTime = pivot.fPivotTime;
			pivotApp.pivotPrice = pivot.fPivotPrice;
			pivotApp.timeInterval = pivot.getTimeInterval();
			pivotApp.confirmTime = pivot.fConfirmTime;
			pivotApp.confirmPrice = pivot.fConfirmPrice;
			pivotApp.isUp = !pivot.isStartingDownSwing();
			pivotApp.pivotPhysicalTime = _layerIndicator
					.getPhysicalTimeAt(pivot.fPivotTime);
			pivotApp.confirmPhysicalTime = _layerIndicator
					.getPhysicalTimeAt(pivot.fConfirmTime);
			pivotApp.append();

			// addCompressedBands(pivot.getLevel(), pivotApp);

		} catch (BufferOverflowException e) {
			handleAppenderException(e);
		}
	}

	public void addChannel(Channel channel) throws IOException {
		// if (channel.getCenterLine().getStart().getTime() <= session
		// .getLastSavedTime()) {
		// return;
		// }
		// try {
		// {
		// ChannelMDB.Appender a = _channelAppMap[channel.getLevel()];
		// // a.startTime = channel.getCenterLine().getStart().getTime();
		// a.startTime = (long) channel.getStart();
		// a.startPhysicalTime = _layerIndicator
		// .getPhysicalTimeAt((int) a.startTime);
		//
		// // a.endTime = channel.getCenterLine().getEnd().getTime();
		// a.endTime = (long) channel.getEnd();
		// a.endPhysicalTime = _layerIndicator
		// .getPhysicalTimeAt((int) a.endTime);
		//
		// // a.topStartPrice = channel.getTopLine().getStart().getPrice();
		// a.topStartPrice = channel.getTopY1();
		//
		// // a.topEndPrice = channel.getTopLine().getEnd().getPrice();
		// a.topEndPrice = channel.getTopY2();
		//
		// // a.centerStartPrice =
		// // channel.getCenterLine().getStart().getPrice();
		// a.centerStartPrice = channel.getCenterY1();
		// // a.centerEndPrice =
		// // channel.getCenterLine().getEnd().getPrice();
		// a.centerEndPrice = channel.getCenterY2();
		//
		// // a.bottomStartPrice =
		// // channel.getBottomLine().getStart().getPrice();
		// a.bottomStartPrice = channel.getBottomY1();
		// // a.bottomEndPrice =
		// // channel.getBottomLine().getEnd().getPrice();
		// a.bottomEndPrice = channel.getBottomY2();
		//
		// a.slope = channel.getSlope() > 0;
		//
		// a.append();
		// }
		// } catch (BufferOverflowException e) {
		// handleAppenderException(e);
		// }

		// channel 2
		Channel2MDB.Appender a2 = _channel2AppMap[channel.getLevel()];
		a2.startTime = (long) channel.getStart();
		a2.endTime = (long) channel.getEnd();
		double[] params = channel.getChannelCoefficients();
		a2.c0 = params[0];
		a2.c1 = params[1];
		if (_polylineDegree > 1) {
			a2.c2 = params[2];
			if (_polylineDegree > 2) {
				a2.c3 = params[3];
				if (_polylineDegree > 3) {
					a2.c4 = params[4];
				}
			}
		}
		a2.topDistance = channel.getTopY1() - channel.getCenterY1();
		a2.bottomDistance = channel.getCenterY1() - channel.getBottomY1();

		a2.append();

	}

	@Override
	public IndicatorMDBSession getSession() {
		return (IndicatorMDBSession) super.getSession();
	}

	public void addBands(PointRegressionLine point) throws IOException {
		try {
			int level = point.getLevel();
			BandsMDB.Appender a = _bandsAppMap[level];
			a.time = _layerIndicator.getCurrentTime();
			a.physicalTime = _layerIndicator.getPhysicalTimeAt((int) a.time);
			a.topPrice = point.getPriceTop();
			a.centerPrice = point.getPriceCenter();
			a.bottomPrice = point.getPriceBottom();

			a.topRaw = point.getRawTop();
			a.centerRaw = point.getRawCenter();
			a.bottomRaw = point.getRawBottom();

			a.append();

		} catch (BufferOverflowException e) {
			handleAppenderException(e);
		}
	}

	public void updateBandsRecord(BandsMDB.Record rec, PointRegressionLine point) {
		rec.time = _layerIndicator.getCurrentTime();
		rec.physicalTime = _layerIndicator.getPhysicalTimeAt((int) rec.time);
		rec.topPrice = point.getPriceTop();
		rec.centerPrice = point.getPriceCenter();
		rec.bottomPrice = point.getPriceBottom();

		rec.topRaw = point.getRawTop();
		rec.centerRaw = point.getRawCenter();
		rec.bottomRaw = point.getRawBottom();
	}
}
