package com.jerry.dbtest.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import android.app.Application;

public class CryptoUtil {

    private CryptoUtil() {
    }

    public static CryptoUtil instance = new CryptoUtil();

    public static CryptoUtil getInstance() {
        return instance;
    }

    private native byte[] aesEncode(byte[] key, byte[] datas);

    static {
        System.loadLibrary("pnc-crypto");
    }

    final static char[] CHARS_ALL = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};
    final static char CHAR_SPLIT = 29;

    public static String encryptData(Application app, String str) {
        String encryptData = "";
        Random rnd = new Random();
        StringBuilder keyStrBui = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            keyStrBui.append(String.valueOf(CHARS_ALL[rnd.nextInt(62)]));
        }
        String keyStr = keyStrBui.toString();
        try {
            String strMD5 = MD5.md5(keyStr + str);
            byte[] keyArr = keyStr.getBytes("utf-8");
            byte[] dataArr = str.getBytes("utf-8");
            byte[] encryptArr = getInstance().aesEncode(keyArr, dataArr);
            String strAESC = Converts.base64ToString(encryptArr);
            String encryptKeyStr = RSACerPlus.getInstance(app)
                    .doEncrypt(keyStr);
            encryptData = strMD5 + CHAR_SPLIT + strAESC + CHAR_SPLIT
                    + encryptKeyStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    public static String genRandomKey() {
        SecureRandom rnd = null;
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (rnd == null) {
            return null;
        }
        StringBuilder keyStrBui = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            keyStrBui.append(String.valueOf(CHARS_ALL[rnd.nextInt(62)]));
        }
        return keyStrBui.toString();
    }

    public String cryptoString(String data) {
        try {
            byte[] keys = "123456789abcdefg".getBytes();
            byte[] datas = data.getBytes("utf-8");
            byte[] rst = aesEncode(keys, datas);
            return new String(rst, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }

}
