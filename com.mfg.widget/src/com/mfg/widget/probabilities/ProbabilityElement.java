package com.mfg.widget.probabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.garret.perst.StorageError;
import org.garret.perst.StorageFactory;

import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.logger.ILogRecord;
import com.mfg.utils.jobs.MFGJob;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;

public class ProbabilityElement {
	private String name;
	private String fileName;
	transient DistributionsContainer distributionsContainer;
	private Configuration fProbabilityConfiguration;
	private IndicatorConfiguration fIndicatorConfiguration;
	transient List<ILogRecord> allLogMessages;

	static class MyRoot extends Persistent {
		List<ILogRecord> allLogMessages;

		public MyRoot() {
			super();
		}

		public MyRoot(Storage aStorage) {
			super(aStorage);
			allLogMessages = aStorage.createLink();
		}

	}

	transient Storage db;
	transient MyRoot myRoot;
	private List<UUID> tradeDependency = new ArrayList<>();
	public transient static long pagePoolSize = 100 * 1024;

	public ProbabilityElement() {
		super();
	}

	public ProbabilityElement(String aName,
			DistributionsContainer aDistributionsContainer) {
		super();
		name = aName;
		fileName = aDistributionsContainer.getIndicatorConfiguration()
				.getSymbol().getName()
				+ "--"
				+ aDistributionsContainer.getIndicatorConfiguration().getName()
				+ "--" + getName();
		setDistributionsContainer(aDistributionsContainer);
	}

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String aFileName) {
		fileName = aFileName;
	}

	public void loadDistributionContainer() {
		WidgetPlugin.getDefault().getProbabilitiesManager();
		setDistributionsContainer(ProbabilitiesManager
				.loadDistributionContainer(this));
		getDistributionsContainer().setIndicatorConfiguration(
				getIndicatorConfiguration());
		getDistributionsContainer().setConfiguration(fProbabilityConfiguration);
		getDistributionsContainer().setAllLogMessages(allLogMessages);
	}

	protected void buildLog() {
		db = StorageFactory.getInstance().createStorage();
		db.open(getPerstName(), pagePoolSize); // Open
		// the
		// database
		if (db.getRoot() == null) {
			myRoot = new MyRoot(db);
			db.setRoot(myRoot);
		} else {
			myRoot = (MyRoot) db.getRoot();
		}
		allLogMessages = myRoot.allLogMessages;
		System.out.println("loaded " + allLogMessages.size() + " messages");
	}

	protected String getPerstName() {
		return ProbabilitiesManager.getFileName(this) + ".perst";
	}

	@XmlTransient
	public DistributionsContainer getDistributionsContainer() {
		return distributionsContainer;
	}

	public void setDistributionsContainer(
			DistributionsContainer aDistributionsContainer) {
		distributionsContainer = aDistributionsContainer;
		buildLog();
		if (aDistributionsContainer.getAllLogMessages() != null
				&& !aDistributionsContainer.getAllLogMessages().isEmpty()) {
			MFGJob saveJob = new MFGJob("...saving log") {

				@Override
				protected IStatus run(IProgressMonitor aMonitor) {
					allLogMessages.clear();
					allLogMessages.addAll(distributionsContainer
							.getAllLogMessages());
					db.modify(myRoot);
					db.commit();
					System.out.println("saved " + allLogMessages.size()
							+ " messages");
					return Status.OK_STATUS;
				}
			};
			saveJob.schedule();
		}
	}

	public Configuration getProbabilityConfiguration() {
		return fProbabilityConfiguration;
	}

	public void setProbabilityConfiguration(
			Configuration aProbabilityConfiguration) {
		fProbabilityConfiguration = aProbabilityConfiguration;
	}

	public IndicatorConfiguration getIndicatorConfiguration() {
		return fIndicatorConfiguration;
	}

	public void setIndicatorConfiguration(
			IndicatorConfiguration aIndicatorConfiguration) {
		fIndicatorConfiguration = aIndicatorConfiguration;
	}

	public void writeDistributionContainer() {
		WidgetPlugin.getDefault().getProbabilitiesManager();
		ProbabilitiesManager.writeDistributionContainer(this);

	}

	public void removeDistributionData() {
		WidgetPlugin.getDefault().getProbabilitiesManager();
		ProbabilitiesManager.removeDistributionContainer(this);
		if (db != null && db.isOpened()) {
			db.close();
		}
		String perstName = getPerstName();
		File f = new File(perstName);
		System.out.println("deleting " + perstName);
		f.delete();

	}

	public DistributionsContainer loadDistributionContainer(boolean force) {
		if (force || distributionsContainer == null) {
			loadDistributionContainer();
		}
		return distributionsContainer;
	}

	@XmlTransient
	public List<ILogRecord> getAllLogMessages() {
		return allLogMessages;
	}

	public void setAllLogMessages(List<ILogRecord> aAllLogMessages) {
		allLogMessages = aAllLogMessages;
	}

	public void close() {
		if (db != null && db.isOpened()) {
			try {
				db.close();
			} catch (StorageError e) {
				e.printStackTrace();
			}
		}
	}

	public boolean hasTradeDependencies() {
		return tradeDependency.size() > 0;
	}

	public boolean isRunning() {
		return (Job.getJobManager().find(getNameForProbability()).length > 0);
	}

	public void removeTradeDependency(UUID aUuid) {
		tradeDependency.remove(aUuid);
	}

	public void addTradeDependency(UUID aUuid) {
		if (!tradeDependency.contains(aUuid))
			tradeDependency.add(aUuid);
	}

	/**
	 * @return the tradeDependency
	 */
	public List<UUID> getTradeDependency() {
		return tradeDependency;
	}

	/**
	 * @param aTradeDependency
	 *            the tradeDependency to set
	 */
	public void setTradeDependency(List<UUID> aTradeDependency) {
		tradeDependency = aTradeDependency;
	}

	public String getNameForProbability() {
		return symbolName() + "/" + this.getIndicatorConfiguration().getName()
				+ "/" + this.getName();
	}

	private String symbolName() {
		CSVSymbolData symbol = this.getIndicatorConfiguration().getSymbol();
		if (symbol != null)
			return symbol.getName();
		return "<corrupted symbol>";
	}

}
