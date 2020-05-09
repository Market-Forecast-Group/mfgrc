package com.mfg.utils.socket;

import static com.mfg.utils.Utils.debug_var;

import java.net.SocketException;

import com.mfg.common.DFSException;

/**
 * The command to delete a push source from the server is here because it is
 * needed by the {@link BaseSocketHelper} class.
 * 
 * <p>
 * The nasty thing is that the "dp" string which encodes this command is here
 * and this is then repeated in the {@link ICommandFactory} which is derived in
 * the real proxy.
 * 
 * <p>
 * I could have a "simple" command factory used to create the basic commands,
 * but for now I only have this as a basic command, so the need is not relevant.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DeletePushCommand extends SimpleRemoteCommand {
	public static final String DELETE_PUSH_COMMAND = "dp";

	public DeletePushCommand(String sinkToDelete) {
		super(DELETE_PUSH_COMMAND);
		_unparsedParams = sinkToDelete; // only one param
	}

	public DeletePushCommand(int handle, String unsplitted_params) {
		super(handle, DELETE_PUSH_COMMAND, unsplitted_params);
	}

	@Override
	protected int parseAnswerImpl(String payload) throws DFSException {
		debug_var(564214, "delete push answer is ", payload);
		return 0; // the payload is not used.
	}

	@Override
	protected void _internalPerform(SimpleTextServerStub aStub)
			throws DFSException, SocketException {
		String[] pars = _getSplittedParamsSafe(_unparsedParams, 1);
		boolean res = aStub._deletePushSource(pars[0]);
		_answer = new Boolean(res);
	}

}
