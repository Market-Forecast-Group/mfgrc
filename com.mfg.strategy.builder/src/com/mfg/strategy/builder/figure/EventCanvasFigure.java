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
package com.mfg.strategy.builder.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Pattern;

public class EventCanvasFigure extends Figure implements IIndexedContainer {
	private ZoomManager zoom;
	private CompartmentFigure comp = new CompartmentFigure();
	private boolean superAdd;
	
	public EventCanvasFigure(ZoomManager aZoom) {
		super();
		zoom = aZoom;
        FlowLayout fl = new FlowLayout(true);
        fl.setMajorAlignment(OrderedLayout.ALIGN_CENTER);
        fl.setMajorSpacing(6);
        fl.setMinorSpacing(6);
        setLayoutManager(fl);
        superAdd = true;
        super.add(comp, OrderedLayout.ALIGN_CENTER);
        superAdd = false;
		comp.setVisible(true);
		comp.setHorizontal(true);
		setOpaque(true);
		
	}
	

	@Override
	public void paint(Graphics aGraphics) {
		Rectangle rect = getBounds();
		Device currentd = aGraphics.getFont().getDevice();
		Color mybg = new Color(currentd, 238, 238, 238);
		double d = zoom.getZoom();
		aGraphics.setBackgroundPattern(new Pattern(currentd,
				(int) (d * (rect.x)), (int) (d * (rect.y)),
				(int) (d * (rect.x) + d * rect.width), (int) (d * (rect.y) + d
						* rect.height), mybg, ColorConstants.white));
		super.paint(aGraphics);
	}

	@Override
	public void add(IFigure aFigure, Object aConstraint, int aIndex) {
		if (superAdd)
			super.add(aFigure, aConstraint, aIndex);
		else
			comp.add(aFigure, aConstraint, aIndex);
	}

	@Override
	public int getInsertIndex(Point coordinate){
		final int insertIndex = comp.getInsertIndex(coordinate,zoom.getZoom());
		return insertIndex;
	}

	@Override
	public void remove(IFigure aFigure) {
		if (getChildren().contains(aFigure))
			super.remove(aFigure);
		else
			comp.remove(aFigure);
	}
	
}
