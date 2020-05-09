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

import java.util.UUID;

import javax.xml.bind.annotation.XmlID;

import com.mfg.persist.interfaces.AbstractStorageObject;
import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;

public class StrategyInfo extends AbstractStorageObject {
	private String fileName;
	private String description;
	private String _patternJSON;
	private final UUID uuid;


	public StrategyInfo() {
		uuid = UUID.randomUUID();
	}


	/**
	 * @param aDescription
	 */
	public StrategyInfo(String name, String aDescription) {
		setName(name);
		uuid = UUID.randomUUID();
	}


	public String getPatternJSON() {
		return _patternJSON;
	}


	public void setPatternJSON(String patternJSON) {
		_patternJSON = patternJSON;
	}


	@Override
	public boolean allowRename() {
		return false;
	}


	@Override
	public UUID getUUID() {
		return uuid;
	}


	@XmlID
	public String getFileName() {
		return fileName;
	}


	public String getDescription() {
		return description;
	}


	public void setFileName(String aFileName) {
		fileName = aFileName;
	}


	public void setDescription(String aDescription) {
		description = aDescription;
	}

	@Override
	public StrategyBuilderStorage getStorage() {
		return StrategyBuilderPlugin.getDefault().getStrategiesStorage();
	}
}
