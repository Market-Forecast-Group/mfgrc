package com.mfg.utils.ui.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.mfg.utils.UtilsPlugin;

public class MFGColumnLabelProvider extends ColumnLabelProvider {
	private int index;
	private TableModelMiddleMan mm;
	private static Color disabledColor;
	private static Color hlColor;

	private static final String CHECKED_KEY = "CHECKED";
	private static final String UNCHECK_KEY = "UNCHECKED";

	public static Color getDisabledColor() {
		if (disabledColor == null)
			disabledColor = new Color(PlatformUI.getWorkbench().getDisplay(),
					127, 127, 127);
		return disabledColor;
	}

	private static Color getHighLightColor(@SuppressWarnings("unused") int aHl) {
		if (hlColor == null) {
			hlColor = new Color(PlatformUI.getWorkbench().getDisplay(), 153,
					252, 204);
		}
		return hlColor;
	}

	public static Color getGrayColor() {
		if (grayColor == null)
			grayColor = new Color(PlatformUI.getWorkbench().getDisplay(), 240,
					240, 240);
		return grayColor;
	}

	private static Color grayColor;

	public MFGColumnLabelProvider(int aIndex, TableModelMiddleMan aMiddleMan) {
		index = aIndex;
		mm = aMiddleMan;

		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
			JFaceResources.getImageRegistry().put(UNCHECK_KEY,
					simpleImage(false));
			JFaceResources.getImageRegistry().put(CHECKED_KEY,
					simpleImage(true));
		}
	}

	private static Image simpleImage(boolean type) {
		Shell shell = new Shell(Display.getDefault(), SWT.NO_TRIM);

		// otherwise we have a default gray color
		// shell.setBackground(greenScreen);

		Button button = new Button(shell, SWT.CHECK);
		// button.setBackground(greenScreen);
		button.setSelection(type);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				System.out.println("check");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent aE) {
				//
			}
		});

		// otherwise an image is located in a corner
		button.setLocation(1, 1);
		Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		// otherwise an image is stretched by width
		bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
		bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
		button.setSize(bsize);
		shell.setSize(bsize);

		if (type)
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					UtilsPlugin.PLUGIN_ID, "icons/checked.ico").createImage();
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				UtilsPlugin.PLUGIN_ID, "icons/unchecked.ico").createImage();
	}

	// private Image makeShot(Control control, boolean type) {
	// // Hopefully no platform uses exactly this color because we'll make
	// // it transparent in the image.
	// Color greenScreen = new Color(control.getDisplay(), 222, 223, 224);
	//
	// Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);
	//
	// // otherwise we have a default gray color
	// shell.setBackground(greenScreen);
	//
	// Button button = new Button(shell, SWT.CHECK);
	// button.setBackground(greenScreen);
	// button.setSelection(type);
	//
	// // otherwise an image is located in a corner
	// button.setLocation(1, 1);
	// Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	//
	// // otherwise an image is stretched by width
	// bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
	// bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
	// button.setSize(bsize);
	// shell.setSize(bsize);
	//
	// shell.open();
	// GC gc = new GC(shell);
	// Image image = new Image(control.getDisplay(), bsize.x, bsize.y);
	// gc.copyArea(image, 0, 0);
	// gc.dispose();
	// shell.close();
	//
	// ImageData imageData = image.getImageData();
	// imageData.transparentPixel = imageData.palette.getPixel(greenScreen
	// .getRGB());
	//
	// return new Image(control.getDisplay(), imageData);
	// }

	@Override
	public Image getImage(Object element) {
		Object content = getContent(element);
		if (isCheckBox(content)) {
			if (isChecked(content)) {
				return JFaceResources.getImageRegistry().get(CHECKED_KEY);
			}
			return JFaceResources.getImageRegistry().get(UNCHECK_KEY);
		}
		return null;
	}

	protected static boolean isChecked(Object element) {
		return element != null && element.toString().equals("true");
	}

	protected static boolean isCheckBox(Object element) {
		return element != null
				&& (element.toString().equals("true") || element.toString()
						.equals("false"));
	}

	@Override
	public String getText(Object element) {
		Object content = getContent(element);
		return content == null ? "" : content.toString();
	}

	protected Object getContent(Object element) {
		if (element == null || getModel() == null)
			return "";
		@SuppressWarnings("boxing")
		Object content = getModel().getContent(new Integer(element.toString()),
				index);
		return content;
	}

	private IMfgTableModel getModel() {
		return mm.getModel();
	}

	@SuppressWarnings("boxing")
	@Override
	public Color getBackground(Object element) {
		if (element == null
				|| getModel() == null
				|| !getModel()
						.isEnabled(new Integer(element.toString()), index))
			return getDisabledColor();
		int hl = getModel()
				.getHighLight(new Integer(element.toString()), index);
		if (hl > 0)
			return getHighLightColor(hl);
		int e = new Integer(element.toString());
		if (e % 2 == 1)
			return getGrayColor();
		return super.getBackground(element);
	}

}
