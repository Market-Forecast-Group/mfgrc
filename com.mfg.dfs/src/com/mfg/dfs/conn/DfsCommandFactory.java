package com.mfg.dfs.conn;

import java.net.SocketException;

import com.mfg.dfs.conn.Reqs.RemoteCommands;
import com.mfg.utils.socket.BaseCommandFactory;
import com.mfg.utils.socket.SimpleRemoteCommand;

/**
 * The factory which will create the command from its serialized version.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DfsCommandFactory extends BaseCommandFactory {

	@Override
	protected SimpleRemoteCommand _createSpecificCommand(int handle,
			String command, String unsplitted_params) throws SocketException {
		RemoteCommands parsedCommand = null;

		try {
			parsedCommand = RemoteCommands.valueOf(command);
		} catch (IllegalArgumentException e) {
			throw new SocketException("Cannot parse the command " + command);
		}
		SimpleRemoteCommand answer = null;

		switch (parsedCommand) {
		// case cache:
		// answer = new RequestHistoryCommand(handle, unsplitted_params);
		// break;
		case dab:
			answer = new GetDateAfterXBarsReq(handle, unsplitted_params);
			break;
		case dbb:
			answer = new GetDateBeforeXBarsReq(handle, unsplitted_params);
			break;
		case dp:
			/*
			 * should not happen, because now the delete push command is in
			 * charge of the BaseCommandFactory.
			 */
			throw new IllegalStateException();
			// answer = new DeletePushCommand(handle, unsplitted_params);
			// break;
		case gbw:
			answer = new GetBarsBetweenCommand(handle, unsplitted_params);
			break;
		case gsl:
			answer = new SymbolsListCommand(handle, unsplitted_params);
			break;
		case gss:
			answer = new GetSymbolStatusCommand(handle, unsplitted_params);
			break;
		case login:
			answer = new LoginCommand(handle, unsplitted_params);
			break;
		case invalid:
			// nothing to do... it is invalid!
			break;
		case sub:
			answer = new SubscribeCommand(handle, unsplitted_params);
			break;
		case us:
			answer = new UnsubscribeRequest(handle, unsplitted_params);
			break;
		case gbc:
			answer = new GetBarCountCommand(handle, unsplitted_params);
			break;
		case cds:
			answer = new CreateDataSourceCommand(handle, unsplitted_params);
			break;
		case vsc:
			answer = new VirtualSymbolCommand(handle, unsplitted_params);
			break;
		case icsdf:
			answer = new IsConnectedToSimulatedDataFeedCommand(handle,
					unsplitted_params);
			break;
		case wmc:
			answer = new WatchMaturityCommand(handle, unsplitted_params);
			break;
		case umc:
			answer = new UnwatchMaturityCommand(handle, unsplitted_params);
			break;
		default:
			break;
		}

		if (answer == null) {
			throw new SocketException("cannot parse the string " + command
					+ " parsed command is " + parsedCommand);
		}

		return answer;
	}

}
