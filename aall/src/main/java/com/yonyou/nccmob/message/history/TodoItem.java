package com.yonyou.nccmob.message.history;


/*
 * @功能: 代办实体对象
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class TodoItem {
	
	private String name;
	private String name2;
	private String name3;
	private String time;
	private int imageId;
	
	public TodoItem(String name, String name2, String name3, String time, int imageId) {
		this.name = name;
		this.name2 = name2;
		this.name3 = name3;
		this.time = time;
		this.imageId = imageId;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName2() {
		return name2;
	}
	
	public void setName2(String name2) {
		this.name2 = name2;
	}
	
	public String getName3() {
		return name3;
	}
	
	public void setName3(String name3) {
		this.name3 = name3;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("TodoItem{");
		sb.append("name='").append(name).append('\'');
		sb.append(", name2='").append(name2).append('\'');
		sb.append(", name3='").append(name3).append('\'');
		sb.append(", time='").append(time).append('\'');
		sb.append(", imageId=").append(imageId);
		sb.append('}');
		return sb.toString();
	}
}
