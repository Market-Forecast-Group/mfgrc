package com.mfg.widget.probabilities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * contains tools to compute histograms.
 * 
 * @author gardero
 * 
 * @param <T>
 */
public class DistributionSet<T> {
	public static class DoubleDistributionSet extends DistributionSet<Double> {
		protected double sum = 0;
		protected int cant = 0;

		@SuppressWarnings("boxing")
		@Override
		public void add(Double e) {
			super.add(e);
			sum += e;
			cant++;
		}

		@SuppressWarnings("boxing")
		@Override
		public void add(Double e, int times) {
			super.add(e, times);
			sum += e;
			cant += times;
		}

		public double getMean() {
			return sum / cant;
		}
	}

	protected Hashtable<T, Integer> table;
	protected T mode;
	protected int modeCount = 0;
	protected ArrayList<T> values;

	public DistributionSet() {
		super();
		table = new Hashtable<>();
		values = new ArrayList<>();
	}

	@SuppressWarnings("boxing")
	public void add(T e) {
		values.add(e);
		int v;
		if (table.containsKey(e)) {
			table.put(e, v = table.get(e) + 1);
		} else
			table.put(e, v = 1);
		if (v > modeCount) {
			modeCount = v;
			mode = e;
		}
	}

	@SuppressWarnings("boxing")
	public void add(T e, int times) {
		for (int i = 0; i < times; i++) {
			values.add(e);
		}
		int v;
		if (table.containsKey(e)) {
			table.put(e, v = table.get(e) + times);
		} else
			table.put(e, v = times);
		if (v > modeCount) {
			modeCount = v;
			mode = e;
		}
	}

	public List<T> getValues() {
		return values;
	}

	@SuppressWarnings("boxing")
	public int getCount(T e) {
		return table.get(e);
	}

	public Set<T> getElements() {
		return table.keySet();
	}

	public T getMode() {
		return mode;
	}

	@SuppressWarnings("boxing")
	public double getVariationRatio() {
		return 1 - new Double(modeCount) / new Double(values.size());
	}

	@Override
	public String toString() {
		return "DistributionSet [table=" + table + "]";
	}

	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		DistributionSet<Integer> i = new DistributionSet<>();
		i.add(1);
		i.add(0);
		i.add(1);
		System.out.println(i);
	}

}
