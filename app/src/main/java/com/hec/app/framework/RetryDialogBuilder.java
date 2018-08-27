package com.hec.app.framework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by wangxingjian on 2016/12/19.
 */

public class RetryDialogBuilder extends AlertDialog.Builder {

    public RetryDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        return super.setPositiveButton(text, listener);
    }
}
