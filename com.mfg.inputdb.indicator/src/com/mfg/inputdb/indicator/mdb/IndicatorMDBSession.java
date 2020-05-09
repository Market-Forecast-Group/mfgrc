package com.mfg.inputdb.indicator.mdb;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */

/* END USER IMPORTS */

public class IndicatorMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends com.mfg.inputdb.prices.CommonMDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"Indicator\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"1324de2f-d059-4caa-b5b3-09636736be01\",\"name\":\"Bands\",\"columns\":[{\"name\":\"time\",\"uuid\":\"5488d6af-775b-458c-b71d-4afcbe603da3\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topPrice\",\"uuid\":\"94afa187-cf12-41e9-ba6e-1fc683c84d74\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerPrice\",\"uuid\":\"4a07d26d-3e14-45e3-a421-8e3ddd493205\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomPrice\",\"uuid\":\"9f1623dd-4317-4182-87ff-53ecd990f334\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"physicalTime\",\"uuid\":\"e366347d-3bb9-4447-8ce9-68651f4ab78e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topRaw\",\"uuid\":\"ccdcd77d-7ea9-4c08-a898-16a4687fc1be\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerRaw\",\"uuid\":\"55a56721-5116-4da2-a134-8c5e5c75d6ff\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomRaw\",\"uuid\":\"96c7d594-1285-4450-b3cc-a8eb62fbc42a\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"fc117a2b-39f1-4fb1-b3ac-356cbf52e9e4\",\"name\":\"Pivot\",\"columns\":[{\"name\":\"pivotPrice\",\"uuid\":\"79b74b3a-20f1-48d5-a0fd-4d7edbfcb9a2\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pivotTime\",\"uuid\":\"e17c10dc-52b2-4952-a906-92b239fe43d2\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmPrice\",\"uuid\":\"e933e331-4a1d-45c5-80a3-a39b8f07aaa0\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmTime\",\"uuid\":\"fb5c4409-1dac-4f13-a42a-1b3b6524b08a\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"timeInterval\",\"uuid\":\"b7e01af1-21bc-4e43-808d-fb3fa487d678\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isUp\",\"uuid\":\"9c27d821-6300-4769-8f4d-3659c76ba446\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"pivotPhysicalTime\",\"uuid\":\"6ddfa1e8-dbd4-4ed4-b28d-b378d755cc18\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"confirmPhysicalTime\",\"uuid\":\"47733df6-20f1-4c90-bcd3-72bfa0e0f6a0\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"4295d3b5-f743-4d0e-9c00-7101036ba349\",\"name\":\"Channel\",\"columns\":[{\"name\":\"startTime\",\"uuid\":\"2049491e-83fa-43f9-846a-d783e58fe865\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"endTime\",\"uuid\":\"372c317b-c2c2-4126-814c-46ca6edb7ec5\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topStartPrice\",\"uuid\":\"7de840a6-c975-4a66-a75b-b42d9dc51829\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topEndPrice\",\"uuid\":\"d47a3ed0-beb3-4487-b8da-9ba7930ea24f\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerStartPrice\",\"uuid\":\"7f1f32aa-a3d0-4263-a8ea-e48cc272afdd\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"centerEndPrice\",\"uuid\":\"20a81701-5b6d-4441-b9af-022f385a4ce9\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomStartPrice\",\"uuid\":\"59158fc8-e15e-4201-9a22-50dee19fd877\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomEndPrice\",\"uuid\":\"41ed0b90-9527-421e-9f6d-011a57349fd0\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"slope\",\"uuid\":\"23f5ee57-2ac7-48e3-a11f-5ef21555aa54\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"startPhysicalTime\",\"uuid\":\"bec417c2-2730-4bfa-8aaf-68196587e4a3\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"endPhysicalTime\",\"uuid\":\"5634f269-54c0-4ee4-943b-71447056f2b5\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"d290623f-05b0-4186-8805-b5c37f7850a7\",\"name\":\"Channel2\",\"columns\":[{\"name\":\"startTime\",\"uuid\":\"daba14bc-91b4-4a74-9892-32b05bc7eb4b\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"endTime\",\"uuid\":\"f34523d8-c3a4-4351-b54f-23ac930a518e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"c0\",\"uuid\":\"e736b7cf-09f3-4af5-918a-da9f3d1a4141\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"c1\",\"uuid\":\"2e444897-d30b-42e5-80e1-62ba461d0793\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"c2\",\"uuid\":\"4264ef67-104d-4fd3-9880-cd3c89e5b063\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"c3\",\"uuid\":\"9941f015-d54b-4e22-8d90-177e6bfe35f0\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"c4\",\"uuid\":\"c6c227fc-c4fb-4535-9d1d-df9511985bd5\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"topDistance\",\"uuid\":\"1274b0ae-e596-4103-8d1c-1da9ceb16aec\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"bottomDistance\",\"uuid\":\"4130e36c-b48f-4aa2-bc33-280658a0c2bd\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("1324de2f-d059-4caa-b5b3-09636736be01", "5488d6af-775b-458c-b71d-4afcbe603da3 LONG; 94afa187-cf12-41e9-ba6e-1fc683c84d74 DOUBLE; 4a07d26d-3e14-45e3-a421-8e3ddd493205 DOUBLE; 9f1623dd-4317-4182-87ff-53ecd990f334 DOUBLE; e366347d-3bb9-4447-8ce9-68651f4ab78e LONG; ccdcd77d-7ea9-4c08-a898-16a4687fc1be DOUBLE; 55a56721-5116-4da2-a134-8c5e5c75d6ff DOUBLE; 96c7d594-1285-4450-b3cc-a8eb62fbc42a DOUBLE; ");
		SIGNATURES.put("fc117a2b-39f1-4fb1-b3ac-356cbf52e9e4", "79b74b3a-20f1-48d5-a0fd-4d7edbfcb9a2 DOUBLE; e17c10dc-52b2-4952-a906-92b239fe43d2 LONG; e933e331-4a1d-45c5-80a3-a39b8f07aaa0 DOUBLE; fb5c4409-1dac-4f13-a42a-1b3b6524b08a LONG; b7e01af1-21bc-4e43-808d-fb3fa487d678 LONG; 9c27d821-6300-4769-8f4d-3659c76ba446 BOOLEAN; 6ddfa1e8-dbd4-4ed4-b28d-b378d755cc18 LONG; 47733df6-20f1-4c90-bcd3-72bfa0e0f6a0 LONG; ");
		SIGNATURES.put("4295d3b5-f743-4d0e-9c00-7101036ba349", "2049491e-83fa-43f9-846a-d783e58fe865 LONG; 372c317b-c2c2-4126-814c-46ca6edb7ec5 LONG; 7de840a6-c975-4a66-a75b-b42d9dc51829 DOUBLE; d47a3ed0-beb3-4487-b8da-9ba7930ea24f DOUBLE; 7f1f32aa-a3d0-4263-a8ea-e48cc272afdd DOUBLE; 20a81701-5b6d-4441-b9af-022f385a4ce9 DOUBLE; 59158fc8-e15e-4201-9a22-50dee19fd877 DOUBLE; 41ed0b90-9527-421e-9f6d-011a57349fd0 DOUBLE; 23f5ee57-2ac7-48e3-a11f-5ef21555aa54 BOOLEAN; bec417c2-2730-4bfa-8aaf-68196587e4a3 LONG; 5634f269-54c0-4ee4-943b-71447056f2b5 LONG; ");
		SIGNATURES.put("d290623f-05b0-4186-8805-b5c37f7850a7", "daba14bc-91b4-4a74-9892-32b05bc7eb4b LONG; f34523d8-c3a4-4351-b54f-23ac930a518e LONG; e736b7cf-09f3-4af5-918a-da9f3d1a4141 DOUBLE; 2e444897-d30b-42e5-80e1-62ba461d0793 DOUBLE; 4264ef67-104d-4fd3-9880-cd3c89e5b063 DOUBLE; 9941f015-d54b-4e22-8d90-177e6bfe35f0 DOUBLE; c6c227fc-c4fb-4535-9d1d-df9511985bd5 DOUBLE; 1274b0ae-e596-4103-8d1c-1da9ceb16aec DOUBLE; 4130e36c-b48f-4aa2-bc33-280658a0c2bd DOUBLE; ");
 	}
 	
	public IndicatorMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public IndicatorMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */

	public static final String PROP_SCALES_COUNT = "scalesCount";
	private static final String PROP_POLYLINE_DEGREE = "polylineDegree";

	public IndicatorMDBSession(String sessionName, File root, SessionMode mode,
			boolean temporal) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON, temporal);
	}

	public int getScalesCount() {
		try {
			return Integer.parseInt(getProperties().getProperty(
					PROP_SCALES_COUNT));
		} catch (Exception e) {
			return 0;
		}
	}

	public void setScalesCount(int scalesCount) {
		getProperties().setProperty(PROP_SCALES_COUNT,
				Integer.toString(scalesCount));
	}

	public void setPolylineDegree(int polylineDegree) {
		getProperties().setProperty(PROP_POLYLINE_DEGREE,
				Integer.toString(polylineDegree));
	}

	public int getPolylineDegree() {
		try {
			return Integer.parseInt(getProperties().getProperty(
					PROP_POLYLINE_DEGREE, "1"));
		} catch (Exception e) {
			return 0;
		}
	}

	// -- connect methods

	public PivotMDB connectTo_PivotMDB(int layer, int level) throws IOException {
		return connectTo_PivotMDB(getPivotMDBFile(layer, level));
	}

	public static String getPivotMDBFile(int layer, int level) {
		return "layer-" + layer + "/" + level + "/pivot.mdb";
	}

	public BandsMDB connectTo_BandsMDB(int layer, int level) throws IOException {
		return connectTo_BandsMDB(getBandsMDBFile(layer, level));
	}

	public static String getBandsMDBFile(int layer, int level) {
		return "layer-" + layer + "/" + level + "/regression.mdb";
	}

	public ChannelMDB connectTo_ChannelMDB(int layer, int level)
			throws IOException {
		return connectTo_ChannelMDB(getChannelMDBFile(layer, level));
	}

	public Channel2MDB connectTo_Channel2MDB(int layer, int level)
			throws IOException {
		return connectTo_Channel2MDB(getChannel2MDBFile(layer, level));
	}

	public static String getChannelMDBFile(int layer, int level) {
		return "layer-" + layer + "/" + level + "/channel.mdb";
	}

	public static String getChannel2MDBFile(int layer, int level) {
		return "layer-" + layer + "/" + level + "/channel2.mdb";
	}

	/* END USER SESSION CODE */	

	private BandsMDB internal_connectTo_BandsMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (BandsMDB) _cache.get(file);
			}
			BandsMDB mdb = new BandsMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "1324de2f-d059-4caa-b5b3-09636736be01");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public BandsMDB connectTo_BandsMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_BandsMDB(getFile(filename), bufferSize);
	}
	
	public BandsMDB connectTo_BandsMDB(String filename) throws IOException {
		return connectTo_BandsMDB(filename, 100);
	}

	private PivotMDB internal_connectTo_PivotMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (PivotMDB) _cache.get(file);
			}
			PivotMDB mdb = new PivotMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "fc117a2b-39f1-4fb1-b3ac-356cbf52e9e4");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public PivotMDB connectTo_PivotMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_PivotMDB(getFile(filename), bufferSize);
	}
	
	public PivotMDB connectTo_PivotMDB(String filename) throws IOException {
		return connectTo_PivotMDB(filename, 100);
	}

	private ChannelMDB internal_connectTo_ChannelMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ChannelMDB) _cache.get(file);
			}
			ChannelMDB mdb = new ChannelMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "4295d3b5-f743-4d0e-9c00-7101036ba349");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ChannelMDB connectTo_ChannelMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ChannelMDB(getFile(filename), bufferSize);
	}
	
	public ChannelMDB connectTo_ChannelMDB(String filename) throws IOException {
		return connectTo_ChannelMDB(filename, 100);
	}

	private Channel2MDB internal_connectTo_Channel2MDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (Channel2MDB) _cache.get(file);
			}
			Channel2MDB mdb = new Channel2MDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "d290623f-05b0-4186-8805-b5c37f7850a7");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public Channel2MDB connectTo_Channel2MDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_Channel2MDB(getFile(filename), bufferSize);
	}
	
	public Channel2MDB connectTo_Channel2MDB(String filename) throws IOException {
		return connectTo_Channel2MDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

