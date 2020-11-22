package com.jerry.baselib.common.bean;

import com.jerry.baselib.common.util.PreferenceHelp;

/**
 * @author Jerry
 * @createDate 2019-05-12
 * @description
 */
public class TypeManager {

    public static final String[] HOUSE_TAGS = {
        "WIFI",
        "冰箱",
        "暖气",
        "洗衣机",
        "热水器",
        "燃气灶",
        "电视机",
        "空调",
        "独立卫生间",
        "大阳台",
        "微波炉",
        "衣柜",
        "地铁房",
        "电梯房",
        "可短租",
        "南",
        "北"
    };

    public static final String[] HOUSE_MORE_TAGS = {
        "便利店",
        "健身会所",
        "公园广场",
        "医院",
        "地铁房",
        "餐饮方便",
        "明亮清洁",
        "欢迎家庭入住",
        "海景房",
        "温馨浪漫",
        "适合举办活动",
        "电梯房",
        "洗漱用品",
        "停车场",
        "暖气",
        "淋浴",
        "电视",
        "允许做饭",
        "烘干机",
        "独立卫生间"
    };
    public static final String[] TYPES = {
        "手机",
        "农用物资",
        "生鲜水果",
        "童鞋",
        "园艺植物",
        "五金工具",
        "电子零件",
        "动漫/周边",
        "宠物/用品",
        "网络设备",
        "服饰配件",
        "家装/建材",
        "家纺布艺",
        "珠宝首饰",
        "钟表眼镜",
        "古董收藏",
        "女士鞋靴",
        "箱包",
        "男士鞋靴",
        "办公用品",
        "游戏设备",
        "图书",
        "运动户外",
        "票务娱乐",
        "工艺礼品",
        "玩具乐器",
        "母婴用品",
        "童装",
        "女士服装",
        "家具",
        "居家日用",
        "家用电器",
        "个护美妆",
        "保健护理",
        "摩托车/用品",
        "自行车/用品",
        "汽车/用品",
        "电动车/用品",
        "3C数码",
        "男士服装",
        "其他闲置",
        "音像"
    };

    public static int findIndex(String s) {
        for (int i = 0; i < TYPES.length; i++) {
            if (TYPES[i].equals(s)) {
                return i;
            }
        }
        return PreferenceHelp.getInt(PreferenceHelp.PRODUCT_TYPE, 8);
    }

    public static final String[] RENT = {
        "整租",
        "合租",
        "短租",
        "办公",
        "车位",
        "其他"
    };

    public static int findRent(String s) {
        for (int i = 0; i < RENT.length; i++) {
            if (RENT[i].equals(s)) {
                return i;
            }
        }
        return 1;
    }

    public static final String[] RENOVATION = {
        "毛坯",
        "简单装修",
        "精装修",
        "豪华装修"
    };

    public static int findRenovation(String s) {
        for (int i = 0; i < RENOVATION.length; i++) {
            if (RENOVATION[i].equals(s)) {
                return i;
            }
        }
        return 2;
    }

    public static final String[] ROOM_TYPE = {
        "隔间",
        "次卧",
        "主卧"
    };

    public static int findRoomType(String s) {
        for (int i = 0; i < ROOM_TYPE.length; i++) {
            if (ROOM_TYPE[i].equals(s)) {
                return i;
            }
        }
        return 2;
    }
}
