package com.mfg.dm;

/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class is a container for all the parameters of a merged history related
 * to data provider (eSignal or IB).
 * 
 * @author arian
 * 
 * 
 */
@XmlSeeAlso(DataProviderParams.class)
public class MergedSeriesParams /* extends GenericIdentifier */{

	// private DataProviderParams ibParams;
	private DataProviderParams eSignalParams;
	private DataProviderParams selectedParams;
	private int numberOfPricesForCsv;
	private boolean saveSnapshot;
	private boolean useSavedSnapshot;
	private boolean useAllAvailableData;
	private int intervalForSavingSnapshot;

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	public MergedSeriesParams() {
		// ibParams = new DataProviderParams(true);
		eSignalParams = new DataProviderParams();
		selectedParams = eSignalParams;
		numberOfPricesForCsv = 100;
		useSavedSnapshot = false;
		useAllAvailableData = false;
		saveSnapshot = false;
		intervalForSavingSnapshot = 200;
	}

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

	public int getIntervalForSavingSnapshot() {
		return intervalForSavingSnapshot;
	}

	public void setIntervalForSavingSnapshot(int intervalForSavingSnapshot1) {
		this.intervalForSavingSnapshot = intervalForSavingSnapshot1;
	}

	public int getNumberOfPricesForCsv() {
		return numberOfPricesForCsv;
	}

	public void setNumberOfPricesForCsv(int numberOfPricesForCSV) {
		this.numberOfPricesForCsv = numberOfPricesForCSV;
		firePropertyChange("numberOfPricesForCsv");
	}

	/**
	 * @return the useAllAvailableData
	 */
	public boolean isUseAllAvailableData() {
		return useAllAvailableData;
	}

	/**
	 * @param useAllAvailableData1
	 *            the useAllAvailableData to set
	 */
	public void setUseAllAvailableData(boolean useAllAvailableData1) {
		this.useAllAvailableData = useAllAvailableData1;
		firePropertyChange("useAllAvailableData");
	}

	/**
	 * @return the useSavedSnapshot
	 */
	public boolean isUseSavedSnapshot() {
		return useSavedSnapshot;
	}

	/**
	 * @param useSavedSnapshot1
	 *            the useSavedSnapshot to set
	 */
	public void setUseSavedSnapshot(boolean useSavedSnapshot1) {
		this.useSavedSnapshot = useSavedSnapshot1;
		firePropertyChange("useSavedSnapshot");
	}

	/**
	 * @return the saveSnapshot
	 */
	public boolean isSaveSnapshot() {
		return saveSnapshot;
	}

	/**
	 * @param saveSnapshot1
	 *            the saveSnapshot to set
	 */
	public void setSaveSnapshot(boolean saveSnapshot1) {
		this.saveSnapshot = saveSnapshot1;
		firePropertyChange("saveSnapshot");
	}

	@XmlIDREF
	public DataProviderParams getSelectedParams() {
		return selectedParams;
	}

	public void setSelectedParams(DataProviderParams selectedParams1) {
		this.selectedParams = selectedParams1;
		firePropertyChange("selectedParams");
	}

	/**
	 * @return the ibParams
	 */
	// public DataProviderParams getIbParams() {
	// return ibParams;
	// }

	// public void setIbParams(DataProviderParams ibParams1) {
	// this.ibParams = ibParams1;
	// }

	/**
	 * @return the eSignalParams
	 */
	// public DataProviderParams getESignalParams() {
	// return eSignalParams;
	// }
	//
	// public void setESignalParams(DataProviderParams eSignalParams1) {
	// this.eSignalParams = eSignalParams1;
	// }

	// @Override
	// protected void _toJsonEmbedded(JSONStringer stringer) throws
	// JSONException {
	// stringer.key("selectedParams");
	// stringer.object();
	// this.selectedParams._toJsonEmbedded(stringer);
	// stringer.endObject();
	// }
	//
	// @Override
	// protected void _updateFromJSON(JSONObject json) throws JSONException {
	// assert (false) : "not implemented here";
	// }
}
