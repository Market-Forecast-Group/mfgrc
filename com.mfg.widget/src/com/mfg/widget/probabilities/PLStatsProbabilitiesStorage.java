package com.mfg.widget.probabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.utils.Utils;
import com.mfg.widget.IndicatorConfiguration;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.thoughtworks.xstream.XStream;

public class PLStatsProbabilitiesStorage extends
		SimpleStorage<PLStatsProbabilityStorageObject> {

	@Override
	public void configureXStream(XStream x) {
		super.configureXStream(x);
		x.alias("indicator-params", IndicatorParamBean.class);
		x.alias("probabilities-settings",
				com.mfg.interfaces.trading.Configuration.class);
		x.alias("prob-config", PLStatsProbabilityStorageObject.class);
		x.alias("distribution", ProbabilityElement.class);
		x.omitField(ProbabilityElement.class, "distributionsContainer");
		x.omitField(ProbabilityElement.class, "allLogMessages");
	}

	@Override
	public void loadAll(File workspace) {
		super.loadAll(workspace);
	}

	@Override
	public String getFileName(PLStatsProbabilityStorageObject obj) {
		return obj.getName() + "-" + obj.getUUID().toString();
	}

	@Override
	public String getStorageName() {
		return "PLStats-Probabilities-Config";
	}

	@Override
	public PLStatsProbabilityStorageObject createDefaultObject() {
		return new PLStatsProbabilityStorageObject();
	}

	// Start Enrique's code

	public List<ProbabilityElement> getDistributions(IIndicatorConfiguration ind) {
		UUID uuid = ind.getUUID();
		for (PLStatsProbabilityStorageObject obj : getObjects()) {
			ProbabilityElementStorage element = obj.getInfo();
			if (uuid.equals(element.getuUID()))
				return element.getProbabilityList();
		}
		return new ArrayList<>();
	}

	private List<ProbabilityElement> getDistributionsOrAdd(
			IIndicatorConfiguration ind) {
		UUID uuid = ind.getUUID();
		for (PLStatsProbabilityStorageObject obj : getObjects()) {
			ProbabilityElementStorage element = obj.getInfo();
			if (uuid.equals(element.getuUID())) {
				if (element.getProbabilityList() == null)
					element.setProbabilityList(new ArrayList<ProbabilityElement>());
				return element.getProbabilityList();
			}
		}
		ArrayList<ProbabilityElement> value = new ArrayList<>();
		ProbabilityElementStorage element = new ProbabilityElementStorage(
				ind.getUUID(), value);
		PLStatsProbabilityStorageObject obj = new PLStatsProbabilityStorageObject();
		obj.setInfo(element);
		add(obj);
		return value;
	}

	public void add(IIndicatorConfiguration config, DistributionsContainer dist) {
		List<ProbabilityElement> list = getDistributionsOrAdd(config);
		int oldsize = list.size();
		String name = "probability " + (list.size() + 1);
		ProbabilityElement pe = getTheProbabilityElement(config, list, name,
				dist);
		dist.setIndicatorConfiguration((IndicatorConfiguration) config);
		pe.setIndicatorConfiguration((IndicatorConfiguration) config);
		pe.setProbabilityConfiguration(config.getProbabilitiesSettings()
				.clone());
		pe.writeDistributionContainer();
		if (oldsize < list.size())
			fireObjectAdded(pe);
		else
			fireObjectModified(pe);
	}

	private static ProbabilityElement getTheProbabilityElement(
			IIndicatorConfiguration aConfig, List<ProbabilityElement> aList,
			String aName, DistributionsContainer aDist) {
		for (ProbabilityElement probabilityElement : aList) {
			if (probabilityElement.getProbabilityConfiguration().equals(
					aConfig.getProbabilitiesSettings())) {
				probabilityElement.setDistributionsContainer(aDist);
				return probabilityElement;
			}
		}
		ProbabilityElement pe;
		aList.add(pe = new ProbabilityElement(aName, aDist));
		return pe;
	}

	public void add(IIndicatorConfiguration config, ProbabilityElement pElement) {
		List<ProbabilityElement> list = getDistributionsOrAdd(config);
		list.add(pElement);
		pElement.setIndicatorConfiguration((IndicatorConfiguration) config);
		fireObjectAdded(pElement);
	}

	public void remove(IIndicatorConfiguration config) {
		UUID uuid = config.getUUID();
		List<ProbabilityElement> res = getProbElements(uuid);
		if (res != null) {
			for (ProbabilityElement probabilityElement : res) {
				probabilityElement.removeDistributionData();
			}
			fireListRemoved(res);
		}
	}

	public void remove(ProbabilityElement e) {
		UUID uuid = e.getIndicatorConfiguration().getUUID();
		List<ProbabilityElement> res = getProbElements(uuid);
		System.out.println("Trying to delete probability: " + e.getName());
		if (e.isRunning()) {
			MessageDialog
					.openWarning(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"Probability Removing",
							"The probability distribution you want to remove has one "
									+ "or more running trade configurations associated. It cannot be removed.");
			return;
		}
		if (!e.hasTradeDependencies()
				|| MessageDialog
						.openConfirm(
								PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow().getShell(),
								"Probability Removing",
								"The probability distribution you want to remove has one "
										+ "or more trade configurations associated. Do you want to remove it anyway?")) {
			res.remove(e);
			ProbabilitiesManager probabilitiesManager = WidgetPlugin
					.getDefault().getProbabilitiesManager();
			DistributionsContainer distributionsContainer = probabilitiesManager
					.getDistributionsContainer();
			e.removeDistributionData();
			System.out.println("Fire the removed from workspace signal");
			fireObjectRemoved(e);
			if (e.getDistributionsContainer() != null
					&& e.getDistributionsContainer().equals(
							distributionsContainer)) {
				System.out.println("removing it from the statistics tab.");
				probabilitiesManager.setDistributionsContainer(null);
			}
		}
	}

	private List<ProbabilityElement> getProbElements(UUID uuid) {
		List<ProbabilityElement> res = null;
		for (Iterator<PLStatsProbabilityStorageObject> iterator = getObjects()
				.iterator(); iterator.hasNext();) {
			PLStatsProbabilityStorageObject obj = iterator.next();
			ProbabilityElementStorage probabilityS = obj.getInfo();
			if (probabilityS.getuUID().equals(uuid)) {
				res = probabilityS.getProbabilityList();
				iterator.remove();
				break;
			}
		}
		return res;
	}

	public void filter(List<IIndicatorConfiguration> configs) {
		Utils.debug_id(76546, "filtering probs");
		ArrayList<PLStatsProbabilityStorageObject> newMap = new ArrayList<>();
		for (IIndicatorConfiguration indicatorConfiguration : configs) {
			UUID uuid = indicatorConfiguration.getUUID();
			ProbabilityElementStorage elem = new ProbabilityElementStorage(
					uuid, getProbElements(uuid));
			PLStatsProbabilityStorageObject obj = new PLStatsProbabilityStorageObject();
			obj.setInfo(elem);
			newMap.add(obj);
		}
		getObjects().clear();
		getObjects().addAll(newMap);
		fireStorageChanged();
	}

	public List<ProbabilityElement> getDistributions() {
		ArrayList<ProbabilityElement> list = new ArrayList<>();
		for (PLStatsProbabilityStorageObject obj : getObjects()) {
			ProbabilityElementStorage probabilityS = obj.getInfo();
			List<ProbabilityElement> l = probabilityS.getProbabilityList();
			if (l != null)
				list.addAll(l);
		}
		return list;
	}

	public List<String> getProbabilitiesNames() {
		ArrayList<String> list = new ArrayList<>();
		for (ProbabilityElement dist : getDistributions()) {
			list.add(dist.getNameForProbability());
		}
		return list;
	}

	/**
	 * 
	 */
	public void clear() {
		getObjects().clear();
	}

	public ProbabilityElement getProbabilityElementFromName(String aProbName) {
		if (aProbName == null)
			return null;
		StringTokenizer tk = new StringTokenizer(aProbName, "/");
		String sname = tk.nextToken();
		if (!tk.hasMoreTokens())
			return null;
		String iname = tk.nextToken();
		if (!tk.hasMoreTokens())
			return null;
		String pname = tk.nextToken();
		for (ProbabilityElement dist : getDistributions()) {
			if (dist.getIndicatorConfiguration().getSymbol().getName()
					.equals(sname)
					&& dist.getIndicatorConfiguration().getName().equals(iname)
					&& dist.getName().equals(pname))
				return dist;
		}
		return null;
	}

	public DistributionsContainer getProbabilityFromName(String aProbName) {
		ProbabilityElement pe = getProbabilityElementFromName(aProbName);
		return pe == null ? null : pe.loadDistributionContainer(false);
	}

	@Override
	public void storageSaved() {
		super.storageSaved();
		for (ProbabilityElement dist : getDistributions()) {
			dist.close();
		}
	}

	public List<ProbabilityElementStorage> getProbabilityMap() {
		List<ProbabilityElementStorage> list = new ArrayList<>();
		for (PLStatsProbabilityStorageObject obj : getObjects()) {
			list.add(obj.getInfo());
		}
		return list;
	}

	public void setProbabilityMap(
			List<ProbabilityElementStorage> aProbabilityMap) {
		getObjects().clear();
		for (ProbabilityElementStorage elem : aProbabilityMap) {
			PLStatsProbabilityStorageObject obj = new PLStatsProbabilityStorageObject();
			obj.setInfo(elem);
			getObjects().add(obj);
		}
	}

}
