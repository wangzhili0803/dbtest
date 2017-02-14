package com.vince.dbtest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/12/18.
 */
public class DiskLruCacheUtil {
    private DiskLruCache diskLruCache;
    private Context context;
    private static DiskLruCacheUtil cacheUtil;
    private LruCache<String, Bitmap> lruCache;

    private DiskLruCacheUtil() {
    }

    public static DiskLruCacheUtil getInstance() {
        if (cacheUtil == null) {
            cacheUtil = new DiskLruCacheUtil();
        }
        return cacheUtil;
    }

    //打开磁盘缓存，并初始化内存缓存
    public void open(Context context, String disk_cache_subdir, int disk_cache_size) {
        try {
            this.context = context;
            //初始化内存缓存
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获取当前Activity内存大小
            int size = am.getMemoryClass();
            int max = size / 8 * 1024 * 1024;
            lruCache = new LruCache<String, Bitmap>(max);
            //第一个参数：缓存地址
            //第二个参数：应用程序的版本号
            //第三个参数：一个key可对应多少个value文件，通常设为1
            //第四个参数：指定最多可以缓存多少字节的数据，通常为10M
            diskLruCache = DiskLruCache.open(getCacheDir(disk_cache_subdir), getAppVersion(), 1, disk_cache_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取appVersion
     *
     * @return
     */
    private int getAppVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取缓存目录
     *
     * @param name 文件夹名
     * @return
     */
    private File getCacheDir(String name) {
        String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageEmulated() ?
                context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + name);
    }

    /**
     * 根据url获取输入流 即从磁盘缓存中读取数据
     *
     * @param url
     * @return
     */
    public InputStream getFromDiskCache(String url) {
        String key = hashKeyForDisk(url);
        //从DiskLruCache中获取一个快照，类似于百度快照
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskLruCache.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (snapshot != null) {
            return snapshot.getInputStream(0);
        }
        return null;
    }

    //调用接口
    public interface Callback<T> {
        void response(T entity);
    }

    /**
     * 将数据写入磁盘缓存中
     *
     * @param url
     */
    public void putToDiskCache(final String url, final Callback callback) {
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                String key = hashKeyForDisk(params[0]);
                DiskLruCache.Editor editor = null;
                Bitmap bitmap = null;
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    conn.setReadTimeout(30 * 1000);
                    //先将图片下载到手机的内存中，然后通过读取内存获取图片
                    ByteArrayOutputStream baos = null;
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 8 * 1024);
                        baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        baos.close();
                        conn.disconnect();
                    }
                    if (baos != null) {
                        //通过流获取到bitmap对象
                        bitmap = decodeSampleBitmapFromStream(baos.toByteArray(), 100, 100);
                        //将bitmap对象添加到内存缓存中
                        addBitmapToCache(params[0], bitmap);
                        //将bitmap对象添加到磁盘缓存中
                        editor = diskLruCache.edit(key);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, editor.newOutputStream(0));
                        editor.commit();//提交
                    }
                } catch (IOException e) {
                    try {
                        editor.abort();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callback.response(bitmap);
            }
        }.execute(url);
    }

    /**
     * 获取key对应的MD5码
     *
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mDigest.update(key.getBytes());
        cacheKey = bytesToHexString(mDigest.digest());
        return cacheKey;
    }

    /**
     * 获取key对应的MD5码
     *
     * @param bytes
     * @return
     */
    public String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0XFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append(hex);
            }
        }
        return sb.toString();
    }

    /**
     * 从内存缓存中读取bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromCache(String url) {
        String key = hashKeyForDisk(url);
        return lruCache.get(key);
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        String key = hashKeyForDisk(url);
        if (lruCache.get(key) == null) {
            lruCache.put(key, bitmap);
        }
    }

    /**
     * 通过流的方式获取图片
     *
     * @param bytes     传输的字节流
     * @param reqWidth  图宽
     * @param reqHeight 图高
     * @return
     */
    public Bitmap decodeSampleBitmapFromStream(byte[] bytes, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 位图的重采样
     *
     * @param res
     * @param resid
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromResource(Resources res, int resid, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resid, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resid, options);
    }

    /**
     * 计算位图的采样比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //获取位图的原宽高
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;
        if (w > reqWidth || h > reqHeight) {
            if (w > h) {
                inSampleSize = Math.round((float) h / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) w / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    //关闭磁盘缓存
    public void close() {
        if (diskLruCache != null && !diskLruCache.isClosed()) {
            try {
                diskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //刷新磁盘缓存
    public void flush() {
        if (diskLruCache != null) {
            try {
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
