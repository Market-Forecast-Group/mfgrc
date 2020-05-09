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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.NodePropertySource;
import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.utils.GenericIdentifier;

public abstract class EventModelNode extends com.mfg.utils.GenericIdentifier implements IAdaptable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport listeners;
	private transient IPropertySource propertySource = null;

	protected List<EventModelNode> children;
	protected final String CHILDREN = "children";
	private EventModelNode parent;
	private boolean collapsed;
	private boolean vertical = false;


	public EventModelNode() {
		super();
		children = new ArrayList<>();
		listeners = new PropertyChangeSupport(this);
		getAdapter(IPropertySource.class);
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}


	// public PropertyChangeSupport getListeners() {
	// return listeners;
	// }

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		if (getParent() != null) {
			getParent().firePropertyChange(PropertiesID.PROPERTY_MOD_CHILD, oldValue, newValue);
		}
	}


	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}


	/**
	 * @return the children
	 */
	public List<EventModelNode> getChildren() {
		return children;
	}


	/**
	 * @param aChildren
	 *            the children to set
	 */
	public void setChildren(List<EventModelNode> aChildren) {
		children = aChildren;
	}


	public boolean addChild(EventModelNode child) {
		boolean b = this.children.add(child);
		if (b) {
			child.setParent(this);
			updateProbabilistic();
			firePropertyChange(PropertiesID.PROPERTY_ADD, null, child);
		}
		return b;
	}


	public void addChild(int index, EventModelNode aChild) {
		this.children.add(index, aChild);
		aChild.setParent(this);
		updateProbabilistic();
		firePropertyChange(PropertiesID.PROPERTY_ADD, null, aChild);
	}


	public boolean removeChild(EventModelNode child) {
		boolean b = this.children.remove(child);
		if (b) {
			updateProbabilistic();
			firePropertyChange(PropertiesID.PROPERTY_REMOVE, child, null);
		}
		return b;
	}

	boolean probabilistic;


	public boolean isProbabilistic() {
		return probabilistic;
	}


	public void setProbabilistic(boolean aProbabilistic) {
		probabilistic = aProbabilistic;
		firePropertyChange(PropertiesID.PROBABILISTIC_EVENTS, null, Boolean.valueOf(aProbabilistic));
	}


	public void updateProbabilistic() {
		boolean prob = false;
		for (EventModelNode child : getChildren()) {
			prob |= child.isProbabilistic();
		}
		if (probabilistic != prob)
			setProbabilistic(prob);
		if (getParent() != null)
			getParent().updateProbabilistic();
	}


	/**
	 * @return the parent
	 */
	public EventModelNode getParent() {
		return parent;
	}


	/**
	 * @param aParent
	 *            the parent to set
	 */
	public void setParent(EventModelNode aParent) {
		parent = aParent;
	}


	// /**
	// * @return the myEvent
	// */
	// public EventGeneral getMyEvent() {
	// return myEvent;
	// }
	// /**
	// * @param aMyEvent the myEvent to set
	// */
	// public void setMyEvent(EventGeneral aMyEvent) {
	// myEvent = aMyEvent;
	// }

	public String getLabel() {
		return getClass().toString();
	}


	/**
	 * @return the collapsed
	 */
	public boolean isCollapsed() {
		return collapsed;
	}


	/**
	 * @param aCollapsed
	 *            the collapsed to set
	 */
	public void setCollapsed(boolean aCollapsed) {
		collapsed = aCollapsed;
		firePropertyChange(PropertiesID.PROPERTY_EXPANDCOLLAPSE, null, Boolean.valueOf(aCollapsed));
	}


	@Override
	public EventModelNode clone() {
		EventModelNode res = null;
		try {
			res = (EventModelNode) super.clone();
			res.propertySource = null;
			res.parent = null;
			res.children = new ArrayList<>(children.size());
			res.listeners = new PropertyChangeSupport(res);
			for (EventModelNode e : children) {
				res.addChild(e.clone());
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return res;
	}


	/**
	 * @param aChild  
	 */
	@SuppressWarnings("static-method")//Used on inner classes.
	public boolean canAdd(EventModelNode aChild) {
		return true;
	}


	@SuppressWarnings("static-method")//Used on inner classes
	public boolean isValid() {
		return true;
	}


	/**
	 * @return the vertical
	 */
	public boolean isVertical() {
		return vertical;
	}


	/**
	 * @param aVertical
	 *            the vertical to set
	 */
	public void setVertical(boolean aVertical) {
		if (vertical != aVertical) {
			vertical = aVertical;
			firePropertyChange(PropertiesID.PROPERTY_ROTATE, Boolean.valueOf(!aVertical), Boolean.valueOf(aVertical));
		}
	}


	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null)
				propertySource = new NodePropertySource(this);
			return propertySource;
		}
		return null;
	}


	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		getAdapter(IPropertySource.class);
		if (children != null && children.size() > 0) {
			stringer.key(CHILDREN);
			stringer.array();
			for (EventModelNode e : children) {
				stringer.value(e.toJSONString());
			}
			stringer.endArray();
		}
		for (IPropertyDescriptor e : propertySource.getPropertyDescriptors()) {
			stringer.key(e.getId().toString());
			stringer.value(propertySource.getPropertyValue(e.getId()));
		}
		stringer.key(PropertiesID.PROPERTY_ROTATE);
		stringer.value(isVertical());
		stringer.key(PropertiesID.PROPERTY_EXPANDCOLLAPSE);
		stringer.value(isCollapsed());
	}


	/**
	 * This method assumes that the object is already created and it assumes that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		getAdapter(IPropertySource.class);
		if (json.has(CHILDREN)) {
			JSONArray a = json.getJSONArray(CHILDREN);
			for (int i = 0; i < a.length(); i++) {
				String string = a.get(i).toString();
				EventModelNode child = (EventModelNode) GenericIdentifier.createFromString(string);
				addChild(child);
			}
		}
		for (IPropertyDescriptor e : propertySource.getPropertyDescriptors()) {
			String key = e.getId().toString();
			if (json.has(key)) {
				Object v = json.get(key);
				try{
					propertySource.setPropertyValue(e.getId(), v);
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}
		if (json.has(PropertiesID.PROPERTY_ROTATE))
			setVertical(json.getBoolean(PropertiesID.PROPERTY_ROTATE));
		if (json.has(PropertiesID.PROPERTY_EXPANDCOLLAPSE))
			setCollapsed(json.getBoolean(PropertiesID.PROPERTY_EXPANDCOLLAPSE));
	}


	public abstract EventGeneral exportMe();

	// @Override
	// public int hashCode() {
	// return getHashId().hashCode();
	// }
	//
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// EventModelNode other = (EventModelNode) obj;
	// other._invalidateHash();
	// this._invalidateHash();
	// if ((this.getHashId().compareTo(other.getHashId())) != 0) {
	// return false;
	// }
	// return true;
	// }

}
