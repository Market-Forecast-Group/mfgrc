package com.mfg.symbols.dfs.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.chart.ui.actions.ShowObjectInChart;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dm.speedControl.SpeedComposite3;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.configurations.DFSConfigurationInfo;
import com.mfg.symbols.dfs.configurations.DFSSymbolData;
import com.mfg.symbols.dfs.ui.DFSHistoricalDataInfoComposite;
import com.mfg.symbols.dfs.ui.DFSSlotsComposite;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.symbols.ui.widgets.EmptyTickFieldPainter;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.utils.DataBindingUtils;

public class DFSEditorPage extends FormPage {
	// private IJobLogModelListener dataProviderListener;
	private DataBindingContext m_bindingContext;
	@SuppressWarnings("unused")
	private Table table;
	private Text dataProviderText;
	private Text localSymbolText;
	private Text tickSizeText;
	private final DFSEditorPage _self = this;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public DFSEditorPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 */
	public DFSEditorPage(FormEditor editor, String id, String title) {
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
		form.setText("DFS Configuration");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		body.setLayout(new FillLayout());

		_scrolledComposite = new ScrolledComposite(body, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		managedForm.getToolkit().adapt(_scrolledComposite);
		managedForm.getToolkit().paintBordersFor(_scrolledComposite);
		_scrolledComposite.setExpandHorizontal(true);
		_scrolledComposite.setExpandVertical(true);

		Composite content = toolkit.createComposite(_scrolledComposite);
		_scrolledComposite.setContent(content);
		managedForm.getToolkit().adapt(content);
		managedForm.getToolkit().paintBordersFor(content);
		content.setLayout(new GridLayout(2, false));

		Section sctnCommands = managedForm.getToolkit().createSection(content,
				ExpandableComposite.TITLE_BAR);
		sctnCommands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		managedForm.getToolkit().paintBordersFor(sctnCommands);
		sctnCommands.setText("Commands");

		commandsComposite = managedForm.getToolkit().createComposite(content,
				SWT.NONE);
		RowLayout rl_commandsComposite = new RowLayout(SWT.HORIZONTAL);
		rl_commandsComposite.fill = true;
		commandsComposite.setLayout(rl_commandsComposite);
		commandsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 2, 1));
		managedForm.getToolkit().paintBordersFor(commandsComposite);

