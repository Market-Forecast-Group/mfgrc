/*
 * (C) Copyright 2011-3 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 */

package com.mfg.dm;

import java.util.UUID;

import com.mfg.common.DFSException;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;

/**
 * 
 * @author Pasqualino
 * 
 */
public abstract class DataProvider implements IDataProvider {

	@Override
	public IDataSource createDataSource(ISymbolListener aListener,
			TickDataRequest aRequest) throws DFSException {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompositeDataSource createTickDataSource(TickDataRequest req,
			UUID aId) {
		CompositeDataSource cds = new MfgDataSource(req, this, aId);
		return cds;
	}

	@Override
	public void switchOff() throws DFSException {
		//
	}

}
