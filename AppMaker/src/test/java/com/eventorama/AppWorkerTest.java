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
import org.eclipse.jetty.server.handler.ContextHandler;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppWorkerTest {

	protected static final String ENDPOINT = "http://localhost:8080/appmaker/";
	//protected static final String ENDPOINT = "http://ec2-184-73-49-245.compute-1.amazonaws.com:8080/appmaker/";
	protected static final String CALLBACK_ENDPOINT = "http://localhost:8080/appmaker/callback/";
	protected static Server server;
	protected static HttpClient client;
	protected static EntryPoint entryPoint;
	private final static long TWO_WEEKS = 1000 * 60 * 60 * 24 * 14;
	private static boolean success = false;

	private static final Object lock = new Object();

	private static class TestHandler extends AbstractHandler {

		@Override
		public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
				throws IOException, ServletException {

			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			baseRequest.setHandled(true);

			if (target.endsWith("callback/")) {
				synchronized (lock) {
					success = true;
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
		success = false;
		entryPoint = new EntryPoint();
		System.out.println("starting local server for testing");
		final Thread serverThread = new Thread() {

			@Override
			public void run() {
				server = new Server(8080);
				ContextHandler context = new ContextHandler();
				context.setContextPath("/appmaker");
				context.setClassLoader(Thread.currentThread().getContextClassLoader());
				server.setHandler(context);
				context.setHandler(new TestHandler());
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

	@SuppressWarnings("unchecked")
	private static AbstractBuffer makeContent() throws UnsupportedEncodingException {
		String appName = "helloWorld";
		String packageName = "com.eventorama.t" + Math.round(Math.random() * Integer.MAX_VALUE);
		long startDate = System.currentTimeMillis();
		long endDate = startDate + TWO_WEEKS;

		JSONObject o = new JSONObject();
		o.put(AppRequest.Parameter.APP_NAME, appName);
		o.put(AppRequest.Parameter.PACKAGE_NAME, packageName);
		o.put(AppRequest.Parameter.START_DATE, startDate);
		o.put(AppRequest.Parameter.END_DATE, endDate);
		
		//String tmp = "{\"active\":false,\"expirationDate\":1309903200000,\"key\":\"a.gxldmVudC1vLXJhbWFyEgsSC0FwcGxpY2F0aW9uGMMDDA\",\"startDate\":1306706400000,\"title\":\"testing\",\"user\":{\"authDomain\":\"gmail.com\",\"email\":\"test@example.com\",\"userId\":\"18580476422013912411\"},\"version\":1}\"";

		AbstractBuffer content = new ByteArrayBuffer(o.toJSONString().getBytes("UTF-8"));
		return content;
		//return new ByteArrayBuffer(tmp.getBytes("UTF-8"));
	}

	@Test
	public void testCreateProject() throws IOException, InterruptedException {
		final ContentExchange exchange = new ContentExchange(true);
		exchange.setURL(ENDPOINT);
		exchange.setMethod(HttpMethods.POST);
		exchange.addRequestHeader(AppRequest.Parameter.CALLBACK, CALLBACK_ENDPOINT);
		// exchange.setRequestContentType("application/json;charset=utf-8");
		exchange.setRequestContent(makeContent());
		client.send(exchange);
		final int exchangeState = exchange.waitForDone();
		if (exchangeState == HttpExchange.STATUS_COMPLETED) {

			Assert.assertEquals(202, exchange.getResponseStatus());

			exchange.getResponseContentBytes();
		} else {
			throw new IllegalStateException("Error requesting \"" + ENDPOINT + "\" - state: " + exchangeState);
		}
		// now wait for callback
		synchronized (lock) {
			lock.wait();
		}
		Assert.assertTrue(success);
	}

}
