package com.data.user.ui

import android.arch.lifecycle.*
import android.graphics.Color
import android.support.v4.app.Fragment
import android.view.Menu
import com.data.user.R
import com.data.user.apapter.MyFragmentPagerAdapter
import  kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import com.data.user.presenter.MainLifecycle
import com.data.user.bean.PageKeyBean
import com.yutils.JsonManager
import com.yutils.Log


/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 主界面
 */
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, LifecycleRegistryOwner {


    companion object {
        val TAG = "MainActivity"
    }

    private val mRegistry = LifecycleRegistry(this)
    var  mainLifecycle= MainLifecycle(this)
    override fun getLifecycle(): LifecycleRegistry {
       return  mRegistry
    }
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.data.user.R.layout.activity_main)
        mRegistry.addObserver(mainLifecycle)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.app_name)
        initView()
    }

    fun initView() {
        val list: MutableList<Fragment> = ArrayList()
        var i: Int = 0
        for (s: String in resources.getStringArray(com.data.user.R.array.dataList)) {
            android.util.Log.d(MainActivity.Companion.TAG, s)
            tabLayout.addTab(tabLayout.newTab().setText(s))
            list.add(MyListFragment.Companion.newInstance(s, resources.getIntArray(R.array.dataListId)[i]))
            i += 1
        }
        myViewPager.adapter = MyFragmentPagerAdapter(this, list, supportFragmentManager)
        tabLayout.setupWithViewPager(myViewPager)
        tabLayout.isFillViewport = true
        myViewPager.currentItem = 0
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint=getString( R.string.action_search)
        val textView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        textView.setTextColor(Color.WHITE)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        mainLifecycle.onQuery(query).observe(this, Observer<PageKeyBean> {
            bean->
            Log.d(TAG,JsonManager.beanToJson(bean))
        })
        return  false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mRegistry.removeObserver(mainLifecycle)
    }


}
