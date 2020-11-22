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
public class Praiser {

    public static final String SUPER_LIKE = "superlike";
    public static final String COLLECTION = "collection";
    public static final String IWANT = "iwant";

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private int superLike;
    private int collect;
    private int want;
    private boolean isSel;
    private long updateTime;

    @Generated(hash = 1965727020)
    public Praiser(Long id, String name, int superLike, int collect, int want,
        boolean isSel, long updateTime) {
        this.id = id;
        this.name = name;
        this.superLike = superLike;
        this.collect = collect;
        this.want = want;
        this.isSel = isSel;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1702422878)
    public Praiser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getSuperLike() {
        return superLike;
    }

    public void setSuperLike(final int superLike) {
        this.superLike = superLike;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(final int collect) {
        this.collect = collect;
    }

    public int getWant() {
        return want;
    }

    public void setWant(final int want) {
        this.want = want;
    }

    public boolean isSel() {
        return isSel;
    }

    public void setSel(final boolean sel) {
        isSel = sel;
    }

    @Override
    public String toString() {
        return "Praiser{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", superLike=" + superLike +
            ", collect=" + collect +
            ", want=" + want +
            '}';
    }

    public boolean getIsSel() {
        return this.isSel;
    }

    public void setIsSel(boolean isSel) {
        this.isSel = isSel;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final long updateTime) {
        this.updateTime = updateTime;
    }
}
