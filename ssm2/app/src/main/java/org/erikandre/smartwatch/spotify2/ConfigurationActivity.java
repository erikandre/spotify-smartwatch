/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import java.util.ArrayList;

import org.erikandre.smartwatch.spotify2.util.Preferences;
import org.erikandre.smartwatch.spotify2.util.SpotifyUtil;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConfigurationActivity extends Activity {

	private static final String TAG = "SpotifyRemoteConfiguration";

	AppWidgetManager mAppWidgetManager;

	AppWidgetHost mAppWidgetHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

		setContentView(R.layout.configure_extension);

		Button btn = (Button) findViewById(R.id.pick_widget);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start the widget picker
				int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
				Intent pickIntent = new Intent(
						AppWidgetManager.ACTION_APPWIDGET_PICK);
				pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				addEmptyData(pickIntent);
				startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// If the Spotify application is not installed we need to
		// inform the user that it is a requirement to use this extension.
		checkForSpotifyApp();
	}

	/**
	 * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
	 * which is used to select widgets. This just adds empty extras to the
	 * intent, avoiding the bug.
	 * 
	 * See more: http://code.google.com/p/android/issues/detail?id=4272
	 */
	void addEmptyData(Intent pickIntent) {
		ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
		pickIntent.putParcelableArrayListExtra(
				AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
		ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
		pickIntent.putParcelableArrayListExtra(
				AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
				Log.d(TAG, "Picked widget");
				configureWidget(data);
			} else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
				Log.d(TAG, "Created widget");
				createWidget(data);
			}
			Toast.makeText(this, R.string.config_done_toast, Toast.LENGTH_SHORT)
					.show();
			finish();
		} else if (resultCode == RESULT_CANCELED && data != null) {
			int appWidgetId = data.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if (appWidgetId != -1) {
				mAppWidgetHost.deleteAppWidgetId(appWidgetId);
			}
		}
	}

	/**
	 * Check if the Spotify app is installed and if not
	 * then prompt the user to install the app.
	 */
	private void checkForSpotifyApp() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return SpotifyUtil.isAppInstalled(ConfigurationActivity.this);
			}

			protected void onPostExecute(Boolean result) {
				if (!result) {
					startActivity(new Intent(ConfigurationActivity.this,
							InstallSpotifyActivity.class));
				}
			}

		}.execute();
	}

	/**
	 * Checks if the widget needs any configuration. If it needs, launches the
	 * configuration activity.
	 */
	private void configureWidget(Intent data) {
		Bundle extras = data.getExtras();
		int appWidgetId = extras
				.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		if (appWidgetInfo != null && appWidgetInfo.configure != null) {
			Log.d(TAG, "Need to configure widget");
			Intent intent = new Intent(
					AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidgetInfo.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
		} else {
			Log.d(TAG, "Calling create widget");
			createWidget(data);
		}
	}

	public void createWidget(Intent data) {
		Bundle extras = data.getExtras();
		int appWidgetId = extras
				.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		Log.d(TAG, "AppWidgetId=" + appWidgetId);
		Preferences.setWidgetId(this, appWidgetId);
	}

}
