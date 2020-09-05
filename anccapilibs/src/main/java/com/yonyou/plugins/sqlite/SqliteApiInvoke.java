package com.yonyou.plugins.sqlite;

import android.text.TextUtils;
import android.util.Log;

import com.yonyou.common.constant.Constant;
import com.yonyou.common.utils.LitePal;
import com.yonyou.common.utils.user.UserUtil;
import com.yonyou.common.utils.utils.CheckUtil;
import com.yonyou.common.vo.JsonObjectEx;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONArray;
import org.json.JSONObject;


/*
 * @功能: 移动数据库操作
 * @Date  2020/8/5;
 * @Author zhangg
 **/
public class SqliteApiInvoke implements IApiInvoker {

    private static final String QUERY = "query";
    private static final String EXECUTE = "execute"; // 执行sql不返还数据
    private static final String dbstorage = "dbstorage"; // 使用数据库能力的存储

    @Override
    public String call(String apiname, MTLArgs args) throws MTLException {
        String sql = args.getString("sql", "");
//		sql = "CREATE TABLE Persons(Id_P int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
//		sql = "INSERT INTO Persons (LastName, Address) VALUES ('Wilson', 'Champs-Elysees')";
//		sql = "SELECT LastName,FirstName FROM Persons";
        if (TextUtils.isEmpty(sql)) {
            return null;
        }
        switch (apiname) {
            // 只是执行sql语句
            case EXECUTE:
                try {
                    LitePal.executeSQL(sql);

                    JSONObject json = new JSONObject();
                    json.put("code", 200);
                    args.success(json);

                } catch (Exception e) {
                    try {
                        JSONObject json = new JSONObject();
                        String message = e.getMessage();
                        if (message.contains("table") && message.contains("already exists")) {
                            String already_exists = message.substring(0, message.indexOf("already exists") + 14);
                            json.put("errorInfo", already_exists);
                        } else {
                            json.put("errorInfo", e.toString());
                        }

                        args.error("error", json.toString());
                    } catch (Exception ee) {

                    }
                    e.printStackTrace();
                }

                break;
            // 查询数据并返回
            case QUERY:

                try {
                    JSONArray selectDataBySQL = LitePal.getSelectDataBySQL(sql);
                    args.success(selectDataBySQL);

                } catch (Exception e) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("msg", e.toString());
                        args.error("sqliteError", jsonObject.toString()); // 查询库报错
                    } catch (Exception ee) {
                        Log.e("mmmm", ee.toString());
                        e.printStackTrace();
                    }

                }


                break;


            case dbstorage:

                String params = args.getParams();
                JsonObjectEx jsonObj = JsonObjectEx.getJsonObj(params);
                String type = jsonObj.optString("type", "");  // 是存储key-value 还是get value
                // 设置值
                if (type.equals("setKeyValue")) {
                    String key = jsonObj.optString("key", "");
                    String value = jsonObj.optString("value", "");
                    if (!CheckUtil.isNull(key)) {
                        UserUtil.setKeyValue(Constant.H5SETKEYPRE + key, value);
                    }
                } else if (type.equals("getValueByKey")) {  // 获取值
                    String key = jsonObj.optString("key", "");
                    if (!CheckUtil.isNull(key)) {
                        String valueByKey = UserUtil.getValueByKey(Constant.H5SETKEYPRE + key);
                        // 组装返回的json数据
                        JsonObjectEx jsonObj1 = JsonObjectEx.getJsonObj();
                        jsonObj1.putEx(key, valueByKey);
                        args.success(jsonObj1);
                    }
                }

                break;
        }
        return null;
    }


}
