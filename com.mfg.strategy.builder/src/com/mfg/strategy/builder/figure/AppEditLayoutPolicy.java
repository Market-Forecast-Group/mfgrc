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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.mfg.strategy.builder.commands.EventCreateCommand;
import com.mfg.strategy.builder.commands.EventMoveCommand;
import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.SimpleEventModel;
import com.mfg.strategy.builder.part.BoundsPart;
import com.mfg.strategy.builder.part.StrategyAppPart;
import com.mfg.utils.Utils;

public class AppEditLayoutPolicy extends ConstrainedLayoutEditPolicy {

	@Deprecated
	@Override
	protected Command createAddCommand(EditPart aChild, Object aConstraint) {
		return createChangeConstraintCommand(aChild, aConstraint);
	}


	@Deprecated
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (constraint == null ||
				child instanceof BoundsPart)
			return null;
		// System.out.println("change constraint "+getHost());
		EventCreateCommand cmd = new EventMoveCommand();
		Object parentModel = getHost().getModel();
		cmd.setParent(parentModel);
		cmd.setChild(child.getModel());
		if (getHostFigure() instanceof IIndexedContainer) {
			cmd.setIndex(Integer.valueOf(constraint.toString()).intValue());
		}
		return cmd;
	}


	@Override
	protected Command getCloneCommand(ChangeBoundsRequest aRequest) {
		Utils.debug_var(12345, "clone");
		return super.getCloneCommand(aRequest);
	}


	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == REQ_CREATE && getHost() instanceof StrategyAppPart) {
			Point point = request.getLocation();
			// System.out.println("create command on "+point);
			EventCreateCommand cmd = new EventCreateCommand();
			Object parentModel = getHost().getModel();
			if (parentModel instanceof SimpleEventModel)
				return null;
			cmd.setParent(parentModel);
			if (request.getNewObject() instanceof EventModelNode) {
				cmd.setChild(request.getNewObject());
				if (getHostFigure() instanceof IIndexedContainer) {
					// Rectangle bounds = getHostFigure().getBounds();
					// point = new Point(bounds.x+point.x,
					// bounds.y+point.y);
					// getHostFigure().translateToAbsolute(point);
					int idx = ((IIndexedContainer) getHostFigure())
							.getInsertIndex(point);
					cmd.setIndex(idx);
				}
			}
			return cmd;
		}
		return null;
	}


	@Override
	protected Object getConstraintFor(Point point) {
		// System.out.println("1 req host "+getHost()+" point="+point);
		IFigure hostFigure = getHostFigure();
		if (hostFigure instanceof IIndexedContainer) {
			hostFigure.translateToAbsolute(point);
			Rectangle bounds = hostFigure.getBounds();
			Point pointTmp = new Point(bounds.x + point.x,
					bounds.y + point.y);
			int idx = ((IIndexedContainer) hostFigure).getInsertIndex(pointTmp);
			return Integer.valueOf(idx);
		}
		return Integer.valueOf(-1);
	}


	@Override
	protected Object getConstraintFor(Rectangle rect) {
		// System.out.println("3 req host "+getHost());
		Point p = rect.getCenter();
		return getConstraintFor(p);
	}

	// @Override
	// protected Object getConstraintFor(ChangeBoundsRequest aRequest,
	// GraphicalEditPart aChild) {
	// System.out.println("2 req host "+getHost());
	// if (getHostFigure() instanceof EventFigure) {
	// int idx = ((EventFigure) getHostFigure()).getInsertIndex(aRequest
	// .getLocation());
	// return idx;
	// }
	// return null;
	// }
}
