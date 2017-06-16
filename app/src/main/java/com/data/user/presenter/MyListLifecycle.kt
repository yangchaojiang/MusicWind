package com.data.user.presenter

import android.arch.lifecycle.*
import com.data.user.bean.Pagebean
import com.data.user.model.MusicViewModel

/**
 * Created by yangc on 2017/6/14.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 列表逻辑
 */
class MyListLifecycle : LifecycleObserver {

    //数据模型
    private lateinit var myViewModel: MusicViewModel


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
       myViewModel = ViewModelProvider.NewInstanceFactory().create(MusicViewModel::class.java)

    }

    /***
     *按歌手查询歌曲
     * @param topId  歌曲类型id
     * @return LiveData
     * **/
    fun getMusicList(topId: Int): LiveData<Pagebean> {
        return myViewModel.getMusicList(topId)
    }
}