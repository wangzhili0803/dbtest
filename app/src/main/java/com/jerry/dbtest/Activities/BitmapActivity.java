package com.jerry.dbtest.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jerry.dbtest.R;
import com.jerry.dbtest.utils.DiskLruCacheUtil;

import java.io.InputStream;

public class BitmapActivity extends Activity {

    private Button btn_bitmap;
    private ImageView iv_bitmap;
    private DiskLruCacheUtil util;
    private static final String DISK_CACHE_SUBDIR = "temp";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
    private static final String url = "http://img5.duitang.com/uploads/item/201207/18/20120718215150_QyFQZ.thumb.700_0.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        iv_bitmap = (ImageView) findViewById(R.id.iv_bitmap);
        btn_bitmap = (Button) findViewById(R.id.btn_bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        util = DiskLruCacheUtil.getInstance();
        util.open(this, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        util.flush();
    }

    @Override
    protected void onStop() {
        super.onStop();
        util.close();
    }

    public void getBitmapClick(View view) {
        Bitmap bitmap = util.getBitmapFromCache(url);
        if (bitmap == null) {
            InputStream in = util.getFromDiskCache(url);
            if (in == null) {
                util.putToDiskCache(url, new DiskLruCacheUtil.Callback<Bitmap>() {
                    @Override
                    public void response(Bitmap entity) {
                        System.out.println("http load");
                        iv_bitmap.setImageBitmap(entity);
                    }
                });
            } else {
                System.out.println("disk cache");
                bitmap = BitmapFactory.decodeStream(in);
                iv_bitmap.setImageBitmap(bitmap);
                util.addBitmapToCache(url, bitmap);
            }
        } else {
            System.out.println("memory cache");
            iv_bitmap.setImageBitmap(bitmap);
        }
    }
}
