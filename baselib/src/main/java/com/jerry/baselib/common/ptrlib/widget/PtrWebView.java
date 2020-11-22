package com.jerry.baselib.common.ptrlib.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.jerry.baselib.R;
import com.jerry.baselib.common.base.BaseActivity;
import com.jerry.baselib.common.ptrlib.PageProgressView;
import com.jerry.baselib.common.ptrlib.header.PtrSimpleHeader;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.common.util.NetworkUtil;
import com.jerry.baselib.common.util.ToastUtil;

public class PtrWebView extends PtrSimpleView {

    private final Context mContext;
    private WebView mWebView;
    private LoadCallBack mLoadCallBack;

    public PtrWebView(Context context) {
        this(context, null);
    }

    public PtrWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void init() {
        try {
            PageProgressView pageProgressView = new PageProgressView(mContext);
            pageProgressView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress));
            mWebView = new WebView(mContext);
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.addView(pageProgressView);
            linearLayout.addView(mWebView);
            addView(linearLayout);
            PtrSimpleHeader mPtrSimpleHeader = new PtrSimpleHeader(getContext());
            setHeaderView(mPtrSimpleHeader);
            addPtrUIHandler(mPtrSimpleHeader);
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
            mWebView.removeJavascriptInterface("accessibility");

            WebSettings settings = mWebView.getSettings();
            settings.setDefaultTextEncodingName("UTF-8");
            settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
            settings.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
            settings.setAllowFileAccess(false);

            settings.setDisplayZoomControls(false);// 不显示缩放按钮
            settings.setBuiltInZoomControls(true);// 设置内置的缩放控件
            settings.setSupportZoom(true);//是否可以缩放，默认true
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 支持内容重新布局
            settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
            settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
            settings.setLoadsImagesAutomatically(true);// 支持自动加载图片
            settings.setNeedInitialFocus(true); // 当WebView调用requestFocus时为WebView设置节点
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            settings.setAppCacheEnabled(true);//是否使用缓存
            settings.setDomStorageEnabled(true);//DOM Storage

            setCache(settings);

            mWebView.requestFocus();
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
            mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
            WebView.setWebContentsDebuggingEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    LogUtils.d("newProgress==" + newProgress);
                    if (newProgress >= PageProgressView.MAX_PROGRESS) {
                        pageProgressView.setProgress(PageProgressView.MAX_PROGRESS);
                        pageProgressView.dismissWithAnim();
                    } else {
                        pageProgressView.setVisibility(View.VISIBLE);
                        pageProgressView.setProgress(Math.max(newProgress, 5));
                    }
                    if (newProgress == 100) {
                        refreshComplete();
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    LogUtils.d("onReceivedTitle==" + title);
                    if (TextUtils.isEmpty(title) || "找不到网页".equals(title)) {
                        return;
                    }
                    if (mLoadCallBack != null) {
                        mLoadCallBack.onReceivedTitle(title);
                    }
                }
            });

            //该方法解决的问题是打开浏览器不调用系统浏览器，直接用webview打开
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return overrideUrlLoading(url);
                }

                @RequiresApi(api = VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return overrideUrlLoading(request.getUrl().toString());
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    pageProgressView.setVisibility(View.VISIBLE);
                    pageProgressView.setProgress(5);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mLoadCallBack != null) {
                        mLoadCallBack.onPageFinished(url);
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    whenReceiveError(view);
                }

                @TargetApi(VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    LogUtils.d("onReceivedError,mUrlString=" + (view == null ? "" : view.getUrl()));
                    whenReceiveError(view);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();  // 接受网站的证书，防止出现空白页
                }

                private void whenReceiveError(WebView view) {
                    if (NetworkUtil.isNetworkAvailable(false)) {
                        view.stopLoading();
                    }
                }

                private boolean overrideUrlLoading(String url) {
                    if (URLUtil.isNetworkUrl(url)) {
                        //处理微信 H5 支付跳转时验证请求头 referer 失效
                        // 验证不通过会出现“商家参数格式有误，请联系商家解决”
                        // 兼容 Android 4.4.3 和 4.4.4 两个系统版本设置 referer 无效的问题
                        mWebView.loadUrl(url);
                        return false;
                    }
                    try {
                        if (url.startsWith("alipays://") || url.startsWith("alipay")) {
                            // ------  对alipays:相关的scheme处理 -------
                            try {
                                mWebView.loadUrl(url);
                                return true;
                            } catch (Exception e) {
                                // 启动支付宝失败，换成网页支付
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                return true;
                            }
                        } else {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }
                    } catch (Exception e) {
                        mWebView.loadUrl(url);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            ToastUtil.showShortText(R.string.app_not_install);
            ((BaseActivity) mContext).finish();
        }
    }

    private void setCache(WebSettings settings) {
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
//        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(mContext.getDir("appcache", Context.MODE_PRIVATE).getPath());
//        settings.setDatabasePath(mContext.getDir("databases", Context.MODE_PRIVATE).getPath());
//        settings.setGeolocationDatabasePath(mContext.getDir("geolocation", Context.MODE_PRIVATE).getPath());
    }

    public void loadUrl(String url) {
        if (mWebView == null || !URLUtil.isValidUrl(url)) {
            return;
        }
        mWebView.loadUrl(url);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWebView != null) {
            removeView(mWebView);
            mWebView.removeAllViews();
            mWebView.stopLoading();
            mWebView.destroy();
        }
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        if (mWebView != null && webChromeClient != null) {
            mWebView.setWebChromeClient(webChromeClient);
        }
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        if (mWebView != null && webViewClient != null) {
            mWebView.setWebViewClient(webViewClient);
        }
    }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }

    public String getTitle() {
        if (mWebView == null) {
            return mContext.getString(R.string.app_name);
        }
        return mWebView.getTitle();
    }

    public void reload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    public void setLoadCallBack(final LoadCallBack loadCallBack) {
        mLoadCallBack = loadCallBack;
    }

    public interface LoadCallBack {

        void onPageFinished(String url);

        void onReceivedTitle(String title);
    }
}
