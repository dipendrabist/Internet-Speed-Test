package com.sysflame.netdroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The type Utils.
 */
public class Utils {
	private Utils () {
		throw new IllegalStateException ("Utility class");
	}

	/**
	 * Enable notification.
	 *
	 * @param context the context
	 * @param key     the key
	 * @param value   the value
	 */
	public static void EnableNotification (Context context, String key,
	                                       boolean value) {
		SharedPreferences preferences = context.getSharedPreferences (
				Constant.SHARED_PREFS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit ();
		editor.putBoolean (key, value);
		editor.commit ();
	}

	/**
	 * Is enabled notification boolean.
	 *
	 * @param context the context
	 * @param key     the key
	 * @return the boolean
	 */
	public static boolean isEnabledNotification (Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences (
				Constant.SHARED_PREFS, Context.MODE_PRIVATE);
		return preferences.getBoolean (key, true);
	}
}
