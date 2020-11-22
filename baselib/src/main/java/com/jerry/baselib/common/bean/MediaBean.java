package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-08-21
 * @description
 */
public class MediaBean implements Comparable<MediaBean> {

    /**
     * 0：图片，1：视频
     */
    private int type;
    private String path;
    private String modified;
    private boolean hide;
    private boolean selected;

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(final String modified) {
        this.modified = modified;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(final boolean hide) {
        this.hide = hide;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(final MediaBean o) {
        return o.getModified().compareTo(modified);
    }
}
