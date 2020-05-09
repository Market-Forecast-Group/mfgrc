package com.mfg.logger.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.mfg.logger.ui.views.AbstractLoggerViewControl.IItemListener;

public class ActionContributionDDL extends ActionContributionItem {
	public ActionContributionDDL(NavigatorAction aAction) {
		super(aAction);
		Shell shell = new Shell(Display.getDefault());
		dropMenu = new Menu(shell, SWT.POP_UP);
		shell.setMenu(dropMenu);
		for (String e : aAction.getControl().getEventTypes()) {
			addElement(e);
		}
		select(aAction.getControl().getEvent());
		aAction.getControl().addNewEventTypeListener(new IItemListener() {
			@Override
			public void handleItem(Object aItem) {
				addElement(aItem.toString());
			}
		});
		aAction.getControl().addEventTypeSelectedListener(new IItemListener() {
			@Override
			public void handleItem(Object aItem) {
				select(aItem.toString());
			}
		});
	}

	@Override
	public NavigatorAction getAction() {
		return (NavigatorAction) super.getAction();
	}

	private final List<String> elements = new ArrayList<>();
	final HashMap<String, MenuItem> map = new HashMap<>();
	Menu dropMenu = null;
	String selected;

	public void addElement(final String e) {
		if (!elements.contains(e)) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MenuItem itemRadio2 = new MenuItem(dropMenu, SWT.RADIO);
					itemRadio2.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent aE) {
							System.out.println("slected");
							getAction().setEventType(
									((MenuItem) aE.getSource()).getText());
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent aE) {
							// DO NOTHING
						}
					});
					itemRadio2.setText(e);
					map.put(e, itemRadio2);
					if (e.equals(selected)) {
						itemRadio2.setSelection(true);
					}
				}
			});
		}
	}

	@Override
	public void fill(ToolBar aParent, int aIndex) {
		final ToolItem tltmDd = new ToolItem(aParent, SWT.DROP_DOWN);
		tltmDd.setImage(getAction().getImageDescriptor().createImage());
		tltmDd.setToolTipText(getAction().getText());
		getAction().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent aEvent) {
				if (aEvent.getProperty().equals(IAction.TEXT)) {
					tltmDd.setToolTipText(getAction().getText());
				}
			}
		});
		tltmDd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					final ToolItem toolItem = (ToolItem) e.widget;
					final ToolBar toolBar = toolItem.getParent();
					Point point = toolBar.toDisplay(new Point(e.x, e.y));
					dropMenu.setLocation(point.x, point.y);
					dropMenu.setVisible(true);
				} else {
					getAction().run();
				}

			}

		});
	}

	protected void select(String text) {
		selected = text;
		for (MenuItem v : map.values()) {
			v.setSelection(false);
		}
		if (map.get(text) != null) {
			map.get(text).setSelection(true);
			getAction().updateText(text);
		}
	}
}