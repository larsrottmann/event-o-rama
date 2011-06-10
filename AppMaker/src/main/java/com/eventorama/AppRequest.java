package com.eventorama;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.android.sdklib.internal.project.ProjectCreator;

/**
 * container class which contains all parameters needed to make a new app
 * contains all the sanity checks for the input params
 * 
 * @author renard
 * 
 */
public class AppRequest {
	static class Parameter {
		public final static String CALLBACK = "callback";
		public final static String APP_NAME = "name";
		public final static String PACKAGE_NAME = "package";
		public final static String START_DATE = "start";
		public final static String END_DATE = "end";
		public final static String SDK_VERSION = "sdk-version";
	}

//	private final int MAX_SDK_VERSION = 11;
	private final String pkg;
	private final String callback;
	private final String appName;
	private final long startDate;
	private final long endDate;
//	private final int sdkVersion;

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

//	public int getSdkVersion() {
//		return sdkVersion;
//	}

	AppRequest(HttpServletRequest request) throws IllegalArgumentException {
		String tmpString;
		appName = request.getParameter(Parameter.APP_NAME);
		if (null == appName) {
			throw new IllegalArgumentException(Parameter.APP_NAME + " must be set.");
		}
		if (!ProjectCreator.RE_PROJECT_NAME.matcher(appName).matches()) {
			throw new IllegalArgumentException(Parameter.APP_NAME + " containts invalid characters. Only " + ProjectCreator.CHARS_PROJECT_NAME + " allowed.");
		}
		pkg = request.getParameter(Parameter.PACKAGE_NAME);
		if (null == pkg) {
			throw new IllegalArgumentException(Parameter.PACKAGE_NAME + " must be set.");
		}
		if (!ProjectCreator.RE_PACKAGE_NAME.matcher(pkg).matches()) {
			throw new IllegalArgumentException("Package name " + pkg + " contains invalid characters.\n"
					+ "A package name must be constitued of two Java identifiers.\n" + "Each identifier allowed characters are: "
					+ ProjectCreator.CHARS_PACKAGE_NAME);
		}

//		tmpString = request.getParameter(Parameter.SDK_VERSION);
//		if (null == tmpString) {
//			throw new IllegalArgumentException(Parameter.SDK_VERSION + " must be set.");
//		} else {
//			try {
//				sdkVersion = Integer.parseInt(tmpString);
//			} catch (NumberFormatException e) {
//				throw new IllegalArgumentException(e);
//			}
//		}
//		if (sdkVersion < 4 || sdkVersion > MAX_SDK_VERSION) {
//			throw new IllegalArgumentException(Parameter.SDK_VERSION + " is not in [4," + MAX_SDK_VERSION + "]");
//		}

		tmpString = request.getParameter(Parameter.CALLBACK);
		if (null == tmpString) {
			throw new IllegalArgumentException(Parameter.CALLBACK + " must be set.");
		} else {
			//check format
			try {
				callback = new URL(tmpString).toString();
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		try {
			tmpString = request.getParameter(Parameter.START_DATE);
			startDate = Long.parseLong(tmpString);
			tmpString = request.getParameter(Parameter.END_DATE);
			endDate = Long.parseLong(tmpString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
