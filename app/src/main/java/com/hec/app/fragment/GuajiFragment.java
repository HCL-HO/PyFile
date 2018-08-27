package com.hec.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hec.app.util.CustomerAccountManager;
import com.hec.app.R;
import com.hec.app.webservice.BaseService;


public class GuajiFragment extends Fragment {

    private WebView webView;

    public GuajiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guaji, container, false);
        webView = (WebView) view.findViewById(R.id.wb_guaji);
        webView.loadUrl(BaseService.SLOT_CDN_URL
                +"Scheme?username="
                + CustomerAccountManager.getInstance().getCustomer().getUserName()
                //+ "fish002"
                + "&token="
                + CustomerAccountManager.getInstance().getCustomer().getAuthenticationKey());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        Log.d("guaji", "webview create");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        return view;
    }

}
