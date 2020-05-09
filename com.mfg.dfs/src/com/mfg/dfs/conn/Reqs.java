package com.mfg.dfs.conn;

/**
 * This is a package class used to store some useful constants
 * 
 * @author Sergio
 * 
 */
final class Reqs {

	private Reqs() {
		// not possible.
	}

	/**
	 * This is the string which is sent when the first push ends, this because
	 * there are two possible ends for the cache, a "normal" end and another end
	 * which means that the bars are still coming, because there are real time
	 * bars which come from real time.
	 * <p>
	 * Not all requests continue in real time. The fact that a request continues
	 * in real time depends on the request itself. It's a type of request,
	 * that's all.
	 */
	public static final String END_FIRST_PUSH = "END_FIRST_PUSH";

	/**
	 * This si the constant used to signal the end of history. This is the real
	 * end of the history and the push sink can end.
	 */
	public static final String END_HISTORY = "END_OF_HISTORY";

	/**
	 * This enumeration lists all the possible remote commands, its use is to
	 * allow the class {@linkplain RemoteCommand} to format and then to parse
	 * back the command from the socket.
	 * <p>
	 * The command is then put into the socket with the string equal to the name
	 * of the enumeration (for example "gss"), this is done to simplify parsing
	 * later
	 * 
	 * @author Sergio
	 * 
	 */
	public enum RemoteCommands {
		gss("Get status symbol"), gsl("Get symbol list"),
		/*
		 * cache( "request history"),
		 */
		gbw("Get bars between"), login("login"), dp("delete push"), dab(
				"date after x bars"), dbb("date before x bars"), invalid(
				"an invalid command"), us("unsubscribe command"), sub(
				"subscribe command"), gbc("getBarCount command"), cds(
				"create data source"), vsc("virtual symbol command"), icsdf(
				"is connected to simulated data feed"), wmc(
				"watch maturity command"), umc("unwatch maturity command");

		public final String friendlyName;

		RemoteCommands(String aFriendlyName) {
			friendlyName = aFriendlyName;
		}

	}
}
