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
package com.mfg.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.INamedHandleStateIds;
import org.eclipse.core.commands.State;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author arian
 * 
 */
public class PartUtils {
	private PartUtils() {
	}

	public static void updateCommandNameInView(ExecutionEvent event, String name) {
		updateCommandStateInView(event, INamedHandleStateIds.NAME, name);
	}

	public static void updateCommandStateInView(ExecutionEvent event,
			String stateId, Object value) {
		IViewSite site = (IViewSite) HandlerUtil.getActiveSite(event);

		IMenuManager menu = site.getActionBars().getMenuManager();
		CommandContributionItem cmdItem = findCommand(menu, event.getCommand()
				.getId());
		State s = new State();
		s.setValue(value);
		cmdItem.getCommand().getCommand().addState(stateId, s);
		cmdItem.update();
	}

	private static CommandContributionItem findCommand(IMenuManager menu,
			String id) {
		IContributionItem item = menu.find(id);
		if (item == null) {
			for (IContributionItem i : menu.getItems()) {
				if (i instanceof IMenuManager) {
					CommandContributionItem cmdItem = findCommand(
							(IMenuManager) i, id);
					if (cmdItem != null) {
						return cmdItem;
					}
				}
			}
		} else {
			return ((CommandContributionItem) item);
		}
		return null;
	}

	public static void expandTreeFromSavedState(final IMemento memento,
			final String mementoKey, final TreeViewer viewer) {
		if (memento != null) {
			String state = memento.getString(mementoKey);
			if (state != null) {
				restoreTreeExpansionState(viewer, state);
			}
		}
	}

	public static void restoreTreeExpansionState(final TreeViewer viewer,
			String expandedItemsString) {
		String[] data = expandedItemsString.split(";");

		for (String strPath : data) {
			List<Integer> path = new ArrayList<>();
			String[] items = strPath.split(",");
			for (String item : items) {
				path.add(new Integer(Integer.parseInt(item)));
			}

			expandItemsFromPath(path, null, viewer);
		}
	}

	private static void expandItemsFromPath(List<Integer> path, TreeItem aRoot,
			TreeViewer viewer) {
		TreeItem root = aRoot;
		for (int i : path) {
			try {
				TreeItem item;
				if (root == null) {
					Tree tree = viewer.getTree();
					item = tree.getItem(i);
				} else {
					item = root.getItem(i);
				}
				viewer.expandToLevel(item.getData(), 1);
				root = item;
			} catch (Exception e) {
				// Adding a comment to avoid empty block warning.
			}
		}
	}

	public static String saveTreeExpansionState(IMemento aMemento,
			String mementoKey, TreeViewer viewer) {
		String state = getTreeExpansionState(viewer);
		aMemento.putString(mementoKey, state);
		return state;
	}

	public static String getTreeExpansionState(TreeViewer viewer) {
		StringBuilder sb = new StringBuilder();
		buildExpandedItemsString(sb, "", null, viewer.getTree());
		String paths = sb.toString();
		return paths;
	}

	public static void buildExpandedItemsString(StringBuilder sb, String path,
			TreeItem root, Tree tree) {
		TreeItem[] children = root == null ? tree.getItems() : root.getItems();
		int i = 0;
		boolean hasChildren = false;
		for (TreeItem item : children) {
			if (item.getExpanded()) {
				hasChildren = true;
				buildExpandedItemsString(sb, path + i + ",", item, tree);
			}
			i++;
		}
		if (!hasChildren) {
			sb.append(path + ";");
		}
	}

	public static <T extends IViewPart> List<T> getOpenViews(String viewId) {
		ArrayList<T> list = new ArrayList<>();

		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IWorkbenchPage[] pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				List<T> pageList = getOpenViews(viewId, page);
				list.addAll(pageList);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getOpenViews(String viewId, IWorkbenchPage page) {
		ArrayList<T> list = new ArrayList<>();
		IViewReference[] refs = page.getViewReferences();
		for (IViewReference ref : refs) {
			if (ref.getId().equals(viewId)) {
				IViewPart view = ref.getView(false);
				if (view != null) {
					list.add((T) view);
				}
			}
		}
		return list;
	}

	public static <T extends IViewPart> T openView(String viewId) {
		return openView(viewId, false);
	}

	@SuppressWarnings("unchecked")
	public static <T extends IViewPart> T openView(String viewId,
			boolean createNew) {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				try {
					IViewPart view;
					if (createNew) {
						view = page.showView(viewId, System.currentTimeMillis()
								+ "", IWorkbenchPage.VIEW_CREATE);
						page.activate(view);
					} else {
						view = page.showView(viewId);
					}
					return (T) view;
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	/**
	 * @param viewToOpen
	 */
	public static void activatePart(IWorkbenchPart part) {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				page.activate(part);
			}
		}
	}

	public static boolean isActivePart(IWorkbenchPart part) {
		return part.getSite().getWorkbenchWindow().getActivePage()
				.getActivePart() == part;
	}

	/**
	 * @return
	 */
	public static IWorkbenchPart getActivePart() {
		IWorkbenchPart part = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		return part;
	}

	public static boolean isVisiblePart(IWorkbenchPart part) {
		return part.getSite().getWorkbenchWindow().getActivePage()
				.isPartVisible(part);
	}
}
