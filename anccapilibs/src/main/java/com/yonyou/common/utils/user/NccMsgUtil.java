package com.yonyou.common.utils.user;

import android.text.TextUtils;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.utils.StringUtil;
import com.yonyou.common.vo.AppInfo;
import com.yonyou.common.vo.AttachmentVO;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.common.vo.MessageVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;


/*
 * @功能: 处理消息工具类
 * @Date  2020/9/2 3:18 PM
 * @Author zhangg
 **/
public class NccMsgUtil {


    public static String getData() throws JSONException {
        String string = "{\"data\":{\"messages\":{\"todo\":[{\"pk_message\":\"1001Z01000000002D9LN\",\"pk_detail\":\"1001Z01000000002D9LM\",\"subject\":\"zhuhlm提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202005280077\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e32123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e23123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-28 10:14:54\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002DA7D\",\"pk_detail\":\"1001Z01000000002DA5T\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202005280080\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e362123123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e332123\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-28 10:18:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002DZRV\",\"pk_detail\":\"1001Z01000000002DZQB\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202006030081\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e745\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-06-03 13:50:33\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002O04X\",\"pk_detail\":\"1001Z01000000002O04N\",\"subject\":\"kejl提交单据ZDYDJ202007300019，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-30 15:18:43\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 14:55:56\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002NVCP\",\"pk_detail\":\"1001A01000000002NVCM\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202008050120\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e5645456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e56454565645456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 10:22:53\",\"attachment\":[]},{\"pk_message\":\"1001Z010000000030DFC\",\"pk_detail\":\"1001Z010000000030DFA\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202008250128\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5654656\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e54456564\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-25 17:02:52\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002OMSH\",\"pk_detail\":\"1001Z01000000002OMSE\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202008100122\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e45456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-10 13:41:24\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002NCI4\",\"pk_detail\":\"1001A01000000002NCI2\",\"subject\":\"kejl提交单据ZDYDJ202008030025，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-08-03 16:15:57\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-03 16:16:39\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002O05K\",\"pk_detail\":\"1001Z01000000002O05J\",\"subject\":\"kejl 加签,单据类型：自定义工单,单据号: ZDYDJ202007220011,请审批单据\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-05 15:25:48\",\"attachment\":[]}],\"prealert\":[],\"notice\":[{\"pk_message\":\"1001A01000000002L9YR\",\"pk_detail\":\"1001A01000000002L9Y8\",\"subject\":\"kejl提交单据ZDYDJ202007220011，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-22 19:35:19\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-22 19:35:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6N\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-20 17:26:12\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVIJ\",\"pk_detail\":\"1001Z01000000002ZVIC\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:48:26\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7S9\",\"pk_detail\":\"1001A01000000002L7RJ\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:32:01\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000KK\",\"pk_detail\":\"1001A01000000002L8CI\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:55:00\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002MGTK\",\"pk_detail\":\"1001A01000000002MGT0\",\"subject\":\"kejl提交单据ZDYDJ202007290012，请审批！\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e单据日期：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e2020-07-29 19:44:06\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e制单人：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003ekejl\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e交易类型：\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e自定义工单\\u003c/span\\u003e\\n\\u003c/div\\u003e\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e金额：￥\\u003c/span\\u003e\\n\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e123.00\\u003c/span\\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-29 19:44:32\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7TU\",\"pk_detail\":\"1001A01000000002L7TL\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:33:54\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7YW\",\"pk_detail\":\"1001A01000000002L7YN\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 13:36:27\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000H3\",\"pk_detail\":\"1002A0100000000000GS\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:10:36\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000001T5\",\"pk_detail\":\"1001A01000000002L9AE\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-22 16:54:23\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6E\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:31:21\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L7VG\",\"pk_detail\":\"1001A01000000002L7V7\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:35:40\",\"attachment\":[]},{\"pk_message\":\"1001ZZ10000000029T9A\",\"pk_detail\":\"1001ZZ10000000029T86\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"sms\",\"sendtime\":\"2020-04-22 19:43:00\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ66\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:30:41\",\"attachment\":[]},{\"pk_message\":\"1001A01000000002L5TT\",\"pk_detail\":\"1001A01000000002L5TK\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003ekeyy\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202007210086\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5444564654\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e0897878\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 09:38:17\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000HX\",\"pk_detail\":\"1001A01000000002L88W\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:12:45\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVM7\",\"pk_detail\":\"1001Z01000000002ZVLP\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202005210071\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfdsf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfsdf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-24 10:22:25\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002CQ6J\",\"subject\":\"fengjks：（新做excuter）您对业务流进行了操作，特此通知。BizCenterCode：null, UserDataSource：ncc2004\",\"msgtype\":\"nc\",\"sendtime\":\"2020-05-13 15:31:40\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZOIM\",\"pk_detail\":\"1001Z01000000002ZOIG\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:42:38\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVHZ\",\"pk_detail\":\"1001Z01000000002ZVHW\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:47:53\",\"attachment\":[]},{\"pk_message\":\"1001Z010000000029MDV\",\"pk_detail\":\"1001Z010000000029MC4\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"sms\",\"sendtime\":\"2020-04-21 20:07:09\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002L6ZI\",\"pk_detail\":\"1001A01000000002L642\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003ekeyy\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003eKH202007210091\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e5645456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e56456456\\u003c/span \\u003e\\n\\u003c/div\\u003e\\n\\n\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\n\\u003cspan  class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span \\u003e\\n\\u003cspan  class\\u003d\\\"keytext\\\"\\u003e111\\u003c/span \\u003e\\n\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 10:29:28\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZVLC\",\"pk_detail\":\"1001Z01000000002ZVL9\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e555\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202005210071\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e本组织\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfdsf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003esdfsdf\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-24 10:01:10\",\"attachment\":[]},{\"pk_message\":\"1002A0100000000000JD\",\"pk_detail\":\"1001Z01000000002L8JL\",\"subject\":\"客户申请单变更短信通知\",\"content\":\"\\u003cp\\u003e客户申请单变更短信通知。\\u003c/p\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-07-21 15:50:13\",\"attachment\":[]},{\"pk_message\":\"1001Z01000000002ZOI3\",\"pk_detail\":\"1001Z01000000002ZOI0\",\"subject\":\"kejl提交的客户申请单待审批\",\"content\":\"\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e001业务单元\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e申请单号:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003eKH202006030082\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e目的组织:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e集团\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户名称:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e6545665\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户编码:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e45456\\u003c/span\\u003e\\u003c/div\\u003e\\u003cdiv class\\u003d\\\"divtext\\\"\\u003e\\u003cspan class\\u003d\\\"labeltext\\\"\\u003e客户基本分类:\\u003c/span\\u003e\\u003cspan class\\u003d\\\"normaltext\\\"\\u003e111\\u003c/span\\u003e\\u003c/div\\u003e\",\"msgtype\":\"nc\",\"sendtime\":\"2020-08-22 17:42:16\",\"attachment\":[]}]}},\"success\":true}";
//        List<MessageVO> todo_msg = MsgUtil.getMessageVosByType(string, Constant.todo_msg);
//        List<MessageVO> alter_msg = MsgUtil.getMessageVosByType(string, Constant.alter_msg);
//        List<MessageVO> notification_msg = MsgUtil.getMessageVosByType(string, Constant.notification_msg);
//        LogerNcc.e(todo_msg.size());
//        LogerNcc.e(alter_msg.size());
//        LogerNcc.e(notification_msg.size());
//        string = "{\n" +
//                "    \"data\":{\n" +
//                "        \"messages\":{\n" +
//                "            \"todo\":[\n" +
//                "                {\n" +
//                "                    \"pk_message\":\"消息的主键\",\n" +
//                "                    \"pk_detail\":\"消息对应的流程实例主键，审批的时候用\",\n" +
//                "                    \"subject\":\"titlexxxx\",\n" +
//                "                    \"content\":\"contentxxx\",\n" +
//                "                    \"msgtype\":\"msgtypexxxx\",\n" +
//                "                    \"buttonInfo\":{\n" +
//                "                        \"enableActions\":[\n" +
//                "                            \"transfer\",\n" +
//                "                            \"pass\",\n" +
//                "                            \"reject\",\n" +
//                "                            \"addapprover\"\n" +
//                "                        ],\n" +
//                "                        \"SubmitRejectBillMode\":0\n" +
//                "                    },\n" +
//                "                    \"url\":\"urlxxxx\",\n" +
//                "                    \"billStutas\":\"billStutasxxx\",\n" +
//                "                    \"sendtime\":\"2020-10-18\",\n" +
//                "                    \"attachment\":[\n" +
//                "                        {\n" +
//                "                            \"pk_attachment\":\"idxxxx\",\n" +
//                "                            \"pk_file\":\"文件主键，下载的时候用\",\n" +
//                "                            \"downurl\":\"downloadurlxxx\",\n" +
//                "                            \"filename\":\"name...\",\n" +
//                "                            \"type\":\"doc\"\n" +
//                "                        }\n" +
//                "                    ]\n" +
//                "                }\n" +
//                "            ]\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
        return string;
    }

