package com.jerry.baselib.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.common.asyctask.AppTask;
import com.jerry.baselib.common.asyctask.BackgroundTask;
import com.jerry.baselib.common.asyctask.WhenTaskDone;

/**
 * Created by zhangkk on 16/9/19. 类说明: bitmap工具
 */
public class BitmapUtil {

    private BitmapUtil() {
    }

    public static boolean saveBitmap2File(Bitmap bmp, Bitmap.CompressFormat format, int quality, String fileName, String filePath) {
        if (!FileUtil.isHaveSDCard()) {
            ToastUtil.showShortText("没有检测到sd卡");
            return false;
        }
        try {
            File dir = new File(filePath);
            if (dir.exists()) {
                return saveBitmap2File(bmp, format, quality, new File(dir, fileName));
            } else {
                return dir.mkdirs() && saveBitmap2File(bmp, format, quality, new File(dir, fileName));
            }
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean saveBitmap2File(Bitmap bmp, Bitmap.CompressFormat format, int quality, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(format, quality, fos);
            Uri uri = Uri.fromFile(file);
            BaseApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Throwable e) {
            return false;
        } finally {
            if (null != fos) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static void copyFiles(boolean md5, List<String> froms, String to, OnDataCallback<Boolean> onDataCallback) {
        if (froms == null || froms.size() == 0 || to == null) {
            if (onDataCallback != null) {
                onDataCallback.onDataCallback(true);
            }
            return;
        }
        copyFile(md5, froms.size() - 1, froms, to, onDataCallback);
    }

    private static void copyFile(boolean md5, int i, List<String> froms, String to, OnDataCallback<Boolean> onDataCallback) {
        if (!AppUtils.playing) {
            return;
        }
        if (i < 0 || i >= froms.size()) {
            onDataCallback.onDataCallback(true);
            return;
        }
        AppTask.withoutContext().assign((BackgroundTask<Boolean>) () -> {
            String from = froms.get(i);
            String expandName = from.substring(from.lastIndexOf("."));
            String newpath = to + "/img_" + System.currentTimeMillis() + expandName;
            File newFile = new File(newpath);
            if (md5 && !".mp4".equals(expandName)) {
                if (MD5.md5CopyFile(new File(from), newFile)) {
                    BaseApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)));
                    return true;
                }
            } else if (FileUtil.copyFile(new File(from), newFile)) {
                BaseApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)));
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }).whenDone((WhenTaskDone<Boolean>) result -> {
            ToastUtil.showShortText("复制图片：" + (froms.size() - i) + "/" + froms.size());
            copyFile(md5, i - 1, froms, to, onDataCallback);
        }).execute();
    }

    public static void clearFile(String imgcache) {
        File file = new File(imgcache);
        if (FileUtil.clearFile(file)) {
            file.delete();
        }
        BaseApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imgcache))));
    }
}
