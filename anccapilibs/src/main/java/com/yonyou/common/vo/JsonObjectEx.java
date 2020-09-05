package com.yonyou.common.vo;

import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/*
 * @功能:
 * @参数:
 * @Date  2020/8/30 7:25 PM
 * @Author zhangg
 **/
public class JsonObjectEx extends JSONObject {


    public JsonObjectEx() {

    }

    public JsonObjectEx(String string) {
    }

    public static JsonObjectEx getJsonObj(String string) {
        if (TextUtils.isEmpty(string.trim())) {
            return new JsonObjectEx();
        }
        JsonObjectEx jsonObjectEx = new JsonObjectEx(string);
        try {
            JSONObject jsonObject = new JSONObject(string);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.optString(key);
                jsonObjectEx.putEx(key, value);
            }
        } catch (JSONException e) {
            LogerNcc.e(string + "  -> " + e);
            e.printStackTrace();
        }

        return jsonObjectEx;
    }

    public static JsonObjectEx getJsonObj() {
        JsonObjectEx jsonObjectEx = new JsonObjectEx();
        return jsonObjectEx;
    }


    public JsonObjectEx putEx(String key, String value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
        return this;
    }


    public JsonObjectEx putEx(String key, boolean value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
        return this;
    }


    public JsonObjectEx putEx(String key, int value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
        return this;
    }


    public JsonObjectEx putEx(String key, long value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
        return this;
    }

    public JsonObjectEx putEx(String key, Object value) {
        try {
            put(key, value);
        } catch (JSONException e) {
            LogerNcc.e(e);
            e.printStackTrace();
        }
        return this;
    }


    public JSONObject build() {
        return this;
    }


}
