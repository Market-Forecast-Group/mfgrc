package com.mfg.widget.probabilities;

import java.io.Serializable;
import java.util.Arrays;

import com.mfg.utils.StepDefinition;

/**
 * contains the statistics for the algorithm to compute the T1 for the infinite
 * upper bounds patterns.
 * <p>
 * For the corresponding pattern (lower,Inf) we prepare some sub intervals that
 * will be filtered according to the percent ( {@link T1Computer.getPercent}) of
 * the total elements in the pattern:
 * <p>
 * <table border="1">
 * <tr>
 * <td>
 * lower
 * <td>lower
 * <td>...
 * <td>lower
 * <td>lower
 * </tr>
 * <tr>
 * <td>
 * lower+step
 * <td>lower+2*step
 * <td>...
 * <td>lower+(k-1)*step
 * <td>lower+k*step
 * </tr>
 * <tr>
 * <td>
 * C<sub>1</sub>
 * <td>C<sub>2</sub>
 * <td>...
 * <td>C<sub>(k-1)</sub>
 * <td>C<sub>k</sub>=Total
 * </tr>
 * </table>
 * where [lower,lower+k*step) is the first interval that includes all elements
 * of the pattern.
 * <p>
 * Also, for each sub interval is computed a probability as the portion of its
 * elements that reached the upper bound ( {@link T1Computer.getProbability}).
 * Then, it is selected as T1 the upper bound that belongs to the interval with
 * a closer probability to the mean probability for T1.
 * 
 * @author gardero
 * 
 */
public class T1Computer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Double[] upperbounds;
	int[] elements;
	int[] winnerElements;
	private int total;

	public static StepDefinition StepDefinition = new StepDefinition(0.01);

	private transient T1Model model = new T1Model(this);
	private int cursor;
	int bindex = -1;
	double minPercent;
	private double meanProb;

	public T1Computer() {
		super();
	}

	public T1Computer(double lower, double last, StepDefinition step) {
		upperbounds = step.getElements(lower, last, false);
		elements = new int[upperbounds.length];
		winnerElements = new int[upperbounds.length];
		System.out.println("length " + upperbounds.length);
	}

	public T1Computer(double lower, int size, StepDefinition step) {
		upperbounds = step.getNElements(lower, size, false);
		elements = new int[size];
		winnerElements = new int[size];
	}

	/**
	 * counts a case with a th Ratio that contributes to the statistics of the
	 * intervals that contains it.
	 * 
	 * @param thRatio
	 */
	@SuppressWarnings("boxing")
	public void countCase(double thRatio) {
		total++;
		for (int i = upperbounds.length - 1; i >= 0; i--) {
			if (thRatio <= upperbounds[i]) {
				elements[i]++;
				cursor = i;
			} else
				break;
		}
	}

	/**
	 * for a range of meet targets, counts the observations that passed the
	 * upper bound for each interval.
	 * 
	 * @param from
	 *            initial target.
	 * @param to
	 *            end target.
	 */
	public void countTargets(int fromPar, int toPar) {
		int to = toPar;
		int from = fromPar;
		from = Math.max(from, 2);
		from = Math.max(from, cursor + 2);
		to = Math.min(to, winnerElements.length + 2);
		for (int i = from; i < to; i++) {
			winnerElements[i - 2]++;
		}
	}

	/**
	 * gets the upper bound of the interval that is closest to the desired
	 * probability and mets the minPercent rule.
	 * 
	 * @param prob
	 *            the desired probability.
	 * @param aMinPercent
	 *            minimum percent allowed.
	 * @return the upper bound of the best candidate.
	 */
	@SuppressWarnings("boxing")
	public double getClosestTo(double prob, double aMinPercent) {
		bindex = -1;
		meanProb = prob;
		this.minPercent = aMinPercent;
		double best = Double.MAX_VALUE;
		for (int i = upperbounds.length - 1; i >= 0; i--) {
			if (getPercent(i) >= aMinPercent) {
				double abs = Math.abs(prob - getProbability(i));
				if (bindex < 0 || abs < best) {
					bindex = i;
					best = abs;
				}
			} else
				break;
		}
		return upperbounds[bindex];
	}

	/**
	 * gets the resulting probability of an interval.
	 * 
	 * @param index
	 *            the index of the interval.
	 * @return the probability.
	 */
	@SuppressWarnings("boxing")
	double getProbability(int index) {
		return new Double(winnerElements[index]) / new Double(elements[index]);
	}

	/**
	 * gets the percent of the elements in the interval relative to the total
	 * number of elements in the pattern.
	 * 
	 * @param index
	 *            the index of the interval
	 * @return the percent in [0..1]
	 */
	@SuppressWarnings("boxing")
	double getPercent(int index) {
		return new Double(elements[index]) / new Double(total);
	}

	@Override
	public String toString() {
		return "U=" + Arrays.toString(upperbounds) + "\nE="
				+ Arrays.toString(elements) + "\nW="
				+ Arrays.toString(winnerElements) + "\n, total=" + total;
	}

	public T1Model getModel() {
		if (model == null) {
			model = new T1Model(this);
		}
		return model;
	}

	/**
	 * gets the upper bounds of the sub intervals.
	 * 
	 * @return
	 */
	public Double[] getUpperbounds() {
		return upperbounds;
	}

	/**
	 * gets the counters of elements in each interval.
	 * 
	 * @return a counter for each interval.
	 */
	public int[] getElements() {
		return elements;
	}

	public void setElements(int[] aElements) {
		elements = aElements;
	}

	/**
	 * gets the counters of elements that reached the upper bound of the
	 * interval.
	 * 
	 * @return a counter for each interval.
	 */
	public int[] getWinnerElements() {
		return winnerElements;
	}

	/**
	 * gets the minimum percent of elements allowed for an interval to be
	 * considered.
	 * 
	 * @return a [0..1] value.
	 */
	public double getMinPercent() {
		return minPercent;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int aTotal) {
		total = aTotal;
	}

	public void setUpperbounds(Double[] aUpperbounds) {
		upperbounds = aUpperbounds;
	}

	public void setWinnerElements(int[] aWinnerElements) {
		winnerElements = aWinnerElements;
	}

	public void setModel(T1Model aModel) {
		model = aModel;
	}

	public void setMinPercent(double aMinPercent) {
		minPercent = aMinPercent;
	}

	public int getBindex() {
		return bindex;
	}

	public double getMeanProb() {
		return meanProb;
	}

}
