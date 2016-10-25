package com.anhubo.anhubo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyApp extends Application {
    public static MyApp mInstance;
    private static Context context;
    private static Handler handler;
    private static RequestQueue requestQueue;	// NoHttp的请求列表


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=MyApp.this;
        context = this;
        handler = new Handler();
        NoHttp.initialize(this);	// 初始化NoHttp
        requestQueue = NoHttp.newRequestQueue();	// 创建一个请求队列
        // OkHttp
        OkHttpUtils.getInstance().debug("OkHttpUtils").setConnectTimeout(100000, TimeUnit.MILLISECONDS);
        //使用https，但是默认信任全部证书
        OkHttpUtils.getInstance().setCertificates();
    }



    public static MyApp get(){
        return mInstance;
    }


    /** 获取Application类型的上下文 */
    public static Context getContext() {
        return context;
    }

    /** 获取一个可以运行到UI线程的Handler */
    public static Handler getHandler() {
        return handler;
    }

    /** 获取NoHttp的请求列表 */
    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
    
}