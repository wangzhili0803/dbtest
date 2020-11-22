package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2019-04-28
 * @description
 */
@Entity
public class Product {

    public static final String AUTO_REPLAY = "您好！我是小助手，主人有事不在，您拍下我会帮主人吧立即把宝贝的链接给您，请您放心哦";
    @Id(autoincrement = true)
    private Long id;
    private String title;
    private String content;
    /**
     * 租房
     */
    private boolean isHouse;
    private int type = 8;
    /**
     * 并夕夕拼团价
     */
    private String pprice;
    /**
     * 是否为免费送
     */
    private boolean isFree;
    /**
     * 实体商品
     */
    private boolean isEntity;
    /**
     * 全新宝贝
     */
    private boolean isNew;
    /**
     * 发布次数
     */
    private int count = 1;
    /**
     * 随机地址
     */
    private boolean adRandom;
    /**
     * 鱼塘随机
     */
    private boolean fishRandom;
    /**
     * 鱼塘
     */
    private String fishPond;
    /**
     * 价格
     */
    private String price;
    /**
     * 标签
     */
    private String tag;
    /**
     * 回复
     */
    private boolean replyWordSwitch = true;
    private String replay;
    private boolean replyPicSwitch;
    private String replyPic;
    private String link;
    private String remoteImgs;
    /**
     * 图片
     */
    private String picPath;
    /**
     * 省
     */
    private String provice;
    /**
     * 市
     */
    private String city;
    /**
     * 县
     */
    private String district;
    /**
     * 运费
     */
    private String trans;
    private long updateTime;
    private boolean isSel;
    /**
     * 出租类型。0：整组，1：合租，2：短租，3：办公，4：车位，5：其他
     */
    private int hireType;
    /**
     * 卧室类型。0：隔间，1：次卧，2：主卧
     */
    private int roomType;
    /**
     * 小区
     */
    private String village;
    /**
     * 室
     */
    private int houseRoom;
    /**
     * 厅
     */
    private int houseHall;
    /**
     * 卫
     */
    private int houseToilet;
    /**
     * 面积
     */
    private double acreage;
    /**
     * 装修程度 0：毛坯，1：简单装修，2：精装，3：豪华
     */
    private int houseRenovation;
    /**
     * 标签
     */
    private String houseLabels;
    /**
     * 更多标签
     */
    private String moreHouseLabels;

    @Generated(hash = 414760380)
    public Product(Long id, String title, String content, boolean isHouse, int type,
        String pprice, boolean isFree, boolean isEntity, boolean isNew, int count,
        boolean adRandom, boolean fishRandom, String fishPond, String price, String tag,
        boolean replyWordSwitch, String replay, boolean replyPicSwitch, String replyPic,
        String link, String remoteImgs, String picPath, String provice, String city,
        String district, String trans, long updateTime, boolean isSel, int hireType,
        int roomType, String village, int houseRoom, int houseHall, int houseToilet,
        double acreage, int houseRenovation, String houseLabels, String moreHouseLabels) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isHouse = isHouse;
        this.type = type;
        this.pprice = pprice;
        this.isFree = isFree;
        this.isEntity = isEntity;
        this.isNew = isNew;
        this.count = count;
        this.adRandom = adRandom;
        this.fishRandom = fishRandom;
        this.fishPond = fishPond;
        this.price = price;
        this.tag = tag;
        this.replyWordSwitch = replyWordSwitch;
        this.replay = replay;
        this.replyPicSwitch = replyPicSwitch;
        this.replyPic = replyPic;
        this.link = link;
        this.remoteImgs = remoteImgs;
        this.picPath = picPath;
        this.provice = provice;
        this.city = city;
        this.district = district;
        this.trans = trans;
        this.updateTime = updateTime;
        this.isSel = isSel;
        this.hireType = hireType;
        this.roomType = roomType;
        this.village = village;
        this.houseRoom = houseRoom;
        this.houseHall = houseHall;
        this.houseToilet = houseToilet;
        this.acreage = acreage;
        this.houseRenovation = houseRenovation;
        this.houseLabels = houseLabels;
        this.moreHouseLabels = moreHouseLabels;
    }

    @Generated(hash = 1890278724)
    public Product() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsHouse() {
        return this.isHouse;
    }

    public void setIsHouse(boolean isHouse) {
        this.isHouse = isHouse;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPprice() {
        return this.pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public boolean getIsFree() {
        return this.isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public boolean getIsEntity() {
        return this.isEntity;
    }

    public void setIsEntity(boolean isEntity) {
        this.isEntity = isEntity;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getAdRandom() {
        return this.adRandom;
    }

    public void setAdRandom(boolean adRandom) {
        this.adRandom = adRandom;
    }

    public boolean getFishRandom() {
        return this.fishRandom;
    }

    public void setFishRandom(boolean fishRandom) {
        this.fishRandom = fishRandom;
    }

    public String getFishPond() {
        return this.fishPond;
    }

    public void setFishPond(String fishPond) {
        this.fishPond = fishPond;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReplay() {
        return this.replay;
    }

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRemoteImgs() {
        return this.remoteImgs;
    }

    public void setRemoteImgs(String remoteImgs) {
        this.remoteImgs = remoteImgs;
    }

    public String getPicPath() {
        return this.picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getProvice() {
        return this.provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTrans() {
        return this.trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getIsSel() {
        return this.isSel;
    }

    public void setIsSel(boolean isSel) {
        this.isSel = isSel;
    }

    public int getHireType() {
        return this.hireType;
    }

    public void setHireType(int hireType) {
        this.hireType = hireType;
    }

    public String getVillage() {
        return this.village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public int getHouseRoom() {
        return this.houseRoom;
    }

    public void setHouseRoom(int houseRoom) {
        this.houseRoom = houseRoom;
    }

    public int getHouseHall() {
        return this.houseHall;
    }

    public void setHouseHall(int houseHall) {
        this.houseHall = houseHall;
    }

    public int getHouseToilet() {
        return this.houseToilet;
    }

    public void setHouseToilet(int houseToilet) {
        this.houseToilet = houseToilet;
    }

    public double getAcreage() {
        return this.acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public int getHouseRenovation() {
        return this.houseRenovation;
    }

    public void setHouseRenovation(int houseRenovation) {
        this.houseRenovation = houseRenovation;
    }

    public String getHouseLabels() {
        return this.houseLabels;
    }

    public void setHouseLabels(String houseLabels) {
        this.houseLabels = houseLabels;
    }

    public int getRoomType() {
        return this.roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public String getMoreHouseLabels() {
        return this.moreHouseLabels;
    }

    public void setMoreHouseLabels(String moreHouseLabels) {
        this.moreHouseLabels = moreHouseLabels;
    }

    public String getReplyPic() {
        return this.replyPic;
    }

    public void setReplyPic(String replyPic) {
        this.replyPic = replyPic;
    }

    public boolean getReplyWordSwitch() {
        return this.replyWordSwitch;
    }

    public void setReplyWordSwitch(boolean replyWordSwitch) {
        this.replyWordSwitch = replyWordSwitch;
    }

    public boolean getReplyPicSwitch() {
        return this.replyPicSwitch;
    }

    public void setReplyPicSwitch(boolean replyPicSwitch) {
        this.replyPicSwitch = replyPicSwitch;
    }
}
