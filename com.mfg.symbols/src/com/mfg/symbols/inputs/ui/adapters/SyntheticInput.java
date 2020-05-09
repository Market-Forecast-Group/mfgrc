package com.mfg.symbols.inputs.ui.adapters;

import com.mfg.chart.ui.views.IAlternativeChartContent;
import com.mfg.symbols.inputs.configurations.InputConfiguration;

public class SyntheticInput implements IAlternativeChartContent {
	private InputConfiguration _configuration;

	public SyntheticInput(InputConfiguration configuration) {
		_configuration = configuration;
	}

	public InputConfiguration getConfiguration() {
		return _configuration;
	}

	@Override
	public Object getAlternativeContent(Object arg) {
		return _configuration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_configuration == null) ? 0 : _configuration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyntheticInput other = (SyntheticInput) obj;
		if (_configuration == null) {
			if (other._configuration != null)
				return false;
		} else if (!_configuration.equals(other._configuration))
			return false;
		return true;
	}

}
