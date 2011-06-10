package com.eventorama;

import static com.eventorama.ConfigurationParameters.LIB_DIR;
import static com.eventorama.ConfigurationParameters.SDK_DIR;
import static com.eventorama.ConfigurationParameters.WORKING_DIR;

import java.io.File;
import java.util.UUID;

import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkManager;
import com.android.sdklib.internal.project.ProjectCreator;
import com.android.sdklib.internal.project.ProjectCreator.OutputLevel;

/***
 * wrapper for SDK functions
 * @author renard
 *
 */
public class SDKUtils {
//	private final static String TOOLS_DIR = SDK_DIR + "/tools";
	private static SdkManager mSdkManager;
	private static ProjectCreator mProjectCreator;
	
	static {
		mSdkManager = SdkManager.createManager(SDK_DIR, new SDKLogger(SdkManager.class));
		mProjectCreator = new ProjectCreator(mSdkManager, SDK_DIR, OutputLevel.VERBOSE, new SDKLogger(ProjectCreator.class));
	}
	

	private static String buildProjectDirectoryName(final String appName, final String packageName, final IAndroidTarget target) {
		StringBuilder sb =  new StringBuilder();
		sb.append(WORKING_DIR);
		if (!WORKING_DIR.endsWith("/")){
			sb.append("/");
		}
		sb.append(UUID.randomUUID().toString());
		return sb.toString();
	}
	
	
	private static IAndroidTarget getAndroidTarget(IAndroidTarget[] targets, int target) throws IllegalStateException{
		for (IAndroidTarget androidTarget : targets) {
			if (androidTarget.getVersion().getApiLevel()==target){
				return androidTarget;
			}
		}
		throw new IllegalStateException();
	}
	
	/**
	 * creates a new android project. 
	 * assumes sanitized input
	 */
	public static File createProject(final String appName, final String packageName, final int target) throws IllegalStateException{
		IAndroidTarget[] targets = mSdkManager.getTargets();
		IAndroidTarget androidtarget = getAndroidTarget(targets, target);
		String appDir = buildProjectDirectoryName(appName, packageName, androidtarget);
		
		mProjectCreator.createProject(appDir,
				appName, packageName, 
				null, /* activity */
				androidtarget, /* sdk target */
				false, /* islibrary */
				null /* pathToMain */);
		
		return new File(appDir);		
	}
	
	/**
	 * adds a reference to the event-o-rama library project
	 * @param appDir existing and valid Android project in folder
	 * @return true on success
	 */
	public static boolean updateProject(File appDir) {
		return mProjectCreator.updateProject(appDir.getAbsolutePath(), null, null, LIB_DIR);
	}
	
}
