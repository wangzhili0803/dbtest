package com.jerry.baselib.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.jerry.baselib.Key;

/**
 * 字符串相关工具类
 *
 * @author my
 * @time 2017/2/24 15:31
 */
public class StringUtil {

    public static void main(String[] strings) {
        System.out.println(numericInStr("sdg45ad2d2a33s"));
    }

    /**
     * 分割字段
     */
    public static String[] safeSplit(String str) {
        return safeSplit(str, " ");
    }

    /**
     * 分割字段
     */
    public static String[] safeSplit(String str, String regularExpression) {
        if (TextUtils.isEmpty(str)) {
            return new String[]{Key.NIL};
        }
        return str.split(regularExpression);
    }

    public static String randomStr(int length) {
        //1.  定义一个字符串（A-Z，a-z，0-9）即62个数字字母；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //2.  由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //3.  长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //从62个的数字或字母中选择
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }

    public static int getRandomInt(int min, int max) {
        if (min > max) {
            max = max + min;
            min = max - min;
            max = max - min;
        }
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static List<String> getPicsFromStr(String str) {
        List<String> pics = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String[] sdfs = safeSplit(str);
        for (String sdf : sdfs) {
            sb.append(sdf);
            if (sdf.contains(".jpg") || sdf.contains(".png") || sdf.contains(".jpeg") || sdf.contains(".mp4")) {
                pics.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                sb.append(" ");
            }
        }
        return pics;
    }

    public static int numericInStr(String str) {
        byte[] array1 = str.getBytes();//将字符串转换为字符数组
        int count = 0;
        for (byte b : array1) {
            if (b >= 48 && b <= 57) {//数字的ASCII码为48--57
                count++;
            }
        }
        return count;
    }

    public static String getValidateCode4Sms(final String content) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
