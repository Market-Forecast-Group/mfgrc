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
package com.mfg.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public abstract class CommonNavigatorContentProvider implements
		ITreeContentProvider, IDoubleClickListener {
	TreeViewer _viewer;
	private final List<IWorkspaceStorage> storages;
	private final WorkspaceStorageAdapter storageListener;

	/**
 * 
 */
	public CommonNavigatorContentProvider() {
		storageListener = new WorkspaceStorageAdapter() {
			@Override
			public void objectAdded(IWorkspaceStorage sotarage, final Object obj) {
				refreshViewer(obj);
			}

			@Override
			public void listAdded(IWorkspaceStorage storage,
					List<? extends Object> list) {
				if (!list.isEmpty()) {
					_viewer.refresh();
					StructuredSelection sel = (StructuredSelection) _viewer
							.getSelection();
					if (!sel.isEmpty()) {
						(_viewer).expandToLevel(sel.getFirstElement(), 1);
						if (list.size() == 1) {
							Object obj = list.get(0);
							_viewer.setSelection(new StructuredSelection(obj));
						}
					}
				}
			}

			@Override
			public void objectRemoved(IWorkspaceStorage storage, Object obj) {
				// viewer.refresh();
			}

			@Override
			public void objectModified(IWorkspaceStorage storage,
					final Object obj) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						_viewer.refresh(obj);
					}
				});
			}

			@Override
			public void storageChanged(IWorkspaceStorage storage) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!_viewer.getControl().isDisposed()) {
							String state = PartUtils
									.getTreeExpansionState(_viewer);
							_viewer.refresh();
							PartUtils.restoreTreeExpansionState(_viewer, state);
						}
					}
				});
			}
		};

		storages = new ArrayList<>();
		registerStorages(storages);

		for (IWorkspaceStorage storage : getStorages()) {
			addStorageListeners(storage);
		}
	}

	/**
	 * Add the storage listeners.
	 * 
	 * @param storage
	 */
	protected void addStorageListeners(IWorkspaceStorage storage) {
		storage.addStorageListener(storageListener);
	}

	/**
	 * @return the sorages
	 */
	public List<IWorkspaceStorage> getStorages() {
		return storages;
	}

	/**
	 * Implementers should add the storages used to provider content. In this
	 * way the content provider will add storage's listeners to refresh the
	 * viewer when the storages changed.
	 * 
	 * @param storages1
	 */
	protected abstract void registerStorages(List<IWorkspaceStorage> storages1);

	/**
	 * @return the viewer
	 */
	public TreeViewer getViewer() {
		return _viewer;
	}

	@Override
	public void dispose() {
		this._viewer.removeDoubleClickListener(this);
		for (IWorkspaceStorage storage : storages) {
			storage.removeStorageListener(storageListener);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this._viewer != null) {
			this._viewer.removeDoubleClickListener(this);
		}
		this._viewer = (TreeViewer) viewer;
		this._viewer.addDoubleClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.ui.views.SymbolContentProvider#doubleClick(org.eclipse
	 * .jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		StructuredSelection sel = (StructuredSelection) event.getSelection();
		Object elem = sel.getFirstElement();
		try {
			UIPlugin.openEditor(elem);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	protected void refreshViewer(final Object obj) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!_viewer.getControl().isDisposed()) {
					_viewer.refresh();
					if (obj != null) {
						StructuredSelection sel = (StructuredSelection) _viewer
								.getSelection();
						if (!sel.isEmpty()) {
							Object parent = getParent(obj);
							if (parent == null) {
								parent = sel.getFirstElement();
							}
							(_viewer).expandToLevel(parent, 1);
							_viewer.setSelection(new StructuredSelection(obj));
						}
					}
				}
			}
		});
	}
}
