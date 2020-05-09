package com.marketforecastgroup.dfsa.ui.editors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.ui.widgets.EmptyTextFieldPainter;
import com.mfg.utils.Utils;

public class SymbolEditorPage extends FormPage {

	private final SymbolEditorPage _self = this;
	private DfsSymbolStatus status;
	private IDFS _dfs;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public SymbolEditorPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public SymbolEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Symbol");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		composite_1 = managedForm.getToolkit().createComposite(
				managedForm.getForm().getBody(), SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		managedForm.getToolkit().createLabel(composite_1, "Prefix", SWT.NONE);

		text = managedForm.getToolkit().createText(composite_1, "New Text",
				SWT.READ_ONLY);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		text.setText("");

		managedForm.getToolkit()
				.createLabel(composite_1, "Tick Size", SWT.NONE);

		txtTickSize = managedForm.getToolkit().createText(composite_1,
				"New Text", SWT.READ_ONLY);
		txtTickSize.setText("");
		txtTickSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		_label = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(_label, true, true);
		_label.setText("Tick Value");

		_text = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		managedForm.getToolkit().adapt(_text, true, true);

		_label_1 = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(_label_1, true, true);
		_label_1.setText("Currency");

		_text_1 = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		managedForm.getToolkit().adapt(_text_1, true, true);

		_label_2 = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(_label_2, true, true);
		_label_2.setText("Type");

		_text_2 = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		managedForm.getToolkit().adapt(_text_2, true, true);

		_label_3 = new Label(composite_1, SWT.NONE);
		_label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().adapt(_label_3, true, true);
		_label_3.setText("Time Zone");

		_text_3 = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		managedForm.getToolkit().adapt(_text_3, true, true);

		composite = managedForm.getToolkit().createComposite(
				managedForm.getForm().getBody(), SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		managedForm.getToolkit().paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));

		afterCreateWidgets();
		initDataBindings();
	}

	public DfsSymbol getSymbol() {
		return ((SymbolEditorInput) getEditorInput()).getSymbol();
	}

	public void setDFS(IDFS dfs) {
		_dfs = dfs;
	}

	private void afterCreateWidgets() {
		txtTickSize.addPaintListener(new EmptyTextFieldPainter(txtTickSize,
				"(Automatically Computed)"));

		try {
			DfsSymbolStatus status1 = getStatus();
			List<BarType> list = new ArrayList<>();
			for (BarType t : BarType.values()) {
				if (status1.continuousStats._map.containsKey(t)) {
					list.add(t);
				}
			}
		} catch (DFSException e) {
			e.printStackTrace();
		}

		if (getSymbol().tick < 0) {
			throw new IllegalArgumentException(); // Lino: it MUST not happen
													// now
		}
		txtTickSize.setText(Utils.scaledNumberToString2(getSymbol().tick,
				getSymbol().scale));
	}

	public String getPrefix() {
		return getSymbol().prefix;
	}

	public String getCurrency() {
		return getSymbol().currency;
	}

	public String getType() {
		String type = getSymbol().type;
		String name = DfsSymbol.TYPES__DEF_NAME_MAP.get(type);
		return name;
	}

	public String getTickValue() {
		double value = getSymbol().tickValue;
		return Double.toString(value / Math.pow(10, getSymbol().scale));
	}

	public String getTimeZone() {
		return getSymbol().timeZone;
	}

	private DfsSymbolStatus getStatus() throws DFSException {
		if (status == null) {
			status = _dfs.getStatusForSymbol(getSymbol().prefix);
		}
		return status;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private Text text;
	private Composite composite;
	private Composite composite_1;
	private Text txtTickSize;
	private Label _label;
	private Label _label_1;
	private Label _label_2;
	private Text _text;
	private Text _text_1;
	private Text _text_2;
	private Label _label_3;
	private Text _text_3;

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
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(text);
		IObservableValue prefixSelfObserveValue = BeanProperties
				.value("prefix").observe(_self);
		bindingContext.bindValue(observeTextTextObserveWidget,
				prefixSelfObserveValue, null, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(
				SWT.Modify).observe(_text);
		IObservableValue tickValue_selfObserveValue = BeanProperties.value(
				"tickValue").observe(_self);
		bindingContext.bindValue(observeText_textObserveWidget,
				tickValue_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_1ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_1);
		IObservableValue currency_selfObserveValue = BeanProperties.value(
				"currency").observe(_self);
		bindingContext.bindValue(observeText_text_1ObserveWidget,
				currency_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_2ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_2);
		IObservableValue type_selfObserveValue = BeanProperties.value("type")
				.observe(_self);
		bindingContext.bindValue(observeText_text_2ObserveWidget,
				type_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_3ObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(_text_3);
		IObservableValue timeZone_selfObserveValue = BeanProperties.value(
				"timeZone").observe(_self);
		bindingContext.bindValue(observeText_text_3ObserveWidget,
				timeZone_selfObserveValue, null, null);
		//
		return bindingContext;
	}
}
