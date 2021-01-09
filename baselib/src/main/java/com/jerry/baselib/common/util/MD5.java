package com.jerry.baselib.common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.jerry.baselib.BaseApp;

public class MD5 {

    public static String md5(String string) {
        byte[] hash;
        try {
            string = string + BaseApp.Config.SIGN;
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if (Integer.toHexString(0xFF & b).length() == 1) {
                hex.append("0").append(Integer.toHexString(0xFF & b));
            } else {
                hex.append(Integer.toHexString(0xFF & b));
            }
        }
        return hex.toString();
    }

    public static String md55(String string) {
        return md5(string).substring(0, 5);
    }

    /**
     * 给文件生成新的md5验证码
     */
    public static boolean md5CopyFile(File srcFile, File destFile) {
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
        String pathname = srcFile.getAbsolutePath();
        String newFileName = destFile.getAbsolutePath();
        //分割文件
        String[] names = partitionFile(pathname);
        //创建一个空文件
        String newFile = pathname + ".txt";
        copyStringToFile(UUID.randomUUID().toString(), newFile);

        //解决 md5 修改
        String[] newNames = new String[names.length + 1];
        System.arraycopy(names, 0, newNames, 0, names.length);

        newNames[names.length] = newFile;
        //文件合并,多添加了一个空文件
        boolean sucess = uniteFile(newNames, new File(newFileName));
        //删除所有文件
        deletes(newNames);
        return sucess;
    }

    /**
     * 文件分割
     */
    private static String[] partitionFile(String pathname) {
        DataInputStream in = null;
        String[] names;
        try {
            //DataOutputStream
            //通过DataOutputStream 这个对象完成文件的分割
            //缓存
            File file = new File(pathname);
            //long size = file.length();
            //1024*1024 就是 1MB
            //LogUtils.d(size/1024/1024);
            //获取分割文件的名称
            names = getPartitionFileNames(pathname);
            in = new DataInputStream(new FileInputStream(file));
            byte[] buff = new byte[1024];
            DataOutputStream out;
            for (String name : names) {
                out = new DataOutputStream(new FileOutputStream(new File(name)));
                long size = 524288000;
                while (size > 0) {
                    //当没有读取完的情况
                    if (in.read(buff) != -1) {
                        out.write(buff, 0, buff.length);
                    }
                    size = size - buff.length;
                }
                out.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("分割失败");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return names;
    }

    /**
     * 获取文件的MD5
     *
     * @param filename 文件的名称，全路径的
     */
    private static String getMd5(String filename) {
        String value = null;
        FileInputStream in = null;
        File file = new File(filename);
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 合并文件
     *
     * @param files 多个子文件
     * @param newFile 新的文件
     */
    private static boolean uniteFile(String[] files, File newFile) {
        if (!FileUtil.createOrExistsFile(newFile)) {
            return false;
        }
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(newFile));
            for (String name : files) {
                FileInputStream in = new FileInputStream(new File(name));
                byte[] buff = new byte[1024];
                while ((in.read(buff)) != -1) {
                    out.write(buff, 0, buff.length);
                }
                in.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtil.close(out);
            } catch (Exception e2) {
                // TODO: handle exception
            }
        }
        return false;
    }

    /**
     * 根据给定的每个文件的大小来获取文件的数目
     *
     * @param pathname 文件名称
     */
    private static String[] getPartitionFileNames(String pathname) {
        File file = new File(pathname);
        long size = file.length();
        double num = (size / (double) (long) 524288000);
        //向上取整数,获取分割文件的数量
        num = Math.ceil(num);
        String simpleName = pathname;
        String type = pathname;
        int dfs = pathname.indexOf(".");
        if (dfs > 0) {
            simpleName = pathname.substring(0, dfs);
            type = pathname.substring(dfs + 1);
        }
        String[] names = new String[(int) num];
        for (int i = 0; i < num; i++) {
            names[i] = simpleName + "_" + i + "." + type;
        }
        return names;
    }

    /**
     * 将String类型的数据直接转化为文本数据
     */
    private static void copyStringToFile(String str, String filename) {
        FileWriter out = null;
        try {
            //将Str 转化为文件
            out = new FileWriter(new File(filename));
            char[] chars = str.toCharArray();
            out.write(chars, 0, chars.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    /**
     * 删除多个文件
     */
    private static void deletes(String[] fileNames) {
        //删除文件
        for (String path : fileNames) {
            new File(path).delete();
        }
    }
}
