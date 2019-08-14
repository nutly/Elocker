package com.feiyang.elocker.util;

import android.util.Log;
import com.google.gson.JsonObject;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HttpsUtil {

    private static OkHttpClient okHttpClient;

    public static Response get(final String url) {
        return get(url, new HashMap<String, String>());
    }

    public static Response get(final String url, HashMap<String, String> headers) {
        Response response = null;
        Request.Builder builder = new Request.Builder().url(url)
                .get()
                .header("Content-Type", "application/json;charset:utf-8");
        if (headers.size() > 0) {
            for (String name : headers.keySet())
                builder.header(name, headers.get(name));
        }
        Request request = builder.build();
        /*初始化okhttpclient，确保只有一个okhttpclient实例*/
        HttpsUtil.buildOKHttpClient();
        if (HttpsUtil.okHttpClient != null) {
            Call call = HttpsUtil.okHttpClient.newCall(request);
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("HttpsUtil", "Get " + url + " failed");
            }
        }
        return response;
    }

    public static Response post(String url, JsonObject params) {
        return post(url, params, new HashMap<String, String>());
    }

    public static Response post(String url, JsonObject params, HashMap<String, String> headers) {
        Response response = null;
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, params.toString());
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        if (headers.size() > 0) {
            for (String name : headers.keySet())
                builder.header(name, headers.get(name));
        }
        Request request = builder.build();
        HttpsUtil.buildOKHttpClient();
        if (HttpsUtil.okHttpClient != null) {
            Call call = HttpsUtil.okHttpClient.newCall(request);
            try {
                response = call.execute();
            } catch (IOException e) {
                Log.e("HttpsUtil", "post " + url + " failed, params: " + params);
            }
        }
        return response;
    }

    private static synchronized void buildOKHttpClient() {
        if (HttpsUtil.okHttpClient == null) {
            try {
                TrustManager[] trustAllCerts = buildTrustManagers();
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                HttpsUtil.okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        })
                        .build();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.e("HttpClientUtil", "No such algorithm or key management exception");
            }
        }
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }
}
