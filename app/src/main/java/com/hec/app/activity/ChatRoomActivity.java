package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.jsbridge.BridgeImpl;
import com.hec.app.jsbridge.JSBridge;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.webservice.BaseService;

import java.io.UnsupportedEncodingException;

public class ChatRoomActivity extends BaseActivity {

    private WebView webView;
    private String typeurl = "";
    private ProgressDialog progressDialog;
    private View root;
    private int keyboradHeight = 0;
    private RelativeLayout rl_webview_container,activity_chat_room;
    private TextView tv_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        rl_webview_container = (RelativeLayout) findViewById(R.id.rl_webview_container);
        activity_chat_room = (RelativeLayout) findViewById(R.id.activity_chat_room);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginChatroom();
            }
        });
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //webView = (WebView) findViewById(R.id.chatroom_web_view);
        webView = new WebView(ChatRoomActivity.this);
        webView.setLayoutParams(new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rl_webview_container.addView(webView);
        rl_webview_container.post(new Runnable() {
                @Override
                public void run() {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            changeWebviewSize();
                            loginChatroom();
                        }
                    });
                }
            });
        BaseApp.chatRoomActivity = ChatRoomActivity.this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    public void backClick (View v){
        //finish();
        moveTaskToBack(true);
    }

    public void loginChatroom(){

        typeurl = getIntent().getStringExtra("typeurl");
        if(typeurl!=null)
            typeurl = typeurl.toLowerCase();
        //webView.clearCache(true);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            // AppRTC requires third party cookies to work
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + " " + "android-version/" + BaseApp.instance().getVersionCode());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setDomStorageEnabled(true);
        try {
            webView.postUrl(
                    BaseService.CHAT_URL
                    //"http://bigbrothers.info:19040/"
                    ,("username=" + CustomerAccountManager.getInstance().getCustomer().getUserName()
                    + "&ticket=" + CustomerAccountManager.getInstance().getCustomer().getAuthenticationKey()
                    //+"&ticket=2084"
                    //+"&user_level=3"
                    //+"&nickname=Eric"
                    +"&room_label=" + typeurl).getBytes("utf-8")
                    );
            Log.i("wxj","chat username=" + CustomerAccountManager.getInstance().getCustomer().getUserName()
                    + "&ticket=" + CustomerAccountManager.getInstance().getCustomer().getAuthenticationKey()
                    + "&room_label=" + typeurl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                showProgressDialog("正在加载！");
                //tv_update.setVisibility(View.VISIBLE);
                return false;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                closeProgressDialog();
            }

            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        JSBridge.register("bridge", BridgeImpl.class);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //return super.onJsAlert(view, url, message, result);
                AlertDialog dialog = new AlertDialog.Builder(ChatRoomActivity.this).
                        setMessage(message).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        create();
                dialog.show();
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            //            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                //result.confirm(JSBridge.callJava(view, message));
//                return true;
//            }
        });
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(ChatRoomActivity.this,loadingMessage);
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    int originalHeight,inputheight;
    boolean onKeyclose = false;
    private void changeWebviewSize(){
        root = findViewById(android.R.id.content);
        rl_webview_container.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = rl_webview_container.getHeight();
                Rect r = new Rect();
                rl_webview_container.getWindowVisibleDisplayFrame(r);
                int visibleHeight0 = r.bottom - r.top;
                if(originalHeight <= visibleHeight0){
                    inputheight = visibleHeight0 - originalHeight;
                }
            }
        });
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_webview_container.getLayoutParams();
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rl_webview_container.getWindowVisibleDisplayFrame(r);
                int visibleHeight = r.bottom - r.top;

                if(originalHeight > visibleHeight){
                    params.height = visibleHeight - inputheight;
                    rl_webview_container.setLayoutParams(params);
                }else{
                    params.height = originalHeight;
                    rl_webview_container.setLayoutParams(params);
                    if(onKeyclose){
                        webView.clearFocus();
                        webView.loadUrl("javascript:window.document.activeElement.blur()");
                        onKeyclose = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        clearWebViewResource();
        super.onDestroy();
    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(nonRoot);
    }

    private void clearWebViewResource(){
        if(webView != null){
            webView.removeAllViews();
            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.setTag(null);
            webView.clearHistory();
            webView.destroy();
            webView = null;
        }
    }
}
