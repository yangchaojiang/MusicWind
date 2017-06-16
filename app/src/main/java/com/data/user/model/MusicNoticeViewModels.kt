package com.data.user.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

/**
 * Created by yangc on 2017/6/16.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

  class MusicNoticeViewModels(application:Application) : AndroidViewModel(application) {

    val liveData: MutableLiveData<Int> = MutableLiveData()

}