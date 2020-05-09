package com.mfg.utils.ui.table;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CopyFromMFGModelTableAction extends Action {

	public static final String COPY_FROM_TABLE = "com.mfg.utils.ui.table.CopyFromMFGModelTableAction";
	private MfgModelTable table;

	public CopyFromMFGModelTableAction(MfgModelTable aTable) {
		super("Copy Selected Rows");
		setId(COPY_FROM_TABLE);
		setTable(aTable);
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
	}
	@Override
	public ImageDescriptor getImageDescriptor() {
		return super.getImageDescriptor();
	}

	@Override
	public void run() {
		Clipboard cb = new Clipboard(Display.getDefault());
		ISelection selection = getTable().getTableViewer().getSelection();

		StringBuilder sb = new StringBuilder();

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = sel.iterator(); iterator.hasNext();) {
				Object rec = iterator.next();
				sb.append(table.getRowText(Integer.parseInt(rec.toString())));
			}
		}
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]{sb.toString()},
				new Transfer[]{textTransfer});
	}

	public MfgModelTable getTable() {
		return table;
	}

	public void setTable(MfgModelTable aTable) {
		table = aTable;
	}

}
