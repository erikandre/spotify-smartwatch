/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import android.content.ContentValues;
import android.content.Context;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

public class SpotifyRegistrationInformation extends RegistrationInformation {

	private final Context mContext;

	public SpotifyRegistrationInformation(Context context) {
		mContext = context;
	}

	@Override
	public int getRequiredNotificationApiVersion() {
		return 0;
	}

	@Override
	public ContentValues getExtensionRegistrationConfiguration() {
		String iconHostapp = ExtensionUtils.getUriString(mContext,
				R.drawable.icon);
		String iconExtension = ExtensionUtils.getUriString(mContext,
				R.drawable.icon);
		String iconExtensionBw = ExtensionUtils.getUriString(mContext,
				R.drawable.icon);

		ContentValues values = new ContentValues();

		values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
				ConfigurationActivity.class.getName());
		values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,
				"Configure extension");
		values.put(Registration.ExtensionColumns.NAME,
				"Spotify Remote");
		values.put(Registration.ExtensionColumns.EXTENSION_KEY,
				SpotifyExtensionService.KEY);
		values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
		values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI,
				iconExtension);
		values.put(
				Registration.ExtensionColumns.EXTENSION_ICON_URI_BLACK_WHITE,
				iconExtensionBw);
		values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
				getRequiredNotificationApiVersion());
		values.put(Registration.ExtensionColumns.PACKAGE_NAME,
				mContext.getPackageName());

		return values;
	}
	
	@Override
	public boolean isDisplaySizeSupported(int width, int height) {
		return true;
	}

	@Override
	public int getRequiredWidgetApiVersion() {
		return 0;
	}

	@Override
	public int getRequiredControlApiVersion() {
		return 1;
	}

	@Override
	public int getRequiredSensorApiVersion() {
		return 0;
	}

}
