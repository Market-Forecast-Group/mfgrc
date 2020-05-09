package com.mfg.chart.model;

public interface IParallelRealTimeZZModel {

	public final static IParallelRealTimeZZModel EMPTY = new IParallelRealTimeZZModel() {

		@Override
		public Data getRealtimeZZ(int level) {
			return null;
		}
	};

	public static class Data {
		public long x1;
		public long y1;
		public long x2;
		public long y2;
		public double topDistance;
		public double bottomDistance;
	}

	/**
	 * If there is not realtime ZZ, returns null.
	 * 
	 * @param level
	 * @return
	 */
	public Data getRealtimeZZ(int dataLayer);
}
