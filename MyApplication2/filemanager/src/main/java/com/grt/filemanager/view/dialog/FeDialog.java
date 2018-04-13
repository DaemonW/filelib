package com.grt.filemanager.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grt.filemanager.R;
import com.grt.filemanager.util.AppUtils;
import com.grt.filemanager.view.slidingupview.DialogListAdapter;
import com.grt.filemanager.view.slidingupview.DialogMultiChoiceAdapter;
import com.grt.filemanager.view.slidingupview.DialogSingleChoiceAdapter;

import java.io.Serializable;


public class FeDialog extends Dialog implements Serializable {

    private Context mContext;
    private TextView mTitleView, mPositiveButton, mNegativeButton, mNeutralButton,mAlignLeftButton;
    private LinearLayout mContentLayout;
    private RelativeLayout mButtonLayout;
    private RelativeLayout titleLayout;
    private ImageView mIcon;
    private boolean hasTitle = false;
    private static final int BUTTON_LEFT = 0;

    private boolean mIsSingleChoice = false;
    private boolean mIsMultiChoice = false;

    private boolean[] mCheckedItems;
    private int mCheckedItem;

    protected FeDialog(Context context) {
        super(context, R.style.Theme_Dialog_ListSelect);
        mContext = context;//getContext();
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog);

//        initTotalBackground();
        initTitleView();
        initContentLayout();
        initButtonLayout();

