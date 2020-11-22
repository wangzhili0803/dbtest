package com.jerry.baselib.parsehelper;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.WeakHandler;

/**
 * @author Jerry
 * @createDate 2019-11-29
 * @description
 */
public class WebLoader {

    private WebView webview;
    private String currentUrl;
    private WeakHandler mHandler = new WeakHandler();
    private ConcurrentHashMap<String, OnDataChangedListener<String>> mArrayMap = new ConcurrentHashMap<>();
    private ArrayList<String> hasComplete = new ArrayList<>();

    public WebLoader(final Context context) {
        webview = new WebView(context);
        try {
            webview.removeJavascriptInterface("searchBoxJavaBridge_");
            webview.removeJavascriptInterface("accessibilityTraversal");
            webview.removeJavascriptInterface("accessibility");

            WebSettings settings = webview.getSettings();
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
            webview.requestFocus();
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return overrideUrlLoading(url);
                }

                private boolean overrideUrlLoading(String url) {
                    if (URLUtil.isNetworkUrl(url)) {
                        webview.loadUrl(url);
                    }
                    return true;
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookieStr = cookieManager.getCookie(url);
                    if (!TextUtils.isEmpty(cookieStr) && cookieStr.contains("pdd_user_id")) {
                        PreferenceHelp.putString(PreferenceHelp.PDD_COOKIE, cookieStr);
                    }
                }
            });
            webview.addJavascriptInterface(this, "java_obj");
            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(final WebView view, final int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        String current = view.getUrl();
                        if (!hasComplete.contains(current)) {
                            hasComplete.add(current);
                            view.loadUrl("javascript:window.java_obj.getSource(document.documentElement.outerHTML);void(0)");
                        }
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    public synchronized void load(String url, OnDataChangedListener<String> onDataChangedListener) {
        if (currentUrl == null) {
            currentUrl = url;
            webview.loadUrl(url);
        }
        mArrayMap.put(url, onDataChangedListener);
    }

    public void reset() {
        currentUrl = null;
        hasComplete.clear();
        mArrayMap.clear();
    }

    @JavascriptInterface
    public void getSource(String html) {
        LogUtils.d(html);
        OnDataChangedListener<String> onDataChangedListener = mArrayMap.remove(currentUrl);
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged(html);
        }
        if (mArrayMap.isEmpty()) {
            currentUrl = null;
        } else {
            Set<String> ss = mArrayMap.keySet();
            for (String s : ss) {
                if (!TextUtils.isEmpty(s)) {
                    currentUrl = s;
                    mHandler.post(() -> webview.loadUrl(currentUrl));
                    break;
                }
            }
        }
    }
}
