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

public interface IContrarian {

	/**
	 * @return the contrarian
	 */
	public abstract boolean isContrarian();

	/**
	 * @param aContrarian the contrarian to set
	 */
	public abstract void setContrarian(boolean aContrarian);

}