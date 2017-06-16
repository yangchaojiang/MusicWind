package com.data.user.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.data.user.bean.PageKeyBean
import com.data.user.bean.Pagebean
import com.data.user.config.RetrofitManager
import com.yutils.Log
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by yangc on 2017/6/14.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 数据模型
 */
class MusicViewModel : ViewModel() {

companion object{
    val TAG=MusicViewModel::class.java.name
}
    /***
     *按歌手查询歌曲
     * @param query 内容
     * @return LiveData
     * **/
   fun  onQuery(  query: String):LiveData<PageKeyBean>{
     val data= MutableLiveData<PageKeyBean>()
        RetrofitManager.getInstance().getmusic().getMusicSearch(query,0)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//取消订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    bean->
                    if (bean.showapi_res_body.ret_code==0) {
                    data.postValue(bean.showapi_res_body.pagebean)
                    }else{
                        Log.e(TAG,bean.showapi_res_error)
                        data.postValue(null)
                    }
                },{
                    e->
                    Log.d(TAG,e.message)
                    data.postValue(null)
                })
       return  data
    }
    /***
     *按查询歌曲
     * @param topId  歌曲类型id
     * @return LiveData
     * **/
    fun getMusicList(topId:Int) :LiveData<Pagebean> {
        val data= MutableLiveData<Pagebean>()
        RetrofitManager.getInstance().getmusic().getMusicTop(topId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//取消订阅
                .subscribe({
                    base ->
                    if (base.showapi_res_body.ret_code==0) {
                        data.postValue(base.showapi_res_body.pagebean)
                    }else{
                        Log.e(TAG,base.showapi_res_error)
                        data.postValue(null)
                    }
                }, {
                    e ->
                    data.postValue(null)
                    Log.e(TAG,"" + e.message)
                })
        return data
    }

    /***
     * 歌曲查询歌词额e
     * @param  musicid 歌曲名称
     * **/
    fun  onLyricMusic(musicid:String){
        RetrofitManager.getInstance().getmusic().getLyricMusic(musicid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())//取消订阅
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                },{
                    e ->
                    Log.d(TAG,"" + e.message)
                })

    }
}