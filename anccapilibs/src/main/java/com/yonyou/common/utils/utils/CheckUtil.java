package com.yonyou.common.utils.utils;

import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * @功能: 一般校验工具类
 * @Date  2020/8/30;
 * @Author zhangg
 **/
public class CheckUtil {


	/***
	** 功能: 校验ip是否合法
	** 参数:
	** 作者: zhangg
	** 时间:
	**/
	public static boolean isIp(String ipAddress){

		String ipReg = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ipReg);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();

	}
	
	/*
	 * @功能: 校验邮箱
	 * @Date  2020/8/30;
	 * @Author zhangg
	 **/
	public static boolean isEmail(String string) {
		//mailRegex是整体邮箱正则表达式，mailName是@前面的名称部分，mailDomain是后面的域名部分
		String mailRegex, mailName, mailDomain;
		mailName = "^[0-9a-z]+\\w*";       //^表明一行以什么开头；^[0-9a-z]表明要以数字或小写字母开头；\\w*表明匹配任意个大写小写字母或数字或下划线
		mailDomain = "([0-9a-z]+\\.)+[0-9a-z]+$";       //***.***.***格式的域名，其中*为小写字母或数字;第一个括号代表有至少一个***.匹配单元，而[0-9a-z]$表明以小写字母或数字结尾
		mailRegex = mailName + "@" + mailDomain;          //邮箱正则表达式      ^[0-9a-z]+\w*@([0-9a-z]+\.)+[0-9a-z]+$
		Pattern pattern = Pattern.compile(mailRegex);
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 验证手机号码
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(16[^4,\\D])|(17[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			LogerNcc.e("验证手机号码错误", e);
			flag = false;
		}
		return flag;
	}
	
	
	/*
	 * @功能: 判空工具method
	 * @参数  ;
	 * @Date  2020/8/25;
	 * @Author zhangg
	 **/
	public static boolean isNull(String string) {
		if (TextUtils.isEmpty(string)) {
			return true;
		}
		if ("null".equals(string)) {
			return true;
		}
		return false;
	}
	
	
}
