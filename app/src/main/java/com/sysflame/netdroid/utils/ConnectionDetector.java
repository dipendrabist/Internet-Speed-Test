package com.sysflame.netdroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * The type Connection detector.
 */
public class ConnectionDetector {
	private Context _context;

	/**
	 * Instantiates a new Connection detector.
	 *
	 * @param context the context
	 */
	public ConnectionDetector (Context context) {
		this._context = context;
	}

	/**
	 * Is connecting to internet boolean.
	 *
	 * @return the boolean
	 */
	public boolean isConnectingToInternet () {
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService (Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo ();
			if (info != null)
				for (int i = 0 ; i < info.length ; i++)
					if (info[ i ].getState () == NetworkInfo.State.CONNECTED) {
						Log.d ("Network", "NETWORKnAME: " + info[ i ].getTypeName ());
						return true;
					}
		}
		return false;
	}
}