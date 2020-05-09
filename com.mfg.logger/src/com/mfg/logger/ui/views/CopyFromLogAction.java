package com.mfg.logger.ui.views;

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

import com.mfg.logger.ILogRecord;

public class CopyFromLogAction extends Action {

	private static final String COPY_FROM_LOG = "com.mfg.logger.ui.views.CopyFromLogAction";
	private AbstractLoggerViewControl _control;

	public CopyFromLogAction(AbstractLoggerViewControl control) {
		super("Copy Selected Rows");
		setId(COPY_FROM_LOG);
		setControl(control);
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
		// IWorkbenchWindow window = PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow();
		// IWorkbenchPage page = window.getActivePage();
		// IViewPart view = page.findView(View.ID);
		Clipboard cb = new Clipboard(Display.getDefault());
		AbstractLoggerViewControl control = getControl();
		if (control != null) {
			ISelection selection = control.getLogView().getViewer()
					.getSelection();

			StringBuilder sb = new StringBuilder();

			if (selection != null && selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) selection;
				for (Iterator<ILogRecord> iterator = sel.iterator(); iterator
						.hasNext();) {
					ILogRecord rec = iterator.next();
					sb.append(rec.toString());
				}
			}
			TextTransfer textTransfer = TextTransfer.getInstance();
			cb.setContents(new Object[] { sb.toString() },
					new Transfer[] { textTransfer });
		}
	}

	public AbstractLoggerViewControl getControl() {
		return _control;
	}

	public void setControl(AbstractLoggerViewControl aControl) {
		_control = aControl;
	}

}
