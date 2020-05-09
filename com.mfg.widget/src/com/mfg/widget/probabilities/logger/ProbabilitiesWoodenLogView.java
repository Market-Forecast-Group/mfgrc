package com.mfg.widget.probabilities.logger;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.utils.ui.table.MfgModelTable;

public class ProbabilitiesWoodenLogView extends ViewPart {

	public static final String ID = "com.mfg.widget.probabilities.logger.ProbabilitiesWoodenLogView"; //$NON-NLS-1$
	private final FormToolkit ftoolkit = new FormToolkit(Display.getCurrent());
	@SuppressWarnings("unused")
	private MfgModelTable logTable;
	private class LogModel implements IMfgTableModel {

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String[] getColumnNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getContent(int aRow, int aColumn) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEnabled(int aRow, int aColumn) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getHighLight(int aRow, int aColumn) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	public ProbabilitiesWoodenLogView() {
	}
	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@SuppressWarnings("synthetic-access")
	@Override
	public void createPartControl(Composite parent) {
		Composite container = ftoolkit.createComposite(parent, SWT.NONE);
		ftoolkit.paintBordersFor(container);
		logTable = new MfgModelTable(container, new LogModel());
		createActions();
		initializeToolBar();
		initializeMenu();
	}
	@Override
	public void dispose() {
		ftoolkit.dispose();
		super.dispose();
	}
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}
	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		@SuppressWarnings("unused")
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}
	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}
	@Override
	public void setFocus() {
		// Set the focus
	}

}
