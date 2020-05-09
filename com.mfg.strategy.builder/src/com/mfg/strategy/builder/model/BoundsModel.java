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

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class BoundsModel extends SimpleEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double[] bounds = new double[0];
	private String name;
	private boolean lower;
	private final String DIM = "DIM";
	private final String LOWER = "LOWER";


	public BoundsModel(boolean aLower) {
		super();
		this.name = aLower ? "LBounds" : "UBounds";
		this.lower = aLower;
	}


	public BoundsModel() {
		this(true);
	}


	@Override
	public String getLabel() {
		return name;
	}


	/**
	 * @return the bounds
	 */
	public double[] getBounds() {
		return bounds;
	}


	public double getBound(int index) {
		return bounds[index];
	}


	/**
	 * @param aBounds
	 *            the bounds to set
	 */
	public void setBounds(double[] aBounds) {
		bounds = aBounds;
	}


	public void setBound(double aBound, int index) {
		if (bounds[index] != aBound) {
			bounds[index] = aBound;
			firePropertyChange(PropertiesID.PROPERTY_BOUND + index, null, Double.valueOf(aBound));
		}
	}


	/**
	 * @return the lower
	 */
	public boolean isLower() {
		return lower;
	}


	public int size() {
		return bounds.length;
	}


	@Override
	protected void _toJsonEmbedded(JSONStringer aStringer) throws JSONException {
		super._toJsonEmbedded(aStringer);
		aStringer.key(DIM);
		aStringer.value(bounds.length);
		aStringer.key(LOWER);
		aStringer.value(isLower());
	}


	@Override
	protected void _updateFromJSON(JSONObject aJson) throws JSONException {
		int dim = aJson.getInt(DIM);
		bounds = new double[dim];
		lower = aJson.getBoolean(LOWER);
		super._updateFromJSON(aJson);
	}


	@Override
	public EventGeneral exportMe() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public EventModelNode clone() {
		BoundsModel clone = (BoundsModel) super.clone();
		clone.bounds = Arrays.copyOf(bounds, bounds.length);	
		return clone;
	}

}
