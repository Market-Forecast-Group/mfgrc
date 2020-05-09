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

package com.mfg.persist.interfaces;

import java.util.List;

/**
 * @author arian
 * 
 */
public interface IWorkspaceStorageListener {
	public void objectAdded(IWorkspaceStorage sotarage, Object obj);


	public void listAdded(IWorkspaceStorage storage, List<? extends Object> list);


	public void objectAboutToRemove(IWorkspaceStorage storage, Object obj) throws RemoveException;


	public void objectRemoved(IWorkspaceStorage storage, Object obj);


	public void objectModified(IWorkspaceStorage storage, Object obj);


	public void listRemoved(IWorkspaceStorage storage, List<? extends Object> list);


	public void storageChanged(IWorkspaceStorage storage);

}
