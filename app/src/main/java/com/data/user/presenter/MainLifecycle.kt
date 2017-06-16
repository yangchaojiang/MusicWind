package com.data.user.presenter

import android.app.Activity
import android.arch.lifecycle.*
import com.data.user.bean.PageKeyBean
import com.data.user.model.MusicViewModel

/**
 * Created by yangc on 2017/6/14.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:主界面
 */
class MainLifecycle(activity: Activity) : LifecycleObserver {

    //数据模型
    private lateinit var myViewModel: MusicViewModel

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        myViewModel = ViewModelProvider.NewInstanceFactory().create(MusicViewModel::class.java)
    }

    /***
     *按歌手查询歌曲
     * @param query 内容
     * @return LiveData
     * **/
    fun onQuery(query: String): LiveData<PageKeyBean> {
        return myViewModel.onQuery(query)
    }

}