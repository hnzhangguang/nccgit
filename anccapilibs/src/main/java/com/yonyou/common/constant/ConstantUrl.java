package com.yonyou.common.constant;


/*
 * @功能: url常量类
 * @Date  2020/8/14;
 * @Author zhangg
 **/
public class ConstantUrl {


    // 根据ip port 查询ncc服务账套信息platform.busicenter.query
    public static final String requestAccountListUrl = "/nccloud/mob/platform/busicenter/query"; // 还未调试
    // 点击h5应用,计量计费url
    public static final String openH5appUrl = "/nccloud/mob/platform/mob/openapp";
    // 登录url
    public static final String loginUrl = "/nccloud/mob/platform/standard/login";
    // 忘记密码(用户名+邮箱)
    public static final String forgetUrl_sendCode = "/nccloud/mob/riart/login/forpsw"; // 原来的
    // 验证(邮箱验证码)+新密码
    public static final String checkCode_newpw_resetpw_Url = "/nccloud/mob/platform/pwd/mailreset";
    // 首次重置密码
    public static final String firstResetInitUrl = "/nccloud/mob/platform/pwd/init";

    // 绑定yht账号
    public static final String bandingYhtUrl = "/nccloud/mob/platform/yht/bind";
    // 请求图形验证码
    public static final String requestImgCheckCodeUrl = "/nccloud/mob/platform/requestimgcode/init";
    // 请求手机验证码
    public static final String requestPhoneCheckCodeUrl = "/nccloud/mob/platform/requestphonecode/init";
    // 修改密码(ncc用户名登录时候)
    public static final String requestmodifypwUrl = "/nccloud/mob/platform/pwd/reset";


    // 消息部分
    // 代办消息
    public static final String requestTodoMsgUrl = "/nccloud/riart/message/mobileMessageQuery";
    // 预警消息
    public static final String requestAlterMsgUrl = "/nccloud/riart/message/mobileMessageQuery";
    // 通知消息
    public static final String requestNotificationMsgUrl = "/nccloud/riart/message/mobileMessageQuery";


}
