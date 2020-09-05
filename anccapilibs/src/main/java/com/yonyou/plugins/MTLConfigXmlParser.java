/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package com.yonyou.plugins;

import android.content.Context;

import com.yonyou.common.utils.MTLLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class MTLConfigXmlParser {
    private static String TAG = "MTLConfigXmlParser";
    boolean insideFeature = false;
    String service = "", pluginClass = "", paramType = "";
    boolean onload = false;
    private Context action;
    private HashMap<String, MTLPluginEntry> apis = new HashMap<String, MTLPluginEntry>();

    public MTLConfigXmlParser(HashMap<String, MTLPluginEntry> apis) {
        this.apis = apis;
    }

    public void parse(Context action) {
        // First checking the class namespace for config.xml
        int id = action.getResources().getIdentifier("config", "xml", action.getClass().getPackage().getName());
        if (id == 0) {
            // If we couldn't find config.xml there, we'll look in the namespace from AndroidManifest.xml
            id = action.getResources().getIdentifier("config", "xml", action.getPackageName());
            if (id == 0) {
                MTLLog.e(TAG, "res/xml/config.xml is missing!");
                return;
            }
        }
        this.action = action;
        parse(action.getResources().getXml(id));
    }


    /*
     * @功能: 开始解析此xml文档内容
     * @参数:
     * @Date  2020/9/5 1:37 PM
     * @Author zhangg
     **/
    public void parse(XmlPullParser xml) {
        int eventType = -1;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                handleStartTag(xml);
            } else if (eventType == XmlPullParser.END_TAG) {
                handleEndTag(xml);
            }
            try {
                eventType = xml.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <feature name="file">
     * <param
     * name="android-package"
     * value="com.yonyou.plugins.file.FileApiInvoker" />
     * </feature>
     *
     * @param xml
     */
    /*
     * @功能: 标签开始-开始解析此标签内容
     * @参数: xml
     * @Date  2020/9/5 1:34 PM
     * @Author zhangg
     **/
    public void handleStartTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if (strNode.equals("feature")) {
            //Check for supported feature sets  aka. plugins (Accelerometer, Geolocation, etc)
            //Set the bit for reading params
            insideFeature = true;
            service = xml.getAttributeValue(null, "name");
        } else if (insideFeature && strNode.equals("param")) {
            paramType = xml.getAttributeValue(null, "name");
            if (paramType.equals("service")) // check if it is using the older service param
                service = xml.getAttributeValue(null, "value");
            else if (paramType.equals("android-package"))
                pluginClass = xml.getAttributeValue(null, "value");
        }

    }


    /*
     * @功能: 标签结束-把解析出的内容放到map中
     * @参数: xml
     * @Date  2020/9/5 1:33 PM
     * @Author zhangg
     *
     **/
    public void handleEndTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if (strNode.equals("feature")) {
            apis.put(service, new MTLPluginEntry(service, pluginClass, onload));
            service = "";
            pluginClass = "";
            insideFeature = false;
        }
    }
}
