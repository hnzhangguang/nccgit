package com.yonyou.common.vo;

/**
 * 消息实体对象类
 *
 * @author zhangg
 */
public class MessageEventVo {

    private String message;
    private int type;
    private boolean isOpen;

    public MessageEventVo(String msg) {
        message = msg;
    }

    public MessageEventVo(String msg, int type, boolean isOpen) {
        message = msg;
        this.type = type;
        this.isOpen = isOpen;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MessageEventVo{");
        sb.append("message='").append(message).append('\'');
        sb.append(", type=").append(type);
        sb.append(", isOpen=").append(isOpen);
        sb.append('}');
        return sb.toString();
    }
}
