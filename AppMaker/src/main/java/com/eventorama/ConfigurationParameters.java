package com.eventorama;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigurationParameters {
	private static final Logger log = Logger.getLogger(ConfigurationParameters.class);

	/**
	 * SDK and library Location
	 */
	public static String SDK_DIR = "/home/renard/event-o-rama-devel/android-sdk-linux_x86";
	public static String WORKING_DIR = "/home/renard/event-o-rama-devel/apps-tmp/";
	public static String LIB_DIR = "../libProject"; // absolute path names don't work for some reason
	public static String LIB_DIR_ABS = "/libProject"; // absolute path names don't work for some reason

	/**
	 * Keystore parameters
	 */
	public static String KEY_STORE_PATH = "/opt/renard/event-o-rama-devel/event-o-rama-release-key.keystore";
	public static String KEY_STORE_ALIAS = "event-o-rama";
	public static String KEY_STORE_PASSWORD = "";
	public static String KEY_STORE_ALIAS_PASSWORD = "";

	/**
	 * S3 related parameters
	 */
	public static String BUCKET_NAME = "event-o-rama-apps";
	public static String S3_CREDENTIALS_FILE = "/opt/app-maker/AwsCredentials.properties";

	/**
	 * Thread pool parameters
	 */
	public static int CORE_POOL_SIZE = 2; // Parallel running
											// Threads(Executor) on System

	public static int MAX_POOL_SIZE = 4; // Maximum Threads allowed in
											// Pool

	public static long KEEP_ALIVE_TIME = 10; // Keep alive time for
												// waiting threads for
												// jobs(Runnable)

	public static int MAX_QUEUE_SIZE = 50; // number of tasks to accept
											// before rejecting requests

	private final static String DEFAULT_PROPERTY_FILE_NAME = "/opt/app-maker/default.properties";
	private final static String PROPERTY_FILE_PROPERTY = "APP_MAKER_PROPERTIES";

	private static void logCurrentConfig() {
		log.info("Current configuration is: ");
		log.info("SDK_DIR="+ SDK_DIR);
		log.info("WORKING_DIR="+ WORKING_DIR);
		log.info("LIB_DIR="+ LIB_DIR);
		log.info("LIB_DIR_ABS="+ LIB_DIR_ABS);
		log.info("KEY_STORE_PATH="+ KEY_STORE_PATH);
		log.info("KEY_STORE_ALIAS="+ KEY_STORE_ALIAS);
		log.info("KEY_STORE_PASSWORD="+ KEY_STORE_PASSWORD);
		log.info("KEY_STORE_ALIAS_PASSWORD="+ KEY_STORE_ALIAS_PASSWORD);
		log.info("BUCKET_NAME="+ BUCKET_NAME);
		log.info("S3_CREDENTIALS_FILE="+ S3_CREDENTIALS_FILE);		
		log.info("CORE_POOL_SIZE="+ String.valueOf(CORE_POOL_SIZE));
		log.info("MAX_POOL_SIZE="+ String.valueOf(MAX_POOL_SIZE));
		log.info("KEEP_ALIVE_TIME="+ String.valueOf(KEEP_ALIVE_TIME));
		log.info("MAX_QUEUE_SIZE="+ String.valueOf(MAX_QUEUE_SIZE));				
	}
	
	private static void loadProperties(Properties props) {
		SDK_DIR = props.getProperty("SDK_DIR", SDK_DIR);
		WORKING_DIR = props.getProperty("WORKING_DIR", WORKING_DIR);
		LIB_DIR = props.getProperty("LIB_DIR", LIB_DIR);
		LIB_DIR_ABS = props.getProperty("LIB_DIR_ABS", LIB_DIR_ABS);
		KEY_STORE_PATH = props.getProperty("KEY_STORE_PATH", KEY_STORE_PATH);
		KEY_STORE_ALIAS = props.getProperty("KEY_STORE_ALIAS", KEY_STORE_ALIAS);
		KEY_STORE_PASSWORD = props.getProperty("KEY_STORE_PASSWORD", KEY_STORE_PASSWORD);
		KEY_STORE_ALIAS_PASSWORD = props.getProperty("KEY_STORE_ALIAS_PASSWORD", KEY_STORE_ALIAS_PASSWORD);
		BUCKET_NAME = props.getProperty("BUCKET_NAME", BUCKET_NAME);
		S3_CREDENTIALS_FILE = props.getProperty("S3_CREDENTIALS_FILE", S3_CREDENTIALS_FILE);		
		CORE_POOL_SIZE = Integer.parseInt(props.getProperty("CORE_POOL_SIZE", String.valueOf(CORE_POOL_SIZE)));
		MAX_POOL_SIZE = Integer.parseInt(props.getProperty("MAX_POOL_SIZE", String.valueOf(MAX_POOL_SIZE)));
		KEEP_ALIVE_TIME = Integer.parseInt(props.getProperty("KEEP_ALIVE_TIME", String.valueOf(KEEP_ALIVE_TIME)));
		MAX_QUEUE_SIZE = Integer.parseInt(props.getProperty("MAX_QUEUE_SIZE", String.valueOf(MAX_QUEUE_SIZE)));				
	}

	public static void loadPropertiesFromFile() {
		String propertiesPath = System.getenv(PROPERTY_FILE_PROPERTY);

		if (propertiesPath == null) {
			log.warn("System property \"" + PROPERTY_FILE_PROPERTY + "\" is not set!");
			propertiesPath = DEFAULT_PROPERTY_FILE_NAME;
		}
		log.info("trying to load properties from " + propertiesPath);
		FileInputStream in;
		try {
			in = new FileInputStream(propertiesPath);
		} catch (FileNotFoundException e) {
			log.error("Could not open properties file at: " + propertiesPath, e);
			return;
		}
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			log.error("Could not read properties file at: " + propertiesPath, e);
			logCurrentConfig();
			return;
		} finally {
			try {
				in.close();
			} catch (IOException ignore) {}
		}
		loadProperties(props);
		logCurrentConfig();
	}
}
