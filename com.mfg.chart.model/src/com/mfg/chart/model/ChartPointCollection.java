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
package com.mfg.chart.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arian
 * 
 */
public class ChartPointCollection implements ITimePriceCollection {

	private final List<ChartPoint> _list;

	public ChartPointCollection(double... xypairs) {
		_list = new ArrayList<>();
		for (int i = 0; i < xypairs.length / 2; i++) {
			_list.add(new ChartPoint(xypairs[i * 2], xypairs[i * 2 + 1]));
		}
	}

	public ChartPointCollection(List<ChartPoint> list1) {
		this._list = list1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IItemCollection#getSize()
	 */
	@Override
	public int getSize() {
		return _list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.ITimePriceCollection#getTime(int)
	 */
	@Override
	public long getTime(int index) {
		return (long) _list.get(index).x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.ITimePriceCollection#getPrice(int)
	 */
	@Override
	public double getPrice(int index) {
		return _list.get(index).y;
	}

}
