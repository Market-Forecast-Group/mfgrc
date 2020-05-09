package com.mfg.logger.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
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

public class LogMenuContributionGotoTime extends ContributionItem {
	final AbstractLoggerViewControl control;

	public LogMenuContributionGotoTime(AbstractLoggerViewControl aControl) {
		super();
		this.control = aControl;
		Shell shell = new Shell(Display.getDefault());
		dropMenu = new Menu(shell, SWT.POP_UP);
		shell.setMenu(dropMenu);
	}

	final List<String> elements = new ArrayList<>();
	final HashMap<String, MenuItem> map = new HashMap<>();
	Menu dropMenu = null;

	public void addElement(final String e) {
		if (!elements.contains(e)) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					MenuItem itemRadio2 = new MenuItem(dropMenu, SWT.NONE);
					itemRadio2.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent aE) {
							control.gotoTime(Integer.parseInt(((MenuItem) aE
									.getSource()).getText()));
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent aE) {
							// DO NOTHING
						}
					});
					itemRadio2.setText(e);
					elements.add(e);
					map.put(e, itemRadio2);
					if (elements.size() > 5) {
						map.get(elements.get(0)).dispose();
						map.remove(elements.get(0));
						elements.remove(0);
					}
				}
			});
		}
	}

	@Override
	public void fill(ToolBar aParent, int aIndex) {
		final ToolItem tltmDd = new ToolItem(aParent, SWT.DROP_DOWN);
		tltmDd.setText("Goto");
		// getAction().addPropertyChangeListener(new IPropertyChangeListener() {
		// @Override
		// public void propertyChange(PropertyChangeEvent aEvent) {
		// if (aEvent.getProperty().equals(IAction.TEXT)) {
		// tltmDd.setToolTipText(getAction().getText());
		// }
		// }
		// });
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
					InputDialog d = new InputDialog(null, "Goto Time",
							"Goto Time", "0", null);
					d.open();
					String tvalue = d.getValue();
					control.gotoTime(Integer.parseInt(tvalue));
					addElement(tvalue);
				}

			}

		});
	}
}