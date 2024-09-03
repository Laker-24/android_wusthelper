package com.example.wusthelper.request;


import android.util.Log;

import com.example.wusthelper.helper.SharePreferenceLab;
import com.example.wusthelper.request.okhttp.CommonOkHttpClient;
import com.example.wusthelper.request.okhttp.listener.DisposeDataHandle;
import com.example.wusthelper.request.okhttp.listener.DisposeDataListener;
import com.example.wusthelper.request.okhttp.request.CommonRequest;
import com.example.wusthelper.request.okhttp.request.RequestParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求中心
 */
public class RequestCenter {

    private static final String TAG = "RequestCenter";
    private static String token = "";
    private static String message="";

    //根据参数发送所有get请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    //根据参数发送所有get请求
    public static void getRequest(String url, RequestParams params, RequestParams headers,
                                  DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params,headers), new DisposeDataHandle(listener, clazz));
    }

    //根据参数发送所有post请求
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.post(CommonRequest.
                createPostRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    //根据参数发送所有post、请求带请求头的
    public static void postRequest(String url, RequestParams params,RequestParams headers,
                                   DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.post(CommonRequest.
                createPostRequest(url, params,headers), new DisposeDataHandle(listener, clazz));
    }

    //根据参数发送所有post请求
    public static void postJsonRequest(String url, RequestParams params, DisposeDataListener listener,
                                   Class<?> clazz) {
        RequestParams headers = new RequestParams();
        headers.put("Token",token);
        CommonOkHttpClient.post(CommonRequest.
                createPostJsonRequest(url, params,headers), new DisposeDataHandle(listener, clazz));
    }

    //根据参数发送所有post、请求带请求头的
    public static void postJsonRequest(String url, RequestParams params,RequestParams headers,
                                   DisposeDataListener listener, Class<?> clazz) {
        headers.put("Token",token);
        CommonOkHttpClient.post(CommonRequest.
                createPostJsonRequest(url, params,headers), new DisposeDataHandle(listener, clazz));
    }
    //根据参数发送所有post、请求带请求头的、同步请求
    public static Response postRequestExecute(String url, RequestParams params,RequestParams headers,
                                   Class<?> clazz) throws IOException {
        return CommonOkHttpClient.postExecute(CommonRequest.
                createPostRequest(url, params,headers), new DisposeDataHandle(clazz));
    }

    public static Response postJsonRequestExecute(String url, RequestParams params,RequestParams headers,
                                              Class<?> clazz) throws IOException {
        return CommonOkHttpClient.postExecute(CommonRequest.
                createPostJsonRequest(url, params,headers), new DisposeDataHandle(clazz));
    }

    /**
     * 判断新接口是否登陆
     *
     * @return
     */
    public static boolean isLogin() {
        return token != null;
    }

    public static void setToken(String token) {
        RequestCenter.token = token;
    }

    public static String getToken() {
        return  RequestCenter.token;
    }

    public static void setMessage(String message) {
        RequestCenter.message = message;
    }

    /**
     * 有参数的get请求简易封装
     * */
    public static void get(String url, RequestParams params, DisposeDataListener listener,
                           Class<?> clazz) {
        RequestParams headers = new RequestParams();
        Log.e(TAG,"token = " + token);
        Log.e(TAG,"token 2 = " + SharePreferenceLab.getToken());
        headers.put("Token",token);
        RequestCenter.getRequest(url,params,headers,listener, clazz);
    }

    public static void getQr(String token,String url, RequestParams params, DisposeDataListener listener,
                           Class<?> clazz) {
        RequestParams headers = new RequestParams();
        Log.e(TAG,"token = " + token);
        headers.put("Token",token);
        RequestCenter.getRequest(url,params,headers,listener, clazz);
    }

    public static void getCourseData(String token, String semester, Callback callback) {

        //这个接口暂时废弃，扫码的

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .header("token",token)
                .url(WustApi.BASE_API+WustApi.CURRICULUM_API+"?schoolTerm="+semester)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
