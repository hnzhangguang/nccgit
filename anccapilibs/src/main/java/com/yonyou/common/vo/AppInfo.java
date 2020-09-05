package com.yonyou.common.vo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * h5应用信息vo
 *
 * @author zhangg
 */
public class AppInfo extends LitePalSupport implements Serializable {
	@Column(unique = true, defaultValue = "unknown")
	private String appid;
	@Column(defaultValue = "")
	private String appname;
	@Column(defaultValue = "")
	private String iconurl;
	@Column(defaultValue = "")
	private String type; // app类型
	private int version;
	@Column(defaultValue = "")
	private String zipurl;  // 离线下载地址
	@Column(defaultValue = "")
	private String url;  // 在线加载地址
	@Column(defaultValue = "")
	private String isPermission; // 是否有权限  默认没有权限 "false" or ""
	@Column(defaultValue = "")
	private String isExistDownLoad; // 是否已经下载
	@Column(defaultValue = "")
	private String isCommonUse; // 是否是常用应用  默认是 "false" or ""
	
	public String getIsExistDownLoad() {
		return isExistDownLoad;
	}
	
	public void setIsExistDownLoad(String isExistDownLoad) {
		this.isExistDownLoad = isExistDownLoad;
	}
	
	public String getIsCommonUse() {
		return isCommonUse;
	}
	
	public void setIsCommonUse(String isCommonUse) {
		this.isCommonUse = isCommonUse;
	}
	
	public String getIsPermission() {
		return isPermission;
	}
	
	public void setIsPermission(String isPermission) {
		this.isPermission = isPermission;
	}
	
	public String getAppid() {
		return appid;
	}
	
	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	public String getAppname() {
		return appname;
	}
	
	public void setAppname(String appname) {
		this.appname = appname;
	}
	
	public String getIconurl() {
		return iconurl;
	}
	
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getZipurl() {
		return zipurl;
	}
	
	public void setZipurl(String zipurl) {
		this.zipurl = zipurl;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("AppInfo{");
		sb.append("appid='").append(appid).append('\'');
		sb.append(", appname='").append(appname).append('\'');
		sb.append(", iconurl='").append(iconurl).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", version=").append(version);
		sb.append(", zipurl='").append(zipurl).append('\'');
		sb.append(", url='").append(url).append('\'');
		sb.append(", isPermission='").append(isPermission).append('\'');
		sb.append(", isExistDownLoad='").append(isExistDownLoad).append('\'');
		sb.append(", isCommonUse='").append(isCommonUse).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