    /*
     * @功能: 根据后台返回的数据获取对应的列表
     * @参数:
     * @Date  2020/9/2 3:32 PM
     * @Author zhangg
     **/
    public static List<MessageVO> getMessageVosByType(String msgString, String type) throws JSONException {
        if (TextUtils.isEmpty(msgString)) {
            return null;
        }
        List<MessageVO> list = new ArrayList<>();
        if (type.equals(Constant.alter_msg) || type.equals(Constant.todo_msg) || type.equals(Constant.notification_msg)) {
            JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(msgString); //
            String data = jsonObj.optString("data", "");
            JsonObjectEx dataJson = JsonObjectEx.getJsonObj(data); // dataJson
            // 获取messages
            String messages = dataJson.optString("messages", "");
//            LogerNcc.e(messages);
            JsonObjectEx messagesJson = JsonObjectEx.getJsonObj(messages); // messagesJson
            // 获取单个类型的消息
            String todo = messagesJson.optString(type, "");   // 代办
            JSONArray todoArray = new JSONArray(todo);
            int length = todoArray.length();
            for (int i = 0; i < length; i++) {
                Object o = todoArray.get(i);
                MessageVO messageVO = NccMsgUtil.getSingleMessageVo(o.toString());
                list.add(messageVO);
            }
        }
        return list;


    }

