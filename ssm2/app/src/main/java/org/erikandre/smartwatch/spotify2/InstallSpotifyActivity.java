/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import org.erikandre.smartwatch.spotify2.util.SpotifyUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity that is shown if the user has not installed the Spotify for Android
 * application before trying to configure the extension.
 */
public class InstallSpotifyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.install_spotify);

		Button btn = (Button) findViewById(R.id.install_app);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setData(Uri.parse("market://details?id="
						+ SpotifyUtil.DEFAULT_PACKAGE_NAME));
				startActivity(installIntent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// If we are returning here after installing the app
		// we want to go back to the configuration activity
		checkForSpotifyApp();
	}

	/**
	 * Check if the Spotify app is installed and if so return to the config
	 * activity.
	 */
	private void checkForSpotifyApp() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return SpotifyUtil.isAppInstalled(InstallSpotifyActivity.this);
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					Intent intent = new Intent(InstallSpotifyActivity.this,
							ConfigurationActivity.class);
					// If pressing Back in the config activity we do not want
					// to return here.
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					startActivity(intent);
				}
			}

		}.execute();
	}

}
