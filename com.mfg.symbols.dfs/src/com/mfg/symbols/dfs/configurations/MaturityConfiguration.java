package com.mfg.symbols.dfs.configurations;

import java.beans.PropertyChangeListener;
import java.util.UUID;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;

public class MaturityConfiguration implements IStorageObject {

	private UUID uuid;
	private String maturity;

	public MaturityConfiguration() {
		uuid = UUID.randomUUID();

	}

	@Override
	public boolean allowRename() {
		return false;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String getName() {
		return maturity == null ? "?" : maturity;
	}

	@Override
	public SimpleStorage<?> getStorage() {
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(String property) {
		// TODO Auto-generated method stub

	}

}
