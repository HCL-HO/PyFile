package com.hec.app.jsbridge;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.hec.app.framework.widget.MyToast;

/**
 * Created by wangxingjian on 2017/8/22.
 */

public class BridgeImpl implements IBridge {

    public static void hidekeyboard(WebView webView){
        InputMethodManager imm = (InputMethodManager) webView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(webView.getWindowToken(),0);
        MyToast.show(webView.getContext(),"Eric hehe,I get your prompt.");
    }
}
