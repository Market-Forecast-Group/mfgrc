package com.mfg.chart.ui.osd.styles;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;

public class ATLSettingsDialog extends Dialog {
	final ATLSettingsDialog _self = this;
	float[] _anchorColor;
	Table _table;
	private RatioInfo[] _ratios;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ATLSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		composite.setLayout(new GridLayout(2, false));

		_btnChangeColor = new Button(composite, SWT.NONE);
		_btnChangeColor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		_btnChangeColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeLinesColor();
			}
		});
		_btnChangeColor.setText("Change Anchors Color...");

		Group grpColors = new Group(container, SWT.NONE);
		grpColors.setLayout(new GridLayout(2, false));
		grpColors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		grpColors.setText("Ratio Lines");

		_ratioLabel = new Label(grpColors, SWT.NONE);
		_ratioLabel.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		_ratioLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9,
				SWT.ITALIC));
		new Label(grpColors, SWT.NONE).setText("");

		_viewer = CheckboxTableViewer.newCheckList(grpColors, SWT.BORDER
				| SWT.FULL_SELECTION);
		_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				_changeColorBtn.setEnabled(!_viewer.getSelection().isEmpty());
			}
		});
		_table = _viewer.getTable();
		_table.setHeaderVisible(true);
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(_viewer,
				SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				for (int i = 0; i < _table.getItemCount(); i++) {
					if (element == _table.getItem(i).getData()) {
						return "Ratio Line " + (i + 1);
					}
				}
				return super.getText(element);
			}
		});
		TableColumn tblclmnRatioLine_1 = tableViewerColumn_3.getColumn();
		tblclmnRatioLine_1.setWidth(228);
		tblclmnRatioLine_1.setText("Ratio Line");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(_viewer,
				SWT.NONE);
		tableViewerColumn_4.setEditingSupport(new EditingSupport(_viewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(_viewer.getTable());
			}

			@Override
			protected Object getValue(Object element) {
				return Float.toString(((RatioInfo) element).getRatio());
			}

			@Override
			protected void setValue(Object element, Object value) {
				try {
					((RatioInfo) element).setRatio(Float
							.parseFloat((String) value));
				} catch (Exception e) {
					MessageDialog.openError(getShell(), "Format Error",
							e.getMessage());
				}
				_viewer.refresh();
			}
		});
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Float.toString(((RatioInfo) element).getRatio());
			}
		});
		TableColumn tblclmnRatio_1 = tableViewerColumn_4.getColumn();
		tblclmnRatio_1.setWidth(100);
		tblclmnRatio_1.setText("Ratio");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(_viewer,
				SWT.NONE);
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				RatioInfo ratioLine = (RatioInfo) element;
				return createImage(ratioLine.getColor());
			}

			@Override
			public String getText(Object element) {
				return "";
			}
		});
		TableColumn tblclmnColor_1 = tableViewerColumn_5.getColumn();
		tblclmnColor_1.setWidth(100);
		tblclmnColor_1.setText("Color");
		_viewer.setContentProvider(new ArrayContentProvider());

		_changeColorBtn = new Button(grpColors, SWT.NONE);
		_changeColorBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeColor();
			}
		});
		_changeColorBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				false, 1, 1));
		_changeColorBtn.setText("Change Color...");

		afterCreateWidgets();

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Auto Time Lines Settings");
	}

	void changeLinesColor() {
		ColorDialog dlg = new ColorDialog(getShell());
		float[] color = _anchorColor;
		RGB rgb = getRGB(color);
		dlg.setRGB(rgb);
		rgb = dlg.open();
		if (rgb != null) {
			_anchorColor = rgbToColor(rgb);
			updateColorButton();
		}
	}

	private void updateColorButton() {
		_btnChangeColor.setImage(createImage(_anchorColor));
	}

	protected void updateLineButtons() {
		_changeColorBtn.setEnabled(!_viewer.getSelection().isEmpty());
	}

	protected void changeColor() {
		ColorDialog dlg = new ColorDialog(getShell());
		RatioInfo ratioLine = (RatioInfo) ((StructuredSelection) _viewer
				.getSelection()).getFirstElement();
		float[] color = ratioLine.getColor();
		RGB rgb = getRGB(color);
		dlg.setRGB(rgb);
		rgb = dlg.open();
		if (rgb != null) {
			ratioLine.setColor(rgbToColor(rgb));
			_viewer.refresh();
		}
	}

	public static float[] rgbToColor(RGB rgb) {
		float[] color = new float[] { (float) rgb.red / 255,
				(float) rgb.green / 255, (float) rgb.blue / 255, 1 };
		return color;
	}

	private final Map<float[], Image> _imageMap = new HashMap<>();

	public Image createImage(float[] color) {
		Image img = _imageMap.get(color);
		if (img == null) {
			Display display = Display.getDefault();
			img = new Image(display, 16, 16);
			RGB rgb = getRGB(color);
			Color color2 = new Color(display, rgb);
			GC gc = new GC(img);
			gc.setAdvanced(true);
			gc.setBackground(color2);
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(0, 0, 16, 16);
			gc.drawRectangle(0, 0, 15, 15);
			gc.dispose();
			color2.dispose();
		}
		return img;
	}

	@Override
	public boolean close() {
		for (Image img : _imageMap.values()) {
			img.dispose();
		}
		_imageMap.clear();

		return super.close();
	}

	private void afterCreateWidgets() {
		_viewer.setInput(_ratios);

		for (TableItem item : _table.getItems()) {
			item.setChecked(((RatioInfo) item.getData()).isSelected());
		}

		_ratioLabel.setText("Ratios can have at most 3 decimals like "
				+ NumberFormat.getInstance().format(0.618));
		updateColorButton();

		updateLineButtons();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void buttonPressed(int buttonId) {

		updateCheckedRatios();

		setReturnCode(buttonId);
		close();
	}

	private void updateCheckedRatios() {
		for (int i = 0; i < _table.getItemCount(); i++) {
			_ratios[i].setSelected(_table.getItem(i).getChecked());
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(622, 463);
	}

	public void setSettings(RatioInfo[] ratios, float[] anchorColor) {
		RatioInfo[] list1 = ratios;
		RatioInfo[] list2 = new RatioInfo[list1.length];
		for (int i = 0; i < list1.length; i++) {
			list2[i] = list1[i].clone();
		}
		_ratios = list2;

		_anchorColor = anchorColor;
	}

	public float[] getAnchorColor() {
		return _anchorColor;
	}

	public void setAnchorColor(float[] anchorColor) {
		_anchorColor = anchorColor;
	}

	public RatioInfo[] getRatios() {
		return _ratios;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	Button _changeColorBtn;
	private Label _ratioLabel;
	CheckboxTableViewer _viewer;
	private Button _btnChangeColor;

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	static RGB getRGB(float[] color) {
		int r = (int) (255 * color[0]);
		int g = (int) (255 * color[1]);
		int b = (int) (255 * color[2]);
		RGB rgb = new RGB(r, g, b);
		return rgb;
	}
}
