/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.jobs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mfg.common.DFSException;
import com.mfg.common.IContract;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.utils.FinancialMath;
import com.mfg.utils.PriceUtils;

/**
 * @author arian
 * 
 */
public abstract class SymbolContractAdapter<T extends SymbolConfiguration<?, ? extends SymbolConfigurationInfo<? extends SymbolData2>>>
		implements IContract {
	@Override
	public int parsePrice(String price) {
		try {
			return FinancialMath.stringPriceToInt(price, getContractScale());
		} catch (DFSException e) {
			throw new RuntimeException(e);
		}
		// return PriceUtils.stringToLong(price, getContractScale());
	}

	@Override
	public String stringifyPrice(int price) {
		return PriceUtils.longToString(price, getContractScale());
	}

	private final T configuration;
	final SymbolData2 symbolData;

	public SymbolContractAdapter(T aConfiguration) {
		super();
		this.configuration = aConfiguration;
		symbolData = aConfiguration.getInfo().getSymbol();
	}

	/**
	 * @return the symbolData
	 */
	public SymbolData2 getSymbolData() {
		return symbolData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.json.JSONString#toJSONString()
	 */
	// @Override
	// public String toJSONString() {
	// throw new UnsupportedOperationException("Not implemented method");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getSymbol()
	 */
	@Override
	public String getSymbol() {
		return configuration.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getLocalSymbol()
	 */
	@Override
	public String getLocalSymbol() {
		return symbolData.getLocalSymbol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getCurrency()
	 */
	@Override
	public String getCurrency() {
		return symbolData.getCurrency();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getTickValue()
	 */
	@Override
	public int getTickValue() {
		return symbolData.getTickValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getContractTick()
	 */
	@Override
	public int getContractTick() {
		Integer tickSize = symbolData.getTickSize();
		return tickSize == null ? 0 : tickSize.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getContractScale()
	 */
	@Override
	public int getContractScale() {
		Integer tickScale = symbolData.getTickScale();
		return tickScale == null ? 0 : tickScale.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#setComputedValues(int, int)
	 */
	@Override
	public void setComputedValues(final int computedTick,
			final int computedScale) {
		final BigDecimal computedRealTick = new BigDecimal(
				BigInteger.valueOf(computedTick), computedScale);
		if (symbolData.isAutoVerifyTickInfo()
				&& symbolData.getRealTickSize() != null) {
			final int tickSize = symbolData.getTickSize().intValue();
			int tickScale = symbolData.getTickScale().intValue();

			if (tickSize != computedTick || computedScale != tickScale) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						Shell shell = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell();
						boolean update = MessageDialog
								.open(MessageDialog.QUESTION,
										shell,
										"Tick Size Determitation",
										"There is an inconsistence between the actual Tick Size ("
												+ symbolData.getRealTickSize()
												+ ") and the computed one ("
												+ computedRealTick
												+ ").\n\nDo you want to update the Tick Size to "
												+ computedRealTick + "?",
										SWT.None);
						if (update) {
							symbolData.setRealTickSize(computedRealTick);
						}
					}
				});
			}
		} else {
			if (symbolData.getRealTickSize() == null) {
				symbolData.setRealTickSize(computedRealTick);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getManualTick()
	 */
	@Override
	public int getManualTick() {
		return getContractTick();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.sdk.IContract#getManualScale()
	 */
	@Override
	public int getManualScale() {
		return getContractScale();
	}

	public T getConfiguration() {
		return configuration;
	}

}
