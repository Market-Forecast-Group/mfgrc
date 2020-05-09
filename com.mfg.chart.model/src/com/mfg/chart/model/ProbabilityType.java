/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */
/**
 * 
 */

package com.mfg.chart.model;

/**
 * @deprecated Enrique is implementing a new probability.
 * @author arian
 * 
 */
@Deprecated
public enum ProbabilityType {
	SWING0SWINGM1("Probability Swing0/Swing-1", "S0/-1"), //
	SWING0SWINGM2("Probability Swing0/Swing-2", "S0/-2"), //
	TARGETSWINGM1("Probability Target/Swing-1", "T/-1"), //
	TARGETSWINGM2("Probability Target/Swing-2", "T/-2");//

	public final static ProbabilityType[] DISPLAY_VALUES = { SWING0SWINGM1,
			TARGETSWINGM1 };
	private final String description;
	private final String shortDescription;

	private ProbabilityType(String description1, String shortDescription1) {
		this.description = description1;
		this.shortDescription = shortDescription1;
	}

	public String getDescription() {
		return description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

}
