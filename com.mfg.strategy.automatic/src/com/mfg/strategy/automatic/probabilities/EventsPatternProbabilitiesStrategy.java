package com.mfg.strategy.automatic.probabilities;

import com.mfg.interfaces.trading.Configuration;
import com.mfg.strategy.ProbabilitiesDealer;
import com.mfg.strategy.automatic.EventsPatternStrategy;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.utils.StepDefinition;
import com.mfg.widget.probabilities.DistributionsContainer;

public class EventsPatternProbabilitiesStrategy extends EventsPatternStrategy {

	private DistributionsContainer distribution;
	private Configuration configuration;
	private ProbabilitiesDealer pdealer;

	public EventsPatternProbabilitiesStrategy() {
	}

	public EventsPatternProbabilitiesStrategy(EventGeneral aEventPatternModel) {
		super(aEventPatternModel);
		pdealer = new ProbabilitiesDealer();
	}

	public EventsPatternProbabilitiesStrategy(EventGeneral aEventPatternModel,
			DistributionsContainer aDistribution) {
		this(aEventPatternModel);
		this.distribution = aDistribution;
		this.configuration = aDistribution.getConfiguration();
	}

	@Override
	public void begin(int aTickSize) {
		super.begin(aTickSize);
		getDistribution().setWidget(widget);
		pdealer.begin(_shell, distribution, configuration);
	}

	@Override
	public void setTick(StepDefinition stepDefinition) {
		super.setTick(stepDefinition);
		pdealer.getCalculator().setTickSize(stepDefinition);
	}

	@Override
	public void newTick() {
		pdealer.dealWithProbabilities();
		super.newTick();
	}

	public DistributionsContainer getDistribution() {
		return distribution;
	}

	public void setDistribution(DistributionsContainer aDistribution) {
		distribution = aDistribution;
		configuration = aDistribution.getConfiguration();
	}

	public ProbabilitiesDealer getProbabilitiesDealer() {
		return pdealer;
	}

	public void addListener(ProbabilitiesDealer.IListener aExecutionRecorder) {
		pdealer.addListener(aExecutionRecorder);
	}

	public static Object getProbabilityElement() {
		// TODO Auto-generated method stub
		return null;
	}
}
