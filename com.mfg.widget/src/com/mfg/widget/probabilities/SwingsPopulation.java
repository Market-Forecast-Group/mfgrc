package com.mfg.widget.probabilities;

import java.util.ArrayList;
import java.util.List;

import com.mfg.common.QueueTick;
import com.mfg.common.Tick;
import com.mfg.dm.TickAdapter;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.IElement;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * contains information about the result of processing the series with the
 * indicator. Most of the information is related to the THs, Pivots, and Targets
 * extensions.
 * 
 * @author gardero
 * 
 */
public class SwingsPopulation extends TickAdapter implements ISwingsSet {

	List<SwingInfo>[] swings;
	private IndicatorParamBean params;
	private IIndicator widget;
	private int[] thcount;
	private boolean ok = false;
	private List<IElement> pop;
	private int[] indexes;
	private ElementsPatterns elementsPatterns;
	private Configuration configuration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.newPobs.ISwingsSet#getSwing(int, int)
	 */
	@Override
	public SwingInfo getSwing(int scale, int index) {
		return swings[scale].get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.newPobs.ISwingsSet#hasScale(int)
	 */
	@Override
	public boolean hasScale(int scale) {
		return scale < swings.length;
	}

	/**
	 * to be called at the start. Initializes all the structures.
	 */
	public void begin() {
		buildWidgets();
		begin(widget);
	}

	/**
	 * to be called at the start. Initializes all the structures.
	 */
	@SuppressWarnings("unchecked")
	public void begin(IIndicator widget1) {
		this.widget = widget1;
		int dim = widget1.getChscalelevels();
		thcount = new int[dim + 1];
		swings = new List[dim + 1];
		for (int i = 0; i < swings.length; i++) {
			swings[i] = new ArrayList<>();
		}
		pop = new ArrayList<>();
		configuration.fixEndScale(widget1.getChscalelevels());
		configuration.fixStartScale(widget1.getStartScaleLevelWidget());

	}

	private void buildWidgets() {
		IndicatorParamBean p = getParams();
		widget = new MultiscaleIndicator(p, null, 0);
		// widget.begin();
	}

	private IndicatorParamBean getParams() {
		if (params == null) {
			// params = Main.theApp.getModel().getSelectedParamBean();
			params = params == null ? new IndicatorParamBean() : params;
		}
		return params;
	}

	@Override
	public void onNewTick(QueueTick qt) {
		int aTime = qt.getFakeTime();
		int aPrice = qt.getPrice();
		// widget.newTickPrice(aTime, aPrice);
		widget.onNewTick(new QueueTick(new Tick(0, aPrice), aTime, true));
		for (int i = widget.getChscalelevels(); i >= widget
				.getStartScaleLevelWidget(); i--) {
			if (widget.isThereANewPivot(i)) {
				thcount[i]++;
				if (thcount[i] >= 2)
					swings[i].add(new WidgetSwingInfo(widget, i));
				if (ok || isOk()) {
					indexes = new int[thcount.length];
					for (int k = 0; k < thcount.length; k++) {
						indexes[k] = swings[k].size() - 1;
					}
					if (i == configuration.getDefaultScale())
						pop.add(new SwingsElement(indexes, this, configuration
								.getType()));
				}
			}
		}
	}

	public void printStats() {
		System.out.println("==============================================");
		System.out.println("TH on scale "
				+ thcount[configuration.getDefaultScale()]);
		System.out.println("Population of " + pop.size());
		System.out.println("==============================================");
	}

	private boolean isOk() {
		// for (int i = widget.getChscalelevels(); i >=
		// widget.getStartScaleLevelWidget(); i--) {
		// if (thcount[i] < 5)
		// return false;
		// }
		ok = thcount[configuration.getDefaultScale()] >= configuration
				.getMaxRatioLevel() + 2;
		return ok;
	}

	/**
	 * computes the patterns of this series.
	 */
	public void buildProbabilities() {
		elementsPatterns = new ElementsPatterns(pop, configuration);
		elementsPatterns.printMe(" ");
	}

	/**
	 * gets the resulting patterns tree.
	 * 
	 * @return the root of the patterns tree.
	 */
	public ElementsPatterns getElementsPatterns() {
		return elementsPatterns;
	}

	/***
	 * gets the population of swings.
	 * 
	 * @return
	 */
	public List<IElement> getPopulation() {
		return pop;
	}

	public void setConfiguration(Configuration aConfig) {
		configuration = aConfig;
	}

	@Override
	public void onStarting(int tick, int scale) {
		// nothing
	}

	// @Override
	// public void onNoNewTick() {
	// // nothing
	//
	// }

	@Override
	public void onStopping() {
		// nothing
	}

}
