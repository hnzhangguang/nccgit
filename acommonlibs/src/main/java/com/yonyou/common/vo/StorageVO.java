package com.yonyou.common.vo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;


/*
 * @功能: 普通数据存储实体类
 * @Date  2020/8/17;
 * @Author zhangg
 **/
public class StorageVO extends LitePalSupport implements Serializable {
	
	@Column(unique = true, defaultValue = "unknown")
	public String itemKey;
	public String itemValue;
	public int itemType;  // 0 字符串; 1 int ;2 float ;3 double ;4 logn ;5 boolean ;6 date
	
	public String getItemKey() {
		return itemKey;
	}
	
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	
	public String getItemValue() {
		return itemValue;
	}
	
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
	
	public int getItemType() {
		return itemType;
	}
	
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("StorageVO{");
		sb.append("itemKey='").append(itemKey).append('\'');
		sb.append(", itemValue='").append(itemValue).append('\'');
		sb.append(", itemType=").append(itemType);
		sb.append('}');
		return sb.toString();
	}
}
