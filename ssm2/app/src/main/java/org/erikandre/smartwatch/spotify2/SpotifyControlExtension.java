/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import org.erikandre.smartwatch.spotify2.util.Preferences;
import org.erikandre.smartwatch.spotify2.util.SpotifyUtil;
import org.erikandre.smartwatch.spotify2.util.WidgetUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.control.Control.Intents;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

public class SpotifyControlExtension extends ControlExtension {

    private static final String TAG = "SpotifyControlExtension";

    private Bitmap mBitmap;

    private Canvas mCanvas;

    private final Handler mHandler;

    private int width;

    private int height;

    private ViewGroup mPlaybackView;

    private ViewGroup mNotConfiguredView;

    private ControlState mControlState = ControlState.NOT_CONFIGURED;

    private enum ControlState {
        NOT_CONFIGURED, NO_CONTENT, PLAYBACK
    }

    public SpotifyControlExtension(final String hostAppPackageName,
                                   final Context context, Handler handler) {
        super(context, hostAppPackageName);
        mHandler = handler;

        width = getSupportedControlWidth(context);
        height = getSupportedControlHeight(context);

        mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        mCanvas = new Canvas(mBitmap);

        mPlaybackView = new LinearLayout(mContext);
        mPlaybackView.setLayoutParams(new LayoutParams(width, height));
        LayoutInflater.from(mContext).inflate(org.erikandre.smartwatch.spotify2.R.layout.control_playback,
            mPlaybackView);

        mNotConfiguredView = new LinearLayout(mContext);
        mNotConfiguredView.setLayoutParams(new LayoutParams(width, height));
        LayoutInflater.from(mContext).inflate(org.erikandre.smartwatch.spotify2.R.layout.control_not_configured,
            mNotConfiguredView);
    }

    @Override
    public void onResume() {
        mHandler.post(new UpdateControl());
    }

    @Override
    public void onTouch(ControlTouchEvent event) {
        int action = event.getAction();
        if (mControlState == ControlState.PLAYBACK) {
            if (action == Control.Intents.TOUCH_ACTION_PRESS) {
                Intent playIntent = new Intent(
                    "com.spotify.mobile.android.ui.widget.PLAY");
                mContext.sendBroadcast(playIntent);
                mHandler.postDelayed(new UpdateControl(), 200);
            }
        }
        else if (mControlState == ControlState.NOT_CONFIGURED) {
            if (action == Control.Intents.TOUCH_ACTION_PRESS) {
                // Check if we are in the wrong state
                if (Preferences.getWidgetId(mContext) != -1) {
                    mHandler.postDelayed(new UpdateControl(), 200);
                    mControlState = ControlState.PLAYBACK;
                }
                Intent configIntent = new Intent(mContext, ConfigurationActivity.class);
                configIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(configIntent);
            }
        }
        else if (mControlState == ControlState.NO_CONTENT) {
            if (action == Control.Intents.TOUCH_ACTION_PRESS) {
                // Launch Spotify
                Intent spotifyIntent = new Intent(Intent.ACTION_MAIN);
                spotifyIntent.setPackage(SpotifyUtil.getInstalledPackageName(mContext));
                spotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(spotifyIntent);
            }
        }
    }

    @Override
    public void onSwipe(int direction) {
        if (mControlState != ControlState.PLAYBACK) {
            // Only supported in Playback mode
            return;
        }
        if (direction == Intents.SWIPE_DIRECTION_LEFT) {
            // Next track
            // com.spotify.mobile.android.ui.widget.NEXT
            Intent intent = new Intent(
                "com.spotify.mobile.android.ui.widget.NEXT");
            intent.putExtra("is_suggested_track", false);
            mContext.sendBroadcast(intent);
            mHandler.postDelayed(new UpdateControl(), 300);
            mHandler.postDelayed(new UpdateControl(), 1500);
            mHandler.postDelayed(new UpdateControl(), 3000);
            mHandler.postDelayed(new UpdateControl(), 10000);
        }
        else if (direction == Intents.SWIPE_DIRECTION_RIGHT) {
            // Previous track
            Intent intent = new Intent(
                "com.spotify.mobile.android.ui.widget.PREVIOUS");
            mContext.sendBroadcast(intent);
            mHandler.postDelayed(new UpdateControl(), 300);
            mHandler.postDelayed(new UpdateControl(), 1500);
            mHandler.postDelayed(new UpdateControl(), 3000);
            mHandler.postDelayed(new UpdateControl(), 10000);
        }
        else if (direction == Intents.SWIPE_DIRECTION_UP) {
            // Increase volume
            AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, 0);
        }
        else if (direction == Intents.SWIPE_DIRECTION_DOWN) {
            // Decrease volume
            AudioManager audioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, 0);
        }
    }

    /**
     * Get supported control width.
     *
     * @param context The context.
     * @return the width.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(
            org.erikandre.smartwatch.spotify2.R.dimen.smart_watch_control_width);
    }

    /**
     * Get supported control height.
     *
     * @param context The context.
     * @return the height.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(
            org.erikandre.smartwatch.spotify2.R.dimen.smart_watch_control_height);
    }

    private class UpdateControl implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "Starting update of control extension");
            int widgetId = Preferences.getWidgetId(mContext);

            if (widgetId != -1) {
                // Widget is loaded
                Log.d(TAG, "Control in Playback mode");
                mControlState = ControlState.PLAYBACK;
                WidgetData data = WidgetUtil.getWidgetData(mContext, widgetId);

                if (data.album != null) {
                    ImageView albumArt = (ImageView) mPlaybackView
                        .findViewById(org.erikandre.smartwatch.spotify2.R.id.album_art);
                    albumArt.setImageDrawable(data.album);
                }

                TextView noContent = (TextView) mPlaybackView
                    .findViewById(org.erikandre.smartwatch.spotify2.R.id.no_content);
                if (data.trackName != null && !data.trackName.isEmpty()) {
                    TextView trackName = (TextView) mPlaybackView
                        .findViewById(org.erikandre.smartwatch.spotify2.R.id.track_name);
                    trackName.setText(data.trackName);
                    noContent.setVisibility(View.GONE);
                }
                else {
                    noContent.setVisibility(View.VISIBLE);
                    mControlState = ControlState.NO_CONTENT;
                }

                if (data.trackInfo != null) {
                    TextView trackInfo = (TextView) mPlaybackView
                        .findViewById(org.erikandre.smartwatch.spotify2.R.id.track_info);
                    trackInfo.setText(data.trackInfo);
                }

                mPlaybackView.measure(width, height);
                mPlaybackView.layout(0, 0, mPlaybackView.getMeasuredWidth(),
                    mPlaybackView.getMeasuredHeight());
                mPlaybackView.draw(mCanvas);
            }
            else {
                // Widget not configured, show info message
                Log.d(TAG, "Control in Not Configured mode");
                mControlState = ControlState.NOT_CONFIGURED;
                mNotConfiguredView.measure(width, height);
                mNotConfiguredView.layout(0, 0,
                    mNotConfiguredView.getMeasuredWidth(),
                    mNotConfiguredView.getMeasuredHeight());
                mNotConfiguredView.draw(mCanvas);
            }

            showBitmap(mBitmap);
        }

    }

}
