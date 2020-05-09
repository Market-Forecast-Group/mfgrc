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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.ScalableRootEditPart;

import com.mfg.strategy.builder.figure.EventFigure;
import com.mfg.strategy.builder.model.EventModelNode;

public class CollectionPart extends StrategyAppPart {
	
	public CollectionPart() {
		super();
		setBackgroundColor(ColorConstants.orange);
	}

	@Override
	protected List<EventModelNode> getModelChildren() {
		List<EventModelNode> ch = ((EventModelNode)getModel()).getChildren();
		return (ch!=null)?ch:new ArrayList<>();
	}
	
	@Override
	protected IFigure createFigure() {
		EventModelNode md = ((EventModelNode)getModel());
		EventFigure f = new EventFigure(((ScalableRootEditPart)this.getRoot()).getZoomManager());
		f.setCollapsed(md.isCollapsed());
		f.showETC(md.isCollapsed());
		f.setName(md.getLabel());
		f.setBackgroundColor(getBackgroundColor());
		return f;
	}
	
	@Override
	protected void refreshVisuals() {
		EventFigure f = (EventFigure)getFigure();
		EventModelNode m = (EventModelNode)getModel();
		f.setCollapsed(m.isCollapsed());
		f.showETC(m.isCollapsed());
		super.refreshVisuals();
	}

}
