package com.mfg.inputdb.prices.mdb;

import java.io.File;
import java.io.IOException;
import org.mfg.mdb.runtime.*;
import java.util.*;

/* BEGIN USER IMPORTS */

/* END USER IMPORTS */

public class PriceMDBSession
/* BEGIN USER SESSION HINERITANCE */
extends com.mfg.inputdb.prices.CommonMDBSession
/* END USER SESSION HINERITANCE */ 		{
 	
 	private static final Map<String,String> SIGNATURES;
 	private static final String SCHEMA_JSON = "{\"name\":\"Price\",\"source\":\"\",\"packageName\":\"mdb\",\"bufferSize\":100,\"tables\":[{\"uuid\":\"e19d90be-b803-4fe5-9aa2-766011d2c560\",\"name\":\"Price\",\"columns\":[{\"name\":\"physicalTime\",\"uuid\":\"ae19f703-7ef7-443b-aaa8-2132d36bc0be\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"priceRaw\",\"uuid\":\"f6bcfd1a-4ee2-4bf4-a6e3-2d1a28087863\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"price\",\"uuid\":\"2019ecd7-292b-4f20-b448-30963f4f6e19\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":true,\"formula\":\"Math.abs($$.priceRaw)\"},{\"name\":\"real\",\"uuid\":\"bedf8630-e086-4f7b-9d4d-64c04cb7d08d\",\"type\":\"BOOLEAN\",\"order\":\"NONE\",\"virtual\":true,\"formula\":\"$$.priceRaw > 0\"},{\"name\":\"time\",\"uuid\":\"4169855c-60c5-4925-a536-2a46aaaba76e\",\"type\":\"LONG\",\"order\":\"ASCENDING\",\"virtual\":false,\"formula\":\"\"},{\"name\":\"volume\",\"uuid\":\"b5e7118d-ccd1-4211-807e-110c210c62d7\",\"type\":\"INTEGER\",\"order\":\"NONE\",\"virtual\":false,\"formula\":\"\"}]}]}";
 	
 	static {
 		SIGNATURES = new HashMap<>();
		SIGNATURES.put("e19d90be-b803-4fe5-9aa2-766011d2c560", "ae19f703-7ef7-443b-aaa8-2132d36bc0be LONG; f6bcfd1a-4ee2-4bf4-a6e3-2d1a28087863 INTEGER; 4169855c-60c5-4925-a536-2a46aaaba76e LONG; b5e7118d-ccd1-4211-807e-110c210c62d7 INTEGER; ");
 	}
 	
	public PriceMDBSession(String sessionName, File root) throws IOException {
		super(sessionName, root, SIGNATURES, SCHEMA_JSON);
	}
	
	public PriceMDBSession(String sessionName, File root, SessionMode mode) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON);
	}

