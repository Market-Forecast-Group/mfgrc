/*
 * (C) Copyright 2011-2013 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 */

package com.mfg.dm;

import java.util.UUID;

import com.mfg.common.DFSException;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;

/**
 * This is the generic interface to a data provider in an abstract sense. The
 * data provider is an object which is able to subscribe to a real data feed,
 * like eSignal or IB. In reality the dataprovider is only able to give "raw"
 * data, in the form of bars. These bars are then post processed to give the
 * stream of prices.
 * 
 * @author Pasqualino
 * 
 */
public interface IDataProvider {

	/**
	 * Builds a new type data source, a server's side data source which is also
	 * called a virtual symbol.
	 * 
	 * <p>
	 * This creates a data source every time it is called, a new one, even if
	 * the request is the same, because the data source is tied to a particular
	 * client.
	 * 
	 * @param aListener
	 * @param aRequest
	 * @return
	 * @throws DFSException
	 */
	public IDataSource createDataSource(ISymbolListener aListener,
			TickDataRequest aRequest) throws DFSException;

	/**
	 * Creates a TickDataSource. A tick data source is made from one or more
	 * DataRequests.
	 * 
	 * <p>
	 * The tick data source which is being created is either client side (a
	 * {@link CompositeDataSource}) or a {@link MfgDataSource}, which is the
	 * server side version of a tick data source.
	 * 
	 * @param req
	 *            the request (which can have different layers).
	 * 
	 * @param aId
	 *            the id of the data source which has been created.
	 * @return a data source ready to be started (started means to warm up).
	 */
	public TickDataSource createTickDataSource(TickDataRequest req, UUID aId);

	/**
	 * returns the name of this data provider
	 * 
	 * @return the name of this data provider
	 */
	public String getName();

	/**
	 * Switches off (closes the connection: socket, db, file, etc...) If there
	 * are pending requests it returns false immediately This method is
	 * synchronous, it blocks until the dp is shut down completely.
	 * 
	 * @param force
	 *            if true the dp will abort all the data requests.
	 * 
	 * @return true if the dp has been switched off. false if it cannot be
	 *         switched off, because there are pending requests.
	 * @throws DFSException
	 */
	public void switchOff() throws DFSException;

	/**
	 * Enables the data provider. This is called automatically if the data
	 * provider is not switched on already.
	 * 
	 * @return true if the data provider has been switched on.
	 */
	public boolean switchOn();

}
