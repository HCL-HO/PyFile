package com.hec.app.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.hec.app.R;
import com.hec.app.dialog.CustomerServiceAlertDialog;


public final class DialogUtil {

    public static ProgressDialog getProgressDialog(Context activity, String message) {
        return getProgressDialog(activity, ProgressDialog.STYLE_SPINNER, message);
    }

    public static ProgressDialog getProgressDialog(Context activity, int style, String message) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(style);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage(message);
        return dialog;
    }

    public static CustomerServiceAlertDialog getCustomerServiceDialog(Context context, CustomerServiceAlertDialog.CustomerServiceAlertDialogListener listener) {
        return new CustomerServiceAlertDialog(context, listener);
    }

    private static AlertDialog getAlertDialog(Context currentActivity, String title, String messageString,
                                              String positiveButtonText, DialogInterface.OnClickListener positiveButtonListener,
                                              CharSequence negativeButton, DialogInterface.OnClickListener negativeButtonListener,
                                              boolean isUsePositiveButton, boolean isUseNegativeButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        if (TextUtils.isEmpty(title)) {
            builder.setTitle(R.string.alert_dialog_title);
        } else {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(messageString)) {
            builder.setMessage(messageString).setCancelable(false);
        }
        if (isUsePositiveButton) {
            builder.setPositiveButton(positiveButtonText, positiveButtonListener);
        }
        if (isUseNegativeButton) {
            builder.setNegativeButton(negativeButton, negativeButtonListener);
        }
        return builder.create();
    }

    public static AlertDialog getAlertDialog(Context currentActivity, String title, String messageString,
                                             String positiveButtonText, DialogInterface.OnClickListener positiveButtonListener,
                                             CharSequence negativeButton, DialogInterface.OnClickListener negativeButtonListener) {
        return getAlertDialog(currentActivity, title, messageString, positiveButtonText, positiveButtonListener,
                negativeButton, negativeButtonListener, true, true);
    }

    public static AlertDialog getConfirmAlertDialog(Context currentActivity, String title, String messageString,
                                                    DialogInterface.OnClickListener positiveButtonListener) {
        return getAlertDialog(currentActivity, title, messageString, "确认", positiveButtonListener, "取消", null, true,
                true);
    }

    public static AlertDialog getConfirmAlertDialogWithNegativeBtn(Context currentActivity, String title,
                                                                   String messageString, DialogInterface.OnClickListener positiveButtonListener,
                                                                   DialogInterface.OnClickListener negativeButtonListener) {
        return getAlertDialog(currentActivity, title, messageString, "确认", positiveButtonListener, "取消",
                negativeButtonListener, true, true);
    }

    public static AlertDialog getErrorAlertDialog(Context currentActivity, String messageString) {
        return getAlertDialog(currentActivity, "错误", messageString, "确认", null, "", null, true, false);
    }

    public static AlertDialog getPromptAlertDialog(Context currentActivity, String title, String messageString, DialogInterface.OnClickListener negativeButtonListener) {
        return getAlertDialog(currentActivity, title, messageString, "确认", negativeButtonListener, "", null, true, false);
    }
}
