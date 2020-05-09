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
public abstract class WorkspaceStorageAdapter implements IWorkspaceStorageListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IObjectStorageListener#objectAdded(com.mfg.persist.interfaces.IObjectStorage, java.lang.Object)
	 */
	@Override
	public void objectAdded(IWorkspaceStorage sotarage, Object obj) {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IObjectStorageListener#listAdded(com.mfg.persist.interfaces.IObjectStorage, java.util.List)
	 */
	@Override
	public void listAdded(IWorkspaceStorage storage, List<? extends Object> list) {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IObjectStorageListener#objectAboutToRemove(com.mfg.persist.interfaces.IObjectStorage, java.lang.Object)
	 */
	@Override
	public void objectAboutToRemove(IWorkspaceStorage storage, Object obj) throws RemoveException {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IObjectStorageListener#objectRemoved(com.mfg.persist.interfaces.IObjectStorage, java.lang.Object)
	 */
	@Override
	public void objectRemoved(IWorkspaceStorage storage, Object obj) {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IObjectStorageListener#listRemoved(com.mfg.persist.interfaces.IObjectStorage, java.util.List)
	 */
	@Override
	public void listRemoved(IWorkspaceStorage storage, List<? extends Object> list) {
		//DO NOTHING
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IWorkspaceStorageListener#objectModified(com.mfg.persist.interfaces.IWorkspaceStorage, java.lang.Object)
	 */
	@Override
	public void objectModified(IWorkspaceStorage storage, Object obj) {
		//DO NOTHING
	}

	@Override
	public void storageChanged(IWorkspaceStorage storage) {
		//DO NOTHING
	}

}
