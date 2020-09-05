package com.yonyou.common.http;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by xyy on 16/6/16.
 */
public interface MTLUDADemoLoader {

    JSONObject getData(String requestName, HashMap<String, ?> params);
}
