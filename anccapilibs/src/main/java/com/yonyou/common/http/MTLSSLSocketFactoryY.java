package com.yonyou.common.http;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by yanglin  on 17/3/8.
 */

public class MTLSSLSocketFactoryY {
    private static SSLContext s_sSLContext;

    /**
     * 获取Https的证书
     * @param context Activity（fragment）的上下文
     * @return SSL的上下文对象
     */
    public static SSLContext getSSLContext(Context context, String crtfile) {

        CertificateFactory certificateFactory = null;
        InputStream inputStream = null;
        Certificate cer = null;
        KeyStore keystore = null;
        TrustManagerFactory trustManagerFactory = null;
        try {

            certificateFactory = CertificateFactory.getInstance("X.509");
            inputStream = context.getAssets().open(crtfile);//这里导入SSL证书文件

            try {
                //读取证书
                cer = certificateFactory.generateCertificate(inputStream);

            } finally {
                inputStream.close();
            }

            //创建一个证书库，并将证书导入证书库
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null,null); //双向验证时使用
            keystore.setCertificateEntry("trust", cer);

            // 实例化信任库
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // 初始化信任库
            trustManagerFactory.init(keystore);

            s_sSLContext = SSLContext.getInstance("TLS");
            s_sSLContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            //信任所有证书 （官方不推荐使用）
//         s_sSLContext.init(null, new TrustManager[]{new X509TrustManager() {
//
//				@Override
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//
//				@Override
//				public void checkServerTrusted(X509Certificate[] arg0, String arg1)
//						throws CertificateException {
//
//				}
//
//				@Override
//				public void checkClientTrusted(X509Certificate[] arg0, String arg1)
//						throws CertificateException {
//
//				}
//			}}, new SecureRandom());

            return s_sSLContext;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static SSLContext getSSLContext(){
        return s_sSLContext;
    }
}
