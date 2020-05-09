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

package com.mfg.strategy.builder.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.symbols.SymbolsPlugin;
import com.thoughtworks.xstream.XStream;

/**
 * @author arian
 * 
 */
public class StrategyBuilderStorage extends SimpleStorage<StrategyInfo> {
	public StrategyBuilderStorage() {
	}


	@Override
	public void loadAll(File workspace) {
		super.loadAll(workspace);

		// to load the patterns in the old folder
		for (StrategyInfo info : getObjects()) {
			if (info.getPatternJSON() == null) {
				String fname = info.getFileName();
				Path path = Paths.get(fname);
				try {
					if (Files.exists(path)) {
						String json = new String(Files.readAllBytes(path));
						info.setPatternJSON(json);
						save(info, workspace);
						Files.delete(path);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public StrategyInfo add(EventsCanvasModel model) {
		StrategyInfo info = new StrategyInfo(model.getName(), model.getDescription());
		info.setPatternJSON(model.toJSONString());
		add(info);
		return info;
	}


	@Override
	public void configureXStream(XStream xstream) {
		XStream[] list = { xstream, SymbolsPlugin.getDefault().getTradingStorage().getXStream() };
		for (XStream xs : list) {
			xs.alias("strategy-info", StrategyInfo.class);
		}
	}


	@Override
	public String getStorageName() {
		return "Strategy-Builder-Patterns";
	}


	public StrategyInfo findByName(String name) {
		for (StrategyInfo info : getObjects()) {
			if (info.getName().equals(name)) {
				return info;
			}
		}
		return null;
	}


	@Override
	public StrategyInfo createDefaultObject() {
		StrategyInfo info = new StrategyInfo();
		info.setPatternJSON(EventsCanvasModel.createDefault().toJSONString());
		return info;
	}

}
