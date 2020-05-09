package com.mfg.widget.priv;

import java.io.Serializable;

public interface IRandomizer extends Serializable{
	public int nextInt(int n);
	public double nextDouble();
}
