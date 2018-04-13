package com.grt.filemanager.view.slidingupview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grt.filemanager.R;

import java.io.Serializable;

public class SlidingUpDialog extends LinearLayout implements SlidingUpDialogInterface {

    public static final int SINGLE_CHOICE = 1;
    public static final int MULTI_CHOICE = 2;

    private Context mContext;
    private TextView mTitleView;
    private LinearLayout mContentLayout;
    private RelativeLayout mButtonLayout,mTitleLayout;
    private TextView mPositiveButton;
    private TextView mNeutralButton;
    private TextView mNegativeButton;
    private View mTitleDivider;

    private int mType;
    private boolean[] mCheckedItems;
    private int mCheckedItem;

    private SlidingUpDialogInterface.OnCancelListener mOnCancelListener;

    public SlidingUpDialog(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SlidingUpDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public SlidingUpDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.sliding_up_dialog, this);

        mTitleView = (TextView) findViewById(R.id.dialog_title);
        mContentLayout = (LinearLayout) findViewById(R.id.dialog_extra_layout);
        mButtonLayout = (RelativeLayout) findViewById(R.id.layout_bottom);
        mPositiveButton = (TextView) findViewById(R.id.dialog_right);
        mNeutralButton = (TextView) findViewById(R.id.dialog_middle);
        mNegativeButton = (TextView) findViewById(R.id.dialog_left);
        mTitleLayout = (RelativeLayout) findViewById(R.id.dialog_title_layout);
        mTitleDivider = findViewById(R.id.title_divider);
    }

    public void setTitle(CharSequence title) {
        if (title == null) {
            mTitleView.setVisibility(View.GONE);
            mTitleLayout.setVisibility(GONE);
            return;
        }
        mTitleView.setVisibility(View.VISIBLE);
        mTitleLayout.setVisibility(VISIBLE);
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

        View view = mContentLayout.findViewById(R.id.dialog_extra_textview);
        if (view == null) {
            view = View.inflate(mContext, R.layout.dialog_extra_textview, null);
        }

        TextView tv = (TextView) view.findViewById(R.id.extra_text);
        if (linkifyAll) {
            tv.setAutoLinkMask(Linkify.ALL);
        }
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv.setText(message);

        setView(view);
    }

    public void hideTitleDivider() {
        mTitleDivider.setVisibility(GONE);
    }

    public void setItems(CharSequence[] items,
                         final SlidingUpDialogInterface.OnClickListener listener) {
        if (items == null) {
            mContentLayout.removeAllViews();
            return;
        }

        ListView listView = (ListView) findViewById(R.id.dialog_list);
        if (listView == null) {
            listView = (ListView) View.inflate(mContext, R.layout.dialog_listview, null);
        }

        // set different Adapter and different clickListener
        switch (mType) {
            case SINGLE_CHOICE:
                final DialogSingleChoiceAdapter singleAdapter = new DialogSingleChoiceAdapter(
                        mContext, items, mCheckedItem);
                listView.setAdapter(singleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view,
                                            int position, long arg3) {

                        singleAdapter.setCheckedItem(position);
                        singleAdapter.notifyDataSetChanged();
                        if (listener != null) {
                            listener.onClick(SlidingUpDialog.this, position);
                        }
                    }
                });
                break;

            case MULTI_CHOICE:
                final DialogMultiChoiceAdapter multiChoiceAdapter = new DialogMultiChoiceAdapter(
                        mContext, items, mCheckedItems);
                listView.setAdapter(multiChoiceAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view,
                                            int position, long arg3) {

                        CheckedTextView checkBox = (CheckedTextView)
                                view.findViewById(R.id.checkbox_item);
                        checkBox.setChecked(!checkBox.isChecked());

                        if (listener != null) {
                            listener.onClick(SlidingUpDialog.this, position);
                        }
                    }
                });
                break;

            default:
                DialogListAdapter adapter = new DialogListAdapter(mContext, items, mCheckedItems);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        if (listener != null) {
                            listener.onClick(SlidingUpDialog.this, position);
                        }
                    }
                });
                break;
        }

        setView(listView);
    }

    public void setView(View view) {
        mContentLayout.removeAllViews();
        if (view == null) {
            return;
        }

        LayoutParams p = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mContentLayout.addView(view, p);
    }

    public TextView getmPositiveButton() {
        return mPositiveButton;
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

            default:
        }
    }

    public void setButton(int whichButton, CharSequence text,
                          final SlidingUpDialogInterface.OnClickListener listener) {
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

            default:
                processingButton = mNegativeButton;
                processingButtonType = DialogInterface.BUTTON_POSITIVE;
        }

        if (text == null) {
            processingButton.setVisibility(View.GONE);
            if (mPositiveButton.getVisibility() == View.GONE
                    && mNegativeButton.getVisibility() == View.GONE
                    && mNeutralButton.getVisibility() == View.GONE) {
                mButtonLayout.setVisibility(View.GONE);
            }
            return;
        }

        mButtonLayout.setVisibility(View.VISIBLE);
        processingButton.setVisibility(View.VISIBLE);
        processingButton.setText(text);
        processingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processingButtonType == SlidingUpDialogInterface.BUTTON_NEGATIVE) {
                    cancel();
                } else {
                    dismiss();
                }

                if (listener != null) {
                    listener.onClick(SlidingUpDialog.this, processingButtonType);
                }
            }
        });
    }

    public void setOnCancelListener(SlidingUpDialogInterface.OnCancelListener listener) {
        this.mOnCancelListener = listener;
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
    }

    @Override
    public void cancel() {
        mContext = null;
    }

    @Override
    public void dismiss() {

    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mOnCancelListener != null) {
                mOnCancelListener.onCancel(SlidingUpDialog.this);
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public static class Builder implements Serializable {

        private Context mContext;
        private int mType;
        private String mTitle, mMessage;
        private boolean mLinkIfyAll;
        private View mView;
        private String[] mItems;

        private SlidingUpDialogInterface.OnClickListener mOnClickListener;
        private SlidingUpDialogInterface.OnMultiChoiceClickListener mOnMultiChoiceClickListener;

        private String mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;
        private SlidingUpDialogInterface.OnClickListener mPositiveButtonListener,
                mNegativeButtonListener, mNeutralButtonListener;

        private SlidingUpDialog mDialog;
        private boolean[] mCheckedItems;
        private int mCheckedItem;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setType(int type) {
            mType = type;
            return this;
        }

        public Builder setTitle(int titleId) {
            mTitle = mContext.getString(titleId);
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            setMessage(messageId, false);
            return this;
        }

        public Builder setMessage(String message) {
            setMessage(message, false);
            return this;
        }

        public Builder setMessage(int messageId, boolean linkIfyAll) {
            mMessage = mContext.getString(messageId);
            mLinkIfyAll = linkIfyAll;
            return this;
        }

        public Builder setMessage(String message, boolean linkIfyAll) {
            mMessage = message;
            mLinkIfyAll = linkIfyAll;
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setItems(String[] items,
                                final SlidingUpDialogInterface.OnClickListener listener) {
            try {
                mItems = items;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            mOnClickListener = listener;
            return this;
        }

        public Builder setItems(int itemArrayId,
                                final SlidingUpDialogInterface.OnClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            mOnClickListener = listener;
            return this;
        }

        public Builder setSingleChoiceItems(int itemArrayId, int checkedItem,
                                            SlidingUpDialogInterface.OnClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            mCheckedItem = checkedItem;
            mOnClickListener = listener;
            mType = SINGLE_CHOICE;
            return this;
        }

        public Builder setMultiChoiceItems(int itemArrayId, boolean[] checkedItems,
                                           SlidingUpDialogInterface.OnMultiChoiceClickListener listener) {
            try {
                mItems = mContext.getResources().getStringArray(itemArrayId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            mCheckedItems = checkedItems;
            mOnMultiChoiceClickListener = listener;
            mType = MULTI_CHOICE;
            return this;
        }

        public Builder setPositiveButton(int textId,
                                         final SlidingUpDialogInterface.OnClickListener listener) {
            mPositiveButtonText = mContext.getString(textId);
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(String text,
                                         final SlidingUpDialogInterface.OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId,
                                         final SlidingUpDialogInterface.OnClickListener listener) {
            mNegativeButtonText = mContext.getString(textId);
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text,
                                         final SlidingUpDialogInterface.OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId,
                                        final SlidingUpDialogInterface.OnClickListener listener) {
            mNeutralButtonText = mContext.getString(textId);
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(String text,
                                        final SlidingUpDialogInterface.OnClickListener listener) {
            mNeutralButtonText = text;
            mNeutralButtonListener = listener;
            return this;
        }

        public SlidingUpDialog create() {
            mDialog = new SlidingUpDialog(mContext);

            mDialog.mType = mType;

            if (mTitle != null) {
                mDialog.setTitle(mTitle);
            }

            if (mMessage != null) {
                mDialog.setMessage(mMessage, mLinkIfyAll);
            }

            if (mView != null) {
                mDialog.setView(mView);
            }

            if (mItems != null) {
                mDialog.mCheckedItem = mCheckedItem;
                mDialog.mCheckedItems = mCheckedItems;
                mDialog.setItems(mItems, mOnClickListener);
            }

            if (mPositiveButtonText != null) {
                mDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        mPositiveButtonText, mPositiveButtonListener);
            }

            if (mNegativeButtonText != null) {
                mDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        mNegativeButtonText, mNegativeButtonListener);
            }

            if (mNeutralButtonText != null) {
                mDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        mNeutralButtonText, mNeutralButtonListener);
            }

            return mDialog;
        }
    }

}
