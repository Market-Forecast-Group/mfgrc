package com.mfg.dfs.conn;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.ISymbolListener;
import com.mfg.dfs.conn.Reqs.RemoteCommands;
import com.mfg.dfs.misc.IVirtualSymbol;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.U;
import com.mfg.utils.XmlIdentifier;
import com.mfg.utils.socket.SimpleRemoteCommand;
import com.mfg.utils.socket.SimpleTextPushSink;
import com.mfg.utils.socket.SimpleTextServerStub;
import com.thoughtworks.xstream.XStream;

//This is the class used to name the file, but it is not used
public class ProxyRequestClasses {
	private ProxyRequestClasses() {
		// nothing.
	}
}

/**
 * This is the base class from which we derive the other classes used to make
 * synchronous communication toward the DFS.
 * 
 * <p>
 * This class is abstract because only concrete classes can parse the result
 * from the socket.
 * 
 * <p>
 * The class is also able to serialize itself to the socket. The basic format
 * for this command is
 * 
 * <pre>
 * r,$handle,$command,$parameters
 * </pre>
 * 
 * <p>
 * Where the parameters are a (comma separated) list of values.
 * 
 * <p>
 * The answer has the generic form
 * 
 * <pre>
 * &quot;a,$handlereq,$res,$payload&quot;
 * </pre>
 * 
 * <p>
 * The a stands for answer, the handle request is the handle of the request,
 * then there is the server's status code (0 means OK) and the the answer.
 * 
 * <p>
 * Some requests have a simple answer, maybe multiline, but a single answer.
 * <p>
 * Some other requests, instead, are "live", in the sense that they leave a mark
 * on the server and are continuously updated.
 * <p>
 * In this case they produce a so called {@linkplain PushSink} which is simply
 * an object which is used to receive a push message.
 * 
 * <p>
 * In this case the push sink will process messages which starts with a 'p'
 * (push).
 * 
 * <p>
 * Clients are free to remove push sinks, usually with a predetermined message
 * or/and with blocking the push messages from being received again.
 * 
 * @author Sergio
 * 
 */
abstract class RemoteCommand extends SimpleRemoteCommand {

	protected RemoteCommand(Reqs.RemoteCommands aCommand) {
		super(aCommand.toString());

	}

	/**
	 * In this case the handle is given from the outside.
	 * 
	 * <p>
	 * Here we are in the stub side of the socket, so the handle is the number
	 * given by the socket, which is done by the atomic integer in the proxy
	 * side.
	 * 
	 * @param handle
	 * @param unparsed_params
	 */
	protected RemoteCommand(Reqs.RemoteCommands aCommand, int handle,
			String unparsed_params) {
		super(handle, aCommand.toString(), unparsed_params);
	}

	@Override
	public int getReturnCode() throws DFSException {
		if (!isEnded())
			join();
		return _ansCode;
	}

	@Override
	public Object getAnswer() throws DFSException {
		join();
		return _answer;
	}

	@Override
	public void join() throws DFSException {
		try {
			super.join();
		} catch (Exception e) {
			if (e instanceof DFSException) {
				/*
				 * if this is a dfs exception all is good, because it is a
				 * remote server exception, otherwise there is something very
				 * bad happening here.
				 */
				throw (DFSException) e;
			}
			throw new IllegalStateException(e);
		}
	}

}

/**
 * class which dispatches the
 * {@linkplain IDFS#getBarCount(String, BarType, int)} method.
 * 
 * <p>
 * This class is used to
 * 
 * @author Sergio
 * 
 */
class GetBarCountCommand extends RemoteCommand {

	@SuppressWarnings("boxing")
	protected GetBarCountCommand(String symbol, BarType aType, int barWidth) {
		super(RemoteCommands.gbc);
		_unparsedParams = U.join(symbol, aType, barWidth);
	}

	public GetBarCountCommand(int handle, String unsplitted_params) {
		super(RemoteCommands.gbc, handle, unsplitted_params);
	}

	@SuppressWarnings("boxing")
	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		_answer = Integer.parseInt(payload);
		return 0;
	}

	@SuppressWarnings("boxing")
	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 3);

		String symbol = pars[0];
		BarType barType = BarType.valueOf(pars[1]);

		int barWidth = Integer.parseInt(pars[2]);
		int answer;

		answer = ((DfsStub) aStub).getServer().getBarCount(symbol, barType,
				barWidth);

		_answer = answer;

	}

}

