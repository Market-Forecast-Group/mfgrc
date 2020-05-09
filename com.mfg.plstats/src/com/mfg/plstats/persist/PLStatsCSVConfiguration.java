package com.mfg.plstats.persist;

import java.io.File;

import com.mfg.persist.interfaces.AbstractStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.plstats.PLStatsPlugin;

public class PLStatsCSVConfiguration extends AbstractStorageObject {

	private File _file;

	public PLStatsCSVConfiguration() {
		super();
	}

	public PLStatsCSVConfiguration(File file) {
		this();
		_file = file;
		String name = file.getName().substring(0, file.getName().length() - 4);
		setName(name);
	}

	public File getFile() {
		return _file;
	}

	public void setFile(File file) {
		_file = file;
	}

	@Override
	public SimpleStorage<?> getStorage() {
		return PLStatsPlugin.getDefault().getCSVStorage();
	}

}
