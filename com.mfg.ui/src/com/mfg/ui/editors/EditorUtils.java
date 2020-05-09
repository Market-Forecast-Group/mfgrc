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
package com.mfg.ui.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public final class EditorUtils {
	public static void registerBindingListenersToUpdateWorkspace(
			Composite parent, final SimpleStorage<?> storage,
			DataBindingContext... contexts) {
		DataBindingUtils
				.disposeBindingContextAtControlDispose(parent, contexts);
		DataBindingUtils.addDataBindingModelsListener(new IChangeListener() {

			@Override
			public void handleChange(
					org.eclipse.core.databinding.observable.ChangeEvent event) {
				storage.fireStorageChanged();
			}
		}, contexts);
	}

	public static void registerBindingListenersToSetDirtyWorkspace(
			Composite parent, DataBindingContext... contexts) {
		DataBindingUtils
				.disposeBindingContextAtControlDispose(parent, contexts);
		DataBindingUtils.addDataBindingModelsListener(new IChangeListener() {

			@Override
			public void handleChange(
					org.eclipse.core.databinding.observable.ChangeEvent event) {
				PersistInterfacesPlugin.getDefault().setWorkspaceDirty(true);
			}
		}, contexts);
	}

	@SuppressWarnings("unchecked")
	public static <T extends IStorageObject> T getStorageObject(FormPage page) {
		return ((StorageObjectEditorInput<T>) page.getEditorInput())
				.getStorageObject();
	}
}