/**
 * This is a simple "get" command. For now it is unique and it has its own
 * class. If it will be needed I will create a normal "get boolean class".
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class IsConnectedToSimulatedDataFeedCommand extends RemoteCommand {

	protected IsConnectedToSimulatedDataFeedCommand() {
		super(Reqs.RemoteCommands.icsdf);
		_unparsedParams = U.join(VOID);
	}

	public IsConnectedToSimulatedDataFeedCommand(int handle,
			String unsplitted_params) {
		super(Reqs.RemoteCommands.icsdf, handle, unsplitted_params);
	}

	@SuppressWarnings("boxing")
	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		_answer = Boolean.parseBoolean(payload);
		return 0;
	}

	@SuppressWarnings("boxing")
	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException {
		boolean answer;

		answer = ((DfsStub) aStub).getServer().isConnectedToSimulatedDataFeed();

		_answer = answer;

	}

}

class UnwatchMaturityCommand extends RemoteCommand {

	protected UnwatchMaturityCommand(String aSymbol) {
		super(Reqs.RemoteCommands.umc);
		_unparsedParams = aSymbol;
	}

	public UnwatchMaturityCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.umc, handle, unsplitted_params);
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 1);
		String symbol = pars[0];

		DfsStub dfsStub = ((DfsStub) aStub);
		dfsStub.getServer().removeWatcher(dfsStub._proxyListener, symbol);

	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		if (payload.compareTo(VOID) != 0) {
			throw new DFSException("cannot parse " + payload);
		}
		return 0;
	}

}

/**
 * The command which is used to watch a particular maturity
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class WatchMaturityCommand extends RemoteCommand {

	protected WatchMaturityCommand(String symbol) {
		super(Reqs.RemoteCommands.wmc);
		_unparsedParams = symbol;
	}

	public WatchMaturityCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.wmc, handle, unsplitted_params);
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws Exception {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 1);
		String symbol = pars[0];

		DfsStub dfsStub = ((DfsStub) aStub);
		dfsStub.getServer().addWatcher(dfsStub._proxyListener, symbol);

	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		if (payload.compareTo(VOID) != 0) {
			throw new DFSException("cannot parse " + payload);
		}
		return 0;
	}

}

/**
 * The generic class which encapsulate a remote command towards a virtual symbol
 * which is in server's space.
 * 
 * <p>
 * There are different types of virtual symbol's command, but to make things
 * easy (and also because the commands are really simple) they are not
 * implemented as different classes but only as an enumeration which is used to
 * distinguish them.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class VirtualSymbolCommand extends RemoteCommand {

	public enum EVirtualCommandType {
		PAUSE, SET_DELAY, RUN_TO_TIME, FAST_FORWARD, PLAY, STEP
	}

	/**
	 * Simple constructor used to create a virtual symbol command without
	 * parameters.
	 * 
	 * @param aSymbol
	 *            the (virtual) symbol which is being controlled.
	 * 
	 * @param aType
	 *            This is the virtual command type, the parameter is initialized
	 *            to zero and this is fine.
	 */
	protected VirtualSymbolCommand(String aSymbol, EVirtualCommandType aType) {
		super(Reqs.RemoteCommands.vsc);
		_unparsedParams = U.join(aSymbol, aType.toString(), VOID);
	}

	public VirtualSymbolCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.vsc, handle, unsplitted_params);
	}

	/**
	 * Creates a virtual symbol command with a parameter.
	 * 
	 * @param aSymbol
	 * @param aType
	 * @param delay
	 */
	@SuppressWarnings("boxing")
	public VirtualSymbolCommand(String aSymbol, EVirtualCommandType aType,
			long delay) {
		super(Reqs.RemoteCommands.vsc);
		_unparsedParams = U.join(aSymbol, aType.toString(), delay);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// virtual commands have no answer
		if (payload.compareTo(VOID) != 0) {
			throw new DFSException("cannot parse " + payload);
		}
		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 3);
		/*
		 * I have to differentiate between the different types of virtual symbol
		 * commands. This is called in stub space, so the virtual symbol is
		 * accessible.
		 */

		String symbol = pars[0];

		EVirtualCommandType parsedType = EVirtualCommandType.valueOf(pars[1]);

		IVirtualSymbol vs = ((DfsStub) aStub).getServer().getVirtualSymbol(
				symbol);

		switch (parsedType) {
		case FAST_FORWARD:
			vs.fastForward();
			break;
		case PAUSE:
			vs.pause();
			break;
		case PLAY:
			vs.play();
			break;
		case RUN_TO_TIME:
			int time = Integer.parseInt(pars[2]);
			vs.fullSpeedUntil(time);
			break;
		case SET_DELAY:
			long delay = Long.parseLong(pars[2]);
			vs.setDelay(delay);
			break;
		case STEP:
			vs.step();
			break;
		default:
			throw new IllegalStateException();

		}

	}

}

