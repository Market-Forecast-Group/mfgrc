/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model;

import com.mfg.strategy.automatic.eventPatterns.EventAtomTriggerRatio;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.triggers.RatioTrigger;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class SwRatioEventModel extends ScaledEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;

	private int dimension = 0;
	private BoundsModel lowerBoundsM = new BoundsModel(true);
	private BoundsModel upperBoundsM = new BoundsModel(false);
	private double[] infiniteBounds = new double[0];


	public SwRatioEventModel() {
		lowerBoundsM.setParent(this);
		upperBoundsM.setParent(this);
		children.add(lowerBoundsM);
		children.add(upperBoundsM);
		setDimension(2);
	}


	@Override
	public boolean addChild(EventModelNode aChild) {
		if (aChild instanceof BoundsModel) {
			BoundsModel b = (BoundsModel) aChild;
			if (b.isLower()) {
				int pos = children.indexOf(lowerBoundsM);
				if (pos > -1)
					children.set(pos, lowerBoundsM = b);
				else
					children.add(lowerBoundsM = b);
			} else {
				int pos = children.indexOf(upperBoundsM);
				if (pos > -1)
					children.set(pos, upperBoundsM = b);
				else
					children.add(upperBoundsM = b);
			}
			aChild.setParent(this);
			return true;
		}
		return super.addChild(aChild);
	}


	// @Override
	// public void addChild(int aIndex, EventModelNode aChild) {
	// if (aChild instanceof BoundsModel)
	// children.add(aIndex,aChild);
	// else
	// super.addChild(aIndex,aChild);
	// }

	@Override
	public boolean isCollapsed() {
		return false;
	}


	@Override
	public boolean removeChild(EventModelNode aChild) {
		return false;
	}


	@Override
	public String getLabel() {
		return "Sw Ratio{" + "scale=" + getWidgetScale() + ((!isLimitedToSwing0()) ? "" : (", on Sw0")) + "}";
	}


	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}


	/**
	 * @param aDimension
	 *            the dimension to set
	 */
	public void setDimension(int aDimension) {
		if (dimension != aDimension) {
			int old = dimension;
			dimension = aDimension;
			updateDimensions(dimension, old, false);
			firePropertyChange(PropertiesID.PROPERTY_DIMENSION, Integer.valueOf(old), Integer.valueOf(aDimension));
		}
	}


	private static int getBucketDimensions(int numDimensions) {
		return 1 + (numDimensions * (numDimensions - 1)) / 2;
	}


	private void updateDimensions(int numDimensions, int olddim, boolean force) {
		// if (fValueType == null && !isWorkingRatiosFilter()) {
		// return;
		// }
		int newNumRatios = getBucketDimensions(numDimensions);
		double[] lowerBounds = lowerBoundsM.getBounds();
		double[] upperBounds = upperBoundsM.getBounds();
		int oldNumRatios = (lowerBounds == null) ? getBucketDimensions(olddim) : lowerBounds.length;
		// // If the requested dimension is already present... do nothing
		if (lowerBounds == null || newNumRatios != oldNumRatios || force) {
			double[] newlowerBounds = new double[newNumRatios];
			double[] newupperBounds = new double[newNumRatios];
			double[] newinfiniteBounds = new double[newNumRatios];
			if (lowerBounds != null && !force) {
				System.arraycopy(lowerBounds, 0, newlowerBounds, 0, Math.min(oldNumRatios, newNumRatios));
				System.arraycopy(upperBounds, 0, newupperBounds, 0, Math.min(oldNumRatios, newNumRatios));
				System.arraycopy(infiniteBounds, 0, newinfiniteBounds, 0, Math.min(oldNumRatios, newNumRatios));
				for (int i = oldNumRatios; i < newNumRatios; ++i) {
					// the _number_of_ratio_intervals is excluded.
					// System.out.println("Updating bounds");
					double lowerBucket = 0;
					newlowerBounds[i] = lowerBucket;
					double upperBucket = 1;
					newupperBounds[i] = upperBucket;
					newinfiniteBounds[i] = 10;
				}
			} else
				for (int i = 0; i < newNumRatios; ++i) {
					// the _number_of_ratio_intervals is excluded.
					// System.out.println("Updating bounds");
					double lowerBucket = 0;
					newlowerBounds[i] = lowerBucket;
					double upperBucket = 1;
					newupperBounds[i] = upperBucket;
					newinfiniteBounds[i] = 10;
				}
			lowerBoundsM.setBounds(newlowerBounds);
			upperBoundsM.setBounds(newupperBounds);
			infiniteBounds = newinfiniteBounds;
		}
	}


	/**
	 * @return the infiniteBounds
	 */
	public double[] getInfiniteBounds() {
		return infiniteBounds;
	}


	/**
	 * @param aInfiniteBounds
	 *            the infiniteBounds to set
	 */
	public void setInfiniteBounds(double[] aInfiniteBounds) {
		infiniteBounds = aInfiniteBounds;
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomTriggerRatio res = new EventAtomTriggerRatio();
		res.setLimitToSwingZero(isLimitedToSwing0());
		RatioTrigger par = new RatioTrigger();
		par.setWidgetScale(getWidgetScale());
		par.setDimensions(getDimension());
		par.setLowerBounds(((BoundsModel) getChildren().get(0)).getBounds());
		par.setUpperBounds(((BoundsModel) getChildren().get(1)).getBounds());
		res.setTrigger(par);
		return res;
	}

}
