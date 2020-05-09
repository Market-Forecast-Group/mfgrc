package com.mfg.strategy.automatic.triggers;

import java.util.Random;

import com.mfg.widget.priv.IRandomizer;

public class JavaUtilsRandomizer implements IRandomizer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Random random;
	
	protected boolean randomizing;
	
	public JavaUtilsRandomizer() {
		this(true);
	}
	
	public JavaUtilsRandomizer(boolean aRandomizing) {
		super();
		random = new Random();
		this.randomizing = aRandomizing;
	}



	@Override
	public int nextInt(int n) {
		if (!randomizing)
			return n-1;
		return random.nextInt(n);
	}

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}

}
