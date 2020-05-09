package com.mfg.web;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

import com.mfg.web.servlets.HistoricalDataServlet;
import com.mfg.web.servlets.CommandServlet;
import com.mfg.web.servlets.RealTimeDataServlet;
import com.mfg.web.servlets.TestServlet;

public class WebServer {

	private Server _httpServer;
	private List<Listener> _listeners;
	private int _port;

	public WebServer() {
		_listeners = new ArrayList<>();
		_port = 1982;
	}

	public synchronized void addListener(Listener listener) {
		if (_httpServer != null) {
			_httpServer.addLifeCycleListener(listener);
		}
		_listeners.add(listener);

	}

	public synchronized void removeListener(Listener listener) {
		if (_httpServer != null) {
			_httpServer.removeLifeCycleListener(listener);
		}
		_listeners.remove(listener);
	}

	public void stop() throws Exception {
		if (_httpServer != null) {
			_httpServer.stop();
		}
	}

	public void start() throws Exception {
		if (isRunning()) {
			_httpServer.stop();
		}

		_httpServer = new Server(_port);
		_httpServer.setAttribute("useFileMappedBuffer", Boolean.FALSE);
		// add listeners
		for (Listener l : _listeners) {
			_httpServer.addLifeCycleListener(l);
		}
		_httpServer.dumpStdErr();

		// HANDLERS

		// resources
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		// set the folder to serve
		resourceHandler.setResourceBase("platform:/plugin/com.mfg.web/www/app");

		out.println("Serving static files " + resourceHandler.getResourceBase());

		// servlets
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(CommandServlet.class, "/command");
		servletHandler.addServletWithMapping(HistoricalDataServlet.class,
				"/hist-data");
		servletHandler.addServletWithMapping(RealTimeDataServlet.class,
				"/rt-data");
		servletHandler.addServletWithMapping(TestServlet.class, "/test");

		// default
		DefaultHandler defaultHandler = new DefaultHandler();

		// collection
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletHandler,
				defaultHandler });
		_httpServer.setHandler(handlers);

		// configure servlets

		// start server
		_httpServer.start();
		_httpServer.join();
	}

	public Server getHttpServer() {
		return _httpServer;
	}

	public boolean isRunning() {
		return _httpServer != null && _httpServer.isRunning();
	}

	public int getPort() {
		return _port;
	}
}
