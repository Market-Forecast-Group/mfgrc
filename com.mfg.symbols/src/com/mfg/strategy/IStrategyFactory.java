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
package com.mfg.strategy;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;

import com.mfg.logger.application.IAppLogger;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.IDashboardWidgetProvider;
import com.thoughtworks.xstream.XStream;

/**
 * @author arian
 * 
 */
public interface IStrategyFactory extends IExecutableExtension {
	public static String EXTENSION_NAME = "strategyFactory";

	public static class CreateSettingsEditorArgs {
		private final Composite parent;
		private DataBindingContext[] bindings;
		private IStrategySettings settings;

		public CreateSettingsEditorArgs(Composite aParent,
				IStrategySettings aSettings) {
			super();
			this.parent = aParent;
			this.settings = aSettings;
			this.bindings = new DataBindingContext[0];
		}

		public Composite getParent() {
			return parent;
		}

		/**
		 * @return the settings
		 */
		public IStrategySettings getSettings() {
			return settings;
		}

		/**
		 * @param aSettings
		 *            the settings to set
		 */
		public void setSettings(IStrategySettings aSettings) {
			this.settings = aSettings;
		}

		public void setBindings(DataBindingContext... aBindings) {
			this.bindings = aBindings;
		}

		public DataBindingContext[] getBindings() {
			return bindings;
		}
	}

	public static class CreateStrategyArgs {
		private final IStrategySettings settings;
		private final IAppLogger logger;

		public CreateStrategyArgs(IStrategySettings aSettings, IAppLogger aLogger) {
			super();
			this.settings = aSettings;
			this.logger = aLogger;
		}

		public IStrategySettings getSettings() {
			return settings;
		}

		public IAppLogger getLogger() {
			return logger;
		}

	}

	public static class CreateTradingPageActionsArgs {
		private final TradingConfiguration configuration;

		public CreateTradingPageActionsArgs(TradingConfiguration aConfiguration) {
			super();
			this.configuration = aConfiguration;
		}

		/**
		 * @return the configuration
		 */
		public TradingConfiguration getConfiguration() {
			return configuration;
		}
	}

	public String getId();

	public String getName();

	public IStrategySettings createDefaultSettings();

	public void configureXStream(XStream xstream);

	public void createSettingsEditor(CreateSettingsEditorArgs args);

	public Action[] createTradingPageActions(CreateTradingPageActionsArgs args);

	public FinalStrategy createStrategy(CreateStrategyArgs args);
	
	public IDashboardWidgetProvider createDashboardWidget(TradingConfiguration conf);
}
