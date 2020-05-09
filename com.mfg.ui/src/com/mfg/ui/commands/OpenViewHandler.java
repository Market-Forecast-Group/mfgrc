package com.mfg.ui.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.ui.internal.dialogs.ViewLabelProvider;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;

import com.mfg.utils.PartUtils;

@SuppressWarnings("restriction")
public class OpenViewHandler extends AbstractHandler {

	public static class ListViewContentProvider extends ViewContentProvider {
		private final IWorkbenchWindow _window;

		public ListViewContentProvider(IWorkbenchWindow window) {
			super();
			_window = window;
		}

		@Override
		public Object[] getChildren(Object element) {
			List<Object> list = new ArrayList<>();
			Object[] children = super.getChildren(element);
			for (Object obj : children) {
				if (obj instanceof IViewCategory) {
					list.addAll(Arrays.asList(getChildren(obj)));
				} else if (obj instanceof IViewDescriptor) {
					String viewId = ((IViewDescriptor) obj).getId();
					String perspectiveId = _window.getActivePage()
							.getPerspective().getId();

					// org.eclipse.ui.perspectiveExtensions.
					IConfigurationElement[] elements = Platform
							.getExtensionRegistry()
							.getConfigurationElementsFor(
									"org.eclipse.ui.perspectiveExtensions");
					for (IConfigurationElement e : elements) {
						for (IConfigurationElement e2 : e.getChildren()) {
							String name = e2.getName();
							String targetId = e.getAttribute("targetID");
							// TODO: what I have to do is a pattern matching
							if (targetId.equals(perspectiveId)
									|| targetId.equals("*")) {
								if (name.equals("view")) {
									String id = e2.getAttribute("id");
									if (viewId.equals(id)) {
										list.add(obj);
										break;
									}
								}
							}
						}
					}
				}
			}
			return list.toArray();
		}
	}

	private final boolean _openNew;

	public OpenViewHandler(boolean openNew) {
		_openNew = openNew;
	}

	public OpenViewHandler() {
		this(false);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		ListDialog d = new ListDialog(window.getShell());
		d.setContentProvider(new ListViewContentProvider(window));
		d.setLabelProvider(new ViewLabelProvider(window, window.getShell()
				.getDisplay().getSystemColor(SWT.COLOR_BLACK)));
		d.setInput(WorkbenchPlugin.getDefault().getViewRegistry());
		d.setTitle("Show View");
		d.setMessage("Select the view to show.");
		if (d.open() == Window.OK) {
			for (Object obj : d.getResult()) {
				try {
					PartUtils.openView(((IViewDescriptor) obj).getId(),
							_openNew);
				} catch (Exception e) {
					PartUtils.openView(((IViewDescriptor) obj).getId(), false);
				}
			}
		}
		return null;
	}
}
