package com.mfg.dfs.conn;

import com.mfg.common.DFSException;
import com.mfg.common.ISymbolListener;
import com.mfg.common.IDataSource;
import com.mfg.dfs.misc.MultiServer;
import com.mfg.dfs.misc.VirtualSymbol;

/**
 * The {@link DFSDataSource} is the embedded implementation of the interface
 * {@link IDataSource}
 * 
 * <p>
 * The class implements the {@link ISymbolListener} interface because it
 * listens to a {@link VirtualSymbol}.
 * 
 * <P>
 * the class wants another quote listener which will get the quotes from the
 * virtual symbol, this class has no logic, apart from the possibility to change
 * the expansion speed (if this is possible).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DFSDataSource extends BaseDataSource {

	private final MultiServer _server;

	/**
	 * @param aListener
	 *            this is the owner of this particular data source.
	 * @param layersCount
	 *            the count of layers for this data source.
	 */
	public DFSDataSource(String virtualSymbol, ISymbolListener aListener,
			MultiServer aServer, int layersCount) {
		super(virtualSymbol, aListener, layersCount);
		_server = aServer;
	}

	@Override
	public void start() throws DFSException {
		/*
		 * I have to start subscribing to the virtual symbol.
		 * 
		 * This subscription starts the virtual data feed in the virtual symbol
		 * thread in server's space.
		 * 
		 * The virtual symbol is always subscribe, also if it is a database
		 * request, because it is as if it is always a real time symbol (that
		 * could be changed...).
		 */
		_server.subscribeQuote(this, _symbol);
	}

	@Override
	public void _stopImpl() throws DFSException {
		/*
		 * This will automatically recollect the virtual symbol if this (as it
		 * should be) is the last unsubscribe to the symbol.
		 */
		_server.unsubscribeQuote(this, _symbol);
	}

	@Override
	public void pause() {
		_server.getVirtualSymbol(this._symbol).pause();
	}

	@Override
	public void setDelay(long delay) {
		_server.getVirtualSymbol(this._symbol).setDelay(delay);

	}

	@Override
	public void fullSpeedUntil(int limitTime) throws DFSException {
		_server.getVirtualSymbol(_symbol).fullSpeedUntil(limitTime);

	}

	@Override
	public void play() {
		_server.getVirtualSymbol(_symbol).play();
	}

	@Override
	public void fastForward() {
		_server.getVirtualSymbol(_symbol).fastForward();
	}

	@Override
	public void step() {
		_server.getVirtualSymbol(_symbol).step();
	}

}
