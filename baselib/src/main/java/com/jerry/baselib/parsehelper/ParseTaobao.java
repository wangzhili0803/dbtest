package com.jerry.baselib.parsehelper;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jerry.baselib.common.bean.Area;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.PreferenceHelp;

import okhttp3.Response;

/**
 * 淘宝
 */
public class ParseTaobao {

    public static void main(String[] args) {
        Product product = getdata("https://item.taobao.com/item.htm?id=588731147730");
        if (product != null) {
            System.out.println(product.toString());
        }
    }

    public static Product getdata(String url) {
        Product product = new Product();
        if (getdata(url, product)) {
            return product;
        }
        return null;
    }

    public static boolean getdata(String url, Product product) {
        //构造一个webClient 模拟Chrome 浏览器
        try {
            StringBuilder sb = new StringBuilder();
            Document doc = Jsoup.connect(url).get();
            Elements allElements = doc.getAllElements();
            // 提取基本信息 title、content、地区
            Elements keywords = findByName(allElements, "name", "keywords");
            String baseInfo = keywords.first().attr("content").replace("  ", " ");
            String[] info = baseInfo.split(" ");
            if (info.length == 3) {
                product.setTitle(info[0]);
                Area.setArea(info[2], product);
            } else {
                for (int i = 1; i < info.length; i++) {
                    sb.append(info[i]);
                }
                String title = sb.toString().trim();
                product.setTitle(title);
                Area.setArea(PreferenceHelp.getString(PreferenceHelp.PUBLISH_ADDRESS), product);
            }

            keywords = findByName(allElements, "name", "description");
            baseInfo = keywords.first().attr("content").replace("  ", " ");
            product.setContent(baseInfo);

            // 价格
            Elements ems = doc.getElementsByTag("em");
            String price = ems.select(".tb-rmb-num").first().text().replace("¥", "").replace("$", "").trim();
            if (PreferenceHelp.getBoolean(PreferenceHelp.FREESEND)) {
                int index = price.indexOf(".");
                if (index > 0) {
                    product.setPrice(price.substring(0, index));
                } else {
                    product.setPrice(price);
                }
            } else {
                product.setPrice(price);
            }

            // 图片链接
            sb.delete(0, sb.length());
            StringBuilder imgsb = new StringBuilder();
            Elements imgss = findContainNodeKey(allElements, "data-src");
            for (int i = 0; i < imgss.size(); i++) {
                Element element = imgss.get(i);
                String img = element.attr("data-src");
                int last = img.lastIndexOf(".jpg_");
                if (last > 0) {
                    img = img.substring(0, last + 4);
                }
                int alicdn = img.indexOf("alicdn.com");
                if (alicdn >= 0) {
                    img = img.substring(alicdn);
                }
                img = "http://img." + img;
                imgsb.append(img).append(" ");
                Response response = OkHttpUtils.get().url(img).build().execute();
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                String name = "img_" + System.currentTimeMillis() + ".jpg";
                String appExternalPath = FileUtil.getAppExternalPath();
                BitmapUtil.saveBitmap2File(bitmap, Bitmap.CompressFormat.JPEG, 100, name, appExternalPath);
                sb.append(appExternalPath).append(name).append(" ");
            }
            if (sb.length() > 0) {
                product.setPicPath(sb.toString().trim());
            }
            if (imgsb.length() > 0) {
                product.setRemoteImgs(imgsb.toString().trim());
            }
            // 标签
            sb.delete(0, sb.length());
            Element uls = doc.getElementsByTag("ul").select(".attributes-list").first();
            if (uls != null) {
                Elements liss = uls.children();
                Elements lis = findContainNodeKey(liss, "title");
                for (int i = Math.max(lis.size() - 5, 0); i < lis.size(); i++) {
                    sb.append(lis.get(i).attr("title").trim()).append("#");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                product.setTag(sb.toString());
            }
            product.setReplay(PreferenceHelp.getString(PreferenceHelp.AUTO_REPLAY, Product.AUTO_REPLAY));
            product.setReplyPic(ListCacheUtil.getValueFromJsonFile(ListCacheUtil.AUTO_REPLAY_IMG));
            product.setIsEntity(PreferenceHelp.getBoolean(PreferenceHelp.ISENTITY));
            product.setIsNew(PreferenceHelp.getBoolean(PreferenceHelp.ISNEW, true));
            product.setTrans(PreferenceHelp.getString(PreferenceHelp.PUBLISH_TRANS_FEE));
            product.setLink(url);
            product.setType(PreferenceHelp.getInt(PreferenceHelp.PRODUCT_TYPE, 8));
            product.setCount(Math.max(1, PreferenceHelp.getInt(PreferenceHelp.PUBLISH_COUNT, 1)));
            product.setAdRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_ADDRESS));
            product.setUpdateTime(System.currentTimeMillis());
            product.setFishRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_FISH));
            product.setFishPond(PreferenceHelp.getString(PreferenceHelp.FISH_POND));
            product.setIsFree(PreferenceHelp.getBoolean(PreferenceHelp.FREESEND));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getImgs(String url, Product product) {
        //构造一个webClient 模拟Chrome 浏览器
        try {
            Document doc = Jsoup.connect(url).get();
            Elements allElements = doc.getAllElements();
            StringBuilder imgsb = new StringBuilder();
            Elements imgss = findContainNodeKey(allElements, "data-src");
            for (int i = 0; i < imgss.size(); i++) {
                Element element = imgss.get(i);
                String img = element.attr("data-src");
                int last = img.lastIndexOf(".jpg_");
                if (last > 0) {
                    img = img.substring(0, last + 4);
                }
                int alicdn = img.indexOf("alicdn.com");
                if (alicdn >= 0) {
                    img = img.substring(alicdn);
                }
                img = "http://img." + img;
                imgsb.append(img).append(" ");
            }
            if (imgsb.length() > 0) {
                product.setRemoteImgs(imgsb.toString().trim());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从nodes中提取name属性为nname的node
     */
    private static Elements findByName(Elements nodes, String attributeName, String attributeValue) {
        Elements elements = new Elements();
        for (int i = 0; i < nodes.size(); i++) {
            Element node = nodes.get(i);
            Attributes attributes = node.attributes();
            for (Attribute attribute : attributes) {
                if (attributeName.equals(attribute.getKey()) && attributeValue.equals(attribute.getValue())) {
                    elements.add(node);
                }
            }
        }
        return elements;
    }

    /**
     * 从nodes中提取含有nname的node
     */
    private static Elements findContainNodeKey(Elements nodes, String nname) {
        Elements elements = new Elements();
        for (int i = 0; i < nodes.size(); i++) {
            Element node = nodes.get(i);
            Attributes attributes = node.attributes();
            for (Attribute attribute : attributes) {
                if (nname.equals(attribute.getKey())) {
                    elements.add(node);
                    break;
                }
            }
        }
        return elements;
    }

}