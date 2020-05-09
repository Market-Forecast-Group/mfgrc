package com.marketforecastgroup.dfsa.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.mfg.dfs.data.MaturityStats;

public class MaturityEditorInput implements IEditorInput {

	private MaturityStats maturity;

	public MaturityEditorInput(MaturityStats maturity1) {
		this.maturity = maturity1;
	}

	public MaturityStats getMaturity() {
		return maturity;
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
		return maturity.getMaturity() == null ? "Continuous Contract" : maturity.getMaturity()
				.toFileString();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || obj instanceof MaturityEditorInput
				&& maturity.equals(((MaturityEditorInput) obj).maturity);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
