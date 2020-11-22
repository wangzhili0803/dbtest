package com.jerry.baselib.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import android.content.res.AssetManager;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.Key;
import com.jerry.baselib.common.asyctask.AppTask;

public class ListCacheUtil {

    private static final String JSON_DIR_NAME = "app_json";
    public static final String FISHPOND = "fish_pond";
    public static final String AUTO_REPLAY_IMG = "auto_replay_img";

    public static String getValueFromAssetJsonFile(String assetJsonName) {
        AssetManager mgr = BaseApp.getInstance().getAssets();
        if (mgr == null) {
            return Key.NIL;
        }
        InputStream is = null;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            is = mgr.open("json/" + assetJsonName);
            ReadableByteChannel fc = Channels.newChannel(is);
            ByteBuffer buffer = ByteBuffer.allocate(1 << 13);
            int j;
            while ((j = fc.read(buffer)) != -1) {
                buffer.flip();
                bao.write(buffer.array(), 0, j);
                buffer.clear();
            }
            return bao.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return Key.NIL;
        } finally {
            FileUtil.close(is);
            FileUtil.close(bao);
        }
    }

    /**
     * 使用NIO获取json串
     */
    public static String getValueFromJsonFile(String key) {
        File jsonFile = getJsonFile(key);
        if (jsonFile == null || !jsonFile.exists()) {
            return Key.NIL;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(jsonFile);
            FileChannel fc = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1 << 13);
            int j;
            while ((j = fc.read(buffer)) != -1) {
                buffer.flip();
                bao.write(buffer.array(), 0, j);
                buffer.clear();
            }
            return bao.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return Key.NIL;
        } finally {
            FileUtil.close(fis);
            FileUtil.close(bao);
        }
    }

    /**
     * 同步：保存json串到文件中，不保存到sharepreference文件中
     */
    public static boolean saveValueToJsonFile(String key, String value) {
        File jsonFile = getJsonFile(key);
        if (jsonFile == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(jsonFile);
            FileChannel fc = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(value.getBytes());
            buffer.put(value.getBytes());
            buffer.flip();
            fc.write(buffer);
            fc.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(fos);
        }
        return false;
    }

    /**
     * 异步：保存json串到文件中，不保存到sharepreference文件中
     */
    public static void saveValueToJsonFile(String key, String value, OnFinishCallback onFinishCallback) {
        AppTask.withoutContext().assign(() -> saveValueToJsonFile(key, value)).whenDone(result -> {
            if (onFinishCallback != null && (boolean) result) {
                onFinishCallback.onDataSave();
            }
        }).execute();
    }

    /**
     * 清理json文件
     *
     * @param key 文件名
     */
    public static void clearJsonFiles(final String... key) {
        File parentFile = new File(BaseApp.getInstance().getCacheDir().getParent() + File.separator + JSON_DIR_NAME);
        if (parentFile.exists()) {
            for (String s : key) {
                FileUtil.clearFile(new File(parentFile, s));
            }
        }
    }

    /**
     * 获取json文件
     *
     * @param key 文件名
     */
    private static File getJsonFile(final String key) {
        File parentFile = new File(BaseApp.getInstance().getCacheDir().getParent() + File.separator + JSON_DIR_NAME);
        File file = null;
        if (parentFile.exists()) {
            file = new File(parentFile, key);
        } else {
            if (parentFile.mkdir()) {
                file = new File(parentFile, key);
            }
        }
        return file;
    }

    public interface OnFinishCallback {

        void onDataSave();
    }
}