/**
 * This is the command used to create a data source in the server and creating
 * in the client side the push sink used to collect the expanded tick from the
 * virtual symbol.
 * 
 * <p>
 * This command is only used by MFG when DFS is remote, other actors, for
 * example TEA, will not use this command, because it would create
 * <i>another</i> data source, totally unrelated to this. Other actors should
 * simply subscribe to this virtual symbol.
 * 
 * <p>
 * This command does not automatically subscribes to it, it simply creates the
 * virtual symbol in the server and returns the virtual symbol id, if everything
 * is fine.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class CreateDataSourceCommand extends RemoteCommand {

	/**
	 * called in the proxy space.
	 * 
	 * @param aRequest
	 *            the request you want to serialize to the server.
	 */
	protected CreateDataSourceCommand(TickDataRequest aRequest) {
		super(Reqs.RemoteCommands.cds);
		_unparsedParams = aRequest.serializeToString();
	}

	/**
	 * Called in stub space.
	 * <p>
	 * The unsplitted params are simply one big string which is the xml
	 * representation of the request (at least this is the intent).
	 * 
	 * @param handle
	 * @param unsplitted_params
	 */
	public CreateDataSourceCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.cds, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		/*
		 * The answer is simply the id of the remote virtual symbol created.
		 */
		_answer = payload;
		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException {
		TickDataRequest request = (TickDataRequest) XmlIdentifier
				.createFromString(_unparsedParams);
		_answer = ((DfsStub) aStub).getServer().createVirtualSymbol(request);

	}

	public String getVirtualSymbolId() throws DFSException {
		join();
		return (String) _answer;
	}

}

// /**
// * A remote command used to request a history.
// * <p>
// * The history is in the form of a cache, which will in any case
// *
// * @author Sergio
// *
// */
// class RequestHistoryCommand extends RemoteCommand {
//
// private RequestParams _req;
//
// @Override
// public void afterSentHook() {
// // releasing the push, if the push is built!
// if (_cps != null)
// _cps.releasePush();
// }
//
// private IBarCacheAsync _monitor;
// private HistorySink _hs;
// private CachePushSource _cps;
//
// public RequestHistoryCommand(RequestParams aReq, IBarCacheAsync aMonitor) {
// // super(aReq.serialize());
// super(Reqs.RemoteCommands.cache);
// _req = aReq;
// _monitor = aMonitor;
// _unparsedParams = aReq.serialize();
// }
//
// public RequestHistoryCommand(int handle, String unsplitted_params) {
// super(Reqs.RemoteCommands.cache, handle, unsplitted_params);
// }
//
// @Override
// protected int parseAnswerImpl(String payload) throws DFSException {
//
// String splits[] = U.commaPattern.split(payload);
//
// // The first split is the push id, the second is the number of bars
// // in the first push
// if (splits.length != 2) {
// throw new DFSException(
// "Illegal number of parameters, required 2, got " + payload);
// }
//
// int pushInitialSize = Integer.parseInt(splits[1]);
// _monitor.setTotalWorkSize(pushInitialSize);
//
// // Ok, I create the history sink and I put it into the proxy
// _hs = new HistorySink(splits[0], _monitor);
// DfsProxy.getInstance().addSink(_hs);
//
// return 0;
// }
//
// public IBarCache getCache() throws DFSException {
// // I have to join... first of all the answer
//
// // join();
//
// if (getReturnCode() == 0) {
// // then I must join the cache
// boolean res = false;
// try {
// res = _hs.joinFirstPush();
// } catch (InterruptedException e) {
// throw new DFSException(e);
// }
//
// if (!res) {
// // something is gone bad, probably the request has been
// // cancelled.
// DfsProxy.getInstance().removeSink(_hs);
// throw new DFSException(
// "The request has been cancelled, I remove the sink "
// + _hs._pushId);
// }
//
// // then I return
// IBarCache cache = _hs.getCache();
// // recollect the sink, if it is over...
// if (!_req.isOpenRequest()) {
// // I can remove the sink, because the request is over.
// DfsProxy.getInstance().removeSink(_hs);
// }
// return cache;
// }
//
// throw new DFSException("Cannot give the cache code " + getReturnCode());
//
// }
//
// @SuppressWarnings("boxing")
// @Override
// protected void _internalPerform(SimpleTextServerStub aStubB)
// throws DFSException {
//
// DfsStub aStub = (DfsStub) aStubB;
//
// // remember: this is called from the stub side.
//
// // Ok, this is the request for the cache, I have to put it
// // // serialized in the socket.
// //
// RequestParams reqParams = RequestParams.parse(_unparsedParams);
// @SuppressWarnings("resource")
// IBarCache ans = aStub.getServer().getCache(reqParams);
// // Ok, if I am here then I have the cache, so I can create a
// // push
// // source with an id and give to the client the handle of this
// // push source.
//
// _cps = new CachePushSource(aStub, ans, reqParams.isOpenRequest(),
// reqParams);
//
// _answer = U.join(_cps.getPushKey(), ans.size());
//
// // _cs.printLine(psa.serialize());
//
// // Ok, I add the push sink to the list
// aStub.addPushSource(_cps);
// aStub.setModified();
//
// // then the push source will be handled by the push thread...
//
// }
//
// }

