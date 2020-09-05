package com.yonyou.plugins.window;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.yonyou.common.utils.MTLLog;

import org.xutils.http.cookie.DbCookieStore;

import java.io.File;
import java.net.HttpCookie;
import java.util.List;


public class YYWebView extends WebView {
	
	public final static int FILECHOOSER_RESULTCODE = 0101001;
	public static ValueCallback<Uri> mUploadMessage;
	private String mHtml = null;
	private boolean isFinished = true;
	//    private Context mContext = null;
	private Activity activity = null;
	private int scale = 0;
	private boolean mIsWebClient = false;
	private String mRootPath;
	private WebSettings webSettings;
	private String mChangeUrl;
	
	public YYWebView(Activity activity) {
		this(activity, null);
	}
	
	public YYWebView(final Activity activity, AttributeSet attrs) {
		super(activity.getApplicationContext(), attrs);
		this.activity = activity;
		//        this.mContext = context;
		mRootPath = activity.getFilesDir().getAbsolutePath();
		webSettings = getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setAppCacheMaxSize(1024 * 1024 * 12);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		
		//允许webview对文件的操作
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAllowFileAccessFromFileURLs(true);
		webSettings.setAllowContentAccess(true);
		webSettings.setMediaPlaybackRequiresUserGesture(false);
		
		// 开启 database storage API 功能
		webSettings.setDatabaseEnabled(true);
		//设置数据库缓存路径
		webSettings.setDatabasePath(mRootPath);
		//设置  Application Caches 缓存目录
		webSettings.setAppCachePath(mRootPath);
		//开启 Application Caches 功能
		webSettings.setAppCacheEnabled(true);
		
		
		this.setInitialScale(75);// 默认缩放
		setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
		requestFocus();
		webSettings.setUseWideViewPort(true);
		setWebViewClient(new WebViewClient() {
			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				return super.shouldOverrideUrlLoading(view, request);
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// handler.cancel(); //默认的处理方式
				handler.proceed();// 忽略SSL证书错误，继续加载页面.
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
			                            String description, String failingUrl) {
				MTLLog.w("runjs", "CODE:" + errorCode + " - " + description + "[" + failingUrl + "]");
				isFinished = true;
			}
			
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
				return super.shouldInterceptRequest(view, request);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				syncCookie(activity, url);
				if (url.toLowerCase().startsWith("taobao://")) {
					return true;
				}
				
				if (url.startsWith("tel:")) {
					return true;
				}
				
				if (url.startsWith("mailto:")) {
					return true;
				}
				if (!mIsWebClient) {// 在webview中打开
					mChangeUrl = url;
					view.loadUrl(url);
					return false;
				}
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));// 在浏览器中打开
				activity.startActivity(intent);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				MTLLog.d("runjs", "load finish - " + url);
				super.onPageFinished(view, url);
				if (view instanceof YYWebView) {
					((YYWebView) view).setFinish(true);
				}
				
			}
		});
		this.setWebChromeClient(new WebChromeClient() {// 支持<input
			// type="file"…>元素
			// 扩展浏览器上传文件
			// For Android 3.0+
			@SuppressWarnings("unused")
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
			
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				activity.startActivityForResult(
					  Intent.createChooser(i, "File Chooser"),
					  FILECHOOSER_RESULTCODE);
				
			}
			
			// 3.0++版本
			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
			                            String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				activity.startActivityForResult(
					  Intent.createChooser(i, "File Chooser"),
					  FILECHOOSER_RESULTCODE);
			}
			
			// 4.1
			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
			                            String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				activity.startActivityForResult(
					  Intent.createChooser(i, "File Chooser"),
					  FILECHOOSER_RESULTCODE);
			}
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				if (activity != null && !activity.isDestroyed()) {
					MTLAlertDialog dialog = new MTLAlertDialog(activity, message);
					dialog.show();
					result.confirm();
					return true;
				}
				return false;
			}
		});
		
	}
	
	public static ValueCallback<Uri> getUploadMessage() {
		return mUploadMessage;
	}
	
	/**
	 * 给WebView同步Cookie
	 *
	 * @param context 上下文
	 * @param url     可以使用[domain][host]
	 */
	private void syncCookie(Context context, String url) {
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		CookieManager.setAcceptFileSchemeCookies(true);
		cookieManager.removeSessionCookie();// 移除旧的[可以省略]
		try {
			//            if (true) {
			//                return;
			//            }
			DbCookieStore instance = DbCookieStore.INSTANCE;
			
			List<HttpCookie> cookies = instance.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				HttpCookie cookie = cookies.get(i);
				String value = cookie.getName() + "=" + cookie.getValue();
				cookieManager.setCookie(getDomain(url), value);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
				CookieManager.getInstance().flush();
			} else {
				CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
				cookieSyncManager.sync();
			}
		} catch (Exception e) {
			Log.e("mmmm", e + "");
		}
		
	}
	
	/**
	 * 获取URL的域名
	 */
	private String getDomain(String url) {
		url = url.replace("http://", "").replace("https://", "");
		if (url.contains("/")) {
			url = url.substring(0, url.indexOf('/'));
		}
		return url;
	}
	
	synchronized protected void setFinish(boolean value) {
		MTLLog.d("runjs", "isFinish from " + isFinished + " to " + value);
		isFinished = value;
	}
	
	@Override
	public void loadUrl(String url) {
		boolean falg = false;
		String newUrl = mRootPath + ""
			  + url.replace("file:///android_asset/webcontrol/", "/");
		File file = new File(newUrl);
		if (file.exists()) {
			
			falg = true;
			
		}
		if (url.contains("asset") && falg) {
			url = "file://" + newUrl;
		}
		
		syncCookie(activity.getApplicationContext(), url);
		super.loadUrl(url);
	}
	
	public void loadScript(String script) {
		loadUrl(script);
	}
	
	public void loadHtmlData(String html) {
		this.mHtml = html;
		if (scale != 0) {
			this.setInitialScale(scale);// 设置缩放比例
		} else {
			this.setInitialScale(0);
		}
		webSettings.setTextSize(WebSettings.TextSize.LARGER);
		this.loadData(html, "text/html; charset=UTF-8", null);
		MTLLog.w("runjs", "loadHtmlData");
		setFinish(false);
	}
	
	@SuppressLint("JavascriptInterface")
	@Override
	public void addJavascriptInterface(Object obj, String interfaceName) {
		webSettings.setJavaScriptEnabled(true);
		super.addJavascriptInterface(obj, interfaceName);
	}
	
	public void setUMWebViewReadOnly(boolean readonly) {
		if (readonly == true) {
			this.setEnabled(false);
		} else {
			this.setEnabled(true);
		}
	}
	
	/**
	 * 加载htmldata数据 可以显示图片，可以保存 导航路径
	 * 当前进和后退时，它会通过baseUrl来寻找historyUrl的路径来加载historyUrl路径来加载历史界面，
	 * 需要注意的就是history所指向的必须是一个页面，并且页面存在于SD卡中或程序中（assets
	 */
	public void loadHtmlDataWithBackURL(String baseUrl, String historyUrl) {
		this.webSettings.setDefaultTextEncodingName("UTF-8");
		this.loadDataWithBaseURL(baseUrl, mHtml, "text/html", "UTF-8",
			  historyUrl);
		MTLLog.w("runjs", "loadHtmlDataWithBackURL");
		setFinish(false);
	}
	
	public void exec(final String js) {
		loadUrl("javascript:" + js);
	}
	
}
