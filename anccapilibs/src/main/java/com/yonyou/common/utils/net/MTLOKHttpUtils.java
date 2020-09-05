package com.yonyou.common.utils.net;

import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by yanglin  on 2019/4/28
 */
public class MTLOKHttpUtils {
	
	final static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
	private static final String TAG = MTLOKHttpUtils.class.getName();
	
	public static void post(String url, RequestBody requestBody, Map<String, String> headers, final MTLHttpCallBack callBack) {
		post(url, requestBody, headers, 10 * 1000, callBack);
	}
	
	public static void post(String url, RequestBody requestBody, Map<String, String> headers, int timeout, final MTLHttpCallBack callBack) {
		
		Request.Builder builder = new Request.Builder()
			  .url(url)
			  .post(requestBody);
		if (headers != null) {
			for (String key : headers.keySet()) {
				builder.header(key, headers.get(key));
			}
		}
		
		Request request = builder.build();
		OkHttpClient httpClient = new OkHttpClient().newBuilder()
			  .cookieJar(new CookieJar() {
				  @Override
				  public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
					  cookieStore.put(url.host(), cookies);
				  }
				  
				  @Override
				  public List<Cookie> loadForRequest(HttpUrl httpUrl) {
					  List<Cookie> cookies = cookieStore.get(httpUrl.host());
					  return cookies != null ? cookies : new ArrayList<Cookie>();
				  }
			  })
			  .connectTimeout(timeout, TimeUnit.MILLISECONDS)
			  .readTimeout(timeout, TimeUnit.MILLISECONDS)
			  .writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callBack.onFailure(e.getMessage());
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String body = response.body().string();
				int code = response.code();
				callBack.onResponse(code, body);
			}
		});
		
	}
	
	public static void post(String url, FormBody.Builder bodyBuilder, Map<String, String> headers, final MTLHttpCallBack callBack) {
		post(url, bodyBuilder.build(), headers, callBack);
		
	}
	
	public static void get(String url, Map<String, String> headers, int timeout, final MTLHttpCallBack callBack) {
		Request.Builder builder = new Request.Builder()
			  .url(url)
			  .get();
		if (headers != null) {
			for (String key : headers.keySet()) {
				builder.header(key, headers.get(key));
			}
		}
		
		Request request = builder.build();
		OkHttpClient httpClient = new OkHttpClient().newBuilder()
			  .cookieJar(new CookieJar() {
				  @Override
				  public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
					  cookieStore.put(url.host(), cookies);
				  }
				  
				  @Override
				  public List<Cookie> loadForRequest(HttpUrl httpUrl) {
					  List<Cookie> cookies = cookieStore.get(httpUrl.host());
					  return cookies != null ? cookies : new ArrayList<Cookie>();
				  }
			  })
			  .connectTimeout(timeout, TimeUnit.MILLISECONDS)
			  .readTimeout(timeout, TimeUnit.MILLISECONDS)
			  .writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callBack.onFailure(e.getMessage());
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String body = response.body().string();
				int code = response.code();
				callBack.onResponse(code, body);
			}
		});
		
	}
	
	public static void get(String url, Map<String, String> headers, final MTLHttpCallBack callBack) {
		Request.Builder builder = new Request.Builder()
			  .url(url)
			  .get();
		if (headers != null) {
			for (String key : headers.keySet()) {
				builder.header(key, headers.get(key));
			}
		}
		
		Request request = builder.build();
		OkHttpClient httpClient = new OkHttpClient();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callBack.onFailure(e.getMessage());
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String body = response.body().string();
				int code = response.code();
				callBack.onResponse(code, body);
			}
		});
		
	}
	
	/*
	 * @功能: 下载文件
	 * @参数  url 下载文件的url
	 * @参数  filePath 下载文件存放的路径
	 * @参数  fileName 下载文件文件名(xxx.pdf)
	 * @Date  2020/8/26;
	 * @Author zhangg
	 **/
	public static void downLoadFile(String url, final String filePath, final String fileName, final MTLHttpDownCallBack listener) {
		
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder()
			  .url(url)
			  .build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				if (e != null)
					listener.onDownloadFailed(0, e.getMessage());
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response != null && response.code() != 200) {
					listener.onDownloadFailed(response.code(), response.message());
					return;
				}
				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				
				//储存下载文件的目录
				File dir = new File(filePath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, fileName);
				try {
					is = response.body().byteStream();
					long total = response.body().contentLength();
					fos = new FileOutputStream(file);
					long sum = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						sum += len;
						int progress = (int) (sum * 1.0f / total * 100);
						listener.onDownloading(progress);
					}
					fos.flush();
					//下载完成
					listener.onDownloadSuccess(file);
				} catch (Exception e) {
					if (e != null)
						listener.onDownloadFailed(0, e.getMessage());
				} finally {
					
					try {
						if (is != null) {
							is.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
					
					}
					
				}
				
				
			}
		});
	}
	
	public static void downLoadFile(final String url, final String filePath, final String fName, final String fileType, final boolean cover, JSONObject headers, JSONObject form, JSONObject jsonBody, final MTLHttpDownCallBack listener) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if (form != null) {
			if (form.toString().contains(":")) {
				builder.post(FormBody.create(mediaType, form.toString()));
			}
		}
		if (jsonBody != null) {
			RequestBody requestBody = RequestBody.create(mediaType, jsonBody.toString());
			builder.post(requestBody);
		}
		Request request = builder.build();
		if (headers != null) {
			addDownLoadHeader(request, headers);
		}
		OkHttpClient okHttpClient = new OkHttpClient();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				if (e != null)
					listener.onDownloadFailed(0, e.getMessage());
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response != null && response.code() != 200) {
					listener.onDownloadFailed(response.code(), response.message());
					return;
				}
				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				//储存下载文件的目录
				File dir = new File(filePath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String fileName = fName;
				if (TextUtils.isEmpty(fileName)) {
					String array[] = url.split("/");
					if (array != null && array.length > 0) {
						String urlName = array[array.length - 1];
						if (urlName.contains(".")) {
							fileName = urlName;
						}
					}
					if (TextUtils.isEmpty(fileName)) {
						fileName = System.currentTimeMillis() + "." + fileType;
					}
				}
				File file = new File(dir, fileName);
				String newName;
				if (file.exists() && !cover) {
					newName = renameFile(filePath, fileName);
					file = new File(dir, newName);
				}
				try {
					is = response.body().byteStream();
					long total = response.body().contentLength();
					fos = new FileOutputStream(file);
					long sum = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						sum += len;
						int progress = (int) (sum * 1.0f / total * 100);
						listener.onDownloading(progress);
					}
					fos.flush();
					//下载完成
					listener.onDownloadSuccess(file);
				} catch (Exception e) {
					if (e != null) {
						listener.onDownloadFailed(0, e.getMessage());
					}
				} finally {
					
					try {
						if (is != null) {
							is.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
					
					}
					
				}
				
				
			}
		});
	}
	
	private static String renameFile(String filePath, String fileName) {
		boolean rename = true;
		int i = 0;
		String fileNames[] = fileName.split("\\.");
		while (rename) {
			i++;
			fileName = fileNames[0] + "(" + i + ")." + fileNames[1];
			File file = new File(filePath + "/" + fileName);
			if (!file.exists()) {
				rename = false;
			}
		}
		return fileName;
	}
	
	private static void addDownLoadHeader(Request request, JSONObject headers) {
		if (headers != null) {
			Iterator iterator = headers.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				request.header(headers.optString(key));
			}
		}
	}
	
	public void upLoadFile(String url, String filePath, JSONObject header, final MTLHttpCallBack callBack) {
		OkHttpClient okHttpClient = new OkHttpClient();
		File file = new File(Environment.getExternalStorageDirectory() + "test.txt");
		MediaType MEDIATYPE = MediaType.parse("text/plain; charset=utf-8");
		RequestBody requestBody = RequestBody.create(MEDIATYPE, file);
		Request request = new Request.Builder().url(url)
			  .post(requestBody)
			  .build();
		
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				// 请求失败
			}
			
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// 请求成功
			}
		});
