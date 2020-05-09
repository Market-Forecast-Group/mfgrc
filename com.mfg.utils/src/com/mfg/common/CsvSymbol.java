package com.mfg.common;

/**
 * 
 * @author Sergio
 * 
 */
public class CsvSymbol extends MfgSymbol {

	private final String _fileName;
	private int _tick;
	private int _scale;

	public CsvSymbol(String fileName) {
		_fileName = fileName;
	}

	@Override
	public int getTick() {
		return _tick;
	}

	@Override
	public int getScale() {
		return _scale;
	}

	@Override
	public String getSymbol() {
		return _fileName;
	}

	/**
	 * sets the computed tick and scale.
	 * 
	 * <p>
	 * For the case of the
	 * 
	 * @param tick
	 * @param scale_from_the_source
	 */
	public void setComputedValues(int tick, int scale_from_the_source) {
		_tick = tick;
		_scale = scale_from_the_source;
	}

	@Override
	public int getTickValue() {
		/*
		 * The tick value of a csv symbol is one as a default.
		 */
		return 1;
	}

}
