package com.mfg.widget.probabilities;

import com.mfg.interfaces.probabilities.ElementsPatterns;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.interfaces.trading.Configuration;

public class DistributionsDistances {

	private DistributionsContainer distributionsContainer;

	public DistributionsDistances(DistributionsContainer aDistributionsContainer) {
		super();
		distributionsContainer = aDistributionsContainer;
	}

	public DistributionComparison comprare(ProbabilitiesKey key1,
			ProbabilitiesKey key2) {
		System.out.println("compare " + key1);
		if (distributionsContainer.contains(key1)
				&& distributionsContainer.contains(key2)) {
			double[] aDistribution1 = distributionsContainer
					.getStaticTargetsProbabilitiesArray(key1);
			System.out.println("to " + key2);
			double[] aDistribution2 = distributionsContainer
					.getStaticTargetsProbabilitiesArray(key2);
			return new DistributionComparison(aDistribution1, aDistribution2);
		}
		return null;
	}

	public DistributionComparison[][] comprareScales(ElementsPatterns pattern) {
		Configuration configuration = distributionsContainer.getConfiguration();
		int start = configuration.getStartScale();
		int n = configuration.getEndScale() - start;
		DistributionComparison[][] res = new DistributionComparison[n][];
		for (int scale1 = start; scale1 < configuration.getEndScale(); scale1++) {
			res[scale1 - start] = new DistributionComparison[configuration
					.getEndScale() - scale1];
			for (int scale2 = scale1 + 1; scale2 <= configuration.getEndScale(); scale2++) {
				res[scale1 - start][scale2 - scale1 - 1] = comprare(
						new ProbabilitiesKey(scale1, pattern,
								configuration.getType()), new ProbabilitiesKey(
								scale2, pattern, configuration.getType()));
			}
		}
		return res;
	}
}
