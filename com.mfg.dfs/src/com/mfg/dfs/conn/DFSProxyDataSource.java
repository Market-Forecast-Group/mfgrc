package com.mfg.dfs.conn;

import com.mfg.common.DFSException;
import com.mfg.common.ISymbolListener;

/**
 * This is a data source which acts as a proxy for a stub data source in the
 * server.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DFSProxyDataSource extends BaseDataSource {

	private final DfsProxy _proxy;

	public DFSProxyDataSource(String remoteVirtualSymbol, DfsProxy aProxy,
			ISymbolListener aListener, int layersCount) {
		super(remoteVirtualSymbol, aListener, layersCount);
		_proxy = aProxy;
	}

	@Override
	public void start() throws DFSException {
		_proxy.subscribeVirtualSymbol(_symbol, this, true);
	}

	@Override
	public void _stopImpl() throws DFSException {
		_proxy.unsubscribeQuote(_symbol);
	}

	@Override
	public void pause() throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.PAUSE);
		_proxy._sendRequest(vsc);

	}

	@Override
	public void setDelay(long delay) throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.SET_DELAY, delay);
		_proxy._sendRequest(vsc);
	}

	@Override
	public void fullSpeedUntil(int limitTime) throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.RUN_TO_TIME, limitTime);
		_proxy._sendRequest(vsc);
	}

	@Override
	public void play() throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.PLAY);
		_proxy._sendRequest(vsc);

	}

	@Override
	public void fastForward() throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.FAST_FORWARD);
		_proxy._sendRequest(vsc);
	}

	@Override
	public void step() throws DFSException {
		VirtualSymbolCommand vsc = new VirtualSymbolCommand(_symbol,
				VirtualSymbolCommand.EVirtualCommandType.STEP);
		_proxy._sendRequest(vsc);
	}

}
