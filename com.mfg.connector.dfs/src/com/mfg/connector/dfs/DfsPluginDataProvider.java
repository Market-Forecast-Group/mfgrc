package com.mfg.connector.dfs;

import com.mfg.dfs.conn.DfsDataProvider;
import com.mfg.ui.widgets.DfsStatusIndicator;

public class DfsPluginDataProvider extends DfsDataProvider {

	protected DfsPluginDataProvider(boolean useSimulator, boolean dfsRemote,
			String remoteAddress, boolean isMixedMode) {
		super(useSimulator, dfsRemote, remoteAddress, isMixedMode);
	}

	public DfsPluginDataProvider(boolean useSimulator, boolean bridgeOffline) {
		super(useSimulator, bridgeOffline);
	}

	@Override
	public void onConnectionStatusUpdate(ETypeOfData aDataType,
			EConnectionStatus aStatus) {
		DfsStatusIndicator.getInstance().onDFSConnectionStatusChange(aDataType,
				aStatus);

	}

}