/**
 * The remote command to get the list of all the symbols (prefixes) stored in
 * DFS.
 * 
 * @author Sergio
 * 
 */
class SymbolsListCommand extends RemoteCommand {

	/**
	 * This command has no parameters, so the payload is null
	 */
	protected SymbolsListCommand() {
		super(Reqs.RemoteCommands.gsl);
		_unparsedParams = VOID;
	}

	// private DfsSymbolList _list;

	public SymbolsListCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.gsl, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// I simply have to deserialize the payload.
		XStream xstream = new XStream();
		_answer = xstream.fromXML(payload);
		return 0;
	}

	public DfsSymbolList getSymbolsList() throws DFSException {
		join();
		return (DfsSymbolList) _answer;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub) {
		_answer = ((DfsStub) aStub)._dfs.getSymbolsList();
	}
}

/**
 * The request to get the number of bars between two dates.
 * 
 * @author Sergio
 * 
 */
class GetBarsBetweenCommand extends RemoteCommand {

	@SuppressWarnings("boxing")
	@Override
	public void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {

		// first I get the parameters.
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 5);
		// symbol, type, start date, end date.
		String symbol = pars[0];
		BarType type = BarType.valueOf(pars[1]);

		int barWidth = Integer.parseInt(pars[2]);

		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		long startDate, endDate;
		try {
			startDate = sdf.parse(pars[3]).getTime();
			endDate = sdf.parse(pars[4]).getTime();
		} catch (ParseException e) {
			throw new DFSException(e);
		}

		int res = ((DfsStub) aStub).getServer().getBarsBetween(symbol, type,
				barWidth, startDate, endDate);

		_answer = res;
	}

	@SuppressWarnings("boxing")
	public GetBarsBetweenCommand(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) {

		super(Reqs.RemoteCommands.gbw);

		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		_unparsedParams = U.join(symbol, aType, barWidth,
				sdf.format(new Date(startDate)), sdf.format(new Date(endDate)));
	}

	/**
	 * Constructor from the stub side.
	 * 
	 * @param handle
	 * @param unsplitted_params
	 * @throws DFSException
	 */
	public GetBarsBetweenCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.gbw, handle, unsplitted_params);
	}

	@SuppressWarnings("boxing")
	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// I have simply to parse the answer
		_answer = Integer.parseInt(payload);
		return 0;
	}
}

/**
 * abstract class to generalize the request for before or after bars, to get the
 * arrival date.
 * 
 * <P>
 * This class is the mother of the two concrete classes which have the code to
 * get the actual request
 * 
 * @author Sergio
 * 
 */
