/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.persist.interfaces;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

/**
 * @author arian
 * 
 */
public abstract class SimpleStorage<T extends IStorageObject> extends
		AbstractWorkspaceStorage {
	private final List<T> objects;
	private final Map<T, File> fileMap;
	private final XStream xstream;
	private boolean loaded;
	private PropertyChangeSupport _propertySupport;
	private List<IWorkspaceStorageInitiator> _initList;

	public SimpleStorage() {
		loaded = false;
		objects = new ArrayList<>();
		xstream = new XStream(new PureJavaReflectionProvider());
		fileMap = new HashMap<>();
		_propertySupport = new PropertyChangeSupport(this);
		init();
		PersistInterfacesPlugin.getDefault()._loadedStorages.add(this);
	}

	public static IStorageObject findByIdInStorages(String id,
			SimpleStorage<?>... storages) {
		for (SimpleStorage<?> storage : storages) {
			IStorageObject obj = storage.findById(id);
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	/**
	 * Called when it is loading a new workspace
	 */
	public void reload() {
		_propertySupport = new PropertyChangeSupport(this);
		objects.clear();
		loaded = false;
		fileMap.clear();
		init();
	}

	protected void init() {
		if (_initList == null) {
			IConfigurationElement[] configElements;
			configElements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							PersistInterfacesPlugin.STORAGE_INITIATOR_ID);
			_initList = new ArrayList<>();
			try {
				for (IConfigurationElement configElement : configElements) {
					final Object obj = configElement
							.createExecutableExtension("class");
					_initList.add((IWorkspaceStorageInitiator) obj);
				}
			} catch (CoreException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}

		for (IWorkspaceStorageInitiator i : _initList) {
			i.intitialize(this);
		}

		configureXStream();

		File workspace = new File(PersistInterfacesPlugin.getDefault()
				.getCurrentWorkspacePath());
		loadAll(workspace);
		storageLoaded();
	}

	/**
	 * This property change support can be used to listen storage-related events
	 * defined by clients.
	 * 
	 * @return
	 */
	public PropertyChangeSupport getPropertySupport() {
		return _propertySupport;
	}

	/**
	 * @return the xstream
	 */
	public XStream getXStream() {
		return xstream;
	}

	public abstract T createDefaultObject();

	public void add(T obj) {
		objects.add(obj);
		fireObjectAdded(obj);
	}

	public void addAll(List<T> objs) {
		objects.addAll(objs);
		fireListAdded(objs);
	}

	public T findById(UUID uuid) {
		for (T obj : objects) {
			if (obj.getUUID().equals(uuid)) {
				return obj;
			}
		}
		return null;
	}

	public T findById(String uuid) {
		for (T obj : objects) {
			if (obj.getUUID().toString().equals(uuid)) {
				return obj;
			}
		}
		return null;
	}

	public boolean containsName(String name) {
		for (IStorageObject obj : getObjects()) {
			if (obj.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void remove(T obj) throws RemoveException {
		fireObjectAboutToRemove(obj);
		File file = fileMap.get(obj);
		if (file != null) {
			if (!file.delete()) {
				throw new RemoveException("Remove file " + file + " failed.");
			}
		}
		fileMap.remove(obj);
		objects.remove(obj);
		fireObjectRemoved(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.AbstractWorkspaceStorage#isPersisted(java.
	 * lang.Object)
	 */
	@Override
	public boolean isPersisted(Object obj) {
		return fileMap.containsKey(obj);
	}

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return
	 */
	public List<T> getObjects() {
		return objects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.AbstractWorkspaceStorage#getName(java.lang
	 * .Object)
	 */
	@Override
	public String getName(Object obj) {
		if (obj instanceof IStorageObject) {
			return ((IStorageObject) obj).getName();
		}
		return super.getName(obj);
	}

	/**
	 * Save all the objects.
	 * 
	 * @param workspace
	 * @throws IOException
	 */
	public void saveAll(File workspace) throws IOException {
		List<T> list = new ArrayList<>(objects);

		for (T obj : list) {
			save(obj, workspace);
		}
	}

	public void save(T obj, File workspace) throws IOException {
		File file = fileMap.get(obj);
		if (file == null) {
			file = new File(getStorageDir(workspace), getFileName(obj) + ".xml");
			file.getParentFile().mkdirs();
			int i = 0;
			while (file.exists()) {
				file = new File(getStorageDir(workspace), getFileName(obj)
						+ "-" + i + ".xml");
				i++;
			}
			file.createNewFile();
		}

		try (FileOutputStream stream = new FileOutputStream(file)) {
			xstream.toXML(obj, stream);
		}
		fileMap.put(obj, file);
	}

	/**
	 * @param obj
	 * @return By default <code>obj.getName()</code>.
	 */
	public String getFileName(T obj) {
		return obj.getName();
	}

	/**
	 * Load all the objects.
	 * 
	 * @param workspace
	 * 
	 * @param workspace
	 */
	public void loadAll(File workspace) {
		if (!loaded) {
			loaded = true;
			objects.clear();
			File storageDir = getStorageDir(workspace);
			File[] files = storageDir.listFiles();
			if (files != null) {
				for (File file : files) {
					loadFile(file);
				}
			}
			fireStorageChanged();
		}
	}

	protected T loadFile(File file) {
		if (acceptFile(file)) {
			@SuppressWarnings("unchecked")
			T obj = (T) xstream.fromXML(file);
			if (acceptObject(obj)) {
				fileMap.put(obj, file);
				initDeserializedObject(obj);
				add(obj);
			}
			return obj;
		}
		return null;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	protected boolean acceptObject(T obj) {
		return true;
	}

	protected static boolean acceptFile(File file) {
		return file.getName().toLowerCase().endsWith("xml");
	}

	/**
	 * A derived class maybe wants to override this method to initialize a
	 * deserialized object. The more common case is when the object has a
	 * references (UUID) to other object in other storage.
	 * 
	 * @param workspace
	 * @param obj
	 */
	protected void initDeserializedObject(T obj) {
		//
	}

	public String getStorageName() {
		String name = getClass().getSimpleName();
		if (name.isEmpty()) {
			name = "NoNameStorage";
		}
		return name;
	}

	public File getStorageDir(File workspace) {
		return new File(workspace, "Storages/" + getStorageName());
	}

	public final void configureXStream() {
		configureXStream(xstream);
	}

	/**
	 * 
	 * @param aStream
	 */
	public void configureXStream(XStream aStream) {
		//
	}

}
