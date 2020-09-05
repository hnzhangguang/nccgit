package com.yonyou.common.vo;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {
	
	@Column(nullable = false)
	private String key;
	
	private String name;
	
	private int duration;
	
	@Column(ignore = true)
	private String uselessField;
	
	private Album album;
	
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public String getUselessField() {
		return uselessField;
	}
	
	public void setUselessField(String uselessField) {
		this.uselessField = uselessField;
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Song{");
		sb.append("name='").append(name).append('\'');
		sb.append(", duration=").append(duration);
		sb.append(", uselessField='").append(uselessField).append('\'');
		sb.append(", album=").append(album);
		sb.append('}');
		return sb.toString();
	}
}