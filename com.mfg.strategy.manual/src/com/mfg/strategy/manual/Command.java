package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;

public class Command {
	private final Routing routing;
	private final ManualStrategySettings settings;

	public static Command createCommand(WindowCommand windowCommand,
			ManualStrategySettings settings) {

		switch (windowCommand) {
		// average
		case AVG_LONG:
			return new AvgCommand(Routing.LONG, settings);
		case AVG_SHORT:
			return new AvgCommand(Routing.SHORT, settings);

			// cancel trail
		case CANCEL_TRAIL_LONG:
			return new CancelTrailCommand(Routing.LONG, settings);
		case CANCEL_TRAIL_SHORT:
			return new CancelTrailCommand(Routing.SHORT, settings);

			// trailings
		case CL_LONG:
			return new TrailCommand(Routing.LONG, settings, Trailing.CL);
		case CL_SHORT:
			return new TrailCommand(Routing.SHORT, settings, Trailing.CL);
		case RC_LONG:
			return new TrailCommand(Routing.LONG, settings, Trailing.RC);
		case RC_SHORT:
			return new TrailCommand(Routing.SHORT, settings, Trailing.RC);
		case SC_LONG:
			return new TrailCommand(Routing.LONG, settings, Trailing.SC);
		case SC_SHORT:
			return new TrailCommand(Routing.SHORT, settings, Trailing.SC);

			// close
		case CLOSE_ALL:
			return new CloseAllCommand(Routing.AUTO, settings);
		case CANCEL_ALL_PENDING:
			return CancelPendingCommand.createCancelAllPendingCommand(settings);
		case CLOSE_ALL_LONG:
			return new CloseAllCommand(Routing.LONG, settings);
		case CLOSE_ALL_SHORT:
			return new CloseAllCommand(Routing.SHORT, settings);
		case CLOSE_LONG:
			return PositionCommand.createCloseLongCommand(settings);
		case CLOSE_SHORT:
			return PositionCommand.createCloseShortCommand(settings);
		case CANCEL_LONG_PENDING:
			return CancelPendingCommand
					.createCancelLongPendingCommand(settings);
		case CANCEL_SHORT_PENDING:
			return CancelPendingCommand
					.createCancelShortPendingCommand(settings);
		case OPEN_LONG:
			return PositionCommand.createOpenLongCommand(settings);
		case OPEN_SHORT:
			return PositionCommand.createOpenShortCommand(settings);
			// stop and reverse
		case SAR_LONG:
			return SARCommand.createLongCommand(settings);
		case SAR_SHORT:
			return SARCommand.createShortCommand(settings);
		}
		return null;
	}

	public Command(Routing aRouting, ManualStrategySettings aSettings) {
		this.routing = aRouting;
		this.settings = aSettings;
	}

	public Routing getRouting() {
		return routing;
	}

	public ManualStrategySettings getSettings() {
		return settings;
	}
}
