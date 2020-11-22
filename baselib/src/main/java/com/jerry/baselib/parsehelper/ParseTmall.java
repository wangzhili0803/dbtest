package com.jerry.baselib.parsehelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.jerry.baselib.common.bean.Area;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.bean.TmallBean;
import com.jerry.baselib.common.bean.TmallBean.DetailDescBean;
import com.jerry.baselib.common.bean.TmallBean.DetailDescBean.NewWapDescJsonBean;
import com.jerry.baselib.common.bean.TmallBean.DetailDescBean.NewWapDescJsonBean.DataBean;
import com.jerry.baselib.common.bean.TmallBean.RateBean;
import com.jerry.baselib.common.bean.TmallBean.RateBean.KeywordsBean;
import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.PreferenceHelp;

import okhttp3.Response;

/**
 * 天猫
 */
public class ParseTmall {

    public static void main(String[] args) {
        Product product = getdata("https://detail.m.tmall.com/item.htm?id=587625815954");
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
            List<String> imgs = new ArrayList<>();
            // 提取基本信息 title、content
            Elements keywords = findByName(allElements, "property", "og:title");
            product.setTitle(keywords.first().attr("content"));
            keywords = findByName(allElements, "property", "og:image");
            imgs.add(keywords.first().attr("content"));

            String detal = "";
            String detal2 = "";
            Elements scripts = allElements.select("script");
            for (Element script : scripts) {
                if (script.childNodeSize() > 0) {
                    String dfst = script.childNode(0).outerHtml();
                    if (dfst != null) {
                        if (dfst.startsWith("var _DATA_Detail")) {
                            detal = dfst;
                        } else if (dfst.startsWith("var _DATA_Mdskip")) {
                            detal2 = dfst;
                        }
                    }
                }
            }
            detal = detal.substring(19, detal.length() - 1).trim();
            sb.delete(0, sb.length());
            TmallBean tmallBean = JSONObject.parseObject(detal, TmallBean.class);
            if (tmallBean != null) {
                RateBean rateBean = tmallBean.getRate();
                if (rateBean != null) {
                    List<KeywordsBean> keywordsBeans = rateBean.getKeywords();
                    if (!CollectionUtils.isEmpty(keywordsBeans)) {
                        for (KeywordsBean keywordsBean : keywordsBeans) {
                            if (keywordsBean.getType() == 1) {
                                sb.append(keywordsBean.getWord()).append("#");
                            }
                        }
                    }
                }
                DetailDescBean detailDescBean = tmallBean.getDetailDesc();
                if (detailDescBean != null) {
                    List<NewWapDescJsonBean> newWapDescJsonBeans = detailDescBean.getNewWapDescJson();
                    if (!CollectionUtils.isEmpty(newWapDescJsonBeans)) {
                        for (NewWapDescJsonBean newWapDescJsonBean : newWapDescJsonBeans) {
                            if (newWapDescJsonBean.getModuleType() == 1) {
                                List<DataBean> dataBeans = newWapDescJsonBean.getData();
                                if (!CollectionUtils.isEmpty(dataBeans)) {
                                    imgs.add(dataBeans.get(0).getImg());
                                }
                            }
                        }
                    }
                }
                String price = tmallBean.getMock().getPrice().getPrice().getPriceText();
                product.setPrice(price);
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                String content = sb.toString();
                product.setTag(content);
                product.setContent(content.replace("#", "，"));
            } else {
                product.setContent(product.getTitle());
            }

            int from = detal2.indexOf("from\":\"");
            if (from > 0) {
                detal2 = detal2.substring(from + 7);
                int to = detal2.indexOf("\"");
                if (to > 0) {
                    detal2 = detal2.substring(0, to);
                }
            }
            Area.setArea(detal2, product);

            // 图片链接
            sb.delete(0, sb.length());
            StringBuilder imgsb = new StringBuilder();
            for (String img : imgs) {
                if (img == null) {
                    continue;
                }
                if (!img.startsWith("http")) {
                    img = "http:" + img;
                }
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
            StringBuilder sb = new StringBuilder();
            Document doc = Jsoup.connect(url).get();
            Elements allElements = doc.getAllElements();
            List<String> imgs = new ArrayList<>();
            Elements keywords = findByName(allElements, "property", "og:image");
            imgs.add(keywords.first().attr("content"));

            String detal = "";
            Elements scripts = allElements.select("script");
            for (Element script : scripts) {
                if (script.childNodeSize() > 0) {
                    String dfst = script.childNode(0).outerHtml();
                    if (dfst != null) {
                        if (dfst.startsWith("var _DATA_Detail")) {
                            detal = dfst;
                        }
                    }
                }
            }
            detal = detal.substring(19, detal.length() - 1).trim();
            sb.delete(0, sb.length());
            TmallBean tmallBean = JSONObject.parseObject(detal, TmallBean.class);
            if (tmallBean != null) {
                DetailDescBean detailDescBean = tmallBean.getDetailDesc();
                if (detailDescBean != null) {
                    List<NewWapDescJsonBean> newWapDescJsonBeans = detailDescBean.getNewWapDescJson();
                    if (!CollectionUtils.isEmpty(newWapDescJsonBeans)) {
                        for (NewWapDescJsonBean newWapDescJsonBean : newWapDescJsonBeans) {
                            if (newWapDescJsonBean.getModuleType() == 1) {
                                List<DataBean> dataBeans = newWapDescJsonBean.getData();
                                if (!CollectionUtils.isEmpty(dataBeans)) {
                                    imgs.add(dataBeans.get(0).getImg());
                                }
                            }
                        }
                    }
                }
            }

            // 图片链接
            sb.delete(0, sb.length());
            StringBuilder imgsb = new StringBuilder();
            for (String img : imgs) {
                if (img == null) {
                    continue;
                }
                if (!img.startsWith("http")) {
                    img = "http:" + img;
                }
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
}