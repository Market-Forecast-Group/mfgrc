package com.mfg.ui.editors;

import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import com.mfg.persist.interfaces.DoesNotExistObject;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;

public class StorageObjectEditorInputFactory implements IElementFactory {

	public static final String ID = "com.mfg.ui.editors.storageObjectEditorInputFactory";

	@Override
	public IAdaptable createElement(IMemento memento) {
		String uuid = memento
				.getString(StorageObjectEditorInput.OBJECT_UUID_KEY);
		String storageId = memento
				.getString(StorageObjectEditorInput.OBJECT_STORAGE_ID);
		Integer tab = memento
				.getInteger(StorageObjectEditorInput.KEY_LAST_EDITOR_TAB);
		if (tab == null) {
			tab = Integer.valueOf(0);
		}

		if (uuid != null && storageId != null) {
			IWorkspaceStorage storage = PersistInterfacesPlugin.getDefault()
					.getStorage(storageId);
			if (storage instanceof SimpleStorage) {
				SimpleStorage<?> simpleStorage = (SimpleStorage<?>) storage;
				IStorageObject obj = simpleStorage.findById(UUID
						.fromString(uuid));
				if (obj == null) {
					obj = new DoesNotExistObject(simpleStorage);
				}
				StorageObjectEditorInput<IStorageObject> editorInput = new StorageObjectEditorInput<>(
						obj);
				editorInput.setLastTab(tab.intValue());

				return editorInput;
			}
		}
		return null;
	}

}
