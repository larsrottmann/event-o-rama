package com.eventorama;

import java.io.BufferedInputStream;
import static com.eventorama.ConfigurationParameters.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.io.AbstractBuffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

/**
 * runnable which does all the work of creating an apk
 * 
 * @author renard
 * 
 */
class AppMaker implements Runnable {

	private static final Logger log = Logger.getLogger(AppMaker.class);

	private final static String KEY_STORE_PATH_PROP = "key.store";
	private final static String KEY_STORE_ALIAS_PROP = "key.alias";
	private final static String KEY_STORE_PASSWORD_PROP = "key.store.password";
	private final static String KEY_STORE_ALIAS_PASSWORD_PROP = "key.alias.password";
	private final static String DEFAULT_ACTIVITY_NAME = "ACTIVITY_ENTRY_NAME";
	private final static String LIBRARY_ACTIVITY_NAME = "com.eventorama.HelloWorldActivity";
	private final static String BUILD_PROPERTIES_FILE = "build.properties";

	private final AppRequest request;
	private final AppUploader uploader;
	private final HttpClient client;

	AppMaker(AppRequest request, AppUploader uploader, HttpClient client) {
		this.request = request;
		this.uploader = uploader;
		this.client = client;
	}

	@Override
	public void run() {
		File appDir = null;
		try {
			appDir = makeApp();
			verifyApp(appDir);
			addReferenceToLibraryProject(appDir);
			addCustomContent(appDir);
			addReferenceToKeyStore(appDir);
			buildApk(appDir);
			URL url = storeApp(appDir);
			log.info("App was uploaded and is accessbile at: " + url.toString());
			callCallback(true, url, null);
		} catch (IllegalStateException e) {
			log.info("Error creating APP ", e);
			callCallback(false, null, e);
		} finally {
			removeTemporaryFiles(appDir);
		}
	}

	private void removeTemporaryFiles(File appDir) {
		if (appDir != null && appDir.exists()) {
			for (File file : appDir.listFiles()) {
				if (file.isDirectory()) {
					removeTemporaryFiles(file);
				} else {
					file.delete();
				}
			}
			appDir.delete();
		}
	}

	private URL storeApp(File appDir) throws IllegalStateException {
		Date expiration = new Date(request.getEndDate());
		File apkFile = new File(appDir, "bin/" + request.getAppName() + "-release.apk");
		return uploader.upload(apkFile, request.getPackage() + "." + request.getAppName(), expiration);
	}

