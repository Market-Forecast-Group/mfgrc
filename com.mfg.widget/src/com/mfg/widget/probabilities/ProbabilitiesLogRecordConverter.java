package com.mfg.widget.probabilities;

import java.util.HashMap;

import com.mfg.interfaces.probabilities.IProbabilitiesFilter.ProbVer;
import com.mfg.logger.LogRecordConverter;
import com.mfg.logger.mdb.LogMDB.Record;
import com.mfg.widget.probabilities.SimpleLogMessage.KeyLogMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.SCSectionMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.THLogMessage;

public class ProbabilitiesLogRecordConverter extends LogRecordConverter {

	@SuppressWarnings("rawtypes")
	private HashMap<Integer, Class> classMapIC = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private HashMap<Class, Integer> classMapCI = new HashMap<>();
	private HashMap<Integer, ProbVer> probVersionMap = new HashMap<>();

	public ProbabilitiesLogRecordConverter() {
		super();
		for (ProbVer v : ProbVer.values()) {
			addProbVersion(v.ordinal(), v);
		}
		addClass(SimpleLogMessage.SCSectionMessage.class);
		addClass(SimpleLogMessage.THLogMessage.class);
		addClass(SimpleLogMessage.NonReachedTargetMessage.class);
		addClass(SimpleLogMessage.ReachedTargetMessage.class);
	}

	@SuppressWarnings("boxing")
	@Override
	public Object getMessage(Record aMdbRecord) {
		SimpleLogMessage slm = null;
		try {
			slm = (SimpleLogMessage) classMapIC.get(aMdbRecord.classID)
					.newInstance();
			slm.setTime(aMdbRecord.time);
			slm.setPrice(aMdbRecord.price);
			slm.setTHTime(aMdbRecord.thTime);
			slm.setTHPrice(aMdbRecord.thPrice);
			slm.setTargetPrice(aMdbRecord.targetPrice);
			slm.setTimeCPU(aMdbRecord.timeCPU);
			slm.setProbVersion(probVersionMap.get(aMdbRecord.probVersion));
			slm.setMessage(aMdbRecord.strMessage);
			if (slm instanceof SimpleLogMessage.SCSectionMessage) {
				SCSectionMessage scslm = (SCSectionMessage) slm;
				scslm.setHHLLIndex(aMdbRecord.hhllIndex);
				scslm.setTHIndex(aMdbRecord.thIndex);
				scslm.setMessageIndex(aMdbRecord.msgIndex);
				scslm.setBaseScaleCluster(aMdbRecord.baseScaleCluster);
				scslm.setScale(aMdbRecord.scale);
			} else if (slm instanceof SimpleLogMessage.KeyLogMessage) {
				KeyLogMessage kslm = (KeyLogMessage) slm;
				kslm.setPatternID(aMdbRecord.patternID);
				kslm.setClusterID(aMdbRecord.clusterID);
				kslm.setContrarian(aMdbRecord.dirContrarian);
				kslm.setScale(aMdbRecord.scale);
				kslm.setP0Time(aMdbRecord.p0Time);
				kslm.setP0Price(aMdbRecord.p0Price);
				kslm.setPm1Time(aMdbRecord.pm1Time);
				kslm.setPm1Price(aMdbRecord.pm1Price);
			} else if (slm instanceof SimpleLogMessage.THLogMessage) {
				THLogMessage thslm = (THLogMessage) slm;
				thslm.setScale(aMdbRecord.scale);
				thslm.setPID(aMdbRecord.pid);
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return slm;
	}

	@SuppressWarnings("boxing")
	@Override
	public void fillMDBRecord(Object aMessage, Record aMdbRecord) {
		if (aMessage instanceof SimpleLogMessage) {
			SimpleLogMessage slm = (SimpleLogMessage) aMessage;
			aMdbRecord.time = slm.getTime();
			aMdbRecord.price = slm.getPrice();
			aMdbRecord.thTime = slm.getTHTime();
			aMdbRecord.thPrice = slm.getTHPrice();
			aMdbRecord.targetPrice = slm.getTargetPrice();
			aMdbRecord.timeCPU = slm.getTimeCPU();
			addProbVersion(slm.getProbVersion().ordinal(), slm.getProbVersion());
			aMdbRecord.probVersion = slm.getProbVersion().ordinal();
			aMdbRecord.classID = classMapCI.get(slm.getClass());
			aMdbRecord.strMessage = slm.getMessage();
			if (slm instanceof SimpleLogMessage.SCSectionMessage) {
				SCSectionMessage scslm = (SCSectionMessage) slm;
				aMdbRecord.hhllIndex = scslm.getHHLLIndex();
				aMdbRecord.thIndex = scslm.getTHIndex();
				aMdbRecord.msgIndex = scslm.getMessageIndex();
				aMdbRecord.baseScaleCluster = scslm.getBaseScaleCluster();
				aMdbRecord.scale = scslm.getScale();
			} else if (slm instanceof SimpleLogMessage.KeyLogMessage) {
				KeyLogMessage kslm = (KeyLogMessage) slm;
				aMdbRecord.patternID = kslm.getPatternID();
				aMdbRecord.clusterID = kslm.getClusterID();
				aMdbRecord.dirContrarian = kslm.isContrarian();
				aMdbRecord.scale = kslm.getScale();
				aMdbRecord.p0Time = kslm.getP0Time();
				aMdbRecord.p0Price = kslm.getP0Price();
				aMdbRecord.pm1Time = kslm.getPm1Time();
				aMdbRecord.pm1Price = kslm.getPm1Price();
			} else if (slm instanceof SimpleLogMessage.THLogMessage) {
				THLogMessage thslm = (THLogMessage) slm;
				aMdbRecord.scale = thslm.getScale();
				aMdbRecord.pid = thslm.getPID();
			}
		}
	}

	@SuppressWarnings("boxing")
	private void addClass(@SuppressWarnings("rawtypes") Class aClass) {
		int id = classMapCI.size();
		classMapCI.put(aClass, id);
		classMapIC.put(id, aClass);
	}

	@SuppressWarnings("boxing")
	private void addProbVersion(int aOrdinal, ProbVer value) {
		if (!probVersionMap.containsKey(aOrdinal))
			probVersionMap.put(aOrdinal, value);
	}

	@SuppressWarnings("rawtypes")
	public HashMap<Integer, Class> getClassMapIC() {
		return classMapIC;
	}

	public void setClassMapIC(
			@SuppressWarnings("rawtypes") HashMap<Integer, Class> aClassMapIC) {
		classMapIC = aClassMapIC;
	}

	@SuppressWarnings("rawtypes")
	public HashMap<Class, Integer> getClassMapCI() {
		return classMapCI;
	}

	public void setClassMapCI(
			@SuppressWarnings("rawtypes") HashMap<Class, Integer> aClassMapCI) {
		classMapCI = aClassMapCI;
	}

	public HashMap<Integer, ProbVer> getProbVersionMap() {
		return probVersionMap;
	}

	public void setProbVersionMap(HashMap<Integer, ProbVer> aProbVersionMap) {
		probVersionMap = aProbVersionMap;
	}

}
