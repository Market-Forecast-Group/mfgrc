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
import java.util.List;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.mfg.strategy.builder.commands.AppDeletePolicy;
import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.SimpleEventModel;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class AppAbstractTreeEditPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		((EventModelNode) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		((EventModelNode) getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	protected List<EventModelNode> getModelChildren() {
		return ((EventModelNode) getModel()).getChildren();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertiesID.PROPERTY_ADD))
			refreshChildren();
		if (evt.getPropertyName().equals(PropertiesID.PROPERTY_REMOVE))
			refreshChildren();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
	}

	@Override
	public void refreshVisuals() {
		EventModelNode model = (EventModelNode) getModel();
		setWidgetText(model.getLabel());
		String id=null;
		if (model instanceof SimpleEventModel)
			id = ISharedImages.IMG_OBJ_ELEMENT;
		else 
			id = ISharedImages.IMG_OBJ_FOLDER;
		setWidgetImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(id));
		setSelected(0);
	}
	
	@Override
	public DragTracker getDragTracker(Request aReq) {
		return new SelectEditPartTracker(this);
	}

	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				IWorkbenchPage page =
						PlatformUI
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage();
				page.showView(IPageLayout.ID_PROP_SHEET);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
