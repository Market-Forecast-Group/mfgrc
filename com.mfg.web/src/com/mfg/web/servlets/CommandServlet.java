package com.mfg.web.servlets;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class CommandServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(CommandWebsocket.class);
	}

}