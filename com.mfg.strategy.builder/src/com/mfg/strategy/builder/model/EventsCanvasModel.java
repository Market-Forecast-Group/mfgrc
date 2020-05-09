/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model;

import org.eclipse.ui.views.properties.IPropertySource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;
import com.mfg.widget.probabilities.ProbabilitiesNames;

public class EventsCanvasModel extends EventModelNode {

	private static final String DESC = "DESC";

	private static final String FILENAME = "FILENAME";

	private static final String NAME = "NAME";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PROBNAME = "PROBNAME";

	private String name = "Noname", fileName, description;

	private String probName = ProbabilitiesNames.NO_PROBABILITY;

	public static EventsCanvasModel createDefault() {
		EventsCanvasModel can = new EventsCanvasModel();
		ConditionalCommandEventModel c = new ConditionalCommandEventModel();
		c.addChild(new THEventModel());
		c.addChild(new EntryEventModel());
		ConditionalCommandEventModel c1 = new ConditionalCommandEventModel();
		c1.addChild(new THEventModel());
		c1.addChild(new ExitEventModel());
		SortedCollectionEventModel c2 = new SortedCollectionEventModel();
		c2.addChild(c);
		c2.addChild(c1);
		can.addChild(c2);
		return can;
	}
	

	@Override
	public boolean canAdd(EventModelNode aChild) {
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the fileName
	 */
	@Deprecated
	public String getFileName() {
		return fileName;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param aName
	 *            the name to set
	 */
	public void setName(String aName) {
		name = aName;
	}


	/**
	 * @param aFileName
	 *            the fileName to set
	 */
	@Deprecated
	public void setFileName(String aFileName) {
		fileName = aFileName;
	}


	/**
	 * @param aDescription
	 *            the description to set
	 */
	public void setDescription(String aDescription) {
		description = aDescription;
	}


	@Override
	protected void _toJsonEmbedded(JSONStringer aStringer) throws JSONException {
		aStringer.key(NAME);
		aStringer.value(getName());
		aStringer.key(FILENAME);
		aStringer.value(getFileName());
		aStringer.key(DESC);
		aStringer.value(getDescription());
		aStringer.key(PROBNAME);
		aStringer.value(getProbabilityName());
		getAdapter(IPropertySource.class);
		if (children != null && children.size() > 0) {
			aStringer.key(CHILDREN);
			aStringer.array();
			for (EventModelNode e : children) {
				aStringer.value(e.toJSONString());
			}
			aStringer.endArray();
		}
	}


	@Override
	protected void _updateFromJSON(JSONObject aJson) throws JSONException {
		setName(aJson.getString(NAME));
		setFileName(aJson.getString(FILENAME));
		setDescription(aJson.getString(DESC));
		setProbabilityName(aJson.getString(PROBNAME));
		getAdapter(IPropertySource.class);
		if (aJson.has(CHILDREN)) {
			JSONArray a = aJson.getJSONArray(CHILDREN);
			for (int i = 0; i < a.length(); i++) {
				String string = a.get(i).toString();
				EventModelNode child = (EventModelNode) com.mfg.utils.GenericIdentifier.createFromString(string);
				addChild(child);
			}
		}
	}


	@Override
	public EventGeneral exportMe() {
		EventGeneral strategy = getChildren().get(0).exportMe();
		return strategy;
	}


	public void setProbabilityName(String aName) {
		probName = aName;
	}


	public String getProbabilityName() {
		return probName;
	}


	public DistributionsContainer getProbability() {
		return WidgetPlugin.getDefault().getProbabilitiesManager().getProbabilityFromName(probName);
	}

}
