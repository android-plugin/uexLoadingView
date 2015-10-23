package org.zywx.wbpalmstar.plugin.loadingview;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.loadingview.vo.OpenDataVO;

public class ACELoadingView extends FrameLayout {

	private String TAG = "ACELoadingViewActivity";
	private EUExLoadingView mUexBaseObj;
	private int mPointNum = 0;
	private int ANIMATION_MESSAGE_WHAT = 100;
	private int ANIMATION_COLOR_DURATION = 600;
	private int ANIMATION_COLOR_DELAY = 150;
	private int ANIMATION_SINGLE_DURATION = 250;
	private int ANIMATION_SINGLE_DELAY = 150;
	private LinearLayout mColorLayout;
	private LinearLayout mSingleLayout;
	private HashMap<View, AnimationBundle> animationMap = new HashMap<View, AnimationBundle>();
    private OpenDataVO dataVO;
    private Context mContext;

    public ACELoadingView(Context context, OpenDataVO dataVO, EUExLoadingView baseObj) {
        super(context);
        this.mContext = context;
        this.dataVO = dataVO;
        this.mUexBaseObj = baseObj;
        initView();
    }

    private class AnimationBundle extends ArrayList<Tweener> {
		private static final long serialVersionUID = 1L;
		private boolean mSuspended;

		public void start() {
			if (mSuspended)
				return; // ignore attempts to start animations
			final int count = size();
			for (int i = 0; i < count; i++) {
				Tweener anim = get(i);
				anim.animator.start();
			}
		}

		public void cancel() {
			final int count = size();
			for (int i = 0; i < count; i++) {
				Tweener anim = get(i);
				anim.animator.cancel();
			}
			clear();
		}

		public void stop() {
			final int count = size();
			for (int i = 0; i < count; i++) {
				Tweener anim = get(i);
				anim.animator.end();
			}
			clear();
		}

		public void setSuspended(boolean suspend) {
			mSuspended = suspend;
		}
	};

	private void initView() {
		Log.i(TAG, " initView ");
		CRes.init(mContext);
		int styleId = 0;
		if (dataVO.getStyleId() != -1) {
			styleId = dataVO.getStyleId();
		}
        mPointNum = dataVO.getPointNum();

		int[] colorArray = new int[] {};
        if (dataVO.getPointColor() != null){
            colorArray = dataVO.getPointColor();
        }
		DisplayMetrics dm = getResources().getDisplayMetrics();
        LayoutInflater.from(mContext).inflate(CRes.plugin_loadingview_layout,this,true);
		mColorLayout = (LinearLayout) findViewById(CRes.plugin_loadingview_color_points);
		mSingleLayout = (LinearLayout) findViewById(CRes.plugin_loadingview_single_points);

		if (styleId == 0) {
			int colorWidth = getResources().getDimensionPixelSize(
					CRes.plugin_loadingview_color_point_width);
			int colorMargin = getResources().getDimensionPixelSize(
					CRes.plugin_loadingview_color_point_margin);
			LinearLayout.LayoutParams colorViewParams = new LinearLayout.LayoutParams(
					colorWidth, colorWidth);
			colorViewParams.setMargins(colorMargin, 0, colorMargin, 0);
			mColorLayout.setVisibility(View.VISIBLE);
			ANIMATION_COLOR_DELAY = ANIMATION_COLOR_DURATION / (mPointNum - 1);
			for (int i = 0; i < mPointNum; i++) {
				int radius = (int) (colorWidth / dm.density);
				int color = 0;
				if (colorArray.length == mPointNum) {
					color = colorArray[i];
				} else if (colorArray.length == 1) {
					color = colorArray[0];
				}
				CircleView view = new CircleView(mContext, radius, color);
				setScale(view, 0.0f);
				mColorLayout.addView(view, i, colorViewParams);
				AnimationBundle animation = new AnimationBundle();
				animationMap.put(view, animation);
				Message msg = new Message();
				msg.what = ANIMATION_MESSAGE_WHAT;
				msg.arg1 = i;
				mColorPointAnimationHandler.sendMessageDelayed(msg, (i + 1)
						* ANIMATION_COLOR_DELAY);
			}
		} else {
			int singleWidth = getResources().getDimensionPixelSize(
					CRes.plugin_loadingview_single_point_width);
			int singleMargin = getResources().getDimensionPixelSize(
					CRes.plugin_loadingview_single_point_margin);
			LinearLayout.LayoutParams singleViewParams = new LinearLayout.LayoutParams(
					singleWidth, singleWidth);
			singleViewParams.setMargins(singleMargin, 0, singleMargin, 0);
			mSingleLayout.setVisibility(View.VISIBLE);
			for (int i = 0; i < mPointNum; i++) {
				int radius = (int) (singleWidth / dm.density);
				int color = 0;
				if (colorArray.length == mPointNum) {
					color = colorArray[i];
				} else if (colorArray.length == 1) {
					color = colorArray[0];
				}
				CircleView view = new CircleView(mContext, radius, color);
				view.setAlpha(0.3f);
				mSingleLayout.addView(view, i, singleViewParams);
				AnimationBundle animation = new AnimationBundle();
				animationMap.put(view, animation);
				Message msg = new Message();
				msg.what = ANIMATION_MESSAGE_WHAT;
				msg.arg1 = i;
				mSinglePointAnimationHandler.sendMessageDelayed(msg, (i + 1)
						* ANIMATION_SINGLE_DELAY);
			}
		}
	}

