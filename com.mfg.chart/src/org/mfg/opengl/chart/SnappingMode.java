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
package org.mfg.opengl.chart;

/**
 * @author arian
 * 
 */
public enum SnappingMode {
	SNAP_XY, SNAP_Y, DO_NOT_SNAP;

	public SnappingMode next() {
		return values()[(ordinal() + 1) % values().length];
	}

	public boolean snapsX() {
		return this == SNAP_XY;
	}

	public boolean snapsY() {
		return this == SNAP_XY || this == SNAP_Y;
	}
}
