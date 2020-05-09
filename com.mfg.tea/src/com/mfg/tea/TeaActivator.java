package com.mfg.tea;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mfg.common.TEAException;
import com.mfg.tea.conn.TEAFactory;
import com.mfg.tea.db.Db;
import com.mfg.utils.U;

public class TeaActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		TeaActivator.context = bundleContext;
		Db.i().beginTransaction();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		TeaActivator.context = null;

		try {
			TEAFactory.disposeServer();
		} catch (TEAException e) {
			U.debug_var(918145, "Caught exception ", e.toString(),
					" while trying to dispose server");
			e.printStackTrace();
		}
	}

}
