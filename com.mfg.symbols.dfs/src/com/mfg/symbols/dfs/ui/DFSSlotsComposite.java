package com.mfg.symbols.dfs.ui;

import static java.lang.System.out;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.marketforecastgroup.dfsa.ui.DFSSymbolsLabelProvider;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsEmptyDatabaseException;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.RequestMode;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.Slot;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.IAppLogger;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.configurations.DFSProfile;
import com.mfg.symbols.dfs.configurations.DFSProfile.SlotInfo;
import com.mfg.symbols.dfs.persistence.DFSProfileStorage;
import com.mfg.symbols.dfs.persistence.DFSStorage;
import com.mfg.symbols.dfs.ui.editors.DFSEditor;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.utils.DataBindingUtils;
import com.mfg.utils.DataBindingUtils.QuickFix;
import com.mfg.utils.DataBindingUtils.QuickFixSupport;
import com.mfg.utils.ui.bindings.LongToDateStringConverter;

public class DFSSlotsComposite extends Composite implements
		IDatabaseChangeListener {
	private Binding _numberOfUnitsBinding;
	private static final String PROP_NUMBER_OF_UNITS = "numberOfUnits";
	private Binding _enableNumOfUnitsBinding;
	private Binding _barTypeBinding;
	private Binding _availableDaysBinding;
	Binding _numOfDaysBinding;
	Binding _availableBarsBinding;
	Binding _numOfBarsBinding;
	private static final String PROP_NUM_OF_BARS = "numOfBars";
	private Binding _endDateBinding;
	private Binding _startDateBinding;
	private static final String PROP_SELECTED_SLOT_INTERVAL = "selectedSlotInterval";
	private Binding _endDateBlockBinding;
	private Binding _startDateBlockBinding;
	private static final String PROP_SELECTED_SLOT = "selectedSlot";

	DataBindingContext m_bindingContext;

	private static final String PROP_SLOTS = "slots";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_6;
	private Text text_7;

	private List<Slot> _slots;
	private final DFSSlotsComposite self = this;
	static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	Slot _selectedSlot;
	IDFS _dfs;
	String _localSymbol;
	private AvailableIntervalBean _selectedSlotInterval;
	private DFSStorage _storage;
	DFSProfileStorage profileStorage;
	DFSHistoricalDataInfo _historicalDataInfo;
	private DFSHistoricalDataInfo _info;
	private PropertyChangeListener _requestModeListener;
	IAppLogger _logger;
	DFSConfiguration _configuration;
	QuickFixSupport _fixSupport;
	NumberFormat _numFormat;
	MaturityStats _maturityStats;
	DFSEditor _editor;
	private boolean _firstTime = true;

	public class GapValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			Integer gap = (Integer) value;
			if (gap.intValue() <= 0) {
				return ValidationStatus.error("Invalid value (<= 0).");
			}
			return Status.OK_STATUS;
		}

	}

	public class NumberOfUnitsValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			if (_selectedSlot != null) {
				BarType type = _selectedSlot.getBarType();
				if (type != BarType.RANGE) {
					int max = type == BarType.MINUTE ? 240 : 30;
					@SuppressWarnings("boxing")
					// Object value
					int n = (int) value;
					if (n <= 0) {
						return ValidationStatus
								.error("The minimum bar width is 1");
					}
					if (n > max) {
						return ValidationStatus
								.error("The maximum bar width for the bar "
										+ type + " is " + max);
					}
				}
			}
			return Status.OK_STATUS;
		}

	}

	public class AvailableIntervalBean {
		private int _numBars;
		private long _startDate;
		private long _endDate;
		private long _days;
		private Slot _slot;

		public AvailableIntervalBean(Slot slot, DfsIntervalStats stats) {
			_numBars = stats.numBars;
			_startDate = stats.startDate;
			_endDate = stats.endDate;
			_days = TimeUnit.MILLISECONDS.toDays(_endDate - _startDate);
			this.setSlot(slot.clone());
		}

		public int getNumBars() {
			return _numBars;
		}

		public void setNumBars(int numBars) {
			this._numBars = numBars;
		}

		public long getStartDate() {
			return _startDate;
		}

		public void setStartDate(long startDate) {
			this._startDate = startDate;
		}

		public long getEndDate() {
			return _endDate;
		}

		public void setEndDate(long endDate) {
			this._endDate = endDate;
		}

		public long getDays() {
			return _days;
		}

		public void setDays(long days) {
			this._days = days;
		}

		private transient final PropertyChangeSupport _support = new PropertyChangeSupport(
				this);

		public void addPropertyChangeListener(PropertyChangeListener l) {
			_support.addPropertyChangeListener(l);
		}

		public void removePropertyChangeListener(PropertyChangeListener l) {
			_support.removePropertyChangeListener(l);
		}

		public void addPropertyChangeListener(String property,
				PropertyChangeListener l) {
			_support.addPropertyChangeListener(property, l);
		}

		public void removePropertyChangeListener(String property,
				PropertyChangeListener l) {
			_support.removePropertyChangeListener(property, l);
		}

		public void firePropertyChange(String property) {
			_support.firePropertyChange(property, true, false);
		}

		public int getNumOfBarsLimit() {
			try {
				int bars = 0;
				Slot slot = getSlot();
				BarType barType = slot.getBarType();
				long startTime = slot.getStartDate().getTime();
				long endTime = slot.getEndDate().getTime();
				int nunits = slot.getNumbeOfUnits();
				try {
					if (isStartDateBlock()) {
						bars = _dfs.getBarsBetween(_localSymbol, barType,
								nunits, startTime, _endDate);
					} else {
						bars = _dfs.getBarsBetween(_localSymbol, barType,
								nunits, _startDate, endTime);
					}
				} catch (DfsEmptyDatabaseException e) {
					// there is not bars
				}
				return bars;
			} catch (DFSException e) {
				handleError(e);
				return 0;
			}
		}

		public int getNumOfDaysLimit() {
			int days = 0;
			long startTime = getSlot().getStartDate().getTime();
			long endTime = getSlot().getEndDate().getTime();

			if (isStartDateBlock()) {
				days = (int) TimeUnit.MILLISECONDS.toDays(_endDate - startTime);
			} else {
				days = (int) TimeUnit.MILLISECONDS.toDays(endTime - _startDate);
			}
			return days;
		}

		public String getAvailableBarsMessage() {
			return _numFormat.format(getNumOfBarsLimit()) + " / "
					+ _numFormat.format(getNumBars());
		}

		public String getAvailableDaysMessage() {
			return _numFormat.format(getNumOfDaysLimit()) + " / "
					+ _numFormat.format(getDays());
		}

		public void update(AvailableIntervalBean other) {
			other.setDays(_days);
			other.setEndDate(_endDate);
			other.setStartDate(_startDate);
			other.setNumBars(_numBars);
		}

		public Slot getSlot() {
			return _slot;
		}

		public void setSlot(Slot slot) {
			_slot = slot;
		}
	}

	public abstract class AbstractDateValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			String str = (String) value;
			try {
				Date date = dateFormat.parse(str);
				return validateDate(date);
			} catch (ParseException e) {
				return ValidationStatus.error(e.getMessage());
			}
		}

		protected IStatus validateDate(Date date) {
			AvailableIntervalBean interval = getSelectedSlotInterval();
			if (date.getTime() < interval.getStartDate()) {
				return ValidationStatus
						.error("This date could not be set before "
								+ dateFormat.format(new Date(interval
										.getStartDate())));
			}

			if (date.getTime() > interval.getEndDate()) {
				return ValidationStatus
						.error("This date could not be set after "
								+ dateFormat.format(new Date(interval
										.getEndDate())));
			}

			return Status.OK_STATUS;
		}
	}

	public class StartDateValidator extends AbstractDateValidator {
		@Override
		protected IStatus validateDate(Date date) {
			IStatus status = super.validateDate(date);
			if (status.isOK() && _selectedSlot != null) {
				if (date.getTime() > _selectedSlot.getEndDate().getTime()) {
					return ValidationStatus
							.error("The start date is above the end date.");
				}
			}
			return status;
		}
	}

	public class EndDateValidator extends AbstractDateValidator {
		@Override
		protected IStatus validateDate(Date date) {
			IStatus status = super.validateDate(date);
			if (status.isOK() && _selectedSlot != null) {
				if (date.getTime() < _selectedSlot.getStartDate().getTime()) {
					return ValidationStatus
							.error("The end date is below the start date.");
				}
			}
			return status;
		}
	}

	public class NumOfBarsValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			_fixSupport.clear(_numOfBarsBinding);

			int bars = ((Integer) value).intValue();
			AvailableIntervalBean interval = getSelectedSlotInterval();
			int limit = interval.getNumOfBarsLimit();
			int full = interval.getNumBars();

			if (bars > limit) {
				String date = isStartDateBlock() ? "Start" : "End";
				String msg = "Relative to this " + date
						+ " date, there are only " + limit + " available bars.";

				if (bars > full) {
					msg += "\nThe max available number of bars are " + full
							+ ".";
				}

				msg += "\n\nClick on the error marker or press CTRL+1 to select an automatic fix.";

				if (limit != full) {
					_fixSupport
							.addFix(_numOfBarsBinding,
									createFix_relative_num_days(interval,
											limit, "bars"));
				}
				_fixSupport.addFix(_numOfBarsBinding,
						createFix_max_num_days(full, "bars"));

				return ValidationStatus.error(msg);
			}
			if (bars < 0) {
				return ValidationStatus.error("Negative value.");
			}
			return Status.OK_STATUS;
		}
	}

	public class NumOfDaysValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			_fixSupport.clear(_numOfDaysBinding);

			int days = ((Integer) value).intValue();
			final AvailableIntervalBean interval = getSelectedSlotInterval();
			int limit = interval.getNumOfDaysLimit();
			long full = interval.getDays();

			if (days > limit) {
				String date = isStartDateBlock() ? "Start" : "End";
				String msg = "Relative to this " + date
						+ " date, there are only " + limit + " available days.";

				if (days > full) {
					msg += "\nThe max available number of days are " + full
							+ ".";
				}

				msg += "\n\nClick on the error marker or press CTRL+1 to select an automatic fix.";

				if (limit != full) {
					_fixSupport
							.addFix(_numOfDaysBinding,
									createFix_relative_num_days(interval,
											limit, "days"));
				}
				_fixSupport.addFix(_numOfDaysBinding,
						createFix_max_num_days(full, "days"));

				return ValidationStatus.error(msg);
			}

			if (days < 0) {
				return ValidationStatus.error("Negative value.");
			}

			return Status.OK_STATUS;
		}
	}

	/**
	 * Temporal class to use with WB.
	 * 
	 * @author arian
	 * 
	 */
	public static class X implements IValidator {

		@Override
		public IStatus validate(Object value) {
			return null;
		}
	}

	public static class BarTypeToBooleanConverter extends Converter {

		public BarTypeToBooleanConverter() {
			super(BarType.class, boolean.class);
		}

		@Override
		public Object convert(Object fromObject) {
			return Boolean.valueOf(fromObject != BarType.RANGE);
		}
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DFSSlotsComposite(Composite parent, int style) {
		super(parent, style);

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(1, false));

		_tabFolder = new CTabFolder(this, SWT.BORDER);
		_tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		toolkit.adapt(_tabFolder);
		toolkit.paintBordersFor(_tabFolder);

		CTabItem tbtmInfo = new CTabItem(_tabFolder, SWT.NONE);
		tbtmInfo.setText("Slots");
		_tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		Composite grpOverview = new Composite(_tabFolder, SWT.NONE);
		tbtmInfo.setControl(grpOverview);
		grpOverview.setLayout(new GridLayout(1, false));
		toolkit.adapt(grpOverview);
		toolkit.paintBordersFor(grpOverview);

		Composite composite = new Composite(grpOverview, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1);
		gd_composite.minimumHeight = 120;
		gd_composite.heightHint = 120;
		composite.setLayoutData(gd_composite);
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		composite.setLayout(new FillLayout());

		_tableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION);
		Table table = _tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		toolkit.paintBordersFor(table);

		TableViewerColumn colBars = new TableViewerColumn(_tableViewer,
				SWT.NONE);

		TableColumn tblclmnOfBars = colBars.getColumn();
		tblclmnOfBars.setWidth(88);
		tblclmnOfBars.setText("# of Bars");

		TableViewerColumn colDays = new TableViewerColumn(_tableViewer,
				SWT.NONE);
		TableColumn tblclmnOfDays = colDays.getColumn();
		tblclmnOfDays.setWidth(96);
		tblclmnOfDays.setText("# of Days");

		TableViewerColumn colUnits = new TableViewerColumn(_tableViewer,
				SWT.NONE);
		TableColumn tblclmnOfUnits = colUnits.getColumn();
		tblclmnOfUnits.setWidth(94);
		tblclmnOfUnits.setText("# of Units");

		TableViewerColumn colType = new TableViewerColumn(_tableViewer,
				SWT.NONE);
		TableColumn tblclmnBarType = colType.getColumn();
		tblclmnBarType.setWidth(100);
		tblclmnBarType.setText("Bar Type");

		TableViewerColumn colScale = new TableViewerColumn(_tableViewer,
				SWT.NONE);
		TableColumn tblclmnScale = colScale.getColumn();
		tblclmnScale.setWidth(50);
		tblclmnScale.setText("Scale");

		TableViewerColumn colGap = new TableViewerColumn(_tableViewer, SWT.NONE);
		TableColumn tblclmnGap = colGap.getColumn();
		tblclmnGap.setWidth(50);
		tblclmnGap.setText("Gap");

		Composite composite_2 = toolkit.createComposite(grpOverview, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_2.setLayout(new GridLayout(7, false));
		toolkit.paintBordersFor(composite_2);

		profileLabel = toolkit.createLabel(composite_2, "(No Profile)",
				SWT.NONE);

		Hyperlink hprlnkSave = toolkit.createHyperlink(composite_2, "Save",
				SWT.NONE);
		hprlnkSave.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				saveProfile();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});
		toolkit.paintBordersFor(hprlnkSave);

		Hyperlink hprlnkOpen = toolkit.createHyperlink(composite_2, "Open",
				SWT.NONE);
		hprlnkOpen.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				openProfile();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});
		toolkit.paintBordersFor(hprlnkOpen);

		Hyperlink lnkDelete = toolkit.createHyperlink(composite_2, "Delete",
				SWT.NONE);
		lnkDelete.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				deleteProfile();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});
		toolkit.paintBordersFor(lnkDelete);

		Hyperlink hprlnkSetDefault = toolkit.createHyperlink(composite_2,
				"Set Default", SWT.NONE);
		hprlnkSetDefault.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setDefaultProfile();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});
		toolkit.paintBordersFor(hprlnkSetDefault);

		btnAdd = toolkit.createButton(composite_2, "", SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSlot();
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false,
				1, 1));
		btnAdd.setImage(ResourceManager.getPluginImage("org.eclipse.ui",
				"/icons/full/obj16/add_obj.gif"));

		btnRemove = toolkit.createButton(composite_2, "", SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSlots();
			}
		});
		btnRemove.setImage(ResourceManager.getPluginImage("org.eclipse.ui",
				"/icons/full/obj16/delete_obj.gif"));

		CTabItem tbtmRanges = new CTabItem(_tabFolder, SWT.NONE);
		tbtmRanges.setText("Graph");

		_canvas = new SlotsCanvas(_tabFolder);
		tbtmRanges.setControl(_canvas);
		toolkit.paintBordersFor(_canvas);

		Group grpDetails = new Group(this, SWT.NONE);
		grpDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		grpDetails.setLayout(new GridLayout(5, false));
		grpDetails.setText("Details");
		toolkit.adapt(grpDetails);
		toolkit.paintBordersFor(grpDetails);
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");

		dateFormatTodayLabel = new Label(grpDetails, SWT.NONE);
		dateFormatTodayLabel.setFont(SWTResourceManager.getFont(getFont()
				.getFontData()[0].getName(), 8, SWT.ITALIC));
		toolkit.adapt(dateFormatTodayLabel, true, true);
		dateFormatTodayLabel.setText("Date Format (Today): ");
		new Label(grpDetails, SWT.NONE).setText("");
		new Label(grpDetails, SWT.NONE).setText("");

		btnStartDate = new Button(grpDetails, SWT.RADIO);
		toolkit.adapt(btnStartDate, true, true);

		Label lblStartDate = new Label(grpDetails, SWT.NONE);
		toolkit.adapt(lblStartDate, true, true);
		lblStartDate.setText("Start Date");

		startDateText = toolkit.createText(grpDetails, "");
		startDateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Hyperlink lblMinDate = toolkit.createHyperlink(grpDetails, "Min. Date",
				SWT.NONE);
		lblMinDate.setToolTipText("Use the Min. Date as Start Date.");
		lblMinDate.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setMinDate();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});

		text = toolkit.createText(grpDetails, "New Text", SWT.READ_ONLY);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnEndDate = new Button(grpDetails, SWT.RADIO);
		toolkit.adapt(btnEndDate, true, true);

		Label lblEndDate = new Label(grpDetails, SWT.NONE);
		toolkit.adapt(lblEndDate, true, true);
		lblEndDate.setText("End Date");

		endDateText = toolkit.createText(grpDetails, "");
		endDateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		Hyperlink lblMaxDate = toolkit.createHyperlink(grpDetails, "Max. Date",
				SWT.NONE);
		lblMaxDate.setToolTipText("Use the Max. Date as End Date.");
		lblMaxDate.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setMaxDate();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});

		text_1 = toolkit.createText(grpDetails, "New Text", SWT.READ_ONLY);
		text_1.setText("");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));

		Label lblOfDays = toolkit
				.createLabel(grpDetails, "# of Days", SWT.NONE);
		lblOfDays.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		text_2 = toolkit.createText(grpDetails, "New Text", SWT.NONE);
		text_2.setText("");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Hyperlink lblTotalDays = toolkit.createHyperlink(grpDetails,
				"Available Days", SWT.NONE);
		lblTotalDays
				.setToolTipText("Use the relative Available Days as # of Days.");
		lblTotalDays.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setAvailableDays();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});

		text_6 = toolkit.createText(grpDetails, "New Text", SWT.READ_ONLY);
		text_6.setText("");
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Label lblOfBars = toolkit
				.createLabel(grpDetails, "# of Bars", SWT.NONE);
		lblOfBars.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		text_3 = toolkit.createText(grpDetails, "New Text", SWT.NONE);
		text_3.setText("");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Hyperlink lblTotalBars = toolkit.createHyperlink(grpDetails,
				"Available Bars", SWT.NONE);
		lblTotalBars
				.setToolTipText("Use the relative Available Bars as # of Bars.");
		lblTotalBars.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setAvailableBarsDate();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});

		text_7 = toolkit.createText(grpDetails, "New Text", SWT.READ_ONLY);
		text_7.setText("");
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Composite composite_1 = toolkit.createCompositeSeparator(grpDetails);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 5, 1);
		gd_composite_1.verticalIndent = 5;
		gd_composite_1.heightHint = 2;
		composite_1.setLayoutData(gd_composite_1);
		toolkit.paintBordersFor(composite_1);

		Label lblBarType = toolkit
				.createLabel(grpDetails, "Bar Type", SWT.NONE);
		lblBarType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		barTypeCombo = new ComboViewer(grpDetails, SWT.READ_ONLY);
		Combo combo = barTypeCombo.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.paintBordersFor(combo);
		barTypeCombo.setLabelProvider(new LabelProvider());
		barTypeCombo.setContentProvider(new ArrayContentProvider());

		toolkit.createLabel(grpDetails, "Scale", SWT.NONE);

		scaleCombo = new ComboViewer(grpDetails, SWT.READ_ONLY);
		Combo combo_1 = scaleCombo.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_1);
		scaleCombo.setLabelProvider(new LabelProvider() {
			@SuppressWarnings("boxing")
			// Object value.
			@Override
			public String getText(Object element) {
				return (int) element == 0 ? "Price" : element.toString();
			}
		});
		scaleCombo.setContentProvider(new ArrayContentProvider());

		Label lblOfUnits = toolkit.createLabel(grpDetails, "# of Units",
				SWT.NONE);
		lblOfUnits.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));

		text_4 = toolkit.createText(grpDetails, "New Text", SWT.NONE);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		text_4.setText("");

		toolkit.createLabel(grpDetails, "Gap", SWT.NONE);

		Composite composite_3 = new Composite(grpDetails, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		toolkit.adapt(composite_3);
		toolkit.paintBordersFor(composite_3);
		GridLayout gl_composite_3 = new GridLayout(3, false);
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		composite_3.setLayout(gl_composite_3);

		_text = new Text(composite_3, SWT.BORDER);
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.adapt(_text, true, true);

		Label label = new Label(composite_3, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		toolkit.adapt(label, true, true);
		label.setText("/");

		_text_1 = new Text(composite_3, SWT.BORDER);
		_text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(_text_1, true, true);

		Hyperlink hprlnkSetAllSlots = toolkit.createHyperlink(this,
				"Set all Slots Max. Date", SWT.NONE);
		hprlnkSetAllSlots.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setAllSlotsMaxDate();
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
		toolkit.paintBordersFor(hprlnkSetAllSlots);

		Hyperlink hprlnkRefresh = toolkit.createHyperlink(this, "Refresh",
				SWT.NONE);
		hprlnkRefresh.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				refresh();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// nothing
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				// nothing
			}
		});
		toolkit.paintBordersFor(hprlnkRefresh);

		afterCreateWidgets();

		m_bindingContext = initDataBindings();

		afterInitBinding();
	}

	protected void setAvailableBarsDate() {
		setNumOfBars(getSelectedSlotInterval().getNumOfBarsLimit());
	}

	protected void setAvailableDays() {
		setNumOfDays(getSelectedSlotInterval().getNumOfDaysLimit());
		_numOfDaysBinding.updateModelToTarget();
	}

	protected void setMaxDate() {
		Slot slot = getSelectedSlot();
		if (slot.isEndDateBlock()) {
			// date is fixed, so change it but keep the same number of days.
			long time = slot.getEndDate().getTime()
					- slot.getStartDate().getTime();
			long endDate = getSelectedSlotInterval().getEndDate();
			long startDate = endDate - time;
			setRealDates(new Date(startDate), new Date(endDate));
		} else {
			// is not fixed, so change the date and # of days.
			setRealEndDate(new Date(getSelectedSlotInterval().getEndDate()));
		}
	}

	private void setRealDates(Date startDate, Date endDate) {
		_selectedSlot.setStartDate(startDate);
		startDateText.setToolTipText(_selectedSlot.getStartDate().toString());

		_selectedSlot.setEndDate(endDate);
		endDateText.setToolTipText(_selectedSlot.getEndDate().toString());

		firePropertyChange("startDate");
		firePropertyChange("endDate");

		updateDateDependants(true);
		sortTable();
	}

	protected void setAllSlotsMaxDate() {
		for (Slot slot : getSlots()) {
			if (slot != _selectedSlot) {
				DfsIntervalStats interval = getDFSInterval(slot.getBarType());
				if (slot.isEndDateBlock()) {
					// date is fixed, so change it but keep the same number of
					// days.
					long time = slot.getEndDate().getTime()
							- slot.getStartDate().getTime();

					long endDate = interval.endDate;
					long startDate = endDate - time;
					slot.setStartDate(new Date(startDate));
					slot.setEndDate(new Date(endDate));
				} else {
					// is not fixed, so change the date and # of days.
					slot.setEndDate(new Date(interval.endDate));
				}
			}
		}
		setMaxDate();
	}

	protected void setMinDate() {
		if (getSelectedSlot().isStartDateBlock()) {
			// date is fixed, so change it but keep the same number of days.
			long time = getSelectedSlot().getEndDate().getTime()
					- getSelectedSlot().getStartDate().getTime();
			long startDate = getSelectedSlotInterval().getStartDate();
			long endDate = startDate + time;
			setRealStartDate(new Date(startDate));
			setRealEndDate(new Date(endDate));
		} else {
			// is not fixed, so change the date and # of days.
			setRealStartDate(new Date(getSelectedSlotInterval().getStartDate()));
		}

	}

	void refresh() {
		refreshEditor(_editor.getMaturityStats());
	}

	void refreshEditor(final MaturityStats newStats) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				setInfo(_configuration, _historicalDataInfo, newStats, _editor);
				m_bindingContext.updateTargets();
				updateFromRequestMode();
			}
		});
	}

	void addSlot() {
		@SuppressWarnings("unchecked")
		List<Slot> list = (List<Slot>) _tableViewer.getInput();
		Slot slot = _storage.createNewSlot(_configuration);
		list.add(slot);
		setSelectedSlot(slot);
		_info.getSlots().add(slot);
		updateAddButton();
		sortTable();
	}

	private void updateAddButton() {
		btnAdd.setEnabled(getSlots() != null && getSlots().size() < 6);
	}

	void removeSlots() {
		@SuppressWarnings("unchecked")
		List<Slot> list = (List<Slot>) _tableViewer.getInput();
		Object[] sel = ((StructuredSelection) _tableViewer.getSelection())
				.toArray();
		if (sel.length < list.size()) {
			List<Object> toDelete = Arrays.asList(sel);
			List<Object> toDelete2 = new ArrayList<>();
			for (Object obj : toDelete) {
				int i = toDelete.indexOf(obj);
				toDelete2.add(_info.getSlots().get(i));
			}
			list.removeAll(toDelete);
			_info.getSlots().removeAll(toDelete2);
		} else {
			MessageDialog.openError(getShell(), "Remove",
					"You cannot remove all the slots");
		}
		updateAddButton();
	}

	@Override
	public void dispose() {
		if (_localSymbol != null) {
			try {
				_dfs.unWatchDbSymbol(this, _localSymbol);
			} catch (DFSException e) {
				e.printStackTrace();
			}
		}
		if (_info != null) {
			_info.removePropertyChangeListener(
					DFSHistoricalDataInfo.PROP_REQUEST_MODE,
					_requestModeListener);
		}
		super.dispose();
	}

	private void afterInitBinding() {
		EditorUtils.registerBindingListenersToSetDirtyWorkspace(self,
				m_bindingContext);

		DataBindingUtils.addDataBindingModelsListener(new IChangeListener() {

			@Override
			public void handleChange(ChangeEvent event) {
				decorateRows();
				_canvas.redraw();
			}
		}, m_bindingContext);

		DataBindingUtils.decorateBindings(m_bindingContext);
		DataBindingUtils.addQuickFixSupport(
				_fixSupport = new QuickFixSupport(), m_bindingContext);
	}

	private void afterCreateWidgets() {
		// needed by WB to avoid NPE
		if (DFSPlugin.getDefault() == null) {
			return;
		}
		Assert.isTrue(DFSPlugin.getDefault().isDFSReady());
		_dfs = DFSPlugin.getDefault().getDataProvider().getDfs();
		_storage = DFSSymbolsPlugin.getDefault().getDFSStorage();

		_tabFolder.setSelection(_tabFolder.getItem(0));

		_numFormat = NumberFormat.getInstance(Locale.getDefault());

		_logger = LoggerPlugin.getDefault().getAppLogger(DFSStorage.LOGGER_ID,
				"Editor");
		dateFormatTodayLabel.setText(dateFormatTodayLabel.getText()
				+ dateFormat.format(new Date()));
		barTypeCombo.setInput(BarType.values());
		Object[] scales = new Object[] { Integer.valueOf(0),
				Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3),
				Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6),
				Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(9),
				Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12),
				Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15) };
		scaleCombo.setInput(scales);

		sortTable();

		updateAddButton();

		_tableViewer.getTable().addPaintListener(new PaintListener() {
			boolean visited = false;

			@Override
			public void paintControl(PaintEvent e) {
				if (_tableViewer.getTable().getItemCount() > 0 && !visited) {
					visited = true;
					decorateRows();
				}
			}
		});
	}

	private void sortTable() {
		if (_slots != null) {
			DFSStorage.sortSlots(_slots, _logger);
			_tableViewer.refresh();
			validateDatesHierarchy();
			decorateRows();
		}
	}

	void validateDatesHierarchy() {
		Color errorColor = getDisplay().getSystemColor(SWT.COLOR_RED);
		Color normalColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		Table table = _tableViewer.getTable();
		for (int i = 0; i < _slots.size() - 1; i++) {
			TableItem item = table.getItem(i);
			item.setForeground(normalColor);

			String error = "";
			Slot slot = _slots.get(i);
			Slot nextSlot = _slots.get(i + 1);

			long slotStart = slot.getStartDate().getTime();
			long nextSlotStart = nextSlot.getStartDate().getTime();
			long slotEnd = slot.getEndDate().getTime();
			long nextSlotEnd = nextSlot.getEndDate().getTime();

			if (slotStart >= nextSlotStart) {
				error = "Start >=";
			}

			if (slotEnd > nextSlotEnd) {
				error = (error.length() > 0 ? error + "," : "") + "End >";
			}

			long slotDays = slotEnd - slotStart;
			long nextSlotDays = nextSlotEnd - nextSlotStart;

			if (slotDays < nextSlotDays * 2) {
				error = (error.length() > 0 ? error + ", " : "") + "Few Days";
			}

			if (error.length() > 0) {
				item.setForeground(errorColor);
				item.setText(
						1,
						String.format("%s (%s)",
								Long.valueOf(slot.getNumberOfDays()), error));
				_logger.logComment(
						"Validation: slot %s is not contained [%s] in %s",
						slot, error, nextSlot);
			}
		}
	}

	QuickFix createFix_relative_num_days(final AvailableIntervalBean interval,
			int limit, String type) {
		return new QuickFix("Use the relative max number of " + type + " ("
				+ limit + ")") {

			@Override
			public void fix() {
				if (isStartDateBlock()) {
					setRealEndDate(new Date(interval.getEndDate()));
				} else {
					setRealStartDate(new Date(interval.getStartDate()));
				}
				_numOfDaysBinding.updateModelToTarget();
			}
		};
	}

	QuickFix createFix_max_num_days(long full, String type) {
		return new QuickFix("Use the absolute max number of " + type + " ("
				+ full + ")") {

			@Override
			public void fix() {
				setMinDate();
				setMaxDate();
			}

		};
	}

	void decorateRows() {
		Table table = _tableViewer.getTable();
		for (TableItem item : table.getItems()) {
			BarType barType = ((Slot) item.getData()).getBarType();
			DfsIntervalStats interval = _configuration.getInfo().getIntervals()
					.get(barType);
			Image img = DFSSymbolsLabelProvider
					.getMaturityStateImage(interval.state);
			item.setImage(3, img);
		}
	}

	void handleError(Exception e) {
		e.printStackTrace();
		MessageDialog.openError(getShell(), "Error", e.getClass()
				.getSimpleName() + ": " + e.getMessage());
		_logger.logComment("Error: %s:%s", e.getClass().getSimpleName(),
				e.getMessage());
	}

	public void setInfo(DFSConfiguration configuration,
			DFSHistoricalDataInfo info, MaturityStats maturityStats,
			DFSEditor editor) {
		_maturityStats = maturityStats;
		_configuration = configuration;
		_editor = editor;
		this._localSymbol = _configuration.getInfo().getSymbol()
				.getLocalSymbol();
		this._historicalDataInfo = info;
		this._info = info;
		_logger.setSource("Editor - " + _localSymbol);

		setSlots(info.getSlots());

		if (_selectedSlot != null) {
			startDateText.setToolTipText(_selectedSlot.getStartDate()
					.toString());
			endDateText.setToolTipText(_selectedSlot.getEndDate().toString());
		}

		initProfile();
		updateAddButton();

		_requestModeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateFromRequestMode();
			}
		};
		_info.addPropertyChangeListener(
				DFSHistoricalDataInfo.PROP_REQUEST_MODE, _requestModeListener);
		updateFromRequestMode();
		sortTable();

		_canvas.setModel(new RequestLayersModel(_slots, _maturityStats._map));

		if (_firstTime) {
			_firstTime = false;
			try {
				_dfs.watchDbSymbol(this, _localSymbol);
			} catch (DFSException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void updateMaturityStats(MaturityStats maturityStats) {
		_maturityStats = maturityStats;
		updateFromRequestMode();
		_canvas.setModel(new RequestLayersModel(_slots, _maturityStats._map));
	}

	void updateFromRequestMode() {
		boolean changed = false;
		List<Slot> slots = getSlots();
		RequestMode mode = _info.getRequestMode();

		endDateText.setEditable(mode == RequestMode.DATABASE);
		btnStartDate.setEnabled(mode == RequestMode.DATABASE);

		if (slots == null) {
			return;
		}

		if (mode == RequestMode.MIXED) {
			_logger.logComment("Update slots for Request Mode %s", mode);

			for (Slot slot : slots) {
				Slot origSlot = slot.clone();
				BarType type = slot.getBarType();
				DfsIntervalStats interval = getDFSInterval(type);
				long days = 0;
				long maxDays = interval.endDate - interval.startDate;
				long numDays = slot.getEndDate().getTime()
						- slot.getStartDate().getTime();

				slot.setEndDate(new Date(interval.endDate));
				slot.setStartDateBlock(false);
				slot.setEndDateBlock(true);

				if (maxDays < numDays) {
					_logger.setSource(slot.getBarType().name());
					_logger.logComment(
							"The request number of days is bigger (%s) than the available number of days (%s)",
							Long.valueOf(TimeUnit.MILLISECONDS.toDays(numDays)),
							Long.valueOf(TimeUnit.MILLISECONDS.toDays(maxDays)));
					days = maxDays;
				} else {
					days = numDays;
				}

				long startDate = interval.endDate - days;
				slot.setStartDate(new Date(startDate));

				boolean slotChanged = origSlot.getStartDate().getTime() != slot
						.getStartDate().getTime()
						|| origSlot.getEndDate().getTime() != slot.getEndDate()
								.getTime()
						&& origSlot.getNumberOfBars() != slot.getNumberOfBars();
				if (slotChanged) {
					changed = true;
					_logger.setSource(type + " - " + _localSymbol + " - Editor");
					_logger.logComment("Change slot %s --> %s", origSlot, slot);
				}

				_logger.setSource(_localSymbol + " - Editor");
			}
			_startDateBinding.updateModelToTarget();
			_endDateBinding.updateModelToTarget();
			_numOfDaysBinding.updateModelToTarget();
			_availableDaysBinding.updateModelToTarget();
			_numOfBarsBinding.updateModelToTarget();
			_availableBarsBinding.updateModelToTarget();
			_startDateBlockBinding.updateModelToTarget();
			_endDateBlockBinding.updateModelToTarget();
			_tableViewer.refresh();
			_logger.setSource(_localSymbol + " - Editor");
			if (!changed) {
				_logger.logComment("No slots changed");
			}
		} else {
			_endDateBinding.updateModelToTarget();
		}

		validateDatesHierarchy();
	}

	public void setSelectedSlot(Slot selectedSlot) {
		Slot old = this._selectedSlot;
		this._selectedSlot = selectedSlot;

		if (selectedSlot != null) {
			BarType barType = selectedSlot.getBarType();
			DfsIntervalStats dfsInterval = getDFSInterval(barType);
			AvailableIntervalBean intervalBean = new AvailableIntervalBean(
					selectedSlot, dfsInterval);
			setSelectedSlotInterval(intervalBean);
		}

		firePropertyChange(PROP_SELECTED_SLOT, old, selectedSlot);

		_startDateBlockBinding.updateModelToTarget();
		_endDateBlockBinding.updateModelToTarget();
		_startDateBinding.updateModelToTarget();
		_endDateBinding.updateModelToTarget();
		_numOfBarsBinding.updateModelToTarget();
		_numOfDaysBinding.updateModelToTarget();
		_barTypeBinding.updateModelToTarget();
		_enableNumOfUnitsBinding.updateModelToTarget();
		_numberOfUnitsBinding.updateModelToTarget();
	}

	private DfsIntervalStats getDFSInterval(BarType barType) {
		return _maturityStats._map.get(barType);
	}

	public Slot getSelectedSlot() {
		return _selectedSlot;
	}

	public AvailableIntervalBean getSelectedSlotInterval() {
		return _selectedSlotInterval;
	}

	public void setSelectedSlotInterval(
			AvailableIntervalBean selectedSlotInterval) {
		AvailableIntervalBean old = this._selectedSlotInterval;
		this._selectedSlotInterval = selectedSlotInterval;
		firePropertyChange(PROP_SELECTED_SLOT_INTERVAL, old,
				selectedSlotInterval);
	}

	public List<Slot> getSlots() {
		return _slots;
	}

	public void setSlots(List<Slot> slots) {
		firePropertyChange(PROP_SLOTS, this._slots, this._slots = slots);
		if (slots.isEmpty()) {
			setSelectedSlot(null);
		} else {
			setSelectedSlot(slots.get(0));
		}
	}

	public boolean isStartDateBlock() {
		return _selectedSlot == null ? false : _selectedSlot.isStartDateBlock();
	}

	public void setStartDateBlock(boolean startDateBlock) {
		if (_selectedSlot != null) {
			_selectedSlot.setStartDateBlock(startDateBlock);
			_selectedSlot.setEndDateBlock(!startDateBlock);
			firePropertyChange("startDateBlock",
					Boolean.valueOf(!startDateBlock),
					Boolean.valueOf(startDateBlock));

			getSelectedSlotInterval().setSlot(_selectedSlot.clone());
			_availableBarsBinding.updateModelToTarget();
			_availableDaysBinding.updateModelToTarget();
			_numOfBarsBinding.validateTargetToModel();
			_numOfDaysBinding.validateTargetToModel();
		}
	}

	public boolean isEndDateBlock() {
		return _selectedSlot == null ? false : _selectedSlot.isEndDateBlock();
	}

	public void setEndDateBlock(boolean endDateBlock) {
		if (_selectedSlot != null) {
			_selectedSlot.setEndDateBlock(endDateBlock);
			_selectedSlot.setStartDateBlock(!endDateBlock);
			firePropertyChange("endDateBlock", Boolean.valueOf(!endDateBlock),
					Boolean.valueOf(endDateBlock));

			getSelectedSlotInterval().setSlot(_selectedSlot.clone());
			_availableBarsBinding.updateModelToTarget();
			_availableDaysBinding.updateModelToTarget();
		}
	}

	public String getStartDate() {
		return _selectedSlot == null ? null : dateFormat.format(_selectedSlot
				.getStartDate());
	}

	public void setStartDate(String startDate) {
		if (_selectedSlot != null) {
			try {
				Date newDate = dateFormat.parse(startDate);
				setRealStartDate(newDate);
			} catch (ParseException e) {
				handleError(e);
			}
		}
	}

	void setRealStartDate(Date date) {
		_selectedSlot.setStartDate(date);
		startDateText.setToolTipText(_selectedSlot.getStartDate().toString());
		firePropertyChange("startDate");

		updateDateDependants(true);
		sortTable();
	}

	public String getEndDate() {
		if (_historicalDataInfo != null
				&& _historicalDataInfo.getRequestMode() == RequestMode.MIXED) {
			return "Real-Time";
		}
		return _selectedSlot == null ? null : dateFormat.format(_selectedSlot
				.getEndDate());
	}

	public void setEndDate(String startDate) {
		if (_selectedSlot != null) {
			try {
				Date date = dateFormat.parse(startDate);
				setRealEndDate(date);
			} catch (ParseException e) {
				handleError(e);
			}
		}
	}

	void setRealEndDate(Date date) {
		_selectedSlot.setEndDate(date);
		endDateText.setToolTipText(_selectedSlot.getEndDate().toString());
		firePropertyChange("endDate");

		updateDateDependants(true);
		sortTable();
	}

	private void updateDateDependants(boolean updateNumOfDays) {
		int bars = 0;
		try {
			out.println("Start DFS.getBarsBetween");
			long t = System.currentTimeMillis();

			bars = _dfs.getBarsBetween(_localSymbol,
					_selectedSlot.getBarType(),
					_selectedSlot.getNumbeOfUnits(), _selectedSlot
							.getStartDate().getTime(), _selectedSlot
							.getEndDate().getTime());
			out.println("End: " + (System.currentTimeMillis() - t) + "ms");
		} catch (Exception e) {
			// there is not bars.
			e.printStackTrace();
		}
		_selectedSlot.setNumberOfBars(bars);
		getSelectedSlotInterval().setSlot(_selectedSlot.clone());
		_availableBarsBinding.updateModelToTarget();
		_availableDaysBinding.updateModelToTarget();
		_numOfBarsBinding.updateModelToTarget();
		if (updateNumOfDays) {
			_numOfDaysBinding.updateModelToTarget();
		}
		sortTable();
	}

	public int getNumOfBars() {
		return _selectedSlot == null ? 0 : _selectedSlot.getNumberOfBars();
	}

	public void setNumOfBars(int numOfBars) {
		if (_selectedSlot != null) {
			_selectedSlot.setNumberOfBars(numOfBars);
			firePropertyChange(PROP_NUM_OF_BARS);

			BarType barType = _selectedSlot.getBarType();
			try {
				if (isStartDateBlock()) {
					long endDate = _dfs.getDateAfterXBarsFrom(_localSymbol,
							barType, _selectedSlot.getNumbeOfUnits(),
							_selectedSlot.getStartDate().getTime(), numOfBars);
					_selectedSlot.setEndDate(new Date(endDate));
					_endDateBinding.updateModelToTarget();
				} else {
					long startDate = _dfs.getDateBeforeXBarsFrom(_localSymbol,
							barType, _selectedSlot.getNumbeOfUnits(),
							_selectedSlot.getEndDate().getTime(), numOfBars);
					_selectedSlot.setStartDate(new Date(startDate));
					_startDateBinding.updateModelToTarget();
				}
				_numOfDaysBinding.updateModelToTarget();
				_tableViewer.refresh();
			} catch (DFSException e) {
				handleError(e);
			}
		}
	}

	public int getNumOfDays() {
		if (_selectedSlot != null) {
			int days = (int) TimeUnit.MILLISECONDS.toDays(_selectedSlot
					.getEndDate().getTime()
					- _selectedSlot.getStartDate().getTime());
			return days;
		}
		return 0;
	}

	public void setNumOfDays(int numOfDays) {
		if (isStartDateBlock()) {
			long endTime = _selectedSlot.getStartDate().getTime()
					+ TimeUnit.DAYS.toMillis(numOfDays);
			_selectedSlot.setEndDate(new Date(endTime));
			_endDateBinding.updateModelToTarget();
		} else {
			long startTime = _selectedSlot.getEndDate().getTime()
					- TimeUnit.DAYS.toMillis(numOfDays);
			_selectedSlot.setStartDate(new Date(startTime));
			_startDateBinding.updateModelToTarget();
		}
		updateDateDependants(false);
	}

	public int getNumberOfUnits() {
		return _selectedSlot == null ? 0 : _selectedSlot.getNumbeOfUnits();
	}

	public void setNumberOfUnits(int numberOfUnits) {
		if (_selectedSlot != null) {
			try {
				_selectedSlot.setNumbeOfUnits(numberOfUnits);
				firePropertyChange(PROP_NUMBER_OF_UNITS);
				_numOfBarsBinding.updateModelToTarget();
				_numOfDaysBinding.updateModelToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public BarType getBarType() {
		return _selectedSlot == null ? null : _selectedSlot.getBarType();
	}

	public void setBarType(BarType barType) {
		if (_selectedSlot != null) {
			_selectedSlot.setBarType(barType);
			DfsIntervalStats interval = getDFSInterval(barType);
			_selectedSlot.setStartDate(new Date(interval.startDate));
			_selectedSlot.setEndDate(new Date(interval.endDate));
			_selectedSlot.setNumberOfBars(interval.numBars);
			setSelectedSlot(_selectedSlot);
			sortTable();
		}
	}

	private void fireTableChanged() {
		BusyIndicator.showWhile(getDisplay(), new Runnable() {

			@Override
			public void run() {
				_tableViewer.refresh();
				updateButtonsStatus();
				DFSSymbolsPlugin.getDefault().getDFSStorage()
						.fireStorageChanged();
			}
		});
	}

	void updateButtonsStatus() {
		List<Slot> slots = _historicalDataInfo.getSlots();
		btnRemove.setEnabled(!_tableViewer.getSelection().isEmpty()
				&& slots.size() > 1);
		out.println(btnRemove.isEnabled());
		updateAddButton();
	}

	private void initProfile() {
		profileStorage = DFSSymbolsPlugin.getDefault().getProfileStorage();

		UUID id = _historicalDataInfo.getProfileId();
		if (id == null) {
			setNoProfile();
		} else {
			DFSProfile profile = profileStorage.findById(id);
			if (profile == null) {
				setNoProfile();
			} else {
				// let's see if the profile matches
				if (profile.sameOf(_historicalDataInfo.getSlots())) {
					updateProfileName(profile.getName());
				} else {
					setNoProfile();
				}
			}
		}
	}

	private void setNoProfile() {
		_historicalDataInfo.setProfileId(null);
		updateProfileName("(No Profile)");
	}

	private void updateProfileName(String name) {
		profileLabel.setText(name);
		profileLabel.setToolTipText(name);
		profileLabel.getParent().layout();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	boolean openProfile() {
		if (profileStorage.getObjects().isEmpty()) {
			MessageDialog.openInformation(getShell(), "Open Profile",
					"There is not any profile available.");
		} else {
			ListDialog dialog = new ListDialog(getShell()) {
				@Override
				protected Control createContents(Composite parent) {
					Control c = super.createContents(parent);
					ViewerSorter sorter = new ViewerSorter() {
						@Override
						public int compare(Viewer viewer, Object e1, Object e2) {
							return ((DFSProfile) e1).getName().compareTo(
									((DFSProfile) e2).getName());
						}
					};
					getTableViewer().setSorter(sorter);
					return c;
				}
			};
			dialog.setTitle("Open Profile");
			dialog.setMessage("Select the profile.");
			dialog.setContentProvider(new ArrayContentProvider());
			dialog.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					return ((DFSProfile) element).getName()
							+ (element == profileStorage.getDefaultProfile() ? " (default)"
									: "");
				}
			});
			UUID id = _historicalDataInfo.getProfileId();
			if (id != null) {
				dialog.setInitialSelections(new Object[] { profileStorage
						.findById(id) });
			}
			dialog.setInput(profileStorage.getObjects());
			if (dialog.open() == Window.OK) {

				DFSProfile profile = (DFSProfile) dialog.getResult()[0];
				_historicalDataInfo.setProfileId(profile.getUUID());

				List<SlotInfo> newSlots = profile.getSlots();
				updateSlotsWithProfile(newSlots);
				List tableSlots = (List) _tableViewer.getInput();
				tableSlots.clear();
				tableSlots.addAll(_historicalDataInfo.getSlots());

				setSlots(_historicalDataInfo.getSlots());
				_tableViewer.setSelection(new StructuredSelection(
						getSelectedSlot()));

				updateProfileName(profile.getName());
				_canvas.redraw();

				fireTableChanged();
				updateButtonsStatus();
				return true;
			}
		}
		return false;
	}

	private void updateSlotsWithProfile(List<SlotInfo> profileSlots) {
		for (SlotInfo profileSlot : profileSlots) {
			BarType barType = profileSlot.getBarType();

			// look for the equivalent old slot, and update the dates to keep
			// the same # of days
			for (Slot slot : _historicalDataInfo.getSlots()) {
				if (slot.getBarType() == barType) {
					DfsIntervalStats interval = getDFSInterval(barType);

					long days = profileSlot.getNumberOfDays();
					Date endDate = new Date(interval.endDate);
					Date startDate = new Date(interval.endDate
							- TimeUnit.DAYS.toMillis(days));

					slot.setStartDateBlock_doNotNotify(false);
					slot.setEndDateBlock_doNotNotify(true);

					slot.setStartDate_doNotNotify(startDate);
					slot.setEndDate_doNotNotify(endDate);

					// compute the number of bars
					try {
						int bars = _dfs.getBarsBetween(_localSymbol, barType,
								slot.getNumbeOfUnits(), startDate.getTime(),
								endDate.getTime());
						slot.setNumberOfBars(bars);
					} catch (DFSException e) {
						e.printStackTrace();
						_logger.logComment("Error: " + e.getMessage());
						throw new RuntimeException(e);
					}
					break;
				}
			}

		}
	}

	protected void saveProfile() {
		InputDialog dialog = new InputDialog(getShell(), "Create New Profile",
				"Enter the name of the new profile:",
				profileStorage.createNewName("Profile",
						profileStorage.getObjects()), new IInputValidator() {

					@Override
					public String isValid(String newText) {
						return newText.trim().length() == 0 ? "Empty name is not allowed"
								: null;
					}
				});

		if (dialog.open() == Window.OK) {
			String name = dialog.getValue();
			if (profileStorage.containsName(name)) {
				for (DFSProfile profile : profileStorage.getObjects()) {
					if (profile.getName().equals(name)) {
						profile.updateFromSlots(_historicalDataInfo.getSlots());
						break;
					}
				}
			} else {
				DFSProfile profile = createProfile();
				profile.setName(name);
				profileStorage.add(profile);
				_historicalDataInfo.setProfileId(profile.getUUID());
			}
			updateProfileName(name);
			DFSSymbolsPlugin.getDefault().getDFSStorage().fireStorageChanged();
		}
	}

	protected void deleteProfile() {
		UUID id = _historicalDataInfo.getProfileId();
		if (id == null) {
			MessageDialog.openInformation(getShell(), "Delete Profile",
					"There is not any profile selected.");
		} else {
			boolean isDefaultSelected = false;
			DFSProfileStorage storage = DFSSymbolsPlugin.getDefault()
					.getProfileStorage();
			isDefaultSelected = storage.getDefaultProfile().getUUID()
					.equals(id);
			if (isDefaultSelected) {
				MessageDialog.openInformation(getShell(), "Delete Profile",
						"Cannot delete the default profile.");
			} else {
				boolean used = false;
				for (DFSConfiguration config : DFSSymbolsPlugin.getDefault()
						.getDFSStorage().getObjects()) {
					DFSHistoricalDataInfo info = (DFSHistoricalDataInfo) config
							.getInfo().getHistoricalDataInfo();
					UUID id2 = info.getProfileId();
					if (info != _historicalDataInfo && id2 != null
							&& id2.equals(id)) {
						used = true;
						break;
					}
					List<InputConfiguration> inputs = SymbolsPlugin
							.getDefault().getInputsStorage()
							.findBySymbolId(config.getUUID());
					for (InputConfiguration input : inputs) {
						info = (DFSHistoricalDataInfo) input.getInfo()
								.getHistoricalDataInfo();
						id2 = info.getProfileId();
						if (info != _historicalDataInfo && id2 != null
								&& id2.equals(id)) {
							used = true;
							break;
						}
					}
					if (used) {
						break;
					}
				}

				if (used) {
					MessageDialog
							.openInformation(getShell(), "Delete Profile",
									"The profile can not be deleted, it is used by other configurations");
					return;
				}

				DFSProfile profile = profileStorage.findById(id);
				try {
					if (MessageDialog.openConfirm(getShell(), "Delete Profile",
							"Do you want to delete the profile?")) {
						profileStorage.remove(profile);
						setNoProfile();
					}
				} catch (RemoveException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private DFSProfile createProfile() {
		DFSProfile profile = new DFSProfile();
		profile.setName(profileStorage.createNewName("Profile",
				profileStorage.getObjects()));
		profile.updateFromSlots(_historicalDataInfo.getSlots());
		return profile;
	}

	protected void setDefaultProfile() {
		UUID id = _historicalDataInfo.getProfileId();
		DFSProfile profile;
		if (id == null || (profile = profileStorage.findById(id)) == null) {
			MessageDialog.openInformation(getShell(), "Delete Profile",
					"There is not any profile selected.");
		} else {
			profileStorage.setDefaultProfile(profile);
		}
	}

	// bean support

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	TableViewer _tableViewer;
	private ComboViewer barTypeCombo;
	private ComboViewer scaleCombo;
	private Button btnStartDate;
	private Button btnEndDate;
	private Text startDateText;
	private Text endDateText;
	private Label dateFormatTodayLabel;
	private Label profileLabel;
	private Button btnAdd;
	private Button btnRemove;
	SlotsCanvas _canvas;
	private Text _text;
	private Text _text_1;
	private CTabFolder _tabFolder;

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

	public void firePropertyChange(String property, Object oldValue,
			Object newValue) {
		support.firePropertyChange(property, oldValue, newValue);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(
				listContentProvider.getKnownElements(), Slot.class,
				new String[] { "numberOfBarsString", "numberOfDaysString",
						"numbeOfUnits", "barType", "scaleName", "gapString" });
		_tableViewer.setLabelProvider(new ObservableMapLabelProvider(
				observeMaps));
		_tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList slotsSelfObserveList = BeanProperties.list("slots")
				.observe(self);
		_tableViewer.setInput(slotsSelfObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties
				.singleSelection().observe(scaleCombo);
		IObservableValue selectedSlotscaleSelfObserveValue = BeanProperties
				.value("selectedSlot.scale").observe(self);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1,
				selectedSlotscaleSelfObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionTableViewer = ViewerProperties
				.singleSelection().observe(_tableViewer);
		IObservableValue selectedSlotSelfObserveValue = BeanProperties.value(
				"selectedSlot").observe(self);
		bindingContext.bindValue(observeSingleSelectionTableViewer,
				selectedSlotSelfObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnStartDateObserveWidget = WidgetProperties
				.selection().observe(btnStartDate);
		IObservableValue startDateBlockSelfObserveValue = BeanProperties.value(
				"startDateBlock").observe(self);
		_startDateBlockBinding = bindingContext.bindValue(
				observeSelectionBtnStartDateObserveWidget,
				startDateBlockSelfObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnEndDateObserveWidget = WidgetProperties
				.selection().observe(btnEndDate);
		IObservableValue endDateBlockSelfObserveValue = BeanProperties.value(
				"endDateBlock").observe(self);
		_endDateBlockBinding = bindingContext.bindValue(
				observeSelectionBtnEndDateObserveWidget,
				endDateBlockSelfObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(text);
		IObservableValue selectedSlotIntervalstartDateSelfObserveValue = BeanProperties
				.value("selectedSlotInterval.startDate").observe(self);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new LongToDateStringConverter(dateFormat));
		bindingContext.bindValue(observeTextTextObserveWidget,
				selectedSlotIntervalstartDateSelfObserveValue, null, strategy);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_1);
		IObservableValue selectedSlotIntervalendDateSelfObserveValue = BeanProperties
				.value("selectedSlotInterval.endDate").observe(self);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new LongToDateStringConverter(dateFormat));
		bindingContext.bindValue(observeTextText_1ObserveWidget,
				selectedSlotIntervalendDateSelfObserveValue, null, strategy_1);
		//
		IObservableValue observeSelectionDateTimeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(startDateText);
		IObservableValue startDateSelfObserveValue = BeanProperties.value(
				"startDate").observe(self);
		UpdateValueStrategy strategy_4_targetToModel = new UpdateValueStrategy();
		strategy_4_targetToModel
				.setBeforeSetValidator(new StartDateValidator());
		_startDateBinding = bindingContext.bindValue(
				observeSelectionDateTimeObserveWidget,
				startDateSelfObserveValue, strategy_4_targetToModel, null);
		//
		IObservableValue observeSelectionDateTime_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(endDateText);
		IObservableValue endDateSelfObserveValue = BeanProperties.value(
				"endDate").observe(self);
		UpdateValueStrategy strategy_5_targetToModel = new UpdateValueStrategy();
		strategy_5_targetToModel.setBeforeSetValidator(new EndDateValidator());
		_endDateBinding = bindingContext.bindValue(
				observeSelectionDateTime_1ObserveWidget,
				endDateSelfObserveValue, strategy_5_targetToModel, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_3);
		IObservableValue numOfBarsSelfObserveValue = BeanProperties.value(
				"numOfBars").observe(self);
		UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
		strategy_4.setBeforeSetValidator(new NumOfBarsValidator());
		_numOfBarsBinding = bindingContext.bindValue(
				observeTextText_3ObserveWidget, numOfBarsSelfObserveValue,
				strategy_4, null);
		//
		IObservableValue observeTextText_7ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_7);
		IObservableValue selectedSlotIntervalavailableBarsMessageSelfObserveValue = BeanProperties
				.value("selectedSlotInterval.availableBarsMessage").observe(
						self);
		_availableBarsBinding = bindingContext.bindValue(
				observeTextText_7ObserveWidget,
				selectedSlotIntervalavailableBarsMessageSelfObserveValue, null,
				null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_2);
		IObservableValue numOfDaysSelfObserveValue = BeanProperties.value(
				"numOfDays").observe(self);
		UpdateValueStrategy strategy_6 = new UpdateValueStrategy();
		strategy_6.setBeforeSetValidator(new NumOfDaysValidator());
		_numOfDaysBinding = bindingContext.bindValue(
				observeTextText_2ObserveWidget, numOfDaysSelfObserveValue,
				strategy_6, null);
		//
		IObservableValue observeTextText_6ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_6);
		IObservableValue selectedSlotIntervalavailableDaysMessageSelfObserveValue = BeanProperties
				.value("selectedSlotInterval.availableDaysMessage").observe(
						self);
		UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
		strategy_5.setBeforeSetValidator(new NumOfDaysValidator());
		_availableDaysBinding = bindingContext.bindValue(
				observeTextText_6ObserveWidget,
				selectedSlotIntervalavailableDaysMessageSelfObserveValue,
				strategy_5, null);
		//
		IObservableValue observeSingleSelectionBarTypeCombo = ViewerProperties
				.singleSelection().observe(barTypeCombo);
		IObservableValue barTypeSelfObserveValue = BeanProperties.value(
				"barType").observe(self);
		_barTypeBinding = bindingContext.bindValue(
				observeSingleSelectionBarTypeCombo, barTypeSelfObserveValue,
				null, null);
		//
		IObservableValue observeEnabledText_4ObserveWidget = WidgetProperties
				.enabled().observe(text_4);
		UpdateValueStrategy strategy_7 = new UpdateValueStrategy();
		strategy_7.setConverter(new BarTypeToBooleanConverter());
		_enableNumOfUnitsBinding = bindingContext.bindValue(
				observeEnabledText_4ObserveWidget, barTypeSelfObserveValue,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
				strategy_7);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(text_4);
		IObservableValue numberOfUnitsSelfObserveValue = BeanProperties.value(
				"numberOfUnits").observe(self);
		UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
		strategy_2.setBeforeSetValidator(new NumberOfUnitsValidator());
		_numberOfUnitsBinding = bindingContext.bindValue(
				observeTextText_4ObserveWidget, numberOfUnitsSelfObserveValue,
				strategy_2, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_text);
		IObservableValue selectedSlotgap1SelfObserveValue = BeanProperties
				.value("selectedSlot.gap1").observe(self);
		bindingContext.bindValue(observeText_textObserveWidget,
				selectedSlotgap1SelfObserveValue, null, null);
		//
		IObservableValue observeText_text_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_1);
		IObservableValue selectedSlotgap2SelfObserveValue = BeanProperties
				.value("selectedSlot.gap2").observe(self);
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setBeforeSetValidator(new GapValidator());
		bindingContext.bindValue(observeText_text_1ObserveWidget,
				selectedSlotgap2SelfObserveValue, strategy_3, null);
		//
		return bindingContext;
	}

	@Override
	public void onSymbolChanged(String localSymbol, MaturityStats newStats) {
		Assert.isTrue(localSymbol.equals(_localSymbol));
		DFSSymbolsPlugin.getDefault().getDFSStorage()
				.updateMaturityStats(localSymbol, newStats);
		refreshEditor(newStats);
	}

}
