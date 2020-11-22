package com.jerry.baselib.common.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2020/8/2
 * @description 链接表
 */
@Entity
public class Link extends AVBaseObject {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 链接
     */
    private String link;

    @Generated(hash = 1308777077)
    public Link(Long id, String link) {
        this.id = id;
        this.link = link;
    }

    @Generated(hash = 225969300)
    public Link() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }
}
