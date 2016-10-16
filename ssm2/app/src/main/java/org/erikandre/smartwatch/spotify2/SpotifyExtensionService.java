/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import android.os.Handler;
import android.util.Log;

import com.crittercism.app.Crittercism;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class SpotifyExtensionService extends ExtensionService {
	
	private static final String TAG = "SpotifyExtensionService";
	
	public static final String KEY = "com.sonyericsson.extras.liveware.extension.util.ExtensionService.KEY";

	public SpotifyExtensionService() {
		super(KEY);
	}

	@Override
	protected RegistrationInformation getRegistrationInformation() {
		Log.d(TAG, "getRegInfo");
		return new SpotifyRegistrationInformation(this);
	}

	@Override
	protected boolean keepRunningWhenConnected() {
		return false;
	}
	
	@Override
	public ControlExtension createControlExtension(String hostAppPackageName) {
		return new SpotifyControlExtension(hostAppPackageName, this, new Handler());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Crittercism.init(getApplicationContext(), "502960e2be790e5d58000007");
	}

}
