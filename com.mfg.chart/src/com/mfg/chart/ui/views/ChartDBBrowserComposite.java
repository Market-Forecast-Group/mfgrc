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

package com.mfg.chart.ui.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.mfg.mdb.runtime.IRecord;
import org.mfg.mdb.runtime.ISeqCursor;
import org.mfg.mdb.runtime.IValidatorListener;
import org.mfg.mdb.runtime.MDB;
import org.mfg.mdb.runtime.ValidatorError;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.model.mdb.ChartDataValidators;
import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.Channel2MDB;
import com.mfg.inputdb.indicator.mdb.ChannelMDB;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.tradingdb.mdb.EquityMDB;
import com.mfg.tradingdb.mdb.ProbabilityMDB;
import com.mfg.tradingdb.mdb.TradeMDB;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.ui.UIUtils;

/**
 * @author arian
 * 
 */
@SuppressWarnings("rawtypes")
public class ChartDBBrowserComposite extends Composite {
	static DateFormat _dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static DateFormat _timeFormat = new SimpleDateFormat("HH:mm:ss");

	private static final String PROP_ROW = "row";

	private abstract class DBObject {
		private final String _name;
		private final boolean _hasLevels;

		public DBObject(final String name, final boolean hasLevels) {
			super();
			this._name = name;
			this._hasLevels = hasLevels;
		}

		public DBObject(final String name) {
			this(name, true);
		}

		public boolean isHasLevels() {
			return _hasLevels;
		}

		public abstract MDB getMDB(int level, int layer) throws IOException;

		public String getName() {
			return _name;
		}

		public abstract void validate(IValidatorListener l, int level, int layer)
				throws IOException;
	}

	private static class ObjectLabelProvider extends LabelProvider {
		public ObjectLabelProvider() {
		}

		@Override
		public Image getImage(final Object element) {
			return null;
		}

		@Override
		public String getText(final Object element) {
			final DBObject obj = (DBObject) element;
			return obj.getName();
		}
	}

