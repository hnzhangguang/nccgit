package com.yonyou.common.utils.utils;

import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

public class StringUtil {


    public static boolean isNull(String string) {
        return CheckUtil.isNull(string);
    }


    /*
     * @功能: 处理内容是HTML的列表item显示
     * @参数: content
     * @Date  2020/9/9 9:56 AM
     * @Author zhangg
     **/
    public static String getShowContent(String content) {
        //\u003c,\u003d,\u003e,\u003cspan,class\u003d
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        String s1 = content.replaceAll("<div class=\"divtext\">", "")
                .replaceAll("<span class=\"labeltext\">", "")
                .replaceAll("<span  class=\"labeltext\">", "")
                .replaceAll("<span class=\"normaltext\">", "")
                .replaceAll("<span  class=\"normaltext\">", "")
                .replaceAll("<span class=\"keytext\">", "")
                .replaceAll("<span  class=\"keytext\">", "")
                .replaceAll(":</span >", ":")
                .replaceAll("</span >", " | ")
                .replaceAll("</div>", "")
                .replaceAll(":\\n", ":")
                .replaceAll("\\n\\n\\n\\n", "");
        if (s1.startsWith("\n")) {
            s1 = s1.replaceFirst("\\n", "");
        }
        LogerNcc.e(s1);
        return s1;
    }


}
