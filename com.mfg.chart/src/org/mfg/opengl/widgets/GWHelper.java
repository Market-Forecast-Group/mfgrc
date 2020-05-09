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
package org.mfg.opengl.widgets;

/**
 * @author arian
 * 
 */
public class GWHelper {
	public static void setBorderToHideWhenButtonIsNotSelected(
			final IGWButton... buttons) {
		for (final IGWButton button : buttons) {
			GWBorder border = new GWBorder() {
				@Override
				public boolean isVisible() {
					return button.isSelected();
				}
			};
			button.setBorder(border);
		}
	}

	public static GWBorder createEmptyBorder() {
		GWBorder border = new GWBorder();
		border.setVisible(false);
		return border;
	}
}