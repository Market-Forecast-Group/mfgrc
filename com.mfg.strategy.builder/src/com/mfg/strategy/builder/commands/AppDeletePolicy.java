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
package com.mfg.strategy.builder.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;


public class AppDeletePolicy extends ComponentEditPolicy{ 
           
        @Override
		protected Command createDeleteCommand(
        		GroupRequest deleteRequest) { 
        	DeleteCommand cmd = new DeleteCommand();
        	cmd.setModel(getHost().getModel());
        	cmd.setParentModel(getHost().getParent().getModel());
        	return cmd;
        }
        

}
