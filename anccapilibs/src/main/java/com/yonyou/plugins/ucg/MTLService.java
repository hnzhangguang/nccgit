package com.yonyou.plugins.ucg;

import android.app.Activity;
import android.text.TextUtils;

import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.net.MTLHttpCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;
import com.yonyou.plugins.MTLException;
import com.yonyou.plugins.security.UMEncrypt;
import com.yonyou.plugins.security.UMProtocolManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.yonyou.plugins.ucg.UCGApiInvoker.SUCCESS;

/**
 * Created by yanglin  on 2019/4/26
 */
public class MTLService {
	
	public static final String TP = "tp";
	public static final String DATA = "data";
	public static final String APPCONTEXT = "appcontext";
	public static final String SERVICECONTEXT = "servicecontext";
	public static final String DEVICEINFO = "deviceinfo";
	public static final String SERVICEID = "serviceid";
	public static final String COMMON_SERVICE_ID = "umCommonService";
	private static final String CALL_ACTION_PARAM = "params";
	private static final String LOGIN_URl = "oauth/nccLogin";
	private static final String UCGCODE = "ucgcode";
	
	public static String callAction(MTLUCGArgs args) throws MTLException {
		return xService(args);
	}
	
	public static String callService(MTLUCGArgs args) throws MTLException {
		return xService(args);
	}
	
	public static String login(final MTLUCGArgs args, MTLHttpCallBack callBack) {

//        String upesncode = args.getUpesncode();
		String upesncode = args.getString("upesncode");
//        String appcode = args.getAppcode();
		String appcode = args.getString("appcode");
//        String langcode = args.getLangCode();
		String langcode = args.getString("langcode");
		args.setDefurl(LOGIN_URl + "?upesncode=" + upesncode + "&appcode=" + appcode + "&langcode=" + langcode);
		MTLOKHttpUtils.get(getUCGOriginUrl(args), args.getHeaders(), callBack);
		return "";
		
		
	}
	
	/**
	 * 设置公共参数
	 * {"origin":"http://172.23.79.2:8080","upesncode":"42f0bff32b4c400a86b5eaf149099956","appcode":"TEST001","langCode":"simpchn"}
	 *
	 * @param args
	 */
	private static String getUCGOriginUrl(MTLUCGArgs args) {
		String url;
		String origin = args.getOrigin();
		if (!TextUtils.isEmpty(origin) && origin.startsWith("http")) {
			url = origin + "/" + args.getUrl();
		} else {
			url = getUCGUrl(args);
		}
		return url;
	}
	
	/**
	 * 写入配置文件
	 *
	 * @param args
	 */
	public static String writeUCGConfig(MTLUCGArgs args) {
		
		JSONObject res = MTLServiceConfig.setConfig(args);
		if (res == null) {
			args.error("请检查配置文件");
			return "error";
		}
		args.success(res);
		return res.toString();
	}
	
	/**
	 * 读取配置文件
	 *
	 * @param args
	 */
	public static String readUCGConfig(MTLUCGArgs args) {
		try {
			JSONObject res = MTLServiceConfig.getConfig(args);
			args.success(res);
		} catch (JSONException e) {
			e.printStackTrace();
			args.error("未找到 appCode： " + args.getAppcode() + "对应的配置");
		}
		return "success";
	}
	
	private static String xService(final MTLUCGArgs args) throws MTLException {
		if (args.checkUrl()) {
			return "error";
		}
		
		return UCGcallAction(args);
	}
	
