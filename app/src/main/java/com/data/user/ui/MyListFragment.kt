package com.data.user.ui

import android.app.ActivityManager
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.data.user.R
import com.data.user.apapter.ListAdapter
import  kotlinx.android.synthetic.main.fragment_list.*
import com.data.user.bean.MusicItem
import com.data.user.bean.Pagebean
import com.data.user.presenter.MyListLifecycle
import com.data.user.service.MusicService
import com.helper.loadviewhelper.load.LoadViewHelper


/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 歌曲列表
 */
class MyListFragment : LifecycleFragment() {

    companion object {
        val TAG = "MyListFragment"
        fun newInstance(param1: String, position: Int): MyListFragment {
            val fragment = MyListFragment()
            val args = Bundle()
            args.putInt("topId", position)
            args.putString("title", param1)
            fragment.arguments = args
            return fragment
        }

    }

    private var topId: Int = 0 //當前id
    private lateinit var mListAdapter: ListAdapter
    private var mMyListLifecycle = MyListLifecycle()
    private lateinit var helper: LoadViewHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topId = arguments.getInt("topId")
        lifecycle.addObserver(mMyListLifecycle)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mListAdapter = ListAdapter(R.layout.list_item)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        helper = LoadViewHelper(recyclerView)
        recyclerView.adapter = mListAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        swipeRefreshLayout.setOnRefreshListener({
            getData()
        })
        mListAdapter.setOnItemClickListener { adapter, view, position ->
            onclick(position)
        }
        mListAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        swipeRefreshLayout.isRefreshing = true
        helper.setListener {
            helper.showLoading()
            getData()
        }
        getData()
    }

    /***
     * 点击事件
     * @param position 索引
     * ***/
    fun onclick(position: Int) {
        val item = mListAdapter.getItem(position) as MusicItem
        MusicService.setTracks(activity, mListAdapter.data)
        PlayActivity.startActivity(activity, item, ArrayList(mListAdapter.data), position)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mMyListLifecycle)
    }

    fun getData() {
        mMyListLifecycle.getMusicList(topId).observe(this, Observer {
            t: Pagebean? ->
            swipeRefreshLayout.isRefreshing = false
            helper.restore()
            mListAdapter.clear()
            if (t != null) {
                mListAdapter.addAll(t.songlist)
                recyclerView.layoutManager.findViewByPosition(0)
            } else {
                helper.showEmpty()
            }
        })
    }
}

