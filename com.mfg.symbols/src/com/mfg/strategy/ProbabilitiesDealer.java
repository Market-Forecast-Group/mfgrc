package com.mfg.strategy;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlTransient;

import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.interfaces.ProbabilityRecord;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.probabilities.SwingCalculator;
import com.mfg.interfaces.probabilities.WidgetSwingsElement;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.interfaces.trading.IStrategyShell;
import com.mfg.strategy.logger.StrategyMessage;
import com.mfg.utils.StepDefinition;
import com.mfg.widget.probabilities.DistributionsContainer;

public class ProbabilitiesDealer {

	public interface IListener {
		public void probabilityRecordComputed(IStrategyShell strategy,
				ProbabilityRecord record);
	}

	public static boolean COMPUTE_ALL_SCALES = true;
	@XmlTransient
	private WidgetSwingsElement[] element;
	private final StepDefinition step = new StepDefinition(0.01);
	private double[][] tpa;
	private double[][] ntpa;
	private double[][] piv;
	private double[][] niv;
	private double[][] ptd;
	private double[][] ntd;
	private int[][] ptdidx;
	private int[][] ntdidx;
	private int[] bestNPTarget, bestPPTarget, bestNNTarget, bestPNTarget;
	private double[] bestNTD, bestPTD;
	private boolean[] docalculations;
	private ProbabilitiesKey[] negativekey;
	private String[] bestPTDMSG;
	private String[] bestNTDMSG;
	private ProbabilitiesTD[] theTDInfo;
	private IListener[] listeners = new IListener[] {};

	private ProbabilityRecord[] precords;
	private IIndicator widget;
	private Configuration configuration;
	private SwingCalculator calculator;
	private DistributionsContainer distribution;
	private IStrategyShell _shell;
	private double probabilityLinesPercentValue;
	private double minMatchesPercent;

	private long[] priceFromNTID;
	private long[] priceFromTID;
	private boolean[] updateTH;
	private double[][] positiveTHarray;
	private double[][] negativeTHarray;
	private boolean[] sortedPositiveTHarray;
	private boolean[] sortedNegativeTHarray;
	private int cWidgetScale;
	private AbstractStrategy abstractStrategy;
	private int endScale;
	private int startScale;

	private static int[][] intBox(int tdim, int wdim) {
		return new int[wdim][tdim];
	}

	private static double[][] doubleBox(int tdim, int wdim) {
		return new double[wdim][tdim];
	}

	public void begin(IStrategyShell _shell1,
			DistributionsContainer distributionsContainer,
			Configuration aConfiguration) {
		this.configuration = aConfiguration;
		calculator = new SwingCalculator(aConfiguration);
		distribution = distributionsContainer;
		widget = _shell1.getIndicator();
		distributionsContainer.setWidget(widget);

		this._shell = _shell1;
		abstractStrategy = (AbstractStrategy) (_shell1).getStrategies().get(0);

		int tdim = distributionsContainer.getMaxTarget();
		endScale = widget.getChscalelevels();
		startScale = widget.getStartScaleLevelWidget();
		int wdim = endScale + 1;

		precords = new ProbabilityRecord[wdim];
		for (int i = 0; i < wdim; i++) {
			precords[i] = new ProbabilityRecord();
			precords[i].setTargetStep(aConfiguration.getTargetStep());
		}

		tpa = doubleBox(tdim, wdim);
		ntpa = doubleBox(tdim, wdim);
		piv = doubleBox(tdim, wdim);
		niv = doubleBox(tdim, wdim);

		ptd = doubleBox(tdim, wdim);
		ntd = doubleBox(tdim, wdim);
		ptdidx = intBox(tdim, wdim);
		ntdidx = intBox(tdim, wdim);
		bestNPTarget = new int[wdim];
		bestPPTarget = new int[wdim];
		bestNNTarget = new int[wdim];
		bestPNTarget = new int[wdim];
		bestNTD = new double[wdim];
		bestPTD = new double[wdim];
		element = new WidgetSwingsElement[wdim];
		updateTH = new boolean[wdim];
		positiveTHarray = new double[wdim][];
		negativeTHarray = new double[wdim][];
		sortedPositiveTHarray = new boolean[wdim];
		sortedNegativeTHarray = new boolean[wdim];
		priceFromNTID = new long[wdim];
		priceFromTID = new long[wdim];
		docalculations = new boolean[wdim];
		theTDInfo = new ProbabilitiesTD[wdim];
		for (int i = 0; i < wdim; i++) {
			theTDInfo[i] = new ProbabilitiesTD();
		}
		negativekey = new ProbabilitiesKey[wdim];
		bestPTDMSG = new String[wdim];
		bestNTDMSG = new String[wdim];

	}

