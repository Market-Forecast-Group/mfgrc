package com.marketforecastgroup.dfsa.ui;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class SymbolsSorter extends ViewerSorter {

	public SymbolsSorter() {
	}

	public SymbolsSorter(Collator collator1) {
		super(collator1);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return 0;
	}

}
