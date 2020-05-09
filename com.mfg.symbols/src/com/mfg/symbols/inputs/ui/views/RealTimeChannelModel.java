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
package com.mfg.symbols.inputs.ui.views;

import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChannel2Collection;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IRealTimeChannelModel;
import com.mfg.inputdb.indicator.mdb.Channel2MDB.Record;
import com.mfg.symbols.ui.chart.models.Channel2Collection;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.strategy.IndicatorAdaptator;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class RealTimeChannelModel implements IRealTimeChannelModel {

	final Channel[] channels;
	protected final IChartModel _chartModel;
	private final LayeredIndicator _layeredIndicator;
	private final int _degree;

	public RealTimeChannelModel(LayeredIndicator layeredIndicator,
			final int level, ChartModel_MDB chartModel) {
		this._chartModel = chartModel;
		this._layeredIndicator = layeredIndicator;
		_degree = _layeredIndicator.getParamBean()
				.getIndicator_centerLineAlgo().getDegree();

		int layersCount = layeredIndicator.getLayers().size();

		channels = new Channel[layersCount];

		for (int i = 0; i < layersCount; i++) {
			final int layer = i;
			MultiscaleIndicator indicator = layeredIndicator.getLayers().get(i);
			indicator.addIndicatorListener(new IndicatorAdaptator() {
				@Override
				public void newRealTimeChannel(Channel newChannel) {
					if (newChannel.getLevel() == level) {
						channels[layer] = newChannel;
					}
				}

				@Override
				public void newStartedChannel(Channel channel) {
					if (channel.getLevel() == level) {
						channels[layer] = channel;
					}
				}
			});
		}

	}

	protected MultiscaleIndicator getCurrentIndicatorLayer() {
		return _layeredIndicator.getLayers().get(
				_chartModel.getDataLayerModel().getDataLayer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getStartTime()
	 */
	@Override
	public long getStartTime(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getCenterLine().getStart().getTime();
		return (long) channel.getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getStartTopPrice()
	 */
	@Override
	public double getStartTopPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getTopLine().getStart().getPrice();
		return channel.getTopY1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getStartCenterPrice()
	 */
	@Override
	public double getStartCenterPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getCenterLine().getStart().getPrice();
		return channel.getCenterY1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getStartBottomPrice()
	 */
	@Override
	public double getStartBottomPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getBottomLine().getStart().getPrice();
		return channel.getBottomY1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getEndTime()
	 */
	@Override
	public long getEndTime(int dataLayer) {
		Channel channel = channels[dataLayer];
		return (long) channel.getEnd();
	}

	@Override
	public IChannel2Collection getChannel(int dataLayer) {
		Channel channel = channels[dataLayer];

		if (channel != null) {
			Record r = new Record();
			r.startTime = (long) channel.getStart();
			r.endTime = (long) channel.getEnd();
			double[] params = channel.getChannelCoefficients();
			r.c0 = params[0];
			r.c1 = params[1];
			if (_degree > 1) {
				r.c2 = params[2];
				if (_degree > 2) {
					r.c3 = params[3];
					if (_degree > 3) {
						r.c4 = params[4];
					}
				}
			}
			r.topDistance = channel.getTopY1() - channel.getCenterY1();
			r.bottomDistance = channel.getCenterY1() - channel.getBottomY1();
			return new Channel2Collection(new Record[] { r }, _degree);
		}

		return IChannel2Collection.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getEndTopPrice()
	 */
	@Override
	public double getEndTopPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getTopLine().getEnd().getPrice();
		return channel.getTopY2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getEndCenterPrice()
	 */
	@Override
	public double getEndCenterPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getCenterLine().getEnd().getPrice();
		return channel.getCenterY2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#getEndBottomPrice()
	 */
	@Override
	public double getEndBottomPrice(int dataLayer) {
		Channel channel = channels[dataLayer];
		// return channel.getBottomLine().getEnd().getPrice();
		return channel.getBottomY2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeChannelModel#isComputed()
	 */
	@Override
	public boolean isComputed(int dataLayer) {
		Channel channel = channels[dataLayer];
		return channel != null;
	}

}
