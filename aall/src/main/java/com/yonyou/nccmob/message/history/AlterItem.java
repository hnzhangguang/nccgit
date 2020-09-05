package com.yonyou.nccmob.message.history;


/*
 * @功能: 代办实体对象
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class AlterItem {
	
	private String name;
	private String name22;
	private String name33;
	private int imageId;
	
	public AlterItem(String name, String name22, String name33, int imageId) {
		this.name = name;
		this.name22 = name22;
		this.name33 = name33;
		this.imageId = imageId;
		
	}
	
	public String getName() {
		return name;
	}
	
	public int getImageId() {
		return imageId;
	}
	
	public String getName22() {
		return name22;
	}
	
	public void setName22(String name22) {
		this.name22 = name22;
	}
	
	public String getName33() {
		return name33;
	}
	
	public void setName33(String name33) {
		this.name33 = name33;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("AlterItem{");
		sb.append("name='").append(name).append('\'');
		sb.append("name22='").append(name22).append('\'');
		sb.append("name33='").append(name33).append('\'');
		sb.append(", imageId=").append(imageId);
		sb.append('}');
		return sb.toString();
	}
	
	
}
