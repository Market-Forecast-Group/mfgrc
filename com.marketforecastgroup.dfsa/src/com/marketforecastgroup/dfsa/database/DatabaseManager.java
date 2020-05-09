package com.marketforecastgroup.dfsa.database;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public final class DatabaseManager {

	private static DatabaseManager dbManager;

	private List<Database> databases;

	private final XStream xstream;

	private DatabaseManager() {
		databases = new ArrayList<>();

		xstream = new XStream();
		xstream.addDefaultImplementation(ArrayList.class, List.class);
		xstream.alias("Database", List.class);

		//restore();
	}

	public static DatabaseManager getInstance() {
		if (dbManager == null) {
			dbManager = new DatabaseManager();
		}
		return dbManager;
	}

	public Database findDatabase(final String name) {
		for (Database db : databases) {
			if (db.getName().equals(name)) {
				return db;
			}
		}
		return null;
	}

	public List<Database> getDatabases() {
		return databases;
	}

	public void clear() {
		databases.clear();
	}

}
