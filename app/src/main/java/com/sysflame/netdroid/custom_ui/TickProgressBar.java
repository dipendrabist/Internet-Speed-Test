package com.sysflame.netdroid.custom_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

import com.sysflame.netdroid.R;

/**
 * The type Tick progress bar.
 */
public class TickProgressBar extends ProgressBar {
	private final int DEFAULT_LINEHEIGHT = dp2px (getContext (), 15);
	private final int DEFAULT_mTickWidth = dp2px (getContext (), 2);
	private final int DEFAULT_TEXT = spToPx (getContext (), 20);
	private final int DEFAULT_mUnmProgressColor = 0xffeaeaea;
	private final int DEFAULT_mProgressColor = Color.YELLOW;
	private final int DEFAULT_OFFSETDEGREE = 60;
	private final int DEFAULT_DENSITY = 4;
	private final int MIN_DENSITY = 2;
	private final int MAX_DENSITY = 8;
	private float mRadius;
	private float mCenterText;
	private String mPUnit;
	private int mBoardWidth;
	private int mDegree = DEFAULT_OFFSETDEGREE;
	private RectF mArcRectf;
	private Paint mLinePaint;
	private Paint mLinePaintOpacity;
	private Paint mArcPaint;
	private Rect boundsText = new Rect ();
	private Rect boundsmsText = new Rect ();
	private Paint mTextPaint;
	private Paint msTextPaint;
	private int mUnmProgressColor;
	private int mProgressColor;
	private int mTickWidth;
	private int mTickDensity;
	private Bitmap mCenterBitmap;
	private Canvas mCenterCanvas;
	private TickProgressBar.OnCenterDraw mOnCenter;

	/**
	 * Instantiates a new Tick progress bar.
	 *
	 * @param context the context
	 */
	public TickProgressBar (Context context) {
		this (context, null);
	}

	/**
	 * Instantiates a new Tick progress bar.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public TickProgressBar (Context context, AttributeSet attrs) {
		this (context, attrs, 0);
	}

	/**
	 * Instantiates a new Tick progress bar.
	 *
	 * @param context      the context
	 * @param attrs        the attrs
	 * @param defStyleAttr the def style attr
	 */
	public TickProgressBar (Context context, AttributeSet attrs, int defStyleAttr) {
		super (context, attrs, defStyleAttr);
		@SuppressLint ("Recycle") final TypedArray attributes = getContext ().obtainStyledAttributes (
				attrs, R.styleable.TickProgressBar);
		mPUnit = attributes.getString (R.styleable.TickProgressBar_tick_unit);
		mBoardWidth = attributes.getDimensionPixelOffset (R.styleable.TickProgressBar_tick_borderWidth, DEFAULT_LINEHEIGHT);
		mUnmProgressColor = attributes.getColor (R.styleable.TickProgressBar_tick_unprogresColor, DEFAULT_mUnmProgressColor);
		mProgressColor = attributes.getColor (R.styleable.TickProgressBar_tick_progressColor, DEFAULT_mProgressColor);
		mTickWidth = attributes.getDimensionPixelOffset (R.styleable.TickProgressBar_tick_tickWidth, DEFAULT_mTickWidth);
		mTickDensity = attributes.getInt (R.styleable.TickProgressBar_tick_tickDensity, DEFAULT_DENSITY);
		mCenterText = attributes.getDimensionPixelOffset (R.styleable.TickProgressBar_tick_centertext, DEFAULT_TEXT);
		mTickDensity = Math.max (Math.min (mTickDensity, MAX_DENSITY), MIN_DENSITY);
		mDegree = attributes.getInt (R.styleable.TickProgressBar_tick_degree, DEFAULT_OFFSETDEGREE);
		mTextPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setStyle (Paint.Style.FILL);
		msTextPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
		msTextPaint.setStyle (Paint.Style.FILL);
		mArcPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setStrokeWidth (mBoardWidth);
		mArcPaint.setStyle (Paint.Style.STROKE);
		mLinePaintOpacity = new Paint (Paint.ANTI_ALIAS_FLAG);
		mLinePaint = new Paint (Paint.ANTI_ALIAS_FLAG);
		mLinePaintOpacity.setStrokeWidth (dp2px (getContext (), 8) + mTickWidth);
		mLinePaintOpacity.setStrokeCap (Paint.Cap.ROUND);
		mLinePaint.setStrokeWidth (mTickWidth);
	}

	/**
	 * Sets on center draw.
	 *
	 * @param mOnCenter the m on center
	 */
	public void setOnCenterDraw (TickProgressBar.OnCenterDraw mOnCenter) {
		this.mOnCenter = mOnCenter;
	}

