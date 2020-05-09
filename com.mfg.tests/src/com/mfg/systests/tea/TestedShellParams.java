package com.mfg.systests.tea;

import com.mfg.common.RandomSymbol;

/**
 * Simple struct class which holds the parameters used by a {@link TestedShell}
 * to build itself.
 * 
 * <p>
 * All simple data, not methods. All data has sensible default values, but there
 * are some caveats, because some data cannot be set together. The only logic of
 * this class is to validate the set of parameters given to the
 * {@link TestedShell} itself.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class TestedShellParams {

	/**
	 * True if the shell should create a real time data request.
	 */
	public boolean isRealTime = false;

	/**
	 * Every tested shell has its own symbol and for now it is essentially one
	 * of (possible infinite) random symbols.
	 */
	public final RandomSymbol symbol;

	public boolean isPaperTrading = true;

	public final String _name;

	/**
	 * Creates the parameters object.
	 * 
	 * <p>
	 * The symbol is passed as an object because I simulate that is passed to
	 * the shell from the "user" (the test environment).
	 * 
	 * @param aName
	 */
	public TestedShellParams(String aName, RandomSymbol aSymbol) {
		symbol = aSymbol;
		_name = aName;
	}
}
