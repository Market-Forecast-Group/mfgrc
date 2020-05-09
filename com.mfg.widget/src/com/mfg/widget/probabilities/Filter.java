package com.mfg.widget.probabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Listener;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;

/**
 * represents a filter to include/exclude messages on the log of the
 * probabilities calculations.
 * 
 * @author gardero
 * 
 */
public class Filter implements IProbabilitiesFilter {

	// private static Comparator<ISimpleLogMessage> comp;
	private int scale = 0;
	private int PID = 0;
	private int CID = 0;
	private CNCDir dir = CNCDir.Both;
	private boolean simpleMode = true;
	private List<List<ISimpleLogMessage>> messages;
	private DistributionsContainer distribution;
	private List<ISimpleLogMessage> filteredLog;
	private ProbVer version = ProbVer.ALL;
	@SuppressWarnings("boxing")
	private Integer sctIndex = 0;
	@SuppressWarnings("boxing")
	private Integer bcid = 0;

	public List<ProbabilitiesKey> filterKeys() {
		messages = new ArrayList<>();
		ArrayList<ProbabilitiesKey> res = new ArrayList<>(
				distribution.getAllKeys());
		messages.add(filterMSGs(new ArrayList<>(distribution.getEventsList())));
		for (ProbabilitiesKey probabilitiesKey : res) {
			if (aceptsKey(probabilitiesKey)) {
				List<ISimpleLogMessage> log = distribution.getTargetsMap()
						.get(probabilitiesKey).getLog();
				log = filterMSGs(log);
				messages.add(log);
			}
		}
		return res;
	}

	@Override
	public void apply() {
		if (distribution != null) {
			filterKeys();
			filteredLog = mergeMessages(messages);
		}
		fireEvent();
		System.out.println("log filtered");
	}

	private void fireEvent() {
		for (Listener listener : filterChangedListeners) {
			listener.handleEvent(null);
		}
	}

	private List<Listener> filterChangedListeners = new ArrayList<>();

	public void addFilterChangedListener(Listener listener) {
		filterChangedListeners.add(listener);
	}

	public void remiveFilterChangedListener(Listener listener) {
		filterChangedListeners.remove(listener);
	}

	@Override
	public boolean aceptsKey(ProbabilitiesKey k) {
		return (scale == 0 || (scale == k.getScale() && scale == k
				.getBaseScale()))
				&& (PID == 0 || PID == k.getPatternID())
				&& aceptsCID(k.getClusterID()) && aceptsDir(k.isContrarian());
	}

	@Override
	public boolean aceptsProbVer(ProbVer ver) {
		return version == ProbVer.ALL || version.equals(ver)
				|| ver == ProbVer.ALL;
	}

	public List<ISimpleLogMessage> filterMSGs(List<ISimpleLogMessage> aMessages) {
		ISimpleLogMessage[] a = aMessages.toArray(new ISimpleLogMessage[] {});
		Arrays.sort(a, SimpleLogMessage.comparator());
		List<ISimpleLogMessage> res = new ArrayList<>();
		for (Iterator<ISimpleLogMessage> iterator = Arrays.asList(a).iterator(); iterator
				.hasNext();) {
			ISimpleLogMessage iSimpleLogMessage = iterator.next();
			if ((simpleMode && iSimpleLogMessage.getCategory().equals(
					ISimpleLogMessage.CATEGORY_TARGET))
					|| !iSimpleLogMessage.passFilter(this)) {
				//
			} else
				res.add(iSimpleLogMessage);
		}
		return res;
	}

	public static List<ISimpleLogMessage> mergeMessages(
			List<List<ISimpleLogMessage>> aMessages) {
		if (aMessages.size() == 0)
			return new ArrayList<>();
		// ;
		ArrayList<ISimpleLogMessage> res = new ArrayList<>(aMessages.get(0));
		for (int i = 1; i < aMessages.size(); i++) {
			res = merge2(res, aMessages.get(i));
		}
		return res;
	}