	private void callCallback(boolean success, URL url, Exception error) throws IllegalStateException {
		log.info("Notifying: " + request.getCallback());

		final ContentExchange exchange = new ContentExchange(true);
		exchange.setURL(request.getCallback());
		exchange.setMethod(HttpMethods.POST);
		exchange.setRequestContentType("application/json;charset=utf-8");
		try {
			exchange.setRequestContent(buildJSONResponse(success, url, error));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		try {
			client.send(exchange);
			exchange.waitForDone();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (InterruptedException ignore) {
		}
	}

	private static AbstractBuffer buildJSONResponse(boolean success, URL appURL, Exception error) throws UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder();
		sb.append("{\"success\" : ").append(success);
		if (success) {
			sb.append(",\"app-url\":").append(appURL.toString()).append("}");
		} else {
			sb.append(",\"reason\":").append(error.getMessage()).append("}");
		}
		AbstractBuffer content = new ByteArrayBuffer(sb.toString().getBytes("UTF-8"));
		return content;
	}

	private void buildApk(File appDir) {
		File buildFile = new File(appDir.getAbsolutePath() + "/build.xml");
		Project p = new Project();
		// System.setProperty("java.home",
		// "/usr/lib/jvm/java-6-sun-1.6.0.25/bin/");
		try {
			p.setUserProperty("ant.file", buildFile.getAbsolutePath());
			p.init();
			p.setBasedir(appDir.getAbsolutePath());
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget("release");
		} catch (BuildException e) {
			throw new IllegalStateException("build error during ", e);
		}
	}

	private void addReferenceToKeyStore(File appDir) throws IllegalStateException {
		Properties buildprops = new Properties();
		String fullpath = appDir.getPath() + "/" + BUILD_PROPERTIES_FILE;
		try {
			buildprops.load(new FileInputStream(fullpath));
		} catch (IOException e) {
			throw new IllegalStateException("could not find " + BUILD_PROPERTIES_FILE);
		}
		buildprops.put(KEY_STORE_PATH_PROP, KEY_STORE_PATH);
		buildprops.put(KEY_STORE_ALIAS_PROP, KEY_STORE_ALIAS);
		buildprops.put(KEY_STORE_ALIAS_PASSWORD_PROP, KEY_STORE_ALIAS_PASSWORD);
		buildprops.put(KEY_STORE_PASSWORD_PROP, KEY_STORE_PASSWORD);

		try {
			buildprops.store(new FileOutputStream(fullpath), null);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("wanted to write but could not find " + BUILD_PROPERTIES_FILE);
		} catch (IOException e) {
			throw new IllegalStateException("could not write " + BUILD_PROPERTIES_FILE);
		}

	}

	private void deleteAllLayoutFiles(File appDir) {
		File layoutDir = new File(appDir, "res/layout");
		for (File layoutFile : layoutDir.listFiles()) {
			layoutFile.delete();
		}
	}

	private void changeAppNameInBuildXml(File appDir) throws IOException {
		File buildxml = new File(appDir, "build.xml");
		String buildString = readFileAsString(buildxml);

		buildString = buildString.replace(DEFAULT_ACTIVITY_NAME, request.getAppName());

		FileWriter writer = new FileWriter(buildxml);
		writer.write(buildString);
		writer.close();

	}

	private void changeStringResources(File appDir) throws IOException {
		File values = new File(appDir, "res/values/strings.xml");
		String valuesString = readFileAsString(values);

		String newEntry = "<string name=\"app_text\">" + request.getAppName() + "</string>";

		valuesString = valuesString.replace("</resources>", newEntry + "\n</resources>");
		valuesString = valuesString.replace(DEFAULT_ACTIVITY_NAME, request.getAppName());

		FileWriter writer = new FileWriter(values);
		writer.write(valuesString);
		writer.close();
	}

	// put com.eventorama.HelloWorldActivity into Androidmanifest.xml
	private void importLibraryActivityIntoManifest(File appDir) throws IOException {
		File manifest = new File(appDir, "AndroidManifest.xml");
		String manifestString = readFileAsString(manifest);

		manifestString = manifestString.replace(DEFAULT_ACTIVITY_NAME, LIBRARY_ACTIVITY_NAME);

		FileWriter writer = new FileWriter(manifest);
		writer.write(manifestString);
		writer.close();
	}

	private static String readFileAsString(File file) throws java.io.IOException {
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(file));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	private void addCustomContent(File appDir) throws IllegalStateException {
		deleteAllLayoutFiles(appDir);
		try {
			changeStringResources(appDir);
		} catch (IOException e) {
			throw new IllegalStateException("could not update values.xml");
		}
		try {
			changeAppNameInBuildXml(appDir);
		} catch (IOException e) {
			throw new IllegalStateException("could not update values.xml");
		}
		try {
			importLibraryActivityIntoManifest(appDir);
		} catch (IOException e) {
			throw new IllegalStateException("could not update AndroidManifest.xml");
		}
	}

	private File makeApp() throws IllegalStateException {
		return SDKUtils.createProject(request.getAppName(), request.getPackage(), request.getSdkVersion());
	}

	private void verifyApp(File app) throws IllegalStateException {
		if (!app.exists()) {
			throw new IllegalStateException("Directory: " + app.getAbsolutePath() + " not created");
		}
	}

	private void addReferenceToLibraryProject(File appDir) throws IllegalStateException {
		if (!SDKUtils.updateProject(appDir)) {
			throw new IllegalStateException("Reference to library project  not added to " + appDir.getAbsolutePath());
		}
	}

}