	final FormToolkit _toolkit = new FormToolkit(Display.getCurrent());
	private final ComboViewer _objectCombo;
	final ComboViewer _levelCombo;
	private final Section _sctnTable;
	final Section _sectionData;
	final Hyperlink _showDataLink;
	final Hyperlink _validateDataLink;
	private final Section _sctnBrowse;
	private final Composite _composite_2;
	private final Label _lblRunToRow;
	private final Text _textRow;
	private final Button _btnGo;
	private final ChartDBBrowserComposite self = this;
	private int _row;
	private MDBContentProvider _contentProvider;
	@SuppressWarnings("unused")
	private final DataBindingContext m_bindingContext;
	PriceMDBSession _priceSession;
	IndicatorMDBSession _indicatorSession;
	TradingMDBSession _tradingSession;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings({ "unused", "static-access" })
	public ChartDBBrowserComposite(final Composite parent, final int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				_toolkit.dispose();
			}
		});
		_toolkit.adapt(this);
		_toolkit.paintBordersFor(this);
		setLayout(new GridLayout(3, false));

		_sctnTable = _toolkit
				.createSection(this, ExpandableComposite.TITLE_BAR);
		_sctnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		_toolkit.paintBordersFor(_sctnTable);
		_sctnTable.setText("Table");

		final Composite composite = _toolkit.createComposite(_sctnTable,
				SWT.NONE);
		_toolkit.paintBordersFor(composite);
		_sctnTable.setClient(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		_sctnBrowse = _toolkit.createSection(this, Section.TITLE_BAR);
		_sctnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		_toolkit.paintBordersFor(_sctnBrowse);
		_sctnBrowse.setText("Browse");
		_sctnBrowse.setExpanded(true);

		_composite_2 = _toolkit.createComposite(_sctnBrowse, SWT.NONE);
		_toolkit.paintBordersFor(_composite_2);
		_sctnBrowse.setClient(_composite_2);
		_composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		new Label(this, SWT.NONE);

		Composite _composite_3 = _toolkit.createComposite(this, SWT.NONE);
		_composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		_toolkit.paintBordersFor(_composite_3);
		_composite_3.setLayout(new GridLayout(2, false));

		final Label lblObject = _toolkit.createLabel(_composite_3, "Object",
				SWT.NONE);

		_objectCombo = new ComboViewer(_composite_3, SWT.READ_ONLY);
		final Combo combo = _objectCombo.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		_toolkit.paintBordersFor(combo);

		final Label lblLevel = _toolkit.createLabel(_composite_3, "Level",
				SWT.NONE);

		_levelCombo = new ComboViewer(_composite_3, SWT.READ_ONLY);
		final Combo combo_1 = _levelCombo.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_toolkit.paintBordersFor(combo_1);

		Label lblLayer = _toolkit.createLabel(_composite_3, "Layer", SWT.NONE);

		_layerComboViewer = new ComboViewer(_composite_3, SWT.READ_ONLY);
		Combo combo_2 = _layerComboViewer.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		_toolkit.paintBordersFor(combo_2);

		Composite _composite_4 = new Composite(_composite_3, SWT.NONE);
		_composite_4.setLayout(new GridLayout(3, false));
		_composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		_toolkit.adapt(_composite_4);

		_showDataLink = _toolkit.createHyperlink(_composite_4, "Show Data",
				SWT.NONE);
		_showDataLink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				try {
					actionShowData();
				} catch (IOException e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1);
				}
			}

			@Override
			public void linkEntered(final HyperlinkEvent e) {
				//
			}

			@Override
			public void linkExited(final HyperlinkEvent e) {
				//
			}
		});
		_toolkit.paintBordersFor(_showDataLink);

		_validateDataLink = _toolkit.createHyperlink(_composite_4,
				"Validate Data", SWT.NONE);
		_validateDataLink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				actionValidateData();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				//
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				//
			}
		});
		_validateDataLink.setEnabled(false);
		_toolkit.paintBordersFor(_validateDataLink);

		_copyAllLink = _toolkit.createHyperlink(_composite_4, "Copy All",
				SWT.NONE);
		_copyAllLink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				copyAll();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				//
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				//
			}
		});
		_toolkit.paintBordersFor(_copyAllLink);
		new Label(_composite_3, SWT.NONE);
		new Label(_composite_3, SWT.NONE);
		_layerComboViewer.setContentProvider(new ArrayContentProvider());
		_layerComboViewer.setLabelProvider(new LabelProvider());
		_levelCombo.setLabelProvider(new LabelProvider());
		_levelCombo.setContentProvider(new ArrayContentProvider());
		_objectCombo.setLabelProvider(new ObjectLabelProvider());
		_objectCombo.setContentProvider(new ArrayContentProvider());

		Composite _composite_1 = _toolkit.createComposite(this, SWT.NONE);
		_composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		_composite_1.setLayout(new GridLayout(3, false));
		_toolkit.paintBordersFor(_composite_1);

		_lblRunToRow = _toolkit.createLabel(_composite_1, "Run To Row",
				SWT.NONE);
		_textRow = _toolkit.createText(_composite_1, "New Text", SWT.NONE);
		_textRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		_textRow.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case 13:
					runToRow();
					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//
			}
		});
		_textRow.setText("");

		_btnGo = _toolkit.createButton(_composite_1, "Go", SWT.NONE);
		_btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runToRow();
			}
		});
		new Label(this, SWT.NONE);

		_sectionData = _toolkit.createSection(this,
				ExpandableComposite.TITLE_BAR);
		_sectionData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				3, 1));
		_toolkit.paintBordersFor(_sectionData);
		_sectionData.setText("Data                                           ");

		Composite _composite_5 = _toolkit.createComposite(_sectionData,
				SWT.NONE);
		_toolkit.paintBordersFor(_composite_5);
		_sectionData.setClient(_composite_5);
		_composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));

		_tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL | SWT.MULTI);
		_table = _tableViewer.getTable();
		_table.setLinesVisible(true);
		_table.setHeaderVisible(true);
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		_toolkit.paintBordersFor(_table);
		new Label(this, SWT.NONE);

		m_bindingContext = initDataBindings();
	}

	void copyAll() {
		try {
			final DBObject selectedObject = (DBObject) ((StructuredSelection) _objectCombo
					.getSelection()).getFirstElement();
			final Integer selectedLevel = (Integer) ((StructuredSelection) _levelCombo
					.getSelection()).getFirstElement();
			final Integer selectedLayer = (Integer) ((StructuredSelection) _layerComboViewer
					.getSelection()).getFirstElement();

			if (selectedObject != null) {
				MDB mdb = null;

				final boolean hasLevels = selectedObject.isHasLevels();

				if (hasLevels && selectedLevel != null) {
					mdb = selectedObject.getMDB(selectedLevel.intValue(),
							selectedLayer.intValue());
				} else if (!hasLevels) {
					mdb = selectedObject.getMDB(-1, selectedLayer.intValue());
				}

				if (mdb != null) {
					copyAll(mdb);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "boxing", "unchecked" })
	private static void copyAll(MDB mdb) throws IOException {
		StringBuilder html = new StringBuilder();
		StringBuilder text = new StringBuilder();
		IRecord[] list;
		try (ISeqCursor c = mdb.cursor()) {
			list = mdb.selectAll(c);
		}
		html.append("<table>");
		html.append("<tr>");
		int i = 0;
		for (String name : mdb.getColumnsName()) {
			html.append("<td>" + name + "</td>");
			text.append((i == 0 ? "" : ",") + name);
			i++;
		}

		boolean isPriceMDB = mdb instanceof PriceMDB;
		// the case of prices
		if (isPriceMDB) {
			html.append("<td>Date</td>");
			text.append(",Date");

			html.append("<td>Time</td>");
			text.append(",Time");
		}
		//

		boolean isPivotMDB = mdb instanceof PivotMDB;
		// the case of prices
		if (isPivotMDB) {
			html.append("<td>Pivot Date</td>");
			text.append(",Pivot Date");

			html.append("<td>Pivot Time</td>");
			text.append(",Pivot Time");

			html.append("<td>TH Date</td>");
			text.append(",TH Date");

			html.append("<td>TH Time</td>");
			text.append(",TH Time");
		}
		//

		html.append("</tr>");
		text.append("\n");

		for (IRecord r : list) {
			List<Object> values = new ArrayList<>(Arrays.asList(r.toArray()));
			if (isPriceMDB) {
				// prices
				Date date = new Date(
						(long) values.get(PriceMDB.COLUMN_PHYSICALTIME));
				values.add(_dateFormat.format(date));
				values.add(_timeFormat.format(date));
			} else if (isPivotMDB) {
				// pivot
				Date date = new Date(
						(long) values.get(PivotMDB.COLUMN_PIVOTPHYSICALTIME));
				values.add(_dateFormat.format(date));
				values.add(_timeFormat.format(date));

				// th
				date = new Date(
						(long) values.get(PivotMDB.COLUMN_CONFIRMPHYSICALTIME));
				values.add(_dateFormat.format(date));
				values.add(_timeFormat.format(date));
			}

			html.append("<tr>");
			i = 0;
			for (Object obj : values) {
				html.append("<td>" + obj + "</td>");
				text.append((i == 0 ? "" : ",") + obj);
				i++;
			}
			text.append("\n");
			html.append("</tr>");
		}
		html.append("</table>");

		Clipboard clipboard = new Clipboard(Display.getDefault());
		HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(
				new Object[] { html.toString(), text.toString() },
				new Transfer[] { htmlTransfer, textTransfer });
	}

	protected void runToRow() {
		if (!_objectCombo.getSelection().isEmpty()) {
			int row2 = Math.max(0, _row - 1);
			if (_row > _table.getItemCount()) {
				try {
					grow(row2 - _table.getItemCount() + 100);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			_table.setTopIndex(Math.max(0, row2 - 10));
			_table.select(row2);
		}
	}

	public int getRow() {
		return _row;
	}

	public void setRow(int row) {
		this._row = row;
		firePropertyChange(PROP_ROW);
	}

	void actionValidateData() {
		final DBObject selectedObject = (DBObject) ((StructuredSelection) _objectCombo
				.getSelection()).getFirstElement();
		final Integer selectedLevel = (Integer) ((StructuredSelection) _levelCombo
				.getSelection()).getFirstElement();
		final Integer selectedLayer = (Integer) ((StructuredSelection) _layerComboViewer
				.getSelection()).getFirstElement();
		Job job = new Job("Validating Chart's database") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus status;
				try {
					final List<ValidatorError> errors = new ArrayList<>();
					selectedObject.validate(new IValidatorListener() {

						@Override
						public void errorReported(ValidatorError args) {
							errors.add(args);
						}

					}, selectedLevel.intValue(), selectedLayer.intValue());
					Runnable run;
					if (errors.isEmpty()) {
						run = new Runnable() {
							@Override
							public void run() {
								MessageDialog
										.openInformation(getShell(),
												"Data Validation",
												"No errors founded.");
							}
						};
					} else {
						run = new Runnable() {
							@Override
							public void run() {
								ValidationDialog dialog = new ValidationDialog(
										getShell());
								dialog.setErrors(errors);
								dialog.open();
							}
						};
					}
					Display.getDefault().asyncExec(run);

					status = Status.OK_STATUS;

				} catch (Exception e) {
					e.printStackTrace();
					status = new Status(IStatus.ERROR, ChartPlugin.PLUGIN_ID,
							e.getMessage(), e);
				}
				monitor.done();
				return status;
			}
		};
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						_validateDataLink.setEnabled(true);
					}
				});
			}
		});
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				_validateDataLink.setEnabled(false);
			}
		});
		job.setSystem(true);
		job.schedule();
	}

	public TableViewer getTableViewer() {
		return _tableViewer;
	}

	/**
	 * @param amount
	 * @throws IOException
	 */
	void grow(int amount) throws IOException {
		// TODO
	}

	/**
	 * @throws IOException
	 * 
	 */
	protected void actionShowData() throws IOException {
		final DBObject selectedObject = (DBObject) ((StructuredSelection) _objectCombo
				.getSelection()).getFirstElement();
		final Integer selectedLevel = (Integer) ((StructuredSelection) _levelCombo
				.getSelection()).getFirstElement();
		final Integer selectedLayer = (Integer) ((StructuredSelection) _layerComboViewer
				.getSelection()).getFirstElement();

		if (selectedObject != null) {
			MDB mdb = null;

			final boolean hasLevels = selectedObject.isHasLevels();

			if (hasLevels && selectedLevel != null) {
				mdb = selectedObject.getMDB(selectedLevel.intValue(),
						selectedLayer.intValue());
			} else if (!hasLevels) {
				mdb = selectedObject.getMDB(-1, selectedLayer.intValue());
			}

			if (mdb != null) {
				updateTable(mdb);
			}
		}

	}

	/**
	 * @param mdb
	 * @throws IOException
	 */
	private void updateTable(MDB mdb) throws IOException {
		int i = 0;
		final MDB fmdb = mdb;
		final Table table = _tableViewer.getTable();

		table.setRedraw(false);

		while (table.getColumnCount() > 0) {
			table.getColumn(0).dispose();
		}

		TableViewerColumn col = new TableViewerColumn(_tableViewer, SWT.LEFT);
		col.getColumn().setText("Index");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(Integer
						.valueOf(((MDBContentProvider.Row) element).index + 1));
			}
		});

		i = 0;
		for (final String name : fmdb.getColumnsName()) {
			final int index = i;
			if (name != PriceMDB.COLUMNS_NAME[PriceMDB.COLUMN_PHYSICALTIME]) {
				col = new TableViewerColumn(_tableViewer, SWT.LEFT);
				col.getColumn().setText(name);
				if (name == ChannelMDB.COLUMNS_NAME[ChannelMDB.COLUMN_SLOPE]) {
					col.setLabelProvider(new ColumnLabelProvider() {
						@Override
						public String getText(Object element) {
							MDBContentProvider.Row r = (MDBContentProvider.Row) element;
							Boolean up = (Boolean) r.data[index];
							return up.booleanValue() ? "UP" : "DOWN";
						}
					});
				} else {
					col.setLabelProvider(new ColumnLabelProvider() {
						@Override
						public String getText(final Object element) {
							MDBContentProvider.Row r = (MDBContentProvider.Row) element;
							return super.getText(r.data[index]);
						}
					});
				}
			}
			i++;
		}
		if (fmdb instanceof PriceMDB) {
			// add other columns in case of PriceMDB

			// date column
			col = new TableViewerColumn(_tableViewer, SWT.LEFT);
			col.getColumn().setText("Date");
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					MDBContentProvider.Row r = (MDBContentProvider.Row) element;
					@SuppressWarnings("boxing")
					final long time = (Long) r.data[PriceMDB.COLUMN_PHYSICALTIME];
					return super.getText(_dateFormat.format(new Date(time)));
				}
			});

			// time column
			col = new TableViewerColumn(_tableViewer, SWT.LEFT);
			col.getColumn().setText("Time");
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					MDBContentProvider.Row r = (MDBContentProvider.Row) element;
					@SuppressWarnings("boxing")
					final long time = (Long) r.data[PriceMDB.COLUMN_PHYSICALTIME];
					return super.getText(_timeFormat.format(new Date(time)));
				}
			});
		}

		if (fmdb instanceof PivotMDB) {
			// add other columns in case of PivotMDB
			int[] cols = { PivotMDB.COLUMN_PIVOTPHYSICALTIME,
					PivotMDB.COLUMN_CONFIRMPHYSICALTIME };
			String[] names = { "Pivot", "TH" };
			for (int j = 0; j < cols.length; j++) {
				final int colId = cols[j];
				String name = names[j];
				// date column
				col = new TableViewerColumn(_tableViewer, SWT.LEFT);
				col.getColumn().setText(name + " Date");
				col.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(final Object element) {
						MDBContentProvider.Row r = (MDBContentProvider.Row) element;
						@SuppressWarnings("boxing")
						final long time = (Long) r.data[colId];
						return super.getText(_dateFormat.format(new Date(time)));
					}
				});

				// time column
				col = new TableViewerColumn(_tableViewer, SWT.LEFT);
				col.getColumn().setText(name + " Time");
				col.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(final Object element) {
						MDBContentProvider.Row r = (MDBContentProvider.Row) element;
						@SuppressWarnings("boxing")
						final long time = (Long) r.data[colId];
						return super.getText(_timeFormat.format(new Date(time)));
					}
				});
			}
		}

		_tableViewer.setInput(fmdb);

		for (i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
		table.setRedraw(true);

		_sectionData.setText("Data (" + mdb.size() + ")");
	}

	public void setSessions(final PriceMDBSession priceSession,
			final IndicatorMDBSession indicatorSession,
			final TradingMDBSession tradingSession) {
		if (!isDisposed()) {
			this._priceSession = priceSession;
			this._indicatorSession = indicatorSession;
			this._tradingSession = tradingSession;

			updateContent();
		}
	}

	private void updateContent() {
		_showDataLink.setEnabled(_priceSession != null);
		_validateDataLink.setEnabled(_priceSession != null);

		_table.getVerticalBar().addSelectionListener(new SelectionListener() {
			int last = -1;

			@Override
			public void widgetSelected(SelectionEvent e) {
				int bottomIndex = _table.getTopIndex()
						+ (_table.getClientArea().height - _table
								.getHeaderHeight()) / _table.getItemHeight();
				if (bottomIndex > last) {
					if (bottomIndex == _table.getItemCount()) {
						try {
							grow(100);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				last = bottomIndex;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});

		final ArrayList<DBObject> list = new ArrayList<>();
		if (_priceSession != null) {
			list.add(new DBObject("Prices", false) {

				@Override
				public MDB getMDB(final int level, int layer)
						throws IOException {
					return _priceSession.connectTo_PriceMDB(layer);
				}

				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					//
				}
			});
		}
		if (_indicatorSession != null) {
			list.add(new DBObject("Bands") {

				@Override
				public MDB getMDB(final int level, int layer)
						throws IOException {
					return _indicatorSession.connectTo_BandsMDB(layer, level);
				}

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					MDB mdb = getMDB(level, layer);
					mdb.validate(10, l, BandsMDB.TIME_ASCENDING_VALIDATOR);
				}

			});
			list.add(new DBObject("Pivots") {

				@Override
				public MDB getMDB(final int level, int layer)
						throws IOException {
					return _indicatorSession.connectTo_PivotMDB(layer, level);
				}

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					MDB mdb = getMDB(level, layer);
					mdb.validate(
							10,
							l,
							PivotMDB.PIVOTTIME_ASCENDING_VALIDATOR,
							ChartDataValidators.PIVOTS_SUBSEQ_HAS_DIFF_IS_UP_VALUE);
				}
			});

			list.add(new DBObject("Channels") {

				@Override
				public MDB getMDB(final int level, int layer)
						throws IOException {
					return _indicatorSession
							.connectTo_Channel2MDB(layer, level);
				}

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					MDB mdb = getMDB(level, layer);
					mdb.validate(10, l,
							Channel2MDB.ENDTIME_ASCENDING_VALIDATOR,
							Channel2MDB.STARTTIME_ASCENDING_VALIDATOR);
				}
			});
		}

		if (_tradingSession != null) {
			list.add(new DBObject("Probability") {

				@Override
				public MDB getMDB(final int level, int layer)
						throws IOException {
					return _tradingSession.connectTo_ProbabilityMDB(level);
				}

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					MDB mdb = getMDB(level, layer);
					mdb.validate(10, l, ProbabilityMDB.TIME_ASCENDING_VALIDATOR);
				}
			});

			list.add(new DBObject("Trades") {

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					getMDB(level, layer).validate(10, l,
							TradeMDB.CLOSETIME_ASCENDING_VALIDATOR);
				}

				@Override
				public MDB getMDB(int level, int layer) throws IOException {
					return _tradingSession.connectTo_TradeMDB();
				}
			});

			list.add(new DBObject("Equity") {

				@Override
				public MDB getMDB(int level, int layer) throws IOException {
					return _tradingSession.connectTo_EquityMDB();
				}

				@SuppressWarnings("unchecked")
				@Override
				public void validate(IValidatorListener l, int level, int layer)
						throws IOException {
					getMDB(level, layer).validate(10, l,
							EquityMDB.FAKETIME_ASCENDING_VALIDATOR);
				}

			});
		}

		_objectCombo.setInput(list);
		_objectCombo.refresh();
		_objectCombo.getCombo().select(0);

		_contentProvider = new MDBContentProvider();
		_tableViewer.setContentProvider(_contentProvider);

		if (_priceSession == null && _indicatorSession == null
				&& _tradingSession == null) {
			getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!ChartDBBrowserComposite.this.isDisposed()) {
						_levelCombo
								.setContentProvider(new ArrayContentProvider());
						_levelCombo.setInput(new Object[0]);
						_layerComboViewer.setInput(new Object[0]);
						_tableViewer.setInput(new Object[0]);
						for (final TableColumn col : _tableViewer.getTable()
								.getColumns()) {
							col.dispose();
						}
						_tableViewer.refresh();
						_showDataLink.setEnabled(false);
						_validateDataLink.setEnabled(false);
						_sectionData.setText("Data");
					}
				}
			});
		} else {
			_showDataLink.setEnabled(true);
			_validateDataLink.setEnabled(true);
			// level
			final ArrayList<Integer> levelList = new ArrayList<>();
			if (_indicatorSession != null) {
				for (int l = 1; l <= _indicatorSession.getScalesCount(); l++) {
					levelList.add(Integer.valueOf(l));
				}
			}
			_levelCombo.setInput(levelList);

			List<Integer> layersList = new ArrayList<>();
			if (_priceSession != null) {
				for (int i = 0; i < _priceSession.getDataLayersCount(); i++) {
					layersList.add(Integer.valueOf(i));
				}
			}
			_layerComboViewer.setInput(layersList);
			_layerComboViewer.setSelection(new StructuredSelection(Integer
					.valueOf(0)));

			if (!levelList.isEmpty()) {
				_levelCombo.setSelection(new StructuredSelection(levelList
						.get(levelList.size() - 1)));
			}
		}

		UIUtils.updateLayout(this);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	final ComboViewer _layerComboViewer;
	final Table _table;
	final TableViewer _tableViewer;
	private final Hyperlink _copyAllLink;

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

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textRowObserveTextObserveWidget = SWTObservables
				.observeText(_textRow, SWT.Modify);
		IObservableValue selfRowObserveValue = BeansObservables.observeValue(
				self, PROP_ROW);
		bindingContext.bindValue(textRowObserveTextObserveWidget,
				selfRowObserveValue, null, null);
		//
		return bindingContext;
	}

	public Label get_lblRunToRow() {
		return _lblRunToRow;
	}
}
