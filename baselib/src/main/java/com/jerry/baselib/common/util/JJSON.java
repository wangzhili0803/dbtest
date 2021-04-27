package com.jerry.baselib.common.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import androidx.annotation.NonNull;

/**
 * @author Jerry
 * @createDate 2020-03-08
 * @description
 */
public class JJSON {

    public static JSONObject parseObject(final String json) {
        try {
            return JSON.parseObject(json);
        } catch (
            Exception e) {
            return null;
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return JSON.parseObject(text, clazz, new Feature[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parseObject(String input, Type clazz, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, new Feature[0]);
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    public static List<String> parseArray(final String content) {
        try {
            List<String> list = JSON.parseArray(content, String.class);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @NonNull
    public static <T> List<T> parseArray(final String content, Class<T> clazz) {
        try {
            List<T> list = JSON.parseArray(content, clazz);
            return list == null ? new ArrayList<>() : list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
