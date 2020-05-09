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
package com.mfg.symbols.ui.widgets;

import static org.eclipse.swt.SWT.READ_ONLY;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.common.BAR_TYPE;
import com.mfg.dm.SlotParams;
import com.mfg.dm.UnitsType;

/**
 * @author arian
 * 
 */
public class RequestTableComposite extends Composite {

	protected static final String PROP_MODEL = "model";
	protected static final String PRICE_SCALE = "Price";
	protected static final int MAX_NUMBER_OF_SCALES = 15;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	Table table;
	TableViewer tableViewer;
	private List<SlotParams> model;
	private SlotParams defaultSlot;
	private Button addSlotButton;
	Button removeSlotButton;
	Button moveUpSlotButton;
	Button moveDownSlotButton;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RequestTableComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = toolkit.createComposite(this, SWT.NONE);
		toolkit.paintBordersFor(composite);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		Composite compositeTable = new Composite(composite, SWT.NONE);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		compositeTable.setSize(150, 300);
		toolkit.adapt(compositeTable);
		toolkit.paintBordersFor(compositeTable);
		TableColumnLayout tcl_compositeTable = new TableColumnLayout();
		compositeTable.setLayout(tcl_compositeTable);

		tableViewer = new TableViewer(compositeTable, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		toolkit.paintBordersFor(table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				ComboBoxCellEditor comboCellEditor = new ComboBoxCellEditor(
						table, new String[] { UnitsType.DAYS.toString(),
								UnitsType.BARS.toString() }, READ_ONLY);
				comboCellEditor.setValue(Integer.valueOf(((SlotParams) element)
						.getUnitsType().ordinal()));
				return comboCellEditor;
			}

			@Override
			protected Object getValue(Object element) {
				return Integer.valueOf(((SlotParams) element).getUnitsType()
						.ordinal());
			}

			@Override
			protected void setValue(Object element, Object value) {
				((SlotParams) element).setUnitsType(UnitsType.values()[((Integer) value)
						.intValue()]);
				tableViewer.refresh();
				firePropertyChange(PROP_MODEL);
			}
		});
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object elementPar) {
				Object element = elementPar;
				element = ((SlotParams) element).getUnitsType();
				return element == null ? "" : element.toString();
			}
		});
		TableColumn tblclmnUnitsType = tableViewerColumn.getColumn();
		tcl_compositeTable.setColumnData(tblclmnUnitsType,
				new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnUnitsType.setText("Units Type");

		TableViewerColumn tableViewerColumn_Units_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_Units_1.setEditingSupport(new EditingSupport(
				tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				TextCellEditor textCellEditor = new TextCellEditor(table);
				textCellEditor.setValue(""
						+ ((SlotParams) element).getMultiplicityBar());
				return textCellEditor;
			}

			@Override
			protected Object getValue(Object element) {
				return "" + ((SlotParams) element).getMultiplicityBar();
			}

			@Override
			protected void setValue(Object element, Object value) {
				int mulBar = Integer.parseInt(value.toString());
				((SlotParams) element).setMultiplicityBar(mulBar);
				tableViewer.refresh();
				firePropertyChange(PROP_MODEL);
			}
		});

		tableViewerColumn_Units_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((SlotParams) element)
						.getMultiplicityBar());
			}

		});

		TableColumn tblclmnUnits_1 = tableViewerColumn_Units_1.getColumn();
		tcl_compositeTable.setColumnData(tblclmnUnits_1, new ColumnWeightData(
				1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnUnits_1.setText("# Units");

		TableViewerColumn tableViewerColumn_BarType_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_BarType_1.setEditingSupport(new EditingSupport(
				tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				ComboBoxCellEditor editor = new ComboBoxCellEditor(table,
						BAR_TYPE.ITEMS, SWT.READ_ONLY);
				editor.setValue(Integer.valueOf(((SlotParams) element)
						.getBarType().ordinal()));
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return Integer.valueOf(((SlotParams) element).getBarType()
						.ordinal());
			}

			@Override
			protected void setValue(Object element, Object value) {
				((SlotParams) element).setBarType(BAR_TYPE.values()[((Integer) value)
						.intValue()]);
				tableViewer.refresh();
				firePropertyChange(PROP_MODEL);
			}
		});
		tableViewerColumn_BarType_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((SlotParams) element).getBarType().toString();
			}
		});
		TableColumn tblclmnBarType_1 = tableViewerColumn_BarType_1.getColumn();
		tcl_compositeTable.setColumnData(tblclmnBarType_1,
				new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnBarType_1.setText("Bar Type");

		TableViewerColumn tableViewerColumn_NumBars_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_NumBars_1.setEditingSupport(new EditingSupport(
				tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				TextCellEditor editor = new TextCellEditor(table);
				editor.setValue(((SlotParams) element).getNumBars() + "");
				firePropertyChange(PROP_MODEL);
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return ((SlotParams) element).getNumBars() + "";
			}

			@Override
			protected void setValue(Object element, Object value) {
				((SlotParams) element).setNumBars(Integer.parseInt(value
						.toString()));
				tableViewer.refresh();
				firePropertyChange(PROP_MODEL);
			}
		});
		tableViewerColumn_NumBars_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((SlotParams) element).getNumBars());
			}
		});
		TableColumn tblclmnOfBars_1 = tableViewerColumn_NumBars_1.getColumn();
		tcl_compositeTable.setColumnData(tblclmnOfBars_1, new ColumnWeightData(
				1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnOfBars_1.setText("# of Bars");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setEditingSupport(new EditingSupport(tableViewer) {
			private String[] items;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				items = new String[MAX_NUMBER_OF_SCALES + 1];
				items[0] = PRICE_SCALE;
				for (int i = 1; i < items.length; i++) {
					items[i] = Integer.toString(i);
				}
				ComboBoxCellEditor editor = new ComboBoxCellEditor(table,
						items, SWT.READ_ONLY);
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return Integer.valueOf(((SlotParams) element).getScale());
			}

			@Override
			protected void setValue(Object element, Object value) {
				((SlotParams) element).setScale(((Integer) value).intValue());
				tableViewer.refresh();
				firePropertyChange(PROP_MODEL);
			}
		});
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				int scale = ((SlotParams) element).getScale();
				return scale == 0 ? PRICE_SCALE : Integer.toString(scale);
			}
		});

		TableColumn tblclmnScale = tableViewerColumn_1.getColumn();
		tcl_compositeTable.setColumnData(tblclmnScale, new ColumnWeightData(1,
				ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnScale.setText("Scale");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("deprecation")
			// Necessary use of getCap.
			@Override
			public String getText(Object element) {
				return Double.toString(((SlotParams) element).getGap());
			}
		});
		tableViewerColumn_2.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(table);
			}

			@SuppressWarnings("deprecation")
			// Necessary use of getCap.
			@Override
			protected Object getValue(Object element) {
				return Double.toString(((SlotParams) element).getGap());
			}

			@SuppressWarnings("deprecation")
			// Necessary use of setCap.
			@Override
			protected void setValue(Object element, Object value) {
				try {
					((SlotParams) element).setGap(Double
							.parseDouble((String) value));
					firePropertyChange(PROP_MODEL);
				} catch (NumberFormatException e) {
					// Documenting block to avoid warning.
				}
				tableViewer.refresh();
			}
		});
		TableColumn tblclmnGap = tableViewerColumn_2.getColumn();
		tcl_compositeTable.setColumnData(tblclmnGap, new ColumnWeightData(1,
				ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnGap.setText("Gap");

		Composite compositeButtons = toolkit.createComposite(composite,
				SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		toolkit.paintBordersFor(compositeButtons);
		GridLayout gl_compositeButtons = new GridLayout(4, false);
		compositeButtons.setLayout(gl_compositeButtons);

		addSlotButton = toolkit.createButton(compositeButtons, "", SWT.NONE);
		addSlotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSlot();
			}
		});
		addSlotButton.setImage(ResourceManager.getPluginImage("org.eclipse.ui",
				"/icons/full/obj16/add_obj.gif"));
		addSlotButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false, 1, 1));

		removeSlotButton = toolkit.createButton(compositeButtons, "", SWT.NONE);
		removeSlotButton.setEnabled(false);
		removeSlotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSlot();
			}
		});
		removeSlotButton.setImage(ResourceManager.getPluginImage(
				"org.eclipse.ui", "/icons/full/elcl16/remove.gif"));

		moveUpSlotButton = toolkit.createButton(compositeButtons, "", SWT.NONE);
		moveUpSlotButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		moveUpSlotButton.setEnabled(false);
		moveUpSlotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveUpSlot();
			}
		});
		moveUpSlotButton.setImage(ResourceManager.getPluginImage(
				"com.mfg.symbols", "icons/up.png"));

		moveDownSlotButton = toolkit.createButton(compositeButtons, "",
				SWT.NONE);
		moveDownSlotButton.setEnabled(false);
		moveDownSlotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveDownSlot();
			}
		});
		moveDownSlotButton.setImage(ResourceManager.getPluginImage(
				"com.mfg.symbols", "icons/down.png"));
		moveDownSlotButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		tableViewer.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets();
	}

	private void afterCreateWidgets() {
		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection sel = (StructuredSelection) event
								.getSelection();
						removeSlotButton.setEnabled(!sel.isEmpty());
						List<SlotParams> slots = getModel();
						Object selSlot = sel.getFirstElement();
						moveUpSlotButton.setEnabled(!sel.isEmpty()
								&& slots.indexOf(selSlot) > 0);
						moveDownSlotButton.setEnabled(!sel.isEmpty()
								&& slots.indexOf(selSlot) < slots.size() - 1);
					}
				});
		// UIUtils.updateLayout(UIUtils.getRootParent(table));
	}

	private void moveSlot(int dir) {
		StructuredSelection sel = (StructuredSelection) tableViewer
				.getSelection();
		List<SlotParams> slots = getModel();
		SlotParams selSlot = (SlotParams) sel.getFirstElement();
		int i = slots.indexOf(selSlot);
		slots.remove(selSlot);
		slots.add(i + dir, selSlot);
		tableViewer.refresh();
		firePropertyChange(PROP_MODEL);
	}

	void moveDownSlot() {
		moveSlot(1);
	}

	void moveUpSlot() {
		moveSlot(-1);
	}

	protected void removeSlot() {
		StructuredSelection sel = (StructuredSelection) tableViewer
				.getSelection();
		getModel().remove(sel.getFirstElement());
		tableViewer.refresh();
		firePropertyChange(PROP_MODEL);
	}

	protected void addSlot() {
		SlotParams slot = new SlotParams();
		slot.setBarType(defaultSlot.getBarType());
		slot.setMultiplicityBar(defaultSlot.getMultiplicityBar());
		slot.setNumBars(defaultSlot.getNumBars());
		slot.setUnitsType(defaultSlot.getUnitsType());
		getModel().add(slot);
		tableViewer.refresh();
		firePropertyChange(PROP_MODEL);
	}

	public void setModel(List<SlotParams> aModel) {
		this.model = aModel;
		tableViewer.setInput(aModel);
		firePropertyChange(PROP_MODEL);
	}

	/**
	 * @return the model
	 */
	public List<SlotParams> getModel() {
		return model;
	}

	public void setDefaultSlot(SlotParams slot) {
		this.defaultSlot = slot;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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
}
