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

package com.mfg.strategy.builder.model.psource;

import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_BOUND;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_CONTRARIAN;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_CSCARRAY;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_DIMENSION;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_ENTRIESARRAYEXIT;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_FILTERTYPE;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_GLOBAL;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_LIMITFAMILY;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_LSW0;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_MARKETFAMILY;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_NCSCARRAY;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_PERCENT;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_PROBABILISTIC;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_REFSWING;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_ROTATE;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_STARTPOINT;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_TDTH;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_TH2SK;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_UPDATINGREF;
import static com.mfg.strategy.builder.model.psource.PropertiesID.PROPERTY_WSCALE;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.mfg.strategy.automatic.eventPatterns.EventAtomProfitLoss.ProfitLoss;
import com.mfg.strategy.automatic.eventPatterns.LSFilterType;
import com.mfg.strategy.builder.model.BoundsModel;
import com.mfg.strategy.builder.model.CNCEventModel;
import com.mfg.strategy.builder.model.EntryEventModel;
import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.ExitEventModel;
import com.mfg.strategy.builder.model.IBasedOnEntries;
import com.mfg.strategy.builder.model.IConfirmEventModel;
import com.mfg.strategy.builder.model.IContrarian;
import com.mfg.strategy.builder.model.ILimitedToSW0;
import com.mfg.strategy.builder.model.IMarketFamily;
import com.mfg.strategy.builder.model.ProbTDEventModel;
import com.mfg.strategy.builder.model.ProfitLossEventModel;
import com.mfg.strategy.builder.model.ScaledEventModel;
import com.mfg.strategy.builder.model.Sw0LevelEventModel;
import com.mfg.strategy.builder.model.SwRatioEventModel;
import com.mfg.strategy.builder.model.THEventModel;
import com.mfg.strategy.builder.model.TicksEventModel;
import com.mfg.strategy.builder.utils.Utils;
import com.mfg.ui.UIPlugin;
import com.mfg.widget.priv.StartPoint;

public class NodePropertySource implements IPropertySource {

	private static final String CAT_BASIC = "Basic";
	private static final String CAT_FILTERS = "Filters";
	private static final String CAT_OPARAMS = "Order Parameters";
	private static final String CAT_SPECIFIC = "Specific";
	private static final String CAT_BOUNDS = "The Bounds";
	private static final String CAT_INTERACTIVE = "Warnings";

	private EventModelNode node;
	String[] filterLabelsArray = new String[] { "Auto", "Long", "Short" };
	StartPoint[] startPointArray = new StartPoint[] { StartPoint.P_0, StartPoint.P_m1, StartPoint.HHLL };
	String[] startPointArrayStr = new String[] { "P0", "P-1", "HH/LL" };
	String[] labelsDimensionsArray = new String[] { "1", "2", "3", "4", "5" };
	private String[] probArray;


	public NodePropertySource(EventModelNode aNode) {
		this.node = aNode;

	}


	@Override
	public Object getEditableValue() {
		return null;
	}


	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<>();

