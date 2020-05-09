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

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Pattern;

import com.mfg.strategy.builder.utils.Utils;

public class EventFigure extends RoundedRectangle 
						implements IIndexedContainer {

	private Label labelName = new Label();
	private CompartmentFigure comp = new CompartmentFigure();
	private boolean superAdd = true;
	private boolean collapsed = false;
	private Color mybg;
	private static final int margin = 0;
	ZoomManager zoom;
	private IFigure etcLabel = new Label("(...)");
	public EventFigure(ZoomManager aZoom) {
		this.zoom = aZoom;
		FlowLayout layout = new FlowLayout(false);
		setLayoutManager(layout);
		layout.setStretchMinorAxis(true);
		labelName.setForegroundColor(ColorConstants.black);
		add(comp, OrderedLayout.ALIGN_CENTER);
//		comp.setConnectionsSpace(true);
		add(labelName, OrderedLayout.ALIGN_CENTER);
		comp.setVisible(true);
		comp.setHorizontal(true);
		setBorder(new CompartmentFigure.CompartmentFigureBorder());
		superAdd = false;
		setOpaque(true);
		layout.setMajorSpacing(10*margin);
		layout.setMinorSpacing(10*margin);
	}

	@Override
	public void setBackgroundColor(Color aBg) {
//		super.setBackgroundColor(aBg);
//		comp.setBackgroundColor(aBg);
//		labelName.setBackgroundColor(aBg);
		mybg = aBg;
	}
	
	@Override
	public void paint(Graphics aGraphics) {
		Rectangle rect = getBounds();
		Device currentd = aGraphics.getFont().getDevice();
		double d = zoom.getZoom();
		aGraphics.setBackgroundPattern(new Pattern(currentd,
				(int) (d * (rect.x)), 
				(int) (d * (rect.y)),
				(int) (d * (rect.x) + d * rect.width), 
				(int) (d * (rect.y) + d	* rect.height), 
				mybg, ColorConstants.white));
		super.paint(aGraphics);
	}
	
	public static class EventFigureBorder extends LineBorder {
		Insets insets = new Insets(margin, margin, margin, margin);

		@Override
		public Insets getInsets(IFigure figure) {
			return insets;
		}
	}

	public void setName(String text) {
		labelName.setText(text);
	}
	
	public final class TopCenterAnchor extends AbstractConnectionAnchor {
	   public TopCenterAnchor(IFigure owner) {
	      super(owner);
		}

		@Override
		public Point getLocation(Point reference) {
			final Rectangle bounds1 = getOwner().getBounds();
			Point p = bounds1.getCenter();
			final Point res = new Point(p.x, bounds1.y);
			getOwner().translateToAbsolute(res);
			return res;
		}
	}
	
//	private PolylineConnection connection(IFigure aFigure){
//		if (aFigure instanceof EventFigure){
//			PolylineConnection con = new PolylineConnection();
//			con.setSourceAnchor(new ChopboxAnchor(labelName));
//			con.setTargetAnchor(new TopCenterAnchor(aFigure));
//			con.setTargetDecoration(new PolygonDecoration());
//			return con;
//		}
//		return null;
//		
//	}

	@Override
	public void add(IFigure aFigure, Object aConstraint, int aIndex) {
		if (superAdd)
			super.add(aFigure, aConstraint, aIndex);
		else {
			comp.add(aFigure, aConstraint, aIndex);
//			superAdd = true;
//			super.add(connection(aFigure));
//			superAdd = false;
		}
	}

	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return comp.isHorizontal();
	}

	/**
	 * @param aHorizontal
	 *            the horizontal to set
	 */
	public void setHorizontal(boolean aHorizontal) {
		comp.setHorizontal(aHorizontal);
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * @param aCollapsed
	 *            the collapsed to set
	 */
	public void setCollapsed(boolean aCollapsed) {
		collapsed = aCollapsed;
//		comp.setVisible(!collapsed);
		if (!aCollapsed){
			boolean superAdd1 = superAdd;
			superAdd = true;
			add(comp);
			superAdd = superAdd1;
		} else {
			if (getChildren().contains(comp))
				remove(comp);
		}
	}
	
	public void showETC(boolean showetc) {
		if (showetc){
			boolean superAdd1 = superAdd;
			superAdd = true;
			add(etcLabel);
			superAdd = superAdd1;
		} else {
			if (getChildren().contains(etcLabel))
				remove(etcLabel);
		}
		setVisible(!isVisible());
		setVisible(!isVisible());
	}

	@Override
	public void remove(IFigure aFigure) {
		if (getChildren().contains(aFigure))
			super.remove(aFigure);
		else
			comp.remove(aFigure);
	}

	public void setValidBorder(boolean aValid) {
		if (aValid) {
			setLineWidth(1);
			setLineStyle(SWT.LINE_SOLID);
			setForegroundColor(Utils.darker(mybg, 0.8));
		} else {
			setLineWidth(2);
			setLineStyle(SWT.LINE_DASH);
			setForegroundColor(ColorConstants.red);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mfg.strategy.builder.figure.IIndexedContainer#getInsertIndex(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public int getInsertIndex(Point coordinate){
		return comp.getInsertIndex(coordinate,zoom.getZoom());
	}
	
}
