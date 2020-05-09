package com.mfg.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Display;

import com.mfg.dm.symbols.SymbolData;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.IProbabilitiesSet;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.logger.LoggerPlugin;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.utils.ObjectListenersGroup;
import com.mfg.utils.io.ObjectsFileIO;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.Filter;
import com.mfg.widget.probabilities.PLStatsProbabilitiesStorage;
import com.mfg.widget.probabilities.ProbababilitiesFilter;
import com.mfg.widget.probabilities.ProbabilitiesLogRecordConverter;
import com.mfg.widget.probabilities.ProbabilitiesNames;
import com.mfg.widget.probabilities.ProbabilityElement;
import com.mfg.widget.probabilities.logger.IProbabilitiesCalcLoggerManager;
import com.mfg.widget.probabilities.logger.LoggerUtils;
import com.mfg.widget.probabilities.logger.ProbabilitiesCalcLoggerManager;

public class ProbabilitiesManager {
	private DistributionsContainer distributionsContainer;
	private ProbabilitiesKey selectedTargetKey;
	private IProbabilitiesSet selectedTargetSet;
	private IProbabilitiesFilter probabilitiesLogFilter;
	private IProbabilitiesCalcLoggerManager logManager;
	private DataBindingContext bindingContext;
	private final ObjectListenersGroup<ProbabilitiesKey> targetKeySelection;
	private final ObjectListenersGroup<Integer> patternSelection;
	private final ObjectListenersGroup<DistributionsContainer> distributionsContainerSelection;

	public ProbabilitiesManager() {
		super();
		targetKeySelection = new ObjectListenersGroup<>();
		patternSelection = new ObjectListenersGroup<>();
		distributionsContainerSelection = new ObjectListenersGroup<>();
		probabilitiesLogFilter = new ProbababilitiesFilter();
		getLogManager().setFilter((ILogFilter) getProbabilitiesLogFilter());
		LoggerPlugin.getDefault().initLogRecordConverter(
				new ProbabilitiesLogRecordConverter());
	}

	public static DistributionsContainer loadDistributionContainer(
			ProbabilityElement e) {
		return (DistributionsContainer) readInstance(getFileName(e));
	}

	public static DistributionsContainer loadDistributionContainer(
			String fileName) {
		return (DistributionsContainer) readInstance(fileName);
	}

