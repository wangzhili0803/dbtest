package com.jerry.baselib.parsehelper;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.PreferenceHelp;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @description 拼多多拿cookie
 */
public class WebViewActivity extends BaseActivity {

    private String url;

    @Override
    protected void beforeViews() {
        Intent intent = getIntent();
        url = intent.getStringExtra(Key.DATA);
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        setTitle(R.string.app_name);
        WebView webview = findViewById(R.id.webview);
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
                        toast("拼多多登录成功");
                    }
                }
            });
            webview.addJavascriptInterface(new WebViewUtil(), "java_obj");
            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(final WebView view, final int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        view.loadUrl("javascript:window.java_obj.getSource(document.documentElement.outerHTML);void(0)");
                    }
                }
            });
            webview.loadUrl(url);
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
            finish();
        }
    }

    @Override
    public void onClick(final View v) {

    }
}