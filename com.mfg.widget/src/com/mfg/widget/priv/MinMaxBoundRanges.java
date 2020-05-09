package com.mfg.widget.priv;

import java.io.Serializable;
import java.util.Arrays;

import com.mfg.utils.MathUtils;

public class MinMaxBoundRanges implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int dim3;
	protected int dim;

	/**
	 * This is only a temporary vector. It is used to store the swing ratios.
	 */
	protected double[] minSwingsRatios;
	protected double[] maxSwingsRatios;
	protected double[] minSwing0Ratios;
	protected double[] maxSwing0Ratios;
	/**
	 * this is for timeRatios just as price ratios
	 */
	protected double[] minTimeRatios;
	protected double[] maxTimeRatios;
	protected double[] minTime0Ratios;
	protected double[] maxTime0Ratios;
	/**
	 * and this other one considers modules vector ratios
	 */
	protected double[] minVectorRatios;
	protected double[] maxVectorRatios;
	protected double[] minVector0Ratios;
	protected double[] maxVector0Ratios;

	public MinMaxBoundRanges() {
		this(1);
	}

	public MinMaxBoundRanges(int aDim) {
		dim3 = (aDim * (aDim - 1)) / 2;
		this.dim = aDim;
		minSwingsRatios = new double[dim3];
		maxSwingsRatios = new double[dim3];
		minTimeRatios = new double[dim3];
		maxTimeRatios = new double[dim3];
		minVectorRatios = new double[dim3];
		maxVectorRatios = new double[dim3];
		int dimS0R = 3;
		minSwing0Ratios = new double[dimS0R];
		maxSwing0Ratios = new double[dimS0R];
		minTime0Ratios = new double[dimS0R];
		maxTime0Ratios = new double[dimS0R];
		minVector0Ratios = new double[dimS0R];
		maxVector0Ratios = new double[dimS0R];

	}

	// public static int get3Row(int p, int dim3){
	// int c = 2*(dim3 - (p+1));
	// return (int)Math.floor((1+Math.sqrt(1+4*c))/(2.0));
	// }
	//
	// public static int get3Col(int p, int dim3){
	// int m = get3Row(p,dim3);
	// return p+m*(m+1)/2-dim3;
	// }

	public static int get3Row(int p) {
		int c = 2 * p;
		return -1 - (int) Math.floor((1 + Math.sqrt(1 + 4 * c)) / (2.0));
	}

	public static int get3Col(int p) {
		int m = -(get3Row(p) + 2);
		return -p + m * (m + 1) / 2 - 1;
	}

	public void processASwing(int pos, double value) {
		minSwingsRatios[pos] = Math.min(minSwingsRatios[pos], value);
		maxSwingsRatios[pos] = Math.max(maxSwingsRatios[pos], value);
	}

	public void processATime(int pos, double value) {
		minTimeRatios[pos] = Math.min(minTimeRatios[pos], value);
		maxTimeRatios[pos] = Math.max(maxTimeRatios[pos], value);
	}

	public void processAVector(int pos, double value) {
		minVectorRatios[pos] = Math.min(minVectorRatios[pos], value);
		maxVectorRatios[pos] = Math.max(maxVectorRatios[pos], value);
	}

	public void processASwing0(int pos, double value) {
		minSwing0Ratios[pos] = Math.min(minSwing0Ratios[pos], value);
		maxSwing0Ratios[pos] = Math.max(maxSwing0Ratios[pos], value);
	}

	public void processATime0(int pos, double value) {
		minTime0Ratios[pos] = Math.min(minTime0Ratios[pos], value);
		maxTime0Ratios[pos] = Math.max(maxTime0Ratios[pos], value);
	}

	public void processAVector0(int pos, double value) {
		minVector0Ratios[pos] = Math.min(minVector0Ratios[pos], value);
		maxVector0Ratios[pos] = Math.max(maxVector0Ratios[pos], value);
	}

	public double getMaxSwing0Ratio(int i, TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(maxSwing0Ratios, maxTime0Ratios,
				maxVector0Ratios)[i];
	}

	public double getMinSwing0Ratio(int i, TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(minSwing0Ratios, minTime0Ratios,
				minVector0Ratios)[i];
	}

	public double getMaxRange(int i, TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(maxSwingsRatios, maxTimeRatios,
				maxVectorRatios)[i];
	}

	public double[] getMaxRange(TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(maxSwingsRatios, maxTimeRatios,
				maxVectorRatios);
	}

	public double getMinRange(int i, TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(minSwingsRatios, minTimeRatios,
				minVectorRatios)[i];
	}

	public double[] getMinRange(TRIGGER_TYPE type) {
		return type.giveMeArrayToEval(minSwingsRatios, minTimeRatios,
				minVectorRatios);
	}

	public MinMaxBoundRanges intersect(MinMaxBoundRanges other) {
		MinMaxBoundRanges res = new MinMaxBoundRanges(this.dim);

		intersectMin(res.minSwing0Ratios, this.minSwing0Ratios,
				other.minSwing0Ratios);
		intersectMax(res.maxSwing0Ratios, this.maxSwing0Ratios,
				other.maxSwing0Ratios);
		intersectMin(res.minTime0Ratios, this.minTime0Ratios,
				other.minTime0Ratios);
		intersectMax(res.maxTime0Ratios, this.maxTime0Ratios,
				other.maxTime0Ratios);
		intersectMin(res.minVector0Ratios, this.minVector0Ratios,
				other.minVector0Ratios);
		intersectMax(res.maxVector0Ratios, this.maxVector0Ratios,
				other.maxVector0Ratios);

		intersectMin(res.minSwingsRatios, this.minSwingsRatios,
				other.minSwingsRatios);
		intersectMax(res.maxSwingsRatios, this.maxSwingsRatios,
				other.maxSwingsRatios);
		intersectMin(res.minTimeRatios, this.minTimeRatios, other.minTimeRatios);
		intersectMax(res.maxTimeRatios, this.maxTimeRatios, other.maxTimeRatios);
		intersectMin(res.minVectorRatios, this.minVectorRatios,
				other.minVectorRatios);
		intersectMax(res.maxVectorRatios, this.maxVectorRatios,
				other.maxVectorRatios);

		return res;
	}

	public MinMaxBoundRanges join(MinMaxBoundRanges other) {
		MinMaxBoundRanges res = new MinMaxBoundRanges(this.dim);

		joinMin(res.minSwing0Ratios, this.minSwing0Ratios,
				other.minSwing0Ratios);
		joinMax(res.maxSwing0Ratios, this.maxSwing0Ratios,
				other.maxSwing0Ratios);
		joinMin(res.minTime0Ratios, this.minTime0Ratios, other.minTime0Ratios);
		joinMax(res.maxTime0Ratios, this.maxTime0Ratios, other.maxTime0Ratios);
		joinMin(res.minVector0Ratios, this.minVector0Ratios,
				other.minVector0Ratios);
		joinMax(res.maxVector0Ratios, this.maxVector0Ratios,
				other.maxVector0Ratios);

		joinMin(res.minSwingsRatios, this.minSwingsRatios,
				other.minSwingsRatios);
		joinMax(res.maxSwingsRatios, this.maxSwingsRatios,
				other.maxSwingsRatios);
		joinMin(res.minTimeRatios, this.minTimeRatios, other.minTimeRatios);
		joinMax(res.maxTimeRatios, this.maxTimeRatios, other.maxTimeRatios);
		joinMin(res.minVectorRatios, this.minVectorRatios,
				other.minVectorRatios);
		joinMax(res.maxVectorRatios, this.maxVectorRatios,
				other.maxVectorRatios);

		return res;
	}

	private static void intersectMin(double[] res, double[] arr1, double[] arr2) {
		for (int i = 0; i < arr1.length; i++) {
			res[i] = Math.max(arr1[i], arr2[i]);
		}
	}

	private static void joinMin(double[] res, double[] arr1, double[] arr2) {
		for (int i = 0; i < arr1.length; i++) {
			res[i] = Math.min(arr1[i], arr2[i]);
		}
	}

	private static void intersectMax(double[] res, double[] arr1, double[] arr2) {
		for (int i = 0; i < arr1.length; i++) {
			res[i] = Math.min(arr1[i], arr2[i]);
		}
	}

	private static void joinMax(double[] res, double[] arr1, double[] arr2) {
		for (int i = 0; i < arr1.length; i++) {
			res[i] = Math.max(arr1[i], arr2[i]);
		}
	}

	private static void adjustWithStep(double[] arr, int szInt, int szScale) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = MathUtils.normalizeDownUsingStep(arr[i], szInt, szScale);
		}
	}

	public void adjustWithStep() {
		int stepSize_int = DiscreteIntervalDomain.DEFAULT_STEP_int;
		int stepSize_scale = DiscreteIntervalDomain.DEFAULT_STEP_scale * 10;

		adjustWithStep(minSwing0Ratios, stepSize_int, stepSize_scale);
		adjustWithStep(maxSwing0Ratios, stepSize_int, stepSize_scale);
		adjustWithStep(minTime0Ratios, stepSize_int, stepSize_scale);
		adjustWithStep(maxTime0Ratios, stepSize_int, stepSize_scale);
		adjustWithStep(minVector0Ratios, stepSize_int, stepSize_scale);
		adjustWithStep(maxVector0Ratios, stepSize_int, stepSize_scale);

		adjustWithStep(minSwingsRatios, stepSize_int, stepSize_scale);
		adjustWithStep(maxSwingsRatios, stepSize_int, stepSize_scale);
		adjustWithStep(minTimeRatios, stepSize_int, stepSize_scale);
		adjustWithStep(maxTimeRatios, stepSize_int, stepSize_scale);
		adjustWithStep(minVectorRatios, stepSize_int, stepSize_scale);
		adjustWithStep(maxVectorRatios, stepSize_int, stepSize_scale);

	}

	public static int tranlateFrom(int i, int dim, int dim2) {
		// int dim31 = (dim*(dim-1))/2;
		int dim32 = (dim2 * (dim2 - 1)) / 2;
		int row = get3Row(i);
		int col = get3Col(i);
		col = col + (dim2 - dim);
		row = row + (dim2 - dim);
		int res = dim32 - ((row + 1) * row) / 2 + col;
		return res;
	}

	public static boolean contatins(int i, int dim, int dim2) {
		if (dim <= dim2)
			return true;
		// int dim31 = (dim*(dim-1))/2;
		int row = get3Row(i);
		int col = get3Col(i);
		return (col >= (dim - dim2) && row >= (dim - dim2));
	}

	public static void main(String[] args) {
		// MinMaxBoundRanges me = new MinMaxBoundRanges(5);
		int d = 5;
		for (int i = 0; i < (d * (d - 1)) / 2; i++) {
			System.out.println(i + "=>(" + get3Row(i) + "," + get3Col(i) + ")");
		}
	}

	@Override
	public String toString() {
		return "MinMaxBoundRanges ["
				+ (maxSwingsRatios != null ? "maxSwingsRatios="
						+ Arrays.toString(maxSwingsRatios) + ", " : "")
				+ (minSwingsRatios != null ? "minSwingsRatios="
						+ Arrays.toString(minSwingsRatios) + ", " : "")
				+ (maxTimeRatios != null ? "maxTimeRatios="
						+ Arrays.toString(maxTimeRatios) + ", " : "")
				+ (minTimeRatios != null ? "minTimeRatios="
						+ Arrays.toString(minTimeRatios) + ", " : "")
				+ (maxVectorRatios != null ? "maxVectorRatios="
						+ Arrays.toString(maxVectorRatios) + ", " : "")
				+ (minVectorRatios != null ? "minVectorRatios="
						+ Arrays.toString(minVectorRatios) : "") + "]";
	}

	public static int getBoundsSize(int value) {
		return (value * value - value) / 2;
	}

	public int getDim3() {
		return dim3;
	}

	public void setDim3(int aDim3) {
		this.dim3 = aDim3;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int aDim) {
		this.dim = aDim;
	}

	public double[] getMinSwingsRatios() {
		return minSwingsRatios;
	}

	public void setMinSwingsRatios(double[] aMinSwingsRatios) {
		this.minSwingsRatios = aMinSwingsRatios;
	}

	public double[] getMaxSwingsRatios() {
		return maxSwingsRatios;
	}

	public void setMaxSwingsRatios(double[] aMaxSwingsRatios) {
		this.maxSwingsRatios = aMaxSwingsRatios;
	}

	public double[] getMinSwing0Ratios() {
		return minSwing0Ratios;
	}

	public void setMinSwing0Ratios(double[] aMinSwing0Ratios) {
		this.minSwing0Ratios = aMinSwing0Ratios;
	}

	public double[] getMaxSwing0Ratios() {
		return maxSwing0Ratios;
	}

	public void setMaxSwing0Ratios(double[] aMaxSwing0Ratios) {
		this.maxSwing0Ratios = aMaxSwing0Ratios;
	}

	public double[] getMinTimeRatios() {
		return minTimeRatios;
	}

	public void setMinTimeRatios(double[] aMinTimeRatios) {
		this.minTimeRatios = aMinTimeRatios;
	}

	public double[] getMaxTimeRatios() {
		return maxTimeRatios;
	}

	public void setMaxTimeRatios(double[] aMaxTimeRatios) {
		this.maxTimeRatios = aMaxTimeRatios;
	}

	public double[] getMinTime0Ratios() {
		return minTime0Ratios;
	}

	public void setMinTime0Ratios(double[] aMinTime0Ratios) {
		this.minTime0Ratios = aMinTime0Ratios;
	}

	public double[] getMaxTime0Ratios() {
		return maxTime0Ratios;
	}

	public void setMaxTime0Ratios(double[] aMaxTime0Ratios) {
		this.maxTime0Ratios = aMaxTime0Ratios;
	}

	public double[] getMinVectorRatios() {
		return minVectorRatios;
	}

	public void setMinVectorRatios(double[] aMinVectorRatios) {
		this.minVectorRatios = aMinVectorRatios;
	}

	public double[] getMaxVectorRatios() {
		return maxVectorRatios;
	}

	public void setMaxVectorRatios(double[] aMaxVectorRatios) {
		this.maxVectorRatios = aMaxVectorRatios;
	}

	public double[] getMinVector0Ratios() {
		return minVector0Ratios;
	}

	public void setMinVector0Ratios(double[] aMinVector0Ratios) {
		this.minVector0Ratios = aMinVector0Ratios;
	}

	public double[] getMaxVector0Ratios() {
		return maxVector0Ratios;
	}

	public void setMaxVector0Ratios(double[] aMaxVector0Ratios) {
		this.maxVector0Ratios = aMaxVector0Ratios;
	}

	// getter and setter for serialization

}