	private class CircleView extends View {
		private Paint paint;
		private int radius = 0;

		public CircleView(Context context, int radius, int color) {
			super(context);
			this.radius = radius;
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(color);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawCircle(radius, radius, radius, paint);
		}
	}

	private final Handler mSinglePointAnimationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ANIMATION_MESSAGE_WHAT) {
				View view = mSingleLayout.getChildAt(msg.arg1);
				if (view != null) {
					startSinglePointAnimation(animationMap.get(view), view);
					mSinglePointAnimationHandler.sendEmptyMessageDelayed(
							ANIMATION_MESSAGE_WHAT + msg.arg1 + 1, 2
									* ANIMATION_SINGLE_DURATION
									+ (mPointNum - 1)
									* ANIMATION_SINGLE_DELAY);
				}
			} else {
				int count = mSingleLayout.getChildCount();
				for (int i = 0; i < count; i++) {
					if (msg.what == ANIMATION_MESSAGE_WHAT + i + 1) {
						View view = mSingleLayout.getChildAt(i);
						if (view != null) {
							startSinglePointAnimation(animationMap.get(view),
									view);
							mSinglePointAnimationHandler
									.sendEmptyMessageDelayed(
											ANIMATION_MESSAGE_WHAT + i + 1, 2
													* ANIMATION_SINGLE_DURATION
													+ (mPointNum - 1)
													* ANIMATION_SINGLE_DELAY);
							break;
						}
					}
				}
			}
		}
	};

	private final Handler mColorPointAnimationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ANIMATION_MESSAGE_WHAT) {
				View view = mColorLayout.getChildAt(msg.arg1);
				if (view != null) {
					showColorPoint(animationMap.get(view), view);
					mColorPointAnimationHandler.sendEmptyMessageDelayed(
							ANIMATION_MESSAGE_WHAT + msg.arg1 + 1,
							2 * ANIMATION_COLOR_DURATION);
				}
			} else {
				int count = mColorLayout.getChildCount();
				for (int i = 0; i < count; i++) {
					if (msg.what == ANIMATION_MESSAGE_WHAT + i + 1) {
						View view = mColorLayout.getChildAt(i);
						if (view != null) {
							showColorPoint(animationMap.get(view), view);
							mColorPointAnimationHandler
									.sendEmptyMessageDelayed(
											ANIMATION_MESSAGE_WHAT + i + 1,
											2 * ANIMATION_COLOR_DURATION);
							break;
						}
					}
				}
			}
		}
	};

	private void setScale(View view, float scale) {
		view.setScaleX(scale);
		view.setScaleY(scale);
	}

	private void showColorPoint(final AnimationBundle bundles,
			final Object object) {
		bundles.cancel();
		bundles.add(Tweener.to(object, ANIMATION_COLOR_DURATION, 
				"delay", 0,
				"scaleX", 1.0f, 
				"scaleY", 1.0f, 
				"onComplete",
				new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator animator) {
						hideColorPoint(bundles, object);
					}
				}));
		bundles.start();
	}

	private void hideColorPoint(AnimationBundle bundles, Object object) {
		bundles.cancel();
		bundles.add(Tweener.to(object, ANIMATION_COLOR_DURATION, 
				"delay", 0,
				"scaleX", 0.0f, 
				"scaleY", 0.0f));
		bundles.start();
	}

	private void startSinglePointAnimation(final AnimationBundle bundles,
			final Object object) {
		bundles.cancel();
		bundles.add(Tweener.to(object, ANIMATION_SINGLE_DURATION, 
				"delay", 0,
				"alpha", 1.0f, 
				"onComplete", 
				new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator animator) {
						setSinglePointAlpha(bundles, object);
					}
				}));
		bundles.start();
	}
	
	private void setSinglePointAlpha(AnimationBundle bundles, Object object) {
		bundles.cancel();
		bundles.add(Tweener.to(object, ANIMATION_SINGLE_DURATION, 
				"delay", 0,
				"alpha", 0.3f));
		bundles.start();
	}
}