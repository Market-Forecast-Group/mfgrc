package com.mfg.chart.ui.settings.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mfg.opengl.IGLConstants;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.Chart.TimeOfTheDayLabelMode;
import com.mfg.chart.backend.opengl.Chart.TimeOfTheDaySettings;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.TimeOfTheDay;
import com.mfg.chart.ui.interactive.GUIProfileAdapter;
import com.mfg.chart.ui.settings.ChartProfilesComposite;

public class TimesOfTheDayEditor extends Composite implements
		IChartSettingsEditor {
	private static class Sorter extends ViewerSorter {
		public Sorter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			TimeOfTheDay t1 = (TimeOfTheDay) e1;
			TimeOfTheDay t2 = (TimeOfTheDay) e2;
			int _100000 = 100000;
			return Integer.valueOf(t1.getHour() * _100000 + t1.getMinutes())
					.compareTo(
							Integer.valueOf(t2.getHour() * _100000
									+ t2.getMinutes()));
		}
	}

	Chart _chart;

	protected Object result;
	Table table;
	private List<TimeOfTheDay> _list;
	TableViewer _tableViewer;
	Button btnRemove;
	private Button btnRemoveAll;
	Button btnChangeColor;
	private Spinner _maxNumberSpinner;
	private ComboViewer _labelModeCombo;
	private ChartProfilesComposite _chartProfilesComposite;

	private GUIProfileAdapter _adapter;

	public TimesOfTheDayEditor(Composite parent, int style) {
		super(parent, style);
		createContent();

	}

	void createContent() {
		setLayout(new GridLayout(2, false));

		Composite composite_3 = new Composite(this, SWT.BORDER);
		composite_3.setLayout(new GridLayout(3, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));

		Label lblMaximumNumberOf = new Label(composite_3, SWT.NONE);
		lblMaximumNumberOf.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		lblMaximumNumberOf.setText("Max Number of Days to Show");

		_maxNumberSpinner = new Spinner(composite_3, SWT.BORDER);
		_maxNumberSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));

		Label lblShowLabels = new Label(composite_3, SWT.NONE);
		lblShowLabels.setText("Show Labels");

		_labelModeCombo = new ComboViewer(composite_3, SWT.READ_ONLY);
		Combo combo = _labelModeCombo.getCombo();
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1);
		gd_combo.minimumWidth = -1;
		gd_combo.widthHint = 250;
		combo.setLayoutData(gd_combo);
		_labelModeCombo.setLabelProvider(new LabelProvider());
		_labelModeCombo.setContentProvider(new ArrayContentProvider());

		_tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewer.setSorter(new Sorter());
		table = _tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 241;
		table.setLayoutData(gd_table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				return (time.getHour() < 10 ? "0" : "") + time.getHour();
			}
		});
		tableViewerColumn.setEditingSupport(new EditingSupport(_tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				TextCellEditor editor = new TextCellEditor(table);
				editor.setValue(time.getHour() + "");
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				return time.getHour() + "";
			}

			@Override
			protected void setValue(Object element, Object value) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				int hh = Integer.parseInt((String) value);
				time.setHour(Math.min(23, Math.max(0, hh)));
				_tableViewer.refresh();
			}
		});
		TableColumn tblclmnHour = tableViewerColumn.getColumn();
		tblclmnHour.setWidth(62);
		tblclmnHour.setText("Hour");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_2.setEditingSupport(new EditingSupport(_tableViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				TextCellEditor editor = new TextCellEditor(table);
				editor.setValue(time.getMinutes() + "");
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				return time.getMinutes() + "";
			}

			@Override
			protected void setValue(Object element, Object value) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				int mm = Integer.parseInt((String) value);
				time.setMinutes(Math.min(59, Math.max(0, mm)));
				_tableViewer.refresh();
			}
		});
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				return (time.getMinutes() < 10 ? "0" : "") + time.getMinutes();
			}
		});
		TableColumn tblclmnMinutes = tableViewerColumn_2.getColumn();
		tblclmnMinutes.setWidth(98);
		tblclmnMinutes.setText("Minutes");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return "Color";
			}

			@Override
			public Color getBackground(Object element) {
				TimeOfTheDay time = (TimeOfTheDay) element;
				float[] glColor = time.getColor();
				int r = (int) (glColor[0] * 255);
				int g = (int) (glColor[1] * 255);
				int b = (int) (glColor[2] * 255);
				final Color c = new Color(Display.getDefault(),
						new RGB(r, g, b));
				table.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent arg0) {
						c.dispose();
					}
				});
				return c;
			}

			@Override
			public Color getForeground(Object element) {
				return getBackground(element);
			}
		});
		TableColumn tblclmnColor = tableViewerColumn_1.getColumn();
		tblclmnColor.setWidth(77);
		tblclmnColor.setText("Color");
		_tableViewer.setContentProvider(new ArrayContentProvider());

		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, true));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));

		Button btnAdd = new Button(composite_2, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addPressed();
			}
		});
		GridData gd_btnAdd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1);
		gd_btnAdd.widthHint = 130;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText("Add");

		btnChangeColor = new Button(composite_2, SWT.NONE);
		btnChangeColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				changeColorPressed();
			}
		});
		btnChangeColor.setEnabled(false);
		btnChangeColor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnChangeColor.setText("Change Color");

		btnRemove = new Button(composite_2, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removePressed();
			}
		});
		btnRemove.setEnabled(false);
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnRemove.setText("Remove");

		btnRemoveAll = new Button(composite_2, SWT.NONE);
		btnRemoveAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeAllPressed();
			}
		});
		btnRemoveAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnRemoveAll.setText("Remove All");

		_chartProfilesComposite = new ChartProfilesComposite(this, SWT.NONE);
		_chartProfilesComposite.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
	}

	public TimesOfTheDayEditor(Composite parent, Chart chart) {
		super(parent, SWT.None);
		_chart = chart;

		createContent();

		_list = new ArrayList<>();
		_tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						boolean selected = !event.getSelection().isEmpty();
						btnRemove.setEnabled(selected);
						btnChangeColor.setEnabled(selected);
					}
				});
		_labelModeCombo.setInput(TimeOfTheDayLabelMode.values());
		_adapter = new GUIProfileAdapter(_chartProfilesComposite,
				_chart.getTimeOfTheDayProfiledObject()) {

			@Override
			public void updateUI_fromToolSettings() {
				Profile p = new Profile();
				TimeOfTheDaySettings s = _chart.getTimeOfTheDaySettings();
				s.fillProfile(p);
				updateUI_fromProfile(p);
			}

			@Override
			protected void updateUI_fromProfile(Profile profile) {
				TimeOfTheDaySettings s = new TimeOfTheDaySettings();
				s.updateFromProfile(profile);
				updateUI_fromSettings(s);
			}

			@Override
			protected void updateProfile_fromUI(Profile profile) {
				TimeOfTheDaySettings s = new TimeOfTheDaySettings();
				updateSettings_fromUI(s);
				s.fillProfile(profile);
			}

			@Override
			protected void updateModel_fromUI() {
				//
			}
		};
		_adapter.updateUI_fromToolSettings();
	}

	protected void changeColorPressed() {
		TimeOfTheDay time = (TimeOfTheDay) ((StructuredSelection) _tableViewer
				.getSelection()).getFirstElement();

		ColorDialog dialog = new ColorDialog(getShell());
		float[] timeColor = time.getColor();
		dialog.setRGB(new RGB((int) (timeColor[0] * 255),
				(int) (timeColor[1] * 255), (int) (timeColor[2] * 255)));
		RGB rgb = dialog.open();
		if (rgb != null) {
			float[] color = { rgb.red / 255f, rgb.green / 255f,
					rgb.blue / 255f, 0.8f };
			time.setColor(color);
			_tableViewer.refresh();
		}
	}

	protected void removeAllPressed() {
		_list.clear();
		_tableViewer.refresh();
	}

	protected void removePressed() {
		_list.remove(((StructuredSelection) _tableViewer.getSelection())
				.getFirstElement());
		_tableViewer.refresh();
	}

	void addPressed() {
		float[] color = IGLConstants.COLORS[(int) (Math.random()
				* IGLConstants.COLORS.length - 2) + 1];
		TimeOfTheDay time = new TimeOfTheDay(8, 0, color);
		_list.add(time);
		_tableViewer.refresh();
	}

	void updateUI_fromSettings(TimeOfTheDaySettings s) {
		_maxNumberSpinner.setSelection(s.getMaxNumberOfTimesOfTheDay());
		_labelModeCombo.setSelection(new StructuredSelection(s
				.getTimeOfTheDayLabelMode()));
		_list = new ArrayList<>(Arrays.asList(s.getTimesOfTheDay()));
		_tableViewer.setInput(_list);
	}

	void updateSettings_fromUI(TimeOfTheDaySettings s) {
		s.setMaxNumberOfTimesOfTheDay(_maxNumberSpinner.getSelection());
		s.setTimeOfTheDayLabelMode((TimeOfTheDayLabelMode) ((StructuredSelection) _labelModeCombo
				.getSelection()).getFirstElement());
		s.setTimesOfTheDay(_list.toArray(new TimeOfTheDay[_list.size()]));
	}

	@Override
	public void applyChanges() {
		TimeOfTheDaySettings s = new TimeOfTheDaySettings();
		updateSettings_fromUI(s);
		_chart.setTimeOfTheDaySettings(s);
		_chart.getTimeOfTheDayProfiledObject().setProfile(
				_adapter.getSelectedProfile());
	}

	@Override
	public Composite getUI() {
		return this;
	}

}
