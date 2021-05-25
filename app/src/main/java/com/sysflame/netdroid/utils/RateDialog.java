package com.sysflame.netdroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.sysflame.netdroid.R;

/**
 * The type Rate dialog.
 */
public class RateDialog {
	private Activity activity;

	/**
	 * Instantiates a new Rate dialog.
	 *
	 * @param activity the activity
	 */
	public RateDialog (Activity activity) {
		this.activity = activity;
	}

	/**
	 * Display rating dialogue.
	 */
	public void displayRatingDialogue () {
		TextView title1;
		LayoutInflater li = LayoutInflater.from (activity);
		View promptsView = li.inflate (R.layout.lout_rate, null);
		Context wrapper = new ContextThemeWrapper (activity, R.style.MyDialogTheme);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (
				wrapper);
		alertDialogBuilder.setView (promptsView);
		title1 = promptsView.findViewById (R.id.title1);
		title1.post (() -> {
			int length = title1.getMeasuredWidth ();
			float angle = 45;
			Shader textShader = new LinearGradient (0, 0, (int) (Math.sin (Math.PI * angle / 180) * length),
					(int) (Math.cos (Math.PI * angle / 180) * length),
					new int[] {0xFF30E3CA, 0xFFa5dee5},
					null,
					Shader.TileMode.CLAMP);
			title1.getPaint ().setShader (textShader);
			title1.invalidate ();
		});
		title1.setText ("Love" + " " + activity.getResources ().getString (R.string.app_name) + "?");
		alertDialogBuilder
				.setPositiveButton ("Rate 5 star",
						(dialog, id) -> {
							String url = "https://play.google.com/store/apps/details?id="
									+ activity.getPackageName () + "";
							Intent i = new Intent (Intent.ACTION_VIEW);
							i.setData (Uri.parse (url));
							activity.startActivity (i);
							dialog.cancel ();
						})
				.setNegativeButton ("Cancel",
						(dialog, id) -> dialog.cancel ());
		AlertDialog alertDialog = alertDialogBuilder.create ();
		alertDialog.show ();
		Button btn1 = alertDialog.getButton (DialogInterface.BUTTON_NEGATIVE);
		btn1.setTextColor (ContextCompat.getColor (activity, R.color.textColor2));
		Button btn2 = alertDialog.getButton (DialogInterface.BUTTON_POSITIVE);
		btn2.setTextColor (ContextCompat.getColor (activity, R.color.white));
	}
}
