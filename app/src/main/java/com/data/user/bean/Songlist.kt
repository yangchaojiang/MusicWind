package com.data.user.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
class Songlist : Parcelable {
      override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
      }

      override fun describeContents(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
      }

      var songname: String? = null

      var seconds: Int = 0

      var albummid: String? = null

      var songid: Int = 0

      var singerid: Int = 0

      var albumpic_big: String? = null

      var albumpic_small: String? = null

      var downUrl: String? = null

      var url: String? = null

      var singername: String? = null

      var albumid: Int = 0

}