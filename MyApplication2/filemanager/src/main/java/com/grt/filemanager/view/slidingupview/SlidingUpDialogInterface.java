package com.grt.filemanager.view.slidingupview;

import android.view.KeyEvent;

public interface SlidingUpDialogInterface {
    /**
     * The identifier for the positive button.
     */
    int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    int BUTTON_NEUTRAL = -3;

    void cancel();

    void dismiss();

    /**
     * Interface used to allow the creator of a mDialog to run some code when the
     * mDialog is canceled.
     * <p/>
     * This will only be called when the mDialog is canceled, if the creator
     * needs to know when it is dismissed in general, use
     * {@link SlidingUpDialogInterface.OnDismissListener}.
     */
    interface OnCancelListener {
        /**
         * This method will be invoked when the mDialog is canceled.
         *
         * @param dialog The mDialog that was canceled will be passed into the
         *               method.
         */
        void onCancel(SlidingUpDialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a mDialog to run some code when the
     * mDialog is dismissed.
     */
    interface OnDismissListener {
        /**
         * This method will be invoked when the mDialog is dismissed.
         *
         * @param dialog The mDialog that was dismissed will be passed into the
         *               method.
         */
        void onDismiss(SlidingUpDialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a mDialog to run some code when the
     * mDialog is shown.
     */
    interface OnShowListener {
        /**
         * This method will be invoked when the mDialog is shown.
         *
         * @param dialog The mDialog that was shown will be passed into the
         *               method.
         */
        void onShow(SlidingUpDialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a mDialog to run some code when an
     * item on the mDialog is clicked..
     */
    interface OnClickListener {
        /**
         * This method will be invoked when a button in the mDialog is clicked.
         *
         * @param dialog The mDialog that received the click.
         * @param which  The button that was clicked (e.g.
         *               {@link SlidingUpDialogInterface}) or the position
         *               of the item clicked.
         */
        /* TODO: Change to use BUTTON_POSITIVE after API council */
        void onClick(SlidingUpDialogInterface dialog, int which);
    }

    /**
     * Interface used to allow the creator of a mDialog to run some code when an
     * item in a multi-choice mDialog is clicked.
     */
    interface OnMultiChoiceClickListener {
        /**
         * This method will be invoked when an item in the mDialog is clicked.
         *
         * @param dialog    The mDialog where the selection was made.
         * @param which     The position of the item in the list that was clicked.
         * @param isChecked True if the click checked the item, else false.
         */
        void onClick(SlidingUpDialogInterface dialog, int which, boolean isChecked);
    }

    /**
     * Interface definition for a callback to be invoked when a key event is
     * dispatched to this mDialog. The callback will be invoked before the key
     * event is given to the mDialog.
     */
    interface OnKeyListener {
        /**
         * Called when a key is dispatched to a mDialog. This allows listeners to
         * get a chance to respond before the mDialog.
         *
         * @param dialog  The mDialog the key has been dispatched to.
         * @param keyCode The code for the physical key that was pressed
         * @param event   The KeyEvent object containing full information about
         *                the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onKey(SlidingUpDialogInterface dialog, int keyCode, KeyEvent event);
    }
}
