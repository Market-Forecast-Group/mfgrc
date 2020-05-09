
package com.mfg.strategy.automatic.triggers;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.widget.priv.TRIGGER_TYPE;

/**
 * enum with options for the reference swing used to compute the swing<sub>0</sub> ratio.
 * <p>
 * Possible values are:<br>
 * <ul>
 * <li> {@code Swing0P} the swing<sub>0</sub>' associated value.
 * <li> {@code SwingM1} the swing<sub>-1</sub> associated value.
 * <li> {@code SwingM2} the swing<sub>-1</sub> associated value.
 * <li> {@code SwingM2Plus} selects swing<sub>-2</sub> if swing<sub>-1</sub> is too small ( swing<sub>-1</sub> < 0.5*swing<sub>-2</sub>) otherwise it
 * uses swing<sub>-1</sub>.
 * </ul>
 * 
 * @author gardero
 * 
 */
public enum Swing0RatioRefSwing {
	Swing0P("Swing0'") {
		@Override
		public double getSwing(IIndicator widget, int widgetScale, TRIGGER_TYPE type) {
			return type.getTHSegment(widget, widgetScale, 0);
		}


		@Override
		public int getDelay() {
			return 0;
		}
	},
	SwingM1("Swing-1") {
		@Override
		public double getSwing(IIndicator widget, int widgetScale, TRIGGER_TYPE type) {
			return type.getSwingValue(widget, widgetScale, -1);
		}


		@Override
		public int getDelay() {
			return 1;
		}
	},
	SwingM2("Swing-2") {
		@Override
		public double getSwing(IIndicator widget, int widgetScale, TRIGGER_TYPE type) {
			return type.getSwingValue(widget, widgetScale, -2);
		}
	},
	SwingM2Plus("Swing-2 +") {
		@Override
		public double getSwing(IIndicator widget, int widgetScale, TRIGGER_TYPE type) {
			double s1 = SwingM1.getSwing(widget, widgetScale, type);
			double s2 = SwingM2.getSwing(widget, widgetScale, type);
			return (s1 < 0.5 * s2) ? s2 : s1;
		}
	};

	private String s;


	private Swing0RatioRefSwing(String aS) {
		s = aS;
	}


	@Override
	public String toString() {
		return s;
	}


	public String getDescription() {
		return "Swing0/" + toString();
	}


	/**
	 * gets the corresponding value according to the reference swing, the scale and the type of the value we are interested in.
	 * 
	 * @param widget
	 *            the widget with information about pivots.
	 * @param widgetScale
	 *            the scale we are interested in.
	 * @param type
	 *            the type of the values, {@code TIME}, {@code PRICE}, and {@code VECTOR}.
	 * @return the associated value according to the parameters and this enum value.
	 */
	public abstract double getSwing(IIndicator widget, int widgetScale, TRIGGER_TYPE type);


	/**
	 * gets the pivots delay we need to access the swing values
	 * 
	 * @return the number of pivots we need to skip.
	 */
	@SuppressWarnings("static-method")//It's overwritten in this class.
	public int getDelay() {
		return 2;
	}

}
