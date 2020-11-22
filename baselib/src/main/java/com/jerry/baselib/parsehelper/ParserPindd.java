package com.jerry.baselib.parsehelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;
import com.jerry.baselib.common.bean.Area;
import com.jerry.baselib.common.bean.PddBean;
import com.jerry.baselib.common.bean.PddBean.StoreBean.InitDataObjBean.GoodsBean;
import com.jerry.baselib.common.bean.PddBean.StoreBean.InitDataObjBean.GoodsBean.DetailGalleryBean;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.okhttp.OkHttpUtils;
import com.jerry.baselib.common.util.BitmapUtil;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.JJSON;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.OnDataChangedListener;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.ToastUtil;

import okhttp3.Response;

/**
 * @author Jerry
 * @createDate 2019-05-20
 * @description
 */
public class ParserPindd {

    private static ParserPindd instance;
    private WebLoader mWebLoader;
    private static boolean stop;

    public static ParserPindd getInstance() {
        if (instance == null) {
            synchronized (ParserPindd.class) {
                if (instance == null) {
                    instance = new ParserPindd();
                }
            }
        }
        return instance;
    }

    public void getdata(Context context, String url, OnDataChangedListener<Product> onDataChangedListener) {
        if (TextUtils.isEmpty(url)) {
            onDataChangedListener.onDataChanged(null);
            return;
        }
        if (mWebLoader == null) {
            mWebLoader = new WebLoader(context);
        }
        mWebLoader.load(url, data -> AppTask.withoutContext().assign((BackgroundTask<Product>) () -> {
            try {
                if (stop) {
                    return null;
                }
                Product product = new Product();
                List<String> imgs = new ArrayList<>();
                Document doc = Jsoup.parse(data);
                Elements allElements = doc.getAllElements();
                Elements scriptNodes = doc.getElementsByTag("script");
                for (Element scriptNode : scriptNodes) {
                    String dd = scriptNode.outerHtml().trim();
                    if (dd.startsWith("<script>\n")) {
                        dd = dd.replace("\n", "").replace(" ", "");
                        if (dd.startsWith("<script>window.isUseHttps")) {
                            int start = dd.indexOf("window.rawData=");
                            int end = dd.lastIndexOf(";");
                            dd = dd.substring(start + 15, end);
                            PddBean pddBean = JJSON.parseObject(dd, PddBean.class);
                            GoodsBean goods = pddBean.getStore().getInitDataObj().getGoods();
                            if (goods == null || TextUtils.isEmpty(goods.getGoodsName()) /*&& TextUtils.isEmpty(cookie)*/) {
                                Elements sale = allElements.select(".goods-no-on-sale-txt");
                                if (sale != null && sale.size() > 0) {
                                    String saleText = sale.get(0).text();
                                    if (saleText.contains("下架")) {
                                        return null;
                                    }
                                }
                                ToastUtil.showShortText("拼多多采集失败了 登录试试");
                                Intent intent = new Intent(BaseApp.getInstance(), WebViewActivity.class);
                                intent.putExtra(Key.DATA, url);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                BaseApp.getInstance().startActivity(intent);
                                stop = true;
                                return null;
                            }
                            product.setTitle(goods.getGoodsName());
                            product.setContent(goods.getGoodsDesc());
                            product.setPrice(goods.getMinGroupPrice());
                            product.setPprice(goods.getLinePrice());
                            List<String> top = goods.getTopGallery();
                            for (String imgStr : top) {
                                end = imgStr.indexOf("jpg?");
                                if (end > 0) {
                                    imgStr = imgStr.substring(0, end + 4);
                                } else {
                                    end = imgStr.indexOf("jpeg?");
                                    if (end > 0) {
                                        imgStr = imgStr.substring(0, end + 4);
                                    }
                                }
                                if (!TextUtils.isEmpty(imgStr)) {
                                    if (!imgStr.startsWith("http")) {
                                        imgStr = "http:" + imgStr;
                                    }
                                    imgs.add(imgStr);
                                }
                            }
                            List<DetailGalleryBean> detailGalleryBeans = goods.getDetailGallery();
                            for (DetailGalleryBean detailGalleryBean : detailGalleryBeans) {
                                String imgStr = detailGalleryBean.getUrl();
                                end = imgStr.indexOf("jpg?");
                                if (end > 0) {
                                    imgStr = imgStr.substring(0, end + 4);
                                } else {
                                    end = imgStr.indexOf("jpeg?");
                                    if (end > 0) {
                                        imgStr = imgStr.substring(0, end + 4);
                                    }
                                }
                                if (!TextUtils.isEmpty(imgStr)) {
                                    if (!imgStr.startsWith("http")) {
                                        imgStr = "http:" + imgStr;
                                    }
                                    imgs.add(imgStr);
                                }
                            }
                            break;
                        }
                    }
                }

                // 图片链接
                StringBuilder sb = new StringBuilder();
                StringBuilder imgsb = new StringBuilder();
                for (String img : imgs) {
                    imgsb.append(img).append(" ");
                    Response response = OkHttpUtils.get().url(img).build().execute();
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    String name = "img_" + System.currentTimeMillis() + ".jpg";
                    String appExternalPath = FileUtil.getAppExternalPath();
                    sb.append(appExternalPath).append(name).append(" ");
                    BitmapUtil.saveBitmap2File(bitmap, CompressFormat.JPEG, 100, name, appExternalPath);
                }
                if (sb.length() > 0) {
                    product.setPicPath(sb.toString().trim());
                }
                if (imgsb.length() > 0) {
                    product.setRemoteImgs(imgsb.toString().trim());
                }

                sb.delete(0, sb.length());
                product.setReplay(PreferenceHelp.getString(PreferenceHelp.AUTO_REPLAY, Product.AUTO_REPLAY));
                product.setReplyPic(ListCacheUtil.getValueFromJsonFile(ListCacheUtil.AUTO_REPLAY_IMG));
                product.setIsEntity(PreferenceHelp.getBoolean(PreferenceHelp.ISENTITY));
                product.setIsNew(PreferenceHelp.getBoolean(PreferenceHelp.ISNEW, true));
                product.setTrans(PreferenceHelp.getString(PreferenceHelp.PUBLISH_TRANS_FEE));
                product.setLink(url);
                product.setType(PreferenceHelp.getInt(PreferenceHelp.PRODUCT_TYPE, 8));
                product.setCount(Math.max(1, PreferenceHelp.getInt(PreferenceHelp.PUBLISH_COUNT, 1)));
                product.setAdRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_ADDRESS));
                product.setFishRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_FISH));
                product.setFishPond(PreferenceHelp.getString(PreferenceHelp.FISH_POND));
                Area.setArea(PreferenceHelp.getString(PreferenceHelp.PUBLISH_ADDRESS), product);
                product.setUpdateTime(System.currentTimeMillis());
                product.setIsFree(PreferenceHelp.getBoolean(PreferenceHelp.FREESEND));
                if (ProManager.getInstance().insertObject(product)){
                    return product;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).whenDone((WhenTaskDone<Product>) onDataChangedListener::onDataChanged).execute());
    }

    public void reset() {
        if (mWebLoader != null) {
            mWebLoader.reset();
        }
        stop = false;
    }

    public static boolean getImgs(String url, Product product) {
        String cookie = PreferenceHelp.getString(PreferenceHelp.PDD_COOKIE);
        //构造一个webClient 模拟Chrome 浏览器
        try {
            List<String> imgs = new ArrayList<>();
            Document doc = Jsoup.connect(url).header("Cookie", cookie).get();
            Elements scriptNodes = doc.getElementsByTag("script");
            for (Element scriptNode : scriptNodes) {
                String dd = scriptNode.outerHtml().trim();
                if (dd.startsWith("<script>\n")) {
                    dd = dd.replace("\n", "").replace(" ", "");
                    if (dd.startsWith("<script>window.isUseHttps")) {
                        int fsf = dd.indexOf("window.rawData=");
                        if (fsf > 0) {
                            dd = dd.substring(fsf + 15, dd.length() - 1);
                            PddBean pddBean = JJSON.parseObject(dd, PddBean.class);
                            GoodsBean goods = pddBean.getStore().getInitDataObj().getGoods();
                            List<String> top = goods.getTopGallery();
                            for (String imgStr : top) {
                                int end = imgStr.indexOf("jpg?");
                                if (end > 0) {
                                    imgStr = imgStr.substring(0, end + 4);
                                } else {
                                    end = imgStr.indexOf("jpeg?");
                                    if (end > 0) {
                                        imgStr = imgStr.substring(0, end + 4);
                                    }
                                }
                                if (!TextUtils.isEmpty(imgStr)) {
                                    if (!imgStr.startsWith("http")) {
                                        imgStr = "http:" + imgStr;
                                    }
                                    imgs.add(imgStr);
                                }
                            }
                            List<DetailGalleryBean> detailGalleryBeans = goods.getDetailGallery();
                            for (DetailGalleryBean detailGalleryBean : detailGalleryBeans) {
                                String imgStr = detailGalleryBean.getUrl();
                                int end = imgStr.indexOf("jpg?");
                                if (end > 0) {
                                    imgStr = imgStr.substring(0, end + 4);
                                } else {
                                    end = imgStr.indexOf("jpeg?");
                                    if (end > 0) {
                                        imgStr = imgStr.substring(0, end + 4);
                                    }
                                }
                                if (!TextUtils.isEmpty(imgStr)) {
                                    if (!imgStr.startsWith("http")) {
                                        imgStr = "http:" + imgStr;
                                    }
                                    imgs.add(imgStr);
                                }
                            }
                        }
                    }
                }
            }

            // 图片链接
            StringBuilder sb = new StringBuilder();
            for (String img : imgs) {
                sb.append(img).append(" ");
            }
            if (sb.length() > 0) {
                product.setRemoteImgs(sb.toString().trim());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
