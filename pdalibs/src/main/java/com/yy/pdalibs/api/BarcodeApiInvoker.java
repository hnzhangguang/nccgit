package com.yy.pdalibs.api;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;
import com.yonyou.common.utils.MsgUtil;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;
import com.yy.pdalibs.R;

import org.json.JSONException;
import org.json.JSONObject;


/*
 * @功能: pda 扫描
 * @Date  2020/7/24;
 * @Author zhangg
 **/
public class BarcodeApiInvoker implements IApiInvoker {

    private static final String startBarcodeService = "startBarcodeService"; // 开始广播
    private static final String stopBarcodeService = "stopBarcodeService";

    private final String NEW_LAND_MODEL = "NLS-";  // 新大陆
    private final String BANMA_MODE = "MC33";  // 斑马
    private final String honeywell_MODE = "EDA";  // honeywe=ll
    public static final String honeywell_action = "scan";

    private MTLArgs args;
    // 新大陆
    private ScanManager mScanMgr;


    /*
     * @功能: pda广播接收者
     * @Date  2020/8/6;
     * @Author zhangg
     **/
    BroadcastReceiver mBarcodeReadBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 新大陆
            if (intent.getAction().equals(ScanManager.ACTION_SEND_SCAN_RESULT)) {
                MsgUtil.showMsg("新大陆pad结果进来了~");
                if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("SCAN_BARCODE1"))) {
                    String scanResult_1 = intent.getStringExtra("SCAN_BARCODE1").trim();//扫描到数据含有空格，必须执行trim
                    String scanResult_2 = intent.getStringExtra("SCAN_BARCODE2");
                    int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1); // -1:unknown
                    String scanStatus = intent.getStringExtra("SCAN_STATE");
                    if ("ok".equals(scanStatus)) { //成功
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("data", scanResult_1);
                            jsonObject.put("scanResult_2", scanResult_2);
                            jsonObject.put("barcodeType", barcodeType);
                            if (args != null) {
                                args.success(jsonObject, true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (args != null) {
                                args.error("内部错误", true);
                            }
                        }
                    } else {
                        if (args != null) {
                            args.error(scanStatus, true);
                        }
                    }
                } else {
                    args.error("未扫描到数据", true);
                }
            } else if (intent.getAction().equals(R.string.activity_intent_filter_action_banma)) {  // 斑马
                String decodedSource = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_source_banma));
                String decodedData = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_data_banma));
                String decodedLabelType = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_label_type_banma));

                MsgUtil.showMsg(decodedSource + " -> " + decodedData + " -> " + decodedLabelType);

            } else if (honeywell_action.equals(intent.getAction())) {   // honeywell
                String data = intent.getStringExtra("data");
                MsgUtil.showMsg("honeywell结果: " + data);
            }
        }
    };


    /*
     * @功能: pda调用入口
     * @参数  apiname pda类型;
     * @Date  2020/8/6;
     * @Author zhangg
     **/
    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        final Activity context = args.getContext();
        this.args = args;
        switch (apiname) {
            case startBarcodeService:
                // 新大陆
                if (Build.MODEL.contains(NEW_LAND_MODEL)) {

                    mScanMgr = ScanManager.getInstance();
                    mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST); // 设置输出模式(广播)
                    // 注册广播
                    IntentFilter intentFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
                    intentFilter.addAction(ScanManager.ACTION_SEND_SCAN_RESULT);
                    context.registerReceiver(mBarcodeReadBroadCast, intentFilter);
                } else if (Build.MODEL.contains(BANMA_MODE)) {  // 斑马
                    IntentFilter filter = new IntentFilter();
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    filter.addAction(context.getResources().getString(R.string.activity_intent_filter_action_banma));
                    context.registerReceiver(mBarcodeReadBroadCast, filter);
                } else if (Build.MODEL.contains(honeywell_MODE)) { // honeywell
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(honeywell_action);
                    context.registerReceiver(mBarcodeReadBroadCast, filter);
                }
                return "";
            case stopBarcodeService:
                try {
                    context.unregisterReceiver(mBarcodeReadBroadCast);
                    mBarcodeReadBroadCast = null;
                } catch (IllegalArgumentException e) {
                    args.error("未监听扫码枪广播");
                }

                args.success("SERVICE STOP");
                return "";

        }
        throw new MTLException(apiname + ": function not found");
    }


}