	private static String UCGcallAction(final MTLUCGArgs args) {
		
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		String requestBody = args.getParams();
		MTLOKHttpUtils.post(getUCGOriginUrl(args) + "?appcode=" + args.getAppcode(), RequestBody.create(mediaType, requestBody), args.getHeaders(), new MTLHttpCallBack() {
			@Override
			public void onFailure(String error) {
				args.getCallback().error(error);
			}
			
			@Override
			public void onResponse(int status, String body) {
				try {
					JSONObject data = new JSONObject(body);
					
					String code = data.optString("code");
					if (SUCCESS.equals(code)) {
						data.put(UCGCODE, code);
						if (data.optString("data") != null) {
							args.getCallback().success(data.optString("data"));
						} else {
							args.getCallback().success(data);
						}
					} else {
						args.getCallback().error(code, data.toString());
					}
				} catch (JSONException e) {
					args.getCallback().error(e.getMessage());
				} catch (Exception e) {
					args.getCallback().error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		return "success";
	}
	
	private static void builderFormParam(JSONObject json, FormBody.Builder builder) {
		if (json == null) {
			return;
		}
		JSONObject param = json.optJSONObject("params");
		if (param == null) {
			return;
		}
		Iterator iterator = param.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = param.optString(key);
			builder.add(key, value);
		}
	}
	
	private static String getUCGUrl(MTLUCGArgs args) {
		String host = MTLServiceConfig.getHost(args);
		if (TextUtils.isEmpty(host)) {
//            host = "ma-gateway.test.app.yyuap.com";
			host = "ucg-core.test.app.yyuap.com";
		}
		String port = MTLServiceConfig.getPort(args);
		if (TextUtils.isEmpty(port)) {
			port = "80";
		}
		boolean isHttps = MTLServiceConfig.isHttp(args);
		String url = isHttps ? "https://" : "http://";
		url += host;
		if (!TextUtils.isEmpty(args.getUrl()) && args.getUrl().startsWith("/")) {
			url += port.equals("80") ? "" : ":" + port + "";
		} else {
			url += port.equals("80") ? "/" : ":" + port + "/";
		}
		url += args.getUrl();
		
		MTLLog.v("mtlservice", "url: " + url);
		return url;
	}
	
	
	/**
	 * 构造请求数据
	 *
	 * @param act 获取设备信息时候会用到
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject buildParams(Activity act, JSONObject jsonArgs, String serviceId) throws MTLException {
		try {
			JSONObject object = new JSONObject();
			object.put(APPCONTEXT, buildAppContextParam());
			object.put(SERVICECONTEXT, buildServiceContext(jsonArgs));
			object.put(DEVICEINFO, buildDeviceInfo(act));
			object.put(SERVICEID, serviceId);
			object.put(TP, jsonArgs.optString(TP));
			return object;
		} catch (JSONException e) {
			throw new MTLException(e.getMessage());
		}
	}
	
	/**
	 * 构造设备信息参数
	 *
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject buildDeviceInfo(Activity act) throws JSONException {
		JSONObject devinfo = new JSONObject();
		devinfo.put("devid", "");
		devinfo.put("wfaddress", "");
		devinfo.put("mac", "");
		devinfo.put("name", "");
		devinfo.put("uuid", "");
		devinfo.put("style", "android");
		devinfo.put("os", "android");
		devinfo.put("lang", "zh");
		devinfo.put("versionname", "1.0.1");
		devinfo.put("appversion", "1.0.1");
		devinfo.put("isroot", "false");
		devinfo.put("ssid", "yonyou");
		devinfo.put("ncdevid", "");
		devinfo.put("osversion", android.os.Build.VERSION.RELEASE);
		return devinfo;
	}
	
	/**
	 * 服务相关参数
	 *
	 * @param jsonArgs
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject buildServiceContext(JSONObject jsonArgs) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("actionid", COMMON_SERVICE_ID);
		json.put("callback", "nothing");
		json.put("viewid", jsonArgs.optString("viewid"));
		json.put("controllerid", jsonArgs.optString("viewid"));
		json.put("actionname", jsonArgs.optString("action"));
		JSONObject params = jsonArgs.optJSONObject("params");
		if (params == null) {
			params = new JSONObject();
		}
		Iterator<String> it = jsonArgs.keys();
		while (it.hasNext()) {
			String key = it.next();
			String value = jsonArgs.getString(key);
			params.put(key, value);
		}
		json.put(CALL_ACTION_PARAM, params);
		return json;
	}
	
	/**
	 * APP context 相关参数
	 *
	 * @return
	 */
	private static JSONObject buildAppContextParam() {
		JSONObject rs = new JSONObject();
		try {
			rs.put("sessionid", "");
			rs.put("user", "");
			rs.put("pass", "");
			rs.put("userid", "");
			rs.put("groupid", "");
			rs.put("devid", "");
			rs.put("appid", "");
			rs.put("token", "");
			rs.put("massotoken", "");
			rs.put("funcid", "");
			rs.put("tabid", "");
		} catch (JSONException e) {
		
		}
		return rs;
	}
	
	/**
	 * 参数翻译解析
	 *
	 * @param args
	 * @return
	 */
	private static Map<String, String> translateParam(Map<String, String> args) {
		if (args.containsKey("tp") && args.containsKey("data")) {
			String tp = args.get("tp");
			String data = args.get("data");
			if (TextUtils.isEmpty(tp) || TextUtils.isEmpty(data)) {
				args.put("tp", "none");
				return args;
			}
			UMEncrypt encrypt = UMProtocolManager.getEncryption(tp);
			if (encrypt == null) {
				throw new Error("不支持的传输协议 - " + tp);
			}
			try {
				args.put("data", encrypt.encode(data));
			} catch (Exception e) {
				args.put("tp", "none");
			}
		}
		return args;
	}
	
	
	/**
	 * 翻译返回的参数
	 *
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getJSONData(JSONObject rs) throws Exception {
		String tp = rs.optString("tp");
		if (TextUtils.isEmpty(tp) || tp.equalsIgnoreCase("none")) {
			return rs.optJSONObject("data");
		} else {
			UMEncrypt encrypt = UMProtocolManager.getEncryption(tp);
			if (encrypt != null) {
				try {
					return new JSONObject(encrypt.decode(rs
						  .optString("data")));
				} catch (Error e) {
					return null;
				}
			}
		}
		return rs;
	}
	
	public static String login(final MTLUCGArgs args) {
		return login(args, new MTLHttpCallBack() {
			@Override
			public void onFailure(String error) {
				args.getCallback().error(error);
			}
			
			@Override
			public void onResponse(int status, String body) {
				try {
					JSONObject data = new JSONObject(body);
					
					String code = data.optString("code");
					if (SUCCESS.equals(code)) {
						args.getCallback().success(data.optJSONObject("data"));
					} else {
						args.getCallback().error(code, data.optString("msg"));
					}
				} catch (JSONException e) {
					args.getCallback().error(e.getMessage());
				} catch (Exception e) {
					args.getCallback().error(e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
}
