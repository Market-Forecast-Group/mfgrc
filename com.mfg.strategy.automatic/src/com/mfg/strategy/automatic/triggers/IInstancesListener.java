package com.mfg.strategy.automatic.triggers;

public interface IInstancesListener<T> {
	void processInstance(T instance);
}