	@Override
	protected synchronized void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode (widthMeasureSpec);
		int heightMode = MeasureSpec.getMode (heightMeasureSpec);
		mRadius = MeasureSpec.getSize (widthMeasureSpec) / 2f
				>= MeasureSpec.getSize (heightMeasureSpec) / 2f ?
				MeasureSpec.getSize (widthMeasureSpec) / 2f :
				MeasureSpec.getSize (heightMeasureSpec) / 2f;
		if (widthMode != MeasureSpec.EXACTLY) {
			int widthSize = (int) (mRadius * 2);
			widthMeasureSpec = MeasureSpec.makeMeasureSpec (widthSize, MeasureSpec.EXACTLY);
		}
		if (heightMode != MeasureSpec.EXACTLY) {
			int heightSize = (int) (mRadius * 2);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec (heightSize, MeasureSpec.EXACTLY);
		}
		super.onMeasure (widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected synchronized void onDraw (Canvas canvas) {
		canvas.save ();
		float roate = getProgress () * 1.0f / getMax ();
		float x = mRadius;
		float y = mRadius;
		if (mOnCenter != null) {
			if (mCenterCanvas == null) {
				mCenterBitmap = Bitmap.createBitmap ((int) mRadius * 2, (int) mRadius * 2, Bitmap.Config.ARGB_8888);
				mCenterCanvas = new Canvas (mCenterBitmap);
			}
			mCenterCanvas.drawColor (Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			mOnCenter.draw (mCenterCanvas, mArcRectf, x, y, mBoardWidth, getProgress ());
			canvas.drawBitmap (mCenterBitmap, 0, 0, null);
		}
		int angle = mDegree / 2;
		int count = (360 - mDegree) / mTickDensity;
		int target = (int) (roate * count);
		mTextPaint.setColor (Color.WHITE);
		mTextPaint.setTextSize (mCenterText);
		msTextPaint.setColor (Color.WHITE);
		msTextPaint.setTextSize (mCenterText / 4);
		mTextPaint.getTextBounds (String.format ("%.1f", getProgress () / 100f), 0, String.format ("%.1f", getProgress () / 100f).length (), boundsText);
		msTextPaint.getTextBounds (mPUnit, 0, mPUnit.length (), boundsmsText);
		canvas.drawText (String.format ("%.1f", getProgress () / 100f), mRadius - boundsText.width () / 2f, mRadius / 2f + 2 * mBoardWidth - boundsText.height () / 2f - boundsText.bottom - boundsText.top, mTextPaint);
		canvas.drawText (mPUnit, mRadius - boundsmsText.width () / 2f, mRadius + mBoardWidth - boundsmsText.height () / 2f - boundsmsText.bottom - boundsmsText.top, msTextPaint);
		canvas.rotate (180 + angle, x, y);
		for (int i = 0 ; i <= count ; i++) {
			if (i < target) {
				mLinePaint.setColor (mProgressColor);
				canvas.drawLine (x, mBoardWidth + mBoardWidth / 2.0f, x, mBoardWidth - mBoardWidth / 2.0f, mLinePaint);
				canvas.rotate (mTickDensity, x, y);
			} else if (i == target) {
				mLinePaint.setColor (mProgressColor);
				mLinePaintOpacity.setColor (Color.argb (50, 48, 227, 202));
				canvas.drawLine (x, mBoardWidth + mBoardWidth / 2.0f, x, mBoardWidth - mBoardWidth / 2.0f, mLinePaint);
				canvas.drawLine (x, mBoardWidth + mBoardWidth / 2.0f, x, mBoardWidth - mBoardWidth / 2.0f, mLinePaintOpacity);
				canvas.rotate (mTickDensity, x, y);
			} else {
				mLinePaint.setColor (mUnmProgressColor);
				canvas.drawLine (x, mBoardWidth + mBoardWidth / 2.0f, x, mBoardWidth - mBoardWidth / 2.0f, mLinePaint);
				canvas.rotate (mTickDensity, x, y);
			}
		}
		canvas.rotate (mDegree / 2.0f + angle - 5, x, y);
		canvas.restore ();
	}

	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged (w, h, oldw, oldh);
		mArcRectf = new RectF (0,
				0,
				mRadius * 2,
				mRadius * 2);
		Log.e ("DEMO", "right == " + mArcRectf.right + "   mRadius == " + mRadius * 2);
	}

	/**
	 * Animate progress bar.
	 *
	 * @param progress the progress
	 * @param from     the from
	 * @param to       the to
	 * @param duration the duration
	 */
	public void animateProgressBar (TickProgressBar progress, float from, float to, int duration) {
		TickProgressBar.ProgressBarAnimation anim = new TickProgressBar.ProgressBarAnimation (progress, from, to);
		anim.setDuration (duration);
		progress.startAnimation (anim);
	}

	/**
	 * Gets p unit.
	 *
	 * @return the p unit
	 */
	public String getmPUnit () {
		return mPUnit;
	}

	/**
	 * Sets p unit.
	 *
	 * @param mPUnit the m p unit
	 */
	public void setmPUnit (String mPUnit) {
		this.mPUnit = mPUnit;
	}

	@Override
	protected void onDetachedFromWindow () {
		super.onDetachedFromWindow ();
		if (mCenterBitmap != null) {
			mCenterBitmap.recycle ();
			mCenterBitmap = null;
		}
	}

	private int dp2px (Context context, int dpVal) {
		return (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources ().getDisplayMetrics ());
	}

	private int spToPx (Context context, float sp) {
		return (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_SP, sp, context.getResources ().getDisplayMetrics ());
	}

	/**
	 * The interface On center draw.
	 */
	public interface OnCenterDraw {
		/**
		 * Draw.
		 *
		 * @param canvas      the canvas
		 * @param rectF       the rect f
		 * @param x           the x
		 * @param y           the y
		 * @param storkeWidth the storke width
		 * @param progress    the progress
		 */
		void draw (Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress);
	}

	/**
	 * The type Progress bar animation.
	 */
	class ProgressBarAnimation extends Animation {
		private TickProgressBar progressBar;
		private float from;
		private float to;

		/**
		 * Instantiates a new Progress bar animation.
		 *
		 * @param progressBar the progress bar
		 * @param from        the from
		 * @param to          the to
		 */
		ProgressBarAnimation (TickProgressBar progressBar, float from, float to) {
			super ();
			this.progressBar = progressBar;
			this.from = from;
			this.to = to;
		}

		@Override
		protected void applyTransformation (float interpolatedTime, Transformation t) {
			super.applyTransformation (interpolatedTime, t);
			float value = from + (to - from) * interpolatedTime;
			progressBar.setProgress ((int) value);
		}
	}
}