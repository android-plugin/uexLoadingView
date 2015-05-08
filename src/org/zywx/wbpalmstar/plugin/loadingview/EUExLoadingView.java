package org.zywx.wbpalmstar.plugin.loadingview;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class EUExLoadingView extends EUExBase implements Parcelable {

	public static final String LOADINGVIEW_FUN_PARAMS_KEY = "loadingviewFunParamsKey";
	public static final String LOADINGVIEW_ACTIVITY_ID = "loadingviewActivityID";

	public static final int LOADINGVIEW_MSG_OPEN = 0;
	public static final int LOADINGVIEW_MSG_CLOSE = 1;
	private static LocalActivityManager mgr;

	public EUExLoadingView(Context context, EBrowserView view) {
		super(context, view);
		mgr = ((ActivityGroup) mContext).getLocalActivityManager();
	}

	private void sendMessageWithType(int msgType, String[] params) {
		if (mHandler == null) {
			return;
		}
		Message msg = new Message();
		msg.what = msgType;
		msg.obj = this;
		Bundle b = new Bundle();
		b.putStringArray(LOADINGVIEW_FUN_PARAMS_KEY, params);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onHandleMessage(Message msg) {
		if (msg.what == LOADINGVIEW_MSG_OPEN) {
			handleOpen(msg);
		} else {
			handleMessageInChatKeyboard(msg);
		}
	}

	private void handleMessageInChatKeyboard(Message msg) {
		String activityId = LOADINGVIEW_ACTIVITY_ID
				+ EUExLoadingView.this.hashCode();
		Activity activity = mgr.getActivity(activityId);

		if (activity != null && activity instanceof ACELoadingViewActivity) {
			String[] params = msg.getData().getStringArray(
					LOADINGVIEW_FUN_PARAMS_KEY);
			ACELoadingViewActivity lActivity = ((ACELoadingViewActivity) activity);

			switch (msg.what) {
			case LOADINGVIEW_MSG_CLOSE:
				handleClose(lActivity, mgr);
				break;
			}
		}
	}

	private void handleClose(ACELoadingViewActivity lActivity,
			LocalActivityManager mgr) {
		View decorView = lActivity.getWindow().getDecorView();
		mBrwView.removeViewFromCurrentWindow(decorView);
		String activityId = LOADINGVIEW_ACTIVITY_ID
				+ EUExLoadingView.this.hashCode();
		mgr.destroyActivity(activityId, true);
	}

	private void handleOpen(Message msg) {
		String[] params = msg.getData().getStringArray(
				LOADINGVIEW_FUN_PARAMS_KEY);
		try {
			JSONObject json = new JSONObject(params[0]);
			int x = Integer.parseInt(json.getString("x"));
			int y = Integer.parseInt(json.getString("y"));
			int w = Integer.parseInt(json.getString("w"));
			int h = Integer.parseInt(json.getString("h"));

			JSONObject jsonStyle = new JSONObject(
					json.getString(ELoadingViewUtils.LOADINGVIEW_EXTRA_STYLE));
			int id = Integer.parseInt(jsonStyle
					.getString(ELoadingViewUtils.LOADINGVIEW_EXTRA_STYLE_ID));
			int number = Integer.parseInt(jsonStyle
					.getString(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_NUM));
			JSONArray array = jsonStyle
					.getJSONArray(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_COLOR);
			int[] colorArray = new int[array.length()];
			for (int i = 0; i < array.length(); i++) {
				colorArray[i] = BUtility.parseColor(array.getString(i));
			}

			String activityId = LOADINGVIEW_ACTIVITY_ID
					+ EUExLoadingView.this.hashCode();
			ACELoadingViewActivity lActivity = (ACELoadingViewActivity) mgr
					.getActivity(activityId);
			if (lActivity != null) {
				return;
			}
			Intent intent = new Intent(mContext, ACELoadingViewActivity.class);
			intent.putExtra(ELoadingViewUtils.LOADINGVIEW_EXTRA_UEXBASE_OBJ,
					this);
			intent.putExtra(ELoadingViewUtils.LOADINGVIEW_EXTRA_STYLE_ID, id);
			intent.putExtra(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_NUM,
					number);
			intent.putExtra(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_COLOR,
					colorArray);
			Window window = mgr.startActivity(activityId, intent);
			View decorView = window.getDecorView();
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
			lp.leftMargin = x;
			lp.topMargin = y;
			addView2CurrentWindow(decorView, lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addView2CurrentWindow(View child,
			RelativeLayout.LayoutParams parms) {
		int l = (int) (parms.leftMargin);
		int t = (int) (parms.topMargin);
		int w = parms.width;
		int h = parms.height;
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		lp.leftMargin = l;
		lp.topMargin = t;
		adptLayoutParams(parms, lp);
		mBrwView.addViewToCurrentWindow(child, lp);
	}

	public void open(String[] params) {
		sendMessageWithType(LOADINGVIEW_MSG_OPEN, params);
	}

	public void close(String[] params) {
		sendMessageWithType(LOADINGVIEW_MSG_CLOSE, params);
	}

	@Override
	protected boolean clean() {
		close(null);
		return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}
}
