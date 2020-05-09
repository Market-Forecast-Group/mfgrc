package com.mfg.widget.probabilities;

import com.mfg.interfaces.probabilities.IElement;
import com.mfg.interfaces.trading.RefType;

/***
 * an implementation of {@link IElement} mainly used to build the patterns.
 * 
 * @author gardero
 * 
 */
public class SwingsElement implements IElement {

	private int[] indexes;
	private ISwingsSet population;
	private RefType type;

	public SwingsElement(int[] aIndexes, ISwingsSet aPopulation, RefType type1) {
		super();
		indexes = aIndexes;
		indexes = aIndexes;
		population = aPopulation;
		this.type = type1;
	}

	@Override
	public double getRatio(int ratioIndex, int scale) {
		return getRatio(ratioIndex, scale, 0);
	}

	@Override
	public double getRatio(int ratioIndex, int scale, int offset) {
		int ratio = ratioIndex + offset;
		if (ratio == 0)
			return population.getSwing(scale, indexes[scale])
					.getTHLengthPoints();
		else if (ratioIndex > 0)
			return population.getSwing(scale, indexes[scale] - ratio)
					.getSwingLengthPoints();
		return 0;
	}

	//
	// @Override
	// public double getDefaultRatio(int ratioIndex) {
	// return getRatio(ratioIndex, defaultScale);
	// }

	@Override
	public double getTarget(int scale) {
		if (type == RefType.Swing0_00)
			return population.getSwing(scale, indexes[scale])
					.getTarget00LengthPoints();
		return population.getSwing(scale, indexes[scale])
				.getTargetLengthPoints();
	}

	@Override
	public double getSwing(int scale) {
		return population.getSwing(scale, indexes[scale])
				.getSwingLengthPoints();
	}

	@Override
	public boolean hasScale(int scale) {
		return population.hasScale(scale) && indexes[scale] > 5;
	}

	@Override
	public boolean isGoingUP(int scale) {
		return indexes[scale] % 2 == 0;
	}

	@Override
	public double getTargetFromPrice(double aPrice, int aScale) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTargetFromDelta(double aDelta, int aScale) {
		// TODO Auto-generated method stub
		return 0;
	}

}