	public void fireNewProbabilityRecord(ProbabilityRecord record) {
		if (listeners != null) {
			for (IListener l : listeners) {
				l.probabilityRecordComputed(_shell, record);
			}
		}
	}

	public void addListener(IListener listener) {
		ArrayList<IListener> list = new ArrayList<>(Arrays.asList(listeners));
		list.add(listener);
		listeners = list.toArray(new IListener[list.size()]);
	}

	public void removeListener(IListener listener) {
		ArrayList<IListener> list = new ArrayList<>(Arrays.asList(listeners));
		list.remove(listener);
		listeners = list.toArray(new IListener[list.size()]);
	}

	public void dealWithProbabilities() {
		Arrays.fill(docalculations, true);
		for (int scale = getStartScale(); scale <= getEndScale()
				- configuration.getWorkingDepth(); scale++) {
			if (widget.isThereANewPivot(scale) && isOK(scale)) {
				element[scale] = new WidgetSwingsElement(widget, scale,
						distribution.getElementsPatternsRoot(), false,
						calculator);
			}
			if (element[scale] == null)
				continue;
			element[scale].considerPrice(widget.getCurrentPrice());
			if (COMPUTE_ALL_SCALES) {
				getTDInfo(scale, false);
			}
		}

	}

	private int getStartScale() {
		return startScale;
	}

	private int getEndScale() {
		return endScale;
	}

	public boolean isOK(int scale) {
		int thth = configuration.getMaxRatioLevel() + 2;
		boolean res = widget.getCurrentPivotsCount(scale) >= thth;
		if (configuration.isMultiscale()) {
			for (int k = configuration.getEndScale(); k >= configuration
					.getStartScale(); k--) {
				res &= widget.getCurrentPivotsCount(k) >= thth;
				if (!res)
					break;
			}
		}
		return res;
	}

	private void doCalculations(int scale, boolean logInfo) {
		WidgetSwingsElement aElement = this.element[scale];
		if (docalculations[scale]) {
			tpa[scale] = distribution.getTargetsProbabilitiesArray(aElement,
					scale, tpa[scale]);
			piv[scale] = distribution.getTargetsPositiveIndexValues(aElement,
					scale, widget.getCurrentPrice(), piv[scale], true);
			ntpa[scale] = distribution.getNextTargetsProbabilitiesArray(
					aElement, scale, ntpa[scale]);
			negativekey[scale] = distribution
					.getNextKey(aElement, scale, scale);
			niv[scale] = distribution.getTargetsNegativeIndexValues(aElement,
					scale, widget.getCurrentPrice(), niv[scale], true);
			buildPNTD(piv[scale], niv[scale], ptd[scale], ntd[scale],
					ptdidx[scale], ntdidx[scale]);
			getBestTD(ptd[scale], ptdidx[scale], logInfo, true, scale);
			getBestTD(ntd[scale], ntdidx[scale], logInfo, false, scale);

		}
		docalculations[scale] = false;
	}

	private void logTD(int scale) {
		log(StrategyMessage.PTD, tpaToString(ptd[scale], "|"), 0);
		log(StrategyMessage.NTD, tpaToString(ntd[scale], "|"), 0);
	}

	private void logNIV(int scale) {
		log(StrategyMessage.NIV, tpaToString(niv[scale], "|"), 0);
	}

	private void logNTPA(int scale) {
		log(TradeMessage.COMMENT, "Negative " + negativekey[scale], 0);
		log(StrategyMessage.NTPA, tpaToString(ntpa[scale], "|"), 0);
	}

	private void logPIV(int scale) {
		log(StrategyMessage.PIV, tpaToString(piv[scale], "|"), 0);
	}

	private void logTPA(int scale) {
		log(StrategyMessage.PTPA, tpaToString(tpa[scale], "|"), 0);
	}

