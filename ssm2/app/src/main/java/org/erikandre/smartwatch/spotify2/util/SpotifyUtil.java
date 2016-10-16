/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class SpotifyUtil {
	
	private static final String[] PACKAGE_NAMES = {"com.spotify.mobile.android.ui", "com.spotify.music"};

    public static final String DEFAULT_PACKAGE_NAME = "com.spotify.music";

	private static boolean sInjectedValue;
	
	private static boolean sValueWasInjected = false;

    public static String getInstalledPackageName(Context context) {
        for (String pkg : PACKAGE_NAMES) {
            try {
                context.getPackageManager().getApplicationInfo(pkg, 0 );
                return pkg;
            } catch (NameNotFoundException e) {
                // The app is not installed
            }
        }
        return null;
    }

	/**
	 * Checks whether or not the Spotify for Android is installed
	 * on the device.
	 * 
	 * @param context Context used to perform the check.
	 * @return true if the app is installed, otherwise false.
	 */
	public static boolean isAppInstalled(Context context) {
		if (sValueWasInjected) {
			// Used for testing only
			return sInjectedValue;
		}
        boolean installed = false;
        for (String pkg : PACKAGE_NAMES) {
            try {
                context.getPackageManager().getApplicationInfo(pkg, 0 );
                installed = true;
            } catch (NameNotFoundException e) {
                // The app is not installed
            }
        }
		return installed;
	}
	
	/**
	 * Injects a test value to be return by isAppInstalled()
	 * 
	 * NOTE: Must only be called from test cases!
	 * 
	 * @param installed True if the app is installed, false otherwise.
	 */
	public static void injectTestAppInstalledValue(boolean installed) {
		sValueWasInjected = true;
		sInjectedValue = installed;
	}
	
	/**
	 * Clears the value previously injected using injectTestAppInstalledValue
	 * 
	 * NOTE: Must only be called from test cases!
	 */
	public static void clearInjectedTestAppInstalledValue() {
		sValueWasInjected = false;
	}

}
