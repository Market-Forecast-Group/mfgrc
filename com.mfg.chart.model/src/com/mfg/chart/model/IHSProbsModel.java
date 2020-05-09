/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.chart.model;

/**
 * <p>
 * This is the specification of this model:
 * </p>
 * <p>
 * Enrique said:
 * </p>
 * <p>
 * 
 * Hi arian... now giulio wants the chart HS markers back..., in general the
 * chart we show in the statistics perspective should be linked to the log and
 * should show these lines when we select a target....for a message of class
 * KeyLogMessage, you will have a square
 * [getPm1Time(),getPm1Price()]-[getP0Time(),getP0Price()] and another
 * [getP0Time(),getP0Price()] - [getTime(), getTargetPrice()] of the same color
 * on scale getScale(). then it has a list of Higher Scales info, of the class
 * HSTargetInfo, and for each of these scales you will draw a square from
 * [getP0Time(),getP0Price()] to [getTime(),hs[i].getTarget()()].
 * </p>
 * 
 * Since it is all about to paint squares, the interface of this model will
 * provides only a way to get squares.
 * 
 * @author arian
 * 
 */
public interface IHSProbsModel {
	public final static IHSProbsModel EMPTY = new IHSProbsModel() {

		@Override
		public long getTime1(int square) {
			return 0;
		}

		@Override
		public long getTime0(int square) {
			return 0;
		}

		@Override
		public int getSquaresCount() {
			return 0;
		}

		@Override
		public double getPrice1(int square) {
			return 0;
		}

		@Override
		public double getPrice0(int square) {
			return 0;
		}

		@Override
		public int getScale(int square) {
			return 0;
		}

	};

	public int getSquaresCount();

	public long getTime0(int square);

	public double getPrice0(int square);

	public long getTime1(int square);

	public double getPrice1(int square);

	public int getScale(int square);
}