	private int getBestTD(double[] aTD, int[] aTDidx, boolean log,
			boolean positive, int scale) {
		double max = -1;
		int pos = -1;
		for (int i = 0; i < aTDidx.length; i++) {
			if (max < aTD[i]) {
				max = aTD[i];
				pos = i;
			}
		}
		// if (pos == -1) {
		// tpa[scale] = distribution.getTargetsProbabilitiesArray(
		// element[scale], scale, tpa[scale]);
		// buildPNTD(piv[scale], niv[scale], ptd[scale], ntd[scale],
		// ptdidx[scale], ntdidx[scale]);
		// }
		int negT = pos == -1 ? -1 : aTDidx[pos];
		if (positive) {
			bestPPTarget[scale] = pos + 1;
			bestPNTarget[scale] = negT + 1;
			bestPTD[scale] = max;
		} else {
			bestNPTarget[scale] = pos + 1;
			bestNNTarget[scale] = negT + 1;
			bestNTD[scale] = max;
		}
		String ver = positive ? "PTD" : "NTD";
		TradeMessageType type = positive ? StrategyMessage.PTD
				: StrategyMessage.NTD;
		String eventMSG = "The best "
				+ ver
				+ " is +Target("
				+ (pos + 1)
				+ ")="
				+ element[scale].getTargetPrice(pos + 1, scale)
				+ ", -Target("
				+ (negT + 1)
				+ ")="
				+ element[scale].getNegativeTargetPrice((negT + 1), scale)
				+ " with "
				+ ver
				+ "="
				+ step.round(max)
				+ (pos == -1 ? "=nav" : "="
						+ step.round((positive ? piv[scale][pos]
								: niv[scale][negT]))
						+ "/"
						+ step.round(((!positive) ? piv[scale][pos]
								: niv[scale][negT])));
		if (positive)
			bestPTDMSG[scale] = eventMSG;
		else
			bestNTDMSG[scale] = eventMSG;
		if (log)
			log(type, eventMSG, 0);
		return pos;
	}

	private static void buildPNTD(double[] aPIV, double[] aNIV, double[] aPTD,
			double[] aNTD, int[] aPTDIdx, int[] aNTDIdx) {
		for (int i = 0; i < aPIV.length; i++) {
			if (aPIV[i] >= 0) {
				int bestPidx = 0;
				int bestNidx = 0;
				int j = 0;
				// Gets the initial valid NIV.
				for (; j < aNIV.length; j++) {
					if (aNIV[j] >= 0) {
						bestPidx = j;
						bestNidx = j;
						break;
					}
				}
				for (; j < aNIV.length; j++) {
					if (getIV(aPIV[i], aNIV[j]) > getIV(aPIV[i], aNIV[bestPidx])) {
						bestPidx = j;
					}
					if (getIV(aNIV[j], aPIV[i]) > getIV(aNIV[bestNidx], aPIV[i])) {
						bestNidx = j;
					}
				}
				aPTDIdx[i] = bestPidx;
				aNTDIdx[i] = bestNidx;
				aPTD[i] = getIV(aPIV[i], aNIV[bestPidx]);
				aNTD[i] = getIV(aNIV[bestNidx], aPIV[i]);
			} else {
				aPTD[i] = -1;
				aNTD[i] = -1;
				aPTDIdx[i] = -1;
				aNTDIdx[i] = -1;
			}
		}
	}

	private static double getIV(double a, double b) {
		return a / (b == 0 ? 1 : b);
	}

	public String tpaToString(double[] aTpa, String separatorPar) {
		String separator = separatorPar;
		String res = getCHProb(aTpa[0]);
		separator = " " + separator + " ";
		for (int i = 1; i < aTpa.length; i++) {
			res += (separator + getCHProb(aTpa[i]));
			if ((i + 1) % 10 == 0)
				res += " ____ ";
		}
		return res;
	}

	private String getCHProb(double p) {
		if (p < 0)
			return " *  ";
		if (!Double.isInfinite(p)) {
			String string = "" + step.round(p);
			while (string.length() <= 4) {
				string = string + " ";
			}
			return string;
		}
		return "Inf ";
	}

	public void logInfo(int wscale) {
		logTPA(wscale);
		logNTPA(wscale);
		logPIV(wscale);
		logNIV(wscale);
		logTD(wscale);
		logBestTD(wscale);
	}

	public void logBestTD(int wscale) {
		log(StrategyMessage.PTD, bestPTDMSG[wscale], 0);
		log(StrategyMessage.NTD, bestNTDMSG[wscale], 0);
		logBestDir(wscale);
	}

	private void logBestDir(int wscale) {
		log(TradeMessage.COMMENT, "The best direction["
				+ wscale
				+ "] is the "
				+ (theTDInfo[wscale].isPositiveTradeDirection() ? "Positive"
						: "Negative"), 0);
	}

