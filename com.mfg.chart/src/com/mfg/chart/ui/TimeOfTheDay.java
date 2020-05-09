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
package com.mfg.chart.ui;

import org.mfg.opengl.IGLConstants;

import com.mfg.chart.profiles.Profile;

public class TimeOfTheDay {
	private int hh;
	private int mm;
	private float[] color;

	public TimeOfTheDay(int hh1, int mm1, float[] color1) {
		super();
		this.hh = hh1;
		this.mm = mm1;
		this.color = color1;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color1) {
		this.color = color1;
	}

	public int getHour() {
		return hh;
	}

	public void setHour(int hh1) {
		this.hh = hh1;
	}

	public int getMinutes() {
		return mm;
	}

	public void setMinutes(int mm1) {
		this.mm = mm1;
	}

	public static TimeOfTheDay[] read(Profile profile) {
		int count = profile.getInt("timesOfTheDay.count", 0);

		TimeOfTheDay[] list = new TimeOfTheDay[count];

		for (int i = 0; i < count; i++) {
			int h = profile.getInt("timesOfTheDay." + i + ".hh", 0);
			int m = profile.getInt("timesOfTheDay." + i + ".mm", 0);
			float[] color = profile.getFloatArray("timesOfTheDay." + i
					+ ".color", IGLConstants.COLOR_MAGNETA);
			TimeOfTheDay time = new TimeOfTheDay(h, m, color);
			list[i] = time;
		}

		return list;
	}

	public static void write(Profile profile, TimeOfTheDay[] list) {
		int count = list.length;
		profile.putInt("timesOfTheDay.count", count);
		for (int i = 0; i < count; i++) {
			TimeOfTheDay time = list[i];
			profile.putInt("timesOfTheDay." + i + ".hh", time.hh);
			profile.putInt("timesOfTheDay." + i + ".mm", time.mm);
			profile.putFloatArray("timesOfTheDay." + i + ".color", time.color);
		}
	}
}