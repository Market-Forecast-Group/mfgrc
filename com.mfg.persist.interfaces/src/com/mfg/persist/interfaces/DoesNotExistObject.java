package com.mfg.persist.interfaces;

import java.beans.PropertyChangeListener;
import java.util.UUID;

public class DoesNotExistObject implements IStorageObject {

	private final SimpleStorage<?> _storage;

	public DoesNotExistObject(SimpleStorage<?> storage) {
		super();
		_storage = storage;
	}

	@Override
	public UUID getUUID() {
		return UUID.fromString("a2084dc2-f6af-41f8-a315-a6b5a8c498c3");
	}

	@Override
	public String getName() {
		return "<Dos not exist>";
	}

	@Override
	public SimpleStorage<?> getStorage() {
		return _storage;
	}

	@Override
	public void setName(String name) {
		//
	}

	@Override
	public boolean allowRename() {
		return false;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		//
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		//
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		//
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		//
	}

	@Override
	public void firePropertyChange(String property) {
		//
	}

}
