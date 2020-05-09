package com.mfg.widget.probabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mfg.interfaces.probabilities.ProbabilitiesKey;

/**
 * an indexing of the targets that took place in the series. This structure is
 * used on Multi Scale probabilities version to compute the HS cluster and the
 * C/NC direction.
 * 
 * @author gardero
 * 
 */
public class TargetsIndex {

	private List<TargetInfo>[] theIndex;
	private int[] theCursor;

	@SuppressWarnings("unchecked")
	public TargetsIndex(int n) {
		super();
		theIndex = new List[n];
		theCursor = new int[n];
		for (int i = 0; i < n; i++) {
			theIndex[i] = new ArrayList<>();
		}
	}

	/**
	 * adds a target information to the structure.
	 * 
	 * @param time
	 *            the time of the target.
	 * @param target
	 *            the target ID (an integer greater than 0)
	 * @param key
	 *            the key that corresponds to this target.
	 * @param isUP
	 *            if the swing is going up or down.
	 * @param targetsPrices
	 *            the information of the Higher Scales targets to be used on the
	 *            chart.
	 */
	public void addTarget(long time, long thTime, int target,
			ProbabilitiesKey key, boolean isUP,
			ArrayList<HSTargetInfo> targetsPrices) {
		theIndex[key.getScale()].add(new TargetInfo(time, thTime, target, key,
				isUP, targetsPrices));
	}

	/**
	 * finds the target that is located near a specific time.
	 * 
	 * @param time
	 *            the time.
	 * @param scale
	 *            the scale.
	 * @param target
	 *            the target ID
	 * @param readOnly
	 *            if we can modify the cursors to make the search next time
	 *            without considering the targets before the one we find in this
	 *            call.
	 * @return
	 */
	public TargetInfo getTargetNear(long time, int scale, int target,
			boolean readOnly) {
		int tcursor = theCursor[scale];
		while (tcursor < theIndex[scale].size() - 2
				&& (time >= theIndex[scale].get(tcursor + 1).getTime())) {
			tcursor++;
		}
		while (tcursor < theIndex[scale].size() - 1
				&& (target != theIndex[scale].get(tcursor).getTID())) {
			tcursor++;
		}
		// if (target == 1)
		// while (tcursor < theIndex[scale].size() - 1
		// && (1 != theIndex[scale].get(tcursor).getTID())) {
		// tcursor++;
		// }
		if (theIndex[scale].get(tcursor).getThTime() >= time) {
			tcursor = theCursor[scale];
		}
		if (!readOnly)
			theCursor[scale] = tcursor;
		return theIndex[scale].get(tcursor);
	}

	/**
	 * clears the data structure.
	 */
	public void clear() {
		for (List<TargetInfo> e : theIndex) {
			e.clear();
		}
		resetCursors();
		System.out.println("cleared index");
	}

	/***
	 * resets the cursors to the initial position.
	 */
	public void resetCursors() {
		Arrays.fill(theCursor, 0);
	}

	/***
	 * to save the targets information.
	 * 
	 * @author gardero
	 * 
	 */
	public class TargetInfo {
		private long time;
		private long thTime;
		private ProbabilitiesKey key;
		private int TID;
		private ArrayList<HSTargetInfo> targetsPrices;
		private boolean isUP;

		public TargetInfo(long aTime, long athTime, int aTID,
				ProbabilitiesKey aKey, boolean aIsUP,
				ArrayList<HSTargetInfo> aTargetsPrices) {
			super();
			time = aTime;
			thTime = athTime;
			TID = aTID;
			key = aKey;
			targetsPrices = aTargetsPrices;
			this.isUP = aIsUP;
		}

		/**
		 * time of the target.
		 * 
		 * @return
		 */
		public long getTime() {
			return time;
		}

		public void setTime(long aTime) {
			time = aTime;
		}

		public ProbabilitiesKey getKey() {
			return key;
		}

		public void setKey(ProbabilitiesKey aKey) {
			key = aKey;
		}

		public int getTID() {
			return TID;
		}

		public void setTID(int aTID) {
			TID = aTID;
		}

		public ArrayList<HSTargetInfo> getTargetsPrices() {
			return targetsPrices;
		}

		public void setTargetsPrices(ArrayList<HSTargetInfo> aTargetsPrices) {
			targetsPrices = aTargetsPrices;
		}

		public boolean isUP() {
			return isUP;
		}

		public void setUP(boolean aIsUP) {
			isUP = aIsUP;
		}

		public long getThTime() {
			return thTime;
		}

		public void setThTime(long aTthTime) {
			this.thTime = aTthTime;
		}

	}

}
