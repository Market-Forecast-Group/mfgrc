package com.mfg.widget.probabilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.garret.perst.Persistent;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.probabilities.IElement.IDetailedElement;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter.ProbVer;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.utils.ui.HtmlUtils;

/**
 * @author gardero
 * 
 */
public abstract class SimpleLogMessage extends Persistent implements
		ISimpleLogMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Comparator<ISimpleLogMessage> comp;
	private long time;
	private long price;
	private long targetPrice = -1;
	private long THtime;
	private long THprice;
	private String category;
	private String message;
	private long timeCPU;
	private ProbVer probVersion = ProbVer.ALL;

	public SimpleLogMessage() {
		super();
	}

	public SimpleLogMessage(long aTime, long aPrice, String aCategory,
			String aMessage) {
		super();
		time = aTime;
		price = aPrice;
		THtime = -1;
		THprice = -1;
		category = aCategory;
		message = aMessage;
		timeCPU = System.currentTimeMillis();
	}

	public SimpleLogMessage(long aTime, long aPrice, long aTargetPrice,
			String aCategory, String aMessage) {
		super();
		time = aTime;
		price = aPrice;
		THtime = -1;
		THprice = -1;
		category = aCategory;
		message = aMessage;
		targetPrice = aTargetPrice;
	}

	public SimpleLogMessage(long aTime, long aPrice, long aTHtime,
			long aTHprice, String aCategory, String aMessage) {
		super();
		time = aTime;
		price = aPrice;
		THtime = aTHtime;
		THprice = aTHprice;
		category = aCategory;
		message = aMessage;
	}

	public SimpleLogMessage(long aTime, long aPrice, long aTHtime,
			long aTHprice, long aTargetPrice, String aCategory, String aMessage) {
		super();
		time = aTime;
		price = aPrice;
		THtime = aTHtime;
		THprice = aTHprice;
		category = aCategory;
		message = aMessage;
		targetPrice = aTargetPrice;
	}

	@Override
	public String toString() {
		return "" + time + "\t" + price + "\t" + THtime + "\t" + THprice + "\t"
				+ targetPrice + "\t" + category + "\t" + message;
	}

	public ProbVer getProbVersion() {
		return probVersion;
	}

	public void setProbVersion(ProbVer aProbVersion) {
		probVersion = aProbVersion;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public long getPrice() {
		return price;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String aMsg) {
		message = aMsg;
	}

	@Override
	public long getTHTime() {
		return THtime;
	}

	@Override
	public long getTHPrice() {
		return THprice;
	}

	@Override
	public void setTHTime(long aTHtime) {
		THtime = aTHtime;
	}

	@Override
	public void setTHPrice(long aTHprice) {
		THprice = aTHprice;
	}

	@Override
	public void setTime(long aTime) {
		time = aTime;
	}

	@Override
	public void setPrice(long aPrice) {
		price = aPrice;
	}

	@Override
	public void setTargetPrice(long aTargetPrice) {
		targetPrice = aTargetPrice;
	}

	@Override
	public void setCategory(String aCategory) {
		category = aCategory;
	}

	@Override
	public void setTimeCPU(long aTimeCPU) {
		timeCPU = aTimeCPU;
	}

	@Override
	public boolean passFilter(IProbabilitiesFilter f) {
		return f.aceptsProbVer(probVersion);
	}

	public static class THLogMessage extends SimpleLogMessage {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int scale;
		private int PID;

		public THLogMessage() {
			super();
		}

		public THLogMessage(int scale1, int PID1, long aTime, long aPrice,
				String aCategory, String aMessage) {
			super(aTime, aPrice, aCategory, aMessage);
			this.scale = scale1;
			this.PID = PID1;
		}

		public THLogMessage(int scale1, int PID1, long aTime, long aPrice,
				long aTHtime, long aTHprice, String aCategory, String aMessage) {
			super(aTime, aPrice, aTHtime, aTHprice, aCategory, aMessage);
			this.scale = scale1;
			this.PID = PID1;
		}

		@Override
		public boolean passFilter(IProbabilitiesFilter aF) {
			return super.passFilter(aF) && aF.aceptsScale(scale)
					&& aF.aceptsPID(PID);
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int aScale) {
			scale = aScale;
		}

		public int getPID() {
			return PID;
		}

		public void setPID(int aPID) {
			PID = aPID;
		}

	}

	public abstract static class KeyLogMessage extends SimpleLogMessage {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int clusterID, scale;
		private int patternID;
		private boolean contrarian;

		private transient IDetailedElement element;
		private long p0Time;
		private long p0Price;
		private long pm1Time;
		private long pm1Price;
		private List<String> visitedTargetPricesText;
		private ArrayList<HSTargetInfo> visitedTargetPrices;

		public KeyLogMessage() {
			super();
		}

		public KeyLogMessage(ProbabilitiesKey aKey, long aTime, long aPrice,
				long aTHtime, long aTHprice, long aP0Time, long aP0Price,
				long aPm1Time, long aPm1Price, long aTargetPrice,
				String aCategory, String aMessage) {
			super(aTime, aPrice, aTHtime, aTHprice, aTargetPrice, aCategory,
					aMessage);
			this.patternID = aKey.getPatternID();
			this.clusterID = aKey.getClusterID();
			this.contrarian = aKey.isContrarian();
			this.scale = aKey.getScale();
			p0Time = aP0Time;
			p0Price = aP0Price;
			pm1Time = aPm1Time;
			pm1Price = aPm1Price;
		}

		public KeyLogMessage(ProbabilitiesKey aKey, long aTime, long aPrice,
				long aTHtime, long aTHprice, long aP0Time, long aP0Price,
				String aCategory, String aMessage) {
			super(aTime, aPrice, aTHtime, aTHprice, aCategory, aMessage);
			this.patternID = aKey.getPatternID();
			this.clusterID = aKey.getClusterID();
			this.contrarian = aKey.isContrarian();
			this.scale = aKey.getScale();
			p0Time = aP0Time;
			p0Price = aP0Price;
		}

		public int getClusterID() {
			return clusterID;
		}

		public void setClusterID(int aClusterID) {
			clusterID = aClusterID;
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int aScale) {
			scale = aScale;
		}

		public int getPatternID() {
			return patternID;
		}

		public void setPatternID(int aPatternID) {
			patternID = aPatternID;
		}

		public boolean isContrarian() {
			return contrarian;
		}

		public void setContrarian(boolean aContrarian) {
			contrarian = aContrarian;
		}

		public List<String> getVisitedTargetPricesText() {
			return visitedTargetPricesText;
		}

		public void setVisitedTargetPricesText(List<String> aVisitedTargetPrices) {
			visitedTargetPricesText = aVisitedTargetPrices;
		}

		public ArrayList<HSTargetInfo> getVisitedTargetPrices() {
			return visitedTargetPrices;
		}

		protected void setVisitedTargetPrices(
				ArrayList<HSTargetInfo> aTargetsPrices) {
			visitedTargetPrices = aTargetsPrices;
			visitedTargetPricesText = new ArrayList<>();
			if (aTargetsPrices != null)
				for (HSTargetInfo hsTargetInfo : aTargetsPrices) {
					visitedTargetPricesText.add(hsTargetInfo.toString());
				}
		}

		public IDetailedElement getElement() {
			return element;
		}

		public void setElement(IDetailedElement aElement) {
			element = aElement;
			// buildTargetRecords();
		}

		// public void buildTargetRecords() {
		// int level = getKey().getScale();
		// _map = new Record[level + getVisitedTargetPrices().size() + 1];
		// Record record = new Record(getTime(), getTargetPrice(), getP0Time(),
		// getP0Price(), getPm1Time(), getPm1Price(), true, null);
		// _map[level] = record;
		// List<HSTargetInfo> l = getVisitedTargetPrices();
		// for (int i = l.size() - 1; i >= 0; i--) {
		// HSTargetInfo t = l.get(i);
		// level++;
		// IDetailedElement e = getElement();
		// record = new Record(getTime(), t.getPrice(), e.getPivotTime(0,
		// level), e.getPivotPrice(0, level), e.getPivotTime(1, level),
		// e.getPivotPrice(1, level), true, null);
		// _map[level] = record;
		// }
		// }
		//
		//
		// public Record[] getTargetRecords() {
		// return _map;
		// }

		@Override
		public boolean passFilter(IProbabilitiesFilter aF) {
			return super.passFilter(aF) && aF.aceptsCID(clusterID)
					&& aF.aceptsDir(contrarian) && aF.aceptsScale(scale)
					&& aF.aceptsPID(patternID);
			// aF.aceptsKey(key);
		}

		// public ProbabilitiesKey getKey() {
		// return key;
		// }
		//
		// public void setKey(ProbabilitiesKey aKey) {
		// key = aKey;
		// }

		public long getP0Time() {
			return p0Time;
		}

		public void setP0Time(long aP0Time) {
			p0Time = aP0Time;
		}

		public long getP0Price() {
			return p0Price;
		}

		public void setP0Price(long aP0Price) {
			p0Price = aP0Price;
		}

		public long getPm1Time() {
			return pm1Time;
		}

		public void setPm1Time(long aPm1Time) {
			pm1Time = aPm1Time;
		}

		public long getPm1Price() {
			return pm1Price;
		}

		public void setPm1Price(long aPm1Price) {
			pm1Price = aPm1Price;
		}

	}

	public static class ReachedTargetMessage extends KeyLogMessage {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ReachedTargetMessage(ProbabilitiesKey aKey,
				ArrayList<HSTargetInfo> aTargetsPrices, long aTime,
				long aPrice, long aTHtime, long aTHprice, long aP0Time,
				long aP0Price, long aPm1Time, long aPm1Price,
				long aTargetPrice, String aCategory, String aMessage) {
			super(aKey, aTime, aPrice, aTHtime, aTHprice, aP0Time, aP0Price,
					aPm1Time, aPm1Price, aTargetPrice, aCategory, aMessage);
			setVisitedTargetPrices(aTargetsPrices);
			setProbVersion(ProbVer.Targets);
		}

		public ReachedTargetMessage() {
			super();
		}

	}

	public static class SCSectionMessage extends SimpleLogMessage {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int scale;
		private int stcIndex;
		private int baseScaleCluster;
		private int messageIndex = 0;
		private int hhllIndex = -1;
		private int thIndex = -1;

		public SCSectionMessage() {
			super();
		}

		public SCSectionMessage(int scale1, long aTime, long aPrice,
				long aTHtime, long aTHprice, String aCategory, String aMessage,
				int stcIndex1, int aBaseScaleCluster) {
			super(aTime, aPrice, aTHtime, aTHprice, aCategory, aMessage);
			this.scale = scale1;
			setProbVersion(ProbVer.SCT);
			this.stcIndex = stcIndex1;
			this.baseScaleCluster = aBaseScaleCluster;
		}

		@Override
		public boolean passFilter(IProbabilitiesFilter aF) {
			return super.passFilter(aF) && aF.aceptsScale(scale)
					&& aF.aceptsSCT(stcIndex)
					&& aF.aceptsBCID(baseScaleCluster);
		}

		private static HtmlUtils hutils = HtmlUtils.Plain;

		@Override
		public String getMessage() {
			return hutils
					.getRawHtml(
							true,
							"["
									+ hutils.bold(hutils.color("Idx="
											+ messageIndex, Color.blue.darker()))
									+ ", "
									+ ((hhllIndex == -1) ? (hutils.bold(hutils
											.color("TH_" + thIndex,
													Color.green.darker())))
											: (hutils.bold(hutils.color("HHLL_"
													+ hhllIndex,
													Color.gray.brighter()))))
									+ "] " + super.getMessage());
		}

		public int getMessageIndex() {
			return messageIndex;
		}

		public void setMessageIndex(int aMessageIndex) {
			messageIndex = aMessageIndex;
		}

		public int getHHLLIndex() {
			return hhllIndex;
		}

		public void setHHLLIndex(int aWinnerIndex) {
			hhllIndex = aWinnerIndex;
		}

		public void setTHIndex(int aReachedNewTHIndex) {
			thIndex = aReachedNewTHIndex;
		}

		public int getTHIndex() {
			return thIndex;
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int aScale) {
			scale = aScale;
		}

		public int getBaseScaleCluster() {
			return baseScaleCluster;
		}

		public void setBaseScaleCluster(int aBaseScaleCluster) {
			baseScaleCluster = aBaseScaleCluster;
		}

		public int getStcIndex() {
			return stcIndex;
		}

		public void setStcIndex(int aStcIndex) {
			stcIndex = aStcIndex;
		}

	}

	public static class NonReachedTargetMessage extends KeyLogMessage {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NonReachedTargetMessage(ProbabilitiesKey aKey,
				ArrayList<HSTargetInfo> aTargetsPrices, long aTime,
				long aPrice, long aTHtime, long aTHprice, long aP0Time,
				long aP0Price, long aPm1Time, long aPm1Price,
				long aTargetPrice, String aCategory, String aMessage) {
			super(aKey, aTime, aPrice, aTHtime, aTHprice, aP0Time, aP0Price,
					aPm1Time, aPm1Price, aTargetPrice, aCategory, aMessage);
			setVisitedTargetPrices(aTargetsPrices);
			setProbVersion(ProbVer.Targets);
		}

		public NonReachedTargetMessage() {
			super();
		}

	}

	@Override
	public int getLogPriority() {
		if (getCategory().equals(CATEGORY_TH))
			return 1;
		if (getCategory().equals(CATEGORY_TARGET))
			return 2;
		return 3;
	}

	@Override
	public long getTargetPrice() {
		return targetPrice;
	}

	@Override
	public long getTimeCPU() {
		return timeCPU;
	}

	public static Comparator<? super ISimpleLogMessage> comparator() {
		if (comp == null) {
			comp = new Comparator<ISimpleLogMessage>() {
				@Override
				public int compare(ISimpleLogMessage aO1, ISimpleLogMessage aO2) {
					int r = (int) Math.signum(aO1.getTime() - aO2.getTime());
					if (r == 0)
						r = (int) Math
								.signum(aO1.getTHTime() - aO2.getTHTime());
					if (r == 0)
						r = (int) Math.signum(aO1.getLogPriority()
								- aO2.getLogPriority());
					if (r == 0)
						r = (int) Math.signum(aO1.getTimeCPU()
								- aO2.getTimeCPU());
					if (r == 0) {
						int r1 = (aO1 instanceof ReachedTargetMessage) ? -1 : 1;
						int r2 = (aO2 instanceof ReachedTargetMessage) ? -1 : 1;
						r = r1 - r2;
					}
					return r;
				}
			};
		}
		return comp;
	}

}
