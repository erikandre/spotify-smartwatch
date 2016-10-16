/**
 *	© 2012 by Erik André. All rights reserved
 */

package org.erikandre.smartwatch.spotify2;

import android.graphics.drawable.Drawable;

public class WidgetData {
	
	public final Drawable album;
	
	public final String trackName;
	
	public final String trackInfo;
	
	public WidgetData(Drawable album, String trackName, String trackInfo) {
		this.album = album;
		this.trackName = trackName;
		this.trackInfo = trackInfo;
	}

}
