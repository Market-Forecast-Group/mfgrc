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

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class CompartmentFigure extends Layer {

	private static final int MARGIN = 2;

	private boolean horizontal;
	private LayoutManager layout;

	private CompartmentFigureBorder theBorder;


	public CompartmentFigure() {
		layout = getMyLayout();
		setLayoutManager(layout);
		setBorder(theBorder = new CompartmentFigureBorder());
		setOpaque(true);
	}


	private ToolbarLayout getMyLayout() {
		ToolbarLayout toolbarLayout = new ToolbarLayout(horizontal);
		toolbarLayout.setStretchMinorAxis(false);
		toolbarLayout.setSpacing(2);
		return toolbarLayout;
	}

	public static class CompartmentFigureBorder extends MarginBorder {

		public CompartmentFigureBorder() {
			super(MARGIN, MARGIN, MARGIN, MARGIN);
		}


		@Override
		public boolean isOpaque() {
			return false;
		}


		public void setMargin(int margin) {
			insets = new Insets(margin);
		}

	}


	public void setConnectionsSpace(boolean usingConnections) {
		if (usingConnections) {
			theBorder.setMargin(3 * MARGIN);
		} else {
			theBorder.setMargin(MARGIN);
		}
	}


	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}


	/**
	 * @param aHorizontal
	 *            the horizontal to set
	 */
	@SuppressWarnings("deprecation")
	// Necessary use of setVertical() method.
	public void setHorizontal(boolean aHorizontal) {
		horizontal = aHorizontal;
		((ToolbarLayout) layout).setVertical(!horizontal);
		setVisible(!isVisible());
		setVisible(!isVisible());
		// System.out.println("rotating h="+horizontal);
	}


	@SuppressWarnings("deprecation")
	// Necessary use of Point() method.
	public int getInsertIndex(Point coordinate, double dPar) {
		double d = dPar;
		List<IFigure> list = getChildren();
		// System.out.println("search "+coordinate+" on "+list.size());
		for (int i = 0; i < list.size(); i++) {
			Rectangle clientArea = list.get(i).getBounds();
			Point point = clientArea.getCenter();
			// System.out.println("width "+clientArea.width+" of "+clientArea);
			// System.out.print("from "+point);
			// TODO Review d assignment. It's generating a warning. At this point I see can be use a static value 1.
			d = 1;
			point = new Point(
					point.x * d,
					point.y * d);
			translateToAbsolute(point);
			// System.out.print(" to point "+point);
			double diff = getDiff(coordinate, point);
			// System.out.println(" diff="+diff);
			int nsign = (int) Math.signum(diff);
			if (nsign <= 0) {
				// System.out.println("add at "+i);
				return i;
			}
		}
		// System.out.println("add at last");
		return list.size();
	}


	private double getDiff(Point aCoordinate, Point aPoint) {
		if (horizontal) {
			return aCoordinate.preciseX() - aPoint.preciseX();
		}
		return aCoordinate.preciseY() - aPoint.preciseY();
	}

}
