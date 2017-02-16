package com.jerry.dbtest.utils;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/***
 * 与IPHONE android匹配的DES加密算法
 *
 * @author 王胜
 * @since 2011-02-28
 */
public class DesSecurity {
    //  默认 初始化向量
    private String initVec = "NJCB-P&C";
    //  默认 初始化密钥
    private String initKey = "NJCB-P&C";

    private BASE64Encoder encode;
    private BASE64Encoder encoder;
    private Cipher enCipher;
    private Cipher deCipher;

    public static void main(String[] args) {
        try {
            String vec = "NJCB-P&C";
            String key = "NJCB-P&C";
            String test = "X03lQAds7zg=";
            // 默认密钥
            DesSecurity des = new DesSecurity();
            // System.out.println("加密前的字符：" + test);
            // System.out.println("加密后的字符：" +
            // des.encrypt(test.getBytes("UTF-8")));
            // System.out.println("解密后的字符：" +des.decrypt("X03lQAds7zg=",
            // "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化加密对象
     *
     * @throws Exception
     */
    public DesSecurity() throws Exception {
        if (initKey == null) {
            throw new NullPointerException("Parameter is null!");
        }
        initCipher(initKey.getBytes(), initVec.getBytes());
    }

    /**
     * 初始化加密对象
     *
     * @param key 加密密钥 必须为8位
     * @param iv  初始化向量 必须为8位
     * @throws Exception
     */
    public DesSecurity(String key, String iv) throws Exception {
        if (key == null) {
            throw new NullPointerException("Parameter is null!");
        }
        initCipher(key.getBytes(), iv.getBytes());
    }

    private void initCipher(byte[] secKey, byte[] secIv) throws Exception {
        // 获得DES密钥
        DESKeySpec dks = new DESKeySpec(secKey);
        // 获得DES加密密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 生成加密密钥
        SecretKey key = keyFactory.generateSecret(dks);
        // 创建初始化向量对象
        IvParameterSpec iv = new IvParameterSpec(secIv);
        AlgorithmParameterSpec paramSpec = iv;
        // 为加密算法指定填充方式，创建加密会话对象
        enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 为加密算法指定填充方式，创建解密会话对象
        deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 初始化加解密会话对象
        enCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        deCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        encode = new BASE64Encoder();
        encoder = new BASE64Encoder();
    }

    /**
     * 加密数据
     *
     * @param data 待加密二进制数据
     * @return 经BASE64编码过的加密数据
     * @throws Exception
     */
    public String encrypt(byte[] data) throws Exception {
        return encoder.encode(enCipher.doFinal(data));
    }

    /**
     * 加密字符串
     *
     * @param strIn 需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String strIn) throws Exception {
        return encrypt(strIn.getBytes());
    }

    public String base64(byte[] data) throws Exception {
        return encode.encode(data);
    }
}
