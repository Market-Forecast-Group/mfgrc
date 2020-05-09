package com.mfg.symbols.ui.views;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.mfg.symbols.ui.widgets.ConfigurationFullnameComparator;

public class ConfigurationSorter extends ViewerSorter {

	private final ConfigurationFullnameComparator comp;

	public ConfigurationSorter() {
		this(Collator.getInstance());
	}

	public ConfigurationSorter(Collator aCollator) {
		super(aCollator);
		comp = new ConfigurationFullnameComparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return super.compare(o1, o2);
			}
		};
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return comp.compare(e1, e2);
	}

}
