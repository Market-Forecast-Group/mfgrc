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
package com.mfg.symbols.ui;

import static java.lang.System.out;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.symbols.inputs.ui.editors.InputTabComposite;

/**
 * @author arian
 * 
 */
public class RowLayoutTest {

	protected Shell shell;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Text txtNewText;
	private Text txtNewText_1;
	private Text txtNewText_3;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					new Runnable() {

						@Override
						public void run() {
							RowLayoutTest window = new RowLayoutTest();
							window.open();
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	@SuppressWarnings("unused")
	// Some components created to test
	protected void createContents() {
		shell = new Shell();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				// layoutTree(shell);
				// out.println("------------");
			}
		});
		shell.setSize(672, 645);
		shell.setText("SWT Application");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginWidth = 0;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		ScrolledComposite scrolledComposite = new ScrolledComposite(shell,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		formToolkit.adapt(scrolledComposite);
		formToolkit.paintBordersFor(scrolledComposite);

		Composite composite_3 = formToolkit.createComposite(scrolledComposite,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_3);
		composite_3.setLayout(new GridLayout(1, false));

		Section sctnNewSection_1 = formToolkit.createSection(composite_3,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewSection_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		formToolkit.paintBordersFor(sctnNewSection_1);
		sctnNewSection_1.setText("New Section");
		sctnNewSection_1.setExpanded(true);

		Composite composite_4 = formToolkit.createComposite(sctnNewSection_1,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_4);
		sctnNewSection_1.setClient(composite_4);
		composite_4.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnNewButton_17 = formToolkit.createButton(composite_4,
				"New Button", SWT.NONE);

		Button btnNewButton_18 = formToolkit.createButton(composite_4,
				"New Button", SWT.NONE);

		Button btnNewButton_19 = formToolkit.createButton(composite_4,
				"New Button", SWT.NONE);

		Button btnNewButton_20 = formToolkit.createButton(composite_4,
				"New Button", SWT.NONE);

		Composite composite_1 = formToolkit.createComposite(composite_3,
				SWT.NONE);
		composite_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_DARK_YELLOW));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		RowLayout rl_composite_1 = new RowLayout(SWT.HORIZONTAL);
		composite_1.setLayout(rl_composite_1);
		formToolkit.paintBordersFor(composite_1);

		Button btnNewButton_4 = formToolkit.createButton(composite_1,
				"New Button", SWT.NONE);

		Button btnNewButton_5 = formToolkit.createButton(composite_1,
				"New Button", SWT.NONE);

		Button btnNewButton_6 = formToolkit.createButton(composite_1,
				"New Button", SWT.NONE);

		Button btnNewButton_7 = formToolkit.createButton(composite_1,
				"New Button", SWT.NONE);

		Composite composite = formToolkit
				.createComposite(composite_3, SWT.NONE);
		composite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_DARK_CYAN));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button btnNewButton = formToolkit.createButton(composite, "New Button",
				SWT.NONE);

		Button btnNewButton_1 = formToolkit.createButton(composite,
				"New Button", SWT.NONE);

		Button btnNewButton_2 = formToolkit.createButton(composite,
				"New Button", SWT.NONE);

		Button btnNewButton_3 = formToolkit.createButton(composite,
				"New Button", SWT.NONE);

		Button btnNewButton_8 = formToolkit.createButton(composite,
				"New Button", SWT.NONE);

		Section sctnNewSection = formToolkit.createSection(composite_3,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		formToolkit.paintBordersFor(sctnNewSection);
		sctnNewSection.setText("New Section");
		sctnNewSection.setExpanded(true);

		Composite composite_2 = formToolkit.createComposite(sctnNewSection,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_2);
		sctnNewSection.setClient(composite_2);
		composite_2.setLayout(new GridLayout(5, false));

		Button btnNewButton_9 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_13 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_10 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_12 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_11 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_14 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_15 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_16 = formToolkit.createButton(composite_2,
				"New Button", SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);

		CTabFolder tabFolder = new CTabFolder(composite_3, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmItem.setText("Item 1");

		Composite composite_5 = formToolkit
				.createComposite(tabFolder, SWT.NONE);
		tbtmItem.setControl(composite_5);
		formToolkit.paintBordersFor(composite_5);
		composite_5.setLayout(new GridLayout(5, false));

		Button btnNewButton_21 = formToolkit.createButton(composite_5,
				"New Button", SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);

		Button btnNewButton_22 = formToolkit.createButton(composite_5,
				"New Button", SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);

		Button btnNewButton_23 = formToolkit.createButton(composite_5,
				"New Button", SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);

		Button btnNewButton_24 = formToolkit.createButton(composite_5,
				"New Button", SWT.NONE);

		txtNewText = formToolkit.createText(composite_5, "New Text", SWT.NONE);
		txtNewText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		CTabItem tbtmItem_1 = new CTabItem(tabFolder, SWT.NONE);
		tbtmItem_1.setText("Item 2");

		Composite composite_6 = formToolkit
				.createComposite(tabFolder, SWT.NONE);
		tbtmItem_1.setControl(composite_6);
		formToolkit.paintBordersFor(composite_6);
		composite_6.setLayout(new GridLayout(2, false));

		Label lblNewLabel = formToolkit.createLabel(composite_6, "New Label",
				SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		txtNewText_1 = formToolkit
				.createText(composite_6, "New Text", SWT.NONE);
		txtNewText_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Section sctnNewSection_2 = formToolkit.createSection(composite_6,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnNewSection_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		formToolkit.paintBordersFor(sctnNewSection_2);
		sctnNewSection_2.setText("New Section");

		Composite composite_8 = formToolkit.createComposite(composite_6,
				SWT.NONE);
		composite_8.setLayout(new GridLayout(2, false));
		composite_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 1));
		formToolkit.paintBordersFor(composite_8);

		Button btnNewButton_25 = formToolkit.createButton(composite_8,
				"New Button", SWT.NONE);
		new Label(composite_8, SWT.NONE);
		new Label(composite_8, SWT.NONE);

		Button btnNewButton_26 = formToolkit.createButton(composite_8,
				"New Button", SWT.NONE);
		new Label(composite_8, SWT.NONE);

		Composite composite_9 = formToolkit.createComposite(composite_8,
				SWT.NONE);
		composite_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		formToolkit.paintBordersFor(composite_9);
		composite_9.setLayout(new GridLayout(2, false));

		Label lblNewLabel_2 = formToolkit.createLabel(composite_9, "New Label",
				SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		txtNewText_3 = formToolkit
				.createText(composite_9, "New Text", SWT.NONE);
		txtNewText_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		CTabItem tbtmItem_2 = new CTabItem(tabFolder, SWT.NONE);
		tbtmItem_2.setText("Item 3");

		InputTabComposite inputTabComposite = new InputTabComposite(tabFolder,
				SWT.NONE);
		tbtmItem_2.setControl(inputTabComposite);
		formToolkit.paintBordersFor(inputTabComposite);
		scrolledComposite.setContent(composite_3);
		scrolledComposite.setMinSize(new Point(10, 10));

	}

	/**
	 * @param shell2
	 */
	protected void layoutTree(Control control) {
		out.println("layout " + control);
		if (control instanceof Composite) {
			Composite comp = (Composite) control;
			for (Control c : comp.getChildren()) {
				layoutTree(c);
			}
			comp.layout();
		}
		if (control instanceof Section) {
			layoutTree(((Section) control).getClient());
		}
	}
}
