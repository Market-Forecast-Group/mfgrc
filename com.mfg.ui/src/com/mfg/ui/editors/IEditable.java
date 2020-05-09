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
package com.mfg.ui.editors;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

/**
 * @author arian
 * 
 */
public interface IEditable {
	public IEditorPart openEditor() throws PartInitException;
}
