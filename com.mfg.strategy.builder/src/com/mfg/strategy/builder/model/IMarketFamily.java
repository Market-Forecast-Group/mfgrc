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

public interface IMarketFamily {

	/**
	 * @return the marketFamily
	 */
	public abstract boolean isMarketFamily();

	/**
	 * @param aMarketFamily the marketFamily to set
	 */
	public abstract void setMarketFamily(boolean aMarketFamily);

}