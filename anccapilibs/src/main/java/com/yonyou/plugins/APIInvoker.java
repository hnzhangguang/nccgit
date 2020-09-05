package com.yonyou.plugins;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.yonyou.common.utils.MTLLog;

import java.util.HashMap;

/**
 * Created by xyy  on 2019/4/29
 * <p>
 * mtl 扩展功能
 */
public class APIInvoker {
    private static String TAG = "MTLAPIInvoker";
    Activity context;
    private HashMap<String, MTLPluginEntry> apis = new HashMap<String, MTLPluginEntry>();

    public APIInvoker(Activity ctx) {
        context = ctx;

        // 大于零说明解析过了,不用再重新解析了
        if (apis.size() > 0) {

        } else {
            MTLConfigXmlParser parser = new MTLConfigXmlParser(apis);
            parser.parse(ctx);
        }

    }

    public String call(Activity ctx, String request_method, String params,
                       ApiCallback callback,
                       boolean sync) throws MTLException {
        String libname = "";
        String apiname = "";
        String[] strs = request_method.split("\\.");
        if (strs.length < 2) {
            libname = "default";
            apiname = request_method;
        } else {
            libname = strs[0];
            apiname = strs[1];
        }
//		libname = "database";
//		apiname = "execute";
//		apiname = "query";

        IApiInvoker invoker = getInvoker(libname);
        if (invoker == null) {
            String msg = "当前没有加载--" + libname + "." + apiname + "--插件";
            if (!sync && callback != null) {
                callback.error(msg);
            }
            MTLLog.e(TAG, msg);
            return msg;
        }
        return invoker.call(apiname, new MTLArgs(ctx, params, callback, sync));
    }

    private IApiInvoker getInvoker(String pluginEntryKey) {

        MTLPluginEntry pe = apis.get(pluginEntryKey);
        if (pe == null) {
            return null;
        }

        IApiInvoker ret = pe.plugin;
        if (ret == null) {
            ret = instantiatePlugin(pe.pluginClass);
            if (ret != null) {
                apis.put(pluginEntryKey, new MTLPluginEntry(pluginEntryKey, ret));
            }
        }
        return ret;

    }


    /*
     * @功能: 根据类全路径加载类实例
     * @参数  className 类全路径
     * @Date  2020/8/5;
     * @Author zhangg
     **/
    private IApiInvoker instantiatePlugin(String className) {
        IApiInvoker ret = null;
        try {
            Class<?> c = null;
            if ((className != null) && !("".equals(className))) {
                c = Class.forName(className);
            }
            if (c != null & IApiInvoker.class.isAssignableFrom(c)) {
                ret = (IApiInvoker) c.newInstance();
            }
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                Toast.makeText(context, "ClassNotFoundException:\n" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
            Log.e("mmmm", e.toString());
            e.printStackTrace();
            System.out.println("Error adding plugin " + className + ".");
        }
        return ret;
    }
}