/* BEGIN USER SESSION CODE */

	private static final String PROP_DATA_LAYERS_COUNT = "dataLayersCount";
	private static final String PROP_TICK_SIZE = "tickSize";
	private static final String PROP_TICK_SCALE = "tickScale";
	private static final String PROP_DATA_LAYERS_SCALE = "dataLayerScales";
	private static final String PROP_START_REALTIME = "startRealtime";

	private DBSynchronizer _synchronizer;
	private int _dataLayersCount;
	private int _tickSize;
	private int _tickScale;
	private int[] dataLayerScaleMap;
	private Long[] startRealtimes;
	private com.mfg.utils.collections.TimeMap[] _timeMapMap;

	public PriceMDBSession(String sessionName, File root, SessionMode mode,
			boolean temporal, DBSynchronizer synchronizer) throws IOException {
		super(sessionName, root, mode, SIGNATURES, SCHEMA_JSON, temporal);
		_synchronizer = synchronizer;
		_timeMapMap = new com.mfg.utils.collections.TimeMap[] { new com.mfg.utils.collections.TimeMap() };
		readProperties();
	}

	public com.mfg.utils.collections.TimeMap getTimeMap(int dataLayer) {
		return _timeMapMap[dataLayer];
	}

	@Override
	protected void readProperties() {
		if (getProperties().containsKey(PROP_TICK_SIZE)) {
			_tickSize = Integer.parseInt(getProperties().getProperty(
					PROP_TICK_SIZE));
			log("Read tick size (=%d) from databse.",
					Integer.valueOf(_tickSize));
		} else {
			_tickSize = 25; // default tick size
			log("Set default tick size (=%d).", Integer.valueOf(_tickSize));
		}

		if (getProperties().containsKey(PROP_DATA_LAYERS_COUNT)) {
			setDataLayersCount(Integer.parseInt(getProperties().getProperty(
					PROP_DATA_LAYERS_COUNT)));
		}

		if (getProperties().containsKey(PROP_DATA_LAYERS_SCALE)) {
			String str = getProperties().getProperty(PROP_DATA_LAYERS_SCALE);
			str = str.substring(1, str.length() - 1);
			String[] split = str.split(",");
			dataLayerScaleMap = new int[split.length];
			for (int i = 0; i < dataLayerScaleMap.length; i++) {
				dataLayerScaleMap[i] = Integer.parseInt(split[i].trim());
			}
		}

		if (getProperties().containsKey(PROP_START_REALTIME)) {
			String str = getProperties().getProperty(PROP_START_REALTIME);
			str = str.substring(1, str.length() - 1);
			String[] split = str.split(",");
			startRealtimes = new Long[split.length];
			for (int i = 0; i < dataLayerScaleMap.length; i++) {
				try {
					startRealtimes[i] = Long.valueOf(Long.parseLong(split[i]
							.trim()));
				} catch (NumberFormatException e) {
					// this happens when chart is run as stand-along app
					startRealtimes[i] = Long.valueOf(0);

				}
			}
		}
	}

	public DBSynchronizer getSynchronizer() {
		return _synchronizer;
	}

	public Long[] getStartRealtimes() {
		return startRealtimes;
	}

	public void setStartRealtimes(Long[] startRealtime) {
		this.startRealtimes = startRealtime;
		getProperties().setProperty(PROP_START_REALTIME,
				Arrays.toString(startRealtime));
	}

	public int getDataLayersCount() {
		return _dataLayersCount;
	}

	public void setDataLayersCount(int dataLayersCount) {
		this._dataLayersCount = dataLayersCount;
		if (startRealtimes == null) {
			setStartRealtimes(new Long[dataLayersCount]);
		}
		getProperties().setProperty(PROP_DATA_LAYERS_COUNT,
				Long.toString(dataLayersCount));

		_timeMapMap = new com.mfg.utils.collections.TimeMap[_dataLayersCount];
		for (int i = 0; i < _dataLayersCount; i++) {
			_timeMapMap[i] = new com.mfg.utils.collections.TimeMap();
		}
	}

	public int[] getDataLayerScales() {
		return dataLayerScaleMap;
	}

	public void setDataLayerScales(int[] dataLayerScale) {
		this.dataLayerScaleMap = dataLayerScale;
		getProperties().setProperty(PROP_DATA_LAYERS_SCALE,
				Arrays.toString(dataLayerScale));
	}

	public void setTickSize(int tickSize) {
		this._tickSize = tickSize;
		getProperties().setProperty(PROP_TICK_SIZE, Integer.toString(tickSize));
		log("Set tick size (=%d).", Integer.valueOf(tickSize));
	}

	public int getTickSize() {
		return _tickSize;
	}

	public int getTickScale() {
		return _tickScale;
	}

	public void setTickScale(int tickScale) {
		this._tickScale = tickScale;
		getProperties().setProperty(PROP_TICK_SCALE,
				Integer.toString(tickScale));
		log("Set tick scale (=%d).", Integer.valueOf(tickScale));
	}

	// --- connect methods

	public static String getPriceMDBFile(int layer) {
		return "/layer-" + layer + "/prices.mdb";
	}

	public PriceMDB connectTo_PriceMDB(int layer) throws IOException {
		return connectTo_PriceMDB(getPriceMDBFile(layer));
	}

	/* END USER SESSION CODE */	

	private PriceMDB internal_connectTo_PriceMDB(File file, int bufferSize) throws IOException {
	
		if (!file.getAbsolutePath().contains(getRoot().getAbsolutePath())) 
			throw new IllegalArgumentException("The file is not inside the database folder: " + file);
	
		_readLock.lock();
		try {
			if (!_open) {
				throw new IOException("Trying to access a closed session.");
			}
				
			if (_cache.containsKey(file)) {
				return (PriceMDB) _cache.get(file);
			}
			PriceMDB mdb = new PriceMDB(this, file, bufferSize, getMode()); 
			_cache.put(file, mdb);
			if (!_memory) {
				createFileMetadata(file, "e19d90be-b803-4fe5-9aa2-766011d2c560");
			}
			return mdb;
		} finally {
			_readLock.unlock();
		}
	}

	public PriceMDB connectTo_PriceMDB(String filename, int bufferSize) throws IOException {	
		return internal_connectTo_PriceMDB(getFile(filename), bufferSize);
	}
	
	public PriceMDB connectTo_PriceMDB(String filename) throws IOException {
		return connectTo_PriceMDB(filename, 100);
	}
	private long modificationToken = 0;

	public void modified() {
		modificationToken++;
	}

	public long getModificatonToken() {
		return modificationToken;
	}
}

