package com.jerry.baselib.common.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.common.bean.MediaBean;

/**
 * 文件处理类
 *
 * @author Tina
 */
public class FileUtil {

    private static final String SD_CARD = Environment.getExternalStorageDirectory() + File.separator;

    public static String getAppExternalPath() {
        return FileUtil.SD_CARD + PreferenceHelp.getString(PreferenceHelp.DIRNAME, BaseApp.getDefultDir()) + File.separator;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isHaveSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getSaveFile() {
        return new File(BaseApp.getInstance().getFilesDir(), "pic.jpg");
    }

    public static String getPath(Uri uri) {
        if (null == uri) {
            LogUtils.e("uri return null");
            return null;
        }

        LogUtils.d(uri.toString());
        String path = null;
        final String scheme = uri.getScheme();
        if (null == scheme) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = BaseApp.getInstance().getContentResolver().query(uri, proj, null, null, null);
            } catch (Exception e) {
                LogUtils.e(e.getLocalizedMessage());
            }
            if (null != cursor) {
                cursor.moveToFirst();
                try {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                } catch (Exception e) {
                    LogUtils.e(e.getLocalizedMessage());
                }
            }
            close(cursor);
        }
        return path;
    }

    /**
     * 拷贝
     */
    public static boolean copyFile(File srcFile, File destFile) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        // 如果源文件和目标文件相同则返回 false
        if (srcFile.equals(destFile)) {
            return false;
        }
        // 源文件不存在或者不是文件则返回 false
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {// 目标文件存在
            return true;
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destFile.getParentFile())) {
            return false;
        }
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private static boolean writeFileFromIS(File file, InputStream is) {
        if (!createOrExistsFile(file) || is == null) {
            return false;
        }
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            int sBufferSize = 8192;
            byte[] data = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            close(os, is);
        }
    }

    /**
     * 计算图片的缩放值
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     */
    private static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 获取本地所有的图片
     *
     * @return list
     */
    public static List<MediaBean> getAllLocalPhotos(int state) {
        ContentResolver contentResolver = BaseApp.getInstance().getContentResolver();
        List<MediaBean> result = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        if (state == 0 || state == 1) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(uri, null, null, null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(index); // 文件地址
                    index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                    String modified = cursor.getString(index); // 修改日期
                    File file = new File(path);
                    if (file.exists()) {
                        MediaBean mediaBean = new MediaBean();
                        mediaBean.setType(0);
                        mediaBean.setPath(path);
                        mediaBean.setModified(modified);
                        result.add(mediaBean);
                    }
                }
            }
        }
        if (state == 0 || state == 2) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(uri, null, null, null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    String path = cursor.getString(index); // 文件地址
                    index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
                    String modified = cursor.getString(index); // 修改日期
                    File file = new File(path);
                    if (file.exists()) {
                        MediaBean mediaBean = new MediaBean();
                        mediaBean.setType(1);
                        mediaBean.setPath(path);
                        mediaBean.setModified(modified);
                        result.add(mediaBean);
                    }
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 清空文件：参数为文件夹时，只清理其内部文件，不清理本身, 参数为文件时，删除
     */
    public static boolean clearFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return false;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    clearFile(f);
                } else {
                    f.delete();
                }
            }
        } else {
            file.delete();
        }
        return true;
    }

    public static void close(final Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable == null) {
                    continue;
                }
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
