/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfg.widget.priv;

import java.util.NoSuchElementException;

import com.mfg.interfaces.indicator.IIndicator;

/**
 * 
 * @author gardero
 */
public enum StartPoint {
	P_0(0, "P0") {

		@Override
		public double getStartPoint(IIndicator aWidget,
				int aWidgetScale, TRIGGER_TYPE aType) {
			return aType.getPivotValue(aWidget, aWidgetScale, 0);
		}

		@Override
		public int getSign(IIndicator aWidget, int aWidgetScale,
				TRIGGER_TYPE aType) {
			if (aType != TRIGGER_TYPE.PRICE)
				return 1;
			return aWidget.isSwingDown(aWidgetScale) ? -1 : 1;
		}

	},
	P_m1(-1, "P-1") {

		@Override
		public double getStartPoint(IIndicator aWidget,
				int aWidgetScale, TRIGGER_TYPE aType) {
			return aType.getPivotValue(aWidget, aWidgetScale, -1);
		}

		@Override
		public int getSign(IIndicator aWidget, int aWidgetScale,
				TRIGGER_TYPE aType) {
			if (aType != TRIGGER_TYPE.PRICE)
				return 1;
			return aWidget.isSwingDown(aWidgetScale) ? 1 : -1;
		}

	},
	HHLL(0, "HH/LL") {

		@Override
		public int getSign(IIndicator aWidget, int aWidgetScale,
				TRIGGER_TYPE aType) {
			if (aType != TRIGGER_TYPE.PRICE)
				return 1;
			return aWidget.isSwingDown(aWidgetScale) ? 1 : -1;
		}

		@Override
		public double getStartPoint(IIndicator aWidget,
				int aWidgetScale, TRIGGER_TYPE aType) {
			return aType.getPivotValue(aWidget, aWidgetScale, 1);
		}

		@Override
		public int getIndex() {
			throw new NoSuchElementException("HH or LL does not have index...");
		}
	};

	private String s;

	StartPoint(int index1, String s1) {
		this.index = index1;
		this.s = s1;
	}

	private int index;

	public int getIndex() {
		return index;
	}

	public int getDelay() {
		return -index;
	}

	@Override
	public String toString() {
		return s;
	}

	public abstract double getStartPoint(IIndicator aWidget,
			int aWidgetScale, TRIGGER_TYPE type);

	public abstract int getSign(IIndicator aWidget,
			int aWidgetScale, TRIGGER_TYPE type);
}
