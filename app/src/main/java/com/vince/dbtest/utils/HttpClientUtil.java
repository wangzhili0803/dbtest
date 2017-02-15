package com.vince.dbtest.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientUtil {
//    public static HttpClient httpClient = null;
//
//    public static HttpClient getHttpClient() {
//        if (httpClient == null) {
//            // wap网络采用特殊httpclient模式
//            HttpParams httpParameters = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParameters, 8000);
//            HttpConnectionParams.setSoTimeout(httpParameters, 32000);
//            httpClient = new DefaultHttpClient(httpParameters);
//        }
//        return httpClient;
//    }

    /**
     * @param
     * @return RL对应网页的纯文本信息
     * @throws IOException
     */
    public static String getSourceFromURL(String htmlStr) throws IOException {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        // String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script,
                Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        // Pattern p_html = Pattern.compile(regEx_html,
        // Pattern.CASE_INSENSITIVE);
        // Matcher m_html = p_html.matcher(htmlStr);
        // htmlStr = m_html.replaceAll("\r\n"); // 过滤html标签

        // <provider_paths>段落替换为换行
        htmlStr = htmlStr.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        htmlStr = htmlStr.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        htmlStr = htmlStr.replaceAll("\\<.*?>", "");
        // 去掉js中的空格
        htmlStr = htmlStr.replaceAll("&nbsp;", " ");

        return htmlStr.trim(); // 返回文本字符串
    }
}
