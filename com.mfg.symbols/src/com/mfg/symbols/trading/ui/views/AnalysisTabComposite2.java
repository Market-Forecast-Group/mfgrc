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
package com.mfg.symbols.trading.ui.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.strategy.PortfolioStrategy;
import com.mfg.tea.conn.IDuplexStatistics;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class AnalysisTabComposite2 extends Composite {

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final CTabFolder statsTabFolder;
	private final MainTabStatisticsComposite2 allComposite;
	private final MainTabStatisticsComposite2 longComposite;
	private final MainTabStatisticsComposite2 shortComposite;
	private DataBindingContext[] bindings;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public AnalysisTabComposite2(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		statsTabFolder = new CTabFolder(this, SWT.BORDER);
		statsTabFolder.setTabPosition(SWT.BOTTOM);
		statsTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		toolkit.adapt(statsTabFolder);
		toolkit.paintBordersFor(statsTabFolder);
		statsTabFolder.setSelectionBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmAll = new CTabItem(statsTabFolder, SWT.NONE);
		tbtmAll.setText("  All  ");

		Composite composite = toolkit.createComposite(statsTabFolder, SWT.NONE);
		tbtmAll.setControl(composite);
		toolkit.paintBordersFor(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		allComposite = new MainTabStatisticsComposite2(composite, SWT.NONE);
		toolkit.adapt(allComposite);
		toolkit.paintBordersFor(allComposite);

		CTabItem tbtmLong = new CTabItem(statsTabFolder, SWT.NONE);
		tbtmLong.setText("Long");

		Composite composite_1 = toolkit.createComposite(statsTabFolder,
				SWT.NONE);
		tbtmLong.setControl(composite_1);
		toolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		longComposite = new MainTabStatisticsComposite2(composite_1, SWT.NONE);
		toolkit.adapt(longComposite);
		toolkit.paintBordersFor(longComposite);

		CTabItem tbtmShort = new CTabItem(statsTabFolder, SWT.NONE);
		tbtmShort.setText("Short");

		Composite composite_2 = toolkit.createComposite(statsTabFolder,
				SWT.NONE);
		tbtmShort.setControl(composite_2);
		toolkit.paintBordersFor(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));

		shortComposite = new MainTabStatisticsComposite2(composite_2, SWT.NONE);
		toolkit.adapt(shortComposite);
		toolkit.paintBordersFor(shortComposite);

		afterCreateWidgets();
	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {
		statsTabFolder.setSelection(statsTabFolder.getItem(0));
		bindings = new DataBindingContext[] { allComposite.getBindingContext(),
				longComposite.getBindingContext(),
				shortComposite.getBindingContext() };
		DataBindingUtils.disposeBindingContextAtControlDispose(this, bindings);
	}

	public void setPortfolio(PortfolioStrategy portfolio) {
		if (portfolio == null) {
			allComposite.setAccount(null);
			longComposite.setAccount(null);
			shortComposite.setAccount(null);
		} else {
			IDuplexStatistics periodAccount = portfolio.getAccount();
			allComposite.setAccount(periodAccount);
			if (periodAccount != null) {
				longComposite.setAccount(periodAccount.getLongStatistics());
				shortComposite.setAccount(periodAccount.getShortStatistics());
			} else {
				longComposite.setAccount(null);
				shortComposite.setAccount(null);
			}

		}
	}
}