//        call.cancel();
	
	}
	
	public void upLoadMultipartFile(String url, String filePath, JSONObject headers, JSONObject formData, final MTLHttpCallBack callBack) {
		OkHttpClient okHttpClient = new OkHttpClient();
		String fileName = filePath.substring(filePath.lastIndexOf("/"), filePath.length());
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		
		MultipartBody.Builder builder = new MultipartBody.Builder();
		//设置类型是表单
		builder.setType(MultipartBody.FORM);
		//添加数据
		addFormData(builder, formData);
		builder.addFormDataPart("File", "zhangqilu.png", RequestBody.create(MediaType.parse("image/png"), file));
		
		RequestBody requestBody = builder.build();
		Request request = new Request.Builder().url(url).post(requestBody).build();
		addHeader(request, headers);
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
			}
			
			@Override
			public void onResponse(Call call, Response response) {
			}
		});
	}
	
	private void addFormData(MultipartBody.Builder builder, JSONObject formData) {
		if (formData != null) {
			Iterator iterator = formData.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				builder.addFormDataPart(key, formData.optString(key));
			}
		}
	}
	
	private void addHeader(Request request, JSONObject headers) {
		if (headers != null) {
			Iterator iterator = headers.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				request.header(headers.optString(key));
			}
		}
	}
	
}
