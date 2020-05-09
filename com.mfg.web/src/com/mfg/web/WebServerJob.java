package com.mfg.web;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

final class WebServerJob extends Job {
	private WebServer _jobServer;

	public WebServerJob() {
		super("Web Server  [port: " + getServer().getPort() + "]");
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			_jobServer = getServer();
			monitor.beginTask("Running web server at " + _jobServer.getPort(),
					2);
			_jobServer.start();
			return Status.OK_STATUS;
		} catch (Exception e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, WebPlugin.PLUGIN_ID,
					e.getMessage(), e);
		}
	}

	private static WebServer getServer() {
		return WebPlugin.getDefault().getServer();
	}

	@Override
	protected void canceling() {
		super.canceling();
		try {
			_jobServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}