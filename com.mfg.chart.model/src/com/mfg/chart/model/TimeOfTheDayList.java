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
package com.mfg.chart.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arian
 * 
 */
public class TimeOfTheDayList implements ITimesOfTheDayCollection {
	private final List<Long> times;
	private final List<String> labels;

	public TimeOfTheDayList() {
		times = new ArrayList<>();
		labels = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return times.size();
	}

	@Override
	public long getTime(int index) {
		return times.get(index).longValue();
	}

	@Override
	public String getLabel(int index) {
		return labels.get(index);
	}

	public void add(long time, String label) {
		times.add(Long.valueOf(time));
		labels.add(label);
	}

}
