package com.yonyou.common.vo;

import java.util.List;

/*
 * @功能: 消息列表实体对象
 * @Date  2020/8/13;
 * @Author zhangg
 **/
public class MessagesList {
	private List<MessageVO> todo;
	private List<MessageVO> prealert;
	private List<MessageVO> notice;
	
	public List<MessageVO> getTodo() {
		return todo;
	}
	
	public void setTodo(List<MessageVO> todo) {
		this.todo = todo;
	}
	
	public List<MessageVO> getPrealert() {
		return prealert;
	}
	
	public void setPrealert(List<MessageVO> prealert) {
		this.prealert = prealert;
	}
	
	public List<MessageVO> getNotice() {
		return notice;
	}
	
	public void setNotice(List<MessageVO> notice) {
		this.notice = notice;
	}
	
	
}