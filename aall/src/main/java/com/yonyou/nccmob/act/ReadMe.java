package com.yonyou.nccmob.act;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.yonyou.common.download.OfflineUpdateControl;
import com.yonyou.common.utils.FileUtils;
import com.yonyou.common.utils.MTLLog;
import com.yonyou.common.utils.logs.LogerNcc;
import com.yonyou.common.utils.net.MTLHttpDownCallBack;
import com.yonyou.common.utils.net.MTLOKHttpUtils;
import com.yonyou.common.utils.utils.JsonUtil;
import com.yonyou.nccmob.base.BaseActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadMe {

    /*
     * @功能: 请求移动权限
     * @参数:
     * @Date  2020/8/30 9:42 PM
     * @Author zhangg
     **/
    public void requestPermisson(BaseActivity context) {
        List<String> permission = new ArrayList<>();
        PermissionX.init(context)
                .permissions(permission)
                .request(
                        new RequestCallback() {
                            @Override
                            public void onResult(
                                    boolean allGranted, List<String> grantedList, List<String> deniedList) {
                                if (allGranted) {
//							    showMessage("good~");
                                } else {
                                    String s = deniedList.toString();
                                    context.showMessage(s + " 没有授予权限.");
                                }
                            }
                        });

    }


    /*
     * @功能: 构造json对象
     * @参数:
     * @Date  2020/8/30 9:42 PM
     * @Author zhangg
     **/
    public void buildJson() {
        try {
            JSONObject put = JsonUtil.getJsonObject().
                    putEx("key", "aa").
                    putEx("aaa", "aa").
                    putEx("aaa", "aa4").
                    putEx("aa", 1111);
            LogerNcc.e(put);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * @功能: 下载东西
     * @参数:
     * @Date  2020/8/30 9:43 PM
     * @Author zhangg
     **/
    public void downloadMethod(BaseActivity activity, String downloadUrl, String fileName) {

//        String downloadUrl = "http://10.6.204.108/yonyou/mobile_so.zip";
        // /data/user/0/com.yonyou.nccmob/files
        String filePath = OfflineUpdateControl.getOfflinePathWhitoutAppId(activity);
        String url = filePath + "/mobile_so/app/salesmen/main/index.html";
//        String fileName = "mobile_so.zip";
        MTLOKHttpUtils.downLoadFile(
                downloadUrl,
                filePath,
                fileName,
                new MTLHttpDownCallBack() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        MTLLog.i("success", file.getPath());
                        try {
                            FileUtils.unZipFile(file.getPath(), filePath, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDownloading(int progress) {

                        MTLLog.i("success", progress + "");
                    }

                    @Override
                    public void onDownloadFailed(int code, String message) {
                        MTLLog.i("error", message);
                    }
                });
    }


    private void test() {


//			test();
//			Song song = new Song();
//			song.setDuration(1);
//			song.setKey("key");
//			song.setName("name");
//			song.save();
//			List<Song> all = LitePalNcc.findAll(Song.class);
//			LogerNcc.e(all);
//			JSONObject dataJson = new JSONObject();
        try {
//				dataJson.put("zhang", "guang");
//				dataJson.put("qin", "heng");
//				dataJson.put("itemtype", 1);
//				dataJson.put("userid", "userid");
//				dataJson.put("userCode", "userCode");
//				dataJson.put("userName", "userName");
//				dataJson.put("yhtUserId", "yhtUserId");
//				dataJson.put("apps", "apps");
//				UserUtil.setKeyValue(dataJson);
//				List<String> list = new ArrayList<>();
//				list.add("itemKey");
//				list.add("itemValue");
//				list.add("itemtype");
//				list.add("zhang");
//				list.add("qin");
//				JSONObject valuseByKeys = UserUtil.getValuseByKeys(list);
//				LogerNcc.e(valuseByKeys);

//				ArrayList<AppInfo> appInfos = new ArrayList<>();
//				for (int i = 0; i < 2; i++) {
//					AppInfo appInfo = new AppInfo();
//					appInfo.setAppid("appid" + i);
//					appInfo.setAppname("appname" + i);
//					appInfo.setIconurl("iconurl" + i);
//					if (1 == i) {
//						appInfo.setIsCommonUse("false");
//					} else {
//						appInfo.setIsCommonUse("true");
//					}
//					appInfo.setVersion(i);
//					appInfo.setZipurl("zipurl" + i);
//					appInfos.add(appInfo);
//				}

            // 更新应用列表
//				UserUtil.updatePermissionAppList(appInfos);
//
//				SPUtil.putStringForLogin(Constant.userCode, "aaa");
////				NCCUserVo currentUserVo = UserUtil.getCurrentUserVo();
//				List<AppInfo> hasPermissionAppInfos = UserUtil.getHasPermissionAppInfos();
//				LogerNcc.e(hasPermissionAppInfos);
//
//				// 获取我的常用应用
//				List<AppInfo> commonAppInfos = UserUtil.getCommonAppInfos();
//				LogerNcc.e(commonAppInfos);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 跳转界面
        // 忘记密码
//			startActivity(new Intent(LoginActivity.this, NCCForgetPWActivity.class));
        //  第一次重置密码
//			startActivity(new Intent(LoginActivity.this, NCCOpenH5MainActivity.class));
//			return;
    }


}
