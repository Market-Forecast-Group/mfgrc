package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import com.mfg.symbols.trading.ui.views.IDashboardWidgetProvider;

public class StrategyFigure extends Panel {
	private Panel _contLong;
	private Panel _contShort;

	public StrategyFigure() {
		super();
		updateContent(null);
	}

	public void updateContent(IDashboardWidgetProvider provider) {
		removeAll();
		setSize(-1, -1);

		setBackgroundColor(ColorConstants.black);
		setLayoutManager(new FlowLayout(false));
		setBorder(new CompoundBorder(new LineBorder(ColorConstants.black, 3),
				new MarginBorder(5)));

		addAccountFig("LONG ACCOUNT", provider, true);
		addAccountFig("SHORT ACCOUNT", provider, false);

		invalidateTree();
	}

	public Panel getContLong() {
		return _contLong;
	}

	public Panel getContShort() {
		return _contShort;
	}

	private void addAccountFig(String title, IDashboardWidgetProvider provider,
			boolean isLong) {
		Panel panel = new Panel();
		BorderLayout layout1 = new BorderLayout();
		layout1.setHorizontalSpacing(5);
		panel.setLayoutManager(layout1);
		Label label = new Label(title);
		label.setFont(new Font(null, "Arial", 16, SWT.BOLD));
		label.setForegroundColor(ColorConstants.white);
		label.setBorder(new MarginBorder(10));

		Panel cont = new Panel();
		cont.setLayoutManager(new BorderLayout());
		cont.setBackgroundColor(ColorConstants.black);
		IFigure patternFig = null;
		if (provider != null) {
			patternFig = provider.createFigure();
			cont.add(patternFig, BorderLayout.CENTER);
			// cont.setBorder(new LineBorder(ColorConstants.green, 3));
		}

		panel.add(label, BorderLayout.TOP);
		panel.add(cont, BorderLayout.CENTER);
		add(panel);

		if (isLong) {
			_contLong = cont;
		} else {
			_contShort = cont;
		}
	}

	public IFigure getLongFig() {
		return _contLong;
	}

	public IFigure getShortFig() {
		return _contShort;
	}
}