    /*
     * @功能:
     * @参数:
     * @Date  2020/9/2 3:18 PM
     * @Author zhangg
     **/
    public static MessageVO getSingleMessageVo(String messageString) throws JSONException {
        if (TextUtils.isEmpty(messageString)) {
            return null;
        }
        JsonObjectEx item = JsonObjectEx.getJsonObj(messageString); // messagesJson
        String pk_message = item.optString("pk_message", "");
        String pk_detail = item.optString("pk_detail", "");
        String subject = item.optString("subject", "");
        String content = item.optString("content", "");
        String msgtype = item.optString("msgtype", "");
        String sendtime = item.optString("sendtime", "");
        String senderpersonname = item.optString("senderpersonname", "");
        String attachment = item.optString("attachment", "");
        String buttonInfo = item.optString("buttonInfo", ""); // 消息按钮区域所有按钮
        MessageVO messageVO = new MessageVO();
        messageVO.setSenderpersonname(senderpersonname);
        messageVO.setContent(StringUtil.getShowContent(content));
        messageVO.setMsgtype(msgtype);
        messageVO.setPk_detail(pk_detail);
        messageVO.setPk_message(pk_message);
        messageVO.setSubject(subject);
        messageVO.setSendtime(sendtime);

        // 处理附件
        JSONArray attachmentJsonArray = new JSONArray(attachment);
        int length = attachmentJsonArray.length();
        List<AttachmentVO> attachmentVOList = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            JsonObjectEx attachmentJson = JsonObjectEx.getJsonObj(attachmentJsonArray.get(j).toString());
            String pk_attachment = attachmentJson.optString("pk_attachment", "");
            String pk_file = attachmentJson.optString("pk_file", "");
            String downurl = attachmentJson.optString("downurl", "");
            String filename = attachmentJson.optString("filename", "");
            String type = attachmentJson.optString("type", "");
            AttachmentVO attachmentVO = new AttachmentVO();
            attachmentVO.setDownurl(downurl);
            attachmentVO.setFilename(filename);
            attachmentVO.setPk_attachment(pk_attachment);
            attachmentVO.setPk_file(pk_file);
            attachmentVO.setType(type);
            attachmentVOList.add(attachmentVO);
        }

        // 附件
        messageVO.setAttachment(attachmentVOList);

        // 处理按钮
        JsonObjectEx buttonInfoJson = JsonObjectEx.getJsonObj(buttonInfo);
        String SubmitRejectBillMode = buttonInfoJson.optString("SubmitRejectBillMode", "");
        String enableActions = buttonInfoJson.optString("enableActions", "");
        if (!TextUtils.isEmpty(enableActions)) {
            String s = enableActions.replaceAll("\\[", "").replaceAll("\\]", "");
            List<String> acitons = java.util.Arrays.asList(s.split("\\,"));
            messageVO.setEnableActions(acitons);
        }

        return messageVO;
    }


    /***************************** start ***************************************/

    /*
     * @功能: 更新附件
     * @参数:
     * @Date  2020/9/12 1:18 PM
     * @Author zhangg
     **/
    public AttachmentVO updateAttachmentVoFormDb(JsonObjectEx attachmentJson, String pk_parent) {
        if (null == attachmentJson) {
            return null;
        }
        String pk_attachment = attachmentJson.optString("pk_attachment", "");
        String pk_file = attachmentJson.optString("pk_file", "");
        String downurl = attachmentJson.optString("downurl", "");
        String filename = attachmentJson.optString("filename", "");
        String type = attachmentJson.optString("type", "");
        AttachmentVO attachmentVO = null;
        List<AttachmentVO> list = LitePal.where(" pk_attachment = ? ", pk_attachment).find(AttachmentVO.class);
        if (list != null && list.size() == 1) {
            attachmentVO = list.get(0);
        }
        if (null == attachmentVO) {
            attachmentVO = new AttachmentVO();
            attachmentVO.setPk_attachment(pk_attachment);
        }
        attachmentVO.setPk_parent(pk_parent);
        attachmentVO.setDownurl(downurl);
        attachmentVO.setFilename(filename);
        attachmentVO.setPk_file(pk_file);
        attachmentVO.setType(type);
        attachmentVO.save();

        return attachmentVO;
    }

    /***************************** end ***************************************/

}
