package com.mfg.plstats.ui.editors;

import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMfgTableModel;
import com.mfg.widget.probabilities.DistributionComparison;
import com.mfg.widget.probabilities.DistributionsContainer;

public class ComparisonModel implements IMfgTableModel {

	private StepDefinition step = new StepDefinition(0.0001);
	private DistributionComparison[][] comparison;
	private DistributionsContainer distributionsContainer;

	public ComparisonModel(DistributionComparison[][] aComparison,
			DistributionsContainer aDistributionsContainer) {
		super();
		comparison = aComparison;
		distributionsContainer = aDistributionsContainer;
	}

	@Override
	public boolean isEnabled(int aRow, int aColumn) {
		int localColumn = aColumn;
		if (localColumn == 0)
			return true;
		localColumn--;
		if (localColumn == aRow)
			return false;
		int min = Math.min(aRow, localColumn);
		int max = Math.max(aRow, localColumn);
		DistributionComparison c = comparison[min][max - min - 1];
		return (c != null);
	}

	@Override
	public int getRowCount() {
		if (comparison == null || comparison.length == 0)
			return 0;
		return comparison.length + 1;
	}

	@Override
	public int getHighLight(int aRow, int aColumn) {
		return 0;
	}

	@Override
	public Object getContent(int aRow, int aColumn) {
		int localColumn = aColumn;
		if (localColumn  == 0)
			return getColumnNames()[aRow + 1] + "-";
		localColumn--;
		if (localColumn == aRow)
			return "*";
		int min = Math.min(aRow, localColumn);
		int max = Math.max(aRow, localColumn);
		DistributionComparison c = comparison[min][max - min - 1];
		if (c == null)
			return "*";
		return Double.valueOf(step.round(c.getMSE()));
	}

	@Override
	public String[] getColumnNames() {
		int n = getRowCount() - 1;
		if (n > 0) {
			String[] res = new String[n + 2];
			res[0] = "Scale";
			int start = distributionsContainer.getConfiguration()
					.getStartScale();
			for (int i = 0; i <= n; i++) {
				res[i + 1] = "" + (start + i);
			}
			return res;
		}
		return new String[]{};
	}
}