package com.yonyou.nccmob.fragment.adapter_app;

import com.yonyou.common.vo.AppInfo;

import java.util.List;

public class AppGroup {

    private int id;
    private String name;
    private List<AppInfo> apps;

    public AppGroup(String name, List<AppInfo> apps) {
        this.name = name;
        this.apps = apps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppInfo> getApps() {
        return apps;
    }

    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }
}
