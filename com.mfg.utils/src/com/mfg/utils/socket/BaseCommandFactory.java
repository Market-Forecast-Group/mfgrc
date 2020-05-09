package com.mfg.utils.socket;

import java.net.SocketException;

import com.mfg.utils.U;

/**
 * This class is the base for all the command factories.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BaseCommandFactory implements ICommandFactory {

	@Override
	public final SimpleRemoteCommand createCommand(String text)
			throws SocketException {
		/*
		 * The command is composed of four parts: r,$handle,$command,$pars
		 */
		String splits[] = U.commaPattern.split(text, 4);

		if (splits.length != 4) {
			throw new SocketException("Cannot parse the string " + text);
		}

		// Now I have to make sure that the first string is the r, this is just
		// a fixed start
		if (splits[0].compareTo("r") != 0) {
			throw new SocketException("Expected a request but I got "
					+ splits[0]);
		}

		// Ok, now I get the handle
		int handle = Integer.parseInt(splits[1]);

		// OK, now I get the command
		String command = splits[2];
		String unsplitted_params = splits[3]; // these are the unsplitted params

		/*
		 * Probably the delete push command should be moved in DFS, because it
		 * is used only there.
		 */
		if (command.compareTo(DeletePushCommand.DELETE_PUSH_COMMAND) == 0) {
			return new DeletePushCommand(handle, unsplitted_params);
		}

		return _createSpecificCommand(handle, command, unsplitted_params);

	}

	/**
	 * Creates the specific command used by the application.
	 * 
	 * <p>
	 * Every implemented app has its own set of specific commands.
	 * 
	 * @param handle
	 * @param command
	 * @param unsplitted_params
	 * @return
	 * @throws SocketException
	 *             if the command cannot be created.
	 */
	protected abstract SimpleRemoteCommand _createSpecificCommand(int handle,
			String command, String unsplitted_params) throws SocketException;

}