	public ProbabilitiesTD getTDInfo(int aWidgetScale, boolean logInfo) {
		setcWidgetScale(aWidgetScale);
		boolean did = docalculations[aWidgetScale];
		doCalculations(aWidgetScale, logInfo);
		theTDInfo[aWidgetScale].update(bestPPTarget[aWidgetScale],
				bestPNTarget[aWidgetScale], bestNPTarget[aWidgetScale],
				bestNNTarget[aWidgetScale], bestPTD[aWidgetScale],
				bestNTD[aWidgetScale], element[aWidgetScale], aWidgetScale,
				widget.getCurrentPrice());
		if ((logInfo && theTDInfo[aWidgetScale].isChangingDir()) && did
				&& _shell.isARelevantScale(aWidgetScale))
			logBestDir(aWidgetScale);
		if (did) {
			ProbabilityRecord record = precords[aWidgetScale];
			record.setLevel(aWidgetScale);
			record.setPositiveTargetPrice(theTDInfo[aWidgetScale]
					.getBestPPrice());
			record.setNegativeTargetPrice(theTDInfo[aWidgetScale]
					.getBestNPrice());
			record.setTime(widget.getCurrentTime());
			record.setCurrentPrice(widget.getCurrentPrice());
			record.setPositiveTradeDirection(true);
			record.setFirstTarget(element[aWidgetScale]
					.getPattern(aWidgetScale).getFirstTarget());
			record.setFirstNegativeTarget(distribution
					.getElementsPatternsRoot()
					.getNextPatternLeaf(element[aWidgetScale], aWidgetScale)
					.getFirstTarget());
			record.setHHLL(element[aWidgetScale].getHHLL(aWidgetScale));
			record.setSign(element[aWidgetScale].getSign(aWidgetScale));
			record.setPivot0(element[aWidgetScale].getPivotPrice(0,
					aWidgetScale));
			record.setTH0((long) element[aWidgetScale].getPivotInfo(0,
					aWidgetScale).getConfirmPrice());
			record.setPrevSwing(element[aWidgetScale]
					.getPrevSwing(aWidgetScale));
			record.setTarget(element[aWidgetScale].getTargetDelta(aWidgetScale));
			double[] currentTPA = distribution.getTargetsProbabilitiesArray(
					element[aWidgetScale], aWidgetScale);
			boolean currSort = false;// !distribution
			// .getCurrentKey(element[aWidgetScale], aWidgetScale,
			// aWidgetScale, RefType.SwingM1).getPattern()
			// .isInfinite();
			double[] nextTPA = distribution.getNextTargetsProbabilitiesArray(
					element[aWidgetScale], aWidgetScale);
			boolean nextSort = !distribution
					.getNextKey(element[aWidgetScale], aWidgetScale,
							aWidgetScale).getPattern().isInfinite();
			if (widget.isThereANewPivot(aWidgetScale)) {
				positiveTHarray[aWidgetScale] = distribution
						.getStaticTargetsProbabilitiesArray(
								element[aWidgetScale], aWidgetScale);
				ProbabilitiesKey currentKey = distribution.getCurrentRTKey(
						element[aWidgetScale], aWidgetScale, aWidgetScale);
				sortedPositiveTHarray[aWidgetScale] = !currentKey.getPattern()
						.isInfinite();// currSort;
				negativeTHarray[aWidgetScale] = nextTPA;
				sortedNegativeTHarray[aWidgetScale] = nextSort;
				if (logInfo)
					abstractStrategy.log(TradeMessage.COMMENT, "This key "
							+ currentKey, 0);
			}
			updateTH[aWidgetScale] |= widget.isThereANewPivot(aWidgetScale);
			if (updateTH[aWidgetScale]) {
				int nTID = locateProbability(negativeTHarray[aWidgetScale],
						sortedNegativeTHarray[aWidgetScale],
						probabilityLinesPercentValue) + 1;
				priceFromNTID[aWidgetScale] = record.getPriceFromNTID(nTID,
						configuration.getType());
				int tID = locateProbability(positiveTHarray[aWidgetScale],
						sortedPositiveTHarray[aWidgetScale],
						probabilityLinesPercentValue) + 1;
				priceFromTID[aWidgetScale] = record.getPriceFromTID(tID,
						configuration.getType());
				ProbabilitiesKey k = distribution.getNextKey(
						element[aWidgetScale], aWidgetScale, aWidgetScale);
				if (logInfo)
					abstractStrategy
							.log(TradeMessage.COMMENT,
									step.round(positiveTHarray[aWidgetScale][tID - 1])
											+ " ===>> SC="
											+ aWidgetScale
											+ ", nTID="
											+ nTID
											+ ", Prob="
											+ step.round(negativeTHarray[aWidgetScale][nTID - 1])
											+ k
											+ " eval="
											+ element[aWidgetScale]
													.evalNegTrack(aWidgetScale),
									0);
			}

			record.setPositiveTHProbabilitiesPrice(priceFromTID[aWidgetScale]);
			record.setNegativeTHProbabilitiesPrice(priceFromNTID[aWidgetScale]);
			int tID = locateProbability(currentTPA, currSort,
					probabilityLinesPercentValue) + 1;
			int tID2 = locateProbability(nextTPA, nextSort,
					probabilityLinesPercentValue) + 1;

			IProbabilitiesSet tps = distribution.getCurrentTPSet(
					element[aWidgetScale], aWidgetScale);
			double[] minArray = distribution.getTargetsProbabilitiesArray(1,
					tps);

			if (updateTH[aWidgetScale] || tID == 1
					|| minMatchesPercent <= minArray[tID - 1]) {
				record.setPositiveCurrentProbabilitiesPrice(record
						.getPriceFromTID(tID, configuration.getType()));
			}
			long priceFromNTID2 = record.getPriceFromNTID(tID2,
					configuration.getType());
			record.setNegativeCurrentProbabilitiesPrice(priceFromNTID2);
			if (aWidgetScale == 3) {
				int t = element[aWidgetScale].getTargetID(aWidgetScale);
				double targetProbability = distribution.getCurrentTPSet(
						element[aWidgetScale], aWidgetScale)
						.getTargetProbability(t, t + 1);
				if (logInfo)
					abstractStrategy.log(TradeMessage.COMMENT,
							"P=" + step.round(targetProbability) + ", TID=" + t
									+ ", NTPA " + tpaToString(nextTPA, "|"), 0);
			}
			updateTH[aWidgetScale] = false;
			fireNewProbabilityRecord(record);
		}

		return theTDInfo[aWidgetScale];
	}

