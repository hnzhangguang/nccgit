package com.yonyou.nccmob.message.history;


/*
 * @功能: 代办实体对象
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class NotificationItem {
	
	private String name;
	private int imageId;
	
	public NotificationItem(String name, int imageId) {
		this.name = name;
		this.imageId = imageId;
		
	}
	
	public String getName() {
		return name;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("NotificationItem{");
		sb.append("name='").append(name).append('\'');
		sb.append(", imageId=").append(imageId);
		sb.append('}');
		return sb.toString();
	}
	
	
}
