package com.mfg.tea.conn;

import com.mfg.utils.socket.BaseCommandFactory;
import com.mfg.utils.socket.SimpleRemoteCommand;

enum TEARemoteCommands {

	/**
	 * Start command
	 */
	start {

		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new StartTEACommand(handle, unsplitted_params);
		}

	},
	/**
	 * Create virtual broker command
	 */
	cvbc {
		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new CreateVirtualBrokerCommand(handle, unsplitted_params);

		}
	},
	/**
	 * place order command.
	 */
	poc {
		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new PlaceOrderCommand(handle, unsplitted_params);
		}
	},
	sbc {
		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new StartBrokerCommand(handle, unsplitted_params);
		}
	},
	stopB {
		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new StopBrokerCommand(handle, unsplitted_params);
		}
	},
	stopTEA {
		@Override
		public SimpleRemoteCommand createCommand(int handle,
				String unsplitted_params) {
			return new StopTEACommand(handle, unsplitted_params);
		}
	};

	/**
	 * Creates a command with a given handle
	 * 
	 * @param handle
	 * @param unsplitted_params
	 * @return
	 */
	public abstract SimpleRemoteCommand createCommand(int handle,
			String unsplitted_params);
}

class TeaCommandFactory extends BaseCommandFactory {

	@Override
	protected SimpleRemoteCommand _createSpecificCommand(int handle,
			String command, String unsplitted_params) {
		TEARemoteCommands aCmd = TEARemoteCommands.valueOf(command);
		return aCmd.createCommand(handle, unsplitted_params);
	}

}
