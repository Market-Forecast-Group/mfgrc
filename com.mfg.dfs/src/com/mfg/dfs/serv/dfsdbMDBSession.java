package com.mfg.dfs.serv;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */
/* User can insert his code here */
/* END USER IMPORTS */

public class dfsdbMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends MDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"dfsdb\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"c444fd7f-c00b-4261-8355-f30ec29b9ae9\",\"name\":\"RangeBars\",\"columns\":[{\"name\":\"timeStamp\",\"uuid\":\"2a0e09a0-fe51-4114-bc14-a4c3ddc9cc49\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"open\",\"uuid\":\"b2e02706-23d9-469d-b34e-d27e1976b332\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"type\",\"uuid\":\"be9d28ed-1c6e-43ad-a815-696669ca3902\",\"type\":\"BYTE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"volFirst\",\"uuid\":\"4677958a-3d46-4286-98f0-538cf60b16cd\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"volSecond\",\"uuid\":\"71345807-2d8c-4b21-8a1c-e85f871b9e20\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"volume\",\"uuid\":\"912ed736-b20e-4378-853e-c9b09ddf3c85\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":true,\"formula\":\"$$.volFirst + $$.volSecond\"}]},{\"uuid\":\"06c238ef-7bc4-40f7-8a1e-cdf5a9011f18\",\"name\":\"TimeBars\",\"columns\":[{\"name\":\"timestamp\",\"uuid\":\"b4c8d183-8156-40b3-b45e-35b6d52c8937\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"low\",\"uuid\":\"79d52d78-0bd7-455b-b5b0-3b6aad2ba830\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"to_open\",\"uuid\":\"0e6031a8-4abe-4c29-8457-14be2edf198d\",\"type\":\"SHORT\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"to_high\",\"uuid\":\"55808c44-8ad4-4f5a-83c8-e5196219ce3e\",\"type\":\"SHORT\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"to_close\",\"uuid\":\"afd30ebe-1160-469c-94b2-6a9c8eb63997\",\"type\":\"SHORT\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"volume\",\"uuid\":\"c0f92a98-d0d1-4735-9c5f-f716078ab760\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("c444fd7f-c00b-4261-8355-f30ec29b9ae9", "2a0e09a0-fe51-4114-bc14-a4c3ddc9cc49 LONG; b2e02706-23d9-469d-b34e-d27e1976b332 INTEGER; be9d28ed-1c6e-43ad-a815-696669ca3902 BYTE; 4677958a-3d46-4286-98f0-538cf60b16cd INTEGER; 71345807-2d8c-4b21-8a1c-e85f871b9e20 INTEGER; ");
		SIGNATURES.put("06c238ef-7bc4-40f7-8a1e-cdf5a9011f18", "b4c8d183-8156-40b3-b45e-35b6d52c8937 LONG; 79d52d78-0bd7-455b-b5b0-3b6aad2ba830 INTEGER; 0e6031a8-4abe-4c29-8457-14be2edf198d SHORT; 55808c44-8ad4-4f5a-83c8-e5196219ce3e SHORT; afd30ebe-1160-469c-94b2-6a9c8eb63997 SHORT; c0f92a98-d0d1-4735-9c5f-f716078ab760 INTEGER; ");
 	}
 	
	public dfsdbMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public dfsdbMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */
	/* The user can write his code here */
	/* END USER SESSION CODE */	

	private RangeBarsMDB internal_connectTo_RangeBarsMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (RangeBarsMDB) _cache.get(file);
			}
			RangeBarsMDB mdb = new RangeBarsMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "c444fd7f-c00b-4261-8355-f30ec29b9ae9");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public RangeBarsMDB connectTo_RangeBarsMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_RangeBarsMDB(getFile(filename), bufferSize);
	}
	
	public RangeBarsMDB connectTo_RangeBarsMDB(String filename) throws IOException {
		return connectTo_RangeBarsMDB(filename, 100);
	}

	private TimeBarsMDB internal_connectTo_TimeBarsMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (TimeBarsMDB) _cache.get(file);
			}
			TimeBarsMDB mdb = new TimeBarsMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "06c238ef-7bc4-40f7-8a1e-cdf5a9011f18");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public TimeBarsMDB connectTo_TimeBarsMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_TimeBarsMDB(getFile(filename), bufferSize);
	}
	
	public TimeBarsMDB connectTo_TimeBarsMDB(String filename) throws IOException {
		return connectTo_TimeBarsMDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

