package com.data.user

import android.view.Gravity
import com.data.user.config.RetrofitManager
import com.yutils.YUtils

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
class App : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitManager.Companion.getInstance().init(this)
        YUtils.initialize(this)
        YUtils.setGravity(Gravity.CENTER)
    }
}