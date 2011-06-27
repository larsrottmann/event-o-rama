package com.eventorama;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.android.sdklib.internal.project.ProjectCreator;

/**
 * container class which contains all parameters needed to make a new app
 * contains all the sanity checks for the input params
 * 
 * @author renard
 * 
 */
public class AppRequest {
	
	private static final Logger log = Logger.getLogger(AppRequest.class);
	private static Pattern RE_APP_NAME = Pattern.compile("^[0-9a-zA-Z\\.\\-\\s]{3,20}$");

	static class Parameter {
		public final static String CALLBACK = "x-eventorama-callback";
		public final static String APP_NAME = "title";
		public final static String PACKAGE_NAME = "key";
		public final static String START_DATE = "startDate";
		public final static String END_DATE = "expirationDate";
		// public final static String SDK_VERSION = "sdk-version";
	}

	// private final int MAX_SDK_VERSION = 11;
	private final String pkg;
	private final String callback;
	private final String appName;
	private final long startDate;
	private final long endDate;
	private final String projectName = "EVENTORAMA";

	// private final int sdkVersion;

	public String getCallback() {
		return callback;
	}

	public String getAppName() {
		return appName;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public UUID getUUID() {
		return null;
	}

	public String getPackage() {
		return pkg;
	}

	AppRequest(HttpServletRequest request) throws IllegalArgumentException {
		String tmpString = request.getHeader(Parameter.CALLBACK);
		if (null == tmpString) {
			throw new IllegalArgumentException(Parameter.CALLBACK + " must be set.");
		} else {
			// check format
			try {
				callback = new URL(tmpString).toString();
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}

		try {
			JSONObject o = (JSONObject) JSONValue.parse(request.getReader());
			log.info(o.toJSONString());
			
			appName = (String) o.get(Parameter.APP_NAME);
			if (null == appName) {
				throw new IllegalArgumentException(Parameter.APP_NAME + " must be set.");
			}
			if (!RE_APP_NAME.matcher(appName).matches()) {
				throw new IllegalArgumentException(Parameter.APP_NAME + " invalid");
			}
			tmpString = (String) o.get(Parameter.PACKAGE_NAME);
			if (null == tmpString) {
				throw new IllegalArgumentException(Parameter.PACKAGE_NAME + " must be set.");
			}
			pkg = "com.appspot.eventorama.app" + tmpString;
			if (!ProjectCreator.RE_PACKAGE_NAME.matcher(pkg).matches()) {
				throw new IllegalArgumentException("Package name " + pkg + " contains invalid characters.\n"
						+ "A package name must be constitued of two Java identifiers.\n" + "Each identifier allowed characters are: "
						+ ProjectCreator.CHARS_PACKAGE_NAME);
			}
			Number tmpNumber = (Number) o.get(Parameter.START_DATE);
			startDate = tmpNumber.longValue();
			tmpNumber = (Number) o.get(Parameter.END_DATE);
			endDate = tmpNumber.longValue();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getProjectName() {
		return projectName;
	}

}
