package com.eventorama;

import java.util.concurrent.ArrayBlockingQueue;
import static com.eventorama.ConfigurationParameters.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;

/**
 * helper class which manages the threadpool that contains the workers who doe the actual work
 * TODO make all those final static parameters configurable
 * @author renard
 *
 */
public class AppFactory {

	final private ArrayBlockingQueue<Runnable> workQueue;
	final private ThreadPoolExecutor executor;
	final private AppUploader uploader;
	final private HttpClient client; 



	public AppFactory() throws Exception{
		workQueue = new ArrayBlockingQueue<Runnable>(MAX_QUEUE_SIZE);
		executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue);
		uploader = new S3Uploader();
		client = new HttpClient();
		client.start();
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

}
