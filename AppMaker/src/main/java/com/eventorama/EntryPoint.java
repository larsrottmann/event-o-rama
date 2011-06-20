package com.eventorama;

import static com.eventorama.ConfigurationParameters.CORE_POOL_SIZE;
import static com.eventorama.ConfigurationParameters.KEEP_ALIVE_TIME;
import static com.eventorama.ConfigurationParameters.MAX_POOL_SIZE;
import static com.eventorama.ConfigurationParameters.MAX_QUEUE_SIZE;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * main entry point
 * starts up server and takes requests
 * @author renard
 *
 */
public class EntryPoint extends AbstractHandler {
	private static final Logger log = Logger.getLogger(EntryPoint.class);
	final private ArrayBlockingQueue<Runnable> workQueue;
	final private ThreadPoolExecutor executor;
	final private AppUploader uploader;
	final private HttpClient client; 
	
	public EntryPoint() throws Exception{
		workQueue = new ArrayBlockingQueue<Runnable>(MAX_QUEUE_SIZE);
		executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue);
		uploader = new S3Uploader();
		client = new HttpClient();
		client.start();
	}
	
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		try {
			log.info("new app request received");
			startToMakeApp(request);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} catch (AppRequestException e) {
			log.error(e);
			response.setStatus(e.getHttpResponseCode());
		} finally {			
			baseRequest.setHandled(true);
		}

	}
	
	public void startToMakeApp(HttpServletRequest request) throws AppRequestException {
		try {
			addAppRequestToQueue(new AppRequest(request));
		} catch (IllegalArgumentException e) {
			throw new AppRequestException(HttpServletResponse.SC_BAD_REQUEST, e);
		}
	}
	
	private void addAppRequestToQueue(final AppRequest appRequest) throws AppRequestException {
		if (null == appRequest) {
			throw new IllegalArgumentException();
		}
		try {
			executor.execute(new AppMaker(appRequest,uploader,client));
		} catch (RejectedExecutionException e) {
			throw new AppRequestException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

	}

	/**
	 * main entry point
	 */
	public static void main(String[] args) throws Exception {
		ConfigurationParameters.loadPropertiesFromFile();
		
		
		Server server = new Server(8080);

	    ContextHandler context = new ContextHandler();
        context.setContextPath("/appmaker");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(new EntryPoint());
        server.setHandler(context); 

		server.start();
		server.join();
	}
}
