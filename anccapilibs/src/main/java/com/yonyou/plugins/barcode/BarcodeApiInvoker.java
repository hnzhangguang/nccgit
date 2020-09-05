//package com.yonyou.plugins.barcode;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.RemoteException;
//import android.text.TextUtils;
//
//import com.yonyou.plugins.IApiInvoker;
//import com.yonyou.plugins.MTLArgs;
//import com.yonyou.plugins.MTLException;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import static android.content.Context.BIND_AUTO_CREATE;
//
//
///*
// * @功能: pda 扫描
// * @Date  2020/7/24;
// * @Author zhangg
// **/
//public class BarcodeApiInvoker implements IApiInvoker {
//	public static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
//	public static final String SCN_SUNMI_CUST_ACTION_SCODE = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
//	public static final String ACTION_SEND_SCAN_RESULT = "com.android.action.SEND_SCAN_RESULT";
//	public static final String SCN_CUST_EX_SCODE = "scannerdata";
//	public static final String SCN_SUNMI_CUST_EX_SCODE = "data";
//	private static final String NEW_LAND_BARCAST_ACTION = "nlscan.action.SCANNER_RESULT";
//	private static final String SUPOIN_BARCAST_ACTION = "com.android.server.scannerservice.broadcast";
//	private static final String AUTOID6L_BARCAST_ACTION = "com.android.server.scannerservice.broadcastfh";
//	private static final String START_SERVICE = "startBarcodeService"; // 开始广播
//	private static final String STOP_SERVICE = "stopBarcodeService";
//	private static final String START_SCAN = "startBarcodeScan";
//	private static final String STOP_SCAN = "stopBarcodeScan";
//	private static final String CONTROL_SCAN = "controlBarcodeScan";
//	private final String NEW_LAND_MODEL = "NLS-MT66";  // 新大陆
//	private final String NEW_LAND_MODEL_MT90 = "NLS-";  // 新大陆
//	private final String MC33_MODE_RFID = "MC33";  // 斑马
//	private final String SUPOIN_MODEL = "PDA";
//	private final String DS5_AX_MODEL = "DS5_AX";
//	private final String AUTOID6L_MODEL = "PDT-6LP";
//	private final String AUTOIDD3P_MODEL = "PDT-D3P";
//	private final String SUNMI_MODEL = "L2-H";
//	/*Barcode AIDL Connection Event Handler*/
//	private final int SERVICE_CONNECTED = 0;
//	private final int SERVICE_DISCONNECTED = 1;
//	// 斑马rfid
//	String BANMA_RFID_ACTION = "com.dwbasicintent1.ACTION";
//	private MTLArgs args;
//
//
//	/*
//	 * @功能: pda广播接收者
//	 * @Date  2020/8/6;
//	 * @Author zhangg
//	 **/
//	BroadcastReceiver mBarcodeReadBroadCast = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals("app.dsic.barcodetray.BARCODE_BR_DECODING_DATA")) {
//				dsicResult(intent);
//			} else if (intent.getAction().equals(NEW_LAND_BARCAST_ACTION)) {
//				newLandResult(intent);
//			} else if (intent.getAction().equals(SUPOIN_BARCAST_ACTION) && SUPOIN_MODEL.equals(Build.MODEL)) {
//				supoinResult(intent);
//			} else if (intent.getAction().equals(AUTOID6L_BARCAST_ACTION)) {
//				seuicResult(intent);
//			} else if ((intent.getAction().equals(SCN_CUST_ACTION_SCODE) || intent.getAction().equals(SCN_SUNMI_CUST_ACTION_SCODE)) && SUNMI_MODEL.equals(Build.MODEL)) {
//				SUNMIResult(intent);
//			} else if (intent.getAction().equals(BANMA_RFID_ACTION)) {
//
//				String decodedSource = intent.getStringExtra("com.symbol.datawedge.source");
//				String decodedData = intent.getStringExtra("com.symbol.datawedge.data_string");
//				String decodedLabelType = intent.getStringExtra("com.symbol.datawedge.label_type");
//
//				try {
//					JSONObject jsonObject = new JSONObject();
//					jsonObject.put("source", decodedSource);
//					jsonObject.put("data", decodedData);
//					jsonObject.put("type", decodedLabelType);
//					args.success(jsonObject, true);
//
//				} catch (JSONException e) {
//					e.printStackTrace();
//					args.error("BAD CODE", true);
//				}
//
//
//			}
//		}
//	};
//
//
//	/*Barcode AIDL Interface*/
//	private IBarcodeInterface mBarcode;
//	private ServiceConnectionHandler mServiceConnectionHandler =
//		  new ServiceConnectionHandler();
//	/*AIDL Service Connection*/
//	private ServiceConnection srvConn =
//		  new ServiceConnection() {
//			  @Override
//			  public void onServiceConnected(ComponentName name, IBinder service) {
//				  mBarcode = IBarcodeInterface.Stub.asInterface(service);
//				  mServiceConnectionHandler.sendEmptyMessage(SERVICE_CONNECTED);
//			  }
//
//			  @Override
//			  public void onServiceDisconnected(ComponentName name) {
//				  mServiceConnectionHandler.sendEmptyMessage(SERVICE_DISCONNECTED);
//				  mBarcode = null;
//			  }
//		  };
//
//
//	/*
//	 * @功能: pda调用入口
//	 * @参数  apiname pda类型;
//	 * @Date  2020/8/6;
//	 * @Author zhangg
//	 **/
//	@Override
//	public String call(String apiname, final MTLArgs args) throws MTLException {
//		final Activity context = args.getContext();
//		this.args = args;
//		switch (apiname) {
//			case START_SERVICE:
//				// 新大陆
//				if (NEW_LAND_MODEL.equals(Build.MODEL) || Build.MODEL.contains(NEW_LAND_MODEL_MT90)) {
//					IntentFilter intentFilter = new IntentFilter(NEW_LAND_BARCAST_ACTION);
//					intentFilter.addAction(ACTION_SEND_SCAN_RESULT);
//					context.registerReceiver(mBarcodeReadBroadCast, intentFilter);
//				} else if (SUPOIN_MODEL.equals(Build.MODEL)) {
//					context.registerReceiver(mBarcodeReadBroadCast, new IntentFilter(SUPOIN_BARCAST_ACTION));
//				} else if (DS5_AX_MODEL.equals(Build.MODEL)) {
//					context.bindService(new Intent("app.dsic.barcodetray.IBarcodeInterface"), srvConn, BIND_AUTO_CREATE);
//					context.registerReceiver(mBarcodeReadBroadCast, new IntentFilter("app.dsic.barcodetray.BARCODE_BR_DECODING_DATA"));
//				} else if (AUTOID6L_MODEL.equals(Build.MODEL) || AUTOIDD3P_MODEL.equals(Build.MODEL)) {
//					IntentFilter intentFilter = new IntentFilter();
//					intentFilter.addAction(AUTOID6L_BARCAST_ACTION);
//					intentFilter.setPriority(Integer.MAX_VALUE);
//					context.registerReceiver(mBarcodeReadBroadCast, intentFilter);
//				} else if (SUNMI_MODEL.equals(Build.MODEL)) {
//					IntentFilter intentFilter = new IntentFilter();
//					intentFilter.addAction(SCN_CUST_ACTION_SCODE);
//					intentFilter.addAction(SCN_SUNMI_CUST_ACTION_SCODE);
//					context.registerReceiver(mBarcodeReadBroadCast, intentFilter);
//				} else if (Build.MODEL.contains(MC33_MODE_RFID)) { // 斑马
//
//					IntentFilter filter = new IntentFilter();
//					filter.addCategory(Intent.CATEGORY_DEFAULT);
//					filter.addAction(BANMA_RFID_ACTION);
//					context.registerReceiver(mBarcodeReadBroadCast, filter);
//				}
//				return "";
//			case STOP_SERVICE:
//				try {
//					context.unregisterReceiver(mBarcodeReadBroadCast);
//				} catch (IllegalArgumentException e) {
//					args.error("未监听扫码枪广播");
//				}
//
//				if (DS5_AX_MODEL.equals(Build.MODEL)) {
//					context.unbindService(srvConn);
//				}
//				args.success("SERVICE STOP");
//				return "";
//			case START_SCAN:
//				try {
//					mBarcode.ScanStart();
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//				return "";
//			case STOP_SCAN:
//				try {
//					mBarcode.ScanStop();
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//				return "";
//			case CONTROL_SCAN:
//				try {
//					boolean disable = args.getBoolean("disable");
//					mBarcode.SetScanEnable(!disable);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//				return "";
//		}
//		throw new MTLException(apiname + ": function not found");
//	}
//
//	private void SUNMIResult(Intent intent) {
//		String message = "";
//		if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
//			try {
//				message = intent.getStringExtra(SCN_CUST_EX_SCODE).toString();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		} else if (intent.getAction().equals(SCN_SUNMI_CUST_ACTION_SCODE)) {
//			try {
//				message = intent.getStringExtra(SCN_SUNMI_CUST_EX_SCODE).toString();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		JSONObject jsonObject = new JSONObject();
//		try {
//			if (!TextUtils.isEmpty(message)) {
//				jsonObject.put("data", message);
//				args.success(jsonObject, true);
//			} else {
//				args.error("BAD CODE", true);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			args.error("BAD CODE", true);
//		}
//	}
//
//	private void seuicResult(Intent intent) {
//		String codeValue = intent.getStringExtra("scannerdata");
//		JSONObject jsonObject = new JSONObject();
//		try {
//			if (!TextUtils.isEmpty(codeValue)) {
//				jsonObject.put("data", codeValue);
//				args.success(jsonObject, true);
//			} else {
//				args.error("BAD CODE", true);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			args.error("BAD CODE", true);
//		}
//	}
//
//	private void dsicResult(Intent intent) {
//		BarcodeDeclaration.SYMBOLOGY_IDENT symbology_ident = BarcodeDeclaration.SYMBOLOGY_IDENT.fromInteger(intent.getIntExtra("EXTRA_BARCODE_DECODED_SYMBOLE", -1));
//		if (symbology_ident != BarcodeDeclaration.SYMBOLOGY_IDENT.NOT_READ) {
//			String data = /*"[" + symbology_ident.toString() + "]" + */intent.getStringExtra("EXTRA_BARCODE_DECODED_DATA");
//			JSONObject jsonObject = new JSONObject();
//			try {
//				jsonObject.put("data", data);
//				args.success(jsonObject, true);
//			} catch (JSONException e) {
//				e.printStackTrace();
//				args.error("BAD CODE", true);
//			}
//		} else {
//			args.error("NOT READ", true);
//		}
//	}
//
//	private void supoinResult(Intent intent) {
//		String codeType = intent.getStringExtra("codetype");
//		String codeValue = intent.getStringExtra("scannerdata");
//		JSONObject jsonObject = new JSONObject();
//		try {
//			if (!TextUtils.isEmpty(codeValue)) {
//				jsonObject.put("data", codeValue);
//				jsonObject.put("type", codeType);
//				args.success(jsonObject, true);
//			} else {
//				args.error("BAD CODE", true);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			args.error("BAD CODE", true);
//		}
//	}
//
//
//	/*
//	 * @功能: 新大陆pad返回结果
//	 * @参数  intent;
//	 * @Date  2020/8/6;
//	 * @Author zhangg
//	 **/
//	private void newLandResult(Intent intent) {
//		if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("SCAN_BARCODE1"))) {
//			String scanResult_1 = intent.getStringExtra("SCAN_BARCODE1").trim();//扫描到数据含有空格，必须执行trim
//			String scanResult_2 = intent.getStringExtra("SCAN_BARCODE2");
//			int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1); // -1:unknown
//			String scanStatus = intent.getStringExtra("SCAN_STATE");
//			if ("ok".equals(scanStatus)) { //成功
//				JSONObject jsonObject = new JSONObject();
//				try {
//					jsonObject.put("data", scanResult_1);
//					jsonObject.put("scanResult_2", scanResult_2);
//					jsonObject.put("barcodeType", barcodeType);
//					if (args != null) {
//						args.success(jsonObject, true);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//					if (args != null) {
//						args.error("内部错误", true);
//					}
//				}
//			} else {
//				if (args != null) {
//					args.error(scanStatus, true);
//				}
//			}
//		} else {
//			args.error("未扫描到数据", true);
//		}
//	}
//
//	class ServiceConnectionHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//				case SERVICE_CONNECTED:
//					Connect();
//					break;
//				case SERVICE_DISCONNECTED:
//					Disconnect();
//					break;
//			}
//		}
//
//		private void Connect() {
//			try {
//				/*Set Receive type to Intent event*/
//				mBarcode.SetRecvType(BarcodeDeclaration.RECEIVE_TYPE.INTENT_EVENT.ordinal());
////                setNotificationItems();
////                setDecodingCharSet();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		private void Disconnect() {
//		}
//	}
//
//}
