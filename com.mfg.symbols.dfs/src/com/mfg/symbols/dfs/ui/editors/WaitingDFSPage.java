package com.mfg.symbols.dfs.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class WaitingDFSPage extends FormPage {
	private ProgressBar _progressBar;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public WaitingDFSPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public WaitingDFSPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		setPartName("DFS");
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		Composite composite = new Composite(managedForm.getForm().getBody(),
				SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
				true, 1, 1));
		managedForm.getToolkit().adapt(composite);
		managedForm.getToolkit().paintBordersFor(composite);

		Label lblLoadingDfs = new Label(composite, SWT.NONE);
		managedForm.getToolkit().adapt(lblLoadingDfs, true, true);
		lblLoadingDfs.setText("Waiting for DFS...");

		_progressBar = new ProgressBar(composite, SWT.INDETERMINATE);
		_progressBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().adapt(_progressBar, true, true);

	}
}
