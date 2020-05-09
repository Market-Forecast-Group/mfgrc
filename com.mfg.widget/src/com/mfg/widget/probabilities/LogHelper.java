package com.mfg.widget.probabilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.IElement;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.probabilities.SwingReference;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.LogLevel;
import com.mfg.logger.LogRecord;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.widget.probabilities.SimpleLogMessage.NonReachedTargetMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.ReachedTargetMessage;
import com.mfg.widget.probabilities.SimpleLogMessage.THLogMessage;

public class LogHelper {

	private Configuration configuration;
	private int idCounter;
	transient HtmlUtils hutils = new HtmlUtils(false, false);

	public LogHelper(Configuration aConfiguration) {
		super();
		this.configuration = aConfiguration;
	}

	public List<ILogRecord> toLogRecord(List<ISimpleLogMessage> aList) {
		ArrayList<ILogRecord> res = new ArrayList<>();
		for (ISimpleLogMessage e : aList) {
			res.add(toLogRecord(e));
		}
		return res;
	}

	public LogRecord toLogRecord(ISimpleLogMessage msg) {
		LogRecord record = new LogRecord(idCounter++, LogLevel.ANY,
				System.currentTimeMillis(), "Probabilities", msg);
		return record;
	}

	public static ArrayList<ISimpleLogMessage> merge2(
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

	public List<ILogRecord> mergeMessages(List<List<ISimpleLogMessage>> messages) {
		if (messages.size() == 0)
			return new ArrayList<>();
		ArrayList<ISimpleLogMessage> res = new ArrayList<>(messages.get(0));
		for (int i = 1; i < messages.size(); i++) {
			res = merge2(res, messages.get(i));
		}
		return toLogRecord(res);
	}

	public static List<ISimpleLogMessage> sortMSGs(
			List<ISimpleLogMessage> messages) {
		ISimpleLogMessage[] a = messages.toArray(new ISimpleLogMessage[] {});
		Arrays.sort(a, SimpleLogMessage.comparator());
		return Arrays.asList(a);
	}

	SimpleLogMessage THMessage(long aTime, long aPrice, int scale,
			ElementsPatterns patternLeaf, SwingReference ref, IElement wselement) {
		return new THLogMessage(
				scale,
				patternLeaf.getLeafID(),
				aTime,
				aPrice,
				ref.getTHTime(),
				ref.getTHPrice(),
				ISimpleLogMessage.CATEGORY_TH,
				hutils.getRawHtml(
						true,
						hutils.color(hutils.bold("TH arrived, "),
								Color.green.darker())
								+ hutils.color(
										"scale="
												+ scale
												+ (ref.isGoingUP() ? ", UP"
														: ", DOWN")
												+ (patternLeaf.getLeafID() > -1 ? (", PID=" + patternLeaf
														.getLeafID()) : "")
												+ ", "
												+ hutils.getBreakLine()
												+ getHtmlRatios(wselement,
														scale, hutils),
										Color.blue)));
	}

	public String getHtmlRatios(IElement e, int scale, HtmlUtils aHutils) {
		return getHtmlRatios(e, scale, aHutils, 0);
	}

	public String getNextHtmlRatios(IElement e, int scale, HtmlUtils aHutils) {
		return getHtmlRatios(e, scale, aHutils, -1);
	}

	public String getHtmlRatios(IElement e, int scale, HtmlUtils aHutils,
			int offset) {
		String res = configuration.getComputationType().toRString() + "="
				+ configuration.getTargetStep().round(e.getTarget(scale));
		double ratio = configuration.getTargetStep().round(
				e.getRatio(0, scale, offset));
		res += ", Sw" + aHutils.sub("0'") + "/Sw" + aHutils.sub("-1") + "="
				+ ratio;
		for (int i = 1; i < configuration.getMaxRatioLevel(); i++) {
			if (configuration.isRatioIncluded(i)) {
				ratio = configuration.getTargetStep().round(
						e.getRatio(i, scale, offset));
				res += ", Sw" + aHutils.sub("" + (-i)) + "/Sw"
						+ aHutils.sub("" + (-i - 1)) + "=" + ratio;
			}
		}
		return res;
	}

	public void logReached(ProbabilitiesKey aKey, SwingReference ref,
			ArrayList<HSTargetInfo> aTargetsPrices, IProbabilitiesSet d,
			ArrayList<HSTargetInfo> aTargetsPricesAux, int iTarget, long p,
			IIndicator widget) {
		if (!configuration.isLogging())
			return;
		ReachedTargetMessage msg = new ReachedTargetMessage(aKey,
				aTargetsPrices, widget.getCurrentTime(),
				widget.getCurrentPrice(), ref.getTHTime(), ref.getTHPrice(),
				ref.getP0Time(), ref.getP0Price(), ref.getPm1Time(),
				ref.getPm1Price(), p, ISimpleLogMessage.CATEGORY_TARGET,
				hutils.getRawHtml(
						true,
						hutils.color("R ", Color.blue)
								// + "[SC="
								// + aKey.getScale()
								// + ", TID="
								// + i
								// + ", PID="
								// + aKey.getPatternID()
								// + ", "
								+ "["
								+ toSimpleInfo(aKey, iTarget)
								+ "-->"
								+ configuration.getComputationType()
										.toRString()
								+ "="
								+ configuration.getTargetStep().roundMore(
										ref.getCurrentTargetPoints(), 1)
								+ "]"
								+ ((aTargetsPricesAux.size() > 0) ? (hutils
										.getBreakLine() + "HS=" + hutils
										.getHtmlBucketList(aTargetsPricesAux))
										: "")));
		msg.setElement(ref.getSwingElement());
		d.getLog().add(msg);
	}

	NonReachedTargetMessage nonReachedMSG(SwingReference ref, int iTarget,
			ArrayList<HSTargetInfo> targetsPrices, ProbabilitiesKey key,
			long t, long p, ArrayList<HSTargetInfo> aTargetsPricesAux) {
		NonReachedTargetMessage msg = new NonReachedTargetMessage(key,
				targetsPrices, t, p, ref.getTHTime(), ref.getTHPrice(),
				ref.getP0Time(), ref.getP0Price(), ref.getPm1Time(),
				ref.getPm1Price(), p, ISimpleLogMessage.CATEGORY_TARGET,
				hutils.getRawHtml(
						true,
						hutils.color("NR ", Color.red.darker())
								// + "[ SC="
								// + key.getScale()
								// + ", TID="
								// + i
								// + ", PID="
								// + key.getPatternID()
								// + ","

								+ "["
								+ toSimpleInfo(key, iTarget)
								+ "-->"
								+ configuration.getComputationType()
										.toRString()
								+ "="
								+ configuration.getTargetStep().roundMore(
										ref.getPattern().getTarget(iTarget), 1)
								+ "] "
								+ ((aTargetsPricesAux.size() > 0) ? (hutils
										.getBreakLine() + "HS=" + hutils
										.getHtmlBucketList(aTargetsPricesAux))
										: "")

				));
		return msg;
	}

	public static String toSimpleInfo(ProbabilitiesKey key, int target) {
		int priceClusterID = key.getPriceClusterID();
		int timeClusterID = key.getTimeClusterID();
		return "[ SC="
				+ key.getScale()
				+ ", TID="
				+ target
				+ (key.getPatternID() > -1 ? (", PID=" + key.getPatternID())
						: "")
				+ (priceClusterID > 0 ? (", PCID=" + priceClusterID) : "")
				+ (timeClusterID > 0 ? (", TCID=" + timeClusterID) : "");
	}

	public static ArrayList<HSTargetInfo> transformList(
			ArrayList<HSTargetInfo> aTargetsPrices, ProbabilitiesKey pKeyPar) {
		ProbabilitiesKey pkey = pKeyPar;
		ArrayList<HSTargetInfo> res = new ArrayList<>();
		for (int i = aTargetsPrices.size() - 1; i >= 0; i--) {
			HSTargetInfo k = aTargetsPrices.get(i);
			ProbabilitiesKey aKey = new ProbabilitiesKey(k.getKey().getScale(),
					k.getKey().getBaseScale(), k.getKey().getPattern(),
					pkey.isContrarian(), pkey.getClusterID(),
					// k.getKey().getScTouches(),
					k.getKey().getType(), k.getKey().getTimeClusterID(), k
							.getKey().getPriceClusterID());
			res.add(new HSTargetInfo(k.getPrice(), k.getPivotPrice(), k
					.getPivotTime(), k.getTargetID(), k.getTarget(), aKey));
			pkey = k.getKey();
		}
		return res;
	}

}
