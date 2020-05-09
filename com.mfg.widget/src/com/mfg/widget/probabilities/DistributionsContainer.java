package com.mfg.widget.probabilities;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.core.runtime.Assert;

import com.mfg.common.DFSException;
import com.mfg.common.QueueTick;
import com.mfg.dm.TickAdapter;
import com.mfg.interfaces.IObjectProcessor;
import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.IElement;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.probabilities.SCTProbabilityKey;
import com.mfg.interfaces.probabilities.SwingCalculator;
import com.mfg.interfaces.probabilities.SwingReference;
import com.mfg.interfaces.probabilities.WidgetSwingsElement;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.interfaces.trading.Configuration.SCMode;
import com.mfg.interfaces.trading.RefType;
import com.mfg.logger.ILogRecord;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.probabilities.SimpleLogMessage.NonReachedTargetMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.SCSectionMessage;
import com.mfg.widget.probabilities.TargetsIndex.TargetInfo;

/**
 * contains all the data structures needed for the calculations of
 * probabilities. For now we are using mainly HashMap but when the software
 * becomes more stable we can use arrays since there are a finite number of
 * possible keys and we could define a function to map keys to integers domain.
 * <p>
 * We have the distribution of Targets Probabilities which is considered as a
 * mapping of {@link ProbabilitiesKey} and {@link DistributionsContainer} that
 * holds the counters of reached and non-reached targets we find in any swing.
 * the statistics are computed differently according to the Scale, Target,
 * Higher Scale Cluster ID, direction relative to the Higher Scale
 * (Contrarian/Non-Contrarian), Pattern ID and Base Scale. The use of Base Scale
 * is meant for the multiple scales probabilities version, because when we are
 * performing calculations in such mode we need to differentiate the
 * probabilities of scale 5 computed to be used on scale 4 from the
 * probabilities of scale 5 used to compute the probabilities on scale 3. Their
 * dependencies are not the same.
 * <p>
 * We also hold here the distribution of SC probabilities, represented with a
 * mapping of {@link SCTProbabilityKey} and {@link SCTProbabilitySet}. The
 * statistics are computed differently according to the Scale, how many SC we
 * have touched and the corresponding Cluster of the Base Scale.
 * 
 * @author gardero
 * 
 */
