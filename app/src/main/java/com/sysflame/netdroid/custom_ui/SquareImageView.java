package com.sysflame.netdroid.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * The type Square image view.
 */
public class SquareImageView extends ImageView {
	/**
	 * Instantiates a new Square image view.
	 *
	 * @param context the context
	 */
	public SquareImageView (Context context) {
		super (context);
	}

	/**
	 * Instantiates a new Square image view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public SquareImageView (Context context, AttributeSet attrs) {
		super (context, attrs);
	}

	/**
	 * Instantiates a new Square image view.
	 *
	 * @param context  the context
	 * @param attrs    the attrs
	 * @param defStyle the def style
	 */
	public SquareImageView (Context context, AttributeSet attrs, int defStyle) {
		super (context, attrs, defStyle);
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode (widthMeasureSpec);
		int heightMode = MeasureSpec.getMode (heightMeasureSpec);
		int widthSize = MeasureSpec.getSize (widthMeasureSpec);
		int heightSize = MeasureSpec.getSize (heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
			setMeasuredDimension (widthSize, widthSize);
		} else if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
			setMeasuredDimension (heightSize, heightSize);
		} else {
			super.onMeasure (widthMeasureSpec, heightMeasureSpec);
		}
	}
}
