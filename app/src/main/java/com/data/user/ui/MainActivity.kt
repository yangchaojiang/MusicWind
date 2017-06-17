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
import android.content.Intent
import android.os.Build
import android.annotation.SuppressLint
import android.net.Uri
import android.provider.Settings
import android.support.annotation.RequiresApi
import com.yutils.YUtils


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
        checkDrawOverlayPermission()
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
        tabLayout.setupWithViewPager(myViewPager,true)
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

    @SuppressLint("NewApi")
    fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            /** check if we already  have permission to draw over other apps  */
            if (!Settings.canDrawOverlays(this)) {
                YUtils.Toast("无法开启悬浮窗,当前无权限，请授权！")
                /** if not construct intent to request permission  */
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName))
                /** request permission via start activity for result  */
                startActivityForResult(intent, 1)
            }
        }
    }

    /**
     * 用户返回

     * @param requestCode
     * *
     * @param resultCode
     * *
     * @param data
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (!Settings.canDrawOverlays(this)) {
                YUtils.Toast("权限授予失败，无法开启悬浮窗！")
            } else {
                YUtils.Toast("权限授予成功")
                //启动FxService
            }
        }
    }
}
