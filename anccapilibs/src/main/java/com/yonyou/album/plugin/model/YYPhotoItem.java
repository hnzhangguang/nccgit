//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yonyou.album.plugin.model;

import java.io.Serializable;

public class YYPhotoItem implements Serializable {
    private static final long serialVersionUID = 8682674788506891598L;
    private int photoId;
    private String photoPath;
    private boolean select;

    public YYPhotoItem(int id, String path) {
        this.photoId = id;
        this.photoPath = path;
        this.select = false;
    }

    public int getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isSelect() {
        return this.select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String toString() {
        return "PhotoItem [photoID=" + this.photoId + ", select=" + this.select + "]";
    }
}
