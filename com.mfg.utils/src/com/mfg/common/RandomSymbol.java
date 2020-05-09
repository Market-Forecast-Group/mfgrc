package com.mfg.common;

/**
 * A symbol used only in testing. It is not tied to any real data source, not
 * even a simulated data source.
 * 
 * <p>
 * In this way we can create infinite streams of data from infinite symbols
 * (even the simulator needs to have real tables on disk!, in this case,
 * however, we are able to create a infinite supply of pseudorandom data also
 * playing with tick, tick size and tick value, in this way stressing the tea
 * and dfs connection.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class RandomSymbol extends MfgSymbol {

	private final int _tick;
	private final int _scale;
	private final String _symbol;
	private final int _tickValue;

	public RandomSymbol(String aSymbol, int aTick, int aScale, int aTickValue) {
		_symbol = aSymbol;
		_tick = aTick;
		_scale = aScale;
		_tickValue = aTickValue;
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
		return _symbol;
	}

	@Override
	public int getTickValue() {
		return _tickValue;
	}

}
