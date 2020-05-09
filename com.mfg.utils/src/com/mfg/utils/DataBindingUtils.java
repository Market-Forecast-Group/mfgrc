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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author arian
 * 
 */
public class DataBindingUtils {
	private DataBindingUtils() {
	}

	/**
	 * Used to remove from the bean-model the property listeners added by the
	 * jface databindings. Possibely this is a jface bug, because jface data
	 * bindings does not remove model listeners when the SWT widget is disposed.
	 * 
	 * @param contexts
	 * @param control
	 */
	public static void disposeBindingContextAtControlDispose(Control control,
			final DataBindingContext... contexts) {
		control.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeDataBindingContext(contexts);
			}
		});
	}

	/**
	 * @param ctx
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void disposeDataBindingContext(DataBindingContext... contexts) {
		for (DataBindingContext ctx : contexts) {
			List bindings = new ArrayList(ctx.getBindings());
			for (Object obj : bindings) {
				Binding b = (Binding) obj;
				IObservable model = b.getModel();
				if (model != null) {
					model.dispose();
				}
			}
			ctx.dispose();
		}
	}

	/**
	 * @param changeListener
	 * @param contexts
	 */
	public static void addDataBindingModelsListener(
			IChangeListener changeListener, DataBindingContext... contexts) {
		for (DataBindingContext ctx : contexts) {
			for (Object obj : ctx.getBindings()) {
				Binding binding = (Binding) obj;
				binding.getModel().addChangeListener(changeListener);
			}
		}
	}

	public static void decorateBindings(DataBindingContext... bindingContexts) {
		for (DataBindingContext bindingContext : bindingContexts) {
			IObservableList list = bindingContext.getBindings();
			for (int i = 0; i < list.size(); i++) {
				Binding b = (Binding) list.get(i);
				ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
			}
		}
	}

	public static abstract class QuickFix {
		private final String _msg;

		public QuickFix(String msg) {
			_msg = msg;
		}

		public abstract void fix();

		public String getMessage() {
			return _msg;
		}

		@Override
		public String toString() {
			return _msg;
		}
	}

	public static class QuickFixSupport {
		private static final QuickFix[] EMPTY_QUICK_FIXS = new QuickFix[0];
		private final Map<Binding, List<QuickFix>> _map;

		public QuickFixSupport() {
			_map = new HashMap<>();
		}

		public void addFix(Binding binding, QuickFix fix) {
			List<QuickFix> list = _map.get(binding);
			if (list == null) {
				_map.put(binding, list = new ArrayList<>());
			}
			list.add(fix);
		}

		public boolean hasFixes(Binding binding) {
			return _map.containsKey(binding);
		}

		public void clear(Binding binding) {
			_map.put(binding, null);
		}

		public QuickFix[] getFixes(Binding binding) {
			List<QuickFix> list = _map.get(binding);
			if (list != null && list.size() > 0) {
				return list.toArray(new QuickFix[list.size()]);
			}
			return EMPTY_QUICK_FIXS;
		}

		public void runFix(Binding binding, QuickFix fix) {
			List<QuickFix> list = _map.get(binding);
			fix.fix();
			list.remove(fix);
		}

	}

	public static void addQuickFixSupport(final QuickFixSupport support,
			DataBindingContext... bindingContexts) {
		final Map<Widget, List<Binding>> wbMap = new HashMap<>();
		Set<Composite> parents = new HashSet<>();

		for (DataBindingContext bindingContext : bindingContexts) {
			IObservableList list = bindingContext.getBindings();
			for (int i = 0; i < list.size(); i++) {
				Binding b = (Binding) list.get(i);
				IObservable target = b.getTarget();
				if (target instanceof ISWTObservable) {
					ISWTObservable observable = (ISWTObservable) target;
					Widget widget = observable.getWidget();
					final Control control = (Control) widget;
					Composite parent = control.getParent();
					parents.add(parent);
					List<Binding> blist = wbMap.get(widget);
					if (blist == null) {
						wbMap.put(widget, blist = new ArrayList<>());
					}
					blist.add(b);
				}
			}
		}

		for (final Composite parent : parents) {
			parent.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					for (Control c : parent.getChildren()) {
						int dx = c.getBounds().x - e.x;
						int dy = e.y - c.getBounds().y;
						int space = 8;
						if (dx >= 0 && dx < space && dy >= 0 && dy < space) {
							showQuickFixDialog(support, wbMap, c);
							break;
						}
					}
				}

			});
		}
		for (Widget w : wbMap.keySet()) {
			final Control c = (Control) w;
			c.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if ((e.stateMask & SWT.CTRL) != 0 && e.character == '1') {
						showQuickFixDialog(support, wbMap, c);
					}
				}
			});
		}
	}

	static void showQuickFixDialog(final QuickFixSupport support,
			final Map<Widget, List<Binding>> wbMap, Control c) {
		List<Binding> blist = wbMap.get(c);
		if (blist != null) {
			for (Binding b : blist) {
				MultiStatus status = (MultiStatus) b.getValidationStatus()
						.getValue();
				if (!status.isOK()) {
					QuickFix[] fixes = support.getFixes(b);
					if (fixes.length > 0) {
						final FieldDecoration fieldDecoration = FieldDecorationRegistry
								.getDefault().getFieldDecoration(
										FieldDecorationRegistry.DEC_ERROR);
						ListDialog dlg = new ListDialog(c.getShell()) {
							@Override
							protected void configureShell(Shell shell) {
								super.configureShell(shell);
								shell.setImage(fieldDecoration.getImage());
							}
						};
						dlg.setContentProvider(new ArrayContentProvider());
						dlg.setLabelProvider(new LabelProvider());
						dlg.setTitle("Quick Fix");
						dlg.setMessage("Select one of the quick fixes below:");
						dlg.setInput(fixes);
						dlg.setInitialSelections(new Object[] { fixes[0] });

						if (dlg.open() == Window.OK) {
							QuickFix fix = (QuickFix) dlg.getResult()[0];
							support.runFix(b, fix);
						}
					} else {
						MessageDialog.openInformation(c.getShell(),
								"Quick Fix",
								"There are not any quick fix for this error.");
					}
				}
			}
		}
	}
}
