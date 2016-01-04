package org.zywx.wbpalmstar.plugin.loadingview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.loadingview.vo.OpenDataVO;

import java.util.ArrayList;
import java.util.List;

public class EUExLoadingView extends EUExBase implements Parcelable {

	public static final String LOADINGVIEW_FUN_PARAMS_KEY = "loadingviewFunParamsKey";
	public static final String LOADINGVIEW_ACTIVITY_ID = "loadingviewActivityID";

	public static final int LOADINGVIEW_MSG_OPEN = 0;
	public static final int LOADINGVIEW_MSG_CLOSE = 1;
	public static final int LOADINGVIEW_MSG_OPEN_CIRCLE_LOADING = 2;
    private ACELoadingView mView;
    private ProgressDialog progress;

	public EUExLoadingView(Context context, EBrowserView view) {
		super(context, view);
        CRes.init(mContext);
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
		} else if (msg.what == LOADINGVIEW_MSG_OPEN_CIRCLE_LOADING ) {
			handleOpenCircleLoading(msg);
		}else {
			handleMessageInChatKeyboard(msg);
		}
	}

	private void handleMessageInChatKeyboard(Message msg) {
        if (msg == null || (mView == null && progress == null)){
            return;
        }
        switch (msg.what) {
            case LOADINGVIEW_MSG_CLOSE:
                handleClose(mView);
                break;
        }
	}

	private void handleClose(ACELoadingView view) {
        if (progress != null) {
            progress.dismiss();
        }
        if (view != null) {
            mBrwView.removeViewFromCurrentWindow(view);
        }
    }

    private void handleOpen(Message msg) {
        if (mView != null || progress != null) {
            handleClose(mView);
        }
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
            OpenDataVO dataVO = new OpenDataVO();
			int id = Integer.parseInt(jsonStyle
					.getString(ELoadingViewUtils.LOADINGVIEW_EXTRA_STYLE_ID));
			int number = Integer.parseInt(jsonStyle
					.getString(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_NUM));
			JSONArray array = jsonStyle
					.getJSONArray(ELoadingViewUtils.LOADINGVIEW_EXTRA_POINT_COLOR);
            dataVO.setStyleId(id);
            dataVO.setPointNum(number);
            List<String> colors = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++){
                colors.add(array.getString(i));
            }
            dataVO.setPointColor(colors);

            mView = new ACELoadingView(mContext, dataVO, this);
			String activityId = LOADINGVIEW_ACTIVITY_ID
					+ EUExLoadingView.this.hashCode();
			if (mView == null) {
				return;
			}
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
			lp.leftMargin = x;
			lp.topMargin = y;
			addView2CurrentWindow(mView, lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void handleOpenCircleLoading(Message msg) {
		if (mView != null || progress != null) {
			handleClose(mView);
		}
		progress = new ProgressDialog(mContext, CRes.plugin_loading_view_progress_style);
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();
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

	public void openCircleLoading(String [] params) {sendMessageWithType(LOADINGVIEW_MSG_OPEN_CIRCLE_LOADING, params);}
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
