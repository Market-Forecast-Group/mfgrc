
package com.mfg.interfaces.probabilities;

/**
 * represents TH position relative to which a ratios sequence of consecutive swings (Sw<sub>-k</sub>/Sw<sub>-(k+1)</sub>) can be accessed.
 * 
 * @author gardero
 * 
 */
public interface IElement {
	/**
	 * asks if the information of a specific scale is present.
	 * 
	 * @param scale
	 *            the scale in question.
	 * @return {@code true} iff the ratios info can be accessed from this object.
	 */
	boolean hasScale(int scale);


	/**
	 * gets the value of the ratio Sw<sub>-k</sub>/Sw<sub>-(k+1)</sub> for a specific scale.
	 * 
	 * @param ratioIndex
	 *            the index of the ratio in [0,Inf)
	 * @param scale
	 *            the scale of the swings we take for the ratio.
	 * @return the corresponding ratio.
	 */
	double getRatio(int ratioIndex, int scale);


	/**
	 * gets the value of the ratio Sw<sub>-(k+offset)</sub>/Sw<sub>-(k+1+offset)</sub> for a specific scale.
	 * 
	 * @param ratioIndex
	 *            the index of the ratio in [0,Inf)
	 * @param scale
	 *            the scale of the swings we take for the ratio.
	 * @param offset
	 *            the offset to the index
	 * @return the corresponding ratio.
	 */
	double getRatio(int ratioIndex, int scale, int offset);


	/**
	 * tells the direction of Sw<sub>0</sub> at a specific scale.
	 * 
	 * @param scale
	 *            the scale of the direction in question.
	 * @return {@code true} iff Sw<sub>0</sub> is going up at the specified scale.
	 */
	boolean isGoingUP(int scale);


	/**
	 * gets the target points we are on the specified scale. The target price difference to Pivot<sub>0</sub> is divided by Sw<sub>-1</sub>
	 * 
	 * @param scale
	 *            the scale of the target.
	 * @return the target at the specified scale.
	 */
	double getTarget(int scale);


	/**
	 * gets the target corresponding to a specified price.
	 * 
	 * @param price
	 *            the price of the target.
	 * @param scale
	 *            the scale for the computation of the target.
	 * @return the corresponding target to the specified price at the specified scale.
	 */
	double getTargetFromPrice(double price, int scale);


	/**
	 * gets the target corresponding to a specified relative to the Pivot<sub>0</sub> of the specified scale.
	 * 
	 * @param delta
	 *            the delta of the target.
	 * @param scale
	 *            the scale of the target.
	 * @return the corresponding target to the specified delta and the specified scale.
	 */
	double getTargetFromDelta(double delta, int scale);


	/**
	 * gets the length of the current swing at the specified scale.
	 * 
	 * @param scale
	 *            the scale of the swing.
	 * @return the swing length of Sw<sub>0</sub>
	 */
	double getSwing(int scale);

	/**
	 * provides more information about the swings, mainly consisting on pivots prices, times and
	 * 
	 * @author gardero
	 * 
	 */
	public interface IDetailedElement extends IElement {
		/**
		 * the price of the pivot at an index and scale we specify.
		 * 
		 * @param index
		 *            the index of the pivot in [0,Inf)
		 * @param scale
		 *            the scale we are interested in.
		 * @return the price of the pivot.
		 */
		long getPivotPrice(int index, int scale);


		/**
		 * the time of the pivot at an index and scale we specify.
		 * 
		 * @param index
		 *            the index of the pivot in [0,Inf)
		 * @param scale
		 *            the scale we are interested in.
		 * @return the time of the pivot.
		 */
		long getPivotTime(int index, int scale);


		/**
		 * gets the price of the target specified
		 * 
		 * @param index
		 *            the index of the target [1,Inf)
		 * @param scale
		 *            the scale of the target
		 * @return the price corresponding to the target.
		 */
		long getTargetPrice(int index, int scale);


		/**
		 * updates the internal status by considering the new price.
		 * 
		 * @param aCurrentPrice
		 *            the new price
		 */
		void considerPrice(double aCurrentPrice);


		// /**
		// * sets the information about the Higher Scales. Mainly for
		// * Logging purposes.
		// * @param prevHSTI the info of the nearest HS target.
		// * @param scale the scale of the info.
		// */
		// void setPrevHSInfo(HSTargetInfo prevHSTI, int scale);
		// HSTargetInfo getPrevHSInfo(int scale);
		/**
		 * shifts the internal swings values as TH comes.
		 */
		public abstract void shiftConditions(boolean newTID);


		public abstract long getNegativeTargetPrice(int aIndex, int aScale);
	}
}
