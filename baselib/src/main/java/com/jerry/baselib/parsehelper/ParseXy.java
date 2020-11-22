package com.jerry.baselib.parsehelper;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.jerry.baselib.common.bean.LookUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jerry.baselib.common.bean.Area;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.bean.TypeManager;
import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.Patterns;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;

import okhttp3.Response;

/**
 * 淘宝
 */
public class ParseXy {

    public static void main(String[] args) {
        Product product = getdata("https://2.taobao.com/item.htm?id=596588914396");
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

            Elements keywords = findByName(allElements, "name", "keywords");
            String title = keywords.first().attr("content");
            int fsf = title.indexOf("- 闲鱼");
            if (fsf > 0) {
                title = title.substring(0, fsf);
            }
            product.setTitle(title);
            String content = null;
            Elements description = allElements.select("#J_DescContent");
            String requesturl = description.first().attr("data-url");
            if (!TextUtils.isEmpty(requesturl)) {
                if (!requesturl.contains("http")) {
                    requesturl = "http:" + requesturl;
                }
                if (URLUtil.isValidUrl(requesturl)) {
                    Response response = OkHttpUtils.get().url(requesturl).build().execute();
                    content = response.body().string();
                    content = content.replace("\\\n", "\n").replace("&quot;", "\"");
                    int index = content.indexOf("'");
                    if (index > 0) {
                        content = content.substring(index + 1);
                    }
                    index = content.lastIndexOf("'");
                    if (index > 0) {
                        content = content.substring(0, index);
                    }
                }
            }
            if (TextUtils.isEmpty(content)) {
                description = findByName(allElements, "name", "description");
                content = description.first().attr("content");
            }
            product.setContent(content);

            // 价格
            Elements ems = doc.getElementsByTag("span");
            Element ss = ems.select(".price").first();
            String price = ss.getElementsByTag("em").text().trim();
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

            //地区
            Elements areass = allElements.select(".idle-info");
            if (!CollectionUtils.isEmpty(areass)) {
                Element areas = areass.get(0);
                Elements lis = areas.getElementsByTag("li");
                for (int i = 0; i < lis.size(); i++) {
                    Element li = lis.get(i);
                    Elements keyNode = li.select(".para");
                    if (!CollectionUtils.isEmpty(keyNode)) {
                        String key = keyNode.get(0).text();
                        if (!TextUtils.isEmpty(key) && key.contains("所")) {
                            Elements valueNode = li.getElementsByTag("em");
                            if (!CollectionUtils.isEmpty(valueNode)) {
                                String value = valueNode.get(0).text();
                                String[] sdf = StringUtil.safeSplit(value);
                                if (sdf.length > 0) {
                                    Area.setArea(sdf[0], product);
                                    if (sdf.length > 1) {
                                        product.setDistrict(sdf[1]);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(product.getProvice())) {
                Area.setArea(PreferenceHelp.getString(PreferenceHelp.PUBLISH_ADDRESS), product);
            }

            // tag
            Elements tagss = allElements.select(".intro-para");
            if (!CollectionUtils.isEmpty(tagss)) {
                Element tags = tagss.get(0);
                Elements keyValues = tags.getElementsByTag("span");
                StringBuilder lsb = new StringBuilder();
                sb.delete(0, sb.length());
                for (int i = 1; i < keyValues.size(); i++) {
                    if (i % 2 == 1) {
                        String key = keyValues.get(i - 1).text();
                        String value = keyValues.get(i).text();
                        if (key.contains("优先标签")) {
                            lsb.append(value).append(" ");
                        } else if (key.contains("卧室")) {
                            product.setHouseRoom(Patterns.getNumberFromStr(value));
                        } else if (key.contains("客厅")) {
                            product.setHouseHall(Patterns.getNumberFromStr(value));
                        } else if (key.contains("卫生间")) {
                            product.setHouseToilet(Patterns.getNumberFromStr(value));
                        } else if (key.contains("房屋配置")) {
                            lsb.append(value).append(" ");
                        } else if (key.contains("面积")) {
                            product.setAcreage(ParseUtil.parseDouble(value));
                            product.setIsHouse(true);
                            product.setHireType(TypeManager.findRent(TypeManager.RENT[PreferenceHelp.getInt(PreferenceHelp.HOUSE_RENT, 1)]));
                            product.setRoomType(TypeManager.findRoomType(TypeManager.ROOM_TYPE[PreferenceHelp.getInt(PreferenceHelp.HOUSE_ROOM_TYPE, 2)]));
                            product.setHouseRenovation(TypeManager.findRenovation(TypeManager.RENOVATION[PreferenceHelp.getInt(PreferenceHelp.HOUSE_RENOVATION, 2)]));
                        } else {
                            sb.append(value);
                        }
                    }
                }
                if (lsb.length() > 0) {
                    product.setHouseLabels(lsb.toString().trim());
                }
                if (sb.length() > 0) {
                    product.setTag(sb.toString().trim());
                }
            }

            // 图片链接
            sb.delete(0, sb.length());
            StringBuilder imgsb = new StringBuilder();
            Elements imgss = allElements.select(".small-img");
            for (int i = 0; i < imgss.size(); i++) {
                Element element = imgss.get(i);
                String img = element.attr("src");
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
            product.setReplyPic(ListCacheUtil.getValueFromJsonFile(ListCacheUtil.AUTO_REPLAY_IMG));
            product.setReplay(PreferenceHelp.getString(PreferenceHelp.AUTO_REPLAY, Product.AUTO_REPLAY));
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

    public static boolean getdata(LookUrl lookUrl) {
        //构造一个webClient 模拟Chrome 浏览器
        try {
            StringBuilder sb = new StringBuilder();
            Document doc = Jsoup.connect(lookUrl.getLink()).get();
            Elements allElements = doc.getAllElements();

            Elements keywords = findByName(allElements, "name", "keywords");
            String title = keywords.first().attr("content");
            int fsf = title.indexOf("- 闲鱼");
            if (fsf > 0) {
                title = title.substring(0, fsf);
            }
            lookUrl.setTitle(title);
            String content = null;
            Elements description = allElements.select("#J_DescContent");
            String requesturl = description.first().attr("data-url");
            if (!TextUtils.isEmpty(requesturl)) {
                if (!requesturl.contains("http")) {
                    requesturl = "http:" + requesturl;
                }
                if (URLUtil.isValidUrl(requesturl)) {
                    Response response = OkHttpUtils.get().url(requesturl).build().execute();
                    content = response.body().string();
                    content = content.replace("\\\n", "\n").replace("&quot;", "\"");
                    int index = content.indexOf("'");
                    if (index > 0) {
                        content = content.substring(index + 1);
                    }
                    index = content.lastIndexOf("'");
                    if (index > 0) {
                        content = content.substring(0, index);
                    }
                }
            }
            if (TextUtils.isEmpty(content)) {
                description = findByName(allElements, "name", "description");
                content = description.first().attr("content");
            }
            lookUrl.setContent(content);

            // 图片链接
            sb.delete(0, sb.length());
            StringBuilder imgsb = new StringBuilder();
            Element element = allElements.select(".small-img").get(0);
            String img = element.attr("src");
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
            if (sb.length() > 0) {
                lookUrl.setPicPath(sb.toString().trim());
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

    public static boolean getImgs(String url, Product product) {
        //构造一个webClient 模拟Chrome 浏览器
        try {
            Document doc = Jsoup.connect(url).get();
            Elements allElements = doc.getAllElements();

            // 图片链接
            StringBuilder imgsb = new StringBuilder();
            Elements imgss = allElements.select(".small-img");
            for (int i = 0; i < imgss.size(); i++) {
                Element element = imgss.get(i);
                String img = element.attr("src");
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

}