        setCanceledOnTouchOutside(true);
    }

    private void initTotalBackground() {
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }

    private void initTitleView() {
        mTitleView = (TextView) findViewById(R.id.title);
    }

    private void initContentLayout() {
        mContentLayout = (LinearLayout) findViewById(R.id.dialog_extra_layout);
    }

    private void initButtonLayout() {
        mButtonLayout = (RelativeLayout) findViewById(R.id.layout_bottom);
        titleLayout = (RelativeLayout) findViewById(R.id.rl_title);

        mIcon = (ImageView) findViewById(R.id.icon);
        mPositiveButton = (TextView) findViewById(R.id.dialog_right);//right
        mNeutralButton = (TextView) findViewById(R.id.dialog_middle);//middle
        mNegativeButton = (TextView) findViewById(R.id.dialog_left);//left
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(mContext.getText(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title == null) {
            hasTitle = false;
            mIcon.setVisibility(View.GONE);
            mTitleView.setVisibility(View.GONE);
            titleLayout.setVisibility(View.GONE);
            return;
        }
        hasTitle = true;
        if(title.equals(mContext.getResources().getString(R.string.app_name))){
            mIcon.setVisibility(View.VISIBLE);
        }
        titleLayout.setVisibility(View.VISIBLE);
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(title);
    }

    public void setMessage(int messageId) {
        setMessage(messageId, false);
    }

    public void setMessage(CharSequence message) {
        setMessage(message, false);
    }

    public void setMessage(int messageId, boolean linkifyAll) {
        setMessage(mContext.getText(messageId), linkifyAll);
    }

    public void setMessage(CharSequence message, boolean linkifyAll) {
        if (message == null) {
            mContentLayout.removeAllViews();
            return;
        }


        if(!hasTitle){
            TextView textView = new TextView(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.dpToPx(12));
            textView.setLayoutParams(params);
            mContentLayout.addView(textView);
        }

        View view = mContentLayout.findViewById(R.id.dialog_extra_textview);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.dialog_extra_textview_center, null);
        }

        TextView tv = (TextView) view.findViewById(R.id.extra_text);
        if (linkifyAll) {
            tv.setAutoLinkMask(Linkify.ALL);
        }
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv.setText(message);

        setView(view);
    }

    public void setView(View view) {
        mContentLayout.removeAllViews();

        if (view == null) {
            return;
        }

        if(!hasTitle){
            TextView textView = new TextView(mContext);
            textView.setTextSize(18);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    AppUtils.dpToPx(12));
            mContentLayout.addView(textView,p);
        }

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mContentLayout.addView(view, p);
    }

    public void setItems(CharSequence[] items,
                         final OnClickListener listener) {
        if (items == null) {
            mContentLayout.removeAllViews();
            return;
        }

        ListView listView = (ListView) findViewById(R.id.dialog_list);
        if (listView == null) {
            listView = (ListView) LayoutInflater.from(mContext).inflate(R.layout.dialog_listview, null);
        }

        // set different Adapter and different clickListener
        if (mIsMultiChoice) {
            final DialogMultiChoiceAdapter multiChoiceAdapter = new DialogMultiChoiceAdapter(
                    mContext, items, mCheckedItems);
            listView.setAdapter(multiChoiceAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View view,
                                        int position, long arg3) {

                    CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.checkbox_item);
                    checkBox.setChecked(!checkBox.isChecked());

                    if (listener != null) {
                        listener.onClick(FeDialog.this, position);
                    }
                }
            });
            setView(listView);
            return;
        }

        if (mIsSingleChoice) {

            final DialogSingleChoiceAdapter singleAdapter = new DialogSingleChoiceAdapter(
                    mContext, items, mCheckedItem);
            listView.setAdapter(singleAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View view,
                                        int position, long arg3) {

                    singleAdapter.setCheckedItem(position);
                    singleAdapter.notifyDataSetChanged();
                    if (listener != null) {
                        listener.onClick(FeDialog.this, position);
                    }
                }
            });
            setView(listView);
            return;
        }

        DialogListAdapter adapter = new DialogListAdapter(mContext, items, mCheckedItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (listener != null) {
                    listener.onClick(FeDialog.this, position);
                }
            }
        });
        setView(listView);
    }

    public void setButtonEnable(int whichButton, boolean enable) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                mPositiveButton.setEnabled(enable);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                mNegativeButton.setEnabled(enable);
                break;

            case DialogInterface.BUTTON_NEUTRAL:
                mNeutralButton.setEnabled(enable);
                break;
        }

    }

    public void setButton(int whichButton, CharSequence text,
                          final OnClickListener listener) {
        TextView processingButton;
        final int processingButtonType;

        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                processingButton = mPositiveButton;
                processingButtonType = DialogInterface.BUTTON_POSITIVE;
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                processingButton = mNegativeButton;
                processingButtonType = DialogInterface.BUTTON_NEGATIVE;
                break;

            case DialogInterface.BUTTON_NEUTRAL:
                processingButton = mNeutralButton;
                processingButtonType = DialogInterface.BUTTON_NEUTRAL;
                break;

            case BUTTON_LEFT:
                processingButton = mAlignLeftButton;
                processingButtonType = DialogInterface.BUTTON_NEUTRAL;
                break;

            default:
                processingButton = mNegativeButton;
                processingButtonType = DialogInterface.BUTTON_NEGATIVE;
        }

        if (text == null) {
            processingButton.setVisibility(View.GONE);
            if (mPositiveButton.getVisibility() == View.GONE
                    && mNegativeButton.getVisibility() == View.GONE
                    && mNeutralButton.getVisibility() == View.GONE
                    && mAlignLeftButton.getVisibility() == View.GONE) {
                mButtonLayout.setVisibility(View.GONE);
            }
            return;
        }

        mButtonLayout.setVisibility(View.VISIBLE);
        processingButton.setVisibility(View.VISIBLE);
        processingButton.setText(text);
        Log.e("fedialog","===="+text);
        processingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processingButtonType == DialogInterface.BUTTON_NEGATIVE) {
                    FeDialog.this.cancel();
                } else {
                    FeDialog.this.dismiss();
                }
                if (listener != null) {
                    listener.onClick(FeDialog.this, processingButtonType);
                }
            }
        });
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public static class Builder implements Serializable {

        boolean[] mCheckedItems;
        int mCheckedItem;
        private Context mContext;
        private int mType;
        private boolean mCancelable = true;
        private CharSequence mTitle, mMessage;
        private boolean mLinkifyAll;
        private View mView;
        private CharSequence[] mItems;
        private OnClickListener mOnClickListener;
        private OnMultiChoiceClickListener mOnMultiChoiceClickListener;
        private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;
        private OnClickListener mPositiveButtonListener, mNegativeButtonListener, mNeutralButtonListener;
        private OnCancelListener mOnCancelListener;
        private OnDismissListener mOnDismissListener;
        private boolean mIsSingleChoice = false;
        private boolean mIsMultiChoice = false;
        private FeDialog dialog;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setType(int type) {
            mType = type;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setTitle(int titleId) {
            mTitle = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            setMessage(messageId, false);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            setMessage(message, false);
            return this;
        }

        public Builder setMessage(int messageId, boolean linkifyAll) {
            mMessage = mContext.getText(messageId);
            mLinkifyAll = linkifyAll;
            return this;
        }

        public Builder setMessage(CharSequence message, boolean linkifyAll) {
            mMessage = message;
            mLinkifyAll = linkifyAll;
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setItems(CharSequence[] items,
                                final OnClickListener listener) {
            try {
                mItems = items;
            } catch (NotFoundException e) {
                e.printStackTrace();
            }

            mOnClickListener = listener;
            return this;
        }

        public Builder setItems(int itemArrayId,
                                final OnClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (NotFoundException e) {
            }
            mOnClickListener = listener;
            return this;
        }

        public Builder setSingleChoiceItems(int itemArrayId, int checkedItem,
                                            OnClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (NotFoundException e) {
            }
            mCheckedItem = checkedItem;
            mOnClickListener = listener;
            mIsSingleChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(int itemArrayId,
                                           boolean[] checkedItems,
                                           OnMultiChoiceClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (NotFoundException e) {
            }
            mCheckedItems = checkedItems;
            mOnMultiChoiceClickListener = listener;
            mIsMultiChoice = true;
            return this;
        }

        public Builder setPositiveButton(int textId,
                                         final OnClickListener listener) {
            mPositiveButtonText = mContext.getText(textId);
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text,
                                         final OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId,
                                         final OnClickListener listener) {
            mNegativeButtonText = mContext.getText(textId);
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text,
                                         final OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId,
                                        final OnClickListener listener) {
            mNeutralButtonText = mContext.getText(textId);
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text,
                                        final OnClickListener listener) {
            mNeutralButtonText = text;
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(
                OnCancelListener onCancelListener) {
            mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(
                OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        public FeDialog create() {
            dialog = new FeDialog(mContext);
//            if (android.os.Build.VERSION.SDK_INT > 19) {
//                createAlertDialog();
//                return dialog;
//            }

            if (!mCancelable) {
                dialog.setCancelable(false);
            }
            if (mTitle != null) {
                dialog.setTitle(mTitle);
            }
            if (mMessage != null) {
                dialog.setMessage(mMessage, mLinkifyAll);
            }
            if (mView != null) {
                dialog.setView(mView);
            }
            if (mItems != null) {
                dialog.mIsMultiChoice = mIsMultiChoice;
                dialog.mIsSingleChoice = mIsSingleChoice;
                dialog.mCheckedItem = mCheckedItem;
                dialog.mCheckedItems = mCheckedItems;
                dialog.setItems(mItems, mOnClickListener);
            }
            if (mPositiveButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        mPositiveButtonText, mPositiveButtonListener);
            }
            if (mNegativeButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        mNegativeButtonText, mNegativeButtonListener);
            }
            if (mNeutralButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        mNeutralButtonText, mNeutralButtonListener);
            }

            if (mOnCancelListener != null) {
                dialog.setOnCancelListener(mOnCancelListener);
            }
            if (mOnDismissListener != null) {
                dialog.setOnDismissListener(mOnDismissListener);
            }
            return dialog;
        }

        public FeDialog show() {
            FeDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public boolean isShow() {
            return dialog != null && dialog.isShowing();
        }

        public void dismiss() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }

    }

}
