package com.jerry.baselib.parsehelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.bean.Product;

/**
 * Created by 请叫我张懂 on 2017/9/25.
 */

public class TxtManager {

    private TxtManager() {
    }

    public static void main(String[] strs) {
        List<String> urls = readFile("/Users/jerry/Desktop/log.txt");
        if (urls != null) {
            for (String url : urls) {
                Product product = new Product();
                Uri uri = Uri.parse(url);
                if (url.contains("yangkeduo") || url.contains("pinduoduo")) {
                    String id = uri.getQueryParameter("goods_id");
//                    if (ParserPindd.getdata("https://mobile.yangkeduo.com/goods.html?goods_id=" + id, product)) {
//                        ProManager.getInstance().insertObject(product);
//                    }
                } else {
                    String id = uri.getQueryParameter("id");
                    if (ParseTmall.getdata("https://detail.m.tmall.com/item.htm?id=" + id, product)) {
                        ProManager.getInstance().insertObject(product);
                    } else if (ParseTaobao.getdata("https://item.taobao.com/item.htm?id=" + id, product)) {
                        ProManager.getInstance().insertObject(product);
                    }
                }
            }

        }
    }

    /**
     * 读入TXT文件
     */
    public static List<String> readFile(String path) {
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        List<String> urls = new ArrayList<>();
        try {
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                urls.add(line);
            }
            return urls;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }


}
