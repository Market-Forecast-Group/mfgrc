package com.mfg.symbols.dfs.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.mfg.common.BarType;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSProfile;
import com.mfg.symbols.dfs.configurations.DFSProfile.SlotInfo;
import com.thoughtworks.xstream.XStream;

public class DFSProfileStorage extends SimpleStorage<DFSProfile> {
	private DFSProfile _defProfile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#configureXStream(com.thoughtworks
	 * .xstream.XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		DFSSymbolsPlugin.getDefault().getDFSStorage().configureXStream(xstream);
		xstream.alias("dfs-profile", DFSProfile.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#saveAll(java.io.File)
	 */
	@Override
	public void saveAll(File workspace) throws IOException {
		super.saveAll(workspace);
		File file = new File(getStorageDir(workspace), "default-profile.txt");
		file.getParentFile().mkdirs();
		try (FileWriter w = new FileWriter(file)) {
			w.write(_defProfile == null ? "" : _defProfile.getUUID().toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#loadAll(java.io.File)
	 */
	@Override
	public void loadAll(File workspace) {
		super.loadAll(workspace);
		File file = new File(getStorageDir(workspace), "default-profile.txt");
		if (file.exists()) {
			try (BufferedReader r = new BufferedReader(new FileReader(file))) {
				String id = r.readLine();
				r.close();
				if (id != null) {
					try {
						_defProfile = findById(id);
					} catch (IllegalArgumentException e) {
						// Adding a comment to avoid empty block warning.
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (_defProfile == null) {
			_defProfile = new DFSProfile();
			_defProfile.setName("Default Profile");
			_defProfile.getSlots().add(new SlotInfo(BarType.DAILY, 500));
			_defProfile.getSlots().add(new SlotInfo(BarType.MINUTE, 100));
			_defProfile.getSlots().add(new SlotInfo(BarType.RANGE, 50));
			add(_defProfile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#remove(com.mfg.persist.interfaces
	 * .IStorageObject)
	 */
	@Override
	public void remove(DFSProfile obj) throws RemoveException {
		super.remove(obj);
		if (obj == _defProfile) {
			_defProfile = null;
		}
	}

	/**
	 * @return the defaultProfile
	 */
	public DFSProfile getDefaultProfile() {
		return _defProfile;
	}

	/**
	 * @param aDefaultProfile
	 *            the defaultProfile to set
	 */
	public void setDefaultProfile(DFSProfile aDefaultProfile) {
		this._defProfile = aDefaultProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#createDefaultObject()
	 */
	@Override
	public DFSProfile createDefaultObject() {
		return new DFSProfile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#getStorageName()
	 */
	@Override
	public String getStorageName() {
		return "DFS-Profiles-2";
	}
}
