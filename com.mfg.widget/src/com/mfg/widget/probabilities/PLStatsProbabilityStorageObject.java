package com.mfg.widget.probabilities;

import com.mfg.persist.interfaces.AbstractStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.widget.WidgetPlugin;

public class PLStatsProbabilityStorageObject extends AbstractStorageObject {
	private ProbabilityElementStorage _info;

	public PLStatsProbabilityStorageObject() {
		super();
	}

	public ProbabilityElementStorage getInfo() {
		return _info;
	}

	public void setInfo(ProbabilityElementStorage info) {
		_info = info;
	}

	@Override
	public String getName() {
		return super.getUUID().toString();
	}

	@Override
	public SimpleStorage<?> getStorage() {
		return WidgetPlugin.getDefault().getProbsStorage();
	}
}