public class DistributionsContainer extends TickAdapter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Comparator<ISimpleLogMessage> comp;
	private transient LogHelper logHelper;
	private ElementsPatterns root;
	public Configuration configuration;
	private transient SwingCalculator calculator;
	private HashMap<ProbabilitiesKey, IProbabilitiesSet> targetsMap;
	private HashMap<SCTProbabilityKey, SCTProbabilitySet> sctsMap;
	@XmlTransient
	transient IIndicator widget;
	private transient List<SwingReference> references;
	private transient SwingReference[] lastReferences;
	private transient int[] thcount;
	private transient boolean[] okScales;
	transient HtmlUtils hutils = new HtmlUtils(false, false);
	private transient List<ISimpleLogMessage> log;
	transient WidgetSwingsElement wselement;
	private transient TargetsIndex tindex;
	private final int maxTarget = 70;
	private transient int globalScale;
	private transient boolean preComputing;
	private transient int auxScale;
	private HashMap<Integer, T1Computer> t1ComputersMap;
	private transient double meanT1Probabilities;
	// private SCTModelGen SCTModel;
	private transient TargetsIndex baseTindex;
	private transient WidgetSwingsElement baseElement;
	private transient boolean sctsComputation;
	private transient IndicatorRunner indicatorRunner;
	private IndicatorConfiguration indicatorConfig;
	private transient IProbabilitiesSet[] probabilitySets;
	private transient SCTProbabilitySet[] sctprobabilitySets;
	private transient SimpleTickListener tickListener;
	private transient List<ILogRecord> allLogMessages;

	public List<ProbabilitiesKey> getAllKeys() {
		@SuppressWarnings("unused")
		ArrayList<ProbabilitiesKey> res = new ArrayList<ProbabilitiesKey>(
				targetsMap.keySet());
		return res;
	}

	public boolean contains(ProbabilitiesKey key) {
		return targetsMap.containsKey(key);
	}

	public IProbabilitiesSet getElementsSet(ProbabilitiesKey key) {
		return targetsMap.get(key);
	}

	@SuppressWarnings("unused")
	public DistributionsContainer() {
		targetsMap = new HashMap<ProbabilitiesKey, IProbabilitiesSet>();
		sctsMap = new HashMap<SCTProbabilityKey, SCTProbabilitySet>();
	}

	@SuppressWarnings({ "hiding", "unused" })
	public DistributionsContainer(List<IElement> aElist,
			Configuration configuration) throws DFSException {
		this.configuration = configuration;
		calculator = new SwingCalculator(configuration);
		logHelper = new LogHelper(configuration);
		root = new ElementsPatterns(aElist, configuration);
		targetsMap = new HashMap<ProbabilitiesKey, IProbabilitiesSet>();
		t1ComputersMap = new HashMap<Integer, T1Computer>();
		computeTargetsProbability();

	}

	@SuppressWarnings("unused")
	public DistributionsContainer(List<IElement> aPopulation,
			Configuration aConfiguration, IndicatorRunner aProbabilitiesJob)
			throws DFSException {
		this.indicatorRunner = aProbabilitiesJob;
		// this.indicatorConfiguration = aConfiguration;
		this.configuration = aConfiguration;
		calculator = new SwingCalculator(aConfiguration);
		logHelper = new LogHelper(configuration);
		root = new ElementsPatterns(aPopulation, configuration);
		targetsMap = new HashMap<ProbabilitiesKey, IProbabilitiesSet>();
		t1ComputersMap = new HashMap<Integer, T1Computer>();
		computeTargetsProbability();
	}

	@Override
	public void onNewTick(QueueTick qt) {
		int aTime = qt.getFakeTime();
		long aPrice = qt.getPrice();
		widget.onNewTick(qt);
		// check for new swings to consider
		for (int iScale = auxScale; iScale <= globalScale
				+ configuration.getWorkingDepth(); iScale++) {
			if (widget.isThereANewPivot(iScale)) {
				thcount[iScale]++;
				considerRef(aTime, aPrice, iScale);
			} else if (lastReferences[iScale] == null) {
				considerRef(aTime, aPrice, iScale);
			}
		}
		// consider statistics based on the swings we found.
		for (Iterator<SwingReference> iterator = references.iterator(); iterator
				.hasNext();) {
			SwingReference ref = iterator.next();
			if (ref.isStillActive(aPrice, widget)) {
				normalComputation(ref);
			} else {
				iterator.remove();
				if (ref.getScale() == auxScale)
					countNotReached(ref);
			}
		}
	}

	/**
	 * considers a swing reference to contribute to probabilities calculation.
	 * 
	 * @param aTime
	 * @param aPrice
	 * @param scale
	 */
	private void considerRef(long aTime, long aPrice, int scale) {
		if ((okScales[scale] || isScaleOk(scale))
				&& checkType(configuration.getType(), scale)) {
			wselement = new WidgetSwingsElement(widget, scale, root,
					configuration.isMultiscale() && !preComputing, calculator);
			ElementsPatterns patternLeaf = root
					.getPatternLeaf(wselement, scale);
			considerComputation(aTime, aPrice, scale, patternLeaf, wselement);
		} else { // only for logging the THs
			int thth = configuration.getMaxRatioLevel() + 2;
			boolean res = widget.getCurrentPivotsCount(scale) > thth;
			if (res) {
				wselement = new WidgetSwingsElement(widget, scale, root, false,
						calculator);
				ElementsPatterns patternLeaf = root.getPatternLeaf(wselement,
						scale);
				Pivot lastPivot = widget.getLastPivot(0, scale);
				Pivot plastPivot = widget.getLastPivot(-1, scale);
				SwingReference ref = new SwingReference(
						configuration.getType(), patternLeaf, wselement, scale,
						lastPivot, plastPivot, plastPivot, maxTarget, widget);
				if (!preComputing && sctsComputation
						&& configuration.isLogging()) {
					SimpleLogMessage msg = logHelper.THMessage(aTime, aPrice,
							scale, patternLeaf, ref, wselement);
					log.add(msg);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	void logDiscardedSwing(int level) {
		// SimpleLogMessage msg = new ;
		// log.add(msg);
	}

	/**
	 * for the Target2 checks whether P<sub>-2</sub> or P<sub>-4</sub> are
	 * bellow P<sub>0</sub> (for an up swing)
	 * 
	 * @param type
	 * @param level
	 * @return
	 */
	@SuppressWarnings("incomplete-switch")
	private boolean checkType(RefType type, int level) {
		switch (type) {
		case Target2:
			int sign = widget.isSwingDown(level) ? -1 : 1;
			long p0 = widget.getLastPivot(0, level).getPivotPrice();
			long p2 = widget.getLastPivot(-2, level).getPivotPrice();
			if (sign * (p0 - p2) < 0) {
				if (widget.getCurrentPivotsCount(level) <= 4)
					return false;
				long p4 = widget.getLastPivot(-4, level).getPivotPrice();
				if (sign * (p0 - p4) < 0)
					return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * gets the reference pivot (P<sub>-2</sub> or P<sub>-4</sub>) to check when
	 * the computation for a Target based calculation ends.
	 * 
	 * @param type
	 * @param level
	 * @return
	 */
	@SuppressWarnings("incomplete-switch")
	private Pivot getRefPivot(RefType type, int level) {
		Pivot p0 = widget.getLastPivot(0, level);
		switch (type) {
		case Target2:
			int sign = widget.isSwingDown(level) ? -1 : 1;
			Pivot p2 = widget.getLastPivot(-2, level);
			if (sign * (p0.getPivotPrice() - p2.getPivotPrice()) < 0) {
				if (widget.getCurrentPivotsCount(level) <= 4)
					return p0;
				Pivot p4 = widget.getLastPivot(-4, level);
				if (sign * (p0.getPivotPrice() - p4.getPivotPrice()) >= 0)
					return p4;
			} else
				return p2;
			break;

		default:
			break;
		}
		return p0;
	}

	/**
	 * counts the current status in the corresponding statistics.
	 * 
	 * @param ref
	 */
	private void normalComputation(SwingReference ref) {
		int sc = ref.getScale();
		ref.compute(widget);
		if (ref.isThereAnewTID()
				&& (preComputing || ref.getScale() == auxScale)) {
			@SuppressWarnings("unused")
			ArrayList<HSTargetInfo> targetsPrices = new ArrayList<HSTargetInfo>();
			ProbabilitiesKey key = computeKey(ref, sc, targetsPrices);
			increase(key, ref.getLastTID(), ref.getTID(), ref, targetsPrices);
			if (!preComputing)
				ref.getSwingElement().shiftConditions(ref.isThereAnewTID());
		} else {
			for (int i = configuration.getEndScale(); i >= auxScale; i--) {
				if (widget.isThereANewPivot(i)) {
					ref.getSwingElement().shiftConditions(ref.isThereAnewTID());
					break;
				}
			}
		}
		// TODO check if i am in rule 1 case 2.
	}

	/**
	 * computes the current probabilities key used to account statistics
	 * separately.
	 * 
	 * @param ref
	 * @param scale
	 * @param targetsPrices
	 * @param readOnly
	 * @return
	 */
	private ProbabilitiesKey computeKey(SwingReference ref, int scale,
			ArrayList<HSTargetInfo> targetsPrices, boolean readOnly) {
		int clusterID = 0;
		boolean contrarian = true;
		// Computation for single scales.
		WidgetSwingsElement element = ref.getSwingElement();
		ProbabilitiesKey key = new ProbabilitiesKey(scale, globalScale,
				ref.getPattern(), contrarian, clusterID, ref.getType(),
				element.getTimeCluster(scale), element.getPriceCluster(scale));
		boolean cond = !(!scalePresent(scale + 1) || !isScaleOk(scale + 1)
				|| !configuration.isMultiscale()
				|| scale == globalScale + configuration.getDepth() || preComputing);
		if (cond) {
			// Computation for multiple scales.
			int target = element.getTargetID(scale + 1) + 1;
			key = internalKeyComputation(ref, scale, targetsPrices, element,
					cond, target, readOnly);
		}
		return key;
	}

	/**
	 * computes the current probabilities key used to account statistics
	 * 
	 * @param ref
	 * @param scale
	 * @param targetsPrices
	 * @return
	 */
	private ProbabilitiesKey computeKey(SwingReference ref, int scale,
			ArrayList<HSTargetInfo> targetsPrices) {
		return computeKey(ref, scale, targetsPrices, false);
	}

	/**
	 * computation of keys for Higher Scales.
	 * 
	 * @param ref
	 * @param scale
	 * @param targetsPrices
	 * @param element
	 * @param cond
	 * @param target
	 * @param readOnly
	 * @return
	 */
	private ProbabilitiesKey internalKeyComputation(SwingReference ref,
			int scale, ArrayList<HSTargetInfo> targetsPrices,
			WidgetSwingsElement element, boolean cond, int targetPar,
			boolean readOnly) {
		int target = targetPar;
		int clusterID;
		boolean contrarian;
		ProbabilitiesKey key;
		TargetInfo t = tindex.getTargetNear(widget.getCurrentTime(), scale + 1,
				target, readOnly);
		ProbabilitiesKey key2;
		IProbabilitiesSet elementsSet = targetsMap.get(key2 = t.getKey());
		target = t.getTID();
		double prob = elementsSet.getTargetProbability(target);
		clusterID = getClusterIDfromProb(prob);
		contrarian = ref.isGoingUP() != t.isUP();
		key = new ProbabilitiesKey(scale, globalScale, ref.getPattern(),
				contrarian, clusterID,
				// ref.getScTouches(),
				ref.getType(), element.getTimeCluster(scale),
				element.getPriceCluster(scale));
		if (cond && targetsPrices != null) {
			targetsPrices.addAll(t.getTargetsPrices());
			double target2 = element.getTarget(scale + 1);
			targetsPrices.add(new HSTargetInfo(element.getTargetPrice(target,
					scale + 1), element.getPivotPrice(0, scale + 1), element
					.getPivotTime(0, scale + 1), target, calculator
					.normalize(target2), key2));
			// element.setPrevHSInfo(prevHSTI, scale+1);
		}
		return key;
	}

	// private ProbabilitiesKey computeKey(SwingReference ref, int scale,
	// long targetPrice, ArrayList<HSTargetInfo> targetsPrices,
	// boolean readOnly) {
	// int clusterID = 0;
	// boolean contrarian = true;
	// ProbabilitiesKey key = new ProbabilitiesKey(scale, globalScale,
	// ref.getPattern(), contrarian, clusterID,
	// // ref.getScTouches(),
	// ref.getType());
	// IDetailedElement element = ref.getSwingElement();
	// boolean cond = !(!scalePresent(scale + 1) || !isOk(scale + 1)
	// || !configuration.isMultiscale()
	// || scale == globalScale + configuration.getDepth() || preComputing);
	// if (cond) {
	// int target = getTargetIDFromPrice(element, scale + 1, targetPrice)+1;
	// key = internalKeyComputation(ref, scale, targetsPrices, element, cond,
	// target, readOnly);
	// }
	// return key;
	// }
	//
	// private ProbabilitiesKey computeKey(SwingReference ref, int scale,
	// long targetPrice, ArrayList<HSTargetInfo> targetsPrices) {
	// return computeKey(ref, scale, targetPrice, targetsPrices, false);
	// }

	@SuppressWarnings({ "null", "boxing" })
	private void considerComputation(long aTime, long aPrice, int aScale,
			ElementsPatterns aPatternLeaf, WidgetSwingsElement aWselement) {
		Pivot lastPivot = widget.getLastPivot(0, aScale);
		Pivot plastPivot = widget.getLastPivot(-1, aScale);
		SwingReference ref = null;
		for (RefType type : getTypesFromMode()) {
			Pivot refPivot = getRefPivot(type, aScale);
			references.add(ref = new SwingReference(type, aPatternLeaf,
					aWselement, aScale, lastPivot, plastPivot, refPivot,
					maxTarget, widget));
			lastReferences[aScale] = ref;
			aWselement.setLastReferences(lastReferences);
			if (!preComputing && !sctsComputation) {
				ProbabilitiesKey key = computeKey(ref, aScale, null);
				IProbabilitiesSet ps = getProbabilitiesSet(key, ref);
				aWselement.considerPrice(aPrice);
				ps.countTH(aWselement.getTargetID(aScale));
			}
		}
		if (!preComputing && sctsComputation) {
			SimpleLogMessage msg = logHelper.THMessage(aTime, aPrice, aScale,
					aPatternLeaf, ref, aWselement);
			log.add(msg);
		} else if (preComputing) {
			if (ref.getPattern().isInfinite()) {
				int leafID = ref.getPattern().getLeafID();
				T1Computer t = t1ComputersMap.get(leafID);
				if (t == null) {
					t1ComputersMap.put(leafID,
							t = new T1Computer(aPatternLeaf.getLowerBound(),
									aPatternLeaf.getMaxSw0Ratio(),
									configuration.getTargetStep()));
					System.out.println("T1 computer for " + leafID);
				}
				double ratio = aWselement.getRatio(0, aScale);
				t.countCase(ratio);
				// System.out.println("Pat "+aPatternLeaf.getLeafID()+" value "+ratio);
			}
		}
	}

	private List<RefType> getTypesFromMode() {
		if (preComputing) {
			@SuppressWarnings("unused")
			ArrayList<RefType> res = new ArrayList<RefType>();
			res.add(RefType.PreCompute);
			return res;
		}
		return configuration.getTypes();
	}

	/**
	 * checks if we have all the necessary conditions for an scale to contribute
	 * to the statistics.
	 * 
	 * @param i
	 * @return
	 */
	private boolean isScaleOk(int i) {
		int thth = configuration.getMaxRatioLevel() + 2;
		boolean res = widget.getCurrentPivotsCount(i) >= thth;
		if (configuration.isMultiscale() && !preComputing) {
			for (int k = configuration.getEndScale(); k >= configuration
					.getStartScale(); k--) {
				res &= widget.getCurrentPivotsCount(k) >= thth;
				if (!res)
					break;
			}
		}
		if (sctsComputation)
			res = res
					&& widget.getCurrentPivotsCount(configuration
							.getDefaultScale()) >= thth;
		okScales[i] = res;
		return okScales[i];
	}

	/**
	 * counts the targets into the statistics.
	 * 
	 * @param aKey
	 * @param last
	 * @param target
	 * @param ref
	 * @param aTargetsPrices
	 * @return
	 */
	private IProbabilitiesSet increase(ProbabilitiesKey aKey, int last,
			int target, SwingReference ref,
			ArrayList<HSTargetInfo> aTargetsPrices) {
		IProbabilitiesSet d = getProbabilitiesSet(aKey, ref);
		d.countTargets(last, target);
		ArrayList<HSTargetInfo> aTargetsPricesAux = LogHelper.transformList(
				aTargetsPrices, aKey);
		if (target > -1) {
			for (int i = last + 1; i <= target; i++) {
				long p = getTargetPriceFromID(ref, i);
				ref.lastTargetTime = widget.getCurrentTime();
				if (!preComputing) {
					tindex.addTarget(widget.getCurrentTime(), ref.getTHTime(),
							i, aKey, !widget.isSwingDown(aKey.getScale()),
							aTargetsPrices);
					if (aKey.isOnTheBase())
						logHelper.logReached(aKey, ref, aTargetsPrices, d,
								aTargetsPricesAux, i, p, widget);
				}
			}
		}
		return d;
	}

	/**
	 * gets the probability Statistics given a key and a reference swing.
	 * 
	 * @param aKey
	 * @param ref
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private IProbabilitiesSet getProbabilitiesSet(ProbabilitiesKey aKey,
			SwingReference ref) {
		IProbabilitiesSet d = null;
		if (targetsMap.containsKey(aKey)) {
			d = targetsMap.get(aKey);
		} else {
			if (aKey.getPattern().isInfinite())
				d = new InfProbabilitiesSet(maxTarget);
			else
				d = new ProbabilitiesSet(maxTarget, aKey.getPattern());
			targetsMap.put(aKey, d);
			d.setKey(aKey);
			ref.getProbabilitiesKeyList().add(aKey);
		}
		return d;
	}

	private void countNotReached(SwingReference ref) {
		IProbabilitiesSet d = null;
		int sc = ref.getScale();
		// if (sc!=auxScale)
		// return;
		int i = Math.max(ref.getTID() + 1, 1);
		if (i <= maxTarget) {
			@SuppressWarnings("unused")
			ArrayList<HSTargetInfo> targetsPrices = new ArrayList<HSTargetInfo>();
			ProbabilitiesKey key = computeKey(ref, sc, targetsPrices);
			d = countNonReachedTarget(ref, sc, i, key);
			long t = widget.getCurrentTime();
			if (ref.lastTargetTime > -1)
				t = ref.lastTargetTime;
			long p = getTargetPriceFromID(ref, i);

			if (!preComputing) {
				tindex.addTarget(t, ref.getTHTime(), i, key, ref.isGoingUP(),
						targetsPrices);
				if (configuration.isLogging() && key.isOnTheBase()) {
					ArrayList<HSTargetInfo> aTargetsPricesAux = LogHelper
							.transformList(targetsPrices, key);
					NonReachedTargetMessage msg = logHelper.nonReachedMSG(ref,
							i, targetsPrices, key, t, p, aTargetsPricesAux);
					msg.setElement(ref.getSwingElement());
					d.getLog().add(msg);
				}
			}
		}
		i++;
		if (!preComputing)
			for (; i <= maxTarget; i++) {
				long p = getTargetPriceFromID(ref, i);
				@SuppressWarnings("unused")
				ArrayList<HSTargetInfo> targetsPrices = new ArrayList<HSTargetInfo>();
				ProbabilitiesKey key = computeKey(ref, sc, targetsPrices, true);
				d = countNonReachedTarget(ref, sc, i, key);
				if (!preComputing)
					tindex.addTarget(widget.getCurrentTime(), ref.getTHTime(),
							i, key, ref.isGoingUP(), targetsPrices);
				if (configuration.isLogging() && key.isOnTheBase()) {
					ArrayList<HSTargetInfo> aTargetsPricesAux = LogHelper
							.transformList(targetsPrices, key);
					NonReachedTargetMessage msg = logHelper.nonReachedMSG(ref,
							i, targetsPrices, key, widget.getCurrentTime(), p,
							aTargetsPricesAux);
					msg.setElement(ref.getSwingElement());
					d.getLog().add(msg);

				}
			}
	}

	@SuppressWarnings("static-method")
	private long getTargetPriceFromID(SwingReference ref, int aI) {
		return (long) ref.getPriceFromTID(aI);
	}

	/**
	 * counts a not reached target for a swing.
	 * 
	 * @param ref
	 * @param sc
	 * @param i
	 * @param key
	 * @return
	 */
	private IProbabilitiesSet countNonReachedTarget(SwingReference ref, int sc,
			int i, ProbabilitiesKey key) {
		IProbabilitiesSet d = getProbabilitiesSet(key, ref);
		d.countNotReachedElement(i);
		return d;
	}

	/**
	 * main method to compute the probabilities.
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings("unused")
	private void computeTargetsProbability() throws DFSException {
		references = new ArrayList<SwingReference>();
		preComputing = true;
		log = new ArrayList<ISimpleLogMessage>();
		System.out.println("Computing the T1s");
		this.begin();
		auxScale = configuration.getStartScale();
		tindex = new TargetsIndex(configuration.getEndScale() + 1);
		preComputeT1(indicatorRunner);
		references.clear();
		printKeys();
		if (tickListener.isStopped())
			return;
		computeTheT1s();
		preComputing = false;
		references.clear();
		tindex.clear();
		if (tickListener.isStopped())
			return;
		for (globalScale = configuration.getEndScale()
				- configuration.getWorkingDepth(); globalScale >= configuration
				.getStartScale() && !tickListener.isStopped(); globalScale--) {
			for (auxScale = globalScale + configuration.getDepth(); auxScale >= globalScale
					&& !tickListener.isStopped(); auxScale--) {
				tindex.resetCursors();
				scaleProbabilityComputation(indicatorRunner);
			}
			if (globalScale == configuration.getDefaultScale()) {
				baseTindex = tindex;
				tindex = new TargetsIndex(widget.getChscalelevels() + 1);
			}
			tindex.clear();
		}
		if (configuration.isMultiscale()) {
			for (globalScale = configuration.getStartScale(); globalScale >= configuration
					.getStartScale() - configuration.getWorkingDepth()
					&& !tickListener.isStopped(); globalScale--) {
				auxScale = globalScale + configuration.getWorkingDepth();
				tindex.resetCursors();
				scaleProbabilityComputation(indicatorRunner);
				tindex.clear();
			}
		}
		if (tickListener.isStopped())
			return;
		sctComputation();
		if (tickListener.isStopped())
			return;
		ISimpleLogMessage[] a = log.toArray(new ISimpleLogMessage[] {});
		Arrays.sort(a, SimpleLogMessage.comparator());
		log = Arrays.asList(a);
		System.out.println(Arrays.toString(thcount));
		buildLogMessages();
	}

	private void scaleProbabilityComputation(IndicatorRunner ts)
			throws DFSException {
		System.out.println("Working on " + auxScale + " for " + globalScale);
		this.begin();
		ts.run(tickListener = new SimpleTickListener(this));
		references.clear();
		printKeys();
	}

	/**
	 * for the computation of targets 1 for the infinity upper bound intervals.
	 * 
	 * @param ts
	 * @throws DFSException
	 */
	private void preComputeT1(IndicatorRunner ts) throws DFSException {
		ts.run(tickListener = new SimpleTickListener(new TickAdapter() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void onStarting(int tick, int scale) {
				super.onStarting(tick, scale);
				calculator.setTickSize(new StepDefinition(scale, tick));
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void onNewTick(QueueTick qt) {
				int aTime = qt.getFakeTime();
				long aPrice = qt.getPrice();
				widget.onNewTick(qt);
				int i = configuration.getDefaultScale();
				if (widget.isThereANewPivot(i)) {
					thcount[i]++;
					considerRef(aTime, aPrice, i);
				}
				for (Iterator<SwingReference> iterator = references.iterator(); iterator
						.hasNext();) {
					SwingReference ref = iterator.next();
					if (ref.isStillActive(aPrice, widget)) {
						if (ref.getPattern().isInfinite())
							t1Computations(aPrice, ref);
						else
							normalComputation(ref);
					} else {
						iterator.remove();
						countNotReached(ref);
					}
				}
			}
		}));
	}

	/**
	 * computation of the SC touches.
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings("unused")
	private void sctComputation() throws DFSException {
		sctsMap = new HashMap<SCTProbabilityKey, SCTProbabilitySet>();
		sctsComputation = true;
		this.begin();
		indicatorRunner.run(new SimpleTickListener(new TickAdapter() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onNewTick(QueueTick qt) {
				int aTime = qt.getFakeTime();
				long aPrice = qt.getPrice();
				widget.onNewTick(qt);
				int defaultScale = configuration.getDefaultScale();
				if (widget.isThereANewPivot(defaultScale)
						&& widget.getCurrentPivotsCount(defaultScale) > configuration
								.getMaxRatioLevel() + 2)
					baseElement = new WidgetSwingsElement(widget, defaultScale,
							root, false, calculator);
				if (baseElement != null)
					baseElement.considerPrice(aPrice);
				for (int i = configuration.getEndScale(); i >= configuration
						.getStartScale(); i--) {
					if (widget.isThereANewPivot(i)) {
						thcount[i]++;
						considerRef(aTime, aPrice, i);
					}
				}
				for (Iterator<SwingReference> iterator = references.iterator(); iterator
						.hasNext();) {
					SwingReference ref = iterator.next();
					if (ref.isStillActive(aPrice, widget)) {
						normalSCTComputationStep(aPrice, ref);
					} else {
						SCTProbabilitySet sctSet = getSCTSet(ref,
								ref.getLastkey(), false);
						if (sctSet != null)
							sctSet.registerNewTH();
						iterator.remove();
					}
				}
			}
		}));
		references.clear();
	}

	private void normalSCTComputationStep(long aPrice, SwingReference ref) {
		ref.compute(widget);
		SCTProbabilityKey key = getCurrentSCTKey(ref);
		if (ref.isThereAnewSCTouchKey(key) && key.getSctouches() > 0) {
			SCTProbabilitySet set = getSCTSet(ref, key);
			if (configuration.isLogging()) {
				SCSectionMessage msg = new SCSectionMessage(ref.getScale(),
						widget.getCurrentTime(), aPrice, ref.getTHTime(),
						ref.getTHPrice(), "SCT", "Scale="
								+ ref.getScale()
								+ " New SC Section [SCT="
								+ hutils.color(
										hutils.bold("" + key.getSctouches()),
										Color.red.darker())
								+ ", BSC="
								+ hutils.color(
										hutils.bold(""
												+ key.getBaseScaleCluster()),
										Color.BLUE.darker())

								+ "]", key.getSctouches(),
						key.getBaseScaleCluster());
				log.add(msg);
				set.registerMSG(msg);
			}
			set.registerSwing();
			ref.resetHHLL();
			ref.onNewSCTSection = true;
		}
		if (ref.isOnNewHHLL() && key.getSctouches() > 0 && ref.onNewSCTSection) {
			SCTProbabilitySet set = getSCTSet(ref, key);
			set.registerNewHHLL();
			ref.onNewSCTSection = false;
		}
		for (int i = configuration.getEndScale(); i >= configuration
				.getStartScale(); i--) {
			if (widget.isThereANewPivot(i)) {
				ref.getSwingElement().shiftConditions(ref.isThereAnewTID());
				break;
			}
		}
	}

	private SCTProbabilitySet getSCTSet(SwingReference ref,
			SCTProbabilityKey key) {
		return getSCTSet(ref, key, true);
	}

	private SCTProbabilitySet getSCTSet(
			@SuppressWarnings("unused") SwingReference ref,
			SCTProbabilityKey keyPar, boolean addIfnotPresent) {
		SCTProbabilityKey key = keyPar;
		if (configuration.getScMode() == SCMode.NoFilter)
			key = new SCTProbabilityKey(key.getScale(), 0, 0);
		SCTProbabilitySet set = sctsMap.get(key);
		if (!sctsMap.containsKey(key) && addIfnotPresent) {
			sctsMap.put(key, set = new SCTProbabilitySet());
			set.setKey(key);
		}
		return set;
	}

	private SCTProbabilityKey getCurrentSCTKey(SwingReference ref) {
		return new SCTProbabilityKey(ref.getScale(), getSCTouches(ref),
				getBaseScaleCluster(ref));
	}

	@SuppressWarnings("static-method")
	private int getSCTouches(SwingReference ref) {
		return ref.getScTouches();
	}

	private int getBaseScaleCluster(
			@SuppressWarnings("unused") SwingReference ref) {
		if (!configuration.getScMode().equals(SCMode.SC_and_Cluster))
			return 0;
		int scale = configuration.getDefaultScale();
		int target = baseElement.getTargetID(scale);
		TargetInfo t = baseTindex.getTargetNear(widget.getCurrentTime(), scale,
				target, false);
		IProbabilitiesSet elementsSet = targetsMap.get(t.getKey());
		target = t.getTID();
		double prob = elementsSet.getTargetProbability(target);
		return getClusterIDfromProb(prob);
	}

	private void t1Computations(@SuppressWarnings("unused") long aPrice,
			SwingReference aRef) {
		@SuppressWarnings("boxing")
		T1Computer t = t1ComputersMap.get(aRef.getPattern().getLeafID());
		aRef.compute(widget);
		if (aRef.isThereAnewTID()) {
			t.countTargets(aRef.getLastTID(), aRef.getTID());
		}
	}

	private void printKeys() {
		System.out.println("%%%%%%% My keys: ");
		for (ProbabilitiesKey k : getAllKeys()) {
			if (k.getScale() == auxScale)
				System.out.println("-- " + k);
		}
	}

	private void computeTheT1s() {
		double sum = 0;
		int count = 0;
		for (ProbabilitiesKey k : getAllKeys()) {
			if (!Double.isInfinite(k.getPattern().getUpperBound())) {
				IProbabilitiesSet elementsSet = targetsMap.get(k);
				double targetProbability = elementsSet.getTargetProbability(1);
				sum += targetProbability;
				int on = elementsSet.getTargetCount(1);
				int off = elementsSet.getTargetOffCount(1);
				System.out.println("Prob="
						+ on
						+ "/"
						+ (off + on)
						+ "="
						+ configuration.getIntervalsStep().round(
								targetProbability) + " for " + k.toString());
				k.getPattern().setReachedT1Count(on);
				count++;
			}
		}
		meanT1Probabilities = sum / count;
		System.out.println("the mean prob is " + meanT1Probabilities);
		root.visitNodes(new IObjectProcessor<ElementsPatterns>() {
			@SuppressWarnings({ "boxing", "synthetic-access" })
			@Override
			public void process(ElementsPatterns node) {
				if (node.isInfinite() && node.isLeaf()) {
					T1Computer t = t1ComputersMap.get(node.getLeafID());
					System.out.println(t);
					double t1 = t.getClosestTo(meanT1Probabilities,
							configuration.getMinMatchesPercent());
					System.out.println("Patt " + node.getLeafID() + " takes "
							+ t1);
					node.setFirstTarget(t1);
				}
			}
		});
		targetsMap.clear();
	}

	public ProbabilitiesKey getCurrentRTKey(WidgetSwingsElement element,
			int scalePar, int baseScalePar) {
		int scale = scalePar;
		int baseScale = baseScalePar;
		ElementsPatterns patt = root.getPatternLeaf(element, scale);
		boolean dir = isContrarianToNext(element, scale, baseScale);
		int CID = getClusterID(scale, baseScale, element, null);
		int e = configuration.getEndScale();
		int s = configuration.getStartScale();
		if (scale > e) {
			baseScale = baseScale + e - scale;
			scale = e;
		}
		if (baseScale < s) {
			scale = scale + s - baseScale;
			baseScale = s;
		}
		return new ProbabilitiesKey(scale, baseScale, patt, dir, CID,
				configuration.getType(), element.getTimeCluster(scale),
				element.getPriceCluster(scale));
	}

	public ProbabilitiesKey getNextKey(WidgetSwingsElement element,
			int scalePar, int baseScalePar) {
		int scale = scalePar;
		int baseScale = baseScalePar;
		ElementsPatterns patt = root.getNextPatternLeaf(element, scale);
		boolean dir = isContrarianToNext(element, scale, baseScale);
		int CID = getClusterID(scale, baseScale, element, null);
		scale = Math.max(Math.min(scale, configuration.getEndScale()),
				configuration.getStartScale());
		baseScale = Math.max(Math.min(baseScale, configuration.getEndScale()),
				configuration.getStartScale());
		// TODO check here, maybe it is better to hardcode 1 for
		// both (price and time) CID
		return new ProbabilitiesKey(scale, baseScale, patt, dir, CID,
				configuration.getType(), element.getTimeCluster(scale),
				element.getPriceCluster(scale));
	}

	public IProbabilitiesSet getCurrentTPSet(WidgetSwingsElement element,
			int scale) {
		ProbabilitiesKey k = getCurrentRTKey(element, scale, scale);
		IProbabilitiesSet elementsSet = targetsMap.get(k);
		return elementsSet;
	}

	public IProbabilitiesSet getNextTPA(WidgetSwingsElement element, int scale) {
		ProbabilitiesKey k = getNextKey(element, scale, scale);
		IProbabilitiesSet elementsSet = targetsMap.get(k);
		return elementsSet;
	}

	@SuppressWarnings("null")
	public double getRTProbability(WidgetSwingsElement element, int scale,
			int baseScale,
			@SuppressWarnings("unused") ArrayList<HSTargetInfo> aTargetsPrices) {
		ProbabilitiesKey k = getCurrentRTKey(element, scale, baseScale);
		IProbabilitiesSet elementsSet = targetsMap.get(k);
		if (elementsSet == null)
			System.out.println("!!!!!!!!!!!!!Key not found " + k);
		int target = element.getTargetID(scale);
		return elementsSet.getTargetProbability(target);
	}

	public double getCondRTProbability(WidgetSwingsElement element, int scale,
			int target) {
		ProbabilitiesKey k = getCurrentRTKey(element, scale, scale);
		IProbabilitiesSet elementsSet = targetsMap.get(k);
		int targetZero = element.getTargetID(scale);
		double prob = elementsSet.getTargetProbability(targetZero);
		if (target < targetZero)
			return -1;
		if (target == targetZero)
			return prob;
		// otherwise
		return prob * elementsSet.getTargetProbability(target);
	}

	public double[] getTargetsProbabilitiesArray(WidgetSwingsElement element,
			int scale, double[] res) {
		IProbabilitiesSet tps = getCurrentTPSet(element, scale);
		int targetZero = element.getTargetID(scale);
		return getTargetsProbabilitiesArray(targetZero, tps, res);
	}

	@SuppressWarnings("static-method")
	public double[] getTargetsProbabilitiesArray(int targetZero,
			IProbabilitiesSet tps, double[] res) {
		for (int i = 0; i < res.length; i++) {
			if (i < targetZero) {
				res[i] = -1;
			} else {
				res[i] = tps.getTargetProbability(targetZero, i + 1);
			}
		}
		return res;
	}

	public double[] getTargetsProbabilitiesArray(int targetZero,
			IProbabilitiesSet tps) {
		double[] res = new double[maxTarget];
		getTargetsProbabilitiesArray(targetZero, tps, res);
		return res;
	}

	public double[] getStaticTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale, double[] res) {
		IProbabilitiesSet tps = getCurrentTPSet(element, scale);
		return probtoArray(res, tps);
	}

	@SuppressWarnings("static-method")
	public double[] probtoArray(double[] res, IProbabilitiesSet tps) {
		for (int i = 0; i < res.length; i++) {
			res[i] = tps.getTargetProbability(i + 1);
		}
		return res;
	}

	public double[] getStaticTargetsProbabilitiesArray(ProbabilitiesKey aKey1,
			double res[]) {
		IProbabilitiesSet tps = targetsMap.get(aKey1);
		return probtoArray(res, tps);
	}

	public double[] getStaticTargetsProbabilitiesArray(ProbabilitiesKey aKey1) {
		double[] res = new double[maxTarget];
		return getStaticTargetsProbabilitiesArray(aKey1, res);
	}

	public double[] getStaticTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale) {
		double[] res = new double[maxTarget];
		getStaticTargetsProbabilitiesArray(element, scale, res);
		return res;
	}

	public double[] getStaticConditionalTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale, double[] res) {
		IProbabilitiesSet tps = getCurrentTPSet(element, scale);
		for (int i = 0; i < res.length; i++) {
			res[i] = tps.getTargetProbability(i, i + 1);
		}
		return res;
	}

	public double[] getStaticConditionalTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale) {
		double[] res = new double[maxTarget];
		getStaticConditionalTargetsProbabilitiesArray(element, scale, res);
		return res;
	}

	public double[] getTargetsPositiveIndexValues(WidgetSwingsElement element,
			int scale, double currentPrice, double[] res, boolean scalled) {
		double[] tpa = getTargetsProbabilitiesArray(element, scale,
				new double[res.length]);
		double delta = 0;
		for (int i = 0; i < res.length; i++) {
			if (tpa[i] < 0) {
				res[i] = -1;
			} else {
				delta = Math.abs(element.getTargetPrice(i + 1, scale)
						- currentPrice);
				if (scalled)
					res[i] = delta * tpa[i];
				else
					res[i] = delta;
			}
		}
		return res;
	}

	public double[] getTargetsNegativeIndexValues(WidgetSwingsElement element,
			int scale, double currentPrice, double[] res, boolean scalled) {
		double[] tpa = getNextTargetsProbabilitiesArray(element, scale,
				new double[res.length]);
		double delta = 0;
		for (int i = 0; i < res.length; i++) {
			if (tpa[i] < 0) {
				res[i] = -1;
			} else {
				delta = Math.abs(element.getNegativeTargetPrice(i + 1, scale)
						- currentPrice);
				if (scalled)
					res[i] = delta * tpa[i];
				else
					res[i] = delta;
			}
		}
		return res;
	}

	@SuppressWarnings("null")
	public double[] getNextTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale, double[] res) {
		IProbabilitiesSet tps = getNextTPA(element, scale);
		IProbabilitiesSet ctps = getCurrentTPSet(element, scale);
		if (tps == null) {
			getNextTPA(element, scale);
			assert false;
		}
		int targetZero = element.getTargetID(scale);
		double prob = ctps.getTargetProbability(targetZero, targetZero + 1);
		for (int i = 0; i < res.length; i++) {
			res[i] = (1 - prob) * tps.getTargetProbability(i + 1);
		}
		return res;
	}

	public double[] getTargetsProbabilitiesArray(WidgetSwingsElement element,
			int scale) {
		double[] res = new double[maxTarget];
		getTargetsProbabilitiesArray(element, scale, res);
		return res;
	}

	public double[] getNextTargetsProbabilitiesArray(
			WidgetSwingsElement element, int scale) {
		double[] res = new double[maxTarget];
		getNextTargetsProbabilitiesArray(element, scale, res);
		return res;
	}

	public double getRTProbability(WidgetSwingsElement element, int scale,
			int baseScale, long price,
			@SuppressWarnings("unused") ArrayList<HSTargetInfo> aTargetsPrices) {
		ProbabilitiesKey k = getCurrentRTKey(element, scale, baseScale);
		IProbabilitiesSet elementsSet = targetsMap.get(k);
		int target = element.getTargetIDFromPrice(price, scale);
		return elementsSet.getTargetProbability(target);
	}

	@SuppressWarnings("null")
	public double getProbability(WidgetSwingsElement element, int scale,
			@SuppressWarnings("unused") int baseScale,
			ArrayList<HSTargetInfo> aTargetsPrices) {
		int target = element.getTargetID(scale);
		TargetInfo t = tindex.getTargetNear(widget.getCurrentTime(), scale,
				target, false);
		assert (target <= t.getTID()) : " error";
		ProbabilitiesKey key;
		IProbabilitiesSet elementsSet = targetsMap.get(key = t.getKey());
		aTargetsPrices.clear();
		aTargetsPrices.addAll(t.getTargetsPrices());
		if (aTargetsPrices != null) {
			aTargetsPrices.add(new HSTargetInfo(element.getTargetPrice(target,
					scale), element.getPivotPrice(0, scale), element
					.getPivotTime(0, scale), target, configuration
					.getTargetStep().roundMore(element.getTarget(scale), 1),
					key));
		}
		return elementsSet.getTargetProbability(target);
	}

	@SuppressWarnings("null")
	public double getProbability(WidgetSwingsElement element, int scale,
			@SuppressWarnings("unused") int baseScale, long price,
			ArrayList<HSTargetInfo> aTargetsPrices) {
		int target = element.getTargetIDFromPrice(price, scale);
		TargetInfo t = tindex.getTargetNear(widget.getCurrentTime(), scale,
				target, false);
		ProbabilitiesKey key;
		IProbabilitiesSet elementsSet = targetsMap.get(key = t.getKey());
		aTargetsPrices.clear();
		aTargetsPrices.addAll(t.getTargetsPrices());
		if (aTargetsPrices != null) {
			aTargetsPrices.add(new HSTargetInfo(element.getTargetPrice(target,
					scale), element.getPivotPrice(0, scale), element
					.getPivotTime(0, scale), target, configuration
					.getTargetStep().roundMore(element.getTarget(scale), 1),
					key));
		}
		return elementsSet.getTargetProbability(target);
	}

	private int getClusterID(int scale, int baseScale,
			WidgetSwingsElement element, ArrayList<HSTargetInfo> aTargetsPrices) {
		if (!scalePresent(scale + 1) || !isScaleOk(scale + 1)
				|| !configuration.isMultiscale()
				|| scale == baseScale + configuration.getDepth()
				|| preComputing)
			return 0;
		double prob = getRTProbability(element, scale + 1, baseScale,
				aTargetsPrices);
		return getClusterIDfromProb(prob);
	}

	// private int getClusterID(int scale, int baseScale,
	// WidgetSwingsElement element, long price,
	// ArrayList<HSTargetInfo> aTargetsPrices) {
	// if (!scalePresent(scale + 1) || !isScaleOk(scale + 1)
	// || !configuration.isMultiscale()
	// || scale == baseScale + configuration.getDepth()
	// || preComputing)
	// return 0;
	// double prob = getRTProbability(element, scale + 1, baseScale, price,
	// aTargetsPrices);
	// return getClusterIDfromProb(prob);
	// }

	public int getClusterIDfromProb(double prob) {
		if (prob < 0)
			return -1;
		return Math.min((int) (configuration.getClusterSize() * prob),
				configuration.getClusterSize() - 1) + 1;
	}

	public boolean isContrarianToNext(SwingReference ref, int scale,
			int baseScale) {
		if (!scalePresent(scale + 1) || !configuration.isMultiscale()
				|| scale == baseScale + configuration.getDepth()
				|| preComputing)
			return true;
		return isContrarianToNext(ref.getSwingElement(), scale, baseScale);
	}

	public boolean isContrarianToNext(IElement element, int scale, int baseScale) {
		boolean isGoingUp = element.isGoingUP(scale);
		if (!scalePresent(scale + 1) || !configuration.isMultiscale()
				|| scale == baseScale + configuration.getDepth()
				|| preComputing)
			return true;
		boolean dir = isGoingUp != element.isGoingUP(scale + 1);
		return dir;
	}

	@SuppressWarnings("unused")
	private boolean roundTarget(IElement aElement, int aScale) {
		double t = aElement.getTarget(aScale);
		return configuration.getTargetStep().isRoundMultiple(t);
	}

	private boolean scalePresent(int scale) {
		return widget.isLevelInformationPresent(scale)
				&& scale >= widget.getStartScaleLevelWidget()
				&& scale <= widget.getChscalelevels();
	}

	// public int getTargetID(IElement element, int scale) {
	// double target = element.getTarget(scale);
	// return getTargetID(element, scale, target);
	// }
	//
	// private int getTargetInt(IElement element, int scale) {
	// double target = element.getTarget(scale);
	// return getTargetInt(element, scale, target);
	// }
	//
	// private int getTargetIDFromPrice(IElement element, int scale, double
	// price) {
	// double target = element.getTargetFromPrice(price, scale);
	// return getTargetID(element, scale, target);
	// }
	//
	// private int getTargetIDFromDelta(IElement element, int scale, double
	// delta) {
	// double target = element.getTargetFromDelta(delta, scale);
	// return getTargetID(element, scale, target);
	// }

	// private int getTargetID(IElement element, int scale, double target) {
	// return getTargetInt(element, scale, target);
	// // double t1 = root.getPatternLeaf(element, scale).getFirstTarget();
	// // StepDefinition s = configuration.getTargetStep();
	// // if (target < t1)
	// // return 1;
	// // return MathUtils.getClosestStepDiffAbs(target, t1,
	// // s.getStepInteger(),
	// // s.getStepScale()) + 1;
	// }

	// private int getTargetInt(IElement element, int scale, double target) {
	// double t1 = root.getPatternLeaf(element, scale).getFirstTarget();
	// StepDefinition s = configuration.getTargetStep();
	// if (target <= t1)
	// return 0;
	// int steps = MathUtils.getStepDiffAbs(target, t1, s.getStepInteger(),
	// s.getStep10Scale()) + 1;
	// if (MathUtils.isRoundStepDiffAbs(target, t1, s.getStepInteger(),
	// s.getStep10Scale()))
	// return steps - 1;
	// return steps;
	// }

	public ElementsPatterns getElementsPatternsRoot() {
		return root;
	}

	public void begin() {
		buildWidgets();
		initStructs();
	}

	private void initStructs() {
		int dim = widget.getChscalelevels();
		thcount = new int[dim + 1];
		okScales = new boolean[dim + 1];
		lastReferences = new SwingReference[dim + 1];
		configuration.fixEndScale(widget.getChscalelevels());
		configuration.fixStartScale(widget.getStartScaleLevelWidget());
	}

	private void buildWidgets() {
		indicatorRunner.buildIndicator();
		widget = indicatorRunner.getIndicator();
		// indicatorConfig = widget.getParamBean();
	}

	public List<ISimpleLogMessage> getEventsList() {
		return log;
	}

	public List<ILogRecord> getAllLogMessages() {
		return allLogMessages;
	}

	@SuppressWarnings("unused")
	protected List<ILogRecord> buildLogMessages() {
		System.out.println("buildig log");
		ArrayList<List<ISimpleLogMessage>> messages = new ArrayList<List<ISimpleLogMessage>>();
		ArrayList<ProbabilitiesKey> res = new ArrayList<ProbabilitiesKey>(
				getAllKeys());
		if (getEventsList() == null || getEventsList().size() == 0
				|| !configuration.isLogging())
			return new ArrayList<ILogRecord>();
		messages.add(LogHelper.sortMSGs(new ArrayList<ISimpleLogMessage>(
				getEventsList())));
		for (ProbabilitiesKey probabilitiesKey : res) {
			@SuppressWarnings("hiding")
			List<ISimpleLogMessage> log = getTargetsMap().get(probabilitiesKey)
					.getLog();
			log = LogHelper.sortMSGs(log);
			messages.add(log);
		}
		System.out.println("done");
		return allLogMessages = logHelper.mergeMessages(messages);
	}

	@XmlTransient
	public HashMap<ProbabilitiesKey, IProbabilitiesSet> getTargetsMap() {
		return targetsMap;
	}

	@SuppressWarnings("unused")
	public IProbabilitiesSet[] getProbabilitySets() {
		if (probabilitySets == null) {
			probabilitySets = new ArrayList<IProbabilitiesSet>(
					targetsMap.values()).toArray(new IProbabilitiesSet[] {});
		}
		return probabilitySets;
	}

	public void setProbabilitySets(IProbabilitiesSet[] list) {
		probabilitySets = list;
		for (IProbabilitiesSet iProbabilitiesSet : list) {
			iProbabilitiesSet.getKey().getPattern()
					.setConfiguration(configuration);
			targetsMap.put(iProbabilitiesSet.getKey(), iProbabilitiesSet);
		}
	}

	@XmlTransient
	public HashMap<SCTProbabilityKey, SCTProbabilitySet> getSctsMap() {
		return sctsMap;
	}

	@SuppressWarnings("unused")
	public SCTProbabilitySet[] getSCTProbabilitySets() {
		if (sctprobabilitySets == null) {
			sctprobabilitySets = new ArrayList<SCTProbabilitySet>(
					sctsMap.values()).toArray(new SCTProbabilitySet[] {});
		}
		return sctprobabilitySets;
	}

	public void setSCTProbabilitySets(SCTProbabilitySet[] list) {
		sctprobabilitySets = list;
		for (SCTProbabilitySet iProbabilitiesSet : list) {
			sctsMap.put(iProbabilitiesSet.getKey(), iProbabilitiesSet);
		}
	}

	public boolean isSctsComputation() {
		return sctsComputation;
	}

	public void setSctsComputation(boolean aSctsComputation) {
		sctsComputation = aSctsComputation;
	}

	public void setTargetsMap(
			HashMap<ProbabilitiesKey, IProbabilitiesSet> aTargetsMap) {
		targetsMap = aTargetsMap;
	}

	public void setSctsMap(
			HashMap<SCTProbabilityKey, SCTProbabilitySet> aSctsMap) {
		sctsMap = aSctsMap;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration aConfiguration) {
		configuration = aConfiguration;
	}

	public double getMeanT1Probabilities() {
		return meanT1Probabilities;
	}

	public HashMap<Integer, T1Computer> getT1Map() {
		return t1ComputersMap;
	}

	public int getMaxTarget() {
		return maxTarget;
	}

	@XmlIDREF
	public IndicatorConfiguration getIndicatorConfiguration() {
		return indicatorConfig;
	}

	public void setIndicatorConfiguration(IndicatorConfiguration iconf) {
		indicatorConfig = iconf;
	}

	public ElementsPatterns getRootElementsPatterns() {
		return root;
	}

	public void setRootElementsPatterns(ElementsPatterns aRoot) {
		root = aRoot;
		Assert.isNotNull(configuration);
		root.visitNodes(new IObjectProcessor<ElementsPatterns>() {
			@Override
			public void process(ElementsPatterns aObj) {
				aObj.setConfiguration(configuration);
			}
		});
	}

	@XmlTransient
	public IIndicator getWidget() {
		return widget;
	}

	public void setWidget(IIndicator aWidget) {
		widget = aWidget;
		initStructs();
	}

	public void setAllLogMessages(List<ILogRecord> aAllLogMessages) {
		allLogMessages = aAllLogMessages;
	}

	@Override
	public void onStarting(int tick, int scale) {
		calculator.setTickSize(new StepDefinition(scale, tick));
	}

	@Override
	public void onStopping() {
		// TODO close the last swings
	}

}
