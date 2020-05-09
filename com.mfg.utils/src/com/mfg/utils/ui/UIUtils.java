package com.mfg.utils.ui;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.part.ViewPart;

public class UIUtils {

	public static Point getTablePoint(MouseEvent e) {
		Table _table = (Table) e.widget;
		TableItem item = _table.getItem(new Point(e.x, e.y));
		int i = 0;
		int x = 0;
		for (TableColumn col : _table.getColumns()) {
			if (e.x >= x && e.x < x + col.getWidth()) {
				break;
			}
			x += col.getWidth();
			i++;
		}

		return new Point(i, _table.indexOf(item));
	}

	public static void initComboViewer(
			final Map<Object, Object> modelToTargetMap, ComboViewer viewer) {
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Object text = modelToTargetMap.get(element);
				return super.getText(text);
			}
		});
		viewer.setInput(modelToTargetMap.keySet());
	}

	public static void runWithDefaultRealm(Display display, Runnable runnable) {
		Realm.runWithDefault(SWTObservables.getRealm(display), runnable);
	}

	public static void updateFormTextWithProperties(FormText text,
			Map<String, Object> props) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form>");
		for (Entry<String, Object> e : props.entrySet()) {
			String key = e.getKey() == null ? "" : e.getKey();
			String value = e.getValue() == null ? "" : e.getValue().toString();

			sb.append("<p><b>" + key + "</b>: " + value + "</p>");
		}
		sb.append("</form>");

		String str = sb.toString().replace("&", "&amp;");
		text.setText(str, true, false);
	}

	public static ActionContributionItem addItem(Menu menu, final Action action) {
		ActionContributionItem i = new ActionContributionItem(action);
		i.fill(menu, -1);
		return i;
	}

	public static void updateLayout(ManagedForm managedForm) {
		managedForm.getForm().reflow(true);
	}

	public static void updateLayout(Composite composite) {
		composite.layout(true, true);
	}

	public static boolean isDetachedView(ViewPart viewPart) {
		return viewPart.getSite().getShell().getText().length() == 0;
	}

	public static void updateDetachedViewBounds(ViewPart viewPart,
			Control control) {
		if (isDetachedView(viewPart)) {
			Shell shell = viewPart.getSite().getShell();
			Rectangle rect = shell.getBounds();
			Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			shell.setBounds(rect.x, rect.y, size.x + 50, size.y + 50);
		}
	}

	public static void enableAll(Control control, boolean enable) {
		if (control != null) {
			if (control instanceof Composite) {
				Composite comp = (Composite) control;
				for (Control child : comp.getChildren()) {
					enableAll(child, enable);
				}
			}
			control.setEnabled(enable);
		}
	}

	public static Composite createButtonComposite(Composite parent,
			Action... actions) {
		Composite comp = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		comp.setLayout(layout);
		for (final Action action : actions) {
			final Button b = new Button(comp, SWT.NONE);

			b.setText(action.getText());
			b.setEnabled(action.isEnabled());
			if (action.getImageDescriptor() != null) {
				b.setImage(action.getImageDescriptor().createImage());
				b.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent e) {
						b.getImage().dispose();
					}
				});
			}
			action.addPropertyChangeListener(new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					b.setText(action.getText());
					b.setEnabled(action.isEnabled());
				}
			});
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					action.run();
				}
			});
		}
		return comp;
	}

	public static void connectLinkWithAction(final ImageHyperlink link,
			final Action action) {
		link.setText(action.getText());
		link.layout();
		link.setToolTipText(action.getToolTipText());
		link.setEnabled(action.isEnabled());

		if (action.getImageDescriptor() != null) {
			link.setImage(action.getImageDescriptor().createImage());
		}

		final HyperlinkAdapter linkListener = new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				action.run();
			}
		};
		link.addHyperlinkListener(linkListener);

		final IPropertyChangeListener actionListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				link.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!link.isDisposed()) {
							link.setText(action.getText());
							link.setToolTipText(action.getToolTipText());
							link.layout();
							link.setEnabled(action.isEnabled());
						}
					}
				});
			}
		};
		action.addPropertyChangeListener(actionListener);

		link.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				action.removePropertyChangeListener(actionListener);
				link.removeHyperlinkListener(linkListener);
				if (link.getImage() != null) {
					link.getImage().dispose();
				}
			}
		});
	}

	public static void connectToogleButtonWithAction(final Button button,
			final Action action) {
		// button.setText(action.getText());
		button.setToolTipText(action.getToolTipText());
		button.setSelection(action.isEnabled());

		final SelectionListener buttonListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				action.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}
		};
		button.addSelectionListener(buttonListener);

		final IPropertyChangeListener actionListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				button.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!button.isDisposed()) {
							// button.setText(action.getText());
							button.setToolTipText(action.getToolTipText());
							button.setSelection(action.isEnabled());
						}
					}
				});
			}
		};
		action.addPropertyChangeListener(actionListener);

		button.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				action.removePropertyChangeListener(actionListener);
				button.removeSelectionListener(buttonListener);
			}
		});
	}

	/**
	 * @param linkStartDataRequest
	 * @return
	 */
	public static Composite getRootParent(Composite composite) {
		Composite parent = composite.getParent();
		return parent == null ? composite : getRootParent(parent);
	}

}