class GetDateBeforeOrAfterRequest extends RemoteCommand {

	@SuppressWarnings("boxing")
	protected GetDateBeforeOrAfterRequest(Reqs.RemoteCommands aCommand,
			String symbol, BarType aType, int barWidth, long aDate, int numBars) {
		super(aCommand);
		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		_unparsedParams = U.join(symbol, aType, barWidth,
				sdf.format(new Date(aDate)), numBars);
	}

	public GetDateBeforeOrAfterRequest(RemoteCommands aRemoteCommand,
			int handle, String unsplitted_params) {
		super(aRemoteCommand, handle, unsplitted_params);
	}

	@SuppressWarnings("boxing")
	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// the answer is a date, so a long.
		_answer = Long.parseLong(payload);
		return 0;
	}

	// @Override
	// public Long getAnswer() throws DFSException {
	// join();
	// return (Long) _answer;
	// }

	@SuppressWarnings("boxing")
	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 5);

		String symbol = pars[0];
		BarType barType = BarType.valueOf(pars[1]);

		int barWidth = Integer.parseInt(pars[2]);

		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		long date;
		int numBars;
		try {
			date = sdf.parse(pars[3]).getTime();
			numBars = Integer.parseInt(pars[4]);
		} catch (ParseException e) {
			throw new DFSException(e);
		}

		long answer;

		if (_command.compareTo(RemoteCommands.dab.toString()) == 0) {
			answer = ((DfsStub) aStub).getServer().getDateAfterXBarsFrom(
					symbol, barType, barWidth, date, numBars);
		} else {
			answer = ((DfsStub) aStub).getServer().getDateBeforeXBarsFrom(
					symbol, barType, barWidth, date, numBars);
		}

		_answer = answer;
	}
}

class GetDateAfterXBarsReq extends GetDateBeforeOrAfterRequest {

	public GetDateAfterXBarsReq(String symbol, BarType aType, int barWidth,
			long startDate, int numBars) {
		super(Reqs.RemoteCommands.dab, symbol, aType, barWidth, startDate,
				numBars);
	}

	public GetDateAfterXBarsReq(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.dab, handle, unsplitted_params);
	}
}

class GetDateBeforeXBarsReq extends GetDateBeforeOrAfterRequest {

	public GetDateBeforeXBarsReq(String symbol, BarType aType, int barWidth,
			long startDate, int numBars) {
		super(Reqs.RemoteCommands.dbb, symbol, aType, barWidth, startDate,
				numBars);
	}

	public GetDateBeforeXBarsReq(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.dbb, handle, unsplitted_params);
	}
}

/**
 * the request for the symbol status.
 * 
 * <p>
 * The symbol status can be long more than one line, so it must be inside a
 * payload.
 * 
 * @author Sergio
 * 
 */
class GetSymbolStatusCommand extends RemoteCommand {

	public GetSymbolStatusCommand(String symbol) {
		super(Reqs.RemoteCommands.gss);
		_unparsedParams = symbol;
	}

	/**
	 * protected because it is called only from the factory.
	 * 
	 * @param handle
	 * @param unsplitted_params
	 */
	protected GetSymbolStatusCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.gss, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		XStream xstream = new XStream();
		_answer = xstream.fromXML(payload);
		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 1);
		_answer = ((DfsStub) aStub).getServer().getStatusForSymbol(pars[0],
				false);
	}

}

/**
 * an helper class for the {@linkplain IDFS#unsubscribeQuote(String)} mehod.
 * 
 * @author Sergio
 * 
 */
class UnsubscribeRequest extends RemoteCommand {

	// private String _symbol;

	public UnsubscribeRequest(String symbol) {
		super(Reqs.RemoteCommands.us);
		_unparsedParams = symbol;
	}

