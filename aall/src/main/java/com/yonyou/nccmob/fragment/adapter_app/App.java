package com.yonyou.nccmob.fragment.adapter_app;

public class App {

    //h5的id
    private int id;
    //简称
    private String shortName;
    //显示的图标
    private String icon;
    private String downloadUrl; // 下载zip地址
    private String rename; // 下载后重命名
    private boolean isCat; // 是否是常用

    public App(String shortName, boolean isCat) {
        this.shortName = shortName;
        this.isCat = isCat;
    }

    public App(int id, String shortName, String icon, boolean isCat) {
        this.id = id;
        this.shortName = shortName;
        this.icon = icon;
        this.isCat = isCat;
    }


    public App(int id, String shortName, boolean isCat, String downloadUrl, String rename) {
        this.id = id;
        this.shortName = shortName;
        this.icon = icon;
        this.isCat = isCat;
        this.downloadUrl = downloadUrl;
        this.rename = rename;
    }


    public String getRename() {
        return rename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isCat() {
        return isCat;
    }

    public void setCat(boolean cat) {
        isCat = cat;
    }

    @Override
    public String toString() {
        return "App{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", icon='" + icon + '\'' +
                ", isCat=" + isCat +
                '}';
    }
}