	public static Object readInstance(String fileName) {
		try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
				fileName));) {

			Object res = oin.readObject();
			return res;
		} catch (Exception e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	public static String getFileName(ProbabilityElement e) {
		String dir = PersistInterfacesPlugin.getDefault()
				.getCurrentWorkspacePath()
				+ File.separator
				+ "Probabilities"
				+ File.separator;
		File file = new File(dir);
		if (!file.exists())
			file.mkdir();
		return dir + e.getFileName() + ".prob";
	}

	public static void writeDistributionContainer(ProbabilityElement e) {
		ObjectsFileIO.getInstance();
		ObjectsFileIO.writeInstance(e.getDistributionsContainer(),
				getFileName(e));
	}

	public static void removeDistributionContainer(ProbabilityElement e) {
		String fileName = getFileName(e);
		File f = new File(fileName);
		System.out.println("deleting " + fileName);
		f.delete();
	}

	public DistributionsContainer getDistributionsContainer(
			IIndicatorConfiguration aIndicatorParamBean) {
		if (aIndicatorParamBean == null) {
			if (getDistributionsStorate().getDistributions().size() > 0) {
				return getDistributionsStorate().getDistributions().get(0)
						.getDistributionsContainer();
			}
			return null;
		}
		List<ProbabilityElement> distributions = getDistributionsStorate()
				.getDistributions(aIndicatorParamBean);
		if (distributions.size() > 0)
			return distributions.get(0).getDistributionsContainer();
		return null;
	}

	public DistributionsContainer getDistributionsContainer(SymbolData aSymbol,
			AbstractIndicatorParamBean aIndicatorSettings) {
		for (ProbabilityElement dc : getDistributionsStorate()
				.getDistributions()) {
			if (dc.getDistributionsContainer().getIndicatorConfiguration()
					.getSymbol().getUUID().equals(aSymbol.getUUID())
					&& dc.getDistributionsContainer()
							.getIndicatorConfiguration().getIndicatorSettings()
							.equals(aIndicatorSettings)) {
				return dc.getDistributionsContainer();
			}
		}
		return null;
	}

	public DistributionsContainer getDistributionsContainer() {
		return distributionsContainer;
	}

	public void setDistributionsContainer(
			DistributionsContainer aDistributionsContainer) {
		distributionsContainer = aDistributionsContainer;
		ILogger aLogger = LoggerUtils.defaultLogger();
		if (distributionsContainer == null) {
			setSelectedTargetKey(null);
			aLogger.begin("nothing to show");
		} else {
			List<ProbabilitiesKey> allKeys = distributionsContainer
					.getAllKeys();
			if (allKeys.size() > 0)
				setSelectedTargetKey(allKeys.get(0));
			else
				setSelectedTargetKey(null);
			List<ILogRecord> eventsList = distributionsContainer
					.getAllLogMessages();
			// LoggerUtils.addAlltoLog(logger, eventsList);
			getLogManager().changeMemory(
					eventsList == null ? new ArrayList<ILogRecord>()
							: eventsList);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					getLogManager().refreshViews();
				}
			});
			// getLogManager().setFilter((ILogFilter)
			// getProbabilitiesLogFilter());
			// getProbabilitiesLogFilter().setProbabilityKey(
			// getSelectedTargetKey());
		}
		distributionsContainerSelection.handle(distributionsContainer);
	}

	/**
	 * @param aSelectedTargetKey
	 *            the selectedTargetKey to set
	 */
	public void setSelectedTargetKey(ProbabilitiesKey aSelectedTargetKey) {
		this.selectedTargetKey = aSelectedTargetKey;
		if (distributionsContainer != null && aSelectedTargetKey != null) {
			setSelectedTargetSet(distributionsContainer
					.getElementsSet(aSelectedTargetKey));
		} else {
			setSelectedTargetSet(null);
		}
		targetKeySelection.handle(aSelectedTargetKey);
	}

	private int selectedPattern = 1;
	private ILogger logger;

	/**
	 * @return the selectedTargetKey
	 */
	public ProbabilitiesKey getSelectedTargetKey() {
		return selectedTargetKey;
	}

	/**
	 * @param aSelectedTargetSet
	 *            the selectedTargetSet to set
	 */
	public void setSelectedTargetSet(IProbabilitiesSet aSelectedTargetSet) {
		this.selectedTargetSet = aSelectedTargetSet;
	}

	/**
	 * @return the selectedTargetSet
	 */
	public IProbabilitiesSet getSelectedTargetSet() {
		return selectedTargetSet;
	}

	public int getSelectedPattern() {
		return selectedPattern;
	}

	@SuppressWarnings("boxing")
	public void setSelectedPattern(int p) {
		selectedPattern = p;
		patternSelection.handle(p);
	}

	public IProbabilitiesFilter getProbabilitiesLogFilter() {
		return probabilitiesLogFilter;
	}

	public void setProbabilitiesLogFilter(Filter aProbabilitiesLogFilter) {
		probabilitiesLogFilter = aProbabilitiesLogFilter;
	}

	public IProbabilitiesCalcLoggerManager getLogManager() {
		if (logManager == null) {
			logManager = new ProbabilitiesCalcLoggerManager();
		}
		return logManager;
	}

	public void setLogManager(IProbabilitiesCalcLoggerManager aLogManager) {
		logManager = aLogManager;
	}

	public ILogger getLogger() {
		if (logger == null) {
			logger = getLogManager().createLogger();
		}
		return logger;
	}

	public DataBindingContext getLogSettingsBindingContext() {
		return bindingContext;
	}

	public void setLogSettingsBindingContext(DataBindingContext aBindingContext) {
		bindingContext = aBindingContext;
	}

	@SuppressWarnings("static-method")
	public PLStatsProbabilitiesStorage getDistributionsStorate() {
		return WidgetPlugin.getDefault().getProbsStorage();
	}

	private int targetSelected;

	public int getTargetSelected() {
		return targetSelected;
	}

	@SuppressWarnings("boxing")
	public void setTargetSelected(int aTargetSelected) {
		targetSelected = aTargetSelected;
		targetSelectionListener.handle(aTargetSelected);
	}

	private final ObjectListenersGroup<Integer> targetSelectionListener = new ObjectListenersGroup<>();

	public ObjectListenersGroup<Integer> getTargetSelectionListener() {
		return targetSelectionListener;
	}

	public ObjectListenersGroup<ProbabilitiesKey> getTargetKeySelection() {
		return targetKeySelection;
	}

	public ObjectListenersGroup<Integer> getPatternSelection() {
		return patternSelection;
	}

	public ObjectListenersGroup<DistributionsContainer> getDistributionsContainerSelection() {
		return distributionsContainerSelection;
	}

	public DistributionsContainer getProbabilityFromName(String aProbName) {
		if (aProbName.equals(ProbabilitiesNames.CURRENT_PROBABILITY))
			return distributionsContainer;
		if (aProbName.equals(ProbabilitiesNames.NO_PROBABILITY))
			return null;
		return getDistributionsStorate().getProbabilityFromName(aProbName);
	}

}
