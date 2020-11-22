package com.jerry.baselib.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.Key;

public class BackupUtil {

    /**
     * 使用NIO获取json串
     */
    public static List<String> getBackupValues() {
        List<String> keys = new ArrayList<>();
        String path = FileUtil.getAppExternalPath() + "backup";
        File jsonFile = new File(path);
        if (jsonFile.exists()) {
            File[] files = jsonFile.listFiles();
            for (File file : files) {
                keys.add(file.getName());
            }
        }
        return keys;
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
    public static boolean delete(String key) {
        File jsonFile = getJsonFile(key);
        if (jsonFile != null) {
            return jsonFile.delete();
        }
        return false;
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
     * 获取json文件
     *
     * @param key 文件名
     */
    private static File getJsonFile(final String key) {
        String path = FileUtil.getAppExternalPath() + "backup";
        File parentFile = new File(path);
        File file = null;
        if (parentFile.exists()) {
            file = new File(parentFile, key);
        } else {
            if (parentFile.mkdirs()) {
                file = new File(parentFile, key);
            }
        }
        return file;
    }
}
