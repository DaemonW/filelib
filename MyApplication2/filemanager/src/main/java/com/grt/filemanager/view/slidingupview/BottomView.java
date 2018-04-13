package com.grt.filemanager.view.slidingupview;

/**
 * Created by liwei on 2015/11/24.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.grt.filemanager.util.AppUtils;


public class BottomView {

    private View convertView;
    private Context mContext;
    private int theme;
    private Dialog bv;
    private int animationStyle;
    private boolean isTop = false;
    private int mHeight = 0;
    private int mMaxHeight;
    private int width = WindowManager.LayoutParams.MATCH_PARENT;
    private int x = 0;
    private int y = 0;

    private static final int BOTTOM_VIEW_HEIGHT = 48;

    public BottomView(Context c, int theme) {
        this.theme = theme;
        this.mContext = c;
    }

    public BottomView(Context c, int theme, View convertView) {
        this.theme = theme;
        this.mContext = c;
        this.convertView = convertView;
    }

    public BottomView(Context c, int theme, int resource) {
        this.theme = theme;
        this.mContext = c;
        this.convertView = View.inflate(c, resource, null);
    }

    public void initBottomView(boolean canceledOnTouchOutside) {
        if (theme == 0) {
            bv = new Dialog(mContext);
        } else {
            bv = new Dialog(mContext, theme);
        }

        convertView.setVisibility(View.VISIBLE);
        bv.setCanceledOnTouchOutside(canceledOnTouchOutside);
        bv.getWindow().requestFeature(1);
        bv.setContentView(convertView);
        refresh(mContext);
    }

    public void refresh(Context context) {
        mContext = context;
        initMaxHeight(mContext);

        Window wm = bv.getWindow();
        WindowManager.LayoutParams p = wm.getAttributes();

        if (mHeight > 0) {
            p.height = mHeight > mMaxHeight ? mMaxHeight : mHeight;
        }

        p.width = width;
        p.gravity = isTop ? Gravity.TOP | Gravity.CENTER_HORIZONTAL :
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        p.x = x;
        p.y = y;
        wm.setAttributes(p);

        if (this.animationStyle != 0) {
            wm.setWindowAnimations(this.animationStyle);
        }
    }

    private void initMaxHeight(Context context) {
        if (context != null) {
            int statusBarHeight = AppUtils.getStatusBarHeight(context);
            int blankHeight = AppUtils.getAppBarHeight() + AppUtils.dpToPx(BOTTOM_VIEW_HEIGHT) + statusBarHeight;

            int scrHeight;
            int scrWidth;
            //不要删除，修改
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                scrWidth = AppUtils.getScreenWidth(mContext);
                mMaxHeight = scrWidth - blankHeight;
            } else {
                scrHeight = AppUtils.getScreenHeight(mContext);
                mMaxHeight = scrHeight - blankHeight;
            }
        }
    }

    public void setConvertView(View convertView) {
        this.convertView = convertView;
    }

    //cancel
    public void setKeyListener(DialogInterface.OnKeyListener listener) {
        if (bv != null) {
            if (listener != null) {
                bv.setOnKeyListener(listener);
            } else {
                bv.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (bv.isShowing()) {
                                bv.dismiss();
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    //dismiss
    public void setDismissListener(DialogInterface.OnDismissListener listener) {
        if (bv != null) {
            if (listener != null) {
                bv.setOnDismissListener(listener);
            }
        }
    }

    public void setCancelListener(DialogInterface.OnCancelListener listener) {
        if (bv != null) {
            if (listener != null) {
                bv.setOnCancelListener(listener);
            }
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setTopIfNecessary() {
        this.isTop = true;
    }

    public void setAnimation(int animationStyle) {
        this.animationStyle = animationStyle;
    }

    public View getView() {
        return convertView;
    }

    public Dialog getDialog() {
        return bv;
    }

    public void dismissBottomView() {
        if (bv != null) {
            bv.setOnCancelListener(null);
            bv.dismiss();
            mContext = null;
            convertView = null;
        }
    }

    public void showBottomView() {
        if (bv != null) {
            try {
                bv.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isShowing() {
        return bv != null && bv.isShowing();
    }

}
