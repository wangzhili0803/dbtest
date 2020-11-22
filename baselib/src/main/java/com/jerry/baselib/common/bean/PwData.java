package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-07-09
 * @description
 */
public class PwData {

    /**
     * ret : 调用成功
     * url : https://a.m.taobao.com/i590226349604.htm?price=29.9&sourceType=item&sourceType=item&suid=c0ea029d-b80a-4c52-8d2a-b4e2f6cd0582&ut_sk=1.XSPtSsdCMLADAF6YuMeBILL1_21646297_1562635611925.TaoPassword-WeiXin.fenxiangyoushang&un=981086420e71e42d2d7d0aa19bc20c00&share_crt_v=1&tbkShareUId=2200827880025&systype=m&ptl=from%3Afenxiang%3Bsight%3Ataokouling&pid=mm_26632708_477750042_108714650282&from=fenxiangyouli2019&fromScene=126&tbkShareId=382686061&sp_tk=77+lS05od1k2NVNLWVHvv6U=
     * content : 丝飘抽纸整箱批发 30包原木纸巾 家庭装家用批发餐厅3层加厚
     * picUrl : http://img.alicdn.com/imgextra/i1/3937219703/O1CN01MDibZO2LY1YRxHpSm_!!0-item_pic.jpg
     * taopwdOwnerId : 2200827848553
     * validDate : 29天23小时59分25秒
     * pj : taokouling.com
     * code : 1
     * msg : 调用成功
     */

    private String ret;
    private String url;
    private String content;
    private String picUrl;
    private String taopwdOwnerId;
    private String validDate;
    private String pj;
    private int code;
    private String msg;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTaopwdOwnerId() {
        return taopwdOwnerId;
    }

    public void setTaopwdOwnerId(String taopwdOwnerId) {
        this.taopwdOwnerId = taopwdOwnerId;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getPj() {
        return pj;
    }

    public void setPj(String pj) {
        this.pj = pj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
