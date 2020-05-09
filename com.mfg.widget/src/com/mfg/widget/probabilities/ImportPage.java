package com.mfg.widget.probabilities;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ImportPage extends WizardPage {
	private Text ftext;
	private String path;

	/**
	 * Create the wizard.
	 */
	public ImportPage() {
		super("Import Probability Distribution");
		setTitle("Import Probability Distribution");
		setDescription("Imports a Probability Distribution file into the Probabilities Statistics Perspective");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		Label lblFilePath = new Label(container, SWT.NONE);
		lblFilePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblFilePath.setText("File Path: ");

		ftext = new Text(container, SWT.BORDER);
		ftext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse();
			}
		});
		btnNewButton.setToolTipText("Browse");
		btnNewButton.setText("...");
	}

	protected void browse() {
		Shell shell = Display.getDefault().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[]{"*.prob"});
		dialog.setFilterNames(new String[]{"Probability Distribution"});
		dialog.setText("Import Probability Distribution");
		path = dialog.open();
		if (path == null)
			return;
		ftext.setText(path);
	}

	public String getFileNameText() {
		return path;
	}
}
