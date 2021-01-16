package cn.leancloud.chatkit.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static String md5(String string) {
        byte[] hash;
        try {
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
}
