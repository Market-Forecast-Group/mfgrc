/**
 * 
 */
package com.marketforecastgroup.dfsa.database;

/**
 * This is a class to handle databases objects in the persistence model using
 * JABX.
 * 
 * @author Karell
 * 
 */
public class Database {
	private String dbName;
	private String dbPath;

	public Database() {
		dbName = "";
		dbPath = "";
	}

	public String getName() {
		return dbName;
	}

	public void setName(final String name) {
		dbName = name;
	}

	public String getPath() {
		return dbPath;
	}

	public void setPath(final String path) {
		dbPath = path;
	}

	public String getFullPath() {
		return dbPath + ".h2.db";
	}

	public String getFullMDBPath() {
		return dbPath + ".mfgdb";
	}	
	
}
