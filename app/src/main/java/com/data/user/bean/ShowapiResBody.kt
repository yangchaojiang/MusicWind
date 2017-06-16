package com.data.user.bean

/**
 * Created by yangc on 2017/6/10.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
 class  ShowapiResBody<T :Any>{
     var ret_code: Int = 0
      var pagebean: T?=null
    lateinit var  lyric :String;//g歌词
    lateinit  var lyric_txt:String//显示内容
}