	public int locateProbability(double[] array, boolean sorted,
			double probability) {
		int probabilityCursor;
		int maxTargetCount = array.length;
		int start = findFirst(array);
		if (start >= 0) {
			if (sorted) {
				probabilityCursor = binarySearch(array, probability, start,
						maxTargetCount / 2, maxTargetCount - 1);
			} else {
				probabilityCursor = linearSearch(array, probability, start,
						maxTargetCount - 1);
			}
		} else
			probabilityCursor = maxTargetCount - 1;
		return probabilityCursor;
	}

	private static int findFirst(double[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= 0)
				return i;
		}
		// distribution.getNextTargetsProbabilitiesArray(element[cWidgetScale],
		// cWidgetScale);
		return -1;
	}

	protected int binarySearch(double[] array, double probability, int start,
			int mid, int end) {
		if (Math.abs(start - end) <= 1) {
			if (Math.abs(probability - array[start]) < Math.abs(probability
					- array[end]))
				return start;
			return end;
		}
		if (probability >= array[mid]) {
			return binarySearch(array, probability, start, (start + mid) / 2,
					mid);
		}
		return binarySearch(array, probability, mid, (end + mid) / 2, end);
	}

	protected static int linearSearch(double[] array, double probability,
			int start, int end) {
		int best = start;
		for (int i = start + 1; i <= end; i++) {
			if (Math.abs(probability - array[i]) < Math.abs(probability
					- array[best])) {
				best = i;
			}
		}
		return best;
	}

	/*
	 * private long getNegativeTest(int level) { long res =
	 * (widget.isSwingDown(level) ? widget.getLLPrice(level) - 100 :
	 * widget.getHHPrice(level)) + 100;
	 * 
	 * return res; }
	 */

	public void log(TradeMessageType type, String event, int orderID) {
		_shell.log(type, event, orderID);
	}

	public WidgetSwingsElement[] getElement() {
		return element;
	}

	public double getProbabilityLinesPercentValue() {
		return probabilityLinesPercentValue;
	}

	public void setProbabilityLinesPercentValue(
			double aProbabilityLinesPercentValue) {
		probabilityLinesPercentValue = aProbabilityLinesPercentValue;
		if (updateTH != null)
			Arrays.fill(updateTH, true);
	}

	public double getMinMatchesPercent() {
		return minMatchesPercent;
	}

	public void setMinMatchesPercent(double aMinMatchesPercent) {
		this.minMatchesPercent = aMinMatchesPercent;
	}

	public SwingCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(SwingCalculator aCalculator) {
		this.calculator = aCalculator;
	}

	public int getcWidgetScale() {
		return cWidgetScale;
	}

	public void setcWidgetScale(int aCWidgetScale) {
		this.cWidgetScale = aCWidgetScale;
	}
}
