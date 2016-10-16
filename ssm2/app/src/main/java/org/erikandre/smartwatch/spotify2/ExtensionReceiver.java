/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExtensionReceiver extends BroadcastReceiver {
	
	private static final String TAG = "ExtensionReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive - " + intent.getAction());
		intent.setClass(context, SpotifyExtensionService.class);
        context.startService(intent);
	}

}
