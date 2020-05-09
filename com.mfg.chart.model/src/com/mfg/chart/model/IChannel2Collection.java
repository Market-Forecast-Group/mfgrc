package com.mfg.chart.model;

public interface IChannel2Collection extends IItemCollection {
	IChannel2Collection EMPTY = new IChannel2Collection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public double getStart(int i) {
			return 0;
		}

		@Override
		public double getEnd(int i) {
			return 0;
		}

		@Override
		public double evaluateCentarLine(int i, double time) {
			return 0;
		}

		@Override
		public double getTopDistance(int i) {
			return 0;
		}

		@Override
		public double getBottomDistance(int i) {
			return 0;
		}

	};

	public double getStart(int i);

	public double getEnd(int i);

	public double evaluateCentarLine(int i, double time);

	public double getTopDistance(int i);

	public double getBottomDistance(int i);
}
