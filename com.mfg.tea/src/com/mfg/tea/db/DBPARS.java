package com.mfg.tea.db;

/**
 * Simple class to store the final parameters of DB, useful to have static dead
 * code elimination and cheap argument storing.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class DBPARS {

	/**
	 * If true the database will commit at every change, this is useless for a
	 * memory database, and also if we do not want a fine grained security, and
	 * we tolerate a sparse security.
	 */
	public static final boolean COMMIT_ENABLED = false;

	/**
	 * If this is true then the db is created in memory.
	 */
	public static final boolean IS_MEMORY_FILE = true;

	/**
	 * If true the database will dump itself to a stream before shutting down.
	 */
	public static final boolean DUMP_ENABLED = false;

	private DBPARS() {
		// not creatable.
	}
}
