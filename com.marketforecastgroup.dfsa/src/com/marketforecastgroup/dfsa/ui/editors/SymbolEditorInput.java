package com.marketforecastgroup.dfsa.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.mfg.common.DfsSymbol;

public class SymbolEditorInput implements IEditorInput {
	private DfsSymbol symbol;

	public SymbolEditorInput(DfsSymbol symbol1) {
		this.symbol = symbol1;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return symbol.prefix;
	}

	public DfsSymbol getSymbol() {
		return symbol;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return symbol.prefix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SymbolEditorInput) {
			return ((SymbolEditorInput) obj).getSymbol().prefix
					.equals(symbol.prefix);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
