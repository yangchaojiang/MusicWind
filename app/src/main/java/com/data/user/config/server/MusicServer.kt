package com.data.user.config.server

import com.data.user.bean.BaseBean
import com.data.user.bean.PageKeyBean
import com.data.user.bean.Pagebean
import retrofit2.http.*

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
interface MusicServer {
    companion object {
        val appid: String = "40405"
        val sign: String = "c7b54795092345dab29ea5c1c16170a1"
    }

    //热门榜单
    @Headers("Cache-Control: max-age=640000")
    @GET("213-4")
    fun getMusicTop(@Query("topid") topid: Int, @Query("showapi_appid") showapi_appid: String = appid, @Query("showapi_sign") showapi_sign: String = sign)
            : rx.Observable<BaseBean<Pagebean>>

    //歌曲id查询歌词
    @Headers("Cache-Control: max-age=640000")
    @GET("213-2")
    fun getLyricMusic(@Query("song-word") musicid: String, @Query("showapi_appid") showapi_appid: String = appid, @Query("showapi_sign") showapi_sign: String = sign)
            : rx.Observable<BaseBean<Pagebean>>

    //歌名人名查询歌曲
    @Headers("Cache-Control: max-age=640000")
    @GET("213-1")
    fun getMusicSearch(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("showapi_appid") showapi_appid: String = appid, @Query("showapi_sign") showapi_sign: String = sign)
            : rx.Observable<BaseBean<PageKeyBean>>


}