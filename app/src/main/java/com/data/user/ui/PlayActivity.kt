package com.data.user.ui

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.RadioButton
import com.chad.library.adapter.base.BaseViewHolder
import com.data.user.R
import com.data.user.apapter.ListAdapter
import com.data.user.bean.MusicItem
import com.data.user.service.MusicService
import  kotlinx.android.synthetic.main.sliding_rgiht.*
import  kotlinx.android.synthetic.main.sliding_left.*

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 播放主界面
 */
class PlayActivity : AppCompatActivity() {

    private lateinit var items: ArrayList<MusicItem>
    private var position: Int = 0
    private lateinit var mListAdapter: ListAdapter//适配器
    private var myMusicNoticeBroadcastReceiver: MyMusicNoticeBroadcastReceiver = MyMusicNoticeBroadcastReceiver()
   private var isShow :Boolean=false
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        regMyMusicBroadcastReceiver()
        val singe = intent.getSerializableExtra("data") as MusicItem
        items = intent.getSerializableExtra("items") as ArrayList<MusicItem>
        position = intent.getIntExtra("position", -1)
        items[position].isSelected = true
        musicPlayerView.stop()
        updateView(singe)
        isShow=true
        MusicService.setState(this, false)//不显示
        MusicService.playTrack(this, singe)//开始播放
        next.setOnClickListener {
            //下一曲
            updatePosition(position, true)
        }
        previous.setOnClickListener {
            //上一曲
            updatePosition(position, false)
        }
        musicPlayerView.setOnClickListener {
            MusicService.setStateUpdate(this, 1)
            play()
        }
        intListData()
        musicPlayerView.start()
    }

    /****
     * 改变索引值
     * @param clickPosition  当前索引
     * @param  isJia  是否加
     * **/
    fun updatePosition(clickPosition: Int, isJia: Boolean) {
        if (clickPosition >= items.size - 1 && !isJia) {
            return
        } else {
            if (clickPosition <= 0) {
                return
            }
        }
        select(if (isJia) (clickPosition + 1) else (clickPosition - 1))
        MusicService.setStateUpdate(this, if (isJia) 2 else 3)
        updateView(items.get(index = clickPosition))

    }

    /***
     * 播放状态
     * **/
    fun play() {
        // content播放控件
        if (!musicPlayerView.isRotating) {
            musicPlayerView.start()
        } else {
            musicPlayerView.stop()
        }
    }


    /***
     * 改变状态
     * @param MusicItem 当前独享
     *
     * ***/
    fun updateView(singe: MusicItem) {
        textViewSong.text = singe.songname
        textViewSinger.text = singe.singername
        musicPlayerView.setCoverURL(singe.albumpic_big)
        musicPlayerView.setMax(singe.seconds)
        musicPlayerView.progress = 0
    }

    /***
     *  初始化数据
     * @param MusicItem 当前独享
     *
     * ***/
    private fun intListData() {
        mListAdapter = ListAdapter(R.layout.list_item_play)
        mListAdapter.addAll(items)
        recyclerView_play.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView_play.adapter = mListAdapter
        recyclerView_play.itemAnimator = DefaultItemAnimator()
        val mDividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        mDividerItemDecoration.setDrawable(ColorDrawable(Color.WHITE))
        recyclerView_play.addItemDecoration(mDividerItemDecoration)
        mListAdapter.setOnItemClickListener { adapter, view, position ->
            select(position)
            MusicService.playTrack(this, mListAdapter.data[position])
            updateView(mListAdapter.data[position])
        }
    }

    /****
     * 切换选中播放歌曲
     * **/
    fun select(clickPosition: Int) {
        // RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
        if (recyclerView_play.findViewHolderForLayoutPosition(position) != null) {//还在屏幕里
            val couponVH = recyclerView_play.findViewHolderForLayoutPosition(position) as BaseViewHolder
            couponVH.getView<RadioButton>(R.id.item_select_image).isChecked = false
        } else {
            //add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
            //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
            mListAdapter.notifyItemChanged(position)
        }
        mListAdapter.data.get(position).isSelected = false//不管在不在屏幕里 都需要改变数据
        //设置新Item的勾选状态
        position = clickPosition
        mListAdapter.data.get(position).isSelected = true//当前点击的
        if (recyclerView_play.findViewHolderForLayoutPosition(clickPosition) != null) {//还在屏幕里
            val holder = recyclerView_play.findViewHolderForLayoutPosition(clickPosition) as BaseViewHolder
            holder.getView<RadioButton>(R.id.item_select_image).isChecked = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        MusicService.setState(this, true)//暂停显示
        isShow=false
    }

    override fun onRestart() {
        super.onRestart()
        MusicService.setState(this, false)//恢复隐藏
        isShow=true
    }

    override fun onDestroy() {
        if (musicPlayerView != null) {
            musicPlayerView.release()
        }
        if (myMusicNoticeBroadcastReceiver != null) {
            unregisterReceiver(myMusicNoticeBroadcastReceiver)
        }
        super.onDestroy()
    }

    /****
     * 注册广播
     * ***/
    fun regMyMusicBroadcastReceiver() {
        val intentFilter = IntentFilter(ACTION_MUSIC_UPDATE_STATE)
        intentFilter.addAction(ACTION_MUSIC_UPDATE_STATE)
        registerReceiver(myMusicNoticeBroadcastReceiver, intentFilter)
    }

    /****
     * 广播交互
     * ***/
   internal inner class MyMusicNoticeBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, ":" + intent.action)
            if (intent.action == ACTION_MUSIC_UPDATE_STATE&&!isShow) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    1 -> {//播放暂停
                        play()
                    }
                    2 -> {//下一曲
                        updatePosition(position, true)
                    }
                    3 -> {//上一曲
                        updatePosition(position, false)
                    }

                }
            }
        }

    }

    companion object {
        private val TAG = PlayActivity::class.java.name
        private val ACTION_MUSIC_UPDATE_STATE = "com.action.my.state"
        fun startActivity(activity: Context, item: MusicItem, items: ArrayList<MusicItem>, position: Int) {
            val intent = Intent(activity, PlayActivity::class.java)
            intent.putExtra("data", item)
            intent.putExtra("items", items)
            intent.putExtra("position", position)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }

        /***
         * 发送播广播
         * **/
        fun setStateUpdate(context: Context, state: Int) {
            val intent = Intent()
            intent.action = ACTION_MUSIC_UPDATE_STATE
            intent.putExtra("state", state)
            context.sendBroadcast(intent)
        }
    }
}
