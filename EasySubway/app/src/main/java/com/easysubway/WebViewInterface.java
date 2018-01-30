package com.easysubway;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by eunhye Lee on 2017-08-08.
 */

public class WebViewInterface {
     WebView mAppView;
     static Activity mContext;
     static String openAPIKey;
    static String subwayLocationAPIKey;

    public WebViewInterface(Activity activity, WebView view, String openAPIKey, String subwayLocationAPIKey) {
        this.mAppView = view;
        this.mContext = activity;
        this.openAPIKey = openAPIKey;
        this.subwayLocationAPIKey = subwayLocationAPIKey;
    }

    @JavascriptInterface
    public void showSubwayInfo(String station) {
        Intent intent = new Intent(this.mContext, TrafficSubwayInfo.class);
        intent.putExtra("OpenAPIKey", this.openAPIKey);
        intent.putExtra("SubwayLocationAPIKey", this.subwayLocationAPIKey);
        intent.putExtra("StationNM", station);
        this.mContext.startActivity(intent);
    }
}
