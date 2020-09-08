package com.yonyou.common.vo;

import android.text.GetChars;

import java.util.List;


/*
 * @功能: 消息实体对象
 * @Date  2020/8/13;
 * @Author zhangg
 **/
public class MessageVO {

//    msgtype 有如下三中
//    approve  审批
//    working 工作
//    business 业务


    //    {
//        "pk_message":"消息的主键",
//            "pk_detail":"消息对应的流程实例主键，审批的时候用",
//            "subject":"titlexxxx",
//            "content":"contentxxx",
//            "msgtype":"msgtypexxxx",
//            "buttonInfo":{
//        按钮信息，json格式。数据具体含义我还没搞清楚
//    },
//        "url":"urlxxxx",
//            "billStutas":"billStutasxxx",
//            "sendtime":"2020-10-18",
//            "attachment":[
//        {
//            "pk_attachment":"idxxxx",
//                "pk_file":"文件主键，下载的时候用",
//                "downurl":"downloadurlxxx",
//                "filename":"name...",
//                "type":"doc"
//        }
//                    ]
//    }
    private String pk_message;//消息的主键
    private String pk_detail; // 消息对应的流程实例主键，审批的时候用
    private String subject;
    private String content;
    private String msgtype;
    private String url;
    private String billStutas;
    private String sendtime;
    private String senderpersonname;
    private List<AttachmentVO> attachment;
    private int id;
    private List<String> enableActions; //按钮s


    public MessageVO() {
    }

    public MessageVO(String subject, String content, String msgtype, String sendtime, int id) {
        this.subject = subject;
        this.content = content;
        this.billStutas = billStutas;
        this.sendtime = sendtime;
        this.id = id;
    }

    public String getSenderpersonname() {
        return senderpersonname;
    }

    public void setSenderpersonname(String senderpersonname) {
        this.senderpersonname = senderpersonname;
    }

    public List<String> getEnableActions() {
        return enableActions;
    }

    public void setEnableActions(List<String> enableActions) {
        this.enableActions = enableActions;
    }

    public String getPk_message() {
        return pk_message;
    }

    public void setPk_message(String pk_message) {
        this.pk_message = pk_message;
    }

    public String getPk_detail() {
        return pk_detail;
    }

    public void setPk_detail(String pk_detail) {
        this.pk_detail = pk_detail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBillStutas() {
        return billStutas;
    }

    public void setBillStutas(String billStutas) {
        this.billStutas = billStutas;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public List<AttachmentVO> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<AttachmentVO> attachment) {
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MessageVO{");
        sb.append("pk_message='").append(pk_message).append('\'');
        sb.append(", pk_detail='").append(pk_detail).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", msgtype='").append(msgtype).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", billStutas='").append(billStutas).append('\'');
        sb.append(", sendtime='").append(sendtime).append('\'');
        sb.append(", senderpersonname='").append(senderpersonname).append('\'');
        sb.append(", attachment=").append(attachment);
        sb.append(", id=").append(id);
        sb.append(", enableActions=").append(enableActions);
        sb.append('}');
        return sb.toString();
    }
}