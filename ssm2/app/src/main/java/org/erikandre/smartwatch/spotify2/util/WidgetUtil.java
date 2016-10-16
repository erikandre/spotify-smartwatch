/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2.util;

import org.erikandre.smartwatch.spotify2.R;
import org.erikandre.smartwatch.spotify2.WidgetData;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Utility class for instantiating and extracting data from the
 * Spotify widget.
 */
public class WidgetUtil {
	
	private static final String TAG = "WidgetUtil";
	
	private interface ViewMatcher {

		boolean match(View view);

	}

	/**
	 * Returns the data (almbum art, track name, artist & album) that
	 * is currently displayed in the widget (hosted by this extension).
	 * 
	 * @param context a context used to create the widget.
	 * @param widgetId the assigned ID of the widget.
	 * @return the data extracted from the widget.
	 */
	public static WidgetData getWidgetData(Context context, int widgetId) {

		AppWidgetManager mAppWidgetManager = AppWidgetManager
				.getInstance(context);
		AppWidgetHost mAppWidgetHost = new AppWidgetHost(context,
				R.id.APPWIDGET_HOST_ID);

		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(widgetId);

		AppWidgetHostView hostView = mAppWidgetHost.createView(context,
				widgetId, appWidgetInfo);
		hostView.setAppWidget(widgetId, appWidgetInfo);

		drawOffscreen(context, hostView);
		
		return getDataFromSpotifyWidget(hostView);
	}

	/**
	 * Before we extract any data from the widget we need to make
	 * sure that it has been drawn (otherwise the album art will just
	 * be blank. The resulting bitmap is discarded.
	 */
	private static void drawOffscreen(Context context, View view) {
		int width = 720;
		int height = 220;
		try {
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			view.setDrawingCacheEnabled(true);
			view.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					canvas.getHeight(), MeasureSpec.EXACTLY));
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
			Paint paint = new Paint();
			Bitmap cache = view.getDrawingCache();
			if (cache != null) {
				canvas.drawBitmap(cache, 0, 0, paint);
			} else {
				Log.w(TAG, "Could not get drawing cache for the view");
			}
			bitmap.recycle();
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "Failed to allocate memory for offscreen buffer", e);
		}
	}

	private static WidgetData getDataFromSpotifyWidget(View root) {
		
		Drawable albumArt = null;
		
		String trackName = null;
		
		String trackInfo = null;

		// Find album art view
		ImageView album = (ImageView) findView(root, new ViewMatcher() {

			@Override
			public boolean match(View view) {
				if (view instanceof ImageView) {
					if (view.getWidth() > 100 && view.getHeight() > 100) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		});

		if (album != null) {
			albumArt = album.getDrawable();
		} else {
			Log.d(TAG, "Could not find album art view");
		}

		// Find song and artist info
		ViewGroup infoGroup = (ViewGroup) findView(root, new ViewMatcher() {

			@Override
			public boolean match(View view) {
				if (view instanceof ViewGroup) {
					ViewGroup group = (ViewGroup) view;
					if (group.getChildCount() == 2) {
						if (group.getChildAt(0) instanceof TextView
								&& group.getChildAt(1) instanceof TextView) {
							return true;
						}
					}
				}
				return false;
			}
		});
		if (infoGroup != null) {
			TextView title = (TextView) infoGroup.getChildAt(0);
			trackName = title.getText().toString();
			TextView artist = (TextView) infoGroup.getChildAt(1);
			trackInfo = artist.getText().toString();
		} else {
			Log.d(TAG, "Could not find info views");
		}
		
		return new WidgetData(albumArt, trackName, trackInfo);
	}

	/**
	 * Search through the view hierarchy for a view that matches
	 * the criteria set by the matcher.
	 * 
	 * @param view the root view from where the search is started.
	 * @param matcher a ViewMatcher that determines if the correct view is found.
	 * @return the first matching View or null of no view matches.
	 */
	private static View findView(View view, ViewMatcher matcher) {
		if (matcher.match(view)) {
			return view;
		} else {
			if (view instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) view;
				for (int i = 0; i < group.getChildCount(); i++) {
					View match = findView(group.getChildAt(i), matcher);
					if (match != null) {
						return match;
					}
				}
				return null;
			} else {
				return null;
			}
		}
	}
}
