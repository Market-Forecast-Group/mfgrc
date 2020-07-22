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

package org.mfg.opengl.widgets;

/**
 * @author arian
 * 
 */
public interface IGWButton extends IGWidget {

	/**
	 * @return
	 */
	public boolean isSelected();

	public IGWSelectionModel getSelectionModel();

	public void setSelectionModel(IGWSelectionModel model);

	public String getTooltip();
}