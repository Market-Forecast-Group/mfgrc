package com.mfg.dfs.conn;

import com.mfg.utils.CmdLineParser;
import com.mfg.utils.CmdLineParser.IllegalOptionValueException;
import com.mfg.utils.CmdLineParser.Option;
import com.mfg.utils.CmdLineParser.UnknownOptionException;
import com.mfg.utils.U;

/**
 * A simple class used to ease the parsing of command line parameters.
 * 
 * <p>
 * The use of this class is mainly restricted to this package, only for the
 * classes {@link ServerFactory} and {@link SocketServer}, this is why it is
 * package protected and also its fields are package protected.
 * 
 * <p>
 * The use it is simple, just create the object. If there are some inconsistent
 * parameters it complains and dies.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class MfgCmdLineParser {

	boolean offline = false;
	boolean useSimulator = false;
	boolean remote = false;
	/*
	 * if true the factory will ask for a custom prefix for the root.
	 */
	boolean useCustomPrefix = false;
	String customDfsRoot = "";

	@SuppressWarnings("boxing")
	public MfgCmdLineParser(String[] args) throws IllegalOptionValueException,
			UnknownOptionException {
		CmdLineParser cmdLineParser = new CmdLineParser();
		Option isOffline = cmdLineParser.addBooleanOption("isOffline");
		Option useSimulatorOpt = cmdLineParser.addBooleanOption("useSimulator");
		Option isRemote = cmdLineParser.addBooleanOption("isRemote");
		Option isCustomPrefixEnabled = cmdLineParser
				.addBooleanOption("isCustomPrefix");
		Option customDfsRootOpt = cmdLineParser
				.addStringOption("customDfsRoot");

		cmdLineParser.parse(args);

		offline = (boolean) cmdLineParser.getOptionValue(isOffline,
				Boolean.FALSE);
		useSimulator = (boolean) cmdLineParser.getOptionValue(useSimulatorOpt,
				Boolean.FALSE);
		remote = (boolean) cmdLineParser
				.getOptionValue(isRemote, Boolean.FALSE);
		useCustomPrefix = (boolean) cmdLineParser.getOptionValue(
				isCustomPrefixEnabled, Boolean.FALSE);
		customDfsRoot = (String) cmdLineParser.getOptionValue(customDfsRootOpt,
				"");

		U.debug_var(273941, "off " + offline + " sim " + useSimulator
				+ " remo " + remote + " use custom " + useCustomPrefix
				+ " root is " + customDfsRoot);

		if (useSimulator && useCustomPrefix) {
			System.out.println("cannot use a custom root for simulator");
			System.exit(2);
		}

		if (offline && useSimulator) {
			System.out.println("cannot be offline and use the simulator");
			System.exit(2);
		}

		if (remote && (offline || useSimulator)) {
			System.out.println("cannot be remote and be offline or simulating");
			System.exit(2);
		}
	}

}
