package com.eventorama;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * main entry point
 * starts up server and takes requests
 * @author renard
 *
 */
public class EntryPoint extends AbstractHandler {
	private static final Logger log = Logger.getLogger(EntryPoint.class);
	private final AppFactory appFactory;
	
	public EntryPoint() throws Exception{
		appFactory = new AppFactory();
	}
	
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		try {
			log.info("new app request received");
			appFactory.startToMakeApp(request);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} catch (AppRequestException e) {
			log.error(e);
			response.setStatus(e.getHttpResponseCode());
		} finally {			
			baseRequest.setHandled(true);
		}

	}

	public static void main(String[] args) throws Exception {
		ConfigurationParameters.loadPropertiesFromFile();
		
		Server server = new Server(8080);
		server.setHandler(new EntryPoint());

		server.start();
		server.join();
	}
}