	public UnsubscribeRequest(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.us, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		/*
		 * Now in the proxy part I can delete the sink
		 */
		SimpleTextPushSink subSource = DfsProxy.getInstance().removeSink(
				payload);
		U.debug_var(839921, "about to remove the sink ", payload, " answer ",
				subSource);
		return subSource != null ? 0 : -1;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStubB)
			throws DFSException, SocketException {
		String params[] = _getSplittedParamsSafe(_unparsedParams, 1);
		DfsStub aStub = (DfsStub) aStubB;
		aStub._dfs.unsubscribeQuote(aStub._proxyListener, params[0]);
		SubscriptionPushSource oldSource = aStub._proxyListener
				.removeSubscriptionSource(params[0]);

		U.debug_var(912915, "removed push key ", oldSource.getPushKey());
		_answer = oldSource.getPushKey();
	}

}

/**
 * The subscribe command is able to send the request and waits for the answer.
 * <p>
 * The answer will have the subid with which this object can create the sink.
 * 
 * @author Sergio
 * 
 */
class SubscribeCommand extends RemoteCommand {

	// private String _symbol;

	@Override
	public void afterSentHook() {
		if (_sps != null) {
			try {
				_sps.sendStartSubEvent();
			} catch (SocketException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private ISymbolListener _listener;
	private boolean _mustSendAck;
	private SubscriptionPushSource _sps;

	public SubscribeCommand(String symbol, ISymbolListener aListener,
			boolean mustSendAck) {
		super(Reqs.RemoteCommands.sub);
		_unparsedParams = symbol; // the params is only the symbol
		_listener = aListener;
		_mustSendAck = mustSendAck;
	}

	public SubscribeCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.sub, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		/*
		 * If I am here then it is true than the subscription is successful,
		 * then I simply have to create a new push sink in the proxy.
		 */
		// I add the subscription sink which is used to get the sub messages.
		SubscriptionSink ss = new SubscriptionSink(payload, _listener,
				_mustSendAck);
		DfsProxy.getInstance().addSink(ss);

		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStubB)
			throws SocketException, DFSException {
		String parms[] = _getSplittedParamsSafe(_unparsedParams, 1);
		// aStub.getServer().subscribeQuote(aListener, parms[0]);

		String symbol = parms[0];

		DfsStub aStub = (DfsStub) aStubB;

		String pushId = aStub._proxyListener.getPushIdForSymbol(symbol);

		if (pushId != null) {
			// Ok, I am already subscribed to this symbol, I simply repeat the
			// push id
			_answer = pushId;
			return;
		}

		/*
		 * Ok, I am not subscribed... The push source is not put in the stub as
		 * a notrmal push source, but as a subscription push source.
		 */

		_sps = new SubscriptionPushSource(symbol, aStub);

		aStub._proxyListener.addSubscriptionSource(symbol, _sps);

		_answer = _sps.getPushKey();

		/*
		 * This call may start the subscription thread, in any case the start
		 * subscription event is sent in the afterSentHook method.
		 */
		aStub._dfs.subscribeSynchableQuote(aStub._proxyListener, symbol);

	}

}

class LoginCommand extends RemoteCommand {

	public LoginCommand(String user, String password) {
		super(Reqs.RemoteCommands.login);
		_unparsedParams = U.join(user, password);
	}

	public LoginCommand(int handle, String unsplitted_params) {
		super(Reqs.RemoteCommands.login, handle, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		// nothing to parse
		return 0;
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		// first I get the parameters.
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 2);
		// symbol, type, start date, end date.

		String user = pars[0];
		String password = pars[1];

		((DfsStub) aStub).login(user, password);

	}

}

// /**
// * This is a generic request which is used to delete an existing push sink.
// *
// * <p>
// * Usually this request is done to abort the receiving of a cache.
// *
// * @author Sergio
// *
// */
// class DeletePushCommand extends RemoteCommand {
//
// public DeletePushCommand(String sinkToDelete) {
// super(Reqs.RemoteCommands.dp);
// _unparsedParams = sinkToDelete; // only one param
// }
//
// public DeletePushCommand(int handle, String unsplitted_params) {
// super(Reqs.RemoteCommands.dp, handle, unsplitted_params);
// }
//
// @Override
// protected int parseAnswerImpl(String payload) throws DFSException {
// debug_var(564214, "delete push answer is ", payload);
// return 0; // the payload is not used.
// }
//
// @Override
// protected void _internalPerform(SimpleTextServerStub aStub)
// throws DFSException {
// String[] pars = _getSplittedParamsSafe(_unparsedParams, 1);
// boolean res = aStub._deletePushSource(pars[0]);
// _answer = new Boolean(res);
// }
//
// }