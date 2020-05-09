package com.mfg.utils.socket;

import java.util.Observable;

public class SimpleTextClientsModel extends Observable {

	/**
	 * simply overridden public version of the
	 * {@linkplain Observable#setChanged}
	 */
	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

}
