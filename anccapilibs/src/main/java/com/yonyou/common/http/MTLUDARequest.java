package com.yonyou.common.http;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by xyy on 16/6/16.
 */
public class MTLUDARequest {

    protected final static Pattern pExpr = Pattern
            .compile("\\${1}\\{{1}[^${}]+\\}{1}");
    private String requestName;

    private HashMap<String, ?> parameter;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public HashMap<String, ?> getParameter() {
        return parameter;
    }

    public void setParameter(HashMap<String, ?> parameter) {
        this.parameter = parameter;
    }

    public static MTLUDARequest obtain(String requestName, HashMap<String, ?> params) {
        MTLUDARequest result = new MTLUDARequest();
        result.setRequestName(requestName);
        result.setParameter(params);
        return result;
    }

}
