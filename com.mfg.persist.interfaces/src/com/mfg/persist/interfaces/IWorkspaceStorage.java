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

import java.util.Collection;

/**
 * @author arian
 * 
 */
public interface IWorkspaceStorage {
	public void addStorageListener(IWorkspaceStorageListener l);

	public void removeStorageListener(IWorkspaceStorageListener l);

	public String createNewName(String prefix, Collection<? extends Object> list);

	public void storageSaved();

	public void storageLoaded();

	public boolean isPersisted(Object obj);

	public String getStorageId();

}