		Section sctnSymbol = managedForm.getToolkit().createSection(content,
				ExpandableComposite.TITLE_BAR);
		sctnSymbol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnSymbol);
		sctnSymbol.setText("Symbol");

		Section sctnDataType = managedForm.getToolkit().createSection(content,
				ExpandableComposite.TITLE_BAR);
		sctnDataType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnDataType);
		sctnDataType.setText("Data Type");

		Composite composite_1 = managedForm.getToolkit().createComposite(
				content, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1));
		managedForm.getToolkit().paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		Label lblDataProvider = new Label(composite_1, SWT.NONE);
		lblDataProvider.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		managedForm.getToolkit().adapt(lblDataProvider, true, true);
		lblDataProvider.setText("Data Provider");

		dataProviderText = managedForm.getToolkit().createText(composite_1,
				"New Text", SWT.NONE);
		dataProviderText.setEditable(false);
		dataProviderText.setText("DFS");
		dataProviderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		managedForm.getToolkit().createLabel(composite_1, "Prefix", SWT.NONE);

		text = managedForm.getToolkit().createText(composite_1, "New Text",
				SWT.READ_ONLY);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblLocalSymbol = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(lblLocalSymbol, true, true);
		lblLocalSymbol.setText("Local Symbol");

		localSymbolText = managedForm.getToolkit().createText(composite_1,
				"New Text", SWT.READ_ONLY);
		localSymbolText.setText("");
		localSymbolText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblTickSize = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(lblTickSize, true, true);
		lblTickSize.setText("Tick Size");

		tickSizeText = managedForm.getToolkit().createText(composite_1,
				"New Text", SWT.READ_ONLY);
		tickSizeText.setText("");
		tickSizeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		
		Label lblTickValue = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(lblTickValue, true, true);
		lblTickValue.setText("Tick Value");
		
		_text = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(_text, true, true);
		
		Label lblCurrency = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(lblCurrency, true, true);
		lblCurrency.setText("Currency");
		
		_text_1 = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(_text_1, true, true);
		
		Label lblType = new Label(composite_1, SWT.NONE);
		managedForm.getToolkit().adapt(lblType, true, true);
		lblType.setText("Type");
		
		_text_2 = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY);
		_text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(_text_2, true, true);

		slotsComposite = new DFSSlotsComposite(content, SWT.NONE);
		slotsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				false, 1, 3));
		managedForm.getToolkit().adapt(slotsComposite);
		managedForm.getToolkit().paintBordersFor(slotsComposite);

		Section sctnHistoricalData = managedForm.getToolkit().createSection(
				content, ExpandableComposite.TITLE_BAR);
		sctnHistoricalData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnHistoricalData);
		sctnHistoricalData.setText("Historical Data");

		historicalDataInfoComposite = new DFSHistoricalDataInfoComposite(
				content, SWT.NONE);
		historicalDataInfoComposite.setLayoutData(new GridData(SWT.LEFT,
				SWT.TOP, false, false, 1, 1));
		managedForm.getToolkit().adapt(historicalDataInfoComposite);
		managedForm.getToolkit().paintBordersFor(historicalDataInfoComposite);

		afterCreateWidgets();
		m_bindingContext = initDataBindings();
		afterInitBindings();
	}

	private void afterInitBindings() {
		EditorUtils.registerBindingListenersToUpdateWorkspace(
				commandsComposite, DFSSymbolsPlugin.getDefault()
						.getProfileStorage(), m_bindingContext);
		DataBindingUtils.decorateBindings(m_bindingContext,
				historicalDataInfoComposite.getDataBindingContext());
	}

	static class LogClientInfo {
		int clientHandler;
		List<LogRequestInfo> requests;

		/**
		 * builds an empty information for this client
		 */
		public LogClientInfo() {
			requests = new ArrayList<>();
		}
	}

	static class LogRequestInfo {
		final String barType = "N/A";
		final int requested = 0;
		int received;
		int requestHandler;
		final String symbol = "N/A";
		protected final boolean isDay = false;
		protected long lastTime = 0;
		protected long lastBarTime;
		protected int maxIndex = 0;
		protected int numBarsPresent;

		// public LogRequestInfo(GuiRawDataSource bridgeRequest) {
		// symbol = bridgeRequest.getSymbol();
		// barType = bridgeRequest.getBarType();
		// requested = bridgeRequest.getNumBarRequested();
		// requestHandler = bridgeRequest.handleRequest;
		// isDay = bridgeRequest.isDayRequest();
		// received = bridgeRequest.getNumBarsReceived();
		// maxIndex = bridgeRequest.getMaxIndex() < 0 ? 0 : bridgeRequest
		// .getMaxIndex();
		// lastTime = bridgeRequest.lastTime;
		// lastBarTime = bridgeRequest.lastReceivedTime();
		// }
	}

	/**
	 * @param form
	 */
	private void addActions() {
		IToolBarManager manager = getManagedForm().getForm()
				.getToolBarManager();
		manager.add(new ShowObjectInChart(getConfiguration()));

		((AbstractSymbolEditor) getEditor()).addExtraActions(manager);

		manager.update(true);
		getManagedForm().getForm().getForm()
				.setToolBarVerticalAlignment(SWT.RIGHT);
	}

	private void afterCreateWidgets() {
		try {
			DFSConfigurationInfo info = getInfo();
			DFSHistoricalDataInfo historicalDataInfo = (DFSHistoricalDataInfo) info
					.getHistoricalDataInfo();

			// historicalDataSlotsComposite.setModel(historicalDataInfo);
			// historicalDataSlotsComposite.setConfiguration(getConfiguration());
			final DFSEditor editor = getEditor();
			MaturityStats maturityStats;
			maturityStats = editor.getMaturityStats();

			Assert.isNotNull(maturityStats);

			slotsComposite.setInfo(getConfiguration(), historicalDataInfo,
					maturityStats, editor);

			EmptyTickFieldPainter.addPainterToWidgets(tickSizeText);

			SpeedComposite3 speedControl = (SpeedComposite3) editor
					.createCommandsSection(commandsComposite, null, null);

			historicalDataInfoComposite.init(historicalDataInfo, speedControl);

			final PropertyChangeListener nameListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							editor.setPageText(getIndex(), getConfiguration()
									.getName());
						}
					});
				}
			};
			getConfiguration().addPropertyChangeListener(
					IStorageObject.PROP_NAME, nameListener);
			commandsComposite.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					getConfiguration().removePropertyChangeListener(
							IStorageObject.PROP_NAME, nameListener);
				}
			});

			addActions();

			Point p = _scrolledComposite.computeSize(-1, -1);
			_scrolledComposite.setMinHeight(p.y);
			_scrolledComposite.setMinWidth(p.x);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public DFSEditor getEditor() {
		return (DFSEditor) super.getEditor();
	}

	public DFSConfiguration getConfiguration() {
		return EditorUtils.getStorageObject(this);
	}

	public DFSSymbolData getSymbol() {
		return getInfo().getSymbol();
	}

	public DFSHistoricalDataInfo getHistoricalInfo() {
		return (DFSHistoricalDataInfo) getInfo().getHistoricalDataInfo();
	}

	public DFSConfigurationInfo getInfo() {
		return getConfiguration().getInfo();
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private Composite commandsComposite;
	private DFSHistoricalDataInfoComposite historicalDataInfoComposite;
	private Text text;
	private DFSSlotsComposite slotsComposite;
	private ScrolledComposite _scrolledComposite;
	private Text _text;
	private Text _text_1;
	private Text _text_2;

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

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	public static void stopIncrmentalRequest2() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLocalSymbolTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(localSymbolText);
		IObservableValue configurationinfosymbollocalSymbolSelfObserveValue = BeanProperties.value("symbol.localSymbol").observe(_self);
		bindingContext.bindValue(observeTextLocalSymbolTextObserveWidget, configurationinfosymbollocalSymbolSelfObserveValue, null, null);
		//
		IObservableValue observeTextTickSizeTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(tickSizeText);
		IObservableValue symbolrealTickSizeSelfObserveValue = BeanProperties.value("symbol.realTickSize").observe(_self);
		bindingContext.bindValue(observeTextTickSizeTextObserveWidget, symbolrealTickSizeSelfObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue infoprefixSelfObserveValue = BeanProperties.value("info.prefix").observe(_self);
		bindingContext.bindValue(observeTextTextObserveWidget, infoprefixSelfObserveValue, null, null);
		//
		IObservableValue observeText_textObserveWidget = WidgetProperties.text(SWT.Modify).observe(_text);
		IObservableValue infosymboltickValue_selfObserveValue = BeanProperties.value("info.symbol.realTickValue").observe(_self);
		bindingContext.bindValue(observeText_textObserveWidget, infosymboltickValue_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(_text_1);
		IObservableValue symbolcurrency_selfObserveValue = BeanProperties.value("symbol.currency").observe(_self);
		bindingContext.bindValue(observeText_text_1ObserveWidget, symbolcurrency_selfObserveValue, null, null);
		//
		IObservableValue observeText_text_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(_text_2);
		IObservableValue infosymboltype_selfObserveValue = BeanProperties.value("info.symbol.type").observe(_self);
		bindingContext.bindValue(observeText_text_2ObserveWidget, infosymboltype_selfObserveValue, null, null);
		//
		return bindingContext;
	}
}
