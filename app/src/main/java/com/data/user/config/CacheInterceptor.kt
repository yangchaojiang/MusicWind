package com.data.user.config

import com.yutils.YUtils
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.CacheControl




/**
 * Created by yangc on 2017/6/15.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: get缓存方式拦截器
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!YUtils.isNetWorkAvailable()) {//没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }

        val response = chain.proceed(request)
        val responseLatest: Response
        if (YUtils.isNetWorkAvailable()) {
            val maxAge = 60 //有网失效一分钟
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build()
        } else {
            val maxStale = 60 * 60 * 6 // 没网失效6小时
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build()
        }
        return responseLatest
    }
}