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

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

import com.mfg.strategy.builder.part.NodeCreationFactory;

public class StrategyTemplateTransferDropTargetListener extends 
TemplateTransferDropTargetListener  
{ 
        public StrategyTemplateTransferDropTargetListener(EditPartViewer viewer) { 
                super(viewer); 
        } 
 
        @Override 
        protected CreationFactory getFactory(Object template) { 
                return new NodeCreationFactory((Class<?>)template); 
        } 
} 