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

package com.mfg.strategy.builder.ui;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;

import com.mfg.strategy.builder.model.ANDCollectionEventModel;
import com.mfg.strategy.builder.model.CLXoverEventModel;
import com.mfg.strategy.builder.model.CNCEventModel;
import com.mfg.strategy.builder.model.ConditionalCommandEventModel;
import com.mfg.strategy.builder.model.EntryEventModel;
import com.mfg.strategy.builder.model.ExitEventModel;
import com.mfg.strategy.builder.model.ExitProbEventModel;
import com.mfg.strategy.builder.model.ManualEntryEventModel;
import com.mfg.strategy.builder.model.NOTEventModel;
import com.mfg.strategy.builder.model.OANDCollectionEventModel;
import com.mfg.strategy.builder.model.ORCollectionEventModel;
import com.mfg.strategy.builder.model.ProbTDEventModel;
import com.mfg.strategy.builder.model.ProfitLossEventModel;
import com.mfg.strategy.builder.model.RCEventModel;
import com.mfg.strategy.builder.model.SCEventModel;
import com.mfg.strategy.builder.model.SortedCollectionEventModel;
import com.mfg.strategy.builder.model.Sw0LevelEventModel;
import com.mfg.strategy.builder.model.SwRatioEventModel;
import com.mfg.strategy.builder.model.THEventModel;
import com.mfg.strategy.builder.model.TicksEventModel;
import com.mfg.strategy.builder.model.TrendXoverEventModel;
import com.mfg.strategy.builder.model.UnSortedCollectionEventModel;
import com.mfg.strategy.builder.model.XORCollectionEventModel;
import com.mfg.strategy.builder.part.NodeCreationFactory;

public class StrategyPaletteRoot extends PaletteRoot {

	public StrategyPaletteRoot() {
		super();
		StrategyPaletteRoot root = this;
		PaletteGroup manipGroup = new PaletteGroup("Events Handling");
		root.add(manipGroup);
		SelectionToolEntry selectionToolEntry = new SelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		manipGroup.add(new MarqueeToolEntry());

		PaletteDrawer instGroup = null;
		root.add(new PaletteSeparator());

		instGroup = new PaletteDrawer("Simple Events");

		instGroup.add(new CombinedTemplateCreationEntry(
				"TH",
				"Create a Threshold Event",
				THEventModel.class,
				new NodeCreationFactory(THEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"ProbTD",
				"Create a Probability TD Event",
				ProbTDEventModel.class,
				new NodeCreationFactory(ProbTDEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"ExitProb",
				"Create an Exit based on Probabilities Event",
				ExitProbEventModel.class,
				new NodeCreationFactory(ExitProbEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"SC",
				"Create a SC Event",
				SCEventModel.class,
				new NodeCreationFactory(SCEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"RC",
				"Create a RC Event",
				RCEventModel.class,
				new NodeCreationFactory(RCEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"CL XOVER",
				"Create a CL Xover Event",
				CLXoverEventModel.class,
				new NodeCreationFactory(CLXoverEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"TREND XOVER",
				"Create a Trend Xover Event",
				TrendXoverEventModel.class,
				new NodeCreationFactory(TrendXoverEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"Profit/Loss",
				"Create a Profit/Loss Event",
				ProfitLossEventModel.class,
				new NodeCreationFactory(ProfitLossEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"Ticks",
				"Create a Ticks Event",
				TicksEventModel.class,
				new NodeCreationFactory(TicksEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"C/NC",
				"Create a Contr/NonContr Event",
				CNCEventModel.class,
				new NodeCreationFactory(CNCEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"Sw0 Level",
				"Create a Sw0 Level Event",
				Sw0LevelEventModel.class,
				new NodeCreationFactory(Sw0LevelEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"Sw Ratio",
				"Create a Ratio Event",
				SwRatioEventModel.class,
				new NodeCreationFactory(SwRatioEventModel.class),
				null,
				null));
		root.add(instGroup);

		root.add(new PaletteSeparator());
		instGroup = new PaletteDrawer("Event Commands");
		root.add(instGroup);
		instGroup.add(new CombinedTemplateCreationEntry(
				"Entry",
				"Create an Entry Event",
				EntryEventModel.class,
				new NodeCreationFactory(EntryEventModel.class),
				null,
				null));

		instGroup.add(new CombinedTemplateCreationEntry(
				"Exit",
				"Create an Exit Event",
				ExitEventModel.class,
				new NodeCreationFactory(ExitEventModel.class),
				null,
				null));
		
		instGroup.add(new CombinedTemplateCreationEntry(
				"Manual Entry",
				"Create a Manual Entry Event",
				ManualEntryEventModel.class,
				new NodeCreationFactory(ManualEntryEventModel.class),
				null,
				null));

		// the complex events now
		root.add(new PaletteSeparator());
		instGroup = new PaletteDrawer("Binding Events");
		root.add(instGroup);
		instGroup.add(new CombinedTemplateCreationEntry(
				"CND CMD",
				"Create a Conditional Command bind",
				ConditionalCommandEventModel.class,
				new NodeCreationFactory(ConditionalCommandEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"SORTED",
				"Create a SORTED Event Collection",
				SortedCollectionEventModel.class,
				new NodeCreationFactory(SortedCollectionEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"UNSORTED",
				"Create a UNSORTED Event Collection",
				UnSortedCollectionEventModel.class,
				new NodeCreationFactory(UnSortedCollectionEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"OR",
				"Create a OR Event Collection",
				ORCollectionEventModel.class,
				new NodeCreationFactory(ORCollectionEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"AND",
				"Create a AND Event Collection",
				ANDCollectionEventModel.class,
				new NodeCreationFactory(ANDCollectionEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"XOR",
				"Create a XOR Event Collection",
				XORCollectionEventModel.class,
				new NodeCreationFactory(XORCollectionEventModel.class),
				null,
				null));
		instGroup.add(new CombinedTemplateCreationEntry(
				"OAND",
				"Create a OAND Event Collection",
				OANDCollectionEventModel.class,
				new NodeCreationFactory(OANDCollectionEventModel.class),
				null,
				null));

		root.add(new PaletteSeparator());
		instGroup = new PaletteDrawer("Others");
		root.add(instGroup);
		instGroup.add(new CombinedTemplateCreationEntry(
				"NOT",
				"Create a NOT Event",
				NOTEventModel.class,
				new NodeCreationFactory(NOTEventModel.class),
				null,
				null));
		root.setDefaultEntry(selectionToolEntry);

	}

}
