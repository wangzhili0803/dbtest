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
public class LookUrl {
    @Id(autoincrement = true)
    private Long id;
    private String url;
    private String title;
    private String content;
    private boolean selected;
    private String link;
    /**
     * 图片
     */
    private String picPath;
    @Generated(hash = 38008540)
    public LookUrl(Long id, String url, String title, String content,
            boolean selected, String link, String picPath) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.content = content;
        this.selected = selected;
        this.link = link;
        this.picPath = picPath;
    }
    @Generated(hash = 420230587)
    public LookUrl() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
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
    public boolean getSelected() {
        return this.selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public String getLink() {
        return this.link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getPicPath() {
        return this.picPath;
    }
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
