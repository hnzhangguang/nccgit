package com.yonyou.common.http;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.LruCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;

/**
 * 统一数据访问接口
 * 通过getInstance方法，获得数据访问对象
 * <p/>
 * Created by xyy on 16/6/16.
 */
public abstract class MTLUniversalDataAccessor {
    public static final Charset CS_UTF_8 = Charset.forName("UTF-8");
    public static final String KEY_TYPE = "type";
    public static final String KEY_HTTP = "HTTP";
    private static boolean mIsDemo = false;

    private WeakReference<Context> mCtx = null;
    /**
     * 配置文件名称
     */
    protected String requestName = "";
    /**
     * 请求的模块名字
     */
    protected String modular = "";
    /**
     * 完整的demo名字
     */
    protected String reqComplete;

    protected static String isHttp="http";

    private static LruCache<String, JSONObject> configCache = new LruCache<String, JSONObject>(1024 * 1024 * 2);

    /**
     * @param ctx
     * @param requestName 配置文件名
     * @param modular     请求模块名
     * @return
     */
    public static MTLUniversalDataAccessor getInstance(Activity ctx, String requestName, String modular) {
        if (isDemo()) {
            return new MTLUniversalDemoDataAccessor(ctx, requestName, modular);
        }
        JSONObject config = null;
        try {
            config = loadConfigure(ctx, requestName).optJSONObject(modular);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String type = config.optString(KEY_TYPE);
        if (type.equalsIgnoreCase(KEY_HTTP)) {
            return new MTLUniversalHttpDataAccessor(ctx, requestName, config);
        }

        throw new RuntimeException("unsupport type - " + type);
    }

    public static MTLUniversalDataAccessor getInstance(Context ctx, String url){
        return new MTLUniversalHttpDataAccessor(ctx,url);
    }
    private static JSONObject loadConfigure(Activity ctx, String requestName) throws IOException, JSONException {
        String reqConfigureFile = String.format("configure/%s.json", requestName);
        JSONObject result = configCache.get(reqConfigureFile);
        if (result == null) {

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(reqConfigureFile), CS_UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            result = new JSONObject(builder.toString());
            configCache.put(reqConfigureFile, result);
        }
        if (result == null) result = new JSONObject();
        return result;
    }


    public static void setSocketFactory(Context context, String cerfile) throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        AssetManager am = context.getAssets();
        InputStream ins = am.open(cerfile);//"RSA2048CARoot.cer"
        KeyStore keyStore = null;
        try {
            //读取证书
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");  //问1
            Certificate cer = cerFactory.generateCertificate(ins);
            //创建一个证书库，并将证书导入证书库
            keyStore = KeyStore.getInstance("PKCS12", "BC");   //问2
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            ins.close();
        }
    }

    protected MTLUniversalDataAccessor(Context ctx, String requestName, String modular) {
        mCtx = new WeakReference<Context>(ctx);
        this.requestName = requestName;
        this.modular = modular;
        this.reqComplete = requestName + "." + modular;
        if (ctx == null) {
            throw new IllegalArgumentException("ctx is null");
        }
    }

    public Context getContext() {
        return mCtx.get();
    }

    public abstract void setUrl(String url);

    public static void setDemo(boolean value) {
        mIsDemo = value;
    }

    public static boolean isDemo() {
        return mIsDemo;
    }


    public abstract void get(HashMap<String, ?> params, MTLUDACallback callback);

    public abstract void headerParamsGet(HashMap<String, ?> params, boolean isJsonType, MTLUDACallback callback);

    public abstract void paramsPost(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams, boolean isJsonType, MTLUDACallback callback);

    public abstract void paramsGet(HashMap<String, ?> headerParams, HashMap<String, ?> bodyParams, boolean isJsonType, MTLUDACallback callback);

    public abstract void paramsJsonPost(HashMap<String, ?> headerParams, String bodyJson, MTLUDACallback callback);

    public abstract void paramsJsonStrPost(HashMap<String, ?> headerParams, String bodyJson,  boolean isJsonType, MTLUDACallback callback);

    public abstract void post(HashMap<String, ?> params, MTLUDACallback callback);

    public abstract void upload(HashMap<String, ?> params, MTLUDACallback callback);

    public abstract void upload(HashMap<String, ?> paramsHeader, HashMap<String, ?> params, MTLUDACallback callback);

    public abstract void formUpload(HashMap<String, ?> paramsHeader, HashMap<String, ?> params, MTLUDACallback callback);

    public abstract void download(HashMap<String, ?> params, MTLUDACallback callback);



}
