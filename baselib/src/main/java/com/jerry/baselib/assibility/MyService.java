package com.jerry.baselib.assibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.common.util.WeakHandler;

public class MyService extends Service {

    private static ArrayList<String> mUrls = new ArrayList<>();

    public static boolean canRun = true;
    public static boolean playing;

    private static List<WebView> webViews = new ArrayList<>();

    private WeakHandler weakHandler = new WeakHandler(msg -> {
        int random = StringUtil.getRandomInt(1000, 3000);
        if (canRun && playing && webViews.size() == mUrls.size()) {
            for (int i = 0; i < webViews.size(); i++) {
                webViews.get(i).loadUrl(mUrls.get(i));
            }
        } else {
            LogUtils.v("webViews和mUrls数组大小不相等");
        }
        this.weakHandler.sendEmptyMessageDelayed(0, random);
        return false;
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weakHandler.sendEmptyMessage(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setUrl(String... urls) {
        canRun = false;
        int offset = webViews.size() - urls.length;
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                webViews.remove(webViews.size() - 1);
            }
        } else if (offset < 0) {
            for (int i = 0; i < -offset; i++) {
                WebView webView = new WebView(this);
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibilityTraversal");
                webView.removeJavascriptInterface("accessibility");

                WebSettings settings = webView.getSettings();
                settings.setDefaultTextEncodingName("UTF-8");
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccessFromFileURLs(false);
                settings.setAllowUniversalAccessFromFileURLs(false);
                settings.setAllowFileAccess(false);

                settings.setDisplayZoomControls(false);// 不显示缩放按钮
                settings.setBuiltInZoomControls(true);// 设置内置的缩放控件
                settings.setSupportZoom(true);
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 支持内容重新布局
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
                settings.setLoadsImagesAutomatically(true);// 支持自动加载图片
                settings.setNeedInitialFocus(true); // 当WebView调用requestFocus时为WebView设置节点
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                settings.setDomStorageEnabled(true);
                settings.setAppCacheEnabled(true);
                settings.setDatabaseEnabled(true);
                settings.setUserAgentString("pc-wap");

                webView.requestFocus();
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return overrideUrlLoading(url);
                    }

                    private boolean overrideUrlLoading(String url) {
                        if (URLUtil.isNetworkUrl(url)) {
                            webView.loadUrl(url);
                        }
                        return true;
                    }
                });
                webViews.add(webView);
            }
        }
        mUrls.clear();
        mUrls.addAll(Arrays.asList(urls));
        canRun = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }
}