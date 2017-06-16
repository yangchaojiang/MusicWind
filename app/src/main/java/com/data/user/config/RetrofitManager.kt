package com.data.user.config

import android.content.Context
import com.data.user.config.server.MusicServer
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 网络请求挂你类
 */
class RetrofitManager {
    private lateinit var retrofit: retrofit2.Retrofit
    private lateinit var mMusicServer: MusicServer

    companion object {
        fun getInstance(): RetrofitManager {

            return RetrofitManager.Holder.instance
        }
    }

    private object Holder {
        val instance = RetrofitManager()
    }

    /***
     * 初始化
     * **/
    fun init(context: Context) {
        //log拦截器
//        val httpClient = okhttp3.OkHttpClient.Builder().addInterceptor({
//            chain ->
//            val request = chain.request()
//                    .newBuilder()
//                    .addHeader("showapi_appid", "40405")
//                    .addHeader("showapi_sign","c7b54795092345dab29ea5c1c16170a1")
//                    .build()
//            chain.proceed(request)
//
//        })
        val httpClient = okhttp3.OkHttpClient.Builder().addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//日志
                .addNetworkInterceptor(CacheInterceptor())
                .cache(Cache(context.cacheDir, 10 * 1024 * 1024))//设置数据缓存
                .addInterceptor(CacheInterceptor())//设置拦截器
                .build()
        retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("http://route.showapi.com/")
                .addConverterFactory(retrofit2.converter.fastjson.FastJsonConverterFactory.create())
                .addCallAdapterFactory(retrofit2.adapter.rxjava.RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build()
        mMusicServer = retrofit.create(MusicServer::class.java)
    }

    fun getmusic(): MusicServer {
        return mMusicServer
    }
}