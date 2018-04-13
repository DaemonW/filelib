package com.grt.filemanager.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.grt.filemanager.R;


public class DialogLinearLayout extends LinearLayout {

    private Resources mResources;
    private int mDialogMargin;
    private Display mDisplay;
    private int mMinimumWidth;

    public DialogLinearLayout(Context context) {
        super(context);
        init();
    }

    public DialogLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DialogLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMinimumSize();
        setMaximumSize(widthMeasureSpec, heightMeasureSpec);
    }

    private void setMinimumSize() {
        int dialogMinWidthPercentage = 96;
        int minWidth = mDisplay.getWidth() * dialogMinWidthPercentage / 100;
        if (getMinimumWidth() != minWidth) {
            mMinimumWidth = minWidth;
            setMinimumWidth(minWidth);
        }
    }

    @SuppressLint("WrongCall")
    private void setMaximumSize(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.makeMeasureSpec(mDisplay.getWidth() - 2
                * mDialogMargin, MeasureSpec.AT_MOST);
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        int height = MeasureSpec.makeMeasureSpec(
                (mDisplay.getHeight() - rect.top) - 2 * mDialogMargin,
                MeasureSpec.AT_MOST);
        setMeasuredDimension(width & MEASURED_SIZE_MASK, height & MEASURED_SIZE_MASK);
        super.onMeasure(width, height);
    }

    private void init() {
        mResources = getContext().getResources();
        mDisplay = ((WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDialogMargin = (int) mResources.getDimension(R.dimen.dialog_margin);
    }

    @Override
    public int getMinimumWidth() {
//		return super.getMinimumWidth();
        return mMinimumWidth;
    }
}
