package com.jerry.baselib.parsehelper;

import android.webkit.JavascriptInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jerry.baselib.common.util.LogUtils;

/**
 * @author Jerry
 * @createDate 2019-05-31
 * @description
 */
public class WebViewUtil {

    @JavascriptInterface
    public void getSource(String html) {
        Document document = Jsoup.parse(html);
        Elements allElements = document.getAllElements();
        LogUtils.d("ddd");
    }
}
