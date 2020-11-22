package com.jerry.baselib.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.jerry.baselib.common.bean.Product;

/**
 * Created by wzl on 30/11/2016.类说明：集合工具类
 */

public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 获取list的最后一个元素
     * <strong>ATTENTION</strong>: 调用前请确认list非空
     */
    public static <T> T getLastOne(List<T> list) {
        return list.get(list.size() - 1);
    }


    /**
     * 数组随机乱序
     */
    public static <T> void shuffle(List<T> list) {
        int size = list.size();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            // 获取随机位置
            int randomPos = random.nextInt(size);
            // 当前元素与随机元素交换
            Collections.swap(list, i, randomPos);
        }
    }

    /**
     * 位置相对应的元素是不是在集合内
     *
     * @param position 元素位置
     */
    public static boolean isItemInCollection(int position, Collection collection) {
        return !isEmpty(collection) && position > -1 && position < collection.size();
    }

    public static boolean same(List<Product> list1, List<Product> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (Product p1 : list1) {
            long p1Id = p1.getId();
            boolean find = false;
            for (Product p2 : list2) {
                long p2Id = p2.getId();
                if (p1Id == p2Id) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                return false;
            }
        }
        return true;
    }
}
