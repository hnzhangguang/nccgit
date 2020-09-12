package com.yonyou.common.vo;


import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

import javax.xml.validation.Validator;

/*
 * @功能: 消息附件实体
 * @Date  2020/8/13;
 * @Author zhangg
 **/
public class AttachmentVO extends LitePalSupport implements Serializable {


    private String pk_attachment;
    private String pk_file; // 文件主键,下载的时候用
    private String downurl;
    private String filename;
    private String type;
    private String pk_parent;

    public String getPk_parent() {
        return pk_parent;
    }

    public void setPk_parent(String pk_parent) {
        this.pk_parent = pk_parent;
    }

    public String getPk_attachment() {
        return pk_attachment;
    }

    public void setPk_attachment(String pk_attachment) {
        this.pk_attachment = pk_attachment;
    }

    public String getPk_file() {
        return pk_file;
    }

    public void setPk_file(String pk_file) {
        this.pk_file = pk_file;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AttachmentVO{");
        sb.append("pk_attachment='").append(pk_attachment).append('\'');
        sb.append(", pk_file='").append(pk_file).append('\'');
        sb.append(", downurl='").append(downurl).append('\'');
        sb.append(", filename='").append(filename).append('\'');
        sb.append(", pk_parent='").append(pk_parent).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}