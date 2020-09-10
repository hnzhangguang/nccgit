package com.yy.pdalibs.api;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;
import com.yonyou.common.utils.MsgUtil;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;
import com.yy.pdalibs.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/*
 * @功能: pda 扫描
 * @Date  2020/7/24;
 * @Author zhangg
 **/
public class BarcodeApiInvoker implements IApiInvoker {

    private static final String startBarcodeService = "startBarcodeService"; // 开始广播
    private static final String stopBarcodeService = "stopBarcodeService";

    private final String NEW_LAND_MODEL = "NLS-";  // 新大陆

    private final String MC33_MODE_RFID = "MC33";  // 斑马
    private final String BANMA_MODE = "MC33";  // 斑马

    private final String honeywell_MODE = "EDA";  // honeywe=ll
    public static final String honeywell_action = "scan";

    // 斑马rfid
    String BANMA_RFID_ACTION = "com.dwbasicintent1.ACTION";
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
                newLandResult(intent);
            } else if (intent.getAction().equals(R.string.activity_intent_filter_action_banma)) {  // 斑马
                String decodedSource = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_source_banma));
                String decodedData = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_data_banma));
                String decodedLabelType = intent.getStringExtra(context.getResources().getString(R.string.datawedge_intent_key_label_type_banma));

                MsgUtil.showMsg(decodedSource + " -> " + decodedData + " -> " + decodedLabelType);

            } else if (honeywell_action.equals(intent.getAction())) {   // honeywell

            }
        }
    };


    //honeywell start
    //SDK 扫描头阅读器
    private BarcodeReader reader;
    private AidcManager manager = null;

    /**
     * 扫描监听
     */
    private BarcodeReader.BarcodeListener barcodeListener = new BarcodeReader.BarcodeListener() {
        @Override
        public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
            //扫描数据在线程中，如果需要显示在UI 上，必须调用 UI线程来显示
            args.getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String barcodeData = barcodeReadEvent.getBarcodeData();

                    Log.i("CodeID:===", barcodeReadEvent.getCodeId());
                    Log.i("AimID:===", barcodeReadEvent.getAimId());
                }
            });

        }

        @Override
        public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

        }
    };

    /**
     * 监听扫描键 true 为 按下
     */
    private BarcodeReader.TriggerListener triggerListener = new BarcodeReader.TriggerListener() {
        @Override
        public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
            Log.i("扫描键状态", String.valueOf(triggerStateChangeEvent.getState()));
            try {
                //打开红灯
                reader.aim(triggerStateChangeEvent.getState());
                //打开白灯
                reader.light(triggerStateChangeEvent.getState());
                //开始解码
                reader.decode(triggerStateChangeEvent.getState());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    /**
     * 打开SDK模式
     */
    private View.OnClickListener onClickListenerOpenSDK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //通过SDK接口 创建 SDK 管理器 step 1
            AidcManager.create(args.getContext(), new AidcManager.CreatedCallback() {
                @Override
                public void onCreated(AidcManager aidcManager) {

                    //step 2
                    manager = aidcManager;
                    //step 3
                    reader = manager.createBarcodeReader();
                    String strinfo = "";
                    try {
                        strinfo += "Name:" + reader.getInfo().getName() + "\n";
                        strinfo += "ScannerId:" + reader.getInfo().getScannerId() + "\n";
                        strinfo += "DecodeVersion:" + reader.getInfo().getFullDecodeVersion() + "\n";
//                        info.setText(strinfo);


                        // 设置扫描属性
                        reader.setProperty(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                        reader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER, false);
                        // 设置控制类型
                        reader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                                BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
                        reader.claim();

                        //添加扫描监听 step 4
                        reader.addBarcodeListener(barcodeListener);
                        //监听按扫描键 step 5
                        reader.addTriggerListener(triggerListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    //honeywell end


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


    /*
     * @功能: 新大陆pad返回结果
     * @参数  intent;
     * @Date  2020/8/6;
     * @Author zhangg
     **/
    private void newLandResult(Intent intent) {


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
    }


}