		if (hasWidgetScale()) {
			IntegerPropertyDescriptor e = new IntegerPropertyDescriptor(PROPERTY_WSCALE, "widget Scale");
			e.setCategory(CAT_BASIC);
			properties.add(e);
		}
		if (hasLimit2Sw0()) {
			CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PROPERTY_LSW0, "limit to Swing0");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
		}
		if (node instanceof THEventModel) {
			IntegerPropertyDescriptor e = new IntegerPropertyDescriptor(PROPERTY_TH2SK, "TH to Skip");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}
		if (node instanceof TicksEventModel) {
			PropertyDescriptor e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_TICKSTOWAIT, "Ticks to Wait");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_TICKSCONTRARIAN, "Contrarian");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}
		if (node instanceof ProbTDEventModel) {
			IntegerPropertyDescriptor e = new IntegerPropertyDescriptor(PROPERTY_TDTH, "TD Threshold");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}
		if (node instanceof IContrarian) {
			CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PROPERTY_CONTRARIAN, "Contrarian");
			e.setCategory(CAT_BASIC);
			properties.add(e);
		}
		if (node instanceof CNCEventModel) {
			TextPropertyDescriptor e = new TextPropertyDescriptor(PROPERTY_CSCARRAY, "Contrarian");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
			e = new TextPropertyDescriptor(PROPERTY_NCSCARRAY, "Non Contrarian");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
		}

		if (node instanceof IBasedOnEntries) {
			PropertyDescriptor e = new TextPropertyDescriptor(PROPERTY_ENTRIESARRAYEXIT, "for Entries");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
			e = new ComboBoxPropertyDescriptor(PROPERTY_FILTERTYPE, "Filter", filterLabelsArray);
			e.setCategory(CAT_FILTERS);
			properties.add(e);
		}
		if (node instanceof IMarketFamily) {
			String txt;
			txt = "is Market";
			CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PROPERTY_MARKETFAMILY, txt);
			e.setCategory(CAT_OPARAMS);
			properties.add(e);
		}
		if (node instanceof ExitEventModel) {
			String txt = "Use Limit";
			CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PROPERTY_LIMITFAMILY, txt);
			e.setCategory(CAT_OPARAMS);
			properties.add(e);
		}
		if (node instanceof ExitEventModel) {
			CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PROPERTY_GLOBAL, "Global");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
		}

		if (node instanceof Sw0LevelEventModel) {
			PropertyDescriptor e = new ComboBoxPropertyDescriptor(PROPERTY_STARTPOINT, "Start Point", startPointArrayStr);
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new ComboBoxPropertyDescriptor(PROPERTY_REFSWING, "Ref Swing", new String[] { "0", "-1", "-2" });
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new DoublePropertyDescriptor(PROPERTY_PERCENT, "Percent");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PROPERTY_UPDATINGREF, "Udating References");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}
		if (node instanceof SwRatioEventModel) {
			ComboBoxPropertyDescriptor e = new ComboBoxPropertyDescriptor(PROPERTY_DIMENSION, "Dimension", labelsDimensionsArray);
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}

		if (node instanceof ProfitLossEventModel) {
			PropertyDescriptor e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_CONSIDERINGQ, "Considering Q");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_RELATIVE, "Relative");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_AVERAGINGGAIN, "Averaging Gain");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_TICKSTH, "Ticks TH");
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
			e = new ComboBoxPropertyDescriptor(PropertiesID.PROPERTY_PLTYPE, "Type", new String[] { "Profit", "Loss" });
			e.setCategory(CAT_SPECIFIC);
			properties.add(e);
		}

		if (node instanceof EntryEventModel) {
			PropertyDescriptor e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_ID, "ID");
			e.setCategory(CAT_BASIC);
			properties.add(e);
			e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_QUANTITY, "Quantity");
			e.setCategory(CAT_BASIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_LINKEDTOENTRY, "Linked to Entry");
			e.setCategory(CAT_BASIC);
			properties.add(e);
			e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_ENTRYLINKID, "Entry Link ID");
			e.setCategory(CAT_BASIC);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_MULTIPLEENTRIES, "Multiple Entries");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
			e = new TextPropertyDescriptor(PropertiesID.PROPERTY_SINGLEENTRIESSCALES, "Single Entries Scales");
			e.setCategory(CAT_FILTERS);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_SL, "SL Simple Protection");
			e.setCategory(CAT_OPARAMS);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_LIMITCHILD, "Include Limit Child");
			e.setCategory(CAT_OPARAMS);
			properties.add(e);
			e = new IntegerPropertyDescriptor(PropertiesID.PROPERTY_SLSCALE, "Simple Protection Scale");
			e.setCategory(CAT_OPARAMS);
			properties.add(e);
			e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_PROBABILISTIC, "Probabilistic Dir");
			e.setCategory(CAT_BASIC);
			properties.add(e);
		}

		if (node instanceof IConfirmEventModel) {
			{
				String text = node instanceof THEventModel ? "Show Popup" : "Ask Confirmation";
				CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_REQUIRES_CONFIMATION, text);
				e.setCategory(CAT_INTERACTIVE);
				properties.add(e);
			}

			{
				CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_PLAY_SOUND, "Play Sound");
				e.setCategory(CAT_INTERACTIVE);
				properties.add(e);
			}

			{
				CheckboxPropertyDescriptor e = new CheckboxPropertyDescriptor(PropertiesID.PROPERTY_SPEAK, "Speak");
				e.setCategory(CAT_INTERACTIVE);
				properties.add(e);
			}

			{
				final String[] sounds = UIPlugin.SOUNDS;
				ComboBoxPropertyDescriptor e = new ComboBoxPropertyDescriptor(PropertiesID.PROPERTY_SOUND_PATH, "Sound", sounds) {
					@Override
					public CellEditor createPropertyEditor(Composite parent) {
						ComboBoxCellEditor editor = (ComboBoxCellEditor) super.createPropertyEditor(parent);
						final CCombo combo = (CCombo) editor.getControl();
						combo.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e1) {
								String name = UIPlugin.SOUNDS[combo.getSelectionIndex()];
								UIPlugin.getDefault().playSound(name);
							}
						});
						return editor;
					}
				};
				// e.setLabelProvider(new LabelProvider() {
				// @Override
				// public String getText(Object element) {
				// String str = super.getText(element);
				// return str.trim().length() > 0 ? str.substring(0, 1).toUpperCase() + str.substring(1, str.length() - 4) : "";
				// }
				// });
				e.setCategory(CAT_INTERACTIVE);
				properties.add(e);
			}
		}

		if (node instanceof BoundsModel) {
			BoundsModel cnode = (BoundsModel) node;
			String lab = cnode.isLower() ? "L" : "U";
			DoublePropertyDescriptor e = new DoublePropertyDescriptor(PROPERTY_BOUND + 0, lab + "[0'/-1]");
			// double[] inf = ((SwRatioEventModel) cnode.getParent())
			// .getInfiniteBounds();
			e.setCategory(CAT_BOUNDS);
			// if (cnode.isLower()) {
			// e.setDescription("in [0," + inf[0] + ")");
			// } else {
			// e.setDescription("in (0," + inf[0] + "]");
			// }
			properties.add(e);
			for (int i = 1; i < cnode.size(); i++) {
				e = new DoublePropertyDescriptor(PROPERTY_BOUND + i, lab + "[" + (Utils.get3Col(i - 1)) + "/" + Utils.get3Row(i - 1) + "]");
				e.setCategory(CAT_BOUNDS);
				properties.add(e);
			}
		}
		return properties.toArray(new IPropertyDescriptor[0]);
	}


	private boolean hasLimit2Sw0() {
		return node instanceof ILimitedToSW0 && !(node instanceof THEventModel);
	}


	private boolean hasWidgetScale() {
		return node instanceof ScaledEventModel;
	}


	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(PropertiesID.PROBABILITY_DISTRIBUTION)) {
			EventsCanvasModel cnode = (EventsCanvasModel) node;
			int findProb = findProb(cnode.getProbabilityName());
			return Integer.valueOf(findProb);
		} else if (id.equals(PROPERTY_ROTATE))
			return Boolean.valueOf(node.isVertical());
		else if (id.equals(PROPERTY_WSCALE)) {
			ScaledEventModel cnode = (ScaledEventModel) node;
			return Integer.valueOf(cnode.getWidgetScale());
		} else if (id.equals(PROPERTY_LSW0)) {
			ILimitedToSW0 cnode = (ILimitedToSW0) node;
			return Boolean.valueOf(cnode.isLimitedToSwing0());
		} else if (id.equals(PROPERTY_TH2SK)) {
			THEventModel cnode = (THEventModel) node;
			return Integer.valueOf(cnode.getThToSkip());
		} else if (id.equals(PROPERTY_TDTH)) {
			ProbTDEventModel cnode = (ProbTDEventModel) node;
			return Double.valueOf(cnode.getTDTH());
		} else if (id.equals(PROPERTY_CONTRARIAN)) {
			IContrarian cnode = (IContrarian) node;
			return Boolean.valueOf(cnode.isContrarian());
		} else if (id.equals(PROPERTY_PROBABILISTIC)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Boolean.valueOf(cnode.isProbabilistic());
		} else if (id.equals(PropertiesID.PROPERTY_REQUIRES_CONFIMATION)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			return Boolean.valueOf(cnode.isRequiresConfirmation());
		} else if (id.equals(PropertiesID.PROPERTY_PLAY_SOUND)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			return Boolean.valueOf(cnode.isPlaySound());
		} else if (id.equals(PropertiesID.PROPERTY_SPEAK)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			return Boolean.valueOf(cnode.isSpeak());
		} else if (id.equals(PropertiesID.PROPERTY_SOUND_PATH)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			int index = Arrays.asList(UIPlugin.SOUNDS).indexOf(cnode.getSoundPath());
			return Integer.valueOf(index);
		} else if (id.equals(PROPERTY_CSCARRAY)) {
			CNCEventModel cnode = (CNCEventModel) node;
			return Arrays.toString(cnode.getContrarianScales());
		} else if (id.equals(PROPERTY_NCSCARRAY)) {
			CNCEventModel cnode = (CNCEventModel) node;
			return Arrays.toString(cnode.getNonContrarianScales());
		} else if (id.equals(PROPERTY_ENTRIESARRAYEXIT)) {
			IBasedOnEntries cnode = (IBasedOnEntries) node;
			return Arrays.toString(cnode.getEntries());
		} else if (id.equals(PROPERTY_GLOBAL)) {
			ExitEventModel cnode = (ExitEventModel) node;
			return Boolean.valueOf(cnode.isGlobal());
		} else if (id.equals(PROPERTY_MARKETFAMILY)) {
			IMarketFamily cnode = (IMarketFamily) node;
			boolean res = cnode.isMarketFamily();
			return Boolean.valueOf(res);
		} else if (id.equals(PROPERTY_LIMITFAMILY)) {
			ExitEventModel cnode = (ExitEventModel) node;
			return Boolean.valueOf(cnode.isUsingLimitFamily());
		} else if (id.equals(PROPERTY_FILTERTYPE)) {
			IBasedOnEntries cnode = (IBasedOnEntries) node;
			return Integer.valueOf(Arrays.asList(filterLabelsArray).indexOf(cnode.getFilterType().toString()));
		} else if (id.equals(PROPERTY_UPDATINGREF)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			return Boolean.valueOf(cnode.isUpdatingReferences());
		} else if (id.equals(PROPERTY_STARTPOINT)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			return Integer.valueOf(Arrays.asList(startPointArray).indexOf(cnode.getStartPoint()));
		} else if (id.equals(PROPERTY_REFSWING)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			return Integer.valueOf(-cnode.getRefSwing());
		} else if (id.equals(PROPERTY_UPDATINGREF)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			return Boolean.valueOf(cnode.isUpdatingReferences());
		} else if (id.equals(PROPERTY_PERCENT)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			return Double.valueOf(cnode.getPercent());
		} else if (id.equals(PROPERTY_DIMENSION)) {
			SwRatioEventModel cnode = (SwRatioEventModel) node;
			return Integer.valueOf(cnode.getDimension() - 1);
		} else if (isaBound(id)) {
			BoundsModel cnode = (BoundsModel) node;
			return Double.valueOf(cnode.getBound(getBound(id)));
		} else if (id.equals(PropertiesID.PROPERTY_CONSIDERINGQ)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			return Boolean.valueOf(cnode.isConsideringQ());
		} else if (id.equals(PropertiesID.PROPERTY_RELATIVE)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			return Boolean.valueOf(cnode.isRelative());
		} else if (id.equals(PropertiesID.PROPERTY_AVERAGINGGAIN)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			return Boolean.valueOf(cnode.isAveragingGain());
		} else if (id.equals(PropertiesID.PROPERTY_TICKSTH)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			return Integer.valueOf(cnode.getTickTH());
		} else if (id.equals(PropertiesID.PROPERTY_PLTYPE)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			return Integer.valueOf((cnode.getType().equals(ProfitLoss.Profit)) ? 0 : 1);
		} else if (id.equals(PropertiesID.PROPERTY_TICKSTOWAIT)) {
			TicksEventModel cnode = (TicksEventModel) node;
			return Integer.valueOf(cnode.getTicksTH());
		} else if (id.equals(PropertiesID.PROPERTY_TICKSCONTRARIAN)) {
			TicksEventModel cnode = (TicksEventModel) node;
			return Boolean.valueOf(cnode.isContrarian());
		} else if (id.equals(PropertiesID.PROPERTY_ID)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Integer.valueOf(cnode.getID());
		} else if (id.equals(PropertiesID.PROPERTY_QUANTITY)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Integer.valueOf(cnode.getQuantity());
		} else if (id.equals(PropertiesID.PROPERTY_MULTIPLEENTRIES)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Boolean.valueOf(cnode.isMultipleEntries());
		} else if (id.equals(PropertiesID.PROPERTY_SINGLEENTRIESSCALES)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Arrays.toString(cnode.getSingleEntriesScales());
		} else if (id.equals(PropertiesID.PROPERTY_SL)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Boolean.valueOf(cnode.isUsingSL());
		} else if (id.equals(PropertiesID.PROPERTY_SLSCALE)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Integer.valueOf(cnode.getSLScale());
		} else if (id.equals(PropertiesID.PROPERTY_LINKEDTOENTRY)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Boolean.valueOf(cnode.isLinkedToEntry());
		} else if (id.equals(PropertiesID.PROPERTY_ENTRYLINKID)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Integer.valueOf(cnode.getEntryLinkID());
		} else if (id.equals(PropertiesID.PROPERTY_LIMITCHILD)) {
			EntryEventModel cnode = (EntryEventModel) node;
			return Boolean.valueOf(cnode.isUsingLimitChild());
		}
		return null;
	}


	private int findProb(String aProbabilityName) {
		for (int i = 0; i < probArray.length; i++) {
			if (probArray[i].equals(aProbabilityName))
				return i;
		}
		return -1;
	}


	private static int getBound(Object aId) {
		String s = aId.toString().substring(PROPERTY_BOUND.length());
		return Integer.valueOf(s).intValue();
	}


	private static boolean isaBound(Object aId) {
		String s = aId.toString();
		return s.startsWith(PROPERTY_BOUND);
	}


	// Returns if the property with the given id has been changed since its
	// initial default value.
	// We do not handle default properties, so we return <tt>false</tt>.
	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}


	/**
	 * Reset a property to its default value. Since we do not handle default properties, we do nothing.
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// DO NOTHING
	}


	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PropertiesID.PROBABILITY_DISTRIBUTION)) {
			int pos = Integer.parseInt(value.toString());
			EventsCanvasModel cnode = (EventsCanvasModel) node;
			cnode.setProbabilityName(probArray[pos]);
		} else if (id.equals(PROPERTY_ROTATE)) {
			node.setVertical(((Boolean) value).booleanValue());
		} else if (id.equals(PROPERTY_WSCALE)) {
			ScaledEventModel cnode = (ScaledEventModel) node;
			cnode.setWidgetScale(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PROPERTY_LSW0)) {
			ILimitedToSW0 cnode = (ILimitedToSW0) node;
			cnode.setLimitedToSwing0(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PROPERTY_TH2SK)) {
			THEventModel cnode = (THEventModel) node;
			cnode.setThToSkip(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PROPERTY_TDTH)) {
			ProbTDEventModel cnode = (ProbTDEventModel) node;
			cnode.setTDTH(new Double(value.toString()).doubleValue());
		} else if (id.equals(PROPERTY_CONTRARIAN)) {
			IContrarian cnode = (IContrarian) node;
			cnode.setContrarian(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PROPERTY_PROBABILISTIC)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setProbabilistic(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_REQUIRES_CONFIMATION)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			cnode.setRequiresConfirmation(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_PLAY_SOUND)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			cnode.setPlaySound(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_SPEAK)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			cnode.setSpeak(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_SOUND_PATH)) {
			IConfirmEventModel cnode = (IConfirmEventModel) node;
			Integer index = (Integer) value;
			cnode.setSoundPath(UIPlugin.SOUNDS[index.intValue()]);
		} else if (id.equals(PROPERTY_CSCARRAY)) {
			CNCEventModel cnode = (CNCEventModel) node;
			cnode.setContrarianScales(Utils.parseIntArray(value.toString()));
		} else if (id.equals(PROPERTY_NCSCARRAY)) {
			CNCEventModel cnode = (CNCEventModel) node;
			cnode.setNonContrarianScales(Utils.parseIntArray(value.toString()));
		} else if (id.equals(PROPERTY_ENTRIESARRAYEXIT)) {
			IBasedOnEntries cnode = (IBasedOnEntries) node;
			cnode.setEntries(Utils.parseIntArray(value.toString()));
		} else if (id.equals(PROPERTY_GLOBAL)) {
			ExitEventModel cnode = (ExitEventModel) node;
			cnode.setGlobal(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PROPERTY_MARKETFAMILY)) {
			IMarketFamily cnode = (IMarketFamily) node;
			boolean val = new Boolean(value.toString()).booleanValue();
			cnode.setMarketFamily(val);
		} else if (id.equals(PROPERTY_LIMITFAMILY)) {
			ExitEventModel cnode = (ExitEventModel) node;
			boolean val = new Boolean(value.toString()).booleanValue();
			cnode.setUsingLimitFamily(val);
		} else if (id.equals(PROPERTY_FILTERTYPE)) {
			IBasedOnEntries cnode = (IBasedOnEntries) node;
			cnode.setFilterType(Enum.valueOf(LSFilterType.class, filterLabelsArray[Integer.valueOf(value.toString()).intValue()]));
		} else if (id.equals(PROPERTY_STARTPOINT)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			cnode.setStartPoint(startPointArray[Integer.valueOf(value.toString()).intValue()]);
		} else if (id.equals(PROPERTY_REFSWING)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			cnode.setRefSwing(-Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PROPERTY_UPDATINGREF)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			cnode.setUpdatingReferences(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PROPERTY_PERCENT)) {
			Sw0LevelEventModel cnode = (Sw0LevelEventModel) node;
			cnode.setPercent(new Double(value.toString()).doubleValue());
		} else if (id.equals(PROPERTY_DIMENSION)) {
			SwRatioEventModel cnode = (SwRatioEventModel) node;
			cnode.setDimension(Integer.valueOf(value.toString()).intValue() + 1);
		} else if (isaBound(id)) {
			BoundsModel cnode = (BoundsModel) node;
			cnode.setBound(new Double(value.toString()).doubleValue(), getBound(id));
		} else if (id.equals(PropertiesID.PROPERTY_CONSIDERINGQ)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			cnode.setConsideringQ(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_AVERAGINGGAIN)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			cnode.setAveragingGain(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_TICKSTH)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			cnode.setTickTH(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_RELATIVE)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			cnode.setRelative(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_PLTYPE)) {
			ProfitLossEventModel cnode = (ProfitLossEventModel) node;
			int p = Integer.valueOf(value.toString()).intValue();
			cnode.setType((p == 0) ? ProfitLoss.Profit : ProfitLoss.Loss);
		} else if (id.equals(PropertiesID.PROPERTY_TICKSTOWAIT)) {
			TicksEventModel cnode = (TicksEventModel) node;
			cnode.setTicksTH(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_TICKSCONTRARIAN)) {
			TicksEventModel cnode = (TicksEventModel) node;
			cnode.setContrarian(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_ID)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setID(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_QUANTITY)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setQuantity(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_MULTIPLEENTRIES)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setMultipleEntries(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_SINGLEENTRIESSCALES)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setSingleEntriesScales(Utils.parseIntArray(value.toString()));
		} else if (id.equals(PropertiesID.PROPERTY_SL)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setUsingSL(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_LINKEDTOENTRY)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setLinkedToEntry(new Boolean(value.toString()).booleanValue());
		} else if (id.equals(PropertiesID.PROPERTY_ENTRYLINKID)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setEntryLinkID(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_SLSCALE)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setSLScale(Integer.valueOf(value.toString()).intValue());
		} else if (id.equals(PropertiesID.PROPERTY_LIMITCHILD)) {
			EntryEventModel cnode = (EntryEventModel) node;
			cnode.setUsingLimitChild(new Boolean(value.toString()).booleanValue());
		}
	}

}
