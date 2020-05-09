package com.mfg.logger.ui.views;

import org.eclipse.swt.graphics.Image;

public interface ILogClient {
	public String getName();

	public Image getImage();

	public void logConnected(ILogView view);

	public void logSelectionChanged(long time, long price);

	public void logDisposed(ILogView view);

	public void disconnect();
}
