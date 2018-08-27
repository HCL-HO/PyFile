package com.hec.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.webservice.BaseService;
import com.scottyab.aescrypt.AESCrypt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.hec.app.config.CommonConfig.IV_BYTE;
import static com.hec.app.config.CommonConfig.KEY_BYTE;

public class BrandFragment extends Fragment {
    private WebView mWebView;
    private ProgressDialog mLoadingDialog;
    private int mBrandUrlArrayIndex;
    private Map<String,Long> mBrandMap = new ConcurrentHashMap<>();
    private boolean mIsBrandUrlsError = false;
    private boolean mIsShowed = false;

    public BrandFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIsShowed = false;
        mBrandUrlArrayIndex = 0;
        mBrandMap = new ConcurrentHashMap<>();
        View view = inflater.inflate(R.layout.fragment_brand, container, false);
        mWebView = (WebView) view.findViewById(R.id.brand_web_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isAdded()) {
                    showLoading(getResources().getString(R.string.loading_message));
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mIsShowed = true;
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                closeLoading();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!mIsShowed) {
                        mWebView.evaluateJavascript("getCsrf()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                try {
                                    if ("".equals(value) || "null".equals(value) || value == null) {
                                        mIsShowed = false;
                                        loadBrandUrl();
                                    }
                                    else {
                                        String decrypt = decrypt(value.replaceAll("\"", ""), "q3wc2541");
                                        if ("Goddess".equals(decrypt)) {
                                            mIsShowed = true;
                                        }
                                        else {
                                            mIsShowed = false;
                                            loadBrandUrl();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                else {
                    mIsShowed = true;
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result)
            {
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }})
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                result.confirm();
                            }
                        })
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
            {
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }})
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }})
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                result.confirm();
                            }
                        })
                        .show();

                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!title.contains("JX") && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    loadBrandUrl();
                }
            }
        });

        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    public String encrypt(String value) {
        String result = "";

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY_BYTE, "android.media.mediacodec.mode");
            byte[] ciphers = AESCrypt.encrypt(secretKeySpec, IV_BYTE, value.getBytes());
            result = Base64.encodeToString(ciphers, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String encodeUrl(String str) {
        String result = "";
        try {
            for (int i = 0; i < str.length(); i++) {
                char c  = str.charAt(i);
                result += URLEncoder.encode(String.valueOf(c), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBrandUrl();
    }

    private String getGoddnessUrl(String domainUrl) {
        CustomerInfo info = CustomerAccountManager.getInstance().getCustomer();
        String encryptApi;
        String encryptKey;
        if (info != null) {
            GoddnessEntity entity = new GoddnessEntity(info.getUserName(), Integer.parseInt(info.getUserID()), info.getAuthenticationKey(), info.getIsInfoComplete());
            String jsonStr = new Gson().toJson(entity);
            encryptKey = encodeUrl(encrypt(jsonStr));
            encryptApi = encodeUrl(encrypt(BaseService.getRestfulServiceHost().endsWith("/")? BaseService.getRestfulServiceHost() : BaseService.getRestfulServiceHost() + "/"));
            String url = domainUrl + "Account/LogOnTokenMobileValid?key=" + encryptKey + "&api=" + encryptApi;
            return url;
        }
        return "";
    }

    public String decrypt(String message, String key) throws Exception {
        byte[] bytesrc = new byte[message.length() / 2];
        for (int i = 0 ; i < bytesrc.length ; i++) {
            bytesrc[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mWebView != null && !mIsShowed) {
                loadBrandUrl();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void showLoading(String tips) {
        closeLoading();
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtil.getProgressDialog(getActivity(), tips);
            }
            mLoadingDialog.setMessage(tips);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLoading() {
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBrandUrl() {
        if (BuildConfig.SIT || BuildConfig.UAT || BuildConfig.DEBUG) {
            String url = getGoddnessUrl(UrlConfig.BRAND_TEST_DOMAIN);
            mWebView.loadUrl(url);
        } else {
            String url = getGoddnessUrl(UrlConfig.BRAND_URL);
            mWebView.loadUrl(url);
        }
    }

    public class GoddnessEntity {
        private String userName;
        private int userID;
        private String key;
        private Boolean isInfoComplete;

        public GoddnessEntity(String userName, int userID, String key, Boolean isInfoComplete) {
            this.userName = userName;
            this.userID = userID;
            this.key = key;
            this.isInfoComplete = isInfoComplete;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Boolean getInfoComplete() {
            return isInfoComplete;
        }

        public void setInfoComplete(Boolean infoComplete) {
            isInfoComplete = infoComplete;
        }
    }
}
