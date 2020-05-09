package com.marketforecastgroup.dfsa.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.common.DFSException;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;

public class SymbolEditor extends FormEditor {

	public static final String EDITOR_ID = "com.marketforecastgroup.dfsa.ui.editors.symbol";

	@Override
	protected void createPages() {
		DFSPlugin plugin = DFSPlugin.getDefault();
		try {
			plugin.runWithDFS(new IDFSRunnable() {

				@Override
				public void run(final IDFS dfs) {
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							addPages(dfs);
						}
					});
				}

				@Override
				public void notReady() {
					addBusyPage();
				}
			});
		} catch (DFSException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void addPages() {
		// nothing
	}

	protected void addPages(IDFS dfs) {
		try {
			SymbolEditorInput input = getEditorInput();
			SymbolEditorPage page = new SymbolEditorPage(this, input.getName(),
					input.getName());
			page.setDFS(dfs);
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void addBusyPage() {
		try {
			addPage(new WaitingDFSPage(this, "dfsWaiting", "Waiting DFS"));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SymbolEditorInput getEditorInput() {
		return (SymbolEditorInput) super.getEditorInput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		updatePartFromImput();
	}

	private void updatePartFromImput() {
		setPartName(getEditorInput().getName());
		setTitleToolTip(getEditorInput().getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	public void doSaveAs() {
		// Adding a comment to avoid empty block warning.
	}

}
