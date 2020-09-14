package com.yonyou.common.utils.user;

import android.content.ContentValues;
import android.text.TextUtils;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.utils.JsonUtil;
import com.yonyou.common.utils.utils.SPUtil;
import com.yonyou.common.vo.AppInfo;
import com.yonyou.common.vo.NCCUserVo;
import com.yonyou.common.vo.StorageVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


/*
 * @功能: 用户工具类
 * @Date  2020/8/17;
 * @Author zhangg
 **/
public class UserUtil {

    /********************* 常用信息相关 end ********************************/

    /*
     * @功能: 根据keys查询values
     * @参数  key;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static JSONObject getValuseByKeys(List<String> keys) {
        JSONObject jsonObject = new JSONObject();
        if (keys.size() > 0) {
            try {
                for (String key : keys) {
                    jsonObject.put(key, getValueByKey(key));
                }
            } catch (Exception e) {
                LogerNcc.e(e);
            }
        }
        return jsonObject;
    }

    /*
     * @功能: 根据key查询value
     * @参数  key;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static String getValueByKey(String key) {
        return getValueByKey(key, "");
    }

    /*
     * @功能: 根据key查询value
     * @参数  key;
     * @参数  defaultValue 默认值;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static String getValueByKey(String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        List<StorageVO> list = LitePal.where("itemKey = ? ", key).find(StorageVO.class);
        String value = "";
        if (list.size() == 1) {
            value = list.get(0).getItemValue().trim();
        }
        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }


    /*
     * @功能: 存储值
     * @参数  json;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setKeyValue(JSONObject dataJson) {
        try {
            Iterator<String> keys = dataJson.keys();
            List<StorageVO> list = null;
            StorageVO vo;
            while (keys.hasNext()) {
                String key = (String) keys.next();
                list = LitePal.where("itemKey = ? ", key).find(StorageVO.class);
                if (list.size() == 1) {
                    vo = list.get(0);
                } else {
                    vo = new StorageVO();
                    vo.setItemKey(key);
                }

                Object param = dataJson.get(key);
                if (param instanceof Integer) {
                    vo.setItemType(1);
                } else if (param instanceof String) {
                    vo.setItemType(0);
                } else if (param instanceof Double) {
                    vo.setItemType(3);
                } else if (param instanceof Float) {
                    vo.setItemType(2);
                } else if (param instanceof Long) {
                    vo.setItemType(4);
                } else if (param instanceof Boolean) {
                    vo.setItemType(5);
                } else if (param instanceof Date) {
                    vo.setItemType(6);
                }
                String value = dataJson.optString(key, "");
                vo.setItemValue(value);
                vo.save();

//				LogerNcc.e(vo);
            }

        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }

    /*
     * @功能:
     * @参数  ;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setKeyValue(String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            setKeyValue(dataJson);
        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }

    /*
     * @功能:
     * @参数  ;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setKeyValue_gone(String key) {
        try {
            setKeyValue(key, "");
        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }

    /*
     * @功能:
     * @参数  ;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setKeyValue(String key, String value) {
        try {
            List<StorageVO> list = LitePal.where("itemKey = ? ", key).find(StorageVO.class);
            StorageVO storageVO = null;
            if (list.size() == 1) {
                storageVO = list.get(0);
            } else {
                storageVO = new StorageVO();
                storageVO.setItemKey(key);
            }
            storageVO.setItemValue(value);
            storageVO.save();
        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }

    /********************* 常用信息相关 end ********************************/


    /********************* user 信息相关 start ********************************/


