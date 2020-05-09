package com.mfg.tradingdb.mdb;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */
/* END USER IMPORTS */

public class TradingMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends com.mfg.inputdb.prices.CommonMDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"Trading\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"7d81f05b-afc7-4029-8327-53887b876e5d\",\"name\":\"ProbabilityPoint\",\"columns\":[{\"name\":\"time\",\"uuid\":\"5f5cf441-ee7b-4d9f-a172-a4cc8acb8abc\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"f451d3c5-6be4-4b7a-a271-ed4b948584a7\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"a44787d1-d4c4-4c88-9f70-b1cf39899cd8\",\"name\":\"ProbabilityInfo\",\"columns\":[{\"name\":\"time\",\"uuid\":\"090dfe26-3aa6-4db9-b80b-989fd500a8e0\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"type\",\"uuid\":\"c1e82117-5deb-43be-878f-fc2d3c06ad6f\",\"type\":\"BYTE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"positive\",\"uuid\":\"7ee435c8-0c75-45ef-84e5-7d45f3359bf5\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"probabilitiesCount\",\"uuid\":\"208031f0-778e-4d5e-b449-08ec56919a59\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"probabilitiesIndex\",\"uuid\":\"48a58098-b32d-4cdb-a5ab-f8aeeb451381\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"eaf180fa-8f11-422f-a24a-8e02e714b181\",\"name\":\"TriggerExpectedValue\",\"columns\":[{\"name\":\"negValue\",\"uuid\":\"065862c6-cf64-4f5b-9a6f-6dc6fc8c905f\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posValue\",\"uuid\":\"2430c2d5-9fd6-49a9-9801-206961ce00d9\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"d04704a5-486d-4bdb-ab54-5a592f7cbc4c\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negSwingProjection\",\"uuid\":\"bdb52934-4175-4e02-a097-2aba8bd12a33\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posSwingProjection\",\"uuid\":\"8f113ff7-4d54-44c3-ba11-41fb1c219304\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"1278b3a6-e419-4db7-9e9e-53467346c009\",\"name\":\"InterpPriceProb\",\"columns\":[{\"name\":\"time\",\"uuid\":\"c042e202-aa65-4a73-9a12-15ea42bdb8ba\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"02873081-2d40-4e61-b641-b11f60f785ec\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"d1e2394b-8cd7-46c8-b2fb-aed72b8285f2\",\"name\":\"Probability\",\"columns\":[{\"name\":\"time\",\"uuid\":\"75d8f8d8-d4f7-4f96-a7ba-af274af08773\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posPrice\",\"uuid\":\"343ec9da-e456-4bbb-a8c7-eaa8b3718a17\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negPrice\",\"uuid\":\"5b8e9286-75b2-48ad-839f-58971c5e491f\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posTradeDirection\",\"uuid\":\"2a95fba7-e054-4696-af68-427cc75e669b\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"0e554c43-8585-44cf-ae21-0ca2cce7fd5e\",\"name\":\"ProbabilityPercent\",\"columns\":[{\"name\":\"posTHPrice\",\"uuid\":\"f2d1a2d5-f062-4a4c-80de-19586841072a\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negTHPrice\",\"uuid\":\"1bdea4e8-2432-4f47-80ed-276ed4e6c0d3\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posCurrentPrice\",\"uuid\":\"5298d729-05c3-421c-914e-7dbacba5b556\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"negCurrentPrice\",\"uuid\":\"3015c304-93d1-4d41-a4e6-c32d524bc286\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"time\",\"uuid\":\"54ec14e7-aef6-4057-b017-e25bfa07819e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"posTradeDirection\",\"uuid\":\"96b5cd91-c72d-49ce-9551-1ec46d845265\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"265f6fc3-22c8-42c5-84e7-ed6164bb98ce\",\"name\":\"Trade\",\"columns\":[{\"name\":\"openTime\",\"uuid\":\"6aee131c-6e0a-481c-ba9d-a6a6938c59da\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openPrice\",\"uuid\":\"3ade2d0a-917d-4ec1-b08c-b650a6c65fad\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closeTime\",\"uuid\":\"c8347ed1-6c8f-4e2b-8f5c-bf3128f04700\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closePrice\",\"uuid\":\"45f94d5b-f4d6-4163-9012-c7b611aac721\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isGain\",\"uuid\":\"f4bea829-6a2b-4953-98e8-fd2e2726391c\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isClosed\",\"uuid\":\"ea85758a-3f02-446e-9533-e4901ad18cc6\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"isLong\",\"uuid\":\"d6f69786-fa58-416f-abe8-3f2c5ab780cb\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"orderId\",\"uuid\":\"5c9b0859-573c-42d3-bfde-9fc4fdf60cb4\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openingCount\",\"uuid\":\"5631f68b-0f46-4209-983c-c2464b624516\",\"type\":\"BYTE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0\",\"uuid\":\"96e789e8-fdc2-4cf7-b7d9-88507c5c349d\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1\",\"uuid\":\"a3729c6b-ebd8-4f34-a3b8-a1dd95b806a2\",\"type\":\"LONG\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"openPhysicalTime\",\"uuid\":\"afba7d5a-28b2-4ae6-b091-ee87dc6f6b52\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"closePhysicalTime\",\"uuid\":\"0d4a22e1-5d86-4003-8ef7-f566aae45dbb\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"eventPhysicalTime\",\"uuid\":\"02d888ef-9720-4d91-8c3f-db3c8a8a4230\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0_childType\",\"uuid\":\"ea4b87f9-1d13-456e-b8fb-ccf5198b4b20\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1_childType\",\"uuid\":\"0b335ed9-9d6c-4b56-820e-4351fed20214\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening0_orderId\",\"uuid\":\"fa503e8b-8ffb-48aa-80c9-63c4ff7cd902\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"opening1_orderId\",\"uuid\":\"20a9f688-e7cb-4dd9-b7e7-c04afcdbaf46\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]},{\"uuid\":\"bd35377d-bebc-463e-a6ae-e298cc5bcf14\",\"name\":\"Equity\",\"columns\":[{\"name\":\"total\",\"uuid\":\"02d672e4-80e5-4188-a7d3-b1d72b9c45b5\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"totalPrice\",\"uuid\":\"6261394f-a011-4648-94a6-c4de6c03e6a8\",\"type\":\"DOUBLE\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"fakeTime\",\"uuid\":\"a9b51ae3-c76f-4088-9a07-bc36cf636daf\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"index\",\"uuid\":\"7ff0c7a3-0405-4df6-bfd5-902ff5dd5a8e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":true,\"formula\":\"$pos$\"},{\"name\":\"physicalTime\",\"uuid\":\"1169f20c-3975-4c3f-b7ff-ea15c8adf193\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("7d81f05b-afc7-4029-8327-53887b876e5d", "5f5cf441-ee7b-4d9f-a172-a4cc8acb8abc LONG; f451d3c5-6be4-4b7a-a271-ed4b948584a7 DOUBLE; ");
		SIGNATURES.put("a44787d1-d4c4-4c88-9f70-b1cf39899cd8", "090dfe26-3aa6-4db9-b80b-989fd500a8e0 LONG; c1e82117-5deb-43be-878f-fc2d3c06ad6f BYTE; 7ee435c8-0c75-45ef-84e5-7d45f3359bf5 BOOLEAN; 208031f0-778e-4d5e-b449-08ec56919a59 INTEGER; 48a58098-b32d-4cdb-a5ab-f8aeeb451381 INTEGER; ");
		SIGNATURES.put("eaf180fa-8f11-422f-a24a-8e02e714b181", "065862c6-cf64-4f5b-9a6f-6dc6fc8c905f DOUBLE; 2430c2d5-9fd6-49a9-9801-206961ce00d9 DOUBLE; d04704a5-486d-4bdb-ab54-5a592f7cbc4c LONG; bdb52934-4175-4e02-a097-2aba8bd12a33 DOUBLE; 8f113ff7-4d54-44c3-ba11-41fb1c219304 DOUBLE; ");
		SIGNATURES.put("1278b3a6-e419-4db7-9e9e-53467346c009", "c042e202-aa65-4a73-9a12-15ea42bdb8ba LONG; 02873081-2d40-4e61-b641-b11f60f785ec DOUBLE; ");
		SIGNATURES.put("d1e2394b-8cd7-46c8-b2fb-aed72b8285f2", "75d8f8d8-d4f7-4f96-a7ba-af274af08773 LONG; 343ec9da-e456-4bbb-a8c7-eaa8b3718a17 DOUBLE; 5b8e9286-75b2-48ad-839f-58971c5e491f DOUBLE; 2a95fba7-e054-4696-af68-427cc75e669b BOOLEAN; ");
		SIGNATURES.put("0e554c43-8585-44cf-ae21-0ca2cce7fd5e", "f2d1a2d5-f062-4a4c-80de-19586841072a LONG; 1bdea4e8-2432-4f47-80ed-276ed4e6c0d3 LONG; 5298d729-05c3-421c-914e-7dbacba5b556 LONG; 3015c304-93d1-4d41-a4e6-c32d524bc286 LONG; 54ec14e7-aef6-4057-b017-e25bfa07819e LONG; 96b5cd91-c72d-49ce-9551-1ec46d845265 BOOLEAN; ");
		SIGNATURES.put("265f6fc3-22c8-42c5-84e7-ed6164bb98ce", "6aee131c-6e0a-481c-ba9d-a6a6938c59da LONG; 3ade2d0a-917d-4ec1-b08c-b650a6c65fad DOUBLE; c8347ed1-6c8f-4e2b-8f5c-bf3128f04700 LONG; 45f94d5b-f4d6-4163-9012-c7b611aac721 DOUBLE; f4bea829-6a2b-4953-98e8-fd2e2726391c BOOLEAN; ea85758a-3f02-446e-9533-e4901ad18cc6 BOOLEAN; d6f69786-fa58-416f-abe8-3f2c5ab780cb BOOLEAN; 5c9b0859-573c-42d3-bfde-9fc4fdf60cb4 INTEGER; 5631f68b-0f46-4209-983c-c2464b624516 BYTE; 96e789e8-fdc2-4cf7-b7d9-88507c5c349d LONG; a3729c6b-ebd8-4f34-a3b8-a1dd95b806a2 LONG; afba7d5a-28b2-4ae6-b091-ee87dc6f6b52 LONG; 0d4a22e1-5d86-4003-8ef7-f566aae45dbb LONG; 02d888ef-9720-4d91-8c3f-db3c8a8a4230 LONG; ea4b87f9-1d13-456e-b8fb-ccf5198b4b20 INTEGER; 0b335ed9-9d6c-4b56-820e-4351fed20214 INTEGER; fa503e8b-8ffb-48aa-80c9-63c4ff7cd902 INTEGER; 20a9f688-e7cb-4dd9-b7e7-c04afcdbaf46 INTEGER; ");
		SIGNATURES.put("bd35377d-bebc-463e-a6ae-e298cc5bcf14", "02d672e4-80e5-4188-a7d3-b1d72b9c45b5 DOUBLE; 6261394f-a011-4648-94a6-c4de6c03e6a8 DOUBLE; a9b51ae3-c76f-4088-9a07-bc36cf636daf LONG; 1169f20c-3975-4c3f-b7ff-ea15c8adf193 LONG; ");
 	}
 	
	public TradingMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public TradingMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */

	public static final String PROP_SCALES_COUNT = "scalesCount";
	private static final String PROP_IS_PERCENT_PROB_MODE = "isPercentProbabilityMode";
	private static final String PROP_IS_CONDITIONAL_PROB_LINES_ONLY = "isConditionalProbabilityLinesOnly";

	public TradingMDBSession(String sessionName, File root, SessionMode mode,
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

	public boolean isPercentProbabilityMode() {
		return Boolean.parseBoolean(getProperties().getProperty(
				PROP_IS_PERCENT_PROB_MODE, Boolean.toString(false)));
	}

	public void setPercentProbabilityMode(boolean percentProbabilityMode) {
		getProperties().setProperty(PROP_IS_PERCENT_PROB_MODE,
				Boolean.toString(percentProbabilityMode));
	}

	public boolean isConditionalProbabilitiesOnly() {
		return Boolean.parseBoolean(getProperties().getProperty(
				PROP_IS_CONDITIONAL_PROB_LINES_ONLY, Boolean.toString(false)));
	}

	public void setConditionalProbabilitiesOnly(
			boolean conditionalProbabilitiesOnly) {
		getProperties().setProperty(PROP_IS_CONDITIONAL_PROB_LINES_ONLY,
				Boolean.toString(conditionalProbabilitiesOnly));
	}

	// -- connect methods

	public ProbabilityMDB connectTo_ProbabilityMDB(int level)
			throws IOException {
		return connectTo_ProbabilityMDB(getProbabilityFile(level));
	}

	public static String getProbabilityFile(int level) {
		return level + "/probs.mdb";
	}

	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(int level)
			throws IOException {
		return connectTo_ProbabilityPercentMDB(getProbabilityPercentFile(level));
	}

	public static String getProbabilityPercentFile(int level) {
		return level + "/probs-percent.mdb";
	}

	public TradeMDB connectTo_TradeMDB() throws IOException {
		return connectTo_TradeMDB(getTradeFile());
	}

	public static String getTradeFile() {
		return "trades.mdb";
	}

	public EquityMDB connectTo_EquityMDB() throws IOException {
		return connectTo_EquityMDB(getEquityFile());
	}

	public static String getEquityFile() {
		return "equity.mdb";
	}

	/* END USER SESSION CODE */	

	private ProbabilityPointMDB internal_connectTo_ProbabilityPointMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityPointMDB) _cache.get(file);
			}
			ProbabilityPointMDB mdb = new ProbabilityPointMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "7d81f05b-afc7-4029-8327-53887b876e5d");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityPointMDB connectTo_ProbabilityPointMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityPointMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityPointMDB connectTo_ProbabilityPointMDB(String filename) throws IOException {
		return connectTo_ProbabilityPointMDB(filename, 100);
	}

	private ProbabilityInfoMDB internal_connectTo_ProbabilityInfoMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityInfoMDB) _cache.get(file);
			}
			ProbabilityInfoMDB mdb = new ProbabilityInfoMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "a44787d1-d4c4-4c88-9f70-b1cf39899cd8");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityInfoMDB connectTo_ProbabilityInfoMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityInfoMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityInfoMDB connectTo_ProbabilityInfoMDB(String filename) throws IOException {
		return connectTo_ProbabilityInfoMDB(filename, 100);
	}

	private TriggerExpectedValueMDB internal_connectTo_TriggerExpectedValueMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (TriggerExpectedValueMDB) _cache.get(file);
			}
			TriggerExpectedValueMDB mdb = new TriggerExpectedValueMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "eaf180fa-8f11-422f-a24a-8e02e714b181");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public TriggerExpectedValueMDB connectTo_TriggerExpectedValueMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_TriggerExpectedValueMDB(getFile(filename), bufferSize);
	}
	
	public TriggerExpectedValueMDB connectTo_TriggerExpectedValueMDB(String filename) throws IOException {
		return connectTo_TriggerExpectedValueMDB(filename, 100);
	}

	private InterpPriceProbMDB internal_connectTo_InterpPriceProbMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (InterpPriceProbMDB) _cache.get(file);
			}
			InterpPriceProbMDB mdb = new InterpPriceProbMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "1278b3a6-e419-4db7-9e9e-53467346c009");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public InterpPriceProbMDB connectTo_InterpPriceProbMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_InterpPriceProbMDB(getFile(filename), bufferSize);
	}
	
	public InterpPriceProbMDB connectTo_InterpPriceProbMDB(String filename) throws IOException {
		return connectTo_InterpPriceProbMDB(filename, 100);
	}

	private ProbabilityMDB internal_connectTo_ProbabilityMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityMDB) _cache.get(file);
			}
			ProbabilityMDB mdb = new ProbabilityMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "d1e2394b-8cd7-46c8-b2fb-aed72b8285f2");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityMDB connectTo_ProbabilityMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityMDB connectTo_ProbabilityMDB(String filename) throws IOException {
		return connectTo_ProbabilityMDB(filename, 100);
	}

	private ProbabilityPercentMDB internal_connectTo_ProbabilityPercentMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (ProbabilityPercentMDB) _cache.get(file);
			}
			ProbabilityPercentMDB mdb = new ProbabilityPercentMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "0e554c43-8585-44cf-ae21-0ca2cce7fd5e");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_ProbabilityPercentMDB(getFile(filename), bufferSize);
	}
	
	public ProbabilityPercentMDB connectTo_ProbabilityPercentMDB(String filename) throws IOException {
		return connectTo_ProbabilityPercentMDB(filename, 100);
	}

	private TradeMDB internal_connectTo_TradeMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (TradeMDB) _cache.get(file);
			}
			TradeMDB mdb = new TradeMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "265f6fc3-22c8-42c5-84e7-ed6164bb98ce");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public TradeMDB connectTo_TradeMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_TradeMDB(getFile(filename), bufferSize);
	}
	
	public TradeMDB connectTo_TradeMDB(String filename) throws IOException {
		return connectTo_TradeMDB(filename, 100);
	}

	private EquityMDB internal_connectTo_EquityMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (EquityMDB) _cache.get(file);
			}
			EquityMDB mdb = new EquityMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "bd35377d-bebc-463e-a6ae-e298cc5bcf14");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public EquityMDB connectTo_EquityMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_EquityMDB(getFile(filename), bufferSize);
	}
	
	public EquityMDB connectTo_EquityMDB(String filename) throws IOException {
		return connectTo_EquityMDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

