package com.jerry.baselib.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

public class MathUtil {

    private MathUtil() {
    }

    /**
     * 生成随机数大于等于min 小于max
     */
    public static int random(int min, int max) {
        if (min > max) {
            max = max + min;
            min = max - min;
            max = max - min;
        }
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static double random(double min, double max) {
        return halfEven(min + ((max - min) * new Random().nextDouble()));
    }

    /**
     * int 数组的最大值
     */
    public static int maxOfArray(int[] arr) {
        if (arr == null) {
            return 0;
        }

        if (arr.length == 1) {
            return arr[0];
        }

        int size = arr.length;
        int max = arr[0];
        for (int i = 1; i < size; i++) {
            max = Math.max(max, arr[i]);
        }
        return max;
    }

    /**
     * double 数组的最大值
     */
    public static double maxOfArray(double[] arr) {
        if (arr == null) {
            return 0;
        }

        if (arr.length == 1) {
            return arr[0];
        }

        int size = arr.length;
        double max = arr[0];
        for (int i = 1; i < size; i++) {
            max = Math.max(max, arr[i]);
        }
        return max;
    }

    /**
     * double 数组的最小值
     */
    public static double minOfArray(final double[] arr) {
        return minOfArray(arr, false);
    }

    /**
     * double 数组的最小值
     *
     * @param outZero 是否求非零最小值
     */
    public static double minOfArray(double[] arr, boolean outZero) {
        if (arr == null) {
            return 0;
        }
        double min = Double.MAX_VALUE;
        if (outZero) {
            for (double anArr : arr) {
                if (anArr <= 0) {
                    continue;
                }
                min = Math.min(min, anArr);
            }
        } else {
            for (double anArr : arr) {
                min = Math.min(min, anArr);
            }
        }
        return min == Double.MAX_VALUE ? 0 : min;
    }

    /**
     * 折线图寻找适合值
     */
    public static double findDouble(double min, double max) {
        if (min > max) {
            max = min + max;
            min = max - min;
            max = max - min;
        }
        // 有零直接返回0
        if (min == 0 || max == 0) {
            return 0;
        }
        // 正负直接返回0
        if (min < 0 && max > 0) {
            return 0;
        }
        if (max < 0) {
            return -findDouble(-max, -min);
        }
        int index = (int) (Math.min(Math.log10(max), Math.log10(min)));
        int chashulaingji = (int) (Math.log10(max - min) - 1);
        if (chashulaingji > 0) {
            min = intNum(min, chashulaingji);
            max = intNum(max, chashulaingji);
        } else {
            min = halfEven(min, -chashulaingji);
            max = halfEven(max, -chashulaingji);
        }

        double testNum;
        while (true) {
            if (index > 0) {
                testNum = intNum(min, index);
            } else {
                testNum = halfEven(min, -index);
            }
            while (testNum <= max) {
                testNum = MathUtil.halfEven(testNum + Math.pow(10, index), -index);
                if (testNum >= min && testNum <= max) {
                    int check;
                    if (index > 0) {
                        check = (int) (testNum / Math.pow(10, index));
                    } else {
                        check = (int) (testNum * Math.pow(10, -index));
                    }
                    if (check % 4 == 0) {
                        return testNum;
                    }
                }
            }
            index--;
        }
    }

    public static double safeGet(final Map<String, Double> premiumRateMap, final String symbol) {
        Double price = premiumRateMap.get(symbol);
        if (price != null) {
            return price;
        }
        return 0;
    }

    /**
     * 取整（十，百，千）
     */
    public static double intNum(double num, int scale) {
        return (int) (num / Math.pow(10, scale)) * Math.pow(10, scale);
    }

    /**
     * 默认保留两位小数
     */
    public static double halfEven(double num) {
        return halfEven(num, 2);
    }

    /**
     * BigDecimal初始化小数必须用String来构造，HALF_EVEN：银行家舍入法-四舍六入五成双
     */
    public static double halfEven(double num, int digit) {
        return new BigDecimal(String.valueOf(num)).setScale(digit, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static String halfEvenToString(double num) {
        return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
