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
package com.mfg.interfaces.configurations;

import java.util.UUID;

import com.mfg.persist.interfaces.IStorageObject;

/**
 * Common interface of all the MFG configurations. The concept of a
 * configurations is:
 * <ul>
 * <li>It is unique in the system, and its identifier is a {@link UUID}.
 * <li>It has a name</li>
 * <li>It has information that should be cloneable ({@link IConfigurationInfo}.
 * When a job is created from a configuration, the configuration info should be
 * cloned, in this way any configuration editor will change the settings used
 * for a job.</li>
 * </ul>
 * 
 * @author arian
 * 
 */
public interface IConfiguration<T extends IConfigurationInfo> extends
		IStorageObject {
	public static final String PROP_INFO = "info";

	@Override
	public UUID getUUID();

	public T getInfo();

	public void setInfo(T info);
}
