package com.eventorama;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.io.AbstractBuffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppWorkerTest {

	protected static final String endPoint = "http://localhost:8080/";
	protected static final String callbackEndPoint = "http://localhost:8080/callback";
	protected static Server server;
	protected static HttpClient client;
	protected static EntryPoint entryPoint;
	private final static long TWO_WEEKS = 1000 * 60 * 60 * 60 * 24 * 14;

	private static final Object lock = new Object();

	private static class TestHandler extends AbstractHandler {

		@Override
		public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
			if (target.equals("/callback")) {
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				baseRequest.setHandled(true);
				synchronized (lock) {
					lock.notify();
				}
			} else {
				entryPoint.handle(target, baseRequest, request, response);
			}

		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		client.stop();
		if (server != null) {
			server.stop();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConfigurationParameters.loadPropertiesFromFile();
		entryPoint = new EntryPoint();
		System.out.println("starting local server for testing");
		final Thread serverThread = new Thread() {

			@Override
			public void run() {
				server = new Server(8080);
				server.setHandler(new TestHandler());

				try {
					server.start();
					synchronized (lock) {
						lock.notify();
					}
				} catch (final Exception e) {
					throw new RuntimeException("Error starting local server", e);
				}
			}
		};
		serverThread.start();
		synchronized (lock) {
			lock.wait();
		}
		client = new HttpClient();
		client.start();
	}

	private static AbstractBuffer makeContent() throws UnsupportedEncodingException {
		String appName = "helloWorld";
		String packageName = "com.eventorama.t" + Math.round(Math.random() * Integer.MAX_VALUE);
		long startDate = System.currentTimeMillis();
		long endDate = startDate + TWO_WEEKS;

		StringBuilder sb = new StringBuilder();
		sb.append(AppRequest.Parameter.CALLBACK).append("=").append(callbackEndPoint);
		sb.append("&").append(AppRequest.Parameter.APP_NAME).append("=").append(appName);
		sb.append("&").append(AppRequest.Parameter.PACKAGE_NAME).append("=").append(packageName);
		sb.append("&").append(AppRequest.Parameter.START_DATE).append("=").append(startDate);
		sb.append("&").append(AppRequest.Parameter.END_DATE).append("=").append(endDate);
		sb.append("&").append(AppRequest.Parameter.SDK_VERSION).append("=").append(20);
		AbstractBuffer content = new ByteArrayBuffer(sb.toString().getBytes("UTF-8"));
		return content;
	}

	@Test
	public void testCreateProject() throws IOException, InterruptedException {
		final ContentExchange exchange = new ContentExchange(true);
		exchange.setURL(endPoint);
		exchange.setMethod(HttpMethods.POST);
		exchange.setRequestContentType("application/x-www-form-urlencoded;charset=utf-8");
		exchange.setRequestContent(makeContent());
		client.send(exchange);
		final int exchangeState = exchange.waitForDone();
		if (exchangeState == HttpExchange.STATUS_COMPLETED) {

			Assert.assertEquals(202, exchange.getResponseStatus());

			exchange.getResponseContentBytes();
		} else {
			throw new IllegalStateException("Error requesting \"" + endPoint + "\" - state: " + exchangeState);
		}
		// now wait for callback
		synchronized (lock) {
			lock.wait();
		}

	}

}
