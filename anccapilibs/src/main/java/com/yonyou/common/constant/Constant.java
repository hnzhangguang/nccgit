package com.yonyou.common.constant;

/*
 * @功能: ncc移动常量类
 * @Date  2020/8/5;
 * @Author zhangg
 **/
public class Constant {

    public static String BackPressedCallbackId = "BackPressedCallbackId";  // onBackPressed key
    public static String H5SETKEYPRE = "h5_";  // h5调用原生的dbsqlite存储能力的是,key加前缀用于区分壳自己的存储
    public static String ISLOAD_KEY = "isLoad_key"; // 本应用是否加载过,默认没有加载过
    public static String isLogin = "isLogin";
    public static String loginType = "loginType"; // 登录类型 1 用户名密码; 2 手机号密码
    public static String net_ip = "net_ip";
    public static String net_port = "net_port";
    public static int ROW_APP_NUM = 4;  // 每行显示app 数
    public static String MSG = "message";  // message
    public static String ERROR = "error";  // error
    public static String CODE = "code";  // code
    public static String userCode = "userCode";  // userCode
    public static String password = "password";  // userCode
    public static String accountInfoKey = "accountInfo";  // 显示使用的账套信息key
    public static String accountListKey = "accountListKey";  // 账套信息列表 key
    public static String accessToken = "accessToken";  // accessToken
    public static String loginUserKey = "loginUserKey";  // accessToken
    public static String resumecallbackName = "resumecallbackname";  //
    public static String isAutoLogin = "isAutoLogin";  // 是否是自动登录标识

    // 消息常量key
    public static String msgType = "msgType";  // 代办
    public static String todo_msg = "todo";  // 代办
    public static String alter_msg = "prealert";  // 预警
    public static String notification_msg = "notice";  // 通知
    public static String all_msg = "all";  // 所有

    public static String msgtype_approve = "approve";  // 审批
    public static String msgtype_working = "working";  // 工作
    public static String msgtype_business = "business";  // 业务


    // h5注册原生
    public static String titleNameKey = "titleNameKey";  // h5传来的连续扫码界面title key
    public static String scanCallbackKey = "scanCallbackKey";
    public static String leftbtncallbackNameKey = "leftbtncallbackNameKey";
    public static String rightbtncallbackNameKey = "rightbtncallbackNameKey";


}
