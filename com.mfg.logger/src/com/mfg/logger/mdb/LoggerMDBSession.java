package com.mfg.logger.mdb;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */
/* User can insert his code here */
/* END USER IMPORTS */

public class LoggerMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends MDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"Logger\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"8248ffbf-c3bb-483f-a170-502e1c690729\",\"name\":\"Log\",\"columns\":[{\"name\":\"ID\",\"uuid\":\"b8aa657d-2fe1-4897-b3ed-efbea3bdde26\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"timeGeneral\",\"uuid\":\"422eddd9-19f0-4114-baca-3443db2a5282\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"priority\",\"uuid\":\"c9a2843d-67af-46d1-a66f-f1a93ee838d0\",\"type\":\"FLOAT\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"source\",\"uuid\":\"34c47ddb-453e-4935-8ec8-e7a2d204e1d3\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"8596a7c0-60b2-4280-b8a7-df819d0165ae\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"81025ac2-8406-4e52-86e8-e245b772d433\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"thTime\",\"uuid\":\"e1317417-2b91-42fe-8102-fd782a90cb78\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"thPrice\",\"uuid\":\"0e7129c3-57f4-4a36-a769-ab5d9ade9251\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"targetPrice\",\"uuid\":\"5c5abb84-850e-4189-b52c-4f241793c6d1\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"timeCPU\",\"uuid\":\"e52f58b7-980d-40c7-a3ea-123f776232c6\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"probVersion\",\"uuid\":\"d757f244-a340-429f-acd4-70e41d4eaa59\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"classID\",\"uuid\":\"3f7d34bb-91db-48b9-8910-494404adf14e\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"hhllIndex\",\"uuid\":\"7dc38547-5b70-4e1a-b043-922e342de5bf\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"thIndex\",\"uuid\":\"52e7a182-86ea-406d-8315-69642dd05fdb\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"msgIndex\",\"uuid\":\"f6c25839-1f86-45be-9b8b-c64e7da0425e\",\"type\":\"INTEGER\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"baseScaleCluster\",\"uuid\":\"88b9c945-7980-41f6-8960-e5010b890225\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"scale\",\"uuid\":\"47011328-5372-49dc-b68c-a746df687e64\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"p0Time\",\"uuid\":\"fa01072a-87b7-4bd4-83d9-ae056fd90cae\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"p0Price\",\"uuid\":\"ee5306e5-99a8-4f71-ba9d-fc9502208244\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pm1Time\",\"uuid\":\"7dbc06bf-514e-40e8-916c-d9d51aad01e8\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pm1Price\",\"uuid\":\"07e6c2b6-6896-429e-94f1-8733567d9df6\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pid\",\"uuid\":\"2b961434-2f02-4270-a6d7-9ffc6937c0cd\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"clusterID\",\"uuid\":\"455f1a9f-005e-496f-ba12-502ee47277b5\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"textIndex\",\"uuid\":\"1b0eeadc-a14f-426b-9788-ff0b4564f2a9\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"textSize\",\"uuid\":\"cc6eace7-7e3f-4a4e-9a4f-3707e8a814a3\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"strMessage\",\"uuid\":\"b8e5f183-f767-49ce-b77f-af83ae4aa6a2\",\"type\":\"STRING\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"patternID\",\"uuid\":\"757f8468-6efd-4437-a76e-b1c9fba18e43\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"dirContrarian\",\"uuid\":\"eac45ecb-6ece-4b66-be47-77cbea296dab\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("8248ffbf-c3bb-483f-a170-502e1c690729", "b8aa657d-2fe1-4897-b3ed-efbea3bdde26 INTEGER; 422eddd9-19f0-4114-baca-3443db2a5282 LONG; c9a2843d-67af-46d1-a66f-f1a93ee838d0 FLOAT; 34c47ddb-453e-4935-8ec8-e7a2d204e1d3 INTEGER; 8596a7c0-60b2-4280-b8a7-df819d0165ae LONG; 81025ac2-8406-4e52-86e8-e245b772d433 LONG; e1317417-2b91-42fe-8102-fd782a90cb78 LONG; 0e7129c3-57f4-4a36-a769-ab5d9ade9251 LONG; 5c5abb84-850e-4189-b52c-4f241793c6d1 LONG; e52f58b7-980d-40c7-a3ea-123f776232c6 LONG; d757f244-a340-429f-acd4-70e41d4eaa59 INTEGER; 3f7d34bb-91db-48b9-8910-494404adf14e INTEGER; 7dc38547-5b70-4e1a-b043-922e342de5bf INTEGER; 52e7a182-86ea-406d-8315-69642dd05fdb INTEGER; f6c25839-1f86-45be-9b8b-c64e7da0425e INTEGER; 88b9c945-7980-41f6-8960-e5010b890225 INTEGER; 47011328-5372-49dc-b68c-a746df687e64 INTEGER; fa01072a-87b7-4bd4-83d9-ae056fd90cae LONG; ee5306e5-99a8-4f71-ba9d-fc9502208244 LONG; 7dbc06bf-514e-40e8-916c-d9d51aad01e8 LONG; 07e6c2b6-6896-429e-94f1-8733567d9df6 LONG; 2b961434-2f02-4270-a6d7-9ffc6937c0cd INTEGER; 455f1a9f-005e-496f-ba12-502ee47277b5 INTEGER; 1b0eeadc-a14f-426b-9788-ff0b4564f2a9 INTEGER; cc6eace7-7e3f-4a4e-9a4f-3707e8a814a3 INTEGER; b8e5f183-f767-49ce-b77f-af83ae4aa6a2 STRING; 757f8468-6efd-4437-a76e-b1c9fba18e43 INTEGER; eac45ecb-6ece-4b66-be47-77cbea296dab BOOLEAN; ");
 	}
 	
	public LoggerMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public LoggerMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */
	/* The user can write his code here */
	/* END USER SESSION CODE */	

	private LogMDB internal_connectTo_LogMDB(File file, File arrayFile, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
		if (!arrayFile.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The array file is not inside the database folder: " + arrayFile);
		if (!arrayFile.getParentFile().equals(file.getParentFile())) 
			throw new IllegalArgumentException("The array file is not in the same folder of the main file: " + arrayFile);
		if (!arrayFile.getPath().equals(file.getPath() + ".array"))
			throw new IllegalArgumentException("Illegal array file name: " + arrayFile);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (LogMDB) _cache.get(file);
			}
			LogMDB mdb = new LogMDB(this, file, arrayFile, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "8248ffbf-c3bb-483f-a170-502e1c690729");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public LogMDB connectTo_LogMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_LogMDB(getFile(filename), getFile(filename + ".array"), bufferSize);
	}
	
	public LogMDB connectTo_LogMDB(String filename) throws IOException {
		return connectTo_LogMDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

