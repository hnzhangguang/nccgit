package com.yonyou.plugins.file;

import android.content.Intent;
import android.util.Log;

import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.net.MTLHttpDownCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;
import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/*
 * @功能: 文件类插件
 * @参数  ;
 * @Date  2020/8/26;
 * @Author zhangg
 **/
public class FileApiInvoker implements IApiInvoker {

    private static final String downLFile = "downFile"; // 下载文件
    private static final String openLocalFile = "openLocalFile"; // 打开本地文件


    @Override
    public String call(String apiname, final MTLArgs args) throws MTLException {
        switch (apiname) {
            case openLocalFile:  // 打开本地文件

                String fileType1 = args.getString("fileType");
                String filePath1 = args.getString("filePath");
                filePath1 = OfflineUpdateControl.getOfflinePathWhitoutAppId(args.getContext());
                filePath1 = filePath1 + "/" + "messaage.pdf";
                if (fileType1.equals("pdf")) {
                    Intent pdfFileIntent = FileUtils.getPdfFileIntent(filePath1);
                    args.getContext().startActivity(pdfFileIntent);
                }


                return "";
            case downLFile:  // 下载文件


                String downloadUrl = "http://10.6.204.108/yonyou/mobile_platform.zip";
                downloadUrl = "http://10.6.225.37/yonyou/messaage.pdf";
                downloadUrl = "http://10.6.225.37/yonyou/mtl_icon.png";
                // /data/user/0/com.yonyou.nccmob/files
                String filePath = OfflineUpdateControl.getOfflinePathWhitoutAppId(args.getContext());
                String fileName = "mtl_icon.png";
//				fileName = "messaage.pdf";
                MTLOKHttpUtils.downLoadFile(
                        downloadUrl,
                        filePath,
                        fileName,
                        new MTLHttpDownCallBack() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                MTLLog.i("success", file.getPath());
                                Log.e("mmmm", "onDownloadSuccess: " + file.getPath());
                                try {

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDownloading(int progress) {
                                Log.e("mmmm", "onDownloading: " + progress);

                            }

                            @Override
                            public void onDownloadFailed(int code, String message) {
                                MTLLog.i("error", message);
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("message", message);
                                    jsonObject.put("describe", "下载失败");
                                    args.error("1", message, false);
                                } catch (JSONException e) {
                                    LogerNcc.e(e);
                                    e.printStackTrace();
                                }
                            }
                        });


                return "";

        }
        throw new MTLException(apiname + ": function not found");
    }
}
