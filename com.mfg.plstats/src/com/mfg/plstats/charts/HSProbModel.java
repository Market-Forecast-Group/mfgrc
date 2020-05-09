/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.plstats.charts;

import java.util.ArrayList;

import com.mfg.chart.model.IHSProbsModel;
import com.mfg.widget.probabilities.HSTargetInfo;
import com.mfg.widget.probabilities.SimpleLogMessage.KeyLogMessage;

/**
 * @author arian
 * 
 */
public class HSProbModel implements IHSProbsModel {

	private final KeyLogMessage logMsg;
	private final ArrayList<HSTargetInfo> higherScales;

	public HSProbModel(KeyLogMessage aLogMsg) {
		super();
		this.logMsg = aLogMsg;
		higherScales = aLogMsg.getVisitedTargetPrices();
		// TODO: mostrar los higgerScales con el color de la escala
		// higherScales.get(0).getKey().getScale()
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbsModel#getScale(int)
	 */
	@Override
	public int getScale(int square) {
		return square < 2 ? logMsg.getScale() : higherScales.get(square - 2)
				.getKey().getScale();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbModel#getSquaresCount()
	 */
	@Override
	public int getSquaresCount() {
		return 2 + higherScales.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbModel#getTime0(int)
	 */
	@Override
	public long getTime0(int square) {
		switch (square) {
		case 0:
			// [**getPm1Time()**,getPm1Price()]-[getP0Time(),getP0Price()]
			return logMsg.getPm1Time();
		case 1:
			// [**getP0Time()**,getP0Price()] - [getTime(), getTargetPrice()]
			return logMsg.getP0Time();
		default:
			// [**getP0Time()**,getP0Price()]--[getTime(),hs[i].getPrice()]
			return higherScales.get(square - 2).getPivotTime();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbModel#getPrice0(int)
	 */
	@Override
	public double getPrice0(int square) {
		switch (square) {
		case 0:
			// [getPm1Time(), **getPm1Price()**]-[getP0Time(),getP0Price()]
			return logMsg.getPm1Price();
		case 1:
			// [getP0Time(),**getP0Price()**] - [getTime(), getTargetPrice()]
			return logMsg.getP0Price();
		default:
			// [getP0Time(),**getP0Price()**]--[getTime(),hs[i].getPrice()]
			return higherScales.get(square - 2).getPivotPrice();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbModel#getTime1(int)
	 */
	@Override
	public long getTime1(int square) {
		switch (square) {
		case 0:
			// [getPm1Time(),getPm1Price()]-[**getP0Time()**,getP0Price()]
			return logMsg.getP0Time();
		case 1:
			// [getP0Time(),getP0Price()] - [**getTime()**, getTargetPrice()]
			return logMsg.getTime();
		default:
			// [getP0Time(),getP0Price()]--[**getTime()**,hs[i].getPrice()]
			return logMsg.getTime();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IHSProbModel#getPrice1(int)
	 */
	@Override
	public double getPrice1(int square) {
		switch (square) {
		case 0:
			// [getPm1Time(),getPm1Price()]-[getP0Time(),**getP0Price()**]
			return logMsg.getP0Price();
		case 1:
			// [getP0Time(),getP0Price()] - [getTime(), **getTargetPrice()**]
			return logMsg.getTargetPrice();
		default:
			// [getP0Time(),getP0Price()]--[getTime(),**hs[i].getPrice()**]
			return higherScales.get(square - 2).getPrice();
		}
	}

}