	private static ArrayList<ISimpleLogMessage> merge2(
			ArrayList<ISimpleLogMessage> a, List<ISimpleLogMessage> b) {
		Comparator<? super ISimpleLogMessage> c = SimpleLogMessage.comparator();
		ArrayList<ISimpleLogMessage> res = new ArrayList<>();
		int i = 0, j = 0;
		for (; i < a.size() && j < b.size();) {
			if (c.compare(a.get(i), b.get(j)) < 1) {
				res.add(a.get(i));
				i++;
			} else {
				res.add(b.get(j));
				j++;
			}
		}
		for (; i < a.size();) {
			res.add(a.get(i));
			i++;
		}
		for (; j < b.size();) {
			res.add(b.get(j));
			j++;
		}
		return res;
	}

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public boolean aceptsScale(int ascale) {
		return (scale == 0 || scale == ascale);
	}

	@Override
	public void setScale(int aScale) {
		scale = aScale;
	}

	@Override
	public int getPID() {
		return PID;
	}

	@Override
	public void setPID(int aPID) {
		PID = aPID;
	}

	@Override
	public boolean aceptsPID(int aPID) {
		return (PID == 0 || PID == aPID);
	}

	@Override
	public int getCID() {
		return CID;
	}

	@Override
	public void setCID(int aCID) {
		CID = aCID;
	}

	@Override
	public boolean aceptsCID(int aCID) {
		return (CID == 0 || CID == aCID);
	}

	@SuppressWarnings("boxing")
	@Override
	public boolean aceptsBCID(int aBCID) {
		return (bcid == 0 || bcid == aBCID);
	}

	@SuppressWarnings("boxing")
	@Override
	public boolean aceptsSCT(int aSCT) {
		return (sctIndex == 0 || sctIndex == aSCT);
	}

	@Override
	public CNCDir getDir() {
		return dir;
	}

	@Override
	public void setDir(CNCDir aDir) {
		dir = aDir;
	}

	@Override
	public boolean aceptsDir(boolean contrarian) {
		return (dir == CNCDir.Both || (dir == CNCDir.Contr) == contrarian);
	}

	@Override
	public boolean isAllswings() {
		return simpleMode;
	}

	@Override
	public void setAllswings(boolean aSimpleMode) {
		simpleMode = aSimpleMode;
	}

	public DistributionsContainer getDistribution() {
		return distribution;
	}

	public void setDistribution(DistributionsContainer aDistribution) {
		distribution = aDistribution;
	}

	public List<ISimpleLogMessage> getFilteredLog() {
		return filteredLog;
	}

	public void setFilteredLog(List<ISimpleLogMessage> aFilteredLog) {
		filteredLog = aFilteredLog;
	}

	// public static Comparator<? super ISimpleLogMessage> comparator() {
	// if (comp == null) {
	// comp = new Comparator<ISimpleLogMessage>() {
	// @Override
	// public int compare(ISimpleLogMessage aO1, ISimpleLogMessage aO2) {
	// int r = (int) Math.signum(aO1.getTime() - aO2.getTime());
	// if (r == 0)
	// r = (int) Math
	// .signum(aO1.getTHTime() - aO2.getTHTime());
	// if (r == 0)
	// r = (int) Math.signum(aO1.getLogPriority()
	// - aO2.getLogPriority());
	// if (r == 0)
	// r = (int) Math.signum(aO1.getTimeCPU()
	// - aO2.getTimeCPU());
	// if (r == 0){
	// int r1 = (aO1 instanceof ReachedTargetMessage)?-1:1;
	// int r2 = (aO2 instanceof ReachedTargetMessage)?-1:1;
	// r = r1-r2;
	// }
	// return r;
	// }
	// };
	// }
	// return comp;
	// }

	@Override
	public ProbVer getVersion() {
		return version;
	}

	@Override
	public void setVersion(ProbVer aSelectedItem) {
		version = aSelectedItem;
	}

	@Override
	public void setSCTIndex(Integer aInteger) {
		sctIndex = aInteger;
	}

	@SuppressWarnings("boxing")
	@Override
	public int getSCTIndex() {
		return sctIndex;
	}

	@Override
	public void setBCID(Integer aInteger) {
		bcid = aInteger;
	}

	@SuppressWarnings("boxing")
	@Override
	public int getBCID() {
		return bcid;
	}

	@Override
	public void setProbabilityKey(ProbabilitiesKey aSelectedTargetKey) {
		//
	}

}
