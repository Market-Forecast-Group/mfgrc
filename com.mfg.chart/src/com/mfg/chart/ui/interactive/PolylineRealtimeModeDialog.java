package com.mfg.chart.ui.interactive;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PolylineRealtimeModeDialog extends Dialog {
	public static int LAST_PRICE = 1;
	public static int RT_PIVOT = 2;
	protected int _result;
	protected Shell _shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PolylineRealtimeModeDialog(Shell parent) {
		super(parent, 0);
		setText("Real-Time Polyline");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public int open() {
		createContents();
		_shell.open();
		_shell.pack();
		Display display = getParent().getDisplay();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return _result;
	}

	/**
	 * Create contents of the dialog.
	 */
	@SuppressWarnings("unused")
	private void createContents() {
		_shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		_shell.setSize(369, 130);
		_shell.setText(getText());
		_shell.setLayout(new GridLayout(3, true));

		Label lblSelectTheRealtime = new Label(_shell, SWT.NONE);
		GridData gd_lblSelectTheRealtime = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1);
		gd_lblSelectTheRealtime.verticalIndent = 10;
		lblSelectTheRealtime.setLayoutData(gd_lblSelectTheRealtime);
		lblSelectTheRealtime.setText("Select the Real-Time Mode");
		new Label(_shell, SWT.NONE);
		new Label(_shell, SWT.NONE);
		new Label(_shell, SWT.NONE);

		

		Button btnRealtimePivot = new Button(_shell, SWT.NONE);
		btnRealtimePivot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_result = RT_PIVOT;
				_shell.close();
			}
		});
		btnRealtimePivot.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnRealtimePivot.setText("Real-Time Pivot");

		
		Button btnLastPrice = new Button(_shell, SWT.NONE);
		btnLastPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnLastPrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_result = LAST_PRICE;
				_shell.close();
			}
		});
		btnLastPrice.setText("Last Price");
		
		Button btnCancel = new Button(_shell, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_result = 0;
				_shell.close();
			}
		});
		btnCancel.setText("Cancel");
	}

}
