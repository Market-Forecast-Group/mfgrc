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

package com.mfg.strategy.builder.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.mfg.strategy.builder.commands.AppDeletePolicy;
import com.mfg.strategy.builder.figure.AppEditLayoutPolicy;
import com.mfg.strategy.builder.figure.EventFigure;
import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.strategy.builder.ui.GeneralStrategyStettingsView;

public abstract class StrategyAppPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	private Color backgroundColor = ColorConstants.lightGreen;


	@Override
	public void activate() {
		super.activate();
		((EventModelNode) getModel()).addPropertyChangeListener(this);
	}


	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
	}


	@Override
	public void deactivate() {
		super.deactivate();
		((EventModelNode) getModel()).removePropertyChangeListener(this);
	}


	@Override
	protected IFigure createFigure() {
		EventModelNode md = ((EventModelNode) getModel());
		EventFigure f = new EventFigure(((ScalableRootEditPart) this.getRoot()).getZoomManager());
		f.setCollapsed(getCollapsed());
		f.setName(md.getLabel());
		f.setBackgroundColor(getBackgroundColor());
		return f;
	}


	protected Color getBackgroundColor() {
		return backgroundColor;
	}


	/**
	 * @param aBackgroundColor
	 *            the backgroundColor to set
	 */
	public void setBackgroundColor(Color aBackgroundColor) {
		backgroundColor = aBackgroundColor;
	}


	@SuppressWarnings("static-method")// Used on inner classes.
	public boolean getCollapsed() {
		return false;
	}


	@Override
	protected void refreshVisuals() {
		EventFigure f = (EventFigure) getFigure();
		EventModelNode m = (EventModelNode) getModel();
		f.setName(m.getLabel());
		f.setValidBorder(m.isValid());
		f.setHorizontal(!m.isVertical());
		super.refreshVisuals();
	}


	protected void myRefreshChildren() {
		EventFigure f = (EventFigure) getFigure();
		EventModelNode m = (EventModelNode) getModel();
		f.setValidBorder(m.isValid());
		super.refreshChildren();
	}


	@Override
	public void propertyChange(PropertyChangeEvent aEvt) {
		if (aEvt.getPropertyName().equals(PropertiesID.PROPERTY_REMOVE))
			myRefreshChildren();
		else if (aEvt.getPropertyName().equals(PropertiesID.PROPERTY_ADD))
			myRefreshChildren();
		else if (aEvt.getPropertyName().equals(PropertiesID.PROPERTY_ROTATE)) {
			refreshVisuals();
			myRefreshChildren();
		} else
			refreshVisuals();
	}


	@Override
	protected List<EventModelNode> getModelChildren() {
		List<EventModelNode> ch = ((EventModelNode) getModel()).getChildren();
		return (ch != null) ? ch : new ArrayList<>();
	}


	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN) && (this instanceof SimpleEventPart || this instanceof BoundsPart || this instanceof EventsCanvasPart)) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (!(this instanceof EventsCanvasPart))
					page.showView(IPageLayout.ID_PROP_SHEET);
				else {
					GeneralStrategyStettingsView v = (GeneralStrategyStettingsView) page.showView(GeneralStrategyStettingsView.ID);
					v.setModel((EventsCanvasModel) getModel());
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
