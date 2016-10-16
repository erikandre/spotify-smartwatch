/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	private static final String PREF_NAME = "preferences";

	private static final String KEY_WIDGET_ID = "widget_id";

	private Preferences() {
	}

	public static void setWidgetId(Context context, int widgetId) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		pref.edit().putInt(KEY_WIDGET_ID, widgetId).apply();
	}

	public static int getWidgetId(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return pref.getInt(KEY_WIDGET_ID, -1);
	}

}