    /*
     * @功能: 查询当前登录用户信息用户信息
     * @参数  userCode;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static NCCUserVo getCurrentUserVo() {
        String userCode = SPUtil.getStringForLogin(Constant.userCode, "");
        if (TextUtils.isEmpty(userCode)) {
            return null;
        }
        List<NCCUserVo> list = LitePal.where("userCode = ? ", userCode).find(NCCUserVo.class);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /*
     * @功能: 更新用户信息
     * @参数  ;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setLoginInfo(String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            setLoginInfo(dataJson);
        } catch (Exception e) {
            LogerNcc.e(e);
        }
    }


    /*
     * @功能:更新用户信息
     * @参数  ;
     * @Date  2020/8/17;
     * @Author zhangg
     **/
    public static void setLoginInfo(JSONObject dataJson) throws JSONException {

        if (null == dataJson) {
            return;
        }
        String tenantId = dataJson.optString("tenantId", "");
        String accessToken = dataJson.optString("accessToken", "");
        String userid = dataJson.optString("userid", "");
        String userCode = dataJson.optString("userCode", "");  // 后台返回的用户code
        String userName = dataJson.optString("userName", "");
        String yhtUserId = dataJson.optString("yhtUserId", "");
        String userPhone = dataJson.optString("userPhone", "");
        String apps = dataJson.optString("apps", "");
        SPUtil.putStringForLogin(Constant.userCode, userCode);
        UserUtil.setKeyValue(Constant.accessToken, accessToken);

        List<NCCUserVo> userVos = LitePal.where("userCode = ? ", userCode).find(NCCUserVo.class);
        NCCUserVo userVo = null;
        if (userVos.size() == 1) {
            userVo = userVos.get(0);
        } else {
            // 组装用户对象
            userVo = new NCCUserVo();
        }
        userVo.setAccessToken(accessToken);
        userVo.setTenantId(tenantId);
        userVo.setUserCode(userCode);
        userVo.setUserid(userid);
        userVo.setUserName(userName);
        userVo.setYhtUserId(yhtUserId);
        userVo.setPhone(userPhone);
        String loginType = SPUtil.getStringForLogin(Constant.loginType, "1");
        userVo.setUserType(loginType);
        userVo.save();  // 保存到库中


        try {
//            String s = JsonUtil.toJson(userVo);
//            LogerNcc.e(s);
        } catch (Exception q1) {
            LogerNcc.e(q1);
        }

        // 处理apps , 只是记录在本地
        JSONArray appArray = new JSONArray(apps);
        int length = appArray.length();
        for (int i = 0; i < length; i++) {
            Object o = appArray.get(i);
            if (o instanceof String) {
                String tempString = (String) o;
                JSONObject jsonObject = new JSONObject(tempString);
                String value1 = jsonObject.optString("key1", "");

            }

        }

        // 保存用户信息
//			SPUtil.writeObject(SPUtil.configLogin, Constant.loginUserKey, userVo);
        // 读取用户信息
//			UserVo userVo1 = SPUtil.readObject(SPUtil.configLogin, Constant.loginUserKey, UserVo.class);
//			LogerNcc.e(userVo1);

        // 应用列表信息
//			JSONArray array = new JSONArray(apps);


    }


    /********************* user 信息相关 end ********************************/


    /********************* 常用应用相关 start ********************************/


    /*
     * @功能: 根据appId获取对应app信息
     * @Date  2020/8/20;
     * @Author zhangg
     **/
    public static AppInfo getAppInfo(String appId) {
        AppInfo appInfo = null;
        List<AppInfo> list = LitePal.where(" isPermission = ? and appid = ? ", "true", appId).find(AppInfo.class);
        if (list != null && list.size() == 1) {
            appInfo = list.get(0);
        }
        return appInfo;
    }


    /*
     * @功能: 获取我的常用应用信息s
     * @Date  2020/8/20;
     * @Author zhangg
     **/
    public static List<AppInfo> getCommonAppInfos() {
        List<AppInfo> list = LitePal.where(" isPermission = ? and isCommonUse = ? ", "true", "true").find(AppInfo.class);
        return list;
    }

    /*
     * @功能: 获取有权限的应用信息s
     * @Date  2020/8/20;
     * @Author zhangg
     **/
    public static List<AppInfo> getHasPermissionAppInfos() {
        List<AppInfo> list = LitePal.where(" isPermission  = ? ", "true").find(AppInfo.class);
        return list;
    }


    /*
     * @功能: 更新有权限的应用列表
     * @参数  newList 新的应用列表;
     * @Date  2020/8/20;
     * @Author zhangg
     **/
    public static void updatePermissionAppList(List<AppInfo> newList) {
        handleHasPermission(newList);
        // 所有有权限的app信息
        List<AppInfo> hasPermissionAppInfos = getHasPermissionAppInfos();
        for (AppInfo permissionAppInfo : hasPermissionAppInfos) {
            boolean isHasHasPer = false; // 默认现在没有权限了,需要遍历新的有权限的应用列表才能确定是否有权限
            // 新加载的有权限的应用信息
            for (AppInfo appInfo : newList) {
                if (appInfo.getAppid().trim().equals(permissionAppInfo.getAppid().trim())) {
                    isHasHasPer = true;
                    break;
                }
            }
            // 没有权限了要回收
            if (!isHasHasPer) {
                // set update isPermission = "false"
                ContentValues contentValues = new ContentValues();
                contentValues.put("isPermission", "false");
                LitePal.updateAll(AppInfo.class, contentValues, " appid = '" + permissionAppInfo.getAppid().trim() + "' ");
            }
        }
    }

    /*
     * @功能: 处理加载新的应用列表后应用权限问题
     * @参数  [newList]; 当前有权限的应用列表
     * @Date  2020/8/20;
     * @Author zhangg
     **/
    public static void handleHasPermission(List<AppInfo> newList) {

        if (newList == null || newList.size() == 0) {
            return;
        }
        List<AppInfo> list;
        for (AppInfo appInfo : newList) {
            String appid = appInfo.getAppid();
            list = LitePal.where(" appid = ? ", appid).find(AppInfo.class);
            AppInfo appInfo1;
            if (list.size() == 1) {
                appInfo1 = list.get(0);
            } else {
                appInfo1 = appInfo;
            }
            appInfo1.setIsPermission("true");
            appInfo1.save();

        }

    }

    /********************* 常用应用相关 end ********************************/


}
