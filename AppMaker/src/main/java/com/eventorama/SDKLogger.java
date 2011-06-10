package com.eventorama;

import org.apache.log4j.Logger;

import com.android.sdklib.ISdkLog;

/**
 * Wraps the log output from the Android SDK tools to log4j
 * @author renard
 *
 */
public class SDKLogger implements ISdkLog {
	
	
	private Logger mLogger;
	private static String DEFAULT_LOGGER_NAME = "AndroidSDK";
	
	public SDKLogger(Class<?> clazz) {
		if (null!=clazz) {
			mLogger = Logger.getLogger(clazz);
		} else {
			mLogger = Logger.getLogger(DEFAULT_LOGGER_NAME);
		}
	}
	
	public SDKLogger(){
		mLogger = Logger.getLogger(DEFAULT_LOGGER_NAME);		
	}

	@Override
	public void error(Throwable t, String errorFormat, Object... args) {
		if (t!=null) {
			mLogger.error(t.getMessage(), t);
		} else if(null!=errorFormat) {
			mLogger.error(String.format(errorFormat, args));
		}
		
	}

	@Override
	public void printf(String msgFormat, Object... args) {
		if(null!=msgFormat) {
			mLogger.info(String.format(msgFormat, args));
		}		
	}

	@Override
	public void warning(String warningFormat, Object... args) {
		if(null!=warningFormat) {
			mLogger.warn(String.format(warningFormat, args));
		}		
	}

}
