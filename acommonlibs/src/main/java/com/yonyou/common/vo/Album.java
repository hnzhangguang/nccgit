package com.yonyou.common.vo;

import com.yonyou.common.utils.logs.LogerNcc;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Album extends LitePalSupport {
	
	
	@Column(unique = true, defaultValue = "unknown")
	private String key;
	private String name;
	private double price;
	private List<Song> songs = new ArrayList<Song>();
	
	public boolean save() {
		try {
			saveThrows();
			return true;
		} catch (Exception e) {
			LogerNcc.e(e);
			e.printStackTrace();
			return false;
		}
	}
	
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
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Album{");
		sb.append("name='").append(name).append('\'');
		sb.append(", price=").append(price);
		sb.append(", songs=").append(songs);
		sb.append('}');
		return sb.toString();
	}
}